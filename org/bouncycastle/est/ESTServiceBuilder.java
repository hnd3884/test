package org.bouncycastle.est;

public class ESTServiceBuilder
{
    protected final String server;
    protected ESTClientProvider clientProvider;
    protected String label;
    
    public ESTServiceBuilder(final String server) {
        this.server = server;
    }
    
    public ESTServiceBuilder withLabel(final String label) {
        this.label = label;
        return this;
    }
    
    public ESTServiceBuilder withClientProvider(final ESTClientProvider clientProvider) {
        this.clientProvider = clientProvider;
        return this;
    }
    
    public ESTService build() {
        return new ESTService(this.server, this.label, this.clientProvider);
    }
}
