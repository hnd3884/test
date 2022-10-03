package org.apache.catalina.tribes.group.interceptors;

import java.util.Arrays;
import org.apache.juli.logging.LogFactory;
import java.util.Set;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import java.util.HashMap;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;

public class FragmentationInterceptor extends ChannelInterceptorBase implements FragmentationInterceptorMBean
{
    private static final Log log;
    protected static final StringManager sm;
    protected final HashMap<FragKey, FragCollection> fragpieces;
    private int maxSize;
    private long expire;
    protected final boolean deepclone = true;
    
    public FragmentationInterceptor() {
        this.fragpieces = new HashMap<FragKey, FragCollection>();
        this.maxSize = 102400;
        this.expire = 60000L;
    }
    
    @Override
    public void sendMessage(final Member[] destination, final ChannelMessage msg, final InterceptorPayload payload) throws ChannelException {
        final int size = msg.getMessage().getLength();
        final boolean frag = size > this.maxSize && this.okToProcess(msg.getOptions());
        if (frag) {
            this.frag(destination, msg, payload);
        }
        else {
            msg.getMessage().append(frag);
            super.sendMessage(destination, msg, payload);
        }
    }
    
    @Override
    public void messageReceived(final ChannelMessage msg) {
        final boolean isFrag = XByteBuffer.toBoolean(msg.getMessage().getBytesDirect(), msg.getMessage().getLength() - 1);
        msg.getMessage().trim(1);
        if (isFrag) {
            this.defrag(msg);
        }
        else {
            super.messageReceived(msg);
        }
    }
    
    public FragCollection getFragCollection(final FragKey key, final ChannelMessage msg) {
        FragCollection coll = this.fragpieces.get(key);
        if (coll == null) {
            synchronized (this.fragpieces) {
                coll = this.fragpieces.get(key);
                if (coll == null) {
                    coll = new FragCollection(msg);
                    this.fragpieces.put(key, coll);
                }
            }
        }
        return coll;
    }
    
    public void removeFragCollection(final FragKey key) {
        this.fragpieces.remove(key);
    }
    
    public void defrag(final ChannelMessage msg) {
        final FragKey key = new FragKey(msg.getUniqueId());
        final FragCollection coll = this.getFragCollection(key, msg);
        coll.addMessage((ChannelMessage)msg.deepclone());
        if (coll.complete()) {
            this.removeFragCollection(key);
            final ChannelMessage complete = coll.assemble();
            super.messageReceived(complete);
        }
    }
    
    public void frag(final Member[] destination, final ChannelMessage msg, final InterceptorPayload payload) throws ChannelException {
        final int size = msg.getMessage().getLength();
        final int count = size / this.maxSize + ((size % this.maxSize != 0) ? 1 : 0);
        final ChannelMessage[] messages = new ChannelMessage[count];
        int remaining = size;
        for (int i = 0; i < count; ++i) {
            final ChannelMessage tmp = (ChannelMessage)msg.clone();
            final int offset = i * this.maxSize;
            final int length = Math.min(remaining, this.maxSize);
            tmp.getMessage().clear();
            tmp.getMessage().append(msg.getMessage().getBytesDirect(), offset, length);
            tmp.getMessage().append(i);
            tmp.getMessage().append(count);
            tmp.getMessage().append(true);
            messages[i] = tmp;
            remaining -= length;
        }
        for (final ChannelMessage message : messages) {
            super.sendMessage(destination, message, payload);
        }
    }
    
    @Override
    public void heartbeat() {
        try {
            final Set<FragKey> set = this.fragpieces.keySet();
            final Object[] arr$;
            final Object[] keys = arr$ = set.toArray();
            for (final Object o : arr$) {
                final FragKey key = (FragKey)o;
                if (key != null && key.expired(this.getExpire())) {
                    this.removeFragCollection(key);
                }
            }
        }
        catch (final Exception x) {
            if (FragmentationInterceptor.log.isErrorEnabled()) {
                FragmentationInterceptor.log.error((Object)FragmentationInterceptor.sm.getString("fragmentationInterceptor.heartbeat.failed"), (Throwable)x);
            }
        }
        super.heartbeat();
    }
    
    @Override
    public int getMaxSize() {
        return this.maxSize;
    }
    
    @Override
    public long getExpire() {
        return this.expire;
    }
    
    @Override
    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }
    
    @Override
    public void setExpire(final long expire) {
        this.expire = expire;
    }
    
    static {
        log = LogFactory.getLog((Class)FragmentationInterceptor.class);
        sm = StringManager.getManager(FragmentationInterceptor.class);
    }
    
    public static class FragCollection
    {
        private final long received;
        private final ChannelMessage msg;
        private final XByteBuffer[] frags;
        
        public FragCollection(final ChannelMessage msg) {
            this.received = System.currentTimeMillis();
            final int count = XByteBuffer.toInt(msg.getMessage().getBytesDirect(), msg.getMessage().getLength() - 4);
            this.frags = new XByteBuffer[count];
            this.msg = msg;
        }
        
        public void addMessage(final ChannelMessage msg) {
            msg.getMessage().trim(4);
            final int nr = XByteBuffer.toInt(msg.getMessage().getBytesDirect(), msg.getMessage().getLength() - 4);
            msg.getMessage().trim(4);
            this.frags[nr] = msg.getMessage();
        }
        
        public boolean complete() {
            boolean result = true;
            for (int i = 0; i < this.frags.length && result; result = (this.frags[i] != null), ++i) {}
            return result;
        }
        
        public ChannelMessage assemble() {
            if (!this.complete()) {
                throw new IllegalStateException(FragmentationInterceptor.sm.getString("fragmentationInterceptor.fragments.missing"));
            }
            int buffersize = 0;
            for (final XByteBuffer frag : this.frags) {
                buffersize += frag.getLength();
            }
            final XByteBuffer buf = new XByteBuffer(buffersize, false);
            this.msg.setMessage(buf);
            for (final XByteBuffer frag2 : this.frags) {
                this.msg.getMessage().append(frag2.getBytesDirect(), 0, frag2.getLength());
            }
            return this.msg;
        }
        
        public boolean expired(final long expire) {
            return System.currentTimeMillis() - this.received > expire;
        }
    }
    
    public static class FragKey
    {
        private final byte[] uniqueId;
        private final long received;
        
        public FragKey(final byte[] id) {
            this.received = System.currentTimeMillis();
            this.uniqueId = id;
        }
        
        @Override
        public int hashCode() {
            return XByteBuffer.toInt(this.uniqueId, 0);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof FragKey && Arrays.equals(this.uniqueId, ((FragKey)o).uniqueId);
        }
        
        public boolean expired(final long expire) {
            return System.currentTimeMillis() - this.received > expire;
        }
    }
}
