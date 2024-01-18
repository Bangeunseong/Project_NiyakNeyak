package com.capstone.project_niyakneyak.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.model.TimeData;
import com.capstone.project_niyakneyak.ui.main.HorizontalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.MedsTimeAdapter;
import com.capstone.project_niyakneyak.ui.main.VerticalItemDecorator;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AddDialogFragment extends DialogFragment {
    private OnCompleteListener mCallback;
    private MedsTimeAdapter adapter;
    private List<TimeData> meds_time;

    public interface OnCompleteListener{
        void onInputedData(String meds_name, String meds_detail, String meds_duration,
                           List<TimeData> meds_time) throws ParseException;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        meds_time = new ArrayList<>();
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_dialog_form, null);
        builder.setCustomTitle(LayoutInflater.from(getActivity()).inflate(R.layout.item_add_dialog_title,null));
        builder.setView(view);


        final RecyclerView rcv_time = view.findViewById(R.id.dialog_meds_time_rcv);

        final TextInputEditText meds_name = view.findViewById(R.id.dialog_meds_name_text);
        final TextInputEditText meds_detail = view.findViewById(R.id.dialog_meds_detail_text);
        final TextInputEditText meds_date = view.findViewById(R.id.dialog_meds_date_text);

        final Button add_time_btn = view.findViewById(R.id.dialog_meds_time_add_btn);
        final Button submit = view.findViewById(R.id.dialog_meds_submit);
        final Button cancel = view.findViewById(R.id.dialog_meds_cancel);

        adapter = new MedsTimeAdapter(meds_time);
        rcv_time.setHasFixedSize(false);
        rcv_time.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_time.setAdapter(adapter);
        rcv_time.addItemDecoration(new VerticalItemDecorator(20));
        rcv_time.addItemDecoration(new HorizontalItemDecorator(10));

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

        add_time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog timeDlg = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        meds_time.add(new TimeData(String.format("%02d:%02d",hourOfDay,minute), true));
                        meds_time.stream().forEach(data->{Log.d("TimePickerDialog", data.getTime() + ":" + data.getState());});
                        adapter.setItems(meds_time);
                    }
                }, 0, 0, true);
                timeDlg.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String meds_name_text = meds_name.getText().toString();
                String meds_detail_text = meds_detail.getText().toString();
                String meds_date_text = meds_date.getText().toString();
                dismiss();

                try {
                    mCallback.onInputedData(meds_name_text,meds_detail_text,meds_date_text, meds_time);
                } catch (ParseException e) {
                    Log.d("AddDialog","Parsing Failed!");
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
