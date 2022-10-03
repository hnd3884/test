package jcifs.spnego;

import java.util.Enumeration;
import jcifs.spnego.asn1.ASN1OctetString;
import jcifs.spnego.asn1.ASN1Sequence;
import jcifs.spnego.asn1.ASN1TaggedObject;
import java.io.InputStream;
import jcifs.spnego.asn1.DERInputStream;
import java.io.ByteArrayInputStream;
import jcifs.spnego.asn1.DEREncodableVector;
import jcifs.spnego.asn1.DERSequence;
import jcifs.spnego.asn1.DEROctetString;
import jcifs.spnego.asn1.DERObjectIdentifier;
import jcifs.spnego.asn1.DEREncodable;
import jcifs.spnego.asn1.DERTaggedObject;
import jcifs.spnego.asn1.DEREnumerated;
import jcifs.spnego.asn1.ASN1EncodableVector;
import java.io.OutputStream;
import jcifs.spnego.asn1.DEROutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NegTokenTarg extends SpnegoToken
{
    public static final int UNSPECIFIED_RESULT = -1;
    public static final int ACCEPT_COMPLETED = 0;
    public static final int ACCEPT_INCOMPLETE = 1;
    public static final int REJECTED = 2;
    private String mechanism;
    private int result;
    
    public NegTokenTarg() {
        this.result = -1;
    }
    
    public NegTokenTarg(final int result, final String mechanism, final byte[] mechanismToken, final byte[] mechanismListMIC) {
        this.result = -1;
        this.setResult(result);
        this.setMechanism(mechanism);
        this.setMechanismToken(mechanismToken);
        this.setMechanismListMIC(mechanismListMIC);
    }
    
    public NegTokenTarg(final byte[] token) throws IOException {
        this.result = -1;
        this.parse(token);
    }
    
    public int getResult() {
        return this.result;
    }
    
    public void setResult(final int result) {
        this.result = result;
    }
    
    public String getMechanism() {
        return this.mechanism;
    }
    
    public void setMechanism(final String mechanism) {
        this.mechanism = mechanism;
    }
    
    public byte[] toByteArray() {
        try {
            final ByteArrayOutputStream collector = new ByteArrayOutputStream();
            final DEROutputStream der = new DEROutputStream(collector);
            final ASN1EncodableVector fields = new ASN1EncodableVector();
            final int result = this.getResult();
            if (result != -1) {
                fields.add(new DERTaggedObject(true, 0, new DEREnumerated(result)));
            }
            final String mechanism = this.getMechanism();
            if (mechanism != null) {
                fields.add(new DERTaggedObject(true, 1, new DERObjectIdentifier(mechanism)));
            }
            final byte[] mechanismToken = this.getMechanismToken();
            if (mechanismToken != null) {
                fields.add(new DERTaggedObject(true, 2, new DEROctetString(mechanismToken)));
            }
            final byte[] mechanismListMIC = this.getMechanismListMIC();
            if (mechanismListMIC != null) {
                fields.add(new DERTaggedObject(true, 3, new DEROctetString(mechanismListMIC)));
            }
            der.writeObject(new DERTaggedObject(true, 1, new DERSequence(fields)));
            return collector.toByteArray();
        }
        catch (final IOException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    protected void parse(final byte[] token) throws IOException {
        final ByteArrayInputStream tokenStream = new ByteArrayInputStream(token);
        final DERInputStream der = new DERInputStream(tokenStream);
        ASN1TaggedObject tagged = (ASN1TaggedObject)der.readObject();
        final ASN1Sequence sequence = ASN1Sequence.getInstance(tagged, true);
        final Enumeration fields = sequence.getObjects();
        while (fields.hasMoreElements()) {
            tagged = fields.nextElement();
            switch (tagged.getTagNo()) {
                case 0: {
                    final DEREnumerated enumerated = DEREnumerated.getInstance(tagged, true);
                    this.setResult(enumerated.getValue().intValue());
                    continue;
                }
                case 1: {
                    final DERObjectIdentifier mechanism = DERObjectIdentifier.getInstance(tagged, true);
                    this.setMechanism(mechanism.getId());
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
