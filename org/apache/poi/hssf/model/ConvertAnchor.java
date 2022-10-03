package org.apache.poi.hssf.model;

import org.apache.poi.ddf.EscherChildAnchorRecord;
import org.apache.poi.hssf.usermodel.HSSFChildAnchor;
import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hssf.usermodel.HSSFAnchor;

public class ConvertAnchor
{
    public static EscherRecord createAnchor(final HSSFAnchor userAnchor) {
        if (userAnchor instanceof HSSFClientAnchor) {
            final HSSFClientAnchor a = (HSSFClientAnchor)userAnchor;
            final EscherClientAnchorRecord anchor = new EscherClientAnchorRecord();
            anchor.setRecordId(EscherClientAnchorRecord.RECORD_ID);
            anchor.setOptions((short)0);
            anchor.setFlag(a.getAnchorType().value);
            anchor.setCol1((short)Math.min(a.getCol1(), a.getCol2()));
            anchor.setDx1((short)a.getDx1());
            anchor.setRow1((short)Math.min(a.getRow1(), a.getRow2()));
            anchor.setDy1((short)a.getDy1());
            anchor.setCol2((short)Math.max(a.getCol1(), a.getCol2()));
            anchor.setDx2((short)a.getDx2());
            anchor.setRow2((short)Math.max(a.getRow1(), a.getRow2()));
            anchor.setDy2((short)a.getDy2());
            return anchor;
        }
        final HSSFChildAnchor a2 = (HSSFChildAnchor)userAnchor;
        final EscherChildAnchorRecord anchor2 = new EscherChildAnchorRecord();
        anchor2.setRecordId(EscherChildAnchorRecord.RECORD_ID);
        anchor2.setOptions((short)0);
        anchor2.setDx1((short)Math.min(a2.getDx1(), a2.getDx2()));
        anchor2.setDy1((short)Math.min(a2.getDy1(), a2.getDy2()));
        anchor2.setDx2((short)Math.max(a2.getDx2(), a2.getDx1()));
        anchor2.setDy2((short)Math.max(a2.getDy2(), a2.getDy1()));
        return anchor2;
    }
}
