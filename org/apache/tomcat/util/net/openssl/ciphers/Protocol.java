package org.apache.tomcat.util.net.openssl.ciphers;

public enum Protocol
{
    SSLv3("SSLv3"), 
    SSLv2("SSLv2"), 
    TLSv1("TLSv1"), 
    TLSv1_2("TLSv1.2"), 
    TLSv1_3("TLSv1.3");
    
    private final String openSSLName;
    
    private Protocol(final String openSSLName) {
        this.openSSLName = openSSLName;
    }
    
    String getOpenSSLName() {
        return this.openSSLName;
    }
}
