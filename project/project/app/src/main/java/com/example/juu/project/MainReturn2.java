package com.example.juu.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import static com.example.juu.project.MainActivity.actList2;

public class MainReturn2 extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actList2.add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_return2);

        findViewById(R.id.btnReOk2).setOnClickListener(this);
        findViewById(R.id.btnReClose2).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReOk2:
                Toast.makeText(this,"좌석 반환 완료", Toast.LENGTH_SHORT).show();
                for(int i=0;i<actList2.size();i++)
                    actList2.get(i).finish();
                break;
            case R.id.btnReClose2:
                Toast.makeText(this,"취소되었습니다",Toast.LENGTH_SHORT).show();
                for(int i=0;i<actList2.size();i++)
                    actList2.get(i).finish();
                break;
        }
    }
}
