package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.Arrays;
import com.unboundid.util.Validator;
import java.util.Iterator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class DNEntrySource extends EntrySource
{
    private final Iterator<?> dnIterator;
    private final LDAPInterface connection;
    private final String[] attributes;
    
    public DNEntrySource(final LDAPInterface connection, final DN[] dns, final String... attributes) {
        Validator.ensureNotNull(connection, dns);
        this.connection = connection;
        this.dnIterator = Arrays.asList(dns).iterator();
        if (attributes == null) {
            this.attributes = StaticUtils.NO_STRINGS;
        }
        else {
            this.attributes = attributes;
        }
    }
    
    public DNEntrySource(final LDAPInterface connection, final String[] dns, final String... attributes) {
        this(connection, Arrays.asList(dns), attributes);
    }
    
    public DNEntrySource(final LDAPInterface connection, final Collection<String> dns, final String... attributes) {
        Validator.ensureNotNull(connection, dns);
        this.connection = connection;
        this.dnIterator = dns.iterator();
        if (attributes == null) {
            this.attributes = StaticUtils.NO_STRINGS;
        }
        else {
            this.attributes = attributes;
        }
    }
    
    @Override
    public Entry nextEntry() throws EntrySourceException {
        if (!this.dnIterator.hasNext()) {
            return null;
        }
        final String dn = String.valueOf(this.dnIterator.next());
        try {
            final Entry e = this.connection.getEntry(dn, this.attributes);
            if (e == null) {
                throw new EntrySourceException(true, LDAPMessages.ERR_DN_ENTRY_SOURCE_NO_SUCH_ENTRY.get(dn), new LDAPException(ResultCode.NO_RESULTS_RETURNED));
            }
            return e;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new EntrySourceException(true, LDAPMessages.ERR_DN_ENTRY_SOURCE_ERR_RETRIEVING_ENTRY.get(dn, StaticUtils.getExceptionMessage(le)), le);
        }
    }
    
    @Override
    public void close() {
    }
}
