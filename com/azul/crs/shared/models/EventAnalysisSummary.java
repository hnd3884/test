package com.azul.crs.shared.models;

import java.util.Collections;
import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventAnalysisSummary extends Payload
{
    private final Map<String, EventAnalysisStats> entries;
    
    public EventAnalysisSummary() {
        this.entries = new HashMap<String, EventAnalysisStats>();
    }
    
    @JsonCreator
    private EventAnalysisSummary(final Map<String, Map> entries) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   com/azul/crs/shared/models/Payload.<init>:()V
        //     4: aload_0         /* this */
        //     5: new             Ljava/util/HashMap;
        //     8: dup            
        //     9: invokespecial   java/util/HashMap.<init>:()V
        //    12: putfield        com/azul/crs/shared/models/EventAnalysisSummary.entries:Ljava/util/Map;
        //    15: aload_1         /* entries */
        //    16: invokeinterface java/util/Map.entrySet:()Ljava/util/Set;
        //    21: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //    26: astore_2       
        //    27: aload_2        
        //    28: invokeinterface java/util/Iterator.hasNext:()Z
        //    33: ifeq            82
        //    36: aload_2        
        //    37: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    42: checkcast       Ljava/util/Map$Entry;
        //    45: astore_3       
        //    46: aload_0         /* this */
        //    47: getfield        com/azul/crs/shared/models/EventAnalysisSummary.entries:Ljava/util/Map;
        //    50: aload_3        
        //    51: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //    56: checkcast       Ljava/lang/String;
        //    59: invokestatic    com/azul/crs/shared/models/EventAnalysisSummary.objectMapper:()Lcom/azul/crs/com/fasterxml/jackson/databind/ObjectMapper;
        //    62: aload_3        
        //    63: invokeinterface java/util/Map$Entry.getValue:()Ljava/lang/Object;
        //    68: ldc             Lcom/azul/crs/shared/models/EventAnalysisStats;.class
        //    70: invokevirtual   com/azul/crs/com/fasterxml/jackson/databind/ObjectMapper.convertValue:(Ljava/lang/Object;Ljava/lang/Class;)Ljava/lang/Object;
        //    73: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //    78: pop            
        //    79: goto            27
        //    82: return         
        //    Signature:
        //  (Ljava/util/Map<Ljava/lang/String;Ljava/util/Map;>;)V
        //    StackMapTable: 00 02 FF 00 1B 00 03 07 00 02 07 00 0D 07 00 35 00 00 FA 00 36
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
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
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
    public String toJson() throws JsonProcessingException {
        return Payload.objectMapper().writeValueAsString(this.entries);
    }
    
    public Map<String, EventAnalysisStats> getEntries() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends EventAnalysisStats>)this.entries);
    }
    
    public void mergeEntry(final VMInstance.Annotation annotation, final EventAnalysisStats stats) {
        final String key = annotation.getName();
        final EventAnalysisStats oldStats = this.entries.get(key);
        if (oldStats == null) {
            this.entries.put(key, stats);
        }
        else {
            oldStats.merge(stats);
        }
    }
    
    public void putEntry(final VMInstance.Annotation annotation, final EventAnalysisStats stats) {
        this.entries.put(annotation.getName(), stats);
    }
    
    public void putEntry(final VMInstance.Annotation annotation, final EventAnalysisStats stats, final boolean merge) {
        if (merge) {
            this.mergeEntry(annotation, stats);
        }
        else {
            this.putEntry(annotation, stats);
        }
    }
    
    public void putEntry(final VMInstance.Annotation annotation, final int totalEventCount, final int unresolvedEventCount, final long analysisTime) {
        this.putEntry(annotation, new EventAnalysisStats().totalEventCount(totalEventCount).unresolvedEventCount(unresolvedEventCount).analysisTime(analysisTime));
    }
    
    public EventAnalysisStats getEntry(final VMInstance.Annotation annotation) {
        return this.entries.get(annotation.getName());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final EventAnalysisSummary that = (EventAnalysisSummary)o;
        return this.entries.equals(that.entries);
    }
}
