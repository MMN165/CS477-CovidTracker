package com.example.cs477_covidtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs477_covidtracker.ui.dashboard.SearchFragment;
import com.example.cs477_covidtracker.ui.home.HomeFragment;
import com.example.cs477_covidtracker.ui.home.jsonParser;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LocationDetailsSearchActivity extends AppCompatActivity {

    TextView Cases, Deaths, deltaCases, deltaDeaths, loc;
    String locStr;
    ArrayList<int[]> history;
    LineChart mChart;
    String county, state;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details_search);
        Intent intent = getIntent();

        //ArrayList<int[]> history =
        String test = intent.getStringExtra(SearchFragment.LOCATIONCUR);
        county = intent.getStringExtra(SearchFragment.COUNTY);
        state = intent.getStringExtra(SearchFragment.STATE);
        Cases = findViewById(R.id.cCasesInfo2);
        Deaths = findViewById(R.id.cDeathsInfo2);
        deltaCases = findViewById(R.id.cCasesDelta2);
        deltaDeaths = findViewById(R.id.cDeathsDelta2);
        loc = findViewById(R.id.location_name2);
        locStr = "" + county + ", " + state;
        loc.setText(locStr);

        save = findViewById(R.id.save);

        mChart = findViewById(R.id.caseDetails2);

        mChart.setTouchEnabled(false);
        mChart.setPinchZoom(false);

        new fetch().execute();
    }

    public void save(View v){
        SharedPreferences pref = getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Set<String> set = pref.getStringSet("favorites", null);
        if(set == null){
            set = new HashSet<>();
        }
        set.add(locStr);
        editor.clear();
        editor.putStringSet("favorites", set);
        editor.commit();

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    private class fetch extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(30, TimeUnit.SECONDS);
                client.setReadTimeout(30, TimeUnit.SECONDS);
                Request request = new Request.Builder()
                        .url("https://covidti.com/api/public/us/timeseries/" + state + "/" + county)
                        .method("GET", null)
                        .addHeader("Cookie", "__cfduid=d643853aa641016922decbeeaf960a3121604966690; Cookie_2=value")
                        .build();
                Response response = client.newCall(request).execute();
                String test = response.body().string();
                history = jsonParser.filterTimeSeriesResults(test);
            } catch(Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result){
            if(history != null && history.size() != 0) {
                setupChart();
            }
        }
    }

    private void setupChart(){
        Cases.setText("" + (history.get(history.size() - 1)[0] - history.get(history.size() - 2)[0]));
        Deaths.setText("" + (history.get(history.size() - 1)[1] - history.get(history.size() - 2)[1]));

        int dCasesTotal = history.get(history.size() - 1)[0];
        int dDeathsTotal = history.get(history.size() - 1)[1];
        Cases.setText("" + (history.get(history.size() - 1)[0] - history.get(history.size() - 2)[0]) + "(" +  dCasesTotal + ")");
        Deaths.setText("" + (history.get(history.size() - 1)[1] - history.get(history.size() - 2)[1])+ "(" +  dDeathsTotal + ")");

        int dCases = (history.get(history.size() - 1)[0] - history.get(history.size() - 2)[0]) - (history.get(history.size() - 2)[0] - history.get(history.size() - 3)[0]);
        int dDeaths = (history.get(history.size() - 1)[1] - history.get(history.size() - 2)[1]) - (history.get(history.size() - 2)[1] - history.get(history.size() - 3)[1]);

        if(dCases <= 0){
            deltaCases.setText("" + dCases);
            deltaCases.setTextColor(Color.GREEN);
        }
        else{
            deltaCases.setText("+" + dCases);
            deltaCases.setTextColor(Color.RED);
        }

        if(dDeaths <= 0)
        {
            deltaDeaths.setText("" + dDeaths);
            deltaDeaths.setTextColor(Color.GREEN);
        }
        else{
            deltaDeaths.setText("+" + dDeaths);
            deltaDeaths.setTextColor(Color.RED);
        }


        ArrayList<Entry> values  = new ArrayList<>();
        ArrayList<Entry> deaths = new ArrayList<>();
        int x = 1;
        for(int[] timestamp : history){
            values.add(new Entry(x, timestamp[0]));
            deaths.add(new Entry(x, timestamp[1]));
            x++;
        }

        // values.add(new Entry(1, 50));
        // values.add(new Entry(2, 100));
        LineDataSet set1, set2;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Cases");
            set2 = new LineDataSet(deaths, "Deaths");
            //set1.enableDashedLine(10f, 5f, 0f);
            //set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLUE);
            set2.setColor(Color.RED);
            set1.setCircleColor(Color.BLUE);
            set2.setCircleColor(Color.RED);
            set1.setLineWidth(2f);
            set1.setCircleRadius(1f);
            set2.setLineWidth(2f);
            set2.setCircleRadius(1f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {
                //Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
                //set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLUE);
                set2.setFillColor(Color.RED);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);
            LineData data = new LineData(dataSets);
            mChart.setData(data);
        }
        mChart.invalidate();

    }

}