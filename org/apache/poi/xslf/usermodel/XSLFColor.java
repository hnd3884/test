package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.AbstractColorStyle;
import org.apache.poi.util.POILogFactory;
import org.w3c.dom.Node;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveFixedPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import java.util.Iterator;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontReference;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.apache.poi.sl.usermodel.PresetColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHslColor;
import org.apache.poi.sl.usermodel.ColorStyle;
import org.apache.poi.sl.draw.DrawPaint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import java.awt.Color;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
public class XSLFColor
{
    private static final POILogger LOGGER;
    private XmlObject _xmlObject;
    private Color _color;
    private CTSchemeColor _phClr;
    private XSLFSheet _sheet;
    
    public XSLFColor(final XmlObject obj, final XSLFTheme theme, final CTSchemeColor phClr, final XSLFSheet sheet) {
        this._xmlObject = obj;
        this._phClr = phClr;
        this._sheet = sheet;
        this._color = this.toColor(obj, theme);
    }
    
    @Internal
    public XmlObject getXmlObject() {
        return this._xmlObject;
    }
    
    public Color getColor() {
        return DrawPaint.applyColorTransform(this.getColorStyle());
    }
    
    public ColorStyle getColorStyle() {
        return (ColorStyle)new XSLFColorStyle(this._xmlObject, this._color, this._phClr);
    }
    
    private Color toColor(final CTHslColor hsl) {
        return DrawPaint.HSL2RGB(hsl.getHue2() / 60000.0, hsl.getSat2() / 1000.0, hsl.getLum2() / 1000.0, 1.0);
    }
    
    private Color toColor(final CTPresetColor prst) {
        final String colorName = prst.getVal().toString();
        final PresetColor pc = PresetColor.valueOfOoxmlId(colorName);
        return (pc != null) ? pc.color : null;
    }
    
    private Color toColor(final CTSchemeColor schemeColor, final XSLFTheme theme) {
        String colorRef = schemeColor.getVal().toString();
        if (this._phClr != null) {
            colorRef = this._phClr.getVal().toString();
        }
        final CTColor ctColor = (theme == null) ? null : theme.getCTColor(this._sheet.mapSchemeColor(colorRef));
        return (ctColor != null) ? this.toColor((XmlObject)ctColor, null) : null;
    }
    
    private Color toColor(final CTScRgbColor scrgb) {
        return new Color(DrawPaint.lin2srgb(scrgb.getR()), DrawPaint.lin2srgb(scrgb.getG()), DrawPaint.lin2srgb(scrgb.getB()));
    }
    
    private Color toColor(final CTSRgbColor srgb) {
        final byte[] val = srgb.getVal();
        return new Color(0xFF & val[0], 0xFF & val[1], 0xFF & val[2]);
    }
    
    private Color toColor(final CTSystemColor sys) {
        if (sys.isSetLastClr()) {
            final byte[] val = sys.getLastClr();
            return new Color(0xFF & val[0], 0xFF & val[1], 0xFF & val[2]);
        }
        final String colorName = sys.getVal().toString();
        final PresetColor pc = PresetColor.valueOfOoxmlId(colorName);
        return (pc != null && pc.color != null) ? pc.color : Color.black;
    }
    
    private Color toColor(final XmlObject obj, final XSLFTheme theme) {
        Color color = null;
        final List<XmlObject> xo = new ArrayList<XmlObject>();
        xo.add(obj);
        xo.addAll(Arrays.asList(obj.selectPath("*")));
        boolean isFirst = true;
        for (final XmlObject ch : xo) {
            if (ch instanceof CTHslColor) {
                color = this.toColor((CTHslColor)ch);
            }
            else if (ch instanceof CTPresetColor) {
                color = this.toColor((CTPresetColor)ch);
            }
            else if (ch instanceof CTSchemeColor) {
                color = this.toColor((CTSchemeColor)ch, theme);
            }
            else if (ch instanceof CTScRgbColor) {
                color = this.toColor((CTScRgbColor)ch);
            }
            else if (ch instanceof CTSRgbColor) {
                color = this.toColor((CTSRgbColor)ch);
            }
            else if (ch instanceof CTSystemColor) {
                color = this.toColor((CTSystemColor)ch);
            }
            else if (!(ch instanceof CTFontReference) && !isFirst) {
                throw new IllegalArgumentException("Unexpected color choice: " + ch.getClass());
            }
            if (color != null) {
                break;
            }
            isFirst = false;
        }
        return color;
    }
    
    @Internal
    protected void setColor(final Color color) {
        if (!(this._xmlObject instanceof CTSolidColorFillProperties)) {
            XSLFColor.LOGGER.log(7, new Object[] { "XSLFColor.setColor currently only supports CTSolidColorFillProperties" });
            return;
        }
        final CTSolidColorFillProperties fill = (CTSolidColorFillProperties)this._xmlObject;
        if (fill.isSetSrgbClr()) {
            fill.unsetSrgbClr();
        }
        if (fill.isSetScrgbClr()) {
            fill.unsetScrgbClr();
        }
        if (fill.isSetHslClr()) {
            fill.unsetHslClr();
        }
        if (fill.isSetPrstClr()) {
            fill.unsetPrstClr();
        }
        if (fill.isSetSchemeClr()) {
            fill.unsetSchemeClr();
        }
        if (fill.isSetSysClr()) {
            fill.unsetSysClr();
        }
        final float[] rgbaf = color.getRGBComponents(null);
        final boolean addAlpha = rgbaf.length == 4 && rgbaf[3] < 1.0f;
        CTPositiveFixedPercentage alphaPct;
        if (isInt(rgbaf[0]) && isInt(rgbaf[1]) && isInt(rgbaf[2])) {
            final CTSRgbColor rgb = fill.addNewSrgbClr();
            final byte[] rgbBytes = { (byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue() };
            rgb.setVal(rgbBytes);
            alphaPct = (addAlpha ? rgb.addNewAlpha() : null);
        }
        else {
            final CTScRgbColor rgb2 = fill.addNewScrgbClr();
            rgb2.setR(DrawPaint.srgb2lin(rgbaf[0]));
            rgb2.setG(DrawPaint.srgb2lin(rgbaf[1]));
            rgb2.setB(DrawPaint.srgb2lin(rgbaf[2]));
            alphaPct = (addAlpha ? rgb2.addNewAlpha() : null);
        }
        if (alphaPct != null) {
            alphaPct.setVal((int)(100000.0f * rgbaf[3]));
        }
    }
    
    private static boolean isInt(final float f) {
        return Math.abs(f * 255.0 - Math.rint(f * 255.0)) < 1.0E-5;
    }
    
    private static int getRawValue(final CTSchemeColor phClr, final XmlObject xmlObject, final String elem) {
        final String query = "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' $this//a:" + elem;
        if (phClr != null) {
            final XmlObject[] obj = phClr.selectPath(query);
            if (obj.length == 1) {
                final Node attr = obj[0].getDomNode().getAttributes().getNamedItem("val");
                if (attr != null) {
                    return Integer.parseInt(attr.getNodeValue());
                }
            }
        }
        final XmlObject[] obj = xmlObject.selectPath(query);
        if (obj.length == 1) {
            final Node attr = obj[0].getDomNode().getAttributes().getNamedItem("val");
            if (attr != null) {
                return Integer.parseInt(attr.getNodeValue());
            }
        }
        return -1;
    }
    
    private int getPercentageValue(final String elem) {
        final int val = getRawValue(this._phClr, this._xmlObject, elem);
        return (val == -1) ? val : (val / 1000);
    }
    
    int getAlpha() {
        return this.getPercentageValue("alpha");
    }
    
    int getAlphaMod() {
        return this.getPercentageValue("alphaMod");
    }
    
    int getAlphaOff() {
        return this.getPercentageValue("alphaOff");
    }
    
    int getHue() {
        final int val = getRawValue(this._phClr, this._xmlObject, "hue");
        return (val == -1) ? val : (val / 60000);
    }
    
    int getHueMod() {
        return this.getPercentageValue("hueMod");
    }
    
    int getHueOff() {
        return this.getPercentageValue("hueOff");
    }
    
    int getLum() {
        return this.getPercentageValue("lum");
    }
    
    int getLumMod() {
        return this.getPercentageValue("lumMod");
    }
    
    int getLumOff() {
        return this.getPercentageValue("lumOff");
    }
    
    int getSat() {
        return this.getPercentageValue("sat");
    }
    
    int getSatMod() {
        return this.getPercentageValue("satMod");
    }
    
    int getSatOff() {
        return this.getPercentageValue("satOff");
    }
    
    int getRed() {
        return this.getPercentageValue("red");
    }
    
    int getRedMod() {
        return this.getPercentageValue("redMod");
    }
    
    int getRedOff() {
        return this.getPercentageValue("redOff");
    }
    
    int getGreen() {
        return this.getPercentageValue("green");
    }
    
    int getGreenMod() {
        return this.getPercentageValue("greenMod");
    }
    
    int getGreenOff() {
        return this.getPercentageValue("greenOff");
    }
    
    int getBlue() {
        return this.getPercentageValue("blue");
    }
    
    int getBlueMod() {
        return this.getPercentageValue("blueMod");
    }
    
    int getBlueOff() {
        return this.getPercentageValue("blueOff");
    }
    
    public int getShade() {
        return this.getPercentageValue("shade");
    }
    
    public int getTint() {
        return this.getPercentageValue("tint");
    }
    
    static {
        LOGGER = POILogFactory.getLogger((Class)XSLFColor.class);
    }
    
    private static class XSLFColorStyle extends AbstractColorStyle
    {
        private XmlObject xmlObject;
        private Color color;
        private CTSchemeColor phClr;
        
        XSLFColorStyle(final XmlObject xmlObject, final Color color, final CTSchemeColor phClr) {
            this.xmlObject = xmlObject;
            this.color = color;
            this.phClr = phClr;
        }
        
        public Color getColor() {
            return this.color;
        }
        
        public int getAlpha() {
            return getRawValue(this.phClr, this.xmlObject, "alpha");
        }
        
        public int getHueOff() {
            return getRawValue(this.phClr, this.xmlObject, "hueOff");
        }
        
        public int getHueMod() {
            return getRawValue(this.phClr, this.xmlObject, "hueMod");
        }
        
        public int getSatOff() {
            return getRawValue(this.phClr, this.xmlObject, "satOff");
        }
        
        public int getSatMod() {
            return getRawValue(this.phClr, this.xmlObject, "satMod");
        }
        
        public int getLumOff() {
            return getRawValue(this.phClr, this.xmlObject, "lumOff");
        }
        
        public int getLumMod() {
            return getRawValue(this.phClr, this.xmlObject, "lumMod");
        }
        
        public int getShade() {
            return getRawValue(this.phClr, this.xmlObject, "shade");
        }
        
        public int getTint() {
            return getRawValue(this.phClr, this.xmlObject, "tint");
        }
    }
}
