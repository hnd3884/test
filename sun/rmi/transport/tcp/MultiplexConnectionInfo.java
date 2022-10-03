package sun.rmi.transport.tcp;

class MultiplexConnectionInfo
{
    int id;
    MultiplexInputStream in;
    MultiplexOutputStream out;
    boolean closed;
    
    MultiplexConnectionInfo(final int id) {
        this.in = null;
        this.out = null;
        this.closed = false;
        this.id = id;
    }
}
