package org.apache.commons.collections4.sequence;

public interface CommandVisitor<T>
{
    void visitInsertCommand(final T p0);
    
    void visitKeepCommand(final T p0);
    
    void visitDeleteCommand(final T p0);
}
