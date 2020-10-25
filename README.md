# 수면실(S-nap zone)
> *team project for 2019 Software Competition Tournament*

2017년 성신여자대학교의 편의시설로 마련된 수면실은 다음과 같은 문제를 가지고 있었습니다.

 - 자주 여닫히며 소음을 발생하는 문이 수면을 방해함
 - 장시간 자리를 독점하는 것을 막을 수 없음
 - 핸드폰 사용자의 불빛이 수면을 방해함

이에 수면실 사용에 편의성을 가져다주고, 불만 사항을 해결할 수 있는 수면실 어플리케이션을 고안하게 되었습니다.

<br />

# 개발 기간
2019.08.05 ~ 2019.08.23

<br />

# 개발 환경

 - Android Studio 3.5.1
 - SQLite
 
<br />

# Screen Shot

<br />

### 1. 잔여석이 있는 경우
<div>
<img src="https://user-images.githubusercontent.com/55652161/97111165-9ae4a480-1720-11eb-9b6a-2a89c82994e7.png" width="300">
</div>

 - 좌석을 발급받을 수 있는 화면
 <br />
 
<div>
<img src="https://user-images.githubusercontent.com/55652161/97111477-4f32fa80-1722-11eb-9117-6c737272f94a.jpg" width="300">
<img src="https://user-images.githubusercontent.com/55652161/97111279-54dc1080-1721-11eb-8f16-d930af2e7db8.png" width="300">
</div>

 - 전화번호로 좌석을 발급
 - 최소 15분 ~ 최대 5시간까지 대여 시간 선택 가능
 <br />

<div>
<img src="https://user-images.githubusercontent.com/55652161/97111566-dda77c00-1722-11eb-9f3b-acb41da9c23b.png" width="300">
<img src="https://user-images.githubusercontent.com/55652161/97111568-e13b0300-1722-11eb-8933-e7b713184232.png" width="300">
</div>

- 좌석 연장 버튼은 발급중인 좌석이 없을 경우 동작하지 않음
- 최소 15분 ~ 최대 2시간까지 연장 가능하며, 한 번 연장한 좌석은 더 이상 연장할 수 없음


<br />

### 2. 잔여석이 없는 경우
<div>
<img src="https://user-images.githubusercontent.com/55652161/97111166-a041ef00-1720-11eb-8574-cf502444fdc1.png" width="300">
</div>

- 대기 번호를 발급받을 수 있는 화면
<br />

<div>
<img src="https://user-images.githubusercontent.com/55652161/97111597-19424600-1723-11eb-81da-4135a0304346.png" width="300">
<img src="https://user-images.githubusercontent.com/55652161/97111600-1c3d3680-1723-11eb-8014-613691d56880.png" width="300">
</div>

- 전화번호로 대기번호를 발급
- 대기번호에 해당하는 대기시간을 부여함
<br />

<div>
<img src="https://user-images.githubusercontent.com/55652161/97111615-2c551600-1723-11eb-8a40-d4d0c8698241.png" width="300">
</div>

- 대기자에게 다음과 같은 정보를 전송
	- 대기 번호 발급시의 정보
	- 대기 시간 변동 사항
	- 본인의 발급 차례일 경우
	- 시간 내 좌석을 발급받지 않을 경우 취소 안내
<br />

### 3. 모두가 편안한 수면실을 위한 "매너 지킴이 서비스"
<img src="https://user-images.githubusercontent.com/55652161/97111793-11cf6c80-1724-11eb-968c-329b14a9f923.png" width="300">

- 좌석 사용자에게 **규칙 준수 메시지**를 전송해주어, 직접 대면하지 않고 문제 제기를 가능하게 함
<br />

# Database
###  1. System 
| password | systemStartTime | systemEndTime |
|:---:|:---:|:---:|
| 비밀번호 | 수면실 운영 시작 시간 | 수면실 운영 마감 시간 |
| Int| LocalDateTime|LocalDateTime|
<br />

### 2. Seat
|phoneNum | startDateTime | endDateTime | btnName | btnId | flag |
|:---:|:---:|:---:|:---:|:---:|:---:|
|전화번호|좌석 발급 시간|좌석 반환 시간|좌석 번호|좌석ID|연장여부|
|Int|LocalDateTime|LocalDateTime|String|Int|Boolean|
<br />

### 3. Wait
|phoneNum | startDateTime | waitNum| waitTime | Timer | sms |
|:---:|:---:|:---:|:---:|:---:|:---:|
|전화번호|대기 번호 발급 시간|대기 번호|대기 시간|3분 타이머|연장여부|
|Int|LocalDateTime|Int|LocalDateTime|Boolean|Boolean|
<br />

### 4. WaitHelper
| numOfSeat| numOfLeftSeat | nextWaitNum | numOfCanIssue|
|:---:|:---:|:---:|:--:|
| 전체 좌석| 잔여석 | 다음에 발급될 대기 번호 | 발급 차례가 된 대기 번호 |
| Int| Int|Int|Int|
<br />

# Contributor
- [윤주연](https://www.github.com/otu165)
- 김수빈
