package com.hero.cachewhendo.helper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre>
 *
 * </pre>
 */
public class ReentrantLockHelper {
    private Lock reentrantLock = new ReentrantLock();

    public void reentLockDo(WrLockHelper.OnDoInterface onDoInterface) {
        if (onDoInterface == null) {
            return;
        }

        reentrantLock.lock();
        try {
            onDoInterface.onDo();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }
}
