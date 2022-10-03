package org.apache.lucene.facet.taxonomy.directory;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.MultiFields;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.facet.taxonomy.ParallelTaxonomyArrays;

class TaxonomyIndexArrays extends ParallelTaxonomyArrays
{
    private final int[] parents;
    private volatile boolean initializedChildren;
    private int[] children;
    private int[] siblings;
    
    private TaxonomyIndexArrays(final int[] parents) {
        this.initializedChildren = false;
        this.parents = parents;
    }
    
    public TaxonomyIndexArrays(final IndexReader reader) throws IOException {
        this.initializedChildren = false;
        this.parents = new int[reader.maxDoc()];
        if (this.parents.length > 0) {
            this.initParents(reader, 0);
            this.parents[0] = -1;
        }
    }
    
    public TaxonomyIndexArrays(final IndexReader reader, final TaxonomyIndexArrays copyFrom) throws IOException {
        this.initializedChildren = false;
        assert copyFrom != null;
        final int[] copyParents = copyFrom.parents();
        System.arraycopy(copyParents, 0, this.parents = new int[reader.maxDoc()], 0, copyParents.length);
        this.initParents(reader, copyParents.length);
        if (copyFrom.initializedChildren) {
            this.initChildrenSiblings(copyFrom);
        }
    }
    
    private final synchronized void initChildrenSiblings(final TaxonomyIndexArrays copyFrom) {
        if (!this.initializedChildren) {
            this.children = new int[this.parents.length];
            this.siblings = new int[this.parents.length];
            if (copyFrom != null) {
                System.arraycopy(copyFrom.children(), 0, this.children, 0, copyFrom.children().length);
                System.arraycopy(copyFrom.siblings(), 0, this.siblings, 0, copyFrom.siblings().length);
                this.computeChildrenSiblings(copyFrom.parents.length);
            }
            else {
                this.computeChildrenSiblings(0);
            }
            this.initializedChildren = true;
        }
    }
    
    private void computeChildrenSiblings(int first) {
        for (int i = first; i < this.parents.length; ++i) {
            this.children[i] = -1;
        }
        if (first == 0) {
            first = 1;
            this.siblings[0] = -1;
        }
        for (int i = first; i < this.parents.length; ++i) {
            this.siblings[i] = this.children[this.parents[i]];
            this.children[this.parents[i]] = i;
        }
    }
    
    private void initParents(final IndexReader reader, final int first) throws IOException {
        if (reader.maxDoc() == first) {
            return;
        }
        final PostingsEnum positions = MultiFields.getTermPositionsEnum(reader, "$payloads$", Consts.PAYLOAD_PARENT_BYTES_REF, 88);
        if (positions == null || positions.advance(first) == Integer.MAX_VALUE) {
            throw new CorruptIndexException("Missing parent data for category " + first, reader.toString());
        }
        final int num = reader.maxDoc();
        int i = first;
        while (i < num) {
            if (positions.docID() != i) {
                throw new CorruptIndexException("Missing parent data for category " + i, reader.toString());
            }
            if (positions.freq() == 0) {
                throw new CorruptIndexException("Missing parent data for category " + i, reader.toString());
            }
            this.parents[i] = positions.nextPosition();
            if (positions.nextDoc() == Integer.MAX_VALUE) {
                if (i + 1 < num) {
                    throw new CorruptIndexException("Missing parent data for category " + (i + 1), reader.toString());
                }
                break;
            }
            else {
                ++i;
            }
        }
    }
    
    TaxonomyIndexArrays add(final int ordinal, final int parentOrdinal) {
        if (ordinal >= this.parents.length) {
            final int[] newarray = ArrayUtil.grow(this.parents, ordinal + 1);
            newarray[ordinal] = parentOrdinal;
            return new TaxonomyIndexArrays(newarray);
        }
        this.parents[ordinal] = parentOrdinal;
        return this;
    }
    
    @Override
    public int[] parents() {
        return this.parents;
    }
    
    @Override
    public int[] children() {
        if (!this.initializedChildren) {
            this.initChildrenSiblings(null);
        }
        return this.children;
    }
    
    @Override
    public int[] siblings() {
        if (!this.initializedChildren) {
            this.initChildrenSiblings(null);
        }
        return this.siblings;
    }
}
