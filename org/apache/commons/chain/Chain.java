package org.apache.commons.chain;

public interface Chain extends Command
{
    void addCommand(final Command p0);
    
    boolean execute(final Context p0) throws Exception;
}
