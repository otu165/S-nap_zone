package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.juu.project.MainActivity1.seatVariable;


public class WaitToSeat5 extends Dialog {
    public WaitToSeat5(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_wait_issue5);

        //퇴실시간 출력
        TextView textView = (TextView)findViewById(R.id.btnSeTextView4);
        textView.setText(seatVariable.getEndDateTimeForString());

        //다이얼로그 일정시간 후 종료
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 1500);

        //반환 후 잔여석이 1개 이상 + 대기 인원이 0명 + 현재 액티비티가 메인2 이라면 메인2 -> 메인1 전환(현재 메인2 = true, 메인1 = false)
    }
}
