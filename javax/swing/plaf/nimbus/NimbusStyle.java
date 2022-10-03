package javax.swing.plaf.nimbus;

import java.util.Hashtable;
import javax.swing.plaf.ColorUIResource;
import java.awt.Font;
import javax.swing.plaf.synth.ColorType;
import javax.swing.Painter;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.awt.Insets;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.JComponent;
import java.lang.ref.WeakReference;
import javax.swing.plaf.synth.SynthPainter;
import java.util.Comparator;
import java.awt.Color;
import javax.swing.plaf.synth.SynthStyle;

public final class NimbusStyle extends SynthStyle
{
    public static final String LARGE_KEY = "large";
    public static final String SMALL_KEY = "small";
    public static final String MINI_KEY = "mini";
    public static final double LARGE_SCALE = 1.15;
    public static final double SMALL_SCALE = 0.857;
    public static final double MINI_SCALE = 0.714;
    private static final Object NULL;
    private static final Color DEFAULT_COLOR;
    private static final Comparator<RuntimeState> STATE_COMPARATOR;
    private String prefix;
    private SynthPainter painter;
    private Values values;
    private CacheKey tmpKey;
    private WeakReference<JComponent> component;
    
    NimbusStyle(final String prefix, final JComponent component) {
        this.tmpKey = new CacheKey("", 0);
        if (component != null) {
            this.component = new WeakReference<JComponent>(component);
        }
        this.prefix = prefix;
        this.painter = new SynthPainterImpl(this);
    }
    
    @Override
    public void installDefaults(final SynthContext synthContext) {
        this.validate();
        super.installDefaults(synthContext);
    }
    
    private void validate() {
        if (this.values != null) {
            return;
        }
        this.values = new Values();
        Map<String, Object> defaultsForPrefix = ((NimbusLookAndFeel)UIManager.getLookAndFeel()).getDefaultsForPrefix(this.prefix);
        if (this.component != null) {
            final Object clientProperty = this.component.get().getClientProperty("Nimbus.Overrides");
            if (clientProperty instanceof UIDefaults) {
                final Object clientProperty2 = this.component.get().getClientProperty("Nimbus.Overrides.InheritDefaults");
                final boolean b = !(clientProperty2 instanceof Boolean) || (boolean)clientProperty2;
                final UIDefaults uiDefaults = (UIDefaults)clientProperty;
                final TreeMap treeMap = new TreeMap();
                for (final String next : ((Hashtable<String, V>)uiDefaults).keySet()) {
                    if (next instanceof String) {
                        final String s = next;
                        if (!s.startsWith(this.prefix)) {
                            continue;
                        }
                        treeMap.put(s, uiDefaults.get(s));
                    }
                }
                if (b) {
                    defaultsForPrefix.putAll(treeMap);
                }
                else {
                    defaultsForPrefix = treeMap;
                }
            }
        }
        final ArrayList list = new ArrayList();
        final HashMap hashMap = new HashMap();
        final ArrayList list2 = new ArrayList();
        final String s2 = defaultsForPrefix.get(this.prefix + ".States");
        if (s2 != null) {
            final String[] split = s2.split(",");
            for (int i = 0; i < split.length; ++i) {
                split[i] = split[i].trim();
                if (!State.isStandardStateName(split[i])) {
                    final State state = defaultsForPrefix.get(this.prefix + "." + split[i]);
                    if (state != null) {
                        list.add(state);
                    }
                }
                else {
                    list.add(State.getStandardState(split[i]));
                }
            }
            if (list.size() > 0) {
                this.values.stateTypes = (State[])list.toArray(new State[list.size()]);
            }
            int n = 1;
            final Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                hashMap.put(((State)iterator2.next()).getName(), n);
                n <<= 1;
            }
        }
        else {
            list.add(State.Enabled);
            list.add(State.MouseOver);
            list.add(State.Pressed);
            list.add(State.Disabled);
            list.add(State.Focused);
            list.add(State.Selected);
            list.add(State.Default);
            hashMap.put("Enabled", 1);
            hashMap.put("MouseOver", 2);
            hashMap.put("Pressed", 4);
            hashMap.put("Disabled", 8);
            hashMap.put("Focused", 256);
            hashMap.put("Selected", 512);
            hashMap.put("Default", 1024);
        }
        for (final String s3 : defaultsForPrefix.keySet()) {
            final String substring = s3.substring(this.prefix.length());
            if (substring.indexOf(34) == -1) {
                if (substring.indexOf(58) != -1) {
                    continue;
                }
                final String substring2 = substring.substring(1);
                String substring3 = null;
                final int index = substring2.indexOf(93);
                String substring4;
                if (index < 0) {
                    substring4 = substring2;
                }
                else {
                    substring3 = substring2.substring(0, index);
                    substring4 = substring2.substring(index + 2);
                }
                if (substring3 == null) {
                    if ("contentMargins".equals(substring4)) {
                        this.values.contentMargins = defaultsForPrefix.get(s3);
                    }
                    else {
                        if ("States".equals(substring4)) {
                            continue;
                        }
                        this.values.defaults.put(substring4, defaultsForPrefix.get(s3));
                    }
                }
                else {
                    boolean b2 = false;
                    int n2 = 0;
                    for (final String s4 : substring3.split("\\+")) {
                        if (!hashMap.containsKey(s4)) {
                            b2 = true;
                            break;
                        }
                        n2 |= (int)hashMap.get(s4);
                    }
                    if (b2) {
                        continue;
                    }
                    RuntimeState runtimeState = null;
                    for (final RuntimeState runtimeState2 : list2) {
                        if (runtimeState2.state == n2) {
                            runtimeState = runtimeState2;
                            break;
                        }
                    }
                    if (runtimeState == null) {
                        runtimeState = new RuntimeState(n2, substring3);
                        list2.add(runtimeState);
                    }
                    if ("backgroundPainter".equals(substring4)) {
                        runtimeState.backgroundPainter = this.getPainter(defaultsForPrefix, s3);
                    }
                    else if ("foregroundPainter".equals(substring4)) {
                        runtimeState.foregroundPainter = this.getPainter(defaultsForPrefix, s3);
                    }
                    else if ("borderPainter".equals(substring4)) {
                        runtimeState.borderPainter = this.getPainter(defaultsForPrefix, s3);
                    }
                    else {
                        runtimeState.defaults.put(substring4, defaultsForPrefix.get(s3));
                    }
                }
            }
        }
        Collections.sort((List<Object>)list2, (Comparator<? super Object>)NimbusStyle.STATE_COMPARATOR);
        this.values.states = (RuntimeState[])list2.toArray(new RuntimeState[list2.size()]);
    }
    
    private Painter getPainter(final Map<String, Object> map, final String s) {
        Object o = map.get(s);
        if (o instanceof UIDefaults.LazyValue) {
            o = ((UIDefaults.LazyValue)o).createValue(UIManager.getDefaults());
        }
        return (o instanceof Painter) ? ((Painter)o) : null;
    }
    
    @Override
    public Insets getInsets(final SynthContext synthContext, Insets insets) {
        if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        }
        final Values values = this.getValues(synthContext);
        if (values.contentMargins == null) {
            final Insets insets2 = insets;
            final Insets insets3 = insets;
            final Insets insets4 = insets;
            final Insets insets5 = insets;
            final int n = 0;
            insets5.right = n;
            insets4.left = n;
            insets3.top = n;
            insets2.bottom = n;
            return insets;
        }
        insets.bottom = values.contentMargins.bottom;
        insets.top = values.contentMargins.top;
        insets.left = values.contentMargins.left;
        insets.right = values.contentMargins.right;
        final String s = (String)synthContext.getComponent().getClientProperty("JComponent.sizeVariant");
        if (s != null) {
            if ("large".equals(s)) {
                final Insets insets6 = insets;
                insets6.bottom *= (int)1.15;
                final Insets insets7 = insets;
                insets7.top *= (int)1.15;
                final Insets insets8 = insets;
                insets8.left *= (int)1.15;
                final Insets insets9 = insets;
                insets9.right *= (int)1.15;
            }
            else if ("small".equals(s)) {
                final Insets insets10 = insets;
                insets10.bottom *= (int)0.857;
                final Insets insets11 = insets;
                insets11.top *= (int)0.857;
                final Insets insets12 = insets;
                insets12.left *= (int)0.857;
                final Insets insets13 = insets;
                insets13.right *= (int)0.857;
            }
            else if ("mini".equals(s)) {
                final Insets insets14 = insets;
                insets14.bottom *= (int)0.714;
                final Insets insets15 = insets;
                insets15.top *= (int)0.714;
                final Insets insets16 = insets;
                insets16.left *= (int)0.714;
                final Insets insets17 = insets;
                insets17.right *= (int)0.714;
            }
        }
        return insets;
    }
    
    @Override
    protected Color getColorForState(final SynthContext synthContext, final ColorType colorType) {
        String string;
        if (colorType == ColorType.BACKGROUND) {
            string = "background";
        }
        else if (colorType == ColorType.FOREGROUND) {
            string = "textForeground";
        }
        else if (colorType == ColorType.TEXT_BACKGROUND) {
            string = "textBackground";
        }
        else if (colorType == ColorType.TEXT_FOREGROUND) {
            string = "textForeground";
        }
        else if (colorType == ColorType.FOCUS) {
            string = "focus";
        }
        else {
            if (colorType == null) {
                return NimbusStyle.DEFAULT_COLOR;
            }
            string = colorType.toString();
        }
        Color default_COLOR = (Color)this.get(synthContext, string);
        if (default_COLOR == null) {
            default_COLOR = NimbusStyle.DEFAULT_COLOR;
        }
        return default_COLOR;
    }
    
    @Override
    protected Font getFontForState(final SynthContext synthContext) {
        Font font = (Font)this.get(synthContext, "font");
        if (font == null) {
            font = UIManager.getFont("defaultFont");
        }
        final String s = (String)synthContext.getComponent().getClientProperty("JComponent.sizeVariant");
        if (s != null) {
            if ("large".equals(s)) {
                font = font.deriveFont((float)Math.round(font.getSize2D() * 1.15));
            }
            else if ("small".equals(s)) {
                font = font.deriveFont((float)Math.round(font.getSize2D() * 0.857));
            }
            else if ("mini".equals(s)) {
                font = font.deriveFont((float)Math.round(font.getSize2D() * 0.714));
            }
        }
        return font;
    }
    
    @Override
    public SynthPainter getPainter(final SynthContext synthContext) {
        return this.painter;
    }
    
    @Override
    public boolean isOpaque(final SynthContext synthContext) {
        if ("Table.cellRenderer".equals(synthContext.getComponent().getName())) {
            return true;
        }
        final Boolean b = (Boolean)this.get(synthContext, "opaque");
        return b != null && b;
    }
    
    @Override
    public Object get(final SynthContext synthContext, final Object o) {
        final Values values = this.getValues(synthContext);
        final String string = o.toString();
        final String substring = string.substring(string.indexOf(".") + 1);
        final int extendedState = this.getExtendedState(synthContext, values);
        this.tmpKey.init(substring, extendedState);
        Object o2 = values.cache.get(this.tmpKey);
        if (o2 == null) {
            RuntimeState nextState;
            for (int[] array = { -1 }; o2 == null && (nextState = this.getNextState(values.states, array, extendedState)) != null; o2 = nextState.defaults.get(substring)) {}
            if (o2 == null && values.defaults != null) {
                o2 = values.defaults.get(substring);
            }
            if (o2 == null) {
                o2 = UIManager.get(string);
            }
            if (o2 == null && substring.equals("focusInputMap")) {
                o2 = super.get(synthContext, string);
            }
            values.cache.put(new CacheKey(substring, extendedState), (o2 == null) ? NimbusStyle.NULL : o2);
        }
        return (o2 == NimbusStyle.NULL) ? null : o2;
    }
    
    public Painter getBackgroundPainter(final SynthContext synthContext) {
        final Values values = this.getValues(synthContext);
        final int extendedState = this.getExtendedState(synthContext, values);
        this.tmpKey.init("backgroundPainter$$instance", extendedState);
        Painter backgroundPainter = values.cache.get(this.tmpKey);
        if (backgroundPainter != null) {
            return backgroundPainter;
        }
        RuntimeState nextState;
        while ((nextState = this.getNextState(values.states, new int[] { -1 }, extendedState)) != null) {
            if (nextState.backgroundPainter != null) {
                backgroundPainter = nextState.backgroundPainter;
                break;
            }
        }
        if (backgroundPainter == null) {
            backgroundPainter = (Painter)this.get(synthContext, "backgroundPainter");
        }
        if (backgroundPainter != null) {
            values.cache.put(new CacheKey("backgroundPainter$$instance", extendedState), backgroundPainter);
        }
        return backgroundPainter;
    }
    
    public Painter getForegroundPainter(final SynthContext synthContext) {
        final Values values = this.getValues(synthContext);
        final int extendedState = this.getExtendedState(synthContext, values);
        this.tmpKey.init("foregroundPainter$$instance", extendedState);
        Painter foregroundPainter = values.cache.get(this.tmpKey);
        if (foregroundPainter != null) {
            return foregroundPainter;
        }
        RuntimeState nextState;
        while ((nextState = this.getNextState(values.states, new int[] { -1 }, extendedState)) != null) {
            if (nextState.foregroundPainter != null) {
                foregroundPainter = nextState.foregroundPainter;
                break;
            }
        }
        if (foregroundPainter == null) {
            foregroundPainter = (Painter)this.get(synthContext, "foregroundPainter");
        }
        if (foregroundPainter != null) {
            values.cache.put(new CacheKey("foregroundPainter$$instance", extendedState), foregroundPainter);
        }
        return foregroundPainter;
    }
    
    public Painter getBorderPainter(final SynthContext synthContext) {
        final Values values = this.getValues(synthContext);
        final int extendedState = this.getExtendedState(synthContext, values);
        this.tmpKey.init("borderPainter$$instance", extendedState);
        Painter borderPainter = values.cache.get(this.tmpKey);
        if (borderPainter != null) {
            return borderPainter;
        }
        RuntimeState nextState;
        while ((nextState = this.getNextState(values.states, new int[] { -1 }, extendedState)) != null) {
            if (nextState.borderPainter != null) {
                borderPainter = nextState.borderPainter;
                break;
            }
        }
        if (borderPainter == null) {
            borderPainter = (Painter)this.get(synthContext, "borderPainter");
        }
        if (borderPainter != null) {
            values.cache.put(new CacheKey("borderPainter$$instance", extendedState), borderPainter);
        }
        return borderPainter;
    }
    
    private Values getValues(final SynthContext synthContext) {
        this.validate();
        return this.values;
    }
    
    private boolean contains(final String[] array, final String s) {
        assert s != null;
        for (int i = 0; i < array.length; ++i) {
            if (s.equals(array[i])) {
                return true;
            }
        }
        return false;
    }
    
    private int getExtendedState(final SynthContext synthContext, final Values values) {
        final JComponent component = synthContext.getComponent();
        int n = 0;
        int n2 = 1;
        final Object clientProperty = component.getClientProperty("Nimbus.State");
        if (clientProperty != null) {
            final String[] split = clientProperty.toString().split("\\+");
            if (values.stateTypes == null) {
                final String[] array = split;
                for (int length = array.length, i = 0; i < length; ++i) {
                    final State.StandardState standardState = State.getStandardState(array[i]);
                    if (standardState != null) {
                        n |= standardState.getState();
                    }
                }
            }
            else {
                final State[] stateTypes = values.stateTypes;
                for (int length2 = stateTypes.length, j = 0; j < length2; ++j) {
                    if (this.contains(split, stateTypes[j].getName())) {
                        n |= n2;
                    }
                    n2 <<= 1;
                }
            }
        }
        else {
            if (values.stateTypes == null) {
                return synthContext.getComponentState();
            }
            final int componentState = synthContext.getComponentState();
            final State[] stateTypes2 = values.stateTypes;
            for (int length3 = stateTypes2.length, k = 0; k < length3; ++k) {
                if (stateTypes2[k].isInState(component, componentState)) {
                    n |= n2;
                }
                n2 <<= 1;
            }
        }
        return n;
    }
    
    private RuntimeState getNextState(final RuntimeState[] array, final int[] array2, final int n) {
        if (array != null && array.length > 0) {
            int n2 = 0;
            int n3 = -1;
            int n4 = -1;
            if (n == 0) {
                for (int i = array.length - 1; i >= 0; --i) {
                    if (array[i].state == 0) {
                        array2[0] = i;
                        return array[i];
                    }
                }
                array2[0] = -1;
                return null;
            }
            for (int j = ((array2 == null || array2[0] == -1) ? array.length : array2[0]) - 1; j >= 0; --j) {
                final int state = array[j].state;
                if (state == 0) {
                    if (n4 == -1) {
                        n4 = j;
                    }
                }
                else if ((n & state) == state) {
                    final int n5 = state;
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
                array2[0] = n3;
                return array[n3];
            }
            if (n4 != -1) {
                array2[0] = n4;
                return array[n4];
            }
        }
        array2[0] = -1;
        return null;
    }
    
    static {
        NULL = '\0';
        DEFAULT_COLOR = new ColorUIResource(Color.BLACK);
        STATE_COMPARATOR = new Comparator<RuntimeState>() {
            @Override
            public int compare(final RuntimeState runtimeState, final RuntimeState runtimeState2) {
                return runtimeState.state - runtimeState2.state;
            }
        };
    }
    
    private final class RuntimeState implements Cloneable
    {
        int state;
        Painter backgroundPainter;
        Painter foregroundPainter;
        Painter borderPainter;
        String stateName;
        UIDefaults defaults;
        
        private RuntimeState(final int state, final String stateName) {
            this.defaults = new UIDefaults(10, 0.7f);
            this.state = state;
            this.stateName = stateName;
        }
        
        @Override
        public String toString() {
            return this.stateName;
        }
        
        public RuntimeState clone() {
            final RuntimeState runtimeState = new RuntimeState(this.state, this.stateName);
            runtimeState.backgroundPainter = this.backgroundPainter;
            runtimeState.foregroundPainter = this.foregroundPainter;
            runtimeState.borderPainter = this.borderPainter;
            runtimeState.defaults.putAll(this.defaults);
            return runtimeState;
        }
    }
    
    private static final class Values
    {
        State[] stateTypes;
        RuntimeState[] states;
        Insets contentMargins;
        UIDefaults defaults;
        Map<CacheKey, Object> cache;
        
        private Values() {
            this.stateTypes = null;
            this.states = null;
            this.defaults = new UIDefaults(10, 0.7f);
            this.cache = new HashMap<CacheKey, Object>();
        }
    }
    
    private static final class CacheKey
    {
        private String key;
        private int xstate;
        
        CacheKey(final Object o, final int n) {
            this.init(o, n);
        }
        
        void init(final Object o, final int xstate) {
            this.key = o.toString();
            this.xstate = xstate;
        }
        
        @Override
        public boolean equals(final Object o) {
            final CacheKey cacheKey = (CacheKey)o;
            return o != null && this.xstate == cacheKey.xstate && this.key.equals(cacheKey.key);
        }
        
        @Override
        public int hashCode() {
            return 29 * (29 * 3 + this.key.hashCode()) + this.xstate;
        }
    }
}
