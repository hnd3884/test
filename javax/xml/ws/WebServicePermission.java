package javax.xml.ws;

import java.security.BasicPermission;

public final class WebServicePermission extends BasicPermission
{
    private static final long serialVersionUID = -146474640053770988L;
    
    public WebServicePermission(final String name) {
        super(name);
    }
    
    public WebServicePermission(final String name, final String actions) {
        super(name, actions);
    }
}
