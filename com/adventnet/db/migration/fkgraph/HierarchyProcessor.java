package com.adventnet.db.migration.fkgraph;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.TreeSet;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.ArrayList;
import com.adventnet.db.adapter.DBAdapter;
import javax.sql.DataSource;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

public class HierarchyProcessor
{
    private static final Logger LOGGER;
    private List<Node> rootNodes;
    protected Map<String, Node> tables;
    Map<Integer, Set<String>> nodeLevels;
    private DataSource sourceDS;
    private DBAdapter srcDbAdapter;
    
    public HierarchyProcessor(final List<String> allTableNames, final DataSource sourceDS, final DBAdapter srcDbAdapter) throws Exception {
        this.rootNodes = new ArrayList<Node>();
        this.tables = new TreeMap<String, Node>(String.CASE_INSENSITIVE_ORDER);
        this.nodeLevels = new TreeMap<Integer, Set<String>>();
        this.sourceDS = null;
        this.srcDbAdapter = null;
        if (sourceDS == null) {
            throw new Exception("Source DataSource doesn't exists..Check the sourceDb is running or not");
        }
        if (srcDbAdapter == null) {
            throw new Exception("Source DbAdapter doesn't exists..Check the adapter is initialised or not");
        }
        this.sourceDS = sourceDS;
        this.srcDbAdapter = srcDbAdapter;
        for (final String tabName : allTableNames) {
            this.tables.put(tabName, new Node(tabName));
        }
        final ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(10);
        for (final String tabName2 : this.tables.keySet()) {
            newFixedThreadPool.submit((Callable<Object>)new MapHierarchyNodes(tabName2));
        }
        newFixedThreadPool.shutdown();
        this.waitForMapCompletion(newFixedThreadPool);
        for (final Node node : this.tables.values()) {
            if (node.isRootNode()) {
                this.rootNodes.add(node);
                node.setLevel(1);
            }
        }
        for (final Node node : this.rootNodes) {
            this.fillLevelMap(node, this.nodeLevels);
        }
        int tables = 0;
        HierarchyProcessor.LOGGER.info("Splitted levels.");
        for (final Integer level : this.nodeLevels.keySet()) {
            tables += this.nodeLevels.get(level).size();
            HierarchyProcessor.LOGGER.info(this.nodeLevels.get(level).toString());
        }
        HierarchyProcessor.LOGGER.info("Total no of tables in DB :: " + allTableNames.size());
        HierarchyProcessor.LOGGER.info("Total no of tables in Hierarchy :: " + tables);
    }
    
    public Collection<Set<String>> getLevels() {
        return this.nodeLevels.values();
    }
    
    public Set<String> getLevel(final Integer level) {
        return this.nodeLevels.get(level);
    }
    
    public int getTotalLevels() {
        return this.nodeLevels.size();
    }
    
    private void fillLevelMap(final Node node, final Map<Integer, Set<String>> nodeLevels) {
        if (nodeLevels.containsKey(node.getLevel())) {
            nodeLevels.get(node.getLevel()).add(node.getNodeName());
        }
        else {
            final Set<String> names = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            names.add(node.getNodeName());
            nodeLevels.put(node.getLevel(), names);
        }
        for (final Node child : node.getAllChildNodes()) {
            this.fillLevelMap(child, nodeLevels);
        }
    }
    
    private void waitForMapCompletion(final ExecutorService pool) throws InterruptedException {
        HierarchyProcessor.LOGGER.info("Waiting for Hierarchy mapping completion");
        int threshold = 0;
        while (((ThreadPoolExecutor)pool).getActiveCount() != 0 || ((ThreadPoolExecutor)pool).getQueue().size() != 0) {
            if (threshold % 30 == 0) {
                HierarchyProcessor.LOGGER.info("Hierarchy mapping in progress ::: " + ((ThreadPoolExecutor)pool).getActiveCount());
                HierarchyProcessor.LOGGER.info("Hierarchy mapping pending task ::: " + ((ThreadPoolExecutor)pool).getQueue().size());
                threshold = 0;
            }
            ++threshold;
            Thread.sleep(1000L);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(HierarchyProcessor.class.getName());
    }
    
    class MapHierarchyNodes implements Callable<Boolean>
    {
        private String tableName;
        
        public MapHierarchyNodes(final String tableName) {
            this.tableName = tableName;
        }
        
        @Override
        public Boolean call() throws Exception {
            ResultSet importedKeys = null;
            Connection connection = null;
            try {
                boolean processed = false;
                connection = HierarchyProcessor.this.sourceDS.getConnection();
                importedKeys = HierarchyProcessor.this.srcDbAdapter.getFKMetaData(connection, this.tableName);
                while (importedKeys.next()) {
                    processed = true;
                    final String parentTableName = importedKeys.getString("PKTABLE_NAME");
                    final Node parentNode = HierarchyProcessor.this.tables.get(parentTableName);
                    final Node childNode = HierarchyProcessor.this.tables.get(this.tableName);
                    HierarchyProcessor.LOGGER.info("New parent " + parentTableName + " identified for " + this.tableName);
                    if (parentNode == null || childNode == null) {
                        HierarchyProcessor.LOGGER.warning("Graph edges cannot be null.P[" + parentTableName + " is null " + (parentNode == null) + "] C[" + this.tableName + " is null " + (childNode == null) + "]");
                    }
                    else {
                        if (parentTableName.equalsIgnoreCase(this.tableName)) {
                            continue;
                        }
                        childNode.addParent(parentNode);
                        parentNode.addChild(childNode);
                    }
                }
                if (!processed) {
                    HierarchyProcessor.LOGGER.warning("FK meta information not found for table " + this.tableName);
                }
            }
            catch (final Throwable e) {
                e.printStackTrace();
                try {
                    if (importedKeys != null) {
                        importedKeys.close();
                    }
                }
                catch (final SQLException ex) {}
                try {
                    if (connection != null) {
                        connection.close();
                    }
                }
                catch (final SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
            finally {
                try {
                    if (importedKeys != null) {
                        importedKeys.close();
                    }
                }
                catch (final SQLException ex2) {}
                try {
                    if (connection != null) {
                        connection.close();
                    }
                }
                catch (final SQLException sqle2) {
                    sqle2.printStackTrace();
                }
            }
            return Boolean.TRUE;
        }
    }
}
