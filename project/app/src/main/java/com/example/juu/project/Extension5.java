package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;

public class Extension5 extends Dialog {
    public Extension5(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_extension4);

        //좌석이름 나타내기
        TextView tv = (TextView)findViewById(R.id.btnExTextView4);
        tv.setText("[" +seatVariable.getButton().getText() + "] 좌석 연장 완료");

        managerDB.getEndDateTIme();

        //연장된 발급 만료 시간 나타내기
        TextView textView = (TextView)findViewById(R.id.btnExTextView4);
        textView.setText("퇴실 시간\n" + seatVariable.getEndDateTimeForString());

        //다이얼로그 일정시간 후 종료
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 1500);
    }
}
