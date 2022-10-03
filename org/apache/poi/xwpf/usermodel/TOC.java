package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabs;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabTlc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtEndPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtPr;
import org.apache.poi.util.LocaleUtil;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTheme;
import java.math.BigInteger;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;

public class TOC
{
    CTSdtBlock block;
    
    public TOC() {
        this(CTSdtBlock.Factory.newInstance());
    }
    
    public TOC(final CTSdtBlock block) {
        this.block = block;
        final CTSdtPr sdtPr = block.addNewSdtPr();
        final CTDecimalNumber id = sdtPr.addNewId();
        id.setVal(new BigInteger("4844945"));
        sdtPr.addNewDocPartObj().addNewDocPartGallery().setVal("Table of contents");
        final CTSdtEndPr sdtEndPr = block.addNewSdtEndPr();
        final CTRPr rPr = sdtEndPr.addNewRPr();
        final CTFonts fonts = rPr.addNewRFonts();
        fonts.setAsciiTheme(STTheme.MINOR_H_ANSI);
        fonts.setEastAsiaTheme(STTheme.MINOR_H_ANSI);
        fonts.setHAnsiTheme(STTheme.MINOR_H_ANSI);
        fonts.setCstheme(STTheme.MINOR_BIDI);
        rPr.addNewB().setVal(STOnOff.OFF);
        rPr.addNewBCs().setVal(STOnOff.OFF);
        rPr.addNewColor().setVal((Object)"auto");
        rPr.addNewSz().setVal(new BigInteger("24"));
        rPr.addNewSzCs().setVal(new BigInteger("24"));
        final CTSdtContentBlock content = block.addNewSdtContent();
        final CTP p = content.addNewP();
        p.setRsidR("00EF7E24".getBytes(LocaleUtil.CHARSET_1252));
        p.setRsidRDefault("00EF7E24".getBytes(LocaleUtil.CHARSET_1252));
        p.addNewPPr().addNewPStyle().setVal("TOCHeading");
        p.addNewR().addNewT().setStringValue("Table of Contents");
    }
    
    @Internal
    public CTSdtBlock getBlock() {
        return this.block;
    }
    
    public void addRow(final int level, final String title, final int page, final String bookmarkRef) {
        final CTSdtContentBlock contentBlock = this.block.getSdtContent();
        final CTP p = contentBlock.addNewP();
        p.setRsidR("00EF7E24".getBytes(LocaleUtil.CHARSET_1252));
        p.setRsidRDefault("00EF7E24".getBytes(LocaleUtil.CHARSET_1252));
        final CTPPr pPr = p.addNewPPr();
        pPr.addNewPStyle().setVal("TOC" + level);
        final CTTabs tabs = pPr.addNewTabs();
        final CTTabStop tab = tabs.addNewTab();
        tab.setVal(STTabJc.RIGHT);
        tab.setLeader(STTabTlc.DOT);
        tab.setPos(new BigInteger("8290"));
        pPr.addNewRPr().addNewNoProof();
        CTR run = p.addNewR();
        run.addNewRPr().addNewNoProof();
        run.addNewT().setStringValue(title);
        run = p.addNewR();
        run.addNewRPr().addNewNoProof();
        run.addNewTab();
        run = p.addNewR();
        run.addNewRPr().addNewNoProof();
        run.addNewFldChar().setFldCharType(STFldCharType.BEGIN);
        run = p.addNewR();
        run.addNewRPr().addNewNoProof();
        final CTText text = run.addNewInstrText();
        text.setSpace(SpaceAttribute.Space.PRESERVE);
        text.setStringValue(" PAGEREF _Toc" + bookmarkRef + " \\h ");
        p.addNewR().addNewRPr().addNewNoProof();
        run = p.addNewR();
        run.addNewRPr().addNewNoProof();
        run.addNewFldChar().setFldCharType(STFldCharType.SEPARATE);
        run = p.addNewR();
        run.addNewRPr().addNewNoProof();
        run.addNewT().setStringValue(Integer.toString(page));
        run = p.addNewR();
        run.addNewRPr().addNewNoProof();
        run.addNewFldChar().setFldCharType(STFldCharType.END);
    }
}
