package com.adventnet.iam.security.antivirus.clamav;

import java.io.Serializable;

public class CLAMAVConfiguration implements Serializable
{
    private String host;
    private int port;
    private boolean isNonPersistentScan;
    private boolean isClamAVInstrumentationEnabled;
    private int readTimeOut;
    private int persistantConnections;
    
    public CLAMAVConfiguration(final String host, final int port) {
        this.host = null;
        this.port = 0;
        this.isNonPersistentScan = true;
        this.isClamAVInstrumentationEnabled = true;
        this.readTimeOut = 60000;
        this.persistantConnections = 0;
        this.host = host;
        this.port = port;
    }
    
    public void setNonPersistenScan(final boolean isNonPersistentScan) {
        this.isNonPersistentScan = isNonPersistentScan;
    }
    
    public void setClamAVInstrumentation(final boolean isClamAVInstrumentationEnabled) {
        this.isClamAVInstrumentationEnabled = isClamAVInstrumentationEnabled;
    }
    
    public void setReadTimeOut(final int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }
    
    public void setPersistantConnections(final int persistantConnections) {
        this.persistantConnections = persistantConnections;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public boolean isNonPersistentScan() {
        return this.isNonPersistentScan;
    }
    
    public boolean isClamAVInstrumentationEnabled() {
        return this.isClamAVInstrumentationEnabled;
    }
    
    public int getReadTimeOut() {
        return this.readTimeOut;
    }
    
    public int getPersistantConnections() {
        return this.persistantConnections;
    }
}
