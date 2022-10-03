package com.zoho.clustering.filerepl;

import com.zoho.clustering.util.FileUtil;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class DirectoryList
{
    private Map<Integer, String> dirMap;
    private Map<String, Integer> nameBasedIndex;
    
    public DirectoryList(final String dirListStr) {
        this.dirMap = new HashMap<Integer, String>();
        this.nameBasedIndex = new HashMap<String, Integer>();
        final String[] split;
        final String[] entries = split = dirListStr.split(",");
        for (final String entry : split) {
            final String[] parts = entry.split(":");
            if (parts.length != 2) {
                throw new RuntimeException("SlaveConfig Loading Failed. The specified value [" + dirListStr + "] for the 'dirList' property is incorrect");
            }
            try {
                this.addEntry(Integer.parseInt(parts[0]), parts[1]);
            }
            catch (final NumberFormatException exp) {
                throw new RuntimeException("SlaveConfig Loading Failed. The specified value [" + dirListStr + "] for the 'dirList' property is incorrect");
            }
        }
    }
    
    public Set<Map.Entry<Integer, String>> entries() {
        return this.dirMap.entrySet();
    }
    
    public Map.Entry<Integer, String> firstEntry() {
        return this.dirMap.entrySet().iterator().next();
    }
    
    public Set<String> dirNames() {
        return this.nameBasedIndex.keySet();
    }
    
    public String dirName() {
        final Set<String> names = this.dirNames();
        final Iterator<String> iterator = names.iterator();
        if (iterator.hasNext()) {
            final String name = iterator.next();
            return name;
        }
        throw new UnsupportedOperationException("DirList is Empty");
    }
    
    public String getNameForId(final Integer dirId) {
        final String dirName = this.dirMap.get(dirId);
        if (dirName == null) {
            throw new IllegalArgumentException("No Such dirId [" + dirId + "] configured");
        }
        return dirName;
    }
    
    public int getIdForName(final String dirName) {
        final Integer id = this.nameBasedIndex.get(dirName);
        if (id == null) {
            throw new IllegalArgumentException("No Such dir [" + dirName + "] configured");
        }
        return id;
    }
    
    void addEntry(final int dirId, String dirName) {
        dirName = dirName.replace('\\', '/');
        FileUtil.assertDir(dirName);
        if (this.dirMap.put(dirId, dirName) != null) {
            throw new IllegalArgumentException("Already an entry with dirId [" + dirId + "] is present");
        }
        if (this.nameBasedIndex.put(dirName, dirId) != null) {
            throw new IllegalArgumentException("Already an entry with dirName [" + dirName + "] is present");
        }
    }
    
    @Override
    public String toString() {
        return this.dirMap.toString();
    }
}
