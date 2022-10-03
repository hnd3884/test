package com.adventnet.persistence;

import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.interceptor.PersistenceRequest;
import com.adventnet.persistence.interceptor.RetrievePersistenceRequest;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.persistence.interceptor.PersistenceInterceptor;
import java.util.logging.Logger;
import com.adventnet.mfw.bean.Initializable;

public class ReadOnlyPersistenceBean implements Initializable, ReadOnlyPersistence
{
    private static final String CLASS_NAME;
    private static final Logger logger;
    private static final String CORE_INTERCEPTOR = "com.adventnet.persistence.interceptor.CorePersistenceInterceptor";
    PersistenceInterceptor startup;
    
    public ReadOnlyPersistenceBean() {
        this.startup = null;
    }
    
    public void initialize(final DataObject beanDO) throws Exception {
        ReadOnlyPersistenceBean.logger.log(Level.FINEST, "BeanDO :: {0}", beanDO);
        PersistenceInterceptor current = null;
        final Iterator i = beanDO.getRows("BeanInterceptor");
        final List interceptors = new ArrayList();
        while (i.hasNext()) {
            final Row interceptorRow = i.next();
            final String interceptorName = (String)interceptorRow.get("CLASSNAME");
            final PersistenceInterceptor interceptor = (PersistenceInterceptor)Class.forName(interceptorName).newInstance();
            interceptor.setBeanConfiguration(beanDO);
            interceptors.add(interceptor);
        }
        PersistenceInterceptor interceptor2 = (PersistenceInterceptor)Class.forName("com.adventnet.persistence.interceptor.CorePersistenceInterceptor").newInstance();
        interceptors.add(interceptor2);
        this.startup = interceptors.get(0);
        current = this.startup;
        for (int j = 1; j < interceptors.size(); ++j) {
            interceptor2 = interceptors.get(j);
            current.setNextInterceptor(interceptor2);
            current = interceptor2;
        }
    }
    
    public DataObject get(final String tableName, final Row instance) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableName, instance);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final String tableName, final List instances) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableName, instances);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final String tableName, final Criteria condition) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableName, condition);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final List tableNames, final Criteria condition) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, condition);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final List tableNames, final List instances) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, instances);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final List tableNames, final Row instance) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, instance);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final List tableNames, final List optionalTableNames, final Criteria condition) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, optionalTableNames, condition);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final SelectQuery selectQuery) throws DataAccessException {
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(selectQuery));
    }
    
    public DataObject getForPersonalities(final List personalities, final Criteria condition) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonalities(personalities, condition);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(query));
    }
    
    public DataObject getForPersonalities(final List personalities, final List instances) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonalities(personalities, instances);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(query));
    }
    
    public DataObject getForPersonalities(final List personalities, final Row instance) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonalities(personalities, instance);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(query));
    }
    
    public DataObject getForPersonalities(final List personalities, final List deepRetrievedPersonalities, final Row instance) throws DataAccessException {
        if (PersonalityConfigurationUtil.areAllPersonalitiesNotIndexed(deepRetrievedPersonalities)) {
            final SelectQuery query = QueryConstructor.getForPersonalities(personalities, deepRetrievedPersonalities, QueryConstructor.formCriteria(instance));
            return (DataObject)this.startup.process(new RetrievePersistenceRequest(query));
        }
        return DataAccess.getForPersonalities(personalities, deepRetrievedPersonalities, instance);
    }
    
    public DataObject getForPersonalities(final List personalities, final List deepRetrievedPersonalities, final List instances) throws DataAccessException {
        if (PersonalityConfigurationUtil.areAllPersonalitiesNotIndexed(deepRetrievedPersonalities)) {
            final SelectQuery query = QueryConstructor.getForPersonalities(personalities, deepRetrievedPersonalities, QueryConstructor.formCriteria(instances));
            return (DataObject)this.startup.process(new RetrievePersistenceRequest(query));
        }
        return DataAccess.getForPersonalities(personalities, deepRetrievedPersonalities, instances);
    }
    
    public DataObject getForPersonalities(final List personalities, final List deepRetrievedPersonalities, final Criteria condition) throws DataAccessException {
        if (PersonalityConfigurationUtil.areAllPersonalitiesNotIndexed(deepRetrievedPersonalities)) {
            final SelectQuery query = QueryConstructor.getForPersonalities(personalities, deepRetrievedPersonalities, condition);
            return (DataObject)this.startup.process(new RetrievePersistenceRequest(query));
        }
        return DataAccess.getForPersonalities(personalities, deepRetrievedPersonalities, condition);
    }
    
    public DataObject getForPersonality(final String personalityName, final Criteria condition) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonality(personalityName, condition);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(query));
    }
    
    public DataObject getForPersonality(final String personalityName, final List instances) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonality(personalityName, instances);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(query));
    }
    
    public DataObject getForPersonality(final String personalityName, final Row instance) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonality(personalityName, instance);
        return (DataObject)this.startup.process(new RetrievePersistenceRequest(query));
    }
    
    public List getPersonalities(final Row instance) throws DataAccessException {
        return DataAccess.getPersonalities(instance);
    }
    
    public DataObject getCompleteData(final Row row) throws DataAccessException {
        return DataAccess.getCompleteData(row);
    }
    
    public DataObject getPrimaryKeys(final String tableName, final Criteria condition) throws DataAccessException {
        return DataAccess.getPrimaryKeys(tableName, condition);
    }
    
    public boolean isInstanceOf(final Row instance, final List personalities) throws DataAccessException {
        return DataAccess.isInstanceOf(instance, personalities);
    }
    
    public boolean isInstanceOf(final Row instance, final String personalityName) throws DataAccessException {
        return DataAccess.isInstanceOf(instance, personalityName);
    }
    
    public List getDominantPersonalities(final Row instance) throws DataAccessException {
        return DataAccess.getDominantPersonalities(instance);
    }
    
    static {
        CLASS_NAME = ReadOnlyPersistenceBean.class.getName();
        logger = Logger.getLogger(ReadOnlyPersistenceBean.CLASS_NAME);
    }
}
