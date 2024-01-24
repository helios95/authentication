# AS-IS
- 현재는 G.Round의 모든 백엔드 서버에서(런처, 클라이언트, 노티, 어드민) 무기한 토큰 발급하고 만료 관리안되고 있음
- 프론트 인증 요청 수신 시 클라이언트에서 jwt 토큰 발급해서 포인트, 노티 api 서버로 전달함.(런처 api는 client내에 구현됨)
- 인가 서비스 제공하지 않음. 유저 등급을 1~5로 분류하여 최소한의 인가 사용. 권한 꼬이는 현상 발생중

# TO-BE  
# 인증 (Authentication)
## 인증 서버를 따로 둔 이유?
인증, 인가 확인 작업을 따로 서버로 만들어 사용하는 이유는? 
- 대용량 웹서버 구축을 위한(인증,인가에 대한 요청이 많아지게 될 것이고 이로 인한 서버 부하를 막기 위해서) 인증, 인가 서버 따로 분리
- 또한 보안 강화를 위해서 인증,인가 서버와 해당 애플리케이션 서버를 따로 두어 관리. 대입식 공격 시 인증 서버에서만 부하 받음
  
## 구성
인증, 인가 관련 작업을 처리하는 서버
- 인증 : G.Round client에서 인증 요청이 수신 시 여기서 처리를 진행하고 jwt 생성 및 redis 저장 후 Access Token 반환
- 인가 : G.Round client에서 인가 요청시 DB에서 권한 정보 확인 후(기획팀 정책 수립) 접근 허용

## 해당 구조의 흐름(authentication - 인증)
![image](https://github.com/yalooStore/yalooStore-auth/assets/81970382/c254b6ec-642c-43a3-8faa-6ebd613f1d6c)

## 설명
### 인증 작업
인증 작업은 회원 로그인과 같이 인증이 필요한 경우 사용. 이때 회원 인증을 위해서는 아래와 같이 3개의 서버가 동작. 자세한 설명 아래 참고

#### G.Round front (react)
  - G.Round 로그인 시에 사용하는 로그인 폼에 작성한 회원 아이디, 비밀번호를 `Http request`로 인증 요청
  - `authenticationFilter`에서 해당 요청을 낚아 채 authentication 객체로 만들어 다음 로직으로 전달.(authentication 객체를 생성한 뒤 인증 과정을 위임)
    - 인증에 성공한 경우에 successfulAuthentication() 메소드 호출
  - `authenticationManager`는 auth 서버와 통신을 통해서 인증 받은 사용자라면 jwt 관련 정보를 넘겨 받아 인증 서비스 진행
    - 이때 API 서버와도 통신하여 해당 회원 정보를 넘겨받는 작업을 추가적으로 진행
    - 해당 정보를 직렬화한 클래스를 사용해서 redis에 객체 형식으로 저장

#### auth server
  - 인증 관련 처리 요청 수행 서버
  - `authenticationFilter`를 사용해 해당 객체를 authentication 객체로 다음 작업으로 넘겨줍니다.
    - 이때 `setFilterProcessesUrl`로 설정해둔 경로를 통해 요청이 들어오면 해당 필터를 사용해서 인증 작업 진행.(약간 컨트롤러 처럼 해당 경로에 들어온 요청을 처리함)
  - `UserDetailsService`를 사용해서 해당 request와 저장된 회원 정보가 일치하는지 확인
    - 이때 실제 회원 정보를 저장한 데이터베이스와 관련된 서버는 API server로 이 작업을 위해서 auth server와 API server가 통신하여 회원 정보가 담긴 dto 객체를 넘겨받아 확인할 수 있게 합니다.
 - UserDetailsService에서 넘겨받은 회원 아이디와 저장된 회원 아이디가 일치하는 회원이 있는지를 확인하는 작업을 하고, `AuthenticationProvider`에서 해당 회원의 비밀번호가 맞는지 확인
 - 인증 실패 시 : 회원 정보가 잘못된 경우라면 해당 필터에서는 `FailureHandler`를 사용해서 해당 로그인 폼으로 redirection
 - 인증 성공 시: 회원 정보가 일치하고 인증에 성공했다면 `successfulAuthentication()` 메소드를 사용해 jwt - accessToken, refreshToken을 발급하고 해당 아이디를 uuid로 만들어 해당 정보들을 uuid : {hashKey:hashValue} 형태로 redis에 저장
 - successfulAuthentication()
   - jwt accessToken, refreshToken 발급
   - Header에 uuid, token expired time, authentication 정보(jwt access Token)전달
   - redis 저장
 - redis 저장 정보 종류
     - access token
     - refresh token
     - login id(유저 식별자)
     - principal(유저에게 부여된 권한 종류)  

 - Redis 사용 이유
   - 토큰 유효기간 지정 
     - 발급한 토큰을 mysql에 저장 시 토큰 만료를 위해 주기적으로 삭제하는 로직필요(스케줄러)하나 redis는 자동으로 TTL 만료 기능있음
   - 성능
     - 디스크 기반의 mysql 대비 인메모리방식으로 성능 효율 높음
   - 안정성
     - redis 주저앉았을 때 재로그인만 하면 됨
   
#### API server
  - 실제 데이터베이스와 연결된 서버로 해당 회원 정보를 조회할 때 작성한 API를 통해서 확인할 수 있게 했다.
  - 인증 서버에서 해당 로그인 아이디를 통한 회원이 있는지 조회를 할 때 작성한 api를 통해서 해당 정보를 주고 받을 때 사용한다.
  - 클라이언트 서버(front)에서 인증 작업이 완료된 회원의 아이디를 통해서 회원 정보를 주고 받을 때 사용한다.

# 인가(Authorization)
## 구성

## 해당 구조 흐름

## 설명
### 인가 작업
#### front server
#### auth server
#### API server

## 인가 작업 url pattern





