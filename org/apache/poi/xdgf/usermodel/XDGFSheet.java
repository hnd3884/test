package org.apache.poi.xdgf.usermodel;

import java.awt.Color;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.xdgf.exceptions.XDGFException;
import org.apache.poi.ooxml.POIXMLException;
import java.util.TreeMap;
import java.util.HashMap;
import org.apache.poi.xdgf.usermodel.section.CharacterSection;
import org.apache.poi.xdgf.usermodel.section.GeometrySection;
import java.util.SortedMap;
import org.apache.poi.xdgf.usermodel.section.XDGFSection;
import java.util.Map;
import com.microsoft.schemas.office.visio.x2012.main.SheetType;

public abstract class XDGFSheet
{
    protected XDGFDocument _document;
    protected SheetType _sheet;
    protected Map<String, XDGFCell> _cells;
    protected Map<String, XDGFSection> _sections;
    protected SortedMap<Long, GeometrySection> _geometry;
    protected CharacterSection _character;
    
    public XDGFSheet(final SheetType sheet, final XDGFDocument document) {
        this._cells = new HashMap<String, XDGFCell>();
        this._sections = new HashMap<String, XDGFSection>();
        this._geometry = new TreeMap<Long, GeometrySection>();
        try {
            this._sheet = sheet;
            this._document = document;
            for (final CellType cell : sheet.getCellArray()) {
                if (this._cells.containsKey(cell.getN())) {
                    throw new POIXMLException("Unexpected duplicate cell " + cell.getN());
                }
                this._cells.put(cell.getN(), new XDGFCell(cell));
            }
            for (final SectionType section : sheet.getSectionArray()) {
                final String name = section.getN();
                if (name.equals("Geometry")) {
                    this._geometry.put(section.getIX(), new GeometrySection(section, this));
                }
                else if (name.equals("Character")) {
                    this._character = new CharacterSection(section, this);
                }
                else {
                    this._sections.put(name, XDGFSection.load(section, this));
                }
            }
        }
        catch (final POIXMLException e) {
            throw XDGFException.wrap(this.toString(), e);
        }
    }
    
    abstract SheetType getXmlObject();
    
    public XDGFDocument getDocument() {
        return this._document;
    }
    
    public XDGFCell getCell(final String cellName) {
        return this._cells.get(cellName);
    }
    
    public XDGFSection getSection(final String sectionName) {
        return this._sections.get(sectionName);
    }
    
    public XDGFStyleSheet getLineStyle() {
        if (!this._sheet.isSetLineStyle()) {
            return null;
        }
        return this._document.getStyleById(this._sheet.getLineStyle());
    }
    
    public XDGFStyleSheet getFillStyle() {
        if (!this._sheet.isSetFillStyle()) {
            return null;
        }
        return this._document.getStyleById(this._sheet.getFillStyle());
    }
    
    public XDGFStyleSheet getTextStyle() {
        if (!this._sheet.isSetTextStyle()) {
            return null;
        }
        return this._document.getStyleById(this._sheet.getTextStyle());
    }
    
    public Color getFontColor() {
        if (this._character != null) {
            final Color fontColor = this._character.getFontColor();
            if (fontColor != null) {
                return fontColor;
            }
        }
        final XDGFStyleSheet style = this.getTextStyle();
        if (style != null) {
            return style.getFontColor();
        }
        return null;
    }
    
    public Double getFontSize() {
        if (this._character != null) {
            final Double fontSize = this._character.getFontSize();
            if (fontSize != null) {
                return fontSize;
            }
        }
        final XDGFStyleSheet style = this.getTextStyle();
        if (style != null) {
            return style.getFontSize();
        }
        return null;
    }
    
    public Integer getLineCap() {
        final Integer lineCap = XDGFCell.maybeGetInteger(this._cells, "LineCap");
        if (lineCap != null) {
            return lineCap;
        }
        final XDGFStyleSheet style = this.getLineStyle();
        if (style != null) {
            return style.getLineCap();
        }
        return null;
    }
    
    public Color getLineColor() {
        final String lineColor = XDGFCell.maybeGetString(this._cells, "LineColor");
        if (lineColor != null) {
            return Color.decode(lineColor);
        }
        final XDGFStyleSheet style = this.getLineStyle();
        if (style != null) {
            return style.getLineColor();
        }
        return null;
    }
    
    public Integer getLinePattern() {
        final Integer linePattern = XDGFCell.maybeGetInteger(this._cells, "LinePattern");
        if (linePattern != null) {
            return linePattern;
        }
        final XDGFStyleSheet style = this.getLineStyle();
        if (style != null) {
            return style.getLinePattern();
        }
        return null;
    }
    
    public Double getLineWeight() {
        final Double lineWeight = XDGFCell.maybeGetDouble(this._cells, "LineWeight");
        if (lineWeight != null) {
            return lineWeight;
        }
        final XDGFStyleSheet style = this.getLineStyle();
        if (style != null) {
            return style.getLineWeight();
        }
        return null;
    }
}
