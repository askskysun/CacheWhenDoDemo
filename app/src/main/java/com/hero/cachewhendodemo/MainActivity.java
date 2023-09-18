package com.hero.cachewhendodemo;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.hero.cachewhendo.CacheWhenContants;
import com.hero.cachewhendo.bean.CommonEventDataBean;
import com.hero.cachewhendo.bean.SimpleEventDataBean;
import com.hero.cachewhendo.helper.CommonCacheWhenDoHelper;
import com.hero.cachewhendo.helper.SimpleCacheWhenDaHelper;
import com.hero.cachewhendo.JsonUtils;
import com.hero.cachewhendo.inerfaces.CommonDoOperationInterface;
import com.hero.cachewhendo.inerfaces.OnCreateParameterCache;
import com.hero.cachewhendo.bean.base.BaseParameterCacheBean;
import com.hero.cachewhendo.inerfaces.SimpleDoOperationInterface;
import com.jeremyliao.liveeventbus.LiveEventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends FragmentActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SecondActivity.class));
            }
        });

        findViewById(R.id.buttontoact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,FirstActivity.class));
            }
        });
    }
}