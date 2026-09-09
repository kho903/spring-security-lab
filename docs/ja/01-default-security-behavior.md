# Spring Security Default Behavior

## 1. Goal

- Spring Security の依存関係を追加した際のデフォルト動作を確認
- Security 設定を作成していない状態で適用されるセキュリティ機能を確認
- リクエストが Controller に到達する前に Spring Security が動作することを確認

## 2. Environment

- Dependencies
    - Spring Web
    - Spring Security
- `SecurityConfig` の設定なし
- テスト用 Controller を作成

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

アクセス先

```text
http://localhost:8080/
```

予想した結果

```text
Hello Spring Security
```

実際の結果

- Spring Security のデフォルトログイン画面を表示
- Controller のレスポンスは直接表示されない

アプリケーション実行ログ

```text
Using generated security password: ...
```

デフォルトアカウント

```text
Username: user
Password: generated password
```

ログイン成功後

```text
Hello Spring Security
```

## 4. Default Behavior

Spring Security の依存関係を追加

```text
Spring Security dependency
↓
Spring Boot Auto Configuration
↓
Default Security Configuration
↓
Authentication Required
```

主なデフォルト動作

- Spring Security 関連の Auto Configuration を適用
- デフォルトユーザーを生成
    - Username: `user`
- 一時 Password を生成
- リクエストに認証を要求
- デフォルトログイン画面を提供

## 5. Request Flow

### 認証前

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

### 認証後

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

- HTTP リクエストは Controller に直接渡されない
- Spring Security が Controller より先にリクエストを処理
- 認証状態を確認した後、リクエストを Controller に渡すかどうかを判断

```text
Request
↓
Spring Security
↓
Controller
```

- 今後学習する `SecurityFilterChain`　の基本的な出発点

## 7. Key Concepts

### Auto Configuration

- Spring Boot の自動設定機能
- classpath や Bean などの状態を基準に必要な設定を適用
- Spring Security の依存関係が存在する場合、 デフォルト Security 設定を適用

### Authentication

- ユーザーの身元を確認する処理
- 「ユーザーは誰か？」
- 認証されていないユーザーが保護されたリソースにアクセスする場合、認証を要求

### Authorization

- 認証済みユーザーのアクセス権限を確認する処理
- 「このリソースにアクセスする権限があるか？」

## 8. What I Learned

- Spring Security の依存関係を追加するだけでデフォルトの Security 設定を適用
- Security 設定を直接作成しなくても認証機能が動作
- デフォルトユーザーと一時 Password を自動生成
- リクエストは Controller より先に Spring Security を通過
- 次の学習内容
    - `SecurityFilterChain`　を直接設定
    - 公開 URL と認証が必要な URL　を区別

