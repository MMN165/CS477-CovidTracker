package com.example.cs477_covidtracker.ui.home;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

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

    public static HashMap<String, ArrayList<String>> filterCounties() throws IOException, JSONException {
        String[] states = {"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};
        String body = "";

        HashMap<String, ArrayList<String>> countyList = new HashMap<>();
        JSONObject countyListObj = new JSONObject();
        for (String state:states){
           // state = state.replaceAll("\\s", "");
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
            for(int x = 0; x < data.length(); x++){
                try{
                    counties.add(data.getJSONObject(x).getString("County"));
                }catch (Exception e){
                    if(state.equals("Alaska")) {
                        counties.add(data.getJSONObject(x).getString("Borough"));
                    }
                    else if (state.equals("Louisiana")){
                        counties.add(data.getJSONObject(x).getString("Parish"));
                    }

                }
            }
            JSONArray countiesJSON = new JSONArray(counties);
            countyListObj.put(state, countiesJSON);
            countyList.put(state, counties);
        }
       // String str = countyListObj.toString();
       // FileWriter file = new FileWriter( "/counties.json",false);
        //file.write(str);
        //file.close();
        return countyList;
    }

    public static ArrayList<String> filterCountiesList() throws IOException, JSONException {
        String[] states = {"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};
        String body = "";

        ArrayList<String> countyList = new ArrayList<>();
        JSONObject countyListObj = new JSONObject();
        for (String state:states){
            // state = state.replaceAll("\\s", "");
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

            for(int x = 0; x < data.length(); x++){
                try{
                    countyList.add("" + data.getJSONObject(x).getString("County") + ", " + state);
                }catch (Exception e){
                    if(state.equals("Alaska")) {
                        countyList.add("" + data.getJSONObject(x).getString("Borough")  + ", " + state);
                    }
                    else if (state.equals("Louisiana")){
                        countyList.add("" + data.getJSONObject(x).getString("Parish") + ", " + state);
                    }

                }
            }
        }
        // String str = countyListObj.toString();
        // FileWriter file = new FileWriter( "/counties.json",false);
        //file.write(str);
        //file.close();
        return countyList;
    }


    public static void main(String[] args) throws IOException, JSONException {
        String[] states = {"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};
        String body = "";

        HashMap<String, ArrayList<String>> countyList = new HashMap<>();
        JSONObject countyListObj = new JSONObject();
        for (String state:states){
            // state = state.replaceAll("\\s", "");
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
            for(int x = 0; x < data.length(); x++){
                try{
                    counties.add(data.getJSONObject(x).getString("County"));
                }catch (Exception e){
                    if(state.equals("Alaska")) {
                        counties.add(data.getJSONObject(x).getString("Borough"));
                    }
                    else if (state.equals("Louisiana")){
                        counties.add(data.getJSONObject(x).getString("Parish"));
                    }

                }
            }
            JSONArray countiesJSON = new JSONArray(counties);
            countyListObj.put(state, countiesJSON);
            countyList.put(state, counties);
        }




      //  String str = countyListObj.toString();
        FileWriter file = new FileWriter( "/counties.json",false);
       // file.write(str);
       // file.close();
    }

}
