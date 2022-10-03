package org.bouncycastle.est.jcajce;

import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.ESTClientSourceProvider;
import org.bouncycastle.est.ESTClient;
import java.util.Set;
import org.bouncycastle.est.ESTClientProvider;

class DefaultESTHttpClientProvider implements ESTClientProvider
{
    private final JsseHostnameAuthorizer hostNameAuthorizer;
    private final SSLSocketFactoryCreator socketFactoryCreator;
    private final int timeout;
    private final ChannelBindingProvider bindingProvider;
    private final Set<String> cipherSuites;
    private final Long absoluteLimit;
    private final boolean filterCipherSuites;
    
    public DefaultESTHttpClientProvider(final JsseHostnameAuthorizer hostNameAuthorizer, final SSLSocketFactoryCreator socketFactoryCreator, final int timeout, final ChannelBindingProvider bindingProvider, final Set<String> cipherSuites, final Long absoluteLimit, final boolean filterCipherSuites) {
        this.hostNameAuthorizer = hostNameAuthorizer;
        this.socketFactoryCreator = socketFactoryCreator;
        this.timeout = timeout;
        this.bindingProvider = bindingProvider;
        this.cipherSuites = cipherSuites;
        this.absoluteLimit = absoluteLimit;
        this.filterCipherSuites = filterCipherSuites;
    }
    
    public ESTClient makeClient() throws ESTException {
        try {
            return new DefaultESTClient(new DefaultESTClientSourceProvider(this.socketFactoryCreator.createFactory(), this.hostNameAuthorizer, this.timeout, this.bindingProvider, this.cipherSuites, this.absoluteLimit, this.filterCipherSuites));
        }
        catch (final Exception ex) {
            throw new ESTException(ex.getMessage(), ex.getCause());
        }
    }
    
    public boolean isTrusted() {
        return this.socketFactoryCreator.isTrusted();
    }
}
