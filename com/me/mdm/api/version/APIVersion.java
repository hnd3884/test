package com.me.mdm.api.version;

public class APIVersion implements Comparable<APIVersion>
{
    public final Integer[] versionParts;
    private final int maxVersionIndex = 3;
    private boolean fwserver;
    
    public APIVersion(String apiVersion) {
        this.versionParts = new Integer[3];
        this.fwserver = false;
        if (apiVersion.equals("1.4")) {
            this.fwserver = true;
            return;
        }
        apiVersion = apiVersion.substring(1);
        final int noOfParts = String.valueOf(apiVersion).split("\\.").length;
        for (int i = 0; i < noOfParts; ++i) {
            this.versionParts[i] = Integer.valueOf(String.valueOf(apiVersion).split("\\.")[i]);
        }
        for (int i = noOfParts; i < 3; ++i) {
            this.versionParts[i] = 0;
        }
    }
    
    @Override
    public int compareTo(final APIVersion apiVersion2) {
        if (this.fwserver) {
            return 0;
        }
        final int compareResult = this.compareTo(apiVersion2, 0);
        return (compareResult == 0) ? 0 : ((compareResult > 0) ? 1 : -1);
    }
    
    public int compareTo(final APIVersion apiVersion, final int versionIndex) {
        final int compareResult = this.versionParts[versionIndex].compareTo(apiVersion.versionParts[versionIndex]);
        if (compareResult != 0) {
            return compareResult;
        }
        if (versionIndex + 1 < 3) {
            return this.compareTo(apiVersion, versionIndex + 1);
        }
        return 0;
    }
}
