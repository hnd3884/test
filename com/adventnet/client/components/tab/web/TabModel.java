package com.adventnet.client.components.tab.web;

import java.util.HashMap;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import com.adventnet.client.view.web.TabInformationAPI;
import com.adventnet.client.view.ViewModel;
import com.adventnet.authorization.AuthorizationException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.client.util.web.WebClientUtil;
import java.util.logging.Logger;
import com.adventnet.persistence.Row;
import java.util.List;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;

public class TabModel implements WebConstants
{
    protected ViewContext viewCtx;
    protected List<Row> childConfigList;
    protected int refreshLevel;
    private String selectedView;
    private String viewType;
    protected String tabSelMethodName;
    protected String selTabMethodName;
    private static final Logger logger;
    
    public TabModel(final ViewContext viewCtx, final List<Row> childConfigListArg) {
        this.viewCtx = null;
        this.childConfigList = null;
        this.refreshLevel = -1;
        this.tabSelMethodName = "tabSelected";
        this.selTabMethodName = "selectTabBasedOnDCA";
        this.viewCtx = viewCtx;
        this.childConfigList = childConfigListArg;
        this.updateVariables();
    }
    
    public ViewContext getViewContext() {
        return this.viewCtx;
    }
    
    public List<Row> getChildConfigList() {
        return this.childConfigList;
    }
    
    public void setSelectedView(final String selectedView) {
        this.selectedView = selectedView;
        this.viewCtx.setStateOrURLStateParam("selectedView", (Object)selectedView);
    }
    
    public String getSelectedView() {
        return this.selectedView;
    }
    
    public void setTabSelectedMethodName(final String newTabSelMethodName) {
        this.tabSelMethodName = newTabSelMethodName;
    }
    
    protected void updateVariables() {
        try {
            this.selectedView = (String)this.viewCtx.getStateOrURLStateParameter("selectedView");
            if (this.viewCtx.getModel().getViewConfiguration().containsTable("UINavigationConfig")) {
                final Row uiNavigRow = this.viewCtx.getModel().getViewConfiguration().getFirstRow("UINavigationConfig");
                if (this.selectedView == null) {
                    final String view_name = this.viewCtx.getUniqueId();
                    final Row row = new Row("ACUserPreference");
                    row.set(1, (Object)WebClientUtil.getAccountId());
                    row.set(2, (Object)(view_name + ":defaultSelectedView"));
                    final Persistence pers = LookUpUtil.getPersistence();
                    final DataObject daob = pers.get("ACUserPreference", row);
                    if (!daob.isEmpty()) {
                        this.selectedView = (String)daob.getFirstValue("ACUserPreference", 3);
                    }
                    else {
                        final Long selectedViewNo = (Long)uiNavigRow.get(4);
                        this.selectedView = WebViewAPI.getViewName((Object)selectedViewNo);
                    }
                    if (this.selectedView != null && (!this.isViewPresent(this.selectedView) || !this.isViewAuthorized(this.selectedView))) {
                        TabModel.logger.log(Level.WARNING, "Exception occurred while setting selectedView for tab [" + view_name + "]. defaultSelectedView should be present in childConfigList :");
                        this.selectedView = this.getAuthorizedChildView();
                    }
                }
                this.refreshLevel = (int)uiNavigRow.get(6);
            }
            this.viewCtx.setStateOrURLStateParam("selectedView", (Object)this.selectedView);
            this.viewType = this.viewCtx.getModel().getFeatureValue("WEB_VIEW");
            if (this.viewType == null) {
                this.viewType = "plaintab";
            }
        }
        catch (final DataAccessException ex) {
            throw new RuntimeException((Throwable)ex);
        }
    }
    
    private String getAuthorizedChildView() {
        String childTabViewName = null;
        for (int i = 0; i < this.childConfigList.size(); ++i) {
            childTabViewName = WebViewAPI.getViewName(this.childConfigList.get(i).get(2));
            if (this.isViewAuthorized(childTabViewName)) {
                break;
            }
        }
        return childTabViewName;
    }
    
    private boolean isViewAuthorized(final String selectedView) {
        try {
            ViewContext.getViewContext((Object)selectedView, (Object)selectedView, this.viewCtx.getRequest());
            return true;
        }
        catch (final AuthorizationException ae) {
            return false;
        }
    }
    
    public String getDCAName() {
        try {
            final Row uiNCRow = this.viewCtx.getModel().getViewConfiguration().getRow("UINavigationConfig");
            if (uiNCRow != null) {
                return (String)uiNCRow.get(2);
            }
            return null;
        }
        catch (final DataAccessException e) {
            throw new RuntimeException((Throwable)e);
        }
    }
    
    public String getViewType() {
        return this.viewType;
    }
    
    public boolean isViewPresent(final String viewName) {
        for (int i = 0; i < this.childConfigList.size(); ++i) {
            final String childViewName = WebViewAPI.getViewName((Object)this.childConfigList.get(i).get(2));
            if (childViewName.equals(viewName)) {
                return true;
            }
        }
        return false;
    }
    
    public TabIterator getIterator() {
        return new TabIterator();
    }
    
    public String getTabSelectionJS() {
        final String dcaName = this.getDCAName();
        final String js = "associateDCAForView('" + this.viewCtx.getUniqueId() + "'," + ((dcaName == null) ? "null" : ("'" + dcaName + "'")) + ",'" + this.selTabMethodName + "');";
        return js;
    }
    
    static {
        logger = Logger.getLogger(TabModel.class.getName());
    }
    
    public class TabIterator
    {
        protected int currentCount;
        protected ViewModel currentModel;
        protected ViewContext childCtx;
        protected Row currentChildViewRow;
        protected Row currentTabChildRow;
        protected boolean isSelected;
        
        public TabIterator() {
            this.currentCount = -1;
            this.currentModel = null;
            this.childCtx = null;
            this.isSelected = false;
        }
        
        public boolean next() {
            try {
                while (++this.currentCount < TabModel.this.childConfigList.size()) {
                    this.currentTabChildRow = TabModel.this.childConfigList.get(this.currentCount);
                    final Object childVName = this.currentTabChildRow.get(2);
                    try {
                        this.childCtx = ViewContext.getViewContext(childVName, childVName, TabModel.this.viewCtx.getRequest());
                        this.updateIndex();
                        return true;
                    }
                    catch (final AuthorizationException ae) {
                        TabModel.logger.finer("User is not allowed to view " + childVName);
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
            this.currentModel = (ViewModel)this.childCtx.getModel();
            this.currentChildViewRow = this.currentModel.getViewConfiguration().getFirstRow("ViewConfiguration");
            this.isSelected = this.childCtx.getModel().getViewName().equals(TabModel.this.selectedView);
        }
        
        public String getCurrentClass() {
            return this.isSelected ? "selected" : "notSelected";
        }
        
        public String getTabAction() throws Exception {
            try {
                if (this.getDropDownFeatureMap().containsKey("displayType")) {
                    final String displayType = this.getDropDownFeatureMap().get("displayType").toLowerCase();
                    if ("staticTextImageMouseOver".equalsIgnoreCase(displayType) || "staticTextImageMouseClick".equalsIgnoreCase(displayType)) {
                        return "javascript:void(0)";
                    }
                    if (displayType.indexOf("text") != -1 && displayType.indexOf("mouseclick") != -1) {
                        return null;
                    }
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            String reqAuth = "false";
            try {
                final String roleName = (String)this.getChildViewContext().getModel().getViewConfiguration().getFirstValue("ViewConfiguration", "ROLENAME");
                if (roleName != null) {
                    reqAuth = "true";
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
            if (!WebClientUtil.isRestful(TabModel.this.viewCtx.getRequest())) {
                return "javascript:" + TabModel.this.tabSelMethodName + "(\"" + TabModel.this.viewCtx.getReferenceId() + "\"," + this.currentTabChildRow.get(3) + "," + TabModel.this.refreshLevel + "," + reqAuth + ",null,null,\"" + this.getParamString() + "\")";
            }
            final String tabchildview = WebViewAPI.getViewName(this.currentTabChildRow.get(2));
            final String rootview = WebViewAPI.getRootViewContext(TabModel.this.viewCtx.getRequest()).toString();
            final String url = TabModel.this.viewCtx.getRequest().getContextPath() + "/view/" + rootview + TabInformationAPI.getTabsInfoAsURL(rootview, TabModel.this.viewCtx.getRequest(), TabModel.this.getDCAName()) + "/" + tabchildview;
            return url;
        }
        
        public String getPopUpView() {
            return WebViewAPI.getViewName(this.currentTabChildRow.get(4));
        }
        
        public String getTitle() {
            if (this.currentTabChildRow.get("DISPLAYNAME") != null) {
                return this.currentTabChildRow.get("DISPLAYNAME").toString();
            }
            return this.childCtx.getTitle();
        }
        
        public String getCurrentView() {
            return this.childCtx.getModel().getViewName();
        }
        
        public String getImage() {
            return (String)this.currentChildViewRow.get(5);
        }
        
        public String getChildIconFile() {
            String icoFile = (String)this.currentChildViewRow.get("ICONFILE");
            if (icoFile != null) {
                icoFile = ((icoFile.charAt(0) == '/') ? (TabModel.this.viewCtx.getRequest().getContextPath() + icoFile) : icoFile);
            }
            return icoFile;
        }
        
        public Row getTabChildRow() {
            return this.currentTabChildRow;
        }
        
        public String getCurrentRefId() {
            return this.childCtx.getReferenceId();
        }
        
        public ViewContext getChildViewContext() {
            return this.childCtx;
        }
        
        public String getCurrentIndex() {
            return "" + this.currentTabChildRow.get(3);
        }
        
        public boolean dropDownExists() throws Exception {
            final String popUpView = WebViewAPI.getViewName(this.currentTabChildRow.get(4));
            final String dropMenu = MenuVariablesGenerator.getMenuID(this.currentTabChildRow.get(6));
            final String tabViewName = WebViewAPI.getViewName(this.currentTabChildRow.get(5));
            return popUpView != null || dropMenu != null || tabViewName != null;
        }
        
        private String getDropDownFeatureParams() {
            if ("DROPDOWNPARAMS" == null) {
                return "";
            }
            return "" + this.currentTabChildRow.get(7);
        }
        
        public boolean isMenuDropDown() throws Exception {
            final String dropMenu = MenuVariablesGenerator.getMenuID(this.currentTabChildRow.get(6));
            return dropMenu != null;
        }
        
        public String getMenuId() throws Exception {
            final String menuId = MenuVariablesGenerator.getMenuID(this.currentTabChildRow.get(6));
            return menuId;
        }
        
        public String getDropDownTab() throws Exception {
            final Object viewName = this.currentTabChildRow.get(5);
            return (viewName != null) ? viewName.toString() : null;
        }
        
        public String getParamString() throws Exception {
            final String paramnames = (String)this.currentTabChildRow.get(8);
            if (paramnames == null || paramnames.equals("")) {
                return "null";
            }
            final String[] params = paramnames.split(",");
            final StringBuffer returnstring = new StringBuffer();
            for (int i = 0; i < params.length; ++i) {
                returnstring.append(params[i]);
                returnstring.append("=");
                if (TabModel.this.viewCtx.getRequest().getParameter(params[i]) == null) {
                    throw new RuntimeException("Ajax Params Specified not present in request");
                }
                returnstring.append(TabModel.this.viewCtx.getRequest().getParameter(params[i]));
                returnstring.append("&&");
            }
            return returnstring.toString();
        }
        
        public String handleDropDowns() throws Exception {
            final String popUpView = WebViewAPI.getViewName(this.currentTabChildRow.get(4));
            final String dropMenu = WebViewAPI.getViewName(this.currentTabChildRow.get(6));
            final String tabViewName = WebViewAPI.getViewName(this.currentTabChildRow.get(5));
            final StringBuffer returnString = new StringBuffer();
            final String buttonExists = this.isButtonDropDown() ? "true" : "false";
            if (!this.dropDownExists()) {
                return "";
            }
            if (popUpView != null) {
                returnString.append("handleScriptForDropDownTab('").append(popUpView).append("','").append(this.getCurrentIndex()).append("','").append(TabModel.this.viewCtx.getUniqueId()).append("',0,'" + buttonExists + "')");
            }
            else if (tabViewName != null) {
                returnString.append("handleScriptForDropDownTab('").append(tabViewName).append("','").append(this.getCurrentIndex()).append("','").append(TabModel.this.viewCtx.getUniqueId()).append("',1,'" + buttonExists + "')");
            }
            else if (dropMenu != null) {
                returnString.append("handleScriptForDropDownTab('").append(tabViewName).append("','").append(this.getCurrentIndex()).append("','").append(TabModel.this.viewCtx.getUniqueId()).append("',2,'" + buttonExists + "')");
            }
            return returnString.toString();
        }
        
        public String handleFeatureParams() throws Exception {
            final HashMap hm = this.getDropDownFeatureMap();
            String isVerticalTab = "false";
            final String overflow = hm.containsKey("overflow") ? hm.get("overflow") : "null";
            final String displayType = hm.containsKey("displayType") ? hm.get("displayType") : "null";
            final String offsetLeft = hm.containsKey("offsetLeft") ? hm.get("offsetLeft") : "null";
            final String offsetTop = hm.containsKey("offsetTop") ? hm.get("offsetTop") : "null";
            final String offsetWidth = hm.containsKey("offsetWidth") ? hm.get("offsetWidth") : "null";
            final String offsetHeight = hm.containsKey("offsetHeight") ? hm.get("offsetHeight") : "null";
            final String zIndex = hm.containsKey("zIndex") ? hm.get("zIndex") : "null";
            if (TabModel.this.getViewType().equals("verticaltab") || TabModel.this.getViewType().equals("droptab")) {
                isVerticalTab = "true";
            }
            final StringBuffer buff = new StringBuffer();
            buff.append("var " + TabModel.this.viewCtx.getUniqueId() + "_" + this.getCurrentIndex() + "_isVerticalTab ='" + isVerticalTab + "'; ");
            buff.append("var " + TabModel.this.viewCtx.getUniqueId() + "_" + this.getCurrentIndex() + "_displayType ='" + displayType + "'; ");
            buff.append("var " + TabModel.this.viewCtx.getUniqueId() + "_" + this.getCurrentIndex() + "_offsetLeft ='" + offsetLeft + "'; ");
            buff.append("var " + TabModel.this.viewCtx.getUniqueId() + "_" + this.getCurrentIndex() + "_offsetTop ='" + offsetTop + "'; ");
            buff.append("var " + TabModel.this.viewCtx.getUniqueId() + "_" + this.getCurrentIndex() + "_offsetWidth ='" + offsetWidth + "'; ");
            buff.append("var " + TabModel.this.viewCtx.getUniqueId() + "_" + this.getCurrentIndex() + "_offsetHeight ='" + offsetHeight + "'; ");
            buff.append("var " + TabModel.this.viewCtx.getUniqueId() + "_" + this.getCurrentIndex() + "_zIndex ='" + zIndex + "'; ");
            buff.append("var " + TabModel.this.viewCtx.getUniqueId() + "_" + this.getCurrentIndex() + "_overflow ='" + overflow + "'; ");
            return buff.toString();
        }
        
        private HashMap getDropDownFeatureMap() throws Exception {
            String params = this.getDropDownFeatureParams();
            final HashMap hm = new HashMap(20, 0.75f);
            while (params.indexOf(44) != -1 || params.indexOf("=") != -1) {
                String keyValuePair;
                if (params.indexOf(44) == -1) {
                    keyValuePair = params;
                    params = "";
                }
                else {
                    final int index = params.indexOf(44);
                    keyValuePair = params.substring(0, index);
                    params = params.substring(index + 1, params.length());
                }
                if (keyValuePair.indexOf(61) == -1) {
                    throw new Exception("Format error in DropDownParams");
                }
                final int index2 = keyValuePair.indexOf(61);
                final String key = keyValuePair.substring(0, index2);
                final String value = keyValuePair.substring(index2 + 1, keyValuePair.length());
                hm.put(key, value);
            }
            return hm;
        }
        
        public boolean isButtonDropDown() throws Exception {
            final HashMap hm = this.getDropDownFeatureMap();
            if (!hm.containsKey("displayType")) {
                return false;
            }
            final String displayType = hm.get("displayType");
            return displayType.indexOf("Image") != -1 || displayType.indexOf("image") != -1;
        }
    }
}
