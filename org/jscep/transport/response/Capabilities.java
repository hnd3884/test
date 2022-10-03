package org.jscep.transport.response;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.security.Provider;
import java.security.Security;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import net.jcip.annotations.Immutable;

@Immutable
public final class Capabilities
{
    private final EnumSet<Capability> caps;
    
    public Capabilities(final Capability... capabilities) {
        Collections.addAll(this.caps = EnumSet.noneOf(Capability.class), capabilities);
        if (this.caps.contains(Capability.SCEP_STANDARD)) {
            Collections.addAll(this.caps, new Capability[] { Capability.AES, Capability.POST_PKI_OPERATION, Capability.SHA_256 });
        }
    }
    
    public boolean contains(final Capability capability) {
        return this.caps.contains(capability);
    }
    
    public boolean isPostSupported() {
        return this.caps.contains(Capability.POST_PKI_OPERATION);
    }
    
    public boolean isRolloverSupported() {
        return this.caps.contains(Capability.GET_NEXT_CA_CERT);
    }
    
    public boolean isRenewalSupported() {
        return this.caps.contains(Capability.RENEWAL);
    }
    
    public boolean isUpdateSupported() {
        return this.caps.contains(Capability.UPDATE);
    }
    
    public String getStrongestCipher() {
        String cipher;
        if (this.cipherExists("AES") && this.caps.contains(Capability.AES)) {
            cipher = "AES";
        }
        else if (this.cipherExists("DESede") && this.caps.contains(Capability.TRIPLE_DES)) {
            cipher = "DESede";
        }
        else {
            cipher = "DES";
        }
        return cipher;
    }
    
    private boolean cipherExists(final String algorithm) {
        return this.algorithmExists("Cipher", algorithm);
    }
    
    private boolean algorithmExists(final String serviceType, final String algorithm) {
        for (final Provider provider : Security.getProviders()) {
            for (final Provider.Service service : provider.getServices()) {
                if (service.getType().equals(serviceType) && service.getAlgorithm().equalsIgnoreCase(algorithm)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public MessageDigest getStrongestMessageDigest() {
        if (this.digestExists("SHA-512") && this.caps.contains(Capability.SHA_512)) {
            return this.getDigest("SHA-512");
        }
        if (this.digestExists("SHA-256") && this.caps.contains(Capability.SHA_256)) {
            return this.getDigest("SHA-256");
        }
        if (this.digestExists("SHA-1") && this.caps.contains(Capability.SHA_1)) {
            return this.getDigest("SHA-1");
        }
        if (this.digestExists("MD5")) {
            return this.getDigest("MD5");
        }
        return null;
    }
    
    public String getStrongestSignatureAlgorithm() {
        if (this.sigExists("SHA512") && this.caps.contains(Capability.SHA_512)) {
            return "SHA512withRSA";
        }
        if (this.sigExists("SHA256") && this.caps.contains(Capability.SHA_256)) {
            return "SHA256withRSA";
        }
        if (this.sigExists("SHA1") && this.caps.contains(Capability.SHA_1)) {
            return "SHA1withRSA";
        }
        if (this.sigExists("MD5")) {
            return "MD5withRSA";
        }
        return null;
    }
    
    private boolean sigExists(final String sig) {
        return (this.algorithmExists("Signature", sig + "withRSA") || this.algorithmExists("Signature", sig + "WithRSAEncryption")) && this.digestExists(sig);
    }
    
    private boolean digestExists(final String digest) {
        return this.algorithmExists("MessageDigest", digest) || this.algorithmExists("MessageDigest", digest.replaceFirst("SHA", "SHA-"));
    }
    
    private MessageDigest getDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String toString() {
        return this.caps.toString();
    }
}
