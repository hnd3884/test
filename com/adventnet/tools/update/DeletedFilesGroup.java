package com.adventnet.tools.update;

import java.util.Set;
import java.util.ArrayList;
import java.util.Hashtable;

public class DeletedFilesGroup
{
    private static Hashtable<String, ArrayList<String>> deletedJarEntries;
    private static ArrayList<String> deletedFiles;
    
    public static void addDeletedFileEntry(final String deletedFile) {
        if (!DeletedFilesGroup.deletedFiles.contains(deletedFile)) {
            DeletedFilesGroup.deletedFiles.add(deletedFile);
        }
    }
    
    public static ArrayList<String> getDeletedFiles() {
        return DeletedFilesGroup.deletedFiles;
    }
    
    public static void setDeletedJarEntries(final String jarName, final ArrayList<String> deletedEntries) {
        DeletedFilesGroup.deletedJarEntries.put(jarName, deletedEntries);
    }
    
    public static ArrayList<String> getDeletedJarEntries(final String jarName) {
        return DeletedFilesGroup.deletedJarEntries.get(jarName);
    }
    
    public static Set<String> getJarNamesContainingDeleteEntries() {
        return DeletedFilesGroup.deletedJarEntries.keySet();
    }
    
    public static void removeDeletedJarEntries(final String jarName) {
        DeletedFilesGroup.deletedJarEntries.remove(jarName);
    }
    
    public static void clearEntries() {
        DeletedFilesGroup.deletedFiles.clear();
        DeletedFilesGroup.deletedJarEntries.clear();
    }
    
    static {
        DeletedFilesGroup.deletedJarEntries = new Hashtable<String, ArrayList<String>>();
        DeletedFilesGroup.deletedFiles = new ArrayList<String>();
    }
}
