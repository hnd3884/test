package org.tanukisoftware.wrapper.event;

public class WrapperServiceResumeEvent extends WrapperServiceActionEvent
{
    private static final long serialVersionUID = 338313484021328312L;
    
    public WrapperServiceResumeEvent(final int actionSourceCode) {
        super(actionSourceCode);
    }
    
    public String toString() {
        return "WrapperServiceResumeEvent[actionSourceCode=" + this.getSourceCodeName() + "]";
    }
}
