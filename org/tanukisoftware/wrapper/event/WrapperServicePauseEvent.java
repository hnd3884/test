package org.tanukisoftware.wrapper.event;

public class WrapperServicePauseEvent extends WrapperServiceActionEvent
{
    private static final long serialVersionUID = 1308747091110200773L;
    
    public WrapperServicePauseEvent(final int actionSourceCode) {
        super(actionSourceCode);
    }
    
    public String toString() {
        return "WrapperServicePauseEvent[actionSourceCode=" + this.getSourceCodeName() + "]";
    }
}
