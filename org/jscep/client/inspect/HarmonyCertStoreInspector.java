package org.jscep.client.inspect;

import java.util.Arrays;
import java.io.IOException;
import java.security.cert.X509CertSelector;
import java.util.Collection;
import java.security.cert.CertStore;

final class HarmonyCertStoreInspector extends AbstractCertStoreInspector implements CertStoreInspector
{
    HarmonyCertStoreInspector(final CertStore store) {
        super(store);
    }
    
    @Override
    protected Collection<X509CertSelector> getIssuerSelectors(final byte[] subjectDN) {
        final X509CertSelector caSelector = new X509CertSelector();
        caSelector.setBasicConstraints(0);
        try {
            caSelector.setSubject(subjectDN);
        }
        catch (final IOException ex) {}
        return Arrays.asList(caSelector);
    }
    
    @Override
    protected Collection<X509CertSelector> getSignerSelectors() {
        final X509CertSelector digSigSelector = new X509CertSelector();
        digSigSelector.setBasicConstraints(-2);
        digSigSelector.setKeyUsage(new boolean[] { true });
        final X509CertSelector caSelector = new X509CertSelector();
        caSelector.setBasicConstraints(0);
        return Arrays.asList(digSigSelector, caSelector);
    }
    
    @Override
    protected Collection<X509CertSelector> getRecipientSelectors() {
        final X509CertSelector keyEncSelector = new X509CertSelector();
        keyEncSelector.setKeyUsage(new boolean[] { false, false, true });
        final X509CertSelector dataEncSelector = new X509CertSelector();
        dataEncSelector.setKeyUsage(new boolean[] { false, false, false, true });
        final X509CertSelector caSelector = new X509CertSelector();
        caSelector.setBasicConstraints(0);
        return Arrays.asList(keyEncSelector, dataEncSelector, caSelector);
    }
}
