package com.funnyseals.app.feature.patientNursingPlan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.funnyseals.app.R;
import com.funnyseals.app.feature.MyApplication;
import com.funnyseals.app.model.SportsPlan;
import com.funnyseals.app.model.UserDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class PatientThreeFragment extends Fragment {
    private static Connection conn;
    MyApplication application;
    private List<SportsPlan> sportsPlans;
    //将数据封装成数据源
    List<Map<String, Object>> sports_list = new ArrayList<>();
    private Thread  mThread;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                sportsPlans = (List<SportsPlan>) msg.obj;

                for (SportsPlan p : sportsPlans) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sportstitle", p.getSportsName());
                    map.put("sportsimg", R.drawable.sports);
                    map.put("sportscontent", p.getSportsTracks());
                    map.put("sportsattention", p.getSportsNote());
                    map.put("sportstime", "10:00");
                    sports_list.add(map);
                }

                ListView listview = getActivity().findViewById(R.id.listViewsports);
                listview.setAdapter(new MyAdapter());
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (mThread == null) {
            mThread = new Thread(runnable);
            mThread.start();
        }
        View view = inflater.inflate(R.layout.fragment_patient_three, null);
        return view;
    }

    Runnable runnable = () -> {
        try {
            conn = UserDao.getConnection();
            application = (MyApplication) getActivity().getApplication();
            String id = application.getAccount();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement("SELECT HLJH_BH FROM HLJH WHERE HZ_ZH=? and HLJH_SYZT=1");
                statement.setString(1, id);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    PreparedStatement sportsS = conn.prepareStatement("SELECT * FROM HLJHYD WHERE HLJH_BH=?");  //！！！！
                    sportsS.setString(1, rs.getString(1));
                    rs = sportsS.executeQuery();
                    sportsPlans = new ArrayList<>();
                    while (rs.next()) {
                        sportsPlans.add(new SportsPlan(rs.getString("HLJHYD_YDZL"), rs.getString("HLJHYD_JYSJ"), rs.getString("HLJHYD_ZYSX")));
                    }
                    Message message = Message.obtain();
                    message.what = 0;
                    message.obj = sportsPlans;
                    mHandler.sendMessage(message);
                    conn.close();
                    sportsS.close();
                    rs.close();
                    statement.close();
                }
            } else {
                System.err.println("警告: DbConnectionManager.getConnection() 获得数据库链接失败.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return sports_list.size();
        }

        @Override
        public Object getItem(int position) {
            return sports_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            PatientThreeFragment.ViewHolder mHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.sports_list_item, null);
                mHolder = new ViewHolder();
                mHolder.cardsports_title = view.findViewById(R.id.sports);
                mHolder.cardsports_image = view.findViewById(R.id.sportsImg);
                mHolder.cardsports_content = view.findViewById(R.id.sportsnum);
                mHolder.cardsports_attention = view.findViewById(R.id.sportsattention);
                mHolder.cardsports_time = view.findViewById(R.id.sportstime);
                view.setTag(mHolder);  //将ViewHolder存储在View中
            } else {
                view = convertView;
                mHolder = (PatientThreeFragment.ViewHolder) view.getTag();  //重新获得ViewHolder
            }
            mHolder.cardsports_title.setText(sports_list.get(position).get("sportstitle").toString());
            mHolder.cardsports_image.setImageResource((int) sports_list.get(position).get("sportsimg"));
            mHolder.cardsports_content.setText(sports_list.get(position).get("sportscontent").toString());
            mHolder.cardsports_attention.setText(sports_list.get(position).get("sportsattention").toString());
            mHolder.cardsports_time.setText(sports_list.get(position).get("sportstime").toString());

            Button moretime = view.findViewById(R.id.moresportstime);
            moretime.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), SportsRetimeActivity.class);
                startActivity(intent);
            });
            return view;
        }
    }

    class ViewHolder {
        TextView  cardsports_title;
        ImageView cardsports_image;
        TextView  cardsports_content;
        TextView  cardsports_attention;
        TextView  cardsports_time;
    }
}
