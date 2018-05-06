package com.developer.java.yandex.yandexgallerytask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.developer.java.yandex.yandexgallerytask.model.PhotoViewModel;

public class ErrorFragmentDialog extends DialogFragment {
    private TextView mTitleError;
    private TextView mContentError;
    private PhotoViewModel model;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        model = ViewModelProviders.of(getActivity()).get(PhotoViewModel.class);

        View view = layoutInflater.inflate(R.layout.dialog_error, null);
        mTitleError = view.findViewById(R.id.tv_title_error);
        //mContentError = view.findViewById()
        builder.setView(view);

        builder.setPositiveButton("refresh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.w("ErrorFragmentDialog", model.getText());
                model.updatePhotoResponses();
            }
        });

        return builder.create();
    }
}
