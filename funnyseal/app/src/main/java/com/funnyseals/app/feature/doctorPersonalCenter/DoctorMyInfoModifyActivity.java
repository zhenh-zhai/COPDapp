package com.funnyseals.app.feature.doctorPersonalCenter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.funnyseals.app.R;
import com.funnyseals.app.feature.MyApplication;
import com.funnyseals.app.feature.patientPersonalCenter.PatientMyInfoModifyActivity;
import com.funnyseals.app.model.User;
import com.funnyseals.app.util.SocketUtil;
import com.lljjcoder.citypickerview.widget.CityPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 医生端
 * 个人信息界面 activity
 */
public class DoctorMyInfoModifyActivity extends AppCompatActivity {

    private String et1 = "", et2 = "", et3 = "", et4 = "", et5 = "", et6 = "",et7="";
    private EditText et_doctor_modify_myname, et_doctor_modify_mysex, et_doctor_modify_myage,
           et_doctor_modify_myhospital, et_doctor_modify_mypost;
    private Button      bt_doctor_modify_complete;
    private ImageButton ib_doctor_modify_return, ib_doctor_modify_changepassword;
    private Intent        intent1;
    private User          myUser;
    private MyApplication myApplication;
    private TextView  et_doctor_modify_mylocation,tv_doctor_modify_myaccount;
    private CityPicker cityPicker;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_my_info_modify);
        myApplication = (MyApplication) getApplication();
        myUser = myApplication.getUser();
        init();
    }

    /**
     * 初始化要使用的
     * 按钮控件
     * 文本控件
     */
    private void init () {
        et_doctor_modify_myage = findViewById(R.id.et_doctor_modify_myage);
        et_doctor_modify_myhospital = findViewById(R.id.et_doctor_modify_myhospital);
        et_doctor_modify_myname = findViewById(R.id.et_doctor_modify_myname);
        et_doctor_modify_mysex = findViewById(R.id.et_doctor_modify_mysex);
        et_doctor_modify_mylocation = findViewById(R.id.et_doctor_modify_mylocation);
        et_doctor_modify_mypost = findViewById(R.id.et_doctor_modify_mypost);
        tv_doctor_modify_myaccount=findViewById(R.id.tv_doctor_modify_myaccount);

        et_doctor_modify_myage.setText(String.valueOf(myUser.getAge()));
        et_doctor_modify_myhospital.setText(myUser.getCompany());
        et_doctor_modify_myname.setText(myUser.getName());
        et_doctor_modify_mysex.setText(myUser.getSex());
        et_doctor_modify_mylocation.setText(myUser.getAddress());
        et_doctor_modify_mypost.setText(myUser.getPosition());
        tv_doctor_modify_myaccount.setText(myUser.getAccount());
        et_doctor_modify_mylocation.setOnClickListener(new addListeners());
        ib_doctor_modify_return = findViewById(R.id.ib_doctor_modify_return);
        ib_doctor_modify_return.setOnClickListener(new addListeners());
        bt_doctor_modify_complete = findViewById(R.id.bt_doctor_modify_complete);
        bt_doctor_modify_complete.setOnClickListener(new addListeners());
        ib_doctor_modify_changepassword = findViewById(R.id.ib_doctor_modify_changepassword);
        ib_doctor_modify_changepassword.setOnClickListener(new addListeners());


    }

    /**
     * 滚轮文字的大小
     * 滚轮文字的颜色
     * 省份滚轮是否循环显示
     * 城市滚轮是否循环显示
     * 地区（县）滚轮是否循环显示
     * 滚轮显示的item个数
     * 滚轮item间距
     */
    public void initCityPicker(){
        cityPicker = new CityPicker.Builder(DoctorMyInfoModifyActivity.this)
                .textSize(20)
                .title("地址选择")
                .backgroundPop(0xa0000000)
                .titleBackgroundColor("#0CB6CA")
                .titleTextColor("#000000")
                .backgroundPop(0xa0000000)
                .confirTextColor("#000000")
                .cancelTextColor("#000000")
                .province("xx省")
                .city("xx市")
                .district("xx区")
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .onlyShowProvinceAndCity(false)
                .build();

        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                String province = citySelected[0];

                String city = citySelected[1];

                String district = citySelected[2];

                et_doctor_modify_mylocation.setText(province+city+district);

            }

            @Override
            public void onCancel() {

            }
        });

    }


    /**
     * 弹出框设置
     * 后退弹出
     * 确认  返回个人中心
     * 取消  停留当前页面
     */
    public void Sure () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改未保存，确定退出？");
        builder.setPositiveButton("确定", (dialog, which) -> {
            dialog.dismiss();
            finish();
        })
                .setNegativeButton("取消", (dialog, which) -> {
                    //取消对话框，返回界面
                    dialog.cancel();
                }).create().show();
        //只有点击按钮才行，点击空白无用
        builder.setCancelable(false);
    }

    /**
     * 弹出提示
     */
    public void showToast (final String msg) {
        runOnUiThread(() -> Toast.makeText(DoctorMyInfoModifyActivity.this, msg, Toast
                .LENGTH_SHORT).show());
    }

    /**
     * 按钮的监听事件
     * 返回按钮，返回个人中心
     * 连接服务器，更新界面信息，返回个人中心
     * 跳转修改密码界面
     */
    private class addListeners implements View.OnClickListener {

        @Override
        public void onClick (View v) {
            switch (v.getId()) {
                case R.id.ib_doctor_modify_return:
                    Sure();
                    break;
                case R.id.bt_doctor_modify_complete:
                    et1 = et_doctor_modify_myname.getText().toString().trim();
                    et2 = et_doctor_modify_myage.getText().toString().trim();
                    et3 = et_doctor_modify_myhospital.getText().toString().trim();
                    et4 = et_doctor_modify_mypost.getText().toString().trim();
                    et5 = et_doctor_modify_mysex.getText().toString().trim();
                    et6 = et_doctor_modify_mylocation.getText().toString().trim();
                    et7=tv_doctor_modify_myaccount.getText().toString().trim();

                    //连接服务器操作
                    Thread thread = new Thread(() -> {
                        String send = "";
                        Socket socket;
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("dID", myApplication.getAccount());
                            jsonObject.put("dName", et1);
                            jsonObject.put("dAge", et2);
                            jsonObject.put("dCompany", et3);
                            jsonObject.put("dPosition", et4);
                            jsonObject.put("dSex", et5);
                            jsonObject.put("dAddress", et6);
                            jsonObject.put("dAccount",et7);
                            jsonObject.put("request_type", "7");
                            jsonObject.put("user_type", "d");
                            send = jsonObject.toString();
                            socket = SocketUtil.getSendSocket();
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            out.writeUTF(send);
                            out.close();

                            socket = SocketUtil.setPort(2019);
                            DataInputStream dataInputStream = new DataInputStream(socket
                                    .getInputStream());
                            String message = dataInputStream.readUTF();

                            jsonObject = new JSONObject(message);
                            switch (jsonObject.getString("update_result")) {
                                case "成功":
                                    myUser.setName(et1);
                                    myUser.setSex(et5);
                                    myUser.setCompany(et3);
                                    myUser.setPosition(et4);
                                    myUser.setAge(Integer.parseInt(et2));
                                    myUser.setAddress(et6);
                                    myUser.setAccount(et7);
                                    break;
                                case "失败":
                                    Looper.prepare();
                                  Toast.makeText(DoctorMyInfoModifyActivity.this,"数据更新失败",Toast.LENGTH_LONG);
                                  Looper.loop();
                                    break;

                            }
                            socket.close();
                            Thread.interrupted();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    while (thread.isAlive()) {

                    }
                    finish();
                    break;
                case R.id.ib_doctor_modify_changepassword:
                    intent1 = new Intent(DoctorMyInfoModifyActivity.this, DoctorPasswordActivity
                            .class);
                    startActivity(intent1);
                    break;
                case R.id.et_doctor_modify_mylocation:
                    initCityPicker();
                    cityPicker.show();
                    break;
                default:
                    break;
            }
        }
    }

}
