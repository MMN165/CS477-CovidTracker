package com.example.cs477_covidtracker.ui.notifications;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cs477_covidtracker.R;
import com.example.cs477_covidtracker.resetAlertDialog;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;


    ImageButton darkMode, reset, changeLocation;
    private boolean isdarkMode = false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        /*final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
       darkMode = root.findViewById(R.id.darkmode);

        darkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isdarkMode) {
                    isdarkMode = true;
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_YES);
                }
                else{
                    isdarkMode = false;
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_NO);
                }
            }
        });

        reset = root.findViewById(R.id.reset);

        reset.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                resetAlertDialog dialog = resetAlertDialog.newInstance("Reset");
                dialog.show(fm, "Reset");
            }
        });
        return root;
    }

    public void resetAlert(View root){

    }
    //public void toggleDarkMode(View v){

    //}
}