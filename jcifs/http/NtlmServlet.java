package jcifs.http;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import jcifs.smb.SmbAuthException;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.util.Base64;
import jcifs.smb.SmbSession;
import jcifs.UniAddress;
import jcifs.netbios.NbtAddress;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.Enumeration;
import jcifs.Config;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

public abstract class NtlmServlet extends HttpServlet
{
    private String defaultDomain;
    private String domainController;
    private boolean loadBalance;
    private boolean enableBasic;
    private boolean insecureBasic;
    private String realm;
    
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        Config.setProperty("jcifs.smb.client.soTimeout", "300000");
        Config.setProperty("jcifs.netbios.cachePolicy", "600");
        final Enumeration e = config.getInitParameterNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            if (name.startsWith("jcifs.")) {
                Config.setProperty(name, config.getInitParameter(name));
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
    }
    
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final boolean offerBasic = this.enableBasic && (this.insecureBasic || request.isSecure());
        final String msg = request.getHeader("Authorization");
        if (msg != null && (msg.startsWith("NTLM ") || (offerBasic && msg.startsWith("Basic ")))) {
            UniAddress dc;
            if (this.loadBalance) {
                dc = new UniAddress(NbtAddress.getByName(this.domainController, 28, null));
            }
            else {
                dc = UniAddress.getByName(this.domainController, true);
            }
            NtlmPasswordAuthentication ntlm;
            if (msg.startsWith("NTLM ")) {
                final byte[] challenge = SmbSession.getChallenge(dc);
                ntlm = NtlmSsp.authenticate(request, response, challenge);
                if (ntlm == null) {
                    return;
                }
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
            }
            try {
                SmbSession.logon(dc, ntlm);
            }
            catch (final SmbAuthException sae) {
                response.setHeader("WWW-Authenticate", "NTLM");
                if (offerBasic) {
                    response.addHeader("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
                }
                response.setHeader("Connection", "close");
                response.setStatus(401);
                response.flushBuffer();
                return;
            }
            final HttpSession ssn = request.getSession();
            ssn.setAttribute("NtlmHttpAuth", (Object)ntlm);
            ssn.setAttribute("ntlmdomain", (Object)ntlm.getDomain());
            ssn.setAttribute("ntlmuser", (Object)ntlm.getUsername());
        }
        else {
            final HttpSession ssn2 = request.getSession(false);
            if (ssn2 == null || ssn2.getAttribute("NtlmHttpAuth") == null) {
                response.setHeader("WWW-Authenticate", "NTLM");
                if (offerBasic) {
                    response.addHeader("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
                }
                response.setStatus(401);
                response.flushBuffer();
                return;
            }
        }
        super.service(request, response);
    }
}
