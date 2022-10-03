package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.BindingIteratorHelper;
import org.omg.PortableServer.Servant;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CORBA.SystemException;
import java.util.logging.Level;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.PortableServer.POA;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import java.util.Hashtable;
import com.sun.corba.se.impl.logging.NamingSystemException;
import java.util.logging.Logger;

public class TransientNamingContext extends NamingContextImpl implements NamingContextDataStore
{
    private Logger readLogger;
    private Logger updateLogger;
    private Logger lifecycleLogger;
    private NamingSystemException wrapper;
    private final Hashtable theHashtable;
    public org.omg.CORBA.Object localRoot;
    
    public TransientNamingContext(final ORB orb, final org.omg.CORBA.Object localRoot, final POA poa) throws Exception {
        super(orb, poa);
        this.theHashtable = new Hashtable();
        this.wrapper = NamingSystemException.get(orb, "naming");
        this.localRoot = localRoot;
        this.readLogger = orb.getLogger("naming.read");
        this.updateLogger = orb.getLogger("naming.update");
        (this.lifecycleLogger = orb.getLogger("naming.lifecycle")).fine("Root TransientNamingContext LIFECYCLE.CREATED");
    }
    
    @Override
    public final void Bind(final NameComponent nameComponent, final org.omg.CORBA.Object theObjectRef, final BindingType bindingType) throws SystemException {
        final InternalBindingKey internalBindingKey = new InternalBindingKey(nameComponent);
        final InternalBindingValue internalBindingValue = new InternalBindingValue(new Binding(new NameComponent[] { nameComponent }, bindingType), null);
        internalBindingValue.theObjectRef = theObjectRef;
        if (this.theHashtable.put(internalBindingKey, internalBindingValue) != null) {
            this.updateLogger.warning("<<NAMING BIND>>Name " + this.getName(nameComponent) + " Was Already Bound");
            throw this.wrapper.transNcBindAlreadyBound();
        }
        if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING BIND>><<SUCCESS>>Name Component: " + nameComponent.id + "." + nameComponent.kind);
        }
    }
    
    @Override
    public final org.omg.CORBA.Object Resolve(final NameComponent nameComponent, final BindingTypeHolder bindingTypeHolder) throws SystemException {
        if (nameComponent.id.length() == 0 && nameComponent.kind.length() == 0) {
            bindingTypeHolder.value = BindingType.ncontext;
            return this.localRoot;
        }
        final InternalBindingValue internalBindingValue = this.theHashtable.get(new InternalBindingKey(nameComponent));
        if (internalBindingValue == null) {
            return null;
        }
        if (this.readLogger.isLoggable(Level.FINE)) {
            this.readLogger.fine("<<NAMING RESOLVE>><<SUCCESS>>Namecomponent :" + this.getName(nameComponent));
        }
        bindingTypeHolder.value = internalBindingValue.theBinding.binding_type;
        return internalBindingValue.theObjectRef;
    }
    
    @Override
    public final org.omg.CORBA.Object Unbind(final NameComponent nameComponent) throws SystemException {
        final InternalBindingValue internalBindingValue = this.theHashtable.remove(new InternalBindingKey(nameComponent));
        if (internalBindingValue == null) {
            if (this.updateLogger.isLoggable(Level.FINE)) {
                this.updateLogger.fine("<<NAMING UNBIND>><<FAILURE>> There was no binding with the name " + this.getName(nameComponent) + " to Unbind ");
            }
            return null;
        }
        if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING UNBIND>><<SUCCESS>> NameComponent:  " + this.getName(nameComponent));
        }
        return internalBindingValue.theObjectRef;
    }
    
    @Override
    public final void List(final int n, final BindingListHolder bindingListHolder, final BindingIteratorHolder bindingIteratorHolder) throws SystemException {
        try {
            final TransientBindingIterator transientBindingIterator = new TransientBindingIterator(this.orb, (Hashtable)this.theHashtable.clone(), this.nsPOA);
            transientBindingIterator.list(n, bindingListHolder);
            bindingIteratorHolder.value = BindingIteratorHelper.narrow(this.nsPOA.id_to_reference(this.nsPOA.activate_object(transientBindingIterator)));
        }
        catch (final SystemException ex) {
            this.readLogger.warning("<<NAMING LIST>><<FAILURE>>" + ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.readLogger.severe("<<NAMING LIST>><<FAILURE>>" + ex2);
            throw this.wrapper.transNcListGotExc(ex2);
        }
    }
    
    @Override
    public final NamingContext NewContext() throws SystemException {
        try {
            final org.omg.CORBA.Object id_to_reference = this.nsPOA.id_to_reference(this.nsPOA.activate_object(new TransientNamingContext(this.orb, this.localRoot, this.nsPOA)));
            this.lifecycleLogger.fine("TransientNamingContext LIFECYCLE.CREATE SUCCESSFUL");
            return NamingContextHelper.narrow(id_to_reference);
        }
        catch (final SystemException ex) {
            this.lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE CREATE>><<FAILURE>>", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE CREATE>><<FAILURE>>", ex2);
            throw this.wrapper.transNcNewctxGotExc(ex2);
        }
    }
    
    @Override
    public final void Destroy() throws SystemException {
        try {
            final byte[] servant_to_id = this.nsPOA.servant_to_id(this);
            if (servant_to_id != null) {
                this.nsPOA.deactivate_object(servant_to_id);
            }
            if (this.lifecycleLogger.isLoggable(Level.FINE)) {
                this.lifecycleLogger.fine("<<LIFECYCLE DESTROY>><<SUCCESS>>");
            }
        }
        catch (final SystemException ex) {
            this.lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE DESTROY>><<FAILURE>>", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE DESTROY>><<FAILURE>>", ex2);
            throw this.wrapper.transNcDestroyGotExc(ex2);
        }
    }
    
    private String getName(final NameComponent nameComponent) {
        return nameComponent.id + "." + nameComponent.kind;
    }
    
    @Override
    public final boolean IsEmpty() {
        return this.theHashtable.isEmpty();
    }
}
