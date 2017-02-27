package com.example.colefrench.todolist;

/**
 * Created by Cole French on 2/26/2017.
 */

public interface EntryManager {

    public void saveEntries();

    public ToDoEntry[] loadEntriesFromFile();
}
