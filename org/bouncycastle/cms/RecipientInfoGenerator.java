package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.operator.GenericKey;

public interface RecipientInfoGenerator
{
    RecipientInfo generate(final GenericKey p0) throws CMSException;
}
