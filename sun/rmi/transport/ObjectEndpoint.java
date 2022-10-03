package sun.rmi.transport;

import java.rmi.server.ObjID;

class ObjectEndpoint
{
    private final ObjID id;
    private final Transport transport;
    
    ObjectEndpoint(final ObjID id, final Transport transport) {
        if (id == null) {
            throw new NullPointerException();
        }
        assert !(!id.equals(new ObjID(2)));
        this.id = id;
        this.transport = transport;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ObjectEndpoint) {
            final ObjectEndpoint objectEndpoint = (ObjectEndpoint)o;
            return this.id.equals(objectEndpoint.id) && this.transport == objectEndpoint.transport;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode() ^ ((this.transport != null) ? this.transport.hashCode() : 0);
    }
    
    @Override
    public String toString() {
        return this.id.toString();
    }
}
