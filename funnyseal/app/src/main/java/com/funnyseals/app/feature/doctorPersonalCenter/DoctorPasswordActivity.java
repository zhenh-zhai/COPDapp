package com.funnyseals.app.feature.doctorPersonalCenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.funnyseals.app.LoginActivity;
import com.funnyseals.app.R;
import com.funnyseals.app.feature.MyApplication;
import com.funnyseals.app.util.SocketUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 医生端
 * 修改密码界面 activity
 */
public class DoctorPasswordActivity extends AppCompatActivity {
    private EditText et_doctor_change_oldpassword, et_doctor_change_newpassword,
            et_doctor_change_againpassword;
    private Button        bt_doctor_change_complete;
    private ImageButton   ib_doctor_change_return;
    private MyApplication myApplication;
    private boolean       type = false;

    /**
     * 判断密码不能有特殊字符，只能是英文或者数字
     * 判断两次密码一致
     */
    public static boolean isLetterOrDigit (String str) {
        boolean isLetterOrDigit = false;
        for (int i = 0; i < str.length(); i++)
            if (Character.isLetterOrDigit(str.charAt(i))) {
                isLetterOrDigit = true;
            }
        String regex = "^[a-zA-Z0-9]{6,20}$";
        boolean isRight = isLetterOrDigit && str.matches(regex);
        return isRight;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_password);
        myApplication = (MyApplication) getApplication();
        init();

    }

    /**
     * 检测密码的合法性
     * 密码不为空
     * 密码不包含特殊字符
     * 密码为6-20位
     * 两次密码一致
     */
    public boolean myCorrectPas (String oldpassword, String newpassword, String againpassword) {

        if (oldpassword.equals("") || newpassword.equals("") || againpassword.equals("")) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isLetterOrDigit(newpassword)) {
            Toast.makeText(this, "密码不能包含特殊字符", Toast.LENGTH_SHORT).show();
            return false;
        } else if (newpassword.length() < 6 || newpassword.length() > 20) {
            Toast.makeText(this, "密码长度应为6-20位", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isSame(newpassword, againpassword)) {
            Toast.makeText(this, "两次密码不一致，请重新输入", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    public boolean isSame (String str1, String str2) {
        boolean isSame = false;
        if (str1.equals(str2)) {
            isSame = true;
        }
        return isSame;
    }

    /**
     * 初始化控件
     */
    private void init () {
        //获取各个edittext值
        et_doctor_change_oldpassword = findViewById(R.id.et_doctor_change_oldpassword);
        et_doctor_change_newpassword = findViewById(R.id.et_doctor_change_newpassword);
        et_doctor_change_againpassword = findViewById(R.id.et_doctor_change_againpassword);


        ib_doctor_change_return = findViewById(R.id.ib_doctor_change_return);
        ib_doctor_change_return.setOnClickListener(new addListeners());
        bt_doctor_change_complete = findViewById(R.id.bt_doctor_change_complete);
        bt_doctor_change_complete.setOnClickListener(new addListeners());

    }

    /**
     * 后退按钮提示
     * 确认 返回个人信息
     * 取消  停留当前页面
     */
    public void Sure () {
        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorPasswordActivity.this);
        builder.setMessage("密码未修改，确定退出？");
        builder.setPositiveButton("确定", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            //取消对话框，返回界面
            dialog.cancel();
        }).create().show();
        //只有点击按钮才行，点击空白无用
        builder.setCancelable(false);


    }

    /**
     * 获取
     * 旧密码
     * 新密码
     * 重复输入的新密码
     */
    public String getOldpassword () {
        return et_doctor_change_oldpassword.getText().toString().trim();
    }

    public String getNewpassword () {
        return et_doctor_change_newpassword.getText().toString().trim();
    }

    public String getAgainpassword () {
        return et_doctor_change_againpassword.getText().toString().trim();
    }

    /**
     * 按钮监听
     * 返回个人信息，取消修改密码操作
     * 完成修改，连接服务器，更新密码
     */
    private class addListeners implements View.OnClickListener {

        @Override
        public void onClick (View v) {
            switch (v.getId()) {
                case R.id.ib_doctor_change_return:
                    Sure();
                    break;
                case R.id.bt_doctor_change_complete:
                    if (myCorrectPas(getOldpassword(), getNewpassword(), getAgainpassword())) {
                        new Thread(() -> {
                            String send = "";
                            Socket socket;
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("request_type", "8");
                                jsonObject.put("modify_type", "update");
                                jsonObject.put("ID", myApplication.getAccount());
                                jsonObject.put("oldPassword", getOldpassword());
                                jsonObject.put("newPassword", getNewpassword());
                                send = jsonObject.toString();
                                socket = SocketUtil.getSendSocket();
                                DataOutputStream out = new DataOutputStream(socket
                                        .getOutputStream());
                                out.writeUTF(send);
                                out.close();

                                socket = SocketUtil.getGetSocket();
                                DataInputStream dataInputStream = new DataInputStream(socket
                                        .getInputStream());
                                String message = dataInputStream.readUTF();

                                jsonObject = new JSONObject(message);
                                switch (jsonObject.getString("password_result")) {
                                    case "0":
                                        type = true;
                                        Looper.prepare();
                                        Toast.makeText(DoctorPasswordActivity.this, "修改密码成功",
                                                Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                        break;
                                    case "1":
                                        Looper.prepare();
                                        Toast.makeText(DoctorPasswordActivity.this, "用户名错误",
                                                Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                        break;
                                    case "2":
                                        Looper.prepare();
                                        Toast.makeText(DoctorPasswordActivity.this, "密码错误", Toast
                                                .LENGTH_LONG).show();
                                        Looper.loop();
                                        break;
                                    case "3":
                                        Looper.prepare();
                                        Toast.makeText(DoctorPasswordActivity.this, "修改失败", Toast
                                                .LENGTH_LONG).show();
                                        Looper.loop();
                                        break;
                                }
                                socket.close();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                    if (type) {
                        Intent intent = new Intent(DoctorPasswordActivity.this, LoginActivity
                                .class);
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
