package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.LbsDataSubRecord;
import org.apache.poi.hssf.record.FtCblsSubRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ddf.EscherContainerRecord;

public class HSSFCombobox extends HSSFSimpleShape
{
    public HSSFCombobox(final EscherContainerRecord spContainer, final ObjRecord objRecord) {
        super(spContainer, objRecord);
    }
    
    public HSSFCombobox(final HSSFShape parent, final HSSFAnchor anchor) {
        super(parent, anchor);
        super.setShapeType(201);
        final CommonObjectDataSubRecord cod = this.getObjRecord().getSubRecords().get(0);
        cod.setObjectType((short)20);
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
        sp.setOptions((short)3218);
        sp.setFlags(2560);
        opt.setRecordId(EscherOptRecord.RECORD_ID);
        opt.addEscherProperty(new EscherBoolProperty(EscherPropertyTypes.PROTECTION__LOCKAGAINSTGROUPING, 17039620));
        opt.addEscherProperty(new EscherBoolProperty(EscherPropertyTypes.TEXT__SIZE_TEXT_TO_FIT_SHAPE, 524296));
        opt.addEscherProperty(new EscherBoolProperty(EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 524288));
        opt.addEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.GROUPSHAPE__FLAGS, 131072));
        final HSSFClientAnchor userAnchor = (HSSFClientAnchor)this.getAnchor();
        userAnchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_DO_RESIZE);
        final EscherRecord anchor = userAnchor.getEscherAnchor();
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
        c.setObjectType((short)201);
        c.setLocked(true);
        c.setPrintable(false);
        c.setAutofill(true);
        c.setAutoline(false);
        final FtCblsSubRecord f = new FtCblsSubRecord();
        final LbsDataSubRecord l = LbsDataSubRecord.newAutoFilterInstance();
        final EndSubRecord e = new EndSubRecord();
        obj.addSubRecord(c);
        obj.addSubRecord(f);
        obj.addSubRecord(l);
        obj.addSubRecord(e);
        return obj;
    }
    
    @Override
    public void setShapeType(final int shapeType) {
        throw new IllegalStateException("Shape type can not be changed in " + this.getClass().getSimpleName());
    }
}
