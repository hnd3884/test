package org.eclipse.jdt.internal.compiler.env;

public interface IDependent
{
    public static final char JAR_FILE_ENTRY_SEPARATOR = '|';
    
    char[] getFileName();
}
