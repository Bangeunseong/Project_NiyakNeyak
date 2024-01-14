package com.capstone.project_niyakneyak.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.capstone.project_niyakneyak.R;
import com.google.android.material.textfield.TextInputEditText;

public class AddDialogFragment extends DialogFragment {
    public interface OnCompleteListener{
        void onInputedData(String meds_name, String meds_detail, String meds_duration,
                           @Nullable String meds_time_morning, @Nullable String meds_time_afternoon,
                           @Nullable String meds_time_evening, @Nullable String meds_time_latenight);
    }
    private OnCompleteListener mCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCompleteListener) context;
        } catch (ClassCastException e) {
            Log.d("AddDialogFragment", "Activity doesn't implement the OnCompleteListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_add_dialog_form, null);
        builder.setCustomTitle(LayoutInflater.from(getActivity()).inflate(R.layout.item_add_dialog_title,null));
        builder.setView(view);

        final Switch morning_time = (Switch) view.findViewById(R.id.dialog_meds_time_morning);
        final Switch afternoon_time = (Switch) view.findViewById(R.id.dialog_meds_time_afternoon);
        final Switch evening_time = (Switch) view.findViewById(R.id.dialog_meds_time_evening);
        final Switch latenight_time = (Switch) view.findViewById(R.id.dialog_meds_time_latenight);
        final TextView morning_text = (TextView) view.findViewById(R.id.dialog_meds_time_morning_text);
        final TextView afternoon_text = (TextView) view.findViewById(R.id.dialog_meds_time_afternoon_text);
        final TextView evening_text = (TextView) view.findViewById(R.id.dialog_meds_time_evening_text);
        final TextView latenight_text = (TextView) view.findViewById(R.id.dialog_meds_time_latenight_text);

        final TextInputEditText meds_name = (TextInputEditText) view.findViewById(R.id.dialog_meds_name_text);
        final TextInputEditText meds_detail = (TextInputEditText) view.findViewById(R.id.dialog_meds_detail_text);
        final TextInputEditText meds_date = (TextInputEditText) view.findViewById(R.id.dialog_meds_date_text);

        final Button submit = (Button) view.findViewById(R.id.dialog_meds_submit);
        final Button cancel = (Button) view.findViewById(R.id.dialog_meds_cancel);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String morning = morning_time.isChecked() ? morning_text.getText().toString() : null;
                String afternoon = afternoon_time.isChecked() ? afternoon_text.getText().toString() : null;
                String evening = evening_time.isChecked() ? evening_text.getText().toString() : null;
                String latenight = latenight_time.isChecked() ? latenight_text.getText().toString() : null;
                String meds_name_text = meds_name.getText().toString();
                String meds_detail_text = meds_detail.getText().toString();
                String meds_date_text = meds_date.getText().toString();
                dismiss();

                mCallback.onInputedData(meds_name_text,meds_detail_text,meds_date_text,morning,afternoon,evening,latenight);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }
}
