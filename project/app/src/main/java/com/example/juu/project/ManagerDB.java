package com.example.juu.project;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.telephony.SmsManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import static com.example.juu.project.MainActivity1.activity1;
import static com.example.juu.project.MainActivity1.buttonList;
import static com.example.juu.project.MainActivity1.getSystemEndMinute;
import static com.example.juu.project.MainActivity1.getSystemStartMinute;
import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;
import static com.example.juu.project.MainActivity2.activity2;
import static com.example.juu.project.SendCaution2.sendCaution2;
import static com.example.juu.project.WaitToSeat2.waitToSeat2;

//DB 관리 클래스
public class ManagerDB extends SQLiteOpenHelper {
    //---------------------------------------------------ManagerDB 부분 ----------------------------------------------
    private static ManagerDB managerDB; //매니저 객체

    private static final String CREATE_SYSTEM_TABLE =
            "CREATE TABLE IF NOT EXISTS System (password TEXT, systemStartTime TEXT, systemEndTime TEXT); ";
    private static final String CREATE_ACTIVITY_TABLE =
            "CREATE TABLE IF NOT EXISTS Activity(nowActivity1 TEXT, nowActivity2 TEXT); ";
    private static final String CREATE_SEAT_TABLE =
            "CREATE TABLE IF NOT EXISTS Seat (phoneNumber TEXT, startDateTime TEXT, endDateTime TEXT, buttonName TEXT, buttonId INTEGER, flag TEXT); ";
    private static final String CREATE_WAIT_TABLE =
            "CREATE TABLE IF NOT EXISTS Wait (phoneNumber TEXT, startDateTime TEXT, waitNumber INTEGER, waitTime TEXT, timer TEXT, sms TEXT); ";
    private static final String CREATE_WAITHELPER_TABLE =
            "CREATE TABLE WaitHelper (numberOfSeat INTEGER, numberOfRemainSeat INTEGER, nextWaitNumber INTEGER, numberOfCanIssue INTEGER)"; //대기자리스트 헬퍼 테이블

    private static SQLiteDatabase db = null;
    private static final int DB_VERSION = 1; //버전

    public static synchronized ManagerDB getInstance(Context context) {
        con = context;
        if(managerDB == null) {
            managerDB = new ManagerDB(context); //onCreate() 실행 -> 시스템, 발급, 대기 테이블 생성됨
        }
        return managerDB;
    }

    protected ManagerDB(Context context) {
        super(context, "Manager.db", null, DB_VERSION);
        runTime(); //스레드 실행
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SYSTEM_TABLE); //시스템DB
        db.execSQL(CREATE_SEAT_TABLE); //좌석DB
        db.execSQL(CREATE_WAIT_TABLE); //대기DB
        db.execSQL(CREATE_WAITHELPER_TABLE); //대기헬퍼DB
        db.execSQL(CREATE_ACTIVITY_TABLE); //액티비티DB
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //테이블 삭제
        db.execSQL("DROP TABLE IF EXISTS System");
        db.execSQL("DROP TABLE IF EXISTS Seat");
        db.execSQL("DROP TABLE IF EXISTS Wait");
        db.execSQL("DROP TABLE IF EXISTS WaitHelper");
        db.execSQL("DROP TABLE IF EXISTS Activity");
        //테이블 재생성
        onCreate(db);
    }

    public void deleteEveryData() { //모든 테이블의 전체 튜플 삭제
        db = getWritableDatabase();
        try {
            db.execSQL("DELETE FROM System");
            db.execSQL("DELETE FROM Seat");
            db.execSQL("DELETE FROM Wait");
            db.execSQL("DELETE FROM WaitHelper");
            db.execSQL("DELETE FROM Activity");
        }catch (Exception e) {
            e.printStackTrace();
        }
        initialSystemDB(); //시스템 DB는 항상 가동되어야함
        initialActivityDB(); //액티비티 초기화
        initialWaitHelperDB(); //대기헬퍼 초기화
    }

    public void systemClosed() { //금일 수면실 영업 종료 -> Thread의 종료 포함
        setStop(); //첫번째 스레드 종료
        try { //시스템 제외 모든 테이블 초기화
            db.execSQL("DELETE FROM Seat");
            db.execSQL("DELETE FROM Wait");
            db.execSQL("DELETE FROM WaitHelper");
            db.execSQL("DELETE FROM Activity");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        initialActivityDB(); //액티비티 초기화
        initialWaitHelperDB(); //대기헬퍼 초기화
    }

    public void dropEveryTable() { //테이블 지우고 재생성됨
        db = getWritableDatabase();
        try {
            db.execSQL("DROP TABLE IF EXISTS System");
            db.execSQL("DROP TABLE IF EXISTS Seat");
            db.execSQL("DROP TABLE IF EXISTS Wait");
            db.execSQL("DROP TABLE IF EXISTS WaitHelper");
            db.execSQL("DROP TABLE Activity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        onCreate(db);

        initialSystemDB(); //시스템 DB는 항상 가동되어야 함
        initialActivityDB(); //액티비티 초기화
        initialWaitHelperDB(); //대기헬퍼 초기화
    }

    //-------------------------------------------------- SystemDB 부분 --------------------------- 테이블 생성하자마자 유일한 튜플 존재해야함
    public void initialSystemDB() { //수면실 어플리케이션 기본 설정값(튜플 개수 > 0이면 실행X)
        db = getWritableDatabase();
        try {
            int count = db.rawQuery("SELECT * FROM System",null).getCount(); //테이블의 총 레코드 개수 확인
            if(count == 0)
                db.execSQL(
                        "INSERT INTO System VALUES ('000000', '08시 00분', '20시 00분');" //비밀번호 000000, 8 AM, 8 PM
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSystemPassword(String password) { //시스템 비밀번호 변경
        db = getWritableDatabase();
        try {
            db.execSQL("UPDATE System SET password = '"+password+"' " ); //비밀번호 변경
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSystemPassword() { //시스템 비밀번호 반환
        String password = "";
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT password FROM System",null);
            while(cursor.moveToNext()) {
                password = cursor.getString(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    public void setSystemStartTime(int selectedHour, int selectedMin) { //수면실 오픈시간
        String systemStartTime = String.format("%02d시 %02d분", selectedHour, selectedMin); //substring으로 가져올 수 있도록 format 지정
        db = getWritableDatabase();
        try {
            db.execSQL("UPDATE System SET systemStartTime = '"+systemStartTime+"' "); //systemStartTime 갱신
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSystemStartTime() { //수면실 오픈시간 반환
        String systemStartTime = "";
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT systemStartTime FROM System", null);
            while(cursor.moveToNext()) {
                systemStartTime = cursor.getString(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return systemStartTime;
    }

    public String printSystemStartTime() { //수면실 오픈시간 출력형태로 반환
        String systemStartTime = getSystemStartTime();
        int hour = Integer.parseInt(systemStartTime.substring(0,2));
        if(hour >= 12)
            systemStartTime = (hour - 12) + systemStartTime.substring(2) + " PM";
        else if(hour == 12)
            systemStartTime = hour + systemStartTime.substring(2) + " PM";
        else
            systemStartTime = hour + systemStartTime.substring(2) + " AM";

        return systemStartTime;
    }

    public void setSystemEndTime(int selectedHour, int selectedMin) { //수면실 닫는시간
        String systemEndTime = String.format("%02d시 %02d분", selectedHour, selectedMin); //substring으로 가져올 수 있도록 format 지정

        db = getWritableDatabase();
        try {
            db.execSQL("UPDATE System SET systemEndTime = '"+systemEndTime+"' "); //systemEndTime 갱신
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSystemEndTime() { //수면실 닫는시간 반환
        String systemEndTime = "";
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT systemEndTime FROM System", null);
            while(cursor.moveToNext()) {
                systemEndTime = cursor.getString(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return systemEndTime;
    }

    public String printSystemEndTime() { //수면실 닫는시간 출력 형태로 반환
        String systemEndTime = getSystemEndTime();

        int hour = Integer.parseInt(systemEndTime.substring(0,2));
        if(hour > 12)
            systemEndTime = (hour - 12) + systemEndTime.substring(2) + " PM";
        else if(hour == 12)
            systemEndTime = hour + systemEndTime.substring(2) + " PM";
        else
            systemEndTime = hour + systemEndTime.substring(2) + " AM";
        return systemEndTime;
    }

    //-------------------------------------------------- ActivityDB 부분 ----------------------------------------------
    public void initialActivityDB() { //액티비티 기본 상태 지정
        db = getWritableDatabase();
        try {
            int count = db.rawQuery("SELECT * FROM Activity",null).getCount(); //테이블의 총 레코드 개수 확인
            if(count == 0)
                db.execSQL(
                        "INSERT INTO Activity VALUES ('true', 'false');" //nowActivity1 TEXT, nowActivity2 TEXT, activityChanged TEXT
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isNowActivity1() {
        boolean flag = true;
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT nowActivity1 FROM Activity",null);
            while(cursor.moveToNext()) {
                flag = Boolean.parseBoolean(cursor.getString(0));
                break;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean isNowActivity2() {
        boolean flag = false;
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT nowActivity2 FROM Activity",null);
            while(cursor.moveToNext()) {
                flag = Boolean.parseBoolean(cursor.getString(0));
                break;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void updateActivity() { //메인전환시, 값들 반전
        boolean nowActivity1 = isNowActivity1();
        boolean nowActivity2 = isNowActivity2();
        db = getWritableDatabase();
        try {
            db.execSQL("UPDATE Activity SET nowActivity1 = '"+nowActivity2+"'," +
                    " nowActivity2 = '"+nowActivity1+"' ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    //-------------------------------------------------- SeatDB 부분 --------------------------------------------------
    public void insertToSeatDB(String phoneNumber, LocalDateTime endDateTime, Button button) { //좌석 발급시 데이터 삽입
        String buttonName = button.getText().toString();
        System.out.println("마지막에 삽입될 버튼 이름 : " + buttonName);
        int buttonId = button.getId();
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //한국(서울)기준 현재시간 계산

        db = getWritableDatabase();
        try {
            db.execSQL( //연장여부 기본으로 false
                    "INSERT INTO Seat VALUES ('"+phoneNumber+"', '"+now+"', '"+endDateTime+"', '"+buttonName+"', '"+buttonId+"', '"+false+"')"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity1.findViewById(buttonId).setEnabled(false); //좌석 비활성화 처리
    }

    public void updateSeatDB() { //연장시에 SeatDB 갱신
        db = getWritableDatabase();
        try { //전화번호가 일치하는 행의 종료시간, flag 갱신
            db.execSQL("UPDATE Seat SET endDateTime = '"+ seatVariable.getEndDateTime()+"', flag = '"+true+"' WHERE phoneNumber = '"+ seatVariable.getPhoneNumber()+"' ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFromSeatDB() { //저장된 버튼 정보와 일치하는 행 삭제
        db = getWritableDatabase();
        try {
            db.execSQL("DELETE FROM Seat WHERE buttonName = '"+ seatVariable.getButton().getText().toString()+"' "); //저장된 버튼과 이름(buttonName)이 일치하는 행 삭제
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getFlagFromSeatDB() { //전화번호와 일치하는 행의 연장여부 반환
        String s = null;
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT phoneNumber, flag FROM Seat",null);
            while(cursor.moveToNext()) {
                if(cursor.getString(0).equals(seatVariable.getPhoneNumber())) { //db에 저장되어있는 전화번호와 저장된 전화번호가 같으면
                    s = cursor.getString(1); //flag속성 저장
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(s.equals("true"))
            return true; //연장 불가
        else
            return false; //연장 가능
    }

    public int getSeatSize() { //발급한 사람 수 반환(SeatDB size)
        int count = 0;

        db = getWritableDatabase();
        try {
            count = db.rawQuery("SELECT * FROM Seat",null).getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public void setSeatDisabled() {
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT buttonId FROM Seat",null); //전체 레코드
            while(cursor.moveToNext()) {
                int id = cursor.getInt(0); //buttonId 값 가져옴
                activity1.findViewById(id).setEnabled(false); //id에 해당하는 버튼 찾아서 좌석 비활성화 수행
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //이미 선택된 좌석 비활성화 처리
    public void setSelectedSeatDisabled(ArrayList<Button> buttonList) { //MainWaitIssue2에서 이미 선택된 좌석 비활성화 처리(buttonName으로 비교)
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT buttonName FROM Seat", null); //전체 buttonName 레코드
            while(cursor.moveToNext()) {
                for(Button b : buttonList) { //버튼하나씩 비교
                    if(cursor.getString(0).equals(b.getText())) {
                        waitToSeat2.findViewById(b.getId()).setEnabled(false); //buttonName이 일치하는 버튼의 좌석 비활성화
                        break;
                    }
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //발급중인 좌석 활성화(SendCaution에 이용)
    public void setSelectedSeatEnabled(ArrayList<Button> buttonList) { //SendCaution에서 사용중인 좌석 선택가능 처리
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT buttonName FROM Seat", null); //전체 buttonName 레코드
            while(cursor.moveToNext()) {
                for(Button b : buttonList) { //버튼 비교
                    if(cursor.getString(0).equals(b.getText())) { //좌석 이름이 같을 시
                        sendCaution2.findViewById(b.getId()).setEnabled(true); //buttonName이 일치하는 버튼의 좌석 활성화
                        break;
                    }
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setButtonEnabledWhenSystemClose() {
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT buttonId FROM Seat", null);
            while(cursor.moveToNext()) {
                Button button = activity1.findViewById(cursor.getInt(0));
                button.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getEndDateTIme() { //연장시의 만료시간 출력용
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT phoneNumber, endDateTime FROM Seat WHERE phoneNumber = '"+seatVariable.getPhoneNumber()+"'", null);
            while(cursor.moveToNext()) {
                if(cursor.getString(0).equals(seatVariable.getPhoneNumber())) {
                    seatVariable.setEndDateTime(LocalDateTime.parse(cursor.getString(1)));
                    System.out.println("액티비티에 출력될 시간 : " + cursor.getString(1));
                    break;
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------------------- WaitDB 부분 --------------------------------------------------
    public void insertToWaitDB(String phoneNumber) { //대기번호 발급시 데이터 삽입
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //현재 시간
        db = getWritableDatabase();
        try {
            db.execSQL("INSERT INTO Wait VALUES('"+phoneNumber+"','"+now+"','"+getNextWaitNumber()+"' ,'"+seatVariable.getEndDateTime()+"', '"+false+"', '"+false+"')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateWaitTime() { //연장 및 반환시의 대기자의 waitTime 갱신
        ArrayList<Integer> waitNumberList1 = new ArrayList<>();
        ArrayList<String> phoneNumberList = new ArrayList<>();
        db = getWritableDatabase();
        try {
            //0. timer = true인 애들의 수 세기
            int numberOfTrue = db.rawQuery("SELECT * FROM Wait WHERE timer = '" + true + "'", null).getCount(); //true인 애 수

            //1. 대기자의 대기번호 (순서에 맞춰 저장)
            Cursor cursor_1 = db.rawQuery("SELECT waitNumber FROM Wait ORDER BY WaitNumber", null);
            cursor_1.moveToNext();
            int startWaitNumber = cursor_1.getInt(0);
            int seatSize = getSeatSize();
            int sum = seatSize + startWaitNumber - numberOfTrue; //Seat개수 + 대기번호시작번호 - true개수
            Cursor cursor = db.rawQuery("SELECT phoneNumber, waitNumber FROM Wait WHERE timer = '" + false + "' AND waitNumber < '" + sum + "' ORDER BY waitNumber", null); //timer가 true가 아닌애의
            while (cursor.moveToNext()) {
                waitNumberList1.add(cursor.getInt(1)); //대기자의 대기번호 배열 생성됨
                phoneNumberList.add(cursor.getString(0)); // 대기자의 전화번호 배열 생성
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //2. SeatDB의 종료시간 가져옴
            Cursor cursor1 = db.rawQuery("SELECT endDateTime FROM Seat ORDER BY endDateTime", null); //발급종료시간 기준 정렬
            while (cursor1.moveToNext()) {
                db.execSQL("UPDATE Wait SET waitTime = '" + cursor1.getString(0) + "' WHERE waitNumber = '" + waitNumberList1.get(cursor1.getPosition()) + "'");
                // ****** 대기시간 변동시 문자보내는 코드 ***********
                seatVariable.setEndDateTime(LocalDateTime.parse(cursor1.getString(0)));
                String endTime = seatVariable.getLeftTimeNowFromEndDateTime();
                //해당 사용자에게 대기시간 변동 문자 전송
                String str = "[S-nap zone]\n" + waitNumberList1.get(cursor1.getPosition()) + "번 대기자님의 대기시간이 " + endTime + "으로 변경되었습니다.";
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phoneNumberList.get(cursor1.getPosition()), null, str, sentPI, null);
                // ******* 문자보내는 코드 ***********
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //3. canIssue의 개수만큼 앞에서부터 waitTime = now.plusMinutes(3) 설정
        try {
            LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //현재 시간

            int count = getCanIssue();
            db.execSQL("UPDATE Wait SET waitTime = '" + now + "', timer = '" + true + "' WHERE waitNumber <= '" + count + "' AND timer = '"+false+"' ");
            Cursor cursor5 = db.rawQuery("SELECT waitNumber FROM Wait WHERE timer = '" + true + "'", null);
            while (cursor5.moveToNext()) {
                waitNumberList.add(cursor5.getInt(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFromWaitDB(String phoneNumber) { //대기자가 좌석 발급시 데이터 제거
        db = getWritableDatabase();
        try {
            db.execSQL("DELETE FROM Wait WHERE phoneNumber = '"+phoneNumber+"'"); //전화번호와 일치하는 행 삭제

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getWaitTime() { //대기번호 발급시의 대기시간 계산
        int waitNumber = getWaitSize() + 1;
        String waitTime = "";
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT endDateTime FROM Seat ORDER BY endDateTime", null); //SeatDB 종료시간 기준 오름차순 정렬
            int count = 1;
            while(cursor.moveToNext()) {
                if((count++) == waitNumber) { //행 번호와 대기번호 비교
                    seatVariable.setEndDateTime(LocalDateTime.parse(cursor.getString(0))); //대기자의 waitTime
                    waitTime = seatVariable.getLeftTimeNowFromEndDateTime(); //waitTime 저장
                }
            }
            if(waitTime.equals("")) { //실질적 대기순번이 좌석 개수보다 많은 경우의 대기시간 계산
                cursor.moveToLast(); //커서 마지막 행으로 이동
                seatVariable.setEndDateTime(LocalDateTime.parse(cursor.getString(0))); //가장 늦는 endDateTime 저장
                waitTime = seatVariable.getLeftTimeNowFromEndDateTime() + " 이상";
                seatVariable.setEndDateTime(seatVariable.getEndDateTime().plusDays(1)); //디비에 저장되는 endDateTime의 날짜를 1 증가시킴
                //사용자의 좌석 연장 or 반환시 다시 업데이트 될 것이므로 문제 없음
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return waitTime;
    }

    public int getWaitSize() { //대기인원 반환(현재 레코드 수)
        int count = 0;
        db = getWritableDatabase();
        try {
            count = db.rawQuery("SELECT * FROM Wait",null).getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public void sendIssIn3MinuteSMS (String phoneNumber) {
        String IssIn3Minute = "[S-nap zone]\n좌석을 발급할 수 있습니다.\n3분 안에 좌석 발급을 완료해주시기 바랍니다.";
        SmsManager sms = SmsManager.getDefault();

        // 아래 구문으로 지정된 핸드폰으로 문자 메시지를 보낸다
        sms.sendTextMessage(phoneNumber, null, IssIn3Minute, sentPI, null);
    }

    public void sendDeleteInWaitSMS(String phoneNumber) {
        String str = "[S-nap zone]\n제한 시간안에 좌석을 발급받지 않아 대기열에서 삭제되었습니다.";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, str, sentPI, null);
    }

    //-------------------------------------------------- WaitHelperDB 부분 ------------------------------------------------
    public void initialWaitHelperDB() { //WaitDB 헬퍼
        db = getWritableDatabase();
        try {
            int count = db.rawQuery("SELECT * FROM WaitHelper",null).getCount(); //테이블의 총 레코드 개수 확인
            if(count == 0)
                db.execSQL(
                        "INSERT INTO WaitHelper VALUES (4, 4, 1, 0);" //numberOfSeat(전체좌석수), numberOfRemainSeat(잔여석), nextWaitNumber(다음에발급될대기번호), numberOfCanIssue(발급가능대기자)
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNumberOfSeat() { //전체좌석수 반환
        int numberOfSeat = 0;
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT numberOfSeat FROM WaitHelper", null);
            while(cursor.moveToNext()) {
                numberOfSeat = cursor.getInt(0);
                break;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numberOfSeat;
    }

    public int getRemainSeat() { //잔여석 개수 반환(setText시)
        int remainSeat = 0;
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT numberOfRemainSeat FROM WaitHelper",null);
            while(cursor.moveToNext()) {
                remainSeat = cursor.getInt(0);
                break;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return remainSeat;
    }

    public void setRemainSeat() { //잔여석 갱신(반환, 발급시)
        int numberOfSeat = 0;
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT numberOfSeat FROM WaitHelper", null);
            while(cursor.moveToNext()) {
                numberOfSeat = cursor.getInt(0);
                break;
            }
            cursor.close();
            db.execSQL("UPDATE WaitHelper SET numberOfRemainSeat = '"+(numberOfSeat - getSeatSize())+"' "); //전체좌석수 - SeatDB 레코드 개수
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNextWaitNumber() { //발급될 대기번호
        int waitNumber = 0;
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT nextWaitNumber FROM WaitHelper", null);
            while (cursor.moveToNext()) {
                waitNumber = cursor.getInt(0);
                break;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return waitNumber;
    }

    public void updateNextWaitNumber() { //대기번호 발급시 대기번호 하나 증가
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT nextWaitNumber FROM WaitHelper", null);
            while(cursor.moveToNext()) {
                db.execSQL("UPDATE WaitHelper SET nextWaitNumber = '"+(cursor.getInt(0) + 1)+"'"); //대기번호 증가
                break;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCanIssue() { //좌석을 발급받을 수 있는 대기번호
        int canIssue = 0;
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT numberOfCanIssue FROM WaitHelper", null);
            while (cursor.moveToNext()) {
                canIssue = cursor.getInt(0);
                break;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return canIssue;
    }

    public void updateCanIssue() { //대기자가 있고, 좌석이 반환되었을시 발급가능번호 하나 증가
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT numberOfCanIssue FROM WaitHelper", null);
            while(cursor.moveToNext()) {
                db.execSQL("UPDATE WaitHelper SET numberOfCanIssue = '"+(cursor.getInt(0) + 1)+"'"); //발급가능번호 증가
                break;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //-------------------------------------------------- 공통 메서드 부분 --------------------------------------------------
    PendingIntent sentPI;
    Activity act = activity1;
    public static Context con;
    InputMethodManager imm = (InputMethodManager)con.getSystemService(Context.INPUT_METHOD_SERVICE);;


    public boolean findSeat(String phoneNumber) { //SeatDB에서 전화번호와 일치하는 버튼 찾기 + 일치하는 좌석(button) 저장됨 -> seatVariable에 저장됨
        int id = -1;
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM Seat WHERE phoneNumber = '"+phoneNumber+"'", null);
            while(cursor.moveToNext()) {
                id = cursor.getInt(4);
                seatVariable.setButton((Button)activity1.findViewById(id)); //전화번호와 일치하는 좌석(button) 저장
                seatVariable.setEndDateTime(LocalDateTime.parse(cursor.getString(2))); //종료시간(String) -> LocalDateTime 변환 후 저장
                break;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(id != -1)
            return true; //이미 등록된 전화번호 -> 거절
        else
            return false; //신규 전화번호 -> 승낙
    }

    public boolean findWait(String phoneNumber) { //WaitDB에서 전화번호와 일치하는 버튼 찾기 -> seatVariable에 저장됨
        boolean flag = false;
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM Wait WHERE phoneNumber = '"+phoneNumber+"'", null);
            while(cursor.moveToNext()) {
                if(cursor.getString(0).equals(phoneNumber)) {
                    seatVariable.setWaitNumber(cursor.getInt(2)); //전화번호와 일치하는 대상의 대기번호 저장
                    flag = true;
                    break;
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag; //true -> 거절
    }

    public void setButton(Button button) { //텍스트가 같은 버튼 정보 저장
        db = getWritableDatabase();
        try {
            for(Button b : buttonList) {
                if(b.getText().equals(button.getText())) {
                    seatVariable.setButton(b);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void findUser(String btnName) { //텍스트가 같은 버튼의 전화번호 저장
        db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT phoneNumber, buttonName FROM Seat", null);
            while(cursor.moveToNext()) {
                if(cursor.getString(1).equals(btnName)) {
                    seatVariable.setPhoneNumber(cursor.getString(0)); //해당 좌석 사용자의 전화번호 저장
                    break;
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //-------------------------------------------------- Thread 부분 --------------------------------------------------
    private static boolean stop = true;
    private static boolean endStop = true;
    private static boolean start = true;
    private static ArrayList<Integer> waitNumberList = new ArrayList<>();

    public void setStop() {
        this.stop = (stop == false) ? true : false; //현재 상태의 반대로 설정
    }

    public void setEndStop() {
        this.endStop = (endStop == false) ? true : false; //현재 상태의 반대로 설정
    }

    public void setStart() {
        this.start = (start == false) ? true : false; //현재 상태의 반대로 설정
    }


    public void runTime(){
        Thread t = new Thread(new Runnable(){ //스레드1 = stop
            @Override
            public void run() {
                try{
                    while(stop){
                        db = getWritableDatabase();

                        try {
                            //1. 종료시간 <= 현재시간인 튜플 삭제
                            Cursor cursor = db.rawQuery("SELECT phoneNumber, endDateTime, buttonId FROM Seat", null);
                            LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //현재 시간
                            while (cursor.moveToNext()) {
                                if (LocalDateTime.parse(cursor.getString(1)).isBefore(now)) { //종료시간 < 현재시간이면
                                    db.execSQL("DELETE FROM Seat WHERE phoneNumber = '" + cursor.getString(0) + "'"); //튜플 삭제 + 잔여석 처리 + 해당 좌석 활성화
                                    int id = cursor.getInt(2); //삭제할 버튼의 아이디 저장
                                    if(isNowActivity1()) { //메인1이면 메인1에서 실행
                                        activity1.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                activity1.findViewById(id).setEnabled(true);//해당 좌석 활성화
                                            }
                                        });
                                    }
                                    else { //메인2이면 메인2에서 실행
                                        activity2.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                activity1.findViewById(id).setEnabled(true);
                                            }
                                        });
                                    }
                                    seatVariable.showRemainSeat(); //잔여석 처리
                                    if(getWaitSize() != 0)
                                        updateWaitTime(); //대기자의 대기시간 업데이트
                                    if(managerDB.isNowActivity2() && (managerDB.getWaitSize() != 0) && ((managerDB.getRemainSeat() <= managerDB.getWaitSize()))) { //발급가능 대기자 증가 조건
                                        managerDB.updateCanIssue(); //발급받을 수 있는 대기번호 증가
                                        TextView textView2 = activity2.findViewById(R.id.textView3);
                                        String str1 = managerDB.getCanIssue() + " 번 까지의 대기자는 좌석을 발급받을 수 있습니다\n(아래 버튼을 눌러 좌석을 발급받으십시오)";
                                        SpannableStringBuilder ssb2 = new SpannableStringBuilder(str1); //글자색 포인트 지정
                                        ssb2.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        textView2.setText(ssb2);
                                    }
                                }
                            }
                            cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //waitNumberList이용함
                        try {
                            //2. 발급받아야하는시간(endDateTime) <= 현재시간인 튜플 검색 후 flag = true 설정
                            LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //현재 시간
                            Cursor cursor = db.rawQuery("SELECT phoneNumber, waitNumber, waitTime, sms FROM Wait", null); //대기번호와 발급받을시간
                            while (cursor.moveToNext()) {
                                if (LocalDateTime.parse(cursor.getString(2)).isBefore(now)) { //발급받을시간 <= 현재시간이면
                                    if(!Boolean.parseBoolean(cursor.getString(3))) {//sms = false이면 sms전송
                                        sendIssIn3MinuteSMS(cursor.getString(0)); //문자 전송
                                        db.execSQL("UPDATE Wait SET sms = '"+true+"' WHERE waitNumber = '"+cursor.getInt(1)+"'"); //sms = true 설정

                                        seatVariable.setEndDateTime(LocalDateTime.parse(cursor.getString(2))); //기존 종료시간 저장
                                        seatVariable.setEndDateTimeForExtension(0, 3); //3분이 더해진 종료시간 endDateTime에 저장됨
                                        db.execSQL("UPDATE Wait SET timer = '" + true + "', waitTime = '"+seatVariable.getEndDateTime()+"' WHERE waitNumber = '"+cursor.getInt(1)+"'"); //timer = true설정, 대기종료시간 = 3분 더해서 재설정
                                        waitNumberList.add(cursor.getInt(1)); //타이머 동작할 리스트에 추가
                                    }
                                    //endDateTime 업데이트
                                }
                            }
                            cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            //3. flag = true인 사람의 3분 타이머 실행
                            LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //현재 시간
                            Cursor cursor = db.rawQuery("SELECT phoneNumber, waitTime FROM Wait WHERE timer = '"+true+"'", null);
                            int num = db.rawQuery("SELECT * FROM Wait WHERE timer = '"+false+"'", null).getCount(); //false인 개수
                            while(cursor.moveToNext()) { //대기자 종료시간<= 현재시간인 경우 레코드 삭제
                                for(int list : waitNumberList) {
                                    if (LocalDateTime.parse(cursor.getString(1)).isBefore(now)) { //종료시간(5시) < 현재시간(6시)이면
                                        db.execSQL("DELETE FROM Wait WHERE waitNumber = '" + list + "'"); //대기번호가 같은 행 삭제
                                        waitNumberList.remove((Integer)list); //리스트에서도 제거
                                        sendDeleteInWaitSMS(cursor.getString(0));
                                        // --------추가코드 --------- 안될시 바로 삭제!
                                        //자동반환시의 canissue 증가
                                        if(num >= 1 && getRemainSeat() > 0) {
                                            updateCanIssue();
                                            TextView textView;
                                            if(isNowActivity1()) {
                                                textView = activity1.findViewById(R.id.notice1);
                                            }
                                            else {
                                                textView = activity2.findViewById(R.id.textView3);
                                            }
                                            String str = managerDB.getCanIssue() + " 번 까지의 대기자는 좌석을 발급받을 수 있습니다\n(아래 버튼을 눌러 좌석을 발급받으십시오)";
                                            SpannableStringBuilder ssb = new SpannableStringBuilder(str); //글자색 포인트 지정
                                            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            textView.setText(ssb);
                                            updateWaitTime();
                                        }
                                        // --------추가코드 ---------
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //5. 시스템종료시간이 되었을 경우 데이터 초기화
                        try {
                            db = getWritableDatabase();
                            LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //현재 시간
                            int nowMin = now.getHour() * 60 + now.getMinute();
                            int endMin = Integer.parseInt(managerDB.getSystemEndTime().substring(0,2)) * 60 + getSystemEndMinute();
                            if(nowMin > endMin) { // 시스템 종료시간 이후일 경우, 데이터베이스 초기화
                                setButtonEnabledWhenSystemClose(); //현재 발급중인 모든 좌석 활성화 처리
                                systemClosed(); //stop = false됨
                                runWhenEndSystem(); //스레드2 시작
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //6. 변경된 사항 액티비티에 적용하기 위해 화면 새로고침(반드시 Thread.sleep()보다 밑에 있어야 함
                        activity1.runOnUiThread(new Runnable() { //화면 새로고침
                            @Override
                            public void run() {
                                try {
                                    if (managerDB.isNowActivity1()) { //화면 새로고침
                                        Intent intent = new Intent(activity1.getApplicationContext(), MainActivity1.class);
                                        intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        activity1.startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(activity2.getApplicationContext(), MainActivity2.class);
                                        intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        activity2.startActivity(intent);
                                    }
                                    //반환 후 잔여석이 1개 이상 + 대기 인원이 0명 + 현재 액티비티가 메인2라면 메인2 -> 메인1 전환(현재 메인2 = true, 메인1 = false)
                                    if ((managerDB.getRemainSeat() > 0) && (managerDB.getWaitSize() == 0) && managerDB.isNowActivity2()) {
                                        managerDB.updateActivity(); //메인2 = false, 메인1 = true 설정
                                        activity2.startActivity(new Intent(activity2.getApplicationContext(), MainActivity1.class));
                                        activity2.finish(); //메인2 종료
                                    }
                                    //대기자가 있고 메인2에서 좌석이 반환된 경우
                                    if (managerDB.isNowActivity2() && (managerDB.getWaitSize() != 0)) { //대기자가 0명이 아닐때
                                        if (managerDB.getRemainSeat() > managerDB.getWaitSize()) { //잔여석 > 대기자 수
                                            managerDB.updateActivity(); //메인2 = false, 메인1 = true 설정
                                            activity2.startActivity(new Intent(activity2.getApplicationContext(), MainActivity1.class)); //메인1로 전환
                                            activity2.finish();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Thread.sleep(5000); // 5초마다 한번씩 수행
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void runWhenEndSystem() { //스레드2 = endStop / 메인1 = false, 메인2 = true, 메인3 = true
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(endStop) {
                        Thread.sleep(10000); //10초뒤에 수행 // 300000 = 5분
                        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //현재 시간
                        if((now.getHour() * 60 + now.getMinute()) < (Integer.parseInt(getSystemEndTime().substring(0,2)) * 60 + getSystemEndMinute())) {
                            //현재시간(5시) < 종료시간(6시) = 시스템 유지
                            setEndStop(); //메인2스레드 종료, 메인2 = false
                            setStop(); //메인1스레드 재실행, 메인1 = true
                            runTime(); //메인1스레드 재실행
                            break;
                        }

                        if((Integer.parseInt(getSystemEndTime().substring(0,2)) * 60 + getSystemEndMinute()) + 10 < (now.getHour() * 60 + now.getMinute())) {
                            setEndStop(); //스레드2 종료, 메인2 = false
                            terminateSystem(); //스레드3 실행
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void terminateSystem() { //스레드3 = start / 메인1 = false, 메인2 = false, 메인3 = true
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(start) {
                        Thread.sleep(30000); //30초마다 한번씩 실행
                        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //현재 시간
                        if((Integer.parseInt(getSystemStartTime().substring(0,2))*60+getSystemStartMinute()) < (now.getHour() * 60 + now.getMinute())) {
                            //시작시간(7시) < 현재시간(7시 1분)이면 시스템 시작
                            setStart(); //스레드3 종료, 메인3 = false
                            setStop(); //스레드1 실행, 메인1 = true
                            runTime(); //스레드1 실행
                        }
                        System.exit(0); //시스템 종료
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
