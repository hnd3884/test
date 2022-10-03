package com.adventnet.client.components.action.web;

import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.action.web.MenuHandler;
import java.io.Writer;
import java.io.IOException;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.authorization.AuthorizationException;
import javax.servlet.jsp.JspTagException;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import javax.servlet.jsp.JspWriter;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.action.web.MenuActionConstants;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class MenuItemTag extends BodyTagSupport implements MenuActionConstants
{
    private String menuItemId;
    private DataObject menuItem;
    private JspWriter writer;
    private final String tableName;
    
    public MenuItemTag() {
        this.menuItemId = null;
        this.menuItem = null;
        this.writer = null;
        this.tableName = null;
    }
    
    public void setMenuItemId(final String menuItemId) {
        this.menuItemId = menuItemId;
    }
    
    public String getMenuItemId() {
        return this.menuItemId;
    }
    
    public void setMenuItem(final DataObject menuItemDO) {
        this.menuItem = menuItemDO;
    }
    
    public DataObject getMenuItem() {
        return this.menuItem;
    }
    
    public int doStartTag() throws JspTagException {
        this.writer = this.pageContext.getOut();
        Label_0108: {
            if (this.menuItem == null && this.menuItemId != null) {
                try {
                    this.menuItem = MenuVariablesGenerator.getCompleteMenuItemData((Object)this.menuItemId);
                    break Label_0108;
                }
                catch (final AuthorizationException ae) {
                    throw new JspTagException(ae.getMessage());
                }
                catch (final DataAccessException dae) {
                    throw new JspTagException("No menu item is found for the menu item id " + this.menuItemId, (Throwable)dae);
                }
            }
            if (this.menuItem == null && this.menuItemId == null) {
                throw new JspTagException("Either menuItem DO or menu item ID should be provided for this tab.");
            }
        }
        if (this.menuItem == null) {
            return 0;
        }
        final boolean isAlreadyAuthorised = this.pageContext.getAttribute("MenuItemAuthorized") != null && (boolean)this.pageContext.getAttribute("MenuItemAuthorized");
        if (!isAlreadyAuthorised && !WebClientUtil.isMenuItemAuthorized(this.menuItem)) {
            return 0;
        }
        return 2;
    }
    
    public void doInitBody() throws JspTagException {
        try {
            MenuItemProperties menuProps = null;
            if (this.menuItem.containsTable("MenuItem")) {
                this.menuItemId = (String)this.menuItem.getFirstValue("MenuItem", 2);
                final String displayName = (String)this.menuItem.getFirstValue("MenuItem", 4);
                final String imageSrc = (String)this.menuItem.getFirstValue("MenuItem", 6);
                final String imageCSSClass = (String)this.menuItem.getFirstValue("MenuItem", 8);
                final int menuView = this.checkAndProcessMenuItemHandling();
                if (menuView == 2) {
                    menuProps = new MenuItemProperties(this.menuItemId, displayName, imageSrc, imageCSSClass);
                    menuProps.setViewType(menuView);
                }
                else {
                    menuProps = new MenuItemProperties(this.menuItemId, displayName, imageSrc, imageCSSClass);
                }
                this.pageContext.setAttribute("CurrentMenuItem", (Object)menuProps);
                try {
                    this.writer.println(this.getGeneratedScript(this.menuItem, this.pageContext));
                }
                catch (final IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        catch (final DataAccessException dae) {
            throw new JspTagException((Throwable)dae);
        }
    }
    
    public int doAfterBody() throws JspTagException {
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
    
    protected int checkAndProcessMenuItemHandling() {
        final Integer menuView = (Integer)this.pageContext.getAttribute("MenuViewType");
        if (menuView != null) {
            return menuView;
        }
        final MenuHandler menuHandler = (MenuHandler)this.pageContext.getAttribute("MenuHandlerInstance");
        if (menuHandler != null) {
            final ViewContext context = (ViewContext)this.pageContext.getAttribute("MenuViewContext");
            return menuHandler.handleMenuItem(this.menuItemId, context, (HttpServletRequest)this.pageContext.getRequest(), (HttpServletResponse)this.pageContext.getResponse());
        }
        return 1;
    }
    
    protected String getGeneratedScript(final DataObject dataObj, final PageContext pageContext) {
        final StringBuffer dataBuffer = new StringBuffer();
        try {
            final String scriptIncl = MenuVariablesGenerator.getScriptInclusion(dataObj, (HttpServletRequest)pageContext.getRequest());
            if (scriptIncl != null) {
                dataBuffer.append(scriptIncl);
            }
            dataBuffer.append(MenuVariablesGenerator.generateMenuVariableScript(dataObj, (HttpServletRequest)pageContext.getRequest(), false));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return dataBuffer.toString();
    }
}
