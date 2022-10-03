package com.adventnet.cli.terminal;

public interface TranslationHandler
{
    void readTranslationTables(final String p0) throws TerminalException;
    
    void useTranslationTable(final String p0) throws TerminalException;
    
    byte translate(final byte p0) throws TerminalException;
    
    byte inverseTranslate(final byte p0) throws TerminalException;
    
    String[] getTranslationTableNames() throws TerminalException;
}
