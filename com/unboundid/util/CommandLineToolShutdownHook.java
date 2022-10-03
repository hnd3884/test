package com.unboundid.util;

import com.unboundid.ldap.sdk.ResultCode;
import java.util.concurrent.atomic.AtomicReference;

final class CommandLineToolShutdownHook extends Thread
{
    private final AtomicReference<ResultCode> resultCodeRef;
    private final CommandLineTool tool;
    
    CommandLineToolShutdownHook(final CommandLineTool tool, final AtomicReference<ResultCode> resultCodeRef) {
        this.tool = tool;
        this.resultCodeRef = resultCodeRef;
    }
    
    @Override
    public void run() {
        this.tool.doShutdownHookProcessing(this.resultCodeRef.get());
    }
}
