package com.capstone.project_niyakneyak.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.model.TimeData;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ModifyDialogFragment extends DialogFragment {
    public interface OnCompleteListener{
        void onModifiedData(String meds_name, String meds_detail, String meds_duration,
                           List<TimeData> meds_time) throws ParseException;
    }
    private ModifyDialogFragment.OnCompleteListener mCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mCallback = (ModifyDialogFragment.OnCompleteListener) context;
        } catch (ClassCastException e) {
            Log.d("ModifyDialogFragment", "Activity doesn't implement the OnCompleteListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_dialog_form, null);
        builder.setCustomTitle(LayoutInflater.from(getActivity()).inflate(R.layout.item_modify_dialog_title,null));
        builder.setView(view);

        final TextInputEditText meds_name = view.findViewById(R.id.dialog_meds_name_text);
        final TextInputEditText meds_detail = view.findViewById(R.id.dialog_meds_detail_text);
        final TextInputEditText meds_date = view.findViewById(R.id.dialog_meds_date_text);

        final Button submit = view.findViewById(R.id.dialog_meds_submit);
        final Button cancel = view.findViewById(R.id.dialog_meds_cancel);

        meds_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                dateFormat.setTimeZone(tz);
                Date today = new Date();

                Calendar calendar = Calendar.getInstance();
                if(!meds_date.getText().toString().isEmpty()){
                    DateFormat dateFormat_select = new SimpleDateFormat("yyyy/MM/dd");
                    try {
                        calendar.setTime(dateFormat_select.parse(meds_date.getText().toString()));
                    } catch (ParseException e) {
                        Log.d("DatePicker","Parsing Failed!");
                    }
                }
                else calendar.setTime(today);

                final DatePickerDialog dateDlg = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        meds_date.setText(String.format("%d/%d/%d",year,month + 1,dayOfMonth));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dateDlg.getDatePicker().setMinDate(today.getTime());
                dateDlg.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String meds_name_text = meds_name.getText().toString();
                String meds_detail_text = meds_detail.getText().toString();
                String meds_date_text = meds_date.getText().toString();
                List<TimeData> meds_time = Arrays.asList();
                dismiss();

                try {
                    mCallback.onModifiedData(meds_name_text,meds_detail_text,meds_date_text, meds_time);
                } catch (ParseException e) {
                    Log.d("ModifyDialog","Parsing Failed!");
                }
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
