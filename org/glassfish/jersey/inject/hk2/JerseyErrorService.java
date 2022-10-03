package org.glassfish.jersey.inject.hk2;

import javax.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorService;

public final class JerseyErrorService implements ErrorService
{
    public void onFailure(final ErrorInformation error) throws MultiException {
        String msg = null;
        switch (error.getErrorType()) {
            case FAILURE_TO_REIFY: {
                msg = LocalizationMessages.HK_2_REIFICATION_ERROR(error.getDescriptor().getImplementation(), this.printStackTrace((Throwable)error.getAssociatedException()));
                break;
            }
            default: {
                msg = LocalizationMessages.HK_2_UNKNOWN_ERROR(this.printStackTrace((Throwable)error.getAssociatedException()));
                break;
            }
        }
        try {
            Errors.warning((Object)error.getInjectee(), msg);
        }
        catch (final IllegalStateException ex) {
            Errors.process((Runnable)new Runnable() {
                @Override
                public void run() {
                    Errors.warning((Object)this, LocalizationMessages.HK_2_FAILURE_OUTSIDE_ERROR_SCOPE());
                    Errors.warning((Object)error.getInjectee(), msg);
                }
            });
        }
    }
    
    private String printStackTrace(final Throwable t) {
        final StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
    
    public static final class Binder extends AbstractBinder
    {
        protected void configure() {
            this.bind((Class)JerseyErrorService.class).to((Class)ErrorService.class).in((Class)Singleton.class);
        }
    }
}
