package org.bouncycastle.tsp;

import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import org.bouncycastle.asn1.x509.Extension;
import java.util.Enumeration;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import java.util.Set;

public class TimeStampRequest
{
    private static Set EMPTY_SET;
    private TimeStampReq req;
    private Extensions extensions;
    
    public TimeStampRequest(final TimeStampReq req) {
        this.req = req;
        this.extensions = req.getExtensions();
    }
    
    public TimeStampRequest(final byte[] array) throws IOException {
        this(new ByteArrayInputStream(array));
    }
    
    public TimeStampRequest(final InputStream inputStream) throws IOException {
        this(loadRequest(inputStream));
    }
    
    private static TimeStampReq loadRequest(final InputStream inputStream) throws IOException {
        try {
            return TimeStampReq.getInstance((Object)new ASN1InputStream(inputStream).readObject());
        }
        catch (final ClassCastException ex) {
            throw new IOException("malformed request: " + ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new IOException("malformed request: " + ex2);
        }
    }
    
    public int getVersion() {
        return this.req.getVersion().getValue().intValue();
    }
    
    public ASN1ObjectIdentifier getMessageImprintAlgOID() {
        return this.req.getMessageImprint().getHashAlgorithm().getAlgorithm();
    }
    
    public byte[] getMessageImprintDigest() {
        return this.req.getMessageImprint().getHashedMessage();
    }
    
    public ASN1ObjectIdentifier getReqPolicy() {
        if (this.req.getReqPolicy() != null) {
            return this.req.getReqPolicy();
        }
        return null;
    }
    
    public BigInteger getNonce() {
        if (this.req.getNonce() != null) {
            return this.req.getNonce().getValue();
        }
        return null;
    }
    
    public boolean getCertReq() {
        return this.req.getCertReq() != null && this.req.getCertReq().isTrue();
    }
    
    public void validate(Set convert, Set convert2, Set convert3) throws TSPException {
        convert = this.convert(convert);
        convert2 = this.convert(convert2);
        convert3 = this.convert(convert3);
        if (!convert.contains(this.getMessageImprintAlgOID())) {
            throw new TSPValidationException("request contains unknown algorithm", 128);
        }
        if (convert2 != null && this.getReqPolicy() != null && !convert2.contains(this.getReqPolicy())) {
            throw new TSPValidationException("request contains unknown policy", 256);
        }
        if (this.getExtensions() != null && convert3 != null) {
            final Enumeration oids = this.getExtensions().oids();
            while (oids.hasMoreElements()) {
                if (!convert3.contains(oids.nextElement())) {
                    throw new TSPValidationException("request contains unknown extension", 8388608);
                }
            }
        }
        if (TSPUtil.getDigestLength(this.getMessageImprintAlgOID().getId()) != this.getMessageImprintDigest().length) {
            throw new TSPValidationException("imprint digest the wrong length", 4);
        }
    }
    
    public byte[] getEncoded() throws IOException {
        return this.req.getEncoded();
    }
    
    Extensions getExtensions() {
        return this.extensions;
    }
    
    public boolean hasExtensions() {
        return this.extensions != null;
    }
    
    public Extension getExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (this.extensions != null) {
            return this.extensions.getExtension(asn1ObjectIdentifier);
        }
        return null;
    }
    
    public List getExtensionOIDs() {
        return TSPUtil.getExtensionOIDs(this.extensions);
    }
    
    public Set getNonCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return TimeStampRequest.EMPTY_SET;
        }
        return Collections.unmodifiableSet((Set<?>)new HashSet<Object>(Arrays.asList(this.extensions.getNonCriticalExtensionOIDs())));
    }
    
    public Set getCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return TimeStampRequest.EMPTY_SET;
        }
        return Collections.unmodifiableSet((Set<?>)new HashSet<Object>(Arrays.asList(this.extensions.getCriticalExtensionOIDs())));
    }
    
    private Set convert(final Set set) {
        if (set == null) {
            return set;
        }
        final HashSet set2 = new HashSet(set.size());
        for (final Object next : set) {
            if (next instanceof String) {
                set2.add(new ASN1ObjectIdentifier((String)next));
            }
            else {
                set2.add(next);
            }
        }
        return set2;
    }
    
    static {
        TimeStampRequest.EMPTY_SET = Collections.unmodifiableSet((Set<?>)new HashSet<Object>());
    }
}
