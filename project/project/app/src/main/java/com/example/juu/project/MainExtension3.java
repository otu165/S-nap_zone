package com.example.juu.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.juu.project.MainActivity.actList2;

//확인버튼 없이 main 이동하도록 수정하고 싶음
public class MainExtension3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actList2.add(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_extension3);

        Timer timer = new Timer();
        timer.schedule( new TimerTask()
                        {
                            public void run()
                            {
                                for(int i=0;i<actList2.size();i++)
                                    actList2.get(i).finish();
                            }
                        }
                , 1000);
    }
}
