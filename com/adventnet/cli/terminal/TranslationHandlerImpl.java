package com.adventnet.cli.terminal;

import java.util.Enumeration;
import java.util.Hashtable;

public class TranslationHandlerImpl implements TranslationHandler
{
    TranslationTableReader reader;
    Hashtable translationTable;
    int[] currentTable;
    
    public TranslationHandlerImpl() {
        this.reader = null;
        this.translationTable = null;
        this.currentTable = null;
    }
    
    public void readTranslationTables(final String s) throws TerminalException {
        try {
            (this.reader = new TranslationTableReader(s)).parseXml();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new TerminalException(ex.getMessage());
        }
        this.translationTable = this.reader.readTables();
        this.currentTable = this.translationTable.get(this.translationTable.keys().nextElement());
    }
    
    public void useTranslationTable(final String s) throws TerminalException {
        this.currentTable = this.translationTable.get(s);
        if (this.currentTable == null) {
            throw new TerminalException("Cannot find table");
        }
    }
    
    public byte translate(final byte b) throws TerminalException {
        final int n = this.currentTable[b];
        if (n > 255) {
            return b;
        }
        return (byte)(0xFF & n);
    }
    
    public byte inverseTranslate(final byte b) throws TerminalException {
        for (int i = 0; i < 256; ++i) {
            if (this.currentTable[i] == b) {
                return (byte)(0xFF & i);
            }
        }
        return b;
    }
    
    public String[] getTranslationTableNames() throws TerminalException {
        final Enumeration keys = this.translationTable.keys();
        final int size = this.translationTable.size();
        final String[] array = new String[this.translationTable.size()];
        for (int i = 0; i < size; ++i) {
            array[i] = (String)keys.nextElement();
        }
        return array;
    }
}
