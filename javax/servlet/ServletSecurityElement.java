package javax.servlet;

import java.util.Iterator;
import java.util.HashSet;
import javax.servlet.annotation.HttpMethodConstraint;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.annotation.ServletSecurity;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServletSecurityElement extends HttpConstraintElement
{
    private final Map<String, HttpMethodConstraintElement> methodConstraints;
    
    public ServletSecurityElement() {
        this.methodConstraints = new HashMap<String, HttpMethodConstraintElement>();
    }
    
    public ServletSecurityElement(final HttpConstraintElement httpConstraintElement) {
        this(httpConstraintElement, null);
    }
    
    public ServletSecurityElement(final Collection<HttpMethodConstraintElement> httpMethodConstraints) {
        this.methodConstraints = new HashMap<String, HttpMethodConstraintElement>();
        this.addHttpMethodConstraints(httpMethodConstraints);
    }
    
    public ServletSecurityElement(final HttpConstraintElement httpConstraintElement, final Collection<HttpMethodConstraintElement> httpMethodConstraints) {
        super(httpConstraintElement.getEmptyRoleSemantic(), httpConstraintElement.getTransportGuarantee(), httpConstraintElement.getRolesAllowed());
        this.methodConstraints = new HashMap<String, HttpMethodConstraintElement>();
        this.addHttpMethodConstraints(httpMethodConstraints);
    }
    
    public ServletSecurityElement(final ServletSecurity annotation) {
        this(new HttpConstraintElement(annotation.value().value(), annotation.value().transportGuarantee(), annotation.value().rolesAllowed()));
        final List<HttpMethodConstraintElement> l = new ArrayList<HttpMethodConstraintElement>();
        final HttpMethodConstraint[] constraints = annotation.httpMethodConstraints();
        if (constraints != null) {
            for (final HttpMethodConstraint constraint : constraints) {
                final HttpMethodConstraintElement e = new HttpMethodConstraintElement(constraint.value(), new HttpConstraintElement(constraint.emptyRoleSemantic(), constraint.transportGuarantee(), constraint.rolesAllowed()));
                l.add(e);
            }
        }
        this.addHttpMethodConstraints(l);
    }
    
    public Collection<HttpMethodConstraintElement> getHttpMethodConstraints() {
        final Collection<HttpMethodConstraintElement> result = new HashSet<HttpMethodConstraintElement>(this.methodConstraints.values());
        return result;
    }
    
    public Collection<String> getMethodNames() {
        final Collection<String> result = new HashSet<String>(this.methodConstraints.keySet());
        return result;
    }
    
    private void addHttpMethodConstraints(final Collection<HttpMethodConstraintElement> httpMethodConstraints) {
        if (httpMethodConstraints == null) {
            return;
        }
        for (final HttpMethodConstraintElement constraint : httpMethodConstraints) {
            final String method = constraint.getMethodName();
            if (this.methodConstraints.containsKey(method)) {
                throw new IllegalArgumentException("Duplicate method name: " + method);
            }
            this.methodConstraints.put(method, constraint);
        }
    }
}
