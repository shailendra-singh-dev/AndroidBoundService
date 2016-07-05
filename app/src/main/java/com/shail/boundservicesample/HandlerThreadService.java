package com.shail.boundservicesample;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by iTexico Developer on 7/5/2016.
 */
public class HandlerThreadService extends Service {

    public static final String ACTION = "com.shail.boundservicesample.action.MAIN";
    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;
    private LocalBroadcastManager mLocalBroadcastManager;

    // Define how the handler will process messages
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        // Define how to handle any incoming messages here
        @Override
        public void handleMessage(Message message) {
            // ...
            // When needed, stop the service with
            // stopSelf();
        }
    }

    // Fires when a service is first initialized
    public void onCreate() {
        super.onCreate();
        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("HandlerThreadService.HandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());

        // Get access to local broadcast manager
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    // Fires when a service is started up
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Send empty message to background thread
        mServiceHandler.sendEmptyMessageDelayed(0, 500);

        // or run code in background
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                // Do something here in background!
                // ...
                // If desired, stop the service
//                stopSelf();
            }
        });

        //Updating UI using LocalBroadcastManager. We can use also Messanger once we override onBind()
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                // Send broadcast out with action filter and extras
                Intent intent = new Intent(ACTION);
                intent.putExtra("result", "baz");
                mLocalBroadcastManager.sendBroadcast(intent);
                // If desired, stop the service
//                stopSelf();
            }
        });
        // Keep service around "sticky"
        return START_STICKY;
    }

    // Defines the shutdown sequence
    @Override
    public void onDestroy() {
        // Cleanup service before destruction
        mHandlerThread.quit();
    }

    // Binding is another way to communicate between service and activity
    // Not needed here, local broadcasts will be used instead
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
