package jcifs.smb;

import java.io.PrintStream;
import jcifs.util.LogStream;

class SmbTree
{
    private static int tree_conn_counter;
    private int tid;
    private String share;
    String service;
    SmbSession session;
    boolean treeConnected;
    boolean inDfs;
    int tree_num;
    
    SmbTree(final SmbSession session, final String share, final String service) {
        this.service = "?????";
        this.session = session;
        this.share = share.toUpperCase();
        if (service != null && !service.startsWith("??")) {
            this.service = service;
        }
    }
    
    boolean matches(final String share, final String service) {
        return this.share.equalsIgnoreCase(share) && (service == null || service.startsWith("??") || this.service.equalsIgnoreCase(service));
    }
    
    public boolean equals(final Object obj) {
        if (obj instanceof SmbTree) {
            final SmbTree tree = (SmbTree)obj;
            return this.matches(tree.share, tree.service);
        }
        return false;
    }
    
    void send(final ServerMessageBlock request, final ServerMessageBlock response) throws SmbException {
        if (response != null) {
            response.received = false;
        }
        this.treeConnect(request, response);
        if (request == null || (response != null && response.received)) {
            return;
        }
        Label_0285: {
            if (!this.service.equals("A:")) {
                switch (request.command) {
                    case -94:
                    case 4:
                    case 45:
                    case 46:
                    case 47:
                    case 113: {
                        break;
                    }
                    case 37:
                    case 50: {
                        switch (((SmbComTransaction)request).subCommand & 0xFF) {
                            case 0:
                            case 16:
                            case 35:
                            case 38:
                            case 83:
                            case 84:
                            case 104:
                            case 215: {
                                break Label_0285;
                            }
                            default: {
                                throw new SmbException("Invalid operation for " + this.service + " service");
                            }
                        }
                        break;
                    }
                    default: {
                        throw new SmbException("Invalid operation for " + this.service + " service" + request);
                    }
                }
            }
        }
        request.tid = this.tid;
        if (this.inDfs && request.path != null && request.path.length() > 0) {
            request.flags2 = 4096;
            request.path = '\\' + this.session.transport().tconHostName + '\\' + this.share + request.path;
        }
        try {
            this.session.send(request, response);
        }
        catch (final SmbException se) {
            if (se.getNtStatus() == -1073741623) {
                this.treeDisconnect(true);
            }
            throw se;
        }
    }
    
    void treeConnect(final ServerMessageBlock andx, final ServerMessageBlock andxResponse) throws SmbException {
        final SmbTransport transport = this.session.transport();
        synchronized (transport.setupDiscoLock) {
            synchronized (transport) {
                if (this.treeConnected) {
                    return;
                }
                this.session.transport.connect();
                final String unc = "\\\\" + this.session.transport.tconHostName + '\\' + this.share;
                final SmbTransport transport2 = this.session.transport;
                final LogStream log = SmbTransport.log;
                if (LogStream.level >= 4) {
                    final SmbTransport transport3 = this.session.transport;
                    SmbTransport.log.println("treeConnect: unc=" + unc + ",service=" + this.service);
                }
                final SmbComTreeConnectAndXResponse response = new SmbComTreeConnectAndXResponse(andxResponse);
                final SmbComTreeConnectAndX request = new SmbComTreeConnectAndX(this.session, unc, this.service, andx);
                this.session.send(request, response);
                this.tid = response.tid;
                this.service = response.service;
                this.inDfs = response.shareIsInDfs;
                this.treeConnected = true;
                this.tree_num = SmbTree.tree_conn_counter++;
            }
        }
    }
    
    void treeDisconnect(final boolean inError) {
        synchronized (this.session.transport) {
            if (this.treeConnected && !inError) {
                try {
                    this.send(new SmbComTreeDisconnect(), null);
                }
                catch (final SmbException se) {
                    final SmbTransport transport = this.session.transport;
                    final LogStream log = SmbTransport.log;
                    if (LogStream.level > 1) {
                        final SmbException ex = se;
                        final SmbTransport transport2 = this.session.transport;
                        ex.printStackTrace(SmbTransport.log);
                    }
                }
            }
            this.treeConnected = false;
        }
    }
    
    public String toString() {
        return "SmbTree[share=" + this.share + ",service=" + this.service + ",tid=" + this.tid + ",inDfs=" + this.inDfs + ",treeConnected=" + this.treeConnected + "]";
    }
}
