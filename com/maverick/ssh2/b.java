package com.maverick.ssh2;

import com.maverick.ssh.message.Message;
import com.maverick.ssh.message.SshChannelMessage;
import com.maverick.ssh.ChannelOpenException;
import com.maverick.ssh.message.SshAbstractChannel;
import com.maverick.ssh.message.SshMessage;
import java.io.IOException;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.SshException;
import com.maverick.ssh.message.SshMessageReader;
import com.maverick.ssh.SshContext;
import java.util.Hashtable;
import com.maverick.ssh.message.MessageObserver;
import com.maverick.ssh.message.SshMessageRouter;

class b extends SshMessageRouter implements TransportProtocolListener
{
    Object wc;
    static final MessageObserver uc;
    static final MessageObserver vc;
    TransportProtocol xc;
    Hashtable sc;
    Hashtable tc;
    
    public b(final TransportProtocol xc, final SshContext sshContext, final boolean b) {
        super(xc, sshContext.getChannelLimit(), b);
        this.wc = new Object();
        this.sc = new Hashtable();
        this.tc = new Hashtable();
        (this.xc = xc).addListener(this);
        xc.b(new Runnable() {
            public void run() {
                b.this.stop();
            }
        });
    }
    
    public void b(final ChannelFactory channelFactory) throws SshException {
        final String[] supportedChannelTypes = channelFactory.supportedChannelTypes();
        for (int i = 0; i < supportedChannelTypes.length; ++i) {
            if (this.sc.containsKey(supportedChannelTypes[i])) {
                throw new SshException(supportedChannelTypes[i] + " channel is already registered!", 4);
            }
            this.sc.put(supportedChannelTypes[i], channelFactory);
        }
    }
    
    public void b(final GlobalRequestHandler globalRequestHandler) throws SshException {
        final String[] supportedRequests = globalRequestHandler.supportedRequests();
        for (int i = 0; i < supportedRequests.length; ++i) {
            if (this.tc.containsKey(supportedRequests[i])) {
                throw new SshException(supportedRequests[i] + " request is already registered!", 4);
            }
            this.tc.put(supportedRequests[i], globalRequestHandler);
        }
    }
    
    public boolean b(final GlobalRequest globalRequest, final boolean b) throws SshException {
        return this.b(globalRequest, b, 0L);
    }
    
    public boolean b(final GlobalRequest globalRequest, final boolean b, final long n) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(80);
            byteArrayWriter.writeString(globalRequest.getName());
            byteArrayWriter.writeBoolean(b);
            if (globalRequest.getData() != null) {
                byteArrayWriter.write(globalRequest.getData());
            }
            this.b(byteArrayWriter.toByteArray(), true);
            if (!b) {
                return true;
            }
            final SshMessage nextMessage = this.getGlobalMessages().nextMessage(b.vc, n);
            if (nextMessage.getMessageId() == 81) {
                if (nextMessage.available() > 0) {
                    final byte[] data = new byte[nextMessage.available()];
                    nextMessage.read(data);
                    globalRequest.setData(data);
                }
                else {
                    globalRequest.setData(null);
                }
                return true;
            }
            return false;
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public void b(final Ssh2Channel ssh2Channel) {
        this.freeChannel(ssh2Channel);
    }
    
    public SshContext e() {
        return this.xc.qb;
    }
    
    public void b(final Ssh2Channel ssh2Channel, final byte[] array) throws SshException, ChannelOpenException {
        this.b(ssh2Channel, array, 0L);
    }
    
    public void b(final Ssh2Channel ssh2Channel, final byte[] array, final long n) throws SshException, ChannelOpenException {
        try {
            final int allocateChannel = this.allocateChannel(ssh2Channel);
            if (allocateChannel == -1) {
                throw new ChannelOpenException("Maximum number of channels exceeded", 4);
            }
            ssh2Channel.b(this, allocateChannel);
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(90);
            byteArrayWriter.writeString(ssh2Channel.getName());
            byteArrayWriter.writeInt(ssh2Channel.getChannelId());
            byteArrayWriter.writeInt(ssh2Channel.c());
            byteArrayWriter.writeInt(ssh2Channel.b());
            if (array != null) {
                byteArrayWriter.write(array);
            }
            this.xc.sendMessage(byteArrayWriter.toByteArray(), true);
            final SshMessage nextMessage = ssh2Channel.getMessageStore().nextMessage(b.uc, n);
            if (nextMessage.getMessageId() == 92) {
                this.freeChannel(ssh2Channel);
                throw new ChannelOpenException(nextMessage.readString(), (int)nextMessage.readInt());
            }
            final int n2 = (int)nextMessage.readInt();
            final long int1 = nextMessage.readInt();
            final int n3 = (int)nextMessage.readInt();
            final byte[] array2 = new byte[nextMessage.available()];
            nextMessage.read(array2);
            ssh2Channel.open(n2, int1, n3, array2);
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    protected void b(final byte[] array, final boolean b) throws SshException {
        this.xc.sendMessage(array, b);
    }
    
    protected SshMessage createMessage(final byte[] array) throws SshException {
        if (array[0] >= 91 && array[0] <= 100) {
            return new SshChannelMessage(array);
        }
        return new SshMessage(array);
    }
    
    protected boolean processGlobalMessage(final SshMessage sshMessage) throws SshException {
        try {
            switch (sshMessage.getMessageId()) {
                case 90: {
                    final String string = sshMessage.readString();
                    final int n = (int)sshMessage.readInt();
                    final int n2 = (int)sshMessage.readInt();
                    final int n3 = (int)sshMessage.readInt();
                    final byte[] array = (byte[])((sshMessage.available() > 0) ? new byte[sshMessage.available()] : null);
                    sshMessage.read(array);
                    this.b(string, n, n2, n3, array);
                    return true;
                }
                case 80: {
                    final String string2 = sshMessage.readString();
                    final boolean b = sshMessage.read() != 0;
                    final byte[] array2 = new byte[sshMessage.available()];
                    sshMessage.read(array2);
                    this.b(string2, b, array2);
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    void b(final String s, final int n, final int n2, final int n3, final byte[] array) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            if (this.sc.containsKey(s)) {
                try {
                    final Ssh2Channel channel = this.sc.get(s).createChannel(s, array);
                    final int allocateChannel = this.allocateChannel(channel);
                    Label_0252: {
                        if (allocateChannel > -1) {
                            try {
                                channel.b(this, allocateChannel);
                                final byte[] create = channel.create();
                                byteArrayWriter.write(91);
                                byteArrayWriter.writeInt(n);
                                byteArrayWriter.writeInt(allocateChannel);
                                byteArrayWriter.writeInt(channel.c());
                                byteArrayWriter.writeInt(channel.b());
                                if (create != null) {
                                    byteArrayWriter.write(create);
                                }
                                this.xc.sendMessage(byteArrayWriter.toByteArray(), true);
                                channel.open(n, n2, n3);
                                return;
                            }
                            catch (final SshException ex) {
                                byteArrayWriter.write(92);
                                byteArrayWriter.writeInt(n);
                                byteArrayWriter.writeInt(2);
                                byteArrayWriter.writeString(ex.getMessage());
                                byteArrayWriter.writeString("");
                                break Label_0252;
                            }
                        }
                        byteArrayWriter.write(92);
                        byteArrayWriter.writeInt(n);
                        byteArrayWriter.writeInt(4);
                        byteArrayWriter.writeString("Maximum allowable open channel limit of " + String.valueOf(this.maximumChannels()) + " exceeded!");
                        byteArrayWriter.writeString("");
                    }
                }
                catch (final ChannelOpenException ex2) {
                    byteArrayWriter.write(92);
                    byteArrayWriter.writeInt(n);
                    byteArrayWriter.writeInt(ex2.getReason());
                    byteArrayWriter.writeString(ex2.getMessage());
                    byteArrayWriter.writeString("");
                }
            }
            else {
                byteArrayWriter.write(92);
                byteArrayWriter.writeInt(n);
                byteArrayWriter.writeInt(3);
                byteArrayWriter.writeString(s + " is not a supported channel type!");
                byteArrayWriter.writeString("");
            }
            this.xc.sendMessage(byteArrayWriter.toByteArray(), true);
        }
        catch (final IOException ex3) {
            throw new SshException(ex3.getMessage(), 5);
        }
    }
    
    void b(final String s, final boolean b, final byte[] array) throws SshException {
        try {
            boolean processGlobalRequest = false;
            final GlobalRequest globalRequest = new GlobalRequest(s, array);
            if (this.tc.containsKey(s)) {
                processGlobalRequest = this.tc.get(s).processGlobalRequest(globalRequest);
            }
            if (b) {
                if (processGlobalRequest) {
                    final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
                    byteArrayWriter.write(81);
                    if (globalRequest.getData() != null) {
                        byteArrayWriter.write(globalRequest.getData());
                    }
                    this.xc.sendMessage(byteArrayWriter.toByteArray(), true);
                }
                else {
                    this.xc.sendMessage(new byte[] { 82 }, true);
                }
            }
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    protected void onThreadExit() {
        if (this.xc.isConnected()) {
            this.xc.disconnect(10, "Exiting");
        }
        this.stop();
    }
    
    public void onDisconnect(final String s, final int n) {
    }
    
    public void onIdle(final long n) {
        final SshAbstractChannel[] activeChannels = this.getActiveChannels();
        for (int i = 0; i < activeChannels.length; ++i) {
            activeChannels[i].idle();
        }
    }
    
    static {
        uc = new MessageObserver() {
            public boolean wantsNotification(final Message message) {
                switch (message.getMessageId()) {
                    case 91:
                    case 92: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        };
        vc = new MessageObserver() {
            public boolean wantsNotification(final Message message) {
                switch (message.getMessageId()) {
                    case 81:
                    case 82: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        };
    }
}
