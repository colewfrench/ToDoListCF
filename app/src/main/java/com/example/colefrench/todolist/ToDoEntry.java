package com.example.colefrench.todolist;

/**
 * Created by Cole French on 2/15/2017.
 */

public abstract class ToDoEntry {

    private String entryHeader;

    public ToDoEntry(String initEntryHeader)
    {
        this.entryHeader = initEntryHeader;
    }

    public String getEntryHeaderText()
    {
        return entryHeader;
    }

    public void setEntryHeaderText(String newEntryHeader)
    {
        this.entryHeader = newEntryHeader;
    }
}
