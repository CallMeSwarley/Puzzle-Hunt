package com.socialgaming.androidtutorial.Util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class HTTPGetter extends AsyncTask<String, Void, String> {
    private String serverUrl = "";

    public HTTPGetter(String url) {
        this.serverUrl = url;
    }

    // Uses the default url specified in the Configuration class
    public HTTPGetter() {
        this(Configuration.ServerURL);
    }

    private String httpGetResult = "";

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
            URL getter = new URL(url);
            Log.d("INFORMATION", "URL: " + url);
            Log.e("INFORMATION", "InputStream " + (getter.openConnection()).getInputStream());
            result = convertInputStreamToString((getter.openConnection()).getInputStream());
            Log.d("INFORMATION", "Getter got " + result);
        } catch (Exception e) {
            /*
             * some exception handling should take place here
             */

            Log.e("INFORMATION", "Getter got " + e);

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
        httpGetResult = result;
    }

    /**
     * Returns the result of the operation if needed lateron.
     *
     * @return
     */
    public String getResult() {
        return httpGetResult;
    }

}