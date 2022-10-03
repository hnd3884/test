package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.AttributeTable;
import java.util.Map;

public interface CMSAttributeTableGenerator
{
    public static final String CONTENT_TYPE = "contentType";
    public static final String DIGEST = "digest";
    public static final String SIGNATURE = "encryptedDigest";
    public static final String DIGEST_ALGORITHM_IDENTIFIER = "digestAlgID";
    public static final String MAC_ALGORITHM_IDENTIFIER = "macAlgID";
    public static final String SIGNATURE_ALGORITHM_IDENTIFIER = "signatureAlgID";
    
    AttributeTable getAttributes(final Map p0) throws CMSAttributeTableGenerationException;
}
