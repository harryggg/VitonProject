package com.example.fengling.vitontest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

//import java.util.logging.Handler;

public class MyActivity extends Activity {

    private final String TAG = "watch main";
    private static final String START_ACTIVITY_PATH = "/viton/start";
    private GoogleApiClient mGoogleApiClient;
    public static Handler mUiHandler = null;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });




        mUiHandler = new Handler() // Receive messages from service class
        {
            public void handleMessage(Message msg)
            {
                switch(msg.what)
                {
                    case 0:
                        // add the status which came from service and show on GUI
                        Toast.makeText(MyActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                        break;

                    default:
                        break;
                }
            }
        };
    }



    //send sensor data to phone
    public void sendDataToPhone(View v) {

        if(null != MyService.mMyServiceHandler)
        {
            //first build the message and send.
            //put a integer value here and get it from the service handler
            //For Example: lets use 0 (msg.what = 0;) for getting service running status from the service
            Message msg = new Message();
            msg.what = 1;
            msg.obj  = "Add your Extra Message Here"; // you can put extra message here
            MyService.mMyServiceHandler.sendMessage(msg);
        }
    }
        /**

        if (mGoogleApiClient.isConnected()) {
            Log.i(TAG, "connected");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

                    for (final Node node : nodes.getNodes()) {
                        Log.i("wear", "Node: " + node.getId());
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), START_ACTIVITY_PATH, "Hello".getBytes()).await();
                        if (!result.getStatus().isSuccess()) {
                            Log.e(TAG, "msg not sent");
                        } else {
                            Log.i(TAG, "msg sent");
                        }
                    }
                }
            }).start();
        } else {
            Log.e(TAG, "not connected");
        }

    }
     */
    //start the service
    public void onClickStartService(View V)
    {
        //start the service from here //MyService is your service class name
        startService(new Intent(this, MyService.class));
    }
    //Stop the started service
    public void onClickStopService(View V)
    {
        //Stop the running service from here//MyService is your service class name
        //Service will only stop if it is already running.
        stopService(new Intent(this, MyService.class));
    }
    //get service status
    public void getStatus (View v)
    {
        //only we need a handler to send message to any component.
        //here we will get the handler from the service first, then
        //we will send a message to the service.

        if(null != MyService.mMyServiceHandler)
        {
            //first build the message and send.
            //put a integer value here and get it from the service handler
            //For Example: lets use 0 (msg.what = 0;) for getting service running status from the service
            Message msg = new Message();
            msg.what = 0;
            msg.obj  = "Add your Extra Message Here"; // you can put extra message here
            MyService.mMyServiceHandler.sendMessage(msg);
        }

    }
}
