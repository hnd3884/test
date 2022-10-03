package org.apache.poi.xwpf.usermodel;

import java.util.Iterator;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdnRef;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;

public class XWPFFootnote extends XWPFAbstractFootnoteEndnote
{
    @Internal
    public XWPFFootnote(final CTFtnEdn note, final XWPFAbstractFootnotesEndnotes xFootnotes) {
        super(note, xFootnotes);
    }
    
    @Internal
    public XWPFFootnote(final XWPFDocument document, final CTFtnEdn body) {
        super(document, body);
    }
    
    @Override
    public void ensureFootnoteRef(final XWPFParagraph p) {
        XWPFRun r = null;
        if (p.getRuns().size() > 0) {
            r = p.getRuns().get(0);
        }
        if (r == null) {
            r = p.createRun();
        }
        final CTR ctr = r.getCTR();
        boolean foundRef = false;
        for (final CTFtnEdnRef ref : ctr.getFootnoteReferenceList()) {
            if (this.getId().equals(ref.getId())) {
                foundRef = true;
                break;
            }
        }
        if (!foundRef) {
            ctr.addNewRPr().addNewRStyle().setVal("FootnoteReference");
            ctr.addNewFootnoteRef();
        }
    }
}
