package com.sun.corba.se.impl.naming.pcosnaming;

import org.omg.CORBA.INTERNAL;
import org.omg.PortableServer.Servant;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.BindingHolder;
import org.omg.CORBA.ORB;
import java.util.Enumeration;
import java.util.Hashtable;
import org.omg.PortableServer.POA;
import com.sun.corba.se.impl.naming.cosnaming.BindingIteratorImpl;

public class PersistentBindingIterator extends BindingIteratorImpl
{
    private POA biPOA;
    private int currentSize;
    private Hashtable theHashtable;
    private Enumeration theEnumeration;
    private ORB orb;
    
    public PersistentBindingIterator(final ORB orb, final Hashtable theHashtable, final POA biPOA) throws Exception {
        super(orb);
        this.orb = orb;
        this.theHashtable = theHashtable;
        this.theEnumeration = this.theHashtable.keys();
        this.currentSize = this.theHashtable.size();
        this.biPOA = biPOA;
    }
    
    public final boolean NextOne(final BindingHolder bindingHolder) {
        final boolean hasMoreElements = this.theEnumeration.hasMoreElements();
        if (hasMoreElements) {
            final InternalBindingKey internalBindingKey = this.theEnumeration.nextElement();
            bindingHolder.value = new Binding(new NameComponent[] { new NameComponent(internalBindingKey.id, internalBindingKey.kind) }, ((InternalBindingValue)this.theHashtable.get(internalBindingKey)).theBindingType);
        }
        else {
            bindingHolder.value = new Binding(new NameComponent[0], BindingType.nobject);
        }
        return hasMoreElements;
    }
    
    public final void Destroy() {
        try {
            final byte[] servant_to_id = this.biPOA.servant_to_id(this);
            if (servant_to_id != null) {
                this.biPOA.deactivate_object(servant_to_id);
            }
        }
        catch (final Exception ex) {
            throw new INTERNAL("Exception in BindingIterator.Destroy " + ex);
        }
    }
    
    public final int RemainingElements() {
        return this.currentSize;
    }
}
