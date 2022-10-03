package org.apache.catalina.ha.session;

import org.apache.juli.logging.LogFactory;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Cluster;
import org.apache.catalina.Session;
import org.apache.catalina.session.ManagerBase;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.catalina.Manager;
import org.apache.catalina.session.PersistentManager;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import org.apache.tomcat.util.res.StringManager;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.juli.logging.Log;
import org.apache.catalina.ha.ClusterValve;
import org.apache.catalina.valves.ValveBase;

public class JvmRouteBinderValve extends ValveBase implements ClusterValve
{
    public static final Log log;
    protected CatalinaCluster cluster;
    protected static final StringManager sm;
    protected boolean enabled;
    protected long numberOfSessions;
    protected String sessionIdAttribute;
    
    public JvmRouteBinderValve() {
        super(true);
        this.enabled = true;
        this.numberOfSessions = 0L;
        this.sessionIdAttribute = "org.apache.catalina.ha.session.JvmRouteOriginalSessionID";
    }
    
    public String getSessionIdAttribute() {
        return this.sessionIdAttribute;
    }
    
    public void setSessionIdAttribute(final String sessionIdAttribute) {
        this.sessionIdAttribute = sessionIdAttribute;
    }
    
    public long getNumberOfSessions() {
        return this.numberOfSessions;
    }
    
    public boolean getEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        if (this.getEnabled() && request.getContext() != null && request.getContext().getDistributable() && !request.isAsyncDispatching()) {
            final Manager manager = request.getContext().getManager();
            if (manager != null && ((manager instanceof ClusterManager && this.getCluster() != null && this.getCluster().getManager(((ClusterManager)manager).getName()) != null) || manager instanceof PersistentManager)) {
                this.handlePossibleTurnover(request);
            }
        }
        this.getNext().invoke(request, response);
    }
    
    protected void handlePossibleTurnover(final Request request) {
        final String sessionID = request.getRequestedSessionId();
        if (sessionID != null) {
            final long t1 = System.currentTimeMillis();
            final String jvmRoute = this.getLocalJvmRoute(request);
            if (jvmRoute == null) {
                if (JvmRouteBinderValve.log.isDebugEnabled()) {
                    JvmRouteBinderValve.log.debug((Object)JvmRouteBinderValve.sm.getString("jvmRoute.missingJvmRouteAttribute"));
                }
                return;
            }
            this.handleJvmRoute(request, sessionID, jvmRoute);
            if (JvmRouteBinderValve.log.isDebugEnabled()) {
                final long t2 = System.currentTimeMillis();
                final long time = t2 - t1;
                JvmRouteBinderValve.log.debug((Object)JvmRouteBinderValve.sm.getString("jvmRoute.turnoverInfo", new Object[] { time }));
            }
        }
    }
    
    protected String getLocalJvmRoute(final Request request) {
        final Manager manager = this.getManager(request);
        if (manager instanceof ManagerBase) {
            return ((ManagerBase)manager).getJvmRoute();
        }
        return null;
    }
    
    protected Manager getManager(final Request request) {
        final Manager manager = request.getContext().getManager();
        if (JvmRouteBinderValve.log.isDebugEnabled()) {
            if (manager != null) {
                JvmRouteBinderValve.log.debug((Object)JvmRouteBinderValve.sm.getString("jvmRoute.foundManager", new Object[] { manager, request.getContext().getName() }));
            }
            else {
                JvmRouteBinderValve.log.debug((Object)JvmRouteBinderValve.sm.getString("jvmRoute.notFoundManager", new Object[] { request.getContext().getName() }));
            }
        }
        return manager;
    }
    
    public CatalinaCluster getCluster() {
        return this.cluster;
    }
    
    public void setCluster(final CatalinaCluster cluster) {
        this.cluster = cluster;
    }
    
    protected void handleJvmRoute(final Request request, final String sessionId, final String localJvmRoute) {
        String requestJvmRoute = null;
        final int index = sessionId.indexOf(46);
        if (index > 0) {
            requestJvmRoute = sessionId.substring(index + 1);
        }
        if (requestJvmRoute != null && !requestJvmRoute.equals(localJvmRoute)) {
            if (JvmRouteBinderValve.log.isDebugEnabled()) {
                JvmRouteBinderValve.log.debug((Object)JvmRouteBinderValve.sm.getString("jvmRoute.failover", new Object[] { requestJvmRoute, localJvmRoute, sessionId }));
            }
            Session catalinaSession = null;
            try {
                catalinaSession = this.getManager(request).findSession(sessionId);
            }
            catch (final IOException ex) {}
            final String id = sessionId.substring(0, index);
            final String newSessionID = id + "." + localJvmRoute;
            if (catalinaSession != null) {
                this.changeSessionID(request, sessionId, newSessionID, catalinaSession);
                ++this.numberOfSessions;
            }
            else {
                try {
                    catalinaSession = this.getManager(request).findSession(newSessionID);
                }
                catch (final IOException ex2) {}
                if (catalinaSession != null) {
                    this.changeRequestSessionID(request, sessionId, newSessionID);
                }
                else if (JvmRouteBinderValve.log.isDebugEnabled()) {
                    JvmRouteBinderValve.log.debug((Object)JvmRouteBinderValve.sm.getString("jvmRoute.cannotFindSession", new Object[] { sessionId }));
                }
            }
        }
    }
    
    protected void changeSessionID(final Request request, final String sessionId, final String newSessionID, final Session catalinaSession) {
        this.fireLifecycleEvent("Before session migration", (Object)catalinaSession);
        catalinaSession.getManager().changeSessionId(catalinaSession, newSessionID);
        this.changeRequestSessionID(request, sessionId, newSessionID);
        this.fireLifecycleEvent("After session migration", (Object)catalinaSession);
        if (JvmRouteBinderValve.log.isDebugEnabled()) {
            JvmRouteBinderValve.log.debug((Object)JvmRouteBinderValve.sm.getString("jvmRoute.changeSession", new Object[] { sessionId, newSessionID }));
        }
    }
    
    protected void changeRequestSessionID(final Request request, final String sessionId, final String newSessionID) {
        request.changeSessionId(newSessionID);
        if (this.sessionIdAttribute != null && !this.sessionIdAttribute.isEmpty()) {
            if (JvmRouteBinderValve.log.isDebugEnabled()) {
                JvmRouteBinderValve.log.debug((Object)JvmRouteBinderValve.sm.getString("jvmRoute.set.originalsessionid", new Object[] { this.sessionIdAttribute, sessionId }));
            }
            request.setAttribute(this.sessionIdAttribute, (Object)sessionId);
        }
    }
    
    protected synchronized void startInternal() throws LifecycleException {
        if (this.cluster == null) {
            final Cluster containerCluster = this.getContainer().getCluster();
            if (containerCluster instanceof CatalinaCluster) {
                this.setCluster((CatalinaCluster)containerCluster);
            }
        }
        if (JvmRouteBinderValve.log.isInfoEnabled()) {
            JvmRouteBinderValve.log.info((Object)JvmRouteBinderValve.sm.getString("jvmRoute.valve.started"));
            if (this.cluster == null) {
                JvmRouteBinderValve.log.info((Object)JvmRouteBinderValve.sm.getString("jvmRoute.noCluster"));
            }
        }
        super.startInternal();
    }
    
    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.cluster = null;
        this.numberOfSessions = 0L;
        if (JvmRouteBinderValve.log.isInfoEnabled()) {
            JvmRouteBinderValve.log.info((Object)JvmRouteBinderValve.sm.getString("jvmRoute.valve.stopped"));
        }
    }
    
    static {
        log = LogFactory.getLog((Class)JvmRouteBinderValve.class);
        sm = StringManager.getManager((Class)JvmRouteBinderValve.class);
    }
}
