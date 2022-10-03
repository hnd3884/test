package net.sf.jsqlparser.schema;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Server implements MultiPartName
{
    public static final Pattern SERVER_PATTERN;
    private String serverName;
    private String instanceName;
    private String simpleName;
    
    public Server(final String serverAndInstanceName) {
        if (serverAndInstanceName != null) {
            final Matcher matcher = Server.SERVER_PATTERN.matcher(serverAndInstanceName);
            if (!matcher.find()) {
                this.simpleName = serverAndInstanceName;
            }
            else {
                this.setServerName(matcher.group(1));
                this.setInstanceName(matcher.group(2));
            }
        }
    }
    
    public Server(final String serverName, final String instanceName) {
        this.setServerName(serverName);
        this.setInstanceName(instanceName);
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    public String getInstanceName() {
        return this.instanceName;
    }
    
    public void setInstanceName(final String instanceName) {
        this.instanceName = instanceName;
    }
    
    @Override
    public String getFullyQualifiedName() {
        if (this.serverName != null && !this.serverName.isEmpty() && this.instanceName != null && !this.instanceName.isEmpty()) {
            return String.format("[%s\\%s]", this.serverName, this.instanceName);
        }
        if (this.serverName != null && !this.serverName.isEmpty()) {
            return String.format("[%s]", this.serverName);
        }
        if (this.simpleName != null && !this.simpleName.isEmpty()) {
            return this.simpleName;
        }
        return "";
    }
    
    @Override
    public String toString() {
        return this.getFullyQualifiedName();
    }
    
    static {
        SERVER_PATTERN = Pattern.compile("\\[([^\\]]+?)(?:\\\\([^\\]]+))?\\]");
    }
}
