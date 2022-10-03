package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.TabStop;
import java.util.Objects;
import org.apache.poi.xslf.model.PropertyFetcher;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextAutonumberBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAutonumberScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPoint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPercent;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacing;
import java.util.function.Supplier;
import java.util.function.Function;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStopList;
import org.apache.poi.util.Units;
import org.apache.poi.sl.usermodel.AutoNumberingScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePoint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePercent;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.apache.poi.sl.draw.DrawPaint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import java.awt.Color;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontAlignType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAlignType;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.apache.poi.xslf.model.ParagraphPropertyFetcher;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.apache.poi.util.Internal;
import java.util.Iterator;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import java.util.ArrayList;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.apache.poi.sl.usermodel.TextParagraph;

public class XSLFTextParagraph implements TextParagraph<XSLFShape, XSLFTextParagraph, XSLFTextRun>
{
    private final CTTextParagraph _p;
    private final List<XSLFTextRun> _runs;
    private final XSLFTextShape _shape;
    
    XSLFTextParagraph(final CTTextParagraph p, final XSLFTextShape shape) {
        this._p = p;
        this._runs = new ArrayList<XSLFTextRun>();
        this._shape = shape;
        final XmlCursor c = this._p.newCursor();
        try {
            if (c.toFirstChild()) {
                do {
                    final XmlObject r = c.getObject();
                    if (r instanceof CTTextLineBreak) {
                        this._runs.add(new XSLFLineBreak((CTTextLineBreak)r, this));
                    }
                    else {
                        if (!(r instanceof CTRegularTextRun) && !(r instanceof CTTextField)) {
                            continue;
                        }
                        this._runs.add(new XSLFTextRun(r, this));
                    }
                } while (c.toNextSibling());
            }
        }
        finally {
            c.dispose();
        }
    }
    
    public String getText() {
        final StringBuilder out = new StringBuilder();
        for (final XSLFTextRun r : this._runs) {
            out.append(r.getRawText());
        }
        return out.toString();
    }
    
    @Internal
    public CTTextParagraph getXmlObject() {
        return this._p;
    }
    
    public XSLFTextShape getParentShape() {
        return this._shape;
    }
    
    public List<XSLFTextRun> getTextRuns() {
        return this._runs;
    }
    
    public Iterator<XSLFTextRun> iterator() {
        return this._runs.iterator();
    }
    
    public XSLFTextRun addNewTextRun() {
        final CTRegularTextRun r = this._p.addNewR();
        final CTTextCharacterProperties rPr = r.addNewRPr();
        rPr.setLang("en-US");
        final XSLFTextRun run = this.newTextRun((XmlObject)r);
        this._runs.add(run);
        return run;
    }
    
    public XSLFTextRun addLineBreak() {
        final XSLFLineBreak run = new XSLFLineBreak(this._p.addNewBr(), this);
        final CTTextCharacterProperties brProps = run.getRPr(true);
        if (this._runs.size() > 0) {
            final CTTextCharacterProperties prevRun = this._runs.get(this._runs.size() - 1).getRPr(true);
            brProps.set((XmlObject)prevRun);
            if (brProps.isSetHlinkClick()) {
                brProps.unsetHlinkClick();
            }
            if (brProps.isSetHlinkMouseOver()) {
                brProps.unsetHlinkMouseOver();
            }
        }
        this._runs.add(run);
        return run;
    }
    
    public TextParagraph.TextAlign getTextAlign() {
        final ParagraphPropertyFetcher<TextParagraph.TextAlign> fetcher = new ParagraphPropertyFetcher<TextParagraph.TextAlign>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetAlgn()) {
                    final TextParagraph.TextAlign val = TextParagraph.TextAlign.values()[props.getAlgn().intValue() - 1];
                    this.setValue(val);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setTextAlign(final TextParagraph.TextAlign align) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (align == null) {
            if (pr.isSetAlgn()) {
                pr.unsetAlgn();
            }
        }
        else {
            pr.setAlgn(STTextAlignType.Enum.forInt(align.ordinal() + 1));
        }
    }
    
    public TextParagraph.FontAlign getFontAlign() {
        final ParagraphPropertyFetcher<TextParagraph.FontAlign> fetcher = new ParagraphPropertyFetcher<TextParagraph.FontAlign>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetFontAlgn()) {
                    final TextParagraph.FontAlign val = TextParagraph.FontAlign.values()[props.getFontAlgn().intValue() - 1];
                    this.setValue(val);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setFontAlign(final TextParagraph.FontAlign align) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (align == null) {
            if (pr.isSetFontAlgn()) {
                pr.unsetFontAlgn();
            }
        }
        else {
            pr.setFontAlgn(STTextFontAlignType.Enum.forInt(align.ordinal() + 1));
        }
    }
    
    public String getBulletFont() {
        final ParagraphPropertyFetcher<String> fetcher = new ParagraphPropertyFetcher<String>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetBuFont()) {
                    this.setValue(props.getBuFont().getTypeface());
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setBulletFont(final String typeface) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        final CTTextFont font = pr.isSetBuFont() ? pr.getBuFont() : pr.addNewBuFont();
        font.setTypeface(typeface);
    }
    
    public String getBulletCharacter() {
        final ParagraphPropertyFetcher<String> fetcher = new ParagraphPropertyFetcher<String>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetBuChar()) {
                    this.setValue(props.getBuChar().getChar());
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setBulletCharacter(final String str) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        final CTTextCharBullet c = pr.isSetBuChar() ? pr.getBuChar() : pr.addNewBuChar();
        c.setChar(str);
    }
    
    public PaintStyle getBulletFontColor() {
        final XSLFTheme theme = this.getParentShape().getSheet().getTheme();
        final ParagraphPropertyFetcher<Color> fetcher = new ParagraphPropertyFetcher<Color>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetBuClr()) {
                    final XSLFColor c = new XSLFColor((XmlObject)props.getBuClr(), theme, null, XSLFTextParagraph.this._shape.getSheet());
                    this.setValue(c.getColor());
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        final Color col = fetcher.getValue();
        return (PaintStyle)((col == null) ? null : DrawPaint.createSolidPaint(col));
    }
    
    public void setBulletFontColor(final Color color) {
        this.setBulletFontColor((PaintStyle)DrawPaint.createSolidPaint(color));
    }
    
    public void setBulletFontColor(final PaintStyle color) {
        if (!(color instanceof PaintStyle.SolidPaint)) {
            throw new IllegalArgumentException("Currently XSLF only supports SolidPaint");
        }
        final PaintStyle.SolidPaint sp = (PaintStyle.SolidPaint)color;
        final Color col = DrawPaint.applyColorTransform(sp.getSolidColor());
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        final CTColor c = pr.isSetBuClr() ? pr.getBuClr() : pr.addNewBuClr();
        final CTSRgbColor clr = c.isSetSrgbClr() ? c.getSrgbClr() : c.addNewSrgbClr();
        clr.setVal(new byte[] { (byte)col.getRed(), (byte)col.getGreen(), (byte)col.getBlue() });
    }
    
    public Double getBulletFontSize() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetBuSzPct()) {
                    this.setValue(props.getBuSzPct().getVal() * 0.001);
                    return true;
                }
                if (props.isSetBuSzPts()) {
                    this.setValue(-props.getBuSzPts().getVal() * 0.01);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setBulletFontSize(final double bulletSize) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (bulletSize >= 0.0) {
            final CTTextBulletSizePercent pt = pr.isSetBuSzPct() ? pr.getBuSzPct() : pr.addNewBuSzPct();
            pt.setVal((int)(bulletSize * 1000.0));
            if (pr.isSetBuSzPts()) {
                pr.unsetBuSzPts();
            }
        }
        else {
            final CTTextBulletSizePoint pt2 = pr.isSetBuSzPts() ? pr.getBuSzPts() : pr.addNewBuSzPts();
            pt2.setVal((int)(-bulletSize * 100.0));
            if (pr.isSetBuSzPct()) {
                pr.unsetBuSzPct();
            }
        }
    }
    
    public AutoNumberingScheme getAutoNumberingScheme() {
        final ParagraphPropertyFetcher<AutoNumberingScheme> fetcher = new ParagraphPropertyFetcher<AutoNumberingScheme>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetBuAutoNum()) {
                    final AutoNumberingScheme ans = AutoNumberingScheme.forOoxmlID(props.getBuAutoNum().getType().intValue());
                    if (ans != null) {
                        this.setValue(ans);
                        return true;
                    }
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public Integer getAutoNumberingStartAt() {
        final ParagraphPropertyFetcher<Integer> fetcher = new ParagraphPropertyFetcher<Integer>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetBuAutoNum() && props.getBuAutoNum().isSetStartAt()) {
                    this.setValue(props.getBuAutoNum().getStartAt());
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setIndent(final Double indent) {
        if (indent == null && !this._p.isSetPPr()) {
            return;
        }
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (indent == null) {
            if (pr.isSetIndent()) {
                pr.unsetIndent();
            }
        }
        else {
            pr.setIndent(Units.toEMU((double)indent));
        }
    }
    
    public Double getIndent() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetIndent()) {
                    this.setValue(Units.toPoints((long)props.getIndent()));
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setLeftMargin(final Double leftMargin) {
        if (leftMargin == null && !this._p.isSetPPr()) {
            return;
        }
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (leftMargin == null) {
            if (pr.isSetMarL()) {
                pr.unsetMarL();
            }
        }
        else {
            pr.setMarL(Units.toEMU((double)leftMargin));
        }
    }
    
    public Double getLeftMargin() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetMarL()) {
                    final double val = Units.toPoints((long)props.getMarL());
                    this.setValue(val);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setRightMargin(final Double rightMargin) {
        if (rightMargin == null && !this._p.isSetPPr()) {
            return;
        }
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (rightMargin == null) {
            if (pr.isSetMarR()) {
                pr.unsetMarR();
            }
        }
        else {
            pr.setMarR(Units.toEMU((double)rightMargin));
        }
    }
    
    public Double getRightMargin() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetMarR()) {
                    final double val = Units.toPoints((long)props.getMarR());
                    this.setValue(val);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public Double getDefaultTabSize() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetDefTabSz()) {
                    final double val = Units.toPoints((long)props.getDefTabSz());
                    this.setValue(val);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public double getTabStop(final int idx) {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetTabLst()) {
                    final CTTextTabStopList tabStops = props.getTabLst();
                    if (idx < tabStops.sizeOfTabArray()) {
                        final CTTextTabStop ts = tabStops.getTabArray(idx);
                        final double val = Units.toPoints((long)ts.getPos());
                        this.setValue(val);
                        return true;
                    }
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return (fetcher.getValue() == null) ? 0.0 : fetcher.getValue();
    }
    
    public void addTabStop(final double value) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        final CTTextTabStopList tabStops = pr.isSetTabLst() ? pr.getTabLst() : pr.addNewTabLst();
        tabStops.addNewTab().setPos(Units.toEMU(value));
    }
    
    public void setLineSpacing(final Double lineSpacing) {
        this.setSpacing(lineSpacing, props -> props::getLnSpc, props -> props::addNewLnSpc, props -> props::unsetLnSpc);
    }
    
    public Double getLineSpacing() {
        final Double lnSpc = this.getSpacing(props -> props::getLnSpc);
        if (lnSpc != null && lnSpc > 0.0) {
            final CTTextNormalAutofit normAutofit = this.getParentShape().getTextBodyPr().getNormAutofit();
            if (normAutofit != null) {
                final double scale = 1.0 - normAutofit.getLnSpcReduction() / 100000.0;
                return lnSpc * scale;
            }
        }
        return lnSpc;
    }
    
    public void setSpaceBefore(final Double spaceBefore) {
        this.setSpacing(spaceBefore, props -> props::getSpcBef, props -> props::addNewSpcBef, props -> props::unsetSpcBef);
    }
    
    public Double getSpaceBefore() {
        return this.getSpacing(props -> props::getSpcBef);
    }
    
    public void setSpaceAfter(final Double spaceAfter) {
        this.setSpacing(spaceAfter, props -> props::getSpcAft, props -> props::addNewSpcAft, props -> props::unsetSpcAft);
    }
    
    public Double getSpaceAfter() {
        return this.getSpacing(props -> props::getSpcAft);
    }
    
    private void setSpacing(final Double space, final Function<CTTextParagraphProperties, Supplier<CTTextSpacing>> getSpc, final Function<CTTextParagraphProperties, Supplier<CTTextSpacing>> addSpc, final Function<CTTextParagraphProperties, Procedure> unsetSpc) {
        final CTTextParagraphProperties pPr = (space == null || this._p.isSetPPr()) ? this._p.getPPr() : this._p.addNewPPr();
        if (pPr == null) {
            return;
        }
        CTTextSpacing spc = getSpc.apply(pPr).get();
        if (space == null) {
            if (spc != null) {
                unsetSpc.apply(pPr).accept();
            }
            return;
        }
        if (spc == null) {
            spc = addSpc.apply(pPr).get();
        }
        if (space >= 0.0) {
            if (spc.isSetSpcPts()) {
                spc.unsetSpcPts();
            }
            final CTTextSpacingPercent pct = spc.isSetSpcPct() ? spc.getSpcPct() : spc.addNewSpcPct();
            pct.setVal((int)(space * 1000.0));
        }
        else {
            if (spc.isSetSpcPct()) {
                spc.unsetSpcPct();
            }
            final CTTextSpacingPoint pts = spc.isSetSpcPts() ? spc.getSpcPts() : spc.addNewSpcPts();
            pts.setVal((int)(-space * 100.0));
        }
    }
    
    private Double getSpacing(final Function<CTTextParagraphProperties, Supplier<CTTextSpacing>> getSpc) {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                final CTTextSpacing spc = getSpc.apply(props).get();
                if (spc == null) {
                    return false;
                }
                if (spc.isSetSpcPct()) {
                    this.setValue(spc.getSpcPct().getVal() * 0.001);
                    return true;
                }
                if (spc.isSetSpcPts()) {
                    this.setValue(-spc.getSpcPts().getVal() * 0.01);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setIndentLevel(final int level) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        pr.setLvl(level);
    }
    
    public int getIndentLevel() {
        final CTTextParagraphProperties pr = this._p.getPPr();
        return (pr == null || !pr.isSetLvl()) ? 0 : pr.getLvl();
    }
    
    public boolean isBullet() {
        final ParagraphPropertyFetcher<Boolean> fetcher = new ParagraphPropertyFetcher<Boolean>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetBuNone()) {
                    this.setValue(false);
                    return true;
                }
                if (props.isSetBuFont() || props.isSetBuChar()) {
                    this.setValue(true);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue() != null && fetcher.getValue();
    }
    
    public void setBullet(final boolean flag) {
        if (this.isBullet() == flag) {
            return;
        }
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (flag) {
            pr.addNewBuFont().setTypeface("Arial");
            pr.addNewBuChar().setChar("\u2022");
        }
        else {
            if (pr.isSetBuFont()) {
                pr.unsetBuFont();
            }
            if (pr.isSetBuChar()) {
                pr.unsetBuChar();
            }
            if (pr.isSetBuAutoNum()) {
                pr.unsetBuAutoNum();
            }
            if (pr.isSetBuBlip()) {
                pr.unsetBuBlip();
            }
            if (pr.isSetBuClr()) {
                pr.unsetBuClr();
            }
            if (pr.isSetBuClrTx()) {
                pr.unsetBuClrTx();
            }
            if (pr.isSetBuFont()) {
                pr.unsetBuFont();
            }
            if (pr.isSetBuFontTx()) {
                pr.unsetBuFontTx();
            }
            if (pr.isSetBuSzPct()) {
                pr.unsetBuSzPct();
            }
            if (pr.isSetBuSzPts()) {
                pr.unsetBuSzPts();
            }
            if (pr.isSetBuSzTx()) {
                pr.unsetBuSzTx();
            }
            pr.addNewBuNone();
        }
    }
    
    public void setBulletAutoNumber(final AutoNumberingScheme scheme, final int startAt) {
        if (startAt < 1) {
            throw new IllegalArgumentException("Start Number must be greater or equal that 1");
        }
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        final CTTextAutonumberBullet lst = pr.isSetBuAutoNum() ? pr.getBuAutoNum() : pr.addNewBuAutoNum();
        lst.setType(STTextAutonumberScheme.Enum.forInt(scheme.ooxmlId));
        lst.setStartAt(startAt);
    }
    
    @Override
    public String toString() {
        return "[" + this.getClass() + "]" + this.getText();
    }
    
    private CTTextParagraphProperties getDefaultMasterStyle() {
        final CTPlaceholder ph = this._shape.getPlaceholderDetails().getCTPlaceholder(false);
        String defaultStyleSelector = null;
        switch ((ph == null) ? -1 : ph.getType().intValue()) {
            case 1:
            case 3: {
                defaultStyleSelector = "titleStyle";
                break;
            }
            case -1:
            case 5:
            case 6:
            case 7: {
                defaultStyleSelector = "otherStyle";
                break;
            }
            default: {
                defaultStyleSelector = "bodyStyle";
                break;
            }
        }
        int level = this.getIndentLevel();
        final String nsPML = "http://schemas.openxmlformats.org/presentationml/2006/main";
        XSLFSheet m;
        for (XSLFSheet masterSheet = m = this._shape.getSheet(); m != null; m = (XSLFSheet)m.getMasterSheet()) {
            masterSheet = m;
            final XmlObject xo = masterSheet.getXmlObject();
            final XmlCursor cur = xo.newCursor();
            try {
                cur.push();
                if ((cur.toChild("http://schemas.openxmlformats.org/presentationml/2006/main", "txStyles") && cur.toChild("http://schemas.openxmlformats.org/presentationml/2006/main", defaultStyleSelector)) || (cur.pop() && cur.toChild("http://schemas.openxmlformats.org/presentationml/2006/main", "notesStyle"))) {
                    while (level >= 0) {
                        cur.push();
                        if (cur.toChild("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl" + (level + 1) + "pPr")) {
                            return (CTTextParagraphProperties)cur.getObject();
                        }
                        cur.pop();
                        --level;
                    }
                }
            }
            finally {
                cur.dispose();
            }
        }
        return null;
    }
    
    private void fetchParagraphProperty(final ParagraphPropertyFetcher<?> visitor) {
        final XSLFTextShape shape = this.getParentShape();
        final XSLFSheet sheet = shape.getSheet();
        if (!(sheet instanceof XSLFSlideMaster)) {
            if (this._p.isSetPPr() && visitor.fetch(this._p.getPPr())) {
                return;
            }
            if (shape.fetchShapeProperty(visitor)) {
                return;
            }
            if (this.fetchThemeProperty(visitor)) {
                return;
            }
        }
        this.fetchMasterProperty(visitor);
    }
    
    void fetchMasterProperty(final ParagraphPropertyFetcher<?> visitor) {
        final CTTextParagraphProperties defaultProps = this.getDefaultMasterStyle();
        if (defaultProps != null) {
            visitor.fetch(defaultProps);
        }
    }
    
    boolean fetchThemeProperty(final ParagraphPropertyFetcher<?> visitor) {
        final XSLFTextShape shape = this.getParentShape();
        if (shape.isPlaceholder()) {
            return false;
        }
        final XMLSlideShow ppt = shape.getSheet().getSlideShow();
        final CTTextParagraphProperties themeProps = ppt.getDefaultParagraphStyle(this.getIndentLevel());
        return themeProps != null && visitor.fetch(themeProps);
    }
    
    void copy(final XSLFTextParagraph other) {
        if (other == this) {
            return;
        }
        final CTTextParagraph thisP = this.getXmlObject();
        final CTTextParagraph otherP = other.getXmlObject();
        if (thisP.isSetPPr()) {
            thisP.unsetPPr();
        }
        if (thisP.isSetEndParaRPr()) {
            thisP.unsetEndParaRPr();
        }
        this._runs.clear();
        for (int i = thisP.sizeOfBrArray(); i > 0; --i) {
            thisP.removeBr(i - 1);
        }
        for (int i = thisP.sizeOfRArray(); i > 0; --i) {
            thisP.removeR(i - 1);
        }
        for (int i = thisP.sizeOfFldArray(); i > 0; --i) {
            thisP.removeFld(i - 1);
        }
        final XmlCursor thisC = thisP.newCursor();
        thisC.toEndToken();
        final XmlCursor otherC = otherP.newCursor();
        otherC.copyXmlContents(thisC);
        otherC.dispose();
        thisC.dispose();
        for (final XSLFTextRun tr : other.getTextRuns()) {
            final XmlObject xo = tr.getXmlObject();
            final XSLFTextRun run = (xo instanceof CTTextLineBreak) ? this.newTextRun((CTTextLineBreak)xo) : this.newTextRun(xo);
            run.copy(tr);
            this._runs.add(run);
        }
        final TextParagraph.TextAlign srcAlign = other.getTextAlign();
        if (srcAlign != this.getTextAlign()) {
            this.setTextAlign(srcAlign);
        }
        final boolean isBullet = other.isBullet();
        if (isBullet != this.isBullet()) {
            this.setBullet(isBullet);
            if (isBullet) {
                final String buFont = other.getBulletFont();
                if (buFont != null && !buFont.equals(this.getBulletFont())) {
                    this.setBulletFont(buFont);
                }
                final String buChar = other.getBulletCharacter();
                if (buChar != null && !buChar.equals(this.getBulletCharacter())) {
                    this.setBulletCharacter(buChar);
                }
                final PaintStyle buColor = other.getBulletFontColor();
                if (buColor != null && !buColor.equals(this.getBulletFontColor())) {
                    this.setBulletFontColor(buColor);
                }
                final Double buSize = other.getBulletFontSize();
                if (doubleNotEquals(buSize, this.getBulletFontSize())) {
                    this.setBulletFontSize(buSize);
                }
            }
        }
        final Double leftMargin = other.getLeftMargin();
        if (doubleNotEquals(leftMargin, this.getLeftMargin())) {
            this.setLeftMargin(leftMargin);
        }
        final Double indent = other.getIndent();
        if (doubleNotEquals(indent, this.getIndent())) {
            this.setIndent(indent);
        }
        final Double spaceAfter = other.getSpaceAfter();
        if (doubleNotEquals(spaceAfter, this.getSpaceAfter())) {
            this.setSpaceAfter(spaceAfter);
        }
        final Double spaceBefore = other.getSpaceBefore();
        if (doubleNotEquals(spaceBefore, this.getSpaceBefore())) {
            this.setSpaceBefore(spaceBefore);
        }
        final Double lineSpacing = other.getLineSpacing();
        if (doubleNotEquals(lineSpacing, this.getLineSpacing())) {
            this.setLineSpacing(lineSpacing);
        }
    }
    
    private static boolean doubleNotEquals(final Double d1, final Double d2) {
        return !Objects.equals(d1, d2);
    }
    
    public Double getDefaultFontSize() {
        CTTextCharacterProperties endPr = this._p.getEndParaRPr();
        if (endPr == null || !endPr.isSetSz()) {
            final CTTextParagraphProperties masterStyle = this.getDefaultMasterStyle();
            if (masterStyle != null) {
                endPr = masterStyle.getDefRPr();
            }
        }
        return (endPr == null || !endPr.isSetSz()) ? 12.0 : (endPr.getSz() / 100.0);
    }
    
    public String getDefaultFontFamily() {
        return this._runs.isEmpty() ? "Arial" : this._runs.get(0).getFontFamily();
    }
    
    public TextParagraph.BulletStyle getBulletStyle() {
        if (!this.isBullet()) {
            return null;
        }
        return (TextParagraph.BulletStyle)new TextParagraph.BulletStyle() {
            public String getBulletCharacter() {
                return XSLFTextParagraph.this.getBulletCharacter();
            }
            
            public String getBulletFont() {
                return XSLFTextParagraph.this.getBulletFont();
            }
            
            public Double getBulletFontSize() {
                return XSLFTextParagraph.this.getBulletFontSize();
            }
            
            public PaintStyle getBulletFontColor() {
                return XSLFTextParagraph.this.getBulletFontColor();
            }
            
            public void setBulletFontColor(final Color color) {
                this.setBulletFontColor((PaintStyle)DrawPaint.createSolidPaint(color));
            }
            
            public void setBulletFontColor(final PaintStyle color) {
                XSLFTextParagraph.this.setBulletFontColor(color);
            }
            
            public AutoNumberingScheme getAutoNumberingScheme() {
                return XSLFTextParagraph.this.getAutoNumberingScheme();
            }
            
            public Integer getAutoNumberingStartAt() {
                return XSLFTextParagraph.this.getAutoNumberingStartAt();
            }
        };
    }
    
    public void setBulletStyle(final Object... styles) {
        if (styles.length == 0) {
            this.setBullet(false);
        }
        else {
            this.setBullet(true);
            for (final Object ostyle : styles) {
                if (ostyle instanceof Number) {
                    this.setBulletFontSize(((Number)ostyle).doubleValue());
                }
                else if (ostyle instanceof Color) {
                    this.setBulletFontColor((Color)ostyle);
                }
                else if (ostyle instanceof Character) {
                    this.setBulletCharacter(ostyle.toString());
                }
                else if (ostyle instanceof String) {
                    this.setBulletFont((String)ostyle);
                }
                else if (ostyle instanceof AutoNumberingScheme) {
                    this.setBulletAutoNumber((AutoNumberingScheme)ostyle, 0);
                }
            }
        }
    }
    
    public List<XSLFTabStop> getTabStops() {
        final ParagraphPropertyFetcher<List<XSLFTabStop>> fetcher = new ParagraphPropertyFetcher<List<XSLFTabStop>>(this.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetTabLst()) {
                    final List<XSLFTabStop> list = new ArrayList<XSLFTabStop>();
                    for (final CTTextTabStop ta : props.getTabLst().getTabArray()) {
                        list.add(new XSLFTabStop(ta));
                    }
                    this.setValue(list);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void addTabStops(final double positionInPoints, final TabStop.TabStopType tabStopType) {
        final XSLFSheet sheet = this.getParentShape().getSheet();
        CTTextParagraphProperties tpp;
        if (sheet instanceof XSLFSlideMaster) {
            tpp = this.getDefaultMasterStyle();
        }
        else {
            final CTTextParagraph xo = this.getXmlObject();
            tpp = (xo.isSetPPr() ? xo.getPPr() : xo.addNewPPr());
        }
        if (tpp == null) {
            return;
        }
        final CTTextTabStopList stl = tpp.isSetTabLst() ? tpp.getTabLst() : tpp.addNewTabLst();
        final XSLFTabStop tab = new XSLFTabStop(stl.addNewTab());
        tab.setPositionInPoints(positionInPoints);
        tab.setType(tabStopType);
    }
    
    public void clearTabStops() {
        final XSLFSheet sheet = this.getParentShape().getSheet();
        final CTTextParagraphProperties tpp = (sheet instanceof XSLFSlideMaster) ? this.getDefaultMasterStyle() : this.getXmlObject().getPPr();
        if (tpp != null && tpp.isSetTabLst()) {
            tpp.unsetTabLst();
        }
    }
    
    void clearButKeepProperties() {
        final CTTextParagraph thisP = this.getXmlObject();
        for (int i = thisP.sizeOfBrArray(); i > 0; --i) {
            thisP.removeBr(i - 1);
        }
        for (int i = thisP.sizeOfFldArray(); i > 0; --i) {
            thisP.removeFld(i - 1);
        }
        if (!this._runs.isEmpty()) {
            final int size = this._runs.size();
            final XSLFTextRun lastRun = this._runs.get(size - 1);
            final CTTextCharacterProperties cpOther = lastRun.getRPr(false);
            if (cpOther != null) {
                if (thisP.isSetEndParaRPr()) {
                    thisP.unsetEndParaRPr();
                }
                final CTTextCharacterProperties cp = thisP.addNewEndParaRPr();
                cp.set((XmlObject)cpOther);
            }
            for (int j = size; j > 0; --j) {
                thisP.removeR(j - 1);
            }
            this._runs.clear();
        }
    }
    
    public boolean isHeaderOrFooter() {
        final CTPlaceholder ph = this._shape.getPlaceholderDetails().getCTPlaceholder(false);
        final int phId = (ph == null) ? -1 : ph.getType().intValue();
        switch (phId) {
            case 5:
            case 6:
            case 7:
            case 8: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    protected XSLFTextRun newTextRun(final XmlObject r) {
        return new XSLFTextRun(r, this);
    }
    
    protected XSLFTextRun newTextRun(final CTTextLineBreak r) {
        return new XSLFLineBreak(r, this);
    }
    
    @FunctionalInterface
    private interface Procedure
    {
        void accept();
    }
}
