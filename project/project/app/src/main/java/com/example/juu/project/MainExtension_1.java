package com.example.juu.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import static com.example.juu.project.MainActivity.actList2;

public class MainExtension_1 extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actList2.add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_extension_1);

        findViewById(R.id.btnExOk).setOnClickListener(this);
        findViewById(R.id.btnExClose).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnExOk:
                Toast.makeText(this, "휴대폰 번호와\n 일치하는 좌석 확인됨", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "휴대폰 번호와\n 일치하는 좌석이 확인되지 않음", Toast.LENGTH_SHORT).show(); 기능 처리 필요***
                startActivity(new Intent(this, TimePicker.class));
                break;
            case R.id.btnExClose:
                this.finish();
                break;
        }
    }
}
