package com.shail.boundservicesample;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by iTexico Developer on 7/5/2016.
 */
public class ThreadPoolExecutorActivity extends AppCompatActivity {
    // Flag indicating whether we have called bind on the service.
    boolean mBound;

    private ThreadPoolExecutorService mThreadPoolExecutorService;

    // Class for interacting with the main interface of the service.
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            // This is called when the connection with the iBinder has been established, giving us the object we can use
            // to interact with the iBinder.  We are communicating with the iBinder using a Messenger, so here we get a
            // client-side representation of that from the raw IBinder object.
            ThreadPoolExecutorService.ThreadPoolExecutorServiceBinder threadPoolExecutorServiceBinder = (ThreadPoolExecutorService.ThreadPoolExecutorServiceBinder) iBinder;
            mThreadPoolExecutorService = threadPoolExecutorServiceBinder.getService();
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected -- that is,
            // its process crashed.
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.threadpoolexcutor_main);
        Button button = (Button) findViewById(R.id.performBackgroundOperationButton);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackgroundOperation();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        bindService(new Intent(this, ThreadPoolExecutorService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    public void performBackgroundOperation() {
        if (mBound) {
            // Call a method from the ThreadPoolExecutorService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            mThreadPoolExecutorService.generateRandomNumber();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(ThreadPoolExecutorService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
        // or `registerReceiver(mBroadcastReceiver, filter)` for a normal broadcast
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        // or `unregisterReceiver(mBroadcastReceiver)` for a normal broadcast
    }

    // Define the callback for what to do when message is received
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == ThreadPoolExecutorService.ACTION){
                int result = intent.getIntExtra("result",0);
                Toast.makeText(ThreadPoolExecutorActivity.this, "Result:"+result, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
