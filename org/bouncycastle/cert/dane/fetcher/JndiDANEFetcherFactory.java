package org.bouncycastle.cert.dane.fetcher;

import java.io.IOException;
import org.bouncycastle.cert.dane.DANEEntry;
import java.util.Iterator;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.NamingException;
import org.bouncycastle.cert.dane.DANEException;
import javax.naming.Binding;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import org.bouncycastle.cert.dane.DANEEntryFetcher;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.cert.dane.DANEEntryFetcherFactory;

public class JndiDANEFetcherFactory implements DANEEntryFetcherFactory
{
    private static final String DANE_TYPE = "53";
    private List dnsServerList;
    private boolean isAuthoritative;
    
    public JndiDANEFetcherFactory() {
        this.dnsServerList = new ArrayList();
    }
    
    public JndiDANEFetcherFactory usingDNSServer(final String s) {
        this.dnsServerList.add(s);
        return this;
    }
    
    public JndiDANEFetcherFactory setAuthoritative(final boolean isAuthoritative) {
        this.isAuthoritative = isAuthoritative;
        return this;
    }
    
    public DANEEntryFetcher build(final String s) {
        final Hashtable hashtable = new Hashtable();
        hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        hashtable.put("java.naming.authoritative", this.isAuthoritative ? "true" : "false");
        if (this.dnsServerList.size() > 0) {
            final StringBuffer sb = new StringBuffer();
            final Iterator iterator = this.dnsServerList.iterator();
            while (iterator.hasNext()) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append("dns://" + iterator.next());
            }
            hashtable.put("java.naming.provider.url", sb.toString());
        }
        return new DANEEntryFetcher() {
            public List getEntries() throws DANEException {
                final ArrayList list = new ArrayList();
                try {
                    final InitialDirContext initialDirContext = new InitialDirContext(hashtable);
                    if (s.indexOf("_smimecert.") > 0) {
                        final Attribute value = initialDirContext.getAttributes(s, new String[] { "53" }).get("53");
                        if (value != null) {
                            JndiDANEFetcherFactory.this.addEntries(list, s, value);
                        }
                    }
                    else {
                        final NamingEnumeration<Binding> listBindings = initialDirContext.listBindings("_smimecert." + s);
                        while (listBindings.hasMore()) {
                            final DirContext dirContext = (DirContext)listBindings.next().getObject();
                            final Attribute value2 = initialDirContext.getAttributes(dirContext.getNameInNamespace().substring(1, dirContext.getNameInNamespace().length() - 1), new String[] { "53" }).get("53");
                            if (value2 != null) {
                                final String nameInNamespace = dirContext.getNameInNamespace();
                                JndiDANEFetcherFactory.this.addEntries(list, nameInNamespace.substring(1, nameInNamespace.length() - 1), value2);
                            }
                        }
                    }
                    return list;
                }
                catch (final NamingException ex) {
                    throw new DANEException("Exception dealing with DNS: " + ex.getMessage(), ex);
                }
            }
        };
    }
    
    private void addEntries(final List list, final String s, final Attribute attribute) throws NamingException, DANEException {
        for (int i = 0; i != attribute.size(); ++i) {
            final byte[] array = (byte[])attribute.get(i);
            if (DANEEntry.isValidCertificate(array)) {
                try {
                    list.add(new DANEEntry(s, array));
                }
                catch (final IOException ex) {
                    throw new DANEException("Exception parsing entry: " + ex.getMessage(), ex);
                }
            }
        }
    }
}
