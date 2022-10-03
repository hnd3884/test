package com.adventnet.customview;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.customview.service.ServiceConfiguration;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Hashtable;
import java.util.HashMap;
import java.io.Serializable;

public class CustomViewRequest implements Serializable, Cloneable
{
    private HashMap<String, Object> userProps;
    String customViewConfigurationName;
    protected Hashtable serviceConfigurations;
    SelectQuery selectQuery;
    DataObject customViewConfiguration;
    RequestContext requestContext;
    private boolean isNew;
    
    public CustomViewRequest(final SelectQuery selectQuery) {
        this.userProps = new HashMap<String, Object>();
        this.serviceConfigurations = null;
        this.selectQuery = selectQuery;
    }
    
    public CustomViewRequest(final DataObject customViewConfiguration) {
        this.userProps = new HashMap<String, Object>();
        this.serviceConfigurations = null;
        this.customViewConfiguration = customViewConfiguration;
    }
    
    public CustomViewRequest(final String customViewConfigurationName) {
        this.userProps = new HashMap<String, Object>();
        this.serviceConfigurations = null;
        this.setCustomViewConfigurationName(customViewConfigurationName);
    }
    
    public String getCustomViewConfigurationName() {
        return this.customViewConfigurationName;
    }
    
    @Deprecated
    public void setCustomViewConfigurationName(final String v) {
        this.customViewConfigurationName = v;
    }
    
    public synchronized ServiceConfiguration putServiceConfiguration(final ServiceConfiguration serviceConfiguration) {
        if (this.serviceConfigurations == null) {
            this.serviceConfigurations = new Hashtable();
        }
        return this.serviceConfigurations.put(serviceConfiguration.getServiceName(), serviceConfiguration);
    }
    
    public synchronized ServiceConfiguration getServiceConfiguration(final String serviceName) {
        if (this.serviceConfigurations == null) {
            return null;
        }
        return this.serviceConfigurations.get(serviceName);
    }
    
    public SelectQuery getSelectQuery() throws CustomViewException {
        if (this.selectQuery != null) {
            return this.selectQuery;
        }
        try {
            if (this.selectQuery == null && this.customViewConfiguration != null) {
                final SelectQuery[] queriesInDO = QueryUtil.getSelectQueryFromDO(this.customViewConfiguration);
                this.selectQuery = queriesInDO[0];
            }
            else if (this.customViewConfigurationName != null) {
                final DataObject queryDO = DataAccess.get("SelectQuery", new Criteria(Column.getColumn("CustomViewConfiguration", "CVNAME"), (Object)this.customViewConfigurationName, 0));
                final Row selectQueryRow = queryDO.getRow("SelectQuery");
                if (selectQueryRow == null) {
                    throw new CustomViewException("No CustomViewConfiguration with this name :: [" + this.customViewConfigurationName + "] found");
                }
                this.selectQuery = QueryUtil.getSelectQuery((long)selectQueryRow.get(1));
            }
        }
        catch (final DataAccessException dae) {
            throw new CustomViewException((Throwable)dae);
        }
        return this.selectQuery;
    }
    
    public void setSelectQuery(final SelectQuery v) {
        this.selectQuery = v;
    }
    
    public synchronized ServiceConfiguration removeServiceConfiguration(final String serviceName) {
        if (this.serviceConfigurations == null) {
            return null;
        }
        return this.serviceConfigurations.remove(serviceName);
    }
    
    public DataObject getCustomViewConfiguration() {
        return this.customViewConfiguration;
    }
    
    public void setCustomViewConfiguration(final DataObject v) {
        this.customViewConfiguration = v;
    }
    
    public RequestContext getRequestContext() {
        return this.requestContext;
    }
    
    public void setRequestContext(final RequestContext v) {
        this.requestContext = v;
    }
    
    public void setNew(final boolean isNew) {
        this.isNew = isNew;
    }
    
    public boolean isNew() {
        return this.isNew;
    }
    
    public Object clone() {
        try {
            final CustomViewRequest cvRequest = (CustomViewRequest)super.clone();
            cvRequest.customViewConfigurationName = this.customViewConfigurationName;
            cvRequest.isNew = this.isNew;
            if (this.customViewConfiguration != null) {}
            if (this.selectQuery != null) {
                cvRequest.selectQuery = (SelectQuery)this.selectQuery.clone();
            }
            if (this.serviceConfigurations != null) {
                cvRequest.serviceConfigurations = (Hashtable)this.serviceConfigurations.clone();
            }
            if (this.requestContext != null) {
                cvRequest.setRequestContext((RequestContext)this.requestContext.clone());
            }
            return cvRequest;
        }
        catch (final CloneNotSupportedException cnse) {
            final InternalError internalError = new InternalError("Could not clone CustomViewRequest");
            internalError.initCause(cnse);
            throw internalError;
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append("<CustomViewRequest>");
        buff.append("\n\t<CustomViewConfiguration>");
        buff.append(this.customViewConfiguration);
        buff.append("\n\t</CustomViewConfiguration>");
        buff.append("\n\t<CustomViewConfiguration-CheckoutInfo>");
        buff.append("\n\t<CustomViewConfigurationName>" + this.customViewConfigurationName + "</CustomViewConfigurationName>");
        buff.append("\n\t<SelectQuery>");
        buff.append(this.selectQuery);
        buff.append("\n\t</SelectQuery>");
        buff.append("\n\t</CustomViewConfiguration-CheckoutInfo>");
        buff.append("\n\t<ServiceConfigurations>");
        buff.append(this.serviceConfigurations);
        buff.append("\n\t</ServiceConfigurations>");
        buff.append("</CustomViewRequest>");
        return buff.toString();
    }
    
    public void set(final String key, final Object value) {
        this.userProps.put(key, value);
    }
    
    public Object get(final String key) {
        return this.userProps.get(key);
    }
    
    public Object getWithDefault(final String key, final Object defaultValue) {
        if (this.userProps.containsKey(key)) {
            return this.userProps.get(key);
        }
        return defaultValue;
    }
}
