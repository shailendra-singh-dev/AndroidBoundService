package com.shail.boundservicesample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by iTexico Developer on 7/5/2016.
 */
public class HandlerThreadActivity  extends AppCompatActivity{

    @Override
    protected void onStart() {
        super.onStart();
        launchService();
    }

    // Call `launchTestService` to initiate the service
    public void launchService() {
        // Construct our Intent specifying the Service
        Intent i = new Intent(this, HandlerThreadService.class);
        // Add extras to the bundle
        i.putExtra("foo", "bar");
        // Start the service
        startService(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(HandlerThreadService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
        // or `unregisterReceiver(testReceiver)` for a normal broadcast
    }

    // Define the callback for what to do when message is received
    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");
            Toast.makeText(HandlerThreadActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    };
}
