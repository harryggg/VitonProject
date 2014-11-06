package com.example.fengling.vitontest;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MyActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks {

    private final String TAG = "phone main";
    private GoogleApiClient mGoogleApiClient;
    private static final String START_ACTIVITY_PATH_START = "/viton/start";
    private static final String START_ACTIVITY_PATH_STOP = "/viton/stop";
    private static final String START_ACTIVITY_PATH_GETDATA = "/viton/getData";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //create data connection
         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.i(TAG, "onConnected: " + connectionHint);
                        // Now you can use the data layer API
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.i(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.e(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        //start connection

        mGoogleApiClient.connect();
        Log.i(TAG,""+mGoogleApiClient.isConnecting());
        //sendMessageToStartActivity();
    }



    public void sendMessageToStartService(View v) {
        Log.i(TAG,"fileTransfer");
        Context context = this.getApplicationContext();
        Class myService = null;

        try {
            myService = Class.forName("com.example.fengling.vitontest.DataTransferService");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Intent myIntent1 = new Intent(context, myService);
        context.startService(myIntent1);


        /*
        if (mGoogleApiClient.isConnected()){
            Log.i(TAG,"connected");

            new Thread (new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

                    for (final Node node : nodes.getNodes()){
                        Log.i(TAG, "Node: " + node.getId());
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), START_ACTIVITY_PATH_START, "Stop data measurement".getBytes()).await();
                        if (!result.getStatus().isSuccess()){
                            Log.e(TAG,"msg not sent");
                        } else {
                            Log.i(TAG,"msg sent");
                        }
                    }
                }
            }).start();
        }else {
            Log.e(TAG, "not connected");
        }
        //saveFile();
        // */

    }

    public void sendMessageToStopService(View v) {
        Context context = this.getApplicationContext();
        Class myService = null;

        try {
            myService = Class.forName("com.example.fengling.vitontest.DataTransferService");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Intent myIntent = new Intent(context, myService);
        myIntent.setAction("TERMINATION");
        context.startService(myIntent);

        /*
        if (mGoogleApiClient.isConnected()){
            Log.i(TAG,"connected");

            new Thread (new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

                    for (final Node node : nodes.getNodes()){
                        Log.i(TAG, "Node: " + node.getId());
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), START_ACTIVITY_PATH_STOP, "Stop data measurement".getBytes()).await();
                        if (!result.getStatus().isSuccess()){
                            Log.e(TAG,"msg not sent");
                            Toast.makeText(getParent(), "test", Toast.LENGTH_LONG).show();
                        } else {
                            Log.i(TAG,"msg sent");
                        }
                    }
                }
            }).start();
        }else {
            Log.e(TAG, "not connected");
        }
        //saveFile();
        */
    }

    public void sendMessageToGetDataFromWatch(View v) {
        if (mGoogleApiClient.isConnected()){
            Log.i(TAG,"connected");

            new Thread (new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

                    for (final Node node : nodes.getNodes()){
                        Log.i(TAG, "Node: " + node.getId());
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), START_ACTIVITY_PATH_GETDATA, "Retrieve measurement".getBytes()).await();
                        if (!result.getStatus().isSuccess()){
                            Log.e(TAG,"msg not sent");
                            Toast.makeText(getParent(), "test", Toast.LENGTH_LONG).show();
                        } else {
                            Log.i(TAG,"msg sent");
                        }
                    }
                }
            }).start();
        }else {
            Log.e(TAG, "not connected");
        }
        //saveFile();
    }

    public void startBLE(View v){
        startActivity(new Intent(this,DeviceScanActivity.class));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //use a public folder
    public File getDocumentsStorageDir(String folderName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS)+"/viton/", folderName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        } else {
            Log.i(TAG, "Directory created");
        }
        return file;
    }
}
