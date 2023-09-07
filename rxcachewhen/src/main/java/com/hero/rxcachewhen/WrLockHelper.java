package com.hero.rxcachewhen;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <pre>
 *
 * </pre>
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
