package org.apache.commons.httpclient.methods;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.EncodingUtil;
import java.util.Vector;
import org.apache.commons.logging.Log;

public class PostMethod extends EntityEnclosingMethod
{
    private static final Log LOG;
    public static final String FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private Vector params;
    
    public PostMethod() {
        this.params = new Vector();
    }
    
    public PostMethod(final String uri) {
        super(uri);
        this.params = new Vector();
    }
    
    public String getName() {
        return "POST";
    }
    
    protected boolean hasRequestContent() {
        PostMethod.LOG.trace((Object)"enter PostMethod.hasRequestContent()");
        return !this.params.isEmpty() || super.hasRequestContent();
    }
    
    protected void clearRequestBody() {
        PostMethod.LOG.trace((Object)"enter PostMethod.clearRequestBody()");
        this.params.clear();
        super.clearRequestBody();
    }
    
    protected RequestEntity generateRequestEntity() {
        if (!this.params.isEmpty()) {
            final String content = EncodingUtil.formUrlEncode(this.getParameters(), this.getRequestCharSet());
            final ByteArrayRequestEntity entity = new ByteArrayRequestEntity(EncodingUtil.getAsciiBytes(content), "application/x-www-form-urlencoded");
            return entity;
        }
        return super.generateRequestEntity();
    }
    
    public void setParameter(final String parameterName, final String parameterValue) {
        PostMethod.LOG.trace((Object)"enter PostMethod.setParameter(String, String)");
        this.removeParameter(parameterName);
        this.addParameter(parameterName, parameterValue);
    }
    
    public NameValuePair getParameter(final String paramName) {
        PostMethod.LOG.trace((Object)"enter PostMethod.getParameter(String)");
        if (paramName == null) {
            return null;
        }
        final Iterator iter = this.params.iterator();
        while (iter.hasNext()) {
            final NameValuePair parameter = iter.next();
            if (paramName.equals(parameter.getName())) {
                return parameter;
            }
        }
        return null;
    }
    
    public NameValuePair[] getParameters() {
        PostMethod.LOG.trace((Object)"enter PostMethod.getParameters()");
        final int numPairs = this.params.size();
        final Object[] objectArr = this.params.toArray();
        final NameValuePair[] nvPairArr = new NameValuePair[numPairs];
        for (int i = 0; i < numPairs; ++i) {
            nvPairArr[i] = (NameValuePair)objectArr[i];
        }
        return nvPairArr;
    }
    
    public void addParameter(final String paramName, final String paramValue) throws IllegalArgumentException {
        PostMethod.LOG.trace((Object)"enter PostMethod.addParameter(String, String)");
        if (paramName == null || paramValue == null) {
            throw new IllegalArgumentException("Arguments to addParameter(String, String) cannot be null");
        }
        super.clearRequestBody();
        this.params.add(new NameValuePair(paramName, paramValue));
    }
    
    public void addParameter(final NameValuePair param) throws IllegalArgumentException {
        PostMethod.LOG.trace((Object)"enter PostMethod.addParameter(NameValuePair)");
        if (param == null) {
            throw new IllegalArgumentException("NameValuePair may not be null");
        }
        this.addParameter(param.getName(), param.getValue());
    }
    
    public void addParameters(final NameValuePair[] parameters) {
        PostMethod.LOG.trace((Object)"enter PostMethod.addParameters(NameValuePair[])");
        if (parameters == null) {
            PostMethod.LOG.warn((Object)"Attempt to addParameters(null) ignored");
        }
        else {
            super.clearRequestBody();
            for (int i = 0; i < parameters.length; ++i) {
                this.params.add(parameters[i]);
            }
        }
    }
    
    public boolean removeParameter(final String paramName) throws IllegalArgumentException {
        PostMethod.LOG.trace((Object)"enter PostMethod.removeParameter(String)");
        if (paramName == null) {
            throw new IllegalArgumentException("Argument passed to removeParameter(String) cannot be null");
        }
        boolean removed = false;
        final Iterator iter = this.params.iterator();
        while (iter.hasNext()) {
            final NameValuePair pair = iter.next();
            if (paramName.equals(pair.getName())) {
                iter.remove();
                removed = true;
            }
        }
        return removed;
    }
    
    public boolean removeParameter(final String paramName, final String paramValue) throws IllegalArgumentException {
        PostMethod.LOG.trace((Object)"enter PostMethod.removeParameter(String, String)");
        if (paramName == null) {
            throw new IllegalArgumentException("Parameter name may not be null");
        }
        if (paramValue == null) {
            throw new IllegalArgumentException("Parameter value may not be null");
        }
        final Iterator iter = this.params.iterator();
        while (iter.hasNext()) {
            final NameValuePair pair = iter.next();
            if (paramName.equals(pair.getName()) && paramValue.equals(pair.getValue())) {
                iter.remove();
                return true;
            }
        }
        return false;
    }
    
    public void setRequestBody(final NameValuePair[] parametersBody) throws IllegalArgumentException {
        PostMethod.LOG.trace((Object)"enter PostMethod.setRequestBody(NameValuePair[])");
        if (parametersBody == null) {
            throw new IllegalArgumentException("Array of parameters may not be null");
        }
        this.clearRequestBody();
        this.addParameters(parametersBody);
    }
    
    static {
        LOG = LogFactory.getLog(PostMethod.class);
    }
}
