package com.funnyseals.app.feature.doctorNursingPlan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.funnyseals.app.R;

public class SportsDetailActivity extends AppCompatActivity {

    private TextView tv;
    private Button   quit_button;
    private Button   done_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_detail);
        ActionBar actionBar = getSupportActionBar();   //隐藏自带actionBar
        if (actionBar != null) {
            actionBar.hide();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        tv = findViewById(R.id.sportsname);
        tv.setText(bundle.getString("sportsname"));
        quit_button = findViewById(R.id.quititsports);
        done_button = findViewById(R.id.donesports);
        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  //关闭此activity
            }
        });
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  //关闭此activity
            }
        });
    }
}
