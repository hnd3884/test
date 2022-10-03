package org.apache.poi.xwpf.usermodel;

import java.util.Iterator;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import java.util.ArrayList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentRun;
import java.util.List;

public class XWPFSDTContent implements ISDTContent
{
    private List<ISDTContents> bodyElements;
    
    public XWPFSDTContent(final CTSdtContentRun sdtRun, final IBody part, final IRunBody parent) {
        this.bodyElements = new ArrayList<ISDTContents>();
        if (sdtRun == null) {
            return;
        }
        for (final CTR ctr : sdtRun.getRArray()) {
            final XWPFRun run = new XWPFRun(ctr, parent);
            this.bodyElements.add(run);
        }
    }
    
    public XWPFSDTContent(final CTSdtContentBlock block, final IBody part, final IRunBody parent) {
        this.bodyElements = new ArrayList<ISDTContents>();
        if (block == null) {
            return;
        }
        final XmlCursor cursor = block.newCursor();
        cursor.selectPath("./*");
        while (cursor.toNextSelection()) {
            final XmlObject o = cursor.getObject();
            if (o instanceof CTP) {
                final XWPFParagraph p = new XWPFParagraph((CTP)o, part);
                this.bodyElements.add(p);
            }
            else if (o instanceof CTTbl) {
                final XWPFTable t = new XWPFTable((CTTbl)o, part);
                this.bodyElements.add(t);
            }
            else if (o instanceof CTSdtBlock) {
                final XWPFSDT c = new XWPFSDT((CTSdtBlock)o, part);
                this.bodyElements.add(c);
            }
            else {
                if (!(o instanceof CTR)) {
                    continue;
                }
                final XWPFRun run = new XWPFRun((CTR)o, parent);
                this.bodyElements.add(run);
            }
        }
        cursor.dispose();
    }
    
    @Override
    public String getText() {
        final StringBuilder text = new StringBuilder();
        boolean addNewLine = false;
        for (int i = 0; i < this.bodyElements.size(); ++i) {
            final Object o = this.bodyElements.get(i);
            if (o instanceof XWPFParagraph) {
                this.appendParagraph((XWPFParagraph)o, text);
                addNewLine = true;
            }
            else if (o instanceof XWPFTable) {
                this.appendTable((XWPFTable)o, text);
                addNewLine = true;
            }
            else if (o instanceof XWPFSDT) {
                text.append(((XWPFSDT)o).getContent().getText());
                addNewLine = true;
            }
            else if (o instanceof XWPFRun) {
                text.append(o);
                addNewLine = false;
            }
            if (addNewLine && i < this.bodyElements.size() - 1) {
                text.append("\n");
            }
        }
        return text.toString();
    }
    
    private void appendTable(final XWPFTable table, final StringBuilder text) {
        for (final XWPFTableRow row : table.getRows()) {
            final List<ICell> cells = row.getTableICells();
            for (int i = 0; i < cells.size(); ++i) {
                final ICell cell = cells.get(i);
                if (cell instanceof XWPFTableCell) {
                    text.append(((XWPFTableCell)cell).getTextRecursively());
                }
                else if (cell instanceof XWPFSDTCell) {
                    text.append(((XWPFSDTCell)cell).getContent().getText());
                }
                if (i < cells.size() - 1) {
                    text.append("\t");
                }
            }
            text.append('\n');
        }
    }
    
    private void appendParagraph(final XWPFParagraph paragraph, final StringBuilder text) {
        for (final IRunElement run : paragraph.getRuns()) {
            text.append(run);
        }
    }
    
    @Override
    public String toString() {
        return this.getText();
    }
}
