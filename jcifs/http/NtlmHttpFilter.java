package jcifs.http;

import javax.servlet.http.HttpSession;
import jcifs.smb.SmbAuthException;
import jcifs.util.Hexdump;
import jcifs.util.Base64;
import jcifs.UniAddress;
import jcifs.smb.SmbSession;
import jcifs.smb.NtlmChallenge;
import jcifs.smb.NtlmPasswordAuthentication;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.Enumeration;
import java.io.IOException;
import java.io.OutputStream;
import jcifs.Config;
import javax.servlet.FilterConfig;
import jcifs.util.LogStream;
import javax.servlet.Filter;

public class NtlmHttpFilter implements Filter
{
    private static LogStream log;
    private String defaultDomain;
    private String domainController;
    private boolean loadBalance;
    private boolean enableBasic;
    private boolean insecureBasic;
    private String realm;
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        Config.setProperty("jcifs.smb.client.soTimeout", "300000");
        Config.setProperty("jcifs.netbios.cachePolicy", "1200");
        final Enumeration e = filterConfig.getInitParameterNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            if (name.startsWith("jcifs.")) {
                Config.setProperty(name, filterConfig.getInitParameter(name));
            }
        }
        this.defaultDomain = Config.getProperty("jcifs.smb.client.domain");
        this.domainController = Config.getProperty("jcifs.http.domainController");
        if (this.domainController == null) {
            this.domainController = this.defaultDomain;
            this.loadBalance = Config.getBoolean("jcifs.http.loadBalance", true);
        }
        this.enableBasic = Boolean.valueOf(Config.getProperty("jcifs.http.enableBasic"));
        this.insecureBasic = Boolean.valueOf(Config.getProperty("jcifs.http.insecureBasic"));
        this.realm = Config.getProperty("jcifs.http.basicRealm");
        if (this.realm == null) {
            this.realm = "jCIFS";
        }
        final int level;
        if ((level = Config.getInt("jcifs.util.loglevel", -1)) != -1) {
            LogStream.setLevel(level);
        }
        final LogStream log = NtlmHttpFilter.log;
        if (LogStream.level > 2) {
            try {
                Config.store(NtlmHttpFilter.log, "JCIFS PROPERTIES");
            }
            catch (final IOException ex) {}
        }
    }
    
    public void destroy() {
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest)request;
        final HttpServletResponse resp = (HttpServletResponse)response;
        final NtlmPasswordAuthentication ntlm;
        if ((ntlm = this.negotiate(req, resp, false)) == null) {
            return;
        }
        chain.doFilter((ServletRequest)new NtlmHttpServletRequest(req, ntlm), response);
    }
    
    protected NtlmPasswordAuthentication negotiate(final HttpServletRequest req, final HttpServletResponse resp, final boolean skipAuthentication) throws IOException, ServletException {
        NtlmPasswordAuthentication ntlm = null;
        final String msg = req.getHeader("Authorization");
        final boolean offerBasic = this.enableBasic && (this.insecureBasic || req.isSecure());
        if (msg != null && (msg.startsWith("NTLM ") || (offerBasic && msg.startsWith("Basic ")))) {
            UniAddress dc;
            if (msg.startsWith("NTLM ")) {
                final HttpSession ssn = req.getSession();
                byte[] challenge;
                if (this.loadBalance) {
                    NtlmChallenge chal = (NtlmChallenge)ssn.getAttribute("NtlmHttpChal");
                    if (chal == null) {
                        chal = SmbSession.getChallengeForDomain();
                        ssn.setAttribute("NtlmHttpChal", (Object)chal);
                    }
                    dc = chal.dc;
                    challenge = chal.challenge;
                }
                else {
                    dc = UniAddress.getByName(this.domainController, true);
                    challenge = SmbSession.getChallenge(dc);
                }
                if ((ntlm = NtlmSsp.authenticate(req, resp, challenge)) == null) {
                    return null;
                }
                ssn.removeAttribute("NtlmHttpChal");
            }
            else {
                final String auth = new String(Base64.decode(msg.substring(6)), "US-ASCII");
                int index = auth.indexOf(58);
                String user = (index != -1) ? auth.substring(0, index) : auth;
                final String password = (index != -1) ? auth.substring(index + 1) : "";
                index = user.indexOf(92);
                if (index == -1) {
                    index = user.indexOf(47);
                }
                final String domain = (index != -1) ? user.substring(0, index) : this.defaultDomain;
                user = ((index != -1) ? user.substring(index + 1) : user);
                ntlm = new NtlmPasswordAuthentication(domain, user, password);
                dc = UniAddress.getByName(this.domainController, true);
            }
            try {
                SmbSession.logon(dc, ntlm);
                final LogStream log = NtlmHttpFilter.log;
                if (LogStream.level > 2) {
                    NtlmHttpFilter.log.println("NtlmHttpFilter: " + ntlm + " successfully authenticated against " + dc);
                }
            }
            catch (final SmbAuthException sae) {
                final LogStream log2 = NtlmHttpFilter.log;
                if (LogStream.level > 1) {
                    NtlmHttpFilter.log.println("NtlmHttpFilter: " + ntlm.getName() + ": 0x" + Hexdump.toHexString(sae.getNtStatus(), 8) + ": " + sae);
                }
                if (sae.getNtStatus() == -1073741819) {
                    final HttpSession ssn2 = req.getSession(false);
                    if (ssn2 != null) {
                        ssn2.removeAttribute("NtlmHttpAuth");
                    }
                }
                resp.setHeader("WWW-Authenticate", "NTLM");
                if (offerBasic) {
                    resp.addHeader("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
                }
                resp.setStatus(401);
                resp.setContentLength(0);
                resp.flushBuffer();
                return null;
            }
            req.getSession().setAttribute("NtlmHttpAuth", (Object)ntlm);
        }
        else if (!skipAuthentication) {
            final HttpSession ssn = req.getSession(false);
            if (ssn == null || (ntlm = (NtlmPasswordAuthentication)ssn.getAttribute("NtlmHttpAuth")) == null) {
                resp.setHeader("WWW-Authenticate", "NTLM");
                if (offerBasic) {
                    resp.addHeader("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
                }
                resp.setStatus(401);
                resp.setContentLength(0);
                resp.flushBuffer();
                return null;
            }
        }
        return ntlm;
    }
    
    public void setFilterConfig(final FilterConfig f) {
        try {
            this.init(f);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public FilterConfig getFilterConfig() {
        return null;
    }
    
    static {
        NtlmHttpFilter.log = LogStream.getInstance();
    }
}
