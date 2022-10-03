package org.apache.catalina.tribes.transport;

import java.io.IOException;
import org.apache.catalina.tribes.jmx.JmxRegistry;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.transport.nio.PooledParallelSender;
import javax.management.ObjectName;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelSender;

public class ReplicationTransmitter implements ChannelSender
{
    private Channel channel;
    private ObjectName oname;
    private MultiPointSender transport;
    
    public ReplicationTransmitter() {
        this.oname = null;
        this.transport = new PooledParallelSender();
    }
    
    public MultiPointSender getTransport() {
        return this.transport;
    }
    
    public void setTransport(final MultiPointSender transport) {
        this.transport = transport;
    }
    
    @Override
    public void sendMessage(final ChannelMessage message, final Member[] destination) throws ChannelException {
        final MultiPointSender sender = this.getTransport();
        sender.sendMessage(destination, message);
    }
    
    @Override
    public void start() throws IOException {
        this.getTransport().connect();
        final JmxRegistry jmxRegistry = JmxRegistry.getRegistry(this.channel);
        if (jmxRegistry != null) {
            this.oname = jmxRegistry.registerJmx(",component=Sender", this.transport);
        }
    }
    
    @Override
    public synchronized void stop() {
        this.getTransport().disconnect();
        if (this.oname != null) {
            JmxRegistry.getRegistry(this.channel).unregisterJmx(this.oname);
            this.oname = null;
        }
        this.channel = null;
    }
    
    @Override
    public void heartbeat() {
        if (this.getTransport() != null) {
            this.getTransport().keepalive();
        }
    }
    
    @Override
    public synchronized void add(final Member member) {
        this.getTransport().add(member);
    }
    
    @Override
    public synchronized void remove(final Member member) {
        this.getTransport().remove(member);
    }
    
    @Override
    public Channel getChannel() {
        return this.channel;
    }
    
    @Override
    public void setChannel(final Channel channel) {
        this.channel = channel;
    }
}
