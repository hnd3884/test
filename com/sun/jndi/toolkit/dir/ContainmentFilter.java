package com.sun.jndi.toolkit.dir;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public class ContainmentFilter implements AttrFilter
{
    private Attributes matchingAttrs;
    
    public ContainmentFilter(final Attributes matchingAttrs) {
        this.matchingAttrs = matchingAttrs;
    }
    
    @Override
    public boolean check(final Attributes attributes) throws NamingException {
        return this.matchingAttrs == null || this.matchingAttrs.size() == 0 || contains(attributes, this.matchingAttrs);
    }
    
    public static boolean contains(final Attributes attributes, final Attributes attributes2) throws NamingException {
        if (attributes2 == null) {
            return true;
        }
        final NamingEnumeration<? extends Attribute> all = attributes2.getAll();
        while (all.hasMore()) {
            if (attributes == null) {
                return false;
            }
            final Attribute attribute = (Attribute)all.next();
            final Attribute value = attributes.get(attribute.getID());
            if (value == null) {
                return false;
            }
            if (attribute.size() <= 0) {
                continue;
            }
            final NamingEnumeration<?> all2 = attribute.getAll();
            while (all2.hasMore()) {
                if (!value.contains(all2.next())) {
                    return false;
                }
            }
        }
        return true;
    }
}
