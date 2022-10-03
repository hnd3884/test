package com.adventnet.sym.server.resource;

public class Resource extends com.me.devicemanagement.framework.server.resource.Resource
{
    public static final int AD_OBJECT = 0;
    public static final int ACTIVE_DIR = 1;
    public static final int WORKGROUP = 2;
    public static final int LOCAL_USER = 102;
    public static final int MOBILE_DEVICE = 120;
    public static final int BLOCKED_COMPUTER = 200;
    public static final int UNIQUECG_COMPUTER = 250;
    public String name;
    public String domainName;
    public int type;
    
    public Resource(final String name, final String domainName) {
        this.name = "";
        this.domainName = "";
        this.type = 111;
        this.name = name;
        this.domainName = domainName;
    }
    
    public Resource(final String name, final String domainName, final int type) {
        this.name = "";
        this.domainName = "";
        this.type = 111;
        this.name = name;
        this.domainName = domainName;
        this.type = type;
    }
    
    public boolean equals(final Resource res) {
        return res != null && (res.name.equalsIgnoreCase(this.name) && res.domainName.equalsIgnoreCase(this.domainName) && res.type == this.type);
    }
    
    public String toString() {
        return "Name=" + this.name + "\t DomainName=" + this.domainName + "\t Type=" + this.type;
    }
}
