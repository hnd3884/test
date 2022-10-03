package org.apache.catalina.ha.tcp;

import org.apache.juli.logging.LogFactory;
import java.io.Serializable;
import org.apache.catalina.ha.session.SessionMessage;
import org.apache.catalina.ha.ClusterMessage;
import java.util.Iterator;
import org.apache.catalina.tribes.group.interceptors.TcpFailureDetector;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptor;
import org.apache.catalina.ha.session.JvmRouteBinderValve;
import org.apache.catalina.ha.session.ClusterSessionListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Engine;
import org.apache.catalina.Manager;
import org.apache.catalina.ha.ClusterValve;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import org.apache.catalina.ha.session.DeltaManager;
import java.util.HashMap;
import org.apache.catalina.tribes.group.GroupChannel;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.ha.ClusterListener;
import javax.management.ObjectName;
import org.apache.catalina.ha.ClusterDeployer;
import org.apache.catalina.Valve;
import java.util.List;
import org.apache.catalina.ha.ClusterManager;
import java.util.Map;
import java.beans.PropertyChangeSupport;
import org.apache.catalina.Container;
import org.apache.tomcat.util.res.StringManager;
import org.apache.catalina.tribes.Channel;
import org.apache.juli.logging.Log;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.util.LifecycleMBeanBase;

public class SimpleTcpCluster extends LifecycleMBeanBase implements CatalinaCluster, MembershipListener, ChannelListener
{
    public static final Log log;
    public static final String BEFORE_MEMBERREGISTER_EVENT = "before_member_register";
    public static final String AFTER_MEMBERREGISTER_EVENT = "after_member_register";
    public static final String BEFORE_MANAGERREGISTER_EVENT = "before_manager_register";
    public static final String AFTER_MANAGERREGISTER_EVENT = "after_manager_register";
    public static final String BEFORE_MANAGERUNREGISTER_EVENT = "before_manager_unregister";
    public static final String AFTER_MANAGERUNREGISTER_EVENT = "after_manager_unregister";
    public static final String BEFORE_MEMBERUNREGISTER_EVENT = "before_member_unregister";
    public static final String AFTER_MEMBERUNREGISTER_EVENT = "after_member_unregister";
    public static final String SEND_MESSAGE_FAILURE_EVENT = "send_message_failure";
    public static final String RECEIVE_MESSAGE_FAILURE_EVENT = "receive_message_failure";
    protected Channel channel;
    protected static final StringManager sm;
    protected String clusterName;
    protected boolean heartbeatBackgroundEnabled;
    protected Container container;
    protected final PropertyChangeSupport support;
    protected final Map<String, ClusterManager> managers;
    protected ClusterManager managerTemplate;
    private final List<Valve> valves;
    private ClusterDeployer clusterDeployer;
    private ObjectName onameClusterDeployer;
    protected final List<ClusterListener> clusterListeners;
    private boolean notifyLifecycleListenerOnFailure;
    private int channelSendOptions;
    private int channelStartOptions;
    private final Map<Member, ObjectName> memberOnameMap;
    protected boolean hasMembers;
    
    public SimpleTcpCluster() {
        this.channel = (Channel)new GroupChannel();
        this.heartbeatBackgroundEnabled = false;
        this.container = null;
        this.support = new PropertyChangeSupport(this);
        this.managers = new HashMap<String, ClusterManager>();
        this.managerTemplate = new DeltaManager();
        this.valves = new ArrayList<Valve>();
        this.clusterListeners = new ArrayList<ClusterListener>();
        this.notifyLifecycleListenerOnFailure = false;
        this.channelSendOptions = 8;
        this.channelStartOptions = 15;
        this.memberOnameMap = new ConcurrentHashMap<Member, ObjectName>();
        this.hasMembers = false;
    }
    
    public boolean isHeartbeatBackgroundEnabled() {
        return this.heartbeatBackgroundEnabled;
    }
    
    public void setHeartbeatBackgroundEnabled(final boolean heartbeatBackgroundEnabled) {
        this.heartbeatBackgroundEnabled = heartbeatBackgroundEnabled;
    }
    
    public void setClusterName(final String clusterName) {
        this.clusterName = clusterName;
    }
    
    public String getClusterName() {
        if (this.clusterName == null && this.container != null) {
            return this.container.getName();
        }
        return this.clusterName;
    }
    
    public void setContainer(final Container container) {
        final Container oldContainer = this.container;
        this.container = container;
        this.support.firePropertyChange("container", oldContainer, this.container);
    }
    
    public Container getContainer() {
        return this.container;
    }
    
    public boolean isNotifyLifecycleListenerOnFailure() {
        return this.notifyLifecycleListenerOnFailure;
    }
    
    public void setNotifyLifecycleListenerOnFailure(final boolean notifyListenerOnFailure) {
        final boolean oldNotifyListenerOnFailure = this.notifyLifecycleListenerOnFailure;
        this.notifyLifecycleListenerOnFailure = notifyListenerOnFailure;
        this.support.firePropertyChange("notifyLifecycleListenerOnFailure", oldNotifyListenerOnFailure, this.notifyLifecycleListenerOnFailure);
    }
    
    public void addValve(final Valve valve) {
        if (valve instanceof ClusterValve && !this.valves.contains(valve)) {
            this.valves.add(valve);
        }
    }
    
    public Valve[] getValves() {
        return this.valves.toArray(new Valve[0]);
    }
    
    public ClusterListener[] findClusterListeners() {
        if (this.clusterListeners.size() > 0) {
            final ClusterListener[] listener = new ClusterListener[this.clusterListeners.size()];
            this.clusterListeners.toArray(listener);
            return listener;
        }
        return new ClusterListener[0];
    }
    
    public void addClusterListener(final ClusterListener listener) {
        if (listener != null && !this.clusterListeners.contains(listener)) {
            this.clusterListeners.add(listener);
            listener.setCluster(this);
        }
    }
    
    public void removeClusterListener(final ClusterListener listener) {
        if (listener != null) {
            this.clusterListeners.remove(listener);
            listener.setCluster(null);
        }
    }
    
    public ClusterDeployer getClusterDeployer() {
        return this.clusterDeployer;
    }
    
    public void setClusterDeployer(final ClusterDeployer clusterDeployer) {
        this.clusterDeployer = clusterDeployer;
    }
    
    public void setChannel(final Channel channel) {
        this.channel = channel;
    }
    
    public void setManagerTemplate(final ClusterManager managerTemplate) {
        this.managerTemplate = managerTemplate;
    }
    
    public void setChannelSendOptions(final int channelSendOptions) {
        this.channelSendOptions = channelSendOptions;
    }
    
    public boolean hasMembers() {
        return this.hasMembers;
    }
    
    public Member[] getMembers() {
        return this.channel.getMembers();
    }
    
    public Member getLocalMember() {
        return this.channel.getLocalMember(true);
    }
    
    public Map<String, ClusterManager> getManagers() {
        return this.managers;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public ClusterManager getManagerTemplate() {
        return this.managerTemplate;
    }
    
    public int getChannelSendOptions() {
        return this.channelSendOptions;
    }
    
    public synchronized Manager createManager(final String name) {
        if (SimpleTcpCluster.log.isDebugEnabled()) {
            SimpleTcpCluster.log.debug((Object)("Creating ClusterManager for context " + name + " using class " + this.getManagerTemplate().getClass().getName()));
        }
        ClusterManager manager = null;
        try {
            manager = this.managerTemplate.cloneFromTemplate();
            manager.setName(name);
        }
        catch (final Exception x) {
            SimpleTcpCluster.log.error((Object)SimpleTcpCluster.sm.getString("simpleTcpCluster.clustermanager.cloneFailed"), (Throwable)x);
            manager = new DeltaManager();
        }
        finally {
            if (manager != null) {
                manager.setCluster(this);
            }
        }
        return (Manager)manager;
    }
    
    public void registerManager(final Manager manager) {
        if (!(manager instanceof ClusterManager)) {
            SimpleTcpCluster.log.warn((Object)SimpleTcpCluster.sm.getString("simpleTcpCluster.clustermanager.notImplement", new Object[] { manager }));
            return;
        }
        final ClusterManager cmanager = (ClusterManager)manager;
        this.fireLifecycleEvent("before_manager_register", (Object)manager);
        final String clusterName = this.getManagerName(cmanager.getName(), manager);
        cmanager.setName(clusterName);
        cmanager.setCluster(this);
        this.managers.put(clusterName, cmanager);
        this.fireLifecycleEvent("after_manager_register", (Object)manager);
    }
    
    public void removeManager(final Manager manager) {
        if (manager instanceof ClusterManager) {
            final ClusterManager cmgr = (ClusterManager)manager;
            this.fireLifecycleEvent("before_manager_unregister", (Object)manager);
            this.managers.remove(this.getManagerName(cmgr.getName(), manager));
            cmgr.setCluster(null);
            this.fireLifecycleEvent("after_manager_unregister", (Object)manager);
        }
    }
    
    public String getManagerName(final String name, final Manager manager) {
        String clusterName = name;
        if (clusterName == null) {
            clusterName = manager.getContext().getName();
        }
        if (this.getContainer() instanceof Engine) {
            final Context context = manager.getContext();
            final Container host = context.getParent();
            if (host instanceof Host && clusterName != null && !clusterName.startsWith(host.getName() + "#")) {
                clusterName = host.getName() + "#" + clusterName;
            }
        }
        return clusterName;
    }
    
    public Manager getManager(final String name) {
        return (Manager)this.managers.get(name);
    }
    
    public void backgroundProcess() {
        if (this.clusterDeployer != null) {
            this.clusterDeployer.backgroundProcess();
        }
        if (this.isHeartbeatBackgroundEnabled() && this.channel != null) {
            this.channel.heartbeat();
        }
        this.fireLifecycleEvent("periodic", (Object)null);
    }
    
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.clusterDeployer != null) {
            final StringBuilder name = new StringBuilder("type=Cluster");
            final Container container = this.getContainer();
            if (container != null) {
                name.append(container.getMBeanKeyProperties());
            }
            name.append(",component=Deployer");
            this.onameClusterDeployer = this.register((Object)this.clusterDeployer, name.toString());
        }
    }
    
    protected void startInternal() throws LifecycleException {
        if (SimpleTcpCluster.log.isInfoEnabled()) {
            SimpleTcpCluster.log.info((Object)SimpleTcpCluster.sm.getString("simpleTcpCluster.start"));
        }
        try {
            this.checkDefaults();
            this.registerClusterValve();
            this.channel.addMembershipListener((MembershipListener)this);
            this.channel.addChannelListener((ChannelListener)this);
            this.channel.setName(this.getClusterName() + "-Channel");
            this.channel.start(this.channelStartOptions);
            if (this.clusterDeployer != null) {
                this.clusterDeployer.start();
            }
            this.registerMember(this.channel.getLocalMember(false));
        }
        catch (final Exception x) {
            SimpleTcpCluster.log.error((Object)SimpleTcpCluster.sm.getString("simpleTcpCluster.startUnable"), (Throwable)x);
            throw new LifecycleException((Throwable)x);
        }
        this.setState(LifecycleState.STARTING);
    }
    
    protected void checkDefaults() {
        if (this.clusterListeners.size() == 0 && this.managerTemplate instanceof DeltaManager) {
            this.addClusterListener(new ClusterSessionListener());
        }
        if (this.valves.size() == 0) {
            this.addValve((Valve)new JvmRouteBinderValve());
            this.addValve((Valve)new ReplicationValve());
        }
        if (this.clusterDeployer != null) {
            this.clusterDeployer.setCluster(this);
        }
        if (this.channel == null) {
            this.channel = (Channel)new GroupChannel();
        }
        if (this.channel instanceof GroupChannel && !((GroupChannel)this.channel).getInterceptors().hasNext()) {
            this.channel.addInterceptor((ChannelInterceptor)new MessageDispatchInterceptor());
            this.channel.addInterceptor((ChannelInterceptor)new TcpFailureDetector());
        }
        if (this.heartbeatBackgroundEnabled) {
            this.channel.setHeartbeat(false);
        }
    }
    
    protected void registerClusterValve() {
        if (this.container != null) {
            for (final ClusterValve valve : this.valves) {
                if (SimpleTcpCluster.log.isDebugEnabled()) {
                    SimpleTcpCluster.log.debug((Object)("Invoking addValve on " + this.getContainer() + " with class=" + valve.getClass().getName()));
                }
                if (valve != null) {
                    this.container.getPipeline().addValve((Valve)valve);
                    valve.setCluster(this);
                }
            }
        }
    }
    
    protected void unregisterClusterValve() {
        for (final ClusterValve valve : this.valves) {
            if (SimpleTcpCluster.log.isDebugEnabled()) {
                SimpleTcpCluster.log.debug((Object)("Invoking removeValve on " + this.getContainer() + " with class=" + valve.getClass().getName()));
            }
            if (valve != null) {
                this.container.getPipeline().removeValve((Valve)valve);
                valve.setCluster(null);
            }
        }
    }
    
    protected void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
        this.unregisterMember(this.channel.getLocalMember(false));
        if (this.clusterDeployer != null) {
            this.clusterDeployer.stop();
        }
        this.managers.clear();
        try {
            if (this.clusterDeployer != null) {
                this.clusterDeployer.setCluster(null);
            }
            this.channel.stop(this.channelStartOptions);
            this.channel.removeChannelListener((ChannelListener)this);
            this.channel.removeMembershipListener((MembershipListener)this);
            this.unregisterClusterValve();
        }
        catch (final Exception x) {
            SimpleTcpCluster.log.error((Object)SimpleTcpCluster.sm.getString("simpleTcpCluster.stopUnable"), (Throwable)x);
        }
    }
    
    protected void destroyInternal() throws LifecycleException {
        if (this.onameClusterDeployer != null) {
            this.unregister(this.onameClusterDeployer);
            this.onameClusterDeployer = null;
        }
        super.destroyInternal();
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append('[');
        if (this.container == null) {
            sb.append("Container is null");
        }
        else {
            sb.append(this.container.getName());
        }
        sb.append(']');
        return sb.toString();
    }
    
    public void send(final ClusterMessage msg) {
        this.send(msg, null);
    }
    
    public void send(final ClusterMessage msg, final Member dest) {
        try {
            msg.setAddress(this.getLocalMember());
            int sendOptions = this.channelSendOptions;
            if (msg instanceof SessionMessage && ((SessionMessage)msg).getEventType() == 12) {
                sendOptions = 6;
            }
            if (dest != null) {
                if (!this.getLocalMember().equals(dest)) {
                    this.channel.send(new Member[] { dest }, (Serializable)msg, sendOptions);
                }
                else {
                    SimpleTcpCluster.log.error((Object)SimpleTcpCluster.sm.getString("simpleTcpCluster.unableSend.localMember", new Object[] { msg }));
                }
            }
            else {
                final Member[] destmembers = this.channel.getMembers();
                if (destmembers.length > 0) {
                    this.channel.send(destmembers, (Serializable)msg, sendOptions);
                }
                else if (SimpleTcpCluster.log.isDebugEnabled()) {
                    SimpleTcpCluster.log.debug((Object)("No members in cluster, ignoring message:" + msg));
                }
            }
        }
        catch (final Exception x) {
            SimpleTcpCluster.log.error((Object)SimpleTcpCluster.sm.getString("simpleTcpCluster.sendFailed"), (Throwable)x);
        }
    }
    
    public void memberAdded(final Member member) {
        try {
            this.hasMembers = this.channel.hasMembers();
            if (SimpleTcpCluster.log.isInfoEnabled()) {
                SimpleTcpCluster.log.info((Object)SimpleTcpCluster.sm.getString("simpleTcpCluster.member.added", new Object[] { member }));
            }
            this.fireLifecycleEvent("before_member_register", (Object)member);
            this.registerMember(member);
            this.fireLifecycleEvent("after_member_register", (Object)member);
        }
        catch (final Exception x) {
            SimpleTcpCluster.log.error((Object)SimpleTcpCluster.sm.getString("simpleTcpCluster.member.addFailed"), (Throwable)x);
        }
    }
    
    public void memberDisappeared(final Member member) {
        try {
            this.hasMembers = this.channel.hasMembers();
            if (SimpleTcpCluster.log.isInfoEnabled()) {
                SimpleTcpCluster.log.info((Object)SimpleTcpCluster.sm.getString("simpleTcpCluster.member.disappeared", new Object[] { member }));
            }
            this.fireLifecycleEvent("before_member_unregister", (Object)member);
            this.unregisterMember(member);
            this.fireLifecycleEvent("after_member_unregister", (Object)member);
        }
        catch (final Exception x) {
            SimpleTcpCluster.log.error((Object)SimpleTcpCluster.sm.getString("simpleTcpCluster.member.removeFailed"), (Throwable)x);
        }
    }
    
    public boolean accept(final Serializable msg, final Member sender) {
        return msg instanceof ClusterMessage;
    }
    
    public void messageReceived(final Serializable message, final Member sender) {
        final ClusterMessage fwd = (ClusterMessage)message;
        fwd.setAddress(sender);
        this.messageReceived(fwd);
    }
    
    public void messageReceived(final ClusterMessage message) {
        if (SimpleTcpCluster.log.isDebugEnabled() && message != null) {
            SimpleTcpCluster.log.debug((Object)("Assuming clocks are synched: Replication for " + message.getUniqueId() + " took=" + (System.currentTimeMillis() - message.getTimestamp()) + " ms."));
        }
        boolean accepted = false;
        if (message != null) {
            for (final ClusterListener listener : this.clusterListeners) {
                if (listener.accept(message)) {
                    accepted = true;
                    listener.messageReceived(message);
                }
            }
            if (!accepted && this.notifyLifecycleListenerOnFailure) {
                final Member dest = message.getAddress();
                this.fireLifecycleEvent("receive_message_failure", (Object)new SendMessageData(message, dest, null));
                if (SimpleTcpCluster.log.isDebugEnabled()) {
                    SimpleTcpCluster.log.debug((Object)("Message " + message.toString() + " from type " + message.getClass().getName() + " transferred but no listener registered"));
                }
            }
        }
    }
    
    public int getChannelStartOptions() {
        return this.channelStartOptions;
    }
    
    public void setChannelStartOptions(final int channelStartOptions) {
        this.channelStartOptions = channelStartOptions;
    }
    
    protected String getDomainInternal() {
        final Container container = this.getContainer();
        if (container == null) {
            return null;
        }
        return container.getDomain();
    }
    
    protected String getObjectNameKeyProperties() {
        final StringBuilder name = new StringBuilder("type=Cluster");
        final Container container = this.getContainer();
        if (container != null) {
            name.append(container.getMBeanKeyProperties());
        }
        return name.toString();
    }
    
    private void registerMember(final Member member) {
        final StringBuilder name = new StringBuilder("type=Cluster");
        final Container container = this.getContainer();
        if (container != null) {
            name.append(container.getMBeanKeyProperties());
        }
        name.append(",component=Member,name=");
        name.append(ObjectName.quote(member.getName()));
        final ObjectName oname = this.register((Object)member, name.toString());
        this.memberOnameMap.put(member, oname);
    }
    
    private void unregisterMember(final Member member) {
        if (member == null) {
            return;
        }
        final ObjectName oname = this.memberOnameMap.remove(member);
        if (oname != null) {
            this.unregister(oname);
        }
    }
    
    static {
        log = LogFactory.getLog((Class)SimpleTcpCluster.class);
        sm = StringManager.getManager("org.apache.catalina.ha.tcp");
    }
}
