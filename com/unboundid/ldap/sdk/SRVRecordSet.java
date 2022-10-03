package com.unboundid.ldap.sdk;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import com.unboundid.util.StaticUtils;
import javax.naming.directory.InitialDirContext;
import java.util.logging.Level;
import com.unboundid.util.Debug;
import com.unboundid.util.DebugType;
import java.util.Hashtable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class SRVRecordSet implements Serializable
{
    private static final String DNS_ATTR_SRV = "SRV";
    private static final String[] ATTRIBUTE_IDS;
    private static final long serialVersionUID = 7075112952759306499L;
    private final int totalRecords;
    private final List<SRVRecord> allRecords;
    private final List<SRVRecordPrioritySet> recordSets;
    private final long expirationTime;
    
    SRVRecordSet(final long expirationTime, final List<SRVRecord> records) {
        this.expirationTime = expirationTime;
        this.allRecords = Collections.unmodifiableList((List<? extends SRVRecord>)records);
        this.totalRecords = records.size();
        final TreeMap<Long, List<SRVRecord>> m = new TreeMap<Long, List<SRVRecord>>();
        for (final SRVRecord r : records) {
            final Long priority = r.getPriority();
            List<SRVRecord> l = m.get(priority);
            if (l == null) {
                l = new ArrayList<SRVRecord>(records.size());
                m.put(priority, l);
            }
            l.add(r);
        }
        final ArrayList<SRVRecordPrioritySet> i = new ArrayList<SRVRecordPrioritySet>(m.size());
        for (final Map.Entry<Long, List<SRVRecord>> e : m.entrySet()) {
            i.add(new SRVRecordPrioritySet(e.getKey(), e.getValue()));
        }
        this.recordSets = Collections.unmodifiableList((List<? extends SRVRecordPrioritySet>)i);
    }
    
    long getExpirationTime() {
        return this.expirationTime;
    }
    
    boolean isExpired() {
        return System.currentTimeMillis() >= this.expirationTime;
    }
    
    List<SRVRecord> getOrderedRecords() {
        final ArrayList<SRVRecord> l = new ArrayList<SRVRecord>(this.totalRecords);
        for (final SRVRecordPrioritySet s : this.recordSets) {
            l.addAll(s.getOrderedRecords());
        }
        return l;
    }
    
    static SRVRecordSet getRecordSet(final String name, final Hashtable<String, String> jndiProperties, final long ttlMillis) throws LDAPException {
        final ArrayList<String> recordStrings = new ArrayList<String>(10);
        DirContext context = null;
        try {
            if (Debug.debugEnabled(DebugType.CONNECT)) {
                Debug.debug(Level.INFO, DebugType.CONNECT, "Issuing JNDI query to retrieve DNS SRV record '" + name + "' using properties '" + jndiProperties + "'.");
            }
            context = new InitialDirContext(jndiProperties);
            final Attributes recordAttributes = context.getAttributes(name, SRVRecordSet.ATTRIBUTE_IDS);
            context.close();
            final Attribute srvAttr = recordAttributes.get("SRV");
            if (srvAttr == null) {
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_SRV_RECORD_SET_NO_RECORDS.get(name));
            }
            final NamingEnumeration<?> values = srvAttr.getAll();
            while (values.hasMore()) {
                final Object value = values.next();
                recordStrings.add(String.valueOf(value));
            }
            values.close();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_SRV_RECORD_SET_ERROR_QUERYING_DNS.get(name, StaticUtils.getExceptionMessage(e)), e);
        }
        finally {
            if (context != null) {
                try {
                    context.close();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                }
            }
        }
        if (recordStrings.isEmpty()) {
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_SRV_RECORD_SET_NO_RECORDS.get(name));
        }
        final List<SRVRecord> recordList = new ArrayList<SRVRecord>(recordStrings.size());
        for (final String s : recordStrings) {
            final SRVRecord r = new SRVRecord(s);
            recordList.add(r);
            if (Debug.debugEnabled(DebugType.CONNECT)) {
                Debug.debug(Level.INFO, DebugType.CONNECT, "Decoded DNS SRV record " + r.toString());
            }
        }
        return new SRVRecordSet(System.currentTimeMillis() + ttlMillis, recordList);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    private void toString(final StringBuilder buffer) {
        buffer.append("SRVRecordSet(records={");
        final Iterator<SRVRecord> iterator = this.allRecords.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next().toString());
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
    
    static {
        ATTRIBUTE_IDS = new String[] { "SRV" };
    }
}
