package com.example.fengling.vitontest;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class phoneWearableListenerService extends WearableListenerService {

    private static final String TAG = "phone service";
    private static final String START_ACTIVITY_PATH = "/viton/start";
    private static final String BUFFERDATA_PATH = "/viton/bufferData";
    private static int countHeartBeat = 0;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i("TAG", "msg received");
        Log.i("TAG", String.valueOf(messageEvent.getPath()));
        //Toast.makeText(this, "msg received", Toast.LENGTH_LONG).show();
        if (START_ACTIVITY_PATH.equals(messageEvent.getPath())) {

            Log.i(TAG, "received");
        } else {
            Log.e(TAG, "msg not received");
        }
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent event : dataEvents) {
            DataMapItem mapItem = DataMapItem.fromDataItem(event.getDataItem());
            String path = event.getDataItem().getUri().getPath();

            DataItem dataItem = event.getDataItem();
            if (BUFFERDATA_PATH.equals(dataItem.getUri().getPath())) {
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                //show the number of data received
                countHeartBeat += dataMap.getStringArrayList(BUFFERDATA_PATH).size();
                Log.i(TAG, "buffer data received" + countHeartBeat);
                Toast.makeText(this, String.valueOf(countHeartBeat), Toast.LENGTH_LONG).show();
                saveFile(dataMap.getStringArrayList(BUFFERDATA_PATH));


                /////////////////send data to server
                try {
                    sendData("fengling_2000@yahoo.com", dataMap.getStringArrayList(BUFFERDATA_PATH));
                }catch(Exception e){
                    Log.e(TAG, "error sending data", e);
                }
            }
        }
    }

    private void saveFile(ArrayList<String> dataFromWatch) {
        //write to file
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");
        String filename = sdf.format(new Date());


        File folder = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/viton/" + fDate));
        if (!folder.exists()){
            folder.mkdir();
            Log.i(TAG,"folder is in place");
        }else {
            Log.i(TAG,"folder is already there");
        }

        if (!folder.exists()){
            Log.e(TAG,"folder is not created");
            return;
        }

        File file = new File(folder+"/"+filename+".csv");
        OutputStream out = null;
        Log.i(TAG,"output file is "+file.getAbsoluteFile());

        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            for (int i=0;i<dataFromWatch.size();i++){
                out.write(String.valueOf(dataFromWatch.get(i)).getBytes());
                out.write("\n".getBytes());
            }
            Log.i(TAG,"file is written");
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //String path3 = getFilesDir().getAbsolutePath() + "/"+file.getName();
        //File f3 = new File(path3);
        //file.setReadable(true,false);

        //copy the file to public directory
        //String sourcePath = getFilesDir().getAbsolutePath();
        //String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        //copyFile(String.valueOf(sourcePath),"/test.txt",String.valueOf(destinationPath));
    }

    /*
    //copy file
    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }
    */

    public void sendData(String username, List<String> dataList) {

        try {

            Authenticator.setDefault(new BasicAuthenticator());
            URL url = new URL("http://app.vitonhealth.com/users/" + username
                    + "/indexes");
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();

            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = connection.getOutputStream();

            JSONArray array = new JSONArray();

            if(dataList.size() > 0) {
                for (String data : dataList) {
                    String[] items = data.split(" ");
                    JSONObject sample = new JSONObject();
                    sample.put("bpm", items[1]);
                    sample.put("captured", items[0]);
                    array.put(sample);
                }
            }
            String jsonString = array.toString();
            os.write(jsonString.getBytes(Charset.forName("UTF-8")));

            os.flush();

            connection.getResponseCode();
            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    static class BasicAuthenticator extends Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication("admin@viton.com",
                    "secret".toCharArray());
        }
    }

}
