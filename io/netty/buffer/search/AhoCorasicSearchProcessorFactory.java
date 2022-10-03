package io.netty.buffer.search;

import io.netty.util.internal.PlatformDependent;
import java.util.Queue;
import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class AhoCorasicSearchProcessorFactory extends AbstractMultiSearchProcessorFactory
{
    private final int[] jumpTable;
    private final int[] matchForNeedleId;
    static final int BITS_PER_SYMBOL = 8;
    static final int ALPHABET_SIZE = 256;
    
    AhoCorasicSearchProcessorFactory(final byte[]... needles) {
        for (final byte[] needle : needles) {
            if (needle.length == 0) {
                throw new IllegalArgumentException("Needle must be non empty");
            }
        }
        final Context context = buildTrie(needles);
        this.jumpTable = context.jumpTable;
        this.matchForNeedleId = context.matchForNeedleId;
        this.linkSuffixes();
        for (int i = 0; i < this.jumpTable.length; ++i) {
            if (this.matchForNeedleId[this.jumpTable[i] >> 8] >= 0) {
                this.jumpTable[i] = -this.jumpTable[i];
            }
        }
    }
    
    private static Context buildTrie(final byte[][] needles) {
        final ArrayList<Integer> jumpTableBuilder = new ArrayList<Integer>(256);
        for (int i = 0; i < 256; ++i) {
            jumpTableBuilder.add(-1);
        }
        final ArrayList<Integer> matchForBuilder = new ArrayList<Integer>();
        matchForBuilder.add(-1);
        for (int needleId = 0; needleId < needles.length; ++needleId) {
            final byte[] needle = needles[needleId];
            int currentPosition = 0;
            for (final byte ch0 : needle) {
                final int ch2 = ch0 & 0xFF;
                final int next = currentPosition + ch2;
                if (jumpTableBuilder.get(next) == -1) {
                    jumpTableBuilder.set(next, jumpTableBuilder.size());
                    for (int j = 0; j < 256; ++j) {
                        jumpTableBuilder.add(-1);
                    }
                    matchForBuilder.add(-1);
                }
                currentPosition = jumpTableBuilder.get(next);
            }
            matchForBuilder.set(currentPosition >> 8, needleId);
        }
        final Context context = new Context();
        context.jumpTable = new int[jumpTableBuilder.size()];
        for (int k = 0; k < jumpTableBuilder.size(); ++k) {
            context.jumpTable[k] = jumpTableBuilder.get(k);
        }
        context.matchForNeedleId = new int[matchForBuilder.size()];
        for (int k = 0; k < matchForBuilder.size(); ++k) {
            context.matchForNeedleId[k] = matchForBuilder.get(k);
        }
        return context;
    }
    
    private void linkSuffixes() {
        final Queue<Integer> queue = new ArrayDeque<Integer>();
        queue.add(0);
        final int[] suffixLinks = new int[this.matchForNeedleId.length];
        Arrays.fill(suffixLinks, -1);
        while (!queue.isEmpty()) {
            final int v = queue.remove();
            final int vPosition = v >> 8;
            final int u = (suffixLinks[vPosition] == -1) ? 0 : suffixLinks[vPosition];
            if (this.matchForNeedleId[vPosition] == -1) {
                this.matchForNeedleId[vPosition] = this.matchForNeedleId[u >> 8];
            }
            for (int ch = 0; ch < 256; ++ch) {
                final int vIndex = v | ch;
                final int uIndex = u | ch;
                final int jumpV = this.jumpTable[vIndex];
                final int jumpU = this.jumpTable[uIndex];
                if (jumpV != -1) {
                    suffixLinks[jumpV >> 8] = ((v > 0 && jumpU != -1) ? jumpU : 0);
                    queue.add(jumpV);
                }
                else {
                    this.jumpTable[vIndex] = ((jumpU != -1) ? jumpU : 0);
                }
            }
        }
    }
    
    @Override
    public Processor newSearchProcessor() {
        return new Processor(this.jumpTable, this.matchForNeedleId);
    }
    
    private static class Context
    {
        int[] jumpTable;
        int[] matchForNeedleId;
    }
    
    public static class Processor implements MultiSearchProcessor
    {
        private final int[] jumpTable;
        private final int[] matchForNeedleId;
        private long currentPosition;
        
        Processor(final int[] jumpTable, final int[] matchForNeedleId) {
            this.jumpTable = jumpTable;
            this.matchForNeedleId = matchForNeedleId;
        }
        
        @Override
        public boolean process(final byte value) {
            this.currentPosition = PlatformDependent.getInt(this.jumpTable, this.currentPosition | ((long)value & 0xFFL));
            if (this.currentPosition < 0L) {
                this.currentPosition = -this.currentPosition;
                return false;
            }
            return true;
        }
        
        @Override
        public int getFoundNeedleId() {
            return this.matchForNeedleId[(int)this.currentPosition >> 8];
        }
        
        @Override
        public void reset() {
            this.currentPosition = 0L;
        }
    }
}
