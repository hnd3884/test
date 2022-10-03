package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Operation;
import java.util.HashSet;
import org.glassfish.hk2.api.ValidationInformation;

public class ValidationInformationImpl implements ValidationInformation
{
    private static final String SERVICE_LOCATOR_IMPL = "org.jvnet.hk2.internal.ServiceLocatorImpl";
    private static final String VALIDATE_METHOD = "validate";
    private static final String CHECK_METHOD = "checkConfiguration";
    private static final String[] SKIP_ME;
    private static final HashSet<String> PACKAGES_TO_SKIP;
    private final Operation operation;
    private final ActiveDescriptor<?> candidate;
    private final Injectee injectee;
    private final Filter filter;
    
    public ValidationInformationImpl(final Operation operation, final ActiveDescriptor<?> candidate, final Injectee injectee, final Filter filter) {
        this.operation = operation;
        this.candidate = candidate;
        this.injectee = injectee;
        this.filter = filter;
    }
    
    public ValidationInformationImpl(final Operation operation, final ActiveDescriptor<?> candidate) {
        this(operation, candidate, null, null);
    }
    
    public Operation getOperation() {
        return this.operation;
    }
    
    public ActiveDescriptor<?> getCandidate() {
        return this.candidate;
    }
    
    public Injectee getInjectee() {
        return this.injectee;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    private String getPackage(final String name) {
        final int index = name.lastIndexOf(46);
        if (index < 0) {
            return name;
        }
        return name.substring(0, index);
    }
    
    public StackTraceElement getCaller() {
        final StackTraceElement[] frames = Thread.currentThread().getStackTrace();
        boolean foundValidationCaller = false;
        for (final StackTraceElement e : frames) {
            if (!foundValidationCaller) {
                if ("org.jvnet.hk2.internal.ServiceLocatorImpl".equals(e.getClassName()) && ("validate".equals(e.getMethodName()) || "checkConfiguration".equals(e.getMethodName()))) {
                    foundValidationCaller = true;
                }
            }
            else {
                final String pack = this.getPackage(e.getClassName());
                if (!ValidationInformationImpl.PACKAGES_TO_SKIP.contains(pack)) {
                    return e;
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "ValidationInformation(" + this.operation + "," + this.candidate + "," + this.injectee + "," + this.filter + "," + System.identityHashCode(this) + ")";
    }
    
    static {
        SKIP_ME = new String[] { "org.jvnet.hk2.internal", "org.jvnet.hk2.external.generator", "org.glassfish.hk2.extension", "org.glassfish.hk2.api", "org.glassfish.hk2.internal", "org.glassfish.hk2.utilities", "org.glassfish.hk2.utilities.binding", "org.jvnet.hk2.annotations", "org.glassfish.hk2.utilities.cache", "org.glassfish.hk2.utilities.cache.internal", "org.glassfish.hk2.utilities.reflection", "org.jvnet.hk2.component", "java.util.concurrent" };
        PACKAGES_TO_SKIP = new HashSet<String>();
        for (final String pack : ValidationInformationImpl.SKIP_ME) {
            ValidationInformationImpl.PACKAGES_TO_SKIP.add(pack);
        }
    }
}
