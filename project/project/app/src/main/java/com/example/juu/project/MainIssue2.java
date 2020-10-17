package com.example.juu.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static com.example.juu.project.MainActivity.actList;


public class MainIssue2 extends AppCompatActivity implements View.OnClickListener {
    protected void onCreate(Bundle savedInstanceState) {
        actList.add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue2);

        findViewById(R.id.btnOk2).setOnClickListener(this);
        findViewById(R.id.btnClose2).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk2:
                startActivity(new Intent(this, MainIssue3.class));
                break;
            case R.id.btnClose2:
                startActivity(new Intent(this, MainIssue.class));
                this.finish();
                break;
        }
    }
}