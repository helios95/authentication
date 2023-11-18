# yalooStore-auth
- spring security - authentication 관련 정리 레포지토리 : https://github.com/yeomyaloo/spring-security-practice
# 인증 (Authentication)
## 인증 서버를 따로 둔 이유?
인증, 인가 확인 작업을 따로 서버로 만들어 사용하는 이유는? 
- 작업을 진행하다보면 인증,인가에 대한 요청이 많아지게 될 것이고 이로 인한 서버 부하를 막기 위해서 인증, 인가 관련 서버를 따로 두었습니다.
- 또한 보안 강화를 위해서 인증,인가 서버와 해당 애플리케이션 서버를 따로 두어 관리합니다. 이는 해커가 공격을 하더라도 조금 더 안전하게 정보를 지킬 수 있다는 장점이 있어 이를 따로 두게 됐습니다.
  
## 구성
인증, 인가 관련 작업을 처리하는 서버입니다.
- 인증 작업은 front server(client)에서 요청이 들어오면 여기서 처리를 진행하고 jwt, redis 저장 작업을 진행하여 넘겨줍니다.
- 인가 작업 역시 해당 페이지에 인가 작업이 필요하다면 이곳에서 진행하여 넘겨줄 수 있게 합니다.

## 해당 구조의 흐름(authentication - 인증)
![image](https://github.com/yalooStore/yalooStore-auth/assets/81970382/c254b6ec-642c-43a3-8faa-6ebd613f1d6c)

## 설명
### 인증 작업
인증 작업은 회원 로그인과 같이 인증이 필요한 경우에 사용합니다. 이때 회원 인증을 위해서는 아래와 같이 3개의 서버가 동작하고 이에 대한 자세한 설명을 아래에 작성했습니다.
* 해당 작업은 msa의 구조로 이루어져있는 프로젝트에 적용한 예시로 단일 서버라면 해당 작업을 한 프로젝트 내에서 진행해도 무방합니다.(위에 링크로 달아둔 레포지토리에도 해당 작업을 진행하였습니다.)

#### front server
  - 로그인 시에 사용하는 로그인 폼에 작성한 회원 아이디, 비밀번호를 `Http request`로 인증 요청을 보냅니다.
  - `authenticationFilter`에서 해당 요청을 낚아 채 authentication 객체로 만들어 다음 로직으로 보내줍니다.(authentication 객체를 생성한 뒤 인증 과정을 위임)
    - 이때 단일 서버에서는 인증에 성공한 경우에 successfulAuthentication()를 작성해뒀다면 해당 메소드가 작동합니다. (이 프로젝트에서는 해당 successfulAuthentication()를 사용한 곳이 auth server 입니다.)
  - `authenticationManager`는 auth 서버와 통신을 통해서 인증 받은 사용자라면 jwt 관련 정보를 넘겨 받아 개발자가 원하는 다음 작업을 이곳에서 진행합니다.
    - 이때 API 서버와도 통신하여 해당 회원 정보를 넘겨받는 작업을 추가적으로 진행해줍니다.
    - 마지막으로 해당 정보를 직렬화한 클래스를 사용해서 redis에 객체 형식으로 저장할 수 있게 합니다.
  - **참고로 위의 작업은 모두 front server와 관련된 spring security flow 입니다. (auth server는 또 다른 spring security flow임)**
#### auth server
  - 인증 관련 처리 요청이 해당 url로 들어오면 인증 관련 처리를 대신해주는 서버입니다.
  - `authenticationFilter`를 사용해 해당 객체를 authentication 객체로 다음 작업으로 넘겨줍니다.
    - 이때 `setFilterProcessesUrl`로 설정해둔 경로를 통해 요청이 들어오면 해당 필터를 사용해서 인증 작업을 진행합니다.(약간 컨트롤러 처럼 해당 경로에 들어온 요청을 처리함)
  - `UserDetailsService`를 사용해서 해당 request와 저장된 회원 정보가 일치하는지 확인하는 작업을 진행합니다.
    - 이때 실제 회원 정보를 저장한 데이터베이스와 관련된 서버는 API server로 이 작업을 위해서 auth server와 API server가 통신하여 회원 정보가 담긴 dto 객체를 넘겨받아 확인할 수 있게 합니다.
 - UserDetailsService에서 넘겨받은 회원 아이디와 저장된 회원 아이디가 일치하는 회원이 있는지를 확인하는 작업을 하고, `AuthenticationProvider`에서 해당 회원의 비밀번호가 맞는지 확인하는 작업을 합니다.
 - 인증 실패 시 : 회원 정보가 잘못된 경우라면 해당 필터에서는 `FailureHandler`를 사용해서 해당 로그인 폼으로 다시 돌아갈 수 있게 하였습니다.
 - 인증 성공 시: 회원 정보가 일치하고 인증에 성공했다면 `successfulAuthentication()` 메소드를 사용해 jwt - accessToken, refreshToken을 발급하고 해당 아이디를 uuid로 만들어 해당 정보들을 uuid : {hashKey:hashValue} 형태로 redis에 저장합니다.
 - jwt 발급과 redis 저장과 더불어서 성공
#### API server
  - 실제 데이터베이스와 연결된 서버로 해당 회원 정보를 조회할 때 작성한 API를 통해서 확인할 수 있게 했다.
  - 인증 서버에서 해당 로그인 아이디를 통한 회원이 있는지 조회를 할 때 작성한 api를 통해서 해당 정보를 주고 받을 때 사용한다.
  - 클라이언트 서버(front)에서 인증 작업이 완료된 회원의 아이디를 통해서 회원 정보를 주고 받을 때 사용한다.
## 인증 작업 url pattern
![image](https://github.com/yalooStore/yalooStore-auth/assets/81970382/54a3d825-e501-468c-b9b8-a6eeeaea1010)
-----
# 인가(Authorization)
## 구성

## 해당 구조 흐름

## 설명
### 인가 작업
#### front server
#### auth server
#### API server

## 인가 작업 url pattern





