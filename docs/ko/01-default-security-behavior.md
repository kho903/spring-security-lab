# Spring Security Default Behavior

## 1. Goal

- Spring Security 의존성 추가 시 기본 동작 확인
- 별도 Security 설정 없이 적용되는 보안 기능 확인
- 요청이 Controller에 도달하기 전 Security가 동작하는지 확인

## 2. Environment

- Dependencies
    - Spring Web
    - Spring Security
- 별도 `SecurityConfig` 없음
- 테스트용 Controller 작성

```java

@RestController
public class HelloController {

	@GetMapping("/")
	public String hello() {
		return "Hello Spring Security";
	}
}
```

## 3. Test

접속 주소

```text
http://localhost:8080/
```

예상 결과

```text
Hello Spring Security
```

실제 결과

- Spring Security 기본 로그인 페이지 출력
- Controller 응답 바로 출력되지 않음

애플리케이션 실행 로그

```text
Using generated security password: ...
```

기본 계정

```text
Username: user
Password: generated password
```

로그인 성공 후

```text
Hello Spring Security
```

## 4. Default Behavior

Spring Security 의존성 추가

```text
Spring Security dependency
↓
Spring Boot Auto Configuration
↓
Default Security Configuration
↓
Authentication Required
```

주요 기본 동작

- Spring Security 관련 Auto Configuration 적용
- 기본 사용자 생성
    - Username: `user`
- 임시 Password 생성
- 요청에 인증 요구
- 기본 로그인 페이지 제공

## 5. Request Flow

### 인증 전

```text
GET /
↓
Spring Security
↓
Authenticated?
↓
NO
↓
Login Page
```

### 인증 후

```text
GET /
  ↓
Spring Security
  ↓
Authenticated?
  ↓
YES
  ↓
HelloController
  ↓
"Hello Spring Security"
```

## 6. Key Point

- HTTP 요청이 Controller에 바로 전달되지 않음
- Spring Security가 Controller보다 먼저 요청 처리
- 인증 여부 확인 후 요청 전달 여부 결정

```text
Request
↓
Spring Security
↓
Controller
```

- 앞으로 학습할 `SecurityFilterChain`의 기본 출발점

## 7. Key Concepts

### Auto Configuration

- Spring Boot의 자동 설정 기능
- classpath, Bean 등의 상태를 기준으로 필요한 설정 적용
- Spring Security 의존성 존재 시 기본 Security 설정 적용

### Authentication

- 사용자 신원 확인 과정
- "사용자가 누구인가?"
- 인증되지 않은 사용자가 보호된 리소스 접근 시 인증 요구

### Authorization

- 인증된 사용자의 접근 권한 확인 과정
- "이 리소스에 접근할 권한이 있는가?"

## 8. What I Learned

- Spring Security 의존성만 추가해도 기본 보안 설정 적용
- 직접 Security 설정을 작성하지 않아도 인증 기능 동작
- 기본 사용자와 임시 Password 자동 생성
- 요청이 Controller보다 먼저 Spring Security를 통과
- 다음 단계
    - `SecurityFilterChain` 직접 설정
    - 공개 URL / 인증 필요 URL 구분

## 9. Japanese Terms

| Korean | English            | Japanese      |
|--------|--------------------|---------------|
| 인증     | Authentication     | 認証（にんしょう）     |
| 인가     | Authorization      | 認可（にんか）       |
| 요청     | Request            | リクエスト         |
| 응답     | Response           | レスポンス         |
| 자동 설정  | Auto Configuration | 自動設定（じどうせってい） |
| 접근 제어  | Access Control     | アクセス制御（せいぎょ）  |
