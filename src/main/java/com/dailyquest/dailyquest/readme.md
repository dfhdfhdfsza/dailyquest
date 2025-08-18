# DailyQuest - 모바일 게임 숙제 관리 서비스

##  소개
여러 모바일 게임의 **일일/주간/이벤트 숙제**를 한 곳에서 관리할 수 있는 웹 애플리케이션입니다.
JWT 기반 인증과 실시간 숙제 체크 기능을 제공하며, PC·모바일 환경 모두 대응합니다.

## 🚀 주요 기능
- 사용자 회원가입/로그인 (JWT 인증)
- 게임별 숙제 등록/수정/삭제
- 일간·주간·이벤트 숙제 구분
- 완료 체크 및 히스토리 조회
- Swagger 기반 API 문서 제공

## 🛠 기술 스택
- **Backend**: Spring Boot, Spring Web, Spring Security, Spring Data JPA
- **Frontend**: React, Tailwind CSS
- **Database**: MySQL
- **Auth**: JWT (Access/Refresh Token)
- **Docs**: springdoc-openapi (Swagger UI)

## 🚀 실행 방법
### Backend
```bash
./gradlew bootRun
```

### Frontend
```bash
- npm install
- npm run dev
```

## 🔑 환경 변수(.env.example)
```env
# JWT secrets (토큰 서명 키)
JWT_SECRET=your-access-token-secret
JWT_REFRESH_SECRET=your-refresh-token-secret

# API base URL (개발 환경 기준)
VITE_API_BASE_URL=http://localhost:8080

# Database configuration
DB_URL=jdbc:mysql://localhost:3306/dailyquest
DB_USERNAME=root
DB_PASSWORD=your-db-password

# Email SMTP configuration
EMAIL_USERNAME=your-email@example.com
EMAIL_PASSWORD=your-email-password
```

## 📜 API 문서
- 로컬: http://localhost:8080/swagger-ui/index.html
- [에러 코드 목록](docs/error-codes.md)

## 📂 폴더 구조
```plaintext
dailyquest/
├── src/
│   ├── main/
│   │   ├── java/com/dailyquest/dailyquest/
│   │   │   ├── config/              # 설정 (Security, Swagger 등)
│   │   │   ├── controller/          # REST API 컨트롤러
│   │   │   ├── dto/                 # 요청·응답 DTO
│   │   │   ├── entity/              # JPA 엔티티
│   │   │   ├── repository/          # 데이터 접근 레이어
│   │   │   ├── security/            # JWT 및 인증·인가 로직
│   │   │   ├── service/             # 비즈니스 로직
│   │   │   ├── type/                # Enum 등 타입 정의
│   │   │   └── DailyquestApplication # Spring Boot 메인 클래스
│   │   └── resources/
│   │       ├── static/              # 정적 리소스(css, js, fontawesome)
│   │       │   ├── css/
│   │       │   ├── fontawesome/
│   │       │   └── js/
│   │       ├── templates/           # Thymeleaf 템플릿
│   │       ├── application.yml
│   │       └── .env                 # ← 실제 비밀키 커밋 금지 (예시는 루트에)
│   └── test/                        # 테스트 코드
├── .gitattributes
├── .gitignore
├── build.gradle
├── gradlew
├── gradlew.bat
└── settings.gradle

```

# 📑 Error Codes

모든 실패 응답은 아래 JSON 포맷을 따릅니다:
```json
{
  "success": false,
  "code": "USER_DUPLICATE_EMAIL",
  "message": "이미 사용 중인 이메일입니다.",
  "data": null
}
```

## 📸 스크린샷
주요 화면 예시입니다. (이미지는 docs/images 폴더에 저장하고 상대 경로로 참조)

- 로그인 화면

- 숙제 관리 화면

- API 문서 (Swagger UI)