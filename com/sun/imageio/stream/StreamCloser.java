package com.sun.imageio.stream;

import javax.imageio.stream.ImageInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.io.IOException;
import java.util.WeakHashMap;

public class StreamCloser
{
    private static WeakHashMap<CloseAction, Object> toCloseQueue;
    private static Thread streamCloser;
    
    public static void addToQueue(final CloseAction closeAction) {
        synchronized (StreamCloser.class) {
            if (StreamCloser.toCloseQueue == null) {
                StreamCloser.toCloseQueue = new WeakHashMap<CloseAction, Object>();
            }
            StreamCloser.toCloseQueue.put(closeAction, null);
            if (StreamCloser.streamCloser == null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    final /* synthetic */ Runnable val$streamCloserRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (StreamCloser.toCloseQueue != null) {
                                synchronized (StreamCloser.class) {
                                    final Set keySet = StreamCloser.toCloseQueue.keySet();
                                    for (final CloseAction closeAction : (CloseAction[])keySet.toArray(new CloseAction[keySet.size()])) {
                                        if (closeAction != null) {
                                            try {
                                                closeAction.performAction();
                                            }
                                            catch (final IOException ex) {}
                                        }
                                    }
                                }
                            }
                        }
                    };
                    
                    @Override
                    public Object run() {
                        ThreadGroup threadGroup2;
                        ThreadGroup threadGroup;
                        for (threadGroup = (threadGroup2 = Thread.currentThread().getThreadGroup()); threadGroup2 != null; threadGroup2 = threadGroup.getParent()) {
                            threadGroup = threadGroup2;
                        }
                        StreamCloser.streamCloser = new Thread(threadGroup, this.val$streamCloserRunnable);
                        StreamCloser.streamCloser.setContextClassLoader(null);
                        Runtime.getRuntime().addShutdownHook(StreamCloser.streamCloser);
                        return null;
                    }
                });
            }
        }
    }
    
    public static void removeFromQueue(final CloseAction closeAction) {
        synchronized (StreamCloser.class) {
            if (StreamCloser.toCloseQueue != null) {
                StreamCloser.toCloseQueue.remove(closeAction);
            }
        }
    }
    
    public static CloseAction createCloseAction(final ImageInputStream imageInputStream) {
        return new CloseAction(imageInputStream);
    }
    
    public static final class CloseAction
    {
        private ImageInputStream iis;
        
        private CloseAction(final ImageInputStream iis) {
            this.iis = iis;
        }
        
        public void performAction() throws IOException {
            if (this.iis != null) {
                this.iis.close();
            }
        }
    }
}
