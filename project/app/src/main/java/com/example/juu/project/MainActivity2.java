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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.juu.project.MainActivity1.managerDB;


//액티비티 외부 클릭시 뒤로가기 효과 나타나지 않도록 설정 ***
public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    private backPressClose back;
    static Activity activity2;
    public static ArrayList<Activity> mainList2 = new ArrayList<>(); //대기자 좌석 발급시 액티비티 종료용
    private ActionBar ab; //액션바 총괄

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity2 = this;
        super.onCreate(savedInstanceState);

        //타이틀 문구
        ActionBar ab = getSupportActionBar() ;
        ab.setTitle("S-nap zone");
        ab.setSubtitle("대기 번호를 발급하십시오");

        //액션바 설정
        setTitleBar();

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        getWindow().setAttributes(lpWindow);
//        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 가로 고정
        setContentView(R.layout.activity_main2);
        back = new backPressClose(this); //뒤로가기 2번 종료

        findViewById(R.id.btnIssue).setOnClickListener(this);
        findViewById(R.id.btnExtension).setOnClickListener(this);
        findViewById(R.id.btnReturn).setOnClickListener(this);
        findViewById(R.id.btnWaitIssue).setOnClickListener(this); //대기자의 좌석 발급 버튼
        findViewById(R.id.btnMain2DeleteDB2).setOnClickListener(this);
        findViewById(R.id.btnMain2DropDB2).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnExtension: //연장
                new Extension1(MainActivity2.this).show();
                break;
            case R.id.btnReturn: //반환
                new Return1(MainActivity2.this).show();
                break;
            case R.id.btnIssue: //대기번호 발급
                new WaitNumberIssue1(MainActivity2.this).show();
                break;
            case R.id.btnMain2DeleteDB2: //테이블 지우고 재생성
                managerDB.dropEveryTable();
                break;
            case R.id.btnMain2DropDB2: //테이블에 저장된 모든 데이터 제거
                managerDB.deleteEveryData();
                break;
            case R.id.btnWaitIssue: //대기자의 좌석 발급
                if(managerDB.getWaitSize() == 0) {
                    Toast.makeText(this, "대기자가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                }
                else {
                    new WaitToSeat1(MainActivity2.this).show();
                }
                break;
        }
    }

    //새로고침시 실행되어야 할 작업 정의 ** 중요 **
    public void onResume() {
        System.out.println("메인2 새로고침이 실행중입니다.");
        super.onResume();
        //시스템 운영 시간이 변경된 경우
        ab.setSubtitle("[ " + managerDB.printSystemStartTime() + " ~ " + managerDB.printSystemEndTime() + " ]");

        //대기자 수 설정
        TextView textView = (TextView)findViewById(R.id.btnMainTextView2);
        String str = "대기 인원 : " + managerDB.getWaitSize() + " 명 ";
        SpannableStringBuilder ssb = new SpannableStringBuilder(str); //글자색 포인트 지정
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 8, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ssb);

        str = managerDB.getCanIssue() + " 번 까지의 대기자는 좌석을 발급받을 수 있습니다\n(아래 버튼을 눌러 좌석을 발급받으십시오)";
        SpannableStringBuilder ssb1 = new SpannableStringBuilder(str);
        TextView textView1 = (TextView)findViewById(R.id.textView3) ;
        ssb1.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView1.setText(ssb1);
    }

    //액션버튼 동작 지정
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.message:
                new SendCaution1(MainActivity2.this).show();
                break;
            case R.id.setting:
                new AdminOption1(MainActivity2.this).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //액션버튼 메뉴바 추가
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void onBackPressed() {
        back.onBackPressed();
    }

    public void setTitleBar() { //타이틀바 설정에 기여함
        ab = getSupportActionBar() ;
        ab.setTitle("S-nap zone") ;
        ab.setSubtitle("[ " + managerDB.printSystemStartTime() + " ~ " + managerDB.printSystemEndTime() + " ]");
        ab.setIcon(R.drawable.icon_sungshin);
        ab.setDisplayShowHomeEnabled(true); //아이콘 보이게 설정
    }
}
