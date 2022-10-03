package com.adventnet.sym.winaccess;

public class NetworkElementProvider
{
    public static final int ROOT_NETWORK_ELEMENTS = 0;
    public static final int NETWORK_ELEMENTS = 1;
    public static final int DOMAIN_ELEMENTS = 2;
    public static final int SERVER_ELEMENTS = 3;
    public static final int DIRECTORY_ELEMENTS = 4;
    public static final int FILE_ELEMENTS = 5;
    public String name;
    public String path;
    public int type;
    public boolean hasChildren;
    
    public NetworkElementProvider(final String objName, final String objPath, final int objType, final boolean objHasChildren) {
        this.name = objName;
        this.path = objPath;
        this.type = objType;
        this.hasChildren = objHasChildren;
    }
    
    @Override
    public String toString() {
        return "name :'" + this.name + "' path :'" + this.path + "'type :'" + this.type + "'hasChildren :'" + this.hasChildren;
    }
}
