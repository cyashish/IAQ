package com.example.ashishtiwari.navdraw_iaq;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Schedules a call to hide() in [delay] milliseconds, canceling any
 * previously scheduled calls.
 */
/*  private void delayedHide(int delayMillis) {
    mHideHandler.removeCallbacks(mHideRunnable);
    mHideHandler.postDelayed(mHideRunnable, delayMillis);
}*/

//http://api.thingspeak.com/channels/(channel_id)/feed/last.json
//http://api.thingspeak.com/channels/43649/feeds.json?results =10

public class FetchTSFeedTask extends AsyncTask<String, Void, Vector<ContentValues>> {

    private final String LOG_TAG = FetchTSFeedTask.class.getSimpleName();



    Vector<ContentValues> cVVector;
    private void getAQIfromJson(String tsResultString) throws JSONException {
        JSONObject tsfeedObject = new JSONObject(tsResultString);
        JSONArray tsfeedsArray = tsfeedObject.getJSONArray("feeds");

        cVVector = new Vector<ContentValues>(tsfeedsArray.length());

        for (int i = 0 ; i < tsfeedsArray.length(); i++) {

            String createdAt = null;
            long entryid;
            double fieldVal;

            JSONObject feedobj = tsfeedsArray.getJSONObject(i);
            createdAt = feedobj.getString("created_at");
            entryid = feedobj.getLong("entry_id");
            fieldVal = feedobj.getInt("field1");

            ContentValues cVal = new ContentValues();
            cVal.put("created_at", createdAt);
            cVal.put("entry_id", entryid);
            cVal.put("field1", fieldVal);

            cVVector.add(cVal);
        }
        if (IAQActity.mContentResult != null)
            IAQActity.mContentResult.clear();
        IAQActity.mContentResult = cVVector;
    }


    @Override
    protected Vector<ContentValues> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String tsResultString = null;
        final String APP_RESULTS = "results";
        final String resultsnum = "20";

        try {
            final String BASE_URL = "http://api.thingspeak.com/channels/43649/feeds.json";
            /*Uri builtUri = Uri.parse(BASE_URL).buildUpon().build();*/
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(APP_RESULTS, resultsnum)
                    .build();

            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream stream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (stream == null) return null;
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null)
                buffer.append(line + "\n");

            tsResultString = buffer.toString();
            getAQIfromJson(tsResultString);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

        }
        return cVVector;
    }
}


