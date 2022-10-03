package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.ForwardRequest;

public interface POAPolicyMediator
{
    Policies getPolicies();
    
    int getScid();
    
    int getServerId();
    
    Object getInvocationServant(final byte[] p0, final String p1) throws ForwardRequest;
    
    void returnServant();
    
    void etherealizeAll();
    
    void clearAOM();
    
    ServantManager getServantManager() throws WrongPolicy;
    
    void setServantManager(final ServantManager p0) throws WrongPolicy;
    
    Servant getDefaultServant() throws NoServant, WrongPolicy;
    
    void setDefaultServant(final Servant p0) throws WrongPolicy;
    
    void activateObject(final byte[] p0, final Servant p1) throws ObjectAlreadyActive, ServantAlreadyActive, WrongPolicy;
    
    Servant deactivateObject(final byte[] p0) throws ObjectNotActive, WrongPolicy;
    
    byte[] newSystemId() throws WrongPolicy;
    
    byte[] servantToId(final Servant p0) throws ServantNotActive, WrongPolicy;
    
    Servant idToServant(final byte[] p0) throws ObjectNotActive, WrongPolicy;
}
