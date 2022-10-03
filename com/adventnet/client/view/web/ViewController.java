package com.adventnet.client.view.web;

import org.json.JSONObject;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface ViewController
{
    default void preModelFetch(final ViewContext viewContext) {
    }
    
    void updateViewModel(final ViewContext p0) throws Exception;
    
    default void postModelFetch(final ViewContext viewContext) {
    }
    
    String processPreRendering(final ViewContext p0, final HttpServletRequest p1, final HttpServletResponse p2, final String p3) throws Exception;
    
    void processPostRendering(final ViewContext p0, final HttpServletRequest p1, final HttpServletResponse p2) throws Exception;
    
    @Deprecated
    ActionForward processEvent(final ViewContext p0, final HttpServletRequest p1, final HttpServletResponse p2, final String p3) throws Exception;
    
    void processViewEvent(final ViewContext p0, final HttpServletRequest p1, final HttpServletResponse p2, final String p3) throws Exception;
    
    String getTitle(final ViewContext p0) throws Exception;
    
    void updateAssociatedTiledViews(final ViewContext p0) throws Exception;
    
    void savePreferences(final ViewContext p0) throws Exception;
    
    JSONObject getModelAsJSON(final ViewContext p0) throws Exception;
}
