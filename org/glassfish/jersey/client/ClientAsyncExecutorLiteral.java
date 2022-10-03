package org.glassfish.jersey.client;

import org.glassfish.jersey.internal.inject.AnnotationLiteral;

public final class ClientAsyncExecutorLiteral extends AnnotationLiteral<ClientAsyncExecutor> implements ClientAsyncExecutor
{
    public static final ClientAsyncExecutor INSTANCE;
    
    private ClientAsyncExecutorLiteral() {
    }
    
    static {
        INSTANCE = new ClientAsyncExecutorLiteral();
    }
}
