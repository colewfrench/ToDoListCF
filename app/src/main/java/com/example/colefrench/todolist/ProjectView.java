package com.example.colefrench.todolist;

import android.content.Context;
import android.widget.CompoundButton;

/**
 * Created by Cole French on 2/12/2017.
 */

public class ProjectView extends ToDoView {

    // TODO ProjectEntry is currently not implemented; does nothing
    public ProjectView(Context context, TaskEntry e, CompoundButton.OnCheckedChangeListener l)
    {
        super(context, e.getEntryHeaderText(), l);
    }

}
