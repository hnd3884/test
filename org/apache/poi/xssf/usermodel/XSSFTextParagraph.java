package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextAutonumberBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAutonumberScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacing;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStopList;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePoint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePercent;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import java.awt.Color;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontAlignType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAlignType;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.apache.poi.xssf.model.ParagraphPropertyFetcher;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.apache.poi.util.Internal;
import java.util.Iterator;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import java.util.ArrayList;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;

public class XSSFTextParagraph implements Iterable<XSSFTextRun>
{
    private final CTTextParagraph _p;
    private final CTShape _shape;
    private final List<XSSFTextRun> _runs;
    
    XSSFTextParagraph(final CTTextParagraph p, final CTShape ctShape) {
        this._p = p;
        this._shape = ctShape;
        this._runs = new ArrayList<XSSFTextRun>();
        for (final XmlObject ch : this._p.selectPath("*")) {
            if (ch instanceof CTRegularTextRun) {
                final CTRegularTextRun r = (CTRegularTextRun)ch;
                this._runs.add(new XSSFTextRun(r, this));
            }
            else if (ch instanceof CTTextLineBreak) {
                final CTTextLineBreak br = (CTTextLineBreak)ch;
                final CTRegularTextRun r2 = CTRegularTextRun.Factory.newInstance();
                r2.setRPr(br.getRPr());
                r2.setT("\n");
                this._runs.add(new XSSFTextRun(r2, this));
            }
            else if (ch instanceof CTTextField) {
                final CTTextField f = (CTTextField)ch;
                final CTRegularTextRun r2 = CTRegularTextRun.Factory.newInstance();
                r2.setRPr(f.getRPr());
                r2.setT(f.getT());
                this._runs.add(new XSSFTextRun(r2, this));
            }
        }
    }
    
    public String getText() {
        final StringBuilder out = new StringBuilder();
        for (final XSSFTextRun r : this._runs) {
            out.append(r.getText());
        }
        return out.toString();
    }
    
    @Internal
    public CTTextParagraph getXmlObject() {
        return this._p;
    }
    
    @Internal
    public CTShape getParentShape() {
        return this._shape;
    }
    
    public List<XSSFTextRun> getTextRuns() {
        return this._runs;
    }
    
    @Override
    public Iterator<XSSFTextRun> iterator() {
        return this._runs.iterator();
    }
    
    public XSSFTextRun addNewTextRun() {
        final CTRegularTextRun r = this._p.addNewR();
        final CTTextCharacterProperties rPr = r.addNewRPr();
        rPr.setLang("en-US");
        final XSSFTextRun run = new XSSFTextRun(r, this);
        this._runs.add(run);
        return run;
    }
    
    public XSSFTextRun addLineBreak() {
        final CTTextLineBreak br = this._p.addNewBr();
        final CTTextCharacterProperties brProps = br.addNewRPr();
        if (this._runs.size() > 0) {
            final CTTextCharacterProperties prevRun = this._runs.get(this._runs.size() - 1).getRPr();
            brProps.set((XmlObject)prevRun);
        }
        final CTRegularTextRun r = CTRegularTextRun.Factory.newInstance();
        r.setRPr(brProps);
        r.setT("\n");
        final XSSFTextRun run = new XSSFLineBreak(r, this, brProps);
        this._runs.add(run);
        return run;
    }
    
    public TextAlign getTextAlign() {
        final ParagraphPropertyFetcher<TextAlign> fetcher = new ParagraphPropertyFetcher<TextAlign>(this.getLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetAlgn()) {
                    final TextAlign val = TextAlign.values()[props.getAlgn().intValue() - 1];
                    this.setValue(val);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return (fetcher.getValue() == null) ? TextAlign.LEFT : fetcher.getValue();
    }
    
    public void setTextAlign(final TextAlign align) {
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
    
    public TextFontAlign getTextFontAlign() {
        final ParagraphPropertyFetcher<TextFontAlign> fetcher = new ParagraphPropertyFetcher<TextFontAlign>(this.getLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetFontAlgn()) {
                    final TextFontAlign val = TextFontAlign.values()[props.getFontAlgn().intValue() - 1];
                    this.setValue(val);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return (fetcher.getValue() == null) ? TextFontAlign.BASELINE : fetcher.getValue();
    }
    
    public void setTextFontAlign(final TextFontAlign align) {
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
        final ParagraphPropertyFetcher<String> fetcher = new ParagraphPropertyFetcher<String>(this.getLevel()) {
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
        final ParagraphPropertyFetcher<String> fetcher = new ParagraphPropertyFetcher<String>(this.getLevel()) {
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
    
    public Color getBulletFontColor() {
        final ParagraphPropertyFetcher<Color> fetcher = new ParagraphPropertyFetcher<Color>(this.getLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetBuClr() && props.getBuClr().isSetSrgbClr()) {
                    final CTSRgbColor clr = props.getBuClr().getSrgbClr();
                    final byte[] rgb = clr.getVal();
                    this.setValue(new Color(0xFF & rgb[0], 0xFF & rgb[1], 0xFF & rgb[2]));
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setBulletFontColor(final Color color) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        final CTColor c = pr.isSetBuClr() ? pr.getBuClr() : pr.addNewBuClr();
        final CTSRgbColor clr = c.isSetSrgbClr() ? c.getSrgbClr() : c.addNewSrgbClr();
        clr.setVal(new byte[] { (byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue() });
    }
    
    public double getBulletFontSize() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getLevel()) {
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
        return (fetcher.getValue() == null) ? 100.0 : fetcher.getValue();
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
    
    public void setIndent(final double value) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (value == -1.0) {
            if (pr.isSetIndent()) {
                pr.unsetIndent();
            }
        }
        else {
            pr.setIndent(Units.toEMU(value));
        }
    }
    
    public double getIndent() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getLevel()) {
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
        return (fetcher.getValue() == null) ? 0.0 : fetcher.getValue();
    }
    
    public void setLeftMargin(final double value) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (value == -1.0) {
            if (pr.isSetMarL()) {
                pr.unsetMarL();
            }
        }
        else {
            pr.setMarL(Units.toEMU(value));
        }
    }
    
    public double getLeftMargin() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getLevel()) {
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
        return (fetcher.getValue() == null) ? 0.0 : fetcher.getValue();
    }
    
    public void setRightMargin(final double value) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (value == -1.0) {
            if (pr.isSetMarR()) {
                pr.unsetMarR();
            }
        }
        else {
            pr.setMarR(Units.toEMU(value));
        }
    }
    
    public double getRightMargin() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getLevel()) {
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
        return (fetcher.getValue() == null) ? 0.0 : fetcher.getValue();
    }
    
    public double getDefaultTabSize() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getLevel()) {
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
        return (fetcher.getValue() == null) ? 0.0 : fetcher.getValue();
    }
    
    public double getTabStop(final int idx) {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getLevel()) {
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
    
    public void setLineSpacing(final double linespacing) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        final CTTextSpacing spc = CTTextSpacing.Factory.newInstance();
        if (linespacing >= 0.0) {
            spc.addNewSpcPct().setVal((int)(linespacing * 1000.0));
        }
        else {
            spc.addNewSpcPts().setVal((int)(-linespacing * 100.0));
        }
        pr.setLnSpc(spc);
    }
    
    public double getLineSpacing() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetLnSpc()) {
                    final CTTextSpacing spc = props.getLnSpc();
                    if (spc.isSetSpcPct()) {
                        this.setValue(spc.getSpcPct().getVal() * 0.001);
                    }
                    else if (spc.isSetSpcPts()) {
                        this.setValue(-spc.getSpcPts().getVal() * 0.01);
                    }
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        double lnSpc = (fetcher.getValue() == null) ? 100.0 : fetcher.getValue();
        if (lnSpc > 0.0) {
            final CTTextNormalAutofit normAutofit = this._shape.getTxBody().getBodyPr().getNormAutofit();
            if (normAutofit != null) {
                final double scale = 1.0 - normAutofit.getLnSpcReduction() / 100000.0;
                lnSpc *= scale;
            }
        }
        return lnSpc;
    }
    
    public void setSpaceBefore(final double spaceBefore) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        final CTTextSpacing spc = CTTextSpacing.Factory.newInstance();
        if (spaceBefore >= 0.0) {
            spc.addNewSpcPct().setVal((int)(spaceBefore * 1000.0));
        }
        else {
            spc.addNewSpcPts().setVal((int)(-spaceBefore * 100.0));
        }
        pr.setSpcBef(spc);
    }
    
    public double getSpaceBefore() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetSpcBef()) {
                    final CTTextSpacing spc = props.getSpcBef();
                    if (spc.isSetSpcPct()) {
                        this.setValue(spc.getSpcPct().getVal() * 0.001);
                    }
                    else if (spc.isSetSpcPts()) {
                        this.setValue(-spc.getSpcPts().getVal() * 0.01);
                    }
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return (fetcher.getValue() == null) ? 0.0 : fetcher.getValue();
    }
    
    public void setSpaceAfter(final double spaceAfter) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        final CTTextSpacing spc = CTTextSpacing.Factory.newInstance();
        if (spaceAfter >= 0.0) {
            spc.addNewSpcPct().setVal((int)(spaceAfter * 1000.0));
        }
        else {
            spc.addNewSpcPts().setVal((int)(-spaceAfter * 100.0));
        }
        pr.setSpcAft(spc);
    }
    
    public double getSpaceAfter() {
        final ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(this.getLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetSpcAft()) {
                    final CTTextSpacing spc = props.getSpcAft();
                    if (spc.isSetSpcPct()) {
                        this.setValue(spc.getSpcPct().getVal() * 0.001);
                    }
                    else if (spc.isSetSpcPts()) {
                        this.setValue(-spc.getSpcPts().getVal() * 0.01);
                    }
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return (fetcher.getValue() == null) ? 0.0 : fetcher.getValue();
    }
    
    public void setLevel(final int level) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        pr.setLvl(level);
    }
    
    public int getLevel() {
        final CTTextParagraphProperties pr = this._p.getPPr();
        if (pr == null) {
            return 0;
        }
        return pr.getLvl();
    }
    
    public boolean isBullet() {
        final ParagraphPropertyFetcher<Boolean> fetcher = new ParagraphPropertyFetcher<Boolean>(this.getLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetBuNone()) {
                    this.setValue(false);
                    return true;
                }
                if (props.isSetBuFont() && (props.isSetBuChar() || props.isSetBuAutoNum())) {
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
        if (!flag) {
            pr.addNewBuNone();
            if (pr.isSetBuAutoNum()) {
                pr.unsetBuAutoNum();
            }
            if (pr.isSetBuBlip()) {
                pr.unsetBuBlip();
            }
            if (pr.isSetBuChar()) {
                pr.unsetBuChar();
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
        }
        else {
            if (pr.isSetBuNone()) {
                pr.unsetBuNone();
            }
            if (!pr.isSetBuFont()) {
                pr.addNewBuFont().setTypeface("Arial");
            }
            if (!pr.isSetBuAutoNum()) {
                pr.addNewBuChar().setChar("\u2022");
            }
        }
    }
    
    public void setBullet(final ListAutoNumber scheme, final int startAt) {
        if (startAt < 1) {
            throw new IllegalArgumentException("Start Number must be greater or equal that 1");
        }
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        final CTTextAutonumberBullet lst = pr.isSetBuAutoNum() ? pr.getBuAutoNum() : pr.addNewBuAutoNum();
        lst.setType(STTextAutonumberScheme.Enum.forInt(scheme.ordinal() + 1));
        lst.setStartAt(startAt);
        if (!pr.isSetBuFont()) {
            pr.addNewBuFont().setTypeface("Arial");
        }
        if (pr.isSetBuNone()) {
            pr.unsetBuNone();
        }
        if (pr.isSetBuBlip()) {
            pr.unsetBuBlip();
        }
        if (pr.isSetBuChar()) {
            pr.unsetBuChar();
        }
    }
    
    public void setBullet(final ListAutoNumber scheme) {
        final CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        final CTTextAutonumberBullet lst = pr.isSetBuAutoNum() ? pr.getBuAutoNum() : pr.addNewBuAutoNum();
        lst.setType(STTextAutonumberScheme.Enum.forInt(scheme.ordinal() + 1));
        if (!pr.isSetBuFont()) {
            pr.addNewBuFont().setTypeface("Arial");
        }
        if (pr.isSetBuNone()) {
            pr.unsetBuNone();
        }
        if (pr.isSetBuBlip()) {
            pr.unsetBuBlip();
        }
        if (pr.isSetBuChar()) {
            pr.unsetBuChar();
        }
    }
    
    public boolean isBulletAutoNumber() {
        final ParagraphPropertyFetcher<Boolean> fetcher = new ParagraphPropertyFetcher<Boolean>(this.getLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetBuAutoNum()) {
                    this.setValue(true);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return fetcher.getValue() != null && fetcher.getValue();
    }
    
    public int getBulletAutoNumberStart() {
        final ParagraphPropertyFetcher<Integer> fetcher = new ParagraphPropertyFetcher<Integer>(this.getLevel()) {
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
        return (fetcher.getValue() == null) ? 0 : fetcher.getValue();
    }
    
    public ListAutoNumber getBulletAutoNumberScheme() {
        final ParagraphPropertyFetcher<ListAutoNumber> fetcher = new ParagraphPropertyFetcher<ListAutoNumber>(this.getLevel()) {
            @Override
            public boolean fetch(final CTTextParagraphProperties props) {
                if (props.isSetBuAutoNum()) {
                    this.setValue(ListAutoNumber.values()[props.getBuAutoNum().getType().intValue() - 1]);
                    return true;
                }
                return false;
            }
        };
        this.fetchParagraphProperty(fetcher);
        return (fetcher.getValue() == null) ? ListAutoNumber.ARABIC_PLAIN : fetcher.getValue();
    }
    
    private boolean fetchParagraphProperty(final ParagraphPropertyFetcher visitor) {
        boolean ok = false;
        if (this._p.isSetPPr()) {
            ok = visitor.fetch(this._p.getPPr());
        }
        if (!ok) {
            ok = visitor.fetch(this._shape);
        }
        return ok;
    }
    
    @Override
    public String toString() {
        return "[" + this.getClass() + "]" + this.getText();
    }
}
