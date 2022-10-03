package com.adventnet.persistence.interceptor;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.internal.Operation;
import com.adventnet.persistence.DeleteUtil;
import com.adventnet.persistence.OperationInfo;
import com.adventnet.persistence.DataAccess;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class CorePersistenceInterceptor implements PersistenceInterceptor
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    PersistenceInterceptor nextPI;
    
    public CorePersistenceInterceptor() {
        this.nextPI = null;
    }
    
    @Override
    public String getInterceptorName() {
        return "CorePersistenceInterceptor";
    }
    
    @Override
    public void setBeanConfiguration(final DataObject beanDO) {
    }
    
    @Override
    public Object process(final PersistenceRequest prequest) throws DataAccessException {
        CorePersistenceInterceptor.OUT.entering(CorePersistenceInterceptor.CLASS_NAME, "process", prequest);
        Object retObj = null;
        final int operationType = prequest.getOperationType();
        if (operationType == 600) {
            final CreatePersistenceRequest request = (CreatePersistenceRequest)prequest;
            final DataObject dObj = request.getDataObject();
            CorePersistenceInterceptor.OUT.log(Level.FINEST, "Adding object: {0}", dObj);
            retObj = DataAccess.add(dObj);
        }
        else if (operationType == 601) {
            final ModifyPersistenceRequest request2 = (ModifyPersistenceRequest)prequest;
            final DataObject dObj = request2.getDataObject();
            final UpdateQuery updateQuery = request2.getUpdateQuery();
            CorePersistenceInterceptor.OUT.log(Level.FINEST, "Update Query: {0}", updateQuery);
            if (updateQuery != null) {
                retObj = DataAccess.update(updateQuery);
            }
            else {
                CorePersistenceInterceptor.OUT.log(Level.FINEST, "Updating object: {0}", dObj);
                retObj = DataAccess.update(dObj);
            }
        }
        else if (operationType == 602) {
            final DeletePersistenceRequest request3 = (DeletePersistenceRequest)prequest;
            final DeleteQuery query = request3.getQuery();
            if (query != null) {
                final OperationInfo delInfo = (OperationInfo)request3.getContextInfo(query);
                if (delInfo == null) {
                    CorePersistenceInterceptor.OUT.log(Level.FINEST, "Deleting: {0}", query);
                    retObj = DataAccess.delete(query);
                }
                else {
                    retObj = DeleteUtil.executeDelete(query);
                    final List criteriaList = Operation.getDeleteCriteriaList();
                    for (int i = 0; i < criteriaList.size(); ++i) {
                        final Criteria delCriteria = criteriaList.get(i);
                        final String tableName = this.getTableName(delCriteria);
                        DeleteUtil.executeDelete(tableName, delCriteria);
                    }
                    Operation.clearCriteriaList();
                }
            }
            else {
                final Criteria criteria = request3.getCriteria();
                final OperationInfo delInfo2 = (OperationInfo)request3.getContextInfo(criteria);
                if (delInfo2 == null) {
                    CorePersistenceInterceptor.OUT.log(Level.FINEST, "Deleting: {0}", criteria);
                    DataAccess.delete(criteria);
                }
                else {
                    final List criteriaList2 = Operation.getDeleteCriteriaList();
                    for (int j = 0; j < criteriaList2.size(); ++j) {
                        final Criteria delCriteria2 = criteriaList2.get(j);
                        final String tableName2 = this.getTableName(delCriteria2);
                        DeleteUtil.executeDelete(tableName2, delCriteria2);
                    }
                    Operation.clearCriteriaList();
                }
            }
        }
        else if (operationType == 603) {
            final RetrievePersistenceRequest request4 = (RetrievePersistenceRequest)prequest;
            final SelectQuery query2 = request4.getQuery();
            CorePersistenceInterceptor.OUT.log(Level.FINEST, "Retrieving: {0}", query2);
            retObj = DataAccess.get(query2);
        }
        CorePersistenceInterceptor.OUT.exiting(CorePersistenceInterceptor.CLASS_NAME, "process", retObj);
        return retObj;
    }
    
    private String getTableName(Criteria criteria) {
        while (criteria.getLeftCriteria() != null) {
            criteria = criteria.getLeftCriteria();
        }
        return criteria.getColumn().getTableAlias();
    }
    
    @Override
    public void setNextInterceptor(final PersistenceInterceptor pi) {
        this.nextPI = pi;
    }
    
    @Override
    public void cleanup() {
        System.out.println("Clean up not implemented in BasePersistenceInterceptor");
    }
    
    static {
        CLASS_NAME = CorePersistenceInterceptor.class.getName();
        OUT = Logger.getLogger(CorePersistenceInterceptor.CLASS_NAME);
    }
}
