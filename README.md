# 📌 AI 활용 비즈니즈 프로젝트 (2NE1)

## 🎯 팀원 역할분담
<table>
  <tr>
    <th>
      <a href="https://github.com/Leewon2" target="_blank">
        이원희&lt;팀장&gt;
      </a>
    </th>
    <th>
      <a href="https://github.com/dkki4887" target="_blank">
        이채연
      </a>
    </th>
    <th>
      <a href="https://github.com/leeseowoo" target="_blank">
        이서우
      </a>
    </th>
    <th>
      <a href="https://github.com/ckdrmsdl9999" target="_blank">
        윤창근
      </a>
    </th>
    <th>
      <a href="https://github.com/jjsh0208" target="_blank">
        전승현
      </a>
    </th>
  </tr>
  <tr>
    <td>
      <img src="https://github.com/Leewon2.png" width="150" alt="이원희 팀장">
    </td>
    <td>
      <img src="https://github.com/dkki4887.png" width="150" alt="이채연">
    </td>
    <td>
      <img src="https://github.com/leeseowoo.png" width="150" alt="이서우">
    </td>
    <td>
      <img src="https://github.com/ckdrmsdl9999.png" width="150" alt="윤창근">
    </td>
    <td>
      <img src="https://github.com/jjsh0208.png" width="150" alt="전승현">
    </td>
  </tr>
  <tr>
    <th>본인 맡은 역할 작성</th> <!-- 원희 -->
    <th>본인 맡은 역할 작성</th> <!-- 채연 -->
    <th>본인 맡은 역할 작성</th> <!-- 서우 -->
    <th>본인 맡은 역할 작성</th> <!-- 창근 -->
    <th>User<br> DeliveryAddress <br> Spring Security</th> <!-- 승현 -->
  </tr>
</table>

## 🏗 서비스 구성 및 실행 방법
### 💾 환경 설정
```bash
git clone https://github.com/사용자명/레포명.git
cd 레포명
```

------
## 📖 프로젝트 목적/상세

개발 주문 관리 플랫폼을 스프링 부트 기반의 모놀리식 어플케이션을 개발합니다.


------
## 📌 ERD

![Image](https://github.com/user-attachments/assets/f144479b-b3e2-47f8-8ca2-6c9f0beb9f0f)

------

## ☁️ 인프라 설계서
![Image](https://github.com/user-attachments/assets/30111de9-b5b7-47f3-9a0d-b9e71399e04c)

------
## 🛠 기술 스택
## Backend
- **Framework**: Spring Boot 3.x
  <!-- Spring Boot 최신 버전 사용 -->
- **Database Access**: Spring Data JPA , QueryDSL
  <!-- ORM 프레임워크로 데이터베이스와의 연동을 쉽게 처리 -->
- **Security**: Spring Security 6.x
  <!-- 인증과 인가를 위한 보안 모듈 -->
- **API Documentation**: Swagger (Springdoc OpenAPI)
  <!-- API 문서를 자동 생성해주는 Swagger 도구 -->
- **WebSocket**: 실시간 데이터 통신 적용
  <!-- 실시간 통신을 위해 WebSocket을 사용 -->
- **REST API**: RESTful API 설계
  <!-- REST 아키텍처 스타일에 따른 API 설계 -->

## Database
- **Primary DB**: PostgreSQL
  <!-- 주 데이터베이스로 사용 -->
- **In-memory DB**: H2 ( 통합 테스트 환경 )
  <!-- 테스트 및 개발 환경에서 사용하는 인메모리 데이터베이스 -->

## Server
- **Application Server**: Apache Tomcat 9.0
  <!-- 서블릿 컨테이너로 사용하는 Tomcat 서버 -->

## Authentication
- **Token-Based Authentication**: JWT (JSON Web Token)
  <!-- 토큰 기반 인증 방식으로 JWT 사용 -->

## Etc
- **외부 API 연동**
    - 사용한 외부 API 리스트 (예: 결제, 지도, SNS 로그인 등)
    - 예: **Google OAuth2**, **Kakao API**, **PayPal API** 등
  <!-- 프로젝트에서 연동한 외부 API에 대한 설명 -->

