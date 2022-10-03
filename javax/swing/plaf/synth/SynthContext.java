package javax.swing.plaf.synth;

import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JComponent;
import java.util.Queue;

public class SynthContext
{
    private static final Queue<SynthContext> queue;
    private JComponent component;
    private Region region;
    private SynthStyle style;
    private int state;
    
    static SynthContext getContext(final JComponent component, final SynthStyle synthStyle, final int n) {
        return getContext(component, SynthLookAndFeel.getRegion(component), synthStyle, n);
    }
    
    static SynthContext getContext(final JComponent component, final Region region, final SynthStyle synthStyle, final int n) {
        SynthContext synthContext = SynthContext.queue.poll();
        if (synthContext == null) {
            synthContext = new SynthContext();
        }
        synthContext.reset(component, region, synthStyle, n);
        return synthContext;
    }
    
    static void releaseContext(final SynthContext synthContext) {
        SynthContext.queue.offer(synthContext);
    }
    
    SynthContext() {
    }
    
    public SynthContext(final JComponent component, final Region region, final SynthStyle synthStyle, final int n) {
        if (component == null || region == null || synthStyle == null) {
            throw new NullPointerException("You must supply a non-null component, region and style");
        }
        this.reset(component, region, synthStyle, n);
    }
    
    public JComponent getComponent() {
        return this.component;
    }
    
    public Region getRegion() {
        return this.region;
    }
    
    boolean isSubregion() {
        return this.getRegion().isSubregion();
    }
    
    void setStyle(final SynthStyle style) {
        this.style = style;
    }
    
    public SynthStyle getStyle() {
        return this.style;
    }
    
    void setComponentState(final int state) {
        this.state = state;
    }
    
    public int getComponentState() {
        return this.state;
    }
    
    void reset(final JComponent component, final Region region, final SynthStyle style, final int state) {
        this.component = component;
        this.region = region;
        this.style = style;
        this.state = state;
    }
    
    void dispose() {
        this.component = null;
        this.style = null;
        releaseContext(this);
    }
    
    SynthPainter getPainter() {
        final SynthPainter painter = this.getStyle().getPainter(this);
        if (painter != null) {
            return painter;
        }
        return SynthPainter.NULL_PAINTER;
    }
    
    static {
        queue = new ConcurrentLinkedQueue<SynthContext>();
    }
}
