# 🌱 Planty Backend
### 📁 프로젝트 구조
```
Planty-BE
├── build.gradle                      # Gradle 빌드 설정 파일
└── src/
    ├── main/
    │   ├── java/com/BioTy/Planty/
    │   │   ├── PlantyApplication.java       # SpringBoot 애플리케이션 진입점
    │   │   │
    │   │   ├── config/
    │   │   │   ├── AdafruitClient.java          # Adafruit API 통신 설정
    │   │   │   ├── AppConfig.java               # 일반 애플리케이션 설정
    │   │   │   ├── FirebaseConfig.java          # Firebase Admin SDK 설정
    │   │   │   ├── JwtProperties.java           # JWT 관련 프로퍼티 설정
    │   │   │   ├── SchedulerConfig.java         # 스케줄링 관련 설정
    │   │   │   ├── SecurityConfig.java          # Spring Security 설정
    │   │   │   ├── SensorFetchScheduler.java    # 센서 데이터 주기적 수집 스케줄러
    │   │   │   └── SwaggerConfig.java           # Swagger API 문서 설정
    │   │   │
    │   │   ├── controller/
    │   │   │   ├── AuthController.java         # 로그인/회원가입 등 인증 API
    │   │   │   ├── ChatController.java         # 챗봇 연동 API
    │   │   │   ├── FCMController.java          # 푸시 알림 관련 API
    │   │   │   ├── IotController.java          # IoT 제어 관련 API
    │   │   │   ├── PersonalityController.java  # 식물 성격 관련 API
    │   │   │   ├── PlantInfoController.java    # 식물 도감 관련 API
    │   │   │   └── UserPlantController.java    # 반려식물 등록/조회 API
    │   │   │
    │   │   ├── dto/
    │   │   │   ├── chat/                      # 채팅 관련 DTO
    │   │   │   ├── fcm/                       # FCM 토큰/알림 관련 DTO
    │   │   │   ├── iot/                       # IoT 기기 제어/센서 관련 DTO
    │   │   │   ├── plant/                     # 식물 도감 관련 DTO
    │   │   │   ├── user/                      # 사용자 로그인/회원가입 DTO
    │   │   │   └── userPlant/                 # 사용자 식물 관련 DTO
    │   │   │
    │   │   ├── entity/                    # DB 테이블과 매핑되는 JPA 엔티티
    │   │   │
    │   │   ├── repository/               # DB 접근 레이어 (JPA Repository)
    │   │   │   ├── ~Repository.java           # 엔티티별 CRUD 리포지토리
    │   │   │   └── ~RepositoryImpl.java       # 커스텀 구현체
    │   │   │
    │   │   ├── security/                  # JWT 유틸 등 보안 관련 클래스
    │   │   │
    │   │   └── service/                   # 비즈니스 로직 처리
    │   │       ├── AuthService.java
    │   │       ├── ChatService.java
    │   │       ├── DeviceCommandService.java
    │   │       ├── FCMService.java
    │   │       ├── IotService.java
    │   │       ├── PlantInfoService.java
    │   │       ├── PlantStatusService.java
    │   │       └── UserPlantService.java
    │   │
    │   └── resources/                 # 정적 자원 및 설정 파일
    │       ├── application.yml            # SpringBoot 전체 설정 파일
    │       └──firebase/                  # Firebase 인증키 저장
    │
    └── test/                        # 테스트 코드 디렉토리

```