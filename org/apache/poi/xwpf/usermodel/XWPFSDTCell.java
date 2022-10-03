package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtCell;

public class XWPFSDTCell extends XWPFAbstractSDT implements ICell
{
    private final XWPFSDTContentCell cellContent;
    
    public XWPFSDTCell(final CTSdtCell sdtCell, final XWPFTableRow xwpfTableRow, final IBody part) {
        super(sdtCell.getSdtPr(), part);
        this.cellContent = new XWPFSDTContentCell(sdtCell.getSdtContent(), xwpfTableRow, part);
    }
    
    @Override
    public ISDTContent getContent() {
        return this.cellContent;
    }
}
