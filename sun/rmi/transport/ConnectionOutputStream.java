package sun.rmi.transport;

import java.io.DataOutput;
import java.io.IOException;
import java.rmi.server.UID;
import sun.rmi.server.MarshalOutputStream;

class ConnectionOutputStream extends MarshalOutputStream
{
    private final Connection conn;
    private final boolean resultStream;
    private final UID ackID;
    private DGCAckHandler dgcAckHandler;
    
    ConnectionOutputStream(final Connection conn, final boolean resultStream) throws IOException {
        super(conn.getOutputStream());
        this.dgcAckHandler = null;
        this.conn = conn;
        this.resultStream = resultStream;
        this.ackID = (resultStream ? new UID() : null);
    }
    
    void writeID() throws IOException {
        assert this.resultStream;
        this.ackID.write(this);
    }
    
    boolean isResultStream() {
        return this.resultStream;
    }
    
    void saveObject(final Object o) {
        if (this.dgcAckHandler == null) {
            this.dgcAckHandler = new DGCAckHandler(this.ackID);
        }
        this.dgcAckHandler.add(o);
    }
    
    DGCAckHandler getDGCAckHandler() {
        return this.dgcAckHandler;
    }
    
    void done() {
        if (this.dgcAckHandler != null) {
            this.dgcAckHandler.startTimer();
        }
    }
}
