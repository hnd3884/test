package com.me.devicemanagement.framework.server.util;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import java.util.Set;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class QueryWrapperDBUtil
{
    private static QueryWrapperDBUtil queryWrapperDBUtil;
    private Logger logger;
    private int maxInCriteriaValues;
    
    private QueryWrapperDBUtil() {
        this.logger = Logger.getLogger("QueryWrapperUtil");
        this.maxInCriteriaValues = 1000;
        this.setMaxInCriteriaValuesCount();
    }
    
    public static QueryWrapperDBUtil getInstance() {
        if (QueryWrapperDBUtil.queryWrapperDBUtil == null) {
            QueryWrapperDBUtil.queryWrapperDBUtil = new QueryWrapperDBUtil();
        }
        return QueryWrapperDBUtil.queryWrapperDBUtil;
    }
    
    public List<Criteria> getInCriteriaList(final Column column, final Set<Object> values) {
        return this.getInCriteriaList(column, values, this.getChunkMaxThreshold(), true);
    }
    
    public List<Criteria> getInCriteriaList(final Column column, final Set<Object> values, final boolean caseSensitive) {
        return this.getInCriteriaList(column, values, this.getChunkMaxThreshold(), caseSensitive);
    }
    
    public List<Criteria> getInCriteriaList(final Column column, final Set<Object> inCriteriaValues, final int chunkMaxThreshold, final boolean caseSensitive) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ifnull          245
        //     4: aload_2         /* inCriteriaValues */
        //     5: ifnull          245
        //     8: aload_2         /* inCriteriaValues */
        //     9: invokeinterface java/util/Set.size:()I
        //    14: ifle            245
        //    17: new             Ljava/util/ArrayList;
        //    20: dup            
        //    21: invokespecial   java/util/ArrayList.<init>:()V
        //    24: astore          criteriaList
        //    26: new             Ljava/util/ArrayList;
        //    29: dup            
        //    30: invokespecial   java/util/ArrayList.<init>:()V
        //    33: astore          chunkList
        //    35: iload_3         /* chunkMaxThreshold */
        //    36: bipush          10
        //    38: if_icmpge       59
        //    41: aload_0         /* this */
        //    42: getfield        com/me/devicemanagement/framework/server/util/QueryWrapperDBUtil.logger:Ljava/util/logging/Logger;
        //    45: getstatic       java/util/logging/Level.INFO:Ljava/util/logging/Level;
        //    48: ldc             "chunkMaxThreshold value is below 10, Setting minimum chunkMaxThreshold value to 10"
        //    50: invokevirtual   java/util/logging/Logger.log:(Ljava/util/logging/Level;Ljava/lang/String;)V
        //    53: bipush          10
        //    55: istore_3        /* chunkMaxThreshold */
        //    56: goto            82
        //    59: iload_3         /* chunkMaxThreshold */
        //    60: sipush          10000
        //    63: if_icmple       82
        //    66: aload_0         /* this */
        //    67: getfield        com/me/devicemanagement/framework/server/util/QueryWrapperDBUtil.logger:Ljava/util/logging/Logger;
        //    70: getstatic       java/util/logging/Level.INFO:Ljava/util/logging/Level;
        //    73: ldc             "chunkMaxThreshold value is above 10000, Setting maximum chunkMaxThreshold value to 10000"
        //    75: invokevirtual   java/util/logging/Logger.log:(Ljava/util/logging/Level;Ljava/lang/String;)V
        //    78: sipush          10000
        //    81: istore_3        /* chunkMaxThreshold */
        //    82: new             Ljava/util/ArrayList;
        //    85: dup            
        //    86: invokespecial   java/util/ArrayList.<init>:()V
        //    89: astore          temp
        //    91: aload_2         /* inCriteriaValues */
        //    92: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //    97: astore          8
        //    99: aload           8
        //   101: invokeinterface java/util/Iterator.hasNext:()Z
        //   106: ifeq            161
        //   109: aload           8
        //   111: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   116: astore          value
        //   118: aload           temp
        //   120: invokeinterface java/util/List.size:()I
        //   125: iload_3         /* chunkMaxThreshold */
        //   126: if_icmpne       148
        //   129: aload           chunkList
        //   131: aload           temp
        //   133: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   138: pop            
        //   139: new             Ljava/util/ArrayList;
        //   142: dup            
        //   143: invokespecial   java/util/ArrayList.<init>:()V
        //   146: astore          temp
        //   148: aload           temp
        //   150: aload           value
        //   152: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   157: pop            
        //   158: goto            99
        //   161: aload           temp
        //   163: invokeinterface java/util/List.size:()I
        //   168: ifeq            181
        //   171: aload           chunkList
        //   173: aload           temp
        //   175: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   180: pop            
        //   181: aload           chunkList
        //   183: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
        //   188: astore          8
        //   190: aload           8
        //   192: invokeinterface java/util/Iterator.hasNext:()Z
        //   197: ifeq            242
        //   200: aload           8
        //   202: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   207: checkcast       Ljava/util/List;
        //   210: astore          object
        //   212: aload           criteriaList
        //   214: new             Lcom/adventnet/ds/query/Criteria;
        //   217: dup            
        //   218: aload_1         /* column */
        //   219: aload           object
        //   221: invokeinterface java/util/List.toArray:()[Ljava/lang/Object;
        //   226: bipush          8
        //   228: iload           caseSensitive
        //   230: invokespecial   com/adventnet/ds/query/Criteria.<init>:(Lcom/adventnet/ds/query/Column;Ljava/lang/Object;IZ)V
        //   233: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   238: pop            
        //   239: goto            190
        //   242: aload           criteriaList
        //   244: areturn        
        //   245: aconst_null    
        //   246: areturn        
        //    Signature:
        //  (Lcom/adventnet/ds/query/Column;Ljava/util/Set<Ljava/lang/Object;>;IZ)Ljava/util/List<Lcom/adventnet/ds/query/Criteria;>;
        //    StackMapTable: 00 09 FD 00 3B 07 00 66 07 00 66 16 FD 00 10 07 00 66 07 00 67 FC 00 30 07 00 68 F9 00 0C 13 FC 00 08 07 00 67 FA 00 33 F8 00 02
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
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:314)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2611)
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
    
    public DataObject getDataObject(final SelectQuery selectQuery, final List<Criteria> criteriaList) throws DataAccessException {
        DataObject combinedDataObject = null;
        for (final Criteria criteria : criteriaList) {
            selectQuery.setCriteria(criteria);
            final DataObject dao = SyMUtil.getPersistence().get(selectQuery);
            if (combinedDataObject == null) {
                combinedDataObject = dao;
            }
            else {
                combinedDataObject.merge(dao);
            }
        }
        return combinedDataObject;
    }
    
    public DataObject getDataObject(final List<SelectQuery> selectQueryList) throws DataAccessException {
        DataObject combinedDataObject = null;
        for (final SelectQuery selectQuery : selectQueryList) {
            final DataObject dao = SyMUtil.getPersistence().get(selectQuery);
            if (combinedDataObject == null) {
                combinedDataObject = dao;
            }
            else {
                combinedDataObject.merge(dao);
            }
        }
        return combinedDataObject;
    }
    
    public DataObject getDataObject(final String tableName, final List<Criteria> criteriaList) throws DataAccessException {
        DataObject combinedDataObject = null;
        for (final Criteria criteria : criteriaList) {
            final DataObject dao = SyMUtil.getPersistence().get(tableName, criteria);
            if (combinedDataObject == null) {
                combinedDataObject = dao;
            }
            else {
                combinedDataObject.merge(dao);
            }
        }
        return combinedDataObject;
    }
    
    private int getChunkMaxThreshold() {
        return this.maxInCriteriaValues;
    }
    
    private void setMaxInCriteriaValuesCount() {
        if (!CustomerInfoUtil.isSAS()) {
            if (DBUtil.getActiveDBName().equalsIgnoreCase("mssql")) {
                try {
                    final int maxInCriteriaValuesCountFromJson = (int)FrameworkConfigurations.getSpecificPropertyIfExists("query_wrapper_db_util", "max_in_criteria_values_for_mssql", (Object)1000);
                    if (this.maxInCriteriaValues != maxInCriteriaValuesCountFromJson) {
                        this.logger.log(Level.INFO, "maxInCriteriaValuesForMsSql value changed from " + this.maxInCriteriaValues + " to " + maxInCriteriaValuesCountFromJson);
                        this.maxInCriteriaValues = maxInCriteriaValuesCountFromJson;
                    }
                }
                catch (final JSONException jsonException) {
                    if (this.maxInCriteriaValues != 1000) {
                        this.logger.log(Level.INFO, "maxInCriteriaValuesForMsSql value changed from " + this.maxInCriteriaValues + " to 1000", (Throwable)jsonException);
                        this.maxInCriteriaValues = 1000;
                    }
                }
            }
            else if (DBUtil.getActiveDBName().equalsIgnoreCase("postgres")) {
                try {
                    final int maxInCriteriaValuesCountFromJson = (int)FrameworkConfigurations.getSpecificPropertyIfExists("query_wrapper_db_util", "max_in_criteria_values_for_pgsql", (Object)1000);
                    if (this.maxInCriteriaValues != maxInCriteriaValuesCountFromJson) {
                        this.logger.log(Level.INFO, "maxInCriteriaValuesForMsSql value changed from " + this.maxInCriteriaValues + " to " + maxInCriteriaValuesCountFromJson);
                        this.maxInCriteriaValues = maxInCriteriaValuesCountFromJson;
                    }
                }
                catch (final JSONException jsonException) {
                    if (this.maxInCriteriaValues != 1000) {
                        this.logger.log(Level.INFO, "maxInCriteriaValuesForMsSql value changed from " + this.maxInCriteriaValues + " to 1000", (Throwable)jsonException);
                        this.maxInCriteriaValues = 1000;
                    }
                }
            }
        }
        else {
            try {
                final int maxInCriteriaValuesCountFromJson = (int)FrameworkConfigurations.getSpecificPropertyIfExists("query_wrapper_db_util", "max_in_criteria_values_for_cloud", (Object)1000);
                if (this.maxInCriteriaValues != maxInCriteriaValuesCountFromJson) {
                    this.logger.log(Level.INFO, "maxInCriteriaValuesForMsSql value changed from " + this.maxInCriteriaValues + " to " + maxInCriteriaValuesCountFromJson);
                    this.maxInCriteriaValues = maxInCriteriaValuesCountFromJson;
                }
            }
            catch (final JSONException jsonException) {
                if (this.maxInCriteriaValues != 1000) {
                    this.logger.log(Level.INFO, "maxInCriteriaValuesForMsSql value changed from " + this.maxInCriteriaValues + " to 1000", (Throwable)jsonException);
                    this.maxInCriteriaValues = 1000;
                }
            }
        }
    }
    
    static {
        QueryWrapperDBUtil.queryWrapperDBUtil = null;
    }
}
