package com.example.juu.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static com.example.juu.project.MainActivity.actList2;

public class MainReturn1 extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actList2.add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_return1);

        findViewById(R.id.btnReOk1).setOnClickListener(this);
        findViewById(R.id.btnReClose1).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReOk1:
                startActivity(new Intent(this, MainReturn2.class));
                break;
            case R.id.btnReClose1:
                this.finish();
                break;
        }
    }
}
