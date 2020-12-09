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

//For unsaved changes in add/edit workouts. Recommiting
public class resetAlertDialog extends DialogFragment {
    public static SharedPreferences sharedPref;
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
        sharedPref = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        builder.setMessage("You are resetting all settings to default and erasing all preferences. Do you want to continue?")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.edit().clear().commit();
                        Toast.makeText(getContext(), "Settings reset", Toast.LENGTH_SHORT).show();
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