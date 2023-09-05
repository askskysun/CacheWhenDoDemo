package com.hero.cachewhendodemo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <pre>
 *
 * </pre>
 * Author by sunhaihong, Email 1910713921@qq.com, Date on 2023/9/5.
 */
public class WrLockHelper {

    //读写锁
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    //获取写锁
    private Lock wlock = rwLock.writeLock();
    //获取读锁
    private Lock rLock = rwLock.readLock();


    public void wlockDo(OnDoInterface onDoInterface) {
        if (onDoInterface == null) {
            return;
        }

        wlock.lock();
        try {
            onDoInterface.onDo();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            wlock.unlock();
        }
    }

    public void rlockDo(OnDoInterface onDoInterface) {
        if (onDoInterface == null) {
            return;
        }

        rLock.lock();
        try {
            onDoInterface.onDo();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            rLock.unlock();
        }
    }

    public interface OnDoInterface {
        void onDo();
    }
}
