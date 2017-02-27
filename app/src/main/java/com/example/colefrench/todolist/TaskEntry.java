package com.example.colefrench.todolist;

/**
 * Created by Cole French on 2/15/2017.
 */

public class TaskEntry extends ToDoEntry {

    private static final String DATA_PARTITION = "#$%";

    private int dueYear, dueMonth, dueDay;
    private int combinedDueDate; // used for sorting due dates

    public TaskEntry(String initEntryHeader, int initDueYear, int initDueMonth, int initDueDay)
    {
        super(initEntryHeader);

        this.dueYear = initDueYear;
        this.dueMonth = initDueMonth;
        this.dueDay = initDueDay;

        combinedDueDate = setCombinedDueDate();
    }

    // combined due date is ordered YYYYMMDD
    private int setCombinedDueDate()
    {
        int tempYear = dueYear;
        int tempMonth = dueMonth;

        // if either the month or day is a single digit, add an extra digit to the
        // date component preceding it to account for the offset
        if (isSingleDigit(tempMonth)) tempYear = tempYear * 10;
        if (isSingleDigit(dueDay)) tempMonth = tempMonth * 10;

        String s = tempYear + "" + tempMonth + "" + dueDay;

        return (Integer.parseInt(s));
    }

    /**
     * returns the entry text and due date in combined format as a string.
     * note that the last 8 characters will always be the due date, and
     * the string will always be at least 8 characters long.
     *
     * @return a string consisting of the entry text and the combined due date in that order
     */
    public String toString()
    {
        return (this.getEntryHeaderText() + combinedDueDate);
    }

    private boolean isSingleDigit(int n)
    {
        return (n < 10 && n > -10);
    }

    public int getCombinedDueDate()
    {
        return combinedDueDate;
    }

    public int getDueYear()
    {
        return dueYear;
    }

    public int getDueMonth()
    {
        return dueMonth;
    }

    public int getDueDay()
    {
        return dueDay;
    }
}
