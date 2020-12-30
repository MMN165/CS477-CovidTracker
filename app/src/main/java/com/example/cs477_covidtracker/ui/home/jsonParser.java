package com.example.cs477_covidtracker.ui.home;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Helper class. Parses the API responses into JSON and retrieves information based off of it.
 */
public class jsonParser {

    /**
     * Given an API result, get the history of cases as an Arraylist
     */
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
                }

            }
            return arrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Uses the API to get the details of counties in each state. Placed in a hashmap of state, Arraylist<Counties>
     */
    public static HashMap<String, ArrayList<String>> filterCounties() throws IOException, JSONException {
        String[] states = {"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};
        String body = "";

        HashMap<String, ArrayList<String>> countyList = new HashMap<>();
        for (String state:states){
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(30, TimeUnit.SECONDS);
            client.setReadTimeout(30, TimeUnit.SECONDS);
            Request request = new Request.Builder()
                    .url("https://covidti.com/api/public/us/wiki/" + state)
                    .method("GET", null)
                    .addHeader("Cookie", "__cfduid=d3c9ca1ca688f6a06153112fec4c010921607398642")
                    .build();
            Response response = client.newCall(request).execute();
            body = response.body().string();

            JSONObject result = new JSONObject(body);
            Log.e("State", state);
            JSONArray data = result.getJSONObject("response").getJSONArray("countyArray");

            ArrayList<String> counties = new ArrayList<>();
            String countyUnfiltered;
            for(int x = 0; x < data.length(); x++){
                try{
                    countyUnfiltered = data.getJSONObject(x).getString("County");
                    countyUnfiltered = countyUnfiltered.replaceAll("\\d+(?:[.,]\\d+)*\\s*", "").replaceAll ("\"[\\\\p{P}&&[^\\u0027]]\"", "")
                    .replaceAll("[\\[\\](){}]","").replaceAll("\\*", "");
                    if(!counties.contains(countyUnfiltered)) {
                        counties.add(countyUnfiltered);
                    }
                }catch (Exception e){
                    if(state.equals("Alaska")) {
                        countyUnfiltered = data.getJSONObject(x).getString("Borough").replaceAll("\\s*\\bBorough\\b\\s*", "");
                        counties.add(countyUnfiltered);
                    }
                    else if (state.equals("Louisiana")){
                        counties.add(data.getJSONObject(x).getString("Parish"));
                    }

                }
            }
            countyList.put(state, counties);
        }
        return countyList;
    }
}
