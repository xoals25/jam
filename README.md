# 개요
jam(초성게임)  
‘ㅇㄱ’, ‘ㅅㅈ’ 같은 자음을 주제로 한사람 한사람 단어를 말하며 이어나가며 마지막에 단어를 말하지 못한 사람이 지게 되는 혹은 가장 많이 말하는 사람이 이기는 게임입니다.

# ERD
https://www.erdcloud.com/d/tBybEs5hsPZTwpuGe
![image](https://user-images.githubusercontent.com/68364917/236453794-864f1a0a-9880-4ae6-9c8e-1d82972f4e78.png)


# 필수 기능
### 🎮 게임
#### 게임 시작 전
- [ ] 게임 준비
- [ ] 게임 시작
- [ ] 채팅
#### 게임 시작 후
- [ ] 게임 초성 제공 (redis 사용)
- [ ] 채팅 - 초성 이어가기
    - 자기 순서에만 채팅을 할 수 있다.
    - 사전에 등록되어 있는 단어 인지 판별하기
- [ ] 올바른 단어를 말한 사람 점수 추가
- [ ] 올바른 단어 체크 (사전 DB 관리 목록에 자세히 작성)
- [ ] 중복 단어 체크
#### 게임 마무리
- [ ] 우승자 결정

---

### 🚪 게임 방
#### 방 생성
- [ ] 여러가지 조건에 맞춰 방 만들기
    - [ ] 인원수 설정
    - [ ] 비밀번호

#### 방 입장
- [ ] 방 검색 및 필터 기능
    - [ ] 검색 - elastic search 사용
    - [ ] 방 조회 및 필터querydsl 사용

--- 

### ✅ 사전 단어 DB 관리
#### 단어 저장
- [ ] memoryDb(Redis), mainDb(Mysql)에서 사용자가 입력한 단어 확인
- [ ] 단어가 Db에 없으면 사전 API(Naver Search API) 요청 후 단어 확인
- [ ] 사용자가 입력한 단어 mainDb, memoryDb에 저장

---
 
### 👥 유저
- [ ] 카카오 소셜 로그인 or 네이버 소셜 로그인
- [ ] JWT, Spring Security

# 그 외 우선 순위 낮은 기능들
### 👥 유저
- [ ] 블랙리스트 지정
- [ ] 친구

### 🎮 게임
- [ ] 문제 틀린 사람은 탈락처리 (순서전)
- [ ] 단어 말한사람 말한 점수 추가 (난타전)
- [ ] 힌트 주기

### 🚪 게임 방
- [ ] 초대하기
- [ ] 개인전, 팀전
- [ ] 해당 방 블랙리스트 입장 불가(redis 사용)

# 필요한 기술 스택
- Spring-boot(Java)
- MySQL
- Redis
- WebSocket
- JPA
- Querydsl
- Naver Search API
- Kakao Oauth API or Naver Oauth API
- ElasticSearch
- JWT
- Spring Security
- Docker
- GCP or AWS or Firebase

### 게임하기
- websocket (채팅)
- naver search api (유저가 작성한 단어의 유무 체크)
### 방 만들기
- JPA
### 방 참여하기
- querydsl
- elasticsearch
- redis (블랙리스트 체크)
### 유저
- JWT
- Spring Security
- Kakao Oauth API or Naver Oauth API
