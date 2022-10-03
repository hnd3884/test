package com.me.mdm.server.profiles.screenlayout;

import com.adventnet.persistence.Row;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScreenPageLayoutObject
{
    private int maxRow;
    private int maxColumn;
    private int maxCount;
    private List<Long> screenPageLayoutIds;
    
    public ScreenPageLayoutObject(final Iterator rowIterator) {
        int row = 0;
        int column = 0;
        int totalCount = 0;
        this.screenPageLayoutIds = new ArrayList<Long>();
        while (rowIterator.hasNext()) {
            final Row pageLayoutRow = rowIterator.next();
            final Long screenPageLayoutId = (Long)pageLayoutRow.get("PAGE_LAYOUT_ID");
            final int pageRow = (int)pageLayoutRow.get("PAGE_LAYOUT_ROW");
            final int pageColumn = (int)pageLayoutRow.get("PAGE_LAYOUT_COLUMN");
            if (row < pageRow) {
                row = pageRow;
            }
            if (column < pageColumn) {
                column = pageColumn;
            }
            ++totalCount;
            this.screenPageLayoutIds.add(screenPageLayoutId);
        }
        this.maxRow = row;
        this.maxColumn = column;
        this.maxCount = totalCount;
    }
    
    public int getMaxRow() {
        return this.maxRow;
    }
    
    public int getMaxColumn() {
        return this.maxColumn;
    }
    
    public int getTotalCount() {
        return this.maxCount;
    }
    
    public List<Long> getScreenPageLayoutIds() {
        return this.screenPageLayoutIds;
    }
}
