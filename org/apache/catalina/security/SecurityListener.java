package org.apache.catalina.security;

import org.apache.juli.logging.LogFactory;
import java.util.Collection;
import org.apache.tomcat.util.buf.StringUtils;
import java.util.Locale;
import org.apache.catalina.LifecycleEvent;
import java.util.HashSet;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.LifecycleListener;

public class SecurityListener implements LifecycleListener
{
    private static final Log log;
    private static final StringManager sm;
    private static final String UMASK_PROPERTY_NAME = "org.apache.catalina.security.SecurityListener.UMASK";
    private static final String UMASK_FORMAT = "%04o";
    private final Set<String> checkedOsUsers;
    private Integer minimumUmask;
    
    public SecurityListener() {
        this.checkedOsUsers = new HashSet<String>();
        this.minimumUmask = 7;
        this.checkedOsUsers.add("root");
    }
    
    @Override
    public void lifecycleEvent(final LifecycleEvent event) {
        if (event.getType().equals("before_init")) {
            this.doChecks();
        }
    }
    
    public void setCheckedOsUsers(final String userNameList) {
        if (userNameList == null || userNameList.length() == 0) {
            this.checkedOsUsers.clear();
        }
        else {
            final String[] arr$;
            final String[] userNames = arr$ = userNameList.split(",");
            for (final String userName : arr$) {
                if (userName.length() > 0) {
                    this.checkedOsUsers.add(userName.toLowerCase(Locale.getDefault()));
                }
            }
        }
    }
    
    public String getCheckedOsUsers() {
        return StringUtils.join((Collection)this.checkedOsUsers);
    }
    
    public void setMinimumUmask(final String umask) {
        if (umask == null || umask.length() == 0) {
            this.minimumUmask = 0;
        }
        else {
            this.minimumUmask = Integer.valueOf(umask, 8);
        }
    }
    
    public String getMinimumUmask() {
        return String.format("%04o", this.minimumUmask);
    }
    
    protected void doChecks() {
        this.checkOsUser();
        this.checkUmask();
    }
    
    protected void checkOsUser() {
        final String userName = System.getProperty("user.name");
        if (userName != null) {
            final String userNameLC = userName.toLowerCase(Locale.getDefault());
            if (this.checkedOsUsers.contains(userNameLC)) {
                throw new Error(SecurityListener.sm.getString("SecurityListener.checkUserWarning", new Object[] { userName }));
            }
        }
    }
    
    protected void checkUmask() {
        final String prop = System.getProperty("org.apache.catalina.security.SecurityListener.UMASK");
        Integer umask = null;
        if (prop != null) {
            try {
                umask = Integer.valueOf(prop, 8);
            }
            catch (final NumberFormatException nfe) {
                SecurityListener.log.warn((Object)SecurityListener.sm.getString("SecurityListener.checkUmaskParseFail", new Object[] { prop }));
            }
        }
        if (umask == null) {
            if ("\r\n".equals(System.lineSeparator())) {
                if (SecurityListener.log.isDebugEnabled()) {
                    SecurityListener.log.debug((Object)SecurityListener.sm.getString("SecurityListener.checkUmaskSkip"));
                }
                return;
            }
            if (this.minimumUmask > 0) {
                SecurityListener.log.warn((Object)SecurityListener.sm.getString("SecurityListener.checkUmaskNone", new Object[] { "org.apache.catalina.security.SecurityListener.UMASK", this.getMinimumUmask() }));
            }
        }
        else if ((umask & this.minimumUmask) != this.minimumUmask) {
            throw new Error(SecurityListener.sm.getString("SecurityListener.checkUmaskFail", new Object[] { String.format("%04o", umask), this.getMinimumUmask() }));
        }
    }
    
    static {
        log = LogFactory.getLog((Class)SecurityListener.class);
        sm = StringManager.getManager("org.apache.catalina.security");
    }
}
