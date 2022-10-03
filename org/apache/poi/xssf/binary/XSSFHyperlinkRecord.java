package org.apache.poi.xssf.binary;

import java.util.Objects;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Internal;

@Internal
public class XSSFHyperlinkRecord
{
    private final CellRangeAddress cellRangeAddress;
    private final String relId;
    private String location;
    private String toolTip;
    private String display;
    
    XSSFHyperlinkRecord(final CellRangeAddress cellRangeAddress, final String relId, final String location, final String toolTip, final String display) {
        this.cellRangeAddress = cellRangeAddress;
        this.relId = relId;
        this.location = location;
        this.toolTip = toolTip;
        this.display = display;
    }
    
    void setLocation(final String location) {
        this.location = location;
    }
    
    void setToolTip(final String toolTip) {
        this.toolTip = toolTip;
    }
    
    void setDisplay(final String display) {
        this.display = display;
    }
    
    CellRangeAddress getCellRangeAddress() {
        return this.cellRangeAddress;
    }
    
    public String getRelId() {
        return this.relId;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public String getToolTip() {
        return this.toolTip;
    }
    
    public String getDisplay() {
        return this.display;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final XSSFHyperlinkRecord that = (XSSFHyperlinkRecord)o;
        Label_0062: {
            if (this.cellRangeAddress != null) {
                if (this.cellRangeAddress.equals((Object)that.cellRangeAddress)) {
                    break Label_0062;
                }
            }
            else if (that.cellRangeAddress == null) {
                break Label_0062;
            }
            return false;
        }
        Label_0095: {
            if (this.relId != null) {
                if (this.relId.equals(that.relId)) {
                    break Label_0095;
                }
            }
            else if (that.relId == null) {
                break Label_0095;
            }
            return false;
        }
        Label_0128: {
            if (this.location != null) {
                if (this.location.equals(that.location)) {
                    break Label_0128;
                }
            }
            else if (that.location == null) {
                break Label_0128;
            }
            return false;
        }
        if (this.toolTip != null) {
            if (this.toolTip.equals(that.toolTip)) {
                return (this.display != null) ? this.display.equals(that.display) : (that.display == null);
            }
        }
        else if (that.toolTip == null) {
            return (this.display != null) ? this.display.equals(that.display) : (that.display == null);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.cellRangeAddress, this.relId, this.location, this.toolTip, this.display);
    }
    
    @Override
    public String toString() {
        return "XSSFHyperlinkRecord{cellRangeAddress=" + this.cellRangeAddress + ", relId='" + this.relId + '\'' + ", location='" + this.location + '\'' + ", toolTip='" + this.toolTip + '\'' + ", display='" + this.display + '\'' + '}';
    }
}
