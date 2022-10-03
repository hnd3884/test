package com.adventnet.cli.terminal;

public interface TransformationHandler
{
    void readTransformationTables(final String p0) throws TerminalException;
    
    void useTransformationTable(final String p0) throws TerminalException;
    
    byte[] transform(final byte[] p0) throws TerminalException;
    
    String[] getTransformationTableNames() throws TerminalException;
}
