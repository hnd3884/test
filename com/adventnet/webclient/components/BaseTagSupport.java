package com.adventnet.webclient.components;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.webclient.util.FrameWorkUtil;
import com.adventnet.webclient.util.ValueRetriever;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class BaseTagSupport extends BodyTagSupport
{
    public ValueRetriever retrieve;
    public FrameWorkUtil util;
    public String dataSource;
    protected HttpServletRequest request;
    
    public BaseTagSupport() {
        this.dataSource = null;
        this.request = null;
        this.util = FrameWorkUtil.getInstance();
    }
    
    public void setDataSource(final String dataSourceName) {
        this.dataSource = dataSourceName;
    }
    
    public int doStartTag() throws JspTagException {
        this.request = (HttpServletRequest)this.pageContext.getRequest();
        this.setValue("RETRIEVER", (Object)(this.retrieve = new ValueRetriever(this.pageContext)));
        final ServletContext context = this.pageContext.getServletContext();
        return 2;
    }
    
    public String getDataSource() {
        return this.dataSource;
    }
    
    public int doEndTag() throws JspTagException {
        try {
            this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
        }
        catch (final IOException e) {
            throw new JspTagException(e.getMessage());
        }
        return 6;
    }
    
    public Object getDataModel() throws JspTagException {
        Object data = null;
        if (this.dataSource == null) {
            return data;
        }
        data = this.pageContext.findAttribute(this.dataSource);
        if (data == null) {
            throw new JspTagException("The data could not be retrieved from the datasource");
        }
        return data;
    }
}
