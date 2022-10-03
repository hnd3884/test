package sun.security.pkcs;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class ContentInfo
{
    private static int[] pkcs7;
    private static int[] data;
    private static int[] sdata;
    private static int[] edata;
    private static int[] sedata;
    private static int[] ddata;
    private static int[] crdata;
    private static int[] nsdata;
    private static int[] tstInfo;
    private static final int[] OLD_SDATA;
    private static final int[] OLD_DATA;
    public static ObjectIdentifier PKCS7_OID;
    public static ObjectIdentifier DATA_OID;
    public static ObjectIdentifier SIGNED_DATA_OID;
    public static ObjectIdentifier ENVELOPED_DATA_OID;
    public static ObjectIdentifier SIGNED_AND_ENVELOPED_DATA_OID;
    public static ObjectIdentifier DIGESTED_DATA_OID;
    public static ObjectIdentifier ENCRYPTED_DATA_OID;
    public static ObjectIdentifier OLD_SIGNED_DATA_OID;
    public static ObjectIdentifier OLD_DATA_OID;
    public static ObjectIdentifier NETSCAPE_CERT_SEQUENCE_OID;
    public static ObjectIdentifier TIMESTAMP_TOKEN_INFO_OID;
    ObjectIdentifier contentType;
    DerValue content;
    
    public ContentInfo(final ObjectIdentifier contentType, final DerValue content) {
        this.contentType = contentType;
        this.content = content;
    }
    
    public ContentInfo(final byte[] array) {
        final DerValue content = new DerValue((byte)4, array);
        this.contentType = ContentInfo.DATA_OID;
        this.content = content;
    }
    
    public ContentInfo(final DerInputStream derInputStream) throws IOException, ParsingException {
        this(derInputStream, false);
    }
    
    public ContentInfo(final DerInputStream derInputStream, final boolean b) throws IOException, ParsingException {
        final DerValue[] sequence = derInputStream.getSequence(2);
        if (sequence.length < 1 || sequence.length > 2) {
            throw new ParsingException("Invalid length for ContentInfo");
        }
        this.contentType = new DerInputStream(sequence[0].toByteArray()).getOID();
        if (b) {
            this.content = sequence[1];
        }
        else if (sequence.length > 1) {
            final DerValue[] set = new DerInputStream(sequence[1].toByteArray()).getSet(1, true);
            if (set.length != 1) {
                throw new ParsingException("ContentInfo encoding error");
            }
            this.content = set[0];
        }
    }
    
    public DerValue getContent() {
        return this.content;
    }
    
    public ObjectIdentifier getContentType() {
        return this.contentType;
    }
    
    public byte[] getData() throws IOException {
        if (!this.contentType.equals((Object)ContentInfo.DATA_OID) && !this.contentType.equals((Object)ContentInfo.OLD_DATA_OID) && !this.contentType.equals((Object)ContentInfo.TIMESTAMP_TOKEN_INFO_OID)) {
            throw new IOException("content type is not DATA: " + this.contentType);
        }
        if (this.content == null) {
            return null;
        }
        return this.content.getOctetString();
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putOID(this.contentType);
        if (this.content != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            this.content.encode(derOutputStream3);
            derOutputStream2.putDerValue(new DerValue((byte)(-96), derOutputStream3.toByteArray()));
        }
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    public byte[] getContentBytes() throws IOException {
        if (this.content == null) {
            return null;
        }
        return new DerInputStream(this.content.toByteArray()).getOctetString();
    }
    
    @Override
    public String toString() {
        return "" + "Content Info Sequence\n\tContent type: " + this.contentType + "\n" + "\tContent: " + this.content;
    }
    
    static {
        ContentInfo.pkcs7 = new int[] { 1, 2, 840, 113549, 1, 7 };
        ContentInfo.data = new int[] { 1, 2, 840, 113549, 1, 7, 1 };
        ContentInfo.sdata = new int[] { 1, 2, 840, 113549, 1, 7, 2 };
        ContentInfo.edata = new int[] { 1, 2, 840, 113549, 1, 7, 3 };
        ContentInfo.sedata = new int[] { 1, 2, 840, 113549, 1, 7, 4 };
        ContentInfo.ddata = new int[] { 1, 2, 840, 113549, 1, 7, 5 };
        ContentInfo.crdata = new int[] { 1, 2, 840, 113549, 1, 7, 6 };
        ContentInfo.nsdata = new int[] { 2, 16, 840, 1, 113730, 2, 5 };
        ContentInfo.tstInfo = new int[] { 1, 2, 840, 113549, 1, 9, 16, 1, 4 };
        OLD_SDATA = new int[] { 1, 2, 840, 1113549, 1, 7, 2 };
        OLD_DATA = new int[] { 1, 2, 840, 1113549, 1, 7, 1 };
        ContentInfo.PKCS7_OID = ObjectIdentifier.newInternal(ContentInfo.pkcs7);
        ContentInfo.DATA_OID = ObjectIdentifier.newInternal(ContentInfo.data);
        ContentInfo.SIGNED_DATA_OID = ObjectIdentifier.newInternal(ContentInfo.sdata);
        ContentInfo.ENVELOPED_DATA_OID = ObjectIdentifier.newInternal(ContentInfo.edata);
        ContentInfo.SIGNED_AND_ENVELOPED_DATA_OID = ObjectIdentifier.newInternal(ContentInfo.sedata);
        ContentInfo.DIGESTED_DATA_OID = ObjectIdentifier.newInternal(ContentInfo.ddata);
        ContentInfo.ENCRYPTED_DATA_OID = ObjectIdentifier.newInternal(ContentInfo.crdata);
        ContentInfo.OLD_SIGNED_DATA_OID = ObjectIdentifier.newInternal(ContentInfo.OLD_SDATA);
        ContentInfo.OLD_DATA_OID = ObjectIdentifier.newInternal(ContentInfo.OLD_DATA);
        ContentInfo.NETSCAPE_CERT_SEQUENCE_OID = ObjectIdentifier.newInternal(ContentInfo.nsdata);
        ContentInfo.TIMESTAMP_TOKEN_INFO_OID = ObjectIdentifier.newInternal(ContentInfo.tstInfo);
    }
}
