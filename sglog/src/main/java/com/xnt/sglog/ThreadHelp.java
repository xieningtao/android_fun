package com.xnt.sglog;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadHelp {
    private static final int nThreads = 5;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "ThreadHelp #" + mCount.getAndIncrement());
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    };
    private static ExecutorService excutors = Executors
            .newFixedThreadPool(nThreads, sThreadFactory);
    private static DelayThread delayThread = new DelayThread("delay_thread");
    private static DelayThread elkLogThread = new DelayThread("log_thread");
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    /**
     * elk日志专用线程
     * @param runnable
     */
    public static void runInLogThread(Runnable runnable){
        elkLogThread.excute(runnable,0);
    }
    public static void runInBackThreadPool(Runnable runnable) {
        excutors.execute(runnable);
    }

    public static void runInSingleBackThread(Runnable runnable, int delay) {
        delayThread.excute(runnable, delay);
    }
    public static void runInSingleBackThread(Runnable runnable) {
        delayThread.excute(runnable,0);
    }

    public static void removeRunableInSingleBack(Runnable runnable) {
        delayThread.mHandler.removeCallbacks(runnable);
    }

    public static void runInMain(Runnable runnable) {
        if(Looper.myLooper() == Looper.getMainLooper()){
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }

    public static void removeRunnable(Runnable runnable) {
        sHandler.removeCallbacks(runnable);
    }

    public static void runInMain(Runnable runnable, int delay) {
        sHandler.postDelayed(runnable, delay);
    }

    static class DelayThread extends HandlerThread {
        private Handler mHandler;

        public DelayThread(String name) {
            super(name);
            init();
        }

        public DelayThread(String name, int priority) {
            super(name, priority);
            init();
        }

        private void init() {
            start();
            mHandler = new Handler(getLooper());
        }

        public void excute(Runnable runnable, int delay) {
            mHandler.postDelayed(runnable, delay);
        }

    }
}
