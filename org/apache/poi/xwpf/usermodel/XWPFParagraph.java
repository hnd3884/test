package org.apache.poi.xwpf.usermodel;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTProofErr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextAlignment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTextAlignment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import java.math.BigInteger;
import java.util.Collections;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRunTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import java.util.Iterator;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdnRef;
import org.apache.xmlbeans.XmlObject;
import java.util.ArrayList;
import java.util.List;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.apache.poi.wp.usermodel.Paragraph;

public class XWPFParagraph implements IBodyElement, IRunBody, ISDTContents, Paragraph
{
    private final CTP paragraph;
    protected IBody part;
    protected XWPFDocument document;
    protected List<XWPFRun> runs;
    protected List<IRunElement> iruns;
    private StringBuilder footnoteText;
    
    public XWPFParagraph(final CTP prgrph, final IBody part) {
        this.footnoteText = new StringBuilder(64);
        this.paragraph = prgrph;
        this.part = part;
        this.document = part.getXWPFDocument();
        if (this.document == null) {
            throw new NullPointerException();
        }
        this.runs = new ArrayList<XWPFRun>();
        this.iruns = new ArrayList<IRunElement>();
        this.buildRunsInOrderFromXml((XmlObject)this.paragraph);
        for (final XWPFRun run : this.runs) {
            final CTR r = run.getCTR();
            final XmlCursor c = r.newCursor();
            c.selectPath("child::*");
            while (c.toNextSelection()) {
                final XmlObject o = c.getObject();
                if (o instanceof CTFtnEdnRef) {
                    final CTFtnEdnRef ftn = (CTFtnEdnRef)o;
                    this.footnoteText.append(" [").append(ftn.getId()).append(": ");
                    final XWPFAbstractFootnoteEndnote footnote = ftn.getDomNode().getLocalName().equals("footnoteReference") ? this.document.getFootnoteByID(ftn.getId().intValue()) : this.document.getEndnoteByID(ftn.getId().intValue());
                    if (null != footnote) {
                        boolean first = true;
                        for (final XWPFParagraph p : footnote.getParagraphs()) {
                            if (!first) {
                                this.footnoteText.append("\n");
                            }
                            first = false;
                            this.footnoteText.append(p.getText());
                        }
                    }
                    else {
                        this.footnoteText.append("!!! End note with ID \"").append(ftn.getId()).append("\" not found in document.");
                    }
                    this.footnoteText.append("] ");
                }
            }
            c.dispose();
        }
    }
    
    private void buildRunsInOrderFromXml(final XmlObject object) {
        final XmlCursor c = object.newCursor();
        c.selectPath("child::*");
        while (c.toNextSelection()) {
            final XmlObject o = c.getObject();
            if (o instanceof CTR) {
                final XWPFRun r = new XWPFRun((CTR)o, this);
                this.runs.add(r);
                this.iruns.add(r);
            }
            if (o instanceof CTHyperlink) {
                final CTHyperlink link = (CTHyperlink)o;
                for (final CTR r2 : link.getRArray()) {
                    final XWPFHyperlinkRun hr = new XWPFHyperlinkRun(link, r2, this);
                    this.runs.add(hr);
                    this.iruns.add(hr);
                }
            }
            if (o instanceof CTSimpleField) {
                final CTSimpleField field = (CTSimpleField)o;
                for (final CTR r2 : field.getRArray()) {
                    final XWPFFieldRun fr = new XWPFFieldRun(field, r2, this);
                    this.runs.add(fr);
                    this.iruns.add(fr);
                }
            }
            if (o instanceof CTSdtBlock) {
                final XWPFSDT cc = new XWPFSDT((CTSdtBlock)o, this.part);
                this.iruns.add(cc);
            }
            if (o instanceof CTSdtRun) {
                final XWPFSDT cc = new XWPFSDT((CTSdtRun)o, this.part);
                this.iruns.add(cc);
            }
            if (o instanceof CTRunTrackChange) {
                for (final CTR r3 : ((CTRunTrackChange)o).getRArray()) {
                    final XWPFRun cr = new XWPFRun(r3, this);
                    this.runs.add(cr);
                    this.iruns.add(cr);
                }
            }
            if (o instanceof CTSmartTagRun) {
                this.buildRunsInOrderFromXml(o);
            }
            if (o instanceof CTRunTrackChange) {
                for (final CTRunTrackChange change : ((CTRunTrackChange)o).getInsArray()) {
                    this.buildRunsInOrderFromXml((XmlObject)change);
                }
            }
        }
        c.dispose();
    }
    
    @Internal
    public CTP getCTP() {
        return this.paragraph;
    }
    
    public List<XWPFRun> getRuns() {
        return Collections.unmodifiableList((List<? extends XWPFRun>)this.runs);
    }
    
    public List<IRunElement> getIRuns() {
        return Collections.unmodifiableList((List<? extends IRunElement>)this.iruns);
    }
    
    public boolean isEmpty() {
        return !this.paragraph.getDomNode().hasChildNodes();
    }
    
    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }
    
    public String getText() {
        final StringBuilder out = new StringBuilder(64);
        for (final IRunElement run : this.iruns) {
            if (run instanceof XWPFRun) {
                final XWPFRun xRun = (XWPFRun)run;
                if (xRun.getCTR().getDelTextArray().length != 0) {
                    continue;
                }
                out.append(xRun);
            }
            else if (run instanceof XWPFSDT) {
                out.append(((XWPFSDT)run).getContent().getText());
            }
            else {
                out.append(run);
            }
        }
        out.append((CharSequence)this.footnoteText);
        return out.toString();
    }
    
    public String getStyleID() {
        if (this.paragraph.getPPr() != null && this.paragraph.getPPr().getPStyle() != null && this.paragraph.getPPr().getPStyle().getVal() != null) {
            return this.paragraph.getPPr().getPStyle().getVal();
        }
        return null;
    }
    
    public BigInteger getNumID() {
        if (this.paragraph.getPPr() != null && this.paragraph.getPPr().getNumPr() != null && this.paragraph.getPPr().getNumPr().getNumId() != null) {
            return this.paragraph.getPPr().getNumPr().getNumId().getVal();
        }
        return null;
    }
    
    public void setNumID(final BigInteger numPos) {
        if (this.paragraph.getPPr() == null) {
            this.paragraph.addNewPPr();
        }
        if (this.paragraph.getPPr().getNumPr() == null) {
            this.paragraph.getPPr().addNewNumPr();
        }
        if (this.paragraph.getPPr().getNumPr().getNumId() == null) {
            this.paragraph.getPPr().getNumPr().addNewNumId();
        }
        this.paragraph.getPPr().getNumPr().getNumId().setVal(numPos);
    }
    
    public void setNumILvl(final BigInteger iLvl) {
        if (this.paragraph.getPPr() == null) {
            this.paragraph.addNewPPr();
        }
        if (this.paragraph.getPPr().getNumPr() == null) {
            this.paragraph.getPPr().addNewNumPr();
        }
        if (this.paragraph.getPPr().getNumPr().getIlvl() == null) {
            this.paragraph.getPPr().getNumPr().addNewIlvl();
        }
        this.paragraph.getPPr().getNumPr().getIlvl().setVal(iLvl);
    }
    
    public BigInteger getNumIlvl() {
        if (this.paragraph.getPPr() != null && this.paragraph.getPPr().getNumPr() != null && this.paragraph.getPPr().getNumPr().getIlvl() != null) {
            return this.paragraph.getPPr().getNumPr().getIlvl().getVal();
        }
        return null;
    }
    
    public String getNumFmt() {
        final BigInteger numID = this.getNumID();
        final XWPFNumbering numbering = this.document.getNumbering();
        if (numID != null && numbering != null) {
            final XWPFNum num = numbering.getNum(numID);
            if (num != null) {
                final BigInteger ilvl = this.getNumIlvl();
                final BigInteger abstractNumId = num.getCTNum().getAbstractNumId().getVal();
                final CTAbstractNum anum = numbering.getAbstractNum(abstractNumId).getAbstractNum();
                CTLvl level = null;
                for (int i = 0; i < anum.sizeOfLvlArray(); ++i) {
                    final CTLvl lvl = anum.getLvlArray(i);
                    if (lvl.getIlvl().equals(ilvl)) {
                        level = lvl;
                        break;
                    }
                }
                if (level != null && level.getNumFmt() != null && level.getNumFmt().getVal() != null) {
                    return level.getNumFmt().getVal().toString();
                }
            }
        }
        return null;
    }
    
    public String getNumLevelText() {
        final BigInteger numID = this.getNumID();
        final XWPFNumbering numbering = this.document.getNumbering();
        if (numID != null && numbering != null) {
            final XWPFNum num = numbering.getNum(numID);
            if (num != null) {
                final BigInteger ilvl = this.getNumIlvl();
                final CTNum ctNum = num.getCTNum();
                if (ctNum == null) {
                    return null;
                }
                final CTDecimalNumber ctDecimalNumber = ctNum.getAbstractNumId();
                if (ctDecimalNumber == null) {
                    return null;
                }
                final BigInteger abstractNumId = ctDecimalNumber.getVal();
                if (abstractNumId == null) {
                    return null;
                }
                final XWPFAbstractNum xwpfAbstractNum = numbering.getAbstractNum(abstractNumId);
                if (xwpfAbstractNum == null) {
                    return null;
                }
                final CTAbstractNum anum = xwpfAbstractNum.getCTAbstractNum();
                if (anum == null) {
                    return null;
                }
                CTLvl level = null;
                for (int i = 0; i < anum.sizeOfLvlArray(); ++i) {
                    final CTLvl lvl = anum.getLvlArray(i);
                    if (lvl != null && lvl.getIlvl() != null && lvl.getIlvl().equals(ilvl)) {
                        level = lvl;
                        break;
                    }
                }
                if (level != null && level.getLvlText() != null && level.getLvlText().getVal() != null) {
                    return level.getLvlText().getVal();
                }
            }
        }
        return null;
    }
    
    public BigInteger getNumStartOverride() {
        final BigInteger numID = this.getNumID();
        final XWPFNumbering numbering = this.document.getNumbering();
        if (numID != null && numbering != null) {
            final XWPFNum num = numbering.getNum(numID);
            if (num != null) {
                final CTNum ctNum = num.getCTNum();
                if (ctNum == null) {
                    return null;
                }
                final BigInteger ilvl = this.getNumIlvl();
                CTNumLvl level = null;
                for (int i = 0; i < ctNum.sizeOfLvlOverrideArray(); ++i) {
                    final CTNumLvl ctNumLvl = ctNum.getLvlOverrideArray(i);
                    if (ctNumLvl != null && ctNumLvl.getIlvl() != null && ctNumLvl.getIlvl().equals(ilvl)) {
                        level = ctNumLvl;
                        break;
                    }
                }
                if (level != null && level.getStartOverride() != null) {
                    return level.getStartOverride().getVal();
                }
            }
        }
        return null;
    }
    
    public boolean isKeepNext() {
        return this.getCTP() != null && this.getCTP().getPPr() != null && this.getCTP().getPPr().isSetKeepNext() && this.getCTP().getPPr().getKeepNext().getVal() == STOnOff.ON;
    }
    
    public void setKeepNext(final boolean keepNext) {
        final CTOnOff state = CTOnOff.Factory.newInstance();
        state.setVal(keepNext ? STOnOff.ON : STOnOff.OFF);
        this.getCTP().getPPr().setKeepNext(state);
    }
    
    public String getParagraphText() {
        final StringBuilder out = new StringBuilder(64);
        for (final XWPFRun run : this.runs) {
            out.append(run);
        }
        return out.toString();
    }
    
    public String getPictureText() {
        final StringBuilder out = new StringBuilder(64);
        for (final XWPFRun run : this.runs) {
            out.append(run.getPictureText());
        }
        return out.toString();
    }
    
    public String getFootnoteText() {
        return this.footnoteText.toString();
    }
    
    public ParagraphAlignment getAlignment() {
        final CTPPr pr = this.getCTPPr();
        return (pr == null || !pr.isSetJc()) ? ParagraphAlignment.LEFT : ParagraphAlignment.valueOf(pr.getJc().getVal().intValue());
    }
    
    public void setAlignment(final ParagraphAlignment align) {
        final CTPPr pr = this.getCTPPr();
        final CTJc jc = pr.isSetJc() ? pr.getJc() : pr.addNewJc();
        final STJc.Enum en = STJc.Enum.forInt(align.getValue());
        jc.setVal(en);
    }
    
    public int getFontAlignment() {
        return this.getAlignment().getValue();
    }
    
    public void setFontAlignment(final int align) {
        final ParagraphAlignment pAlign = ParagraphAlignment.valueOf(align);
        this.setAlignment(pAlign);
    }
    
    public TextAlignment getVerticalAlignment() {
        final CTPPr pr = this.getCTPPr();
        return (pr == null || !pr.isSetTextAlignment()) ? TextAlignment.AUTO : TextAlignment.valueOf(pr.getTextAlignment().getVal().intValue());
    }
    
    public void setVerticalAlignment(final TextAlignment valign) {
        final CTPPr pr = this.getCTPPr();
        final CTTextAlignment textAlignment = pr.isSetTextAlignment() ? pr.getTextAlignment() : pr.addNewTextAlignment();
        final STTextAlignment.Enum en = STTextAlignment.Enum.forInt(valign.getValue());
        textAlignment.setVal(en);
    }
    
    public Borders getBorderTop() {
        final CTPBdr border = this.getCTPBrd(false);
        CTBorder ct = null;
        if (border != null) {
            ct = border.getTop();
        }
        final STBorder.Enum ptrn = (ct != null) ? ct.getVal() : STBorder.NONE;
        return Borders.valueOf(ptrn.intValue());
    }
    
    public void setBorderTop(final Borders border) {
        final CTPBdr ct = this.getCTPBrd(true);
        if (ct == null) {
            throw new RuntimeException("invalid paragraph state");
        }
        final CTBorder pr = ct.isSetTop() ? ct.getTop() : ct.addNewTop();
        if (border.getValue() == Borders.NONE.getValue()) {
            ct.unsetTop();
        }
        else {
            pr.setVal(STBorder.Enum.forInt(border.getValue()));
        }
    }
    
    public Borders getBorderBottom() {
        final CTPBdr border = this.getCTPBrd(false);
        CTBorder ct = null;
        if (border != null) {
            ct = border.getBottom();
        }
        final STBorder.Enum ptrn = (ct != null) ? ct.getVal() : STBorder.NONE;
        return Borders.valueOf(ptrn.intValue());
    }
    
    public void setBorderBottom(final Borders border) {
        final CTPBdr ct = this.getCTPBrd(true);
        final CTBorder pr = ct.isSetBottom() ? ct.getBottom() : ct.addNewBottom();
        if (border.getValue() == Borders.NONE.getValue()) {
            ct.unsetBottom();
        }
        else {
            pr.setVal(STBorder.Enum.forInt(border.getValue()));
        }
    }
    
    public Borders getBorderLeft() {
        final CTPBdr border = this.getCTPBrd(false);
        CTBorder ct = null;
        if (border != null) {
            ct = border.getLeft();
        }
        final STBorder.Enum ptrn = (ct != null) ? ct.getVal() : STBorder.NONE;
        return Borders.valueOf(ptrn.intValue());
    }
    
    public void setBorderLeft(final Borders border) {
        final CTPBdr ct = this.getCTPBrd(true);
        final CTBorder pr = ct.isSetLeft() ? ct.getLeft() : ct.addNewLeft();
        if (border.getValue() == Borders.NONE.getValue()) {
            ct.unsetLeft();
        }
        else {
            pr.setVal(STBorder.Enum.forInt(border.getValue()));
        }
    }
    
    public Borders getBorderRight() {
        final CTPBdr border = this.getCTPBrd(false);
        CTBorder ct = null;
        if (border != null) {
            ct = border.getRight();
        }
        final STBorder.Enum ptrn = (ct != null) ? ct.getVal() : STBorder.NONE;
        return Borders.valueOf(ptrn.intValue());
    }
    
    public void setBorderRight(final Borders border) {
        final CTPBdr ct = this.getCTPBrd(true);
        final CTBorder pr = ct.isSetRight() ? ct.getRight() : ct.addNewRight();
        if (border.getValue() == Borders.NONE.getValue()) {
            ct.unsetRight();
        }
        else {
            pr.setVal(STBorder.Enum.forInt(border.getValue()));
        }
    }
    
    public Borders getBorderBetween() {
        final CTPBdr border = this.getCTPBrd(false);
        CTBorder ct = null;
        if (border != null) {
            ct = border.getBetween();
        }
        final STBorder.Enum ptrn = (ct != null) ? ct.getVal() : STBorder.NONE;
        return Borders.valueOf(ptrn.intValue());
    }
    
    public void setBorderBetween(final Borders border) {
        final CTPBdr ct = this.getCTPBrd(true);
        final CTBorder pr = ct.isSetBetween() ? ct.getBetween() : ct.addNewBetween();
        if (border.getValue() == Borders.NONE.getValue()) {
            ct.unsetBetween();
        }
        else {
            pr.setVal(STBorder.Enum.forInt(border.getValue()));
        }
    }
    
    public boolean isPageBreak() {
        final CTPPr ppr = this.getCTPPr();
        final CTOnOff ctPageBreak = ppr.isSetPageBreakBefore() ? ppr.getPageBreakBefore() : null;
        return ctPageBreak != null && isTruelike(ctPageBreak.getVal());
    }
    
    private static boolean isTruelike(final STOnOff.Enum value) {
        if (value == null) {
            return false;
        }
        switch (value.intValue()) {
            case 1:
            case 3:
            case 6: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public void setPageBreak(final boolean pageBreak) {
        final CTPPr ppr = this.getCTPPr();
        final CTOnOff ctPageBreak = ppr.isSetPageBreakBefore() ? ppr.getPageBreakBefore() : ppr.addNewPageBreakBefore();
        if (pageBreak) {
            ctPageBreak.setVal(STOnOff.TRUE);
        }
        else {
            ctPageBreak.setVal(STOnOff.FALSE);
        }
    }
    
    public int getSpacingAfter() {
        final CTSpacing spacing = this.getCTSpacing(false);
        return (spacing != null && spacing.isSetAfter()) ? spacing.getAfter().intValue() : -1;
    }
    
    public void setSpacingAfter(final int spaces) {
        final CTSpacing spacing = this.getCTSpacing(true);
        if (spacing != null) {
            final BigInteger bi = new BigInteger(Integer.toString(spaces));
            spacing.setAfter(bi);
        }
    }
    
    public int getSpacingAfterLines() {
        final CTSpacing spacing = this.getCTSpacing(false);
        return (spacing != null && spacing.isSetAfterLines()) ? spacing.getAfterLines().intValue() : -1;
    }
    
    public void setSpacingAfterLines(final int spaces) {
        final CTSpacing spacing = this.getCTSpacing(true);
        final BigInteger bi = new BigInteger(Integer.toString(spaces));
        spacing.setAfterLines(bi);
    }
    
    public int getSpacingBefore() {
        final CTSpacing spacing = this.getCTSpacing(false);
        return (spacing != null && spacing.isSetBefore()) ? spacing.getBefore().intValue() : -1;
    }
    
    public void setSpacingBefore(final int spaces) {
        final CTSpacing spacing = this.getCTSpacing(true);
        final BigInteger bi = new BigInteger(Integer.toString(spaces));
        spacing.setBefore(bi);
    }
    
    public int getSpacingBeforeLines() {
        final CTSpacing spacing = this.getCTSpacing(false);
        return (spacing != null && spacing.isSetBeforeLines()) ? spacing.getBeforeLines().intValue() : -1;
    }
    
    public void setSpacingBeforeLines(final int spaces) {
        final CTSpacing spacing = this.getCTSpacing(true);
        final BigInteger bi = new BigInteger(Integer.toString(spaces));
        spacing.setBeforeLines(bi);
    }
    
    public LineSpacingRule getSpacingLineRule() {
        final CTSpacing spacing = this.getCTSpacing(false);
        return (spacing != null && spacing.isSetLineRule()) ? LineSpacingRule.valueOf(spacing.getLineRule().intValue()) : LineSpacingRule.AUTO;
    }
    
    public void setSpacingLineRule(final LineSpacingRule rule) {
        final CTSpacing spacing = this.getCTSpacing(true);
        spacing.setLineRule(STLineSpacingRule.Enum.forInt(rule.getValue()));
    }
    
    public double getSpacingBetween() {
        final CTSpacing spacing = this.getCTSpacing(false);
        if (spacing == null || !spacing.isSetLine()) {
            return -1.0;
        }
        if (spacing.getLineRule() == null || spacing.getLineRule() == STLineSpacingRule.AUTO) {
            final BigInteger[] val = spacing.getLine().divideAndRemainder(BigInteger.valueOf(240L));
            return val[0].doubleValue() + val[1].doubleValue() / 240.0;
        }
        final BigInteger[] val = spacing.getLine().divideAndRemainder(BigInteger.valueOf(20L));
        return val[0].doubleValue() + val[1].doubleValue() / 20.0;
    }
    
    public void setSpacingBetween(final double spacing, final LineSpacingRule rule) {
        final CTSpacing ctSp = this.getCTSpacing(true);
        if (rule == LineSpacingRule.AUTO) {
            ctSp.setLine(new BigInteger(String.valueOf(Math.round(spacing * 240.0))));
        }
        else {
            ctSp.setLine(new BigInteger(String.valueOf(Math.round(spacing * 20.0))));
        }
        ctSp.setLineRule(STLineSpacingRule.Enum.forInt(rule.getValue()));
    }
    
    public void setSpacingBetween(final double spacing) {
        this.setSpacingBetween(spacing, LineSpacingRule.AUTO);
    }
    
    public int getIndentationLeft() {
        final CTInd indentation = this.getCTInd(false);
        return (indentation != null && indentation.isSetLeft()) ? indentation.getLeft().intValue() : -1;
    }
    
    public void setIndentationLeft(final int indentation) {
        final CTInd indent = this.getCTInd(true);
        final BigInteger bi = new BigInteger(Integer.toString(indentation));
        indent.setLeft(bi);
    }
    
    public int getIndentationRight() {
        final CTInd indentation = this.getCTInd(false);
        return (indentation != null && indentation.isSetRight()) ? indentation.getRight().intValue() : -1;
    }
    
    public void setIndentationRight(final int indentation) {
        final CTInd indent = this.getCTInd(true);
        final BigInteger bi = new BigInteger(Integer.toString(indentation));
        indent.setRight(bi);
    }
    
    public int getIndentationHanging() {
        final CTInd indentation = this.getCTInd(false);
        return (indentation != null && indentation.isSetHanging()) ? indentation.getHanging().intValue() : -1;
    }
    
    public void setIndentationHanging(final int indentation) {
        final CTInd indent = this.getCTInd(true);
        final BigInteger bi = new BigInteger(Integer.toString(indentation));
        indent.setHanging(bi);
    }
    
    public int getIndentationFirstLine() {
        final CTInd indentation = this.getCTInd(false);
        return (indentation != null && indentation.isSetFirstLine()) ? indentation.getFirstLine().intValue() : -1;
    }
    
    public void setIndentationFirstLine(final int indentation) {
        final CTInd indent = this.getCTInd(true);
        final BigInteger bi = new BigInteger(Integer.toString(indentation));
        indent.setFirstLine(bi);
    }
    
    public int getIndentFromLeft() {
        return this.getIndentationLeft();
    }
    
    public void setIndentFromLeft(final int dxaLeft) {
        this.setIndentationLeft(dxaLeft);
    }
    
    public int getIndentFromRight() {
        return this.getIndentationRight();
    }
    
    public void setIndentFromRight(final int dxaRight) {
        this.setIndentationRight(dxaRight);
    }
    
    public int getFirstLineIndent() {
        return this.getIndentationFirstLine();
    }
    
    public void setFirstLineIndent(final int first) {
        this.setIndentationFirstLine(first);
    }
    
    public boolean isWordWrapped() {
        final CTOnOff wordWrap = this.getCTPPr().isSetWordWrap() ? this.getCTPPr().getWordWrap() : null;
        return wordWrap != null && isTruelike(wordWrap.getVal());
    }
    
    public void setWordWrapped(final boolean wrap) {
        final CTOnOff wordWrap = this.getCTPPr().isSetWordWrap() ? this.getCTPPr().getWordWrap() : this.getCTPPr().addNewWordWrap();
        if (wrap) {
            wordWrap.setVal(STOnOff.TRUE);
        }
        else {
            wordWrap.unsetVal();
        }
    }
    
    public boolean isWordWrap() {
        return this.isWordWrapped();
    }
    
    @Deprecated
    public void setWordWrap(final boolean wrap) {
        this.setWordWrapped(wrap);
    }
    
    public String getStyle() {
        final CTPPr pr = this.getCTPPr();
        final CTString style = pr.isSetPStyle() ? pr.getPStyle() : null;
        return (style != null) ? style.getVal() : null;
    }
    
    public void setStyle(final String styleId) {
        final CTPPr pr = this.getCTPPr();
        final CTString style = (pr.getPStyle() != null) ? pr.getPStyle() : pr.addNewPStyle();
        style.setVal(styleId);
    }
    
    private CTPBdr getCTPBrd(final boolean create) {
        final CTPPr pr = this.getCTPPr();
        CTPBdr ct = pr.isSetPBdr() ? pr.getPBdr() : null;
        if (create && ct == null) {
            ct = pr.addNewPBdr();
        }
        return ct;
    }
    
    private CTSpacing getCTSpacing(final boolean create) {
        final CTPPr pr = this.getCTPPr();
        CTSpacing ct = pr.getSpacing();
        if (create && ct == null) {
            ct = pr.addNewSpacing();
        }
        return ct;
    }
    
    private CTInd getCTInd(final boolean create) {
        final CTPPr pr = this.getCTPPr();
        CTInd ct = pr.getInd();
        if (create && ct == null) {
            ct = pr.addNewInd();
        }
        return ct;
    }
    
    private CTPPr getCTPPr() {
        return (this.paragraph.getPPr() == null) ? this.paragraph.addNewPPr() : this.paragraph.getPPr();
    }
    
    protected void addRun(final CTR run) {
        final int pos = this.paragraph.sizeOfRArray();
        this.paragraph.addNewR();
        this.paragraph.setRArray(pos, run);
    }
    
    public XWPFRun createRun() {
        final XWPFRun xwpfRun = new XWPFRun(this.paragraph.addNewR(), (IRunBody)this);
        this.runs.add(xwpfRun);
        this.iruns.add(xwpfRun);
        return xwpfRun;
    }
    
    public XWPFHyperlinkRun createHyperlinkRun(final String uri) {
        final String rId = this.getPart().getPackagePart().addExternalRelationship(uri, XWPFRelation.HYPERLINK.getRelation()).getId();
        final CTHyperlink ctHyperLink = this.getCTP().addNewHyperlink();
        ctHyperLink.setId(rId);
        ctHyperLink.addNewR();
        final XWPFHyperlinkRun link = new XWPFHyperlinkRun(ctHyperLink, ctHyperLink.getRArray(0), this);
        this.runs.add(link);
        this.iruns.add(link);
        return link;
    }
    
    public XWPFRun insertNewRun(final int pos) {
        if (pos >= 0 && pos <= this.runs.size()) {
            int rPos = 0;
            for (int i = 0; i < pos; ++i) {
                final XWPFRun currRun = this.runs.get(i);
                if (!(currRun instanceof XWPFHyperlinkRun) && !(currRun instanceof XWPFFieldRun)) {
                    ++rPos;
                }
            }
            final CTR ctRun = this.paragraph.insertNewR(rPos);
            final XWPFRun newRun = new XWPFRun(ctRun, (IRunBody)this);
            int iPos = this.iruns.size();
            if (pos < this.runs.size()) {
                final XWPFRun oldAtPos = this.runs.get(pos);
                final int oldAt = this.iruns.indexOf(oldAtPos);
                if (oldAt != -1) {
                    iPos = oldAt;
                }
            }
            this.iruns.add(iPos, newRun);
            this.runs.add(pos, newRun);
            return newRun;
        }
        return null;
    }
    
    public TextSegment searchText(final String searched, final PositionInParagraph startPos) {
        final int startRun = startPos.getRun();
        final int startText = startPos.getText();
        final int startChar = startPos.getChar();
        int beginRunPos = 0;
        int candCharPos = 0;
        boolean newList = false;
        final CTR[] rArray = this.paragraph.getRArray();
        for (int runPos = startRun; runPos < rArray.length; ++runPos) {
            int beginTextPos = 0;
            int beginCharPos = 0;
            int textPos = 0;
            final CTR ctRun = rArray[runPos];
            final XmlCursor c = ctRun.newCursor();
            c.selectPath("./*");
            try {
                while (c.toNextSelection()) {
                    final XmlObject o = c.getObject();
                    if (o instanceof CTText) {
                        if (textPos >= startText) {
                            final String candidate = ((CTText)o).getStringValue();
                            int charPos;
                            if (runPos == startRun) {
                                charPos = startChar;
                            }
                            else {
                                charPos = 0;
                            }
                            while (charPos < candidate.length()) {
                                if (candidate.charAt(charPos) == searched.charAt(0) && candCharPos == 0) {
                                    beginTextPos = textPos;
                                    beginCharPos = charPos;
                                    beginRunPos = runPos;
                                    newList = true;
                                }
                                if (candidate.charAt(charPos) == searched.charAt(candCharPos)) {
                                    if (candCharPos + 1 < searched.length()) {
                                        ++candCharPos;
                                    }
                                    else if (newList) {
                                        final TextSegment segment = new TextSegment();
                                        segment.setBeginRun(beginRunPos);
                                        segment.setBeginText(beginTextPos);
                                        segment.setBeginChar(beginCharPos);
                                        segment.setEndRun(runPos);
                                        segment.setEndText(textPos);
                                        segment.setEndChar(charPos);
                                        return segment;
                                    }
                                }
                                else {
                                    candCharPos = 0;
                                }
                                ++charPos;
                            }
                        }
                        ++textPos;
                    }
                    else if (o instanceof CTProofErr) {
                        c.removeXml();
                    }
                    else {
                        if (o instanceof CTRPr) {
                            continue;
                        }
                        candCharPos = 0;
                    }
                }
            }
            finally {
                c.dispose();
            }
        }
        return null;
    }
    
    public String getText(final TextSegment segment) {
        final int runBegin = segment.getBeginRun();
        final int textBegin = segment.getBeginText();
        final int charBegin = segment.getBeginChar();
        final int runEnd = segment.getEndRun();
        final int textEnd = segment.getEndText();
        final int charEnd = segment.getEndChar();
        final StringBuilder out = new StringBuilder();
        final CTR[] rArray = this.paragraph.getRArray();
        for (int i = runBegin; i <= runEnd; ++i) {
            final CTText[] tArray = rArray[i].getTArray();
            int startText = 0;
            int endText = tArray.length - 1;
            if (i == runBegin) {
                startText = textBegin;
            }
            if (i == runEnd) {
                endText = textEnd;
            }
            for (int j = startText; j <= endText; ++j) {
                final String tmpText = tArray[j].getStringValue();
                int startChar = 0;
                int endChar = tmpText.length() - 1;
                if (j == textBegin && i == runBegin) {
                    startChar = charBegin;
                }
                if (j == textEnd && i == runEnd) {
                    endChar = charEnd;
                }
                out.append(tmpText, startChar, endChar + 1);
            }
        }
        return out.toString();
    }
    
    public boolean removeRun(final int pos) {
        if (pos < 0 || pos >= this.runs.size()) {
            return false;
        }
        final XWPFRun run = this.runs.get(pos);
        if (run instanceof XWPFHyperlinkRun || run instanceof XWPFFieldRun) {
            throw new IllegalArgumentException("Removing Field or Hyperlink runs not yet supported");
        }
        this.runs.remove(pos);
        this.iruns.remove(run);
        int rPos = 0;
        for (int i = 0; i < pos; ++i) {
            final XWPFRun currRun = this.runs.get(i);
            if (!(currRun instanceof XWPFHyperlinkRun) && !(currRun instanceof XWPFFieldRun)) {
                ++rPos;
            }
        }
        this.getCTP().removeR(rPos);
        return true;
    }
    
    @Override
    public BodyElementType getElementType() {
        return BodyElementType.PARAGRAPH;
    }
    
    @Override
    public IBody getBody() {
        return this.part;
    }
    
    @Override
    public POIXMLDocumentPart getPart() {
        if (this.part != null) {
            return this.part.getPart();
        }
        return null;
    }
    
    @Override
    public BodyType getPartType() {
        return this.part.getPartType();
    }
    
    public void addRun(final XWPFRun r) {
        if (!this.runs.contains(r)) {
            this.runs.add(r);
        }
    }
    
    public XWPFRun getRun(final CTR r) {
        for (int i = 0; i < this.getRuns().size(); ++i) {
            if (this.getRuns().get(i).getCTR() == r) {
                return this.getRuns().get(i);
            }
        }
        return null;
    }
    
    public void addFootnoteReference(final XWPFAbstractFootnoteEndnote footnote) {
        final XWPFRun run = this.createRun();
        final CTR ctRun = run.getCTR();
        ctRun.addNewRPr().addNewRStyle().setVal("FootnoteReference");
        if (footnote instanceof XWPFEndnote) {
            ctRun.addNewEndnoteReference().setId(footnote.getId());
        }
        else {
            ctRun.addNewFootnoteReference().setId(footnote.getId());
        }
    }
}
