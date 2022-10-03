package org.apache.poi.poifs.crypt.dsig.services;

import java.security.cert.X509Certificate;
import java.util.List;

public interface TimeStampServiceValidator
{
    void validate(final List<X509Certificate> p0, final RevocationData p1) throws Exception;
}
