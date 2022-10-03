package com.unboundid.ldif;

import com.unboundid.ldap.sdk.EntrySourceException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Validator;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.EntrySource;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDIFEntrySource extends EntrySource
{
    private final AtomicBoolean closed;
    private final LDIFReader ldifReader;
    
    public LDIFEntrySource(final LDIFReader ldifReader) {
        Validator.ensureNotNull(ldifReader);
        this.ldifReader = ldifReader;
        this.closed = new AtomicBoolean(false);
    }
    
    @Override
    public Entry nextEntry() throws EntrySourceException {
        if (this.closed.get()) {
            return null;
        }
        try {
            final Entry e = this.ldifReader.readEntry();
            if (e == null) {
                this.close();
            }
            return e;
        }
        catch (final LDIFException le) {
            Debug.debugException(le);
            if (le.mayContinueReading()) {
                throw new EntrySourceException(true, le);
            }
            this.close();
            throw new EntrySourceException(false, le);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            this.close();
            throw new EntrySourceException(false, e2);
        }
    }
    
    @Override
    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            try {
                this.ldifReader.close();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
    }
}
