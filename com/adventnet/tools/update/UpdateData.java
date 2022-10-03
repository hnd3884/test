package com.adventnet.tools.update;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Set;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Vector;

public class UpdateData
{
    private Vector contextVector;
    private Object[] preInstallArray;
    private Object[] postInstallArray;
    private String readme;
    private String contextType;
    private String description;
    private String contextPath;
    private String contextName;
    private Vector dependencyVector;
    private ArrayList newFilesList;
    private ArrayList zipFilesList;
    private Hashtable<String, Set<String>> jarEntriesToBeDeleted;
    private Set<String> filesToBeDeleted;
    
    public UpdateData() {
        this.contextVector = null;
        this.preInstallArray = null;
        this.postInstallArray = null;
        this.readme = null;
        this.contextType = null;
        this.description = null;
        this.contextPath = null;
        this.contextName = null;
        this.dependencyVector = null;
        this.newFilesList = null;
        this.zipFilesList = null;
        this.jarEntriesToBeDeleted = null;
        this.filesToBeDeleted = null;
        this.contextVector = new Vector();
        this.preInstallArray = new Object[0];
        this.postInstallArray = new Object[0];
        this.dependencyVector = new Vector();
        this.newFilesList = new ArrayList();
        this.zipFilesList = new ArrayList();
        this.jarEntriesToBeDeleted = new Hashtable<String, Set<String>>();
        this.filesToBeDeleted = new TreeSet<String>();
    }
    
    public void setContextVector(final Vector vec) {
        this.contextVector = vec;
    }
    
    public Vector getContextVector() {
        return this.contextVector;
    }
    
    public void setNewFileGroup(final ArrayList list) {
        this.newFilesList = list;
    }
    
    public ArrayList getNewFileGroup() {
        return this.newFilesList;
    }
    
    public void setZipFileGroup(final ArrayList list) {
        this.zipFilesList = list;
    }
    
    public ArrayList getZipFileGroup() {
        return this.zipFilesList;
    }
    
    public void setDependencyContext(final Vector depVector) {
        this.dependencyVector = depVector;
    }
    
    public Vector getDependencyVector() {
        return this.dependencyVector;
    }
    
    public void setPreInstallArray(final Object[] array) {
        this.preInstallArray = array;
    }
    
    public void setPostInstallArray(final Object[] array) {
        this.postInstallArray = array;
    }
    
    public Object[] getPreInstallArray() {
        return this.preInstallArray;
    }
    
    public Object[] getPostInstallArray() {
        return this.postInstallArray;
    }
    
    public void setContextName(final String name) {
        this.contextName = name;
    }
    
    public void setContextType(final String type) {
        this.contextType = type;
    }
    
    public String getContextType() {
        return this.contextType;
    }
    
    public void setContextDescription(final String desc) {
        this.description = desc;
    }
    
    public String getContextDescription() {
        return this.description;
    }
    
    public String getContextName() {
        return this.contextName;
    }
    
    public void setContextReadme(final String read) {
        this.readme = read;
    }
    
    public String getContextReadme() {
        return this.readme;
    }
    
    public void setContextPath(final String path) {
        this.contextPath = path;
    }
    
    public String getContextPath() {
        return this.contextPath;
    }
    
    public void markFileForDelete(final String deletedFile) {
        this.filesToBeDeleted.add(deletedFile);
    }
    
    public Set<String> getFilesMarkedForDelete() {
        return this.filesToBeDeleted;
    }
    
    public void markJarEntriesForDelete(final String jarName, final Set<String> entriesToBeDeleted) {
        if (this.jarEntriesToBeDeleted.containsKey(jarName)) {
            final Set<String> alreadyMarkedEntriesForDelete = this.jarEntriesToBeDeleted.get(jarName);
            for (final String fileName : alreadyMarkedEntriesForDelete) {
                entriesToBeDeleted.add(fileName);
            }
        }
        this.jarEntriesToBeDeleted.put(jarName, entriesToBeDeleted);
    }
    
    public Hashtable<String, Set<String>> getAllJarEntriesMarkedForDelete() {
        return this.jarEntriesToBeDeleted;
    }
    
    public Set<String> getJarEntriesMarkedForDelete(final String jarName) {
        return this.jarEntriesToBeDeleted.get(jarName);
    }
    
    public Set<String> getJarNamesContainingDeleteEntries() {
        return this.jarEntriesToBeDeleted.keySet();
    }
}
