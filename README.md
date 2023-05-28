# 개요
jam(초성게임)  
‘ㅇㄱ’, ‘ㅅㅈ’ 같은 자음을 주제로 한사람 한사람 단어를 말하며 이어나가며 마지막에 단어를 말하지 못한 사람이 지게 되는 혹은 가장 많이 말하는 사람이 이기는 게임입니다.

# MainDB(Mysql)
### ERD
https://www.erdcloud.com/d/tBybEs5hsPZTwpuGe
![image](https://user-images.githubusercontent.com/68364917/236725504-9acdd424-526a-432e-bde7-4290a0380623.png)

# MemoryDB(redis)
### 사용할 기능들
#### 게임 내 중복단어 체크
<pre>
{
    "sameword::${roomId}" : [
        {"userId": 유저 고유번호, "nickname": "유저 닉네임", "word": "작성단어"},
        ...
    ]
}
</pre>

#### 참가자 수
<pre>
{
    "participant::${roomId}": 참가자 수
}
</pre>

#### 게임 내 현재 순서
<pre>
{
    "gameOrder::${roomId}": 현재 순서
}
</pre>

#### 게임 내 채팅(신고 기능 추가 되면 저장할 예정)
<pre>
{
    "chat::${roomId}": [
        {"userId": 유저 고유번호, "nickname": "유저 닉네임", "message": "작성단어", "createdAt":"작성시간"},
        ...
    ]
}
</pre>

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
- [ ] 금지 단어 체크

#### 게임 마무리
- [ ] 우승자 결정

---

### 🚪 게임 방
#### 방 생성
- [ ] 여러가지 조건에 맞춰 방 만들기
    - [x] 참가자 생성 및 redis 사용하여 참가자 수 적용
    - [x] 인원수 설정
    - [ ] 비밀번호

#### 방 조회
- [ ] 방 검색 및 필터 기능
    - [ ] 검색 - elastic search 사용
    - [x] 방 조회 및 필터querydsl 사용

#### 방 입장
- [ ] 참가자 방 입장
    - [ ] 참가자 수 redis 및 db로 관리
    - [ ] 방 입장 동시성 이슈 해결 (lock 구현)

#### 방 삭제
- [x] 방 삭제 (상태 변경)

--- 

### ✅ 사전 단어 DB 관리
#### 단어 저장
- [ ] memoryDb(Redis), mainDb(Mysql)에서 사용자가 입력한 단어 확인
- [ ] 단어가 Db에 없으면 사전 API(Naver Search API) 요청 후 단어 확인
- [ ] 사용자가 입력한 단어 mainDb, memoryDb에 저장

---
 
### 👥 유저
- [x] 카카오 소셜 로그인 or 네이버 소셜 로그인
- [x] JWT, Spring Security
- [x] 앱 자체 회원가입, 로그아웃, 회원탈퇴 

# 그 외 우선 순위 낮은 기능들
### 👥 유저
- [ ] 블랙리스트 지정
- [ ] 친구(양방향)
    - [ ] 친구 추가
    - [ ] 친구 초대
    - [ ] 친구 프로필 보기

### 🎮 게임
- [ ] 문제 틀린 사람은 탈락처리 (순서전)
- [ ] 단어 말한사람 말한 점수 추가 (난타전)
- [ ] 힌트 주기

### 🚪 게임 방
- [ ] 초대하기
- [ ] 개인전, 팀전
- [ ] 해당 방 블랙리스트 입장 불가(redis 사용)


### 기능 외 성능관련
- [ ] db 인덱싱 적용
- [ ] 검색 기능 elactic search 적용
- [ ] redi

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
