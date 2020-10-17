package com.example.juu.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static com.example.juu.project.MainActivity.actList2;

public class MainExtension2 extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actList2.add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_extension2);

        findViewById(R.id.btnExOk3).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnExOk3:
                startActivity(new Intent(this, MainExtension3.class));
                break;
            case R.id.btnExClose2:
                //startActivity(new Intent(this, TimePicker.class)); // *** 화면 전환 안됨 -> 왜?
                this.finish();
                break;
        }
    }
}
