package io.netty.handler.ssl;

public final class OpenSslContextOption<T> extends SslContextOption<T>
{
    public static final OpenSslContextOption<Boolean> USE_TASKS;
    public static final OpenSslContextOption<Boolean> TLS_FALSE_START;
    public static final OpenSslContextOption<OpenSslPrivateKeyMethod> PRIVATE_KEY_METHOD;
    public static final OpenSslContextOption<OpenSslAsyncPrivateKeyMethod> ASYNC_PRIVATE_KEY_METHOD;
    
    private OpenSslContextOption(final String name) {
        super(name);
    }
    
    static {
        USE_TASKS = new OpenSslContextOption<Boolean>("USE_TASKS");
        TLS_FALSE_START = new OpenSslContextOption<Boolean>("TLS_FALSE_START");
        PRIVATE_KEY_METHOD = new OpenSslContextOption<OpenSslPrivateKeyMethod>("PRIVATE_KEY_METHOD");
        ASYNC_PRIVATE_KEY_METHOD = new OpenSslContextOption<OpenSslAsyncPrivateKeyMethod>("ASYNC_PRIVATE_KEY_METHOD");
    }
}
