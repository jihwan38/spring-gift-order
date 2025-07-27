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
- [x] 카카오 로그인의 서비스로직을 담당하는 KakaoLoginService 구현
  - [x] 인가 코드를 통해 카카오의 엑세스 토큰을 반환받는 메서드 구현
  - [x] 카카오 사용자 정보를 얻을 수 있는 getUserInfo 추가
  - [x] MemberRepository에서 카카오 회원이 있는지 조회하여 반환하거나 없을 경우 DB에 저장 후 반환하는 registerOrLoginUser 추가 
  - [x] 기존 TokenProvider 를 이용해 엑세스 토큰을 생성하여 반환하도록 loginUsingKakao 추가
  - [x] 카카오 메시지 수신 동의를 했는지 확인하는 checkTalkMessageAgree 추가
- [x] 회원 로그인 방식을 두 개로 나뉘기 위해 Member와 관련된 전체적인 코드 변경
- [x] 커스텀 예외 클래스(`KakaoClientException`, `KakaoServerException`) 추가 및 적용
- [x] KakaoLoginService에서 사용하는 RestClient에 Timeout 설정
- [x] KakaoLoginService 테스트 코드 추가
## 1단계 Merge 이후 리뷰 반영
- [x] .gitignore이 아닌 환경변수 사용하여 RestAPI key 숨기도록 하기
## 2단계 - 주문하기
### 기능 목록
- [x] Order API를 위한 기본 틀 구현
  - [x] OrderRequestDto, OrderResponseDto 구현
  - [x] Order 엔티티 구현
  - [x] OrderController, OrderService, OrderRepository 생성