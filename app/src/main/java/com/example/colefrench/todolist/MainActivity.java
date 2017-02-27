package com.example.colefrench.todolist;

import android.app.Activity;
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

    private TaskEntryManager taskManager;

    private LinearLayout entryListLayout;
    private Button addNewEntryButton;
    private Button clearCheckedEntriesButton;

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
        clearCheckedEntriesButton = (Button)findViewById(R.id.clearCheckedButton);

        // set listeners for the buttons on the screen
        addNewEntryButton.setOnClickListener(new ButtonListener());
        clearCheckedEntriesButton.setOnClickListener(new ButtonListener());

        // instantiate the task manager that stores and manipulates the Task Entries
        taskManager = new TaskEntryManager(this);

        Log.d("created","done");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        taskManager.saveEntries();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        taskManager.loadEntriesFromFile();
        setCurrentDateHeader();
        updateEntryDisplay();
        Log.d("onREsume","here");
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
                    Log.d("activityResult","returned");
                    //updateEntryDisplay();
                }
                if (resultCode == RESULT_CANCELED)
                {
                    // activity backed out/not handled
                }
                break;
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

            if (v == clearCheckedEntriesButton)
            {
                taskManager.deleteCheckedEntries();
                updateEntryDisplay();
            }
        }
    }

    public class CheckBoxListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            TaskView checkedView = (TaskView)buttonView.getParent();
            TaskEntry checkedEntry = checkedView.getTaskEntry();
            checkedEntry.setChecked(isChecked);
        }
    }

    private void updateEntryDisplay()
    {
        entryListLayout.removeAllViews();

        TaskEntry[] currentEntries = taskManager.getCurrentSortedEntries();

        if (currentEntries != null)
        {
            for (TaskEntry e : currentEntries) {
                TaskView newTaskView = new TaskView(this, e, new CheckBoxListener());
                newTaskView.setChecked(e.isChecked());
                entryListLayout.addView(newTaskView);
            }
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
