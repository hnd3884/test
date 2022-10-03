package redis.clients.jedis;

import redis.clients.util.SafeEncoder;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class ZParams
{
    private List<byte[]> params;
    
    public ZParams() {
        this.params = new ArrayList<byte[]>();
    }
    
    @Deprecated
    public ZParams weights(final int... weights) {
        this.params.add(Protocol.Keyword.WEIGHTS.raw);
        for (final int weight : weights) {
            this.params.add(Protocol.toByteArray(weight));
        }
        return this;
    }
    
    public ZParams weightsByDouble(final double... weights) {
        this.params.add(Protocol.Keyword.WEIGHTS.raw);
        for (final double weight : weights) {
            this.params.add(Protocol.toByteArray(weight));
        }
        return this;
    }
    
    public Collection<byte[]> getParams() {
        return Collections.unmodifiableCollection((Collection<? extends byte[]>)this.params);
    }
    
    public ZParams aggregate(final Aggregate aggregate) {
        this.params.add(Protocol.Keyword.AGGREGATE.raw);
        this.params.add(aggregate.raw);
        return this;
    }
    
    public enum Aggregate
    {
        SUM, 
        MIN, 
        MAX;
        
        public final byte[] raw;
        
        private Aggregate() {
            this.raw = SafeEncoder.encode(this.name());
        }
    }
}
