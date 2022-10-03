package com.adventnet.authorization.interceptor;

import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.authorization.AuthorizationException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.persistence.DeleteUtil;
import com.adventnet.persistence.OperationInfo;
import com.adventnet.persistence.interceptor.DeletePersistenceRequest;
import com.adventnet.persistence.interceptor.CreatePersistenceRequest;
import com.adventnet.persistence.interceptor.ModifyPersistenceRequest;
import com.adventnet.persistence.interceptor.RetrievePersistenceRequest;
import com.adventnet.persistence.DataAccess;
import com.adventnet.authorization.AuthorizationEngine;
import java.util.logging.Level;
import com.adventnet.persistence.interceptor.PersistenceRequest;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Logger;
import java.util.List;
import com.adventnet.persistence.interceptor.PersistenceInterceptor;

public class AuthorizationInterceptor implements PersistenceInterceptor
{
    PersistenceInterceptor nextPI;
    private List noAuthModuleList;
    private static Logger logger;
    private String beanName;
    
    public AuthorizationInterceptor() throws DataAccessException {
        this.nextPI = null;
        this.noAuthModuleList = null;
        this.beanName = null;
    }
    
    public String getInterceptorName() {
        return "AuthorizationInterceptor";
    }
    
    public void setBeanConfiguration(final DataObject beanDO) {
    }
    
    public Object process(final PersistenceRequest pRequest) throws DataAccessException {
        AuthorizationInterceptor.logger.log(Level.FINEST, "process persistence request invoked");
        RetrievePersistenceRequest rpRequest = null;
        CreatePersistenceRequest cpRequest = null;
        ModifyPersistenceRequest mpRequest = null;
        DeletePersistenceRequest dpRequest = null;
        String type = "VOID";
        Object retObj = null;
        if (!AuthorizationEngine.getFGAEnabled()) {
            AuthorizationInterceptor.logger.log(Level.FINEST, "Authorization is disabled in the System ");
            retObj = this.nextPI.process(pRequest);
            return retObj;
        }
        DataObject dao = DataAccess.constructDataObject();
        try {
            if (pRequest.getOperationType() == 603) {
                rpRequest = (RetrievePersistenceRequest)pRequest;
                final SelectQuery sq = rpRequest.getQuery();
                AuthorizationEngine.scopeSelectQuery(sq);
            }
            if (pRequest.getOperationType() == 601) {
                type = "U";
                mpRequest = (ModifyPersistenceRequest)pRequest;
                dao = mpRequest.getDataObject();
                AuthorizationEngine.checkPermission(dao, type);
            }
            if (pRequest.getOperationType() == 600) {
                type = "C";
                cpRequest = (CreatePersistenceRequest)pRequest;
                dao = cpRequest.getDataObject();
            }
            if (pRequest.getOperationType() == 602) {
                dpRequest = (DeletePersistenceRequest)pRequest;
                final Criteria delCriteria = dpRequest.getCriteria();
                OperationInfo delInfo = (OperationInfo)dpRequest.getContextInfo((Object)delCriteria);
                if (delInfo == null) {
                    final String tableName = this.getTableName(delCriteria);
                    delInfo = DeleteUtil.getDeleteInfo(tableName, delCriteria);
                    dpRequest.setContextInfo((Object)delCriteria, (Object)delInfo);
                }
                AuthorizationInterceptor.logger.log(Level.FINEST, "deleteOperationInfo obtained is {0}", delInfo);
                dao = (DataObject)(retObj = delInfo.getDataObject());
                type = "D";
                if (delInfo.getBulkTableNames() == null || delInfo.getTableNames().size() != delInfo.getBulkTableNames().size()) {
                    final SelectQuery sq2 = AuthorizationEngine.getScopeQueryForDO(delInfo, type);
                    AuthorizationInterceptor.logger.log(Level.FINE, "scoped query obtained for delinfo : {0}", sq2);
                    if (sq2 != null) {
                        QueryUtil.syncForDataType(sq2);
                    }
                }
                else {
                    final List delSQ = delInfo.getSelectQueries();
                    final List bulkTables = delInfo.getBulkTableNames();
                    for (int i = 0; i < delSQ.size(); ++i) {
                        try {
                            final SelectQuery sq3 = delSQ.get(i);
                            final List delTables = sq3.getTableList();
                            AuthorizationEngine.authorizeDeleteOperation(delTables, sq3);
                        }
                        catch (final Exception ex) {
                            AuthorizationInterceptor.logger.log(Level.SEVERE, "Exception occured while authorizing for delete operation ", ex);
                            throw new AuthorizationException(" Got Exception while processing request ", ex);
                        }
                    }
                }
            }
        }
        catch (final Exception re) {
            if (re instanceof AuthorizationException) {
                AuthorizationInterceptor.logger.log(Level.SEVERE, "Got Exception while processing request {0}", re);
                throw new AuthorizationException(" Got Exception while processing request ", re);
            }
            AuthorizationInterceptor.logger.log(Level.WARNING, "Problement while accessing persistence lite bean ", re);
        }
        retObj = this.nextPI.process(pRequest);
        try {
            AuthorizationInterceptor.logger.log(Level.FINEST, "POST:FGAuthorizationInterceptor", "Authorize");
            if (pRequest.getOperationType() == 603) {
                return retObj;
            }
            if (pRequest.getOperationType() == 600) {
                AuthorizationEngine.checkPermission(dao, type);
            }
        }
        catch (final Exception re) {
            AuthorizationInterceptor.logger.log(Level.SEVERE, " Exception occured while authorizing for add operation ", re);
            throw new AuthorizationException(" Got Exception while processing request ", re);
        }
        return retObj;
    }
    
    private static boolean containsAny(final List one, final List two) {
        final Iterator it = one.iterator();
        while (it.hasNext()) {
            if (two.contains(it.next())) {
                return true;
            }
        }
        return false;
    }
    
    public void setNextInterceptor(final PersistenceInterceptor nextPI) {
        this.nextPI = nextPI;
    }
    
    public void cleanup() {
    }
    
    private String getTableName(Criteria criteria) {
        while (criteria.getLeftCriteria() != null) {
            criteria = criteria.getLeftCriteria();
        }
        return criteria.getColumn().getTableAlias();
    }
    
    static {
        AuthorizationInterceptor.logger = Logger.getLogger(AuthorizationInterceptor.class.getName());
    }
}
