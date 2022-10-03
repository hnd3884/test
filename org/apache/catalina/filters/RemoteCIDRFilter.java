package org.apache.catalina.filters;

import java.util.LinkedList;
import java.util.Collections;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.net.InetAddress;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.juli.logging.LogFactory;
import org.apache.catalina.util.NetMask;
import java.util.List;
import org.apache.juli.logging.Log;

public final class RemoteCIDRFilter extends FilterBase
{
    private static final String PLAIN_TEXT_MIME_TYPE = "text/plain";
    private final Log log;
    private final List<NetMask> allow;
    private final List<NetMask> deny;
    
    public RemoteCIDRFilter() {
        this.log = LogFactory.getLog((Class)RemoteCIDRFilter.class);
        this.allow = new ArrayList<NetMask>();
        this.deny = new ArrayList<NetMask>();
    }
    
    public String getAllow() {
        return this.allow.toString().replace("[", "").replace("]", "");
    }
    
    public void setAllow(final String input) {
        final List<String> messages = this.fillFromInput(input, this.allow);
        if (messages.isEmpty()) {
            return;
        }
        for (final String message : messages) {
            this.log.error((Object)message);
        }
        throw new IllegalArgumentException(RemoteCIDRFilter.sm.getString("remoteCidrFilter.invalid", new Object[] { "allow" }));
    }
    
    public String getDeny() {
        return this.deny.toString().replace("[", "").replace("]", "");
    }
    
    public void setDeny(final String input) {
        final List<String> messages = this.fillFromInput(input, this.deny);
        if (messages.isEmpty()) {
            return;
        }
        for (final String message : messages) {
            this.log.error((Object)message);
        }
        throw new IllegalArgumentException(RemoteCIDRFilter.sm.getString("remoteCidrFilter.invalid", new Object[] { "deny" }));
    }
    
    @Override
    protected boolean isConfigProblemFatal() {
        return true;
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (this.isAllowed(request.getRemoteAddr())) {
            chain.doFilter(request, response);
            return;
        }
        if (!(response instanceof HttpServletResponse)) {
            this.sendErrorWhenNotHttp(response);
            return;
        }
        ((HttpServletResponse)response).sendError(403);
    }
    
    public Log getLogger() {
        return this.log;
    }
    
    private boolean isAllowed(final String property) {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(property);
        }
        catch (final UnknownHostException e) {
            this.log.error((Object)RemoteCIDRFilter.sm.getString("remoteCidrFilter.noRemoteIp"), (Throwable)e);
            return false;
        }
        for (final NetMask nm : this.deny) {
            if (nm.matches(addr)) {
                return false;
            }
        }
        for (final NetMask nm : this.allow) {
            if (nm.matches(addr)) {
                return true;
            }
        }
        return !this.deny.isEmpty() && this.allow.isEmpty();
    }
    
    private void sendErrorWhenNotHttp(final ServletResponse response) throws IOException {
        final PrintWriter writer = response.getWriter();
        response.setContentType("text/plain");
        writer.write(RemoteCIDRFilter.sm.getString("http.403"));
        writer.flush();
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
}
