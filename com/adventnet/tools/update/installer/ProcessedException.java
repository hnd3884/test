package com.adventnet.tools.update.installer;

public class ProcessedException extends Exception
{
    private Throwable th;
    
    public ProcessedException(final String msgArg, final Throwable thArg) {
        super(PureUtils.getCorrectErrorMsg(msgArg, thArg));
        this.th = thArg;
    }
    
    public ProcessedException(final String msgArg, final Throwable thArg, final String fromArg) {
        super(PureUtils.getCorrectErrorMsg(msgArg, thArg, fromArg));
        this.th = thArg;
    }
    
    public ProcessedException(final String msgArg) {
        super(msgArg);
        this.th = null;
    }
    
    public Throwable getOriginalException() {
        Throwable orig;
        for (orig = this.th; orig instanceof ProcessedException; orig = ((ProcessedException)orig).getOriginalException()) {}
        return orig;
    }
}
