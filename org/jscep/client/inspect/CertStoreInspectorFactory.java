package org.jscep.client.inspect;

import java.security.cert.CertStore;

public interface CertStoreInspectorFactory
{
    CertStoreInspector getInstance(final CertStore p0);
}
