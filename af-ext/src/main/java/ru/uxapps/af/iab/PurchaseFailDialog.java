package ru.uxapps.af.iab;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;

import ru.uxapps.af.AfViews;
import ru.uxapps.af.R;

public class PurchaseFailDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View root = AfViews.inflate(R.layout.d_purchase_fail, requireContext());
        root.findViewById(R.id.d_purchase_fail_ok_btn).setOnClickListener(v -> dismiss());
        return new AlertDialog.Builder(requireContext()).setView(root).create();
    }

}
