package com.example.juu.project;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.example.juu.project.MainActivity1.getSystemEndMinute;
import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;

public class Select2 extends Dialog {
    private TimePicker timePicker;
    private int TIME_PICKER_INTERVAL = 15;

    private NumberPicker hourPicker, minutePicker;
    private List<String> limitedValues;
    private List<String> displayedValues;

    public Select2(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_main_select2); //레이아웃 설정

        timePicker = (TimePicker) findViewById(R.id.btnMainTimePicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

            }
        });
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS); //데이터 선택시 editText 방지
        timePicker.setIs24HourView(true);

        //API버전 호환성 위함
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M){ //API버전이 낮은 경우
            timePicker.setCurrentHour(0);
            timePicker.setCurrentMinute(0);
        } else{
            timePicker.setHour(0);
            timePicker.setMinute(0);
        }

        setTimePickerInterval(timePicker);
        Button ok = (Button)findViewById(R.id.btnSeOk2);

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

                if(hour == 0 && min == 0) {
                    Toast.makeText(context, "0분 이상의 시간을 선택하셔야 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if((hour * 60 + min) > 300) {//선택이 5시간을 넘어가면(API28위한 코드)
                    Toast.makeText(context, "최대 5시간만 선택 가능합니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    seatVariable.setEndDateTime(hour, min * TIME_PICKER_INTERVAL); //선택한 시간으로 endDateTime 계산
                    if((seatVariable.getEndDateTime().getHour() * 60 + seatVariable.getEndDateTime().getMinute()) //사용자 종료시간 > systemEndTime인 경우
                            > ((Integer.parseInt(managerDB.getSystemEndTime().substring(0,2)) * 60) + getSystemEndMinute())) {
                        Toast.makeText(context, "금일 수면실 이용시간은 " + managerDB.getSystemEndTime() + "까지 입니다\n이용에 참고해주세요", Toast.LENGTH_SHORT).show();
                    }
                    new Select3(context).show();
                }
                dismiss();
            }
        });
    }

    @SuppressLint("NewApi")
    private void setTimePickerInterval(TimePicker timePicker) {
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) { //28버전부터 불가능
                //28버전부터는 함수가 없어짐
            } else {
                //시간(hour) 가져오기
                Field fieldH = classForid.getField("hour");
                hourPicker = (NumberPicker) timePicker.findViewById(fieldH.getInt(null));

                //시간(hour) 범위 0 ~ 4 제한 //4로 나누어서 처리 *** 새로 바꾼 코드 ***
                hourPicker.setMaxValue(0);
                hourPicker.setMaxValue(4);
                limitedValues = new ArrayList<String>();
                for (int i = 0; i < 60; i++) {
                    limitedValues.add(String.format("%02d", i % 5));
                }
                hourPicker.setDisplayedValues(limitedValues.toArray(new String[0]));
                hourPicker.setWrapSelectorWheel(true);
            }

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