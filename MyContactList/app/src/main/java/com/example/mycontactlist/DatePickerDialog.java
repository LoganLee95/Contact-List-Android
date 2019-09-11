package com.example.mycontactlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerDialog extends DialogFragment {


        public interface SaveDateListener {
            void didFinishDatePickerDialog(Calendar selectedTime);
        }

        public DatePickerDialog() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.select_date, container);

            getDialog().setTitle("Select Date");

            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.birthdayPicker);

            Button saveButton = (Button) view.findViewById(R.id.buttonSelect);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Calendar selectedTime = Calendar.getInstance();
                    selectedTime.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                    saveItem(selectedTime);
                }
            });
            Button cancelButton = (Button) view.findViewById(R.id.buttonCancel);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
            return view;
        }

        private void saveItem(Calendar selectedTime) {
            SaveDateListener activity = (SaveDateListener) getActivity();

                if (activity != null) {
                    activity.didFinishDatePickerDialog(selectedTime);
                }
                getDialog().dismiss();
            }

    }

