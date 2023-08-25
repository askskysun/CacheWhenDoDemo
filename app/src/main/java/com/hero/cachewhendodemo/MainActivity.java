package com.hero.cachewhendodemo;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.hero.cachewhendodemo.cachewhen.CacheWhenDoHelper;
import com.hero.cachewhendodemo.cachewhen.DoOperationInterface;
import com.hero.cachewhendodemo.cachewhen.EventData;
import com.hero.cachewhendodemo.cachewhen.JsonUtils;
import com.hero.cachewhendodemo.cachewhen.OnCreateParameterCache;
import com.hero.cachewhendodemo.cachewhen.ParameterCache;
import com.hero.cachewhendodemo.cachewhen.SimpleParameterCache;
import com.jeremyliao.liveeventbus.LiveEventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity implements DoOperationInterface {

    private String TAG = "MainActivity";
    private CacheWhenDoHelper cacheWhenDoHelper1;
    private CacheWhenDoHelper cacheWhenDoHelper2;
    private CacheWhenDoHelper cacheWhenDoHelper3;
    private DoOperationInterface doOperationInterface3 = new DoOperationInterface() {
        @Override
        public void doOperation(ParameterCache cloneData, List<String> eventIdList) {
            Log.i(TAG, "每一秒钟执行 拿到缓存数据，开始执行操作  cacheWhenDoHelper3  doOperation 回调  cloneData："
                    + JsonUtils.javabeanToJson(cloneData));
            if (cloneData == null) {
                return;
            }
            if (!(cloneData instanceof SimpleParameterCache)) {
                return;
            }
            //获取参数
            Object data = ((SimpleParameterCache) cloneData).getData();
            //此处模拟一个耗时操作
            int imax = 0;
            int jmax = 0;
            for (int i = 0; i < 100000; i++) {
                for (int j = 0; j < 100; j++) {
                    jmax = j;
                }
                imax = i;
            }

            Log.i(TAG, "每一秒钟执行 处理完成 cacheWhenDoHelper3  doOperation 回调 ：eventIdList:" + JsonUtils.javabeanToJson(eventIdList)
                    + "\n 结果:" + data + imax + jmax);

            for (int i = 0; i < eventIdList.size(); i++) {
                String eventId = eventIdList.get(i);
                Log.i(TAG, "去做 " + eventId + "之后的事情");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cacheWhenDoHelper1 = CacheWhenDoHelper.getInstance()
                //是否进行调试
                .setDebug(true)
                //延迟启动时间
                .setInitialDelay(1)
                //循环执行是否等待上一个执行完毕
                .setAtFixed(true)
                //执行线程数
                .setThreadCount(2)
                //循环时间
                .setPeriod(3)
                //单位
                .setUnit(TimeUnit.SECONDS)
                // 停止方式,正在执行的任务会继续执行下去，没有被执行的则中断
                .setShutdown(false)
                //操作事件的处理接口
                //注意此处使用弱引用 所以不要以局部变量作为参数，否则很快被回收
                .setDoOperationInterface(this)
                .build();

        cacheWhenDoHelper2 = CacheWhenDoHelper.getInstance()
                //是否进行调试
                .setDebug(true)
                .setAtFixed(false)
                .setThreadCount(9)
                .setPeriod(200)
                .setShutdown(true)
                .setUnit(TimeUnit.MILLISECONDS)
                .build();

        cacheWhenDoHelper3 = CacheWhenDoHelper.getInstance()
                //是否进行调试
                .setDebug(true)
                .setAtFixed(false)
                .setThreadCount(9)
                .setPeriod(200)
                .setShutdown(true)
                .setUnit(TimeUnit.MILLISECONDS)
                //操作事件的处理接口
                //注意此处使用弱引用 所以不要以局部变量作为参数，否则很快被回收
                .setDoOperationInterface(doOperationInterface3)
                .build();

        LiveEventBus.get(CacheWhenDoHelper.LIVEEVENTBUS_KEY, EventData.class)
                .observe(this, new Observer<EventData>() {
                    @Override
                    public void onChanged(EventData eventData) {
                        ParameterCacheMy cloneData = (ParameterCacheMy) eventData.getClone();
                        Log.i(TAG, "每一秒钟执行 拿到缓存数据，开始执行操作  cacheWhenDoHelper2  doOperation 回调  eventData："
                                + JsonUtils.javabeanToJson(eventData));
                        //此处模拟一个耗时操作
                        long count = 0;
                        for (int i = 0; i < 100000; i++) {
                            count += i + i + i;
                        }

                        for (int i = 0; i < 100000; i++) {
                            count += (i + i + i) * 3 - i;
                        }
                        Log.d("TAG", "doOperation: " + count);

                        List<String> dataList = cloneData.getData();
                        List<String> eventIdList = eventData.getIdList();
                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.set(i, "第" + i + "个结果:" + dataList.get(i));
                        }
                        Log.i(TAG, "每一秒钟执行 处理完成 cacheWhenDoHelper2  doOperation 回调 ：eventIdList:" + JsonUtils.javabeanToJson(eventIdList)
                                + "\n 结果:" + JsonUtils.javabeanToJson(dataList));

                        for (int i = 0; i < eventIdList.size(); i++) {
                            String eventId = eventIdList.get(i);
                            Log.i(TAG, "去做 " + eventId + "之后的事情");
                        }
                    }
                });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test1();
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test2();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test3();
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheWhenDoHelper1.stop();
                cacheWhenDoHelper2.stop();
            }
        });
    }

    public void test1() {
        do1(cacheWhenDoHelper1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                do2(cacheWhenDoHelper1);
            }
        }).start();
    }

    public void test2() {
        do1(cacheWhenDoHelper2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                do2(cacheWhenDoHelper2);
            }
        }).start();
    }

    public void test3() {
        do3(cacheWhenDoHelper3, "一");
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                do3(cacheWhenDoHelper3, "二");
            }
        });
    }

    private void do1(CacheWhenDoHelper cacheWhenDoHelper) {
        Log.i(TAG, "do1");
        /**
         * 执行操作
         * 一个CacheWhenDoHelper对象只能传一种 参数类型
         * @param idEvent   操作事件的id，记录执行操作的位置，操作回调会返回此id
         * @param onCreateParameterCache   创建缓存数据，作为结果返回
         */
        cacheWhenDoHelper.doCacheWhen("do1", new OnCreateParameterCache() {
            @Override
            public ParameterCache onCreateParameterCache() {
                ParameterCacheMy parameterCacheMy = new ParameterCacheMy();
                List<String> arrayList = new ArrayList();
                for (int i = 0; i < 6; i++) {
                    arrayList.add("1");
                }
                parameterCacheMy.setData(arrayList);
                Log.i(TAG, "do1 传入数据 parameterCacheMy：" + JsonUtils.javabeanToJson(parameterCacheMy));
                return parameterCacheMy;
            }
        });
    }

    private void do2(CacheWhenDoHelper cacheWhenDoHelper) {
        Log.i(TAG, "do2");
        cacheWhenDoHelper.doCacheWhen("do2", new OnCreateParameterCache() {
            @Override
            public ParameterCache onCreateParameterCache() {
                ParameterCacheMy parameterCacheMy = new ParameterCacheMy();
                List<String> arrayList = new ArrayList();
                for (int i = 0; i < 6; i++) {
                    arrayList.add("2");
                }
                parameterCacheMy.setData(arrayList);
                Log.i(TAG, "do2 传入数据 parameterCacheMy：" + JsonUtils.javabeanToJson(parameterCacheMy));
                return parameterCacheMy;
            }
        });
    }

    private void do3(CacheWhenDoHelper cacheWhenDoHelper, String aString) {
        Log.i(TAG, "do3");
        cacheWhenDoHelper.doCacheWhen("do3", aString);
    }

    /**
     * 处理定时做的事情
     *
     * @param cloneData   复制之后的缓存数据
     * @param eventIdList 调用事件列表
     */
    @Override
    public void doOperation(ParameterCache cloneData, List<String> eventIdList) {
        Log.i(TAG, "每一秒钟执行 拿到缓存数据，开始执行操作  cacheWhenDoHelper1  doOperation 回调  eventData："
                + JsonUtils.javabeanToJson(cloneData)
                + "\n eventIdList:" + JsonUtils.javabeanToJson(eventIdList));
        //此处模拟一个耗时操作
        long count = 0;
        for (int i = 0; i < 10000; i++) {
            count += i + i + i;
        }

        for (int i = 0; i < 100000; i++) {
            count += (i + i + i) * 3 - i;
        }
        Log.d("TAG", "doOperation: " + count);

        ParameterCacheMy data1 = (ParameterCacheMy) cloneData;
        List<String> dataList = data1.getData();
        for (int i = 0; i < dataList.size(); i++) {
            dataList.set(i, "操作结果:第" + i + "个，事件id为：" + dataList.get(i));
        }
        Log.i(TAG, "每一秒钟执行 处理完成 cacheWhenDoHelper1  doOperation 回调 ：eventIdList:" + JsonUtils.javabeanToJson(eventIdList)
                + "\n 结果:" + JsonUtils.javabeanToJson(dataList));
        for (int i = 0; i < eventIdList.size(); i++) {
            String eventId = eventIdList.get(i);
            Log.i(TAG, "去做 " + eventId + "之后的事情");
        }
    }

    /**
     * 重写缓存类
     * 保存操作的数据作为属性，可以自由包装
     */
    private static class ParameterCacheMy extends ParameterCache {
        public List<String> getData() {
            return dataList;
        }

        public void setData(List<String> dataList) {
            this.dataList = dataList;
        }

        private List<String> dataList;

        public ParameterCacheMy() {
        }

        /**
         * 此类一定要实现  复制一份数据，不影响原数据
         *
         * @return
         */
        @Override
        public ParameterCacheMy clone() {
            ParameterCacheMy parameterCacheMy = new ParameterCacheMy();
            List<String> dataListClone = new ArrayList<>();
            dataListClone.addAll(dataList);
            parameterCacheMy.setData(dataListClone);
            return parameterCacheMy;
        }
    }
}