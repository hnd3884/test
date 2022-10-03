package com.adventnet.client.components.layout.table.web;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.client.box.web.BoxAPI;
import com.adventnet.persistence.Row;
import com.adventnet.client.components.layout.web.ChildIterator;
import java.util.List;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;

public class TableLayoutModel implements WebConstants
{
    protected ViewContext viewCtx;
    protected List childConfigList;
    private String viewType;
    protected String childBox;
    
    public TableLayoutModel(final ViewContext viewCtx, final List childConfigListArg) {
        this.viewCtx = null;
        this.childConfigList = null;
        this.childBox = null;
        try {
            this.viewCtx = viewCtx;
            this.childConfigList = childConfigListArg;
            this.childBox = (String)viewCtx.getModel().getViewConfiguration().getFirstValue("ViewConfiguration", 14);
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public int getChildCount() {
        return this.childConfigList.size();
    }
    
    public TableLayoutIterator getIterator() {
        return new TableLayoutIterator();
    }
    
    public class TableLayoutIterator extends ChildIterator
    {
        protected int currentCount;
        protected ViewContext childCtx;
        protected Row currentTblLytChildRow;
        protected boolean blnStartRow;
        protected boolean blnEndRow;
        protected int curRow;
        protected String boxType;
        protected boolean isOpen;
        
        public TableLayoutIterator() {
            this.currentCount = -1;
            this.childCtx = null;
            this.blnStartRow = false;
            this.blnEndRow = false;
            this.curRow = -1;
            this.boxType = null;
            this.isOpen = true;
        }
        
        @Override
        public boolean next() throws Exception {
            if (++this.currentCount < TableLayoutModel.this.childConfigList.size()) {
                this.currentTblLytChildRow = TableLayoutModel.this.childConfigList.get(this.currentCount);
                final Long viewNameNo = (Long)this.currentTblLytChildRow.get(2);
                this.boxType = (String)this.currentTblLytChildRow.get(9);
                this.isOpen = (boolean)this.currentTblLytChildRow.get(10);
                this.childCtx = ViewContext.getViewContext((Object)viewNameNo, (Object)viewNameNo, TableLayoutModel.this.viewCtx.getRequest());
                if (this.boxType == null && TableLayoutModel.this.childBox != null) {
                    this.boxType = TableLayoutModel.this.childBox;
                }
                BoxAPI.setBoxForView(this.childCtx, this.boxType, this.isOpen);
                this.updateIndex();
                return true;
            }
            return false;
        }
        
        public void updateIndex() throws DataAccessException {
            this.blnStartRow = false;
            this.blnEndRow = false;
            if ((int)this.currentTblLytChildRow.get(3) != this.curRow) {
                this.blnStartRow = true;
                this.curRow = (int)this.currentTblLytChildRow.get(3);
            }
            if (TableLayoutModel.this.childConfigList.size() - 1 == this.currentCount) {
                this.blnEndRow = true;
            }
            else if ((int)TableLayoutModel.this.childConfigList.get(this.currentCount + 1).get(3) != this.curRow) {
                this.blnEndRow = true;
            }
        }
        
        public boolean isRowStart() {
            return this.blnStartRow;
        }
        
        public boolean isRowEnd() {
            return this.blnEndRow;
        }
        
        @Override
        public ViewContext getChildCtx() {
            return this.childCtx;
        }
        
        public Object get(final int propIndex) {
            return this.currentTblLytChildRow.get(propIndex);
        }
        
        public String getBoxType() {
            return this.boxType;
        }
        
        public boolean isOpen() {
            return this.isOpen;
        }
        
        public String getCurrentView() {
            return this.childCtx.getModel().getViewName();
        }
    }
}
