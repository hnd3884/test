package com.sun.jndi.dns;

import javax.naming.CommunicationException;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

class Resolver
{
    private DnsClient dnsClient;
    private int timeout;
    private int retries;
    
    Resolver(final String[] array, final int timeout, final int retries) throws NamingException {
        this.timeout = timeout;
        this.retries = retries;
        this.dnsClient = new DnsClient(array, timeout, retries);
    }
    
    public void close() {
        this.dnsClient.close();
        this.dnsClient = null;
    }
    
    ResourceRecords query(final DnsName dnsName, final int n, final int n2, final boolean b, final boolean b2) throws NamingException {
        return this.dnsClient.query(dnsName, n, n2, b, b2);
    }
    
    ResourceRecords queryZone(final DnsName dnsName, final int n, final boolean b) throws NamingException {
        final DnsClient dnsClient = new DnsClient(this.findNameServers(dnsName, b), this.timeout, this.retries);
        try {
            return dnsClient.queryZone(dnsName, n, b);
        }
        finally {
            dnsClient.close();
        }
    }
    
    DnsName findZoneName(DnsName dnsName, final int n, final boolean b) throws NamingException {
        dnsName = (DnsName)dnsName.clone();
        while (dnsName.size() > 1) {
            ResourceRecords query = null;
            try {
                query = this.query(dnsName, n, 6, b, false);
            }
            catch (final NameNotFoundException ex) {
                throw ex;
            }
            catch (final NamingException ex2) {}
            if (query != null) {
                if (query.answer.size() > 0) {
                    return dnsName;
                }
                for (int i = 0; i < query.authority.size(); ++i) {
                    final ResourceRecord resourceRecord = query.authority.elementAt(i);
                    if (resourceRecord.getType() == 6) {
                        final DnsName name = resourceRecord.getName();
                        if (dnsName.endsWith(name)) {
                            return name;
                        }
                    }
                }
            }
            dnsName.remove(dnsName.size() - 1);
        }
        return dnsName;
    }
    
    ResourceRecord findSoa(final DnsName dnsName, final int n, final boolean b) throws NamingException {
        final ResourceRecords query = this.query(dnsName, n, 6, b, false);
        for (int i = 0; i < query.answer.size(); ++i) {
            final ResourceRecord resourceRecord = query.answer.elementAt(i);
            if (resourceRecord.getType() == 6) {
                return resourceRecord;
            }
        }
        return null;
    }
    
    private String[] findNameServers(final DnsName dnsName, final boolean b) throws NamingException {
        final ResourceRecords query = this.query(dnsName, 1, 2, b, false);
        final String[] array = new String[query.answer.size()];
        for (int i = 0; i < array.length; ++i) {
            final ResourceRecord resourceRecord = query.answer.elementAt(i);
            if (resourceRecord.getType() != 2) {
                throw new CommunicationException("Corrupted DNS message");
            }
            array[i] = (String)resourceRecord.getRdata();
            array[i] = array[i].substring(0, array[i].length() - 1);
        }
        return array;
    }
}
