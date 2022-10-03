package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;

public class XWPFSDT extends XWPFAbstractSDT implements IBodyElement, IRunBody, ISDTContents, IRunElement
{
    private final ISDTContent content;
    
    public XWPFSDT(final CTSdtRun sdtRun, final IBody part) {
        super(sdtRun.getSdtPr(), part);
        this.content = new XWPFSDTContent(sdtRun.getSdtContent(), part, this);
    }
    
    public XWPFSDT(final CTSdtBlock block, final IBody part) {
        super(block.getSdtPr(), part);
        this.content = new XWPFSDTContent(block.getSdtContent(), part, this);
    }
    
    @Override
    public ISDTContent getContent() {
        return this.content;
    }
}
