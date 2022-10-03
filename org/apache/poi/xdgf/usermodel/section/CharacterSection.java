package org.apache.poi.xdgf.usermodel.section;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import java.util.HashMap;
import org.apache.poi.xdgf.usermodel.XDGFSheet;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import java.util.Map;
import java.awt.Color;

public class CharacterSection extends XDGFSection
{
    Double _fontSize;
    Color _fontColor;
    Map<String, XDGFCell> _characterCells;
    
    public CharacterSection(final SectionType section, final XDGFSheet containingSheet) {
        super(section, containingSheet);
        this._characterCells = new HashMap<String, XDGFCell>();
        final RowType row = section.getRowArray(0);
        for (final CellType cell : row.getCellArray()) {
            this._characterCells.put(cell.getN(), new XDGFCell(cell));
        }
        this._fontSize = XDGFCell.maybeGetDouble(this._characterCells, "Size");
        final String tmpColor = XDGFCell.maybeGetString(this._characterCells, "Color");
        if (tmpColor != null) {
            this._fontColor = Color.decode(tmpColor);
        }
    }
    
    public Double getFontSize() {
        return this._fontSize;
    }
    
    public Color getFontColor() {
        return this._fontColor;
    }
    
    @Override
    public void setupMaster(final XDGFSection section) {
    }
}
