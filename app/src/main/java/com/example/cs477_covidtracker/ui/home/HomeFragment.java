package com.example.cs477_covidtracker.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cs477_covidtracker.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private class fetch extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://covidti.com/api/public/us/timeseries/Virginia/Fairfax")
                    .method("GET", null)
                    .addHeader("Cookie", "__cfduid=d643853aa641016922decbeeaf960a3121604966690")
                    .build();
            Response response;
            try {
                response = client.newCall(request).execute();
                String test = response.body().string();
                return null;
            }catch (Exception e){
            }
            return null;
        }
    }

    ListView favorites;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        CardView textView = root.findViewById(R.id.local_dest);
      /*  homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
*/

        favorites = root.findViewById(R.id.favorites_list);
        ArrayAdapter lister = new ArrayAdapter<String>(root.getContext(), R.layout.card_info_layout);
        favorites.setAdapter(lister);

        favorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        return root;
    }
}