package com.adventnet.client.view.web;

import java.util.Iterator;
import com.adventnet.client.view.ViewHandler;
import com.zoho.authentication.AuthenticationUtil;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.view.ViewAPI;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.authorization.AuthorizationException;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.State;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.List;
import com.adventnet.client.util.web.WebConstants;

public class ViewContext implements WebConstants
{
    private final String uniqueId;
    private ViewContext parentContext;
    private WebViewModel model;
    private Object viewModel;
    private List<ViewContext> childCtxList;
    private Map<String, Object> stateMap;
    private Map<String, Object> previousStateMap;
    private Map<String, Object> urlstateMap;
    private Map<String, Object> prevurlstateMap;
    private HttpServletRequest request;
    private String referenceId;
    private static final int NOT_AUTHORIZED_YET = 0;
    private static final int AUTHORIZATION_SUCCEEDED = 1;
    private static final int AUTHORIZATION_FAILED = 2;
    private int isAuthorized;
    private Map<String, Object> transientState;
    private String title;
    private boolean isCSRComponent;
    private String contextPath;
    private int renderType;
    
    public boolean isCSRComponent() {
        return this.isCSRComponent;
    }
    
    private ViewContext(final String uniqueIdArg, final HttpServletRequest req) {
        this.childCtxList = null;
        this.stateMap = null;
        this.previousStateMap = null;
        this.urlstateMap = null;
        this.prevurlstateMap = null;
        this.request = null;
        this.referenceId = null;
        this.isAuthorized = 0;
        this.transientState = null;
        this.title = null;
        this.isCSRComponent = false;
        this.contextPath = "";
        this.renderType = 4;
        this.uniqueId = uniqueIdArg;
        this.previousStateMap = StateAPI.getStateMap(this.uniqueId);
        this.prevurlstateMap = StateAPI.getURLStateMap(this.uniqueId);
        if (this.previousStateMap != null && !this.previousStateMap.containsKey("_VN")) {
            this.previousStateMap.put("_VN", this.uniqueId);
        }
        this.request = req;
    }
    
    private ViewContext(final String viewName) {
        this.childCtxList = null;
        this.stateMap = null;
        this.previousStateMap = null;
        this.urlstateMap = null;
        this.prevurlstateMap = null;
        this.request = null;
        this.referenceId = null;
        this.isAuthorized = 0;
        this.transientState = null;
        this.title = null;
        this.isCSRComponent = false;
        this.contextPath = "";
        this.renderType = 4;
        this.uniqueId = viewName;
    }
    
    public State getViewState() {
        return this.getModel().getState();
    }
    
    public void setViewState(final State viewState) {
        this.getModel().setState(viewState);
    }
    
    public String getContextPath() {
        return this.contextPath;
    }
    
    public void setContextPath(final String contextPath) {
        this.contextPath = contextPath;
    }
    
    @Deprecated
    public long getClientCacheGenTime() {
        if (WebViewAPI.isAjaxRequest(this.request) && this.previousStateMap != null) {
            final String viewGenStr = this.previousStateMap.get("VGT");
            return (viewGenStr != null) ? Long.parseLong(viewGenStr) : -1L;
        }
        return -1L;
    }
    
    public String getReferenceId() {
        try {
            if (this.referenceId == null) {
                if (this.uniqueId.equals(this.getModel().getViewName())) {
                    final DataObject dao = WebViewAPI.getConfigModel(this.uniqueId, false).getViewConfiguration();
                    this.referenceId = String.valueOf(dao.getFirstValue("ViewConfiguration", 1));
                }
                else {
                    this.referenceId = this.uniqueId;
                }
            }
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        return this.referenceId;
    }
    
    public String getUniqueId() {
        return this.uniqueId;
    }
    
    @Deprecated
    public ViewContext getParentContext() {
        return this.parentContext;
    }
    
    @Deprecated
    public void setParentContext(final ViewContext newParentContext) {
        this.parentContext = newParentContext;
    }
    
    public WebViewModel getModel() {
        if (this.model == null) {
            String viewName = (String)this.getStateParameter("_VN");
            if (viewName == null) {
                viewName = this.uniqueId;
            }
            this.model = WebViewAPI.getConfigModel(viewName, true);
        }
        if (this.model == null) {
            throw new RuntimeException("View Name has not been associated with uniqueId : " + this.uniqueId + ". Possibly the client state has not been passed");
        }
        return this.model;
    }
    
    public void setModel(final WebViewModel newModel) {
        this.model = newModel;
    }
    
    @Deprecated
    public Object getStateParameter(final String paramName) {
        Object stateData = (this.stateMap != null) ? this.stateMap.get(paramName) : null;
        if (stateData == null && this.previousStateMap != null) {
            stateData = this.previousStateMap.get(paramName);
        }
        return (stateData != ViewContext.NULLOBJ) ? stateData : null;
    }
    
    public Object getURLStateParameter(final String paramName) {
        Object stateData = (this.urlstateMap != null) ? this.urlstateMap.get(paramName) : null;
        if (stateData == null && this.prevurlstateMap != null) {
            stateData = this.prevurlstateMap.get(paramName);
        }
        return (stateData != ViewContext.NULLOBJ) ? stateData : null;
    }
    
    @Deprecated
    public void updateStateParameter(final String paramName, final Object paramValue) {
        if (this.previousStateMap == null) {
            return;
        }
        final String parameter = this.previousStateMap.get(paramName);
        if (parameter != null) {
            this.setStateParameter(paramName, paramValue);
        }
    }
    
    @Deprecated
    public boolean isPresentInPrevState(final String paramName) {
        return this.previousStateMap.containsKey(paramName);
    }
    
    @Deprecated
    public void setStateParameter(final String paramName, final Object value) {
        if (this.stateMap == null) {
            this.stateMap = new HashMap<String, Object>();
        }
        this.stateMap.put(paramName, (value != null) ? value : ViewContext.NULLOBJ);
    }
    
    public void setURLStateParameter(final String paramName, final Object value) {
        if (this.urlstateMap == null) {
            this.urlstateMap = new HashMap<String, Object>();
        }
        this.urlstateMap.put(paramName, (value != null) ? value : ViewContext.NULLOBJ);
    }
    
    @Deprecated
    public Map<String, Object> getState() {
        return this.stateMap;
    }
    
    @Deprecated
    public void setState(final Map<String, Object> state) {
        this.stateMap = state;
    }
    
    public Map<String, Object> getURLState() {
        return this.urlstateMap;
    }
    
    @Deprecated
    public Map<String, Object> getPreviousState() {
        return this.previousStateMap;
    }
    
    @Deprecated
    public void addChildViewContext(final ViewContext childCtx) {
        if (this.childCtxList == null) {
            this.childCtxList = new ArrayList<ViewContext>();
        }
        this.childCtxList.add(childCtx);
    }
    
    @Deprecated
    public List<ViewContext> getChildViewContexts() {
        return this.childCtxList;
    }
    
    @Deprecated
    public static ViewContext getPreviousRootViewCtx(final HttpServletRequest request) {
        final String uniqueId = (String)StateAPI.getRequiredState("_REQS", "_RVID");
        return getViewContext(uniqueId, request);
    }
    
    public static ViewContext getViewContext(final String viewName) {
        return getViewContext(viewName, null);
    }
    
    public static ViewContext getViewContext(final String viewName, final State state) {
        if (viewName == null) {
            throw new NullPointerException("viewName should not be null");
        }
        final ViewContext vc = new ViewContext(viewName);
        if (!vc.isAuthorized(viewName)) {
            throw new AuthorizationException("User is not authorized of this view " + vc.getUniqueId());
        }
        if (vc.model == null) {
            vc.setModel(WebViewAPI.getConfigModel(viewName, true));
        }
        vc.setViewState(state);
        try {
            final DataObject uiDO = vc.getModel().getUIComponentConfig();
            vc.isCSRComponent = (uiDO != null && (boolean)uiDO.getValue("WebUIComponent", "ISCSRCOMPONENT", (Criteria)null));
        }
        catch (final DataAccessException e) {
            throw new RuntimeException("Exception while setting 'isCSRComponent' of ViewContext for the specified view : " + viewName, (Throwable)e);
        }
        return vc;
    }
    
    @Deprecated
    public static ViewContext getViewContext(final Object uniqueId, final HttpServletRequest request) {
        if (uniqueId == null) {
            throw new NullPointerException("uniqueId cannot be null");
        }
        Object viewName = uniqueId;
        final Map stateMap = StateAPI.getStateMap(String.valueOf(uniqueId));
        if (stateMap != null && stateMap.containsKey("_VN")) {
            viewName = stateMap.get("_VN");
        }
        return getViewContext(uniqueId, viewName, request);
    }
    
    @Deprecated
    public static ViewContext getViewContext(final Object uniqueId, final Object viewName, final HttpServletRequest request) {
        final String _VN = (uniqueId instanceof Long) ? WebViewAPI.getViewName(uniqueId) : uniqueId;
        return (request != null) ? getViewctx(_VN, viewName, request) : getViewContext(_VN);
    }
    
    private static ViewContext getViewctx(Object uniqueId, final Object viewName, final HttpServletRequest request) {
        if (viewName == null) {
            throw new NullPointerException("viewName cannot be null");
        }
        if (uniqueId instanceof Long) {
            uniqueId = WebViewAPI.getViewName(uniqueId);
        }
        ViewContext viewCtx = (ViewContext)request.getAttribute((String)uniqueId);
        if (viewCtx == null) {
            viewCtx = new ViewContext((String)uniqueId, request);
            request.setAttribute((String)uniqueId, (Object)viewCtx);
        }
        if (!viewCtx.isAuthorized(viewName)) {
            throw new AuthorizationException("User is not allowed to view " + viewCtx.getUniqueId());
        }
        if (viewCtx.model == null) {
            viewCtx.setModel(WebViewAPI.getConfigModel(viewName, true));
        }
        viewCtx.request = request;
        viewCtx.contextPath = request.getContextPath();
        try {
            final DataObject uiDO = viewCtx.getModel().getUIComponentConfig();
            viewCtx.isCSRComponent = (uiDO != null && (boolean)uiDO.getValue("WebUIComponent", "ISCSRCOMPONENT", (Criteria)null));
            if (viewCtx.isCSRComponent) {
                ViewAPI.updateViewStates(viewCtx, request);
            }
        }
        catch (final DataAccessException e) {
            throw new RuntimeException("Exception while setting 'isCSRComponent' of ViewContext for the specified view : " + viewName, (Throwable)e);
        }
        return viewCtx;
    }
    
    public static void refreshViewContext(final String uniqueId, final HttpServletRequest request) {
        final ViewContext viewCtx = (ViewContext)request.getAttribute(uniqueId);
        if (viewCtx != null && viewCtx.model != null) {
            viewCtx.setModel(WebViewAPI.getConfigModel(viewCtx.model.getViewName(), true));
            viewCtx.viewModel = null;
        }
    }
    
    public HttpServletRequest getRequest() {
        return this.request;
    }
    
    @Deprecated
    public void clearPreviousState() {
        this.previousStateMap = null;
    }
    
    public void setViewModel(final Object viewModel) {
        this.viewModel = viewModel;
    }
    
    public Object getViewModel() {
        try {
            return this.getViewModel(false);
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public Object getViewModel(final boolean refreshModelEvenIfAlreadyPresent) throws Exception {
        if (this.viewModel == null || refreshModelEvenIfAlreadyPresent) {
            this.getModel().getController().updateViewModel(this);
        }
        return this.viewModel;
    }
    
    private boolean isAuthorized(final Object viewName) {
        if (this.isAuthorized == 0) {
            this.isAuthorized = 2;
            try {
                final DataObject viewConfig = WebViewAPI.getViewConfiguration(viewName);
                final String roleName = (String)viewConfig.getFirstValue("ViewConfiguration", 8);
                if (roleName != null) {
                    if (WebClientUtil.isNewAuthPropertySet()) {
                        if (!AuthenticationUtil.isUserExists(roleName)) {
                            return this.isAuthorized == 1;
                        }
                    }
                    else if (!WebClientUtil.getAuthImpl().userExists(roleName)) {
                        return this.isAuthorized == 1;
                    }
                }
                this.isAuthorized = 1;
                final String handlerClassName = (String)viewConfig.getFirstValue("ViewConfiguration", 16);
                if (handlerClassName != null) {
                    final ViewHandler vh = (ViewHandler)WebClientUtil.createInstance(handlerClassName);
                    this.isAuthorized = (vh.canRender(this) ? 1 : 2);
                }
            }
            catch (final Exception e) {
                this.isAuthorized = 2;
                throw new RuntimeException(e);
            }
        }
        return this.isAuthorized == 1;
    }
    
    public Object getTransientState(final String stateName) {
        return (this.transientState == null) ? null : this.transientState.get(stateName);
    }
    
    public void setTransientState(final String stateName, final Object value) {
        if (this.transientState == null) {
            this.transientState = new HashMap<String, Object>();
        }
        this.transientState.put(stateName, value);
    }
    
    @Override
    public int hashCode() {
        return this.uniqueId.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ViewContext && ((ViewContext)obj).uniqueId.equals(this.uniqueId);
    }
    
    public String getTitle() {
        try {
            if (this.title == null) {
                this.title = this.getModel().getController().getTitle(this);
            }
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        return this.title;
    }
    
    public void setTitle(final String arg) {
        this.title = arg;
    }
    
    public int getRenderType() {
        return this.renderType;
    }
    
    public void setRenderType(final int renderType) {
        this.renderType = renderType;
    }
    
    public void setURLStateParameters() {
        final HashMap<String, Object> urlstatemap = WebViewAPI.getURLStateParameterMap(this.getRequest());
        for (final String key : urlstatemap.keySet()) {
            final Object value = urlstatemap.get(key);
            if (this.prevurlstateMap == null) {
                this.prevurlstateMap = new HashMap<String, Object>();
            }
            this.prevurlstateMap.put(key, value);
        }
    }
    
    @Override
    public String toString() {
        if (this.uniqueId.equals(this.getModel().getViewName())) {
            return this.uniqueId;
        }
        return this.uniqueId + "[" + this.getModel().getViewName() + "]";
    }
    
    public void setStateOrURLStateParam(final String name, final Object value) {
        this.setStateOrURLStateParam(name, value, false);
    }
    
    public void setStateOrURLStateParam(final String name, final Object value, final boolean settostate) {
        if (WebClientUtil.isRestful(this.getRequest())) {
            this.setURLStateParameter(name, value);
        }
        else {
            this.setStateParameter(name, value);
        }
        if (settostate) {
            this.setStateParameter(name, value);
        }
    }
    
    public Object getStateOrURLStateParameter(final String name) {
        Object value = null;
        if (WebClientUtil.isRestful(this.getRequest())) {
            value = this.getURLStateParameter(name);
        }
        if (value == null) {
            value = this.getStateParameter(name);
        }
        return value;
    }
    
    public void updateStateOrURLStateParam(final String name, final Object value) {
        if (WebClientUtil.isRestful(this.getRequest())) {
            this.setURLStateParameter(name, value);
        }
        else {
            this.updateStateParameter(name, value);
        }
    }
    
    public boolean isExportType() {
        final int renderType = this.getRenderType();
        return renderType == 3 || renderType == 6 || renderType == 5 || renderType == 7;
    }
}
