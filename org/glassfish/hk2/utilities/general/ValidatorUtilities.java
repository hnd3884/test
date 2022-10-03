package org.glassfish.hk2.utilities.general;

import java.lang.annotation.ElementType;
import javax.validation.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;
import javax.validation.MessageInterpolator;
import org.glassfish.hk2.utilities.general.internal.MessageInterpolatorImpl;
import javax.validation.Validation;
import org.hibernate.validator.HibernateValidator;
import javax.validation.Validator;
import javax.validation.TraversableResolver;

public class ValidatorUtilities
{
    private static final TraversableResolver TRAVERSABLE_RESOLVER;
    private static Validator validator;
    
    private static Validator initializeValidator() {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(HibernateValidator.class.getClassLoader());
            final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            final ValidatorContext validatorContext = validatorFactory.usingContext();
            validatorContext.messageInterpolator((MessageInterpolator)new MessageInterpolatorImpl());
            return validatorContext.traversableResolver(ValidatorUtilities.TRAVERSABLE_RESOLVER).getValidator();
        }
        finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
    
    public static synchronized Validator getValidator() {
        if (ValidatorUtilities.validator == null) {
            ValidatorUtilities.validator = AccessController.doPrivileged((PrivilegedAction<Validator>)new PrivilegedAction<Validator>() {
                @Override
                public Validator run() {
                    return initializeValidator();
                }
            });
        }
        if (ValidatorUtilities.validator == null) {
            throw new IllegalStateException("Could not find a javax.validator");
        }
        return ValidatorUtilities.validator;
    }
    
    static {
        TRAVERSABLE_RESOLVER = (TraversableResolver)new TraversableResolver() {
            public boolean isReachable(final Object traversableObject, final Path.Node traversableProperty, final Class<?> rootBeanType, final Path pathToTraversableObject, final ElementType elementType) {
                return true;
            }
            
            public boolean isCascadable(final Object traversableObject, final Path.Node traversableProperty, final Class<?> rootBeanType, final Path pathToTraversableObject, final ElementType elementType) {
                return true;
            }
        };
    }
}
