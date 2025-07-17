<img alt="spring boot" src="https://img.shields.io/badge/springboot-6DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white" height="20"/> <img alt="java" src="https://img.shields.io/badge/java-007396.svg?style=for-the-badge&logo=java&logoColor=white" height="20"/> <img alt="mysql" src="https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white" height="20"/>

# 🌱 Planty Backend

**Planty Project**에서 사용하는 SpringBoot 기반 백엔드 API 서버 레포지토리입니다.<br>
Adafruit IO를 통해 식물의 센서 데이터를 주기적으로 수집하고, 식물 상태를 평가해 자동/수동으로 IoT 제어를 수행합니다. Firebase 기반 푸시 알림 전송, FastAPI 챗봇 연동 등 다양한 기능을 제공합니다.

## 프로젝트 개요
- **전체 개발 기간**: 2025.04.01 - 2025.06.11
    - ERD 및 API 설계: 2025.04.01 - 2025.04.09
    - 기능 개발: 2025.04.10 - 2025.06.11
        - 4월: 회원가입/로그인, 식물 등록, 홈(식물 목록 제공)
        - 5월: 식물 상세 리포트, 챗봇, IoT 연동, FastAPI 서버 연동 
        - 6월: IoT 제어로직 개선, FCM 푸시 알림, 마이페이지

<br>

## 📌 기술 스택
이 프로젝트는 아래 환경에서 개발되었습니다.

|분야| 기술                            |
|--|-------------------------------|
|Backend Framework| Spring Boot 3.4.4             |
|언어| Java 17.0.15                         |
|DB| MySQL 9.3.0                   |
|인증| JWT                           |
|외부 연동| Adafruit IO, Firebase Admin SDK |
|문서화| Springdoc OpenAPI (Swagger UI) |


<br>

## 🌱 주요 기능

Planty 백엔드의 핵심 기능은 다음과 같습니다.

### 1. 센서 데이터 수집 및 저장

- `SensorFetchScheduler`가 매시 정각마다 Adafruit IO에서 센서 데이터 수집
- `PlantStatusService`에서 식물 종류에 따른 기준값과 비교해 식물 상태를 판단
- 센서 데이터와 상태 각각 DB에 저장

### 2. IoT 제어 (자동/수동)

- 자동제어모드에서 조건 충족 시 `DeviceCommandService`가 물주기/팬켜기/조명켜기 명령 수행
- 수동제어 시 `IotController`가 명령을 수신하여 수행
- 명령은 일정 시간 경과 후 자동으로 OFF 처리되며, 이후 상태 업데이트함

### 3. 푸시 알림 (FCM)

- 상태 이상, 자동 제어 수행 등의 이벤트가 발생했을 때, `FCMService`로 푸시 알림 전송
- Flutter 앱에서 알림 수신 후 저장 및 표시

### 4. 챗봇 연동

- 사용자의 메시지 + 식물의 정보/성격/현재 상태를 FastAPI 서버로 전송
- FastAPI 챗봇이 응답 생성 후 Spring 서버를 통해 저장 및 반환


<br>

## 📦 실행 환경 설정 가이드

### 1. JDK 17 설치

- [OpenJDK 17](https://jdk.java.net/java-se-ri/17)
- 설치 후, `java --version`으로 정상 설치 확인

### 2. MySQL 설치 및 초기 데이터 세팅

- MySQL 8.0 이상 설치 
- root 계정 비밀번호 설정 
- `planty` DB 생성

### 3. Adafruit IO 연동 설정

1. https://io.adafruit.com 회원가입 및 로그인
2. My Key 메뉴에서 AIO Key와 Username 확인
3. Feeds 메뉴에서 아래 이름으로 피드 3개 생성
- planty.temperature
- planty.soilmoisture
- planty.lightintensity

> 위 정보는 application.yml의 adafruit.api.key, username, feed.key 항목에 사용됩니다.

### 4. Firebase Admin SDK 설정

1. Firebase 콘솔에서 새 프로젝트 생성
2. 설정 > 서비스 계정 탭으로 이동
3. "새 비공개 키 발급" 버튼 클릭 → JSON 파일 다운로드 
4. 다운로드한 JSON 파일을 다음 경로에 저장
```
src/main/resources/firebase/<planty-firebase-adminsdk-xxx.json>
```

> 이 JSON 파일은 민감 정보가 포함되므로 .gitignore에 추가해야 합니다.

### 5. JWT 비밀키 설정

JWT 토큰 생성을 위해 랜덤 문자열을 jwt.secret에 입력합니다.

```
jwt:
  secret: planty-secure-key-123456789 # 예시
  expiration: 3600000 # 1시간

```

### 📍 application.yml 작성 예시

`src/main/resources/application.yml` 파일을 생성하고, 아래 예시를 참고해 설정해주세요. <br>
모든 키 값은 직접 발급받은 정보(1-5번에서 설정한 값들)로 교체해야 합니다.

```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/planty?serverTimezone=Asia/Seoul
    username: root
    password: <DB_비밀번호>
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    database-platform: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: update
    show-sql: true

  application:
    name: Planty

adafruit:
  api:
    key: <ADAFRUIT_API_KEY>
    url: https://io.adafruit.com
    username: <ADAFRUIT_USERNAME>
  feed:
    key: planty.soilmoisture

jwt:
  secret: <JWT_SECRET_KEY>
  expiration: 3600000

springdoc:
  swagger-ui:
    path: /swagger-ui
    operations-sorter: method
    tags-sorter: alpha
    display-request-duration: true
    
fcm:
  firebase_config_path: firebase/<planty-firebase-adminsdk-xxx.json>

```

<br>

## 📁 프로젝트 구조
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