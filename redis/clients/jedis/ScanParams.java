package redis.clients.jedis;

import java.util.Collections;
import java.util.Collection;
import redis.clients.util.SafeEncoder;
import java.util.ArrayList;
import java.util.List;

public class ScanParams
{
    private List<byte[]> params;
    public static final String SCAN_POINTER_START;
    public static final byte[] SCAN_POINTER_START_BINARY;
    
    public ScanParams() {
        this.params = new ArrayList<byte[]>();
    }
    
    public ScanParams match(final byte[] pattern) {
        this.params.add(Protocol.Keyword.MATCH.raw);
        this.params.add(pattern);
        return this;
    }
    
    public ScanParams match(final String pattern) {
        this.params.add(Protocol.Keyword.MATCH.raw);
        this.params.add(SafeEncoder.encode(pattern));
        return this;
    }
    
    public ScanParams count(final int count) {
        this.params.add(Protocol.Keyword.COUNT.raw);
        this.params.add(Protocol.toByteArray(count));
        return this;
    }
    
    public Collection<byte[]> getParams() {
        return Collections.unmodifiableCollection((Collection<? extends byte[]>)this.params);
    }
    
    static {
        SCAN_POINTER_START = String.valueOf(0);
        SCAN_POINTER_START_BINARY = SafeEncoder.encode(ScanParams.SCAN_POINTER_START);
    }
}
