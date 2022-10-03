package org.omg.CORBA;

@Deprecated
public interface DynUnion extends Object, DynAny
{
    boolean set_as_default();
    
    void set_as_default(final boolean p0);
    
    DynAny discriminator();
    
    TCKind discriminator_kind();
    
    DynAny member();
    
    String member_name();
    
    void member_name(final String p0);
    
    TCKind member_kind();
}
