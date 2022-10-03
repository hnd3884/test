package com.sun.jndi.cosnaming;

import javax.naming.spi.NamingManager;
import com.sun.jndi.toolkit.corba.CorbaUtils;
import javax.naming.Name;
import javax.naming.CompositeName;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.Context;
import java.util.Hashtable;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;
import org.omg.CosNaming.NamingContext;
import javax.naming.ContextNotEmptyException;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import javax.naming.NameAlreadyBoundException;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import javax.naming.InvalidNameException;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import javax.naming.CannotProceedException;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import javax.naming.NameNotFoundException;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import javax.naming.NamingException;
import org.omg.CosNaming.NameComponent;

public final class ExceptionMapper
{
    private static final boolean debug = false;
    
    private ExceptionMapper() {
    }
    
    public static final NamingException mapException(final Exception rootCause, final CNCtx resolvedObj, final NameComponent[] array) throws NamingException {
        if (rootCause instanceof NamingException) {
            return (NamingException)rootCause;
        }
        if (rootCause instanceof RuntimeException) {
            throw (RuntimeException)rootCause;
        }
        NamingException ex;
        if (rootCause instanceof NotFound) {
            if (resolvedObj.federation) {
                return tryFed((NotFound)rootCause, resolvedObj, array);
            }
            ex = new NameNotFoundException();
        }
        else if (rootCause instanceof CannotProceed) {
            ex = new CannotProceedException();
            final NamingContext cxt = ((CannotProceed)rootCause).cxt;
            final NameComponent[] rest_of_name = ((CannotProceed)rootCause).rest_of_name;
            if (array != null && array.length > rest_of_name.length) {
                final NameComponent[] array2 = new NameComponent[array.length - rest_of_name.length];
                System.arraycopy(array, 0, array2, 0, array2.length);
                ex.setResolvedObj(new CNCtx(resolvedObj._orb, resolvedObj.orbTracker, cxt, resolvedObj._env, resolvedObj.makeFullName(array2)));
            }
            else {
                ex.setResolvedObj(resolvedObj);
            }
            ex.setRemainingName(CNNameParser.cosNameToName(rest_of_name));
        }
        else if (rootCause instanceof InvalidName) {
            ex = new InvalidNameException();
        }
        else if (rootCause instanceof AlreadyBound) {
            ex = new NameAlreadyBoundException();
        }
        else if (rootCause instanceof NotEmpty) {
            ex = new ContextNotEmptyException();
        }
        else {
            ex = new NamingException("Unknown reasons");
        }
        ex.setRootCause(rootCause);
        return ex;
    }
    
    private static final NamingException tryFed(final NotFound notFound, final CNCtx altNameCtx, final NameComponent[] array) throws NamingException {
        NameComponent[] rest_of_name = notFound.rest_of_name;
        if (rest_of_name.length == 1 && array != null) {
            final NameComponent nameComponent = array[array.length - 1];
            if (!rest_of_name[0].id.equals(nameComponent.id) || rest_of_name[0].kind == null || !rest_of_name[0].kind.equals(nameComponent.kind)) {
                final NameNotFoundException ex = new NameNotFoundException();
                ex.setRemainingName(CNNameParser.cosNameToName(rest_of_name));
                ex.setRootCause(notFound);
                throw ex;
            }
        }
        NameComponent[] array2 = null;
        if (array != null && array.length >= rest_of_name.length) {
            int n;
            if (notFound.why == NotFoundReason.not_context) {
                n = array.length - (rest_of_name.length - 1);
                if (rest_of_name.length == 1) {
                    rest_of_name = null;
                }
                else {
                    final NameComponent[] array3 = new NameComponent[rest_of_name.length - 1];
                    System.arraycopy(rest_of_name, 1, array3, 0, array3.length);
                    rest_of_name = array3;
                }
            }
            else {
                n = array.length - rest_of_name.length;
            }
            if (n > 0) {
                array2 = new NameComponent[n];
                System.arraycopy(array, 0, array2, 0, n);
            }
        }
        final CannotProceedException ex2 = new CannotProceedException();
        ex2.setRootCause(notFound);
        if (rest_of_name != null && rest_of_name.length > 0) {
            ex2.setRemainingName(CNNameParser.cosNameToName(rest_of_name));
        }
        ex2.setEnvironment(altNameCtx._env);
        final Object o = (array2 != null) ? altNameCtx.callResolve(array2) : altNameCtx;
        if (o instanceof Context) {
            final Reference resolvedObj = new Reference("java.lang.Object", new RefAddr("nns") {
                private static final long serialVersionUID = 669984699392133792L;
                
                @Override
                public Object getContent() {
                    return o;
                }
            });
            final CompositeName altName = new CompositeName();
            altName.add("");
            ex2.setResolvedObj(resolvedObj);
            ex2.setAltName(altName);
            ex2.setAltNameCtx((Context)o);
            return ex2;
        }
        final Name cosNameToName = CNNameParser.cosNameToName(array2);
        Object objectInstance = null;
        try {
            if (CorbaUtils.isObjectFactoryTrusted(o)) {
                objectInstance = NamingManager.getObjectInstance(o, cosNameToName, altNameCtx, altNameCtx._env);
            }
        }
        catch (final NamingException ex3) {
            throw ex3;
        }
        catch (final Exception rootCause) {
            final NamingException ex4 = new NamingException("problem generating object using object factory");
            ex4.setRootCause(rootCause);
            throw ex4;
        }
        if (objectInstance instanceof Context) {
            ex2.setResolvedObj(objectInstance);
        }
        else {
            cosNameToName.add("");
            ex2.setAltName(cosNameToName);
            ex2.setResolvedObj(new Reference("java.lang.Object", new RefAddr("nns") {
                private static final long serialVersionUID = -785132553978269772L;
                
                @Override
                public Object getContent() {
                    return objectInstance;
                }
            }));
            ex2.setAltNameCtx(altNameCtx);
        }
        return ex2;
    }
}
