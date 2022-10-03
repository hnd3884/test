package com.sun.corba.se.impl.naming.namingutil;

import java.util.ArrayList;
import java.util.StringTokenizer;
import com.sun.corba.se.impl.logging.NamingSystemException;

public class CorbalocURL extends INSURLBase
{
    static NamingSystemException wrapper;
    
    public CorbalocURL(final String s) {
        String cleanEscapes = s;
        if (cleanEscapes != null) {
            try {
                cleanEscapes = Utility.cleanEscapes(cleanEscapes);
            }
            catch (final Exception ex) {
                this.badAddress(ex);
            }
            int n = cleanEscapes.indexOf(47);
            if (n == -1) {
                n = cleanEscapes.length();
            }
            if (n == 0) {
                this.badAddress(null);
            }
            final StringTokenizer stringTokenizer = new StringTokenizer(cleanEscapes.substring(0, n), ",");
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                IIOPEndpointInfo iiopEndpointInfo = null;
                if (nextToken.startsWith("iiop:")) {
                    iiopEndpointInfo = this.handleIIOPColon(nextToken);
                }
                else if (nextToken.startsWith("rir:")) {
                    this.handleRIRColon(nextToken);
                    this.rirFlag = true;
                }
                else if (nextToken.startsWith(":")) {
                    iiopEndpointInfo = this.handleColon(nextToken);
                }
                else {
                    this.badAddress(null);
                }
                if (!this.rirFlag) {
                    if (this.theEndpointInfo == null) {
                        this.theEndpointInfo = new ArrayList();
                    }
                    this.theEndpointInfo.add(iiopEndpointInfo);
                }
            }
            if (cleanEscapes.length() > n + 1) {
                this.theKeyString = cleanEscapes.substring(n + 1);
            }
        }
    }
    
    private void badAddress(final Throwable t) {
        throw CorbalocURL.wrapper.insBadAddress(t);
    }
    
    private IIOPEndpointInfo handleIIOPColon(String substring) {
        substring = substring.substring(4);
        return this.handleColon(substring);
    }
    
    private IIOPEndpointInfo handleColon(String s) {
        String host;
        s = (host = s.substring(1));
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "@");
        final IIOPEndpointInfo iiopEndpointInfo = new IIOPEndpointInfo();
        final int countTokens = stringTokenizer.countTokens();
        if (countTokens == 0 || countTokens > 2) {
            this.badAddress(null);
        }
        if (countTokens == 2) {
            final String nextToken = stringTokenizer.nextToken();
            final int index = nextToken.indexOf(46);
            if (index == -1) {
                this.badAddress(null);
            }
            try {
                iiopEndpointInfo.setVersion(Integer.parseInt(nextToken.substring(0, index)), Integer.parseInt(nextToken.substring(index + 1)));
                host = stringTokenizer.nextToken();
            }
            catch (final Throwable t) {
                this.badAddress(t);
            }
        }
        try {
            if (host.indexOf(91) != -1) {
                final String ipv6Port = this.getIPV6Port(host);
                if (ipv6Port != null) {
                    iiopEndpointInfo.setPort(Integer.parseInt(ipv6Port));
                }
                iiopEndpointInfo.setHost(this.getIPV6Host(host));
                return iiopEndpointInfo;
            }
            final StringTokenizer stringTokenizer2 = new StringTokenizer(host, ":");
            if (stringTokenizer2.countTokens() == 2) {
                iiopEndpointInfo.setHost(stringTokenizer2.nextToken());
                iiopEndpointInfo.setPort(Integer.parseInt(stringTokenizer2.nextToken()));
            }
            else if (host != null && host.length() != 0) {
                iiopEndpointInfo.setHost(host);
            }
        }
        catch (final Throwable t2) {
            this.badAddress(t2);
        }
        Utility.validateGIOPVersion(iiopEndpointInfo);
        return iiopEndpointInfo;
    }
    
    private void handleRIRColon(final String s) {
        if (s.length() != 4) {
            this.badAddress(null);
        }
    }
    
    private String getIPV6Port(final String s) {
        final int index = s.indexOf(93);
        if (index + 1 == s.length()) {
            return null;
        }
        if (s.charAt(index + 1) != ':') {
            throw new RuntimeException("Host and Port is not separated by ':'");
        }
        return s.substring(index + 2);
    }
    
    private String getIPV6Host(final String s) {
        return s.substring(1, s.indexOf(93));
    }
    
    @Override
    public boolean isCorbanameURL() {
        return false;
    }
    
    static {
        CorbalocURL.wrapper = NamingSystemException.get("naming.read");
    }
}
