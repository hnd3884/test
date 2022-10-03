package com.adventnet.client.components.layout.grid.web;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.authorization.AuthorizationException;
import com.adventnet.client.box.web.BoxAPI;
import com.adventnet.persistence.Row;
import com.adventnet.client.components.layout.web.ChildIterator;
import java.util.logging.Logger;
import java.util.List;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;

public class GridModel implements WebConstants
{
    protected ViewContext viewCtx;
    protected List childConfigList;
    private String viewType;
    private String colWidth;
    private int columnCount;
    protected String childBox;
    private static final Logger LOGGER;
    
    public GridModel(final ViewContext viewCtx, final List childConfigListArg) {
        this.viewCtx = null;
        this.childConfigList = null;
        this.colWidth = "";
        this.columnCount = 0;
        this.childBox = null;
        try {
            this.viewCtx = viewCtx;
            this.columnCount = (int)viewCtx.getModel().getViewConfiguration().getFirstValue("ACGridLayoutConfig", "COLUMNCOUNT");
            this.childBox = (String)viewCtx.getModel().getViewConfiguration().getFirstValue("ViewConfiguration", 14);
            this.colWidth = 100 / this.columnCount + "%";
            this.childConfigList = childConfigListArg;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public int getChildCount() {
        return this.childConfigList.size();
    }
    
    public String getViewType() {
        return this.viewType;
    }
    
    public String getColWidth() {
        return this.colWidth;
    }
    
    public int getColCount() {
        return this.columnCount;
    }
    
    public GridIterator getIterator() {
        return new GridIterator();
    }
    
    static {
        LOGGER = Logger.getLogger(GridModel.class.getName());
    }
    
    public class GridIterator extends ChildIterator
    {
        protected int currentCount;
        protected int viewCount;
        protected ViewContext childCtx;
        protected Row currentGridChildRow;
        protected boolean blnStartRow;
        protected boolean blnEndRow;
        protected String boxType;
        protected boolean isOpen;
        
        public GridIterator() {
            this.currentCount = -1;
            this.viewCount = -1;
            this.childCtx = null;
            this.blnStartRow = false;
            this.blnEndRow = false;
            this.boxType = null;
            this.isOpen = true;
        }
        
        @Override
        public boolean next() throws Exception {
            try {
                while (++this.currentCount < GridModel.this.childConfigList.size()) {
                    this.currentGridChildRow = GridModel.this.childConfigList.get(this.currentCount);
                    final Object viewName = this.currentGridChildRow.get(2);
                    this.boxType = (String)this.currentGridChildRow.get("SHOWINBOX");
                    this.isOpen = (boolean)this.currentGridChildRow.get("ISOPEN");
                    try {
                        this.childCtx = ViewContext.getViewContext(viewName, viewName, GridModel.this.viewCtx.getRequest());
                        if (this.boxType == null && GridModel.this.childBox != null) {
                            this.boxType = GridModel.this.childBox;
                        }
                        BoxAPI.setBoxForView(this.childCtx, this.boxType, this.isOpen);
                        ++this.viewCount;
                        this.updateIndex();
                        return true;
                    }
                    catch (final AuthorizationException ae) {
                        GridModel.LOGGER.finer("User is not allowed to view " + viewName);
                        continue;
                    }
                    break;
                }
                return false;
            }
            catch (final DataAccessException ex) {
                throw new RuntimeException((Throwable)ex);
            }
        }
        
        public void updateIndex() throws DataAccessException {
            this.blnStartRow = false;
            this.blnEndRow = false;
            if (this.viewCount == 0 || this.viewCount % GridModel.this.columnCount == 0) {
                this.blnStartRow = true;
            }
            if (this.viewCount % GridModel.this.columnCount == GridModel.this.columnCount - 1) {
                this.blnEndRow = true;
            }
            if (GridModel.this.childConfigList.size() - 1 == this.viewCount) {
                this.blnEndRow = true;
            }
        }
        
        public String getTitle() {
            try {
                return this.childCtx.getTitle();
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
        
        public String getCurrentView() {
            return this.childCtx.getModel().getViewName();
        }
        
        public int getChildIndex() {
            return (int)this.currentGridChildRow.get(3);
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
        
        public String getBoxType() {
            return this.boxType;
        }
        
        public boolean isOpen() {
            return this.isOpen;
        }
    }
}
