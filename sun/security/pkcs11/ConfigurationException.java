package sun.security.pkcs11;

import java.io.IOException;

class ConfigurationException extends IOException
{
    private static final long serialVersionUID = 254492758807673194L;
    
    ConfigurationException(final String s) {
        super(s);
    }
}
