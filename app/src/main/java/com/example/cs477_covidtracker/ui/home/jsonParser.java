package com.example.cs477_covidtracker.ui.home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class jsonParser {

    public static ArrayList<int[]> filterTimeSeriesResults(String jsonText){
        try{
            JSONObject result = new JSONObject(jsonText);
            JSONObject data = result.getJSONArray("response").getJSONObject(0).getJSONArray("data").getJSONObject(0);
            String county = data.getString("county");
            JSONObject times = data.getJSONObject("timeseries");
            Iterator<String> timestamps = times.keys();
            ArrayList<int[]> arrayList = new ArrayList<int[]>();
            while(timestamps.hasNext()){
                String time = timestamps.next();
                if (times.get(time) instanceof JSONObject) {
                    JSONObject pair = (JSONObject) times.get(time);
                    arrayList.add(new int[]{pair.getInt("cases"), pair.getInt("deaths")});
                    // do something with jsonObject here
                }

            }
            return arrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
