package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.juu.project.MainActivity1.managerDB;


public class AdminOption2 extends Dialog {
    public static AdminOption2 adminOption2;
    public AdminOption2(Context context) {
        super(context);
        adminOption2 = this;
        setCancelable(false);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_setting_option2); //레이아웃

        Toast.makeText(context, "관리자님 환영합니다", Toast.LENGTH_SHORT).show();

        TextView tv = findViewById(R.id.setting1);
        tv.setText(managerDB.printSystemStartTime() + " ~ " + managerDB.printSystemEndTime());

        Button ok = (Button)findViewById(R.id.btnSetOk2);
        Button btnStartTime = (Button)findViewById(R.id.setStartTIme);
        Button btnEndTime = (Button)findViewById(R.id.setEndTime);
        Button pw = (Button)findViewById(R.id.btnSetPw2);

        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AdminOption3(context).show();
            }
        });

        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AdminOption4(context).show();
            }
        });

        pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AdminOption5(context).show();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "설정이 완료되었습니다", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
