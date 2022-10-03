package org.apache.lucene.util.fst;

import org.apache.lucene.util.PriorityQueue;
import org.apache.lucene.util.Constants;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.apache.lucene.store.ByteArrayDataOutput;
import org.apache.lucene.util.ArrayUtil;
import java.io.InputStream;
import org.apache.lucene.store.InputStreamDataInput;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import org.apache.lucene.store.OutputStreamDataOutput;
import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import org.apache.lucene.store.RAMOutputStream;
import org.apache.lucene.store.DataOutput;
import java.util.List;
import org.apache.lucene.util.Accountables;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.codecs.CodecUtil;
import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.packed.GrowableWriter;
import org.apache.lucene.util.packed.PackedInts;
import org.apache.lucene.util.Accountable;

public final class FST<T> implements Accountable
{
    private static final long BASE_RAM_BYTES_USED;
    private static final long ARC_SHALLOW_RAM_BYTES_USED;
    static final int BIT_FINAL_ARC = 1;
    static final int BIT_LAST_ARC = 2;
    static final int BIT_TARGET_NEXT = 4;
    static final int BIT_STOP_NODE = 8;
    public static final int BIT_ARC_HAS_OUTPUT = 16;
    static final int BIT_ARC_HAS_FINAL_OUTPUT = 32;
    private static final int BIT_TARGET_DELTA = 64;
    private static final byte ARCS_AS_FIXED_ARRAY = 32;
    static final int FIXED_ARRAY_SHALLOW_DISTANCE = 3;
    static final int FIXED_ARRAY_NUM_ARCS_SHALLOW = 5;
    static final int FIXED_ARRAY_NUM_ARCS_DEEP = 10;
    private static final String FILE_FORMAT_NAME = "FST";
    private static final int VERSION_START = 0;
    private static final int VERSION_INT_NUM_BYTES_PER_ARC = 1;
    private static final int VERSION_SHORT_BYTE2_LABELS = 2;
    private static final int VERSION_PACKED = 3;
    private static final int VERSION_VINT_TARGET = 4;
    private static final int VERSION_NO_NODE_ARC_COUNTS = 5;
    private static final int VERSION_CURRENT = 5;
    private static final long FINAL_END_NODE = -1L;
    private static final long NON_FINAL_END_NODE = 0L;
    public static final int END_LABEL = -1;
    public final INPUT_TYPE inputType;
    T emptyOutput;
    final BytesStore bytes;
    final byte[] bytesArray;
    private long startNode;
    public final Outputs<T> outputs;
    private final boolean packed;
    private PackedInts.Reader nodeRefToAddress;
    private Arc<T>[] cachedRootArcs;
    private GrowableWriter nodeAddress;
    private GrowableWriter inCounts;
    private final int version;
    public static final int DEFAULT_MAX_BLOCK_BITS;
    private int cachedArcsBytesUsed;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    private static boolean flag(final int flags, final int bit) {
        return (flags & bit) != 0x0;
    }
    
    FST(final INPUT_TYPE inputType, final Outputs<T> outputs, final boolean willPackFST, final float acceptableOverheadRatio, final int bytesPageBits) {
        this.startNode = -1L;
        this.inputType = inputType;
        this.outputs = outputs;
        this.version = 5;
        this.bytesArray = null;
        (this.bytes = new BytesStore(bytesPageBits)).writeByte((byte)0);
        if (willPackFST) {
            this.nodeAddress = new GrowableWriter(15, 8, acceptableOverheadRatio);
            this.inCounts = new GrowableWriter(1, 8, acceptableOverheadRatio);
        }
        else {
            this.nodeAddress = null;
            this.inCounts = null;
        }
        this.emptyOutput = null;
        this.packed = false;
        this.nodeRefToAddress = null;
    }
    
    public FST(final DataInput in, final Outputs<T> outputs) throws IOException {
        this(in, outputs, FST.DEFAULT_MAX_BLOCK_BITS);
    }
    
    public FST(final DataInput in, final Outputs<T> outputs, final int maxBlockBits) throws IOException {
        this.startNode = -1L;
        this.outputs = outputs;
        if (maxBlockBits < 1 || maxBlockBits > 30) {
            throw new IllegalArgumentException("maxBlockBits should be 1 .. 30; got " + maxBlockBits);
        }
        this.version = CodecUtil.checkHeader(in, "FST", 3, 5);
        this.packed = (in.readByte() == 1);
        if (in.readByte() == 1) {
            final BytesStore emptyBytes = new BytesStore(10);
            final int numBytes = in.readVInt();
            emptyBytes.copyBytes(in, numBytes);
            BytesReader reader;
            if (this.packed) {
                reader = emptyBytes.getForwardReader();
            }
            else {
                reader = emptyBytes.getReverseReader();
                if (numBytes > 0) {
                    reader.setPosition(numBytes - 1);
                }
            }
            this.emptyOutput = outputs.readFinalOutput(reader);
        }
        else {
            this.emptyOutput = null;
        }
        final byte t = in.readByte();
        switch (t) {
            case 0: {
                this.inputType = INPUT_TYPE.BYTE1;
                break;
            }
            case 1: {
                this.inputType = INPUT_TYPE.BYTE2;
                break;
            }
            case 2: {
                this.inputType = INPUT_TYPE.BYTE4;
                break;
            }
            default: {
                throw new IllegalStateException("invalid input type " + t);
            }
        }
        if (this.packed) {
            this.nodeRefToAddress = PackedInts.getReader(in);
        }
        else {
            this.nodeRefToAddress = null;
        }
        this.startNode = in.readVLong();
        if (this.version < 5) {
            in.readVLong();
            in.readVLong();
            in.readVLong();
        }
        final long numBytes2 = in.readVLong();
        if (numBytes2 > 1 << maxBlockBits) {
            this.bytes = new BytesStore(in, numBytes2, 1 << maxBlockBits);
            this.bytesArray = null;
        }
        else {
            this.bytes = null;
            in.readBytes(this.bytesArray = new byte[(int)numBytes2], 0, this.bytesArray.length);
        }
        this.cacheRootArcs();
    }
    
    public INPUT_TYPE getInputType() {
        return this.inputType;
    }
    
    private long ramBytesUsed(final Arc<T>[] arcs) {
        long size = 0L;
        if (arcs != null) {
            size += RamUsageEstimator.shallowSizeOf(arcs);
            for (final Arc<T> arc : arcs) {
                if (arc != null) {
                    size += FST.ARC_SHALLOW_RAM_BYTES_USED;
                    if (arc.output != null && arc.output != this.outputs.getNoOutput()) {
                        size += this.outputs.ramBytesUsed(arc.output);
                    }
                    if (arc.nextFinalOutput != null && arc.nextFinalOutput != this.outputs.getNoOutput()) {
                        size += this.outputs.ramBytesUsed(arc.nextFinalOutput);
                    }
                }
            }
        }
        return size;
    }
    
    @Override
    public long ramBytesUsed() {
        long size = FST.BASE_RAM_BYTES_USED;
        if (this.bytesArray != null) {
            size += this.bytesArray.length;
        }
        else {
            size += this.bytes.ramBytesUsed();
        }
        if (this.packed) {
            size += this.nodeRefToAddress.ramBytesUsed();
        }
        else if (this.nodeAddress != null) {
            size += this.nodeAddress.ramBytesUsed();
            size += this.inCounts.ramBytesUsed();
        }
        size += this.cachedArcsBytesUsed;
        return size;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        final List<Accountable> resources = new ArrayList<Accountable>();
        if (this.packed) {
            resources.add(Accountables.namedAccountable("node ref to address", this.nodeRefToAddress));
        }
        else if (this.nodeAddress != null) {
            resources.add(Accountables.namedAccountable("node addresses", this.nodeAddress));
            resources.add(Accountables.namedAccountable("in counts", this.inCounts));
        }
        return resources;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(input=" + this.inputType + ",output=" + this.outputs + ",packed=" + this.packed;
    }
    
    void finish(long newStartNode) throws IOException {
        assert newStartNode <= this.bytes.getPosition();
        if (this.startNode != -1L) {
            throw new IllegalStateException("already finished");
        }
        if (newStartNode == -1L && this.emptyOutput != null) {
            newStartNode = 0L;
        }
        this.startNode = newStartNode;
        this.bytes.finish();
        this.cacheRootArcs();
    }
    
    private long getNodeAddress(final long node) {
        if (this.nodeAddress != null) {
            return this.nodeAddress.get((int)node);
        }
        return node;
    }
    
    private void cacheRootArcs() throws IOException {
        assert this.cachedArcsBytesUsed == 0;
        final Arc<T> arc = new Arc<T>();
        this.getFirstArc(arc);
        if (targetHasArcs(arc)) {
            final BytesReader in = this.getBytesReader();
            final Arc<T>[] arcs = new Arc[128];
            this.readFirstRealTargetArc(arc.target, arc, in);
            int count = 0;
            while (FST.$assertionsDisabled || arc.label != -1) {
                if (arc.label < arcs.length) {
                    arcs[arc.label] = new Arc<T>().copyFrom(arc);
                    if (!arc.isLast()) {
                        this.readNextRealArc(arc, in);
                        ++count;
                        continue;
                    }
                }
                final int cacheRAM = (int)this.ramBytesUsed(arcs);
                if (count >= 5 && cacheRAM < this.ramBytesUsed() / 5L) {
                    this.cachedRootArcs = arcs;
                    this.cachedArcsBytesUsed = cacheRAM;
                }
                return;
            }
            throw new AssertionError();
        }
    }
    
    public T getEmptyOutput() {
        return this.emptyOutput;
    }
    
    void setEmptyOutput(final T v) throws IOException {
        if (this.emptyOutput != null) {
            this.emptyOutput = this.outputs.merge(this.emptyOutput, v);
        }
        else {
            this.emptyOutput = v;
        }
    }
    
    public void save(final DataOutput out) throws IOException {
        if (this.startNode == -1L) {
            throw new IllegalStateException("call finish first");
        }
        if (this.nodeAddress != null) {
            throw new IllegalStateException("cannot save an FST pre-packed FST; it must first be packed");
        }
        if (this.packed && !(this.nodeRefToAddress instanceof PackedInts.Mutable)) {
            throw new IllegalStateException("cannot save a FST which has been loaded from disk ");
        }
        CodecUtil.writeHeader(out, "FST", 5);
        if (this.packed) {
            out.writeByte((byte)1);
        }
        else {
            out.writeByte((byte)0);
        }
        if (this.emptyOutput != null) {
            out.writeByte((byte)1);
            final RAMOutputStream ros = new RAMOutputStream();
            this.outputs.writeFinalOutput(this.emptyOutput, ros);
            final byte[] emptyOutputBytes = new byte[(int)ros.getFilePointer()];
            ros.writeTo(emptyOutputBytes, 0);
            if (!this.packed) {
                for (int stopAt = emptyOutputBytes.length / 2, upto = 0; upto < stopAt; ++upto) {
                    final byte b = emptyOutputBytes[upto];
                    emptyOutputBytes[upto] = emptyOutputBytes[emptyOutputBytes.length - upto - 1];
                    emptyOutputBytes[emptyOutputBytes.length - upto - 1] = b;
                }
            }
            out.writeVInt(emptyOutputBytes.length);
            out.writeBytes(emptyOutputBytes, 0, emptyOutputBytes.length);
        }
        else {
            out.writeByte((byte)0);
        }
        byte t;
        if (this.inputType == INPUT_TYPE.BYTE1) {
            t = 0;
        }
        else if (this.inputType == INPUT_TYPE.BYTE2) {
            t = 1;
        }
        else {
            t = 2;
        }
        out.writeByte(t);
        if (this.packed) {
            ((PackedInts.Mutable)this.nodeRefToAddress).save(out);
        }
        out.writeVLong(this.startNode);
        if (this.bytes != null) {
            final long numBytes = this.bytes.getPosition();
            out.writeVLong(numBytes);
            this.bytes.writeTo(out);
        }
        else {
            assert this.bytesArray != null;
            out.writeVLong(this.bytesArray.length);
            out.writeBytes(this.bytesArray, 0, this.bytesArray.length);
        }
    }
    
    public void save(final Path path) throws IOException {
        try (final OutputStream os = new BufferedOutputStream(Files.newOutputStream(path, new OpenOption[0]))) {
            this.save(new OutputStreamDataOutput(os));
        }
    }
    
    public static <T> FST<T> read(final Path path, final Outputs<T> outputs) throws IOException {
        try (final InputStream is = Files.newInputStream(path, new OpenOption[0])) {
            return new FST<T>(new InputStreamDataInput(new BufferedInputStream(is)), outputs);
        }
    }
    
    private void writeLabel(final DataOutput out, final int v) throws IOException {
        assert v >= 0 : "v=" + v;
        if (this.inputType == INPUT_TYPE.BYTE1) {
            assert v <= 255 : "v=" + v;
            out.writeByte((byte)v);
        }
        else if (this.inputType == INPUT_TYPE.BYTE2) {
            assert v <= 65535 : "v=" + v;
            out.writeShort((short)v);
        }
        else {
            out.writeVInt(v);
        }
    }
    
    public int readLabel(final DataInput in) throws IOException {
        int v;
        if (this.inputType == INPUT_TYPE.BYTE1) {
            v = (in.readByte() & 0xFF);
        }
        else if (this.inputType == INPUT_TYPE.BYTE2) {
            v = (in.readShort() & 0xFFFF);
        }
        else {
            v = in.readVInt();
        }
        return v;
    }
    
    public static <T> boolean targetHasArcs(final Arc<T> arc) {
        return arc.target > 0L;
    }
    
    long addNode(final Builder<T> builder, final Builder.UnCompiledNode<T> nodeIn) throws IOException {
        final T NO_OUTPUT = this.outputs.getNoOutput();
        if (nodeIn.numArcs == 0) {
            if (nodeIn.isFinal) {
                return -1L;
            }
            return 0L;
        }
        else {
            final long startAddress = builder.bytes.getPosition();
            final boolean doFixedArray = this.shouldExpand(builder, nodeIn);
            if (doFixedArray && builder.reusedBytesPerArc.length < nodeIn.numArcs) {
                builder.reusedBytesPerArc = new int[ArrayUtil.oversize(nodeIn.numArcs, 1)];
            }
            builder.arcCount += nodeIn.numArcs;
            final int lastArc = nodeIn.numArcs - 1;
            long lastArcStart = builder.bytes.getPosition();
            int maxBytesPerArc = 0;
            for (int arcIdx = 0; arcIdx < nodeIn.numArcs; ++arcIdx) {
                final Builder.Arc<T> arc = nodeIn.arcs[arcIdx];
                final Builder.CompiledNode target = (Builder.CompiledNode)arc.target;
                int flags = 0;
                if (arcIdx == lastArc) {
                    flags += 2;
                }
                if (builder.lastFrozenNode == target.node && !doFixedArray) {
                    flags += 4;
                }
                if (arc.isFinal) {
                    ++flags;
                    if (arc.nextFinalOutput != NO_OUTPUT) {
                        flags += 32;
                    }
                }
                else {
                    assert arc.nextFinalOutput == NO_OUTPUT;
                }
                final boolean targetHasArcs = target.node > 0L;
                if (!targetHasArcs) {
                    flags += 8;
                }
                else if (this.inCounts != null) {
                    this.inCounts.set((int)target.node, this.inCounts.get((int)target.node) + 1L);
                }
                if (arc.output != NO_OUTPUT) {
                    flags += 16;
                }
                builder.bytes.writeByte((byte)flags);
                this.writeLabel(builder.bytes, arc.label);
                if (arc.output != NO_OUTPUT) {
                    this.outputs.write(arc.output, builder.bytes);
                }
                if (arc.nextFinalOutput != NO_OUTPUT) {
                    this.outputs.writeFinalOutput(arc.nextFinalOutput, builder.bytes);
                }
                if (targetHasArcs && (flags & 0x4) == 0x0) {
                    assert target.node > 0L;
                    builder.bytes.writeVLong(target.node);
                }
                if (doFixedArray) {
                    builder.reusedBytesPerArc[arcIdx] = (int)(builder.bytes.getPosition() - lastArcStart);
                    lastArcStart = builder.bytes.getPosition();
                    maxBytesPerArc = Math.max(maxBytesPerArc, builder.reusedBytesPerArc[arcIdx]);
                }
            }
            if (doFixedArray) {
                final int MAX_HEADER_SIZE = 11;
                assert maxBytesPerArc > 0;
                final byte[] header = new byte[11];
                final ByteArrayDataOutput bad = new ByteArrayDataOutput(header);
                bad.writeByte((byte)32);
                bad.writeVInt(nodeIn.numArcs);
                bad.writeVInt(maxBytesPerArc);
                final int headerLen = bad.getPosition();
                final long fixedArrayStart = startAddress + headerLen;
                long srcPos = builder.bytes.getPosition();
                long destPos = fixedArrayStart + nodeIn.numArcs * maxBytesPerArc;
                assert destPos >= srcPos;
                if (destPos > srcPos) {
                    builder.bytes.skipBytes((int)(destPos - srcPos));
                    for (int arcIdx2 = nodeIn.numArcs - 1; arcIdx2 >= 0; --arcIdx2) {
                        destPos -= maxBytesPerArc;
                        srcPos -= builder.reusedBytesPerArc[arcIdx2];
                        if (srcPos != destPos) {
                            assert destPos > srcPos : "destPos=" + destPos + " srcPos=" + srcPos + " arcIdx=" + arcIdx2 + " maxBytesPerArc=" + maxBytesPerArc + " reusedBytesPerArc[arcIdx]=" + builder.reusedBytesPerArc[arcIdx2] + " nodeIn.numArcs=" + nodeIn.numArcs;
                            builder.bytes.copyBytes(srcPos, destPos, builder.reusedBytesPerArc[arcIdx2]);
                        }
                    }
                }
                builder.bytes.writeBytes(startAddress, header, 0, headerLen);
            }
            final long thisNodeAddress = builder.bytes.getPosition() - 1L;
            builder.bytes.reverse(startAddress, thisNodeAddress);
            if (this.nodeAddress != null && builder.nodeCount == 2147483647L) {
                throw new IllegalStateException("cannot create a packed FST with more than 2.1 billion nodes");
            }
            ++builder.nodeCount;
            long node;
            if (this.nodeAddress != null) {
                if ((int)builder.nodeCount == this.nodeAddress.size()) {
                    this.nodeAddress = this.nodeAddress.resize(ArrayUtil.oversize(this.nodeAddress.size() + 1, this.nodeAddress.getBitsPerValue()));
                    this.inCounts = this.inCounts.resize(ArrayUtil.oversize(this.inCounts.size() + 1, this.inCounts.getBitsPerValue()));
                }
                this.nodeAddress.set((int)builder.nodeCount, thisNodeAddress);
                node = builder.nodeCount;
            }
            else {
                node = thisNodeAddress;
            }
            return node;
        }
    }
    
    public Arc<T> getFirstArc(final Arc<T> arc) {
        final T NO_OUTPUT = this.outputs.getNoOutput();
        if (this.emptyOutput != null) {
            arc.flags = 3;
            arc.nextFinalOutput = this.emptyOutput;
            if (this.emptyOutput != NO_OUTPUT) {
                arc.flags |= 0x20;
            }
        }
        else {
            arc.flags = 2;
            arc.nextFinalOutput = NO_OUTPUT;
        }
        arc.output = NO_OUTPUT;
        arc.target = this.startNode;
        return arc;
    }
    
    public Arc<T> readLastTargetArc(final Arc<T> follow, final Arc<T> arc, final BytesReader in) throws IOException {
        if (!targetHasArcs(follow)) {
            assert follow.isFinal();
            arc.label = -1;
            arc.target = -1L;
            arc.output = follow.nextFinalOutput;
            arc.flags = 2;
            return arc;
        }
        else {
            in.setPosition(this.getNodeAddress(follow.target));
            arc.node = follow.target;
            final byte b = in.readByte();
            if (b == 32) {
                arc.numArcs = in.readVInt();
                if (this.packed || this.version >= 4) {
                    arc.bytesPerArc = in.readVInt();
                }
                else {
                    arc.bytesPerArc = in.readInt();
                }
                arc.posArcsStart = in.getPosition();
                arc.arcIdx = arc.numArcs - 2;
            }
            else {
                arc.flags = b;
                arc.bytesPerArc = 0;
                while (!arc.isLast()) {
                    this.readLabel(in);
                    if (arc.flag(16)) {
                        this.outputs.skipOutput(in);
                    }
                    if (arc.flag(32)) {
                        this.outputs.skipFinalOutput(in);
                    }
                    if (!arc.flag(8)) {
                        if (!arc.flag(4)) {
                            if (this.packed) {
                                in.readVLong();
                            }
                            else {
                                this.readUnpackedNodeTarget(in);
                            }
                        }
                    }
                    arc.flags = in.readByte();
                }
                in.skipBytes(-1L);
                arc.nextArc = in.getPosition();
            }
            this.readNextRealArc(arc, in);
            assert arc.isLast();
            return arc;
        }
    }
    
    private long readUnpackedNodeTarget(final BytesReader in) throws IOException {
        long target;
        if (this.version < 4) {
            target = in.readInt();
        }
        else {
            target = in.readVLong();
        }
        return target;
    }
    
    public Arc<T> readFirstTargetArc(final Arc<T> follow, final Arc<T> arc, final BytesReader in) throws IOException {
        if (follow.isFinal()) {
            arc.label = -1;
            arc.output = follow.nextFinalOutput;
            arc.flags = 1;
            if (follow.target <= 0L) {
                arc.flags |= 0x2;
            }
            else {
                arc.node = follow.target;
                arc.nextArc = follow.target;
            }
            arc.target = -1L;
            return arc;
        }
        return this.readFirstRealTargetArc(follow.target, arc, in);
    }
    
    public Arc<T> readFirstRealTargetArc(final long node, final Arc<T> arc, final BytesReader in) throws IOException {
        final long address = this.getNodeAddress(node);
        in.setPosition(address);
        arc.node = node;
        if (in.readByte() == 32) {
            arc.numArcs = in.readVInt();
            if (this.packed || this.version >= 4) {
                arc.bytesPerArc = in.readVInt();
            }
            else {
                arc.bytesPerArc = in.readInt();
            }
            arc.arcIdx = -1;
            final long position = in.getPosition();
            arc.posArcsStart = position;
            arc.nextArc = position;
        }
        else {
            arc.nextArc = address;
            arc.bytesPerArc = 0;
        }
        return this.readNextRealArc(arc, in);
    }
    
    boolean isExpandedTarget(final Arc<T> follow, final BytesReader in) throws IOException {
        if (!targetHasArcs(follow)) {
            return false;
        }
        in.setPosition(this.getNodeAddress(follow.target));
        return in.readByte() == 32;
    }
    
    public Arc<T> readNextArc(final Arc<T> arc, final BytesReader in) throws IOException {
        if (arc.label != -1) {
            return this.readNextRealArc(arc, in);
        }
        if (arc.nextArc <= 0L) {
            throw new IllegalArgumentException("cannot readNextArc when arc.isLast()=true");
        }
        return this.readFirstRealTargetArc(arc.nextArc, arc, in);
    }
    
    public int readNextArcLabel(final Arc<T> arc, final BytesReader in) throws IOException {
        assert !arc.isLast();
        if (arc.label == -1) {
            final long pos = this.getNodeAddress(arc.nextArc);
            in.setPosition(pos);
            final byte b = in.readByte();
            if (b == 32) {
                in.readVInt();
                if (this.packed || this.version >= 4) {
                    in.readVInt();
                }
                else {
                    in.readInt();
                }
            }
            else {
                in.setPosition(pos);
            }
        }
        else if (arc.bytesPerArc != 0) {
            in.setPosition(arc.posArcsStart);
            in.skipBytes((1 + arc.arcIdx) * arc.bytesPerArc);
        }
        else {
            in.setPosition(arc.nextArc);
        }
        in.readByte();
        return this.readLabel(in);
    }
    
    public Arc<T> readNextRealArc(final Arc<T> arc, final BytesReader in) throws IOException {
        if (arc.bytesPerArc != 0) {
            ++arc.arcIdx;
            assert arc.arcIdx < arc.numArcs;
            in.setPosition(arc.posArcsStart);
            in.skipBytes(arc.arcIdx * arc.bytesPerArc);
        }
        else {
            in.setPosition(arc.nextArc);
        }
        arc.flags = in.readByte();
        arc.label = this.readLabel(in);
        if (arc.flag(16)) {
            arc.output = this.outputs.read(in);
        }
        else {
            arc.output = this.outputs.getNoOutput();
        }
        if (arc.flag(32)) {
            arc.nextFinalOutput = this.outputs.readFinalOutput(in);
        }
        else {
            arc.nextFinalOutput = this.outputs.getNoOutput();
        }
        if (arc.flag(8)) {
            if (arc.flag(1)) {
                arc.target = -1L;
            }
            else {
                arc.target = 0L;
            }
            arc.nextArc = in.getPosition();
        }
        else if (arc.flag(4)) {
            arc.nextArc = in.getPosition();
            if (this.nodeAddress == null) {
                if (!arc.flag(2)) {
                    if (arc.bytesPerArc == 0) {
                        this.seekToNextNode(in);
                    }
                    else {
                        in.setPosition(arc.posArcsStart);
                        in.skipBytes(arc.bytesPerArc * arc.numArcs);
                    }
                }
                arc.target = in.getPosition();
            }
            else {
                arc.target = arc.node - 1L;
                assert arc.target > 0L;
            }
        }
        else {
            if (this.packed) {
                final long pos = in.getPosition();
                final long code = in.readVLong();
                if (arc.flag(64)) {
                    arc.target = pos + code;
                }
                else if (code < this.nodeRefToAddress.size()) {
                    arc.target = this.nodeRefToAddress.get((int)code);
                }
                else {
                    arc.target = code;
                }
            }
            else {
                arc.target = this.readUnpackedNodeTarget(in);
            }
            arc.nextArc = in.getPosition();
        }
        return arc;
    }
    
    private boolean assertRootCachedArc(final int label, final Arc<T> cachedArc) throws IOException {
        final Arc<T> arc = new Arc<T>();
        this.getFirstArc(arc);
        final BytesReader in = this.getBytesReader();
        final Arc<T> result = this.findTargetArc(label, arc, arc, in, false);
        if (result == null) {
            assert cachedArc == null;
        }
        else {
            assert cachedArc != null;
            assert cachedArc.arcIdx == result.arcIdx;
            assert cachedArc.bytesPerArc == result.bytesPerArc;
            assert cachedArc.flags == result.flags;
            assert cachedArc.label == result.label;
            assert cachedArc.nextArc == result.nextArc;
            assert cachedArc.nextFinalOutput.equals(result.nextFinalOutput);
            assert cachedArc.node == result.node;
            assert cachedArc.numArcs == result.numArcs;
            assert cachedArc.output.equals(result.output);
            assert cachedArc.posArcsStart == result.posArcsStart;
            assert cachedArc.target == result.target;
        }
        return true;
    }
    
    public Arc<T> findTargetArc(final int labelToMatch, final Arc<T> follow, final Arc<T> arc, final BytesReader in) throws IOException {
        return this.findTargetArc(labelToMatch, follow, arc, in, true);
    }
    
    private Arc<T> findTargetArc(final int labelToMatch, final Arc<T> follow, final Arc<T> arc, final BytesReader in, final boolean useRootArcCache) throws IOException {
        if (labelToMatch == -1) {
            if (follow.isFinal()) {
                if (follow.target <= 0L) {
                    arc.flags = 2;
                }
                else {
                    arc.flags = 0;
                    arc.nextArc = follow.target;
                    arc.node = follow.target;
                }
                arc.output = follow.nextFinalOutput;
                arc.label = -1;
                return arc;
            }
            return null;
        }
        else if (useRootArcCache && this.cachedRootArcs != null && follow.target == this.startNode && labelToMatch < this.cachedRootArcs.length) {
            final Arc<T> result = this.cachedRootArcs[labelToMatch];
            assert this.assertRootCachedArc(labelToMatch, result);
            if (result == null) {
                return null;
            }
            arc.copyFrom(result);
            return arc;
        }
        else {
            if (!targetHasArcs(follow)) {
                return null;
            }
            in.setPosition(this.getNodeAddress(follow.target));
            arc.node = follow.target;
            if (in.readByte() == 32) {
                arc.numArcs = in.readVInt();
                if (this.packed || this.version >= 4) {
                    arc.bytesPerArc = in.readVInt();
                }
                else {
                    arc.bytesPerArc = in.readInt();
                }
                arc.posArcsStart = in.getPosition();
                int low = 0;
                int high = arc.numArcs - 1;
                while (low <= high) {
                    final int mid = low + high >>> 1;
                    in.setPosition(arc.posArcsStart);
                    in.skipBytes(arc.bytesPerArc * mid + 1);
                    final int midLabel = this.readLabel(in);
                    final int cmp = midLabel - labelToMatch;
                    if (cmp < 0) {
                        low = mid + 1;
                    }
                    else {
                        if (cmp <= 0) {
                            arc.arcIdx = mid - 1;
                            return this.readNextRealArc(arc, in);
                        }
                        high = mid - 1;
                    }
                }
                return null;
            }
            this.readFirstRealTargetArc(follow.target, arc, in);
            while (arc.label != labelToMatch) {
                if (arc.label > labelToMatch) {
                    return null;
                }
                if (arc.isLast()) {
                    return null;
                }
                this.readNextRealArc(arc, in);
            }
            return arc;
        }
    }
    
    private void seekToNextNode(final BytesReader in) throws IOException {
        int flags;
        do {
            flags = in.readByte();
            this.readLabel(in);
            if (flag(flags, 16)) {
                this.outputs.skipOutput(in);
            }
            if (flag(flags, 32)) {
                this.outputs.skipFinalOutput(in);
            }
            if (!flag(flags, 8) && !flag(flags, 4)) {
                if (this.packed) {
                    in.readVLong();
                }
                else {
                    this.readUnpackedNodeTarget(in);
                }
            }
        } while (!flag(flags, 2));
    }
    
    private boolean shouldExpand(final Builder<T> builder, final Builder.UnCompiledNode<T> node) {
        return builder.allowArrayArcs && ((node.depth <= 3 && node.numArcs >= 5) || node.numArcs >= 10);
    }
    
    public BytesReader getBytesReader() {
        if (this.packed) {
            if (this.bytesArray != null) {
                return new ForwardBytesReader(this.bytesArray);
            }
            return this.bytes.getForwardReader();
        }
        else {
            if (this.bytesArray != null) {
                return new ReverseBytesReader(this.bytesArray);
            }
            return this.bytes.getReverseReader();
        }
    }
    
    private FST(final INPUT_TYPE inputType, final Outputs<T> outputs, final int bytesPageBits) {
        this.startNode = -1L;
        this.version = 5;
        this.packed = true;
        this.inputType = inputType;
        this.bytesArray = null;
        this.bytes = new BytesStore(bytesPageBits);
        this.outputs = outputs;
    }
    
    FST<T> pack(final Builder<T> builder, final int minInCountDeref, final int maxDerefNodes, final float acceptableOverheadRatio) throws IOException {
        if (this.nodeAddress == null) {
            throw new IllegalArgumentException("this FST was not built with willPackFST=true");
        }
        final T NO_OUTPUT = this.outputs.getNoOutput();
        final Arc<T> arc = new Arc<T>();
        final BytesReader r = this.getBytesReader();
        final int topN = Math.min(maxDerefNodes, this.inCounts.size());
        final NodeQueue q = new NodeQueue(topN);
        NodeAndInCount bottom = null;
        for (int node = 0; node < this.inCounts.size(); ++node) {
            if (this.inCounts.get(node) >= minInCountDeref) {
                if (bottom == null) {
                    q.add(new NodeAndInCount(node, (int)this.inCounts.get(node)));
                    if (q.size() == topN) {
                        bottom = q.top();
                    }
                }
                else if (this.inCounts.get(node) > bottom.count) {
                    q.insertWithOverflow(new NodeAndInCount(node, (int)this.inCounts.get(node)));
                }
            }
        }
        this.inCounts = null;
        final Map<Integer, Integer> topNodeMap = new HashMap<Integer, Integer>();
        for (int downTo = q.size() - 1; downTo >= 0; --downTo) {
            final NodeAndInCount n = q.pop();
            topNodeMap.put(n.node, downTo);
        }
        final GrowableWriter newNodeAddress = new GrowableWriter(PackedInts.bitsRequired(builder.bytes.getPosition()), (int)(1L + builder.nodeCount), acceptableOverheadRatio);
        for (int node2 = 1; node2 <= builder.nodeCount; ++node2) {
            newNodeAddress.set(node2, 1L + builder.bytes.getPosition() - this.nodeAddress.get(node2));
        }
        while (true) {
            boolean changed = false;
            boolean negDelta = false;
            final FST<T> fst = new FST<T>(this.inputType, this.outputs, builder.bytes.getBlockBits());
            final BytesStore writer = fst.bytes;
            writer.writeByte((byte)0);
            int nextCount;
            int topCount;
            int absCount;
            int deltaCount = absCount = (topCount = (nextCount = 0));
            int changedCount = 0;
            long addressError = 0L;
            int node3 = (int)builder.nodeCount;
        Label_0408:
            while (true) {
                while (node3 >= 1) {
                    final long address = writer.getPosition();
                    if (address != newNodeAddress.get(node3)) {
                        addressError = address - newNodeAddress.get(node3);
                        changed = true;
                        newNodeAddress.set(node3, address);
                        ++changedCount;
                    }
                    int nodeArcCount = 0;
                    int bytesPerArc = 0;
                    boolean retry = false;
                    boolean anyNegDelta = false;
                    while (true) {
                        this.readFirstRealTargetArc(node3, arc, r);
                        final boolean useArcArray = arc.bytesPerArc != 0;
                        if (useArcArray) {
                            if (bytesPerArc == 0) {
                                bytesPerArc = arc.bytesPerArc;
                            }
                            writer.writeByte((byte)32);
                            writer.writeVInt(arc.numArcs);
                            writer.writeVInt(bytesPerArc);
                        }
                        int maxBytesPerArc = 0;
                        while (true) {
                            final long arcStartPos = writer.getPosition();
                            ++nodeArcCount;
                            byte flags = 0;
                            if (arc.isLast()) {
                                flags += 2;
                            }
                            if (!useArcArray && node3 != 1 && arc.target == node3 - 1) {
                                flags += 4;
                                if (!retry) {
                                    ++nextCount;
                                }
                            }
                            if (arc.isFinal()) {
                                ++flags;
                                if (arc.nextFinalOutput != NO_OUTPUT) {
                                    flags += 32;
                                }
                            }
                            else {
                                assert arc.nextFinalOutput == NO_OUTPUT;
                            }
                            if (!targetHasArcs(arc)) {
                                flags += 8;
                            }
                            if (arc.output != NO_OUTPUT) {
                                flags += 16;
                            }
                            final boolean doWriteTarget = targetHasArcs(arc) && (flags & 0x4) == 0x0;
                            long absPtr;
                            if (doWriteTarget) {
                                final Integer ptr = topNodeMap.get(arc.target);
                                if (ptr != null) {
                                    absPtr = ptr;
                                }
                                else {
                                    absPtr = topNodeMap.size() + newNodeAddress.get((int)arc.target) + addressError;
                                }
                                long delta = newNodeAddress.get((int)arc.target) + addressError - writer.getPosition() - 2L;
                                if (delta < 0L) {
                                    anyNegDelta = true;
                                    delta = 0L;
                                }
                                if (delta < absPtr) {
                                    flags |= 0x40;
                                }
                            }
                            else {
                                absPtr = 0L;
                            }
                            assert flags != 32;
                            writer.writeByte(flags);
                            fst.writeLabel(writer, arc.label);
                            if (arc.output != NO_OUTPUT) {
                                this.outputs.write(arc.output, writer);
                            }
                            if (arc.nextFinalOutput != NO_OUTPUT) {
                                this.outputs.writeFinalOutput(arc.nextFinalOutput, writer);
                            }
                            if (doWriteTarget) {
                                long delta2 = newNodeAddress.get((int)arc.target) + addressError - writer.getPosition();
                                if (delta2 < 0L) {
                                    anyNegDelta = true;
                                    delta2 = 0L;
                                }
                                if (flag(flags, 64)) {
                                    writer.writeVLong(delta2);
                                    if (!retry) {
                                        ++deltaCount;
                                    }
                                }
                                else {
                                    writer.writeVLong(absPtr);
                                    if (!retry) {
                                        if (absPtr >= topNodeMap.size()) {
                                            ++absCount;
                                        }
                                        else {
                                            ++topCount;
                                        }
                                    }
                                }
                            }
                            if (useArcArray) {
                                final int arcBytes = (int)(writer.getPosition() - arcStartPos);
                                maxBytesPerArc = Math.max(maxBytesPerArc, arcBytes);
                                writer.skipBytes((int)(arcStartPos + bytesPerArc - writer.getPosition()));
                            }
                            if (arc.isLast()) {
                                if (useArcArray && maxBytesPerArc != bytesPerArc && (!retry || maxBytesPerArc > bytesPerArc)) {
                                    bytesPerArc = maxBytesPerArc;
                                    writer.truncate(address);
                                    nodeArcCount = 0;
                                    retry = true;
                                    anyNegDelta = false;
                                    break;
                                }
                                negDelta |= anyNegDelta;
                                --node3;
                                continue Label_0408;
                            }
                            else {
                                this.readNextRealArc(arc, r);
                            }
                        }
                    }
                }
                break;
            }
            if (!changed) {
                assert !negDelta;
                long maxAddress = 0L;
                for (final long key : topNodeMap.keySet()) {
                    maxAddress = Math.max(maxAddress, newNodeAddress.get((int)key));
                }
                final PackedInts.Mutable nodeRefToAddressIn = PackedInts.getMutable(topNodeMap.size(), PackedInts.bitsRequired(maxAddress), acceptableOverheadRatio);
                for (final Map.Entry<Integer, Integer> ent : topNodeMap.entrySet()) {
                    nodeRefToAddressIn.set(ent.getValue(), newNodeAddress.get(ent.getKey()));
                }
                fst.nodeRefToAddress = nodeRefToAddressIn;
                fst.startNode = newNodeAddress.get((int)this.startNode);
                if (this.emptyOutput != null) {
                    fst.setEmptyOutput(this.emptyOutput);
                }
                fst.bytes.finish();
                fst.cacheRootArcs();
                return fst;
            }
        }
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(FST.class);
        ARC_SHALLOW_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(Arc.class);
        DEFAULT_MAX_BLOCK_BITS = (Constants.JRE_IS_64BIT ? 30 : 28);
    }
    
    public enum INPUT_TYPE
    {
        BYTE1, 
        BYTE2, 
        BYTE4;
    }
    
    public static final class Arc<T>
    {
        public int label;
        public T output;
        long node;
        public long target;
        byte flags;
        public T nextFinalOutput;
        long nextArc;
        public long posArcsStart;
        public int bytesPerArc;
        public int arcIdx;
        public int numArcs;
        
        public Arc<T> copyFrom(final Arc<T> other) {
            this.node = other.node;
            this.label = other.label;
            this.target = other.target;
            this.flags = other.flags;
            this.output = other.output;
            this.nextFinalOutput = other.nextFinalOutput;
            this.nextArc = other.nextArc;
            this.bytesPerArc = other.bytesPerArc;
            if (this.bytesPerArc != 0) {
                this.posArcsStart = other.posArcsStart;
                this.arcIdx = other.arcIdx;
                this.numArcs = other.numArcs;
            }
            return this;
        }
        
        boolean flag(final int flag) {
            return flag(this.flags, flag);
        }
        
        public boolean isLast() {
            return this.flag(2);
        }
        
        public boolean isFinal() {
            return this.flag(1);
        }
        
        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder();
            b.append("node=" + this.node);
            b.append(" target=" + this.target);
            b.append(" label=0x" + Integer.toHexString(this.label));
            if (this.flag(1)) {
                b.append(" final");
            }
            if (this.flag(2)) {
                b.append(" last");
            }
            if (this.flag(4)) {
                b.append(" targetNext");
            }
            if (this.flag(8)) {
                b.append(" stop");
            }
            if (this.flag(16)) {
                b.append(" output=" + this.output);
            }
            if (this.flag(32)) {
                b.append(" nextFinalOutput=" + this.nextFinalOutput);
            }
            if (this.bytesPerArc != 0) {
                b.append(" arcArray(idx=" + this.arcIdx + " of " + this.numArcs + ")");
            }
            return b.toString();
        }
    }
    
    public abstract static class BytesReader extends DataInput
    {
        public abstract long getPosition();
        
        public abstract void setPosition(final long p0);
        
        public abstract boolean reversed();
    }
    
    private static class NodeAndInCount implements Comparable<NodeAndInCount>
    {
        final int node;
        final int count;
        
        public NodeAndInCount(final int node, final int count) {
            this.node = node;
            this.count = count;
        }
        
        @Override
        public int compareTo(final NodeAndInCount other) {
            if (this.count > other.count) {
                return 1;
            }
            if (this.count < other.count) {
                return -1;
            }
            return other.node - this.node;
        }
    }
    
    private static class NodeQueue extends PriorityQueue<NodeAndInCount>
    {
        public NodeQueue(final int topN) {
            super(topN, false);
        }
        
        public boolean lessThan(final NodeAndInCount a, final NodeAndInCount b) {
            final int cmp = a.compareTo(b);
            assert cmp != 0;
            return cmp < 0;
        }
    }
}
