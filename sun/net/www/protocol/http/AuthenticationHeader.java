package sun.net.www.protocol.http;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
import java.util.HashMap;
import sun.net.www.HeaderParser;
import sun.net.www.MessageHeader;

public class AuthenticationHeader
{
    MessageHeader rsp;
    HeaderParser preferred;
    String preferred_r;
    private final HttpCallerInfo hci;
    boolean dontUseNegotiate;
    static String authPref;
    String hdrname;
    HashMap<String, SchemeMapValue> schemes;
    
    @Override
    public String toString() {
        return "AuthenticationHeader: prefer " + this.preferred_r;
    }
    
    public AuthenticationHeader(final String s, final MessageHeader messageHeader, final HttpCallerInfo httpCallerInfo, final boolean b) {
        this(s, messageHeader, httpCallerInfo, b, Collections.emptySet());
    }
    
    public AuthenticationHeader(final String hdrname, final MessageHeader rsp, final HttpCallerInfo hci, final boolean dontUseNegotiate, final Set<String> set) {
        this.dontUseNegotiate = false;
        this.hci = hci;
        this.dontUseNegotiate = dontUseNegotiate;
        this.rsp = rsp;
        this.hdrname = hdrname;
        this.schemes = new HashMap<String, SchemeMapValue>();
        this.parse(set);
    }
    
    public HttpCallerInfo getHttpCallerInfo() {
        return this.hci;
    }
    
    private void parse(final Set<String> set) {
        final Iterator<String> multiValueIterator = this.rsp.multiValueIterator(this.hdrname);
        while (multiValueIterator.hasNext()) {
            final String s = multiValueIterator.next();
            final HeaderParser headerParser = new HeaderParser(s);
            final Iterator<String> keys = headerParser.keys();
            int n = 0;
            int n2 = -1;
            while (keys.hasNext()) {
                keys.next();
                if (headerParser.findValue(n) == null) {
                    if (n2 != -1) {
                        final HeaderParser subsequence = headerParser.subsequence(n2, n);
                        final String key = subsequence.findKey(0);
                        if (!set.contains(key)) {
                            this.schemes.put(key, new SchemeMapValue(subsequence, s));
                        }
                    }
                    n2 = n;
                }
                ++n;
            }
            if (n > n2) {
                final HeaderParser subsequence2 = headerParser.subsequence(n2, n);
                final String key2 = subsequence2.findKey(0);
                if (set.contains(key2)) {
                    continue;
                }
                this.schemes.put(key2, new SchemeMapValue(subsequence2, s));
            }
        }
        SchemeMapValue schemeMapValue = null;
        if (AuthenticationHeader.authPref == null || (schemeMapValue = this.schemes.get(AuthenticationHeader.authPref)) == null) {
            if (schemeMapValue == null && !this.dontUseNegotiate) {
                SchemeMapValue schemeMapValue2 = this.schemes.get("negotiate");
                if (schemeMapValue2 != null) {
                    if (this.hci == null || !NegotiateAuthentication.isSupported(new HttpCallerInfo(this.hci, "Negotiate"))) {
                        schemeMapValue2 = null;
                    }
                    schemeMapValue = schemeMapValue2;
                }
            }
            if (schemeMapValue == null && !this.dontUseNegotiate) {
                SchemeMapValue schemeMapValue3 = this.schemes.get("kerberos");
                if (schemeMapValue3 != null) {
                    if (this.hci == null || !NegotiateAuthentication.isSupported(new HttpCallerInfo(this.hci, "Kerberos"))) {
                        schemeMapValue3 = null;
                    }
                    schemeMapValue = schemeMapValue3;
                }
            }
            if (schemeMapValue == null && (schemeMapValue = this.schemes.get("digest")) == null && (!NTLMAuthenticationProxy.supported || (schemeMapValue = this.schemes.get("ntlm")) == null)) {
                schemeMapValue = this.schemes.get("basic");
            }
        }
        else if (this.dontUseNegotiate && AuthenticationHeader.authPref.equals("negotiate")) {
            schemeMapValue = null;
        }
        if (schemeMapValue != null) {
            this.preferred = schemeMapValue.parser;
            this.preferred_r = schemeMapValue.raw;
        }
    }
    
    public HeaderParser headerParser() {
        return this.preferred;
    }
    
    public String scheme() {
        if (this.preferred != null) {
            return this.preferred.findKey(0);
        }
        return null;
    }
    
    public String raw() {
        return this.preferred_r;
    }
    
    public boolean isPresent() {
        return this.preferred != null;
    }
    
    static {
        AuthenticationHeader.authPref = null;
        AuthenticationHeader.authPref = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("http.auth.preference"));
        if (AuthenticationHeader.authPref != null) {
            AuthenticationHeader.authPref = AuthenticationHeader.authPref.toLowerCase();
            if (AuthenticationHeader.authPref.equals("spnego") || AuthenticationHeader.authPref.equals("kerberos")) {
                AuthenticationHeader.authPref = "negotiate";
            }
        }
    }
    
    static class SchemeMapValue
    {
        String raw;
        HeaderParser parser;
        
        SchemeMapValue(final HeaderParser parser, final String raw) {
            this.raw = raw;
            this.parser = parser;
        }
    }
}
