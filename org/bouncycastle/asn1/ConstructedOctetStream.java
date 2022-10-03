package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

class ConstructedOctetStream extends InputStream
{
    private final ASN1StreamParser _parser;
    private boolean _first;
    private InputStream _currentStream;
    
    ConstructedOctetStream(final ASN1StreamParser parser) {
        this._first = true;
        this._parser = parser;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            final ASN1OctetStringParser asn1OctetStringParser = (ASN1OctetStringParser)this._parser.readObject();
            if (asn1OctetStringParser == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = asn1OctetStringParser.getOctetStream();
        }
        int n3 = 0;
        while (true) {
            final int read = this._currentStream.read(array, n + n3, n2 - n3);
            if (read >= 0) {
                n3 += read;
                if (n3 == n2) {
                    return n3;
                }
                continue;
            }
            else {
                final ASN1OctetStringParser asn1OctetStringParser2 = (ASN1OctetStringParser)this._parser.readObject();
                if (asn1OctetStringParser2 == null) {
                    this._currentStream = null;
                    return (n3 < 1) ? -1 : n3;
                }
                this._currentStream = asn1OctetStringParser2.getOctetStream();
            }
        }
    }
    
    @Override
    public int read() throws IOException {
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            final ASN1OctetStringParser asn1OctetStringParser = (ASN1OctetStringParser)this._parser.readObject();
            if (asn1OctetStringParser == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = asn1OctetStringParser.getOctetStream();
        }
        while (true) {
            final int read = this._currentStream.read();
            if (read >= 0) {
                return read;
            }
            final ASN1OctetStringParser asn1OctetStringParser2 = (ASN1OctetStringParser)this._parser.readObject();
            if (asn1OctetStringParser2 == null) {
                this._currentStream = null;
                return -1;
            }
            this._currentStream = asn1OctetStringParser2.getOctetStream();
        }
    }
}
