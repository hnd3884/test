package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Enforcement")
class Enforcement
{
    private String commandLine;
    private Integer timeOut;
    private Integer retryCount;
    private Integer retryInterval;
    
    public String getCommandLine() {
        return this.commandLine;
    }
    
    @XmlElement(name = "CommandLine")
    public void setCommandLine(final String commandLine) {
        this.commandLine = commandLine;
    }
    
    public Integer getTimeOut() {
        return this.timeOut;
    }
    
    @XmlElement(name = "TimeOut")
    public void setTimeOut(final Integer timeOut) {
        this.timeOut = timeOut;
    }
    
    public Integer getRetryCount() {
        return this.retryCount;
    }
    
    @XmlElement(name = "RetryCount")
    public void setRetryCount(final Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Integer getRetryInterval() {
        return this.retryInterval;
    }
    
    @XmlElement(name = "RetryInterval")
    public void setRetryInterval(final Integer retryInterval) {
        this.retryInterval = retryInterval;
    }
}
