package com.example.juu.project;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity1 extends AppCompatActivity implements View.OnClickListener  {
    public static ManagerDB managerDB; //SeatDB, WaitDB, SystemDB 총괄
    public static SeatVariable seatVariable = new SeatVariable(); //임시데이터 저장
    private backPressClose back; //뒤로가기 2번 종료
    static Activity activity1; //전환시 종료용, 액티비티는 다른 곳에서도 사용할 일이 많음
    private ActionBar ab; //액션바 총괄
    public static ArrayList<Button> buttonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Stetho.initializeWithDefaults(this);
        managerDB = ManagerDB.getInstance(this); //객체 가져옴
        managerDB.initialSystemDB(); //SystemDB 초기값 설정
        managerDB.initialActivityDB(); //액티비티 초기화
        managerDB.initialWaitHelperDB(); //대기헬퍼 초기화
        activity1 = this;

        //타이틀 설정
        setTitleBar();

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        super.onCreate(savedInstanceState);
//        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_main1);

        back = new backPressClose(this); //뒤로가기 2번 종료

        //메인1의 잔여석 표기
        TextView textView1 = (TextView)findViewById(R.id.remainSeatMain1);
        seatVariable.setRemainSeatMain1(textView1);
        seatVariable.showRemainSeat();


        //메인2에서 종료한경우 메인2로 이동
        if(managerDB.isNowActivity2()) {
            startActivity(new Intent(this, MainActivity2.class));
            this.finish();
        }

        //대기자가 있는데 메인1로 돌아온 경우(이때 잔여석 > 대기자수)
        if(managerDB.getWaitSize() != 0) {
            TextView textView = findViewById(R.id.notice);
            String str = "대기인원 : " + managerDB.getWaitSize() + " 명, \n";
            TextView textView2 = findViewById(R.id.notice1);
            String str2 = managerDB.getCanIssue() + " 번 까지의 대기자는 좌석을 선택할 수 있습니다.";
            SpannableStringBuilder ssb = new SpannableStringBuilder(str); //글자색 포인트 지정
            SpannableStringBuilder ssb2 = new SpannableStringBuilder(str2); //글자색 포인트 지정

            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 7, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb2.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(ssb);
            textView2.setText(ssb2);
        }
        else { //대기자가 0명인 경우
            TextView textView = findViewById(R.id.notice);
            textView.setText(null); //텍스트 없애버림
            TextView textView2 = findViewById(R.id.notice1);
            textView2.setText(null); //텍스트 없애버림
        }

        //메인2 -> 메인1인 경우 or 저장된 좌석 리스트가 있는 경우
        if(managerDB.getSeatSize() != 0) {
            managerDB.setSeatDisabled(); //SeatDB에 저장되어있는 모든 항목에 대해 좌석 비활성화 처리
        }

        findViewById(R.id.btnMainExtension).setOnClickListener(this);
        findViewById(R.id.btnMainReturn).setOnClickListener(this);
        findViewById(R.id.btnDrop).setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);

        int[] array = {R.id.Main_A1, R.id.Main_A2, R.id.Main_A3, R.id.Main_A4, R.id.Main_A5, R.id.Main_A6, R.id.Main_A7, R.id.Main_A8, R.id.Main_A9
                , R.id.Main_A10, R.id.Main_A11, R.id.Main_A12, R.id.Main_B1, R.id.Main_B2, R.id.Main_B3, R.id.Main_B4, R.id.Main_B5, R.id.Main_B6, R.id.Main_B7
                , R.id.Main_B8, R.id.Main_B9, R.id.Main_B10, R.id.Main_B11, R.id.Main_B12, R.id.Main_C1, R.id.Main_C2, R.id.Main_C3, R.id.Main_C4
                , R.id.Main_C5, R.id.Main_C6, R.id.Main_C7, R.id.Main_C8, R.id.Main_C9, R.id.Main_C10, R.id.Main_C11, R.id.Main_C12};

        for(int i=0;i<36;i++) {
            Button b = findViewById(array[i]);
            buttonList.add(b);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMainExtension: //연장
                if(managerDB.getRemainSeat() == managerDB.getNumberOfSeat()) { //잔여석 = 전체개수이면
                    Toast.makeText(this, "연장 가능한 좌석이 없습니다", Toast.LENGTH_SHORT).show(); //거절
                }
                else { //발급된 좌석이 있는경우
                    new Extension1(MainActivity1.this).show();
                }
                break;
            case R.id.btnMainReturn: //반환
                if(managerDB.getRemainSeat() == managerDB.getNumberOfSeat()) { //잔여석 = 전체개수이면
                    Toast.makeText(this, "반환 가능한 좌석이 없습니다", Toast.LENGTH_SHORT).show(); //거절
                }
                else { //발급된 좌석이 있는경우
                    new Return1(MainActivity1.this).show();
                }
                break;
            case R.id.btnDelete:
                managerDB.dropEveryTable();
                break;
            case R.id.btnDrop:
                managerDB.deleteEveryData();
                break;
        }
    }

    public void onMainClick(View v) {
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime(); //현재 시간
        int startMin = Integer.parseInt(managerDB.getSystemStartTime().substring(0,2)) * 60 + getSystemStartMinute();
        int nowMin = now.getHour() * 60 + now.getMinute();
        int endMin = Integer.parseInt(managerDB.getSystemEndTime().substring(0,2)) * 60 + getSystemEndMinute();
        if((nowMin < startMin) || (nowMin > endMin)) { //시스템 사용시간 이전 혹은 시스템 종료시간 이후일 경우 발급 거절
            Toast.makeText(this, "이용 가능 시간이 아닙니다", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            seatVariable.setButton((Button) activity1.findViewById(v.getId()));//버튼 저장
            new Select1(MainActivity1.this).show();
        }
    }

    //새로고침시 실행되어야 할 작업 정의 ** 중요 **
    public void onResume() {
        super.onResume();
        // 시스템 운영 시간이 변경된 경우
        ab.setSubtitle("[ " + managerDB.printSystemStartTime() + " ~ " + managerDB.printSystemEndTime() + " ]");

        //대기자가 있는데 메인1로 돌아온 경우(이때 잔여석 > 대기자수)
        if(managerDB.getWaitSize() != 0) {
            TextView textView = findViewById(R.id.notice);
            String str = "대기인원 : " + managerDB.getWaitSize() + " 명, \n";
            TextView textView2 = findViewById(R.id.notice1);
            String str2 = managerDB.getCanIssue() + " 번 까지의 대기자는 좌석을 선택할 수 있습니다.";
            SpannableStringBuilder ssb = new SpannableStringBuilder(str); //글자색 포인트 지정
            SpannableStringBuilder ssb2 = new SpannableStringBuilder(str2); //글자색 포인트 지정

            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 7, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb2.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(ssb);
            textView2.setText(ssb2);
        }
        else { //대기자 = 0명인 경우
            TextView textView = findViewById(R.id.notice);
            textView.setText(null); //텍스트 없애버림
            TextView textView2 = findViewById(R.id.notice1);
            textView2.setText(null); //텍스트 없애버림
        }
    }

    //액션버튼 동작 지정
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.message:
                new SendCaution1(MainActivity1.this).show();
                break;
            case R.id.setting:
                new AdminOption1(MainActivity1.this).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //전화번호 체크
    public static boolean isValidCellPhoneNumber(String cellphoneNumber) {

        boolean returnValue = false;
        try {
            String regex = "^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(cellphoneNumber);
            if (m.matches()) {
                returnValue = true;
            }

            if (returnValue && cellphoneNumber != null
                    && cellphoneNumber.length() > 0
                    && cellphoneNumber.startsWith("010")) {
                cellphoneNumber = cellphoneNumber.replace("-", "");
                if (cellphoneNumber.length() != 11) {
                    returnValue = false;
                }
            }
            return returnValue;
        } catch (Exception e) {
            return false;
        }
    }

    public void onBackPressed() { //뒤로가기 2번 종료
        back.onBackPressed();
    }

    //액션버튼 메뉴바 추가
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public static int getSystemStartMinute() { //시스템 시작시간 중 '분' 계산해 리턴
        String systemStartTime = managerDB.getSystemStartTime();
        int min = Integer.parseInt(systemStartTime.substring(4,5));
        if(min == 1)
            min = 15;
        else if(min == 3)
            min = 30;
        else if(min == 4)
            min = 45;

        return min;
    }

    public static int getSystemEndMinute() { //시스템 종료시간 중 '분' 계산해 리턴
        String systemEndTime = managerDB.getSystemEndTime();
        int min = Integer.parseInt(systemEndTime.substring(4,5));
        if(min == 1)
            min = 15;
        else if(min == 3)
            min = 30;
        else if(min == 4)
            min = 45;

        return min;
    }

    public void setTitleBar() { //타이틀바 설정에 기여함
        ab = getSupportActionBar() ;
        ab.setTitle("S-nap zone") ;
        ab.setSubtitle("[ " + managerDB.printSystemStartTime() + " ~ " + managerDB.printSystemEndTime() + " ]");
        ab.setIcon(R.drawable.icon_sungshin);
        ab.setDisplayShowHomeEnabled(true); //아이콘 보이게 설정
    }
}

