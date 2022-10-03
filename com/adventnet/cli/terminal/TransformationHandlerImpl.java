package com.adventnet.cli.terminal;

import java.util.Enumeration;
import java.util.Hashtable;

public class TransformationHandlerImpl implements TransformationHandler
{
    Hashtable transformationTable;
    Hashtable currentTable;
    Transformation tr;
    
    public TransformationHandlerImpl() {
        this.transformationTable = null;
        this.currentTable = null;
        this.tr = null;
    }
    
    public void readTransformationTables(final String s) throws TerminalException {
        TransformationTableReader transformationTableReader;
        try {
            transformationTableReader = new TransformationTableReader(s);
            transformationTableReader.parseXml();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new TerminalException(ex.getMessage());
        }
        this.transformationTable = transformationTableReader.readTables();
        this.currentTable = this.transformationTable.get(this.transformationTable.keys().nextElement());
        (this.tr = new Transformation()).setTransformationTable(this.currentTable);
    }
    
    public void useTransformationTable(final String s) throws TerminalException {
        this.currentTable = this.transformationTable.get(s);
        if (this.currentTable == null) {
            throw new TerminalException("Cannot find table");
        }
        this.tr.setTransformationTable(this.currentTable);
    }
    
    public byte[] transform(final byte[] array) throws TerminalException {
        return this.tr.transform(array);
    }
    
    public String[] getTransformationTableNames() throws TerminalException {
        final Enumeration keys = this.transformationTable.keys();
        final int size = this.transformationTable.size();
        final String[] array = new String[this.transformationTable.size()];
        for (int i = 0; i < size; ++i) {
            array[i] = (String)keys.nextElement();
        }
        return array;
    }
}
