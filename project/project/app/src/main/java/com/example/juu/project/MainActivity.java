package com.example.juu.project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.session.PlaybackState;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

//액티비티 외부 클릭시 뒤로가기 효과 나타나지 않도록 설정 ***
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static ArrayList<Activity> actList = new ArrayList<>();
    public static ArrayList<Activity> actList2 = new ArrayList<>();
    public static ArrayList<Activity> actList3 = new ArrayList<>();

    private backPressClose back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
        back = new backPressClose(this);

        findViewById(R.id.issue).setOnClickListener(this);
        findViewById(R.id.btnExtension).setOnClickListener(this);
        findViewById(R.id.btnReturn).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.issue:
                startActivity(new Intent(this, MainIssue.class));
                break;
            case R.id.btnExtension:
                startActivity(new Intent(this, MainExtension_1.class));
                break;
            case R.id.btnReturn:
                startActivity(new Intent(this, MainReturn1.class));
                break;
        }
    }

    public void onBackPressed() {
        back.onBackPressed();
    }
}
