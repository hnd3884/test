package sun.rmi.transport.tcp;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.rmi.runtime.NewThreadAction;
import java.util.ArrayList;
import sun.rmi.transport.Connection;
import java.util.List;

class ConnectionAcceptor implements Runnable
{
    private TCPTransport transport;
    private List<Connection> queue;
    private static int threadNum;
    
    public ConnectionAcceptor(final TCPTransport transport) {
        this.queue = new ArrayList<Connection>();
        this.transport = transport;
    }
    
    public void startNewAcceptor() {
        AccessController.doPrivileged((PrivilegedAction<Thread>)new NewThreadAction(this, "Multiplex Accept-" + ++ConnectionAcceptor.threadNum, true)).start();
    }
    
    public void accept(final Connection connection) {
        synchronized (this.queue) {
            this.queue.add(connection);
            this.queue.notify();
        }
    }
    
    @Override
    public void run() {
        final Connection connection;
        synchronized (this.queue) {
            while (this.queue.size() == 0) {
                try {
                    this.queue.wait();
                }
                catch (final InterruptedException ex) {}
            }
            this.startNewAcceptor();
            connection = this.queue.remove(0);
        }
        this.transport.handleMessages(connection, true);
    }
    
    static {
        ConnectionAcceptor.threadNum = 0;
    }
}
