package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.juu.project.MainActivity1.seatVariable;


public class Return2_accept extends Dialog {
    public Return2_accept(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_main_return_1); //레이아웃

        TextView tv = (TextView)findViewById(R.id.textView12);
        tv.setText("일치하는 좌석[" + seatVariable.getButton().getText() +"] 확인됨");

        TextView textView = (TextView)findViewById(R.id.btnExTextView_1);
        textView.setText(seatVariable.getPhoneNumber());

        //다이얼로그 일정시간 후 종료
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            //Do Something
            @Override
            public void run() {
                new Return3(context).show();
                dismiss();
            }
        }, 1500);
    }
}
