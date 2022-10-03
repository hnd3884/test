package com.zoho.clustering.filerepl.slave.api;

import com.zoho.clustering.util.MyProperties;

public class HttpMasterStubConfig
{
    private int connTimeoutMillis;
    private int readTimeoutMillis;
    private String masterURL;
    private String uriGetEvents;
    private String uriDownloadFile;
    private String uriTakeSnapshot;
    private String uriDownloadSnapshot;
    private boolean mutable;
    private String hostNameVerifierClassName;
    
    public HttpMasterStubConfig() {
        this.mutable = true;
    }
    
    public HttpMasterStubConfig(final String prefix, final MyProperties props) {
        this.mutable = true;
        this.connTimeoutMillis = props.intValue(prefix + ".connTimeoutMillis", 1000);
        this.readTimeoutMillis = props.intValue(prefix + ".readTimeoutMillis", 1000);
        this.uriGetEvents = props.value(prefix + ".uri.getEvents");
        this.uriDownloadFile = props.value(prefix + ".uri.downloadFile");
        this.uriTakeSnapshot = props.value(prefix + ".uri.takeSnapshot");
        this.uriDownloadSnapshot = props.value(prefix + ".uri.downloadSnapshot");
        this.hostNameVerifierClassName = props.optionalValue(prefix + ".hostnameVerifierClassName");
    }
    
    public int connTimeoutMillis() {
        return this.connTimeoutMillis;
    }
    
    public int readTimeoutMillis() {
        return this.readTimeoutMillis;
    }
    
    public String masterURL() {
        return this.masterURL;
    }
    
    public String uriDownloadFile() {
        return this.uriDownloadFile;
    }
    
    public String uriDownloadSnapshot() {
        return this.uriDownloadSnapshot;
    }
    
    public String uriGetEvents() {
        return this.uriGetEvents;
    }
    
    public String uriTakeSnapshot() {
        return this.uriTakeSnapshot;
    }
    
    public String hostNameVerifierClassName() {
        return this.hostNameVerifierClassName;
    }
    
    public void makeImmutable() {
        this.mutable = false;
    }
    
    private void assertMutability() {
        if (!this.mutable) {
            throw new IllegalStateException("This HttpMasterStub object is Not mutable");
        }
    }
    
    public void setConnTimeoutMillis(final int connTimeoutMillis) {
        this.assertMutability();
        this.connTimeoutMillis = connTimeoutMillis;
    }
    
    public void setReadTimeoutMillis(final int readTimeoutMillis) {
        this.assertMutability();
        this.readTimeoutMillis = readTimeoutMillis;
    }
    
    public void setMasterURL(final String masterURL) {
        this.assertMutability();
        this.masterURL = masterURL;
    }
    
    public void setUriDownloadFile(final String uriDownloadFile) {
        this.assertMutability();
        this.uriDownloadFile = uriDownloadFile;
    }
    
    public void setUriDownloadSnapshot(final String uriDownloadSnapshot) {
        this.assertMutability();
        this.uriDownloadSnapshot = uriDownloadSnapshot;
    }
    
    public void setUriGetEvents(final String uriGetEvents) {
        this.assertMutability();
        this.uriGetEvents = uriGetEvents;
    }
    
    public void setUriTakeSnapshot(final String uriTakeSnapshot) {
        this.assertMutability();
        this.uriTakeSnapshot = uriTakeSnapshot;
    }
    
    public void setHostNameVerifierClassName(final String hostNameVerifierClassName) {
        this.assertMutability();
        this.hostNameVerifierClassName = hostNameVerifierClassName;
    }
}
