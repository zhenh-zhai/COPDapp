package com.funnyseals.app.feature.patientNursingPlan;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.funnyseals.app.R;

import java.util.ArrayList;
import java.util.List;

public class InstrumentRetimeActivity extends AppCompatActivity {

    private Button       quit_button;
    private Button       done_button;
    private ListView     listView;
    private List<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retime);
        initList();
        ActionBar actionBar = getSupportActionBar();   //隐藏自带actionBar
        if (actionBar != null) {
            actionBar.hide();
        }

        this.listView = findViewById(R.id.listViewtime);

        final MyAdapter adapter = new MyAdapter(InstrumentRetimeActivity.this, mList);
        listView.setAdapter(adapter);
        //ListView item的点击事件
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            TextView txt = view.findViewById(R.id.item_tv);
            Toast.makeText(InstrumentRetimeActivity.this, txt.getText().toString(), Toast.LENGTH_SHORT).show();
        });
        quit_button = findViewById(R.id.quitit);
        done_button = findViewById(R.id.done);
        quit_button.setOnClickListener(v -> {
            finish();  //关闭此activity
        });
        done_button.setOnClickListener(v -> {
            finish();  //关闭此activity
        });

    }

    private void initList() {  //添加所有时间
        mList.add("8：00");
        mList.add("14：00");
        mList.add("18：00");
    }
}
