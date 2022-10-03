package com.adventnet.client.components.quicklinks.web;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import com.adventnet.persistence.Row;
import java.util.List;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;

public class LinkModel implements WebConstants
{
    protected ViewContext viewCtx;
    protected List linkList;
    protected int currentCount;
    protected Row curLinkRow;
    protected long accountId;
    
    public LinkModel(final ViewContext viewCtx, final List linkListArg, final long accountIdArg) {
        this.viewCtx = null;
        this.linkList = null;
        this.currentCount = -1;
        this.curLinkRow = null;
        this.accountId = -1L;
        this.viewCtx = viewCtx;
        this.linkList = linkListArg;
        this.accountId = accountIdArg;
    }
    
    public ViewContext getViewContext() {
        return this.viewCtx;
    }
    
    public List getLinkList() {
        return this.linkList;
    }
    
    public boolean next() {
        while (++this.currentCount < this.linkList.size()) {
            this.curLinkRow = this.linkList.get(this.currentCount);
            final Long menuitemID_NO = (Long)this.curLinkRow.get(2);
            try {
                if (WebClientUtil.isMenuItemAuthorized(MenuVariablesGenerator.getMenuItemID(menuitemID_NO))) {
                    return true;
                }
                continue;
            }
            catch (final DataAccessException exp) {
                exp.printStackTrace();
            }
        }
        return false;
    }
    
    public String getCurrentMenuItemId() {
        try {
            final Long menuitemID_NO = (Long)this.curLinkRow.get(2);
            return MenuVariablesGenerator.getMenuItemID(menuitemID_NO);
        }
        catch (final DataAccessException exp) {
            exp.printStackTrace();
            return null;
        }
    }
    
    public boolean canDelete() {
        final Long createdBy = (Long)this.curLinkRow.get("CREATEDBY");
        return createdBy != null && createdBy == this.accountId;
    }
}
