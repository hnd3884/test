package io.netty.buffer;

abstract class SizeClasses implements SizeClassesMetric
{
    static final int LOG2_QUANTUM = 4;
    private static final int LOG2_SIZE_CLASS_GROUP = 2;
    private static final int LOG2_MAX_LOOKUP_SIZE = 12;
    private static final int INDEX_IDX = 0;
    private static final int LOG2GROUP_IDX = 1;
    private static final int LOG2DELTA_IDX = 2;
    private static final int NDELTA_IDX = 3;
    private static final int PAGESIZE_IDX = 4;
    private static final int SUBPAGE_IDX = 5;
    private static final int LOG2_DELTA_LOOKUP_IDX = 6;
    private static final byte no = 0;
    private static final byte yes = 1;
    protected final int pageSize;
    protected final int pageShifts;
    protected final int chunkSize;
    protected final int directMemoryCacheAlignment;
    final int nSizes;
    int nSubpages;
    int nPSizes;
    int smallMaxSizeIdx;
    private int lookupMaxSize;
    private final short[][] sizeClasses;
    private final int[] pageIdx2sizeTab;
    private final int[] sizeIdx2sizeTab;
    private final int[] size2idxTab;
    
    protected SizeClasses(final int pageSize, final int pageShifts, final int chunkSize, final int directMemoryCacheAlignment) {
        this.pageSize = pageSize;
        this.pageShifts = pageShifts;
        this.chunkSize = chunkSize;
        this.directMemoryCacheAlignment = directMemoryCacheAlignment;
        final int group = PoolThreadCache.log2(chunkSize) + 1 - 4;
        this.sizeClasses = new short[group << 2][7];
        this.nSizes = this.sizeClasses();
        this.sizeIdx2sizeTab = new int[this.nSizes];
        this.pageIdx2sizeTab = new int[this.nPSizes];
        this.idx2SizeTab(this.sizeIdx2sizeTab, this.pageIdx2sizeTab);
        this.size2idxTab(this.size2idxTab = new int[this.lookupMaxSize >> 4]);
    }
    
    private int sizeClasses() {
        int normalMaxSize = -1;
        int index = 0;
        int size = 0;
        int log2Group = 4;
        int log2Delta = 4;
        final int ndeltaLimit = 4;
        for (int nDelta = 0; nDelta < ndeltaLimit; size = this.sizeClass(index++, log2Group, log2Delta, nDelta++)) {}
        log2Group += 2;
        while (size < this.chunkSize) {
            for (int nDelta = 1; nDelta <= ndeltaLimit && size < this.chunkSize; size = (normalMaxSize = this.sizeClass(index++, log2Group, log2Delta, nDelta++))) {}
            ++log2Group;
            ++log2Delta;
        }
        assert this.chunkSize == normalMaxSize;
        return index;
    }
    
    private int sizeClass(final int index, final int log2Group, final int log2Delta, final int nDelta) {
        short isMultiPageSize;
        if (log2Delta >= this.pageShifts) {
            isMultiPageSize = 1;
        }
        else {
            final int pageSize = 1 << this.pageShifts;
            final int size = (1 << log2Group) + (1 << log2Delta) * nDelta;
            isMultiPageSize = (short)((size == size / pageSize * pageSize) ? 1 : 0);
        }
        final int log2Ndelta = (nDelta == 0) ? 0 : PoolThreadCache.log2(nDelta);
        byte remove = (byte)((1 << log2Ndelta < nDelta) ? 1 : 0);
        final int log2Size = (log2Delta + log2Ndelta == log2Group) ? (log2Group + 1) : log2Group;
        if (log2Size == log2Group) {
            remove = 1;
        }
        final short isSubpage = (short)((log2Size < this.pageShifts + 2) ? 1 : 0);
        final int log2DeltaLookup = (log2Size < 12 || (log2Size == 12 && remove == 0)) ? log2Delta : 0;
        final short[] sz = { (short)index, (short)log2Group, (short)log2Delta, (short)nDelta, isMultiPageSize, isSubpage, (short)log2DeltaLookup };
        this.sizeClasses[index] = sz;
        final int size2 = (1 << log2Group) + (nDelta << log2Delta);
        if (sz[4] == 1) {
            ++this.nPSizes;
        }
        if (sz[5] == 1) {
            ++this.nSubpages;
            this.smallMaxSizeIdx = index;
        }
        if (sz[6] != 0) {
            this.lookupMaxSize = size2;
        }
        return size2;
    }
    
    private void idx2SizeTab(final int[] sizeIdx2sizeTab, final int[] pageIdx2sizeTab) {
        int pageIdx = 0;
        for (int i = 0; i < this.nSizes; ++i) {
            final short[] sizeClass = this.sizeClasses[i];
            final int log2Group = sizeClass[1];
            final int log2Delta = sizeClass[2];
            final int nDelta = sizeClass[3];
            final int size = (1 << log2Group) + (nDelta << log2Delta);
            sizeIdx2sizeTab[i] = size;
            if (sizeClass[4] == 1) {
                pageIdx2sizeTab[pageIdx++] = size;
            }
        }
    }
    
    private void size2idxTab(final int[] size2idxTab) {
        int idx = 0;
        int size = 0;
        int i = 0;
        while (size <= this.lookupMaxSize) {
            final int log2Delta = this.sizeClasses[i][2];
            for (int times = 1 << log2Delta - 4; size <= this.lookupMaxSize && times-- > 0; size = idx + 1 << 4) {
                size2idxTab[idx++] = i;
            }
            ++i;
        }
    }
    
    @Override
    public int sizeIdx2size(final int sizeIdx) {
        return this.sizeIdx2sizeTab[sizeIdx];
    }
    
    @Override
    public int sizeIdx2sizeCompute(final int sizeIdx) {
        final int group = sizeIdx >> 2;
        final int mod = sizeIdx & 0x3;
        final int groupSize = (group == 0) ? 0 : (32 << group);
        final int shift = (group == 0) ? 1 : group;
        final int lgDelta = shift + 4 - 1;
        final int modSize = mod + 1 << lgDelta;
        return groupSize + modSize;
    }
    
    @Override
    public long pageIdx2size(final int pageIdx) {
        return this.pageIdx2sizeTab[pageIdx];
    }
    
    @Override
    public long pageIdx2sizeCompute(final int pageIdx) {
        final int group = pageIdx >> 2;
        final int mod = pageIdx & 0x3;
        final long groupSize = (group == 0) ? 0L : (1L << this.pageShifts + 2 - 1 << group);
        final int shift = (group == 0) ? 1 : group;
        final int log2Delta = shift + this.pageShifts - 1;
        final int modSize = mod + 1 << log2Delta;
        return groupSize + modSize;
    }
    
    @Override
    public int size2SizeIdx(int size) {
        if (size == 0) {
            return 0;
        }
        if (size > this.chunkSize) {
            return this.nSizes;
        }
        if (this.directMemoryCacheAlignment > 0) {
            size = this.alignSize(size);
        }
        if (size <= this.lookupMaxSize) {
            return this.size2idxTab[size - 1 >> 4];
        }
        final int x = PoolThreadCache.log2((size << 1) - 1);
        final int shift = (x < 7) ? 0 : (x - 6);
        final int group = shift << 2;
        final int log2Delta = (x < 7) ? 4 : (x - 2 - 1);
        final int deltaInverseMask = -1 << log2Delta;
        final int mod = (size - 1 & deltaInverseMask) >> log2Delta & 0x3;
        return group + mod;
    }
    
    @Override
    public int pages2pageIdx(final int pages) {
        return this.pages2pageIdxCompute(pages, false);
    }
    
    @Override
    public int pages2pageIdxFloor(final int pages) {
        return this.pages2pageIdxCompute(pages, true);
    }
    
    private int pages2pageIdxCompute(final int pages, final boolean floor) {
        final int pageSize = pages << this.pageShifts;
        if (pageSize > this.chunkSize) {
            return this.nPSizes;
        }
        final int x = PoolThreadCache.log2((pageSize << 1) - 1);
        final int shift = (x < 2 + this.pageShifts) ? 0 : (x - (2 + this.pageShifts));
        final int group = shift << 2;
        final int log2Delta = (x < 2 + this.pageShifts + 1) ? this.pageShifts : (x - 2 - 1);
        final int deltaInverseMask = -1 << log2Delta;
        final int mod = (pageSize - 1 & deltaInverseMask) >> log2Delta & 0x3;
        int pageIdx = group + mod;
        if (floor && this.pageIdx2sizeTab[pageIdx] > pages << this.pageShifts) {
            --pageIdx;
        }
        return pageIdx;
    }
    
    private int alignSize(final int size) {
        final int delta = size & this.directMemoryCacheAlignment - 1;
        return (delta == 0) ? size : (size + this.directMemoryCacheAlignment - delta);
    }
    
    @Override
    public int normalizeSize(int size) {
        if (size == 0) {
            return this.sizeIdx2sizeTab[0];
        }
        if (this.directMemoryCacheAlignment > 0) {
            size = this.alignSize(size);
        }
        if (size > this.lookupMaxSize) {
            return normalizeSizeCompute(size);
        }
        final int ret = this.sizeIdx2sizeTab[this.size2idxTab[size - 1 >> 4]];
        assert ret == normalizeSizeCompute(size);
        return ret;
    }
    
    private static int normalizeSizeCompute(final int size) {
        final int x = PoolThreadCache.log2((size << 1) - 1);
        final int log2Delta = (x < 7) ? 4 : (x - 2 - 1);
        final int delta = 1 << log2Delta;
        final int delta_mask = delta - 1;
        return size + delta_mask & ~delta_mask;
    }
}
