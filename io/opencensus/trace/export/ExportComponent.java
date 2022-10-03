package io.opencensus.trace.export;

public abstract class ExportComponent
{
    public static ExportComponent newNoopExportComponent() {
        return new NoopExportComponent();
    }
    
    public abstract SpanExporter getSpanExporter();
    
    public abstract RunningSpanStore getRunningSpanStore();
    
    public abstract SampledSpanStore getSampledSpanStore();
    
    public void shutdown() {
    }
    
    private static final class NoopExportComponent extends ExportComponent
    {
        private final SampledSpanStore noopSampledSpanStore;
        
        private NoopExportComponent() {
            this.noopSampledSpanStore = SampledSpanStore.newNoopSampledSpanStore();
        }
        
        @Override
        public SpanExporter getSpanExporter() {
            return SpanExporter.getNoopSpanExporter();
        }
        
        @Override
        public RunningSpanStore getRunningSpanStore() {
            return RunningSpanStore.getNoopRunningSpanStore();
        }
        
        @Override
        public SampledSpanStore getSampledSpanStore() {
            return this.noopSampledSpanStore;
        }
    }
}
