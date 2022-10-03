package com.adventnet.client.components.filter.web;

import com.adventnet.persistence.Row;
import javax.swing.table.TableModel;
import com.adventnet.client.util.web.WebConstants;

public class FilterModel implements WebConstants
{
    private boolean newGrp;
    private int grpRow;
    private int curRow;
    private long accountId;
    private TableModel tblModel;
    private String selectedFilter;
    private int selectedFilterRow;
    private Long listId;
    
    public FilterModel(final TableModel tableModel, final long accountID, final Long filterListId) {
        this.newGrp = false;
        this.grpRow = 0;
        this.curRow = -1;
        this.selectedFilterRow = -1;
        this.tblModel = tableModel;
        this.accountId = accountID;
        this.listId = filterListId;
    }
    
    public void setSelectedFilter(final String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }
    
    public boolean isNewGroup() {
        return this.newGrp;
    }
    
    public String getGroup() {
        return (String)this.tblModel.getValueAt(this.curRow, 0);
    }
    
    public String getGroupTitle() {
        return (String)this.tblModel.getValueAt(this.curRow, 1);
    }
    
    public String getFilterName() {
        return (String)this.tblModel.getValueAt(this.curRow, 3);
    }
    
    public String getFilterTitle() {
        return (String)this.tblModel.getValueAt(this.curRow, 4);
    }
    
    public boolean next() {
        while (this.tblModel.getRowCount() > ++this.curRow && !this.canShowFilter(this.curRow)) {}
        if (this.tblModel.getRowCount() <= this.curRow) {
            return false;
        }
        if (this.curRow == 0) {
            this.newGrp = true;
        }
        else {
            this.newGrp = ((int)this.tblModel.getValueAt(this.curRow, 2) != (int)this.tblModel.getValueAt(this.grpRow, 2));
            if (this.newGrp) {
                this.grpRow = this.curRow;
            }
        }
        return true;
    }
    
    protected boolean canShowFilter(final int row) {
        final Long createdBy = (Long)this.tblModel.getValueAt(row, 6);
        return createdBy == null || createdBy == this.accountId;
    }
    
    public boolean isSelected() {
        if (this.tblModel.getValueAt(this.curRow, 3).equals(this.selectedFilter)) {
            this.selectedFilterRow = this.curRow;
            return true;
        }
        return false;
    }
    
    private int getSelectedFilterRow() {
        if (this.selectedFilterRow > -1) {
            return this.selectedFilterRow;
        }
        if (this.selectedFilter == null) {
            throw new IllegalStateException("Selected Filter is not set");
        }
        if (this.selectedFilterRow == -1) {
            for (int i = 0; i < this.tblModel.getRowCount(); ++i) {
                if (this.tblModel.getValueAt(i, 3).equals(this.selectedFilter)) {
                    return this.selectedFilterRow = i;
                }
            }
        }
        throw new IllegalStateException("Selected Filter is not present in the filter model");
    }
    
    public boolean isEditable() {
        final Long createdBy = (Long)this.tblModel.getValueAt(this.getSelectedFilterRow(), 6);
        return createdBy != null && createdBy == this.accountId;
    }
    
    public boolean isDeleteable() {
        final Long createdBy = (Long)this.tblModel.getValueAt(this.getSelectedFilterRow(), 6);
        return createdBy != null && createdBy == this.accountId;
    }
    
    public Long getListId() {
        return this.listId;
    }
    
    public String getSelectedFilter() {
        return this.selectedFilter;
    }
    
    public Row getEmptyTableMessageRow() {
        final Object empty_message_id = this.tblModel.getValueAt(this.getSelectedFilterRow(), 8);
        if (empty_message_id != null) {
            try {
                final Row emptyTableMessageRow = new Row("EmptyTableMessage");
                emptyTableMessageRow.set("EMPTY_MESSAGE_ID", empty_message_id);
                emptyTableMessageRow.set("ICON_URL", this.tblModel.getValueAt(this.getSelectedFilterRow(), 9));
                emptyTableMessageRow.set("TITLE_TEXT", this.tblModel.getValueAt(this.getSelectedFilterRow(), 10));
                emptyTableMessageRow.set("MESSAGE_TEXT", this.tblModel.getValueAt(this.getSelectedFilterRow(), 11));
                emptyTableMessageRow.set("MENU_ID", this.tblModel.getValueAt(this.getSelectedFilterRow(), 12));
                return emptyTableMessageRow;
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public String getMenuNameForEmptyTableMessage() {
        return (String)this.tblModel.getValueAt(this.getSelectedFilterRow(), 13);
    }
}
