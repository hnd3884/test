package com.adventnet.client.components.table;

import java.util.function.ToIntFunction;
import java.util.Arrays;
import com.adventnet.client.view.Setter;
import com.adventnet.client.view.Getter;
import org.json.JSONObject;
import com.adventnet.client.view.State;

public class TableViewState implements State
{
    private int fromIndex;
    private int pageLength;
    private int toIndex;
    private int pageNumber;
    private String sortBy;
    private boolean sortOrder;
    private String modifiedParam;
    private long total;
    private String[] searchColumns;
    private String[] searchValues;
    private int[] searchComparators;
    private String[] selectedRowIndices;
    private String[] visibleColumnArray;
    private JSONObject columnAliasVSColumnWidth;
    private String selectedFilter;
    private boolean fetchPrevPage;
    
    public TableViewState() {
        this.fromIndex = Integer.MIN_VALUE;
        this.pageLength = Integer.MIN_VALUE;
        this.toIndex = Integer.MIN_VALUE;
        this.pageNumber = Integer.MIN_VALUE;
        this.sortBy = null;
        this.sortOrder = true;
        this.modifiedParam = null;
        this.total = Long.MIN_VALUE;
        this.searchColumns = new String[0];
        this.searchValues = new String[0];
        this.searchComparators = new int[0];
        this.selectedRowIndices = new String[0];
        this.visibleColumnArray = null;
        this.selectedFilter = null;
        this.fetchPrevPage = false;
    }
    
    @Getter(paramName = "_SB")
    public String getSortBy() {
        return this.sortBy;
    }
    
    @Setter(paramName = "_SB")
    public void setSortBy(final String sortBy) {
        this.sortBy = sortBy;
    }
    
    @Getter(paramName = "_SO")
    public boolean getSortOrder() {
        return this.sortOrder;
    }
    
    @Setter(paramName = "_SO")
    private void setSortOrder(final String sortOrder) {
        this.sortOrder = "A".equals(sortOrder);
    }
    
    public void setSortOrder(final boolean sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    @Getter(paramName = "_RS")
    public String[] getSelectedRowIndices() {
        return this.selectedRowIndices;
    }
    
    @Setter(paramName = "_RS")
    private void setSelectedRowIndices(final String selectedRowIndices) {
        this.selectedRowIndices = selectedRowIndices.split(",");
    }
    
    public void setSelectedRowIndices(final String[] selectedRowIndices) {
        this.selectedRowIndices = selectedRowIndices;
    }
    
    @Getter(paramName = "_FI")
    public int getFromIndex() {
        return this.fromIndex;
    }
    
    @Setter(paramName = "_FI")
    private void setFromIndex(final String fromIndex) {
        this.fromIndex = Integer.parseInt(fromIndex);
    }
    
    public void setFromIndex(final int fromIndex) {
        this.fromIndex = fromIndex;
    }
    
    @Getter(paramName = "_PL")
    public int getPageLength() {
        return this.pageLength;
    }
    
    @Setter(paramName = "_PL")
    private void setPageLength(final String pageLength) {
        this.pageLength = Integer.parseInt(pageLength);
    }
    
    public void setPageLength(final int pageLength) {
        this.pageLength = pageLength;
    }
    
    @Getter(paramName = "_TL")
    public long getTotal() {
        return this.total;
    }
    
    @Setter(paramName = "_TL")
    private void setTotal(final String total) {
        this.total = Long.parseLong(total);
    }
    
    public void setTotal(final long total) {
        this.total = total;
    }
    
    @Getter(paramName = "_TI")
    public int getToIndex() {
        return this.toIndex;
    }
    
    @Setter(paramName = "_TI")
    private void setToIndex(final String toIndex) {
        this.toIndex = Integer.parseInt(toIndex);
    }
    
    public void setToIndex(final int toIndex) {
        this.toIndex = toIndex;
    }
    
    @Getter(paramName = "_PN")
    public int getPageNumber() {
        return this.pageNumber;
    }
    
    @Setter(paramName = "_PN")
    private void setPageNumber(final String pageNumber) {
        this.pageNumber = Integer.parseInt(pageNumber);
    }
    
    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    @Getter(paramName = "_MP")
    public String getModifiedParam() {
        return this.modifiedParam;
    }
    
    @Setter(paramName = "_MP")
    public void setModifiedParam(final String modifiedParam) {
        this.modifiedParam = modifiedParam;
    }
    
    @Getter(paramName = "SEARCH_COLUMN")
    public String[] getSearchColumns() {
        return this.searchColumns;
    }
    
    @Setter(paramName = "SEARCH_COLUMN")
    private void setSearchColumns(final String searchColumns) {
        this.searchColumns = searchColumns.split(",");
    }
    
    public void setSearchColumns(final String[] searchColumnAlias) {
        this.searchColumns = searchColumnAlias;
    }
    
    @Getter(paramName = "SEARCH_VALUE")
    public String[] getSearchValues() {
        return this.searchValues;
    }
    
    @Setter(paramName = "SEARCH_VALUE")
    private void setSearchValues(final String searchValues) {
        this.searchValues = searchValues.split(",");
    }
    
    public void setSearchValues(final String[] searchValues) {
        this.searchValues = searchValues;
    }
    
    @Getter(paramName = "SEARCHCOMBO_VALUE")
    public int[] getSearchComparators() {
        return this.searchComparators;
    }
    
    @Setter(paramName = "SEARCHCOMBO_VALUE")
    private void setSearchComparators(final String searchComparators) {
        this.searchComparators = Arrays.stream(searchComparators.split(",")).mapToInt(Integer::parseInt).toArray();
    }
    
    public void setSearchComparators(final int[] searchComparators) {
        this.searchComparators = searchComparators;
    }
    
    @Setter(paramName = "colChooserReq[]")
    public void setVisibleColumns(final String[] visibleColumnAlias) {
        this.visibleColumnArray = visibleColumnAlias;
    }
    
    @Getter(paramName = "colChooserReq[]")
    public String[] getVisibleColumns() {
        return this.visibleColumnArray;
    }
    
    @Getter(paramName = "columnResizeReq")
    public JSONObject getColumnAliasVSColumnWidth() {
        return this.columnAliasVSColumnWidth;
    }
    
    @Setter(paramName = "columnResizeReq")
    private void setColumnAliasVSColumnWidth(final String columnAliasVSColumnWidth) {
        this.columnAliasVSColumnWidth = new JSONObject(columnAliasVSColumnWidth);
    }
    
    public void setColumnAliasVSColumnWidth(final JSONObject columnAliasVSColumnWidth) {
        this.columnAliasVSColumnWidth = columnAliasVSColumnWidth;
    }
    
    @Getter(paramName = "SELFILTER")
    public String getSelectedFilter() {
        return this.selectedFilter;
    }
    
    @Setter(paramName = "SELFILTER")
    public void setSelectedFilter(final String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }
    
    @Getter(paramName = "fetchPrevPage")
    public boolean isPrevPageToBeFetched() {
        return this.fetchPrevPage;
    }
    
    @Setter(paramName = "fetchPrevPage")
    private void setPrevPageBeFetched(final String fetchPrevPage) {
        this.fetchPrevPage = Boolean.parseBoolean(fetchPrevPage);
    }
    
    public void setPrevPageBeFetched(final boolean fetchPrevPage) {
        this.fetchPrevPage = fetchPrevPage;
    }
    
    @Override
    public String toString() {
        return "sortBy : " + this.sortBy + "\nsortOrder : " + this.sortOrder + "\nsearchColumns : " + Arrays.toString(this.searchColumns) + "\nsearchVals : " + Arrays.toString(this.searchValues) + "\nsearchComboVals : " + Arrays.toString(this.searchComparators) + "\nfromIndex : " + this.fromIndex + "\ntoIndex : " + this.toIndex + "\npageLength : " + this.pageLength + "\npageNumber : " + this.pageNumber + "\nmodifiedParam : " + this.modifiedParam + "\ntotal : " + this.total + "\nfetchPrevPage : " + this.fetchPrevPage + "\n visibleColumnArray : " + Arrays.toString(this.visibleColumnArray) + "\n columnAliasVSColumnWidth : " + this.columnAliasVSColumnWidth;
    }
}
