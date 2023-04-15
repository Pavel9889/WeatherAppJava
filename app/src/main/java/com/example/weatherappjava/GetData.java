package com.example.weatherappjava;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GetData extends AsyncTask<URL, Void, String> {

    private static final String TAG = "GetData";
    public interface AsyncResponce{
        void processFinish(String output);
    }

    protected String getResponceFromHttpGetUrl (URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {



            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            String result;
            if (hasInput) {
                result = scanner.next();
                return result;

            } else {
                return null;
            }
        }finally {
            urlConnection.disconnect();
        }
    }



    public AsyncResponce delegate;

    public GetData(AsyncResponce delegate){
        this.delegate = delegate;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(URL[] urls) {
        Log.d(TAG, "doInBackground: called");
        String result  = null;
        URL urlQuery = urls[0];
        try {
            result = getResponceFromHttpGetUrl(urlQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute: called");
        Log.d(TAG, "onPostExecute: "+ result);
        delegate.processFinish(result);
       // super.onPostExecute(o);
    }



}
