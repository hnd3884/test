package com.unboundid.ldap.sdk.migrate.jndi;

import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import javax.naming.directory.SearchResult;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ExtendedResult;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.BasicControl;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.util.StaticUtils;
import java.util.Iterator;
import java.util.Collection;
import javax.naming.directory.BasicAttributes;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.NamingException;
import com.unboundid.asn1.ASN1OctetString;
import javax.naming.directory.ModificationItem;
import com.unboundid.ldap.sdk.Modification;
import javax.naming.ldap.Control;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JNDIConverter
{
    private static final Attribute[] NO_ATTRIBUTES;
    private static final Control[] NO_JNDI_CONTROLS;
    private static final Modification[] NO_MODIFICATIONS;
    private static final ModificationItem[] NO_MODIFICATION_ITEMS;
    private static final com.unboundid.ldap.sdk.Control[] NO_SDK_CONTROLS;
    
    private JNDIConverter() {
    }
    
    public static Attribute convertAttribute(final javax.naming.directory.Attribute a) throws NamingException {
        if (a == null) {
            return null;
        }
        final String name = a.getID();
        final ASN1OctetString[] values = new ASN1OctetString[a.size()];
        for (int i = 0; i < values.length; ++i) {
            final Object value = a.get(i);
            if (value instanceof byte[]) {
                values[i] = new ASN1OctetString((byte[])value);
            }
            else {
                values[i] = new ASN1OctetString(String.valueOf(value));
            }
        }
        return new Attribute(name, values);
    }
    
    public static javax.naming.directory.Attribute convertAttribute(final Attribute a) {
        if (a == null) {
            return null;
        }
        final BasicAttribute attr = new BasicAttribute(a.getName(), true);
        for (final String v : a.getValues()) {
            attr.add(v);
        }
        return attr;
    }
    
    public static Attribute[] convertAttributes(final Attributes a) throws NamingException {
        if (a == null) {
            return JNDIConverter.NO_ATTRIBUTES;
        }
        int i = 0;
        final Attribute[] attributes = new Attribute[a.size()];
        final NamingEnumeration<? extends javax.naming.directory.Attribute> e = a.getAll();
        try {
            while (e.hasMoreElements()) {
                attributes[i++] = convertAttribute((javax.naming.directory.Attribute)e.next());
            }
        }
        finally {
            e.close();
        }
        return attributes;
    }
    
    public static Attributes convertAttributes(final Attribute... a) {
        final BasicAttributes attrs = new BasicAttributes(true);
        if (a == null) {
            return attrs;
        }
        for (final Attribute attr : a) {
            attrs.put(convertAttribute(attr));
        }
        return attrs;
    }
    
    public static Attributes convertAttributes(final Collection<Attribute> a) {
        final BasicAttributes attrs = new BasicAttributes(true);
        if (a == null) {
            return attrs;
        }
        for (final Attribute attr : a) {
            attrs.put(convertAttribute(attr));
        }
        return attrs;
    }
    
    public static com.unboundid.ldap.sdk.Control convertControl(final Control c) throws NamingException {
        if (c == null) {
            return null;
        }
        final byte[] valueBytes = c.getEncodedValue();
        ASN1OctetString value;
        if (valueBytes == null || valueBytes.length == 0) {
            value = null;
        }
        else {
            try {
                value = ASN1OctetString.decodeAsOctetString(valueBytes);
            }
            catch (final ASN1Exception ae) {
                throw new NamingException(StaticUtils.getExceptionMessage(ae));
            }
        }
        return new com.unboundid.ldap.sdk.Control(c.getID(), c.isCritical(), value);
    }
    
    public static Control convertControl(final com.unboundid.ldap.sdk.Control c) {
        if (c == null) {
            return null;
        }
        final ASN1OctetString value = c.getValue();
        if (value == null) {
            return new BasicControl(c.getOID(), c.isCritical(), null);
        }
        return new BasicControl(c.getOID(), c.isCritical(), value.encode());
    }
    
    public static com.unboundid.ldap.sdk.Control[] convertControls(final Control... c) throws NamingException {
        if (c == null) {
            return JNDIConverter.NO_SDK_CONTROLS;
        }
        final com.unboundid.ldap.sdk.Control[] controls = new com.unboundid.ldap.sdk.Control[c.length];
        for (int i = 0; i < controls.length; ++i) {
            controls[i] = convertControl(c[i]);
        }
        return controls;
    }
    
    public static Control[] convertControls(final com.unboundid.ldap.sdk.Control... c) {
        if (c == null) {
            return JNDIConverter.NO_JNDI_CONTROLS;
        }
        final Control[] controls = new Control[c.length];
        for (int i = 0; i < controls.length; ++i) {
            controls[i] = convertControl(c[i]);
        }
        return controls;
    }
    
    public static com.unboundid.ldap.sdk.ExtendedRequest convertExtendedRequest(final ExtendedRequest r) throws NamingException {
        if (r == null) {
            return null;
        }
        return JNDIExtendedRequest.toSDKExtendedRequest(r);
    }
    
    public static ExtendedRequest convertExtendedRequest(final com.unboundid.ldap.sdk.ExtendedRequest r) {
        if (r == null) {
            return null;
        }
        return new JNDIExtendedRequest(r);
    }
    
    public static ExtendedResult convertExtendedResponse(final ExtendedResponse r) throws NamingException {
        if (r == null) {
            return null;
        }
        return JNDIExtendedResponse.toSDKExtendedResult(r);
    }
    
    public static ExtendedResponse convertExtendedResult(final ExtendedResult r) {
        if (r == null) {
            return null;
        }
        return new JNDIExtendedResponse(r);
    }
    
    public static Modification convertModification(final ModificationItem m) throws NamingException {
        if (m == null) {
            return null;
        }
        ModificationType modType = null;
        switch (m.getModificationOp()) {
            case 1: {
                modType = ModificationType.ADD;
                break;
            }
            case 3: {
                modType = ModificationType.DELETE;
                break;
            }
            case 2: {
                modType = ModificationType.REPLACE;
                break;
            }
            default: {
                throw new NamingException("Unsupported modification type " + m);
            }
        }
        final Attribute a = convertAttribute(m.getAttribute());
        return new Modification(modType, a.getName(), a.getRawValues());
    }
    
    public static ModificationItem convertModification(final Modification m) throws NamingException {
        if (m == null) {
            return null;
        }
        int modType = 0;
        switch (m.getModificationType().intValue()) {
            case 0: {
                modType = 1;
                break;
            }
            case 1: {
                modType = 3;
                break;
            }
            case 2: {
                modType = 2;
                break;
            }
            default: {
                throw new NamingException("Unsupported modification type " + m);
            }
        }
        return new ModificationItem(modType, convertAttribute(m.getAttribute()));
    }
    
    public static Modification[] convertModifications(final ModificationItem... m) throws NamingException {
        if (m == null) {
            return JNDIConverter.NO_MODIFICATIONS;
        }
        final Modification[] mods = new Modification[m.length];
        for (int i = 0; i < m.length; ++i) {
            mods[i] = convertModification(m[i]);
        }
        return mods;
    }
    
    public static ModificationItem[] convertModifications(final Modification... m) throws NamingException {
        if (m == null) {
            return JNDIConverter.NO_MODIFICATION_ITEMS;
        }
        final ModificationItem[] mods = new ModificationItem[m.length];
        for (int i = 0; i < m.length; ++i) {
            mods[i] = convertModification(m[i]);
        }
        return mods;
    }
    
    public static Entry convertSearchEntry(final SearchResult r) throws NamingException {
        return convertSearchEntry(r, null);
    }
    
    public static Entry convertSearchEntry(final SearchResult r, final String contextBaseDN) throws NamingException {
        if (r == null) {
            return null;
        }
        String dn;
        if (contextBaseDN == null || contextBaseDN.isEmpty()) {
            dn = r.getName();
        }
        else {
            final String name = r.getName();
            if (name == null || name.isEmpty()) {
                dn = contextBaseDN;
            }
            else {
                dn = r.getName() + ',' + contextBaseDN;
            }
        }
        return new Entry(dn, convertAttributes(r.getAttributes()));
    }
    
    public static SearchResult convertSearchEntry(final Entry e) {
        return convertSearchEntry(e, null);
    }
    
    public static SearchResult convertSearchEntry(final Entry e, final String contextBaseDN) {
        if (e == null) {
            return null;
        }
        String name = e.getDN();
        if (contextBaseDN != null && !contextBaseDN.isEmpty()) {
            try {
                final DN parsedEntryDN = e.getParsedDN();
                final DN parsedBaseDN = new DN(contextBaseDN);
                if (parsedEntryDN.equals(parsedBaseDN)) {
                    name = "";
                }
                else if (parsedEntryDN.isDescendantOf(parsedBaseDN, false)) {
                    final RDN[] entryRDNs = parsedEntryDN.getRDNs();
                    final RDN[] baseRDNs = parsedBaseDN.getRDNs();
                    final RDN[] remainingRDNs = new RDN[entryRDNs.length - baseRDNs.length];
                    System.arraycopy(entryRDNs, 0, remainingRDNs, 0, remainingRDNs.length);
                    name = new DN(remainingRDNs).toString();
                }
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
            }
        }
        final Collection<Attribute> attrs = e.getAttributes();
        final Attribute[] attributes = new Attribute[attrs.size()];
        attrs.toArray(attributes);
        return new SearchResult(name, (Object)null, convertAttributes(attributes));
    }
    
    static {
        NO_ATTRIBUTES = new Attribute[0];
        NO_JNDI_CONTROLS = new Control[0];
        NO_MODIFICATIONS = new Modification[0];
        NO_MODIFICATION_ITEMS = new ModificationItem[0];
        NO_SDK_CONTROLS = new com.unboundid.ldap.sdk.Control[0];
    }
}
