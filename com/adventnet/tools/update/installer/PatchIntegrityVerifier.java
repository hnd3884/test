package com.adventnet.tools.update.installer;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Iterator;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.jar.Manifest;
import sun.security.util.SignatureFileVerifier;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.security.CodeSigner;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import sun.security.pkcs.PKCS7;
import java.util.HashMap;
import java.io.File;
import com.zoho.tools.CertificateUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PatchIntegrityVerifier
{
    private static final Logger LOGGER;
    
    public static PatchIntegrityState verifyPatch(final String ppmFilePath, final String keyStoreDir, final String password) {
        if (password == null || password.isEmpty()) {
            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Please provide keystore password.");
            return PatchIntegrityState.PASSWORD_NOT_SUPPLIED;
        }
        if (keyStoreDir == null || keyStoreDir.isEmpty()) {
            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Please provide keystore directory.");
            return PatchIntegrityState.NO_KEYSTORE_FILE;
        }
        if (!CertificateUtil.isKeyStoreExists(keyStoreDir)) {
            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Please provide a valid keystore file.");
            return PatchIntegrityState.NO_KEYSTORE_FILE;
        }
        final Map<String, X509Certificate> certificates = CertificateUtil.getCertificatesWithAlias(keyStoreDir, password);
        if (ppmFilePath == null || !ppmFilePath.endsWith(".ppm")) {
            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Please provide a PPM file.");
            return PatchIntegrityState.NO_PATCH_FILE;
        }
        if (certificates.isEmpty()) {
            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Please provide certificates to verify.");
            return PatchIntegrityState.NO_CERTIFICATES;
        }
        if (!new File(ppmFilePath).exists()) {
            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "PPM File does not exist. Please specify a valid PPM file.");
            return PatchIntegrityState.PATCH_FILE_DOES_NOT_EXIST;
        }
        boolean isPPMSigned = false;
        final Map<String, String> digestMap = new HashMap<String, String>();
        final Map<String, PKCS7> signatureMap = new HashMap<String, PKCS7>();
        final List<X509Certificate> certificateList = new ArrayList<X509Certificate>();
        final List<String> unsignedFiles = new ArrayList<String>();
        final List<String> digestFailedFiles = new ArrayList<String>();
        final List<String> signatureMismatchFiles = new ArrayList<String>();
        final Map<CodeSigner, X509Certificate> codeSignerX509CertificateMap = new HashMap<CodeSigner, X509Certificate>();
        try (final JarFile jarFile = new JarFile(ppmFilePath, true)) {
            boolean hasSignature = false;
            final Enumeration<JarEntry> entries = jarFile.entries();
            final byte[] buffer = new byte[8192];
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                boolean isDigestFailed = false;
                try (final InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                    final String name = jarEntry.getName();
                    if (SignatureFileVerifier.isSigningRelated(name) && SignatureFileVerifier.isBlockOrSF(name)) {
                        hasSignature = (hasSignature || SignatureFileVerifier.isBlockOrSF(name));
                        final String alias = name.substring(name.lastIndexOf(47) + 1, name.lastIndexOf(46));
                        try {
                            if (name.endsWith(".SF")) {
                                final Manifest sf = new Manifest(inputStream);
                                boolean digestFound = false;
                                for (final Object obj : sf.getMainAttributes().keySet()) {
                                    final String key = obj.toString();
                                    if (key.endsWith("-Digest-Manifest")) {
                                        digestMap.put(alias, key.substring(0, key.length() - 16));
                                        digestFound = true;
                                        break;
                                    }
                                }
                                if (!digestFound) {
                                    PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Digest values not found in {0}", name);
                                }
                            }
                            else {
                                final PKCS7 pkcs7 = new PKCS7(inputStream);
                                signatureMap.put(alias, pkcs7);
                                certificateList.add(CertificateUtil.getClientCertificate(pkcs7.getCertificates()));
                            }
                        }
                        catch (final IOException ioe) {
                            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Unable to parse signature file {0}", name);
                            return PatchIntegrityState.UNABLE_TO_PARSE_SIGNATURE_FILE;
                        }
                    }
                    else {
                        try {
                            while (inputStream.read(buffer, 0, buffer.length) != -1) {}
                        }
                        catch (final SecurityException securityException) {
                            isDigestFailed = true;
                        }
                    }
                }
                if (isDigestFailed) {
                    digestFailedFiles.add(jarEntry.getName());
                }
                else {
                    final CodeSigner[] signers = jarEntry.getCodeSigners();
                    final boolean isSigned = signers != null;
                    isPPMSigned |= isSigned;
                    if (!jarEntry.isDirectory() && !isSigned && !SignatureFileVerifier.isSigningRelated(jarEntry.getName())) {
                        unsignedFiles.add(jarEntry.getName());
                    }
                    if (!isSigned) {
                        continue;
                    }
                    boolean anyMatch = false;
                    for (final CodeSigner signer : signers) {
                        if (!codeSignerX509CertificateMap.containsKey(signer)) {
                            codeSignerX509CertificateMap.put(signer, getCertificate(signer));
                        }
                        final X509Certificate certificate = codeSignerX509CertificateMap.get(signer);
                        if (certificates.containsValue(certificate)) {
                            anyMatch = true;
                            break;
                        }
                    }
                    if (anyMatch) {
                        continue;
                    }
                    signatureMismatchFiles.add(jarEntry.getName());
                }
            }
            if (!hasSignature) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "PPM is not signed");
                return PatchIntegrityState.PATCH_NOT_SIGNED;
            }
            boolean anyMatch2 = false;
            for (final X509Certificate certificate2 : certificateList) {
                if (certificates.containsValue(certificate2)) {
                    final String alias2 = getAliasFromMap(certificates, certificate2);
                    if (alias2 != null && alias2.startsWith("blacklisted_")) {
                        PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "PPM is signed with a blacklisted certificate");
                        return PatchIntegrityState.SIGNED_WITH_BLACKLISTED_CERTIFICATE;
                    }
                    anyMatch2 = true;
                    break;
                }
            }
            if (!anyMatch2) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Signature does not match with any available certificates.");
                return PatchIntegrityState.SIGNATURE_DOES_NOT_MATCH;
            }
            if (!digestMap.isEmpty() || !signatureMap.isEmpty()) {
                for (final String s : signatureMap.keySet()) {
                    if (!digestMap.containsKey(s)) {
                        PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Digest not found for certificate {0}", s);
                        return PatchIntegrityState.DIGEST_NOT_FOUND;
                    }
                }
            }
            if (jarFile.getManifest() == null) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Manifest is empty");
                return PatchIntegrityState.EMPTY_MANIFEST;
            }
            if (!isPPMSigned) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "PPM contents are not signed");
                return PatchIntegrityState.CONTENTS_NOT_SIGNED;
            }
            if (!digestFailedFiles.isEmpty()) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Some PPM contents are modified. {0}", digestFailedFiles);
                return PatchIntegrityState.CONTENTS_MODIFIED;
            }
            if (!unsignedFiles.isEmpty()) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Some PPM contents are not signed. {0}", unsignedFiles);
                return PatchIntegrityState.CONTENTS_NOT_SIGNED;
            }
            if (!signatureMismatchFiles.isEmpty()) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Some PPM contents are signed with different signer. {0}", signatureMismatchFiles);
                return PatchIntegrityState.CONTENTS_SIGNED_WITH_DIFFERENT_CERTIFICATE;
            }
            PatchIntegrityVerifier.LOGGER.log(Level.INFO, "Patch Integrity verified. PPM file [{0}] and its contents are signed properly.", ppmFilePath);
            return PatchIntegrityState.SUCCESS;
        }
        catch (final Exception e) {
            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return PatchIntegrityState.FAILURE;
        }
    }
    
    private static String getAliasFromMap(final Map<String, X509Certificate> certificateMap, final X509Certificate certificate) {
        for (final Map.Entry<String, X509Certificate> certificateEntry : certificateMap.entrySet()) {
            if (certificateEntry.getValue().equals(certificate)) {
                return certificateEntry.getKey();
            }
        }
        return null;
    }
    
    @Deprecated
    public static PatchIntegrityState verifyPatch(final String ppmFilePath, final List<X509Certificate> certificates) {
        if (ppmFilePath == null || !ppmFilePath.endsWith(".ppm")) {
            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Please provide a PPM file.");
            return PatchIntegrityState.NO_PATCH_FILE;
        }
        if (certificates == null || certificates.isEmpty()) {
            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Please provide certificates to verify.");
            return PatchIntegrityState.NO_CERTIFICATES;
        }
        if (!new File(ppmFilePath).exists()) {
            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "PPM File does not exist. Please specify a valid PPM file.");
            return PatchIntegrityState.PATCH_FILE_DOES_NOT_EXIST;
        }
        boolean isPPMSigned = false;
        final Map<String, String> digestMap = new HashMap<String, String>();
        final Map<String, PKCS7> signatureMap = new HashMap<String, PKCS7>();
        final List<X509Certificate> certificateList = new ArrayList<X509Certificate>();
        final List<String> unsignedFiles = new ArrayList<String>();
        final List<String> digestFailedFiles = new ArrayList<String>();
        final List<String> signatureMismatchFiles = new ArrayList<String>();
        final Map<CodeSigner, X509Certificate> codeSignerX509CertificateMap = new HashMap<CodeSigner, X509Certificate>();
        try (final JarFile jarFile = new JarFile(ppmFilePath, true)) {
            boolean hasSignature = false;
            final Enumeration<JarEntry> entries = jarFile.entries();
            final byte[] buffer = new byte[8192];
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                boolean isDigestFailed = false;
                try (final InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                    final String name = jarEntry.getName();
                    if (SignatureFileVerifier.isSigningRelated(name) && SignatureFileVerifier.isBlockOrSF(name)) {
                        hasSignature = (hasSignature || SignatureFileVerifier.isBlockOrSF(name));
                        final String alias = name.substring(name.lastIndexOf(47) + 1, name.lastIndexOf(46));
                        try {
                            if (name.endsWith(".SF")) {
                                final Manifest sf = new Manifest(inputStream);
                                boolean digestFound = false;
                                for (final Object obj : sf.getMainAttributes().keySet()) {
                                    final String key = obj.toString();
                                    if (key.endsWith("-Digest-Manifest")) {
                                        digestMap.put(alias, key.substring(0, key.length() - 16));
                                        digestFound = true;
                                        break;
                                    }
                                }
                                if (!digestFound) {
                                    PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Digest values not found in {0}", name);
                                }
                            }
                            else {
                                final PKCS7 pkcs7 = new PKCS7(inputStream);
                                signatureMap.put(alias, pkcs7);
                                certificateList.add(CertificateUtil.getClientCertificate(pkcs7.getCertificates()));
                            }
                        }
                        catch (final IOException ioe) {
                            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Unable to parse signature file {0}", name);
                            return PatchIntegrityState.UNABLE_TO_PARSE_SIGNATURE_FILE;
                        }
                    }
                    else {
                        try {
                            while (inputStream.read(buffer, 0, buffer.length) != -1) {}
                        }
                        catch (final SecurityException securityException) {
                            isDigestFailed = true;
                        }
                    }
                }
                if (isDigestFailed) {
                    digestFailedFiles.add(jarEntry.getName());
                }
                else {
                    final CodeSigner[] signers = jarEntry.getCodeSigners();
                    final boolean isSigned = signers != null;
                    isPPMSigned |= isSigned;
                    if (!jarEntry.isDirectory() && !isSigned && !SignatureFileVerifier.isSigningRelated(jarEntry.getName())) {
                        unsignedFiles.add(jarEntry.getName());
                    }
                    if (!isSigned) {
                        continue;
                    }
                    boolean anyMatch = false;
                    for (final CodeSigner signer : signers) {
                        if (!codeSignerX509CertificateMap.containsKey(signer)) {
                            codeSignerX509CertificateMap.put(signer, getCertificate(signer));
                        }
                        final X509Certificate certificate = codeSignerX509CertificateMap.get(signer);
                        if (certificates.contains(certificate)) {
                            anyMatch = true;
                            break;
                        }
                    }
                    if (anyMatch) {
                        continue;
                    }
                    signatureMismatchFiles.add(jarEntry.getName());
                }
            }
            if (!hasSignature) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "PPM is not signed");
                return PatchIntegrityState.PATCH_NOT_SIGNED;
            }
            boolean anyMatch2 = false;
            for (final X509Certificate certificate2 : certificateList) {
                if (certificates.contains(certificate2)) {
                    anyMatch2 = true;
                    break;
                }
            }
            if (!anyMatch2) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Signature doesn't match with the signer");
                return PatchIntegrityState.SIGNATURE_DOES_NOT_MATCH;
            }
            if (!digestMap.isEmpty() || !signatureMap.isEmpty()) {
                for (final String s : signatureMap.keySet()) {
                    if (!digestMap.containsKey(s)) {
                        PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Digest not found for certificate {0}", s);
                        return PatchIntegrityState.DIGEST_NOT_FOUND;
                    }
                }
            }
            if (jarFile.getManifest() == null) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Manifest is empty");
                return PatchIntegrityState.EMPTY_MANIFEST;
            }
            if (!isPPMSigned) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "PPM contents are not signed");
                return PatchIntegrityState.CONTENTS_NOT_SIGNED;
            }
            if (!digestFailedFiles.isEmpty()) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Some PPM contents are modified. {0}", digestFailedFiles);
                return PatchIntegrityState.CONTENTS_MODIFIED;
            }
            if (!unsignedFiles.isEmpty()) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Some PPM contents are not signed. {0}", unsignedFiles);
                return PatchIntegrityState.CONTENTS_NOT_SIGNED;
            }
            if (!signatureMismatchFiles.isEmpty()) {
                PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, "Some PPM contents are signed with different signer. {0}", signatureMismatchFiles);
                return PatchIntegrityState.CONTENTS_SIGNED_WITH_DIFFERENT_CERTIFICATE;
            }
            PatchIntegrityVerifier.LOGGER.log(Level.INFO, "Patch Integrity verified. PPM file [{0}] and its contents are signed properly.", ppmFilePath);
            return PatchIntegrityState.SUCCESS;
        }
        catch (final Exception e) {
            PatchIntegrityVerifier.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return PatchIntegrityState.FAILURE;
        }
    }
    
    private static List<String> flatMap(final Map<String, List<String>> map) {
        final List<String> list = new ArrayList<String>();
        for (final Map.Entry<String, List<String>> entry : map.entrySet()) {
            list.addAll(entry.getValue());
        }
        return list;
    }
    
    private static List<String> getThumbprints(final CodeSigner[] signers) {
        final List<String> thumbprints = new ArrayList<String>();
        for (final CodeSigner signer : signers) {
            thumbprints.addAll(getThumbprints(signer.getSignerCertPath().getCertificates().toArray(new X509Certificate[0])));
        }
        return thumbprints;
    }
    
    private static List<String> getThumbprints(final X509Certificate[] certificates) {
        final List<String> thumbprints = new ArrayList<String>();
        for (final X509Certificate certificate : certificates) {
            final String thumbprint = getThumbprint(certificate);
            if (thumbprint != null) {
                thumbprints.add(thumbprint);
            }
        }
        return thumbprints;
    }
    
    private static String getThumbprint(final X509Certificate cert) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] der = cert.getEncoded();
            md.update(der);
            final byte[] digest = md.digest();
            final String digestHex = DatatypeConverter.printHexBinary(digest);
            return digestHex.toLowerCase();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    private static X509Certificate getCertificate(final CodeSigner signer) {
        return CertificateUtil.getClientCertificate(signer.getSignerCertPath().getCertificates().toArray(new X509Certificate[0]));
    }
    
    static {
        LOGGER = Logger.getLogger(PatchIntegrityVerifier.class.getName());
    }
}
