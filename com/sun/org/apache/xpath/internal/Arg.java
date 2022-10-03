package com.sun.org.apache.xpath.internal;

import java.util.Objects;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xml.internal.utils.QName;

public class Arg
{
    private QName m_qname;
    private XObject m_val;
    private String m_expression;
    private boolean m_isFromWithParam;
    private boolean m_isVisible;
    
    public final QName getQName() {
        return this.m_qname;
    }
    
    public final void setQName(final QName name) {
        this.m_qname = name;
    }
    
    public final XObject getVal() {
        return this.m_val;
    }
    
    public final void setVal(final XObject val) {
        this.m_val = val;
    }
    
    public void detach() {
        if (null != this.m_val) {
            this.m_val.allowDetachToRelease(true);
            this.m_val.detach();
        }
    }
    
    public String getExpression() {
        return this.m_expression;
    }
    
    public void setExpression(final String expr) {
        this.m_expression = expr;
    }
    
    public boolean isFromWithParam() {
        return this.m_isFromWithParam;
    }
    
    public boolean isVisible() {
        return this.m_isVisible;
    }
    
    public void setIsVisible(final boolean b) {
        this.m_isVisible = b;
    }
    
    public Arg() {
        this.m_qname = new QName("");
        this.m_val = null;
        this.m_expression = null;
        this.m_isVisible = true;
        this.m_isFromWithParam = false;
    }
    
    public Arg(final QName qname, final String expression, final boolean isFromWithParam) {
        this.m_qname = qname;
        this.m_val = null;
        this.m_expression = expression;
        this.m_isFromWithParam = isFromWithParam;
        this.m_isVisible = !isFromWithParam;
    }
    
    public Arg(final QName qname, final XObject val) {
        this.m_qname = qname;
        this.m_val = val;
        this.m_isVisible = true;
        this.m_isFromWithParam = false;
        this.m_expression = null;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.m_qname);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof QName) {
            return this.m_qname.equals(obj);
        }
        return super.equals(obj);
    }
    
    public Arg(final QName qname, final XObject val, final boolean isFromWithParam) {
        this.m_qname = qname;
        this.m_val = val;
        this.m_isFromWithParam = isFromWithParam;
        this.m_isVisible = !isFromWithParam;
        this.m_expression = null;
    }
}
