package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;

public class CreateIndexClause
{
    private String uniqueOrBitMapString;
    private String clusteredOrNonClustered;
    private String indexOrKey;
    private TableObject tableObject;
    private TableObject indexObject;
    private String on;
    private String openBraces;
    private ArrayList indexColumns;
    private String closedBrace;
    private String cluster;
    private String clusterName;
    private String with;
    private ArrayList padIndexArrayList;
    private String padIndexComma;
    private ArrayList physicalAttributes;
    private LocalOrGlobalPartitionTable localOrGlobalPartitionTable;
    private String onGoIdentifier;
    private String parallelOrNoParallel;
    private String parallelIdentifier;
    private String using;
    private String tree;
    private String removeIndent;
    private boolean isTenroxRequirement;
    private String objectName;
    private UserObjectContext context;
    private boolean isToOracle;
    private boolean isToSybase;
    private boolean isToTeradata;
    
    public CreateIndexClause() {
        this.isTenroxRequirement = false;
        this.context = null;
        this.isToOracle = false;
        this.isToSybase = false;
        this.isToTeradata = false;
    }
    
    public void setObjectName(final String name) {
        this.objectName = name;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public String getObjectName() {
        return this.objectName;
    }
    
    public void setUniqueOrBitMapString(final String uniqueOrBitMapString) {
        this.uniqueOrBitMapString = uniqueOrBitMapString;
    }
    
    public void setClusteredOrNonClustered(final String clusteredOrNonClustered) {
        this.clusteredOrNonClustered = clusteredOrNonClustered;
    }
    
    public void setIndexOrKey(final String indexOrKey) {
        this.indexOrKey = indexOrKey;
    }
    
    public void setIndexName(final TableObject indexObject) {
        this.indexObject = indexObject;
    }
    
    public void setOn(final String on) {
        this.on = on;
    }
    
    public void setTableOrView(final TableObject tableObject) {
        this.tableObject = tableObject;
    }
    
    public void setOpenBraces(final String openBraces) {
        this.openBraces = openBraces;
    }
    
    public void setIndexColumns(final ArrayList indexColumns) {
        this.indexColumns = indexColumns;
    }
    
    public void setClosedBraces(final String closedBrace) {
        this.closedBrace = closedBrace;
    }
    
    public void setCluster(final String cluster) {
        this.cluster = cluster;
    }
    
    public void setClusterName(final String clusterName) {
        this.clusterName = clusterName;
    }
    
    public void setWith(final String with) {
        this.with = with;
    }
    
    public void setToOracle(final boolean isToOracle) {
        this.isToOracle = isToOracle;
    }
    
    public void setToSybase(final boolean isToSybase) {
        this.isToSybase = isToSybase;
    }
    
    public void setToTeradata(final boolean isToTeradata) {
        this.isToTeradata = isToTeradata;
    }
    
    public void setPadIndexArrayList(final ArrayList padIndexArrayList) {
        this.padIndexArrayList = padIndexArrayList;
    }
    
    public void setPadIndexComma(final String padIndexComma) {
        this.padIndexComma = padIndexComma;
    }
    
    public void setPhysicalAttributes(final ArrayList physicalAttributes) {
        this.physicalAttributes = physicalAttributes;
    }
    
    public void setLocalOrGlobalPartitionTable(final LocalOrGlobalPartitionTable localOrGlobalPartitionTable) {
        this.localOrGlobalPartitionTable = localOrGlobalPartitionTable;
    }
    
    public void setOnGoIdentifier(final String onGoIdentifier) {
        this.onGoIdentifier = onGoIdentifier;
    }
    
    public void setParallelOrNoParallel(final String parallelOrNoParallel) {
        this.parallelOrNoParallel = parallelOrNoParallel;
    }
    
    public void setParallelIdentifier(final String parallelIdentifier) {
        this.parallelIdentifier = parallelIdentifier;
    }
    
    public void setUsing(final String using) {
        this.using = using;
    }
    
    public void setTree(final String tree) {
        this.tree = tree;
    }
    
    public String getUniqueOrBitMapString() {
        return this.uniqueOrBitMapString;
    }
    
    public String getClusteredOrNonClustered() {
        return this.clusteredOrNonClustered;
    }
    
    public String getIndexOrKey() {
        return this.indexOrKey;
    }
    
    public TableObject getIndexName() {
        return this.indexObject;
    }
    
    public String getOn() {
        return this.on;
    }
    
    public TableObject getTableOrView() {
        return this.tableObject;
    }
    
    public String getOpenBraces() {
        return this.openBraces;
    }
    
    public ArrayList getIndexColumns() {
        return this.indexColumns;
    }
    
    public String getClosedBraces() {
        return this.closedBrace;
    }
    
    public String getCluster() {
        return this.cluster;
    }
    
    public String getClusterName() {
        return this.clusterName;
    }
    
    public String getWith() {
        return this.with;
    }
    
    public ArrayList getPadIndexArrayList() {
        return this.padIndexArrayList;
    }
    
    public String getPadIndexComma() {
        return this.padIndexComma;
    }
    
    public LocalOrGlobalPartitionTable getLocalOrGlobalPartitionTable() {
        return this.localOrGlobalPartitionTable;
    }
    
    public ArrayList getPhysicalAttributes() {
        return this.physicalAttributes;
    }
    
    public String getOnGoIdentifier() {
        return this.onGoIdentifier;
    }
    
    public String getParallelOrNoParallel() {
        return this.parallelOrNoParallel;
    }
    
    public String getParallelIdentifier() {
        return this.parallelIdentifier;
    }
    
    public String getUsing() {
        return this.using;
    }
    
    public String getTree() {
        return this.tree;
    }
    
    public String removeIndent(String str) {
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        return str;
    }
    
    public CreateIndexClause toANSI() throws ConvertException {
        final CreateIndexClause cic = this.copyObjectValues();
        cic.setUniqueOrBitMapString(null);
        cic.setClusteredOrNonClustered(null);
        if (cic.getIndexOrKey() != null) {
            final String tempIndexOrKey = cic.getIndexOrKey();
            if (tempIndexOrKey.equalsIgnoreCase("key")) {
                cic.setIndexOrKey("Index");
            }
        }
        if (cic.getIndexName() != null) {
            final TableObject tempIndexObject = cic.getIndexName();
            String ownerName = tempIndexObject.getOwner();
            String userName = tempIndexObject.getUser();
            String tableName = tempIndexObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempIndexObject.setOwner(ownerName);
            tempIndexObject.setUser(userName);
            tempIndexObject.setTableName(tableName);
            tempIndexObject.toANSISQL();
        }
        if (cic.getOn() != null) {
            cic.getOn();
        }
        if (cic.getTableOrView() != null) {
            final TableObject tempTableObject = cic.getTableOrView();
            String ownerName = tempTableObject.getOwner();
            String userName = tempTableObject.getUser();
            String tableName = tempTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempTableObject.setOwner(ownerName);
            tempTableObject.setUser(userName);
            tempTableObject.setTableName(tableName);
            tempTableObject.toANSISQL();
        }
        if (cic.getOpenBraces() != null) {
            cic.getOpenBraces();
        }
        if (cic.getIndexColumns() != null) {
            final ArrayList ansiIndexColumnArrayList = new ArrayList();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                final IndexColumn ansiIndexColumn = indexColumn.toANSI();
                ansiIndexColumnArrayList.add(ansiIndexColumn);
            }
            cic.setIndexColumns(ansiIndexColumnArrayList);
        }
        cic.setWith(null);
        cic.setPadIndexArrayList(null);
        cic.setPadIndexComma(null);
        if (cic.getClosedBraces() != null) {
            cic.getClosedBraces();
        }
        cic.setPhysicalAttributes(null);
        cic.setLocalOrGlobalPartitionTable(null);
        cic.setOnGoIdentifier(null);
        cic.setParallelOrNoParallel(null);
        cic.setParallelIdentifier(null);
        cic.setUsing(null);
        cic.setTree(null);
        return cic;
    }
    
    public CreateIndexClause toDB2() throws ConvertException {
        final CreateIndexClause cic = this.copyObjectValues();
        if (cic.getUniqueOrBitMapString() != null) {
            final String tempUniqueOrBitMapString = cic.getUniqueOrBitMapString();
            if (tempUniqueOrBitMapString.equalsIgnoreCase("CLUSTER") || tempUniqueOrBitMapString.equalsIgnoreCase("DISTINCT") || tempUniqueOrBitMapString.equalsIgnoreCase("BITMAP")) {
                cic.setUniqueOrBitMapString("UNIQUE");
            }
        }
        cic.setClusteredOrNonClustered(null);
        if (cic.getIndexOrKey() != null) {
            final String tempIndexOrKey = cic.getIndexOrKey();
            if (tempIndexOrKey.equalsIgnoreCase("key")) {
                cic.setIndexOrKey("Index");
            }
        }
        if (cic.getIndexName() != null) {
            final TableObject cqsTableObject = cic.getIndexName();
            String ownerName = cqsTableObject.getOwner();
            String userName = cqsTableObject.getUser();
            String tableName = cqsTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (SwisSQLAPI.truncateTableNameForDB2 && tableName.length() > 18) {
                if (tableName.indexOf("\"") != -1) {
                    tableName = tableName.substring(0, 11) + "_ADV" + SwisSQLAPI.truncateIndexCount + "\"";
                    ++SwisSQLAPI.truncateIndexCount;
                }
                else {
                    tableName = tableName.substring(0, 12) + "_ADV" + SwisSQLAPI.truncateIndexCount;
                    ++SwisSQLAPI.truncateIndexCount;
                }
            }
            cqsTableObject.setOwner(ownerName);
            cqsTableObject.setUser(userName);
            cqsTableObject.setTableName(tableName);
            cqsTableObject.toDB2();
        }
        if (cic.getOn() != null) {
            cic.getOn();
        }
        if (cic.getTableOrView() != null) {
            final TableObject orgTableObject = cic.getTableOrView();
            String table_name = orgTableObject.getTableName();
            String ownerName2 = orgTableObject.getOwner();
            String userName2 = orgTableObject.getUser();
            if (ownerName2 != null && ((ownerName2.startsWith("[") && ownerName2.endsWith("]")) || (ownerName2.startsWith("`") && ownerName2.endsWith("`")))) {
                ownerName2 = ownerName2.substring(1, ownerName2.length() - 1);
                if (ownerName2.indexOf(32) != -1) {
                    ownerName2 = "\"" + ownerName2 + "\"";
                }
            }
            if (userName2 != null && ((userName2.startsWith("[") && userName2.endsWith("]")) || (userName2.startsWith("`") && userName2.endsWith("`")))) {
                userName2 = userName2.substring(1, userName2.length() - 1);
                if (userName2.indexOf(32) != -1) {
                    userName2 = "\"" + userName2 + "\"";
                }
            }
            if (table_name != null && ((table_name.startsWith("[") && table_name.endsWith("]")) || (table_name.startsWith("`") && table_name.endsWith("`")))) {
                table_name = table_name.substring(1, table_name.length() - 1);
                if (table_name.indexOf(32) != -1) {
                    table_name = "\"" + table_name + "\"";
                }
            }
            orgTableObject.setOwner(ownerName2);
            orgTableObject.setUser(userName2);
            orgTableObject.setTableName(table_name);
            orgTableObject.toOracle();
        }
        if (cic.getOpenBraces() != null) {
            cic.getOpenBraces();
        }
        if (cic.getIndexColumns() != null) {
            final ArrayList db2IndexColumnArrayList = new ArrayList();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                final IndexColumn db2IndexColumn = indexColumn.toDB2();
                db2IndexColumnArrayList.add(db2IndexColumn);
            }
            cic.setIndexColumns(db2IndexColumnArrayList);
        }
        cic.setWith(null);
        cic.setPadIndexArrayList(null);
        cic.setPadIndexComma(null);
        if (cic.getClosedBraces() != null) {
            cic.getClosedBraces();
        }
        cic.setPhysicalAttributes(null);
        cic.setLocalOrGlobalPartitionTable(null);
        cic.setOnGoIdentifier(null);
        cic.setParallelOrNoParallel(null);
        cic.setParallelIdentifier(null);
        cic.setUsing(null);
        cic.setTree(null);
        return cic;
    }
    
    public CreateIndexClause toInformix() throws ConvertException {
        final CreateIndexClause cic = this.copyObjectValues();
        if (cic.getUniqueOrBitMapString() != null) {
            final String tempUniqueOrBitMapString = cic.getUniqueOrBitMapString();
            if (tempUniqueOrBitMapString.equalsIgnoreCase("BITMAP")) {
                cic.setUniqueOrBitMapString("UNIQUE");
            }
        }
        cic.setClusteredOrNonClustered(null);
        if (cic.getIndexOrKey() != null) {
            final String tempIndexOrKey = cic.getIndexOrKey();
            if (tempIndexOrKey.equalsIgnoreCase("key")) {
                cic.setIndexOrKey("Index");
            }
        }
        if (cic.getIndexName() != null) {
            final TableObject tempIndexObject = cic.getIndexName();
            String ownerName = tempIndexObject.getOwner();
            String userName = tempIndexObject.getUser();
            String tableName = tempIndexObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempIndexObject.setOwner(ownerName);
            tempIndexObject.setUser(userName);
            tempIndexObject.setTableName(tableName);
            tempIndexObject.toInformix();
            cic.setIndexName(tempIndexObject);
        }
        if (cic.getOn() != null) {
            cic.getOn();
        }
        if (cic.getTableOrView() != null) {
            final TableObject tempTableObject = cic.getTableOrView();
            tempTableObject.toInformix();
        }
        if (cic.getOpenBraces() != null) {
            cic.getOpenBraces();
        }
        if (cic.getIndexColumns() != null) {
            final ArrayList informixIndexColumnArrayList = new ArrayList();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                final IndexColumn informixIndexColumn = indexColumn.toInformix();
                informixIndexColumnArrayList.add(informixIndexColumn);
            }
            cic.setIndexColumns(informixIndexColumnArrayList);
        }
        cic.setWith(null);
        cic.setPadIndexArrayList(null);
        cic.setPadIndexComma(null);
        if (cic.getClosedBraces() != null) {
            cic.getClosedBraces();
        }
        cic.setPhysicalAttributes(null);
        cic.setLocalOrGlobalPartitionTable(null);
        cic.setOnGoIdentifier(null);
        cic.setParallelOrNoParallel(null);
        cic.setParallelIdentifier(null);
        cic.setUsing(null);
        cic.setTree(null);
        return cic;
    }
    
    public CreateIndexClause toMSSQLServer() throws ConvertException {
        final CreateIndexClause cic = this.copyObjectValues();
        if (cic.getUniqueOrBitMapString() != null) {
            final String tempUniqueOrBitMapString = cic.getUniqueOrBitMapString();
            if (tempUniqueOrBitMapString.equalsIgnoreCase("CLUSTER") || tempUniqueOrBitMapString.equalsIgnoreCase("DISTINCT") || tempUniqueOrBitMapString.equalsIgnoreCase("BITMAP")) {
                cic.setUniqueOrBitMapString("UNIQUE");
            }
        }
        if (cic.getClusteredOrNonClustered() != null) {
            cic.getClusteredOrNonClustered();
        }
        if (cic.getIndexOrKey() != null) {
            final String tempIndexOrKey = cic.getIndexOrKey();
            if (tempIndexOrKey.equalsIgnoreCase("key")) {
                cic.setIndexOrKey("Index");
            }
        }
        if (cic.getIndexName() != null) {
            final TableObject tempIndexObject = cic.getIndexName();
            String ownerName = tempIndexObject.getOwner();
            String userName = tempIndexObject.getUser();
            String tableName = tempIndexObject.getTableName();
            if (SwisSQLOptions.TRUNCATE_ORACLE_SCHEMA_INFORMATION) {
                userName = null;
                ownerName = null;
            }
            if (SwisSQLOptions.EnableDeltekSpecificConversions && userName != null && userName.trim().equalsIgnoreCase("DELTEK")) {
                userName = null;
            }
            if (ownerName != null && ownerName.startsWith("`") && ownerName.endsWith("`")) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && userName.startsWith("`") && userName.endsWith("`")) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && tableName.startsWith("`") && tableName.endsWith("`")) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempIndexObject.setOwner(ownerName);
            tempIndexObject.setUser(userName);
            tempIndexObject.setTableName(tableName);
            tempIndexObject.toMSSQLServer();
        }
        if (cic.getOn() != null) {
            cic.getOn();
        }
        if (cic.getTableOrView() != null) {
            final TableObject tempTableObject = cic.getTableOrView();
            String ownerName = tempTableObject.getOwner();
            String userName = tempTableObject.getUser();
            String tableName = tempTableObject.getTableName();
            if (ownerName != null && ownerName.startsWith("`") && ownerName.endsWith("`")) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && userName.startsWith("`") && userName.endsWith("`")) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && tableName.startsWith("`") && tableName.endsWith("`")) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempTableObject.setOwner(ownerName);
            tempTableObject.setUser(userName);
            tempTableObject.setTableName(tableName);
            tempTableObject.toMSSQLServer();
        }
        if (cic.getOpenBraces() != null) {
            cic.getOpenBraces();
        }
        if (cic.getIndexColumns() != null) {
            final ArrayList msSQLServerIndexColumnArrayList = new ArrayList();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                final IndexColumn msSQLServerIndexColumn = indexColumn.toMSSQLServer();
                msSQLServerIndexColumnArrayList.add(msSQLServerIndexColumn);
            }
            cic.setIndexColumns(msSQLServerIndexColumnArrayList);
        }
        if (cic.getClosedBraces() != null) {
            cic.getClosedBraces();
        }
        if (cic.getWith() != null) {
            cic.getWith();
        }
        if (cic.getPadIndexArrayList() != null) {
            final ArrayList msSQLServerPadIndexArrayList = new ArrayList();
            final ArrayList tempPadIndexArrayList = cic.getPadIndexArrayList();
            final int size = tempPadIndexArrayList.size();
            for (int j = 0; j < tempPadIndexArrayList.size(); ++j) {
                final PhysicalAttributesClause physicalAttributesClause = tempPadIndexArrayList.get(j);
                final PhysicalAttributesClause msSQLServerPhysicalAttributesClause = physicalAttributesClause.toMSSQLServer();
                msSQLServerPadIndexArrayList.add(msSQLServerPhysicalAttributesClause);
            }
            cic.setPadIndexArrayList(msSQLServerPadIndexArrayList);
        }
        if (cic.getPadIndexComma() != null) {
            cic.getPadIndexComma();
        }
        if (cic.getOnGoIdentifier() != null) {
            cic.getOnGoIdentifier();
        }
        if (cic.getPhysicalAttributes() != null) {
            final ArrayList tempPhysicalAttributes = cic.getPhysicalAttributes();
            final ArrayList msSQLServerPhysicalAttributesArrayList = new ArrayList();
            for (int k = 0; k < tempPhysicalAttributes.size(); ++k) {
                final PhysicalAttributesClause physicalAttributesClause2 = tempPhysicalAttributes.get(k);
                final PhysicalAttributesClause msSQLServerPhysicalAttributesClause2 = physicalAttributesClause2.toMSSQLServer();
                if (!msSQLServerPhysicalAttributesClause2.toString().equalsIgnoreCase("WITH ALLOW_DUP_ROW")) {
                    msSQLServerPhysicalAttributesArrayList.add(msSQLServerPhysicalAttributesClause2);
                }
            }
            cic.setPhysicalAttributes(msSQLServerPhysicalAttributesArrayList);
        }
        if (cic.getPhysicalAttributes() != null && cic.getPhysicalAttributes().size() != 0) {
            final ArrayList tempPadIndexArrayList2 = cic.getPhysicalAttributes();
            final PhysicalAttributesClause tempPhysicalAttributesClause = tempPadIndexArrayList2.get(0);
            final PhysicalAttributesClause msSQLServerPhysicalAttributesClause3 = tempPhysicalAttributesClause.toMSSQLServer();
            if (msSQLServerPhysicalAttributesClause3.getWith() == null && (msSQLServerPhysicalAttributesClause3.getFillFactor() != null || msSQLServerPhysicalAttributesClause3.getPadIndex() != null || (msSQLServerPhysicalAttributesClause3.getDiskAttr() != null && msSQLServerPhysicalAttributesClause3.getDiskAttr().get("IGNORE_DUP_KEY") != null) || msSQLServerPhysicalAttributesClause3.getDropExisting() != null || msSQLServerPhysicalAttributesClause3.getStatisticsNoreCompute() != null)) {
                cic.setWith("WITH");
            }
            if (msSQLServerPhysicalAttributesClause3.getDiskAttr() != null && msSQLServerPhysicalAttributesClause3.getDiskAttr().get("IGNORE_DUP_KEY") != null) {
                cic.setUniqueOrBitMapString("UNIQUE");
            }
        }
        cic.setLocalOrGlobalPartitionTable(null);
        cic.setParallelOrNoParallel(null);
        cic.setParallelIdentifier(null);
        cic.setUsing(null);
        if (cic.getTree() != null) {
            final String tempTree = cic.getTree();
            if (tempTree.equalsIgnoreCase("Btree")) {
                cic.setClusteredOrNonClustered("Clustered");
                cic.setTree(null);
            }
            else {
                cic.setTree(null);
            }
        }
        return cic;
    }
    
    public CreateIndexClause toSybase() throws ConvertException {
        final CreateIndexClause cic = this.copyObjectValues();
        cic.setToSybase(true);
        if (cic.getUniqueOrBitMapString() != null) {
            final String tempUniqueOrBitMapString = cic.getUniqueOrBitMapString();
            if (tempUniqueOrBitMapString.equalsIgnoreCase("CLUSTER") || tempUniqueOrBitMapString.equalsIgnoreCase("DISTINCT") || tempUniqueOrBitMapString.equalsIgnoreCase("BITMAP")) {
                cic.setUniqueOrBitMapString("UNIQUE");
            }
        }
        if (cic.getClusteredOrNonClustered() != null) {
            cic.getClusteredOrNonClustered();
        }
        if (cic.getIndexOrKey() != null) {
            final String tempIndexOrKey = cic.getIndexOrKey();
            if (tempIndexOrKey.equalsIgnoreCase("key")) {
                cic.setIndexOrKey("Index");
            }
        }
        if (cic.getIndexName() != null) {
            final TableObject tempIndexObject = cic.getIndexName();
            String ownerName = tempIndexObject.getOwner();
            String userName = tempIndexObject.getUser();
            String tableName = tempIndexObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempIndexObject.setOwner(ownerName);
            tempIndexObject.setUser(userName);
            tempIndexObject.setTableName(tableName);
            tempIndexObject.toSybase();
            cic.setIndexName(tempIndexObject);
        }
        if (cic.getOn() != null) {
            cic.getOn();
        }
        if (cic.getTableOrView() != null) {
            final TableObject tempTableObject = cic.getTableOrView();
            tempTableObject.toSybase();
        }
        if (cic.getOpenBraces() != null) {
            cic.getOpenBraces();
        }
        if (cic.getIndexColumns() != null) {
            final ArrayList sybaseIndexColumnArrayList = new ArrayList();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                final IndexColumn sybaseIndexColumn = indexColumn.toSybase();
                sybaseIndexColumnArrayList.add(sybaseIndexColumn);
            }
            cic.setIndexColumns(sybaseIndexColumnArrayList);
        }
        if (cic.getClosedBraces() != null) {
            cic.getClosedBraces();
        }
        if (cic.getWith() != null) {
            cic.getWith();
        }
        if (cic.getPadIndexArrayList() != null) {
            final ArrayList sybasePadIndexArrayList = new ArrayList();
            final ArrayList tempPadIndexArrayList = cic.getPadIndexArrayList();
            final int size = tempPadIndexArrayList.size();
            for (int j = 0; j < tempPadIndexArrayList.size(); ++j) {
                final PhysicalAttributesClause physicalAttributesClause = tempPadIndexArrayList.get(j);
                final PhysicalAttributesClause sybasePhysicalAttributesClause = physicalAttributesClause.toSybase();
                sybasePadIndexArrayList.add(sybasePhysicalAttributesClause);
            }
            cic.setPadIndexArrayList(sybasePadIndexArrayList);
        }
        if (cic.getPadIndexComma() != null) {
            cic.getPadIndexComma();
        }
        if (cic.getOnGoIdentifier() != null) {
            cic.getOnGoIdentifier();
        }
        if (cic.getPhysicalAttributes() != null) {
            final ArrayList tempPhysicalAttributes = cic.getPhysicalAttributes();
            final ArrayList sybasePhysicalAttributesArrayList = new ArrayList();
            for (int k = 0; k < tempPhysicalAttributes.size(); ++k) {
                final PhysicalAttributesClause physicalAttributesClause2 = tempPhysicalAttributes.get(k);
                final PhysicalAttributesClause sybasePhysicalAttributesClause2 = physicalAttributesClause2.toSybase();
                sybasePhysicalAttributesArrayList.add(sybasePhysicalAttributesClause2);
            }
            cic.setPhysicalAttributes(sybasePhysicalAttributesArrayList);
        }
        if (cic.getPhysicalAttributes() != null && cic.getPhysicalAttributes().size() != 0) {
            final ArrayList tempPadIndexArrayList2 = cic.getPhysicalAttributes();
            final PhysicalAttributesClause tempPhysicalAttributesClause = tempPadIndexArrayList2.get(0);
            final PhysicalAttributesClause sybasePhysicalAttributesClause3 = tempPhysicalAttributesClause.toSybase();
            if (sybasePhysicalAttributesClause3.getWith() == null && (sybasePhysicalAttributesClause3.getFillFactor() != null || sybasePhysicalAttributesClause3.getPadIndex() != null || (sybasePhysicalAttributesClause3.getDiskAttr() != null && sybasePhysicalAttributesClause3.getDiskAttr().get("IGNORE_DUP_KEY") != null) || sybasePhysicalAttributesClause3.getDropExisting() != null || sybasePhysicalAttributesClause3.getStatisticsNoreCompute() != null)) {
                cic.setWith("WITH");
            }
        }
        cic.setLocalOrGlobalPartitionTable(null);
        cic.setParallelOrNoParallel(null);
        cic.setParallelIdentifier(null);
        cic.setUsing(null);
        if (cic.getTree() != null) {
            final String tempTree = cic.getTree();
            if (tempTree.equalsIgnoreCase("Btree")) {
                cic.setClusteredOrNonClustered("Clustered");
                cic.setTree(null);
            }
            else {
                cic.setTree(null);
            }
        }
        return cic;
    }
    
    public CreateIndexClause toMySQL() throws ConvertException {
        final CreateIndexClause cic = this.copyObjectValues();
        if (cic.getUniqueOrBitMapString() != null) {
            final String tempUniqueOrBitMapString = cic.getUniqueOrBitMapString();
            if (tempUniqueOrBitMapString.equalsIgnoreCase("CLUSTER") || tempUniqueOrBitMapString.equalsIgnoreCase("DISTINCT") || tempUniqueOrBitMapString.equalsIgnoreCase("BITMAP")) {
                cic.setUniqueOrBitMapString("UNIQUE");
            }
        }
        cic.setClusteredOrNonClustered(null);
        if (cic.getIndexOrKey() != null) {
            cic.getIndexOrKey();
        }
        if (cic.getIndexName() != null) {
            final TableObject tempIndexObject = cic.getIndexName();
            String ownerName = tempIndexObject.getOwner();
            String userName = tempIndexObject.getUser();
            String tableName = tempIndexObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempIndexObject.setOwner(ownerName);
            tempIndexObject.setUser(userName);
            tempIndexObject.setTableName(tableName);
            final String tableNameStr = tempIndexObject.getTableName();
            tempIndexObject.toMySQL();
            cic.setIndexName(tempIndexObject);
        }
        if (cic.getOn() != null) {
            cic.getOn();
        }
        if (cic.getTableOrView() != null) {
            final TableObject orgTableObject = cic.getTableOrView();
            String table_name = orgTableObject.getTableName();
            String ownerName2 = orgTableObject.getOwner();
            String userName2 = orgTableObject.getUser();
            if (ownerName2 != null && ((ownerName2.startsWith("[") && ownerName2.endsWith("]")) || (ownerName2.startsWith("`") && ownerName2.endsWith("`")))) {
                ownerName2 = ownerName2.substring(1, ownerName2.length() - 1);
                if (ownerName2.indexOf(32) != -1) {
                    ownerName2 = "\"" + ownerName2 + "\"";
                }
            }
            if (userName2 != null && ((userName2.startsWith("[") && userName2.endsWith("]")) || (userName2.startsWith("`") && userName2.endsWith("`")))) {
                userName2 = userName2.substring(1, userName2.length() - 1);
                if (userName2.indexOf(32) != -1) {
                    userName2 = "\"" + userName2 + "\"";
                }
            }
            if (table_name != null && ((table_name.startsWith("[") && table_name.endsWith("]")) || (table_name.startsWith("`") && table_name.endsWith("`")))) {
                table_name = table_name.substring(1, table_name.length() - 1);
                if (table_name.indexOf(32) != -1) {
                    table_name = "\"" + table_name + "\"";
                }
            }
            orgTableObject.setOwner(ownerName2);
            orgTableObject.setUser(userName2);
            orgTableObject.setTableName(table_name);
            orgTableObject.toMySQL();
        }
        if (cic.getOpenBraces() != null) {
            cic.getOpenBraces();
        }
        if (cic.getIndexColumns() != null) {
            final ArrayList mySQLIndexColumnArrayList = new ArrayList();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                final IndexColumn mySQLIndexColumn = indexColumn.toMySQL();
                mySQLIndexColumnArrayList.add(mySQLIndexColumn);
            }
            cic.setIndexColumns(mySQLIndexColumnArrayList);
        }
        cic.setWith(null);
        cic.setPadIndexArrayList(null);
        cic.setPadIndexComma(null);
        if (cic.getClosedBraces() != null) {
            cic.getClosedBraces();
        }
        cic.setPhysicalAttributes(null);
        cic.setLocalOrGlobalPartitionTable(null);
        cic.setOnGoIdentifier(null);
        cic.setParallelOrNoParallel(null);
        cic.setParallelIdentifier(null);
        cic.setUsing(null);
        cic.setTree(null);
        return cic;
    }
    
    public CreateIndexClause toOracle() throws ConvertException {
        final CreateIndexClause cic = this.copyObjectValues();
        if (cic.getUniqueOrBitMapString() != null) {
            final String tempUniqueOrBitMapString = cic.getUniqueOrBitMapString();
            if (tempUniqueOrBitMapString.equalsIgnoreCase("CLUSTER") || tempUniqueOrBitMapString.equalsIgnoreCase("DISTINCT")) {
                cic.setUniqueOrBitMapString("UNIQUE");
            }
        }
        cic.setClusteredOrNonClustered(null);
        cic.setToOracle(true);
        if (cic.getIndexOrKey() != null) {
            final String tempIndexOrKey = cic.getIndexOrKey();
            if (tempIndexOrKey.equalsIgnoreCase("key")) {
                cic.setIndexOrKey("INDEX");
            }
            else {
                cic.setIndexOrKey(tempIndexOrKey);
            }
        }
        if (cic.getIndexName() != null) {
            final TableObject tempIndexObject = cic.getIndexName();
            String ownerName = tempIndexObject.getOwner();
            String userName = tempIndexObject.getUser();
            String tableName = tempIndexObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (tableName.startsWith("#")) {
                tableName = tableName.substring(1);
            }
            tempIndexObject.setOwner(ownerName);
            tempIndexObject.setUser(userName);
            tempIndexObject.setTableName(tableName);
            final String tableNameStr = tempIndexObject.getTableName();
            tempIndexObject.toOracle();
            cic.setIndexName(tempIndexObject);
        }
        if (cic.getOn() != null) {
            cic.getOn();
        }
        if (cic.getTableOrView() != null) {
            final TableObject orgTableObject = cic.getTableOrView();
            String table_name = orgTableObject.getTableName();
            String ownerName2 = orgTableObject.getOwner();
            String userName2 = orgTableObject.getUser();
            if (ownerName2 != null && ((ownerName2.startsWith("[") && ownerName2.endsWith("]")) || (ownerName2.startsWith("`") && ownerName2.endsWith("`")))) {
                ownerName2 = ownerName2.substring(1, ownerName2.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || ownerName2.indexOf(32) != -1) {
                    ownerName2 = "\"" + ownerName2 + "\"";
                }
            }
            if (userName2 != null && ((userName2.startsWith("[") && userName2.endsWith("]")) || (userName2.startsWith("`") && userName2.endsWith("`")))) {
                userName2 = userName2.substring(1, userName2.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || userName2.indexOf(32) != -1) {
                    userName2 = "\"" + userName2 + "\"";
                }
            }
            if (table_name != null && ((table_name.startsWith("[") && table_name.endsWith("]")) || (table_name.startsWith("`") && table_name.endsWith("`")))) {
                table_name = table_name.substring(1, table_name.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || table_name.indexOf(32) != -1) {
                    table_name = "\"" + table_name + "\"";
                }
            }
            orgTableObject.setOwner(ownerName2);
            orgTableObject.setUser(userName2);
            orgTableObject.setTableName(table_name);
            orgTableObject.toOracle();
        }
        if (cic.getOpenBraces() != null) {
            cic.getOpenBraces();
        }
        if (cic.getIndexColumns() != null) {
            final ArrayList oracleIndexColumnArrayList = new ArrayList();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                final IndexColumn oracleIndexColumn = indexColumn.toOracle();
                oracleIndexColumnArrayList.add(oracleIndexColumn);
            }
            cic.setIndexColumns(oracleIndexColumnArrayList);
        }
        if (cic.getClosedBraces() != null) {
            cic.getClosedBraces();
        }
        if (cic.getClusterName() != null) {
            cic.getClusterName();
        }
        if (cic.getPhysicalAttributes() != null) {
            final ArrayList tempPhysicalAttributes = cic.getPhysicalAttributes();
            final ArrayList oraclePhysicalAttributesArrayList = new ArrayList();
            for (int j = 0; j < tempPhysicalAttributes.size(); ++j) {
                final PhysicalAttributesClause physicalAttributesClause = tempPhysicalAttributes.get(j);
                final PhysicalAttributesClause oraclePhysicalAttributesClause = physicalAttributesClause.toOracle();
                oraclePhysicalAttributesArrayList.add(oraclePhysicalAttributesClause);
            }
            cic.setPhysicalAttributes(oraclePhysicalAttributesArrayList);
        }
        if (cic.getPadIndexArrayList() != null) {
            final ArrayList oraclePadIndexArrayList = new ArrayList();
            final ArrayList tempPadIndexArrayList = cic.getPadIndexArrayList();
            for (int j = 0; j < tempPadIndexArrayList.size(); ++j) {
                final PhysicalAttributesClause physicalAttributesClause = tempPadIndexArrayList.get(j);
                final PhysicalAttributesClause oraclePhysicalAttributesClause = physicalAttributesClause.toOracle();
                oraclePadIndexArrayList.add(oraclePhysicalAttributesClause);
            }
            cic.setPadIndexArrayList(oraclePadIndexArrayList);
        }
        cic.setWith(null);
        cic.setPadIndexComma(null);
        if (cic.getParallelOrNoParallel() != null) {
            cic.getParallelOrNoParallel();
        }
        if (cic.getParallelIdentifier() != null) {
            cic.getParallelIdentifier();
        }
        if (cic.getLocalOrGlobalPartitionTable() != null) {
            final LocalOrGlobalPartitionTable tempLocalOrGlobalPartitionTable = cic.getLocalOrGlobalPartitionTable();
            final LocalOrGlobalPartitionTable oracleLocalOrGlobalPartitionTable = tempLocalOrGlobalPartitionTable.toOracle();
            cic.setLocalOrGlobalPartitionTable(oracleLocalOrGlobalPartitionTable);
        }
        cic.setOnGoIdentifier(null);
        cic.setUsing(null);
        cic.setTree(null);
        return cic;
    }
    
    public CreateIndexClause toPostgreSQL() throws ConvertException {
        final CreateIndexClause cic = this.copyObjectValues();
        if (cic.getUniqueOrBitMapString() != null) {
            final String tempUniqueOrBitMapString = cic.getUniqueOrBitMapString();
            if (tempUniqueOrBitMapString.equalsIgnoreCase("CLUSTER") || tempUniqueOrBitMapString.equalsIgnoreCase("DISTINCT") || tempUniqueOrBitMapString.equalsIgnoreCase("BITMAP")) {
                cic.setUniqueOrBitMapString("UNIQUE");
            }
        }
        if (cic.getClusteredOrNonClustered() != null) {
            final String tempClusteredOrNonClustered = cic.getClusteredOrNonClustered();
            if (tempClusteredOrNonClustered.equalsIgnoreCase("Clustered")) {
                cic.setTree("BTree");
            }
            cic.setUsing("Using");
            cic.setClusteredOrNonClustered(null);
        }
        if (cic.getIndexOrKey() != null) {
            final String tempIndexOrKey = cic.getIndexOrKey();
            if (tempIndexOrKey.equalsIgnoreCase("key")) {
                cic.setIndexOrKey("Index");
            }
        }
        if (cic.getIndexName() != null) {
            final TableObject tempIndexObject = cic.getIndexName();
            String ownerName = tempIndexObject.getOwner();
            String userName = tempIndexObject.getUser();
            String tableName = tempIndexObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempIndexObject.setOwner(ownerName);
            tempIndexObject.setUser(userName);
            tempIndexObject.setTableName(tableName);
            tempIndexObject.toPostgreSQL();
            cic.setIndexName(tempIndexObject);
        }
        if (cic.getOn() != null) {
            cic.getOn();
        }
        if (cic.getTableOrView() != null) {
            final TableObject tempTableObject = cic.getTableOrView();
            String ownerName = tempTableObject.getOwner();
            String userName = tempTableObject.getUser();
            String tableName = tempTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempTableObject.setOwner(ownerName);
            tempTableObject.setUser(userName);
            tempTableObject.setTableName(tableName);
            tempTableObject.toPostgreSQL();
        }
        if (cic.getIndexColumns() != null) {
            final ArrayList postgreSQLIndexColumnArrayList = new ArrayList();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                final IndexColumn postgreSQLIndexColumn = indexColumn.toPostgreSQL();
                postgreSQLIndexColumnArrayList.add(postgreSQLIndexColumn);
            }
            cic.setIndexColumns(postgreSQLIndexColumnArrayList);
        }
        if (cic.getUsing() != null) {
            cic.getUsing();
        }
        if (cic.getTree() != null) {
            cic.getTree();
        }
        if (cic.getOpenBraces() != null) {
            cic.getOpenBraces();
        }
        cic.setWith(null);
        cic.setPadIndexArrayList(null);
        cic.setPadIndexComma(null);
        if (cic.getClosedBraces() != null) {
            cic.getClosedBraces();
        }
        cic.setPhysicalAttributes(null);
        cic.setLocalOrGlobalPartitionTable(null);
        cic.setOnGoIdentifier(null);
        cic.setParallelOrNoParallel(null);
        cic.setParallelIdentifier(null);
        if (cic.getUsing() != null) {
            cic.getUsing();
        }
        if (cic.getTree() != null) {
            cic.getTree();
        }
        return cic;
    }
    
    public CreateIndexClause toTimesTen() throws ConvertException {
        final CreateIndexClause cic = this.copyObjectValues();
        if (cic.getUniqueOrBitMapString() != null) {
            if (cic.getUniqueOrBitMapString().equalsIgnoreCase("unique")) {
                cic.setUniqueOrBitMapString("UNIQUE");
            }
            else {
                cic.setUniqueOrBitMapString(null);
            }
        }
        if (cic.getClusteredOrNonClustered() != null) {
            cic.setClusteredOrNonClustered(null);
        }
        if (cic.getIndexOrKey() != null) {
            final String tempIndexOrKey = cic.getIndexOrKey();
            if (tempIndexOrKey.equalsIgnoreCase("key")) {
                cic.setIndexOrKey("INDEX");
            }
        }
        if (cic.getIndexName() != null) {
            final TableObject tempIndexObject = cic.getIndexName();
            String ownerName = tempIndexObject.getOwner();
            String tableName = tempIndexObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempIndexObject.setOwner(ownerName);
            tempIndexObject.setTableName(tableName);
            tempIndexObject.toTimesTen();
            cic.setIndexName(tempIndexObject);
        }
        if (cic.getTableOrView() != null) {
            final TableObject tempTableObject = cic.getTableOrView();
            tempTableObject.toTimesTen();
        }
        if (cic.getIndexColumns() != null) {
            final ArrayList indexColumnArrayList = new ArrayList();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                final IndexColumn timesTenIndexColumn = indexColumn.toTimesTen();
                indexColumnArrayList.add(timesTenIndexColumn);
            }
            cic.setIndexColumns(indexColumnArrayList);
        }
        if (cic.getWith() != null) {
            cic.setWith(null);
        }
        if (cic.getPadIndexArrayList() != null) {
            cic.setPadIndexArrayList(null);
        }
        if (cic.getPhysicalAttributes() != null) {
            cic.setPhysicalAttributes(null);
        }
        cic.setLocalOrGlobalPartitionTable(null);
        cic.setParallelOrNoParallel(null);
        cic.setParallelIdentifier(null);
        cic.setUsing(null);
        cic.setOnGoIdentifier(null);
        if (cic.getTree() != null) {
            cic.setTree(null);
        }
        return cic;
    }
    
    public CreateIndexClause toNetezza() throws ConvertException {
        final CreateIndexClause cic = this.copyObjectValues();
        cic.setUniqueOrBitMapString(null);
        cic.setClusteredOrNonClustered(null);
        if (cic.getIndexOrKey() != null) {
            final String tempIndexOrKey = cic.getIndexOrKey();
            if (tempIndexOrKey.equalsIgnoreCase("key")) {
                cic.setIndexOrKey("Index");
            }
        }
        if (cic.getIndexName() != null) {
            final TableObject tempIndexObject = cic.getIndexName();
            String ownerName = tempIndexObject.getOwner();
            String userName = tempIndexObject.getUser();
            String tableName = tempIndexObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempIndexObject.setOwner(ownerName);
            tempIndexObject.setUser(userName);
            tempIndexObject.setTableName(tableName);
            tempIndexObject.toNetezza();
        }
        if (cic.getOn() != null) {
            cic.getOn();
        }
        if (cic.getTableOrView() != null) {
            final TableObject tempTableObject = cic.getTableOrView();
            String ownerName = tempTableObject.getOwner();
            String userName = tempTableObject.getUser();
            String tableName = tempTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempTableObject.setOwner(ownerName);
            tempTableObject.setUser(userName);
            tempTableObject.setTableName(tableName);
            tempTableObject.toNetezza();
        }
        if (cic.getOpenBraces() != null) {
            cic.getOpenBraces();
        }
        if (cic.getIndexColumns() != null) {
            final ArrayList netezzaIndexColumnArrayList = new ArrayList();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                final IndexColumn netezzaIndexColumn = indexColumn.toNetezza();
                netezzaIndexColumnArrayList.add(netezzaIndexColumn);
            }
            cic.setIndexColumns(netezzaIndexColumnArrayList);
        }
        cic.setWith(null);
        cic.setPadIndexArrayList(null);
        cic.setPadIndexComma(null);
        if (cic.getClosedBraces() != null) {
            cic.getClosedBraces();
        }
        cic.setPhysicalAttributes(null);
        cic.setLocalOrGlobalPartitionTable(null);
        cic.setOnGoIdentifier(null);
        cic.setParallelOrNoParallel(null);
        cic.setParallelIdentifier(null);
        cic.setUsing(null);
        cic.setTree(null);
        return cic;
    }
    
    public CreateIndexClause toTeradata() throws ConvertException {
        final CreateIndexClause cic = this.copyObjectValues();
        cic.setToTeradata(true);
        cic.setUniqueOrBitMapString(null);
        cic.setClusteredOrNonClustered(null);
        if (cic.getIndexOrKey() != null) {
            final String tempIndexOrKey = cic.getIndexOrKey();
            if (tempIndexOrKey.equalsIgnoreCase("key")) {
                cic.setIndexOrKey("Index");
            }
        }
        if (cic.getIndexName() != null) {
            final TableObject tempIndexObject = cic.getIndexName();
            String ownerName = tempIndexObject.getOwner();
            String userName = tempIndexObject.getUser();
            String tableName = tempIndexObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempIndexObject.setOwner(ownerName);
            tempIndexObject.setUser(userName);
            tempIndexObject.setTableName(tableName);
            tempIndexObject.toTeradata();
        }
        if (cic.getOn() != null) {
            cic.getOn();
        }
        if (cic.getTableOrView() != null) {
            final TableObject tempTableObject = cic.getTableOrView();
            String ownerName = tempTableObject.getOwner();
            String userName = tempTableObject.getUser();
            String tableName = tempTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tempTableObject.setOwner(ownerName);
            tempTableObject.setUser(userName);
            tempTableObject.setTableName(tableName);
            tempTableObject.toTeradata();
        }
        if (cic.getOpenBraces() != null) {
            cic.getOpenBraces();
        }
        if (cic.getIndexColumns() != null) {
            final ArrayList TeradataIndexColumnArrayList = new ArrayList();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                final IndexColumn TeradataIndexColumn = indexColumn.toTeradata();
                TeradataIndexColumnArrayList.add(TeradataIndexColumn);
            }
            cic.setIndexColumns(TeradataIndexColumnArrayList);
        }
        cic.setWith(null);
        cic.setPadIndexArrayList(null);
        cic.setPadIndexComma(null);
        if (cic.getClosedBraces() != null) {
            cic.getClosedBraces();
        }
        cic.setPhysicalAttributes(null);
        cic.setLocalOrGlobalPartitionTable(null);
        cic.setOnGoIdentifier(null);
        cic.setParallelOrNoParallel(null);
        cic.setParallelIdentifier(null);
        cic.setUsing(null);
        cic.setTree(null);
        return cic;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.uniqueOrBitMapString != null) {
            sb.append(this.uniqueOrBitMapString.toUpperCase() + " ");
        }
        if (this.clusteredOrNonClustered != null) {
            sb.append(this.clusteredOrNonClustered.toUpperCase() + " ");
        }
        if (this.indexOrKey != null) {
            sb.append(this.indexOrKey.toUpperCase() + " ");
        }
        if (this.indexObject != null) {
            this.indexObject.setObjectContext(this.context);
            if (this.context == null) {
                String tableNameStr = this.indexObject.getTableName();
                if (this.isToOracle) {
                    if (tableNameStr.length() > 30) {
                        tableNameStr = tableNameStr.substring(0, 26) + SwisSQLAPI.truncateIndexCount;
                        if (tableNameStr.startsWith("\"") && !tableNameStr.endsWith("\"")) {
                            tableNameStr += "\"";
                        }
                        this.indexObject.setTableName(tableNameStr);
                        SwisSQLUtils.setObjectNameForMapping(this.indexObject.getOrigTableName(), tableNameStr);
                        ++SwisSQLAPI.truncateIndexCount;
                    }
                }
                else if (this.isToSybase && tableNameStr.length() > 30) {
                    tableNameStr = tableNameStr.substring(0, 27) + SwisSQLAPI.truncateIndexCount;
                    this.indexObject.setTableName(tableNameStr);
                    ++SwisSQLAPI.truncateIndexCount;
                }
            }
            sb.append(this.indexObject + " ");
        }
        if (this.isToTeradata && this.openBraces != null) {
            sb.append(this.openBraces.toUpperCase() + " ");
        }
        if (this.isToTeradata && this.indexColumns != null) {
            final int size = this.indexColumns.size();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                indexColumn.setObjectContext(this.context);
                sb.append(indexColumn.toString());
                if (i < size - 1) {
                    sb.append(",");
                }
            }
        }
        if (this.isToTeradata && this.closedBrace != null) {
            sb.append(this.closedBrace + "\n");
        }
        if (this.on != null) {
            sb.append(this.on.toUpperCase());
        }
        if (this.tableObject != null) {
            this.tableObject.setObjectContext(this.context);
            sb.append("\n" + this.tableObject + "  ");
        }
        if (this.using != null) {
            sb.append(this.using.toUpperCase() + " ");
        }
        if (this.tree != null) {
            sb.append(this.tree.toUpperCase() + " ");
        }
        if (this.openBraces != null && !this.isToTeradata) {
            sb.append(this.openBraces.toUpperCase() + " ");
        }
        if (this.cluster != null) {
            sb.append("\n" + this.cluster.toUpperCase() + " ");
        }
        if (this.clusterName != null) {
            sb.append(this.clusterName + "\n");
        }
        if (this.indexColumns != null && !this.isToTeradata) {
            final int size = this.indexColumns.size();
            for (int i = 0; i < this.indexColumns.size(); ++i) {
                final IndexColumn indexColumn = this.indexColumns.get(i);
                indexColumn.setObjectContext(this.context);
                sb.append(indexColumn.toString());
                if (i < size - 1) {
                    sb.append(",");
                }
            }
        }
        if (this.closedBrace != null && !this.isToTeradata) {
            sb.append(this.closedBrace + "\n");
        }
        if (this.with != null) {
            sb.append("\t" + this.with.toUpperCase() + "  ");
        }
        if (this.padIndexArrayList != null) {
            for (int j = 0; j < this.padIndexArrayList.size(); ++j) {
                final int size2 = this.padIndexArrayList.size();
                final PhysicalAttributesClause physicalAttributesClause = this.padIndexArrayList.get(j);
                sb.append(physicalAttributesClause.toString());
                if (j < size2 - 1 && this.padIndexComma != null) {
                    sb.append(this.padIndexComma + " ");
                }
            }
            sb.append("\n");
        }
        if (this.physicalAttributes != null) {
            sb.append("\t");
            boolean comma = false;
            for (int i = 0; i < this.physicalAttributes.size(); ++i) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = this.physicalAttributes.get(i);
                if (tempPhysicalAttributesClause.getWith() != null) {
                    comma = true;
                }
                if (comma && i != this.physicalAttributes.size() - 1) {
                    sb.append(tempPhysicalAttributesClause.toString() + ", ");
                }
                else {
                    sb.append(tempPhysicalAttributesClause.toString() + " ");
                }
            }
            sb.append("\n");
        }
        if (this.localOrGlobalPartitionTable != null) {
            sb.append(this.localOrGlobalPartitionTable.toString() + " ");
        }
        if (this.onGoIdentifier != null) {
            sb.append(this.onGoIdentifier.toUpperCase() + " ");
        }
        if (this.parallelOrNoParallel != null) {
            sb.append(this.parallelOrNoParallel.toUpperCase() + " ");
        }
        if (this.parallelIdentifier != null) {
            sb.append(this.parallelIdentifier + " ");
        }
        return sb.toString();
    }
    
    public CreateIndexClause copyObjectValues() {
        final CreateIndexClause dupCreateIndexClause = new CreateIndexClause();
        dupCreateIndexClause.setUniqueOrBitMapString(this.uniqueOrBitMapString);
        dupCreateIndexClause.setClusteredOrNonClustered(this.clusteredOrNonClustered);
        dupCreateIndexClause.setIndexOrKey(this.indexOrKey);
        dupCreateIndexClause.setIndexName(this.indexObject);
        dupCreateIndexClause.setOn(this.on);
        dupCreateIndexClause.setTableOrView(this.tableObject);
        dupCreateIndexClause.setOpenBraces(this.openBraces);
        dupCreateIndexClause.setIndexColumns(this.indexColumns);
        dupCreateIndexClause.setClosedBraces(this.closedBrace);
        dupCreateIndexClause.setCluster(this.cluster);
        dupCreateIndexClause.setClusterName(this.clusterName);
        dupCreateIndexClause.setWith(this.with);
        dupCreateIndexClause.setPadIndexArrayList(this.padIndexArrayList);
        dupCreateIndexClause.setPadIndexComma(this.padIndexComma);
        dupCreateIndexClause.setPhysicalAttributes(this.physicalAttributes);
        dupCreateIndexClause.setOnGoIdentifier(this.onGoIdentifier);
        dupCreateIndexClause.setParallelOrNoParallel(this.parallelOrNoParallel);
        dupCreateIndexClause.setParallelIdentifier(this.parallelIdentifier);
        dupCreateIndexClause.setUsing(this.using);
        dupCreateIndexClause.setTree(this.tree);
        dupCreateIndexClause.setObjectContext(this.context);
        dupCreateIndexClause.setObjectName(this.objectName);
        dupCreateIndexClause.setLocalOrGlobalPartitionTable(this.localOrGlobalPartitionTable);
        dupCreateIndexClause.setToOracle(false);
        dupCreateIndexClause.setToSybase(false);
        if (this.indexOrKey != null) {
            dupCreateIndexClause.getIndexOrKey();
        }
        if (this.indexColumns != null) {
            dupCreateIndexClause.getIndexColumns();
        }
        return dupCreateIndexClause;
    }
}
