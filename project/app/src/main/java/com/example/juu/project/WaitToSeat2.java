package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;

import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;


public class WaitToSeat2 extends Dialog {
    public static WaitToSeat2 waitToSeat2;
    private ArrayList<Button> buttonList = new ArrayList<>();
    Context context;

    public WaitToSeat2(Context context) {
        super(context);
        waitToSeat2 = this;
        this.context = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_wait_issue2);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button)findViewById(v.getId()); //버튼 데이터 임시 저장 ** 이 버튼은 MainWaitIssue2에서 존재하는 버튼임 **
                managerDB.setButton(button); //버튼text가 같은 메인1의 버튼 seatVariable에 저장됨
                System.out.println("내가 선택한 버튼 이름 : " + button.getText().toString());
                new WaitToSeat3(context).show();
                dismiss();
            }
        };

        int[] array = {R.id.Iss_A1, R.id.Iss_A2, R.id.Iss_A3,R.id.Iss_A4,R.id.Iss_A5,R.id.Iss_A6,R.id.Iss_A7,R.id.Iss_A8,R.id.Iss_A9,
                R.id.Iss_A10,R.id.Iss_A11,R.id.Iss_A12,R.id.Iss_B1,R.id.Iss_B2,R.id.Iss_B3,R.id.Iss_B4,
                R.id.Iss_B5,R.id.Iss_B6,R.id.Iss_B7,R.id.Iss_B8,R.id.Iss_B9,R.id.Iss_B10,R.id.Iss_B11,R.id.Iss_B12,
                R.id.Iss_C1,R.id.Iss_C2,R.id.Iss_C3,R.id.Iss_C4,R.id.Iss_C5,R.id.Iss_C6,R.id.Iss_C7,R.id.Iss_C8,
                R.id.Iss_C9, R.id.Iss_C10, R.id.Iss_C11, R.id.Iss_C12};

        for(int i=0;i<36;i++) {
            Button b = findViewById(array[i]);
            b.setOnClickListener(listener);
            buttonList.add(b);
        }

        //이미 선택된 좌석 비활성화 처리
        managerDB.setSelectedSeatDisabled(buttonList);
    }
}
