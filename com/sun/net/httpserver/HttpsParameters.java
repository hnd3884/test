package com.sun.net.httpserver;

import javax.net.ssl.SSLParameters;
import java.net.InetSocketAddress;
import jdk.Exported;

@Exported
public abstract class HttpsParameters
{
    private String[] cipherSuites;
    private String[] protocols;
    private boolean wantClientAuth;
    private boolean needClientAuth;
    
    protected HttpsParameters() {
    }
    
    public abstract HttpsConfigurator getHttpsConfigurator();
    
    public abstract InetSocketAddress getClientAddress();
    
    public abstract void setSSLParameters(final SSLParameters p0);
    
    public String[] getCipherSuites() {
        return (String[])((this.cipherSuites != null) ? ((String[])this.cipherSuites.clone()) : null);
    }
    
    public void setCipherSuites(final String[] array) {
        this.cipherSuites = (String[])((array != null) ? ((String[])array.clone()) : null);
    }
    
    public String[] getProtocols() {
        return (String[])((this.protocols != null) ? ((String[])this.protocols.clone()) : null);
    }
    
    public void setProtocols(final String[] array) {
        this.protocols = (String[])((array != null) ? ((String[])array.clone()) : null);
    }
    
    public boolean getWantClientAuth() {
        return this.wantClientAuth;
    }
    
    public void setWantClientAuth(final boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
    }
    
    public boolean getNeedClientAuth() {
        return this.needClientAuth;
    }
    
    public void setNeedClientAuth(final boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }
}
