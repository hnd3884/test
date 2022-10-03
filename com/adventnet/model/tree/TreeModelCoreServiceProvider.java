package com.adventnet.model.tree;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.model.Model;
import com.adventnet.customview.CustomViewException;
import com.adventnet.tree.query.TreeQuery;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewRequest;
import java.util.logging.Level;
import com.adventnet.customview.CustomViewManagerContext;
import java.util.logging.Logger;
import com.adventnet.customview.service.ServiceProvider;

public class TreeModelCoreServiceProvider implements ServiceProvider
{
    Logger logger;
    private TreeModelManager treeModelManager;
    private ServiceProvider nextServiceProvider;
    private CustomViewManagerContext customViewManagerContext;
    
    public TreeModelCoreServiceProvider() throws Exception {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.treeModelManager = null;
        this.nextServiceProvider = null;
        this.customViewManagerContext = null;
        this.treeModelManager = TreeModelManager.getInstance();
        this.logger.log(Level.FINER, "TreeModelCoreServiceProvider : treeModelManager : {0}", this.treeModelManager);
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
        this.treeModelManager = null;
        this.nextServiceProvider = null;
        this.customViewManagerContext = null;
    }
    
    @Override
    public ViewData process(final CustomViewRequest customViewRequest) throws CustomViewException {
        this.logger.log(Level.FINER, "TreeModelCoreServiceProvider : Inside process : {0}", customViewRequest);
        Model model = null;
        try {
            final SelectQuery selectQuery = customViewRequest.getSelectQuery();
            this.logger.log(Level.FINER, "TreeModelCoreServiceProvider : selectQuery : {0}", selectQuery);
            final TreeQuery tq = (TreeQuery)selectQuery;
            tq.compile(tq.getTreeDefinition());
            model = this.treeModelManager.getModel(tq);
        }
        catch (final Exception me) {
            throw new CustomViewException("Exception retrieving tree model from TreeModelManager", me);
        }
        this.logger.log(Level.FINER, "TreeModelCoreServiceProvider : model : {0}", model);
        final ViewData viewData = new ViewData(customViewRequest.getCustomViewConfiguration(), model);
        this.logger.log(Level.FINER, "TreeModelCoreServiceProvider : viewData : {0}", viewData);
        return viewData;
    }
}
