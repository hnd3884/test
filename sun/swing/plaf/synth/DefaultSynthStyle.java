package sun.swing.plaf.synth;

import java.util.Arrays;
import javax.swing.UIDefaults;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.Region;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.SynthContext;
import java.util.HashMap;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthGraphicsUtils;
import java.awt.Font;
import java.util.Map;
import java.awt.Insets;
import javax.swing.plaf.synth.SynthStyle;

public class DefaultSynthStyle extends SynthStyle implements Cloneable
{
    private static final Object PENDING;
    private boolean opaque;
    private Insets insets;
    private StateInfo[] states;
    private Map data;
    private Font font;
    private SynthGraphicsUtils synthGraphics;
    private SynthPainter painter;
    
    public DefaultSynthStyle() {
    }
    
    public DefaultSynthStyle(final DefaultSynthStyle defaultSynthStyle) {
        this.opaque = defaultSynthStyle.opaque;
        if (defaultSynthStyle.insets != null) {
            this.insets = new Insets(defaultSynthStyle.insets.top, defaultSynthStyle.insets.left, defaultSynthStyle.insets.bottom, defaultSynthStyle.insets.right);
        }
        if (defaultSynthStyle.states != null) {
            this.states = new StateInfo[defaultSynthStyle.states.length];
            for (int i = defaultSynthStyle.states.length - 1; i >= 0; --i) {
                this.states[i] = (StateInfo)defaultSynthStyle.states[i].clone();
            }
        }
        if (defaultSynthStyle.data != null) {
            (this.data = new HashMap()).putAll(defaultSynthStyle.data);
        }
        this.font = defaultSynthStyle.font;
        this.synthGraphics = defaultSynthStyle.synthGraphics;
        this.painter = defaultSynthStyle.painter;
    }
    
    public DefaultSynthStyle(final Insets insets, final boolean opaque, final StateInfo[] states, final Map data) {
        this.insets = insets;
        this.opaque = opaque;
        this.states = states;
        this.data = data;
    }
    
    @Override
    public Color getColor(final SynthContext synthContext, final ColorType colorType) {
        return this.getColor(synthContext.getComponent(), synthContext.getRegion(), synthContext.getComponentState(), colorType);
    }
    
    public Color getColor(final JComponent component, final Region region, final int n, final ColorType colorType) {
        if (!region.isSubregion() && n == 1) {
            if (colorType == ColorType.BACKGROUND) {
                return component.getBackground();
            }
            if (colorType == ColorType.FOREGROUND) {
                return component.getForeground();
            }
            if (colorType == ColorType.TEXT_FOREGROUND) {
                final Color foreground = component.getForeground();
                if (!(foreground instanceof UIResource)) {
                    return foreground;
                }
            }
        }
        final Color colorForState = this.getColorForState(component, region, n, colorType);
        if (colorForState == null) {
            if (colorType == ColorType.BACKGROUND || colorType == ColorType.TEXT_BACKGROUND) {
                return component.getBackground();
            }
            if (colorType == ColorType.FOREGROUND || colorType == ColorType.TEXT_FOREGROUND) {
                return component.getForeground();
            }
        }
        return colorForState;
    }
    
    @Override
    protected Color getColorForState(final SynthContext synthContext, final ColorType colorType) {
        return this.getColorForState(synthContext.getComponent(), synthContext.getRegion(), synthContext.getComponentState(), colorType);
    }
    
    protected Color getColorForState(final JComponent component, final Region region, final int n, final ColorType colorType) {
        final StateInfo stateInfo = this.getStateInfo(n);
        final Color color;
        if (stateInfo != null && (color = stateInfo.getColor(colorType)) != null) {
            return color;
        }
        if (stateInfo == null || stateInfo.getComponentState() != 0) {
            final StateInfo stateInfo2 = this.getStateInfo(0);
            if (stateInfo2 != null) {
                return stateInfo2.getColor(colorType);
            }
        }
        return null;
    }
    
    public void setFont(final Font font) {
        this.font = font;
    }
    
    @Override
    public Font getFont(final SynthContext synthContext) {
        return this.getFont(synthContext.getComponent(), synthContext.getRegion(), synthContext.getComponentState());
    }
    
    public Font getFont(final JComponent component, final Region region, final int n) {
        if (!region.isSubregion() && n == 1) {
            return component.getFont();
        }
        final Font font = component.getFont();
        if (font != null && !(font instanceof UIResource)) {
            return font;
        }
        return this.getFontForState(component, region, n);
    }
    
    protected Font getFontForState(final JComponent component, final Region region, final int n) {
        if (component == null) {
            return this.font;
        }
        final StateInfo stateInfo = this.getStateInfo(n);
        final Font font;
        if (stateInfo != null && (font = stateInfo.getFont()) != null) {
            return font;
        }
        if (stateInfo == null || stateInfo.getComponentState() != 0) {
            final StateInfo stateInfo2 = this.getStateInfo(0);
            final Font font2;
            if (stateInfo2 != null && (font2 = stateInfo2.getFont()) != null) {
                return font2;
            }
        }
        return this.font;
    }
    
    @Override
    protected Font getFontForState(final SynthContext synthContext) {
        return this.getFontForState(synthContext.getComponent(), synthContext.getRegion(), synthContext.getComponentState());
    }
    
    public void setGraphicsUtils(final SynthGraphicsUtils synthGraphics) {
        this.synthGraphics = synthGraphics;
    }
    
    @Override
    public SynthGraphicsUtils getGraphicsUtils(final SynthContext synthContext) {
        if (this.synthGraphics == null) {
            return super.getGraphicsUtils(synthContext);
        }
        return this.synthGraphics;
    }
    
    public void setInsets(final Insets insets) {
        this.insets = insets;
    }
    
    @Override
    public Insets getInsets(final SynthContext synthContext, Insets insets) {
        if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        }
        if (this.insets != null) {
            insets.left = this.insets.left;
            insets.right = this.insets.right;
            insets.top = this.insets.top;
            insets.bottom = this.insets.bottom;
        }
        else {
            final Insets insets2 = insets;
            final Insets insets3 = insets;
            final Insets insets4 = insets;
            final Insets insets5 = insets;
            final int n = 0;
            insets5.bottom = n;
            insets4.top = n;
            insets3.right = n;
            insets2.left = n;
        }
        return insets;
    }
    
    public void setPainter(final SynthPainter painter) {
        this.painter = painter;
    }
    
    @Override
    public SynthPainter getPainter(final SynthContext synthContext) {
        return this.painter;
    }
    
    public void setOpaque(final boolean opaque) {
        this.opaque = opaque;
    }
    
    @Override
    public boolean isOpaque(final SynthContext synthContext) {
        return this.opaque;
    }
    
    public void setData(final Map data) {
        this.data = data;
    }
    
    public Map getData() {
        return this.data;
    }
    
    @Override
    public Object get(final SynthContext synthContext, final Object o) {
        final StateInfo stateInfo = this.getStateInfo(synthContext.getComponentState());
        if (stateInfo != null && stateInfo.getData() != null && this.getKeyFromData(stateInfo.getData(), o) != null) {
            return this.getKeyFromData(stateInfo.getData(), o);
        }
        final StateInfo stateInfo2 = this.getStateInfo(0);
        if (stateInfo2 != null && stateInfo2.getData() != null && this.getKeyFromData(stateInfo2.getData(), o) != null) {
            return this.getKeyFromData(stateInfo2.getData(), o);
        }
        if (this.getKeyFromData(this.data, o) != null) {
            return this.getKeyFromData(this.data, o);
        }
        return this.getDefaultValue(synthContext, o);
    }
    
    private Object getKeyFromData(final Map map, final Object o) {
        Object o2 = null;
        if (map != null) {
            synchronized (map) {
                o2 = map.get(o);
            }
            while (o2 == DefaultSynthStyle.PENDING) {
                synchronized (map) {
                    try {
                        map.wait();
                    }
                    catch (final InterruptedException ex) {}
                    o2 = map.get(o);
                }
            }
            if (o2 instanceof UIDefaults.LazyValue) {
                synchronized (map) {
                    map.put(o, DefaultSynthStyle.PENDING);
                }
                o2 = ((UIDefaults.LazyValue)o2).createValue(null);
                synchronized (map) {
                    map.put(o, o2);
                    map.notifyAll();
                }
            }
        }
        return o2;
    }
    
    public Object getDefaultValue(final SynthContext synthContext, final Object o) {
        return super.get(synthContext, o);
    }
    
    public Object clone() {
        DefaultSynthStyle defaultSynthStyle;
        try {
            defaultSynthStyle = (DefaultSynthStyle)super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
        if (this.states != null) {
            defaultSynthStyle.states = new StateInfo[this.states.length];
            for (int i = this.states.length - 1; i >= 0; --i) {
                defaultSynthStyle.states[i] = (StateInfo)this.states[i].clone();
            }
        }
        if (this.data != null) {
            (defaultSynthStyle.data = new HashMap()).putAll(this.data);
        }
        return defaultSynthStyle;
    }
    
    public DefaultSynthStyle addTo(final DefaultSynthStyle defaultSynthStyle) {
        if (this.insets != null) {
            defaultSynthStyle.insets = this.insets;
        }
        if (this.font != null) {
            defaultSynthStyle.font = this.font;
        }
        if (this.painter != null) {
            defaultSynthStyle.painter = this.painter;
        }
        if (this.synthGraphics != null) {
            defaultSynthStyle.synthGraphics = this.synthGraphics;
        }
        defaultSynthStyle.opaque = this.opaque;
        if (this.states != null) {
            if (defaultSynthStyle.states == null) {
                defaultSynthStyle.states = new StateInfo[this.states.length];
                for (int i = this.states.length - 1; i >= 0; --i) {
                    if (this.states[i] != null) {
                        defaultSynthStyle.states[i] = (StateInfo)this.states[i].clone();
                    }
                }
            }
            else {
                int n = 0;
                int n2 = 0;
                final int length = defaultSynthStyle.states.length;
                for (int j = this.states.length - 1; j >= 0; --j) {
                    final int componentState = this.states[j].getComponentState();
                    boolean b = false;
                    for (int k = length - 1 - n2; k >= 0; --k) {
                        if (componentState == defaultSynthStyle.states[k].getComponentState()) {
                            defaultSynthStyle.states[k] = this.states[j].addTo(defaultSynthStyle.states[k]);
                            final StateInfo stateInfo = defaultSynthStyle.states[length - 1 - n2];
                            defaultSynthStyle.states[length - 1 - n2] = defaultSynthStyle.states[k];
                            defaultSynthStyle.states[k] = stateInfo;
                            ++n2;
                            b = true;
                            break;
                        }
                    }
                    if (!b) {
                        ++n;
                    }
                }
                if (n != 0) {
                    final StateInfo[] states = new StateInfo[n + length];
                    int n3 = length;
                    System.arraycopy(defaultSynthStyle.states, 0, states, 0, length);
                    for (int l = this.states.length - 1; l >= 0; --l) {
                        final int componentState2 = this.states[l].getComponentState();
                        boolean b2 = false;
                        for (int n4 = length - 1; n4 >= 0; --n4) {
                            if (componentState2 == defaultSynthStyle.states[n4].getComponentState()) {
                                b2 = true;
                                break;
                            }
                        }
                        if (!b2) {
                            states[n3++] = (StateInfo)this.states[l].clone();
                        }
                    }
                    defaultSynthStyle.states = states;
                }
            }
        }
        if (this.data != null) {
            if (defaultSynthStyle.data == null) {
                defaultSynthStyle.data = new HashMap();
            }
            defaultSynthStyle.data.putAll(this.data);
        }
        return defaultSynthStyle;
    }
    
    public void setStateInfo(final StateInfo[] states) {
        this.states = states;
    }
    
    public StateInfo[] getStateInfo() {
        return this.states;
    }
    
    public StateInfo getStateInfo(final int n) {
        if (this.states != null) {
            int n2 = 0;
            int n3 = -1;
            int n4 = -1;
            if (n == 0) {
                for (int i = this.states.length - 1; i >= 0; --i) {
                    if (this.states[i].getComponentState() == 0) {
                        return this.states[i];
                    }
                }
                return null;
            }
            for (int j = this.states.length - 1; j >= 0; --j) {
                final int componentState = this.states[j].getComponentState();
                if (componentState == 0) {
                    if (n4 == -1) {
                        n4 = j;
                    }
                }
                else if ((n & componentState) == componentState) {
                    final int n5 = componentState;
                    final int n6 = n5 - ((0xAAAAAAAA & n5) >>> 1);
                    final int n7 = (n6 & 0x33333333) + (n6 >>> 2 & 0x33333333);
                    final int n8 = n7 + (n7 >>> 4) & 0xF0F0F0F;
                    final int n9 = n8 + (n8 >>> 8);
                    final int n10 = n9 + (n9 >>> 16) & 0xFF;
                    if (n10 > n2) {
                        n3 = j;
                        n2 = n10;
                    }
                }
            }
            if (n3 != -1) {
                return this.states[n3];
            }
            if (n4 != -1) {
                return this.states[n4];
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(super.toString()).append(',');
        sb.append("data=").append(this.data).append(',');
        sb.append("font=").append(this.font).append(',');
        sb.append("insets=").append(this.insets).append(',');
        sb.append("synthGraphics=").append(this.synthGraphics).append(',');
        sb.append("painter=").append(this.painter).append(',');
        final StateInfo[] stateInfo = this.getStateInfo();
        if (stateInfo != null) {
            sb.append("states[");
            final StateInfo[] array = stateInfo;
            for (int length = array.length, i = 0; i < length; ++i) {
                sb.append(array[i].toString()).append(',');
            }
            sb.append(']').append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    static {
        PENDING = new Object();
    }
    
    public static class StateInfo
    {
        private Map data;
        private Font font;
        private Color[] colors;
        private int state;
        
        public StateInfo() {
        }
        
        public StateInfo(final int state, final Font font, final Color[] colors) {
            this.state = state;
            this.font = font;
            this.colors = colors;
        }
        
        public StateInfo(final StateInfo stateInfo) {
            this.state = stateInfo.state;
            this.font = stateInfo.font;
            if (stateInfo.data != null) {
                if (this.data == null) {
                    this.data = new HashMap();
                }
                this.data.putAll(stateInfo.data);
            }
            if (stateInfo.colors != null) {
                this.colors = new Color[stateInfo.colors.length];
                System.arraycopy(stateInfo.colors, 0, this.colors, 0, stateInfo.colors.length);
            }
        }
        
        public Map getData() {
            return this.data;
        }
        
        public void setData(final Map data) {
            this.data = data;
        }
        
        public void setFont(final Font font) {
            this.font = font;
        }
        
        public Font getFont() {
            return this.font;
        }
        
        public void setColors(final Color[] colors) {
            this.colors = colors;
        }
        
        public Color[] getColors() {
            return this.colors;
        }
        
        public Color getColor(final ColorType colorType) {
            if (this.colors != null) {
                final int id = colorType.getID();
                if (id < this.colors.length) {
                    return this.colors[id];
                }
            }
            return null;
        }
        
        public StateInfo addTo(final StateInfo stateInfo) {
            if (this.font != null) {
                stateInfo.font = this.font;
            }
            if (this.data != null) {
                if (stateInfo.data == null) {
                    stateInfo.data = new HashMap();
                }
                stateInfo.data.putAll(this.data);
            }
            if (this.colors != null) {
                if (stateInfo.colors == null) {
                    stateInfo.colors = new Color[this.colors.length];
                    System.arraycopy(this.colors, 0, stateInfo.colors, 0, this.colors.length);
                }
                else {
                    if (stateInfo.colors.length < this.colors.length) {
                        final Color[] colors = stateInfo.colors;
                        System.arraycopy(colors, 0, stateInfo.colors = new Color[this.colors.length], 0, colors.length);
                    }
                    for (int i = this.colors.length - 1; i >= 0; --i) {
                        if (this.colors[i] != null) {
                            stateInfo.colors[i] = this.colors[i];
                        }
                    }
                }
            }
            return stateInfo;
        }
        
        public void setComponentState(final int state) {
            this.state = state;
        }
        
        public int getComponentState() {
            return this.state;
        }
        
        private int getMatchCount(int n) {
            n &= this.state;
            n -= (0xAAAAAAAA & n) >>> 1;
            n = (n & 0x33333333) + (n >>> 2 & 0x33333333);
            n = (n + (n >>> 4) & 0xF0F0F0F);
            n += n >>> 8;
            n += n >>> 16;
            return n & 0xFF;
        }
        
        public Object clone() {
            return new StateInfo(this);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append(super.toString()).append(',');
            sb.append("state=").append(Integer.toString(this.state)).append(',');
            sb.append("font=").append(this.font).append(',');
            if (this.colors != null) {
                sb.append("colors=").append(Arrays.asList(this.colors)).append(',');
            }
            return sb.toString();
        }
    }
}
