package com.adventnet.tools.update.installer;

import java.security.cert.X509Certificate;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import com.zoho.tools.CertificateUtil;
import sun.security.pkcs.PKCS7;
import java.util.zip.ZipEntry;
import sun.security.util.SignatureFileVerifier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class JarSignerUtils
{
    private static final Logger LOGGER;
    
    public static boolean isTrustedArchive(final String filePath) {
        boolean isTrusted = false;
        try (final JarFile jarFile = new JarFile(filePath, true)) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                final String name = jarEntry.getName();
                if (SignatureFileVerifier.isSigningRelated(name) && SignatureFileVerifier.isBlockOrSF(name) && !name.endsWith(".SF")) {
                    try (final InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                        final PKCS7 pkcs7 = new PKCS7(inputStream);
                        final X509Certificate certificate = CertificateUtil.getClientCertificate(pkcs7.getCertificates());
                        if (certificate != null) {
                            isTrusted |= !CertificateUtil.isSelfSigned(certificate);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            JarSignerUtils.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        return isTrusted;
    }
    
    static {
        LOGGER = Logger.getLogger(JarSignerUtils.class.getName());
    }
}
