package com.adventnet.customview.table;

import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.ds.adapter.MDSContext;
import java.util.List;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.adapter.DataSourceException;
import com.adventnet.customview.CustomViewException;
import java.util.logging.Logger;
import com.adventnet.ds.DataSourceManager;

public class MDSTableModelCoreServiceProvider extends TableModelCoreServiceProvider
{
    private DataSourceManager dsManager;
    private static Logger logger;
    
    public MDSTableModelCoreServiceProvider() {
        this.dsManager = null;
        this.queryPIDX = false;
        this.getMDSHandle();
    }
    
    private void getMDSHandle() {
        this.dsManager = DataSourceManager.getInstance();
    }
    
    @Override
    Object getContext() throws CustomViewException {
        try {
            return DataSourceManager.getContext();
        }
        catch (final DataSourceException dsExp) {
            throw new CustomViewException(dsExp.getMessage(), (Throwable)dsExp);
        }
    }
    
    @Override
    DataSet execute(final SelectQuery sql, final Object dsCtxt) throws CustomViewException {
        DataSet ds = null;
        try {
            final CustomViewRequest cvRequest = this.cvRequestThLocal.get();
            final List dsList = (List)cvRequest.get("DATASOURCE_LIST");
            ds = this.dsManager.executeQuery((MDSContext)dsCtxt, sql, dsList);
            MDSTableModelCoreServiceProvider.logger.log(Level.FINE, " MDSTableModelCoreServiceProvider: DataSet obtained is {0}", ds);
        }
        catch (final DataSourceException dsExp) {
            final CustomViewException exp = new CustomViewException(dsExp.getMessage());
            exp.initCause((Throwable)dsExp);
            throw exp;
        }
        return ds;
    }
    
    @Override
    protected void cleanup(final DataSet ds, final Object dsCtxt) {
        try {
            super.cleanup(ds, null);
            final MDSContext context = (MDSContext)dsCtxt;
            DataSourceManager.cleanUp(context);
        }
        catch (final DataSourceException exp) {
            MDSTableModelCoreServiceProvider.logger.log(Level.SEVERE, "Exception when cleaning up resources : ", (Throwable)exp);
        }
        finally {
            MDSTableModelCoreServiceProvider.logger.log(Level.FINEST, " inside finally of cleanup in MDSTableModelCoreServiceProvider");
        }
    }
    
    @Override
    protected HashMap getTableAliasToPKColsMapping(final SelectQuery selectQuery) throws CustomViewException {
        return new HashMap();
    }
    
    static {
        MDSTableModelCoreServiceProvider.logger = Logger.getLogger(MDSTableModelCoreServiceProvider.class.getName());
    }
}
