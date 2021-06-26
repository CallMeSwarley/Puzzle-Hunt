package com.socialgaming.androidtutorial.Util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class HTTPPoster extends AsyncTask<String, Void, String> {
    private String serverUrl = "";
    private String httpPostResult;

    public HTTPPoster(String url) {
        this.serverUrl = url;
    }

    // Uses the default url specified in the Configuration class
    public HTTPPoster() {
        this(Configuration.ServerURL);
    }

    @Override
    protected String doInBackground(String... params) {
        BufferedReader inBuffer = null;
        String url = this.serverUrl;
        String result = "fail";

        for (String parameter : params) {
            url = url + "/" + parameter;
        }

        try {
            // https://stackoverflow.com/questions/31433687/android-gradle-apache-httpclient-does-not-exist
            URL poster = new URL(url);
            Log.d("INFORMATION", "URL: " + url);
            HttpURLConnection connection = (HttpURLConnection) poster.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            result = connection.getResponseMessage();
            //Remove following line to revert the poster to its inital state
            result = convertInputStreamToString(connection.getInputStream());

            Log.d("INFORMATION", "Poster returned " + result);

        } catch (Exception e) {

            Log.e("INFORMATION", "Post failed with " + e);
            Log.e("INFORMATION", Arrays.toString(e.getStackTrace()));
            /*
             * some useful exception handling should be here
             */
        } finally {
            if (inBuffer != null) {
                try {
                    inBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    protected void onPostExecute(String result) {
        // this is used to access the result of the HTTP GET lateron
        httpPostResult = result;
    }

    /**
     * Returns the result of the operation if needed later on.
     *
     * @return
     */

    public String getResult() {
        return httpPostResult;
    }
}


