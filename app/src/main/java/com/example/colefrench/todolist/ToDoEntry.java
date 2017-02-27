package com.example.colefrench.todolist;

/**
 * Created by Cole French on 2/15/2017.
 */

public abstract class ToDoEntry {

    protected static final String IS_CHECKED_STRING = "y";
    protected static final String IS_NOT_CHECKED_STRING = "n";

    private String entryHeader;
    private boolean isChecked;

    public ToDoEntry(String initEntryHeader)
    {
        this.entryHeader = initEntryHeader;
        this.isChecked = false;
    }

    public String getEntryHeaderText()
    {
        return entryHeader;
    }

    public void setEntryHeaderText(String newEntryHeader)
    {
        this.entryHeader = newEntryHeader;
    }

    public boolean isChecked()
    {
        return isChecked;
    }

    public void setChecked(boolean checked)
    {
        this.isChecked = checked;
    }
}
