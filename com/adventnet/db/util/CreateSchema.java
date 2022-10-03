package com.adventnet.db.util;

import com.zoho.conf.Configuration;
import java.util.Enumeration;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.regex.Matcher;
import java.sql.Statement;
import java.util.regex.Pattern;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.api.RelationalAPI;
import java.util.Set;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import com.adventnet.persistence.PersistenceInitializer;
import java.sql.Connection;
import java.util.Iterator;
import com.zoho.conf.AppResources;
import java.sql.SQLException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceException;
import com.adventnet.persistence.DeploymentNotificationInfo;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.Vector;
import java.util.Map;

public class CreateSchema
{
    private Map<String, Vector<String>> create;
    private Map<String, Vector<String>> index;
    private Map<String, Vector<String>> fkConstraint;
    private Map<String, Vector<String>> pkConstraint;
    private Map<String, Vector<String>> ukConstraint;
    private Map<String, Vector<String>> sqls;
    private Map<String, String> moduleNameVsNameInConf;
    private static String server_home;
    private static final Logger LOGGER;
    private Map<String, List<String>> moduleNameVsProcessedTable;
    
    public CreateSchema() {
        this.create = new ConcurrentHashMap<String, Vector<String>>();
        this.index = new ConcurrentHashMap<String, Vector<String>>();
        this.fkConstraint = new ConcurrentHashMap<String, Vector<String>>();
        this.pkConstraint = new ConcurrentHashMap<String, Vector<String>>();
        this.ukConstraint = new ConcurrentHashMap<String, Vector<String>>();
        this.sqls = new ConcurrentHashMap<String, Vector<String>>();
        this.moduleNameVsNameInConf = new ConcurrentHashMap<String, String>();
        this.moduleNameVsProcessedTable = new HashMap<String, List<String>>();
    }
    
    public CreateSchema(final DataSource dataSource) {
        this.create = new ConcurrentHashMap<String, Vector<String>>();
        this.index = new ConcurrentHashMap<String, Vector<String>>();
        this.fkConstraint = new ConcurrentHashMap<String, Vector<String>>();
        this.pkConstraint = new ConcurrentHashMap<String, Vector<String>>();
        this.ukConstraint = new ConcurrentHashMap<String, Vector<String>>();
        this.sqls = new ConcurrentHashMap<String, Vector<String>>();
        this.moduleNameVsNameInConf = new ConcurrentHashMap<String, String>();
        this.moduleNameVsProcessedTable = new HashMap<String, List<String>>();
    }
    
    public void createDBObjects(final DeploymentNotificationInfo md, final String dbName, final boolean ignoreQuotes) throws PersistenceException {
        this.createDBObjects(md, dbName, ignoreQuotes, true);
    }
    
    public void createDBObjects(final DeploymentNotificationInfo md, final String dbName, final boolean ignoreQuotes, final boolean createModuleTables) throws PersistenceException {
        final String moduleName = md.getModuleName();
        try {
            CreateSchema.LOGGER.log(Level.INFO, "Creating tables for the module {0}", moduleName);
            String schemaURL = null;
            URL url = null;
            if (dbName != null) {
                schemaURL = dbName + "/DatabaseSchema.conf";
                url = md.getResource(schemaURL);
            }
            if (url == null) {
                CreateSchema.LOGGER.log(Level.INFO, "DatabaseSchema.conf is not found at conf/{0}. Going to use generic DatabaseSchema.conf present in the conf directory", dbName);
                schemaURL = "DatabaseSchema.conf";
                url = md.getResource(schemaURL);
            }
            if (url == null) {
                CreateSchema.LOGGER.log(Level.INFO, "Generic DatabaseSchema.conf is also not found at conf.");
            }
            else {
                final InputStream stream = url.openStream();
                this.createSchemas(true, ignoreQuotes, stream);
                stream.close();
            }
            if (createModuleTables) {
                this.createTables(moduleName);
            }
        }
        catch (final PersistenceException pers) {
            CreateSchema.LOGGER.log(Level.WARNING, pers.getMessage(), pers);
            throw pers;
        }
        catch (final Exception e) {
            CreateSchema.LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new PersistenceException("Exception while creating tables :" + e.getMessage(), e);
        }
    }
    
    public void createSchemas(final boolean createIndices, final boolean ignoreQuotes, final InputStream stream) throws PersistenceException, SQLException {
        this.create(createIndices, ignoreQuotes, stream);
        this.createFKConstraints();
    }
    
    public void create(final boolean createIndices, final boolean ignoreQuotes, final InputStream stream) throws PersistenceException, SQLException {
        final String useSchema = AppResources.getString("com.adventnet.persistence.useSchema", "true");
        if (Boolean.valueOf(useSchema)) {
            this.readDataBaseSchema(stream, ignoreQuotes);
            this.createTables();
            this.createIndices(createIndices);
            this.executeSQLS();
            for (final String moduleName : this.pkConstraint.keySet()) {
                this.executeAllQueries(this.pkConstraint.get(moduleName));
            }
            for (final String moduleName : this.ukConstraint.keySet()) {
                this.executeAllQueries(this.ukConstraint.get(moduleName));
            }
        }
        else {
            CreateSchema.LOGGER.log(Level.INFO, "Since system property com.adventnet.persistence.useSchema is set to false, DatabaseSchema.conf is not used for creating schema objects");
        }
        this.create = new ConcurrentHashMap<String, Vector<String>>();
        this.index = new ConcurrentHashMap<String, Vector<String>>();
        this.cleanup();
    }
    
    private void cleanup() {
        this.pkConstraint.clear();
        this.ukConstraint.clear();
        this.sqls.clear();
    }
    
    public void createFKConstraints() throws PersistenceException, SQLException {
        CreateSchema.LOGGER.log(Level.FINER, "Going to create FK queries {0}", new Object[] { this.fkConstraint });
        for (final String moduleName : this.fkConstraint.keySet()) {
            this.executeAllQueries(this.fkConstraint.get(moduleName));
        }
        this.fkConstraint.clear();
    }
    
    public void createSchemas(final String moduleName, final boolean createTables, final boolean createTableConstraints, final boolean createFKConstraints, final Connection con, final String schemaName) throws PersistenceException, SQLException {
        if (createTables) {
            this.createTablesInModule(moduleName, con, schemaName);
        }
        if (createTableConstraints) {
            CreateSchema.LOGGER.info("Going to execute Index queries");
            this.createIndexForTheGivenStatements(this.index.get(moduleName), con);
            CreateSchema.LOGGER.info("Going to execute PK creation queries");
            this.executeAllQueries(this.pkConstraint.get(moduleName), con);
            CreateSchema.LOGGER.info("Going to execute UK creation queries");
            this.executeAllQueries(this.ukConstraint.get(moduleName), con);
        }
        if (createFKConstraints) {
            CreateSchema.LOGGER.info("Going to execute FK creation queries");
            this.executeAllQueries(this.fkConstraint.get(moduleName), con);
        }
    }
    
    public String readDataBaseSchema(final String moduleName) throws IOException {
        return this.readDataBaseSchema(moduleName, PersistenceInitializer.getConfigurationValue("DBName"));
    }
    
    public String getModuleNameFromDataBaseSchemaConf(final String moduleName) {
        return this.moduleNameVsNameInConf.get(moduleName);
    }
    
    public String readDataBaseSchema(final String moduleName, final String dbName) throws IOException {
        InputStream stream = null;
        String newModuleName = null;
        try {
            final String fileName = CreateSchema.server_home + "/conf/" + moduleName + "/" + dbName + "/DatabaseSchema.conf";
            final File schemaFile = new File(fileName);
            if (schemaFile.exists()) {
                CreateSchema.LOGGER.info("Processing DatabaseSchema.conf for module " + moduleName);
                stream = new FileInputStream(schemaFile);
                newModuleName = this.readDataBaseSchema(stream, false);
                this.moduleNameVsNameInConf.put(moduleName, newModuleName);
                return newModuleName;
            }
            CreateSchema.LOGGER.info("DatabaseSchema.conf file not exist for module " + moduleName);
            return newModuleName;
        }
        finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
    
    public int getTableCount(final String moduleName) {
        return this.create.get(moduleName).size();
    }
    
    private String readDataBaseSchema(final InputStream stream, final boolean ignoreQuotes) {
        BufferedReader din = null;
        String line = null;
        String modules = null;
        try {
            din = new BufferedReader(new InputStreamReader(stream));
            while ((line = din.readLine()) != null) {
                line = line.trim();
                if (!line.equals("") && !line.startsWith("#")) {
                    if (!line.startsWith("BeginModules")) {
                        continue;
                    }
                    modules = line.substring(12, line.length()).trim();
                    while (!(line = din.readLine()).startsWith("EndModules")) {
                        line = line.trim();
                        CreateSchema.LOGGER.log(Level.FINEST, "Next line: {0}", line);
                        if (line.equals("BeginCreateSchema")) {
                            this.fillCreateTableEntries(din, modules, ignoreQuotes);
                        }
                        else if (line.equals("BeginIndex")) {
                            this.fillQueries(din, modules, this.index, "EndIndex", ignoreQuotes);
                        }
                        else if (line.equals("BeginSQL")) {
                            this.fillQueries(din, modules, this.sqls, "EndSQL", ignoreQuotes);
                        }
                        else if (line.equals("BeginFK")) {
                            this.fillQueries(din, modules, this.fkConstraint, "EndFK", ignoreQuotes);
                        }
                        else if (line.equals("BeginUK")) {
                            this.fillQueries(din, modules, this.ukConstraint, "EndUK", ignoreQuotes);
                        }
                        else {
                            if (!line.equals("BeginPK")) {
                                continue;
                            }
                            this.fillQueries(din, modules, this.pkConstraint, "EndPK", ignoreQuotes);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            CreateSchema.LOGGER.log(Level.WARNING, "FileReader Ioexception in reading DatabaseSchema.conf", ex);
        }
        finally {
            try {
                if (din != null) {
                    din.reset();
                }
                if (din != null) {
                    din.close();
                }
            }
            catch (final Exception ex2) {}
        }
        CreateSchema.LOGGER.log(Level.FINEST, "All create table entries {0}", this.create);
        CreateSchema.LOGGER.log(Level.FINEST, "All create index entries {0}", this.index);
        CreateSchema.LOGGER.log(Level.FINEST, "All create pk entries {0}", this.pkConstraint);
        CreateSchema.LOGGER.log(Level.FINEST, "All create fk entries {0}", this.fkConstraint);
        CreateSchema.LOGGER.log(Level.FINEST, "All create uk entries {0}", this.ukConstraint);
        return modules;
    }
    
    private void fillCreateTableEntries(final BufferedReader din, final String modules, final boolean ignoreQuotes) throws IOException {
        String str = "";
        String line = null;
        while (!(line = din.readLine().trim()).equals("EndCreateSchema")) {
            str = str + line + " ";
        }
        if (ignoreQuotes) {
            str = str.replace('\"', ' ');
        }
        Vector<String> vec = this.create.get(modules);
        if (vec == null) {
            vec = new Vector<String>();
        }
        vec.addElement(str);
        this.create.put(modules, vec);
    }
    
    private void fillQueries(final BufferedReader din, final String modules, final Map<String, Vector<String>> queryMap, final String closingAnnotation, final boolean ignoreQuotes) throws IOException {
        String line = null;
        while (!(line = din.readLine().trim()).equals(closingAnnotation)) {
            if (ignoreQuotes) {
                line = line.replace('\"', ' ');
            }
            line.trim();
            if (line.length() > 0) {
                Vector<String> vec = queryMap.get(modules);
                if (vec == null) {
                    vec = new Vector<String>();
                }
                vec.addElement(line);
                queryMap.put(modules, vec);
            }
        }
    }
    
    public void executeSQLS() throws PersistenceException {
        final Set<String> keySet = this.sqls.keySet();
        for (final String key : keySet) {
            this.executeSQLS(key);
        }
    }
    
    public void executeSQLS(final String moduleName) throws PersistenceException {
        try {
            final RelationalAPI relapi = RelationalAPI.getInstance();
            final List<String> queries = this.sqls.get(moduleName);
            for (final String query : queries) {
                relapi.execute(query);
            }
        }
        catch (final SQLException sqle) {
            throw new PersistenceException(sqle.getMessage(), sqle);
        }
    }
    
    public void createTables(final String moduleName) throws PersistenceException {
        this.createTables(null, moduleName);
    }
    
    public void createTables(final String schemaName, final String moduleName) throws PersistenceException {
        final List tablesPresent = null;
        InputStream stream = null;
        try {
            final String fileName = CreateSchema.server_home + "/conf/" + moduleName + "/" + PersistenceInitializer.getConfigurationValue("DBName") + "/DatabaseSchema.conf";
            final File schemaFile = new File(fileName);
            if (schemaFile.exists()) {
                try {
                    stream = new FileInputStream(schemaFile);
                    this.createSchemas(true, true, stream);
                }
                catch (final Exception e) {
                    CreateSchema.LOGGER.log(Level.SEVERE, "Exception while creating tables", e);
                }
            }
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (final Exception e2) {
                    CreateSchema.LOGGER.log(Level.SEVERE, "Exception while creating tables", e2);
                }
            }
        }
        DataDictionary dd = null;
        try {
            dd = MetaDataUtil.getDataDictionary(moduleName);
        }
        catch (final MetaDataException mde) {
            final String mess = "Exception occured while getting DataDictionary for the module " + moduleName;
            CreateSchema.LOGGER.log(Level.FINER, mess, mde);
            throw new PersistenceException(mess, mde);
        }
        if (dd == null) {
            CreateSchema.LOGGER.log(Level.SEVERE, "Table creation requested for unknown module {0}", moduleName);
            throw new PersistenceException("Table creation requested for unknown module or module which doesnot have datadictionary" + moduleName);
        }
        final List tableDefns = dd.getTableDefinitions();
        final RelationalAPI relapi = RelationalAPI.getInstance();
        try {
            relapi.createTables(schemaName, tableDefns, null);
        }
        catch (final SQLException sqex) {
            CreateSchema.LOGGER.log(Level.WARNING, "Exception in creating table. ", sqex);
            throw new PersistenceException("Exception in creating tables ", sqex);
        }
    }
    
    private void createTables() throws PersistenceException, SQLException {
        final Set<String> keySet = this.create.keySet();
        for (final String moduleName : keySet) {
            this.createTablesInModule(moduleName);
        }
    }
    
    private synchronized void setProcessedTableNames(final String moduleName, final String tableName) {
        List<String> list = this.moduleNameVsProcessedTable.get(moduleName);
        if (list == null) {
            list = new ArrayList<String>();
        }
        list.add(tableName);
        this.moduleNameVsProcessedTable.put(moduleName, list);
    }
    
    public List<String> getSchemaConfCreatedTableNames(final String moduleName) {
        return this.moduleNameVsProcessedTable.get(moduleName);
    }
    
    private void createTablesInModule(final String moduleName) throws PersistenceException, SQLException {
        Connection con = null;
        try {
            con = RelationalAPI.getInstance().getConnection();
            this.createTablesInModule(moduleName, con, null);
        }
        finally {
            if (con != null) {
                con.close();
            }
        }
    }
    
    private void createTablesInModule(final String moduleName, final Connection conn, final String schemaName) throws PersistenceException {
        Statement stmt = null;
        final Vector<String> createStringVector = this.create.get(moduleName);
        if (createStringVector == null || createStringVector.size() <= 0) {
            return;
        }
        final Set<String> tablesPresent = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        try {
            tablesPresent.addAll(RelationalAPI.getInstance().getDBAdapter().getTables(conn, schemaName));
        }
        catch (final SQLException sqle) {
            CreateSchema.LOGGER.log(Level.SEVERE, "Exception occured while finding out the list of tables available in the database. Skipping table creation for the module {0} \n SQLException {1)", new Object[] { moduleName, sqle });
            return;
        }
        for (final String createstring : createStringVector) {
            String prstmt = null;
            String schemaType = null;
            String tableName = null;
            try {
                final int indexPointer = createstring.indexOf("(");
                if (indexPointer > 0) {
                    prstmt = createstring.substring(0, indexPointer);
                }
                try {
                    if (prstmt != null) {
                        prstmt = prstmt.trim();
                        final Pattern pattern = Pattern.compile("(create\\s*table)(.*)", 2);
                        final Matcher matcher = pattern.matcher(prstmt);
                        if (matcher.matches()) {
                            schemaType = "table";
                            tableName = matcher.group(2).trim();
                        }
                    }
                }
                catch (final Exception anye) {
                    CreateSchema.LOGGER.log(Level.FINE, "Exception occured while parsing string{0}", new Object[] { anye.getMessage() });
                    CreateSchema.LOGGER.log(Level.FINE, "", anye);
                }
                if (tableName != null) {
                    this.setProcessedTableNames(moduleName, tableName);
                }
                if (tableName != null && tablesPresent.contains(tableName)) {
                    CreateSchema.LOGGER.log(Level.FINE, "Table {0} already exists", tableName);
                }
                else if (createstring != null) {
                    CreateSchema.LOGGER.log(Level.FINEST, "Processing {0} {1}", new String[] { schemaType, tableName });
                    List<String> relatedTables = new ArrayList<String>();
                    if (tableName != null) {
                        relatedTables = this.getRelatedTables(tableName);
                    }
                    CreateSchema.LOGGER.log(Level.FINEST, "Creating {0} {1}", new String[] { schemaType, tableName });
                    CreateSchema.LOGGER.log(Level.FINEST, "Related tables are {0}", relatedTables);
                    stmt = conn.createStatement();
                    stmt.execute(createstring);
                    CreateSchema.LOGGER.log(Level.INFO, "Created{0}", createstring);
                }
            }
            catch (final SQLException ex) {
                String mess = ex.getMessage();
                mess = mess.toLowerCase();
                final int ecode = ex.getErrorCode();
                CreateSchema.LOGGER.log(Level.FINE, "Message :{0} Error Code :{1}", new Object[] { mess, new Integer(ecode) });
                CreateSchema.LOGGER.log(Level.FINE, "", ex);
                if (ecode != 955 && ecode != 2714) {
                    CreateSchema.LOGGER.log(Level.WARNING, "Exception in creating table ");
                    CreateSchema.LOGGER.log(Level.WARNING, "Statement being executed was :{0}", createstring);
                    CreateSchema.LOGGER.log(Level.WARNING, mess, ex);
                    throw new PersistenceException("Exception in creating table Statement being executed was :" + createstring, ex);
                }
                CreateSchema.LOGGER.log(Level.INFO, "Database object {0} already exists", schemaName);
                if (stmt == null) {
                    continue;
                }
                try {
                    stmt.close();
                }
                catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
            finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (final SQLException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }
    
    private List<String> getRelatedTables(final String tableName) throws PersistenceException {
        List<String> relatedTables = null;
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException mde) {
            CreateSchema.LOGGER.log(Level.WARNING, "Exception occured while getting table definition for {0}. Ignoring the exception.{1}", new Object[] { tableName, mde });
        }
        if (td != null) {
            relatedTables = new ArrayList<String>();
            final List<ForeignKeyDefinition> fkList = td.getForeignKeyList();
            if (fkList != null) {
                for (final ForeignKeyDefinition fk : fkList) {
                    final String masterTable = fk.getMasterTableName();
                    if (!tableName.equals(masterTable) && !relatedTables.contains(masterTable)) {
                        relatedTables.add(masterTable);
                    }
                }
            }
        }
        return relatedTables;
    }
    
    private void createIndices(final boolean createIndices) throws PersistenceException, SQLException {
        if (!createIndices) {
            return;
        }
        final Set<String> keySet = this.index.keySet();
        Connection con = null;
        try {
            con = RelationalAPI.getInstance().getConnection();
            for (final String modulename : keySet) {
                final Vector<String> createIndexVector = this.index.get(modulename);
                if (createIndexVector != null) {
                    if (createIndexVector.size() <= 0) {
                        continue;
                    }
                    this.createIndexForTheGivenStatements(createIndexVector, con);
                }
            }
        }
        finally {
            if (con != null) {
                con.close();
            }
        }
    }
    
    private void createIndexForTheGivenStatements(final Vector createIndexVector, final Connection con) throws PersistenceException {
        if (createIndexVector == null) {
            return;
        }
        final String[] errorMessages = { "already", "duplicate", "unique" };
        String indexstring = "";
        Statement stmt = null;
        try {
            final Enumeration indexStringEnu = createIndexVector.elements();
            while (indexStringEnu.hasMoreElements()) {
                indexstring = indexStringEnu.nextElement();
                try {
                    stmt = con.createStatement();
                    stmt.executeUpdate(indexstring);
                }
                catch (final SQLException ex) {
                    String mess = ex.getMessage();
                    mess = mess.toLowerCase();
                    final int ecode = ex.getErrorCode();
                    if (mess.indexOf(errorMessages[0]) >= 0 || mess.indexOf(errorMessages[1]) >= 0 || mess.indexOf(errorMessages[2]) >= 0) {
                        break;
                    }
                    if (ecode != 955 && ecode != 1408 && ecode != 1913) {
                        CreateSchema.LOGGER.log(Level.WARNING, "Exception while creating database table index.Unable to create index as specified by {0}", indexstring);
                        CreateSchema.LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                        throw new PersistenceException("Exception while creating database table index.Unable to create index as specified by " + indexstring, ex);
                    }
                    CreateSchema.LOGGER.log(Level.INFO, "Found ecode : {0}", ecode);
                }
            }
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (final Exception e) {
                CreateSchema.LOGGER.log(Level.INFO, "Exception occurred while closing connection/statement - {0}", e);
            }
        }
    }
    
    private boolean isErrorMessageInList(final String mess) {
        final String[] errorMessages = { "already", "duplicate", "unique", "conflicts" };
        for (int i = 0; i < errorMessages.length; ++i) {
            if (mess.indexOf(errorMessages[i]) != -1) {
                return true;
            }
        }
        return false;
    }
    
    private void executeAllQueries(final Vector<String> queriesToBeExecuted) throws PersistenceException, SQLException {
        Connection con = null;
        try {
            con = RelationalAPI.getInstance().getConnection();
            this.executeAllQueries(queriesToBeExecuted, con);
        }
        finally {
            if (con != null) {
                con.close();
            }
        }
    }
    
    private void executeAllQueries(final Vector<String> queriesToBeExecuted, final Connection con) throws PersistenceException, SQLException {
        if (queriesToBeExecuted == null) {
            return;
        }
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            for (final String query : queriesToBeExecuted) {
                try {
                    CreateSchema.LOGGER.log(Level.INFO, "Query executed:: " + query);
                    stmt.executeUpdate(query);
                }
                catch (final SQLException ex) {
                    throw new PersistenceException("Exception while executing query :::  " + query, ex);
                }
            }
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    static {
        CreateSchema.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        LOGGER = Logger.getLogger(CreateSchema.class.getName());
    }
}
