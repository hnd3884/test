package javax.security.auth.login;

import sun.security.util.ResourcesMgr;
import java.util.Collections;
import java.util.Map;

public class AppConfigurationEntry
{
    private String loginModuleName;
    private LoginModuleControlFlag controlFlag;
    private Map<String, ?> options;
    
    public AppConfigurationEntry(final String loginModuleName, final LoginModuleControlFlag controlFlag, final Map<String, ?> map) {
        if (loginModuleName == null || loginModuleName.length() == 0 || (controlFlag != LoginModuleControlFlag.REQUIRED && controlFlag != LoginModuleControlFlag.REQUISITE && controlFlag != LoginModuleControlFlag.SUFFICIENT && controlFlag != LoginModuleControlFlag.OPTIONAL) || map == null) {
            throw new IllegalArgumentException();
        }
        this.loginModuleName = loginModuleName;
        this.controlFlag = controlFlag;
        this.options = Collections.unmodifiableMap((Map<? extends String, ?>)map);
    }
    
    public String getLoginModuleName() {
        return this.loginModuleName;
    }
    
    public LoginModuleControlFlag getControlFlag() {
        return this.controlFlag;
    }
    
    public Map<String, ?> getOptions() {
        return this.options;
    }
    
    public static class LoginModuleControlFlag
    {
        private String controlFlag;
        public static final LoginModuleControlFlag REQUIRED;
        public static final LoginModuleControlFlag REQUISITE;
        public static final LoginModuleControlFlag SUFFICIENT;
        public static final LoginModuleControlFlag OPTIONAL;
        
        private LoginModuleControlFlag(final String controlFlag) {
            this.controlFlag = controlFlag;
        }
        
        @Override
        public String toString() {
            return ResourcesMgr.getString("LoginModuleControlFlag.") + this.controlFlag;
        }
        
        static {
            REQUIRED = new LoginModuleControlFlag("required");
            REQUISITE = new LoginModuleControlFlag("requisite");
            SUFFICIENT = new LoginModuleControlFlag("sufficient");
            OPTIONAL = new LoginModuleControlFlag("optional");
        }
    }
}
