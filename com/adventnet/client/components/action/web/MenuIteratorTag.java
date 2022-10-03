package com.adventnet.client.components.action.web;

import java.io.IOException;
import java.io.Writer;
import com.adventnet.authorization.AuthorizationException;
import com.adventnet.persistence.Row;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.persistence.DataAccessException;
import javax.servlet.jsp.JspTagException;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import java.util.Iterator;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.action.web.MenuHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.action.web.MenuActionConstants;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class MenuIteratorTag extends BodyTagSupport implements MenuActionConstants
{
    private String menuId;
    private DataObject menuObj;
    private MenuHandler menuHandler;
    private String uniqueId;
    private ViewContext context;
    private Iterator iter;
    
    public MenuIteratorTag() {
        this.menuId = null;
        this.menuObj = null;
        this.menuHandler = null;
        this.uniqueId = null;
        this.context = null;
        this.iter = null;
    }
    
    public void setMenuId(final String menuId) {
        this.menuId = menuId;
    }
    
    public String getMenuId() {
        return this.menuId;
    }
    
    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public String getUniqueId() {
        return this.uniqueId;
    }
    
    public int doStartTag() throws JspTagException {
        try {
            this.menuObj = MenuVariablesGenerator.getCompleteMenuData((Object)this.menuId);
            if (!this.menuObj.containsTable("MenuAndMenuItem")) {
                return 0;
            }
            this.iter = this.menuObj.getRows("MenuAndMenuItem");
            String handlerClsName = null;
            try {
                handlerClsName = (String)this.menuObj.getFirstValue("Menu", "HANDLER");
            }
            catch (final DataAccessException dae) {
                throw new JspTagException((Throwable)dae);
            }
            if (handlerClsName != null) {
                this.menuHandler = (MenuHandler)WebClientUtil.createInstance(handlerClsName);
                if (this.uniqueId != null) {
                    this.context = ViewContext.getViewContext((Object)this.uniqueId, (HttpServletRequest)this.pageContext.getRequest());
                }
                this.menuHandler.initMenuHandler(this.context, (HttpServletRequest)this.pageContext.getRequest(), (HttpServletResponse)this.pageContext.getResponse());
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return 2;
    }
    
    public void doInitBody() throws JspTagException {
        this.evalBody();
    }
    
    public int evalBody() throws JspTagException {
        DataObject dataObj = null;
        try {
            if (this.iter.hasNext()) {
                final Row indRow = this.iter.next();
                final Long menuItemID_NO = (Long)indRow.get("MENUITEMID");
                try {
                    dataObj = MenuVariablesGenerator.getCompleteMenuItemData((Object)menuItemID_NO);
                }
                catch (final AuthorizationException ae) {
                    ae.printStackTrace();
                    this.pageContext.setAttribute("MenuItemDO", (Object)null, 1);
                    return this.evalBody();
                }
                if (this.menuHandler != null) {
                    final int menuView = this.menuHandler.handleMenuItem((String)dataObj.getFirstValue("MenuItem", "MENUITEMID"), this.context, (HttpServletRequest)this.pageContext.getRequest(), (HttpServletResponse)this.pageContext.getResponse());
                    if (menuView == 0) {
                        return this.evalBody();
                    }
                    this.pageContext.setAttribute("MenuViewType", (Object)new Integer(menuView));
                    if (this.context != null) {
                        this.pageContext.setAttribute("MenuViewContext", (Object)this.context);
                    }
                    this.pageContext.setAttribute("MenuHandlerInstance", (Object)this.menuHandler);
                }
                this.pageContext.setAttribute("MenuItemAuthorized", (Object)new Boolean(true));
                this.pageContext.setAttribute("MenuItemDO", (Object)dataObj, 1);
                return 2;
            }
        }
        catch (final DataAccessException dae) {
            throw new JspTagException((Throwable)dae);
        }
        return 6;
    }
    
    public int doAfterBody() throws JspTagException {
        if (this.iter.hasNext()) {
            return this.evalBody();
        }
        return 6;
    }
    
    public int doEndTag() throws JspTagException {
        if (this.bodyContent != null) {
            try {
                this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
            }
            catch (final IOException e) {
                throw new JspTagException(e.getMessage());
            }
        }
        return 6;
    }
}
