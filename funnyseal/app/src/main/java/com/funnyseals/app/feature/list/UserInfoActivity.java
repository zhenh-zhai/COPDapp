package com.funnyseals.app.feature.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.funnyseals.app.R;
import com.funnyseals.app.feature.doctorMessage.DoctorChatActivity;
import com.funnyseals.app.model.User;

/*
展示患者详细信息
 */
public class UserInfoActivity extends AppCompatActivity {
    private Button   mBtnReturn;
    private Button   mBtnSendMessage;
    private TextView mTvName;
    private TextView mTvSex;
    private TextView mTvAge;
    private TextView mTvAddress;

    private User mMyPatient;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Intent intent = getIntent();
        mMyPatient = (User) intent.getSerializableExtra("user");
        initView();
        initData();
        addListener();
    }

    private void initView () {
        mBtnReturn = findViewById(R.id.gotomainlist);
        mBtnSendMessage = findViewById(R.id.sendmessage);
        mTvName = findViewById(R.id.patientname);
        mTvSex = findViewById(R.id.patientsex);
        mTvAge = findViewById(R.id.patientage);
        mTvAddress = findViewById(R.id.patientlocation);
    }

    private void initData () {
        mTvName.setText(mMyPatient.getName());
        mTvSex.setText(mMyPatient.getSex());
        mTvAge.setText(String.valueOf(mMyPatient.getAge()));
        mTvAddress.setText(mMyPatient.getAddress());
    }

    private void addListener () {
        mBtnReturn.setOnClickListener(v -> finish());
        mBtnSendMessage.setOnClickListener(v -> {
            Intent intent = new Intent(UserInfoActivity.this, DoctorChatActivity.class);
            intent.putExtra("myPatient", mMyPatient);
            startActivity(intent);
            finish();
        });
    }
}
