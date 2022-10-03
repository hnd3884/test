package org.bouncycastle.asn1;

import java.io.IOException;

public class BERTaggedObjectParser implements ASN1TaggedObjectParser
{
    private boolean _constructed;
    private int _tagNumber;
    private ASN1StreamParser _parser;
    
    BERTaggedObjectParser(final boolean constructed, final int tagNumber, final ASN1StreamParser parser) {
        this._constructed = constructed;
        this._tagNumber = tagNumber;
        this._parser = parser;
    }
    
    public boolean isConstructed() {
        return this._constructed;
    }
    
    public int getTagNo() {
        return this._tagNumber;
    }
    
    public ASN1Encodable getObjectParser(final int n, final boolean b) throws IOException {
        if (!b) {
            return this._parser.readImplicit(this._constructed, n);
        }
        if (!this._constructed) {
            throw new IOException("Explicit tags must be constructed (see X.690 8.14.2)");
        }
        return this._parser.readObject();
    }
    
    public ASN1Primitive getLoadedObject() throws IOException {
        return this._parser.readTaggedObject(this._constructed, this._tagNumber);
    }
    
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (final IOException ex) {
            throw new ASN1ParsingException(ex.getMessage());
        }
    }
}
