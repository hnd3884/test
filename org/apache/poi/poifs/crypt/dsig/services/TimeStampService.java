package org.apache.poi.poifs.crypt.dsig.services;

import org.apache.poi.poifs.crypt.dsig.SignatureConfig;

public interface TimeStampService extends SignatureConfig.SignatureConfigurable
{
    byte[] timeStamp(final byte[] p0, final RevocationData p1) throws Exception;
}
