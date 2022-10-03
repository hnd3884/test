package com.unboundid.ldap.sdk.migrate.ldapjdk;

import java.util.Iterator;
import java.util.ArrayList;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public class LDAPModificationSet implements Serializable
{
    private static final long serialVersionUID = -1789929614205832665L;
    private final ArrayList<LDAPModification> mods;
    
    public LDAPModificationSet() {
        this.mods = new ArrayList<LDAPModification>(1);
    }
    
    public void add(final int op, final LDAPAttribute attr) {
        this.mods.add(new LDAPModification(op, attr));
    }
    
    public LDAPModification elementAt(final int index) throws IndexOutOfBoundsException {
        return this.mods.get(index);
    }
    
    public void removeElementAt(final int index) throws IndexOutOfBoundsException {
        this.mods.remove(index);
    }
    
    public void remove(final String name) {
        final Iterator<LDAPModification> iterator = this.mods.iterator();
        while (iterator.hasNext()) {
            final LDAPModification mod = iterator.next();
            if (mod.getAttribute().getName().equalsIgnoreCase(name)) {
                iterator.remove();
            }
        }
    }
    
    public int size() {
        return this.mods.size();
    }
    
    public LDAPModification[] toArray() {
        final LDAPModification[] modArray = new LDAPModification[this.mods.size()];
        return this.mods.toArray(modArray);
    }
    
    @Override
    public String toString() {
        return this.mods.toString();
    }
}
