package com.adventnet.client.components.web;

import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;

public interface TransformerContext
{
    Object getDataModel();
    
    void setRequest(final HttpServletRequest p0);
    
    HttpServletRequest getRequest();
    
    void setPageContext(final PageContext p0);
    
    PageContext getPageContext();
    
    void setColumnConfiguration(final DataObject p0) throws Exception;
    
    DataObject getColumnConfiguration();
    
    Row getColumnConfigRow();
    
    String getPropertyName();
    
    Object getPropertyValue();
    
    Object getAssociatedPropertyValue(final String p0);
    
    Object getPropertyIndex(final String p0);
    
    Object getAssociatedIndexedValue(final Object p0);
    
    HashMap<String, Object> getRenderedAttributes();
    
    ViewContext getViewContext();
    
    int getRowIndex();
    
    int getColumnIndex();
    
    int getViewIndexForCol();
    
    HashMap<String, String> getRendererConfigProps();
    
    void setCreatorConfiguration(final Object p0);
    
    Object getCreatorConfiguration();
    
    String getDisplayName();
    
    Object getAssociatedPropertyValue(final String p0, final boolean p1);
}
