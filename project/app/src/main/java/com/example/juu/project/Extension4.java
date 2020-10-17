package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;

public class Extension4 extends Dialog {
    public Extension4(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_extension3);

        //사용자가 선택한 연장 시간 TextView 나타내기
        TextView btnSelectedTime = (TextView)findViewById(R.id.btnExSelectedTime3);
        if(seatVariable.getSelectedHour() == 0) {
            btnSelectedTime.setText(seatVariable.getSelectedMin() + "분");
        }
        else {
            btnSelectedTime.setText(seatVariable.getSelectedHour() + "시간 " + seatVariable.getSelectedMin() + "분");
        }

        Button ok = (Button)findViewById(R.id.btnExOk3);
        Button close = (Button)findViewById(R.id.btnExClose3);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SeatDB정보 갱신
                managerDB.updateSeatDB();

                if(managerDB.getWaitSize() != 0)
                    managerDB.updateWaitTime();

                new Extension5(context).show();
                dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "연장이 취소되었습니다", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
