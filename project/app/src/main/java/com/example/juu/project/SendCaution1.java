package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Window;

public class SendCaution1 extends Dialog {
    public SendCaution1(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_send_caution1); //레이아웃 설정

        //다이얼로그 일정시간 후 종료
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new SendCaution2(context).show();
                dismiss();
            }
        }, 1500);
    }
}
