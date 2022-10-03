package com.sun.corba.se.impl.naming.namingutil;

import java.util.ArrayList;
import org.omg.CORBA.BAD_PARAM;
import com.sun.corba.se.impl.logging.NamingSystemException;

public class CorbanameURL extends INSURLBase
{
    private static NamingSystemException wrapper;
    
    public CorbanameURL(final String s) {
        String cleanEscapes = s;
        try {
            cleanEscapes = Utility.cleanEscapes(cleanEscapes);
        }
        catch (final Exception ex) {
            this.badAddress(ex);
        }
        final int index = cleanEscapes.indexOf(35);
        String s2;
        if (index != -1) {
            s2 = "corbaloc:" + cleanEscapes.substring(0, index) + "/";
        }
        else {
            s2 = "corbaloc:" + cleanEscapes.substring(0, cleanEscapes.length());
            if (!s2.endsWith("/")) {
                s2 += "/";
            }
        }
        try {
            this.copyINSURL(INSURLHandler.getINSURLHandler().parseURL(s2));
            if (index > -1 && index < s.length() - 1) {
                this.theStringifiedName = cleanEscapes.substring(index + 1);
            }
        }
        catch (final Exception ex2) {
            this.badAddress(ex2);
        }
    }
    
    private void badAddress(final Throwable t) throws BAD_PARAM {
        throw CorbanameURL.wrapper.insBadAddress(t);
    }
    
    private void copyINSURL(final INSURL insurl) {
        this.rirFlag = insurl.getRIRFlag();
        this.theEndpointInfo = (ArrayList)insurl.getEndpointInfo();
        this.theKeyString = insurl.getKeyString();
        this.theStringifiedName = insurl.getStringifiedName();
    }
    
    @Override
    public boolean isCorbanameURL() {
        return true;
    }
    
    static {
        CorbanameURL.wrapper = NamingSystemException.get("naming");
    }
}
