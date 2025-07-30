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
- [x] Option의 quantity 를 줄일 수 있는 기능 구현
  - Product의 decreaseOptionQuantity 로직 수정
  - Option의 decreaseOptionQuantity 로직 수정(주문 수량이 재고보다 많을 경우 예외를 던짐)
- [x] Order의 주문하기 기능 구현
  - [x] OptionController에 order 메서드 구현
  - [x] `/api/orders/**` 경로에 로그인 인터셉터 적용
  - [x] OrderService에 orderProduct 메서드 구현
- [x] 토큰 정보 저장 및 업데이트 기능 구현
  - [x] UserKakaoToken entity 추가
  - [x] UserKakaoTokenRepository 추가
  - [x] KakaoLoginService 의 loginUsingKakao 메서드에 토큰 저장 및 업데이트 로직 추가
- [x] 상품 주문 시 주문 내역을 Kakao의 나에게 보내기 API를 통해 수령인에게 전달하는 기능 구현
  - [x] UserKakaoToken에 엑세스 코인 getter 추가하기
  - [x] application-kakao.properties에 Kakao의 talk_message 기능을 사용할 수 있는 URI 경로 추가하기
  - [x] 카카오의 메시지 기본 템플릿(텍스트) 사용을 위해 MessageLinkDto 및 MessageTemplateDto 생성
  - [x] KakaoMessageService 추가 및 수령인에게 메시지 보내기 기능 구현
  - [x] OrderService 에서 Order 저장 후 KakaoMessageService의 sendMessage 호출하도록 로직 변경
  - [x] 주문과 카카오 메시지 전송 로직을 이벤트 기반으로 분리
- [x] 주문하기 기능에 동시성 문제 해결을 위해 @Version(Option) 및 @Retryable(OptionService) 적용
- [x] OrderService에 대한 테스트 추가

