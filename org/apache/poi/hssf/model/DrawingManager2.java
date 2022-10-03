package org.apache.poi.hssf.model;

import org.apache.poi.util.Removal;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.poi.ddf.EscherDgRecord;
import java.util.List;
import org.apache.poi.ddf.EscherDggRecord;

public class DrawingManager2
{
    private final EscherDggRecord dgg;
    private final List<EscherDgRecord> drawingGroups;
    
    public DrawingManager2(final EscherDggRecord dgg) {
        this.drawingGroups = new ArrayList<EscherDgRecord>();
        this.dgg = dgg;
    }
    
    public void clearDrawingGroups() {
        this.drawingGroups.clear();
    }
    
    public EscherDgRecord createDgRecord() {
        final EscherDgRecord dg = new EscherDgRecord();
        dg.setRecordId(EscherDgRecord.RECORD_ID);
        final short dgId = this.findNewDrawingGroupId();
        dg.setOptions((short)(dgId << 4));
        dg.setNumShapes(0);
        dg.setLastMSOSPID(-1);
        this.drawingGroups.add(dg);
        this.dgg.addCluster(dgId, 0);
        this.dgg.setDrawingsSaved(this.dgg.getDrawingsSaved() + 1);
        return dg;
    }
    
    @Deprecated
    @Removal(version = "4.0")
    public int allocateShapeId(final short drawingGroupId) {
        for (final EscherDgRecord dg : this.drawingGroups) {
            if (dg.getDrawingGroupId() == drawingGroupId) {
                return this.allocateShapeId(dg);
            }
        }
        throw new IllegalStateException("Drawing group id " + drawingGroupId + " doesn't exist.");
    }
    
    @Deprecated
    @Removal(version = "4.0")
    public int allocateShapeId(final short drawingGroupId, final EscherDgRecord dg) {
        return this.allocateShapeId(dg);
    }
    
    public int allocateShapeId(final EscherDgRecord dg) {
        return this.dgg.allocateShapeId(dg, true);
    }
    
    public short findNewDrawingGroupId() {
        return this.dgg.findNewDrawingGroupId();
    }
    
    public EscherDggRecord getDgg() {
        return this.dgg;
    }
    
    public void incrementDrawingsSaved() {
        this.dgg.setDrawingsSaved(this.dgg.getDrawingsSaved() + 1);
    }
}
