package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.EmbeddedObjectRefSubRecord;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.ddf.EscherProperty;
import java.util.Iterator;
import org.apache.poi.hssf.record.Record;
import java.util.Map;
import java.util.List;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.ddf.EscherContainerRecord;

public class HSSFShapeFactory
{
    public static void createShapeTree(final EscherContainerRecord container, final EscherAggregate agg, final HSSFShapeContainer out, final DirectoryNode root) {
        if (container.getRecordId() == EscherContainerRecord.SPGR_CONTAINER) {
            ObjRecord obj = null;
            final EscherClientDataRecord clientData = ((EscherContainerRecord)container.getChild(0)).getChildById(EscherClientDataRecord.RECORD_ID);
            if (null != clientData) {
                obj = agg.getShapeToObjMapping().get(clientData);
            }
            final HSSFShapeGroup group = new HSSFShapeGroup(container, obj);
            final List<EscherContainerRecord> children = container.getChildContainers();
            if (children.size() > 1) {
                children.subList(1, children.size()).forEach(c -> createShapeTree(c, agg, group, root));
            }
            out.addShape(group);
        }
        else if (container.getRecordId() == EscherContainerRecord.SP_CONTAINER) {
            final Map<EscherRecord, Record> shapeToObj = agg.getShapeToObjMapping();
            ObjRecord objRecord = null;
            TextObjectRecord txtRecord = null;
            for (final EscherRecord record : container) {
                switch (EscherRecordTypes.forTypeID(record.getRecordId())) {
                    case CLIENT_DATA: {
                        objRecord = shapeToObj.get(record);
                        continue;
                    }
                    case CLIENT_TEXTBOX: {
                        txtRecord = shapeToObj.get(record);
                        continue;
                    }
                }
            }
            if (objRecord == null) {
                throw new RecordFormatException("EscherClientDataRecord can't be found.");
            }
            if (isEmbeddedObject(objRecord)) {
                final HSSFObjectData objectData = new HSSFObjectData(container, objRecord, root);
                out.addShape(objectData);
                return;
            }
            final CommonObjectDataSubRecord cmo = objRecord.getSubRecords().get(0);
            HSSFShape shape = null;
            switch (cmo.getObjectType()) {
                case 8: {
                    shape = new HSSFPicture(container, objRecord);
                    break;
                }
                case 2: {
                    shape = new HSSFSimpleShape(container, objRecord, txtRecord);
                    break;
                }
                case 1: {
                    shape = new HSSFSimpleShape(container, objRecord);
                    break;
                }
                case 20: {
                    shape = new HSSFCombobox(container, objRecord);
                    break;
                }
                case 30: {
                    final EscherOptRecord optRecord = container.getChildById(EscherOptRecord.RECORD_ID);
                    if (optRecord == null) {
                        shape = new HSSFSimpleShape(container, objRecord, txtRecord);
                        break;
                    }
                    final EscherProperty property = optRecord.lookup(EscherPropertyTypes.GEOMETRY__VERTICES);
                    if (null != property) {
                        shape = new HSSFPolygon(container, objRecord, txtRecord);
                    }
                    else {
                        shape = new HSSFSimpleShape(container, objRecord, txtRecord);
                    }
                    break;
                }
                case 6: {
                    shape = new HSSFTextbox(container, objRecord, txtRecord);
                    break;
                }
                case 25: {
                    shape = new HSSFComment(container, objRecord, txtRecord, agg.getNoteRecordByObj(objRecord));
                    break;
                }
                default: {
                    shape = new HSSFSimpleShape(container, objRecord, txtRecord);
                    break;
                }
            }
            out.addShape(shape);
        }
    }
    
    private static boolean isEmbeddedObject(final ObjRecord obj) {
        for (final SubRecord sub : obj.getSubRecords()) {
            if (sub instanceof EmbeddedObjectRefSubRecord) {
                return true;
            }
        }
        return false;
    }
}
