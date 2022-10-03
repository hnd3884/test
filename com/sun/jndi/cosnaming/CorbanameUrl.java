package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import javax.naming.NamingException;
import javax.naming.Name;

public final class CorbanameUrl
{
    private String stringName;
    private String location;
    
    public String getStringName() {
        return this.stringName;
    }
    
    public Name getCosName() throws NamingException {
        return CNCtx.parser.parse(this.stringName);
    }
    
    public String getLocation() {
        return "corbaloc:" + this.location;
    }
    
    public CorbanameUrl(final String s) throws MalformedURLException {
        if (!s.startsWith("corbaname:")) {
            throw new MalformedURLException("Invalid corbaname URL: " + s);
        }
        final int n = 10;
        int n2 = s.indexOf(35, n);
        if (n2 < 0) {
            n2 = s.length();
            this.stringName = "";
        }
        else {
            this.stringName = UrlUtil.decode(s.substring(n2 + 1));
        }
        this.location = s.substring(n, n2);
        final int index = this.location.indexOf("/");
        if (index >= 0) {
            if (index == this.location.length() - 1) {
                this.location += "NameService";
            }
        }
        else {
            this.location += "/NameService";
        }
    }
}
