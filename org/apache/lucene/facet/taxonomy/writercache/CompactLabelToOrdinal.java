package org.apache.lucene.facet.taxonomy.writercache;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.lucene.facet.taxonomy.FacetLabel;

public class CompactLabelToOrdinal extends LabelToOrdinal
{
    public static final float DefaultLoadFactor = 0.15f;
    static final char TERMINATOR_CHAR = '\uffff';
    private static final int COLLISION = -5;
    private HashArray[] hashArrays;
    private CollisionMap collisionMap;
    private CharBlockArray labelRepository;
    private int capacity;
    private int threshold;
    private float loadFactor;
    
    public int sizeOfMap() {
        return this.collisionMap.size();
    }
    
    private CompactLabelToOrdinal() {
    }
    
    public CompactLabelToOrdinal(final int initialCapacity, final float loadFactor, final int numHashArrays) {
        this.hashArrays = new HashArray[numHashArrays];
        this.capacity = determineCapacity((int)Math.pow(2.0, numHashArrays), initialCapacity);
        this.init();
        this.collisionMap = new CollisionMap(this.labelRepository);
        this.counter = 0;
        this.loadFactor = loadFactor;
        this.threshold = (int)(this.loadFactor * this.capacity);
    }
    
    static int determineCapacity(final int minCapacity, final int initialCapacity) {
        int capacity;
        for (capacity = minCapacity; capacity < initialCapacity; capacity <<= 1) {}
        return capacity;
    }
    
    private void init() {
        this.labelRepository = new CharBlockArray();
        CategoryPathUtils.serialize(new FacetLabel(new String[0]), this.labelRepository);
        int c = this.capacity;
        for (int i = 0; i < this.hashArrays.length; ++i) {
            this.hashArrays[i] = new HashArray(c);
            c /= 2;
        }
    }
    
    @Override
    public void addLabel(final FacetLabel label, final int ordinal) {
        if (this.collisionMap.size() > this.threshold) {
            this.grow();
        }
        final int hash = stringHashCode(label);
        for (int i = 0; i < this.hashArrays.length; ++i) {
            if (this.addLabel(this.hashArrays[i], label, hash, ordinal)) {
                return;
            }
        }
        final int prevVal = this.collisionMap.addLabel(label, hash, ordinal);
        if (prevVal != ordinal) {
            throw new IllegalArgumentException("Label already exists: " + label + " prev ordinal " + prevVal);
        }
    }
    
    @Override
    public int getOrdinal(final FacetLabel label) {
        if (label == null) {
            return -2;
        }
        final int hash = stringHashCode(label);
        for (int i = 0; i < this.hashArrays.length; ++i) {
            final int ord = this.getOrdinal(this.hashArrays[i], label, hash);
            if (ord != -5) {
                return ord;
            }
        }
        return this.collisionMap.get(label, hash);
    }
    
    private void grow() {
        final HashArray temp = this.hashArrays[this.hashArrays.length - 1];
        for (int i = this.hashArrays.length - 1; i > 0; --i) {
            this.hashArrays[i] = this.hashArrays[i - 1];
        }
        this.capacity *= 2;
        this.hashArrays[0] = new HashArray(this.capacity);
        for (int i = 1; i < this.hashArrays.length; ++i) {
            final int[] sourceOffsetArray = this.hashArrays[i].offsets;
            final int[] sourceCidsArray = this.hashArrays[i].cids;
            for (int k = 0; k < sourceOffsetArray.length; ++k) {
                for (int j = 0; j < i && sourceOffsetArray[k] != 0; ++j) {
                    final int[] targetOffsetArray = this.hashArrays[j].offsets;
                    final int[] targetCidsArray = this.hashArrays[j].cids;
                    final int newIndex = indexFor(stringHashCode(this.labelRepository, sourceOffsetArray[k]), targetOffsetArray.length);
                    if (targetOffsetArray[newIndex] == 0) {
                        targetOffsetArray[newIndex] = sourceOffsetArray[k];
                        targetCidsArray[newIndex] = sourceCidsArray[k];
                        sourceOffsetArray[k] = 0;
                    }
                }
            }
        }
        for (int i = 0; i < temp.offsets.length; ++i) {
            final int offset = temp.offsets[i];
            if (offset > 0) {
                final int hash = stringHashCode(this.labelRepository, offset);
                this.addLabelOffset(hash, temp.cids[i], offset);
            }
        }
        final CollisionMap oldCollisionMap = this.collisionMap;
        this.collisionMap = new CollisionMap(oldCollisionMap.capacity(), this.labelRepository);
        this.threshold = (int)(this.capacity * this.loadFactor);
        final Iterator<CollisionMap.Entry> it = oldCollisionMap.entryIterator();
        while (it.hasNext()) {
            final CollisionMap.Entry e = it.next();
            this.addLabelOffset(stringHashCode(this.labelRepository, e.offset), e.cid, e.offset);
        }
    }
    
    private boolean addLabel(final HashArray a, final FacetLabel label, final int hash, final int ordinal) {
        final int index = indexFor(hash, a.offsets.length);
        final int offset = a.offsets[index];
        if (offset == 0) {
            a.offsets[index] = this.labelRepository.length();
            CategoryPathUtils.serialize(label, this.labelRepository);
            a.cids[index] = ordinal;
            return true;
        }
        return false;
    }
    
    private void addLabelOffset(final int hash, final int cid, final int knownOffset) {
        for (int i = 0; i < this.hashArrays.length; ++i) {
            if (this.addLabelOffsetToHashArray(this.hashArrays[i], hash, cid, knownOffset)) {
                return;
            }
        }
        this.collisionMap.addLabelOffset(hash, knownOffset, cid);
        if (this.collisionMap.size() > this.threshold) {
            this.grow();
        }
    }
    
    private boolean addLabelOffsetToHashArray(final HashArray a, final int hash, final int ordinal, final int knownOffset) {
        final int index = indexFor(hash, a.offsets.length);
        final int offset = a.offsets[index];
        if (offset == 0) {
            a.offsets[index] = knownOffset;
            a.cids[index] = ordinal;
            return true;
        }
        return false;
    }
    
    private int getOrdinal(final HashArray a, final FacetLabel label, final int hash) {
        if (label == null) {
            return -2;
        }
        final int index = indexFor(hash, a.offsets.length);
        final int offset = a.offsets[index];
        if (offset == 0) {
            return -2;
        }
        if (CategoryPathUtils.equalsToSerialized(label, this.labelRepository, offset)) {
            return a.cids[index];
        }
        return -5;
    }
    
    static int indexFor(final int h, final int length) {
        return h & length - 1;
    }
    
    static int stringHashCode(final FacetLabel label) {
        int hash = label.hashCode();
        hash ^= (hash >>> 20 ^ hash >>> 12);
        hash = (hash ^ hash >>> 7 ^ hash >>> 4);
        return hash;
    }
    
    static int stringHashCode(final CharBlockArray labelRepository, final int offset) {
        int hash = CategoryPathUtils.hashCodeOfSerialized(labelRepository, offset);
        hash ^= (hash >>> 20 ^ hash >>> 12);
        hash = (hash ^ hash >>> 7 ^ hash >>> 4);
        return hash;
    }
    
    int getMemoryUsage() {
        int memoryUsage = 0;
        if (this.hashArrays != null) {
            for (final HashArray ha : this.hashArrays) {
                memoryUsage += ha.capacity * 2 * 4 + 4;
            }
        }
        if (this.labelRepository != null) {
            final int blockSize = this.labelRepository.blockSize;
            final int actualBlockSize = blockSize * 2 + 4;
            memoryUsage += this.labelRepository.blocks.size() * actualBlockSize;
            memoryUsage += 8;
        }
        if (this.collisionMap != null) {
            memoryUsage += this.collisionMap.getMemoryUsage();
        }
        return memoryUsage;
    }
    
    static CompactLabelToOrdinal open(final Path file, final float loadFactor, final int numHashArrays) throws IOException {
        final CompactLabelToOrdinal l2o = new CompactLabelToOrdinal();
        l2o.loadFactor = loadFactor;
        l2o.hashArrays = new HashArray[numHashArrays];
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new BufferedInputStream(Files.newInputStream(file, new OpenOption[0])));
            l2o.counter = dis.readInt();
            l2o.capacity = determineCapacity((int)Math.pow(2.0, l2o.hashArrays.length), l2o.counter);
            l2o.init();
            l2o.labelRepository = CharBlockArray.open(dis);
            l2o.collisionMap = new CollisionMap(l2o.labelRepository);
            int cid = 0;
            int lastStartOffset;
            int offset = lastStartOffset = 1;
            while (offset < l2o.labelRepository.length()) {
                final int length = (short)l2o.labelRepository.charAt(offset++);
                int hash;
                if ((hash = length) != 0) {
                    for (int i = 0; i < length; ++i) {
                        final int len = (short)l2o.labelRepository.charAt(offset++);
                        hash = hash * 31 + l2o.labelRepository.subSequence(offset, offset + len).hashCode();
                        offset += len;
                    }
                }
                hash ^= (hash >>> 20 ^ hash >>> 12);
                hash = (hash ^ hash >>> 7 ^ hash >>> 4);
                l2o.addLabelOffset(hash, cid, lastStartOffset);
                ++cid;
                lastStartOffset = offset;
            }
        }
        catch (final ClassNotFoundException cnfe) {
            throw new IOException("Invalid file format. Cannot deserialize.");
        }
        finally {
            if (dis != null) {
                dis.close();
            }
        }
        l2o.threshold = (int)(l2o.loadFactor * l2o.capacity);
        return l2o;
    }
    
    void flush(final Path file) throws IOException {
        final OutputStream fos = Files.newOutputStream(file, new OpenOption[0]);
        try {
            final BufferedOutputStream os = new BufferedOutputStream(fos);
            final DataOutputStream dos = new DataOutputStream(os);
            dos.writeInt(this.counter);
            this.labelRepository.flush(dos);
            dos.close();
        }
        finally {
            fos.close();
        }
    }
    
    private static final class HashArray
    {
        int[] offsets;
        int[] cids;
        int capacity;
        
        HashArray(final int c) {
            this.capacity = c;
            this.offsets = new int[this.capacity];
            this.cids = new int[this.capacity];
        }
    }
}
