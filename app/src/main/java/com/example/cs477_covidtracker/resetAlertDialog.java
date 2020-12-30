package com.example.cs477_covidtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.HashSet;
import java.util.Set;

public class resetAlertDialog extends DialogFragment {
    public static resetAlertDialog newInstance(String title){
        DialogFragment dialog = new resetAlertDialog();
        Bundle args = new Bundle();
        args.putString("title", "Unsaved Changes");
        dialog.setArguments(args);
        return (resetAlertDialog) dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You are clearing all of your favorites. Do you want to continue?")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref =  getActivity().getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        Set<String> set = new HashSet<String>();
                        editor.clear();
                        editor.putStringSet("favorites", set);
                        editor.commit();
                        //sharedPref.edit().clear().commit();
                        Toast.makeText(getContext(), "Favorites reset", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        return builder.create();
    }
}