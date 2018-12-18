package com.funnyseals.app.feature.patientPersonalCenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.funnyseals.app.R;
import com.funnyseals.app.util.SocketUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PatientMyDoctorActivity extends AppCompatActivity {

    private TextView tv_patient_doctor_name,tv_patient_doctor_hospital,tv_patient_doctor_post;
    private ImageButton ib_patient_doctor_return;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_my_doctor);
        init();

    }
    //更新，其余写死
    void   init()
    {
        tv_patient_doctor_name=findViewById(R.id.tv_doctor_perinfo);
        tv_patient_doctor_hospital=findViewById(R.id.tv_patient_doctor_hospital);
        tv_patient_doctor_post=findViewById(R.id.tv_patient_doctor_post);

        ib_patient_doctor_return=findViewById(R.id.ib_patient_doctor_return);
        ib_patient_doctor_return.setOnClickListener(new addListeners());
    }
    public void setDoctorName(String name){
        tv_patient_doctor_name.setText(name.toCharArray(),0,name.length());
    }
    public void setDoctorHosptial(String hosptial){
        tv_patient_doctor_hospital.setText(hosptial.toCharArray(),0,hosptial.length());
    }
    public void setDoctorPost(String post){
         tv_patient_doctor_post.setText(post.toCharArray(),0,post.length());
    }
    //监听
    private class addListeners implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ib_patient_doctor_return:
                    new Thread(()->{
                        String send="";
                        Socket socket;
                        try{
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("pID","xxxxxx");
                            jsonObject.put("request_type","1");
                            send = jsonObject.toString();
                            socket = SocketUtil.getSendSocket();
                            DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                            out.writeUTF(send);
                            out.close();

                            socket = SocketUtil.getGetSocket();
                            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                            String message = dataInputStream.readUTF();

                            jsonObject = new JSONObject(message);
                            String name=jsonObject.get("doctorname").toString();
                            String hosptial=jsonObject.get("doctorhosptia;").toString();
                            String post=jsonObject.get("doctorpost").toString();
                            setDoctorName(name);
                            setDoctorHosptial(hosptial);
                            setDoctorPost(post);
                            socket.close();
                        }catch (IOException | JSONException e){
                            e.printStackTrace();
                        }
                    }).start();
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

}