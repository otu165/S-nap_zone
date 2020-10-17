package com.example.juu.project;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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


public class Extension3 extends Dialog {
    private TimePicker timePicker;
    private int TIME_PICKER_INTERVAL = 15;

    private NumberPicker hourPicker, minutePicker;
    private List<String> limitedValues;
    private List<String> displayedValues;

    public Extension3(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_extension2);

        timePicker = (TimePicker)findViewById(R.id.btnExTimePicker);
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

        Button ok = (Button)findViewById(R.id.btnExOk2);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour, min;

                //API버전 호환성 위함
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    hour = timePicker.getCurrentHour();
                    min = timePicker.getCurrentMinute();
                } else {
                    hour = timePicker.getHour();
                    min = timePicker.getMinute();
                }

                if (hour == 0 && min == 0) {
                    Toast.makeText(context, "0분 이상의 시간을 선택하셔야 합니다", Toast.LENGTH_SHORT).show();
                }
                else if((hour * 60 + min) > 120) {//선택이 2시간을 넘어가면(API28위한 코드)
                    Toast.makeText(context, "최대 2시간만 선택 가능합니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    seatVariable.setEndDateTimeForExtension(hour, min * TIME_PICKER_INTERVAL); //선택정보 저장 -> 종료시간 갱신됨
                    if((seatVariable.getEndDateTime().getHour() * 60 + seatVariable.getEndDateTime().getMinute()) //사용자 종료시간 > systemEndTime인 경우
                            > ((Integer.parseInt(managerDB.getSystemEndTime().substring(0,2)) * 60) + getSystemEndMinute())) {
                        Toast.makeText(context, "금일 수면실 이용 시간은 " + managerDB.printSystemEndTime() + "까지 입니다\n이용에 참고해주세요", Toast.LENGTH_SHORT).show();
                    }
                    new Extension4(context).show();
                }
                dismiss();
            }
        });
    }

    @SuppressLint("NewApi")
    private void setTimePickerInterval(TimePicker timePicker) {
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            //Field timePickerField = classForid.getField("timePicker");

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {

            } else {
                //시간(hour) 가져오기
                Field fieldH = classForid.getField("hour");
                hourPicker = (NumberPicker) timePicker.findViewById(fieldH.getInt(null));

                //시간(hour) 범위 0, 1 제한
                hourPicker.setMinValue(0);
                hourPicker.setMaxValue(1);
                limitedValues = new ArrayList<String>();
                for (int i = 0; i < 60; i++) {
                    limitedValues.add(String.format("%02d", i % 2));
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
