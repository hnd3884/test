package com.adventnet.persistence.migration;

import java.util.ArrayList;
import com.zoho.conf.tree.ConfTreeBuilder;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.util.List;
import com.zoho.conf.tree.ConfTree;

public class ExtendedDDAggregator
{
    private static ConfTree newExtendedDDConfTree;
    private static ConfTree oldExtendedDDConfTree;
    private static String oldConfPath;
    private static String newConfPath;
    private static List<String> modifiedMaxSizeOfColsInDD;
    private static List<String> modifiedDefaultValueOfColsInDD;
    private static boolean isExtendedDDModified;
    
    public static void initialize(final List<URL> oldConfFilePaths, final List<URL> newConfFilePaths, final String backUpDirPath, final String homeDirPath) throws IOException {
        ExtendedDDAggregator.isExtendedDDModified = !oldConfFilePaths.equals(newConfFilePaths);
        if (ExtendedDDAggregator.isExtendedDDModified) {
            ExtendedDDAggregator.newExtendedDDConfTree = getConfTree(newConfFilePaths);
            ExtendedDDAggregator.oldExtendedDDConfTree = getConfTree(oldConfFilePaths);
            ExtendedDDAggregator.oldConfPath = backUpDirPath + File.separator + "conf" + File.separator + "ExtendedAttributes";
            ExtendedDDAggregator.newConfPath = homeDirPath + File.separator + "conf" + File.separator + "ExtendedAttributes";
        }
    }
    
    private static ConfTree getConfTree(final List<URL> filePaths) throws IOException {
        return ((ConfTreeBuilder)ConfTreeBuilder.confTree().fromConfFile((URL[])filePaths.toArray(new URL[filePaths.size()]))).build();
    }
    
    public static Integer getNewExtendedMaxValue(final String tableName, final String columnName) {
        final String key = tableName + "." + columnName + ".maxsize";
        if (ExtendedDDAggregator.newExtendedDDConfTree != null && ExtendedDDAggregator.newExtendedDDConfTree.get(key) != null) {
            return Integer.parseInt(ExtendedDDAggregator.newExtendedDDConfTree.get(key));
        }
        return null;
    }
    
    public static Integer getOldExtendedMaxValue(final String tableName, final String columnName) {
        final String key = tableName + "." + columnName + ".maxsize";
        if (ExtendedDDAggregator.oldExtendedDDConfTree != null && ExtendedDDAggregator.oldExtendedDDConfTree.get(key) != null) {
            return Integer.parseInt(ExtendedDDAggregator.oldExtendedDDConfTree.get(key));
        }
        return null;
    }
    
    public static String getNewExtendedDefaultValue(final String tableName, final String columnName) {
        final String key = tableName + "." + columnName + ".defaultvalue";
        if (ExtendedDDAggregator.newExtendedDDConfTree != null) {
            return ExtendedDDAggregator.newExtendedDDConfTree.get(key);
        }
        return null;
    }
    
    public static String getOldExtendedDefaultValue(final String tableName, final String columnName) {
        final String key = tableName + "." + columnName + ".defaultvalue";
        if (ExtendedDDAggregator.oldExtendedDDConfTree != null) {
            return ExtendedDDAggregator.oldExtendedDDConfTree.get(key);
        }
        return null;
    }
    
    public static void setColsModifiedInDD(final String type, final String key) {
        if (type.equalsIgnoreCase("maxsize")) {
            ExtendedDDAggregator.modifiedMaxSizeOfColsInDD.add(key);
        }
        else {
            ExtendedDDAggregator.modifiedDefaultValueOfColsInDD.add(key);
        }
    }
    
    public static boolean isExtendedDDModified() {
        return ExtendedDDAggregator.isExtendedDDModified;
    }
    
    public static ExtendedDDDiff generateChanges(final DDChangeListener.MigrationType operationType, final boolean ignoreMaxSize) throws Exception {
        final ExtendedDDDiff ddDiff = new ExtendedDDDiff(ExtendedDDAggregator.oldExtendedDDConfTree, ExtendedDDAggregator.newExtendedDDConfTree, operationType, ignoreMaxSize, ExtendedDDAggregator.oldConfPath, ExtendedDDAggregator.newConfPath, ExtendedDDAggregator.modifiedMaxSizeOfColsInDD, ExtendedDDAggregator.modifiedDefaultValueOfColsInDD);
        ddDiff.generateDiff();
        return ddDiff;
    }
    
    static {
        ExtendedDDAggregator.newExtendedDDConfTree = null;
        ExtendedDDAggregator.oldExtendedDDConfTree = null;
        ExtendedDDAggregator.oldConfPath = null;
        ExtendedDDAggregator.newConfPath = null;
        ExtendedDDAggregator.modifiedMaxSizeOfColsInDD = new ArrayList<String>();
        ExtendedDDAggregator.modifiedDefaultValueOfColsInDD = new ArrayList<String>();
        ExtendedDDAggregator.isExtendedDDModified = false;
    }
}
