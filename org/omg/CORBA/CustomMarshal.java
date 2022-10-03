package org.omg.CORBA;

public interface CustomMarshal
{
    void marshal(final DataOutputStream p0);
    
    void unmarshal(final DataInputStream p0);
}
