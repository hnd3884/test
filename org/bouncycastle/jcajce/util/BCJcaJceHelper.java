package org.bouncycastle.jcajce.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;
import java.security.Provider;

public class BCJcaJceHelper extends ProviderJcaJceHelper
{
    private static volatile Provider bcProvider;
    
    private static Provider getBouncyCastleProvider() {
        if (Security.getProvider("BC") != null) {
            return Security.getProvider("BC");
        }
        if (BCJcaJceHelper.bcProvider != null) {
            return BCJcaJceHelper.bcProvider;
        }
        return BCJcaJceHelper.bcProvider = new BouncyCastleProvider();
    }
    
    public BCJcaJceHelper() {
        super(getBouncyCastleProvider());
    }
}
