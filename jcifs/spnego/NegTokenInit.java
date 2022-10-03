package jcifs.spnego;

import java.util.Enumeration;
import jcifs.spnego.asn1.ASN1OctetString;
import jcifs.spnego.asn1.ASN1Sequence;
import jcifs.spnego.asn1.ASN1TaggedObject;
import java.io.InputStream;
import jcifs.spnego.asn1.DERInputStream;
import java.io.ByteArrayInputStream;
import jcifs.spnego.asn1.DERObject;
import jcifs.spnego.asn1.DERUnknownTag;
import jcifs.spnego.asn1.DEROctetString;
import jcifs.spnego.asn1.DERBitString;
import jcifs.spnego.asn1.DERTaggedObject;
import jcifs.spnego.asn1.DEREncodableVector;
import jcifs.spnego.asn1.DERSequence;
import jcifs.spnego.asn1.DEREncodable;
import jcifs.spnego.asn1.ASN1EncodableVector;
import jcifs.spnego.asn1.DERObjectIdentifier;
import java.io.OutputStream;
import jcifs.spnego.asn1.DEROutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NegTokenInit extends SpnegoToken
{
    public static final int DELEGATION = 64;
    public static final int MUTUAL_AUTHENTICATION = 32;
    public static final int REPLAY_DETECTION = 16;
    public static final int SEQUENCE_CHECKING = 8;
    public static final int ANONYMITY = 4;
    public static final int CONFIDENTIALITY = 2;
    public static final int INTEGRITY = 1;
    private String[] mechanisms;
    private int contextFlags;
    
    public NegTokenInit() {
    }
    
    public NegTokenInit(final String[] mechanisms, final int contextFlags, final byte[] mechanismToken, final byte[] mechanismListMIC) {
        this.setMechanisms(mechanisms);
        this.setContextFlags(contextFlags);
        this.setMechanismToken(mechanismToken);
        this.setMechanismListMIC(mechanismListMIC);
    }
    
    public NegTokenInit(final byte[] token) throws IOException {
        this.parse(token);
    }
    
    public int getContextFlags() {
        return this.contextFlags;
    }
    
    public void setContextFlags(final int contextFlags) {
        this.contextFlags = contextFlags;
    }
    
    public boolean getContextFlag(final int flag) {
        return (this.getContextFlags() & flag) == flag;
    }
    
    public void setContextFlag(final int flag, final boolean value) {
        this.setContextFlags(value ? (this.getContextFlags() | flag) : (this.getContextFlags() & (-1 ^ flag)));
    }
    
    public String[] getMechanisms() {
        return this.mechanisms;
    }
    
    public void setMechanisms(final String[] mechanisms) {
        this.mechanisms = mechanisms;
    }
    
    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream collector = new ByteArrayOutputStream();
            DEROutputStream der = new DEROutputStream(collector);
            der.writeObject(new DERObjectIdentifier("1.3.6.1.5.5.2"));
            final ASN1EncodableVector fields = new ASN1EncodableVector();
            final String[] mechanisms = this.getMechanisms();
            if (mechanisms != null) {
                final ASN1EncodableVector vector = new ASN1EncodableVector();
                for (int i = 0; i < mechanisms.length; ++i) {
                    vector.add(new DERObjectIdentifier(mechanisms[i]));
                }
                fields.add(new DERTaggedObject(true, 0, new DERSequence(vector)));
            }
            final int contextFlags = this.getContextFlags();
            if (contextFlags != 0) {
                fields.add(new DERTaggedObject(true, 1, new DERBitString(contextFlags)));
            }
            final byte[] mechanismToken = this.getMechanismToken();
            if (mechanismToken != null) {
                fields.add(new DERTaggedObject(true, 2, new DEROctetString(mechanismToken)));
            }
            final byte[] mechanismListMIC = this.getMechanismListMIC();
            if (mechanismListMIC != null) {
                fields.add(new DERTaggedObject(true, 3, new DEROctetString(mechanismListMIC)));
            }
            der.writeObject(new DERTaggedObject(true, 0, new DERSequence(fields)));
            final DERObject token = new DERUnknownTag(96, collector.toByteArray());
            der = new DEROutputStream(collector = new ByteArrayOutputStream());
            der.writeObject(token);
            return collector.toByteArray();
        }
        catch (final IOException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    protected void parse(final byte[] token) throws IOException {
        ByteArrayInputStream tokenStream = new ByteArrayInputStream(token);
        DERInputStream der = new DERInputStream(tokenStream);
        final DERUnknownTag constructed = (DERUnknownTag)der.readObject();
        if (constructed.getTag() != 96) {
            throw new IOException("Malformed NegTokenInit.");
        }
        tokenStream = new ByteArrayInputStream(constructed.getData());
        der = new DERInputStream(tokenStream);
        final DERObjectIdentifier spnego = (DERObjectIdentifier)der.readObject();
        ASN1TaggedObject tagged = (ASN1TaggedObject)der.readObject();
        ASN1Sequence sequence = ASN1Sequence.getInstance(tagged, true);
        final Enumeration fields = sequence.getObjects();
        while (fields.hasMoreElements()) {
            tagged = fields.nextElement();
            switch (tagged.getTagNo()) {
                case 0: {
                    sequence = ASN1Sequence.getInstance(tagged, true);
                    final String[] mechanisms = new String[sequence.size()];
                    for (int i = mechanisms.length - 1; i >= 0; --i) {
                        final DERObjectIdentifier mechanism = (DERObjectIdentifier)sequence.getObjectAt(i);
                        mechanisms[i] = mechanism.getId();
                    }
                    this.setMechanisms(mechanisms);
                    continue;
                }
                case 1: {
                    final DERBitString contextFlags = DERBitString.getInstance(tagged, true);
                    this.setContextFlags(contextFlags.getBytes()[0] & 0xFF);
                    continue;
                }
                case 2: {
                    final ASN1OctetString mechanismToken = ASN1OctetString.getInstance(tagged, true);
                    this.setMechanismToken(mechanismToken.getOctets());
                    continue;
                }
                case 3: {
                    final ASN1OctetString mechanismListMIC = ASN1OctetString.getInstance(tagged, true);
                    this.setMechanismListMIC(mechanismListMIC.getOctets());
                    continue;
                }
                default: {
                    throw new IOException("Malformed token field.");
                }
            }
        }
    }
}
