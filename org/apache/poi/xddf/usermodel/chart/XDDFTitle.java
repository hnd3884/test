package org.apache.poi.xddf.usermodel.chart;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.apache.poi.xddf.usermodel.text.XDDFRunProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTx;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.apache.poi.xddf.usermodel.text.TextContainer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;

public class XDDFTitle
{
    private final CTTitle title;
    private final TextContainer parent;
    
    public XDDFTitle(final TextContainer parent, final CTTitle title) {
        this.parent = parent;
        this.title = title;
    }
    
    public XDDFTextBody getBody() {
        if (!this.title.isSetTx()) {
            this.title.addNewTx();
        }
        final CTTx tx = this.title.getTx();
        if (tx.isSetStrRef()) {
            tx.unsetStrRef();
        }
        if (!tx.isSetRich()) {
            tx.addNewRich();
        }
        return new XDDFTextBody(this.parent, tx.getRich());
    }
    
    public void setText(final String text) {
        if (!this.title.isSetLayout()) {
            this.title.addNewLayout();
        }
        this.getBody().setText(text);
    }
    
    public void setOverlay(final Boolean overlay) {
        if (overlay == null) {
            if (this.title.isSetOverlay()) {
                this.title.unsetOverlay();
            }
        }
        else if (this.title.isSetOverlay()) {
            this.title.getOverlay().setVal((boolean)overlay);
        }
        else {
            this.title.addNewOverlay().setVal((boolean)overlay);
        }
    }
    
    public XDDFRunProperties getOrAddTextProperties() {
        CTTextBody text;
        if (this.title.isSetTxPr()) {
            text = this.title.getTxPr();
        }
        else {
            text = this.title.addNewTxPr();
        }
        return new XDDFRunProperties(this.getOrAddTextProperties(text));
    }
    
    private CTTextCharacterProperties getOrAddTextProperties(final CTTextBody body) {
        if (body.getBodyPr() == null) {
            body.addNewBodyPr();
        }
        CTTextParagraph paragraph;
        if (body.sizeOfPArray() > 0) {
            paragraph = body.getPArray(0);
        }
        else {
            paragraph = body.addNewP();
        }
        CTTextParagraphProperties paraprops;
        if (paragraph.isSetPPr()) {
            paraprops = paragraph.getPPr();
        }
        else {
            paraprops = paragraph.addNewPPr();
        }
        CTTextCharacterProperties properties;
        if (paraprops.isSetDefRPr()) {
            properties = paraprops.getDefRPr();
        }
        else {
            properties = paraprops.addNewDefRPr();
        }
        return properties;
    }
}
