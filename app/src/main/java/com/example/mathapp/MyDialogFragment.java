package com.example.mathapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setView(dialogView)
                .setTitle(MainActivity.dialogText)
                .setPositiveButton("OK", (dialog, id) -> {
                    
                    Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        
        return builder.create();
    }
}

