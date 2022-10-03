package org.tanukisoftware.wrapper.event;

public class WrapperServiceControlEvent extends WrapperServiceEvent
{
    private static final long serialVersionUID = -8642470717850552167L;
    private int m_serviceControlCode;
    
    public WrapperServiceControlEvent(final int serviceControlCode) {
        this.m_serviceControlCode = serviceControlCode;
    }
    
    public int getServiceControlCode() {
        return this.m_serviceControlCode;
    }
    
    public String toString() {
        return "WrapperServiceControlEvent[serviceControlCode=" + this.getServiceControlCode() + "]";
    }
}
