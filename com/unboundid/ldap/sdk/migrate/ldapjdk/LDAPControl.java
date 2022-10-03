package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Extensible;
import java.io.Serializable;

@Extensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPControl implements Serializable
{
    public static final String MANAGEDSAIT = "2.16.840.1.113730.3.4.2";
    public static final String PWEXPIRED = "2.16.840.1.113730.3.4.4";
    public static final String PWEXPIRING = "2.16.840.1.113730.3.4.5";
    private static final long serialVersionUID = 7828506470553016637L;
    private final boolean isCritical;
    private final byte[] value;
    private final String oid;
    
    public LDAPControl(final Control control) {
        this.oid = control.getOID();
        this.isCritical = control.isCritical();
        if (control.hasValue()) {
            this.value = control.getValue().getValue();
        }
        else {
            this.value = null;
        }
    }
    
    public LDAPControl(final String id, final boolean critical, final byte[] vals) {
        this.oid = id;
        this.isCritical = critical;
        this.value = vals;
    }
    
    public String getID() {
        return this.oid;
    }
    
    public boolean isCritical() {
        return this.isCritical;
    }
    
    public byte[] getValue() {
        return this.value;
    }
    
    public final Control toControl() {
        if (this.value == null) {
            return new Control(this.oid, this.isCritical, null);
        }
        return new Control(this.oid, this.isCritical, new ASN1OctetString(this.value));
    }
    
    public static Control[] toControls(final LDAPControl[] ldapControls) {
        if (ldapControls == null) {
            return null;
        }
        final Control[] controls = new Control[ldapControls.length];
        for (int i = 0; i < ldapControls.length; ++i) {
            controls[i] = ldapControls[i].toControl();
        }
        return controls;
    }
    
    public static LDAPControl[] toLDAPControls(final Control[] controls) {
        if (controls == null) {
            return null;
        }
        final LDAPControl[] ldapControls = new LDAPControl[controls.length];
        for (int i = 0; i < controls.length; ++i) {
            ldapControls[i] = new LDAPControl(controls[i]);
        }
        return ldapControls;
    }
    
    public LDAPControl duplicate() {
        return new LDAPControl(this.oid, this.isCritical, this.value);
    }
    
    @Override
    public String toString() {
        return this.toControl().toString();
    }
}
