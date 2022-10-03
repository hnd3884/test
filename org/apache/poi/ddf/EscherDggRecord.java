package org.apache.poi.ddf;

import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import com.zaxxer.sparsebits.SparseBitSet;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.LittleEndian;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;

public final class EscherDggRecord extends EscherRecord
{
    public static final short RECORD_ID;
    private int field_1_shapeIdMax;
    private int field_3_numShapesSaved;
    private int field_4_drawingsSaved;
    private final List<FileIdCluster> field_5_fileIdClusters;
    private int maxDgId;
    
    public EscherDggRecord() {
        this.field_5_fileIdClusters = new ArrayList<FileIdCluster>();
    }
    
    public EscherDggRecord(final EscherDggRecord other) {
        super(other);
        this.field_5_fileIdClusters = new ArrayList<FileIdCluster>();
        this.field_1_shapeIdMax = other.field_1_shapeIdMax;
        this.field_3_numShapesSaved = other.field_3_numShapesSaved;
        this.field_4_drawingsSaved = other.field_4_drawingsSaved;
        other.field_5_fileIdClusters.stream().map((Function<? super Object, ?>)FileIdCluster::new).forEach(this.field_5_fileIdClusters::add);
        this.maxDgId = other.maxDgId;
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        int bytesRemaining = this.readHeader(data, offset);
        final int pos = offset + 8;
        int size = 0;
        this.field_1_shapeIdMax = LittleEndian.getInt(data, pos + size);
        size += 4;
        size += 4;
        this.field_3_numShapesSaved = LittleEndian.getInt(data, pos + size);
        size += 4;
        this.field_4_drawingsSaved = LittleEndian.getInt(data, pos + size);
        size += 4;
        this.field_5_fileIdClusters.clear();
        for (int numIdClusters = (bytesRemaining - size) / 8, i = 0; i < numIdClusters; ++i) {
            final int drawingGroupId = LittleEndian.getInt(data, pos + size);
            final int numShapeIdsUsed = LittleEndian.getInt(data, pos + size + 4);
            final FileIdCluster fic = new FileIdCluster(drawingGroupId, numShapeIdsUsed);
            this.field_5_fileIdClusters.add(fic);
            this.maxDgId = Math.max(this.maxDgId, drawingGroupId);
            size += 8;
        }
        bytesRemaining -= size;
        if (bytesRemaining != 0) {
            throw new RecordFormatException("Expecting no remaining data but got " + bytesRemaining + " byte(s).");
        }
        return 8 + size;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        int pos = offset;
        LittleEndian.putShort(data, pos, this.getOptions());
        pos += 2;
        LittleEndian.putShort(data, pos, this.getRecordId());
        pos += 2;
        final int remainingBytes = this.getRecordSize() - 8;
        LittleEndian.putInt(data, pos, remainingBytes);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_1_shapeIdMax);
        pos += 4;
        LittleEndian.putInt(data, pos, this.getNumIdClusters());
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_3_numShapesSaved);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_4_drawingsSaved);
        pos += 4;
        for (final FileIdCluster fic : this.field_5_fileIdClusters) {
            LittleEndian.putInt(data, pos, fic.getDrawingGroupId());
            pos += 4;
            LittleEndian.putInt(data, pos, fic.getNumShapeIdsUsed());
            pos += 4;
        }
        listener.afterRecordSerialize(pos, this.getRecordId(), this.getRecordSize(), this);
        return this.getRecordSize();
    }
    
    @Override
    public int getRecordSize() {
        return 24 + 8 * this.field_5_fileIdClusters.size();
    }
    
    @Override
    public short getRecordId() {
        return EscherDggRecord.RECORD_ID;
    }
    
    @Override
    public String getRecordName() {
        return EscherRecordTypes.DGG.recordName;
    }
    
    public int getShapeIdMax() {
        return this.field_1_shapeIdMax;
    }
    
    public void setShapeIdMax(final int shapeIdMax) {
        this.field_1_shapeIdMax = shapeIdMax;
    }
    
    public int getNumIdClusters() {
        return this.field_5_fileIdClusters.isEmpty() ? 0 : (this.field_5_fileIdClusters.size() + 1);
    }
    
    public int getNumShapesSaved() {
        return this.field_3_numShapesSaved;
    }
    
    public void setNumShapesSaved(final int numShapesSaved) {
        this.field_3_numShapesSaved = numShapesSaved;
    }
    
    public int getDrawingsSaved() {
        return this.field_4_drawingsSaved;
    }
    
    public void setDrawingsSaved(final int drawingsSaved) {
        this.field_4_drawingsSaved = drawingsSaved;
    }
    
    public int getMaxDrawingGroupId() {
        return this.maxDgId;
    }
    
    public FileIdCluster[] getFileIdClusters() {
        return this.field_5_fileIdClusters.toArray(new FileIdCluster[0]);
    }
    
    public void setFileIdClusters(final FileIdCluster[] fileIdClusters) {
        this.field_5_fileIdClusters.clear();
        if (fileIdClusters != null) {
            this.field_5_fileIdClusters.addAll(Arrays.asList(fileIdClusters));
        }
    }
    
    public FileIdCluster addCluster(final int dgId, final int numShapedUsed) {
        return this.addCluster(dgId, numShapedUsed, true);
    }
    
    public FileIdCluster addCluster(final int dgId, final int numShapedUsed, final boolean sort) {
        final FileIdCluster ficNew = new FileIdCluster(dgId, numShapedUsed);
        this.field_5_fileIdClusters.add(ficNew);
        this.maxDgId = Math.min(this.maxDgId, dgId);
        if (sort) {
            this.sortCluster();
        }
        return ficNew;
    }
    
    private void sortCluster() {
        this.field_5_fileIdClusters.sort((x$0, x$1) -> compareFileIdCluster(x$0, x$1));
    }
    
    public short findNewDrawingGroupId() {
        final SparseBitSet bs = new SparseBitSet();
        bs.set(0);
        for (final FileIdCluster fic : this.field_5_fileIdClusters) {
            bs.set(fic.getDrawingGroupId());
        }
        return (short)bs.nextClearBit(0);
    }
    
    public int allocateShapeId(final EscherDgRecord dg, final boolean sort) {
        final short drawingGroupId = dg.getDrawingGroupId();
        ++this.field_3_numShapesSaved;
        FileIdCluster ficAdd = null;
        int index = 1;
        for (final FileIdCluster fic : this.field_5_fileIdClusters) {
            if (fic.getDrawingGroupId() == drawingGroupId && fic.getNumShapeIdsUsed() < 1024) {
                ficAdd = fic;
                break;
            }
            ++index;
        }
        if (ficAdd == null) {
            ficAdd = this.addCluster(drawingGroupId, 0, sort);
            this.maxDgId = Math.max(this.maxDgId, drawingGroupId);
        }
        final int shapeId = index * 1024 + ficAdd.getNumShapeIdsUsed();
        ficAdd.incrementUsedShapeId();
        dg.setNumShapes(dg.getNumShapes() + 1);
        dg.setLastMSOSPID(shapeId);
        this.field_1_shapeIdMax = Math.max(this.field_1_shapeIdMax, shapeId + 1);
        return shapeId;
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.DGG;
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "fileIdClusters", () -> this.field_5_fileIdClusters, "shapeIdMax", this::getShapeIdMax, "numIdClusters", this::getNumIdClusters, "numShapesSaved", this::getNumShapesSaved, "drawingsSaved", this::getDrawingsSaved);
    }
    
    @Override
    public EscherDggRecord copy() {
        return new EscherDggRecord(this);
    }
    
    static {
        RECORD_ID = EscherRecordTypes.DGG.typeID;
    }
    
    public static class FileIdCluster implements GenericRecord
    {
        private int field_1_drawingGroupId;
        private int field_2_numShapeIdsUsed;
        
        public FileIdCluster(final FileIdCluster other) {
            this.field_1_drawingGroupId = other.field_1_drawingGroupId;
            this.field_2_numShapeIdsUsed = other.field_2_numShapeIdsUsed;
        }
        
        public FileIdCluster(final int drawingGroupId, final int numShapeIdsUsed) {
            this.field_1_drawingGroupId = drawingGroupId;
            this.field_2_numShapeIdsUsed = numShapeIdsUsed;
        }
        
        public int getDrawingGroupId() {
            return this.field_1_drawingGroupId;
        }
        
        public int getNumShapeIdsUsed() {
            return this.field_2_numShapeIdsUsed;
        }
        
        private void incrementUsedShapeId() {
            ++this.field_2_numShapeIdsUsed;
        }
        
        private static int compareFileIdCluster(final FileIdCluster f1, final FileIdCluster f2) {
            final int dgDif = f1.getDrawingGroupId() - f2.getDrawingGroupId();
            final int cntDif = f2.getNumShapeIdsUsed() - f1.getNumShapeIdsUsed();
            return (dgDif != 0) ? dgDif : cntDif;
        }
        
        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("drawingGroupId", this::getDrawingGroupId, "numShapeIdUsed", this::getNumShapeIdsUsed);
        }
    }
}
