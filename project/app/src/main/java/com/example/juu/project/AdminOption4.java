package com.example.juu.project;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.example.juu.project.AdminOption2.adminOption2;
import static com.example.juu.project.MainActivity1.getSystemStartMinute;
import static com.example.juu.project.MainActivity1.managerDB;

public class AdminOption4 extends Dialog {
    TimePicker timePicker;
    private int TIME_PICKER_INTERVAL = 15;

    NumberPicker minutePicker;
    List<String> displayedValues;

    public AdminOption4(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_setting_option4); //레이아웃 설정

        timePicker = (TimePicker) findViewById(R.id.btnMainTimePicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            }
        });
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS); //데이터 선택시 editText 방지

        timePicker.setIs24HourView(false);

        String systemTime = managerDB.getSystemEndTime();
        int hour = Integer.parseInt(systemTime.substring(0,2));

        //기존 시작 시간으로 세팅
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) { //API 버전이 낮은 경우
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(0);
        }
        else {
            timePicker.setHour(hour);
            timePicker.setMinute(0);
        }

        setTimePickerInterval(timePicker);

        Button ok = (Button)findViewById(R.id.btnSetOk4);
        Button close = (Button)findViewById(R.id.btnSetClose4);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour, min;
                //API버전 호환성 위함
                if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M){
                    hour = timePicker.getCurrentHour();
                    min = timePicker.getCurrentMinute();
                } else{
                    hour = timePicker.getHour();
                    min = timePicker.getMinute();
                }

                int preHour = Integer.parseInt(managerDB.getSystemStartTime().substring(0,2));
                int preMinute = getSystemStartMinute();


                if(((hour * 60 + min) - (preHour * 60 + preMinute) < 180)) { //종료시간 - 시작시간 < 3인 경우 거절
                    Toast.makeText(context, "종료시간과 3시간 이상 차이나야 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                //SystemDB 갱신
                managerDB.setSystemEndTime(hour, min * TIME_PICKER_INTERVAL);
                Toast.makeText(context, "종료 시간이\n " + managerDB.getSystemEndTime() + "으로 변경되었습니다", Toast.LENGTH_SHORT).show();

                //관리자 페이지의 텍스트뷰 갱신
                TextView tv = adminOption2.findViewById(R.id.setting1);
                tv.setText(managerDB.printSystemStartTime() + " ~ " + managerDB.printSystemEndTime());

                dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @SuppressLint("NewApi")
    private void setTimePickerInterval(TimePicker timePicker) {
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");

            //분(minute) 가져오기
            Field fieldM = classForid.getField("minute");
            minutePicker = (NumberPicker) timePicker.findViewById(fieldM.getInt(null));

            //분(minute) 15분 간격 처리
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(3);
            displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues.toArray(new String[0]));
            minutePicker.setWrapSelectorWheel(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
