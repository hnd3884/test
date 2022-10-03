package org.apache.lucene.store;

public class IOContext
{
    public final Context context;
    public final MergeInfo mergeInfo;
    public final FlushInfo flushInfo;
    public final boolean readOnce;
    public static final IOContext DEFAULT;
    public static final IOContext READONCE;
    public static final IOContext READ;
    
    public IOContext() {
        this(false);
    }
    
    public IOContext(final FlushInfo flushInfo) {
        assert flushInfo != null;
        this.context = Context.FLUSH;
        this.mergeInfo = null;
        this.readOnce = false;
        this.flushInfo = flushInfo;
    }
    
    public IOContext(final Context context) {
        this(context, null);
    }
    
    private IOContext(final boolean readOnce) {
        this.context = Context.READ;
        this.mergeInfo = null;
        this.readOnce = readOnce;
        this.flushInfo = null;
    }
    
    public IOContext(final MergeInfo mergeInfo) {
        this(Context.MERGE, mergeInfo);
    }
    
    private IOContext(final Context context, final MergeInfo mergeInfo) {
        assert mergeInfo != null : "MergeInfo must not be null if context is MERGE";
        assert context != Context.FLUSH : "Use IOContext(FlushInfo) to create a FLUSH IOContext";
        this.context = context;
        this.readOnce = false;
        this.mergeInfo = mergeInfo;
        this.flushInfo = null;
    }
    
    public IOContext(final IOContext ctxt, final boolean readOnce) {
        this.context = ctxt.context;
        this.mergeInfo = ctxt.mergeInfo;
        this.flushInfo = ctxt.flushInfo;
        this.readOnce = readOnce;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.context == null) ? 0 : this.context.hashCode());
        result = 31 * result + ((this.flushInfo == null) ? 0 : this.flushInfo.hashCode());
        result = 31 * result + ((this.mergeInfo == null) ? 0 : this.mergeInfo.hashCode());
        result = 31 * result + (this.readOnce ? 1231 : 1237);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final IOContext other = (IOContext)obj;
        if (this.context != other.context) {
            return false;
        }
        if (this.flushInfo == null) {
            if (other.flushInfo != null) {
                return false;
            }
        }
        else if (!this.flushInfo.equals(other.flushInfo)) {
            return false;
        }
        if (this.mergeInfo == null) {
            if (other.mergeInfo != null) {
                return false;
            }
        }
        else if (!this.mergeInfo.equals(other.mergeInfo)) {
            return false;
        }
        return this.readOnce == other.readOnce;
    }
    
    @Override
    public String toString() {
        return "IOContext [context=" + this.context + ", mergeInfo=" + this.mergeInfo + ", flushInfo=" + this.flushInfo + ", readOnce=" + this.readOnce + "]";
    }
    
    static {
        DEFAULT = new IOContext(Context.DEFAULT);
        READONCE = new IOContext(true);
        READ = new IOContext(false);
    }
    
    public enum Context
    {
        MERGE, 
        READ, 
        FLUSH, 
        DEFAULT;
    }
}
