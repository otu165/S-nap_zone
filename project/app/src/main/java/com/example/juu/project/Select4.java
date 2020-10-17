package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.juu.project.MainActivity1.activity1;
import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;

public class Select4 extends Dialog {
    public Select4(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_main_select4); //레이아웃 설정

        //퇴실시간 출력
        TextView textView = (TextView)findViewById(R.id.btnSeTextView4);
        textView.setText(seatVariable.getEndDateTimeForString());


        //다이얼로그 일정시간 후 종료
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            //Do Something
            @Override
            public void run() {
                if(managerDB.getRemainSeat() == 0) { //잔여석 0개면 메인2 전환
                    managerDB.updateActivity(); //메인1 = false, 메인2 = true 설정
                    context.startActivity(new Intent(context, MainActivity2.class));
                    activity1.finish(); //메인1 종료
                }
                else if(managerDB.getRemainSeat() - managerDB.getWaitSize() == 0) {   //잔여석 - 대기자 수 == 0 이라면 MainActivity2 실행
                    managerDB.updateActivity();
                    context.startActivity(new Intent(context, MainActivity2.class));
                    activity1.finish(); //메인1 종료
                }
                dismiss();
            }
        }, 1500);
    }
}
