package org.apache.poi.hssf.usermodel;

import org.apache.poi.util.Removal;
import org.apache.poi.util.Internal;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.ss.usermodel.Hyperlink;

public class HSSFHyperlink implements Hyperlink
{
    protected final HyperlinkRecord record;
    protected final HyperlinkType link_type;
    
    @Internal(since = "3.15 beta 3")
    protected HSSFHyperlink(final HyperlinkType type) {
        this.link_type = type;
        this.record = new HyperlinkRecord();
        switch (type) {
            case URL:
            case EMAIL: {
                this.record.newUrlLink();
                break;
            }
            case FILE: {
                this.record.newFileLink();
                break;
            }
            case DOCUMENT: {
                this.record.newDocumentLink();
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid type: " + type);
            }
        }
    }
    
    protected HSSFHyperlink(final HyperlinkRecord record) {
        this.record = record;
        this.link_type = getType(record);
    }
    
    private static HyperlinkType getType(final HyperlinkRecord record) {
        HyperlinkType link_type;
        if (record.isFileLink()) {
            link_type = HyperlinkType.FILE;
        }
        else if (record.isDocumentLink()) {
            link_type = HyperlinkType.DOCUMENT;
        }
        else if (record.getAddress() != null && record.getAddress().startsWith("mailto:")) {
            link_type = HyperlinkType.EMAIL;
        }
        else {
            link_type = HyperlinkType.URL;
        }
        return link_type;
    }
    
    protected HSSFHyperlink(final Hyperlink other) {
        if (other instanceof HSSFHyperlink) {
            final HSSFHyperlink hlink = (HSSFHyperlink)other;
            this.record = hlink.record.copy();
            this.link_type = getType(this.record);
        }
        else {
            this.link_type = other.getType();
            this.record = new HyperlinkRecord();
            this.setFirstRow(other.getFirstRow());
            this.setFirstColumn(other.getFirstColumn());
            this.setLastRow(other.getLastRow());
            this.setLastColumn(other.getLastColumn());
        }
    }
    
    @Override
    public int getFirstRow() {
        return this.record.getFirstRow();
    }
    
    @Override
    public void setFirstRow(final int row) {
        this.record.setFirstRow(row);
    }
    
    @Override
    public int getLastRow() {
        return this.record.getLastRow();
    }
    
    @Override
    public void setLastRow(final int row) {
        this.record.setLastRow(row);
    }
    
    @Override
    public int getFirstColumn() {
        return this.record.getFirstColumn();
    }
    
    @Override
    public void setFirstColumn(final int col) {
        this.record.setFirstColumn((short)col);
    }
    
    @Override
    public int getLastColumn() {
        return this.record.getLastColumn();
    }
    
    @Override
    public void setLastColumn(final int col) {
        this.record.setLastColumn((short)col);
    }
    
    @Override
    public String getAddress() {
        return this.record.getAddress();
    }
    
    public String getTextMark() {
        return this.record.getTextMark();
    }
    
    public void setTextMark(final String textMark) {
        this.record.setTextMark(textMark);
    }
    
    public String getShortFilename() {
        return this.record.getShortFilename();
    }
    
    public void setShortFilename(final String shortFilename) {
        this.record.setShortFilename(shortFilename);
    }
    
    @Override
    public void setAddress(final String address) {
        this.record.setAddress(address);
    }
    
    @Override
    public String getLabel() {
        return this.record.getLabel();
    }
    
    @Override
    public void setLabel(final String label) {
        this.record.setLabel(label);
    }
    
    @Override
    public HyperlinkType getType() {
        return this.link_type;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    @Override
    public HyperlinkType getTypeEnum() {
        return this.getType();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HSSFHyperlink)) {
            return false;
        }
        final HSSFHyperlink otherLink = (HSSFHyperlink)other;
        return this.record == otherLink.record;
    }
    
    @Override
    public int hashCode() {
        return this.record.hashCode();
    }
}
