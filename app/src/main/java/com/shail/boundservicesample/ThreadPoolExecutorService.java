package com.shail.boundservicesample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by iTexico Developer on 7/5/2016.
 */
public class ThreadPoolExecutorService extends Service {

    private static final String TAG = ThreadPoolExecutorService.class.getSimpleName();
    public static final String ACTION = "com.shail.boundservicesample.ThreadPoolExecutorService.action.MAIN";
    // Random number generator
    private final Random mGenerator = new Random();
    // Binder given to clients
    private final IBinder mBinder = new ThreadPoolExecutorServiceBinder();

    // Determine the number of cores on the device
    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // Construct thread pool passing in configuration options
    // int minPoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit,
    // BlockingQueue<Runnable> workQueue
    final private ThreadPoolExecutor mThreadPoolExecutor = new ThreadPoolExecutor(
            NUMBER_OF_CORES * 2,
            NUMBER_OF_CORES * 2,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>()
    );
    private LocalBroadcastManager mLocalBroadcastManager;


    @Override
    public void onCreate() {
        super.onCreate();
        // Get access to local broadcast manager
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // run until explicitly stopped.
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class ThreadPoolExecutorServiceBinder extends Binder {
        ThreadPoolExecutorService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ThreadPoolExecutorService.this;
        }
    }

    /**
     * method for clients
     */
    public void generateRandomNumber() {
        mThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                int nextInt = mGenerator.nextInt(100);
                Log.i(TAG, "generateRandomNumber(),nextInt:" + nextInt);
                Intent intent = new Intent(ACTION);
                intent.putExtra("result", nextInt);
                mLocalBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
