package com.unboundid.ldap.sdk.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPExtendedOperationException;
import com.unboundid.ldap.sdk.InternalSDKHelper;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import com.unboundid.util.ssl.SSLUtil;
import javax.net.ssl.SSLContext;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Control;
import javax.net.ssl.SSLSocketFactory;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class StartTLSExtendedRequest extends ExtendedRequest
{
    public static final String STARTTLS_REQUEST_OID = "1.3.6.1.4.1.1466.20037";
    private static final long serialVersionUID = -3234194603452821233L;
    private final SSLSocketFactory sslSocketFactory;
    
    public StartTLSExtendedRequest() throws LDAPException {
        this((SSLSocketFactory)null, null);
    }
    
    public StartTLSExtendedRequest(final Control[] controls) throws LDAPException {
        this((SSLSocketFactory)null, controls);
    }
    
    public StartTLSExtendedRequest(final SSLContext sslContext) throws LDAPException {
        this(sslContext, null);
    }
    
    public StartTLSExtendedRequest(final SSLSocketFactory sslSocketFactory) throws LDAPException {
        this(sslSocketFactory, null);
    }
    
    public StartTLSExtendedRequest(final SSLContext sslContext, final Control[] controls) throws LDAPException {
        super("1.3.6.1.4.1.1466.20037", controls);
        if (sslContext == null) {
            try {
                final SSLContext ctx = SSLContext.getInstance(SSLUtil.getDefaultSSLProtocol());
                ctx.init(null, null, null);
                this.sslSocketFactory = ctx.getSocketFactory();
                return;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, ExtOpMessages.ERR_STARTTLS_REQUEST_CANNOT_CREATE_DEFAULT_CONTEXT.get(e), e);
            }
        }
        this.sslSocketFactory = sslContext.getSocketFactory();
    }
    
    public StartTLSExtendedRequest(final SSLSocketFactory sslSocketFactory, final Control[] controls) throws LDAPException {
        super("1.3.6.1.4.1.1466.20037", controls);
        if (sslSocketFactory == null) {
            try {
                final SSLContext ctx = SSLContext.getInstance(SSLUtil.getDefaultSSLProtocol());
                ctx.init(null, null, null);
                this.sslSocketFactory = ctx.getSocketFactory();
                return;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, ExtOpMessages.ERR_STARTTLS_REQUEST_CANNOT_CREATE_DEFAULT_CONTEXT.get(e), e);
            }
        }
        this.sslSocketFactory = sslSocketFactory;
    }
    
    public StartTLSExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        this(extendedRequest.getControls());
        if (extendedRequest.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STARTTLS_REQUEST_HAS_VALUE.get());
        }
    }
    
    public ExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        InternalSDKHelper.setSoTimeout(connection, 50);
        final ExtendedResult result = super.process(connection, depth);
        if (result.getResultCode() == ResultCode.SUCCESS) {
            InternalSDKHelper.convertToTLS(connection, this.sslSocketFactory);
            return result;
        }
        throw new LDAPExtendedOperationException(result);
    }
    
    @Override
    public StartTLSExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public StartTLSExtendedRequest duplicate(final Control[] controls) {
        try {
            final StartTLSExtendedRequest r = new StartTLSExtendedRequest(this.sslSocketFactory, controls);
            r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
            return r;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_START_TLS.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("StartTLSExtendedRequest(");
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append("controls={");
            for (int i = 0; i < controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
