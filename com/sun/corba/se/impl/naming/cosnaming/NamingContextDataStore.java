package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.PortableServer.POA;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.BindingType;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;

public interface NamingContextDataStore
{
    void Bind(final NameComponent p0, final org.omg.CORBA.Object p1, final BindingType p2) throws SystemException;
    
    org.omg.CORBA.Object Resolve(final NameComponent p0, final BindingTypeHolder p1) throws SystemException;
    
    org.omg.CORBA.Object Unbind(final NameComponent p0) throws SystemException;
    
    void List(final int p0, final BindingListHolder p1, final BindingIteratorHolder p2) throws SystemException;
    
    NamingContext NewContext() throws SystemException;
    
    void Destroy() throws SystemException;
    
    boolean IsEmpty();
    
    POA getNSPOA();
}
