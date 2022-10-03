package com.sun.management;

import sun.management.VMOptionCompositeData;
import javax.management.openmbean.CompositeData;
import jdk.Exported;

@Exported
public class VMOption
{
    private String name;
    private String value;
    private boolean writeable;
    private Origin origin;
    
    public VMOption(final String name, final String value, final boolean writeable, final Origin origin) {
        this.name = name;
        this.value = value;
        this.writeable = writeable;
        this.origin = origin;
    }
    
    private VMOption(final CompositeData compositeData) {
        VMOptionCompositeData.validateCompositeData(compositeData);
        this.name = VMOptionCompositeData.getName(compositeData);
        this.value = VMOptionCompositeData.getValue(compositeData);
        this.writeable = VMOptionCompositeData.isWriteable(compositeData);
        this.origin = VMOptionCompositeData.getOrigin(compositeData);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public Origin getOrigin() {
        return this.origin;
    }
    
    public boolean isWriteable() {
        return this.writeable;
    }
    
    @Override
    public String toString() {
        return "VM option: " + this.getName() + " value: " + this.value + "  origin: " + this.origin + " " + (this.writeable ? "(read-write)" : "(read-only)");
    }
    
    public static VMOption from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        if (compositeData instanceof VMOptionCompositeData) {
            return ((VMOptionCompositeData)compositeData).getVMOption();
        }
        return new VMOption(compositeData);
    }
    
    @Exported
    public enum Origin
    {
        DEFAULT, 
        VM_CREATION, 
        ENVIRON_VAR, 
        CONFIG_FILE, 
        MANAGEMENT, 
        ERGONOMIC, 
        OTHER;
    }
}
