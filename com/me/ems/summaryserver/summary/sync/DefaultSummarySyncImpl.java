package com.me.ems.summaryserver.summary.sync;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import org.apache.commons.lang3.ArrayUtils;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.DeleteQuery;
import java.util.ArrayList;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Map;
import com.adventnet.persistence.DataAccess;
import com.adventnet.db.summaryserver.summary.adapter.Synchronization;
import com.adventnet.db.summaryserver.summary.sync.SSSyncHandler;
import com.me.ems.summaryserver.summary.sync.utils.SyncDataProcessor;
import com.me.ems.summaryserver.summary.sync.utils.SyncUtil;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.ems.summaryserver.common.sync.SyncException;
import java.io.Reader;
import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.logging.Logger;
import com.me.ems.summaryserver.summary.sync.factory.SyncAPI;

public abstract class DefaultSummarySyncImpl implements SyncAPI
{
    private static final String dbName;
    private final Logger logger;
    private final String sourceClass = "DefaultSummarySyncImpl";
    public final String probeId = "PROBE_ID";
    private String[] csvHeaders;
    private List<String[]> csvData;
    private DataObject mappingDO;
    private HashMap<String, HashMap<String, String>> newMappingEntries;
    
    public DefaultSummarySyncImpl() {
        this.logger = Logger.getLogger("SummarySyncLogger");
        this.csvHeaders = null;
        this.csvData = null;
        this.mappingDO = null;
        this.newMappingEntries = new HashMap<String, HashMap<String, String>>();
    }
    
    @Override
    public void parseCSVFile(final String csvFilePath) throws SyncException {
        final String sourceMethod = "parseCSVFile";
        CSVReader csvReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(csvFilePath);
            csvReader = new CSVReader((Reader)fileReader);
            this.csvData = csvReader.readAll();
            this.csvHeaders = this.csvData.get(0);
            this.csvData.remove(0);
        }
        catch (final Exception e) {
            throw new SyncException(950202, e);
        }
        finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (csvReader != null) {
                    csvReader.close();
                }
            }
            catch (final Exception ex) {
                SyMLogger.error(this.logger, "DefaultSummarySyncImpl", sourceMethod, "Error in finally for readDataFromCSV: ", ex);
            }
        }
    }
    
    @Override
    public List<String[]> getCsvData() throws SyncException {
        return this.csvData;
    }
    
    @Override
    public String[] getCsvHeaders() {
        return this.csvHeaders;
    }
    
    @Override
    public void validateData(final Long probeID, final long moduleID, final String tableName, final String[] headers, final List<String[]> probeData) throws SyncException {
        final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
        final int recordLimit = syncModuleMetaDAOUtil.getRecordLimit(moduleID);
        final int recordCount = probeData.size();
        SyncUtil.getInstance().addToProcessingLogHashMap(tableName, "RecordCount", String.valueOf(recordCount));
        if (recordCount <= 0) {
            throw new SyncException(950203, "Empty CSV, no data to process, record count : " + recordCount);
        }
        if (recordCount > recordLimit) {
            throw new SyncException(950204, "CSV record count exceeds limit : " + recordCount + " > " + recordLimit);
        }
    }
    
    @Override
    public void resolveConflicts(final Long probeID, final long moduleID, final String tableName, final String[] headers, final List<String[]> probeData) throws SyncException {
        final long startTime = System.currentTimeMillis();
        try {
            final SyncDataProcessor syncDataProcessor = new SyncDataProcessor();
            final HashMap<String, HashMap> conflictMetaData = syncDataProcessor.getConflictResolutionMetaData(tableName, moduleID);
            this.resolveConflictValues(probeID, tableName, headers, probeData, conflictMetaData);
        }
        catch (final SyncException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new SyncException(950504, probeID, moduleID, tableName, e2);
        }
        finally {
            final long endTime = System.currentTimeMillis();
            SyncUtil.getInstance().addToProcessingLogHashMap(tableName, "ConflictResolveTime", String.valueOf(endTime - startTime));
        }
    }
    
    @Override
    public void resolveConflictValues(final Long probeID, final String tableName, final String[] headers, final List<String[]> csvData, final HashMap<String, HashMap> conflictMetaData) throws Exception {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: invokespecial   com/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor.<init>:()V
        //     7: astore          syncDataProcessor
        //     9: aconst_null    
        //    10: astore          sequenceGenerator
        //    12: aload           conflictMetaData
        //    14: invokevirtual   java/util/HashMap.entrySet:()Ljava/util/Set;
        //    17: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //    22: astore          8
        //    24: aload           8
        //    26: invokeinterface java/util/Iterator.hasNext:()Z
        //    31: ifeq            646
        //    34: aload           8
        //    36: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    41: checkcast       Ljava/util/Map$Entry;
        //    44: astore          entry
        //    46: aload           entry
        //    48: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //    53: checkcast       Ljava/lang/String;
        //    56: astore          fieldName
        //    58: aload           conflictMetaData
        //    60: aload           fieldName
        //    62: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    65: checkcast       Ljava/util/HashMap;
        //    68: astore          conflictFieldMeta
        //    70: aload           syncDataProcessor
        //    72: aload           conflictFieldMeta
        //    74: invokevirtual   com/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor.getMappingTableName:(Ljava/util/HashMap;)Ljava/lang/String;
        //    77: astore          mappingTableName
        //    79: aload           syncDataProcessor
        //    81: aload           conflictFieldMeta
        //    83: invokevirtual   com/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor.getMappingKeyField:(Ljava/util/HashMap;)Ljava/lang/String;
        //    86: astore          mappingKeyField
        //    88: aload           syncDataProcessor
        //    90: aload           conflictFieldMeta
        //    92: invokevirtual   com/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor.getMappingValueField:(Ljava/util/HashMap;)Ljava/lang/String;
        //    95: astore          mappingValueField
        //    97: aload_0         /* this */
        //    98: getfield        com/me/ems/summaryserver/summary/sync/DefaultSummarySyncImpl.newMappingEntries:Ljava/util/HashMap;
        //   101: aload           mappingTableName
        //   103: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   106: checkcast       Ljava/util/HashMap;
        //   109: astore          mappingEntries
        //   111: aload           mappingEntries
        //   113: ifnonnull       125
        //   116: new             Ljava/util/HashMap;
        //   119: dup            
        //   120: invokespecial   java/util/HashMap.<init>:()V
        //   123: astore          mappingEntries
        //   125: aload           conflictFieldMeta
        //   127: ldc             "CREATE_MAPPING"
        //   129: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   132: checkcast       Ljava/lang/Boolean;
        //   135: invokevirtual   java/lang/Boolean.booleanValue:()Z
        //   138: istore          createMapping
        //   140: aconst_null    
        //   141: astore          uniqueFields
        //   143: iload           createMapping
        //   145: ifeq            275
        //   148: aload           syncDataProcessor
        //   150: aload           conflictFieldMeta
        //   152: invokevirtual   com/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor.getUniqueFields:(Ljava/util/HashMap;)Ljava/util/List;
        //   155: astore          uniqueFields
        //   157: aload_2         /* tableName */
        //   158: invokestatic    com/adventnet/db/persistence/metadata/util/MetaDataUtil.getTableDefinitionByName:(Ljava/lang/String;)Lcom/adventnet/db/persistence/metadata/TableDefinition;
        //   161: astore          tableDefinition
        //   163: aload           tableDefinition
        //   165: invokevirtual   com/adventnet/db/persistence/metadata/TableDefinition.getPrimaryKey:()Lcom/adventnet/db/persistence/metadata/PrimaryKeyDefinition;
        //   168: astore          primaryKeyDefinition
        //   170: aload           primaryKeyDefinition
        //   172: invokevirtual   com/adventnet/db/persistence/metadata/PrimaryKeyDefinition.getColumnList:()Ljava/util/List;
        //   175: astore          pkColumns
        //   177: iconst_0       
        //   178: istore          index
        //   180: iload           index
        //   182: aload           pkColumns
        //   184: invokeinterface java/util/List.size:()I
        //   189: if_icmpge       275
        //   192: aload           primaryKeyDefinition
        //   194: invokevirtual   com/adventnet/db/persistence/metadata/PrimaryKeyDefinition.getColumnList:()Ljava/util/List;
        //   197: iload           index
        //   199: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
        //   204: checkcast       Ljava/lang/String;
        //   207: astore          pkColumn
        //   209: aload           pkColumn
        //   211: aload           fieldName
        //   213: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   216: ifeq            269
        //   219: aload           tableDefinition
        //   221: aload           pkColumn
        //   223: invokevirtual   com/adventnet/db/persistence/metadata/TableDefinition.getColumnDefinitionByName:(Ljava/lang/String;)Lcom/adventnet/db/persistence/metadata/ColumnDefinition;
        //   226: astore          cd
        //   228: aload           cd
        //   230: invokevirtual   com/adventnet/db/persistence/metadata/ColumnDefinition.getUniqueValueGeneration:()Lcom/adventnet/db/persistence/metadata/UniqueValueGeneration;
        //   233: astore          uvg
        //   235: aload           uvg
        //   237: ifnull          258
        //   240: aload           uvg
        //   242: invokevirtual   com/adventnet/db/persistence/metadata/UniqueValueGeneration.getGeneratorName:()Ljava/lang/String;
        //   245: aload           cd
        //   247: invokevirtual   com/adventnet/db/persistence/metadata/ColumnDefinition.getDataType:()Ljava/lang/String;
        //   250: invokestatic    com/adventnet/persistence/internal/SequenceGeneratorRepository.getOrCreate:(Ljava/lang/String;Ljava/lang/String;)Lcom/adventnet/db/persistence/SequenceGenerator;
        //   253: astore          sequenceGenerator
        //   255: goto            275
        //   258: aload_2         /* tableName */
        //   259: ldc             "BIGINT"
        //   261: invokestatic    com/adventnet/persistence/internal/SequenceGeneratorRepository.getOrCreate:(Ljava/lang/String;Ljava/lang/String;)Lcom/adventnet/db/persistence/SequenceGenerator;
        //   264: astore          sequenceGenerator
        //   266: goto            275
        //   269: iinc            index, 1
        //   272: goto            180
        //   275: iconst_m1      
        //   276: istore          fieldIndex
        //   278: iconst_0       
        //   279: istore          i
        //   281: iload           i
        //   283: aload_3         /* headers */
        //   284: arraylength    
        //   285: if_icmpge       313
        //   288: aload_3         /* headers */
        //   289: iload           i
        //   291: aaload         
        //   292: aload           fieldName
        //   294: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   297: ifeq            307
        //   300: iload           i
        //   302: istore          fieldIndex
        //   304: goto            313
        //   307: iinc            i, 1
        //   310: goto            281
        //   313: aload           csvData
        //   315: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
        //   320: astore          19
        //   322: aload           19
        //   324: invokeinterface java/util/Iterator.hasNext:()Z
        //   329: ifeq            623
        //   332: aload           19
        //   334: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   339: checkcast       [Ljava/lang/String;
        //   342: astore          rowData
        //   344: aload           rowData
        //   346: iload           fieldIndex
        //   348: aaload         
        //   349: astore          probeValue
        //   351: aload           syncDataProcessor
        //   353: aload           mappingTableName
        //   355: aload           mappingKeyField
        //   357: aload           probeValue
        //   359: aload           mappingValueField
        //   361: invokevirtual   com/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor.getMappingTableCacheValue:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Long;
        //   364: astore          mappingTableValue
        //   366: aload           mappingTableValue
        //   368: ifnull          381
        //   371: aload           mappingTableValue
        //   373: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   376: astore          summaryValue
        //   378: goto            613
        //   381: iload           createMapping
        //   383: ifeq            564
        //   386: new             Ljava/util/ArrayList;
        //   389: dup            
        //   390: invokespecial   java/util/ArrayList.<init>:()V
        //   393: astore          uniqueValues
        //   395: iconst_0       
        //   396: istore          i
        //   398: iload           i
        //   400: aload           rowData
        //   402: arraylength    
        //   403: if_icmpge       439
        //   406: aload           uniqueFields
        //   408: aload_3         /* headers */
        //   409: iload           i
        //   411: aaload         
        //   412: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
        //   417: ifeq            433
        //   420: aload           uniqueValues
        //   422: aload           rowData
        //   424: iload           i
        //   426: aaload         
        //   427: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   432: pop            
        //   433: iinc            i, 1
        //   436: goto            398
        //   439: aload           syncDataProcessor
        //   441: aload_2         /* tableName */
        //   442: aload           fieldName
        //   444: aload           uniqueFields
        //   446: aload           uniqueValues
        //   448: invokevirtual   com/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor.getTableValue:(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;)Ljava/lang/Long;
        //   451: astore          tableValue
        //   453: aload           tableValue
        //   455: ifnull          468
        //   458: aload           tableValue
        //   460: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   463: astore          summaryValue
        //   465: goto            527
        //   468: aload           sequenceGenerator
        //   470: ifnull          488
        //   473: aload           sequenceGenerator
        //   475: invokeinterface com/adventnet/db/persistence/SequenceGenerator.nextValue:()Ljava/lang/Object;
        //   480: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   483: astore          summaryValue
        //   485: goto            527
        //   488: new             Lcom/me/ems/summaryserver/common/sync/SyncException;
        //   491: dup            
        //   492: ldc             950502
        //   494: new             Ljava/lang/StringBuilder;
        //   497: dup            
        //   498: invokespecial   java/lang/StringBuilder.<init>:()V
        //   501: ldc             "Sequence Generator cannot be created for "
        //   503: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   506: aload_2         /* tableName */
        //   507: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   510: ldc             "."
        //   512: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   515: aload           fieldName
        //   517: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   520: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   523: invokespecial   com/me/ems/summaryserver/common/sync/SyncException.<init>:(ILjava/lang/String;)V
        //   526: athrow         
        //   527: aload_0         /* this */
        //   528: aload           syncDataProcessor
        //   530: aload           mappingTableName
        //   532: aload           mappingKeyField
        //   534: aload           mappingValueField
        //   536: aload           probeValue
        //   538: aload           summaryValue
        //   540: aload_1         /* probeID */
        //   541: aload_0         /* this */
        //   542: getfield        com/me/ems/summaryserver/summary/sync/DefaultSummarySyncImpl.mappingDO:Lcom/adventnet/persistence/DataObject;
        //   545: invokevirtual   com/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor.createMapping:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Long;Lcom/adventnet/persistence/DataObject;)Lcom/adventnet/persistence/DataObject;
        //   548: putfield        com/me/ems/summaryserver/summary/sync/DefaultSummarySyncImpl.mappingDO:Lcom/adventnet/persistence/DataObject;
        //   551: aload           mappingEntries
        //   553: aload           probeValue
        //   555: aload           summaryValue
        //   557: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   560: pop            
        //   561: goto            613
        //   564: new             Lcom/me/ems/summaryserver/common/sync/SyncException;
        //   567: dup            
        //   568: ldc             950503
        //   570: new             Ljava/lang/StringBuilder;
        //   573: dup            
        //   574: invokespecial   java/lang/StringBuilder.<init>:()V
        //   577: ldc             "Dependent Summary data missing for "
        //   579: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   582: aload_2         /* tableName */
        //   583: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   586: ldc             "."
        //   588: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   591: aload           fieldName
        //   593: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   596: ldc             " - "
        //   598: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   601: aload           probeValue
        //   603: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   606: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   609: invokespecial   com/me/ems/summaryserver/common/sync/SyncException.<init>:(ILjava/lang/String;)V
        //   612: athrow         
        //   613: aload           rowData
        //   615: iload           fieldIndex
        //   617: aload           summaryValue
        //   619: aastore        
        //   620: goto            322
        //   623: aload           mappingEntries
        //   625: invokevirtual   java/util/HashMap.isEmpty:()Z
        //   628: ifne            643
        //   631: aload_0         /* this */
        //   632: getfield        com/me/ems/summaryserver/summary/sync/DefaultSummarySyncImpl.newMappingEntries:Ljava/util/HashMap;
        //   635: aload           mappingTableName
        //   637: aload           mappingEntries
        //   639: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   642: pop            
        //   643: goto            24
        //   646: return         
        //    Exceptions:
        //  throws java.lang.Exception
        //    Signature:
        //  (Ljava/lang/Long;Ljava/lang/String;[Ljava/lang/String;Ljava/util/List<[Ljava/lang/String;>;Ljava/util/HashMap<Ljava/lang/String;Ljava/util/HashMap;>;)V
        //    StackMapTable: 00 16 FE 00 18 07 01 34 07 01 35 07 01 36 FF 00 64 00 10 07 01 0A 07 01 0B 07 00 E6 07 00 18 07 01 0C 07 01 37 07 01 34 07 01 35 07 01 36 07 01 38 07 00 E6 07 01 37 07 00 E6 07 00 E6 07 00 E6 07 01 37 00 00 FF 00 36 00 16 07 01 0A 07 01 0B 07 00 E6 07 00 18 07 01 0C 07 01 37 07 01 34 07 01 35 07 01 36 07 01 38 07 00 E6 07 01 37 07 00 E6 07 00 E6 07 00 E6 07 01 37 01 07 01 0C 07 01 39 07 01 3A 07 01 0C 01 00 00 FE 00 4D 07 00 E6 07 01 3B 07 01 3C F8 00 0A FF 00 05 00 12 07 01 0A 07 01 0B 07 00 E6 07 00 18 07 01 0C 07 01 37 07 01 34 07 01 35 07 01 36 07 01 38 07 00 E6 07 01 37 07 00 E6 07 00 E6 07 00 E6 07 01 37 01 07 01 0C 00 00 FD 00 05 01 01 19 FA 00 05 FC 00 08 07 01 36 FF 00 3A 00 18 07 01 0A 07 01 0B 07 00 E6 07 00 18 07 01 0C 07 01 37 07 01 34 07 01 35 07 01 36 07 01 38 07 00 E6 07 01 37 07 00 E6 07 00 E6 07 00 E6 07 01 37 01 07 01 0C 01 07 01 36 07 00 18 07 00 E6 00 07 01 0B 00 00 FD 00 10 07 01 0C 01 22 FA 00 05 FC 00 1C 07 01 0B 13 FF 00 26 00 1A 07 01 0A 07 01 0B 07 00 E6 07 00 18 07 01 0C 07 01 37 07 01 34 07 01 35 07 01 36 07 01 38 07 00 E6 07 01 37 07 00 E6 07 00 E6 07 00 E6 07 01 37 01 07 01 0C 01 07 01 36 07 00 18 07 00 E6 07 00 E6 07 01 0B 07 01 0C 07 01 0B 00 00 FF 00 24 00 18 07 01 0A 07 01 0B 07 00 E6 07 00 18 07 01 0C 07 01 37 07 01 34 07 01 35 07 01 36 07 01 38 07 00 E6 07 01 37 07 00 E6 07 00 E6 07 00 E6 07 01 37 01 07 01 0C 01 07 01 36 07 00 18 07 00 E6 00 07 01 0B 00 00 FF 00 30 00 18 07 01 0A 07 01 0B 07 00 E6 07 00 18 07 01 0C 07 01 37 07 01 34 07 01 35 07 01 36 07 01 38 07 00 E6 07 01 37 07 00 E6 07 00 E6 07 00 E6 07 01 37 01 07 01 0C 01 07 01 36 07 00 18 07 00 E6 07 00 E6 07 01 0B 00 00 FF 00 09 00 13 07 01 0A 07 01 0B 07 00 E6 07 00 18 07 01 0C 07 01 37 07 01 34 07 01 35 07 01 36 07 01 38 07 00 E6 07 01 37 07 00 E6 07 00 E6 07 00 E6 07 01 37 01 07 01 0C 01 00 00 FF 00 13 00 09 07 01 0A 07 01 0B 07 00 E6 07 00 18 07 01 0C 07 01 37 07 01 34 07 01 35 07 01 36 00 00 FA 00 02
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:284)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:279)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:154)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:225)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedGenericType.accept(CoreMetadataFactory.java:653)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:314)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2611)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:790)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2689)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:892)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:593)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:405)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public void populateDataFromCSVFile(final Long probeID, final long moduleID, final String tableName, final String[] headers, final List<String[]> probeData) throws SyncException {
        final String sourceMethod = "populateDataFromCSVFile";
        final Long startTime = System.currentTimeMillis();
        final String tempTable = "TEMP_" + tableName;
        final Synchronization syncImpl = SSSyncHandler.getInstance().getSyncImpl(DefaultSummarySyncImpl.dbName);
        final SyncDataProcessor syncDataProcessor = new SyncDataProcessor();
        try {
            syncDataProcessor.bulkLoadStagingTable(tempTable, headers, probeData);
            syncImpl.upsertToOriginalTable(tableName, tempTable);
        }
        catch (final SyncException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new SyncException(950602, e2);
        }
        finally {
            try {
                syncDataProcessor.truncateTable(tempTable);
            }
            catch (final Exception e3) {
                SyMLogger.error(this.logger, "DefaultSummarySyncImpl", sourceMethod, "Exception in truncateTable", e3);
            }
            final Long endTime = System.currentTimeMillis();
            final Long processingTime = endTime - startTime;
            SyncUtil.getInstance().addToProcessingLogHashMap(tableName, "BulkLoadTime", String.valueOf(processingTime));
        }
    }
    
    @Override
    public void postProcessConflictData(final Long probeID, final long moduleID, final String tableName, final String[] headers, final List<String[]> probeData) throws Exception {
        final String sourceMethod = "postProcessConflictData";
        SyMLogger.info(this.logger, "DefaultSummarySyncImpl", sourceMethod, "Inside postProcessConflictData for ProbeID : {0} , moduleID : {1} ", new Object[] { probeID, moduleID });
        final Long startTime = System.currentTimeMillis();
        try {
            if (this.mappingDO != null) {
                DataAccess.add(this.mappingDO);
            }
            final Long cacheUpdateStart = System.currentTimeMillis();
            if (!this.newMappingEntries.isEmpty()) {
                for (final Map.Entry entry : this.newMappingEntries.entrySet()) {
                    final String mappingTableName = entry.getKey();
                    final HashMap<String, String> mappingEntries = this.newMappingEntries.get(mappingTableName);
                    mappingEntries.forEach((probeValue, summaryValue) -> ApiFactoryProvider.getCacheAccessAPI().putCache(s + "_" + probeValue, Long.valueOf(summaryValue), 2));
                }
            }
            final Long cacheUpdateEnd = System.currentTimeMillis();
            SyMLogger.info(this.logger, "DefaultSummarySyncImpl", sourceMethod, "Cache update Time alone (ms): ", cacheUpdateEnd - cacheUpdateStart);
        }
        catch (final Exception e) {
            throw new SyncException(950505, probeID, moduleID, tableName, e);
        }
        finally {
            final Long endTime = System.currentTimeMillis();
            final Long processingTime = endTime - startTime;
            SyncUtil.getInstance().addToProcessingLogHashMap(tableName, "CacheAndDoUpdateTime", String.valueOf(processingTime));
        }
    }
    
    @Override
    public abstract void preProcessData(final Long p0, final long p1, final String p2, final String[] p3, final List<String[]> p4) throws SyncException;
    
    @Override
    public abstract void postProcessData(final Long p0, final long p1, final String p2, final String[] p3, final List<String[]> p4) throws SyncException, MetaDataException, DataAccessException;
    
    @Override
    public JSONObject getDeletionJSON(final String str) throws SyncException {
        JSONObject deletionJSON;
        try {
            final SyncDataProcessor syncDataProcessor = new SyncDataProcessor();
            deletionJSON = syncDataProcessor.convertDeletionStringToJSONObject(str);
        }
        catch (final Exception e) {
            throw new SyncException(950302, e);
        }
        return deletionJSON;
    }
    
    @Override
    public void validateJSONData(final JSONObject deletionJSON) throws SyncException {
        final int length = deletionJSON.length();
        if (length <= 0) {
            throw new SyncException(950303, "Empty deletion json");
        }
    }
    
    @Override
    public HashMap<String, HashMap> getDeleteDataFromJSON(final JSONObject deletionJSON) throws SyncException {
        try {
            final SyncDataProcessor syncDataProcessor = new SyncDataProcessor();
            return syncDataProcessor.getDeleteDataFromJSON(deletionJSON);
        }
        catch (final Exception e) {
            throw new SyncException(950701, e);
        }
    }
    
    @Override
    public HashMap resolveDeletionConflicts(final Long probeID, final long moduleID, final String tableName, final HashMap<String, ArrayList> tableDataMap) throws SyncException, DataAccessException {
        HashMap summaryData;
        try {
            final SyncDataProcessor syncDataProcessor = new SyncDataProcessor();
            final HashMap<String, HashMap> conflictMetaData = syncDataProcessor.getConflictResolutionMetaData(tableName, moduleID);
            summaryData = syncDataProcessor.getDeletionResolutionData(probeID, moduleID, conflictMetaData, tableName, tableDataMap);
        }
        catch (final SyncException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new SyncException(950504, probeID, moduleID, tableName, e2);
        }
        return summaryData;
    }
    
    @Override
    public List<DeleteQuery> getDeleteQuery(final Long probeID, final long moduleID, final String tableName, final HashMap<String, ArrayList> deleteData) throws SyncException {
        try {
            final List pkColumns = new ArrayList();
            final List<ArrayList> pkIdsToDelete = new ArrayList<ArrayList>();
            for (final Map.Entry entry : deleteData.entrySet()) {
                final String pkColName = entry.getKey();
                final ArrayList pkValues = entry.getValue();
                pkColumns.add(pkColName);
                pkIdsToDelete.add(pkValues);
            }
            final SyncDataProcessor syncDataProcessor = new SyncDataProcessor();
            return syncDataProcessor.getDeleteQuery(moduleID, tableName, pkColumns, pkIdsToDelete);
        }
        catch (final Exception e) {
            throw new SyncException(950701, e);
        }
    }
    
    @Override
    public void postProcessConflictDeletionData(final Long probeID, final long moduleID, final String tableName, final HashMap<String, ArrayList> tableDataMap, final HashMap<String, ArrayList> deleteSummaryData) throws SyncException, DataAccessException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: astore          sourceMethod
        //     4: aload_0         /* this */
        //     5: getfield        com/me/ems/summaryserver/summary/sync/DefaultSummarySyncImpl.logger:Ljava/util/logging/Logger;
        //     8: ldc             "DefaultSummarySyncImpl"
        //    10: aload           sourceMethod
        //    12: ldc             "Inside postProcessConflictDeletionData for ProbeID : {0} , ModuleID : {1}"
        //    14: iconst_2       
        //    15: anewarray       Ljava/lang/Object;
        //    18: dup            
        //    19: iconst_0       
        //    20: aload_1         /* probeID */
        //    21: aastore        
        //    22: dup            
        //    23: iconst_1       
        //    24: lload_2         /* moduleID */
        //    25: invokestatic    java/lang/Long.valueOf:(J)Ljava/lang/Long;
        //    28: aastore        
        //    29: invokestatic    com/me/devicemanagement/framework/server/logger/SyMLogger.info:(Ljava/util/logging/Logger;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V
        //    32: new             Lcom/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor;
        //    35: dup            
        //    36: invokespecial   com/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor.<init>:()V
        //    39: astore          syncDataProcessor
        //    41: aload           syncDataProcessor
        //    43: aload           tableName
        //    45: lload_2         /* moduleID */
        //    46: invokevirtual   com/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor.getConflictResolutionMetaData:(Ljava/lang/String;J)Ljava/util/HashMap;
        //    49: astore          conflictMetaData
        //    51: aload           conflictMetaData
        //    53: invokevirtual   java/util/HashMap.entrySet:()Ljava/util/Set;
        //    56: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //    61: astore          10
        //    63: aload           10
        //    65: invokeinterface java/util/Iterator.hasNext:()Z
        //    70: ifeq            206
        //    73: aload           10
        //    75: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    80: checkcast       Ljava/util/Map$Entry;
        //    83: astore          entry
        //    85: aload           entry
        //    87: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //    92: checkcast       Ljava/lang/String;
        //    95: astore          fieldName
        //    97: aload           conflictMetaData
        //    99: aload           fieldName
        //   101: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   104: checkcast       Ljava/util/HashMap;
        //   107: astore          conflictFieldMeta
        //   109: aload           syncDataProcessor
        //   111: aload           conflictFieldMeta
        //   113: invokevirtual   com/me/ems/summaryserver/summary/sync/utils/SyncDataProcessor.getMappingTableName:(Ljava/util/HashMap;)Ljava/lang/String;
        //   116: astore          mappingTableName
        //   118: aload           tableDataMap
        //   120: aload           fieldName
        //   122: invokevirtual   java/util/HashMap.containsKey:(Ljava/lang/Object;)Z
        //   125: ifeq            203
        //   128: aload           tableDataMap
        //   130: aload           fieldName
        //   132: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   135: checkcast       Ljava/util/ArrayList;
        //   138: astore          pkValuesToDelete
        //   140: aload           pkValuesToDelete
        //   142: invokevirtual   java/util/ArrayList.iterator:()Ljava/util/Iterator;
        //   145: astore          16
        //   147: aload           16
        //   149: invokeinterface java/util/Iterator.hasNext:()Z
        //   154: ifeq            203
        //   157: aload           16
        //   159: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   164: astore          pkValue
        //   166: invokestatic    com/me/devicemanagement/framework/server/factory/ApiFactoryProvider.getCacheAccessAPI:()Lcom/me/devicemanagement/framework/server/cache/CacheAccessAPI;
        //   169: new             Ljava/lang/StringBuilder;
        //   172: dup            
        //   173: invokespecial   java/lang/StringBuilder.<init>:()V
        //   176: aload           mappingTableName
        //   178: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   181: ldc             "_"
        //   183: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   186: aload           pkValue
        //   188: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   191: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   194: iconst_2       
        //   195: invokeinterface com/me/devicemanagement/framework/server/cache/CacheAccessAPI.removeCache:(Ljava/lang/String;I)V
        //   200: goto            147
        //   203: goto            63
        //   206: return         
        //    Exceptions:
        //  throws com.me.ems.summaryserver.common.sync.SyncException
        //  throws com.adventnet.persistence.DataAccessException
        //    Signature:
        //  (Ljava/lang/Long;JLjava/lang/String;Ljava/util/HashMap<Ljava/lang/String;Ljava/util/ArrayList;>;Ljava/util/HashMap<Ljava/lang/String;Ljava/util/ArrayList;>;)V
        //    StackMapTable: 00 04 FF 00 3F 00 0A 07 01 0A 07 01 0B 04 07 00 E6 07 01 37 07 01 37 07 00 E6 07 01 34 07 01 37 07 01 36 00 00 FF 00 53 00 10 07 01 0A 07 01 0B 04 07 00 E6 07 01 37 07 01 37 07 00 E6 07 01 34 07 01 37 07 01 36 07 01 38 07 00 E6 07 01 37 07 00 E6 07 01 6C 07 01 36 00 00 FF 00 37 00 0A 07 01 0A 07 01 0B 04 07 00 E6 07 01 37 07 01 37 07 00 E6 07 01 34 07 01 37 07 01 36 00 00 FA 00 02
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:284)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:279)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:154)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:225)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedGenericType.accept(CoreMetadataFactory.java:653)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:314)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2611)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:790)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2689)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:892)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:593)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:405)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public void performDeletion(final ArrayList<DeleteQuery> delQueriesList) throws SyncException {
        final SyncDataProcessor syncDataProcessor = new SyncDataProcessor();
        try {
            final int rowsDeleted = syncDataProcessor.performDeletion(delQueriesList);
            SyncUtil.getInstance().addToProcessingLogHashMap("Deletion_Info", "RecordCount", String.valueOf(rowsDeleted));
        }
        catch (final Exception e) {
            throw new SyncException(950701, e);
        }
    }
    
    @Override
    public void processDataForProbeMappingTable(final String tableName, final String probeMappingTableName, final Long probeID, final String[] headers, final List<String[]> probeData) throws MetaDataException, DataAccessException {
        final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
        final PrimaryKeyDefinition primaryKeyDefinition = tableDefinition.getPrimaryKey();
        final String pkColumnName = primaryKeyDefinition.getColumnList().get(0);
        final String dataType = tableDefinition.getColumnDefinitionByName(pkColumnName).getDataType();
        final int indexOfPKColumn = ArrayUtils.indexOf((Object[])headers, (Object)pkColumnName);
        final List<Object> valueList = new ArrayList<Object>();
        for (final String[] data : probeData) {
            final String lowerCase = dataType.toLowerCase();
            switch (lowerCase) {
                case "integer": {
                    valueList.add(Integer.parseInt(data[indexOfPKColumn]));
                    continue;
                }
                case "bigint": {
                    valueList.add(Long.parseLong(data[indexOfPKColumn]));
                    continue;
                }
                default: {
                    valueList.add(data[indexOfPKColumn]);
                    continue;
                }
            }
        }
        this.addProbeMappingTableData(probeID, valueList, pkColumnName, probeMappingTableName);
    }
    
    private void addProbeMappingTableData(final Long probeID, final List<Object> valueList, final String columnName, final String probeMappingTableName) throws DataAccessException {
        final String sourceMethod = "addProbeMappingTableData";
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(probeMappingTableName));
        Boolean isValidUpdate = false;
        Criteria probeMappingCriteria = new Criteria(Column.getColumn(probeMappingTableName, columnName), (Object)valueList.toArray(), 8);
        probeMappingCriteria = probeMappingCriteria.and(new Criteria(Column.getColumn(probeMappingTableName, "PROBE_ID"), (Object)probeID, 0));
        query.setCriteria(probeMappingCriteria);
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject probeMappingDO = SyMUtil.getPersistence().get(query);
        final DataObject insertDO = (DataObject)new WritableDataObject();
        for (final Object pkValue : valueList) {
            final Criteria pkCrit = new Criteria(Column.getColumn(probeMappingTableName, columnName), pkValue, 0);
            final Row existingRow = (probeMappingDO != null) ? (probeMappingDO.isEmpty() ? null : probeMappingDO.getRow(probeMappingTableName, pkCrit)) : null;
            if (existingRow == null) {
                isValidUpdate = true;
                final Row newRow = new Row(probeMappingTableName);
                newRow.set("PROBE_ID", (Object)probeID);
                newRow.set(columnName, pkValue);
                insertDO.addRow(newRow);
            }
        }
        if (isValidUpdate) {
            SyMUtil.getPersistence().add(insertDO);
        }
        SyMLogger.info(this.logger, "DefaultSummarySyncImpl", sourceMethod, "Mapping added for tableName: {0}  and values: {1}, for probeID : {2}", new Object[] { probeMappingTableName, valueList, probeID });
    }
    
    @Override
    public abstract void preProcessDeleteData(final Long p0, final long p1, final String p2, final HashMap<String, ArrayList> p3) throws SyncException;
    
    @Override
    public abstract void postProcessDeleteData(final Long p0, final long p1, final String p2, final HashMap<String, ArrayList> p3) throws SyncException;
    
    static {
        dbName = DBUtil.getActiveDBName();
    }
}
