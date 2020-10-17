package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import static com.example.juu.project.MainActivity1.seatVariable;


public class WaitNumberIssue2 extends Dialog {
    public WaitNumberIssue2(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_issue2);

        //전화번호 TextView 나타내기
        TextView issTextView2 = (TextView)findViewById(R.id.IssTextView2);
        issTextView2.setText(seatVariable.getPhoneNumber());

        Button ok = (Button)findViewById(R.id.btnIssOk2);
        Button close = (Button)findViewById(R.id.btnIssClose2);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WaitNumberIssue3(context).show();
                dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WaitNumberIssue1(context).show();
                dismiss();
            }
        });
    }
}
