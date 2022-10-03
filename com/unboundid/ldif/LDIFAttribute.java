package com.unboundid.ldif;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.matchingrules.MatchingRule;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1OctetString;
import java.util.LinkedHashSet;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
class LDIFAttribute implements Serializable
{
    private static final long serialVersionUID = -3771917482408643188L;
    private LinkedHashSet<ASN1OctetString> normalizedValues;
    private final ArrayList<ASN1OctetString> values;
    private final MatchingRule matchingRule;
    private final String name;
    
    LDIFAttribute(final String name, final MatchingRule matchingRule, final ASN1OctetString value) {
        this.name = name;
        this.matchingRule = matchingRule;
        (this.values = new ArrayList<ASN1OctetString>(5)).add(value);
        this.normalizedValues = null;
    }
    
    boolean addValue(final ASN1OctetString value, final DuplicateValueBehavior duplicateValueBehavior) throws LDAPException {
        if (this.normalizedValues == null) {
            this.normalizedValues = new LinkedHashSet<ASN1OctetString>(StaticUtils.computeMapCapacity(this.values.size() + 1));
            for (final ASN1OctetString s : this.values) {
                this.normalizedValues.add(this.matchingRule.normalize(s));
            }
        }
        if (this.normalizedValues.add(this.matchingRule.normalize(value))) {
            this.values.add(value);
            return true;
        }
        if (duplicateValueBehavior == DuplicateValueBehavior.RETAIN) {
            this.values.add(value);
            return true;
        }
        return false;
    }
    
    Attribute toAttribute() {
        final ASN1OctetString[] valueArray = new ASN1OctetString[this.values.size()];
        this.values.toArray(valueArray);
        return new Attribute(this.name, this.matchingRule, valueArray);
    }
}
