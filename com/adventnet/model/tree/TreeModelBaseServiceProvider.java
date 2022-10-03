package com.adventnet.model.tree;

import com.adventnet.persistence.DataObject;
import com.adventnet.customview.CustomViewException;
import com.adventnet.tree.TreeManagerUtility;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.tree.query.TreeQueryUtil;
import com.adventnet.tree.query.TreeQuery;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.customview.CustomViewManagerContext;
import java.util.logging.Logger;
import com.adventnet.persistence.Persistence;
import com.adventnet.customview.service.ServiceProvider;

public class TreeModelBaseServiceProvider implements ServiceProvider
{
    private Persistence persistence;
    private Logger logger;
    private ServiceProvider nextServiceProvider;
    private CustomViewManagerContext customViewManagerContext;
    
    public TreeModelBaseServiceProvider() throws Exception {
        this.persistence = null;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.nextServiceProvider = null;
        this.customViewManagerContext = null;
        try {
            this.persistence = (Persistence)BeanUtil.lookup("Persistence");
        }
        catch (final Exception ne) {
            throw new Exception("cannot lookup Persistence bean Persistence", ne);
        }
    }
    
    @Override
    public String getServiceName() {
        return "TREE";
    }
    
    @Override
    public void setCustomViewManagerContext(final CustomViewManagerContext customViewManagerContext) {
        this.customViewManagerContext = customViewManagerContext;
    }
    
    @Override
    public void setNextServiceProvider(final ServiceProvider sp) {
        this.nextServiceProvider = sp;
    }
    
    @Override
    public void cleanup() {
        this.nextServiceProvider = null;
        this.customViewManagerContext = null;
    }
    
    @Override
    public ViewData process(final CustomViewRequest customViewRequest) throws CustomViewException {
        try {
            if (!(customViewRequest.getSelectQuery() instanceof TreeQuery)) {
                final DataObject dObj = customViewRequest.getCustomViewConfiguration();
                final TreeQuery[] tQueries = TreeQueryUtil.getTreeQueryFromDO(dObj);
                customViewRequest.setSelectQuery((SelectQuery)tQueries[0]);
            }
            final SelectQuery selectQuery = customViewRequest.getSelectQuery();
            final TreeQuery tq = (TreeQuery)selectQuery;
            if (tq.getTreeDefinition() == null) {
                final DataObject tdef = TreeManagerUtility.getTreeDefinition(tq.getTreeType());
                tq.setTreeDefinition(tdef);
            }
            return this.nextServiceProvider.process(customViewRequest);
        }
        catch (final Exception me) {
            throw new CustomViewException("Exception while setting tdef", me);
        }
    }
}
