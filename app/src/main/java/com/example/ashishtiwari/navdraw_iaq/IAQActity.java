package com.example.ashishtiwari.navdraw_iaq;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IAQActity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final String LOG_TAG = IAQActity.class.getSimpleName();
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };



    public static Vector<ContentValues> mContentResult;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        try {
            FetchTSFeedTask fetchTSFeedTask = new FetchTSFeedTask();
            fetchTSFeedTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (mContentResult == null){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.e(LOG_TAG, "back from query in background");

        setContentView(R.layout.graphs);
        BarChart graph = (BarChart) findViewById(R.id.graph1);

        //mpchart data  xaxis labels
        ArrayList<String> labels = new ArrayList<String>();
       /* labels.add("1");
        labels.add("2y");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("Jul");
        labels.add("agu");
        labels.add("Sep");
        labels.add("Oce");*/

        //entries for data
        ArrayList<BarEntry> entries = new ArrayList<>();
        int resultSize = mContentResult.size();
        for (int i = 0; i < resultSize; i++) {
            ContentValues cvalue = mContentResult.elementAt(i);

            long x = cvalue.getAsLong("entry_id");
            String createat = cvalue.getAsString("created_at");
            String fCreate = createat.substring(11, 16);

            labels.add(fCreate);

            double y = cvalue.getAsDouble("field1");
            entries.add(new BarEntry((float)y, i));
        }
        BarDataSet dataSet = new BarDataSet(entries, "aqi");
        BarData data = new BarData(labels, dataSet);

        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        graph.setBackgroundColor(Color.WHITE);
        graph.setDescription("AQI per day");
        graph.setDescriptionTextSize(12);


        graph.setData(data);


        Log.e(LOG_TAG, "m content results " + mContentResult.size()  );

    }

   /* @Override
    public void onCreateGraphLib(Bundle savedInstance) {
        super.onCreate(savedInstance);

        try {
            FetchTSFeedTask fetchTSFeedTask = new FetchTSFeedTask();
            fetchTSFeedTask.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.graphs);

        GraphView graph = (GraphView) findViewById(R.id.graph1);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        Log.e(LOG_TAG, "back from query in background");

        while (mContentResult == null){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 9; i++) {
            ContentValues cvalue = mContentResult.elementAt(i);

            long x = cvalue.getAsLong("entry_id");
            double y = cvalue.getAsDouble("field1");

            DataPoint point = new DataPoint(x, y);
            series.appendData(point, true, 100);
        }
        Log.e(LOG_TAG, "m content results " + mContentResult.size()  );
       *//* LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 117),
                new DataPoint(1, 57)
                new DataPoint(10, 157)*//**//*
        });*//*

        graph.addSeries(series);
        series.setColor(Color.YELLOW);
        graph.setTitle("IAQ vs day");
        graph.setTitleColor(Color.WHITE);
        graph.getViewport().setMaxX(20);
        graph.getViewport().setMaxY(450);
        graph.getViewport().setMinY(50);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("AQI");
        graph.getGridLabelRenderer().setVerticalLabelsVisible(true);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graph.getLegendRenderer().setVisible(true);
        graph.getViewport().setXAxisBoundsStatus(Viewport.AxisBoundsStatus.AUTO_ADJUSTED);


    }*/
}






