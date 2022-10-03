package org.apache.tomcat.util.net.openssl;

import org.apache.juli.logging.LogFactory;
import java.util.Iterator;
import org.apache.tomcat.jni.SSLConf;
import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import java.io.Serializable;

public class OpenSSLConf implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final Log log;
    private static final StringManager sm;
    private final List<OpenSSLConfCmd> commands;
    
    public OpenSSLConf() {
        this.commands = new ArrayList<OpenSSLConfCmd>();
    }
    
    public void addCmd(final OpenSSLConfCmd cmd) {
        this.commands.add(cmd);
    }
    
    public List<OpenSSLConfCmd> getCommands() {
        return this.commands;
    }
    
    public boolean check(final long cctx) throws Exception {
        boolean result = true;
        for (final OpenSSLConfCmd cmd : this.commands) {
            final OpenSSLConfCmd command = cmd;
            final String name = cmd.getName();
            final String value = cmd.getValue();
            if (name == null) {
                OpenSSLConf.log.error((Object)OpenSSLConf.sm.getString("opensslconf.noCommandName", new Object[] { value }));
                result = false;
            }
            else {
                if (OpenSSLConf.log.isDebugEnabled()) {
                    OpenSSLConf.log.debug((Object)OpenSSLConf.sm.getString("opensslconf.checkCommand", new Object[] { name, value }));
                }
                int rc;
                try {
                    rc = SSLConf.check(cctx, name, value);
                }
                catch (final Exception e) {
                    OpenSSLConf.log.error((Object)OpenSSLConf.sm.getString("opensslconf.checkFailed"));
                    return false;
                }
                if (rc <= 0) {
                    OpenSSLConf.log.error((Object)OpenSSLConf.sm.getString("opensslconf.failedCommand", new Object[] { name, value, Integer.toString(rc) }));
                    result = false;
                }
                else {
                    if (!OpenSSLConf.log.isDebugEnabled()) {
                        continue;
                    }
                    OpenSSLConf.log.debug((Object)OpenSSLConf.sm.getString("opensslconf.resultCommand", new Object[] { name, value, Integer.toString(rc) }));
                }
            }
        }
        if (!result) {
            OpenSSLConf.log.error((Object)OpenSSLConf.sm.getString("opensslconf.checkFailed"));
        }
        return result;
    }
    
    public boolean apply(final long cctx, final long ctx) throws Exception {
        boolean result = true;
        SSLConf.assign(cctx, ctx);
        for (final OpenSSLConfCmd cmd : this.commands) {
            final OpenSSLConfCmd command = cmd;
            final String name = cmd.getName();
            final String value = cmd.getValue();
            if (name == null) {
                OpenSSLConf.log.error((Object)OpenSSLConf.sm.getString("opensslconf.noCommandName", new Object[] { value }));
                result = false;
            }
            else {
                if (OpenSSLConf.log.isDebugEnabled()) {
                    OpenSSLConf.log.debug((Object)OpenSSLConf.sm.getString("opensslconf.applyCommand", new Object[] { name, value }));
                }
                int rc;
                try {
                    rc = SSLConf.apply(cctx, name, value);
                }
                catch (final Exception e) {
                    OpenSSLConf.log.error((Object)OpenSSLConf.sm.getString("opensslconf.applyFailed"));
                    return false;
                }
                if (rc <= 0) {
                    OpenSSLConf.log.error((Object)OpenSSLConf.sm.getString("opensslconf.failedCommand", new Object[] { name, value, Integer.toString(rc) }));
                    result = false;
                }
                else {
                    if (!OpenSSLConf.log.isDebugEnabled()) {
                        continue;
                    }
                    OpenSSLConf.log.debug((Object)OpenSSLConf.sm.getString("opensslconf.resultCommand", new Object[] { name, value, Integer.toString(rc) }));
                }
            }
        }
        int rc = SSLConf.finish(cctx);
        if (rc <= 0) {
            OpenSSLConf.log.error((Object)OpenSSLConf.sm.getString("opensslconf.finishFailed", new Object[] { Integer.toString(rc) }));
            result = false;
        }
        if (!result) {
            OpenSSLConf.log.error((Object)OpenSSLConf.sm.getString("opensslconf.applyFailed"));
        }
        return result;
    }
    
    static {
        log = LogFactory.getLog((Class)OpenSSLConf.class);
        sm = StringManager.getManager((Class)OpenSSLConf.class);
    }
}
