package org.glassfish.jersey.server;

import org.glassfish.jersey.internal.inject.AnnotationLiteral;

public final class BackgroundSchedulerLiteral extends AnnotationLiteral<BackgroundScheduler> implements BackgroundScheduler
{
    public static final BackgroundScheduler INSTANCE;
    
    private BackgroundSchedulerLiteral() {
    }
    
    static {
        INSTANCE = new BackgroundSchedulerLiteral();
    }
}
