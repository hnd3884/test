package org.apache.lucene.codecs.blocktree;

import java.util.Locale;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.lucene.util.IOUtils;
import java.io.ByteArrayOutputStream;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.ArrayUtil;

public class Stats
{
    public long indexNumBytes;
    public long totalTermCount;
    public long totalTermBytes;
    public int nonFloorBlockCount;
    public int floorBlockCount;
    public int floorSubBlockCount;
    public int mixedBlockCount;
    public int termsOnlyBlockCount;
    public int subBlocksOnlyBlockCount;
    public int totalBlockCount;
    public int[] blockCountByPrefixLen;
    private int startBlockCount;
    private int endBlockCount;
    public long totalBlockSuffixBytes;
    public long totalBlockStatsBytes;
    public long totalBlockOtherBytes;
    public final String segment;
    public final String field;
    
    Stats(final String segment, final String field) {
        this.blockCountByPrefixLen = new int[10];
        this.segment = segment;
        this.field = field;
    }
    
    void startBlock(final SegmentTermsEnumFrame frame, final boolean isFloor) {
        ++this.totalBlockCount;
        if (isFloor) {
            if (frame.fp == frame.fpOrig) {
                ++this.floorBlockCount;
            }
            ++this.floorSubBlockCount;
        }
        else {
            ++this.nonFloorBlockCount;
        }
        if (this.blockCountByPrefixLen.length <= frame.prefix) {
            this.blockCountByPrefixLen = ArrayUtil.grow(this.blockCountByPrefixLen, 1 + frame.prefix);
        }
        final int[] blockCountByPrefixLen = this.blockCountByPrefixLen;
        final int prefix = frame.prefix;
        ++blockCountByPrefixLen[prefix];
        ++this.startBlockCount;
        this.totalBlockSuffixBytes += frame.suffixesReader.length();
        this.totalBlockStatsBytes += frame.statsReader.length();
    }
    
    void endBlock(final SegmentTermsEnumFrame frame) {
        final int termCount = frame.isLeafBlock ? frame.entCount : frame.state.termBlockOrd;
        final int subBlockCount = frame.entCount - termCount;
        this.totalTermCount += termCount;
        if (termCount != 0 && subBlockCount != 0) {
            ++this.mixedBlockCount;
        }
        else if (termCount != 0) {
            ++this.termsOnlyBlockCount;
        }
        else {
            if (subBlockCount == 0) {
                throw new IllegalStateException();
            }
            ++this.subBlocksOnlyBlockCount;
        }
        ++this.endBlockCount;
        final long otherBytes = frame.fpEnd - frame.fp - frame.suffixesReader.length() - frame.statsReader.length();
        assert otherBytes > 0L : "otherBytes=" + otherBytes + " frame.fp=" + frame.fp + " frame.fpEnd=" + frame.fpEnd;
        this.totalBlockOtherBytes += otherBytes;
    }
    
    void term(final BytesRef term) {
        this.totalTermBytes += term.length;
    }
    
    void finish() {
        assert this.startBlockCount == this.endBlockCount : "startBlockCount=" + this.startBlockCount + " endBlockCount=" + this.endBlockCount;
        assert this.totalBlockCount == this.floorSubBlockCount + this.nonFloorBlockCount : "floorSubBlockCount=" + this.floorSubBlockCount + " nonFloorBlockCount=" + this.nonFloorBlockCount + " totalBlockCount=" + this.totalBlockCount;
        assert this.totalBlockCount == this.mixedBlockCount + this.termsOnlyBlockCount + this.subBlocksOnlyBlockCount : "totalBlockCount=" + this.totalBlockCount + " mixedBlockCount=" + this.mixedBlockCount + " subBlocksOnlyBlockCount=" + this.subBlocksOnlyBlockCount + " termsOnlyBlockCount=" + this.termsOnlyBlockCount;
    }
    
    @Override
    public String toString() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        PrintStream out;
        try {
            out = new PrintStream(bos, false, IOUtils.UTF_8);
        }
        catch (final UnsupportedEncodingException bogus) {
            throw new RuntimeException(bogus);
        }
        out.println("  index FST:");
        out.println("    " + this.indexNumBytes + " bytes");
        out.println("  terms:");
        out.println("    " + this.totalTermCount + " terms");
        out.println("    " + this.totalTermBytes + " bytes" + ((this.totalTermCount != 0L) ? (" (" + String.format(Locale.ROOT, "%.1f", this.totalTermBytes / (double)this.totalTermCount) + " bytes/term)") : ""));
        out.println("  blocks:");
        out.println("    " + this.totalBlockCount + " blocks");
        out.println("    " + this.termsOnlyBlockCount + " terms-only blocks");
        out.println("    " + this.subBlocksOnlyBlockCount + " sub-block-only blocks");
        out.println("    " + this.mixedBlockCount + " mixed blocks");
        out.println("    " + this.floorBlockCount + " floor blocks");
        out.println("    " + (this.totalBlockCount - this.floorSubBlockCount) + " non-floor blocks");
        out.println("    " + this.floorSubBlockCount + " floor sub-blocks");
        out.println("    " + this.totalBlockSuffixBytes + " term suffix bytes" + ((this.totalBlockCount != 0) ? (" (" + String.format(Locale.ROOT, "%.1f", this.totalBlockSuffixBytes / (double)this.totalBlockCount) + " suffix-bytes/block)") : ""));
        out.println("    " + this.totalBlockStatsBytes + " term stats bytes" + ((this.totalBlockCount != 0) ? (" (" + String.format(Locale.ROOT, "%.1f", this.totalBlockStatsBytes / (double)this.totalBlockCount) + " stats-bytes/block)") : ""));
        out.println("    " + this.totalBlockOtherBytes + " other bytes" + ((this.totalBlockCount != 0) ? (" (" + String.format(Locale.ROOT, "%.1f", this.totalBlockOtherBytes / (double)this.totalBlockCount) + " other-bytes/block)") : ""));
        if (this.totalBlockCount != 0) {
            out.println("    by prefix length:");
            int total = 0;
            for (int prefix = 0; prefix < this.blockCountByPrefixLen.length; ++prefix) {
                final int blockCount = this.blockCountByPrefixLen[prefix];
                total += blockCount;
                if (blockCount != 0) {
                    out.println("      " + String.format(Locale.ROOT, "%2d", prefix) + ": " + blockCount);
                }
            }
            assert this.totalBlockCount == total;
        }
        try {
            return bos.toString(IOUtils.UTF_8);
        }
        catch (final UnsupportedEncodingException bogus) {
            throw new RuntimeException(bogus);
        }
    }
}
