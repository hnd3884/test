package HTTPClient;

class DefaultCookiePolicyHandler implements CookiePolicyHandler
{
    private String[] accept_domains;
    private String[] reject_domains;
    private BasicCookieBox popup;
    
    DefaultCookiePolicyHandler() {
        this.accept_domains = new String[0];
        this.reject_domains = new String[0];
        String list;
        try {
            list = System.getProperty("HTTPClient.cookies.hosts.accept");
        }
        catch (final Exception ex) {
            list = null;
        }
        String[] domains = Util.splitProperty(list);
        for (int idx = 0; idx < domains.length; ++idx) {
            this.addAcceptDomain(domains[idx].toLowerCase());
        }
        try {
            list = System.getProperty("HTTPClient.cookies.hosts.reject");
        }
        catch (final Exception ex2) {
            list = null;
        }
        domains = Util.splitProperty(list);
        for (int idx2 = 0; idx2 < domains.length; ++idx2) {
            this.addRejectDomain(domains[idx2].toLowerCase());
        }
    }
    
    public boolean acceptCookie(final Cookie cookie, final RoRequest req, final RoResponse resp) {
        String server = req.getConnection().getHost();
        if (server.indexOf(46) == -1) {
            server = String.valueOf(server) + ".local";
        }
        for (int idx = 0; idx < this.reject_domains.length; ++idx) {
            if (this.reject_domains[idx].length() == 0 || (this.reject_domains[idx].charAt(0) == '.' && server.endsWith(this.reject_domains[idx])) || (this.reject_domains[idx].charAt(0) != '.' && server.equals(this.reject_domains[idx]))) {
                return false;
            }
        }
        for (int idx2 = 0; idx2 < this.accept_domains.length; ++idx2) {
            if (this.accept_domains[idx2].length() == 0 || (this.accept_domains[idx2].charAt(0) == '.' && server.endsWith(this.accept_domains[idx2])) || (this.accept_domains[idx2].charAt(0) != '.' && server.equals(this.accept_domains[idx2]))) {
                return true;
            }
        }
        if (!req.allowUI()) {
            return true;
        }
        if (this.popup == null) {
            this.popup = new BasicCookieBox();
        }
        return this.popup.accept(cookie, this, server);
    }
    
    public boolean sendCookie(final Cookie cookie, final RoRequest req) {
        return true;
    }
    
    void addAcceptDomain(String domain) {
        if (domain.indexOf(46) == -1 && domain.length() > 0) {
            domain = String.valueOf(domain) + ".local";
        }
        for (int idx = 0; idx < this.accept_domains.length; ++idx) {
            if (domain.endsWith(this.accept_domains[idx])) {
                return;
            }
            if (this.accept_domains[idx].endsWith(domain)) {
                this.accept_domains[idx] = domain;
                return;
            }
        }
        (this.accept_domains = Util.resizeArray(this.accept_domains, this.accept_domains.length + 1))[this.accept_domains.length - 1] = domain;
    }
    
    void addRejectDomain(String domain) {
        if (domain.indexOf(46) == -1 && domain.length() > 0) {
            domain = String.valueOf(domain) + ".local";
        }
        for (int idx = 0; idx < this.reject_domains.length; ++idx) {
            if (domain.endsWith(this.reject_domains[idx])) {
                return;
            }
            if (this.reject_domains[idx].endsWith(domain)) {
                this.reject_domains[idx] = domain;
                return;
            }
        }
        (this.reject_domains = Util.resizeArray(this.reject_domains, this.reject_domains.length + 1))[this.reject_domains.length - 1] = domain;
    }
}
