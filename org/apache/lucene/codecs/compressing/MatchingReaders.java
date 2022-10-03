package org.apache.lucene.codecs.compressing;

import java.util.Iterator;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.MergeState;

class MatchingReaders
{
    final boolean[] matchingReaders;
    final int count;
    
    MatchingReaders(final MergeState mergeState) {
        final int numReaders = mergeState.maxDocs.length;
        int matchedCount = 0;
        this.matchingReaders = new boolean[numReaders];
        int i = 0;
    Label_0114_Outer:
        while (i < numReaders) {
            while (true) {
                for (final FieldInfo fi : mergeState.fieldInfos[i]) {
                    final FieldInfo other = mergeState.mergeFieldInfos.fieldInfo(fi.number);
                    if (other != null) {
                        if (other.name.equals(fi.name)) {
                            continue Label_0114_Outer;
                        }
                    }
                    ++i;
                    continue Label_0114_Outer;
                }
                this.matchingReaders[i] = true;
                ++matchedCount;
                continue;
            }
        }
        this.count = matchedCount;
        if (mergeState.infoStream.isEnabled("SM")) {
            mergeState.infoStream.message("SM", "merge store matchedCount=" + this.count + " vs " + numReaders);
            if (this.count != numReaders) {
                mergeState.infoStream.message("SM", "" + (numReaders - this.count) + " non-bulk merges");
            }
        }
    }
}
