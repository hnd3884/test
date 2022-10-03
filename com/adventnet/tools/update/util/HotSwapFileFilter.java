package com.adventnet.tools.update.util;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class HotSwapFileFilter
{
    private List<String> filePaths;
    private List<String> jarFilesWithAddedEntries;
    private List<String> modifiedFiles;
    
    public HotSwapFileFilter() {
        this.filePaths = new ArrayList<String>();
        this.jarFilesWithAddedEntries = new ArrayList<String>();
        this.modifiedFiles = new ArrayList<String>();
    }
    
    public void addFilePath(final String filePath) {
        if (!this.filePaths.contains(filePath)) {
            this.filePaths.add(filePath);
        }
    }
    
    public void removeFilePath(final String filePath) {
        if (this.filePaths.contains(filePath)) {
            this.filePaths.remove(filePath);
        }
    }
    
    public boolean isHotSwapCriteriaMatched(final ZipDiffUtil zdu, final EnhancedFileFilter filter, final List<String> filesInResourcesDir) {
        final ArrayList newFiles = zdu.getNewFiles(null);
        final ArrayList modFiles = zdu.getModifiedFiles(null);
        return (newFiles == null || newFiles.isEmpty() || this.isFiltered(newFiles)) && (filesInResourcesDir == null || filesInResourcesDir.isEmpty() || this.isFiltered(filesInResourcesDir)) && (modFiles != null && !modFiles.isEmpty()) && this.jarFilesWithAddedEntries.isEmpty() && this.isUJarOnlyPresent(modFiles, filter);
    }
    
    private boolean isUJarOnlyPresent(final ArrayList modFiles, final EnhancedFileFilter filter) {
        int count = 0;
        for (final Object modFile : modFiles) {
            final String file = (String)modFile;
            final boolean condition = file.lastIndexOf(".jar") != -1;
            if (condition && filter.accept(file)) {
                ++count;
            }
            else {
                this.modifiedFiles.add(file);
            }
        }
        if (!this.modifiedFiles.isEmpty()) {
            return count > 0 && this.isFiltered(this.modifiedFiles);
        }
        return count > 0;
    }
    
    private boolean isFiltered(final List<String> files) {
        for (final String file : files) {
            if (!this.filePaths.contains(file) && !this.isPatternMatched(file)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isPatternMatched(final String fileName) {
        for (final String fName : this.filePaths) {
            final int index = fName.indexOf("*");
            if (index != -1 && fileName.startsWith(fName.substring(0, index)) && fileName.endsWith(fName.substring(index + 1))) {
                return true;
            }
        }
        return false;
    }
    
    public void setJarsWithAddedEntries(final List<String> jarFilesWithAddedEntries) {
        this.jarFilesWithAddedEntries = jarFilesWithAddedEntries;
    }
    
    public void updatePaths(final String relProductHome) {
        for (int i = 0; i < this.filePaths.size(); ++i) {
            this.filePaths.set(i, relProductHome + "/" + this.filePaths.get(i));
        }
    }
}
