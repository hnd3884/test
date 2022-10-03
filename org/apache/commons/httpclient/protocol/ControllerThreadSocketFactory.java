package org.apache.commons.httpclient.protocol;

import java.net.UnknownHostException;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.util.TimeoutController;
import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;

public final class ControllerThreadSocketFactory
{
    private ControllerThreadSocketFactory() {
    }
    
    public static Socket createSocket(final ProtocolSocketFactory socketfactory, final String host, final int port, final InetAddress localAddress, final int localPort, final int timeout) throws IOException, UnknownHostException, ConnectTimeoutException {
        final SocketTask task = new SocketTask() {
            public void doit() throws IOException {
                this.setSocket(socketfactory.createSocket(host, port, localAddress, localPort));
            }
        };
        try {
            TimeoutController.execute(task, timeout);
        }
        catch (final TimeoutController.TimeoutException e) {
            throw new ConnectTimeoutException("The host did not accept the connection within timeout of " + timeout + " ms");
        }
        final Socket socket = task.getSocket();
        if (task.exception != null) {
            throw task.exception;
        }
        return socket;
    }
    
    public static Socket createSocket(final SocketTask task, final int timeout) throws IOException, UnknownHostException, ConnectTimeoutException {
        try {
            TimeoutController.execute(task, timeout);
        }
        catch (final TimeoutController.TimeoutException e) {
            throw new ConnectTimeoutException("The host did not accept the connection within timeout of " + timeout + " ms");
        }
        final Socket socket = task.getSocket();
        if (task.exception != null) {
            throw task.exception;
        }
        return socket;
    }
    
    public abstract static class SocketTask implements Runnable
    {
        private Socket socket;
        private IOException exception;
        
        protected void setSocket(final Socket newSocket) {
            this.socket = newSocket;
        }
        
        protected Socket getSocket() {
            return this.socket;
        }
        
        public abstract void doit() throws IOException;
        
        public void run() {
            try {
                this.doit();
            }
            catch (final IOException e) {
                this.exception = e;
            }
        }
    }
}
