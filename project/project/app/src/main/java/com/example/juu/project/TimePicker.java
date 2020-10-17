package com.example.juu.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.text.DateFormat;

import static com.example.juu.project.MainActivity.actList2;

public class TimePicker extends AppCompatActivity implements View.OnClickListener {
    //private int hour = 0, minute = 0; *** 시간
    public void onCreate(Bundle savedInstanceState) {
        actList2.add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        findViewById(R.id.btnExOk2).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnExOk2:
                startActivity(new Intent(this, MainExtension2.class));
                break;
        }
    }
}
