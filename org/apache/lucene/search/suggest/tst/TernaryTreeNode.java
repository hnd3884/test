package org.apache.lucene.search.suggest.tst;

import org.apache.lucene.util.RamUsageEstimator;

public class TernaryTreeNode
{
    char splitchar;
    TernaryTreeNode loKid;
    TernaryTreeNode eqKid;
    TernaryTreeNode hiKid;
    String token;
    Object val;
    
    long sizeInBytes() {
        long mem = RamUsageEstimator.shallowSizeOf((Object)this);
        if (this.loKid != null) {
            mem += this.loKid.sizeInBytes();
        }
        if (this.eqKid != null) {
            mem += this.eqKid.sizeInBytes();
        }
        if (this.hiKid != null) {
            mem += this.hiKid.sizeInBytes();
        }
        if (this.token != null) {
            mem += RamUsageEstimator.shallowSizeOf((Object)this.token) + RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + 2 * this.token.length();
        }
        mem += RamUsageEstimator.shallowSizeOf(this.val);
        return mem;
    }
}
