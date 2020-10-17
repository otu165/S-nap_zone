package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.juu.project.MainActivity1.seatVariable;


public class Extension2_accept extends Dialog {
    public Extension2_accept(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_extension_1);

        TextView textView = (TextView)findViewById(R.id.btnExTextView_1);
        textView.setText(seatVariable.getPhoneNumber());

        TextView textView1 = (TextView)findViewById(R.id.btnExTextView);
        textView1.setText("일치하는 좌석[" + seatVariable.getButton().getText() +"] 확인됨");

        //다이얼로그 일정시간 후 종료
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Extension3(context).show();
                dismiss();
            }
        }, 1500);
    }
}
