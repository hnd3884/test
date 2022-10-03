package com.me.ems.onpremise.summaryserver.summary.sync.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.adventnet.iam.security.UploadedFileItem;
import java.net.URLConnection;
import java.util.Properties;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Map;
import java.util.HashMap;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import java.io.IOException;
import java.io.OutputStream;
import com.me.ems.onpremise.summaryserver.common.HttpsHandlerUtil;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import java.net.HttpURLConnection;
import java.net.URL;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import java.util.Iterator;
import java.io.Reader;
import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import com.adventnet.ds.query.BulkLoad;
import java.sql.SQLException;
import java.util.logging.Level;
import com.adventnet.db.api.RelationalAPI;
import com.me.ems.summaryserver.common.sync.SyncException;
import java.util.List;
import com.me.ems.summaryserver.common.sync.utils.SyncMetaDataDAOUtil;
import com.me.ems.summaryserver.summary.sync.factory.SyncAPI;
import java.util.logging.Logger;
import com.me.ems.summaryserver.summary.sync.factory.SummaryServerSyncAPI;

public class SummaryServerSyncImpl implements SummaryServerSyncAPI
{
    private static Logger logger;
    
    public void processCSVData(final SyncAPI dataSync, final Long probeID, final long moduleID, final String filePath, final String tableName) throws SyncException {
        final SyncMetaDataDAOUtil syncMetaDataDAOUtil = new SyncMetaDataDAOUtil();
        try {
            dataSync.parseCSVFile(filePath);
            final String[] headers = dataSync.getCsvHeaders();
            final List<String[]> probeData = dataSync.getCsvData();
            dataSync.validateData(probeID, moduleID, tableName, headers, (List)probeData);
            dataSync.preProcessData(probeID, moduleID, tableName, headers, (List)probeData);
            final boolean isConflictData = syncMetaDataDAOUtil.isConflictTable(tableName);
            if (isConflictData) {
                dataSync.resolveConflicts(probeID, moduleID, tableName, headers, (List)probeData);
                dataSync.populateDataFromCSVFile(probeID, moduleID, tableName, headers, (List)probeData);
                dataSync.postProcessConflictData(probeID, moduleID, tableName, headers, (List)probeData);
            }
            else {
                dataSync.populateDataFromCSVFile(probeID, moduleID, tableName, headers, (List)probeData);
            }
            dataSync.postProcessData(probeID, moduleID, tableName, headers, (List)probeData);
        }
        catch (final SyncException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new SyncException(950201, probeID, moduleID, tableName, (Throwable)e2);
        }
    }
    
    public void processJSONDeletionData(final SyncAPI dataSync, final Long probeID, final long moduleID, final String qDataStr) throws SyncException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: invokespecial   com/me/ems/summaryserver/common/sync/utils/SyncMetaDataDAOUtil.<init>:()V
        //     7: astore          syncMetaDataDAOUtil
        //     9: aload_1         /* dataSync */
        //    10: aload           qDataStr
        //    12: invokeinterface com/me/ems/summaryserver/summary/sync/factory/SyncAPI.getDeletionJSON:(Ljava/lang/String;)Lorg/json/JSONObject;
        //    17: astore          deletionJSON
        //    19: aload_1         /* dataSync */
        //    20: aload           deletionJSON
        //    22: invokeinterface com/me/ems/summaryserver/summary/sync/factory/SyncAPI.validateJSONData:(Lorg/json/JSONObject;)V
        //    27: aload_1         /* dataSync */
        //    28: aload           deletionJSON
        //    30: invokeinterface com/me/ems/summaryserver/summary/sync/factory/SyncAPI.getDeleteDataFromJSON:(Lorg/json/JSONObject;)Ljava/util/HashMap;
        //    35: astore          probeDeleteData
        //    37: aload           probeDeleteData
        //    39: invokevirtual   java/util/HashMap.entrySet:()Ljava/util/Set;
        //    42: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //    47: astore          9
        //    49: aload           9
        //    51: invokeinterface java/util/Iterator.hasNext:()Z
        //    56: ifeq            220
        //    59: aload           9
        //    61: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    66: checkcast       Ljava/util/Map$Entry;
        //    69: astore          entry
        //    71: new             Ljava/util/ArrayList;
        //    74: dup            
        //    75: invokespecial   java/util/ArrayList.<init>:()V
        //    78: astore          deleteQueryList
        //    80: aload           entry
        //    82: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //    87: checkcast       Ljava/lang/String;
        //    90: astore          tableName
        //    92: aload           entry
        //    94: invokeinterface java/util/Map$Entry.getValue:()Ljava/lang/Object;
        //    99: checkcast       Ljava/util/HashMap;
        //   102: astore          deleteData
        //   104: aload_1         /* dataSync */
        //   105: aload_2         /* probeID */
        //   106: lload_3         /* moduleID */
        //   107: aload           tableName
        //   109: aload           deleteData
        //   111: invokeinterface com/me/ems/summaryserver/summary/sync/factory/SyncAPI.preProcessDeleteData:(Ljava/lang/Long;JLjava/lang/String;Ljava/util/HashMap;)V
        //   116: aload           syncMetaDataDAOUtil
        //   118: aload           tableName
        //   120: invokevirtual   com/me/ems/summaryserver/common/sync/utils/SyncMetaDataDAOUtil.isConflictTable:(Ljava/lang/String;)Z
        //   123: istore          isConflictData
        //   125: iload           isConflictData
        //   127: ifeq            179
        //   130: aload_1         /* dataSync */
        //   131: aload_2         /* probeID */
        //   132: lload_3         /* moduleID */
        //   133: aload           tableName
        //   135: aload           deleteData
        //   137: invokeinterface com/me/ems/summaryserver/summary/sync/factory/SyncAPI.resolveDeletionConflicts:(Ljava/lang/Long;JLjava/lang/String;Ljava/util/HashMap;)Ljava/util/HashMap;
        //   142: astore          deleteSummaryData
        //   144: aload           deleteQueryList
        //   146: aload_1         /* dataSync */
        //   147: aload_2         /* probeID */
        //   148: lload_3         /* moduleID */
        //   149: aload           tableName
        //   151: aload           deleteSummaryData
        //   153: invokeinterface com/me/ems/summaryserver/summary/sync/factory/SyncAPI.getDeleteQuery:(Ljava/lang/Long;JLjava/lang/String;Ljava/util/HashMap;)Ljava/util/List;
        //   158: invokevirtual   java/util/ArrayList.addAll:(Ljava/util/Collection;)Z
        //   161: pop            
        //   162: aload_1         /* dataSync */
        //   163: aload_2         /* probeID */
        //   164: lload_3         /* moduleID */
        //   165: aload           tableName
        //   167: aload           deleteData
        //   169: aload           deleteSummaryData
        //   171: invokeinterface com/me/ems/summaryserver/summary/sync/factory/SyncAPI.postProcessConflictDeletionData:(Ljava/lang/Long;JLjava/lang/String;Ljava/util/HashMap;Ljava/util/HashMap;)V
        //   176: goto            197
        //   179: aload           deleteQueryList
        //   181: aload_1         /* dataSync */
        //   182: aload_2         /* probeID */
        //   183: lload_3         /* moduleID */
        //   184: aload           tableName
        //   186: aload           deleteData
        //   188: invokeinterface com/me/ems/summaryserver/summary/sync/factory/SyncAPI.getDeleteQuery:(Ljava/lang/Long;JLjava/lang/String;Ljava/util/HashMap;)Ljava/util/List;
        //   193: invokevirtual   java/util/ArrayList.addAll:(Ljava/util/Collection;)Z
        //   196: pop            
        //   197: aload_1         /* dataSync */
        //   198: aload           deleteQueryList
        //   200: invokeinterface com/me/ems/summaryserver/summary/sync/factory/SyncAPI.performDeletion:(Ljava/util/ArrayList;)V
        //   205: aload_1         /* dataSync */
        //   206: aload_2         /* probeID */
        //   207: lload_3         /* moduleID */
        //   208: aload           tableName
        //   210: aload           deleteData
        //   212: invokeinterface com/me/ems/summaryserver/summary/sync/factory/SyncAPI.postProcessDeleteData:(Ljava/lang/Long;JLjava/lang/String;Ljava/util/HashMap;)V
        //   217: goto            49
        //   220: goto            244
        //   223: astore          e
        //   225: aload           e
        //   227: athrow         
        //   228: astore          e
        //   230: new             Lcom/me/ems/summaryserver/common/sync/SyncException;
        //   233: dup            
        //   234: ldc             950301
        //   236: aload_2         /* probeID */
        //   237: lload_3         /* moduleID */
        //   238: aload           e
        //   240: invokespecial   com/me/ems/summaryserver/common/sync/SyncException.<init>:(ILjava/lang/Long;JLjava/lang/Throwable;)V
        //   243: athrow         
        //   244: return         
        //    Exceptions:
        //  throws com.me.ems.summaryserver.common.sync.SyncException
        //    StackMapTable: 00 07 FF 00 31 00 09 07 01 02 07 01 03 07 01 04 04 07 01 05 07 01 06 07 01 1D 07 01 1E 07 01 1F 00 00 FF 00 81 00 0E 07 01 02 07 01 03 07 01 04 04 07 01 05 07 01 06 07 01 1D 07 01 1E 07 01 1F 07 01 20 07 01 21 07 01 05 07 01 1E 01 00 00 11 FF 00 16 00 06 07 01 02 07 01 03 07 01 04 04 07 01 05 07 01 06 00 00 42 07 01 08 44 07 01 09 0F
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                
        //  -----  -----  -----  -----  ----------------------------------------------------
        //  9      220    223    228    Lcom/me/ems/summaryserver/common/sync/SyncException;
        //  9      220    228    244    Ljava/lang/Exception;
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
    
    public void truncateTable(final String table) throws SQLException {
        try {
            RelationalAPI.getInstance().truncateTable(table);
            SummaryServerSyncImpl.logger.log(Level.INFO, "Truncated table successfully: " + table);
        }
        catch (final Exception e) {
            SummaryServerSyncImpl.logger.log(Level.WARNING, "Exception in truncateTable ", e);
            throw e;
        }
    }
    
    public void bulkLoadStagingTable(final String table, final String filename) throws SyncException {
        SummaryServerSyncImpl.logger.log(Level.INFO, "Entering bulk load for table: " + table);
        BulkLoad load = null;
        CSVReader reader = null;
        boolean isHeader = true;
        try {
            load = new BulkLoad(table);
            load.setBufferSize(2);
            load.setIdleTimeOut(60);
            load.setAutoFillUVG(true);
            load.createTempTable(Boolean.valueOf(false));
            load.setFillDefaultValues(true);
            reader = new CSVReader((Reader)new FileReader(filename));
            String[] headers = null;
            String[] str;
            while ((str = reader.readNext()) != null) {
                if (!isHeader) {
                    for (int index = 0; index < str.length; ++index) {
                        if (str[index].equalsIgnoreCase("*null*")) {
                            str[index] = null;
                        }
                        load.setString(headers[index], str[index]);
                    }
                    load.flush();
                }
                else {
                    headers = new String[str.length];
                    for (int index = 0; index < str.length; ++index) {
                        headers[index] = str[index];
                    }
                    isHeader = false;
                }
            }
            SummaryServerSyncImpl.logger.log(Level.INFO, "Exiting bulk load successfully for table: " + table);
        }
        catch (final Exception e) {
            SummaryServerSyncImpl.logger.log(Level.SEVERE, "Error in bulkLoadStagingTable", e);
            throw new SyncException(950601, table, (Throwable)e);
        }
        finally {
            try {
                if (load != null) {
                    load.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final Exception e2) {
                SummaryServerSyncImpl.logger.log(Level.SEVERE, "Error in bulk load finally ... ", e2);
                throw new SyncException(950601, table, (Throwable)e2);
            }
        }
    }
    
    public void bulkLoadStagingTable(final String tempTable, final String[] headers, final List<String[]> probeData) throws SyncException {
        SummaryServerSyncImpl.logger.log(Level.INFO, "Entering bulk load for table: " + tempTable);
        BulkLoad load = null;
        try {
            load = new BulkLoad(tempTable);
            load.setBufferSize(2);
            load.setIdleTimeOut(60);
            load.setAutoFillUVG(true);
            load.createTempTable(Boolean.valueOf(false));
            load.setFillDefaultValues(true);
            for (final String[] rowData : probeData) {
                for (int index = 0; index < rowData.length; ++index) {
                    if (rowData[index].equalsIgnoreCase("*null*")) {
                        rowData[index] = null;
                    }
                    load.setString(headers[index], rowData[index]);
                }
                load.flush();
            }
            SummaryServerSyncImpl.logger.log(Level.INFO, "Exiting bulk load successfully for table: " + tempTable);
        }
        catch (final Exception e) {
            SummaryServerSyncImpl.logger.log(Level.SEVERE, "Error in bulkLoadStagingTable", e);
            throw new SyncException(950601, tempTable, (Throwable)e);
        }
        finally {
            try {
                if (load != null) {
                    load.close();
                }
            }
            catch (final Exception e2) {
                SummaryServerSyncImpl.logger.log(Level.SEVERE, "Error in bulk load finally ... ", e2);
                throw new SyncException(950601, tempTable, (Throwable)e2);
            }
        }
    }
    
    public void syncSSTableData(final String tableName, final Criteria criteria, final Long probeId) {
        try {
            final Persistence persistence = SyMUtil.getPersistence();
            final DataObject resultDO = persistence.get(tableName, criteria);
            final Iterator iterator = resultDO.getRows(tableName);
            final JSONArray jsonArray = new JSONArray();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final JSONObject jsonObject = row.getAsJSON();
                jsonArray.put((Object)jsonObject);
            }
            sendSSData(tableName, jsonArray, probeId);
        }
        catch (final Exception e) {
            SummaryServerSyncImpl.logger.log(Level.INFO, " Exception while updating ss data for table-> " + tableName + " in updateSSData", e);
        }
    }
    
    private static void sendSSData(final String tableName, final JSONArray jsonArray, final Long probeId) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", (Object)jsonArray);
            jsonObject.put("tableName", (Object)tableName);
            String probeServerUrl = ProbeDetailsUtil.getProbeServerUrl(probeId);
            probeServerUrl += "servlets/SSDataSyncServlet";
            final URL urlObj = new URL(probeServerUrl);
            HttpURLConnection conn = (HttpURLConnection)urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            final String authorization = ProbeAuthUtil.getInstance().getProbeAuthKey(probeId);
            conn.setRequestProperty("ProbeAuthorization", authorization);
            conn.setRequestProperty("hsKey", ProbeAuthUtil.getInstance().getProbeHandShakekey());
            conn.setRequestProperty("summaryServerRequest", "true");
            conn.setRequestProperty("Content-Type", "application/json");
            if (probeServerUrl.contains("https")) {
                conn = HttpsHandlerUtil.skipCertificateCheck(conn);
            }
            SummaryServerSyncImpl.logger.info(jsonObject.toString());
            try (final OutputStream os = conn.getOutputStream()) {
                final byte[] input = jsonObject.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            final int respCode = conn.getResponseCode();
            if (respCode >= 200 && respCode <= 210) {
                SummaryServerSyncImpl.logger.log(Level.INFO, "Posting ss data sync  SuccessFul and posted to ");
            }
            else {
                SummaryServerSyncImpl.logger.log(Level.SEVERE, "Posting ss data sync  UNSuccessFul and posted to ");
            }
        }
        catch (final Exception e) {
            SummaryServerSyncImpl.logger.log(Level.SEVERE, "Exception while posting ss data", e);
        }
    }
    
    public void writeFile(final String strXml, final String filePath, final boolean isCompressed) throws IOException {
    }
    
    public String getProbeServerBaseURL(final Long probeID) {
        final HashMap probeDetail = ProbeUtil.getInstance().getProbeDetail(probeID);
        String host = null;
        String port = null;
        String protocol = null;
        String baseURL = null;
        if (probeDetail != null) {
            if (probeDetail.get("HOST") != null) {
                host = probeDetail.get("HOST");
            }
            if (probeDetail.get("PORT") != null) {
                port = probeDetail.get("PORT").toString();
            }
            if (probeDetail.get("PROTOCOL") != null) {
                protocol = probeDetail.get("PROTOCOL");
            }
            if (port == null || port.trim().length() == 0) {
                baseURL = protocol + "://" + host;
            }
            else {
                baseURL = protocol + "://" + host + ":" + port + "/";
            }
        }
        return baseURL;
    }
    
    public Map<String, String> getUserDomainDetails(final boolean needDefaultAdmin) {
        final Map<String, String> userProb = new HashMap<String, String>();
        String domainName = null;
        String userName = null;
        if (!needDefaultAdmin) {
            try {
                userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
                domainName = ApiFactoryProvider.getAuthUtilAccessAPI().getDomainName();
                userProb.put("domainName", domainName);
                userProb.put("userName", userName);
                return userProb;
            }
            catch (final Exception ex) {}
        }
        if (domainName == null && userName == null) {
            final Long loginID = DMUserHandler.getDefaultAdministratorRoleUserList().get(0).get("LOGIN_ID");
            final Map dcUser = DMUserHandler.getLoginDetails(loginID);
            domainName = ((dcUser.get("DOMAINNAME") == null) ? "-" : dcUser.get("DOMAINNAME"));
            userName = dcUser.get("NAME");
            userProb.put("domainName", domainName);
            userProb.put("userName", userName);
        }
        return userProb;
    }
    
    public URLConnection createProbeServerConnection(final Long probeID, final Properties apiProps, final boolean doOutput, final boolean doInput) throws Exception {
        HttpURLConnection conn = null;
        URL url = null;
        final String apiURL = apiProps.getProperty("url");
        final String contentType = apiProps.getProperty("content-type");
        final String methodType = apiProps.getProperty("requestMethod");
        final String accept = apiProps.getProperty("accept");
        if (apiURL != null) {
            final String baseURL = this.getProbeServerBaseURL(probeID);
            url = new URL(baseURL + apiURL);
            SummaryServerSyncImpl.logger.log(Level.INFO, "Probe Server URL : " + url);
            if (baseURL != null) {
                conn = HttpsHandlerUtil.getServerUrlConnection(url.toString());
                if (conn.getURL().toString().contains("https")) {
                    conn = HttpsHandlerUtil.skipCertificateCheck(conn);
                }
                final String probeAuthKey = ProbeAuthUtil.getInstance().getProbeAuthKey(probeID);
                conn.setRequestProperty("ProbeAuthorization", probeAuthKey);
                conn.setRequestProperty("hsKey", ProbeAuthUtil.getInstance().getProbeHandShakekey());
                conn.setRequestProperty("summaryServerRequest", "true");
                conn.setRequestProperty("userDomain", this.encryptUserDomain(probeAuthKey));
                conn.setDoInput(doInput);
                conn.setDoOutput(doOutput);
                if (methodType != null) {
                    conn.setRequestMethod(methodType);
                }
                if (contentType != null) {
                    conn.setRequestProperty("content-type", contentType);
                }
                if (accept != null) {
                    conn.setRequestProperty("accept", accept);
                }
            }
        }
        return conn;
    }
    
    private String encryptUserDomain(final String probeAuthKey) {
        final Map<String, String> userProb = this.getUserDomainDetails(false);
        final String domainName = userProb.get("domainName");
        final String userName = userProb.get("userName");
        final String encryptedStr = ApiFactoryProvider.getCryptoAPI().encrypt(userName + "::" + domainName, probeAuthKey, (String)null);
        return encryptedStr;
    }
    
    public String pushMultiPartToProbe(final Long probeID, String apiUrl, final String methodType, final Map<String, Object> headersMap, final Map<String, Object> parametersMap, final Map<String, UploadedFileItem> multiFileObj) {
        String response = null;
        try {
            if (apiUrl != null && methodType != null && headersMap != null) {
                final String baseUrl = ProbeMgmtFactoryProvider.getSummaryServerSyncAPI().getProbeServerBaseURL(probeID);
                apiUrl = (apiUrl.startsWith("/") ? apiUrl.replaceFirst("/", "") : apiUrl);
                final URL postUrl = new URL(baseUrl + apiUrl);
                final CloseableHttpClient httpclient = HttpClients.createDefault();
                final String contentType = headersMap.get("content-type");
                final MultipartEntityBuilder mb = MultipartEntityBuilder.create();
                mb.setBoundary(contentType.substring(contentType.indexOf("=") + 1));
                for (final String paramName : parametersMap.keySet()) {
                    mb.addTextBody(paramName, (String)parametersMap.get(paramName));
                }
                for (final UploadedFileItem file : multiFileObj.values()) {
                    mb.addBinaryBody(file.getFieldName(), file.getUploadedFile(), ContentType.DEFAULT_BINARY, file.getFileName());
                }
                final HttpEntity e = mb.build();
                RequestBuilder requestBuilder = null;
                if (methodType != null && methodType.equalsIgnoreCase("POST")) {
                    requestBuilder = RequestBuilder.post(String.valueOf(postUrl));
                }
                else if (methodType != null && methodType.equalsIgnoreCase("PUT")) {
                    requestBuilder = RequestBuilder.put(String.valueOf(postUrl));
                }
                final String probeAuthKey = ProbeAuthUtil.getInstance().getProbeAuthKey(probeID);
                requestBuilder.setHeader("ProbeAuthorization", probeAuthKey);
                requestBuilder.setHeader("hsKey", ProbeAuthUtil.getInstance().getProbeHandShakekey());
                requestBuilder.setHeader("summaryServerRequest", "true");
                requestBuilder.setHeader("userDomain", this.encryptUserDomain(probeAuthKey));
                final String accept = headersMap.get("accept");
                if (accept != null) {
                    requestBuilder.setHeader("accept", accept);
                }
                requestBuilder.setEntity(e);
                final HttpUriRequest multipartRequest = requestBuilder.build();
                final HttpResponse httpresponse = (HttpResponse)httpclient.execute(multipartRequest);
                response = EntityUtils.toString(httpresponse.getEntity());
                SummaryServerSyncImpl.logger.log(Level.INFO, response);
                SummaryServerSyncImpl.logger.log(Level.INFO, httpresponse.getStatusLine().toString());
            }
        }
        catch (final Exception e2) {
            SummaryServerSyncImpl.logger.log(Level.SEVERE, "Exception in pushMultiPartToProbe: ", e2);
        }
        return response;
    }
    
    static {
        SummaryServerSyncImpl.logger = Logger.getLogger("SummarySyncLogger");
    }
}
