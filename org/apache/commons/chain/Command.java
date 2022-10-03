package org.apache.commons.chain;

public interface Command
{
    public static final boolean CONTINUE_PROCESSING = false;
    public static final boolean PROCESSING_COMPLETE = true;
    
    boolean execute(final Context p0) throws Exception;
}
