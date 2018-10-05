package ru.uxapps.af.iab;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import ru.uxapps.af.R;

public class BillingNotSupportedDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.billing_not_supported))
                .setPositiveButton(getString(R.string.ok), null)
                .create();
    }

}
