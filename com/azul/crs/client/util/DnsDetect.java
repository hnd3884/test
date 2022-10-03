package com.azul.crs.client.util;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import javax.naming.NameNotFoundException;
import javax.naming.InvalidNameException;
import com.sun.jndi.dns.ResourceRecord;
import java.util.Vector;
import javax.naming.NamingException;
import java.io.IOException;
import com.sun.jndi.dns.DnsName;
import sun.net.dns.ResolverConfiguration;
import java.util.List;
import com.sun.jndi.dns.DnsClient;
import java.lang.reflect.Method;

public class DnsDetect
{
    private static final int TIMEOUT = 1000;
    private static final int RETRIES = 4;
    private static final int CLASS_INTERNET = 1;
    private static final int TYPE_CNAME = 5;
    private static final int TYPE_TXT = 16;
    private Method queryMethod;
    private DnsClient dns;
    private String postfix;
    private List<String> searchlist;
    
    public DnsDetect(final String stackUuid) throws IOException {
        this.postfix = ((stackUuid == null) ? "" : ("_" + stackUuid));
        final ResolverConfiguration rc = ResolverConfiguration.open();
        final List<String> dnsList = rc.nameservers();
        try {
            this.dns = new DnsClient(dnsList.toArray(new String[0]), 1000, 4);
            (this.queryMethod = DnsClient.class.getDeclaredMethod("query", DnsName.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE)).setAccessible(true);
        }
        catch (final NoSuchMethodException | NamingException ex) {
            throw new IOException(ex);
        }
        (this.searchlist = rc.searchlist()).add(0, "");
    }
    
    private String query(final String name, final int type) throws IOException {
        for (final String domain : this.searchlist) {
            try {
                final Object rrsObject = this.queryMethod.invoke(this.dns, new DnsName((domain == "") ? name : (name + '.' + domain)), 1, type, true, false);
                final Class rrsClass = Class.forName("com.sun.jndi.dns.ResourceRecords");
                final Field rrsVector = rrsClass.getDeclaredField("answer");
                rrsVector.setAccessible(true);
                final Vector<ResourceRecord> rrs = (Vector<ResourceRecord>)rrsVector.get(rrsObject);
                if (rrs.size() > 0) {
                    return rrs.get(0).getRdata().toString();
                }
                continue;
            }
            catch (final ClassNotFoundException | IllegalAccessException | NoSuchFieldException | InvalidNameException ex) {
                throw new IOException(name, ex);
            }
            catch (final InvocationTargetException ex2) {
                if (ex2.getCause() instanceof NameNotFoundException) {
                    continue;
                }
                throw new IOException(ex2);
            }
        }
        return null;
    }
    
    public String queryEndpoint() throws IOException {
        final String result = this.query("crs-endpoint" + this.postfix, 5);
        return (result == null) ? null : (result.endsWith(".") ? result.substring(0, result.length() - 1) : result);
    }
    
    public String queryMailbox() throws IOException {
        return this.query("crs-mailbox" + this.postfix, 16);
    }
    
    public String getRecordNamePostfix() {
        return this.postfix;
    }
}
