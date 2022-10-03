package org.apache.taglibs.standard.tag.common.core;

import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;
import javax.el.ExpressionFactory;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import javax.el.ELException;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import javax.el.VariableMapper;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;
import java.util.Map;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class SetSupport extends BodyTagSupport
{
    private String var;
    private String scope;
    
    protected SetSupport() {
    }
    
    public void release() {
        this.var = null;
        this.scope = null;
        super.release();
    }
    
    public int doEndTag() throws JspException {
        if (this.var != null) {
            this.exportToVariable(this.getResult());
        }
        else {
            final Object target = this.evalTarget();
            if (target == null) {
                throw new JspTagException(Resources.getMessage("SET_INVALID_TARGET"));
            }
            final String property = this.evalProperty();
            if (target instanceof Map) {
                this.exportToMapProperty(target, property, this.getResult());
            }
            else {
                this.exportToBeanProperty(target, property, this.getResult());
            }
        }
        return 6;
    }
    
    Object getResult() throws JspException {
        if (this.isValueSpecified()) {
            return this.evalValue();
        }
        if (this.bodyContent == null) {
            return "";
        }
        final String content = this.bodyContent.getString();
        if (content == null) {
            return "";
        }
        return content.trim();
    }
    
    protected abstract boolean isValueSpecified();
    
    protected abstract Object evalValue() throws JspException;
    
    protected abstract Object evalTarget() throws JspException;
    
    protected abstract String evalProperty() throws JspException;
    
    void exportToVariable(final Object result) throws JspTagException {
        final int scopeValue = Util.getScope(this.scope);
        final ELContext myELContext = this.pageContext.getELContext();
        final VariableMapper vm = myELContext.getVariableMapper();
        if (result != null) {
            if (result instanceof ValueExpression) {
                if (scopeValue != 1) {
                    throw new JspTagException(Resources.getMessage("SET_BAD_DEFERRED_SCOPE", this.scope));
                }
                vm.setVariable(this.var, (ValueExpression)result);
            }
            else {
                if (scopeValue == 1 && vm.resolveVariable(this.var) != null) {
                    vm.setVariable(this.var, (ValueExpression)null);
                }
                this.pageContext.setAttribute(this.var, result, scopeValue);
            }
        }
        else {
            if (vm.resolveVariable(this.var) != null) {
                vm.setVariable(this.var, (ValueExpression)null);
            }
            if (this.scope != null) {
                this.pageContext.removeAttribute(this.var, Util.getScope(this.scope));
            }
            else {
                this.pageContext.removeAttribute(this.var);
            }
        }
    }
    
    void exportToMapProperty(final Object target, final String property, final Object result) {
        final Map<Object, Object> map = (Map<Object, Object>)target;
        if (result == null) {
            map.remove(property);
        }
        else {
            map.put(property, result);
        }
    }
    
    void exportToBeanProperty(final Object target, final String property, final Object result) throws JspTagException {
        PropertyDescriptor[] descriptors;
        try {
            descriptors = Introspector.getBeanInfo(target.getClass()).getPropertyDescriptors();
        }
        catch (final IntrospectionException ex) {
            throw new JspTagException((Throwable)ex);
        }
        final PropertyDescriptor[] arr$ = descriptors;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final PropertyDescriptor pd = arr$[i$];
            if (pd.getName().equals(property)) {
                final Method m = pd.getWriteMethod();
                if (m == null) {
                    throw new JspTagException(Resources.getMessage("SET_NO_SETTER_METHOD", property));
                }
                try {
                    m.invoke(target, this.convertToExpectedType(result, m));
                }
                catch (final ELException ex2) {
                    throw new JspTagException((Throwable)ex2);
                }
                catch (final IllegalAccessException ex3) {
                    throw new JspTagException((Throwable)ex3);
                }
                catch (final InvocationTargetException ex4) {
                    throw new JspTagException((Throwable)ex4);
                }
                return;
            }
            else {
                ++i$;
            }
        }
        throw new JspTagException(Resources.getMessage("SET_INVALID_PROPERTY", property));
    }
    
    private Object convertToExpectedType(final Object value, final Method m) throws ELException {
        if (value == null) {
            return null;
        }
        final Class<?> expectedType = m.getParameterTypes()[0];
        return this.getExpressionFactory().coerceToType(value, (Class)expectedType);
    }
    
    protected ExpressionFactory getExpressionFactory() {
        final JspApplicationContext appContext = JspFactory.getDefaultFactory().getJspApplicationContext(this.pageContext.getServletContext());
        return appContext.getExpressionFactory();
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
}
