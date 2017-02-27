package com.example.colefrench.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class NewEntryActivity extends Activity implements Constants {

    private EditText newEntryEditText;
    private Button confirmButton;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        initializeCalendar();

        confirmButton = (Button)findViewById(R.id.confirmButton);
        newEntryEditText = (EditText)findViewById(R.id.newEntryEditText);

        confirmButton.setOnClickListener(new ButtonListener());
    }

    private void initializeCalendar()
    {
        datePicker = (DatePicker)findViewById(R.id.dueDatePicker);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        datePicker.init(year, month, day, null);
    }

    public class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent resultIntent = new Intent();

            String newEntryText = "" + newEntryEditText.getText();
            int year, month, day;
            year = datePicker.getYear();
            month = datePicker.getMonth() + 1; // getMonth treats january as 0
            day = datePicker.getDayOfMonth();

            resultIntent.putExtra(NEW_ENTRY_HEADER_TEXT, newEntryText);
            resultIntent.putExtra(NEW_ENTRY_DUE_YEAR, year);
            resultIntent.putExtra(NEW_ENTRY_DUE_MONTH, month);
            resultIntent.putExtra(NEW_ENTRY_DUE_DAY, day);
            setResult(RESULT_OK, resultIntent);

            finish();
        }
    }
}
