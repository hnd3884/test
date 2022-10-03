package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPModification implements Serializable
{
    public static final int ADD = 0;
    public static final int DELETE = 1;
    public static final int REPLACE = 2;
    private static final long serialVersionUID = 4385895404606128438L;
    private final Modification modification;
    
    public LDAPModification(final int op, final LDAPAttribute attr) {
        this.modification = new Modification(ModificationType.valueOf(op), attr.getName(), attr.getByteValueArray());
    }
    
    public LDAPModification(final Modification modification) {
        this.modification = modification;
    }
    
    public int getOp() {
        return this.modification.getModificationType().intValue();
    }
    
    public LDAPAttribute getAttribute() {
        return new LDAPAttribute(this.modification.getAttribute());
    }
    
    public Modification toModification() {
        return this.modification;
    }
    
    @Override
    public String toString() {
        return this.modification.toString();
    }
}
