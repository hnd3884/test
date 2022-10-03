package com.adventnet.db.migration.handler;

import java.net.URL;
import com.adventnet.db.persistence.metadata.DataDictionary;
import java.util.Map;
import com.adventnet.persistence.xml.XmlRowTransformer;
import java.util.Iterator;
import java.io.File;
import com.adventnet.persistence.xml.Xml2DoConverter;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.xml.DynamicValueHandlerRepositry;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.Collection;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.QueryConstructor;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class ACSQLTableRowHandler extends AbstractDBMigrationHandler
{
    private static final Logger LOGGER;
    private static DataObject acSQLDo;
    
    @Override
    public Row preInvokeForInsert(final Row row) throws Exception {
        try {
            final Row found = getAcSQLDo().getRow("ACSQLString", row);
            if (found != null) {
                ACSQLTableRowHandler.LOGGER.info("DB specific ACSQLString found " + found);
                row.set("SQL", found.get("SQL"));
                row.set("GROUPBYUSED", found.get("GROUPBYUSED"));
            }
            else {
                ACSQLTableRowHandler.LOGGER.info("Unknown ACSQLString string row found. QUERYID :: " + row.get("QUERYID"));
                if (DBMigrationUtil.getSrcDBType() == DBMigrationUtil.getDestDBType()) {
                    ACSQLTableRowHandler.LOGGER.info("Source and destination db types are same. unknown row's SQL is fetched from DB,");
                    row.set("SQL", this.getACSQLFromDB(row));
                }
                else if (this.ignoreUnknownACSQLStrings()) {
                    return null;
                }
            }
            if (row.get("SQL") == null) {
                ACSQLTableRowHandler.LOGGER.info("Unable to identify destination DB compatible ACSQLString query. QUERYID :: " + row.get("QUERYID"));
            }
            return row;
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    public List<String> getSelectColumns(final SelectQuery sQuery) throws Exception {
        final List<String> colNames = super.getSelectColumns(sQuery);
        colNames.add("SQL");
        colNames.add("GROUPBYUSED");
        return colNames;
    }
    
    protected boolean ignoreUnknownACSQLStrings() {
        return false;
    }
    
    protected String getACSQLFromDB(final Row row) throws DataAccessException {
        final SelectQuery selectQuery = QueryConstructor.get("ACSQLString", row);
        final DataObject dataObject = DataAccess.get(selectQuery);
        return (String)dataObject.getFirstRow("ACSQLString").get("SQL");
    }
    
    public static DataObject getAcSQLDo() throws Exception {
        if (ACSQLTableRowHandler.acSQLDo == null) {
            setAcSQLDo();
        }
        return ACSQLTableRowHandler.acSQLDo;
    }
    
    public static void setAcSQLDo() throws Exception {
        try {
            loadAllDVH();
            final SelectQuery sq = new SelectQueryImpl(Table.getTable("ConfFile"));
            sq.addSelectColumn(Column.getColumn("ConfFile", "FILEID"));
            sq.addSelectColumn(Column.getColumn("ConfFile", "URL"));
            final List<Join> joins = QueryConstructor.getJoins(new ArrayList<String>() {
                {
                    this.add("ConfFile");
                    this.add("UVHValues");
                }
            });
            joins.add(new Join("UVHValues", "SelectQuery", new Criteria(Column.getColumn("UVHValues", "GENVALUES"), Column.getColumn("SelectQuery", "QUERYID"), 0), 2));
            joins.addAll(QueryConstructor.getJoins(new ArrayList<String>() {
                {
                    this.add("SelectQuery");
                    this.add("ACSQLString");
                }
            }));
            for (final Join join : joins) {
                sq.addJoin(join);
            }
            sq.setDistinct(true);
            sq.setCriteria(new Criteria(Column.getColumn("UVHValues", "TABLE_NAME"), "SelectQuery", 0));
            ACSQLTableRowHandler.LOGGER.fine(RelationalAPI.getInstance().getSelectSQL(sq));
            final DataObject dob = DataAccess.get(sq);
            final Iterator rows = dob.getRows("ConfFile");
            ACSQLTableRowHandler.LOGGER.info("ConfFiles :::: " + dob);
            final XmlRowTransformer rowTransformer = DynamicValueHandlerRepositry.getRowTransformer("ACSQLString");
            ACSQLTableRowHandler.acSQLDo = new WritableDataObject();
            final String serverHome = System.getProperty("server.home").replace("\\", "/");
            while (rows.hasNext()) {
                final Row row = rows.next();
                ACSQLTableRowHandler.LOGGER.info("ConfFile row" + row);
                final Map map = Xml2DoConverter.getPatternVsValue((Long)row.get("FILEID"));
                final File confFile = new File(((String)row.get("URL")).replaceAll("^.*\\{server.home\\}", serverHome));
                ACSQLTableRowHandler.LOGGER.info("Parsing ACSQLString DO-XML from :: " + confFile.getAbsolutePath());
                final DataObject conf = Xml2DoConverter.transform(confFile.toURL(), true, map);
                ACSQLTableRowHandler.LOGGER.info("Forming ACSQLString do");
                final Iterator acSQLRows = conf.getRows("ACSQLString");
                while (acSQLRows.hasNext()) {
                    ACSQLTableRowHandler.acSQLDo.addRow(acSQLRows.next());
                }
            }
            ACSQLTableRowHandler.LOGGER.fine("DB Specific ACSQLString do ::: " + ACSQLTableRowHandler.acSQLDo);
        }
        catch (final Throwable e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
    
    public static void loadAllDVH() throws Exception {
        final DynamicValueHandlerRepositry dvhrep = new DynamicValueHandlerRepositry();
        dvhrep.parse(DataDictionary.class.getResource("conf/dynamic-value-handlers.xml"));
        final Iterator moduleItr = DataAccess.get("Module", (Criteria)null).getRows("Module");
        while (moduleItr.hasNext()) {
            final Row moduleRow = moduleItr.next();
            final String moduleName = (String)moduleRow.get(3);
            final String moduleDir = System.getProperty("server.home") + File.separator + "conf" + File.separator + moduleName + File.separator;
            final File f = new File(moduleDir + "dynamic-value-handlers.xml");
            if (f.exists()) {
                final URL dvhurl = f.toURL();
                dvhrep.parse(dvhurl);
            }
        }
    }
    
    @Override
    public void preInvokeForFetchdata(final SelectQuery sQuery) throws Exception {
        ACSQLTableRowHandler.LOGGER.warning("Removing ACSQLString.SQL, ACSQLString.GROUPBYUSED columns from the select column list.");
        sQuery.removeSelectColumn(Column.getColumn("ACSQLString", "SQL"));
        sQuery.removeSelectColumn(Column.getColumn("ACSQLString", "GROUPBYUSED"));
        setAcSQLDo();
        super.preInvokeForFetchdata(sQuery);
    }
    
    public void set(final Object obj) {
    }
    
    public DataObject get() {
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(ACSQLTableRowHandler.class.getName());
        ACSQLTableRowHandler.acSQLDo = null;
    }
}
