package io.opencensus.trace.propagation;

public abstract class PropagationComponent
{
    private static final PropagationComponent NOOP_PROPAGATION_COMPONENT;
    
    public abstract BinaryFormat getBinaryFormat();
    
    public abstract TextFormat getB3Format();
    
    public abstract TextFormat getTraceContextFormat();
    
    public static PropagationComponent getNoopPropagationComponent() {
        return PropagationComponent.NOOP_PROPAGATION_COMPONENT;
    }
    
    static {
        NOOP_PROPAGATION_COMPONENT = new NoopPropagationComponent();
    }
    
    private static final class NoopPropagationComponent extends PropagationComponent
    {
        @Override
        public BinaryFormat getBinaryFormat() {
            return BinaryFormat.getNoopBinaryFormat();
        }
        
        @Override
        public TextFormat getB3Format() {
            return TextFormat.getNoopTextFormat();
        }
        
        @Override
        public TextFormat getTraceContextFormat() {
            return TextFormat.getNoopTextFormat();
        }
    }
}
