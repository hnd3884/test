package jcifs.spnego.asn1;

import java.util.Enumeration;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

public class BERConstructedOctetString extends DEROctetString
{
    private Vector octs;
    
    private static byte[] toBytes(final Vector octs) {
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        for (int i = 0; i != octs.size(); ++i) {
            final DEROctetString o = octs.elementAt(i);
            try {
                bOut.write(o.getOctets());
            }
            catch (final IOException e) {
                throw new RuntimeException("exception converting octets " + e.toString());
            }
        }
        return bOut.toByteArray();
    }
    
    public BERConstructedOctetString(final byte[] string) {
        super(string);
    }
    
    public BERConstructedOctetString(final Vector octs) {
        super(toBytes(octs));
        this.octs = octs;
    }
    
    public BERConstructedOctetString(final DERObject obj) {
        super(obj);
    }
    
    public BERConstructedOctetString(final DEREncodable obj) {
        super(obj.getDERObject());
    }
    
    public byte[] getOctets() {
        return this.string;
    }
    
    public Enumeration getObjects() {
        if (this.octs == null) {
            this.octs = this.generateOcts();
        }
        return this.octs.elements();
    }
    
    private Vector generateOcts() {
        int start = 0;
        int end = 0;
        final Vector vec = new Vector();
        while (end + 1 < this.string.length) {
            if (this.string[end] == 0 && this.string[end + 1] == 0) {
                final byte[] nStr = new byte[end - start + 1];
                for (int i = 0; i != nStr.length; ++i) {
                    nStr[i] = this.string[start + i];
                }
                vec.addElement(new DEROctetString(nStr));
                start = end + 1;
            }
            ++end;
        }
        final byte[] nStr = new byte[this.string.length - start];
        for (int i = 0; i != nStr.length; ++i) {
            nStr[i] = this.string[start + i];
        }
        vec.addElement(new DEROctetString(nStr));
        return vec;
    }
    
    public void encode(final DEROutputStream out) throws IOException {
        if (out instanceof ASN1OutputStream || out instanceof BEROutputStream) {
            out.write(36);
            out.write(128);
            if (this.octs == null) {
                this.octs = this.generateOcts();
            }
            for (int i = 0; i != this.octs.size(); ++i) {
                out.writeObject(this.octs.elementAt(i));
            }
            out.write(0);
            out.write(0);
        }
        else {
            super.encode(out);
        }
    }
}
