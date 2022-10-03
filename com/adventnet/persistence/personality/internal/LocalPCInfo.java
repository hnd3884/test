package com.adventnet.persistence.personality.internal;

import com.adventnet.db.persistence.metadata.DataDictionary;
import java.util.LinkedHashMap;
import java.sql.SQLException;
import com.adventnet.persistence.SchemaBrowserUtil;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.template.TemplateUtil;
import com.adventnet.persistence.RowIterator;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DeleteUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.xml.Xml2DoConverter;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataObject;
import java.net.URL;
import java.util.logging.Level;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import java.util.Map;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashMap;
import java.util.logging.Logger;

public class LocalPCInfo implements PCInfo
{
    private static final String CLASS_NAME;
    private static final Logger LOGGER;
    HashMap<String, SelectQuery> personalityNameVsSQ;
    private HashMap<String, DominantTableConfig> domTbNameVsObj;
    private HashMap<String, PersonalityConfiguration> persNameVsObj;
    private HashMap<String, List<String>> consTbNameVsPersNames;
    private static List<String> persTbNames;
    HashMap<String, PersonalityConfiguration> cloned_persNameVsObj;
    HashMap<String, DominantTableConfig> cloned_domTbNameVsObj;
    private HashMap<Long, String> tableIDVsTableName;
    private HashMap<Long, String> constraintIDVsConsName;
    
    public LocalPCInfo() {
        this.personalityNameVsSQ = new HashMap<String, SelectQuery>();
        this.domTbNameVsObj = new HashMap<String, DominantTableConfig>();
        this.persNameVsObj = new HashMap<String, PersonalityConfiguration>();
        this.consTbNameVsPersNames = new HashMap<String, List<String>>();
        this.tableIDVsTableName = new HashMap<Long, String>();
        this.constraintIDVsConsName = new HashMap<Long, String>();
    }
    
    @Override
    public Object clone() {
        try {
            final LocalPCInfo newLocalPCInfo = (LocalPCInfo)super.clone();
            newLocalPCInfo.consTbNameVsPersNames = new HashMap<String, List<String>>(this.consTbNameVsPersNames);
            newLocalPCInfo.persNameVsObj = new HashMap<String, PersonalityConfiguration>(this.persNameVsObj);
            newLocalPCInfo.domTbNameVsObj = new HashMap<String, DominantTableConfig>(this.domTbNameVsObj);
            newLocalPCInfo.personalityNameVsSQ = new HashMap<String, SelectQuery>(this.personalityNameVsSQ);
            newLocalPCInfo.tableIDVsTableName = new HashMap<Long, String>(this.tableIDVsTableName);
            newLocalPCInfo.constraintIDVsConsName = new HashMap<Long, String>(this.constraintIDVsConsName);
            return newLocalPCInfo;
        }
        catch (final CloneNotSupportedException cnse) {
            throw new RuntimeException("Exception occurred while cloning the PCInfo :: " + this, cnse);
        }
    }
    
    private void checkString(final String val, final String var) throws DataAccessException {
        this.checkString(val, var, null);
    }
    
    private void checkString(final String val, final String var, final Object obj) throws DataAccessException {
        if (val == null || val.trim().equals("")) {
            throw new DataAccessException(var + " cannot be null/empty" + ((obj != null) ? (" in the Object :: [" + obj + "].") : ""));
        }
    }
    
    private List<String> checkList(final List<String> val, final String var) throws DataAccessException {
        if (val == null || val.size() == 0) {
            throw new DataAccessException(var + " cannot be null/empty");
        }
        final Iterator<String> iterator = val.iterator();
        while (iterator.hasNext()) {
            final String str = iterator.next();
            if (str == null || str.trim().equals("")) {
                iterator.remove();
            }
        }
        if (val.size() == 0) {
            throw new DataAccessException(var + " doesnot contain any valid values");
        }
        return val;
    }
    
    @Override
    public List<String> getMandatoryConstituentTables(final String personalityName) throws DataAccessException {
        this.checkString(personalityName, "Personality Name");
        final List<String> retList = new ArrayList<String>();
        this.addConstituentTablesIntoList(personalityName, retList, false);
        return Collections.unmodifiableList((List<? extends String>)retList);
    }
    
    @Override
    public Map<String, String> getFKsForConstituentTables(final String personalityName) throws DataAccessException {
        final Map<String, String> retMap = new HashMap<String, String>();
        final PersonalityConfiguration pc = this.getPersConfig(personalityName, "getFKsForConstituentTables");
        for (final ConstituentTable c : pc.consTabs) {
            retMap.put(c.tableName, c.fkName);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)retMap);
    }
    
    @Override
    public List<String> getAllPersonalities(final String dominantTableName) throws DataAccessException {
        return Collections.unmodifiableList((List<? extends String>)this.consTbNameVsPersNames.get(dominantTableName));
    }
    
    private void addConstituentTablesIntoList(final String personalityName, final List<String> constituentTables, final boolean allTables) throws DataAccessException {
        final PersonalityConfiguration pc = this.getPersConfig(personalityName, "addConstituentTablesIntoList");
        for (final ConstituentTable c : pc.consTabs) {
            if ((c.mandatory || allTables) && !constituentTables.contains(c.tableName)) {
                constituentTables.add(c.tableName);
            }
        }
    }
    
    @Override
    public List<String> getConstituentTables(final String personalityName) throws DataAccessException {
        this.checkString(personalityName, "Personality Name");
        final List<String> constituentTables = new ArrayList<String>();
        this.addConstituentTablesIntoList(personalityName, constituentTables, true);
        return constituentTables;
    }
    
    @Override
    public List<String> getConstituentTables(final List<String> personalityNames) throws DataAccessException {
        this.checkList(personalityNames, "Personality Names List");
        final List<String> retList = new ArrayList<String>();
        for (int i = 0; i < personalityNames.size(); ++i) {
            this.addConstituentTablesIntoList(personalityNames.get(i), retList, true);
        }
        return retList;
    }
    
    @Override
    public List<String> getContainedPersonalities(final String tableName) throws DataAccessException {
        this.checkString(tableName, "Table Name");
        final List<String> personalities = this.consTbNameVsPersNames.get(tableName);
        if (personalities == null || personalities.isEmpty()) {
            throw new DataAccessException("No Personality Defined for the Table : " + tableName);
        }
        return personalities;
    }
    
    @Override
    public List<String> getPersonalities(final List<String> tableNames) throws DataAccessException {
        this.checkList(tableNames, "Table Names List");
        tableNames.removeAll(Collections.singleton((Object)null));
        final List<String> personalities = new ArrayList<String>();
        List<String> list = null;
        for (final String tableName : tableNames) {
            final List<String> persList = this.consTbNameVsPersNames.get(tableName);
            if (persList == null) {
                throw new DataAccessException("No such TableName :: [" + tableName + "] exists in any of the personality");
            }
            list = new ArrayList<String>(persList);
            if (list == null) {
                continue;
            }
            list.removeAll(personalities);
            for (final String persName : list) {
                if (tableNames.containsAll(this.getMandatoryConstituentTables(persName))) {
                    personalities.add(persName);
                }
            }
        }
        if (personalities == null || personalities.isEmpty()) {
            throw new DataAccessException("No Personality Configuration Defined for the given List of Tables");
        }
        return personalities;
    }
    
    int compare(final String personalityName, final String refPersonalityName) throws DataAccessException {
        LocalPCInfo.LOGGER.log(Level.FINER, "Entering :: compare {0} {1}", new Object[] { personalityName, refPersonalityName });
        final List<String> constituentTables = this.getConstituentTables(personalityName);
        final List<String> refConstituentTables = this.getConstituentTables(refPersonalityName);
        if (constituentTables.size() > refConstituentTables.size()) {
            if (constituentTables.containsAll(refConstituentTables)) {
                LocalPCInfo.LOGGER.log(Level.FINER, "Exiting :: compare returns 1");
                return 1;
            }
        }
        else if (refConstituentTables.containsAll(constituentTables)) {
            LocalPCInfo.LOGGER.log(Level.FINER, "Exiting :: compare returns 0");
            return 0;
        }
        LocalPCInfo.LOGGER.log(Level.FINER, "Exiting :: compare returns -1");
        return -1;
    }
    
    @Override
    public List<String> getDominantPersonalities(final List<String> tableNames) throws DataAccessException {
        LocalPCInfo.LOGGER.log(Level.FINER, "Entering :: getDominantPersonalities", tableNames);
        final List<String> personalities = this.getPersonalities(tableNames);
        LocalPCInfo.LOGGER.log(Level.FINEST, " personalities : {0}", personalities);
        final List<String> tobeRemoved = new ArrayList<String>();
        for (int i = 0; i < personalities.size(); ++i) {
            final String personalityName = personalities.get(i);
            for (int j = i + 1; j < personalities.size(); ++j) {
                final String refPersonalityName = personalities.get(j);
                final int result = this.compare(personalityName, refPersonalityName);
                if (result == 0) {
                    tobeRemoved.add(personalities.get(i));
                }
                else if (result == 1) {
                    tobeRemoved.add(personalities.get(j));
                }
            }
        }
        LocalPCInfo.LOGGER.log(Level.FINEST, "tobeRemoved : {0}", tobeRemoved);
        personalities.removeAll(tobeRemoved);
        LocalPCInfo.LOGGER.log(Level.FINER, "Exiting :: getDominantPersonalities", personalities);
        if (personalities != null && personalities.size() == 0) {
            throw new DataAccessException("No Dominant Personality for the given list of Tables.");
        }
        return personalities;
    }
    
    @Override
    public String getDominantTableForPersonality(final String personalityName) throws DataAccessException {
        this.checkString(personalityName, "Personality Name");
        final PersonalityConfiguration pc = this.getPersConfig(personalityName, "getDominantTableForPersonality");
        return pc.dc.domTbName;
    }
    
    @Override
    public DataObject initializePersonalityConfiguration(final String moduleName, final URL url) throws DataAccessException {
        DataObject persDO = DataAccess.constructDataObject();
        Label_0108: {
            if (url != null) {
                try {
                    persDO = Xml2DoConverter.transform(url);
                    break Label_0108;
                }
                catch (final Exception e) {
                    throw new DataAccessException(e);
                }
            }
            this.fillIDMap();
            final Criteria cr = new Criteria(new Column("PersonalityConfiguration", "MODULENAME"), moduleName, 0);
            final SelectQuery sq = QueryConstructor.get(LocalPCInfo.persTbNames, cr);
            final SortColumn sc = new SortColumn(Column.getColumn("ConstituentTable", "TABLEINDEX"), true);
            sq.addSortColumn(sc);
            persDO = DataAccess.get(sq);
            LocalPCInfo.LOGGER.log(Level.FINE, "Fetched from DB :: [{0}]", persDO);
        }
        if (persDO.isEmpty()) {
            LocalPCInfo.LOGGER.log(Level.INFO, "No personalities exists in DB");
        }
        else {
            try {
                this.addPersonalities(moduleName, persDO, true);
            }
            catch (final Exception e) {
                throw new DataAccessException(e);
            }
        }
        return persDO;
    }
    
    private void cleanUp_PERSNAME_VS_OBJ(final PersonalityConfiguration pc, final boolean deleteFromDB) throws DataAccessException {
        if (deleteFromDB) {
            final Criteria delCriteria = new Criteria(Column.getColumn("PersonalityConfiguration", "PERSONALITYNAME"), pc.persName, 0, false);
            DataAccess.delete(delCriteria);
        }
        this.persNameVsObj.remove(pc.persName);
    }
    
    private void cleanUp_CONSTB_VS_PERSNAMES(final PersonalityConfiguration pc, final boolean deleteFromDB) throws DataAccessException {
        final Iterator<ConstituentTable> iterator = pc.consTabs.iterator();
        while (iterator.hasNext()) {
            final String cName = iterator.next().tableName;
            final List<String> persNames = this.consTbNameVsPersNames.get(cName);
            persNames.remove(pc.persName);
            if (persNames.size() == 0) {
                this.consTbNameVsPersNames.remove(cName);
                if (!pc.dc.isIndexed || !deleteFromDB) {
                    continue;
                }
                final String pidxTbName = pc.dc.domTbName + "_PIDX";
                final Criteria c = new Criteria(Column.getColumn(pidxTbName, "TABLE_NAME"), cName, 0);
                DeleteUtil.executeDelete(pidxTbName, c);
            }
        }
    }
    
    private void cleanUp_DOMTB_VS_OBJ(final PersonalityConfiguration pc, final boolean deleteFromDB) throws DataAccessException {
        final List<String> pNames = this.consTbNameVsPersNames.get(pc.dc.domTbName);
        if (pNames == null || (pNames != null && pNames.size() == 0)) {
            if (deleteFromDB) {
                this.deleteDominantTableConfig(pc.dc.domTbName);
                if (pc.dc.isIndexed) {
                    this.dropPIDXTable(pc.dc.domTbName + "_PIDX");
                }
            }
            this.domTbNameVsObj.remove(pc.dc.domTbName);
        }
    }
    
    @Override
    public boolean removePersonality(final String personalityName, final boolean deleteFromDB) throws DataAccessException {
        final PersonalityConfiguration pc = this.getPersConfig(personalityName, "removePersonality ");
        if (pc != null) {
            this.cleanUp_PERSNAME_VS_OBJ(pc, deleteFromDB);
            this.cleanUp_CONSTB_VS_PERSNAMES(pc, deleteFromDB);
            this.cleanUp_DOMTB_VS_OBJ(pc, deleteFromDB);
            return true;
        }
        return false;
    }
    
    @Override
    public void removePersonalityConfiguration(final String moduleName, final boolean deleteFromDB) throws DataAccessException {
        this.checkString(moduleName, "Module Name");
        final List<String> persNames = this.getPersonalityNames(moduleName);
        if (persNames != null && persNames.size() > 0) {
            for (final String persName : persNames) {
                this.removePersonality(persName, deleteFromDB);
            }
        }
    }
    
    @Override
    public String getDominantTable(final String tableName) throws DataAccessException {
        final List<String> persList = this.consTbNameVsPersNames.get(tableName);
        if (persList == null || persList.size() == 0) {
            return null;
        }
        final String persName = persList.get(0);
        return (persName == null) ? null : this.persNameVsObj.get(persName).dc.domTbName;
    }
    
    private Map<String, Boolean> update_NonIndexLimit(final DataObject persDO, final List<String> newPersNames, final boolean isNotifiedByDeployment, final Map<String, List<String>> consTbVsPersMap) throws Exception {
        final Map<String, Boolean> map = new HashMap<String, Boolean>();
        DataObject data = null;
        Row domTbRow = null;
        Iterator<Row> iterator = null;
        final List<String> domTbNames = this.getDominantTableNames(newPersNames);
        for (final String domTbName : domTbNames) {
            final DominantTableConfig dc = this.cloned_domTbNameVsObj.get(domTbName);
            final ArrayList<String> allConsTbNames = this.getAllConsTbNames(domTbName, consTbVsPersMap);
            if (dc.hasTemplateInstances) {
                dc.nonIdxLimit = 100000;
                LocalPCInfo.LOGGER.warning("Non index limit has been changed to 100000 for the dominant table [ " + domTbName + " ] , since Template-Instances has participated in personality.");
            }
            if (dc.isIndexed || allConsTbNames.size() > dc.nonIdxLimit) {
                this.createIndexTable(dc.domTbName, persDO, isNotifiedByDeployment);
                map.put(dc.domTbName, Boolean.TRUE);
                data = new WritableDataObject();
                final String pidxTbName = domTbName + "_PIDX";
                final TableDefinition idxTabDefn = this.getTableDefinition(domTbName);
                final List<String> domTbPkColNames = idxTabDefn.getPrimaryKey().getColumnList();
                final List<String> distTbNamesInPIDXTb = getDistinctTableNamesFromPIDX(pidxTbName);
                final ArrayList<String> consTbNames_TobePopulated = new ArrayList<String>(allConsTbNames);
                consTbNames_TobePopulated.removeAll(distTbNamesInPIDXTb);
                final List<String> tableNamesToBePopulated = getTableNamesContainingSomeData(consTbNames_TobePopulated);
                for (final String tbName : tableNamesToBePopulated) {
                    for (final String persName : consTbVsPersMap.get(tbName)) {
                        final SelectQuery sq = this.getSelectQuery(tbName, persName, domTbName);
                        final DataObject idxTbData = DataAccess.get(sq);
                        iterator = idxTbData.getRows(domTbName);
                        while (iterator.hasNext()) {
                            domTbRow = iterator.next();
                            final Row pidxTbRow = new Row(pidxTbName);
                            for (int j = 0; j < domTbPkColNames.size(); ++j) {
                                final String columnName = domTbPkColNames.get(j);
                                pidxTbRow.set(columnName, domTbRow.get(columnName));
                            }
                            pidxTbRow.set("TABLE_NAME", tbName);
                            if (data.getRow(pidxTbName, pidxTbRow) == null) {
                                data.addRow(pidxTbRow);
                            }
                        }
                    }
                }
                if (data.isEmpty()) {
                    continue;
                }
                DataAccess.add(data);
            }
            else {
                map.put(dc.domTbName, Boolean.FALSE);
            }
        }
        return map;
    }
    
    private static List<String> getTableNamesContainingSomeData(final List<String> tbNames) throws DataAccessException {
        final List<String> retList = new ArrayList<String>();
        final List<String> tableNames = new ArrayList<String>(tbNames);
        for (final String tableName : tableNames) {
            final SelectQuery selqry = new SelectQueryImpl(Table.getTable(tableName));
            selqry.addSelectColumn(Column.getColumn(null, "*"));
            selqry.setRange(new Range(0, 1));
            TableDefinition tabDef = null;
            try {
                tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
            }
            catch (final Exception e) {
                throw new DataAccessException(e);
            }
            selqry.addSortColumn(new SortColumn(new Column(tableName, tabDef.getPrimaryKey().getColumnList().get(0)), true));
            final DataObject data = DataAccess.get(selqry);
            if (!data.isEmpty()) {
                retList.add(tableName);
            }
        }
        return retList;
    }
    
    private static List<String> getDistinctTableNamesFromPIDX(final String pidxTbName) throws Exception {
        final SelectQuery sq = new SelectQueryImpl(Table.getTable(pidxTbName));
        final Column col = Column.getColumn(pidxTbName, "TABLE_NAME").distinct();
        col.setColumnAlias("DIST_TB_NAME");
        sq.addSelectColumn(col);
        Connection c = null;
        DataSet ds = null;
        final List<String> unqTbs = new ArrayList<String>();
        try {
            c = RelationalAPI.getInstance().getConnection();
            ds = RelationalAPI.getInstance().executeQuery(sq, c);
            while (ds.next()) {
                unqTbs.add(ds.getString(1));
            }
            LocalPCInfo.LOGGER.log(Level.FINE, "unqTbs:: [{0}]", unqTbs);
        }
        finally {
            if (null != ds) {
                ds.close();
            }
            if (c != null) {
                try {
                    c.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return unqTbs;
    }
    
    private ConstituentTable getConstituentTable(final PersonalityConfiguration pc, final String tableName) {
        for (final ConstituentTable ct : pc.consTabs) {
            if (ct.tableName.equals(tableName)) {
                return ct;
            }
        }
        return null;
    }
    
    private SelectQuery getSelectQuery(final String tableName, final String persName, final String domTbName) throws Exception {
        final SelectQuery sq = new SelectQueryImpl(Table.getTable(tableName));
        sq.addSelectColumn(Column.getColumn(domTbName, "*"));
        final PersonalityConfiguration pc = this.cloned_persNameVsObj.get(persName);
        ConstituentTable ct = this.getConstituentTable(pc, tableName);
        if (!tableName.equals(domTbName)) {
            do {
                final ForeignKeyDefinition fk = MetaDataUtil.getForeignKeyDefinitionByName(ct.fkName);
                sq.addJoin(new Join(fk.getSlaveTableName(), fk.getMasterTableName(), fk.getFkColumns().toArray(new String[fk.getFkColumns().size()]), fk.getFkRefColumns().toArray(new String[fk.getFkRefColumns().size()]), 2));
                ct = this.getConstituentTable(pc, fk.getMasterTableName());
            } while (!ct.tableName.equals(domTbName));
        }
        return sq;
    }
    
    private void addDomConfig(final DataObject persDO) throws DataAccessException {
        Row row = null;
        final Iterator<Row> iterator = persDO.getRows("DominantTableConfig");
        while (iterator.hasNext()) {
            row = iterator.next();
            final String domTbName = this.getTableName(row.get(1));
            DominantTableConfig dc = this.domTbNameVsObj.get(domTbName);
            if (dc == null) {
                dc = new DominantTableConfig();
                dc.domTbName = domTbName;
                dc.isIndexed = (boolean)row.get(2);
                dc.nonIdxLimit = (int)row.get(3);
                this.cloned_domTbNameVsObj.put(domTbName, dc);
            }
            else {
                if (this.cloned_domTbNameVsObj.get(domTbName) == null) {
                    continue;
                }
                LocalPCInfo.LOGGER.log(Level.WARNING, "Already a DominantTable Configuration with this tableName [{0}] exists, hence this configuration [{1}] is not considered.", new Object[] { domTbName, row });
                ((RowIterator)iterator).removeIgnoreFK();
                ((WritableDataObject)persDO).removeActionsFor("delete");
            }
        }
    }
    
    private List<String> addPersConfig(final DataObject persDO) throws DataAccessException {
        final List<String> newPersNames = new ArrayList<String>();
        final Iterator<Row> iterator = persDO.getRows("PersonalityConfiguration");
        if (!iterator.hasNext()) {
            throw new DataAccessException("Row for PersonalityConfiguration does not exist");
        }
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String newPersName = (String)row.get(2);
            this.checkString(newPersName, "New Personality Name", persDO);
            PersonalityConfiguration pc = this.persNameVsObj.get(newPersName);
            if (pc != null) {
                throw new DataAccessException("Personality with this name [" + newPersName + "] is already exists");
            }
            if (persDO.getRow("ConstituentTable", row) == null) {
                throw new DataAccessException("No ConstituentTable rows defined for the personality :: [" + newPersName + "].");
            }
            pc = new PersonalityConfiguration();
            this.cloned_persNameVsObj.put(newPersName, pc);
            newPersNames.add(pc.persName = newPersName);
            pc.moduleName = (String)row.get(4);
            pc.persID = row.get(1);
            final Object domTbID = row.get(3);
            if (domTbID == null) {
                throw new DataAccessException("PERSONALITYCONFIGURATION.DOMINANTTABLEID cannot be [null] in the row :: " + row);
            }
            final String domTbName = this.getTableName(domTbID);
            DominantTableConfig domTabConf = this.cloned_domTbNameVsObj.get(domTbName);
            if (null == domTabConf) {
                domTabConf = new DominantTableConfig();
                domTabConf.domTbName = domTbName;
                this.cloned_domTbNameVsObj.put(domTbName, domTabConf);
            }
            pc.dc = this.cloned_domTbNameVsObj.get(domTbName);
            Row consRow = new Row("ConstituentTable");
            consRow.set(2, domTbID);
            consRow.set(1, pc.persID);
            consRow = persDO.getRow("ConstituentTable", consRow);
            if (consRow != null) {
                continue;
            }
            consRow = new Row("ConstituentTable");
            consRow.set(2, domTbID);
            consRow.set(1, pc.persID);
            consRow.set(4, Boolean.TRUE);
            consRow.set(3, new Integer(-1));
            persDO.addRow(consRow);
        }
        return newPersNames;
    }
    
    private Map<String, List<String>> addConsTables(final DataObject persDO, final List<String> newPersNames) throws DataAccessException, MetaDataException {
        final Map<String, List<String>> map = new HashMap<String, List<String>>();
        final Map<String, List<String>> persNamesVsTables = new HashMap<String, List<String>>();
        final List<String> updateFKInPers = new ArrayList<String>();
        final Iterator<Row> iterator = persDO.getRows("ConstituentTable");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final ConstituentTable ct = new ConstituentTable();
            ct.tableID = row.get(2);
            ct.mandatory = (boolean)row.get(4);
            ct.pc = this.getPersConfig(row.get(1), newPersNames, persDO);
            ct.tableName = this.getTableName(ct.tableID);
            if (TemplateUtil.isTemplate(ct.tableName)) {
                ct.pc.dc.hasTemplateInstances = true;
                if (ct.pc.dc.isIndexed) {
                    throw new DataAccessException("Template Table [ " + ct.tableName + " ] cannot participate in indexed-personality [ " + ct.pc.persName + " ]. ");
                }
            }
            ct.tableIndex = (int)row.get(3);
            List<String> addedTables = persNamesVsTables.get(ct.pc.persName);
            if (addedTables == null) {
                addedTables = new ArrayList<String>();
                persNamesVsTables.put(ct.pc.persName, addedTables);
            }
            addedTables.add(ct.tableName);
            final Object fkID = row.get(5);
            if (fkID != null) {
                ct.fkName = this.getConstraintName(fkID);
                if (PersistenceInitializer.onSAS()) {
                    final ForeignKeyDefinition fk = MetaDataUtil.getForeignKeyDefinitionByName(ct.fkName);
                    if ((fk.getSlaveTableName().equals(ct.tableName) && !addedTables.contains(fk.getMasterTableName())) || (fk.getMasterTableName().equals(ct.tableName) && !addedTables.contains(fk.getSlaveTableName()))) {
                        throw new IllegalArgumentException("Unrelated FKName [" + ct.fkName + "] specified in the persDO :: " + persDO);
                    }
                }
            }
            else if (!ct.tableName.equals(ct.pc.dc.domTbName) && !updateFKInPers.contains(ct.pc.persName)) {
                updateFKInPers.add(ct.pc.persName);
            }
            ct.pc.consTabs.add(ct);
            List<String> persNames = map.get(ct.tableName);
            if (persNames == null) {
                persNames = this.consTbNameVsPersNames.get(ct.tableName);
                if (persNames == null) {
                    persNames = new ArrayList<String>();
                }
                else {
                    persNames = new ArrayList<String>(persNames);
                }
                map.put(ct.tableName, persNames);
            }
            persNames.add(ct.pc.persName);
        }
        if (updateFKInPers.size() > 0) {
            LocalPCInfo.LOGGER.log(Level.WARNING, "updateFKInPers :: FKConstaints not specified in these personalities is {0}", updateFKInPers);
            this.updateFKConstraintId(updateFKInPers, persDO);
            LocalPCInfo.LOGGER.log(Level.FINEST, "Finished updating FKConstraintIds of CONSTITUENTTABLE");
        }
        return map;
    }
    
    private void addPersonalities(final String moduleName, final DataObject persDO, final boolean isNotifiedByDeployment) throws Exception {
        this.cloned_persNameVsObj = new HashMap<String, PersonalityConfiguration>(this.persNameVsObj);
        this.cloned_domTbNameVsObj = new HashMap<String, DominantTableConfig>(this.domTbNameVsObj);
        this.addDomConfig(persDO);
        final List<String> newPersNames = this.addPersConfig(persDO);
        final Map<String, List<String>> map = this.addConsTables(persDO, newPersNames);
        final Map<String, Boolean> isIndexedMap = this.update_NonIndexLimit(persDO, newPersNames, isNotifiedByDeployment, map);
        if (!isNotifiedByDeployment) {
            DataAccess.update(persDO);
        }
        for (final String tbName : map.keySet()) {
            this.consTbNameVsPersNames.put(tbName, map.get(tbName));
        }
        for (final String domTbName : isIndexedMap.keySet()) {
            final DominantTableConfig dc = this.cloned_domTbNameVsObj.get(domTbName);
            if (!dc.isIndexed && isIndexedMap.get(domTbName)) {
                dc.isIndexed = true;
            }
        }
        this.domTbNameVsObj = this.cloned_domTbNameVsObj;
        this.persNameVsObj = this.cloned_persNameVsObj;
    }
    
    private PersonalityConfiguration getPersConfig(final Object persID, final List<String> newPersNames, final DataObject persDO) throws DataAccessException {
        for (final String persName : newPersNames) {
            final PersonalityConfiguration pc = this.cloned_persNameVsObj.get(persName);
            if (pc.persID.equals(persID)) {
                return pc;
            }
        }
        throw new DataAccessException("No PersonalityConfiguration found for this persID :: [" + persID + "] while adding this personalityConfigurationDO :: " + persDO);
    }
    
    @Override
    public void addPersonalities(final String moduleName, final DataObject persDO) throws DataAccessException {
        try {
            this.addPersonalities(moduleName, persDO, false);
        }
        catch (final Exception e) {
            throw new DataAccessException(e);
        }
    }
    
    private static ForeignKeyDefinition getSuitableFK(final List<String> processedTables, final String tableName) throws DataAccessException {
        ForeignKeyDefinition fkDefn = null;
        fkDefn = QueryConstructor.getSuitableFK(processedTables, tableName, true);
        if (fkDefn == null) {
            fkDefn = QueryConstructor.getSuitableFK(processedTables, tableName, false);
        }
        if (fkDefn == null) {
            throw new DataAccessException("No Suitable FK found between the tables :: " + processedTables + " and the table :: [" + tableName + "]");
        }
        return fkDefn;
    }
    
    private void updateFKConstraintId(final List<String> persNames, final DataObject persDO) throws DataAccessException {
        for (final String persName : persNames) {
            final PersonalityConfiguration pc = this.cloned_persNameVsObj.get(persName);
            final List<String> consTbNames = new ArrayList<String>();
            consTbNames.add(pc.dc.domTbName);
            for (int index = 1; index < pc.consTabs.size(); ++index) {
                final ConstituentTable ct = pc.consTabs.get(index);
                if (ct.fkName == null) {
                    LocalPCInfo.LOGGER.log(Level.WARNING, "updateFKConstraintId :: FKConstraint not-properly/wrongly specified in the :: [{0}]  of the :: [{1}]", new Object[] { ct, persName });
                    final ForeignKeyDefinition fkDef = getSuitableFK(consTbNames, ct.tableName);
                    ct.fkName = fkDef.getName();
                    Row consRow = new Row("ConstituentTable");
                    consRow.set(2, ct.tableID);
                    consRow.set(1, ct.pc.persID);
                    consRow = persDO.getRow("ConstituentTable", consRow);
                    consRow.set(5, fkDef.getID());
                }
                consTbNames.add(ct.tableName);
            }
        }
    }
    
    @Override
    public List<String> getPersonalityNames(final String moduleName) throws DataAccessException {
        final List<String> persNames = new ArrayList<String>();
        for (final PersonalityConfiguration pc : this.persNameVsObj.values()) {
            if (pc.moduleName.equals(moduleName)) {
                persNames.add(pc.persName);
            }
        }
        return persNames;
    }
    
    @Override
    public boolean isIndexed(final String domTableName) throws DataAccessException {
        this.checkString(domTableName, "Table Name");
        final List<String> list = this.consTbNameVsPersNames.get(domTableName);
        if (list == null || list.size() == 0) {
            throw new DataAccessException("No such TableName :: [" + domTableName + "] exists in any personality.");
        }
        final DominantTableConfig dc = this.domTbNameVsObj.get(domTableName);
        if (dc == null) {
            throw new DataAccessException("Table :: [" + domTableName + "] is not defined as DominantTableConfig");
        }
        return dc.isIndexed;
    }
    
    private boolean createIndexTable(final String dominantTable, final DataObject persDO, final boolean isNotifiedByDeployment) throws DataAccessException {
        LocalPCInfo.LOGGER.log(Level.FINE, "createIndexTable :: {0}", dominantTable);
        final TableDefinition td = this.getTableDefinition(dominantTable);
        if (td == null) {
            throw new DataAccessException("Unknown table " + dominantTable + " is defined as dominant table");
        }
        final String idxTableName = dominantTable + "_PIDX";
        if (dominantTable.length() > 21) {
            LocalPCInfo.LOGGER.log(Level.WARNING, "The TableName {0} has {1} characters. A Name for a dominant Table cannot be more than 21 characters.", new Object[] { dominantTable, new Integer(dominantTable.length()) });
        }
        final TableDefinition itd = this.getTableDefinition(idxTableName);
        if (itd != null) {
            LocalPCInfo.LOGGER.log(Level.FINER, "Index table name {0} already exists. Ignoring further processing!", idxTableName);
            return false;
        }
        final List<String> pkColumns = td.getPrimaryKey().getColumnList();
        final TableDefinition idxTD = new TableDefinition(false);
        idxTD.setTableName(idxTableName);
        final ForeignKeyDefinition fkDef = new ForeignKeyDefinition();
        fkDef.setName(idxTableName + "_FK");
        fkDef.setSlaveTableName(idxTableName);
        fkDef.setMasterTableName(dominantTable);
        fkDef.setConstraints(1);
        fkDef.setBidirectional(false);
        idxTD.addForeignKey(fkDef);
        for (int size = pkColumns.size(), i = 0; i < size; ++i) {
            final String columnName = pkColumns.get(i);
            final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
            final ColumnDefinition idxCD = cloneCD(cd);
            idxCD.setTableName(idxTableName);
            final ForeignKeyColumnDefinition fkcd = new ForeignKeyColumnDefinition();
            fkcd.setLocalColumnDefinition(idxCD);
            fkcd.setReferencedColumnDefinition(cd);
            fkDef.addForeignKeyColumns(fkcd);
            idxTD.addColumnDefinition(idxCD);
        }
        final ColumnDefinition tableNameColumn = new ColumnDefinition();
        tableNameColumn.setTableName(idxTableName);
        tableNameColumn.setColumnName("TABLE_NAME");
        tableNameColumn.setDataType("CHAR");
        tableNameColumn.setMaxLength(40);
        tableNameColumn.setNullable(false);
        tableNameColumn.setKey(true);
        idxTD.addColumnDefinition(tableNameColumn);
        final List<ColumnDefinition> colDefns = idxTD.getColumnList();
        final PrimaryKeyDefinition pkDef = new PrimaryKeyDefinition();
        pkDef.setName(idxTableName + "_PK");
        pkDef.setTableName(idxTableName);
        for (final ColumnDefinition cd2 : colDefns) {
            pkDef.addColumnName(cd2.getColumnName());
        }
        idxTD.setPrimaryKey(pkDef);
        String dominantTableModuleName = null;
        try {
            dominantTableModuleName = MetaDataUtil.getModuleNameOfTable(dominantTable);
        }
        catch (final MetaDataException mde) {
            LocalPCInfo.LOGGER.log(Level.FINER, "Exception occured while adding the table definition for personality indexing", mde);
            throw new DataAccessException(mde);
        }
        LocalPCInfo.LOGGER.log(Level.FINE, "Going to create the PIDX table name for the module :: [{0}]", dominantTableModuleName);
        if (isNotifiedByDeployment) {
            Connection conn = null;
            try {
                conn = RelationalAPI.getInstance().getConnection();
                final boolean isTablePresentInDB = RelationalAPI.getInstance().getDBAdapter().isTablePresentInDB(conn, null, idxTableName);
                final boolean isTablePresentInCache = MetaDataUtil.getTableDefinitionByName(idxTableName) != null;
                LocalPCInfo.LOGGER.log(Level.INFO, "isTablePresentInDB :: [{0}], isTablePresentInCache :: [{1}]", new Object[] { isTablePresentInDB, isTablePresentInCache });
                if (!isTablePresentInDB && !isTablePresentInCache) {
                    DataAccess.createTable(dominantTableModuleName, idxTD);
                    LocalPCInfo.LOGGER.log(Level.FINE, "Created the PIDX table successfully :: [{0}]", idxTD);
                }
                else if (isTablePresentInDB && !isTablePresentInCache) {
                    final DataObject dobj = DataAccess.get("TableDetails", new Criteria(new Column("TableDetails", "TABLE_NAME"), idxTableName, 0));
                    if (dobj.isEmpty()) {
                        final DataObject idxDO = new WritableDataObject();
                        SchemaBrowserUtil.addTableDefinitionInDO(dominantTableModuleName, idxTD, idxDO);
                        DataAccess.add(idxDO);
                    }
                    MetaDataUtil.addTableDefinition(dominantTableModuleName, idxTD);
                }
                else if (!isTablePresentInDB && isTablePresentInCache) {
                    throw new RuntimeException();
                }
            }
            catch (final Exception e) {
                throw new DataAccessException(e.getMessage(), e);
            }
            finally {
                if (conn != null) {
                    try {
                        conn.close();
                    }
                    catch (final SQLException ex) {
                        throw new DataAccessException(ex);
                    }
                }
            }
        }
        else {
            try {
                DataAccess.createTable(dominantTableModuleName, idxTD);
                Row domTbRow = new Row("DominantTableConfig");
                domTbRow.set(1, td.getTableID());
                if (persDO.getRow("DominantTableConfig", domTbRow) == null) {
                    final DataObject data = DataAccess.get("DominantTableConfig", domTbRow);
                    domTbRow = data.getRow("DominantTableConfig");
                    domTbRow.set(2, Boolean.TRUE);
                    data.updateRow(domTbRow);
                    DataAccess.update(data);
                }
                else {
                    domTbRow = persDO.getRow("DominantTableConfig", domTbRow);
                    domTbRow.set(2, Boolean.TRUE);
                    persDO.updateRow(domTbRow);
                }
            }
            catch (final SQLException sqle) {
                throw new DataAccessException(sqle);
            }
        }
        LocalPCInfo.LOGGER.exiting(LocalPCInfo.CLASS_NAME, "createIndexTable");
        return true;
    }
    
    public static ColumnDefinition cloneCD(final ColumnDefinition cd) throws DataAccessException {
        try {
            final ColumnDefinition newCD = new ColumnDefinition();
            newCD.setTableName(cd.getTableName());
            newCD.setColumnName(cd.getColumnName());
            newCD.setDataType(cd.getDataType());
            newCD.setMaxLength(cd.getMaxLength());
            newCD.setDefaultValue(cd.getDefaultValue());
            newCD.setAllowedValues(cd.getAllowedValues());
            newCD.setNullable(cd.isNullable());
            newCD.setConstraints(cd.getConstraints());
            newCD.setUnique(false);
            newCD.setKey(cd.isKey());
            return newCD;
        }
        catch (final MetaDataException mde) {
            LocalPCInfo.LOGGER.log(Level.FINER, "Exception occured while cloning ColumnDefinition to form TableDefinition of index table", mde);
            throw new DataAccessException(mde);
        }
    }
    
    @Override
    public LinkedHashMap getSelectQueryTemplates(final String moduleName) throws DataAccessException {
        LocalPCInfo.LOGGER.log(Level.FINEST, " moduleName : {0}", moduleName);
        this.checkString(moduleName, "Module Name");
        DataDictionary dd;
        try {
            dd = MetaDataUtil.getDataDictionary(moduleName);
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException(mde);
        }
        final List<TableDefinition> tableDefs = dd.getTableDefinitions();
        final int noOfTableDefs = tableDefs.size();
        final List<String> tableNames = new ArrayList<String>();
        final List<String> personalities = new ArrayList<String>();
        final List<String> domTables = new ArrayList<String>();
        for (int i = 0; i < noOfTableDefs; ++i) {
            final TableDefinition td = tableDefs.get(i);
            final String tableName = td.getTableName();
            tableNames.add(tableName);
            final List<String> containedPersonalities = this.getContainedPersonalities(tableName);
            for (int noOfContainedPers = containedPersonalities.size(), j = 0; j < noOfContainedPers; ++j) {
                final String personalityName = containedPersonalities.get(j);
                if (!personalities.contains(personalityName)) {
                    personalities.add(personalityName);
                    final String domTable = this.getDominantTableForPersonality(personalityName);
                    if (!domTables.contains(domTables)) {
                        final List<String> personalitiesOfDomTable = this.getContainedPersonalities(domTable);
                        for (int noOfPersOfDomTable = personalitiesOfDomTable.size(), k = 0; k < noOfPersOfDomTable; ++k) {
                            final String personalityOfDomTable = personalitiesOfDomTable.get(k);
                            if (!personalities.contains(personalityOfDomTable)) {
                                personalities.add(personalityOfDomTable);
                            }
                        }
                    }
                }
            }
        }
        LocalPCInfo.LOGGER.log(Level.FINEST, " tableNames : {0}", tableNames);
        LocalPCInfo.LOGGER.log(Level.FINEST, " personalities : {0}", personalities);
        final int noOfPers = personalities.size();
        final LinkedHashMap selectQueryTemplates = new LinkedHashMap();
        final Criteria nullCriteria = null;
        if (noOfPers > 0) {
            for (int l = 0; l < noOfPers; ++l) {
                final String pers = personalities.get(l);
                final SelectQuery sqForPers = QueryConstructor.getForPersonality(pers, nullCriteria);
                selectQueryTemplates.put("[Personality] " + pers, sqForPers);
            }
        }
        for (int noOfTables = tableNames.size(), m = 0; m < noOfTables; ++m) {
            final String tableName2 = tableNames.get(m);
            final SelectQuery sqForTable = new SelectQueryImpl(Table.getTable(tableName2));
            final Column col = Column.getColumn(tableName2, "*");
            sqForTable.addSelectColumn(col);
            selectQueryTemplates.put("[Table] " + tableName2, sqForTable);
        }
        LocalPCInfo.LOGGER.log(Level.FINEST, " selectQueryTemplates : {0}", selectQueryTemplates);
        return selectQueryTemplates;
    }
    
    @Override
    public boolean isFKPartOfPersonality(final String foreignKeyName) throws DataAccessException {
        this.checkString(foreignKeyName, "ForeignKeyName");
        ForeignKeyDefinition fkd = null;
        try {
            fkd = MetaDataUtil.getForeignKeyDefinitionByName(foreignKeyName);
            if (fkd == null) {
                return false;
            }
            final String tableName = fkd.getSlaveTableName();
            final List<String> persNames = this.consTbNameVsPersNames.get(tableName);
            if (persNames != null) {
                for (final String persName : persNames) {
                    for (final ConstituentTable c : this.persNameVsObj.get(persName).consTabs) {
                        if (foreignKeyName.equals(c.fkName)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException(mde);
        }
    }
    
    @Override
    public boolean isPartOfPersonality(final String tableName) throws DataAccessException {
        final List<String> persNames = this.consTbNameVsPersNames.get(tableName);
        return persNames != null && persNames.size() > 0;
    }
    
    @Override
    public boolean isPartOfIndexedPersonality(final String tableName) throws DataAccessException {
        if (this.isPartOfPersonality(tableName)) {
            final String dominantTableName = this.getDominantTable(tableName);
            if (dominantTableName != null) {
                final DominantTableConfig dc = this.domTbNameVsObj.get(dominantTableName);
                return dc.isIndexed;
            }
        }
        return false;
    }
    
    @Override
    public SelectQuery getSelectQuery(final String personalityName) throws DataAccessException {
        final SelectQuery sq = this.personalityNameVsSQ.get(personalityName);
        return sq;
    }
    
    private PersonalityConfiguration getPersConfig(final String personalityName, final String methodName) throws DataAccessException {
        final PersonalityConfiguration pc = this.persNameVsObj.get(personalityName);
        if (pc == null) {
            throw new DataAccessException(methodName + " :: No Such Personality Exists : " + personalityName);
        }
        return pc;
    }
    
    private List<String> getDominantTableNames(final List<String> persNames) {
        final List<String> domTbNames = new ArrayList<String>();
        for (final String persName : persNames) {
            final PersonalityConfiguration pc = this.cloned_persNameVsObj.get(persName);
            if (!domTbNames.contains(pc.dc.domTbName)) {
                domTbNames.add(pc.dc.domTbName);
            }
        }
        return domTbNames;
    }
    
    private ArrayList<String> getAllConsTbNames(final String domTbName, final Map<String, List<String>> map) {
        final ArrayList<String> allConsTbNames = new ArrayList<String>();
        for (final String persName : map.get(domTbName)) {
            for (final ConstituentTable ct : this.cloned_persNameVsObj.get(persName).consTabs) {
                if (!allConsTbNames.contains(ct.tableName)) {
                    allConsTbNames.add(ct.tableName);
                }
            }
        }
        return allConsTbNames;
    }
    
    private void deleteDominantTableConfig(final String domTbName) throws DataAccessException {
        final Long tableID = this.getTableDefinition(domTbName).getTableID();
        final Criteria c = new Criteria(Column.getColumn("DominantTableConfig", "DOMINANTTABLEID"), tableID, 0);
        DataAccess.delete("DominantTableConfig", c);
    }
    
    private void dropPIDXTable(final String pidxTableName) throws DataAccessException {
        if (!DataAccess.get("TableDetails", new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), pidxTableName, 0)).isEmpty()) {
            try {
                DataAccess.dropTable(pidxTableName);
                return;
            }
            catch (final SQLException e) {
                throw new DataAccessException("Exception occurred while dropping the PIDX table [" + pidxTableName + "]", e);
            }
        }
        try {
            RelationalAPI.getInstance().dropTable(pidxTableName, true, new ArrayList());
        }
        catch (final SQLException e) {
            throw new DataAccessException("Exception occurred while dropping the PIDX table [" + pidxTableName + "]", e);
        }
    }
    
    private TableDefinition getTableDefinition(final String tableName) throws DataAccessException {
        try {
            return MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final Exception e) {
            throw new DataAccessException("Exception occurred while getting tableDefinition for :: [" + tableName + "]", e);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n\n\nLocalPCInfo :: ");
        sb.append("\ndomTbNameVsObj :: ");
        sb.append(this.domTbNameVsObj);
        sb.append("\n\npersNameVsObj :: ");
        sb.append(this.persNameVsObj);
        sb.append("\n\nconsTbNameVsPersNames :: ");
        sb.append(this.consTbNameVsPersNames);
        sb.append("\n\n\n");
        return sb.toString();
    }
    
    private void fillIDMap() throws DataAccessException {
        DataObject data = DataAccess.get("TableDetails", (Criteria)null);
        Iterator<Row> iterator = data.getRows("TableDetails");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            this.tableIDVsTableName.put(Long.parseLong(row.get(1).toString().trim()), row.get(3).toString());
        }
        data = DataAccess.get("ConstraintDefinition", (Criteria)null);
        iterator = data.getRows("ConstraintDefinition");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            this.constraintIDVsConsName.put(Long.parseLong(row.get(1).toString().trim()), row.get(2).toString());
        }
    }
    
    private String getTableName(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof String) {
            return (String)o;
        }
        if (o instanceof Long) {
            final Long tid = (Long)o;
            String retName = this.tableIDVsTableName.get(tid);
            if (retName == null) {
                try {
                    final DataObject tDO = DataAccess.get("TableDetails", new Criteria(Column.getColumn("TableDetails", "TABLE_ID"), tid, 0));
                    final Row r = tDO.getRow("TableDetails");
                    if (r == null) {
                        throw new IllegalArgumentException("No table name present for the table id :: [" + tid + "]");
                    }
                    retName = (String)r.get(3);
                    this.tableIDVsTableName.put(tid, retName);
                }
                catch (final DataAccessException dae) {
                    throw new IllegalArgumentException("Exception occurred while fetching tableName for the tableid :: [" + tid + "]", dae);
                }
            }
            return retName;
        }
        throw new IllegalArgumentException("Unknown type of object " + o + " received for this method getTableName()");
    }
    
    private String getConstraintName(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof String) {
            return (String)o;
        }
        if (o instanceof Long) {
            final Long cid = (Long)o;
            String retName = this.constraintIDVsConsName.get(cid);
            if (retName == null) {
                try {
                    final DataObject cDO = DataAccess.get("ConstraintDefinition", new Criteria(Column.getColumn("ConstraintDefinition", "CONSTRAINT_ID"), cid, 0));
                    final Row r = cDO.getRow("ConstraintDefinition");
                    if (r == null) {
                        throw new IllegalArgumentException("No constraint name present for the constraint id :: [" + cid + "]");
                    }
                    retName = (String)r.get(2);
                    this.constraintIDVsConsName.put(cid, retName);
                }
                catch (final DataAccessException dae) {
                    throw new IllegalArgumentException("Exception occurred while fetching constraintName for the constraintid :: [" + cid + "]", dae);
                }
            }
            return retName;
        }
        throw new IllegalArgumentException("Unknown type of object " + o + " received for this method getConstraintName()");
    }
    
    static {
        CLASS_NAME = LocalPCInfo.class.getName();
        LOGGER = Logger.getLogger(LocalPCInfo.CLASS_NAME);
        (LocalPCInfo.persTbNames = new ArrayList<String>()).add("DominantTableConfig");
        LocalPCInfo.persTbNames.add("PersonalityConfiguration");
        LocalPCInfo.persTbNames.add("ConstituentTable");
    }
    
    class DominantTableConfig implements Cloneable
    {
        String domTbName;
        int nonIdxLimit;
        boolean isIndexed;
        boolean hasTemplateInstances;
        
        DominantTableConfig() {
            this.domTbName = null;
            this.nonIdxLimit = 10;
            this.isIndexed = false;
            this.hasTemplateInstances = false;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("<DominantTableConfig DOMINANT_TABLE_NAME=\"");
            sb.append(this.domTbName);
            sb.append("\" IS_INDEXED=\"");
            sb.append(this.isIndexed);
            sb.append("\" NON_INDEX_LIMIT=\"");
            sb.append(this.nonIdxLimit);
            sb.append("\" HAS_TEMPLATE_INSTANCES=\"");
            sb.append(this.hasTemplateInstances);
            sb.append("/>\n");
            return sb.toString();
        }
    }
    
    class PersonalityConfiguration
    {
        Object persID;
        String persName;
        String moduleName;
        DominantTableConfig dc;
        ArrayList<ConstituentTable> consTabs;
        
        PersonalityConfiguration() {
            this.persID = null;
            this.persName = null;
            this.moduleName = null;
            this.dc = null;
            this.consTabs = new ArrayList<ConstituentTable>();
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("<PersonalityConfiguration PERSONALITY_NAME=\"");
            sb.append(this.persName);
            sb.append("\" MODULE_NAME=\"");
            sb.append(this.moduleName);
            sb.append("\" CONSTITUENT_TABLES=");
            sb.append(this.consTabs);
            sb.append("\"/>\n");
            return sb.toString();
        }
    }
    
    class ConstituentTable
    {
        Object tableID;
        String tableName;
        int tableIndex;
        boolean mandatory;
        String fkName;
        PersonalityConfiguration pc;
        
        ConstituentTable() {
            this.tableID = null;
            this.tableName = null;
            this.tableIndex = -1;
            this.mandatory = true;
            this.fkName = null;
            this.pc = null;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("<ConstituentTable TABLE_ID=\"");
            sb.append(this.tableID);
            sb.append("\" TABLE_NAME=\"");
            sb.append(this.tableName);
            sb.append("\" TABLE_INDEX=\"");
            sb.append(this.tableIndex);
            sb.append("\" MANDATORY=\"");
            sb.append(this.mandatory);
            sb.append("\" FKNAME=\"");
            sb.append(this.fkName);
            sb.append("\"/>, ");
            return sb.toString();
        }
    }
}
