# SecurityFilterChain

## 1. Goal

- `SecurityFilterChain` を直接設定
- URL ごとのアクセス権限を設定
- 公開 URL と認証が必要な URL を区別
- `HttpSecurity` と `SecurityFilterChain` の役割を確認
- 認可ルールのマッチング順序を確認

## 2. Environment

- Dependencies
    - Spring Web
    - Spring Security
- `SecurityConfig` を作成
- テスト用 Endpoint を構成
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

- 認証なしでアクセス可能
- `Hello Spring Security` を出力

### `/public`

```text
GET /public
```

- 認証なしでアクセス可能
- `Public Page` を出力

### `/private`

```text
GET /private
```

- 認証されていないユーザー
    - ログイン画面へ移動
- 認証済みユーザー
    - `Private Page` を出力

### その他のリクエスト

```text
GET /unknown
```

- `anyRequest().authenticated()` を適用
- 認証が必要

## 6. HttpSecurity

- Web セキュリティ設定を構成するためのオブジェクト
- HTTP リクエストに適用する Security 設定を記述
- 主な設定内容
  - URL ごとのアクセスルール
  - 認証方式
  - ログイン方式
  - Security Filter の構成

```text
HttpSecurity
↓
Security 設定
↓
http.build()
↓
SecurityFilterChain を生成
```

## 7. SecurityFilterChain

- HTTP リクエストに適用される Spring Security Filter のチェーン
- 複数の Security Filter で構成
- 認証、認可、セキュリティ処理などを順番に実行

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

- 特定のリクエストまたは URL パターンを指定
- 指定したリクエストに認可ルールを適用

```java
.requestMatchers("/", "/public")
```

### permitAll()

- すべてのユーザーにアクセスを許可
- 認証状態に関係なくアクセス可能
- Spring Security 自体を回避するわけではない

```java
.requestMatchers("/", "/public").permitAll()
```

リクエストフロー

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

- 認証済みユーザーのみアクセスを許可
- 特定の Role や Authority を要求する設定とは異なる

```java
.requestMatchers("/private").authenticated()
```

```text
/private
↓
Authenticated?
↓
YES → アクセス許可
NO  → 認証を要求
```

- 現在の設定では `/private`も `anyRequest().authenticated()`の対象
- 学習目的で `/private`のルールを明示的に記述

### anyRequest()

- 前の `requestMatchers()`にマッチしなかったすべてのリクエストを指定

```java
.anyRequest().authenticated()
```

- 残りのすべてのリクエストに認証を要求

## 9. Authorization Rule Order

- 認可ルールは記述順が重要
- 上から順番にリクエストとルールをマッチング
- 最初にマッチしたルールを適用
- 具体的なルールを先に記述
- 広い範囲のルールを後に記述

推奨例

```java
.requestMatchers("/","/public").permitAll()
.requestMatchers("/private").authenticated()
.anyRequest().authenticated()
```

基本的な順序

```text
Specific Rule
↓
General Rule
```

`anyRequest()`は一般的に最後に配置

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

- Form ベースのログインを有効化
- Spring Security のデフォルト設定を使用
- 認証されていないユーザーが保護された URL にアクセスした場合、ログイン画面を提供

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
  - Security 設定を記述
- `http.build()`
  - 設定内容をもとに `SecurityFilterChain` を生成
- `SecurityFilterChain`
  - リクエストに Security Filter を適用
- `requestMatchers()`
  - ルールを適用するリクエストを指定
- `permitAll()`
  - すべてのユーザーにアクセスを許可
- `authenticated()`
  - 認証済みユーザーのみアクセスを許可
- `anyRequest()`
  - 残りのすべてのリクエストを指定
- 認可ルールの宣言順序が重要

## 13. What I Learned

- Spring Security のデフォルト設定を直接変更可能
- `SecurityFilterChain` Bean を利用して Web Security 設定を構成
- `HttpSecurity` を使用してリクエストごとのアクセスルールを設定
- 公開 URLと認証が必要な URL を区別
- `permitAll()`も Spring Security Filter Chain を通過
- `authenticated()`は認証状態を基準にアクセスを制御
- `anyRequest()`はマッチしなかった残りのリクエストに適用
- 認可ルールは具体的なルールから記述
- 次の学習内容
  - Authentication
  - Authorization
  - 認証情報の保存先
  - `SecurityContext`
  - `Authentication` オブジェクト