package com.example.colefrench.todolist;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Cole French on 2/11/2017.
 */

public class TaskView extends ToDoView {

    private static final int DUE_DATE_RIGHT_MARGIN = 25; // 15px

    private Context context;

    private TaskEntry thisViewTaskEntry;

    private TextView dueDateTextView;

    public TaskView(Context context, TaskEntry newEntry,
                    CompoundButton.OnCheckedChangeListener listener)
    {
        super(context, newEntry.getEntryHeaderText(), listener);

        this.context = context;

        this.thisViewTaskEntry = newEntry;

        initTaskEntry(newEntry);
    }

    private void initTaskEntry(TaskEntry newEntry)
    {
        this.dueDateTextView = new TextView(this.context);
        LinearLayout.LayoutParams dueDateTextViewParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dueDateTextViewParams.setMargins(0, 0, DUE_DATE_RIGHT_MARGIN, 0);
        dueDateTextView.setLayoutParams(dueDateTextViewParams);

        dueDateTextView.setTextSize(getResources().getDimension(R.dimen.text_view_text_size));
        dueDateTextView.setTextColor(super.ENTRY_TEXT_COLOR);
        dueDateTextView.setGravity(Gravity.CENTER_VERTICAL);
        dueDateTextView.setText(newEntry.getDueMonth() + "/" + newEntry.getDueDay() + "/" + newEntry.getDueYear());

        this.addView(dueDateTextView);
    }

    // TODO maybe don't need the below methods in later iterations of app
    public void setDueDay(int newDueDay)
    {

    }

    public void setDueMonth(int newDueMonth)
    {

    }

    public void setDueYear(int newDueYear)
    {

    }

    public TaskEntry getTaskEntry()
    {
        return thisViewTaskEntry;
    }
}
