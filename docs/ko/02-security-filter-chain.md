# SecurityFilterChain

## 1. Goal

- `SecurityFilterChain` 직접 설정
- URL별 접근 권한 설정
- 공개 URL과 인증 필요 URL 구분
- `HttpSecurity`와 `SecurityFilterChain`의 역할 확인
- 인가 규칙의 매칭 순서 확인

## 2. Environment

- Dependencies
    - Spring Web
    - Spring Security
- `SecurityConfig` 생성
- 테스트용 Endpoint 구성
    - `/`
    - `/public`
    - `/private`

## 3. Controller

```java

@RestController
public class HelloController {

	@GetMapping("/")
	public String hello() {
		return "Hello Spring Security";
	}

	@GetMapping("/public")
	public String publicPage() {
		return "Public Page";
	}

	@GetMapping("/private")
	public String privatePage() {
		return "Private Page";
	}

}
```

## 4. Security Configuration

```java

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/public").permitAll()
				.requestMatchers("/private").authenticated()
				.anyRequest().authenticated()
			)
			.formLogin(Customizer.withDefaults());

		return http.build();
	}
}
```

## 5. Test Result

### `/`

```text
GET /
```

- 인증 없이 접근 가능
- `Hello Spring Security` 출력

### `/public`

```text
GET /public
```

- 인증 없이 접근 가능
- `Public Page` 출력

### `/private`

```text
GET /private
```

- 인증되지 않은 사용자
    - 로그인 페이지로 이동
- 인증된 사용자
    - `Private Page` 출력

### 그 외 요청

```text
GET /unknown
```

- `anyRequest().authenticated()` 적용
- 인증 필요

## 6. HttpSecurity

- 웹 보안 설정을 구성하기 위한 객체
- HTTP 요청에 적용할 Security 설정 작성
- 주요 설정
    - URL별 접근 규칙
    - 인증 방식
    - 로그인 방식
    - Security Filter 구성

```text
HttpSecurity
↓
Security 설정 작성
↓
http.build()
↓
SecurityFilterChain 생성
```

## 7. SecurityFilterChain

- HTTP 요청에 적용되는 Spring Security Filter들의 체인
- 여러 Security Filter로 구성
- 인증, 인가, 보안 처리 등을 순서대로 수행

```text
Request
↓
Spring Security
↓
SecurityFilterChain
↓
Security Filter
↓
Security Filter
↓
Security Filter
↓
Controller
```

## 8. Request Authorization

### requestMatchers()

- 특정 요청 또는 URL 패턴 지정
- 지정된 요청에 인가 규칙 적용

```java
.requestMatchers("/", "/public")
```

### permitAll()

- 모든 사용자에게 접근 허용
- 인증 여부와 관계없이 접근 가능
- Spring Security 자체를 우회하는 것은 아님

```java
.requestMatchers("/", "/public").permitAll()
```

요청 흐름

```text
Request
↓
Spring Security
↓
Authorization
↓
permitAll
↓
Controller
```

### authenticated()

- 인증된 사용자만 접근 허용
- 특정 Role이나 Authority 요구와는 다름

```java
.requestMatchers("/private").authenticated()
```

```text
/private
↓
Authenticated?
↓
YES → 접근 허용
NO  → 인증 요구
```

- 현재 설정에서는 `/private`도 `anyRequest().authenticated()`에 포함
- 학습 목적으로 `/private` 규칙을 명시적으로 작성

### anyRequest()

- 앞의 `requestMatchers()`에 매칭되지 않은 모든 요청 지정

```java
.anyRequest().authenticated()
```

- 나머지 모든 요청에 인증 요구

## 9. Authorization Rule Order

- 인가 규칙은 작성 순서가 중요
- 위에서부터 요청과 규칙을 매칭
- 먼저 매칭된 규칙 적용
- 구체적인 규칙을 먼저 작성
- 넓은 범위의 규칙은 뒤에 작성

권장 예시

```java
.requestMatchers("/","/public").permitAll()
.requestMatchers("/private").authenticated()
.anyRequest().authenticated()
```

기본 순서

```text
Specific Rule
↓
General Rule
```

`anyRequest()`는 일반적으로 마지막에 배치

```text
requestMatchers(...)
↓
requestMatchers(...)
↓
anyRequest(...)
```

## 10. formLogin()

```java
.formLogin(Customizer.withDefaults())
```

- Form 기반 로그인 활성화
- Spring Security 기본 설정 사용
- 인증되지 않은 사용자가 보호된 URL에 접근할 경우 로그인 화면 제공

## 11. Request Flow

### Public Request

```text
GET /public
↓
Spring Security
↓
SecurityFilterChain
↓
Authorization Rule
↓
permitAll()
↓
HelloController
↓
Public Page
```

### Private Request

```text
GET /private
↓
Spring Security
↓
SecurityFilterChain
↓
Authorization Rule
↓
authenticated()
↓
Authenticated?
├─ YES → HelloController
└─ NO  → Login Page
```

## 12. Key Point

- `HttpSecurity`
  - Security 설정 작성
- `http.build()`
  - 설정을 기반으로 `SecurityFilterChain` 생성
- `SecurityFilterChain`
  - 요청에 Security Filter 적용
- `requestMatchers()`
  - 규칙을 적용할 요청 지정
- `permitAll()`
  - 모든 사용자 접근 허용
- `authenticated()`
  - 인증된 사용자만 접근 허용
- `anyRequest()`
  - 나머지 모든 요청 지정
- 인가 규칙의 선언 순서 중요

## 13. What I Learned

- Spring Security의 기본 설정을 직접 변경 가능
- `SecurityFilterChain` Bean을 통해 웹 Security 설정 구성
- `HttpSecurity`를 사용하여 요청별 접근 규칙 설정
- 공개 URL과 인증 필요 URL 구분 가능
- `permitAll()`도 Spring Security Filter Chain을 통과
- `authenticated()`는 인증 여부를 기준으로 접근 제어
- `anyRequest()`는 매칭되지 않은 나머지 요청에 적용
- 인가 규칙은 구체적인 규칙부터 작성
- 다음 학습 내용
  - Authentication
  - Authorization
  - 인증 정보가 어디에 저장되는지 확인
  - `SecurityContext`
  - `Authentication` 객체