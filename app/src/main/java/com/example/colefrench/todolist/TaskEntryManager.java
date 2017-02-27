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

    public void deleteCheckedTaskEntry(TaskView deleteView)
    {
        TaskEntry deleteEntry = deleteView.getTaskEntry();

        int deleteIndex = getDeleteIndex(deleteEntry);

        if (deleteIndex == -1) return;

        taskEntries[deleteIndex] = null;

        for (int i = deleteIndex + 1; i < MAX_NUM_ENTRIES; i++)
        {
            taskEntries[i - 1] = taskEntries[i];
        }
    }

    private int getDeleteIndex(TaskEntry deleteEntry)
    {
        TaskEntry[] currentEntries = getCurrentEntries(taskEntries);

        for (int i = 0; i < currentEntries.length; i++)
        {
            if (currentEntries[i] == deleteEntry) return i;
        }

        return -1; // deleteEntry not found (Shouldn't happen)
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

    @Override
    public void saveEntries() {
        File file = new File(context.getFilesDir(), TASKENTRIES_FILE_NAME);
        TaskEntry[] currentEntries = getCurrentSortedEntries();

        writeEntriesToOutputStream(TASKENTRIES_FILE_NAME, currentEntries);

        // TODO remove this when finished debugging
        printDirFiles();

        printEntryDates();
    }

    private void writeEntriesToOutputStream(String filename, ToDoEntry[] entries)
    {
        if (entries.length == 0) return;

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);

            for (ToDoEntry entry : entries) {
                outputStream.write((entry.toString() + "\n").getBytes());
            }

            outputStream.close();
        } catch (Exception e)
        {
            Log.d("Error", "could not write file");
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
                Log.d("Raw Line Text", fileEntryLine);
                TaskEntry entry = convertEntryStringToEntry(fileEntryLine);
                addNewTaskEntry(entry);
            }
        } catch (FileNotFoundException e)
        {
            Log.d("Error", "file not found");
        } catch (UnsupportedEncodingException e)
        {
            Log.d("Error", "unsupported encoding");
        } catch (IOException e)
        {
            Log.d("Error", "io error");
        }

        return taskEntries;
    }

    private TaskEntry convertEntryStringToEntry(String fileLine)
    {
        int combinedDueDate = Integer.parseInt(fileLine.substring(fileLine.length() - 8));
        int year = (combinedDueDate / 10000);
        int month = ((combinedDueDate % 10000) / 100);
        int day = (combinedDueDate % 100);
        String headerText = fileLine.substring(0, fileLine.length() - 8);
        TaskEntry savedEntry = new TaskEntry(headerText, year, month, day);

        return savedEntry;
    }

    // <---

    //
    // Helper methods used in multiple function blocks
    // --->

    private TaskEntry[] getCurrentEntries(TaskEntry[] original)
    {
        int currentArraySize = getNextAvailableIndex(original);

        if (currentArraySize == 0) return original;
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
            if (taskEntries[i] == null) break;

            Log.d("Index " + i, taskEntries[i].getCombinedDueDate() + "");
        }

        Log.d("","\n");
    }

    // <---
}
