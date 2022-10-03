package com.adventnet.persistence.xml;

import com.zoho.conf.AppResources;
import java.util.ArrayList;

public class ParentChildrenMap
{
    private ArrayList parentElementNames;
    private String elementName;
    private String masterTableName;
    private ArrayList childPCMs;
    private int useCaseType;
    private boolean groupingTag;
    private boolean singleFKNonPK;
    private boolean bdfk;
    
    void addParentElementName(final String parentElementName) {
        if (this.parentElementNames == null) {
            this.parentElementNames = new ArrayList();
        }
        this.parentElementNames.add(parentElementName);
    }
    
    ArrayList getParentElementNames() {
        return this.parentElementNames;
    }
    
    void removeParentElementName(final String parentElementName) {
        if (this.parentElementNames == null) {
            return;
        }
        this.parentElementNames.remove(parentElementName);
    }
    
    void setElementName(final String elementName) {
        this.elementName = elementName;
    }
    
    public String getElementName() {
        return this.elementName;
    }
    
    void setMasterTableName(final String tableName) {
        this.masterTableName = tableName;
    }
    
    public String getMasterTableName() {
        return this.masterTableName;
    }
    
    void addChildPCM(final ParentChildrenMap childPCM) {
        if (this.childPCMs == null) {
            this.childPCMs = new ArrayList();
        }
        this.childPCMs.add(childPCM);
    }
    
    public ArrayList getChildPCMs() {
        return this.childPCMs;
    }
    
    void removeChildPCM(final ParentChildrenMap pcm) {
        if (this.childPCMs == null) {
            return;
        }
        this.childPCMs.remove(pcm);
    }
    
    void removeAllChildPCMs() {
        this.childPCMs = null;
    }
    
    void setUseCaseType(final int useCaseType) {
        this.useCaseType = useCaseType;
    }
    
    int getUseCaseType() {
        return this.useCaseType;
    }
    
    void setGroupingTag(final boolean groupingTag) {
        this.groupingTag = groupingTag;
    }
    
    boolean isGroupingTag() {
        return this.groupingTag;
    }
    
    void setSingleFKNonPK(final boolean singleFKNonPK) {
        this.singleFKNonPK = singleFKNonPK;
    }
    
    boolean isSingleFKNonPK() {
        return this.singleFKNonPK;
    }
    
    void setBdfk(final boolean bdfk) {
        this.bdfk = bdfk;
    }
    
    boolean isBdfk() {
        return this.bdfk;
    }
    
    @Override
    public boolean equals(final Object obj) {
        final String eName = ((ParentChildrenMap)obj).getElementName();
        return this.getElementName().equals(eName);
    }
    
    public int hashcode() {
        return super.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(AppResources.getString("line.separator"));
        buffer.append("*********************************************************************************");
        buffer.append(AppResources.getString("line.separator"));
        buffer.append("ParentChildrenMap:");
        buffer.append(AppResources.getString("line.separator"));
        buffer.append("Element Name:");
        buffer.append(this.getElementName());
        buffer.append(AppResources.getString("line.separator"));
        buffer.append("ParentElementNames:");
        buffer.append(this.getParentElementNames());
        buffer.append(AppResources.getString("line.separator"));
        buffer.append("UseCaseType:");
        buffer.append(this.getUseCaseType());
        buffer.append(AppResources.getString("line.separator"));
        buffer.append("Is grouping tag:");
        buffer.append(this.isGroupingTag());
        buffer.append(AppResources.getString("line.separator"));
        buffer.append("Is singleFKNonPK :");
        buffer.append(this.isSingleFKNonPK());
        buffer.append(AppResources.getString("line.separator"));
        buffer.append("Is bdfk :");
        buffer.append(this.isBdfk());
        buffer.append(AppResources.getString("line.separator"));
        buffer.append("Child ParentChildrenMaps:");
        buffer.append(this.getChildPCMs());
        return buffer.toString();
    }
}
