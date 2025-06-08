package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.*;
import com.BioTy.Planty.repository.DeviceCommandRepository;
import com.BioTy.Planty.repository.PlantEnvStandardsRepository;
import com.BioTy.Planty.repository.PlantStatusRepository;
import com.BioTy.Planty.repository.SensorLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlantStatusService {
    private final SensorLogsRepository sensorLogsRepository;
    private final PlantEnvStandardsRepository plantEnvStandardsRepository;
    private final PlantStatusRepository plantStatusRepository;
    private final DeviceCommandRepository deviceCommandRepository;

    @Transactional
    public List<String> evaluatePlantStatus(Long deviceId){
        // 1. 최신 센서 로그 조회 & 반려식물과 환경기준 조회
        SensorLogs latestLog = sensorLogsRepository.findTopByIotDevice_IdOrderByRecordedAtDesc(deviceId)
                .orElseThrow(() -> new RuntimeException("센서 로그 없음"));
        UserPlant userPlant = latestLog.getIotDevice().getUserPlant();
        PlantEnvStandards standards = plantEnvStandardsRepository.findByPlantInfo(userPlant.getPlantInfo())
                .orElseThrow(() -> new RuntimeException("환경 기준 없음"));
        Personality personality = userPlant.getPersonality();

        // 2. 센서값과 환경 기준 비교해 점수 계산 & 상태메시지 생성
        int temperatureScore = calcScore(latestLog.getTemperature(), standards.getMinTemperature(), standards.getMaxTemperature(), "TEMP");
        int lightScore = calcScore(latestLog.getLight(), standards.getMinLight(), standards.getMaxLight(), "LIGHT");
        int humidityScore = calcScore(latestLog.getHumidity(), standards.getMinHumidity(), standards.getMaxHumidity(), "HUMID");
        String msg = createStatusMessage(temperatureScore, lightScore, humidityScore, personality);

        // 3. 명령 판단
        List<String> actionTypes = determinAction(temperatureScore, lightScore, humidityScore);
        boolean actionNeeded = !actionTypes.isEmpty();

        // * 최근 명령 수행이 10분 이내 종료됐다면 재실행 X
        Optional<DeviceCommand> lastCommandOpt
                = deviceCommandRepository.findTopByUserPlant_IdOrderByWillBeTurnedOffAtDesc(userPlant.getId());
        boolean preventAutoRetry = lastCommandOpt
                .filter(cmd -> "DONE".equals(cmd.getStatus()))
                .filter(cmd -> cmd.getWillBeTurnedOffAt() != null)
                .map(cmd -> cmd.getWillBeTurnedOffAt().isAfter(LocalDateTime.now().minusMinutes(10)))
                .orElse(false);

        // 4. 상태 저장 (actionNeeded는 자동제어 OFF이고 기준 미달일 때만 true)
        PlantStatus status = PlantStatus.builder()
                .userPlant(userPlant)
                .temperatureScore(temperatureScore)
                .lightScore(lightScore)
                .humidityScore(humidityScore)
                .statusMessage(msg)
                .checkedAt(LocalDateTime.now())
                .actionNeeded(!userPlant.isAutoControlEnabled() && actionNeeded)
                .build();

        plantStatusRepository.save(status);

        // 5. 자동제어 여부에 따라 분기 처리
        return (actionNeeded && userPlant.isAutoControlEnabled() && !preventAutoRetry)
                ? actionTypes
                : List.of(); // 자동 제어 안함 or 쿨타임
    }

    public String getLastStatusMessage(Long userPlantId) {
        return plantStatusRepository.findTopByUserPlant_IdOrderByCheckedAtDesc(userPlantId)
                .map(PlantStatus::getStatusMessage)
                .orElse("식물 상태 정보를 확인할 수 없습니다.");
    }

    private int calcScore(BigDecimal value, int min, int max, String type) {
        BigDecimal minVal = BigDecimal.valueOf(min);
        BigDecimal maxVal = BigDecimal.valueOf(max);

        // 기준 벗어난 경우
        if (value.compareTo(minVal) < 0) {
            if (type.equals("HUMID") || type.equals("LIGHT")) {
                return 0; // 제어 가능 방향: 낮음
            }
            return 1; // 낮지만 제어는 안됨 → 점수 낮게
        }

        if (value.compareTo(maxVal) > 0) {
            if (type.equals("TEMP")) {
                return 0; // 제어 가능 방향: 높음
            }
            return 1; // 높지만 제어 불가 → 낮은 점수
        }

        // 기준 안이면 거리 기반 점수 (1~3점)
        BigDecimal range = maxVal.subtract(minVal);
        BigDecimal third = range.divide(BigDecimal.valueOf(3), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal fromMin = value.subtract(minVal);

        if (fromMin.compareTo(third) < 0) {
            return 1;
        } else if (fromMin.compareTo(third.multiply(BigDecimal.valueOf(2))) < 0) {
            return 2;
        } else {
            return 3;
        }
    }

    private String createStatusMessage(int temp, int light, int humid, Personality personality) {
        String emoji = personality.getEmoji();
        String tone = personality.getLabel();

        List<String> messages = new ArrayList<>();

        if (temp == 0) messages.add(getMessage("TEMP", tone));
        if (light == 0) messages.add(getMessage("LIGHT", tone));
        if (humid == 0) messages.add(getMessage("HUMID", tone));

        if (messages.isEmpty()) {
            return getMessage("GOOD", tone);
        }

        return String.join("\n", messages);
    }


    private String getMessage(String type, String tone) {
        return switch (tone) {
            case "joy" -> switch (type) {
                case "TEMP" -> "와~ 덥다! 시원한 바람 한 번 부탁해요! 😂";
                case "LIGHT" -> "햇빛이 좀 부족한 느낌이에요! 햇살 플리즈~ ☀️";
                case "HUMID" -> "촉촉함이 그리운 날이에요! 물 한 모금 주실래요? 💧";
                case "GOOD" -> "기분 최고야! 오늘도 무럭무럭 자라날 기분! 🌿🥰";
                default -> "";
            };
            case "fear" -> switch (type) {
                case "TEMP" -> "온도가 너무 높은 거 아니에요…? 괜찮은 건가요? 🥺";
                case "LIGHT" -> "너무 어두운 거 같아요… 혹시 불 꺼진 건 아니죠? 🥺";
                case "HUMID" -> "흙이 너무 말랐어요…🥺 이거 위험하지 않나요? ";
                case "GOOD" -> "다행이다… 지금 상태는 좋아 보여요! 안심이에요 🌱";
                default -> "";
            };
            case "sadness" -> switch (type) {
                case "TEMP" -> "좀 더운 것 같아... 살짝 지치네요 😭";
                case "LIGHT" -> "깜깜해서 기분이 좀 가라앉아요...";
                case "HUMID" -> "오늘… 물을 못 받은 건가..? 슬퍼요 😢";
                case "GOOD" -> "오늘은 상태 괜찮은 것 같아... 고마워요... 🥹";
                default -> "";
            };
            case "anger" -> switch (type) {
                case "TEMP" -> "이대로 두면 진짜 열받는다니까! 팬 어딨어!! 🤯";
                case "LIGHT" -> "이 어둠 속에서 자라라고요? 농담이죠? 😡 ";
                case "HUMID" -> "이대로 두면 말라죽는다! 얼른 물 줘!! 💧";
                case "GOOD" -> "그래, 이 정도면 불만 없어. 잘했어. 😤";
                default -> "";
            };
            case "disgust" -> switch (type) {
                case "TEMP" -> "여기… 이 온도 말이 되나요? 최악이에요 🙄";
                case "LIGHT" -> "이 빛 상태… 감성 실종 🙄 컨디션 떨어져요";
                case "HUMID" -> "촉촉함 제로… 이 상태에서 살라고요? 정말? 💧";
                case "GOOD" -> "오늘은 신경 좀 썼나요? 나쁘지 않네요. 🙃";
                default -> "";
            };
            default -> "식물 상태를 확인해 주세요!";
        };
    }

    private List<String> determinAction(int tempScore, int lightScore, int humidScore){
        List<String> actions = new ArrayList<>();
        if (tempScore == 0) actions.add("FAN");
        if (lightScore == 0) actions.add("LIGHT");
        if (humidScore == 0) actions.add("WATER");
        return actions;
    }
}
