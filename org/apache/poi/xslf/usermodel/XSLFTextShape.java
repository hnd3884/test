package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.TextRun;
import java.util.Optional;
import java.util.function.Function;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.apache.poi.ooxml.POIXMLException;
import java.awt.geom.Rectangle2D;
import org.apache.poi.sl.draw.DrawTextShape;
import org.apache.poi.sl.draw.DrawFactory;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Placeholder;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextWrappingType;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVerticalType;
import org.apache.poi.xslf.model.PropertyFetcher;
import org.apache.poi.xslf.model.TextBodyPropertyFetcher;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAnchoringType;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import java.util.Iterator;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.List;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.xddf.usermodel.text.TextContainer;

public abstract class XSLFTextShape extends XSLFSimpleShape implements TextContainer, TextShape<XSLFShape, XSLFTextParagraph>
{
    private final List<XSLFTextParagraph> _paragraphs;
    
    XSLFTextShape(final XmlObject shape, final XSLFSheet sheet) {
        super(shape, sheet);
        this._paragraphs = new ArrayList<XSLFTextParagraph>();
        final CTTextBody txBody = this.getTextBody(false);
        if (txBody != null) {
            for (final CTTextParagraph p : txBody.getPArray()) {
                this._paragraphs.add(this.newTextParagraph(p));
            }
        }
    }
    
    public XDDFTextBody getTextBody() {
        final CTTextBody txBody = this.getTextBody(false);
        if (txBody == null) {
            return null;
        }
        return new XDDFTextBody(this, txBody);
    }
    
    public Iterator<XSLFTextParagraph> iterator() {
        return this.getTextParagraphs().iterator();
    }
    
    public String getText() {
        final StringBuilder out = new StringBuilder();
        for (final XSLFTextParagraph p : this._paragraphs) {
            if (out.length() > 0) {
                out.append('\n');
            }
            out.append(p.getText());
        }
        return out.toString();
    }
    
    public void clearText() {
        this._paragraphs.clear();
        final CTTextBody txBody = this.getTextBody(true);
        txBody.setPArray((CTTextParagraph[])null);
    }
    
    public XSLFTextRun setText(final String text) {
        if (!this._paragraphs.isEmpty()) {
            final CTTextBody txBody = this.getTextBody(false);
            int i;
            for (int cntPs = i = txBody.sizeOfPArray(); i > 1; --i) {
                txBody.removeP(i - 1);
                this._paragraphs.remove(i - 1);
            }
            this._paragraphs.get(0).clearButKeepProperties();
        }
        return this.appendText(text, false);
    }
    
    public XSLFTextRun appendText(final String text, final boolean newParagraph) {
        if (text == null) {
            return null;
        }
        CTTextParagraphProperties otherPPr = null;
        CTTextCharacterProperties otherRPr = null;
        boolean firstPara;
        XSLFTextParagraph para;
        if (this._paragraphs.isEmpty()) {
            firstPara = false;
            para = null;
        }
        else {
            firstPara = !newParagraph;
            para = this._paragraphs.get(this._paragraphs.size() - 1);
            final CTTextParagraph ctp = para.getXmlObject();
            otherPPr = ctp.getPPr();
            final List<XSLFTextRun> runs = para.getTextRuns();
            if (!runs.isEmpty()) {
                final XSLFTextRun r0 = runs.get(runs.size() - 1);
                otherRPr = r0.getRPr(false);
                if (otherRPr == null) {
                    otherRPr = ctp.getEndParaRPr();
                }
            }
        }
        XSLFTextRun run = null;
        for (final String lineTxt : text.split("\\r\\n?|\\n")) {
            if (!firstPara) {
                if (para != null) {
                    final CTTextParagraph ctp2 = para.getXmlObject();
                    final CTTextCharacterProperties unexpectedRPr = ctp2.getEndParaRPr();
                    if (unexpectedRPr != null && unexpectedRPr != otherRPr) {
                        ctp2.unsetEndParaRPr();
                    }
                }
                para = this.addNewTextParagraph();
                if (otherPPr != null) {
                    para.getXmlObject().setPPr(otherPPr);
                }
            }
            boolean firstRun = true;
            for (final String runText : lineTxt.split("[\u000b]")) {
                if (!firstRun) {
                    para.addLineBreak();
                }
                run = para.addNewTextRun();
                run.setText(runText);
                if (otherRPr != null) {
                    run.getRPr(true).set((XmlObject)otherRPr);
                }
                firstRun = false;
            }
            firstPara = false;
        }
        assert run != null;
        return run;
    }
    
    public List<XSLFTextParagraph> getTextParagraphs() {
        return this._paragraphs;
    }
    
    public XSLFTextParagraph addNewTextParagraph() {
        CTTextBody txBody = this.getTextBody(false);
        CTTextParagraph p;
        if (txBody == null) {
            txBody = this.getTextBody(true);
            new XDDFTextBody(this, txBody).initialize();
            p = txBody.getPArray(0);
            p.removeR(0);
        }
        else {
            p = txBody.addNewP();
        }
        final XSLFTextParagraph paragraph = this.newTextParagraph(p);
        this._paragraphs.add(paragraph);
        return paragraph;
    }
    
    public void setVerticalAlignment(final VerticalAlignment anchor) {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr(true);
        if (bodyPr != null) {
            if (anchor == null) {
                if (bodyPr.isSetAnchor()) {
                    bodyPr.unsetAnchor();
                }
            }
            else {
                bodyPr.setAnchor(STTextAnchoringType.Enum.forInt(anchor.ordinal() + 1));
            }
        }
    }
    
    public VerticalAlignment getVerticalAlignment() {
        final PropertyFetcher<VerticalAlignment> fetcher = new TextBodyPropertyFetcher<VerticalAlignment>() {
            @Override
            public boolean fetch(final CTTextBodyProperties props) {
                if (props.isSetAnchor()) {
                    final int val = props.getAnchor().intValue();
                    this.setValue(VerticalAlignment.values()[val - 1]);
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        return (fetcher.getValue() == null) ? VerticalAlignment.TOP : fetcher.getValue();
    }
    
    public void setHorizontalCentered(final Boolean isCentered) {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr(true);
        if (bodyPr != null) {
            if (isCentered == null) {
                if (bodyPr.isSetAnchorCtr()) {
                    bodyPr.unsetAnchorCtr();
                }
            }
            else {
                bodyPr.setAnchorCtr((boolean)isCentered);
            }
        }
    }
    
    public boolean isHorizontalCentered() {
        final PropertyFetcher<Boolean> fetcher = new TextBodyPropertyFetcher<Boolean>() {
            @Override
            public boolean fetch(final CTTextBodyProperties props) {
                if (props.isSetAnchorCtr()) {
                    this.setValue(props.getAnchorCtr());
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        return fetcher.getValue() != null && fetcher.getValue();
    }
    
    public void setTextDirection(final TextShape.TextDirection orientation) {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr(true);
        if (bodyPr != null) {
            if (orientation == null) {
                if (bodyPr.isSetVert()) {
                    bodyPr.unsetVert();
                }
            }
            else {
                bodyPr.setVert(STTextVerticalType.Enum.forInt(orientation.ordinal() + 1));
            }
        }
    }
    
    public TextShape.TextDirection getTextDirection() {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr();
        if (bodyPr != null) {
            final STTextVerticalType.Enum val = bodyPr.getVert();
            if (val != null) {
                switch (val.intValue()) {
                    default: {
                        return TextShape.TextDirection.HORIZONTAL;
                    }
                    case 2:
                    case 5:
                    case 6: {
                        return TextShape.TextDirection.VERTICAL;
                    }
                    case 3: {
                        return TextShape.TextDirection.VERTICAL_270;
                    }
                    case 4:
                    case 7: {
                        return TextShape.TextDirection.STACKED;
                    }
                }
            }
        }
        return TextShape.TextDirection.HORIZONTAL;
    }
    
    public Double getTextRotation() {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr();
        if (bodyPr != null && bodyPr.isSetRot()) {
            return bodyPr.getRot() / 60000.0;
        }
        return null;
    }
    
    public void setTextRotation(final Double rotation) {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr(true);
        if (bodyPr != null) {
            bodyPr.setRot((int)(rotation * 60000.0));
        }
    }
    
    public double getBottomInset() {
        final PropertyFetcher<Double> fetcher = new TextBodyPropertyFetcher<Double>() {
            @Override
            public boolean fetch(final CTTextBodyProperties props) {
                if (props.isSetBIns()) {
                    final double val = Units.toPoints((long)props.getBIns());
                    this.setValue(val);
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        return (fetcher.getValue() == null) ? 3.6 : fetcher.getValue();
    }
    
    public double getLeftInset() {
        final PropertyFetcher<Double> fetcher = new TextBodyPropertyFetcher<Double>() {
            @Override
            public boolean fetch(final CTTextBodyProperties props) {
                if (props.isSetLIns()) {
                    final double val = Units.toPoints((long)props.getLIns());
                    this.setValue(val);
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        return (fetcher.getValue() == null) ? 7.2 : fetcher.getValue();
    }
    
    public double getRightInset() {
        final PropertyFetcher<Double> fetcher = new TextBodyPropertyFetcher<Double>() {
            @Override
            public boolean fetch(final CTTextBodyProperties props) {
                if (props.isSetRIns()) {
                    final double val = Units.toPoints((long)props.getRIns());
                    this.setValue(val);
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        return (fetcher.getValue() == null) ? 7.2 : fetcher.getValue();
    }
    
    public double getTopInset() {
        final PropertyFetcher<Double> fetcher = new TextBodyPropertyFetcher<Double>() {
            @Override
            public boolean fetch(final CTTextBodyProperties props) {
                if (props.isSetTIns()) {
                    final double val = Units.toPoints((long)props.getTIns());
                    this.setValue(val);
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        return (fetcher.getValue() == null) ? 3.6 : fetcher.getValue();
    }
    
    public void setBottomInset(final double margin) {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr(true);
        if (bodyPr != null) {
            if (margin == -1.0) {
                bodyPr.unsetBIns();
            }
            else {
                bodyPr.setBIns(Units.toEMU(margin));
            }
        }
    }
    
    public void setLeftInset(final double margin) {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr(true);
        if (bodyPr != null) {
            if (margin == -1.0) {
                bodyPr.unsetLIns();
            }
            else {
                bodyPr.setLIns(Units.toEMU(margin));
            }
        }
    }
    
    public void setRightInset(final double margin) {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr(true);
        if (bodyPr != null) {
            if (margin == -1.0) {
                bodyPr.unsetRIns();
            }
            else {
                bodyPr.setRIns(Units.toEMU(margin));
            }
        }
    }
    
    public void setTopInset(final double margin) {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr(true);
        if (bodyPr != null) {
            if (margin == -1.0) {
                bodyPr.unsetTIns();
            }
            else {
                bodyPr.setTIns(Units.toEMU(margin));
            }
        }
    }
    
    public Insets2D getInsets() {
        return new Insets2D(this.getTopInset(), this.getLeftInset(), this.getBottomInset(), this.getRightInset());
    }
    
    public void setInsets(final Insets2D insets) {
        this.setTopInset(insets.top);
        this.setLeftInset(insets.left);
        this.setBottomInset(insets.bottom);
        this.setRightInset(insets.right);
    }
    
    public boolean getWordWrap() {
        final PropertyFetcher<Boolean> fetcher = new TextBodyPropertyFetcher<Boolean>() {
            @Override
            public boolean fetch(final CTTextBodyProperties props) {
                if (props.isSetWrap()) {
                    this.setValue(props.getWrap() == STTextWrappingType.SQUARE);
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        return fetcher.getValue() == null || fetcher.getValue();
    }
    
    public void setWordWrap(final boolean wrap) {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr(true);
        if (bodyPr != null) {
            bodyPr.setWrap(wrap ? STTextWrappingType.SQUARE : STTextWrappingType.NONE);
        }
    }
    
    public void setTextAutofit(final TextShape.TextAutofit value) {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr(true);
        if (bodyPr != null) {
            if (bodyPr.isSetSpAutoFit()) {
                bodyPr.unsetSpAutoFit();
            }
            if (bodyPr.isSetNoAutofit()) {
                bodyPr.unsetNoAutofit();
            }
            if (bodyPr.isSetNormAutofit()) {
                bodyPr.unsetNormAutofit();
            }
            switch (value) {
                case NONE: {
                    bodyPr.addNewNoAutofit();
                    break;
                }
                case NORMAL: {
                    bodyPr.addNewNormAutofit();
                    break;
                }
                case SHAPE: {
                    bodyPr.addNewSpAutoFit();
                    break;
                }
            }
        }
    }
    
    public TextShape.TextAutofit getTextAutofit() {
        final CTTextBodyProperties bodyPr = this.getTextBodyPr();
        if (bodyPr != null) {
            if (bodyPr.isSetNoAutofit()) {
                return TextShape.TextAutofit.NONE;
            }
            if (bodyPr.isSetNormAutofit()) {
                return TextShape.TextAutofit.NORMAL;
            }
            if (bodyPr.isSetSpAutoFit()) {
                return TextShape.TextAutofit.SHAPE;
            }
        }
        return TextShape.TextAutofit.NORMAL;
    }
    
    protected CTTextBodyProperties getTextBodyPr() {
        return this.getTextBodyPr(false);
    }
    
    protected CTTextBodyProperties getTextBodyPr(final boolean create) {
        final CTTextBody textBody = this.getTextBody(create);
        if (textBody == null) {
            return null;
        }
        CTTextBodyProperties textBodyPr = textBody.getBodyPr();
        if (textBodyPr == null && create) {
            textBodyPr = textBody.addNewBodyPr();
        }
        return textBodyPr;
    }
    
    protected abstract CTTextBody getTextBody(final boolean p0);
    
    public void setPlaceholder(final Placeholder placeholder) {
        super.setPlaceholder(placeholder);
    }
    
    public Placeholder getTextType() {
        return this.getPlaceholder();
    }
    
    public double getTextHeight() {
        return this.getTextHeight(null);
    }
    
    public double getTextHeight(final Graphics2D graphics) {
        final DrawFactory drawFact = DrawFactory.getInstance(graphics);
        final DrawTextShape dts = drawFact.getDrawable((TextShape)this);
        return dts.getTextHeight(graphics);
    }
    
    public Rectangle2D resizeToFitText() {
        return this.resizeToFitText(null);
    }
    
    public Rectangle2D resizeToFitText(final Graphics2D graphics) {
        final Rectangle2D anchor = this.getAnchor();
        if (anchor.getWidth() == 0.0) {
            throw new POIXMLException("Anchor of the shape was not set.");
        }
        double height = this.getTextHeight(graphics);
        ++height;
        final Insets2D insets = this.getInsets();
        anchor.setRect(anchor.getX(), anchor.getY(), anchor.getWidth(), height + insets.top + insets.bottom);
        this.setAnchor(anchor);
        return anchor;
    }
    
    @Override
    void copy(final XSLFShape other) {
        super.copy(other);
        final XSLFTextShape otherTS = (XSLFTextShape)other;
        final CTTextBody otherTB = otherTS.getTextBody(false);
        if (otherTB == null) {
            return;
        }
        final CTTextBody thisTB = this.getTextBody(true);
        thisTB.setBodyPr((CTTextBodyProperties)otherTB.getBodyPr().copy());
        if (thisTB.isSetLstStyle()) {
            thisTB.unsetLstStyle();
        }
        if (otherTB.isSetLstStyle()) {
            thisTB.setLstStyle((CTTextListStyle)otherTB.getLstStyle().copy());
        }
        final boolean srcWordWrap = otherTS.getWordWrap();
        if (srcWordWrap != this.getWordWrap()) {
            this.setWordWrap(srcWordWrap);
        }
        final double leftInset = otherTS.getLeftInset();
        if (leftInset != this.getLeftInset()) {
            this.setLeftInset(leftInset);
        }
        final double rightInset = otherTS.getRightInset();
        if (rightInset != this.getRightInset()) {
            this.setRightInset(rightInset);
        }
        final double topInset = otherTS.getTopInset();
        if (topInset != this.getTopInset()) {
            this.setTopInset(topInset);
        }
        final double bottomInset = otherTS.getBottomInset();
        if (bottomInset != this.getBottomInset()) {
            this.setBottomInset(bottomInset);
        }
        final VerticalAlignment vAlign = otherTS.getVerticalAlignment();
        if (vAlign != this.getVerticalAlignment()) {
            this.setVerticalAlignment(vAlign);
        }
        this.clearText();
        for (final XSLFTextParagraph srcP : otherTS.getTextParagraphs()) {
            final XSLFTextParagraph tgtP = this.addNewTextParagraph();
            tgtP.copy(srcP);
        }
    }
    
    public void setTextPlaceholder(final TextShape.TextPlaceholder placeholder) {
        switch (placeholder) {
            default: {
                this.setPlaceholder(Placeholder.BODY);
                break;
            }
            case TITLE: {
                this.setPlaceholder(Placeholder.TITLE);
                break;
            }
            case CENTER_BODY: {
                this.setPlaceholder(Placeholder.BODY);
                this.setHorizontalCentered(true);
                break;
            }
            case CENTER_TITLE: {
                this.setPlaceholder(Placeholder.CENTERED_TITLE);
                break;
            }
            case OTHER: {
                this.setPlaceholder(Placeholder.CONTENT);
                break;
            }
        }
    }
    
    public TextShape.TextPlaceholder getTextPlaceholder() {
        final Placeholder ph = this.getTextType();
        if (ph == null) {
            return TextShape.TextPlaceholder.BODY;
        }
        switch (ph) {
            case BODY: {
                return TextShape.TextPlaceholder.BODY;
            }
            case TITLE: {
                return TextShape.TextPlaceholder.TITLE;
            }
            case CENTERED_TITLE: {
                return TextShape.TextPlaceholder.CENTER_TITLE;
            }
            default: {
                return TextShape.TextPlaceholder.OTHER;
            }
        }
    }
    
    protected XSLFTextParagraph newTextParagraph(final CTTextParagraph p) {
        return new XSLFTextParagraph(p, this);
    }
    
    @Override
    public <R> Optional<R> findDefinedParagraphProperty(final Function<CTTextParagraphProperties, Boolean> isSet, final Function<CTTextParagraphProperties, R> getter) {
        return Optional.empty();
    }
    
    @Override
    public <R> Optional<R> findDefinedRunProperty(final Function<CTTextCharacterProperties, Boolean> isSet, final Function<CTTextCharacterProperties, R> getter) {
        return Optional.empty();
    }
}
