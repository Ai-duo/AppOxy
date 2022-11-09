package com.kd.appoxy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.kd.appoxy.databinding.ActivityMainBinding;
import com.kd.appoxy.service.ElementsService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
    Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        EventBus.getDefault().register(this);
        initTimer();
        tf = Typeface.createFromAsset(getAssets(), "fonts/simsun.ttc");
        mainBinding.setType(tf);
        startService();
    }
    public void startService() {
        Intent intent = new Intent(this, ElementsService.class);
        startService(intent);
    }
    Timer timer;

    public void initTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(sdf.format(new Date()));
            }
        }, 0, 60 * 1000);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 E HH:mm", Locale.CHINA);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ws(WS ws) {
        mainBinding.setWs(ws);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void oxy(Oxy oxy) {
        int s = Integer.parseInt(oxy.oxy);
        if(s<500){
            mainBinding.setInfo("浓度中，空气一般");
        }else if(s<1000){
            mainBinding.setInfo("浓度高，空气清新");
        }else{
            mainBinding.setInfo("浓度很高，空气很清新");
        }
        mainBinding.setOxy(oxy);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void time(String date) {
        mainBinding.setDate(date);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}