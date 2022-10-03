package HTTPClient;

import java.io.IOException;
import java.util.StringTokenizer;

public class NTLMAuthorizationHandler implements AuthorizationHandler
{
    private DefaultAuthHandler defaultAuthHandler;
    private static final String NTLM_TAG = "NTLM";
    private static final String PROXY_AUTHENTICATE_HEADER = "Proxy-Authenticate";
    private static final String WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";
    private static AuthorizationPrompter prompter;
    private static boolean prompterSet;
    private String host;
    private String hostDomain;
    
    public NTLMAuthorizationHandler() {
        this.defaultAuthHandler = null;
        this.host = null;
        this.hostDomain = null;
        this.defaultAuthHandler = new DefaultAuthHandler();
        this.host = System.getProperty("HTTPClient.host");
        this.hostDomain = System.getProperty("HTTPClient.hostDomain");
    }
    
    @Override
    public AuthorizationInfo getAuthorization(final AuthorizationInfo arg0, final RoRequest arg1, final RoResponse arg2) throws AuthSchemeNotImplException, IOException {
        if (!arg0.getScheme().equalsIgnoreCase("NTLM")) {
            if (!arg0.getScheme().equalsIgnoreCase("Negotiate")) {
                return this.defaultAuthHandler.getAuthorization(arg0, arg1, arg2);
            }
        }
        try {
            byte[] nonce = null;
            final String ntlmchallenge = arg2.getHeader("nonce");
            if (ntlmchallenge != null) {
                try {
                    nonce = NTUtil.getNonce(Codecs.base64Decode(ntlmchallenge.getBytes()));
                }
                catch (final ArrayIndexOutOfBoundsException exp) {
                    nonce = NTUtil.getNonce(ntlmchallenge.getBytes());
                }
            }
            else {
                nonce = null;
            }
            String msg;
            if (nonce != null) {
                final NVPair answer;
                synchronized (this.getClass()) {
                    if (!arg1.allowUI() || (NTLMAuthorizationHandler.prompterSet && NTLMAuthorizationHandler.prompter == null)) {
                        return null;
                    }
                    if (NTLMAuthorizationHandler.prompter == null) {
                        setDefaultPrompter();
                    }
                    answer = NTLMAuthorizationHandler.prompter.getUsernamePassword(arg0, arg2.getStatusCode() == 407);
                }
                if (answer == null) {
                    return null;
                }
                String userDomain = "";
                String username = answer.getName();
                final String password = answer.getValue();
                final StringTokenizer tokens = new StringTokenizer(username, "\\");
                if (tokens.hasMoreElements()) {
                    userDomain = tokens.nextToken();
                }
                if (tokens.hasMoreElements()) {
                    username = tokens.nextToken();
                }
                msg = new String(Codecs.base64Encode(NTUtil.buildResponse(this.host, username, userDomain, NTUtil.getLanManagerPassword(password), NTUtil.getNTHashedPassword(password), nonce)));
            }
            else {
                msg = new String(Codecs.base64Encode(NTUtil.buildRequest(this.host, this.hostDomain)));
            }
            return new AuthorizationInfo(arg0.getHost(), arg0.getPort(), "NTLM", "", msg);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new IOException();
        }
        return this.defaultAuthHandler.getAuthorization(arg0, arg1, arg2);
    }
    
    @Override
    public AuthorizationInfo fixupAuthInfo(final AuthorizationInfo arg0, final RoRequest arg1, final AuthorizationInfo arg2, final RoResponse arg3) throws AuthSchemeNotImplException, IOException {
        if (arg0.getScheme().equalsIgnoreCase("NTLM")) {
            return arg0;
        }
        return this.defaultAuthHandler.fixupAuthInfo(arg0, arg1, arg2, arg3);
    }
    
    @Override
    public void handleAuthHeaders(final Response arg0, final RoRequest arg1, final AuthorizationInfo arg2, final AuthorizationInfo arg3) throws IOException {
        final byte[] nonce = null;
        String ntlmchallenge = null;
        try {
            String challenge = arg0.getHeader("Proxy-Authenticate");
            if (challenge != null && challenge.startsWith("NTLM") && challenge.length() > 7) {
                ntlmchallenge = challenge.substring(challenge.indexOf(32) + 1).trim();
            }
            else {
                challenge = arg0.getHeader("WWW-Authenticate");
                if (challenge != null && challenge.startsWith("NTLM") && challenge.length() > 7) {
                    ntlmchallenge = challenge.substring(challenge.indexOf(32) + 1).trim();
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        if (ntlmchallenge != null) {
            arg0.setHeader("nonce", ntlmchallenge);
        }
        this.defaultAuthHandler.handleAuthHeaders(arg0, arg1, arg2, arg3);
    }
    
    @Override
    public void handleAuthTrailers(final Response arg0, final RoRequest arg1, final AuthorizationInfo arg2, final AuthorizationInfo arg3) throws IOException {
        if (arg2.getScheme().equalsIgnoreCase("NTLM")) {
            return;
        }
        this.defaultAuthHandler.handleAuthTrailers(arg0, arg1, arg2, arg3);
    }
    
    public static void main(final String[] args) {
    }
    
    public static synchronized AuthorizationPrompter setAuthorizationPrompter(final AuthorizationPrompter prompt) {
        final AuthorizationPrompter prev = NTLMAuthorizationHandler.prompter;
        NTLMAuthorizationHandler.prompter = prompt;
        NTLMAuthorizationHandler.prompterSet = true;
        return prev;
    }
    
    private static void setDefaultPrompter() {
        if (!SimpleAuthPrompt.canUseCLPrompt() || isAWTRunning()) {
            NTLMAuthorizationHandler.prompter = new SimpleAuthPopup();
        }
        else {
            NTLMAuthorizationHandler.prompter = new SimpleAuthPrompt();
        }
    }
    
    private static final boolean isAWTRunning() {
        ThreadGroup root;
        for (root = Thread.currentThread().getThreadGroup(); root.getParent() != null; root = root.getParent()) {}
        final Thread[] t_list = new Thread[root.activeCount() + 5];
        for (int t_num = root.enumerate(t_list), idx = 0; idx < t_num; ++idx) {
            if (t_list[idx].getName().startsWith("AWT-")) {
                return true;
            }
        }
        return false;
    }
    
    static {
        NTLMAuthorizationHandler.prompter = null;
        NTLMAuthorizationHandler.prompterSet = false;
    }
}
