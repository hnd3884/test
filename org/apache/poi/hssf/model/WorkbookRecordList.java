package org.apache.poi.hssf.model;

import java.util.Iterator;
import java.util.ArrayList;
import org.apache.poi.hssf.record.Record;
import java.util.List;

public final class WorkbookRecordList
{
    private List<Record> records;
    private int protpos;
    private int bspos;
    private int tabpos;
    private int fontpos;
    private int xfpos;
    private int backuppos;
    private int namepos;
    private int supbookpos;
    private int externsheetPos;
    private int palettepos;
    
    public WorkbookRecordList() {
        this.records = new ArrayList<Record>();
        this.palettepos = -1;
    }
    
    public void setRecords(final List<Record> records) {
        this.records = records;
    }
    
    public int size() {
        return this.records.size();
    }
    
    public Record get(final int i) {
        return this.records.get(i);
    }
    
    public void add(final int pos, final Record r) {
        this.records.add(pos, r);
        this.updateRecordPos(pos, true);
    }
    
    public List<Record> getRecords() {
        return this.records;
    }
    
    public void remove(final Object record) {
        int i = 0;
        for (final Record r : this.records) {
            if (r == record) {
                this.remove(i);
                break;
            }
            ++i;
        }
    }
    
    public void remove(final int pos) {
        this.records.remove(pos);
        this.updateRecordPos(pos, false);
    }
    
    public int getProtpos() {
        return this.protpos;
    }
    
    public void setProtpos(final int protpos) {
        this.protpos = protpos;
    }
    
    public int getBspos() {
        return this.bspos;
    }
    
    public void setBspos(final int bspos) {
        this.bspos = bspos;
    }
    
    public int getTabpos() {
        return this.tabpos;
    }
    
    public void setTabpos(final int tabpos) {
        this.tabpos = tabpos;
    }
    
    public int getFontpos() {
        return this.fontpos;
    }
    
    public void setFontpos(final int fontpos) {
        this.fontpos = fontpos;
    }
    
    public int getXfpos() {
        return this.xfpos;
    }
    
    public void setXfpos(final int xfpos) {
        this.xfpos = xfpos;
    }
    
    public int getBackuppos() {
        return this.backuppos;
    }
    
    public void setBackuppos(final int backuppos) {
        this.backuppos = backuppos;
    }
    
    public int getPalettepos() {
        return this.palettepos;
    }
    
    public void setPalettepos(final int palettepos) {
        this.palettepos = palettepos;
    }
    
    public int getNamepos() {
        return this.namepos;
    }
    
    public int getSupbookpos() {
        return this.supbookpos;
    }
    
    public void setNamepos(final int namepos) {
        this.namepos = namepos;
    }
    
    public void setSupbookpos(final int supbookpos) {
        this.supbookpos = supbookpos;
    }
    
    public int getExternsheetPos() {
        return this.externsheetPos;
    }
    
    public void setExternsheetPos(final int externsheetPos) {
        this.externsheetPos = externsheetPos;
    }
    
    private void updateRecordPos(final int pos, final boolean add) {
        final int delta = add ? 1 : -1;
        int p = this.getProtpos();
        if (p >= pos) {
            this.setProtpos(p + delta);
        }
        p = this.getBspos();
        if (p >= pos) {
            this.setBspos(p + delta);
        }
        p = this.getTabpos();
        if (p >= pos) {
            this.setTabpos(p + delta);
        }
        p = this.getFontpos();
        if (p >= pos) {
            this.setFontpos(p + delta);
        }
        p = this.getXfpos();
        if (p >= pos) {
            this.setXfpos(p + delta);
        }
        p = this.getBackuppos();
        if (p >= pos) {
            this.setBackuppos(p + delta);
        }
        p = this.getNamepos();
        if (p >= pos) {
            this.setNamepos(p + delta);
        }
        p = this.getSupbookpos();
        if (p >= pos) {
            this.setSupbookpos(p + delta);
        }
        p = this.getPalettepos();
        if (p != -1 && p >= pos) {
            this.setPalettepos(p + delta);
        }
        p = this.getExternsheetPos();
        if (p >= pos) {
            this.setExternsheetPos(p + delta);
        }
    }
}
