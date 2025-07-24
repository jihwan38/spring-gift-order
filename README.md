# spring-gift-order

## 1단계 - 카카오 로그인
### 기능 목록
- [x] `.gitignore` 설정을 통해 앱 키가 유출되지 않도록 함
- [x] application-oauth.properties 파일을 통해 키 설정 추가
- [x] 카카오 로그인 화면 구현
  - [x] 카카오 로그인 페이지를 위한 html 파일 추가
  - [x] `/kakao/login` URL 에 대한 컨트롤러 구현
- [x] 카카오가 Redirect URI 로 설정된 `http://localhost:8080`로 반환하는 인가코드를 처리하는 컨트롤러 구현
- [x] 카카오가 반환하는 엑세스 토큰에 대한 정보를 담을 KakaoTokenResponseDto 구현
- [ ] 카카오 로그인의 서비스로직을 담당하는 KakaoLoginService 구현
  - [x] 인가 코드를 통해 카카오의 엑세스 토큰을 반환받는 메서드 구현
