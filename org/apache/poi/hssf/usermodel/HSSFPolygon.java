package org.apache.poi.hssf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.ddf.EscherArrayProperty;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRGBProperty;
import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherShapePathProperty;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.util.POILogger;

public class HSSFPolygon extends HSSFSimpleShape
{
    private static POILogger logger;
    public static final short OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING = 30;
    
    public HSSFPolygon(final EscherContainerRecord spContainer, final ObjRecord objRecord, final TextObjectRecord _textObjectRecord) {
        super(spContainer, objRecord, _textObjectRecord);
    }
    
    public HSSFPolygon(final EscherContainerRecord spContainer, final ObjRecord objRecord) {
        super(spContainer, objRecord);
    }
    
    HSSFPolygon(final HSSFShape parent, final HSSFAnchor anchor) {
        super(parent, anchor);
    }
    
    @Override
    protected TextObjectRecord createTextObjRecord() {
        return null;
    }
    
    @Override
    protected EscherContainerRecord createSpContainer() {
        final EscherContainerRecord spContainer = new EscherContainerRecord();
        final EscherSpRecord sp = new EscherSpRecord();
        final EscherOptRecord opt = new EscherOptRecord();
        final EscherClientDataRecord clientData = new EscherClientDataRecord();
        spContainer.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spContainer.setOptions((short)15);
        sp.setRecordId(EscherSpRecord.RECORD_ID);
        sp.setOptions((short)2);
        if (this.getParent() == null) {
            sp.setFlags(2560);
        }
        else {
            sp.setFlags(2562);
        }
        opt.setRecordId(EscherOptRecord.RECORD_ID);
        opt.setEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.TRANSFORM__ROTATION, false, false, 0));
        opt.setEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.GEOMETRY__RIGHT, false, false, 100));
        opt.setEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.GEOMETRY__BOTTOM, false, false, 100));
        opt.setEscherProperty(new EscherShapePathProperty(EscherPropertyTypes.GEOMETRY__SHAPEPATH, 4));
        opt.setEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.GEOMETRY__FILLOK, false, false, 65537));
        opt.setEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.LINESTYLE__LINESTARTARROWHEAD, false, false, 0));
        opt.setEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.LINESTYLE__LINEENDARROWHEAD, false, false, 0));
        opt.setEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.LINESTYLE__LINEENDCAPSTYLE, false, false, 0));
        opt.setEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.LINESTYLE__LINEDASHING, 0));
        opt.setEscherProperty(new EscherBoolProperty(EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 524296));
        opt.setEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.LINESTYLE__LINEWIDTH, 9525));
        opt.setEscherProperty(new EscherRGBProperty(EscherPropertyTypes.FILL__FILLCOLOR, 134217737));
        opt.setEscherProperty(new EscherRGBProperty(EscherPropertyTypes.LINESTYLE__COLOR, 134217792));
        opt.setEscherProperty(new EscherBoolProperty(EscherPropertyTypes.FILL__NOFILLHITTEST, 1));
        opt.setEscherProperty(new EscherBoolProperty(EscherPropertyTypes.GROUPSHAPE__FLAGS, 524288));
        final EscherRecord anchor = this.getAnchor().getEscherAnchor();
        clientData.setRecordId(EscherClientDataRecord.RECORD_ID);
        clientData.setOptions((short)0);
        spContainer.addChildRecord(sp);
        spContainer.addChildRecord(opt);
        spContainer.addChildRecord(anchor);
        spContainer.addChildRecord(clientData);
        return spContainer;
    }
    
    @Override
    protected ObjRecord createObjRecord() {
        final ObjRecord obj = new ObjRecord();
        final CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setObjectType((short)30);
        c.setLocked(true);
        c.setPrintable(true);
        c.setAutofill(true);
        c.setAutoline(true);
        final EndSubRecord e = new EndSubRecord();
        obj.addSubRecord(c);
        obj.addSubRecord(e);
        return obj;
    }
    
    @Override
    protected void afterRemove(final HSSFPatriarch patriarch) {
        patriarch.getBoundAggregate().removeShapeToObjRecord(this.getEscherContainer().getChildById(EscherClientDataRecord.RECORD_ID));
    }
    
    public int[] getXPoints() {
        final EscherArrayProperty verticesProp = this.getOptRecord().lookup(EscherPropertyTypes.GEOMETRY__VERTICES);
        if (null == verticesProp) {
            return new int[0];
        }
        final int[] array = new int[verticesProp.getNumberOfElementsInArray() - 1];
        for (int i = 0; i < verticesProp.getNumberOfElementsInArray() - 1; ++i) {
            final byte[] property = verticesProp.getElement(i);
            final short x = LittleEndian.getShort(property, 0);
            array[i] = x;
        }
        return array;
    }
    
    public int[] getYPoints() {
        final EscherArrayProperty verticesProp = this.getOptRecord().lookup(EscherPropertyTypes.GEOMETRY__VERTICES);
        if (null == verticesProp) {
            return new int[0];
        }
        final int[] array = new int[verticesProp.getNumberOfElementsInArray() - 1];
        for (int i = 0; i < verticesProp.getNumberOfElementsInArray() - 1; ++i) {
            final byte[] property = verticesProp.getElement(i);
            final short x = LittleEndian.getShort(property, 2);
            array[i] = x;
        }
        return array;
    }
    
    public void setPoints(final int[] xPoints, final int[] yPoints) {
        if (xPoints.length != yPoints.length) {
            HSSFPolygon.logger.log(7, "xPoint.length must be equal to yPoints.length");
            return;
        }
        if (xPoints.length == 0) {
            HSSFPolygon.logger.log(7, "HSSFPolygon must have at least one point");
        }
        final EscherArrayProperty verticesProp = new EscherArrayProperty(EscherPropertyTypes.GEOMETRY__VERTICES, false, 0);
        verticesProp.setNumberOfElementsInArray(xPoints.length + 1);
        verticesProp.setNumberOfElementsInMemory(xPoints.length + 1);
        verticesProp.setSizeOfElements(65520);
        for (int i = 0; i < xPoints.length; ++i) {
            final byte[] data = new byte[4];
            LittleEndian.putShort(data, 0, (short)xPoints[i]);
            LittleEndian.putShort(data, 2, (short)yPoints[i]);
            verticesProp.setElement(i, data);
        }
        final int point = xPoints.length;
        final byte[] data = new byte[4];
        LittleEndian.putShort(data, 0, (short)xPoints[0]);
        LittleEndian.putShort(data, 2, (short)yPoints[0]);
        verticesProp.setElement(point, data);
        this.setPropertyValue(verticesProp);
        final EscherArrayProperty segmentsProp = new EscherArrayProperty(EscherPropertyTypes.GEOMETRY__SEGMENTINFO, false, 0);
        segmentsProp.setSizeOfElements(2);
        segmentsProp.setNumberOfElementsInArray(xPoints.length * 2 + 4);
        segmentsProp.setNumberOfElementsInMemory(xPoints.length * 2 + 4);
        segmentsProp.setElement(0, new byte[] { 0, 64 });
        segmentsProp.setElement(1, new byte[] { 0, -84 });
        for (int j = 0; j < xPoints.length; ++j) {
            segmentsProp.setElement(2 + j * 2, new byte[] { 1, 0 });
            segmentsProp.setElement(3 + j * 2, new byte[] { 0, -84 });
        }
        segmentsProp.setElement(segmentsProp.getNumberOfElementsInArray() - 2, new byte[] { 1, 96 });
        segmentsProp.setElement(segmentsProp.getNumberOfElementsInArray() - 1, new byte[] { 0, -128 });
        this.setPropertyValue(segmentsProp);
    }
    
    public void setPolygonDrawArea(final int width, final int height) {
        this.setPropertyValue(new EscherSimpleProperty(EscherPropertyTypes.GEOMETRY__RIGHT, width));
        this.setPropertyValue(new EscherSimpleProperty(EscherPropertyTypes.GEOMETRY__BOTTOM, height));
    }
    
    public int getDrawAreaWidth() {
        final EscherSimpleProperty property = this.getOptRecord().lookup(EscherPropertyTypes.GEOMETRY__RIGHT);
        return (property == null) ? 100 : property.getPropertyValue();
    }
    
    public int getDrawAreaHeight() {
        final EscherSimpleProperty property = this.getOptRecord().lookup(EscherPropertyTypes.GEOMETRY__BOTTOM);
        return (property == null) ? 100 : property.getPropertyValue();
    }
    
    static {
        HSSFPolygon.logger = POILogFactory.getLogger(HSSFPolygon.class);
    }
}
