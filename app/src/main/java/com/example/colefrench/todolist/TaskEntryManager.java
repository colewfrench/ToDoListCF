package com.example.colefrench.todolist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Cole French on 2/26/2017.
 */

public class TaskEntryManager implements EntryManager, Constants {

    private static final int MAX_NUM_ENTRIES = 20;
    private static final int DUE_DATE_DEFAULT_VALUE = -1;

    private static final String TASKENTRIES_FILE_NAME = "taskentries.txt";

    private Context context;
    private TaskEntry[] taskEntries;

    public TaskEntryManager(Context context)
    {
        this.context = context;
        taskEntries = new TaskEntry[MAX_NUM_ENTRIES];
    }

    //
    // Adding entries to the array
    // --->

    public int addNewTaskEntry(TaskEntry newEntry)
    {
        int index = getNextAvailableIndex(taskEntries);

        if (index == -1) return OUTPUT_FAILURE;

        taskEntries[index] = newEntry;

        return OUTPUT_SUCCESS;
    }

    public int addNewTaskEntry(Intent data)
    {
        int index = getNextAvailableIndex(taskEntries);
        if (index == -1) return OUTPUT_FAILURE;

        // extra data from the given intent
        String newEntryText = data.getStringExtra(NEW_ENTRY_HEADER_TEXT);
        int year = data.getIntExtra(NEW_ENTRY_DUE_YEAR, DUE_DATE_DEFAULT_VALUE);
        int month = data.getIntExtra(NEW_ENTRY_DUE_MONTH, DUE_DATE_DEFAULT_VALUE);
        int day = data.getIntExtra(NEW_ENTRY_DUE_DAY, DUE_DATE_DEFAULT_VALUE);

        // use data to create new TaskEntry and its view to display the info
        TaskEntry newEntry = new TaskEntry(newEntryText, year, month, day);

        // add new entry to the next null space in the array
        taskEntries[index] = newEntry;

        return OUTPUT_SUCCESS;
    }

    // <---
    // Adding entries to the array
    //

    //
    // Deleting entries from the array
    // --->

    public void deleteCheckedEntries()
    {
        int nullEntryIndex = getNextAvailableIndex(taskEntries);
        if (nullEntryIndex == 0) return;
        if (nullEntryIndex == -1) nullEntryIndex = MAX_NUM_ENTRIES;

        for (int i = 0; i < nullEntryIndex; i++)
        {
            if (taskEntries[i].isChecked())
            {
                taskEntries[i] = null;
            }
        }

        compactEntries(nullEntryIndex);
    }

    private void compactEntries(int lastNullEntryIndex)
    {
        for (int i = 0; i < lastNullEntryIndex; i++)
        {
            if (taskEntries[i] == null)
            {
                for (int j = i + 1; j < lastNullEntryIndex; j++)
                {
                    if (taskEntries[j] != null)
                    {
                        taskEntries[i] = new TaskEntry(taskEntries[j]);
                        taskEntries[j] = null;
                        break;
                    }
                }
            }
        }
    }

    // <---
    // Deleting entries from the array
    //

    //
    // Sorting the entries by closest due date
    // --->

    public TaskEntry[] getCurrentSortedEntries()
    {
        TaskEntry[] currentEntries = getCurrentEntries(taskEntries);

        if (currentEntries == null) return null;

        // only currentEntries is properly ordered, the master copy taskEntries
        // is still unordered
        selectionSortEntries(currentEntries);

        // copy the properly ordered entries back to the master
        copyArrayData(currentEntries, taskEntries);

        return currentEntries;
    }

    // TODO there's a way to make this modular for ProjectEntries as well
    // using the abstract ToDoEntry class and overriding getCombinedDueDate()
    // for each class
    private void selectionSortEntries(TaskEntry[] filledEntries)
    {
        int minDate, minDateIndex;

        for (int i = 0; i < filledEntries.length; i++)
        {
            minDate = filledEntries[i].getCombinedDueDate();
            minDateIndex = i;

            for (int j = i; j < filledEntries.length; j++)
            {
                if (filledEntries[j].getCombinedDueDate() < minDate)
                {
                    minDate = filledEntries[j].getCombinedDueDate();
                    minDateIndex = j;
                }
            }

            if (minDateIndex != i)
            {
                swapArrayElements(filledEntries, i, minDateIndex);
            }
        }
    }

    /**
     * switch the elements in the given TaskEntry[] at index1 and index2
     *
     * @param arr
     * @param index1
     * @param index2
     */
    private void swapArrayElements(TaskEntry[] arr, int index1, int index2)
    {
        TaskEntry tempEntry = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = tempEntry;
    }

    // <---
    // Sorting the entries by closest due date
    //

    //
    // Saving the entries to device's internal storage
    // --->

    // TODO saveEntries is not properly writing to the file
    // TODO the sorting method needs cleaning up
    @Override
    public void saveEntries() {
        File file = new File(context.getFilesDir(), TASKENTRIES_FILE_NAME);
        TaskEntry[] currentEntries = getCurrentSortedEntries();

        writeEntriesToOutputStream(TASKENTRIES_FILE_NAME, currentEntries);

        printEntryDates();
    }

    private void writeEntriesToOutputStream(String filename, ToDoEntry[] entries)
    {
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);

            if (entries == null)
            {
                outputStream.write("".getBytes());
            }
            else
            {
                for (ToDoEntry entry : entries) {
                    outputStream.write((entry.toString() + "\n").getBytes());
                }
            }

            outputStream.close();
        } catch (Exception e)
        {
            // could not write file
        }
    }

    private void printDirFiles()
    {
        String[] list = context.getFilesDir().list();
        for (String s : list)
        {
            Log.d("File/Dir", s);
        }
    }

    // <---
    // Saving the entries to device's internal storage
    //

    //
    // Loading entries from the device's internal storage
    // --->

    @Override
    public ToDoEntry[] loadEntriesFromFile() {
        try {
            taskEntries = new TaskEntry[MAX_NUM_ENTRIES];

            FileInputStream inputStream = context.openFileInput(TASKENTRIES_FILE_NAME);
            InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);

            String fileEntryLine;

            while ((fileEntryLine = bufferedReader.readLine()) != null)
            {
                TaskEntry entry = convertEntryStringToEntry(fileEntryLine);
                addNewTaskEntry(entry);
            }
        } catch (FileNotFoundException e)
        {

        } catch (UnsupportedEncodingException e)
        {

        } catch (IOException e)
        {

        }

        return taskEntries;
    }

    private TaskEntry convertEntryStringToEntry(String fileLine)
    {
        String[] entryData = fileLine.split("\\|\\*\\|");
        int combinedDueDate = Integer.parseInt(entryData[1]);
        int year = (combinedDueDate / 10000);
        int month = ((combinedDueDate % 10000) / 100);
        int day = (combinedDueDate % 100);
        String headerText = entryData[0];
        TaskEntry savedEntry = new TaskEntry(headerText, year, month, day);

        if (entryData[2].equals("y"))
        {
            // entry should appear checked
            savedEntry.setChecked(true);
        }
        else
        {
            savedEntry.setChecked(false);
        }

        return savedEntry;
    }

    // <---

    //
    // Helper methods used in multiple function blocks
    // --->

    private TaskEntry[] getCurrentEntries(TaskEntry[] original)
    {
        int currentArraySize = getNextAvailableIndex(original);

        if (currentArraySize == 0) return null;
        if (currentArraySize == -1) currentArraySize = MAX_NUM_ENTRIES; // array is full

        TaskEntry[] allCurrentTaskEntries = new TaskEntry[currentArraySize];

        copyArrayData(taskEntries, allCurrentTaskEntries);

        return allCurrentTaskEntries;
    }

    /**
     * Copies the array data from the source TaskEntry array to the destination array,
     * matching index for index. Arrays don't have to be the same length;
     * the source will fill the destination array up to the end of the destination array.
     * ONLY COPIES ADDRESS; DATA WILL BE CHANGED IN BOTH ARRAYS
     *
     * @param src the TaskEntry array to read data from
     * @param dest the TaskEntry array to copy the read data to
     */
    private void copyArrayData(TaskEntry[] src, TaskEntry[] dest)
    {
        if (src.length > dest.length)
        {
            for (int i = 0; i < dest.length; i++) {
                dest[i] = src[i];
            }
        }
        else
        {
            for (int i = 0; i < src.length; i++) {
                dest[i] = src[i];
            }
        }
    }

    /**
     * returns the index of the next null index in the array,
     * or -1 if the array is full
     *
     * @param myEntries the array to search for the next open spot
     * @return the index of the next available spot in myEntries array
     */
    private int getNextAvailableIndex(ToDoEntry[] myEntries)
    {
        for (int i = 0; i < myEntries.length; i++)
        {
            if (myEntries[i] == null) return i;
        }

        return -1;
    }

    /**
     * Used for debugging
     */
    public void printEntryDates()
    {
        for (int i = 0; i < taskEntries.length; i++)
        {
            if (taskEntries[i] == null)
            {
                Log.d("Index " + i, "null");
            }
            else
            {
                Log.d("Index " + i, taskEntries[i].getCombinedDueDate() + "");
            }
        }

        Log.d("","\n");
    }

    // <---
}
