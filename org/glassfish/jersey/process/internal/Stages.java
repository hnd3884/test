package org.glassfish.jersey.process.internal;

import java.util.LinkedList;
import java.util.Deque;
import org.glassfish.jersey.internal.util.collection.Ref;
import java.util.function.Function;
import org.glassfish.jersey.process.Inflector;

public final class Stages
{
    private static final ChainableStage IDENTITY;
    
    private Stages() {
    }
    
    public static <DATA> ChainableStage<DATA> identity() {
        return Stages.IDENTITY;
    }
    
    public static <DATA, RESULT> Stage<DATA> asStage(final Inflector<DATA, RESULT> inflector) {
        return new InflectingStage<DATA, Object>(inflector);
    }
    
    public static <DATA, RESULT, T extends Inflector<DATA, RESULT>> T extractInflector(final Object stage) {
        if (stage instanceof Inflecting) {
            return (T)((Inflecting)stage).inflector();
        }
        return null;
    }
    
    public static <DATA> Stage.Builder<DATA> chain(final Function<DATA, DATA> transformation) {
        return new StageChainBuilder<DATA>((Function)transformation);
    }
    
    public static <DATA> Stage.Builder<DATA> chain(final ChainableStage<DATA> rootStage) {
        return new StageChainBuilder<DATA>((ChainableStage)rootStage);
    }
    
    public static <DATA> DATA process(final DATA data, final Stage<DATA> rootStage) {
        Stage.Continuation<DATA> continuation;
        Stage<DATA> currentStage;
        for (continuation = Stage.Continuation.of(data, rootStage); (currentStage = continuation.next()) != null; continuation = currentStage.apply(continuation.result())) {}
        return continuation.result();
    }
    
    public static <DATA, RESULT, T extends Inflector<DATA, RESULT>> DATA process(final DATA data, final Stage<DATA> rootStage, final Ref<T> inflectorRef) {
        Stage<DATA> lastStage;
        Stage.Continuation<DATA> continuation;
        for (lastStage = rootStage, continuation = Stage.Continuation.of(data, lastStage); continuation.next() != null; continuation = lastStage.apply(continuation.result())) {
            lastStage = continuation.next();
        }
        inflectorRef.set(extractInflector(lastStage));
        return continuation.result();
    }
    
    static {
        IDENTITY = new AbstractChainableStage() {
            @Override
            public Stage.Continuation apply(final Object o) {
                return Stage.Continuation.of(o, this.getDefaultNext());
            }
        };
    }
    
    private static class InflectingStage<DATA, RESULT> implements Stage<DATA>, Inflecting<DATA, RESULT>
    {
        private final Inflector<DATA, RESULT> inflector;
        
        public InflectingStage(final Inflector<DATA, RESULT> inflector) {
            this.inflector = inflector;
        }
        
        @Override
        public Inflector<DATA, RESULT> inflector() {
            return this.inflector;
        }
        
        @Override
        public Continuation<DATA> apply(final DATA request) {
            return Continuation.of(request);
        }
    }
    
    private static class StageChainBuilder<DATA> implements Stage.Builder<DATA>
    {
        private final Deque<Function<DATA, DATA>> transformations;
        private Stage<DATA> rootStage;
        private ChainableStage<DATA> lastStage;
        
        private StageChainBuilder(final Function<DATA, DATA> transformation) {
            (this.transformations = new LinkedList<Function<DATA, DATA>>()).push(transformation);
        }
        
        private StageChainBuilder(final ChainableStage<DATA> rootStage) {
            this.transformations = new LinkedList<Function<DATA, DATA>>();
            this.rootStage = rootStage;
            this.lastStage = rootStage;
        }
        
        @Override
        public Stage.Builder<DATA> to(final Function<DATA, DATA> transformation) {
            this.transformations.push(transformation);
            return this;
        }
        
        @Override
        public Stage.Builder<DATA> to(final ChainableStage<DATA> stage) {
            this.addTailStage(stage);
            this.lastStage = stage;
            return this;
        }
        
        private void addTailStage(final Stage<DATA> lastStage) {
            Stage<DATA> tail = lastStage;
            if (!this.transformations.isEmpty()) {
                tail = this.convertTransformations(lastStage);
            }
            if (this.rootStage != null) {
                this.lastStage.setDefaultNext(tail);
            }
            else {
                this.rootStage = tail;
            }
        }
        
        @Override
        public Stage<DATA> build(final Stage<DATA> stage) {
            this.addTailStage(stage);
            return this.rootStage;
        }
        
        @Override
        public Stage<DATA> build() {
            return this.build(null);
        }
        
        private Stage<DATA> convertTransformations(final Stage<DATA> successor) {
            Stage<DATA> stage;
            if (successor == null) {
                stage = new LinkedStage<DATA>(this.transformations.poll());
            }
            else {
                stage = new LinkedStage<DATA>(this.transformations.poll(), successor);
            }
            Function<DATA, DATA> t;
            while ((t = this.transformations.poll()) != null) {
                stage = new LinkedStage<DATA>(t, stage);
            }
            return stage;
        }
    }
    
    public static class LinkedStage<DATA> implements Stage<DATA>
    {
        private final Stage<DATA> nextStage;
        private final Function<DATA, DATA> transformation;
        
        public LinkedStage(final Function<DATA, DATA> transformation, final Stage<DATA> nextStage) {
            this.nextStage = nextStage;
            this.transformation = transformation;
        }
        
        public LinkedStage(final Function<DATA, DATA> transformation) {
            this(transformation, null);
        }
        
        @Override
        public Continuation<DATA> apply(final DATA data) {
            return Continuation.of(this.transformation.apply(data), this.nextStage);
        }
    }
}
