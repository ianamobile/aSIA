package com.iana.sia.utility;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RestApiClient {

    public static ApiResponse callGetApi(String requestURL) {
        StringBuffer responseBuffer = new StringBuffer();
        ApiResponse apiResponse = new ApiResponse();
        int statusCode = 0;

        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;

        try {

            URL url = new URL(requestURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            statusCode = httpURLConnection.getResponseCode();
            Log.v("log_tag", "Status Code " + statusCode);

            if(statusCode != HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(
                        httpURLConnection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(
                        httpURLConnection.getInputStream()));
            }
            String line = null;
            while((line = reader.readLine()) != null) {
                responseBuffer.append(line);
            }
            apiResponse.setCode(statusCode);
            apiResponse.setMessage(responseBuffer.toString());

        } catch(MalformedURLException e) {
            //e.printStackTrace();
            Log.v("log_tag", "Exception: " + e.toString());
        } catch(IOException e) {
            //e.printStackTrace();
            Log.v("log_tag", "Exception: " + e.toString());
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                    httpURLConnection.disconnect();
                } catch(IOException e) {
                    // Do nothing
                    //e.printStackTrace();
                }
            }
        }
        return apiResponse;
    }

    public static ApiResponse callPostApi(String requestString, String requestURL) {
        StringBuffer responseBuffer = new StringBuffer();
        ApiResponse apiResponse = new ApiResponse();
        int statusCode = 0;

        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {

                URL url = new URL(requestURL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                    // is output buffer writter
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
                writer.write(requestString);
// json data
                writer.close();
//input stream

                statusCode = httpURLConnection.getResponseCode();
                Log.v("log_tag", "Status Code " + statusCode);

                if(statusCode != HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(
                            httpURLConnection.getErrorStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(
                            httpURLConnection.getInputStream()));
                }

                String line = null;
                while((line = reader.readLine()) != null) {
                    responseBuffer.append(line);
                }
                apiResponse.setCode(statusCode);
                apiResponse.setMessage(responseBuffer.toString());

        } catch(MalformedURLException e) {
            //e.printStackTrace();
            Log.v("log_tag", "Exception: " + e.toString());
        } catch(IOException e) {
            //e.printStackTrace();
            Log.v("log_tag", "Exception: " + e.toString());
        } finally {
            if(reader != null) {
                try {
                    writer.close();
                    reader.close();
                    httpURLConnection.disconnect();
                } catch(IOException e) {
                    // Do nothing
                    //e.printStackTrace();
                }
            }
        }
        return apiResponse;

    }

}
