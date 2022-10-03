package com.adventnet.tools.update.viewer;

import java.io.File;
import java.util.Properties;

public class DocumentNodeProps
{
    private String id;
    private String desc;
    private String dispName;
    private String fileName;
    private Properties miscProps;
    
    public DocumentNodeProps(final String id, final String dispName, final String fileName) {
        this.id = null;
        this.desc = null;
        this.dispName = null;
        this.fileName = null;
        this.miscProps = new Properties();
        if (id == null) {
            throw new IllegalArgumentException("The ID cannot be null for DocumentNodeProps");
        }
        this.id = id;
        if (dispName == null) {
            this.dispName = id;
        }
        else {
            this.dispName = dispName;
        }
        this.fileName = fileName;
    }
    
    public String getID() {
        return this.id;
    }
    
    public String getDisplayName() {
        return this.dispName;
    }
    
    public void setDescription(final String desc) {
        this.desc = desc;
    }
    
    public String getDescription() {
        return this.desc;
    }
    
    public void setMiscProperties(final Properties props) {
        this.miscProps = props;
    }
    
    public Properties getMiscProperties() {
        return this.miscProps;
    }
    
    public File getDocumentFile() {
        return new File(this.fileName);
    }
    
    @Override
    public String toString() {
        final String str = "DocumentNodeProps - ID : " + this.id + ", DispName : " + this.dispName + ", File Name : " + this.fileName + ", Description : " + this.desc + ", Misc. Properties : " + this.miscProps;
        return str;
    }
}
