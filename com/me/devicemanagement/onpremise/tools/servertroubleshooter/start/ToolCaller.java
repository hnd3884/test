package com.me.devicemanagement.onpremise.tools.servertroubleshooter.start;

import java.util.logging.Level;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSInterface;
import java.util.logging.Logger;
import java.util.concurrent.Callable;

public class ToolCaller implements Callable
{
    private static final Logger LOGGER;
    public STSInterface toolObj;
    
    public ToolCaller() {
        this.toolObj = null;
    }
    
    @Override
    public Object call() {
        try {
            if (this.toolObj != null) {
                ToolCaller.LOGGER.log(Level.INFO, "Starting tool {0}", this.toolObj.getClass().getName());
                this.toolObj.startTool();
                ToolCaller.LOGGER.log(Level.INFO, "{0} tool execution completed", this.toolObj.getClass().getName());
            }
            else {
                ToolCaller.LOGGER.log(Level.INFO, "Tool class object is not provided");
            }
        }
        catch (final Exception ex) {
            ToolCaller.LOGGER.log(Level.WARNING, "Caught exception while starting the tool", ex);
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("STSStarter");
    }
}
