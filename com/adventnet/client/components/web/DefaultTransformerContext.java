package com.adventnet.client.components.web;

import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;

public abstract class DefaultTransformerContext implements TransformerContext
{
    protected HttpServletRequest request;
    protected PageContext pageContext;
    protected String displayName;
    protected ViewContext viewContext;
    protected HashMap<String, String> rendererConfigProps;
    protected HashMap<String, Object> renderedProperties;
    protected DataObject columnConfiguration;
    protected int rowIndex;
    protected int columnIndex;
    protected Object uiCreatorConfig;
    protected Row configRow;
    protected int viewIndexForCol;
    
    public DefaultTransformerContext(final ViewContext viewContext) {
        this.request = null;
        this.pageContext = null;
        this.displayName = null;
        this.viewContext = null;
        this.rendererConfigProps = null;
        this.renderedProperties = null;
        this.columnConfiguration = null;
        this.rowIndex = 0;
        this.columnIndex = 0;
        this.uiCreatorConfig = null;
        this.configRow = null;
        this.viewIndexForCol = -1;
        this.viewContext = viewContext;
        this.renderedProperties = new HashMap<String, Object>();
    }
    
    @Override
    public void setRequest(final HttpServletRequest request) {
        this.request = request;
    }
    
    @Override
    public HttpServletRequest getRequest() {
        return this.request;
    }
    
    @Override
    public void setColumnConfiguration(final DataObject columnConfiguration) throws Exception {
        this.columnConfiguration = columnConfiguration;
        this.configRow = this.columnConfiguration.getFirstRow("ACColumnConfiguration");
        this.reset();
    }
    
    @Override
    public DataObject getColumnConfiguration() {
        return this.columnConfiguration;
    }
    
    @Override
    public Row getColumnConfigRow() {
        return this.configRow;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String getDisplayName() {
        try {
            if (this.displayName != null) {
                return I18N.getMsg(this.displayName, new Object[0]);
            }
            return I18N.getMsg(this.getPropertyName(), new Object[0]);
        }
        catch (final Exception e) {
            if (this.displayName != null) {
                return this.displayName;
            }
            return this.getPropertyName();
        }
    }
    
    @Override
    public Object getPropertyIndex(final String propertyName) {
        return propertyName;
    }
    
    @Override
    public Object getAssociatedIndexedValue(final Object index) {
        return this.getAssociatedPropertyValue((String)index);
    }
    
    @Override
    public ViewContext getViewContext() {
        return this.viewContext;
    }
    
    public void setRendererConfigProps(final HashMap<String, String> properties) {
        this.rendererConfigProps = properties;
    }
    
    @Override
    public HashMap<String, String> getRendererConfigProps() {
        return this.rendererConfigProps;
    }
    
    @Override
    public void setPageContext(final PageContext pageContext) {
        this.pageContext = pageContext;
    }
    
    @Override
    public PageContext getPageContext() {
        return this.pageContext;
    }
    
    @Override
    public void setCreatorConfiguration(final Object dataObject) {
        this.uiCreatorConfig = dataObject;
    }
    
    @Override
    public Object getCreatorConfiguration() {
        return this.uiCreatorConfig;
    }
    
    @Override
    public HashMap<String, Object> getRenderedAttributes() {
        return this.renderedProperties;
    }
    
    public void reset() {
        this.renderedProperties.clear();
    }
    
    public void setRowIndex(final int rowIndex) {
        this.rowIndex = rowIndex;
    }
    
    @Override
    public int getRowIndex() {
        return this.rowIndex;
    }
    
    public void setColumnIndex(final int columnIndex) {
        this.columnIndex = columnIndex;
    }
    
    @Override
    public int getColumnIndex() {
        return this.columnIndex;
    }
    
    public void setViewIndexForCol(final int viewIndexForCol) {
        this.viewIndexForCol = viewIndexForCol;
    }
    
    @Override
    public int getViewIndexForCol() {
        return this.viewIndexForCol;
    }
}
