package org.apache.poi.xssf.usermodel;

import java.net.URISyntaxException;
import org.apache.poi.util.Removal;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.util.CellReference;
import java.net.URI;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlink;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Hyperlink;

public class XSSFHyperlink implements Hyperlink
{
    private final HyperlinkType _type;
    private final PackageRelationship _externalRel;
    private final CTHyperlink _ctHyperlink;
    private String _location;
    
    protected XSSFHyperlink(final HyperlinkType type) {
        this._type = type;
        this._ctHyperlink = CTHyperlink.Factory.newInstance();
        this._externalRel = null;
    }
    
    protected XSSFHyperlink(final CTHyperlink ctHyperlink, final PackageRelationship hyperlinkRel) {
        this._ctHyperlink = ctHyperlink;
        this._externalRel = hyperlinkRel;
        if (this._externalRel == null) {
            if (ctHyperlink.getLocation() != null) {
                this._type = HyperlinkType.DOCUMENT;
                this._location = ctHyperlink.getLocation();
            }
            else {
                if (ctHyperlink.getId() != null) {
                    throw new IllegalStateException("The hyperlink for cell " + ctHyperlink.getRef() + " references relation " + ctHyperlink.getId() + ", but that didn't exist!");
                }
                this._type = HyperlinkType.DOCUMENT;
            }
        }
        else {
            final URI target = this._externalRel.getTargetURI();
            this._location = target.toString();
            if (ctHyperlink.getLocation() != null) {
                this._location = this._location + "#" + ctHyperlink.getLocation();
            }
            if (this._location.startsWith("http://") || this._location.startsWith("https://") || this._location.startsWith("ftp://")) {
                this._type = HyperlinkType.URL;
            }
            else if (this._location.startsWith("mailto:")) {
                this._type = HyperlinkType.EMAIL;
            }
            else {
                this._type = HyperlinkType.FILE;
            }
        }
    }
    
    @Internal
    public XSSFHyperlink(final Hyperlink other) {
        if (other instanceof XSSFHyperlink) {
            final XSSFHyperlink xlink = (XSSFHyperlink)other;
            this._type = xlink.getType();
            this._location = xlink._location;
            this._externalRel = xlink._externalRel;
            this._ctHyperlink = (CTHyperlink)xlink._ctHyperlink.copy();
        }
        else {
            this._type = other.getType();
            this._location = other.getAddress();
            this._externalRel = null;
            this._ctHyperlink = CTHyperlink.Factory.newInstance();
            this.setCellReference(new CellReference(other.getFirstRow(), other.getFirstColumn()));
        }
    }
    
    @Internal
    public CTHyperlink getCTHyperlink() {
        return this._ctHyperlink;
    }
    
    public boolean needsRelationToo() {
        return this._type != HyperlinkType.DOCUMENT;
    }
    
    protected void generateRelationIfNeeded(final PackagePart sheetPart) {
        if (this._externalRel == null && this.needsRelationToo()) {
            final PackageRelationship rel = sheetPart.addExternalRelationship(this._location, XSSFRelation.SHEET_HYPERLINKS.getRelation());
            this._ctHyperlink.setId(rel.getId());
        }
    }
    
    public HyperlinkType getType() {
        return this._type;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public HyperlinkType getTypeEnum() {
        return this.getType();
    }
    
    public String getCellRef() {
        return this._ctHyperlink.getRef();
    }
    
    public String getAddress() {
        return this._location;
    }
    
    public String getLabel() {
        return this._ctHyperlink.getDisplay();
    }
    
    public String getLocation() {
        return this._ctHyperlink.getLocation();
    }
    
    public void setLabel(final String label) {
        this._ctHyperlink.setDisplay(label);
    }
    
    public void setLocation(final String location) {
        this._ctHyperlink.setLocation(location);
    }
    
    public void setAddress(final String address) {
        this.validate(address);
        this._location = address;
        if (this._type == HyperlinkType.DOCUMENT) {
            this.setLocation(address);
        }
    }
    
    private void validate(final String address) {
        switch (this._type) {
            case EMAIL:
            case FILE:
            case URL: {
                try {
                    new URI(address);
                }
                catch (final URISyntaxException e) {
                    throw new IllegalArgumentException("Address of hyperlink must be a valid URI", e);
                }
            }
            case DOCUMENT: {
                break;
            }
            default: {
                throw new IllegalStateException("Invalid Hyperlink type: " + this._type);
            }
        }
    }
    
    @Internal
    public void setCellReference(final String ref) {
        this._ctHyperlink.setRef(ref);
    }
    
    @Internal
    public void setCellReference(final CellReference ref) {
        this.setCellReference(ref.formatAsString());
    }
    
    private CellReference buildCellReference() {
        String ref = this._ctHyperlink.getRef();
        if (ref == null) {
            ref = "A1";
        }
        return new CellReference(ref);
    }
    
    public int getFirstColumn() {
        return this.buildCellReference().getCol();
    }
    
    public int getLastColumn() {
        return this.buildCellReference().getCol();
    }
    
    public int getFirstRow() {
        return this.buildCellReference().getRow();
    }
    
    public int getLastRow() {
        return this.buildCellReference().getRow();
    }
    
    public void setFirstColumn(final int col) {
        this.setCellReference(new CellReference(this.getFirstRow(), col));
    }
    
    public void setLastColumn(final int col) {
        this.setFirstColumn(col);
    }
    
    public void setFirstRow(final int row) {
        this.setCellReference(new CellReference(row, this.getFirstColumn()));
    }
    
    public void setLastRow(final int row) {
        this.setFirstRow(row);
    }
    
    public String getTooltip() {
        return this._ctHyperlink.getTooltip();
    }
    
    public void setTooltip(final String text) {
        this._ctHyperlink.setTooltip(text);
    }
}
