package com.hero.cachewhendodemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import com.hero.cachewhendo.JsonUtils;
import com.hero.rxcachewhen.RxCacheWhenDoDataBean;
import com.hero.rxcachewhen.RxCacheWhenDoHelper;
import com.hero.rxcachewhen.OnWhenDoCallBack;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;

/**
 * <pre>
 *
 * </pre>
 */
public class FirstActivity extends FragmentActivity {
    private static final String TAG = "FirstActivity";
    private RxCacheWhenDoHelper rxCacheWhenDoHelper;
    private int clickCount2;
    private int clickCount3;
    private OnWhenDoCallBack onWhenDoCallBack = new OnWhenDoCallBack() {
        @Override
        public void onNext(@NonNull RxCacheWhenDoDataBean rxCacheWhenDoDataBean) {
            Log.i(TAG, "获得处理数据: " + JsonUtils.javabeanToJson(rxCacheWhenDoDataBean));

            List<String> eventIdList = rxCacheWhenDoDataBean.getEventIdList();
            if (eventIdList != null) {
                for (int i = 0; i < eventIdList.size(); i++) {
                    String eventId = eventIdList.get(i);
                    Log.i(TAG, "处理: " + eventId + " 之后的事");
                }
            }
        }

        @Override
        public void onError(@NonNull Throwable throwable) {
            Log.e(TAG, "WhenDoCallBack throwable: " + throwable.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        rxCacheWhenDoHelper = RxCacheWhenDoHelper.getInstance()
                //是否打开调试日志
                .setDebug(true)
                //操作事件的处理接口
                //注意此处使用弱引用 所以不要以局部变量作为参数，否则很快被回收
                .setWhenDoCallBack(onWhenDoCallBack)
                //设置 owner 防止内存泄漏
                .setLifecycleOwner(this)
                //设置时间单位
                .setUnit(TimeUnit.SECONDS)
                //设置缓存时间
                .setPeriod(5)
                //设置处理线程  默认是当前线程
                .setScheduler(AndroidSchedulers.mainThread())
                .builder();
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: button1");
                rxCacheWhenDoHelper.doCacheWhen("button1", "button1");
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount2++;
                Log.i(TAG, "onClick: button2  : " + clickCount2);
                rxCacheWhenDoHelper.doCacheWhen("button2", "button2  : " + clickCount2);
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount3++;
                Log.i(TAG, "onClick: button3  : " + clickCount3);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        rxCacheWhenDoHelper.doCacheWhen("button3", "button3  : " + clickCount3);
                    }
                }).start();
            }
        });

       /* findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxCacheWhenDoHelper.stop();
            }
        });
    }
}
