package com.microsoft.sqlserver.jdbc.dns;

import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attribute;
import java.util.TreeSet;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DNSUtilities
{
    private static final Logger LOG;
    private static final Level DNS_ERR_LOG_LEVEL;
    
    public static Set<DNSRecordSRV> findSrvRecords(final String dnsSrvRecordToFind) throws NamingException {
        final Hashtable<Object, Object> env = new Hashtable<Object, Object>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns:");
        final DirContext ctx = new InitialDirContext(env);
        final Attributes attrs = ctx.getAttributes(dnsSrvRecordToFind, new String[] { "SRV" });
        final NamingEnumeration<? extends Attribute> allServers = attrs.getAll();
        final TreeSet<DNSRecordSRV> records = new TreeSet<DNSRecordSRV>();
        while (allServers.hasMoreElements()) {
            final Attribute a = allServers.nextElement();
            final NamingEnumeration<?> srvRecord = a.getAll();
            while (srvRecord.hasMore()) {
                final String record = String.valueOf(srvRecord.nextElement());
                try {
                    final DNSRecordSRV rec = DNSRecordSRV.parseFromDNSRecord(record);
                    if (rec == null) {
                        continue;
                    }
                    records.add(rec);
                }
                catch (final IllegalArgumentException errorParsingRecord) {
                    if (!DNSUtilities.LOG.isLoggable(DNSUtilities.DNS_ERR_LOG_LEVEL)) {
                        continue;
                    }
                    DNSUtilities.LOG.log(DNSUtilities.DNS_ERR_LOG_LEVEL, String.format("Failed to parse SRV DNS Record: '%s'", record), errorParsingRecord);
                }
            }
            srvRecord.close();
        }
        allServers.close();
        return records;
    }
    
    static {
        LOG = Logger.getLogger(DNSUtilities.class.getName());
        DNS_ERR_LOG_LEVEL = Level.FINE;
    }
}
