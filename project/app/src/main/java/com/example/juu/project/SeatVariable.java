package com.example.juu.project;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.example.juu.project.MainActivity1.activity1;
import static com.example.juu.project.MainActivity1.managerDB;


//좌석 발급시 사용자에게 입력받은 변수들을 임시 저장하는 곳
public class SeatVariable {
    private Button button; //버튼
    private String phoneNumber; //전화번호(SeatDB / WaitDB 전화번호 전부 저장됨)
    private LocalDateTime endDateTime; //종료시간
    private int id; //전화번호와 일치하는 레코드 아이디
    private int selectedHour; //타임피커에서 선택된 '시간'
    private int selectedMin; //타임피커에서 선택된 '분'
    private boolean flag = false; //연장 여부
    private long minutesNowFromEndDateTime; //현재 ~ endDateTime 까지의 '분'
    private int waitNumber; //대기번호
    private TextView remainSeatMain1; //메인1 텍스트 박스

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("h시 m분 a"); //LocalDateTime의 출력 형태 지정

    public SeatVariable() {}

    public void setButton(Button button) {
        this.button = button;
    }

    public Button getButton() {
        return button;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getEndDateTimeForString() {
        return endDateTime.format(dateTimeFormatter);
    }

    public int getSelectedHour() {
        return selectedHour;
    }

    public int getSelectedMin() {
        return selectedMin;
    }

    //신규 발급시의 종료시간 계산
    public void setEndDateTime(int selectedHour, int selectedMin) {
        this.selectedHour = selectedHour;
        this.selectedMin = selectedMin;

        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //현재 시간
        now = now.plusHours(selectedHour).plusMinutes(selectedMin);

        //현재시간 + 선택시간 계산
        if(now.getMinute() >= 60) {
            now = now.plusHours(1).minusMinutes(60);
        }
        if(now.getHour() >= 24) {
            now = now.plusDays(1).minusHours(24);
        }

        this.endDateTime = now; //종료시간 저장됨
    }

    //전화번호에 일치하는 행의 종료시간으로 갱신
    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    //연장시의 종료시간 계산
    public void setEndDateTimeForExtension(int selectedHour, int selectedMin) {
        this.selectedHour = selectedHour;
        this.selectedMin = selectedMin;

        //연장시 타임피커 선택과 동시에 종료 시간 계산 후 저장(startTime = 기존 종료시간 + 선택된 시간)
        LocalDateTime startTime = endDateTime.plusHours(selectedHour).plusMinutes(selectedMin);

        //기존 종료시간 + 선택시간 계산
        if (startTime.getMinute() >= 60) {
            startTime = startTime.plusHours(1).minusMinutes(60);
        }
        if (startTime.getHour() >= 24) {
            startTime = startTime.plusDays(1).minusHours(24);
        }

        this.endDateTime = startTime;
        System.out.println("계산된 종료시간 : " + getEndDateTimeForString());
    }

    //발급시 - 현재 ~ 종료시간까지 남은시간 String 타입으로 반환
    public String getLeftTimeNowFromEndDateTime() {
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //현재 시간

        if(now.getHour() >= endDateTime.getHour())
            minutesNowFromEndDateTime = endDateTime.until(now, ChronoUnit.MINUTES);
        else
            minutesNowFromEndDateTime = now.until(endDateTime, ChronoUnit.MINUTES);

        //음수 -> 양수로 변환
        if(minutesNowFromEndDateTime < 0)
            minutesNowFromEndDateTime *= -1;

        //'시간', '분' 계산
        int m = (int)minutesNowFromEndDateTime;
        int hour = m / 60;
        int minute = m - (60 * hour);

        if(hour == 0)
            return minute + "분";
        else
            return hour + "시간 " + minute + "분";
    }

    public void setRemainSeatMain1(TextView remainSeatMain1) { //Main1 TextView 저장
        this.remainSeatMain1 = remainSeatMain1;
    }

    public void showRemainSeat() {
        activity1.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                managerDB.setRemainSeat(); //잔여석 갱신
                String str = "잔여석 : " + managerDB.getRemainSeat() + " 개";
                SpannableStringBuilder ssb = new SpannableStringBuilder(str);
                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 6, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                remainSeatMain1.setText(ssb); //잔여석 표기
            }
        });
    }

    public void setWaitNumber(int waitNumber) {
        this.waitNumber = waitNumber;
    }

    public int getWaitNumber() {
        return waitNumber;
    }
}
