package com.unboundid.asn1;

import javax.security.sasl.SaslClient;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class InternalASN1Helper
{
    private InternalASN1Helper() {
    }
    
    @InternalUseOnly
    public static void setSASLClient(final ASN1StreamReader asn1StreamReader, final SaslClient saslClient) {
        asn1StreamReader.setSASLClient(saslClient);
    }
}
