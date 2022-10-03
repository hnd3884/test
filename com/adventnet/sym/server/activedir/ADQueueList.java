package com.adventnet.sym.server.activedir;

import java.util.ArrayList;

public class ADQueueList extends ArrayList
{
    public String domainNetBIOSName;
    public int containedResourceType;
    public int totalSize;
    public int startIndex;
    public int endIndex;
    public boolean isFirstList;
    public boolean isLastList;
    
    public ADQueueList() {
        this.domainNetBIOSName = null;
        this.containedResourceType = -1;
        this.totalSize = -1;
        this.startIndex = -1;
        this.endIndex = -1;
        this.isFirstList = false;
        this.isLastList = false;
    }
}
