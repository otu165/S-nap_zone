package com.example.juu.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import static com.example.juu.project.MainActivity.actList;


public class MainIssue extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actList.add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_main);

        findViewById(R.id.btnOk).setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnOk:
                startActivity(new Intent(this, MainIssue2.class));
                this.finish();
                break;
            case R.id.btnClose:
                this.finish();
                break;
        }
    }
}
