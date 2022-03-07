package com.example.planetx.configviews;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.planetx.R;

/**
 * This class displays the dialog fragment (wifi connection could not be established)
 * It is currently not used.
 */

public class ConfigQrScanDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.qr_scan_msg);
        builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User dismissed the dialog
                NavController navController = NavHostFragment.findNavController(ConfigQrScanDialogFragment.this);
                //navigate here
                navController.navigate(R.id.action_configQrScanDialogFragment_to_configQrScanFragment);
            }
        });

        // Return the AlertDialog object
        return builder.create();
    }
}
