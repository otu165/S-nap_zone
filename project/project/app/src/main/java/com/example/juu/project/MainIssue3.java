package com.example.juu.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static com.example.juu.project.MainActivity.actList;


public class MainIssue3 extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actList.add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainissue3);

        findViewById(R.id.btnOk3).setOnClickListener(this);
        findViewById(R.id.btnClose3).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk3:
                for(int i=0;i<actList.size();i++)
                    actList.get(i).finish();
                Toast.makeText(this,"대기번호 발급 완료", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnClose3:
                for(int i=0;i<actList.size();i++)
                    actList.get(i).finish();
                Toast.makeText(this,"취소되었습니다", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
