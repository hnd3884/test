package org.apache.catalina.valves;

import org.apache.juli.logging.LogFactory;
import java.util.LinkedList;
import java.util.Collections;
import java.net.UnknownHostException;
import java.net.InetAddress;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.catalina.util.NetMask;
import java.util.List;
import org.apache.juli.logging.Log;

public final class RemoteCIDRValve extends RequestFilterValve
{
    private static final Log log;
    private final List<NetMask> allow;
    private final List<NetMask> deny;
    
    public RemoteCIDRValve() {
        this.allow = new ArrayList<NetMask>();
        this.deny = new ArrayList<NetMask>();
    }
    
    @Override
    public String getAllow() {
        return this.allow.toString().replace("[", "").replace("]", "");
    }
    
    @Override
    public void setAllow(final String input) {
        final List<String> messages = this.fillFromInput(input, this.allow);
        if (messages.isEmpty()) {
            return;
        }
        this.allowValid = false;
        for (final String message : messages) {
            RemoteCIDRValve.log.error((Object)message);
        }
        throw new IllegalArgumentException(RemoteCIDRValve.sm.getString("remoteCidrValve.invalid", new Object[] { "allow" }));
    }
    
    @Override
    public String getDeny() {
        return this.deny.toString().replace("[", "").replace("]", "");
    }
    
    @Override
    public void setDeny(final String input) {
        final List<String> messages = this.fillFromInput(input, this.deny);
        if (messages.isEmpty()) {
            return;
        }
        this.denyValid = false;
        for (final String message : messages) {
            RemoteCIDRValve.log.error((Object)message);
        }
        throw new IllegalArgumentException(RemoteCIDRValve.sm.getString("remoteCidrValve.invalid", new Object[] { "deny" }));
    }
    
    @Override
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        String property;
        if (this.getUsePeerAddress()) {
            property = request.getPeerAddr();
        }
        else {
            property = request.getRequest().getRemoteAddr();
        }
        if (this.getAddConnectorPort()) {
            property = property + ";" + request.getConnector().getPort();
        }
        this.process(property, request, response);
    }
    
    @Override
    public boolean isAllowed(final String property) {
        final int portIdx = property.indexOf(59);
        int port;
        String nonPortPart;
        if (portIdx == -1) {
            if (this.getAddConnectorPort()) {
                RemoteCIDRValve.log.error((Object)RemoteCIDRValve.sm.getString("remoteCidrValve.noPort"));
                return false;
            }
            port = -1;
            nonPortPart = property;
        }
        else {
            if (!this.getAddConnectorPort()) {
                RemoteCIDRValve.log.error((Object)RemoteCIDRValve.sm.getString("remoteCidrValve.unexpectedPort"));
                return false;
            }
            nonPortPart = property.substring(0, portIdx);
            try {
                port = Integer.parseInt(property.substring(portIdx + 1));
            }
            catch (final NumberFormatException e) {
                RemoteCIDRValve.log.error((Object)RemoteCIDRValve.sm.getString("remoteCidrValve.noPort"), (Throwable)e);
                return false;
            }
        }
        InetAddress addr;
        try {
            addr = InetAddress.getByName(nonPortPart);
        }
        catch (final UnknownHostException e2) {
            RemoteCIDRValve.log.error((Object)RemoteCIDRValve.sm.getString("remoteCidrValve.noRemoteIp"), (Throwable)e2);
            return false;
        }
        for (final NetMask nm : this.deny) {
            if (this.getAddConnectorPort()) {
                if (nm.matches(addr, port)) {
                    return false;
                }
                continue;
            }
            else {
                if (nm.matches(addr)) {
                    return false;
                }
                continue;
            }
        }
        for (final NetMask nm : this.allow) {
            if (this.getAddConnectorPort()) {
                if (nm.matches(addr, port)) {
                    return true;
                }
                continue;
            }
            else {
                if (nm.matches(addr)) {
                    return true;
                }
                continue;
            }
        }
        return !this.deny.isEmpty() && this.allow.isEmpty();
    }
    
    @Override
    protected Log getLog() {
        return RemoteCIDRValve.log;
    }
    
    private List<String> fillFromInput(final String input, final List<NetMask> target) {
        target.clear();
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }
        final List<String> messages = new LinkedList<String>();
        for (final String s : input.split("\\s*,\\s*")) {
            try {
                final NetMask nm = new NetMask(s);
                target.add(nm);
            }
            catch (final IllegalArgumentException e) {
                messages.add(s + ": " + e.getMessage());
            }
        }
        return Collections.unmodifiableList((List<? extends String>)messages);
    }
    
    static {
        log = LogFactory.getLog((Class)RemoteCIDRValve.class);
    }
}
