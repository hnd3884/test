package java.util.stream;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;

abstract class AbstractTask<P_IN, P_OUT, R, K extends AbstractTask<P_IN, P_OUT, R, K>> extends CountedCompleter<R>
{
    private static final int LEAF_TARGET;
    protected final PipelineHelper<P_OUT> helper;
    protected Spliterator<P_IN> spliterator;
    protected long targetSize;
    protected K leftChild;
    protected K rightChild;
    private R localResult;
    
    protected AbstractTask(final PipelineHelper<P_OUT> helper, final Spliterator<P_IN> spliterator) {
        super(null);
        this.helper = helper;
        this.spliterator = spliterator;
        this.targetSize = 0L;
    }
    
    protected AbstractTask(final K k, final Spliterator<P_IN> spliterator) {
        super(k);
        this.spliterator = spliterator;
        this.helper = k.helper;
        this.targetSize = k.targetSize;
    }
    
    public static int getLeafTarget() {
        final Thread currentThread = Thread.currentThread();
        if (currentThread instanceof ForkJoinWorkerThread) {
            return ((ForkJoinWorkerThread)currentThread).getPool().getParallelism() << 2;
        }
        return AbstractTask.LEAF_TARGET;
    }
    
    protected abstract K makeChild(final Spliterator<P_IN> p0);
    
    protected abstract R doLeaf();
    
    public static long suggestTargetSize(final long n) {
        final long n2 = n / getLeafTarget();
        return (n2 > 0L) ? n2 : 1L;
    }
    
    protected final long getTargetSize(final long n) {
        final long targetSize;
        return ((targetSize = this.targetSize) != 0L) ? targetSize : (this.targetSize = suggestTargetSize(n));
    }
    
    @Override
    public R getRawResult() {
        return this.localResult;
    }
    
    @Override
    protected void setRawResult(final R r) {
        if (r != null) {
            throw new IllegalStateException();
        }
    }
    
    protected R getLocalResult() {
        return this.localResult;
    }
    
    protected void setLocalResult(final R localResult) {
        this.localResult = localResult;
    }
    
    protected boolean isLeaf() {
        return this.leftChild == null;
    }
    
    protected boolean isRoot() {
        return this.getParent() == null;
    }
    
    protected K getParent() {
        return (K)(AbstractTask)this.getCompleter();
    }
    
    @Override
    public void compute() {
        Spliterator<P_IN> spliterator = this.spliterator;
        long n = spliterator.estimateSize();
        final long targetSize = this.getTargetSize(n);
        int n2 = 0;
        AbstractTask abstractTask = this;
        Spliterator trySplit;
        while (n > targetSize && (trySplit = spliterator.trySplit()) != null) {
            final AbstractTask<P_IN, P_OUT, R, K> abstractTask2 = abstractTask.leftChild = abstractTask.makeChild(trySplit);
            final AbstractTask<P_IN, P_OUT, R, K> abstractTask3 = abstractTask.rightChild = abstractTask.makeChild(spliterator);
            abstractTask.setPendingCount(1);
            AbstractTask<P_IN, P_OUT, R, K> abstractTask4;
            if (n2 != 0) {
                n2 = 0;
                spliterator = trySplit;
                abstractTask = abstractTask2;
                abstractTask4 = abstractTask3;
            }
            else {
                n2 = 1;
                abstractTask = abstractTask3;
                abstractTask4 = abstractTask2;
            }
            abstractTask4.fork();
            n = spliterator.estimateSize();
        }
        abstractTask.setLocalResult(abstractTask.doLeaf());
        abstractTask.tryComplete();
    }
    
    @Override
    public void onCompletion(final CountedCompleter<?> countedCompleter) {
        this.spliterator = null;
        final AbstractTask<P_IN, P_OUT, R, K> abstractTask = null;
        this.rightChild = (K)abstractTask;
        this.leftChild = (K)abstractTask;
    }
    
    protected boolean isLeftmostNode() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: astore_1       
        //     2: aload_1        
        //     3: ifnull          30
        //     6: aload_1        
        //     7: invokevirtual   java/util/stream/AbstractTask.getParent:()Ljava/util/stream/AbstractTask;
        //    10: astore_2       
        //    11: aload_2        
        //    12: ifnull          25
        //    15: aload_2        
        //    16: getfield        java/util/stream/AbstractTask.leftChild:Ljava/util/stream/AbstractTask;
        //    19: aload_1        
        //    20: if_acmpeq       25
        //    23: iconst_0       
        //    24: ireturn        
        //    25: aload_2        
        //    26: astore_1       
        //    27: goto            2
        //    30: iconst_1       
        //    31: ireturn        
        //    StackMapTable: 00 03 FC 00 02 07 00 53 FC 00 16 07 00 53 FA 00 04
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
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:667)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:373)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
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
    
    static {
        LEAF_TARGET = ForkJoinPool.getCommonPoolParallelism() << 2;
    }
}
