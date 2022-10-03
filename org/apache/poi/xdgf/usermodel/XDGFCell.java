package org.apache.poi.xdgf.usermodel;

import org.apache.poi.util.Internal;
import org.apache.poi.ooxml.POIXMLException;
import java.util.Map;
import com.microsoft.schemas.office.visio.x2012.main.CellType;

public class XDGFCell
{
    CellType _cell;
    
    public static Boolean maybeGetBoolean(final Map<String, XDGFCell> cells, final String name) {
        final XDGFCell cell = cells.get(name);
        if (cell == null) {
            return null;
        }
        if (cell.getValue().equals("0")) {
            return false;
        }
        if (cell.getValue().equals("1")) {
            return true;
        }
        throw new POIXMLException("Invalid boolean value for '" + cell.getName() + "'");
    }
    
    public static Double maybeGetDouble(final Map<String, XDGFCell> cells, final String name) {
        final XDGFCell cell = cells.get(name);
        if (cell != null) {
            return parseDoubleValue(cell._cell);
        }
        return null;
    }
    
    public static Integer maybeGetInteger(final Map<String, XDGFCell> cells, final String name) {
        final XDGFCell cell = cells.get(name);
        if (cell != null) {
            return parseIntegerValue(cell._cell);
        }
        return null;
    }
    
    public static String maybeGetString(final Map<String, XDGFCell> cells, final String name) {
        final XDGFCell cell = cells.get(name);
        if (cell == null) {
            return null;
        }
        final String v = cell._cell.getV();
        if (v.equals("Themed")) {
            return null;
        }
        return v;
    }
    
    public static Double parseDoubleValue(final CellType cell) {
        try {
            return Double.parseDouble(cell.getV());
        }
        catch (final NumberFormatException e) {
            if (cell.getV().equals("Themed")) {
                return null;
            }
            throw new POIXMLException("Invalid float value for '" + cell.getN() + "': " + e);
        }
    }
    
    public static Integer parseIntegerValue(final CellType cell) {
        try {
            return Integer.parseInt(cell.getV());
        }
        catch (final NumberFormatException e) {
            if (cell.getV().equals("Themed")) {
                return null;
            }
            throw new POIXMLException("Invalid integer value for '" + cell.getN() + "': " + e);
        }
    }
    
    public static Double parseVLength(final CellType cell) {
        try {
            return Double.parseDouble(cell.getV());
        }
        catch (final NumberFormatException e) {
            if (cell.getV().equals("Themed")) {
                return null;
            }
            throw new POIXMLException("Invalid float value for '" + cell.getN() + "': " + e);
        }
    }
    
    public XDGFCell(final CellType cell) {
        this._cell = cell;
    }
    
    @Internal
    protected CellType getXmlObject() {
        return this._cell;
    }
    
    public String getName() {
        return this._cell.getN();
    }
    
    public String getValue() {
        return this._cell.getV();
    }
    
    public String getFormula() {
        return this._cell.getF();
    }
    
    public String getError() {
        return this._cell.getE();
    }
}
