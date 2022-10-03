package org.owasp.esapi.reference.accesscontrol;

import java.util.HashSet;
import java.util.Arrays;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.owasp.esapi.errors.IntrusionException;
import java.util.Set;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AccessControlException;
import org.owasp.esapi.ESAPI;
import java.util.HashMap;
import org.owasp.esapi.Logger;
import java.util.Map;

public class FileBasedACRs
{
    private Map urlMap;
    private Map functionMap;
    private Map dataMap;
    private Map fileMap;
    private Map serviceMap;
    private Rule deny;
    private Logger logger;
    
    public FileBasedACRs() {
        this.urlMap = new HashMap();
        this.functionMap = new HashMap();
        this.dataMap = new HashMap();
        this.fileMap = new HashMap();
        this.serviceMap = new HashMap();
        this.deny = new Rule();
        this.logger = ESAPI.getLogger("FileBasedACRs");
    }
    
    public boolean isAuthorizedForURL(final String url) {
        if (this.urlMap == null || this.urlMap.isEmpty()) {
            this.urlMap = this.loadRules("URLAccessRules.txt");
        }
        return this.matchRule(this.urlMap, url);
    }
    
    public boolean isAuthorizedForFunction(final String functionName) throws AccessControlException {
        if (this.functionMap == null || this.functionMap.isEmpty()) {
            this.functionMap = this.loadRules("FunctionAccessRules.txt");
        }
        return this.matchRule(this.functionMap, functionName);
    }
    
    public boolean isAuthorizedForData(final String action, final Object data) throws AccessControlException {
        if (this.dataMap == null || this.dataMap.isEmpty()) {
            this.dataMap = this.loadDataRules("DataAccessRules.txt");
        }
        return this.matchRule(this.dataMap, (Class)data, action);
    }
    
    public boolean isAuthorizedForFile(final String filepath) throws AccessControlException {
        if (this.fileMap == null || this.fileMap.isEmpty()) {
            this.fileMap = this.loadRules("FileAccessRules.txt");
        }
        return this.matchRule(this.fileMap, filepath.replaceAll("\\\\", "/"));
    }
    
    public boolean isAuthorizedForService(final String serviceName) throws AccessControlException {
        if (this.serviceMap == null || this.serviceMap.isEmpty()) {
            this.serviceMap = this.loadRules("ServiceAccessRules.txt");
        }
        return this.matchRule(this.serviceMap, serviceName);
    }
    
    private boolean matchRule(final Map map, final String path) {
        final User user = ESAPI.authenticator().getCurrentUser();
        final Set roles = user.getRoles();
        final Rule rule = this.searchForRule(map, roles, path);
        return rule.allow;
    }
    
    private boolean matchRule(final Map map, final Class clazz, final String action) {
        final User user = ESAPI.authenticator().getCurrentUser();
        final Set roles = user.getRoles();
        final Rule rule = this.searchForRule(map, roles, clazz, action);
        return rule != null;
    }
    
    private Rule searchForRule(final Map map, final Set roles, final String path) {
        String part;
        final String canonical = part = ESAPI.encoder().canonicalize(path);
        if (part == null) {
            part = "";
        }
        while (part.endsWith("/")) {
            part = part.substring(0, part.length() - 1);
        }
        if (part.indexOf("..") != -1) {
            throw new IntrusionException("Attempt to manipulate access control path", "Attempt to manipulate access control path: " + path);
        }
        String extension = "";
        final int extIndex = part.lastIndexOf(".");
        if (extIndex != -1) {
            extension = part.substring(extIndex + 1);
        }
        Rule rule = map.get(part);
        if (rule == null) {
            rule = map.get(part + "/*");
        }
        if (rule == null) {
            rule = map.get("*." + extension);
        }
        if (rule != null && this.overlap(rule.roles, roles)) {
            return rule;
        }
        final int slash = part.lastIndexOf(47);
        if (slash == -1) {
            return this.deny;
        }
        part = part.substring(0, part.lastIndexOf(47));
        if (part.length() <= 1) {
            return this.deny;
        }
        return this.searchForRule(map, roles, part);
    }
    
    private Rule searchForRule(final Map map, final Set roles, final Class clazz, final String action) {
        final Rule rule = map.get(clazz);
        if (rule != null && this.overlap(rule.actions, action) && this.overlap(rule.roles, roles)) {
            return rule;
        }
        return null;
    }
    
    private boolean overlap(final Set ruleRoles, final Set userRoles) {
        if (ruleRoles.contains("any")) {
            return true;
        }
        for (final String role : userRoles) {
            if (ruleRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean overlap(final List ruleActions, final String action) {
        return ruleActions.contains(action);
    }
    
    private List validateRoles(final List roles) {
        final List ret = new ArrayList();
        for (int x = 0; x < roles.size(); ++x) {
            final String canonical = ESAPI.encoder().canonicalize(roles.get(x).trim());
            if (!ESAPI.validator().isValidInput("Validating user roles in FileBasedAccessController", canonical, "RoleName", 20, false)) {
                this.logger.warning(Logger.SECURITY_FAILURE, "Role: " + roles.get(x).trim() + " is invalid, so was not added to the list of roles for this Rule.");
            }
            else {
                ret.add(canonical.trim());
            }
        }
        return ret;
    }
    
    private Map loadRules(String ruleset) {
        ruleset = "fbac-policies/" + ruleset;
        final Map map = new HashMap();
        InputStream is = null;
        try {
            is = ESAPI.securityConfiguration().getResourceStream(ruleset);
            String line = "";
            while ((line = ESAPI.validator().safeReadLine(is, 500)) != null) {
                if (line.length() > 0 && line.charAt(0) != '#') {
                    final Rule rule = new Rule();
                    final String[] parts = line.split("\\|");
                    rule.path = parts[0].trim().replaceAll("\\\\", "/");
                    List roles = this.commaSplit(parts[1].trim().toLowerCase());
                    roles = this.validateRoles(roles);
                    for (int x = 0; x < roles.size(); ++x) {
                        rule.roles.add(roles.get(x).trim());
                    }
                    final String action = parts[2].trim();
                    rule.allow = action.equalsIgnoreCase("allow");
                    if (map.containsKey(rule.path)) {
                        this.logger.warning(Logger.SECURITY_FAILURE, "Problem in access control file. Duplicate rule ignored: " + rule);
                    }
                    else {
                        map.put(rule.path, rule);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Problem in access control file: " + ruleset, e);
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (final IOException e2) {
                this.logger.warning(Logger.SECURITY_FAILURE, "Failure closing access control file: " + ruleset, e2);
            }
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (final IOException e3) {
                this.logger.warning(Logger.SECURITY_FAILURE, "Failure closing access control file: " + ruleset, e3);
            }
        }
        return map;
    }
    
    private Map loadDataRules(String ruleset) {
        final Map map = new HashMap();
        InputStream is = null;
        try {
            ruleset = "fbac-policies/" + ruleset;
            is = ESAPI.securityConfiguration().getResourceStream(ruleset);
            String line = "";
            while ((line = ESAPI.validator().safeReadLine(is, 500)) != null) {
                if (line.length() > 0 && line.charAt(0) != '#') {
                    final Rule rule = new Rule();
                    final String[] parts = line.split("\\|");
                    rule.clazz = Class.forName(parts[0].trim());
                    List roles = this.commaSplit(parts[1].trim().toLowerCase());
                    roles = this.validateRoles(roles);
                    for (int x = 0; x < roles.size(); ++x) {
                        rule.roles.add(roles.get(x).trim());
                    }
                    final List action = this.commaSplit(parts[2].trim().toLowerCase());
                    for (int x2 = 0; x2 < action.size(); ++x2) {
                        rule.actions.add(action.get(x2).trim());
                    }
                    if (map.containsKey(rule.path)) {
                        this.logger.warning(Logger.SECURITY_FAILURE, "Problem in access control file. Duplicate rule ignored: " + rule);
                    }
                    else {
                        map.put(rule.clazz, rule);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Problem in access control file : " + ruleset, e);
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (final IOException e2) {
                this.logger.warning(Logger.SECURITY_FAILURE, "Failure closing access control file : " + ruleset, e2);
            }
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (final IOException e3) {
                this.logger.warning(Logger.SECURITY_FAILURE, "Failure closing access control file : " + ruleset, e3);
            }
        }
        return map;
    }
    
    private List commaSplit(final String input) {
        final String[] array = input.split(",");
        return Arrays.asList(array);
    }
    
    private class Rule
    {
        protected String path;
        protected Set roles;
        protected boolean allow;
        protected Class clazz;
        protected List actions;
        
        protected Rule() {
            this.path = "";
            this.roles = new HashSet();
            this.allow = false;
            this.clazz = null;
            this.actions = new ArrayList();
        }
        
        @Override
        public String toString() {
            return "URL:" + this.path + " | " + this.roles + " | " + (this.allow ? "allow" : "deny");
        }
    }
}
