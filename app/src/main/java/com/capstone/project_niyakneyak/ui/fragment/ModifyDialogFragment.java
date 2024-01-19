package com.capstone.project_niyakneyak.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import com.capstone.project_niyakneyak.data.model.MedsData;
import com.capstone.project_niyakneyak.data.model.TimeData;
import com.capstone.project_niyakneyak.ui.main.HorizontalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.MedsTimeAdapter;
import com.capstone.project_niyakneyak.ui.main.VerticalItemDecorator;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ModifyDialogFragment extends DialogFragment {
    private OnChangedDataListener changed_communicator;
    private MedsTimeAdapter adapter;
    private List<TimeData> timeData;

    //Time Modification Interface -> Does time modification and deletion
    private OnChangedTimeListener changed = new OnChangedTimeListener() {
        @Override
        public void onChangedTime(String time, int position) {
            timeData.get(position).setTime(time);
            adapter.changeItem(position);
        }
    };
    private OnDeleteTimeListener deleted = new OnDeleteTimeListener() {
        @Override
        public void onDeleteTime(int position) {
            timeData.remove(position);
            adapter.removeItem(position);
        }
    };

    //Constructor
    public ModifyDialogFragment(OnChangedDataListener changed_communicator){
        this.changed_communicator = changed_communicator;
    }

    //Main Dialog Creation
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_dialog_form, null);
        builder.setCustomTitle(LayoutInflater.from(getActivity()).inflate(R.layout.item_modify_dialog_title,null));
        builder.setView(view);

        //XML Layout ID Fields
        final RecyclerView rcv_time = view.findViewById(R.id.dialog_meds_time_rcv);

        final TextInputEditText meds_name = view.findViewById(R.id.dialog_meds_name_text);
        final TextInputEditText meds_detail = view.findViewById(R.id.dialog_meds_detail_text);
        final TextInputEditText meds_date = view.findViewById(R.id.dialog_meds_date_text);

        final Button add_time_btn = view.findViewById(R.id.dialog_meds_time_add_btn);
        final Button submit = view.findViewById(R.id.dialog_meds_submit);
        final Button cancel = view.findViewById(R.id.dialog_meds_cancel);

        //Modify layout field data with bundle data
        //Get Arguments from previous page(RecyclerAdapter)
        Bundle bundle = getArguments();
        MedsData data = (MedsData) bundle.get("BeforeModify");
        meds_name.setText(data.getMeds_name());
        meds_detail.setText(data.getMeds_detail());
        meds_date.setText(data.getMeds_date());
        timeData = data.getMeds_time();

        //Setting recycler adapter(TimeAdapter)
        adapter = new MedsTimeAdapter(getActivity(), timeData, changed, deleted);
        rcv_time.setHasFixedSize(false);
        rcv_time.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcv_time.setAdapter(adapter);
        rcv_time.addItemDecoration(new VerticalItemDecorator(20));
        rcv_time.addItemDecoration(new HorizontalItemDecorator(10));

        //When you click Date setting field, it will show DatePickerDialog
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

        //When you click time addition btn, it will show TimePickerDialog
        //Then, modified data will be shown by Time recycler view(Using TimeAdapter)
        add_time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog timeDlg = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeData.add(new TimeData(String.format("%02d:%02d",hourOfDay,minute), true));
                        timeData.stream().forEach(data->{Log.d("TimePickerDialog", data.getTime() + ":" + data.getState());});
                        adapter.addItem(timeData.size() - 1);
                    }
                }, 0, 0, true);
                timeDlg.show();
            }
        });

        //When submit btn clicked, all data will be transferred to MainActivity and will be saved
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String meds_name_text = meds_name.getText().toString();
                String meds_detail_text = meds_detail.getText().toString();
                String meds_date_text = meds_date.getText().toString();
                dismiss();
                changed_communicator.onChangedData(data, new MedsData(meds_date_text.hashCode(), meds_name_text,
                        meds_detail_text, meds_date_text, timeData));
            }
        });

        //Closes dialog without data transfer(No data modification)
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {dismiss();}
        });

        return builder.create();
    }
}
