package org.apache.tomcat.util.descriptor.web;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.juli.logging.Log;
import javax.servlet.annotation.ServletSecurity;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import javax.servlet.HttpConstraintElement;
import javax.servlet.HttpMethodConstraintElement;
import java.util.HashSet;
import javax.servlet.ServletSecurityElement;
import java.nio.charset.Charset;
import org.apache.tomcat.util.res.StringManager;
import java.io.Serializable;

public class SecurityConstraint extends XmlEncodingBase implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final String ROLE_ALL_ROLES = "*";
    public static final String ROLE_ALL_AUTHENTICATED_USERS = "**";
    private static final StringManager sm;
    private boolean allRoles;
    private boolean authenticatedUsers;
    private boolean authConstraint;
    private String[] authRoles;
    private SecurityCollection[] collections;
    private String displayName;
    private String userConstraint;
    
    public SecurityConstraint() {
        this.allRoles = false;
        this.authenticatedUsers = false;
        this.authConstraint = false;
        this.authRoles = new String[0];
        this.collections = new SecurityCollection[0];
        this.displayName = null;
        this.userConstraint = "NONE";
    }
    
    public boolean getAllRoles() {
        return this.allRoles;
    }
    
    public boolean getAuthenticatedUsers() {
        return this.authenticatedUsers;
    }
    
    public boolean getAuthConstraint() {
        return this.authConstraint;
    }
    
    public void setAuthConstraint(final boolean authConstraint) {
        this.authConstraint = authConstraint;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public String getUserConstraint() {
        return this.userConstraint;
    }
    
    public void setUserConstraint(final String userConstraint) {
        if (userConstraint != null) {
            this.userConstraint = userConstraint;
        }
    }
    
    public void treatAllAuthenticatedUsersAsApplicationRole() {
        if (this.authenticatedUsers) {
            this.authenticatedUsers = false;
            final String[] results = new String[this.authRoles.length + 1];
            for (int i = 0; i < this.authRoles.length; ++i) {
                results[i] = this.authRoles[i];
            }
            results[this.authRoles.length] = "**";
            this.authRoles = results;
            this.authConstraint = true;
        }
    }
    
    public void addAuthRole(final String authRole) {
        if (authRole == null) {
            return;
        }
        if ("*".equals(authRole)) {
            this.allRoles = true;
            return;
        }
        if ("**".equals(authRole)) {
            this.authenticatedUsers = true;
            return;
        }
        final String[] results = new String[this.authRoles.length + 1];
        for (int i = 0; i < this.authRoles.length; ++i) {
            results[i] = this.authRoles[i];
        }
        results[this.authRoles.length] = authRole;
        this.authRoles = results;
        this.authConstraint = true;
    }
    
    @Override
    public void setCharset(final Charset charset) {
        super.setCharset(charset);
        for (final SecurityCollection collection : this.collections) {
            collection.setCharset(this.getCharset());
        }
    }
    
    public void addCollection(final SecurityCollection collection) {
        if (collection == null) {
            return;
        }
        collection.setCharset(this.getCharset());
        final SecurityCollection[] results = new SecurityCollection[this.collections.length + 1];
        for (int i = 0; i < this.collections.length; ++i) {
            results[i] = this.collections[i];
        }
        results[this.collections.length] = collection;
        this.collections = results;
    }
    
    public boolean findAuthRole(final String role) {
        if (role == null) {
            return false;
        }
        for (final String authRole : this.authRoles) {
            if (role.equals(authRole)) {
                return true;
            }
        }
        return false;
    }
    
    public String[] findAuthRoles() {
        return this.authRoles;
    }
    
    public SecurityCollection findCollection(final String name) {
        if (name == null) {
            return null;
        }
        for (final SecurityCollection collection : this.collections) {
            if (name.equals(collection.getName())) {
                return collection;
            }
        }
        return null;
    }
    
    public SecurityCollection[] findCollections() {
        return this.collections;
    }
    
    public boolean included(final String uri, final String method) {
        if (method == null) {
            return false;
        }
        for (final SecurityCollection collection : this.collections) {
            if (collection.findMethod(method)) {
                final String[] arr$2;
                final String[] patterns = arr$2 = collection.findPatterns();
                for (final String pattern : arr$2) {
                    if (this.matchPattern(uri, pattern)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void removeAuthRole(final String authRole) {
        if (authRole == null) {
            return;
        }
        if ("*".equals(authRole)) {
            this.allRoles = false;
            return;
        }
        if ("**".equals(authRole)) {
            this.authenticatedUsers = false;
            return;
        }
        int n = -1;
        for (int i = 0; i < this.authRoles.length; ++i) {
            if (this.authRoles[i].equals(authRole)) {
                n = i;
                break;
            }
        }
        if (n >= 0) {
            int j = 0;
            final String[] results = new String[this.authRoles.length - 1];
            for (int k = 0; k < this.authRoles.length; ++k) {
                if (k != n) {
                    results[j++] = this.authRoles[k];
                }
            }
            this.authRoles = results;
        }
    }
    
    public void removeCollection(final SecurityCollection collection) {
        if (collection == null) {
            return;
        }
        int n = -1;
        for (int i = 0; i < this.collections.length; ++i) {
            if (this.collections[i].equals(collection)) {
                n = i;
                break;
            }
        }
        if (n >= 0) {
            int j = 0;
            final SecurityCollection[] results = new SecurityCollection[this.collections.length - 1];
            for (int k = 0; k < this.collections.length; ++k) {
                if (k != n) {
                    results[j++] = this.collections[k];
                }
            }
            this.collections = results;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SecurityConstraint[");
        for (int i = 0; i < this.collections.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.collections[i].getName());
        }
        sb.append(']');
        return sb.toString();
    }
    
    private boolean matchPattern(String path, String pattern) {
        if (path == null || path.length() == 0) {
            path = "/";
        }
        if (pattern == null || pattern.length() == 0) {
            pattern = "/";
        }
        if (path.equals(pattern)) {
            return true;
        }
        if (pattern.startsWith("/") && pattern.endsWith("/*")) {
            pattern = pattern.substring(0, pattern.length() - 2);
            if (pattern.length() == 0) {
                return true;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            while (!pattern.equals(path)) {
                final int slash = path.lastIndexOf(47);
                if (slash <= 0) {
                    return false;
                }
                path = path.substring(0, slash);
            }
            return true;
        }
        else {
            if (pattern.startsWith("*.")) {
                final int slash = path.lastIndexOf(47);
                final int period = path.lastIndexOf(46);
                return slash >= 0 && period > slash && path.endsWith(pattern.substring(1));
            }
            return pattern.equals("/");
        }
    }
    
    public static SecurityConstraint[] createConstraints(final ServletSecurityElement element, final String urlPattern) {
        final Set<SecurityConstraint> result = new HashSet<SecurityConstraint>();
        final Collection<HttpMethodConstraintElement> methods = element.getHttpMethodConstraints();
        for (final HttpMethodConstraintElement methodElement : methods) {
            final SecurityConstraint constraint = createConstraint((HttpConstraintElement)methodElement, urlPattern, true);
            final SecurityCollection collection = constraint.findCollections()[0];
            collection.addMethod(methodElement.getMethodName());
            result.add(constraint);
        }
        final SecurityConstraint constraint2 = createConstraint((HttpConstraintElement)element, urlPattern, false);
        if (constraint2 != null) {
            final SecurityCollection collection2 = constraint2.findCollections()[0];
            for (final String name : element.getMethodNames()) {
                collection2.addOmittedMethod(name);
            }
            result.add(constraint2);
        }
        return result.toArray(new SecurityConstraint[0]);
    }
    
    private static SecurityConstraint createConstraint(final HttpConstraintElement element, final String urlPattern, final boolean alwaysCreate) {
        final SecurityConstraint constraint = new SecurityConstraint();
        final SecurityCollection collection = new SecurityCollection();
        boolean create = alwaysCreate;
        if (element.getTransportGuarantee() != ServletSecurity.TransportGuarantee.NONE) {
            constraint.setUserConstraint(element.getTransportGuarantee().name());
            create = true;
        }
        if (element.getRolesAllowed().length > 0) {
            final String[] arr$;
            final String[] roles = arr$ = element.getRolesAllowed();
            for (final String role : arr$) {
                constraint.addAuthRole(role);
            }
            create = true;
        }
        if (element.getEmptyRoleSemantic() != ServletSecurity.EmptyRoleSemantic.PERMIT) {
            constraint.setAuthConstraint(true);
            create = true;
        }
        if (create) {
            collection.addPattern(urlPattern);
            constraint.addCollection(collection);
            return constraint;
        }
        return null;
    }
    
    public static SecurityConstraint[] findUncoveredHttpMethods(final SecurityConstraint[] constraints, final boolean denyUncoveredHttpMethods, final Log log) {
        final Set<String> coveredPatterns = new HashSet<String>();
        final Map<String, Set<String>> urlMethodMap = new HashMap<String, Set<String>>();
        final Map<String, Set<String>> urlOmittedMethodMap = new HashMap<String, Set<String>>();
        final List<SecurityConstraint> newConstraints = new ArrayList<SecurityConstraint>();
        for (final SecurityConstraint constraint : constraints) {
            final SecurityCollection[] arr$2;
            final SecurityCollection[] collections = arr$2 = constraint.findCollections();
            for (final SecurityCollection collection : arr$2) {
                final String[] patterns = collection.findPatterns();
                final String[] methods = collection.findMethods();
                final String[] omittedMethods = collection.findOmittedMethods();
                if (methods.length == 0 && omittedMethods.length == 0) {
                    coveredPatterns.addAll(Arrays.asList(patterns));
                }
                else {
                    List<String> omNew = null;
                    if (omittedMethods.length != 0) {
                        omNew = Arrays.asList(omittedMethods);
                    }
                    for (final String pattern : patterns) {
                        if (!coveredPatterns.contains(pattern)) {
                            if (methods.length == 0) {
                                Set<String> om = urlOmittedMethodMap.get(pattern);
                                if (om == null) {
                                    om = new HashSet<String>();
                                    urlOmittedMethodMap.put(pattern, om);
                                    om.addAll(omNew);
                                }
                                else {
                                    om.retainAll(omNew);
                                }
                            }
                            else {
                                Set<String> m = urlMethodMap.get(pattern);
                                if (m == null) {
                                    m = new HashSet<String>();
                                    urlMethodMap.put(pattern, m);
                                }
                                m.addAll(Arrays.asList(methods));
                            }
                        }
                    }
                }
            }
        }
        for (final Map.Entry<String, Set<String>> entry : urlMethodMap.entrySet()) {
            final String pattern2 = entry.getKey();
            if (coveredPatterns.contains(pattern2)) {
                urlOmittedMethodMap.remove(pattern2);
            }
            else {
                final Set<String> omittedMethods2 = urlOmittedMethodMap.remove(pattern2);
                final Set<String> methods2 = entry.getValue();
                if (omittedMethods2 == null) {
                    final StringBuilder msg = new StringBuilder();
                    for (final String method : methods2) {
                        msg.append(method);
                        msg.append(' ');
                    }
                    if (denyUncoveredHttpMethods) {
                        log.info((Object)SecurityConstraint.sm.getString("securityConstraint.uncoveredHttpMethodFix", new Object[] { pattern2, msg.toString().trim() }));
                        final SecurityCollection collection2 = new SecurityCollection();
                        for (final String method2 : methods2) {
                            collection2.addOmittedMethod(method2);
                        }
                        collection2.addPatternDecoded(pattern2);
                        collection2.setName("deny-uncovered-http-methods");
                        final SecurityConstraint constraint2 = new SecurityConstraint();
                        constraint2.setAuthConstraint(true);
                        constraint2.addCollection(collection2);
                        newConstraints.add(constraint2);
                    }
                    else {
                        log.error((Object)SecurityConstraint.sm.getString("securityConstraint.uncoveredHttpMethod", new Object[] { pattern2, msg.toString().trim() }));
                    }
                }
                else {
                    omittedMethods2.removeAll(methods2);
                    handleOmittedMethods(omittedMethods2, pattern2, denyUncoveredHttpMethods, newConstraints, log);
                }
            }
        }
        for (final Map.Entry<String, Set<String>> entry : urlOmittedMethodMap.entrySet()) {
            final String pattern2 = entry.getKey();
            if (coveredPatterns.contains(pattern2)) {
                continue;
            }
            handleOmittedMethods(entry.getValue(), pattern2, denyUncoveredHttpMethods, newConstraints, log);
        }
        return newConstraints.toArray(new SecurityConstraint[0]);
    }
    
    private static void handleOmittedMethods(final Set<String> omittedMethods, final String pattern, final boolean denyUncoveredHttpMethods, final List<SecurityConstraint> newConstraints, final Log log) {
        if (omittedMethods.size() > 0) {
            final StringBuilder msg = new StringBuilder();
            for (final String method : omittedMethods) {
                msg.append(method);
                msg.append(' ');
            }
            if (denyUncoveredHttpMethods) {
                log.info((Object)SecurityConstraint.sm.getString("securityConstraint.uncoveredHttpOmittedMethodFix", new Object[] { pattern, msg.toString().trim() }));
                final SecurityCollection collection = new SecurityCollection();
                for (final String method2 : omittedMethods) {
                    collection.addMethod(method2);
                }
                collection.addPatternDecoded(pattern);
                collection.setName("deny-uncovered-http-methods");
                final SecurityConstraint constraint = new SecurityConstraint();
                constraint.setAuthConstraint(true);
                constraint.addCollection(collection);
                newConstraints.add(constraint);
            }
            else {
                log.error((Object)SecurityConstraint.sm.getString("securityConstraint.uncoveredHttpOmittedMethod", new Object[] { pattern, msg.toString().trim() }));
            }
        }
    }
    
    static {
        sm = StringManager.getManager(Constants.PACKAGE_NAME);
    }
}
