package org.apache.commons.net.smtp;

import java.util.Enumeration;
import java.util.Vector;

public final class RelayPath
{
    Vector<String> _path;
    String _emailAddress;
    
    public RelayPath(final String emailAddress) {
        this._path = new Vector<String>();
        this._emailAddress = emailAddress;
    }
    
    public void addRelay(final String hostname) {
        this._path.addElement(hostname);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append('<');
        final Enumeration<String> hosts = this._path.elements();
        if (hosts.hasMoreElements()) {
            buffer.append('@');
            buffer.append(hosts.nextElement());
            while (hosts.hasMoreElements()) {
                buffer.append(",@");
                buffer.append(hosts.nextElement());
            }
            buffer.append(':');
        }
        buffer.append(this._emailAddress);
        buffer.append('>');
        return buffer.toString();
    }
}
