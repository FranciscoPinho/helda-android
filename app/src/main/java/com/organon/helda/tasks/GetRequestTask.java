package com.organon.helda.tasks;

import android.os.AsyncTask;

import com.organon.helda.utils.Utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;


public class GetRequestTask extends AsyncTask<Void, Void, String> {

    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000; //milliseconds
    public static final int CONNECTION_TIMEOUT = 15000; //milliseconds

    @Override
    protected String doInBackground(Void... voids) {
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        String urlStr = "http://www.mocky.io/v2/5a99f7882f0000e308a7fd48";
        String result = null;

        try {
            //Create a URL object holding our url
            URL url = new URL(urlStr);

            //Create a connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            int status = connection.getResponseCode();
            switch (status) {
                case 200:
                    inputStream = connection.getInputStream();
                    break;
                default:
                    inputStream = connection.getErrorStream();
            }
            result = Utils.convertStreamToString(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.disconnect();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }
}
