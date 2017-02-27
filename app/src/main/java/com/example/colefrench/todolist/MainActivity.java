package com.example.colefrench.todolist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends Activity implements Constants {

    private static final int MAX_NUM_ENTRIES = 20;
    private static final int DUE_DATE_DEFAULT_VALUE = -1;

    private static final String TASKVIEW_FILE_NAME = "taskviews.txt";

    private TaskEntryManager taskManager;

    private LinearLayout entryListLayout;
    private Button addNewEntryButton;
    private Button saveButton;
    private Button loadButton;
    private TextView currentDateHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // display the current date at the top of the screen
        currentDateHeader = (TextView)findViewById(R.id.currentDateTextView);
        setCurrentDateHeader();

        // get references to the elements on the screen
        entryListLayout = (LinearLayout)findViewById(R.id.entryListLayout);
        addNewEntryButton = (Button)findViewById(R.id.addEntryButton);
        saveButton = (Button)findViewById(R.id.saveButton);
        loadButton = (Button)findViewById(R.id.loadButton);

        // set listeners for the buttons on the screen
        addNewEntryButton.setOnClickListener(new ButtonListener());
        saveButton.setOnClickListener(new ButtonListener());
        loadButton.setOnClickListener(new ButtonListener());

        // instantiate the task manager that stores and manipulates the Task Entries
        taskManager = new TaskEntryManager(this);

        // add sample entry
        Intent intent = new Intent();
        intent.putExtra(NEW_ENTRY_HEADER_TEXT, "Sample Header Text");
        intent.putExtra(NEW_ENTRY_DUE_YEAR, 2017);
        intent.putExtra(NEW_ENTRY_DUE_MONTH, 10);
        intent.putExtra(NEW_ENTRY_DUE_DAY, 13);

        taskManager.addNewTaskEntry(intent);
    }

    @Override
    protected void onPause()
    {
        taskManager.saveEntries();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        taskManager.loadEntriesFromFile();
        setCurrentDateHeader();
        super.onResume();
    }

    /*
        Act on results from activity returns
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case GET_NEW_ENTRY:
            {
                if (resultCode == RESULT_OK)
                {
                    taskManager.addNewTaskEntry(data);
                    updateEntryDisplay();
                }
                if (resultCode == RESULT_CANCELED)
                {
                    // activity backed out/not handled
                }
                break;
            }
        }
    }

    // TODO remove this when base functionality is complete
    public class EntryCheckBoxListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked)
            {
                TaskView finishedView = (TaskView)buttonView.getParent();
                // deleteCheckedEntryInView(finishedView);
                entryListLayout.removeView(finishedView);

                // sortEntries();
            }
        }
    }

    public class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == addNewEntryButton)
            {
                Intent intent = new Intent(getApplicationContext(), NewEntryActivity.class);
                startActivityForResult(intent, GET_NEW_ENTRY);
            }

            if (v == saveButton)
            {
                taskManager.saveEntries();
            }

            if (v == loadButton)
            {
                taskManager.loadEntriesFromFile();
                updateEntryDisplay();
            }
        }
    }

    private void updateEntryDisplay()
    {
        entryListLayout.removeAllViews();

        TaskEntry[] currentEntries = taskManager.getCurrentSortedEntries();

        for (TaskEntry e : currentEntries)
        {
            TaskView newTaskView = new TaskView(this, e, new EntryCheckBoxListener());
            entryListLayout.addView(newTaskView);
        }
    }

    private void setCurrentDateHeader()
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        String month = getMonthFromInt(calendar.get(Calendar.MONTH));
        int year = calendar.get(Calendar.YEAR);

        currentDateHeader.setText(month + " " + day + ", " + year);
    }

    private String getMonthFromInt(int month)
    {
        switch (month)
        {
            case 0: // January
                return getResources().getString(R.string.month_1);
            case 1: // February
                return getResources().getString(R.string.month_2);
            case 2: // March
                return getResources().getString(R.string.month_3);
            case 3: // April
                return getResources().getString(R.string.month_4);
            case 4: // May
                return getResources().getString(R.string.month_5);
            case 5: // June
                return getResources().getString(R.string.month_6);
            case 6: // July
                return getResources().getString(R.string.month_7);
            case 7: // August
                return getResources().getString(R.string.month_8);
            case 8: // September
                return getResources().getString(R.string.month_9);
            case 9: // October
                return getResources().getString(R.string.month_10);
            case 10: // November
                return getResources().getString(R.string.month_11);
            case 11: // December
                return getResources().getString(R.string.month_12);

            default:
                return "ErrorUnknownMonth";
        }
    }
}
