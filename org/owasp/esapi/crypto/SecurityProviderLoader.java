package org.owasp.esapi.crypto;

import org.owasp.esapi.ESAPI;
import java.security.Security;
import java.security.Provider;
import java.security.NoSuchProviderException;
import java.util.Hashtable;
import org.owasp.esapi.Logger;

public class SecurityProviderLoader
{
    private static Logger logger;
    private static Hashtable<String, String> jceProviders;
    
    public static int insertProviderAt(final String algProvider, final int pos) throws NoSuchProviderException {
        Class<?> providerClass = null;
        String clzName = null;
        Provider cryptoProvider = null;
        assert pos >= 1 : "Position pos must be -1 or integer >= 1";
        try {
            if (algProvider.indexOf(46) != -1) {
                clzName = algProvider;
            }
            else {
                if (!SecurityProviderLoader.jceProviders.containsKey(algProvider)) {
                    throw new NoSuchProviderException("Unable to locate Provider class for provider " + algProvider + ". Try using fully qualified class name " + "or check provider name for typos. Builtin provider names are: " + SecurityProviderLoader.jceProviders.toString());
                }
                clzName = SecurityProviderLoader.jceProviders.get(algProvider);
            }
            providerClass = Class.forName(clzName);
            cryptoProvider = (Provider)providerClass.newInstance();
            int ret;
            if (pos == -1) {
                ret = Security.addProvider(cryptoProvider);
            }
            else {
                ret = Security.insertProviderAt(cryptoProvider, pos);
            }
            if (ret == -1) {
                final String msg = "JCE provider '" + algProvider + "' already loaded";
                if (pos == -1) {
                    SecurityProviderLoader.logger.always(Logger.SECURITY_SUCCESS, msg);
                }
                else {
                    SecurityProviderLoader.logger.warning(Logger.SECURITY_FAILURE, msg);
                    SecurityProviderLoader.logger.always(Logger.SECURITY_FAILURE, "(audit) " + msg);
                }
            }
            else {
                SecurityProviderLoader.logger.always(Logger.SECURITY_AUDIT, "Successfully loaded preferred JCE provider " + algProvider + " at position " + pos);
            }
            return ret;
        }
        catch (final SecurityException ex) {
            SecurityProviderLoader.logger.always(Logger.SECURITY_FAILURE, "Failed to load preferred JCE provider " + algProvider + " at position " + pos, ex);
            throw ex;
        }
        catch (final Exception ex2) {
            SecurityProviderLoader.logger.error(Logger.EVENT_FAILURE, "Failed to insert failed crypto  provider " + algProvider + " at position " + pos, ex2);
            throw new NoSuchProviderException("Failed to insert crypto  provider for " + algProvider + "; exception msg: " + ex2.toString());
        }
    }
    
    public static int loadESAPIPreferredJCEProvider() throws NoSuchProviderException {
        final String prefJCEProvider = ESAPI.securityConfiguration().getPreferredJCEProvider();
        try {
            if (prefJCEProvider == null || prefJCEProvider.trim().length() == 0) {
                SecurityProviderLoader.logger.always(Logger.SECURITY_AUDIT, "No Encryptor.PreferredJCEProvider specified.");
                return -1;
            }
            return insertProviderAt(prefJCEProvider, 1);
        }
        catch (final NoSuchProviderException ex) {
            final String msg = "failed to load *preferred* JCE crypto provider, " + prefJCEProvider;
            SecurityProviderLoader.logger.always(Logger.SECURITY_AUDIT, msg);
            SecurityProviderLoader.logger.error(Logger.SECURITY_FAILURE, msg);
            throw ex;
        }
    }
    
    static {
        SecurityProviderLoader.logger = ESAPI.getLogger("SecurityProviderLoader");
        (SecurityProviderLoader.jceProviders = new Hashtable<String, String>()).put("SunJCE", "com.sun.crypto.provider.SunJCE");
        SecurityProviderLoader.jceProviders.put("IBMJCE", "com.ibm.crypto.provider.IBMJCE");
        SecurityProviderLoader.jceProviders.put("GnuCrypto", "gnu.crypto.jce.GnuCrypto");
        SecurityProviderLoader.jceProviders.put("BC", "org.bouncycastle.jce.provider.BouncyCastleProvider");
        SecurityProviderLoader.jceProviders.put("IAIK", "iaik.security.provider.IAIK");
        SecurityProviderLoader.jceProviders.put("CryptixCrypto", "cryptix.jce.provider.CryptixCrypto");
        SecurityProviderLoader.jceProviders.put("Cryptix", "cryptix.jce.provider.CryptixCrypto");
        SecurityProviderLoader.jceProviders.put("ABA", "au.net.aba.crypto.provider.ABAProvider");
    }
}
