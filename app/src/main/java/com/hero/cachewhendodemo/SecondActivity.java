package com.hero.cachewhendodemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import com.hero.cachewhendo.CacheWhenContants;
import com.hero.cachewhendo.JsonUtils;
import com.hero.cachewhendo.bean.CommonEventDataBean;
import com.hero.cachewhendo.bean.SimpleEventDataBean;
import com.hero.cachewhendo.bean.base.BaseParameterCacheBean;
import com.hero.cachewhendo.helper.CommonCacheWhenDoHelper;
import com.hero.cachewhendo.helper.SimpleCacheWhenDaHelper;
import com.hero.cachewhendo.inerfaces.CommonDoOperationInterface;
import com.hero.cachewhendo.inerfaces.OnCreateParameterCache;
import com.hero.cachewhendo.inerfaces.SimpleDoOperationInterface;
import com.hero.rxcachewhen.OnWhenDoCallBack;
import com.hero.rxcachewhen.RxCacheWhenDoDataBean;
import com.hero.rxcachewhen.RxCacheWhenDoHelper;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * <pre>
 *
 * </pre>
 */
public class SecondActivity extends FragmentActivity {

    private String TAG = "MainActivity";
    private CommonCacheWhenDoHelper cacheWhenDoHelper1;
    private CommonCacheWhenDoHelper cacheWhenDoHelper2;
    private SimpleCacheWhenDaHelper<String> cacheWhenDoHelper3;
    private SimpleCacheWhenDaHelper<Integer> cacheWhenDoHelper4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        cacheWhenDoHelper1 = CommonCacheWhenDoHelper.getInstance()
                //是否进行调试
                .setDebug(true)
                //设置 owner 防止内存泄漏
                .setLifecycleOwner(this)
                //延迟启动时间
                .setInitialDelay(3)
                //循环执行是否等待上一个执行完毕
                .setAtFixed(true)
                //执行线程数
                .setThreadCount(2)
                //循环时间
                .setPeriod(5)
                //单位
                .setUnit(TimeUnit.SECONDS)
                // 停止方式,正在执行的任务会继续执行下去，没有被执行的则中断
                .setShutdown(false)
                //设置处理线程 为null则为当前线程
                .setScheduler(null)
                //操作事件的处理接口
                .setDoOperationInterface(new CommonDoOperationInterface() {
                    @Override
                    public void doOperation(BaseParameterCacheBean cloneData, List<String> eventIdList) {
                        Log.i(TAG, "每一秒钟执行 拿到缓存数据，开始执行操作  cacheWhenDoHelper1  doOperation 回调  eventData："
                                + JsonUtils.javabeanToJson(cloneData)
                                + "\n eventIdList:" + JsonUtils.javabeanToJson(eventIdList));
                        Log.i(TAG, "cacheWhenDoHelper1 回调线程  ：" + Thread.currentThread());

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
                })
                .build();

        cacheWhenDoHelper2 = CommonCacheWhenDoHelper.getInstance()
                //是否进行调试
                .setDebug(true)
                .setAtFixed(false)
                .setThreadCount(9)
                .setPeriod(200)
                .setShutdown(true)
                .setScheduler(AndroidSchedulers.mainThread())
                .setUnit(TimeUnit.MILLISECONDS)
                .build();

        cacheWhenDoHelper3 = SimpleCacheWhenDaHelper.getInstance()
                //是否进行调试
                .setDebug(true)
                .setAtFixed(false)
                .setThreadCount(9)
                .setPeriod(200)
                .setShutdown(true)
                .setUnit(TimeUnit.MILLISECONDS)
                .setScheduler(Schedulers.io())
                //操作事件的处理接口
                .setDoOperationInterface(new SimpleDoOperationInterface<String>() {
                    @Override
                    public void doOperation(String cloneData, List<String> eventIdList) {
                        Log.i(TAG, "每一秒钟执行 拿到缓存数据，开始执行操作  cacheWhenDoHelper3  doOperation 回调  cloneData："
                                + JsonUtils.javabeanToJson(cloneData));
                        Log.i(TAG, "cacheWhenDoHelper3 回调线程  ：" + Thread.currentThread());
                        if (cloneData == null) {
                            return;
                        }

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
                                + "\n 结果:" + cloneData + imax + jmax);

                        for (int i = 0; i < eventIdList.size(); i++) {
                            String eventId = eventIdList.get(i);
                            Log.i(TAG, "去做 " + eventId + "之后的事情");
                        }
                    }
                })
                .build();

        cacheWhenDoHelper4 = SimpleCacheWhenDaHelper.getInstance()
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
                .setScheduler(Schedulers.trampoline())
                .build();

        LiveEventBus.get(CacheWhenContants.EventbusContants.COMMONCACHEWHENDO_EVENT, CommonEventDataBean.class)
                .observe(this, new Observer<CommonEventDataBean>() {
                    @Override
                    public void onChanged(CommonEventDataBean eventDataBean) {
                        ParameterCacheMy cloneData = (ParameterCacheMy) eventDataBean.getCacheBeanClone();
                        Log.i(TAG, "每一秒钟执行 拿到缓存数据，开始执行操作  cacheWhenDoHelper2  doOperation 回调  eventData："
                                + JsonUtils.javabeanToJson(eventDataBean));
                        Log.i(TAG, "cacheWhenDoHelper2 回调线程  ：" + Thread.currentThread());
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
                        List<String> eventIdList = eventDataBean.getIdList();
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

        LiveEventBus.get(CacheWhenContants.EventbusContants.SIMPLECACHEWHENDO_EVENT, SimpleEventDataBean.class)
                .observe(this, new Observer<SimpleEventDataBean>() {
                    @Override
                    public void onChanged(SimpleEventDataBean eventDataBean) {
                        int aInteger = (int) eventDataBean.getData();
                        Log.i(TAG, "每一秒钟执行 拿到缓存数据，开始执行操作  cacheWhenDoHelper4  doOperation 回调  eventData："
                                + JsonUtils.javabeanToJson(eventDataBean));
                        Log.i(TAG, "cacheWhenDoHelper4 回调线程  ：" + Thread.currentThread());
                        //此处模拟一个耗时操作
                        long count = 0;
                        for (int i = 0; i < 100000; i++) {
                            count += i + i + i;
                        }

                        for (int i = 0; i < 100000; i++) {
                            count += (i + i + i) * 3 - i;
                        }
                        Log.d("TAG", "doOperation: " + count);

                        List<String> eventIdList = eventDataBean.getIdList();
                        Log.i(TAG, "每一秒钟执行 处理完成 cacheWhenDoHelper4  doOperation 回调 ：eventIdList:" + JsonUtils.javabeanToJson(eventIdList)
                                + "\n 结果:" + aInteger + " count:" + count);

                        for (int i = 0; i < eventIdList.size(); i++) {
                            String eventId = eventIdList.get(i);
                            Log.i(TAG, "去做 " + eventId + "之后的事情");
                        }
                    }
                });


        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
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
                test4();
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
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

    public void test4() {
        do4(cacheWhenDoHelper4, 1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                do4(cacheWhenDoHelper4, 2);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                do4(cacheWhenDoHelper4, 3);
            }
        }).start();
    }


    private void do1(CommonCacheWhenDoHelper cacheWhenDoHelper) {
        Log.i(TAG, "do1");
        /**
         * 执行操作
         * 一个CacheWhenDoHelper对象只能传一种 参数类型
         * @param idEvent   操作事件的id，记录执行操作的位置，操作回调会返回此id
         * @param onCreateParameterCache  操作处理的参数 / 创建缓存数据，作为结果返回
         */
        cacheWhenDoHelper.doCacheWhen("do1", new OnCreateParameterCache() {
            @Override
            public BaseParameterCacheBean onCreateParameterCache() {
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

    private void do2(CommonCacheWhenDoHelper cacheWhenDoHelper) {
        Log.i(TAG, "do2");
        cacheWhenDoHelper.doCacheWhen("do2", new OnCreateParameterCache() {
            @Override
            public BaseParameterCacheBean onCreateParameterCache() {
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

    private void do3(SimpleCacheWhenDaHelper cacheWhenDoHelper, String aString) {
        Log.i(TAG, "do3");
        cacheWhenDoHelper.doCacheWhen("do3", aString);
    }

    private void do4(SimpleCacheWhenDaHelper cacheWhenDoHelper, Integer aInteger) {
        Log.i(TAG, "do4");
        cacheWhenDoHelper.doCacheWhen("do4", aInteger);
    }

    /**
     * 重写缓存类
     * 保存操作的数据作为属性，可以自由包装
     */
    private static class ParameterCacheMy extends BaseParameterCacheBean {
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