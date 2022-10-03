package ar.com.fernandospr.wns;

public class WnsProxyProperties
{
    private String protocol;
    private String host;
    private int port;
    private String user;
    private String pass;
    
    public WnsProxyProperties(final String protocol, final String host, final int port, final String user, final String pass) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getPass() {
        return this.pass;
    }
}
