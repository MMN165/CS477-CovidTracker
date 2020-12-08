package com.example.cs477_covidtracker.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cs477_covidtracker.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LocationDetailsActivity extends AppCompatActivity {

    TextView Cases, Deaths, deltaCases, deltaDeaths, loc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        Intent intent = getIntent();

        ArrayList<int[]> history = (ArrayList<int[]>) intent.getSerializableExtra(HomeFragment.CASESPASS);
        String test = intent.getStringExtra(HomeFragment.LOCATIONCUR);

        Cases = findViewById(R.id.cCasesInfo);
        Deaths = findViewById(R.id.cDeathsInfo);
        deltaCases = findViewById(R.id.cCasesDelta);
        deltaDeaths = findViewById(R.id.cDeathsDelta);
        loc = findViewById(R.id.location_name);

        loc.setText(test);
        Cases.setText("" + (history.get(history.size() - 1)[0] - history.get(history.size() - 2)[0]));
        Deaths.setText("" + (history.get(history.size() - 1)[1] - history.get(history.size() - 2)[1]));

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
        LineChart mChart = findViewById(R.id.caseDetails);

        mChart.setTouchEnabled(false);
        mChart.setPinchZoom(false);


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
    }


}