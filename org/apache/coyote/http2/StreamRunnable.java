package org.apache.coyote.http2;

import org.apache.tomcat.util.net.SocketEvent;

class StreamRunnable implements Runnable
{
    private final StreamProcessor processor;
    private final SocketEvent event;
    
    public StreamRunnable(final StreamProcessor processor, final SocketEvent event) {
        this.processor = processor;
        this.event = event;
    }
    
    @Override
    public void run() {
        this.processor.process(this.event);
    }
}
