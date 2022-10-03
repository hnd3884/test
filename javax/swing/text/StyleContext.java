package javax.swing.text;

import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import java.util.Vector;
import java.util.NoSuchElementException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import java.awt.Toolkit;
import java.awt.FontMetrics;
import sun.font.FontUtilities;
import java.awt.Color;
import javax.swing.event.ChangeListener;
import java.util.Enumeration;
import java.util.Collections;
import java.util.WeakHashMap;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.awt.Font;
import java.util.Hashtable;
import java.io.Serializable;

public class StyleContext implements Serializable, AbstractDocument.AttributeContext
{
    private static StyleContext defaultContext;
    public static final String DEFAULT_STYLE = "default";
    private static Hashtable<Object, String> freezeKeyMap;
    private static Hashtable<String, Object> thawKeyMap;
    private Style styles;
    private transient FontKey fontSearch;
    private transient Hashtable<FontKey, Font> fontTable;
    private transient Map<SmallAttributeSet, WeakReference<SmallAttributeSet>> attributesPool;
    private transient MutableAttributeSet search;
    private int unusedSets;
    static final int THRESHOLD = 9;
    
    public static final StyleContext getDefaultStyleContext() {
        if (StyleContext.defaultContext == null) {
            StyleContext.defaultContext = new StyleContext();
        }
        return StyleContext.defaultContext;
    }
    
    public StyleContext() {
        this.fontSearch = new FontKey(null, 0, 0);
        this.fontTable = new Hashtable<FontKey, Font>();
        this.attributesPool = Collections.synchronizedMap(new WeakHashMap<SmallAttributeSet, WeakReference<SmallAttributeSet>>());
        this.search = new SimpleAttributeSet();
        this.styles = new NamedStyle(null);
        this.addStyle("default", null);
    }
    
    public Style addStyle(final String s, final Style style) {
        final NamedStyle namedStyle = new NamedStyle(s, style);
        if (s != null) {
            this.styles.addAttribute(s, namedStyle);
        }
        return namedStyle;
    }
    
    public void removeStyle(final String s) {
        this.styles.removeAttribute(s);
    }
    
    public Style getStyle(final String s) {
        return (Style)this.styles.getAttribute(s);
    }
    
    public Enumeration<?> getStyleNames() {
        return this.styles.getAttributeNames();
    }
    
    public void addChangeListener(final ChangeListener changeListener) {
        this.styles.addChangeListener(changeListener);
    }
    
    public void removeChangeListener(final ChangeListener changeListener) {
        this.styles.removeChangeListener(changeListener);
    }
    
    public ChangeListener[] getChangeListeners() {
        return ((NamedStyle)this.styles).getChangeListeners();
    }
    
    public Font getFont(final AttributeSet set) {
        int n = 0;
        if (StyleConstants.isBold(set)) {
            n |= 0x1;
        }
        if (StyleConstants.isItalic(set)) {
            n |= 0x2;
        }
        final String fontFamily = StyleConstants.getFontFamily(set);
        int fontSize = StyleConstants.getFontSize(set);
        if (StyleConstants.isSuperscript(set) || StyleConstants.isSubscript(set)) {
            fontSize -= 2;
        }
        return this.getFont(fontFamily, n, fontSize);
    }
    
    public Color getForeground(final AttributeSet set) {
        return StyleConstants.getForeground(set);
    }
    
    public Color getBackground(final AttributeSet set) {
        return StyleConstants.getBackground(set);
    }
    
    public Font getFont(final String s, final int n, final int n2) {
        this.fontSearch.setValue(s, n, n2);
        Font font = this.fontTable.get(this.fontSearch);
        if (font == null) {
            final Style style = this.getStyle("default");
            if (style != null) {
                final Font font2 = (Font)style.getAttribute("FONT_ATTRIBUTE_KEY");
                if (font2 != null && font2.getFamily().equalsIgnoreCase(s)) {
                    font = font2.deriveFont(n, (float)n2);
                }
            }
            if (font == null) {
                font = new Font(s, n, n2);
            }
            if (!FontUtilities.fontSupportsDefaultEncoding(font)) {
                font = FontUtilities.getCompositeFontUIResource(font);
            }
            this.fontTable.put(new FontKey(s, n, n2), font);
        }
        return font;
    }
    
    public FontMetrics getFontMetrics(final Font font) {
        return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }
    
    @Override
    public synchronized AttributeSet addAttribute(final AttributeSet set, final Object o, final Object o2) {
        if (set.getAttributeCount() + 1 <= this.getCompressionThreshold()) {
            this.search.removeAttributes(this.search);
            this.search.addAttributes(set);
            this.search.addAttribute(o, o2);
            this.reclaim(set);
            return this.getImmutableUniqueSet();
        }
        final MutableAttributeSet mutableAttributeSet = this.getMutableAttributeSet(set);
        mutableAttributeSet.addAttribute(o, o2);
        return mutableAttributeSet;
    }
    
    @Override
    public synchronized AttributeSet addAttributes(final AttributeSet set, final AttributeSet set2) {
        if (set.getAttributeCount() + set2.getAttributeCount() <= this.getCompressionThreshold()) {
            this.search.removeAttributes(this.search);
            this.search.addAttributes(set);
            this.search.addAttributes(set2);
            this.reclaim(set);
            return this.getImmutableUniqueSet();
        }
        final MutableAttributeSet mutableAttributeSet = this.getMutableAttributeSet(set);
        mutableAttributeSet.addAttributes(set2);
        return mutableAttributeSet;
    }
    
    @Override
    public synchronized AttributeSet removeAttribute(final AttributeSet set, final Object o) {
        if (set.getAttributeCount() - 1 <= this.getCompressionThreshold()) {
            this.search.removeAttributes(this.search);
            this.search.addAttributes(set);
            this.search.removeAttribute(o);
            this.reclaim(set);
            return this.getImmutableUniqueSet();
        }
        final MutableAttributeSet mutableAttributeSet = this.getMutableAttributeSet(set);
        mutableAttributeSet.removeAttribute(o);
        return mutableAttributeSet;
    }
    
    @Override
    public synchronized AttributeSet removeAttributes(final AttributeSet set, final Enumeration<?> enumeration) {
        if (set.getAttributeCount() <= this.getCompressionThreshold()) {
            this.search.removeAttributes(this.search);
            this.search.addAttributes(set);
            this.search.removeAttributes(enumeration);
            this.reclaim(set);
            return this.getImmutableUniqueSet();
        }
        final MutableAttributeSet mutableAttributeSet = this.getMutableAttributeSet(set);
        mutableAttributeSet.removeAttributes(enumeration);
        return mutableAttributeSet;
    }
    
    @Override
    public synchronized AttributeSet removeAttributes(final AttributeSet set, final AttributeSet set2) {
        if (set.getAttributeCount() <= this.getCompressionThreshold()) {
            this.search.removeAttributes(this.search);
            this.search.addAttributes(set);
            this.search.removeAttributes(set2);
            this.reclaim(set);
            return this.getImmutableUniqueSet();
        }
        final MutableAttributeSet mutableAttributeSet = this.getMutableAttributeSet(set);
        mutableAttributeSet.removeAttributes(set2);
        return mutableAttributeSet;
    }
    
    @Override
    public AttributeSet getEmptySet() {
        return SimpleAttributeSet.EMPTY;
    }
    
    @Override
    public void reclaim(final AttributeSet set) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.attributesPool.size();
        }
    }
    
    protected int getCompressionThreshold() {
        return 9;
    }
    
    protected SmallAttributeSet createSmallAttributeSet(final AttributeSet set) {
        return new SmallAttributeSet(set);
    }
    
    protected MutableAttributeSet createLargeAttributeSet(final AttributeSet set) {
        return new SimpleAttributeSet(set);
    }
    
    synchronized void removeUnusedSets() {
        this.attributesPool.size();
    }
    
    AttributeSet getImmutableUniqueSet() {
        final SmallAttributeSet smallAttributeSet = this.createSmallAttributeSet(this.search);
        final WeakReference weakReference = this.attributesPool.get(smallAttributeSet);
        SmallAttributeSet set;
        if (weakReference == null || (set = (SmallAttributeSet)weakReference.get()) == null) {
            set = smallAttributeSet;
            this.attributesPool.put(set, new WeakReference<SmallAttributeSet>(set));
        }
        return set;
    }
    
    MutableAttributeSet getMutableAttributeSet(final AttributeSet set) {
        if (set instanceof MutableAttributeSet && set != SimpleAttributeSet.EMPTY) {
            return (MutableAttributeSet)set;
        }
        return this.createLargeAttributeSet(set);
    }
    
    @Override
    public String toString() {
        this.removeUnusedSets();
        String string = "";
        final Iterator<SmallAttributeSet> iterator = this.attributesPool.keySet().iterator();
        while (iterator.hasNext()) {
            string = string + iterator.next() + "\n";
        }
        return string;
    }
    
    public void writeAttributes(final ObjectOutputStream objectOutputStream, final AttributeSet set) throws IOException {
        writeAttributeSet(objectOutputStream, set);
    }
    
    public void readAttributes(final ObjectInputStream objectInputStream, final MutableAttributeSet set) throws ClassNotFoundException, IOException {
        readAttributeSet(objectInputStream, set);
    }
    
    public static void writeAttributeSet(final ObjectOutputStream objectOutputStream, final AttributeSet set) throws IOException {
        objectOutputStream.writeInt(set.getAttributeCount());
        final Enumeration<?> attributeNames = set.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            if (nextElement instanceof Serializable) {
                objectOutputStream.writeObject(nextElement);
            }
            else {
                final String value = StyleContext.freezeKeyMap.get(nextElement);
                if (value == null) {
                    throw new NotSerializableException(nextElement.getClass().getName() + " is not serializable as a key in an AttributeSet");
                }
                objectOutputStream.writeObject(value);
            }
            final Object attribute = set.getAttribute(nextElement);
            final String value2 = StyleContext.freezeKeyMap.get(attribute);
            if (attribute instanceof Serializable) {
                objectOutputStream.writeObject((value2 != null) ? value2 : attribute);
            }
            else {
                if (value2 == null) {
                    throw new NotSerializableException(((String)attribute).getClass().getName() + " is not serializable as a value in an AttributeSet");
                }
                objectOutputStream.writeObject(value2);
            }
        }
    }
    
    public static void readAttributeSet(final ObjectInputStream objectInputStream, final MutableAttributeSet set) throws ClassNotFoundException, IOException {
        for (int int1 = objectInputStream.readInt(), i = 0; i < int1; ++i) {
            Object object = objectInputStream.readObject();
            Object object2 = objectInputStream.readObject();
            if (StyleContext.thawKeyMap != null) {
                final Object value = StyleContext.thawKeyMap.get(object);
                if (value != null) {
                    object = value;
                }
                final Object value2 = StyleContext.thawKeyMap.get(object2);
                if (value2 != null) {
                    object2 = value2;
                }
            }
            set.addAttribute(object, object2);
        }
    }
    
    public static void registerStaticAttributeKey(final Object o) {
        final String string = o.getClass().getName() + "." + o.toString();
        if (StyleContext.freezeKeyMap == null) {
            StyleContext.freezeKeyMap = new Hashtable<Object, String>();
            StyleContext.thawKeyMap = new Hashtable<String, Object>();
        }
        StyleContext.freezeKeyMap.put(o, string);
        StyleContext.thawKeyMap.put(string, o);
    }
    
    public static Object getStaticAttribute(final Object o) {
        if (StyleContext.thawKeyMap == null || o == null) {
            return null;
        }
        return StyleContext.thawKeyMap.get(o);
    }
    
    public static Object getStaticAttributeKey(final Object o) {
        return o.getClass().getName() + "." + o.toString();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        this.removeUnusedSets();
        objectOutputStream.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        this.fontSearch = new FontKey(null, 0, 0);
        this.fontTable = new Hashtable<FontKey, Font>();
        this.search = new SimpleAttributeSet();
        this.attributesPool = Collections.synchronizedMap(new WeakHashMap<SmallAttributeSet, WeakReference<SmallAttributeSet>>());
        objectInputStream.defaultReadObject();
    }
    
    static {
        try {
            for (int length = StyleConstants.keys.length, i = 0; i < length; ++i) {
                registerStaticAttributeKey(StyleConstants.keys[i]);
            }
        }
        catch (final Throwable t) {
            t.printStackTrace();
        }
    }
    
    public class SmallAttributeSet implements AttributeSet
    {
        Object[] attributes;
        AttributeSet resolveParent;
        
        public SmallAttributeSet(final Object[] attributes) {
            this.attributes = attributes;
            this.updateResolveParent();
        }
        
        public SmallAttributeSet(final AttributeSet set) {
            final Object[] attributes = new Object[2 * set.getAttributeCount()];
            final Enumeration<?> attributeNames = set.getAttributeNames();
            int n = 0;
            while (attributeNames.hasMoreElements()) {
                attributes[n] = attributeNames.nextElement();
                attributes[n + 1] = set.getAttribute(attributes[n]);
                n += 2;
            }
            this.attributes = attributes;
            this.updateResolveParent();
        }
        
        private void updateResolveParent() {
            this.resolveParent = null;
            final Object[] attributes = this.attributes;
            for (int i = 0; i < attributes.length; i += 2) {
                if (attributes[i] == StyleConstants.ResolveAttribute) {
                    this.resolveParent = (AttributeSet)attributes[i + 1];
                    break;
                }
            }
        }
        
        Object getLocalAttribute(final Object o) {
            if (o == StyleConstants.ResolveAttribute) {
                return this.resolveParent;
            }
            final Object[] attributes = this.attributes;
            for (int i = 0; i < attributes.length; i += 2) {
                if (o.equals(attributes[i])) {
                    return attributes[i + 1];
                }
            }
            return null;
        }
        
        @Override
        public String toString() {
            String s = "{";
            final Object[] attributes = this.attributes;
            for (int i = 0; i < attributes.length; i += 2) {
                if (attributes[i + 1] instanceof AttributeSet) {
                    s = s + attributes[i] + "=AttributeSet,";
                }
                else {
                    s = s + attributes[i] + "=" + attributes[i + 1] + ",";
                }
            }
            return s + "}";
        }
        
        @Override
        public int hashCode() {
            int n = 0;
            final Object[] attributes = this.attributes;
            for (int i = 1; i < attributes.length; i += 2) {
                n ^= attributes[i].hashCode();
            }
            return n;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof AttributeSet) {
                final AttributeSet set = (AttributeSet)o;
                return this.getAttributeCount() == set.getAttributeCount() && this.containsAttributes(set);
            }
            return false;
        }
        
        public Object clone() {
            return this;
        }
        
        @Override
        public int getAttributeCount() {
            return this.attributes.length / 2;
        }
        
        @Override
        public boolean isDefined(final Object o) {
            final Object[] attributes = this.attributes;
            for (int length = attributes.length, i = 0; i < length; i += 2) {
                if (o.equals(attributes[i])) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean isEqual(final AttributeSet set) {
            if (set instanceof SmallAttributeSet) {
                return set == this;
            }
            return this.getAttributeCount() == set.getAttributeCount() && this.containsAttributes(set);
        }
        
        @Override
        public AttributeSet copyAttributes() {
            return this;
        }
        
        @Override
        public Object getAttribute(final Object o) {
            Object o2 = this.getLocalAttribute(o);
            if (o2 == null) {
                final AttributeSet resolveParent = this.getResolveParent();
                if (resolveParent != null) {
                    o2 = resolveParent.getAttribute(o);
                }
            }
            return o2;
        }
        
        @Override
        public Enumeration<?> getAttributeNames() {
            return new KeyEnumeration(this.attributes);
        }
        
        @Override
        public boolean containsAttribute(final Object o, final Object o2) {
            return o2.equals(this.getAttribute(o));
        }
        
        @Override
        public boolean containsAttributes(final AttributeSet set) {
            boolean equals = true;
            Object nextElement;
            for (Enumeration<?> attributeNames = set.getAttributeNames(); equals && attributeNames.hasMoreElements(); equals = set.getAttribute(nextElement).equals(this.getAttribute(nextElement))) {
                nextElement = attributeNames.nextElement();
            }
            return equals;
        }
        
        @Override
        public AttributeSet getResolveParent() {
            return this.resolveParent;
        }
    }
    
    class KeyEnumeration implements Enumeration<Object>
    {
        Object[] attr;
        int i;
        
        KeyEnumeration(final Object[] attr) {
            this.attr = attr;
            this.i = 0;
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.i < this.attr.length;
        }
        
        @Override
        public Object nextElement() {
            if (this.i < this.attr.length) {
                final Object o = this.attr[this.i];
                this.i += 2;
                return o;
            }
            throw new NoSuchElementException();
        }
    }
    
    class KeyBuilder
    {
        private Vector<Object> keys;
        private Vector<Object> data;
        
        KeyBuilder() {
            this.keys = new Vector<Object>();
            this.data = new Vector<Object>();
        }
        
        public void initialize(final AttributeSet set) {
            if (set instanceof SmallAttributeSet) {
                this.initialize(((SmallAttributeSet)set).attributes);
            }
            else {
                this.keys.removeAllElements();
                this.data.removeAllElements();
                final Enumeration<?> attributeNames = set.getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    final Object nextElement = attributeNames.nextElement();
                    this.addAttribute(nextElement, set.getAttribute(nextElement));
                }
            }
        }
        
        private void initialize(final Object[] array) {
            this.keys.removeAllElements();
            this.data.removeAllElements();
            for (int length = array.length, i = 0; i < length; i += 2) {
                this.keys.addElement(array[i]);
                this.data.addElement(array[i + 1]);
            }
        }
        
        public Object[] createTable() {
            final int size = this.keys.size();
            final Object[] array = new Object[2 * size];
            for (int i = 0; i < size; ++i) {
                final int n = 2 * i;
                array[n] = this.keys.elementAt(i);
                array[n + 1] = this.data.elementAt(i);
            }
            return array;
        }
        
        int getCount() {
            return this.keys.size();
        }
        
        public void addAttribute(final Object o, final Object o2) {
            this.keys.addElement(o);
            this.data.addElement(o2);
        }
        
        public void addAttributes(final AttributeSet set) {
            if (set instanceof SmallAttributeSet) {
                final Object[] attributes = ((SmallAttributeSet)set).attributes;
                for (int length = attributes.length, i = 0; i < length; i += 2) {
                    this.addAttribute(attributes[i], attributes[i + 1]);
                }
            }
            else {
                final Enumeration<?> attributeNames = set.getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    final Object nextElement = attributeNames.nextElement();
                    this.addAttribute(nextElement, set.getAttribute(nextElement));
                }
            }
        }
        
        public void removeAttribute(final Object o) {
            for (int size = this.keys.size(), i = 0; i < size; ++i) {
                if (this.keys.elementAt(i).equals(o)) {
                    this.keys.removeElementAt(i);
                    this.data.removeElementAt(i);
                    return;
                }
            }
        }
        
        public void removeAttributes(final Enumeration enumeration) {
            while (enumeration.hasMoreElements()) {
                this.removeAttribute(enumeration.nextElement());
            }
        }
        
        public void removeAttributes(final AttributeSet set) {
            final Enumeration<?> attributeNames = set.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                final Object nextElement = attributeNames.nextElement();
                this.removeSearchAttribute(nextElement, set.getAttribute(nextElement));
            }
        }
        
        private void removeSearchAttribute(final Object o, final Object o2) {
            for (int size = this.keys.size(), i = 0; i < size; ++i) {
                if (this.keys.elementAt(i).equals(o)) {
                    if (this.data.elementAt(i).equals(o2)) {
                        this.keys.removeElementAt(i);
                        this.data.removeElementAt(i);
                    }
                    return;
                }
            }
        }
    }
    
    static class FontKey
    {
        private String family;
        private int style;
        private int size;
        
        public FontKey(final String s, final int n, final int n2) {
            this.setValue(s, n, n2);
        }
        
        public void setValue(final String s, final int style, final int size) {
            this.family = ((s != null) ? s.intern() : null);
            this.style = style;
            this.size = size;
        }
        
        @Override
        public int hashCode() {
            return ((this.family != null) ? this.family.hashCode() : 0) ^ this.style ^ this.size;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof FontKey) {
                final FontKey fontKey = (FontKey)o;
                return this.size == fontKey.size && this.style == fontKey.style && this.family == fontKey.family;
            }
            return false;
        }
    }
    
    public class NamedStyle implements Style, Serializable
    {
        protected EventListenerList listenerList;
        protected transient ChangeEvent changeEvent;
        private transient AttributeSet attributes;
        
        public NamedStyle(final String name, final Style resolveParent) {
            this.listenerList = new EventListenerList();
            this.changeEvent = null;
            this.attributes = StyleContext.this.getEmptySet();
            if (name != null) {
                this.setName(name);
            }
            if (resolveParent != null) {
                this.setResolveParent(resolveParent);
            }
        }
        
        public NamedStyle(final StyleContext styleContext, final Style style) {
            this(styleContext, null, style);
        }
        
        public NamedStyle() {
            this.listenerList = new EventListenerList();
            this.changeEvent = null;
            this.attributes = StyleContext.this.getEmptySet();
        }
        
        @Override
        public String toString() {
            return "NamedStyle:" + this.getName() + " " + this.attributes;
        }
        
        @Override
        public String getName() {
            if (this.isDefined(StyleConstants.NameAttribute)) {
                return this.getAttribute(StyleConstants.NameAttribute).toString();
            }
            return null;
        }
        
        public void setName(final String s) {
            if (s != null) {
                this.addAttribute(StyleConstants.NameAttribute, s);
            }
        }
        
        @Override
        public void addChangeListener(final ChangeListener changeListener) {
            this.listenerList.add(ChangeListener.class, changeListener);
        }
        
        @Override
        public void removeChangeListener(final ChangeListener changeListener) {
            this.listenerList.remove(ChangeListener.class, changeListener);
        }
        
        public ChangeListener[] getChangeListeners() {
            return this.listenerList.getListeners(ChangeListener.class);
        }
        
        protected void fireStateChanged() {
            final Object[] listenerList = this.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == ChangeListener.class) {
                    if (this.changeEvent == null) {
                        this.changeEvent = new ChangeEvent(this);
                    }
                    ((ChangeListener)listenerList[i + 1]).stateChanged(this.changeEvent);
                }
            }
        }
        
        public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
            return this.listenerList.getListeners(clazz);
        }
        
        @Override
        public int getAttributeCount() {
            return this.attributes.getAttributeCount();
        }
        
        @Override
        public boolean isDefined(final Object o) {
            return this.attributes.isDefined(o);
        }
        
        @Override
        public boolean isEqual(final AttributeSet set) {
            return this.attributes.isEqual(set);
        }
        
        @Override
        public AttributeSet copyAttributes() {
            final NamedStyle namedStyle = new NamedStyle();
            namedStyle.attributes = this.attributes.copyAttributes();
            return namedStyle;
        }
        
        @Override
        public Object getAttribute(final Object o) {
            return this.attributes.getAttribute(o);
        }
        
        @Override
        public Enumeration<?> getAttributeNames() {
            return this.attributes.getAttributeNames();
        }
        
        @Override
        public boolean containsAttribute(final Object o, final Object o2) {
            return this.attributes.containsAttribute(o, o2);
        }
        
        @Override
        public boolean containsAttributes(final AttributeSet set) {
            return this.attributes.containsAttributes(set);
        }
        
        @Override
        public AttributeSet getResolveParent() {
            return this.attributes.getResolveParent();
        }
        
        @Override
        public void addAttribute(final Object o, final Object o2) {
            this.attributes = StyleContext.this.addAttribute(this.attributes, o, o2);
            this.fireStateChanged();
        }
        
        @Override
        public void addAttributes(final AttributeSet set) {
            this.attributes = StyleContext.this.addAttributes(this.attributes, set);
            this.fireStateChanged();
        }
        
        @Override
        public void removeAttribute(final Object o) {
            this.attributes = StyleContext.this.removeAttribute(this.attributes, o);
            this.fireStateChanged();
        }
        
        @Override
        public void removeAttributes(final Enumeration<?> enumeration) {
            this.attributes = StyleContext.this.removeAttributes(this.attributes, enumeration);
            this.fireStateChanged();
        }
        
        @Override
        public void removeAttributes(final AttributeSet set) {
            final StyleContext this$0 = StyleContext.this;
            if (set == this) {
                this.attributes = this$0.getEmptySet();
            }
            else {
                this.attributes = this$0.removeAttributes(this.attributes, set);
            }
            this.fireStateChanged();
        }
        
        @Override
        public void setResolveParent(final AttributeSet set) {
            if (set != null) {
                this.addAttribute(StyleConstants.ResolveAttribute, set);
            }
            else {
                this.removeAttribute(StyleConstants.ResolveAttribute);
            }
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            StyleContext.writeAttributeSet(objectOutputStream, this.attributes);
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
            objectInputStream.defaultReadObject();
            this.attributes = SimpleAttributeSet.EMPTY;
            StyleContext.readAttributeSet(objectInputStream, this);
        }
    }
}
