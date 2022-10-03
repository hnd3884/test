package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.PortableServer.Servant;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.BindingHolder;
import org.omg.CORBA.ORB;
import java.util.Enumeration;
import java.util.Hashtable;
import org.omg.PortableServer.POA;

public class TransientBindingIterator extends BindingIteratorImpl
{
    private POA nsPOA;
    private int currentSize;
    private Hashtable theHashtable;
    private Enumeration theEnumeration;
    
    public TransientBindingIterator(final ORB orb, final Hashtable theHashtable, final POA nsPOA) throws Exception {
        super(orb);
        this.theHashtable = theHashtable;
        this.theEnumeration = this.theHashtable.elements();
        this.currentSize = this.theHashtable.size();
        this.nsPOA = nsPOA;
    }
    
    public final boolean NextOne(final BindingHolder bindingHolder) {
        final boolean hasMoreElements = this.theEnumeration.hasMoreElements();
        if (hasMoreElements) {
            bindingHolder.value = this.theEnumeration.nextElement().theBinding;
            --this.currentSize;
        }
        else {
            bindingHolder.value = new Binding(new NameComponent[0], BindingType.nobject);
        }
        return hasMoreElements;
    }
    
    public final void Destroy() {
        try {
            final byte[] servant_to_id = this.nsPOA.servant_to_id(this);
            if (servant_to_id != null) {
                this.nsPOA.deactivate_object(servant_to_id);
            }
        }
        catch (final Exception ex) {
            NamingUtils.errprint("BindingIterator.Destroy():caught exception:");
            NamingUtils.printException(ex);
        }
    }
    
    public final int RemainingElements() {
        return this.currentSize;
    }
}
