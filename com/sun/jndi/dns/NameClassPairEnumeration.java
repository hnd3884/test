package com.sun.jndi.dns;

import javax.naming.NamingException;
import javax.naming.Name;
import javax.naming.CompositeName;
import java.util.NoSuchElementException;
import java.util.Hashtable;
import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;

final class NameClassPairEnumeration extends BaseNameClassPairEnumeration<NameClassPair> implements NamingEnumeration<NameClassPair>
{
    NameClassPairEnumeration(final DnsContext dnsContext, final Hashtable<String, NameNode> hashtable) {
        super(dnsContext, hashtable);
    }
    
    @Override
    public NameClassPair next() throws NamingException {
        if (!this.hasMore()) {
            throw new NoSuchElementException();
        }
        final NameNode nameNode = this.nodes.nextElement();
        final String s = (nameNode.isZoneCut() || nameNode.getChildren() != null) ? "javax.naming.directory.DirContext" : "java.lang.Object";
        final Name add = new CompositeName().add(new DnsName().add(nameNode.getLabel()).toString());
        final NameClassPair nameClassPair = new NameClassPair(add.toString(), s);
        nameClassPair.setNameInNamespace(this.ctx.fullyQualify(add).toString());
        return nameClassPair;
    }
}
