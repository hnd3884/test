package org.apache.poi.xdgf.usermodel.section;

import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.util.Internal;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import java.util.HashMap;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import java.util.Map;
import org.apache.poi.xdgf.usermodel.XDGFSheet;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import org.apache.poi.xdgf.util.ObjectFactory;

public abstract class XDGFSection
{
    static final ObjectFactory<XDGFSection, SectionType> _sectionTypes;
    protected SectionType _section;
    protected XDGFSheet _containingSheet;
    protected Map<String, XDGFCell> _cells;
    
    public static XDGFSection load(final SectionType section, final XDGFSheet containingSheet) {
        return XDGFSection._sectionTypes.load(section.getN(), section, containingSheet);
    }
    
    public XDGFSection(final SectionType section, final XDGFSheet containingSheet) {
        this._cells = new HashMap<String, XDGFCell>();
        this._section = section;
        this._containingSheet = containingSheet;
        for (final CellType cell : section.getCellArray()) {
            this._cells.put(cell.getN(), new XDGFCell(cell));
        }
    }
    
    @Internal
    public SectionType getXmlObject() {
        return this._section;
    }
    
    @Override
    public String toString() {
        return "<Section type=" + this._section.getN() + " from " + this._containingSheet + ">";
    }
    
    public abstract void setupMaster(final XDGFSection p0);
    
    static {
        _sectionTypes = new ObjectFactory<XDGFSection, SectionType>();
        try {
            XDGFSection._sectionTypes.put("LineGradient", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("FillGradient", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Character", CharacterSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Paragraph", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Tabs", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Scratch", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Connection", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("ConnectionABCD", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Field", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Control", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Geometry", GeometrySection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Actions", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Layer", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("User", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Property", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Hyperlink", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Reviewer", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("Annotation", GenericSection.class, SectionType.class, XDGFSheet.class);
            XDGFSection._sectionTypes.put("ActionTag", GenericSection.class, SectionType.class, XDGFSheet.class);
        }
        catch (final NoSuchMethodException | SecurityException e) {
            throw new POIXMLException("Internal error");
        }
    }
}
