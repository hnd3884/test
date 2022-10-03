package com.zoho.dddiff;

import java.util.ArrayList;
import java.net.URL;
import java.util.List;

public class DataDictionaryAggregator
{
    private List<URL> files;
    
    public DataDictionaryAggregator() {
        this.files = new ArrayList<URL>();
    }
    
    public DataDictionaryAggregator(final URL dataDictionaryFileURL) {
        (this.files = new ArrayList<URL>()).add(dataDictionaryFileURL);
    }
    
    public void addFile(final URL newDataDictionaryFileURL) {
        this.files.add(newDataDictionaryFileURL);
    }
    
    URL[] getFiles() {
        final URL[] allFiles = new URL[this.files.size()];
        for (int i = 0; i < this.files.size(); ++i) {
            allFiles[i] = this.files.get(i);
        }
        return allFiles;
    }
    
    public DataDictionaryDiff diff(final DataDictionaryAggregator newDataDictionaryAggregator) throws Exception {
        final DataDictionaryDiff dddiff = new DataDictionaryDiff(this.getFiles(), newDataDictionaryAggregator.getFiles());
        return dddiff;
    }
}
