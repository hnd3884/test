package org.jscep.transport.request;

public enum Operation
{
    GET_CA_CAPS("GetCACaps"), 
    GET_CA_CERT("GetCACert"), 
    GET_NEXT_CA_CERT("GetNextCACert"), 
    PKI_OPERATION("PKIOperation");
    
    private final String name;
    
    private Operation(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static Operation forName(final String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        for (final Operation op : values()) {
            if (op.name.equals(name)) {
                return op;
            }
        }
        throw new IllegalArgumentException(name + " not found");
    }
}
