package org.bouncycastle.asn1;

import java.util.Date;

public class DERUTCTime extends ASN1UTCTime
{
    DERUTCTime(final byte[] array) {
        super(array);
    }
    
    public DERUTCTime(final Date date) {
        super(date);
    }
    
    public DERUTCTime(final String s) {
        super(s);
    }
}
