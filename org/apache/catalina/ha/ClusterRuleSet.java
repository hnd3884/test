package org.apache.catalina.ha;

import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSetBase;

public class ClusterRuleSet extends RuleSetBase
{
    protected final String prefix;
    
    public ClusterRuleSet() {
        this("");
    }
    
    public ClusterRuleSet(final String prefix) {
        this.prefix = prefix;
    }
    
    public void addRuleInstances(final Digester digester) {
        digester.addObjectCreate(this.prefix + "Manager", (String)null, "className");
        digester.addSetProperties(this.prefix + "Manager");
        digester.addSetNext(this.prefix + "Manager", "setManagerTemplate", "org.apache.catalina.ha.ClusterManager");
        digester.addObjectCreate(this.prefix + "Manager/SessionIdGenerator", "org.apache.catalina.util.StandardSessionIdGenerator", "className");
        digester.addSetProperties(this.prefix + "Manager/SessionIdGenerator");
        digester.addSetNext(this.prefix + "Manager/SessionIdGenerator", "setSessionIdGenerator", "org.apache.catalina.SessionIdGenerator");
        digester.addObjectCreate(this.prefix + "Channel", (String)null, "className");
        digester.addSetProperties(this.prefix + "Channel");
        digester.addSetNext(this.prefix + "Channel", "setChannel", "org.apache.catalina.tribes.Channel");
        final String channelPrefix = this.prefix + "Channel/";
        digester.addObjectCreate(channelPrefix + "Membership", (String)null, "className");
        digester.addSetProperties(channelPrefix + "Membership");
        digester.addSetNext(channelPrefix + "Membership", "setMembershipService", "org.apache.catalina.tribes.MembershipService");
        digester.addObjectCreate(channelPrefix + "MembershipListener", (String)null, "className");
        digester.addSetProperties(channelPrefix + "MembershipListener");
        digester.addSetNext(channelPrefix + "MembershipListener", "addMembershipListener", "org.apache.catalina.tribes.MembershipListener");
        digester.addObjectCreate(channelPrefix + "Sender", (String)null, "className");
        digester.addSetProperties(channelPrefix + "Sender");
        digester.addSetNext(channelPrefix + "Sender", "setChannelSender", "org.apache.catalina.tribes.ChannelSender");
        digester.addObjectCreate(channelPrefix + "Sender/Transport", (String)null, "className");
        digester.addSetProperties(channelPrefix + "Sender/Transport");
        digester.addSetNext(channelPrefix + "Sender/Transport", "setTransport", "org.apache.catalina.tribes.transport.MultiPointSender");
        digester.addObjectCreate(channelPrefix + "Receiver", (String)null, "className");
        digester.addSetProperties(channelPrefix + "Receiver");
        digester.addSetNext(channelPrefix + "Receiver", "setChannelReceiver", "org.apache.catalina.tribes.ChannelReceiver");
        digester.addObjectCreate(channelPrefix + "Interceptor", (String)null, "className");
        digester.addSetProperties(channelPrefix + "Interceptor");
        digester.addSetNext(channelPrefix + "Interceptor", "addInterceptor", "org.apache.catalina.tribes.ChannelInterceptor");
        digester.addObjectCreate(channelPrefix + "Interceptor/LocalMember", (String)null, "className");
        digester.addSetProperties(channelPrefix + "Interceptor/LocalMember");
        digester.addSetNext(channelPrefix + "Interceptor/LocalMember", "setLocalMember", "org.apache.catalina.tribes.Member");
        digester.addObjectCreate(channelPrefix + "Interceptor/Member", (String)null, "className");
        digester.addSetProperties(channelPrefix + "Interceptor/Member");
        digester.addSetNext(channelPrefix + "Interceptor/Member", "addStaticMember", "org.apache.catalina.tribes.Member");
        digester.addObjectCreate(channelPrefix + "ChannelListener", (String)null, "className");
        digester.addSetProperties(channelPrefix + "ChannelListener");
        digester.addSetNext(channelPrefix + "ChannelListener", "addChannelListener", "org.apache.catalina.tribes.ChannelListener");
        digester.addObjectCreate(this.prefix + "Valve", (String)null, "className");
        digester.addSetProperties(this.prefix + "Valve");
        digester.addSetNext(this.prefix + "Valve", "addValve", "org.apache.catalina.Valve");
        digester.addObjectCreate(this.prefix + "Deployer", (String)null, "className");
        digester.addSetProperties(this.prefix + "Deployer");
        digester.addSetNext(this.prefix + "Deployer", "setClusterDeployer", "org.apache.catalina.ha.ClusterDeployer");
        digester.addObjectCreate(this.prefix + "Listener", (String)null, "className");
        digester.addSetProperties(this.prefix + "Listener");
        digester.addSetNext(this.prefix + "Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate(this.prefix + "ClusterListener", (String)null, "className");
        digester.addSetProperties(this.prefix + "ClusterListener");
        digester.addSetNext(this.prefix + "ClusterListener", "addClusterListener", "org.apache.catalina.ha.ClusterListener");
    }
}
