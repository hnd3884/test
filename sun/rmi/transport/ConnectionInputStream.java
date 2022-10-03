package sun.rmi.transport;

import java.rmi.RemoteException;
import java.io.DataOutput;
import java.io.DataOutputStream;
import sun.rmi.runtime.Log;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;
import java.io.InputStream;
import java.rmi.server.UID;
import java.util.List;
import java.util.Map;
import sun.rmi.server.MarshalInputStream;

class ConnectionInputStream extends MarshalInputStream
{
    private boolean dgcAckNeeded;
    private Map<Endpoint, List<LiveRef>> incomingRefTable;
    private UID ackID;
    
    ConnectionInputStream(final InputStream inputStream) throws IOException {
        super(inputStream);
        this.dgcAckNeeded = false;
        this.incomingRefTable = new HashMap<Endpoint, List<LiveRef>>(5);
    }
    
    void readID() throws IOException {
        this.ackID = UID.read(this);
    }
    
    void saveRef(final LiveRef liveRef) {
        final Endpoint endpoint = liveRef.getEndpoint();
        List list = this.incomingRefTable.get(endpoint);
        if (list == null) {
            list = new ArrayList();
            this.incomingRefTable.put(endpoint, list);
        }
        list.add(liveRef);
    }
    
    void discardRefs() {
        this.incomingRefTable.clear();
    }
    
    void registerRefs() throws IOException {
        if (!this.incomingRefTable.isEmpty()) {
            for (final Map.Entry entry : this.incomingRefTable.entrySet()) {
                DGCClient.registerRefs((Endpoint)entry.getKey(), (List<LiveRef>)entry.getValue());
            }
        }
    }
    
    void setAckNeeded() {
        this.dgcAckNeeded = true;
    }
    
    void done(final Connection connection) {
        if (this.dgcAckNeeded) {
            Connection connection2 = null;
            Channel channel = null;
            boolean b = true;
            DGCImpl.dgcLog.log(Log.VERBOSE, "send ack");
            try {
                channel = connection.getChannel();
                connection2 = channel.newConnection();
                final DataOutputStream dataOutputStream = new DataOutputStream(connection2.getOutputStream());
                dataOutputStream.writeByte(84);
                if (this.ackID == null) {
                    this.ackID = new UID();
                }
                this.ackID.write(dataOutputStream);
                connection2.releaseOutputStream();
                connection2.getInputStream().available();
                connection2.releaseInputStream();
            }
            catch (final RemoteException ex) {
                b = false;
            }
            catch (final IOException ex2) {
                b = false;
            }
            try {
                if (connection2 != null) {
                    channel.free(connection2, b);
                }
            }
            catch (final RemoteException ex3) {}
        }
    }
}
