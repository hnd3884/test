package com.adventnet.swissqlapi.sql.statement;

import java.util.ArrayList;

public class ModifiedObjectNames
{
    private ArrayList modifiedTableAttrList;
    private ArrayList modifiedColAttrList;
    private ArrayList modifiedConstrAttrList;
    
    public ModifiedObjectNames() {
        this.modifiedTableAttrList = new ArrayList();
        this.modifiedColAttrList = new ArrayList();
        this.modifiedConstrAttrList = new ArrayList();
    }
    
    public void addModifiedObjectName(final ModifiedObjectAttr modifiedTableAttr) {
        this.modifiedTableAttrList.add(modifiedTableAttr);
    }
    
    public void addModifiedColumns(final ModifiedObjectAttr modifiedColAttr) {
        this.modifiedColAttrList.add(modifiedColAttr);
    }
    
    public void addModifiedConstraints(final ModifiedObjectAttr modifiedConstrAttr) {
        this.modifiedConstrAttrList.add(modifiedConstrAttr);
    }
    
    public ArrayList getModifiedObjectName() {
        return this.modifiedTableAttrList;
    }
    
    public ArrayList getModifiedColumns() {
        return this.modifiedColAttrList;
    }
    
    public ArrayList getModifiedConstraints() {
        return this.modifiedColAttrList;
    }
}
