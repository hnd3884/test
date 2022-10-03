package org.apache.coyote.ajp;

import org.apache.coyote.UpgradeToken;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.coyote.Processor;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.coyote.UpgradeProtocol;
import java.net.InetAddress;
import org.apache.tomcat.util.net.AbstractEndpoint;
import java.util.regex.Pattern;
import org.apache.tomcat.util.res.StringManager;
import org.apache.coyote.AbstractProtocol;

public abstract class AbstractAjpProtocol<S> extends AbstractProtocol<S>
{
    protected static final StringManager sm;
    protected boolean ajpFlush;
    private boolean tomcatAuthentication;
    private boolean tomcatAuthorization;
    private String secret;
    private boolean secretRequired;
    private Pattern allowedRequestAttributesPattern;
    private int packetSize;
    
    public AbstractAjpProtocol(final AbstractEndpoint<S> endpoint) {
        super(endpoint);
        this.ajpFlush = true;
        this.tomcatAuthentication = true;
        this.tomcatAuthorization = false;
        this.secret = null;
        this.secretRequired = true;
        this.packetSize = 8192;
        this.setConnectionTimeout(-1);
        this.getEndpoint().setUseSendfile(false);
        this.getEndpoint().setAddress(InetAddress.getLoopbackAddress());
        final ConnectionHandler<S> cHandler = new ConnectionHandler<S>(this);
        this.setHandler(cHandler);
        this.getEndpoint().setHandler(cHandler);
    }
    
    @Override
    protected String getProtocolName() {
        return "Ajp";
    }
    
    @Override
    protected AbstractEndpoint<S> getEndpoint() {
        return super.getEndpoint();
    }
    
    @Override
    protected UpgradeProtocol getNegotiatedProtocol(final String name) {
        return null;
    }
    
    @Override
    protected UpgradeProtocol getUpgradeProtocol(final String name) {
        return null;
    }
    
    public boolean getAjpFlush() {
        return this.ajpFlush;
    }
    
    public void setAjpFlush(final boolean ajpFlush) {
        this.ajpFlush = ajpFlush;
    }
    
    public boolean getTomcatAuthentication() {
        return this.tomcatAuthentication;
    }
    
    public void setTomcatAuthentication(final boolean tomcatAuthentication) {
        this.tomcatAuthentication = tomcatAuthentication;
    }
    
    public boolean getTomcatAuthorization() {
        return this.tomcatAuthorization;
    }
    
    public void setTomcatAuthorization(final boolean tomcatAuthorization) {
        this.tomcatAuthorization = tomcatAuthorization;
    }
    
    public void setSecret(final String secret) {
        this.secret = secret;
    }
    
    protected String getSecret() {
        return this.secret;
    }
    
    @Deprecated
    public void setRequiredSecret(final String requiredSecret) {
        this.setSecret(requiredSecret);
    }
    
    @Deprecated
    protected String getRequiredSecret() {
        return this.getSecret();
    }
    
    public void setSecretRequired(final boolean secretRequired) {
        this.secretRequired = secretRequired;
    }
    
    public boolean getSecretRequired() {
        return this.secretRequired;
    }
    
    public void setAllowedRequestAttributesPattern(final String allowedRequestAttributesPattern) {
        this.allowedRequestAttributesPattern = Pattern.compile(allowedRequestAttributesPattern);
    }
    
    public String getAllowedRequestAttributesPattern() {
        return this.allowedRequestAttributesPattern.pattern();
    }
    
    protected Pattern getAllowedRequestAttributesPatternInternal() {
        return this.allowedRequestAttributesPattern;
    }
    
    public int getPacketSize() {
        return this.packetSize;
    }
    
    public void setPacketSize(final int packetSize) {
        if (packetSize < 8192) {
            this.packetSize = 8192;
        }
        else {
            this.packetSize = packetSize;
        }
    }
    
    @Override
    public void addSslHostConfig(final SSLHostConfig sslHostConfig) {
        this.getLog().warn((Object)AbstractAjpProtocol.sm.getString("ajpprotocol.noSSL", new Object[] { sslHostConfig.getHostName() }));
    }
    
    @Override
    public SSLHostConfig[] findSslHostConfigs() {
        return new SSLHostConfig[0];
    }
    
    @Override
    public void addUpgradeProtocol(final UpgradeProtocol upgradeProtocol) {
        this.getLog().warn((Object)AbstractAjpProtocol.sm.getString("ajpprotocol.noUpgrade", new Object[] { upgradeProtocol.getClass().getName() }));
    }
    
    @Override
    public UpgradeProtocol[] findUpgradeProtocols() {
        return new UpgradeProtocol[0];
    }
    
    @Override
    protected Processor createProcessor() {
        final AjpProcessor processor = new AjpProcessor(this.getPacketSize(), this.getEndpoint());
        processor.setAdapter(this.getAdapter());
        processor.setAjpFlush(this.getAjpFlush());
        processor.setTomcatAuthentication(this.getTomcatAuthentication());
        processor.setTomcatAuthorization(this.getTomcatAuthorization());
        processor.setSecret(this.secret);
        processor.setKeepAliveTimeout(this.getKeepAliveTimeout());
        processor.setClientCertProvider(this.getClientCertProvider());
        processor.setSendReasonPhrase(this.getSendReasonPhrase());
        processor.setAllowedRequestAttributesPattern(this.getAllowedRequestAttributesPatternInternal());
        return processor;
    }
    
    @Override
    protected Processor createUpgradeProcessor(final SocketWrapperBase<?> socket, final UpgradeToken upgradeToken) {
        throw new IllegalStateException(AbstractAjpProtocol.sm.getString("ajpprotocol.noUpgradeHandler", new Object[] { upgradeToken.getHttpUpgradeHandler().getClass().getName() }));
    }
    
    @Override
    public void start() throws Exception {
        if (this.getSecretRequired()) {
            final String secret = this.getSecret();
            if (secret == null || secret.length() == 0) {
                throw new IllegalArgumentException(AbstractAjpProtocol.sm.getString("ajpprotocol.noSecret"));
            }
        }
        super.start();
    }
    
    static {
        sm = StringManager.getManager((Class)AbstractAjpProtocol.class);
    }
}
