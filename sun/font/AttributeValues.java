package sun.font;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.Toolkit;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.HashMap;
import java.awt.font.TextAttribute;
import java.util.Iterator;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.awt.im.InputMethodHighlight;
import java.text.Annotation;
import java.awt.font.TransformAttribute;
import java.awt.Font;
import java.awt.Paint;
import java.awt.font.GraphicAttribute;
import java.awt.geom.AffineTransform;
import java.awt.font.NumericShaper;

public final class AttributeValues implements Cloneable
{
    private int defined;
    private int nondefault;
    private String family;
    private float weight;
    private float width;
    private float posture;
    private float size;
    private float tracking;
    private NumericShaper numericShaping;
    private AffineTransform transform;
    private GraphicAttribute charReplacement;
    private Paint foreground;
    private Paint background;
    private float justification;
    private Object imHighlight;
    private Font font;
    private byte imUnderline;
    private byte superscript;
    private byte underline;
    private byte runDirection;
    private byte bidiEmbedding;
    private byte kerning;
    private byte ligatures;
    private boolean strikethrough;
    private boolean swapColors;
    private AffineTransform baselineTransform;
    private AffineTransform charTransform;
    private static final AttributeValues DEFAULT;
    public static final int MASK_ALL;
    private static final String DEFINED_KEY = "sun.font.attributevalues.defined_key";
    
    public AttributeValues() {
        this.family = "Default";
        this.weight = 1.0f;
        this.width = 1.0f;
        this.size = 12.0f;
        this.justification = 1.0f;
        this.imUnderline = -1;
        this.underline = -1;
        this.runDirection = -2;
    }
    
    public String getFamily() {
        return this.family;
    }
    
    public void setFamily(final String family) {
        this.family = family;
        this.update(EAttribute.EFAMILY);
    }
    
    public float getWeight() {
        return this.weight;
    }
    
    public void setWeight(final float weight) {
        this.weight = weight;
        this.update(EAttribute.EWEIGHT);
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public void setWidth(final float width) {
        this.width = width;
        this.update(EAttribute.EWIDTH);
    }
    
    public float getPosture() {
        return this.posture;
    }
    
    public void setPosture(final float posture) {
        this.posture = posture;
        this.update(EAttribute.EPOSTURE);
    }
    
    public float getSize() {
        return this.size;
    }
    
    public void setSize(final float size) {
        this.size = size;
        this.update(EAttribute.ESIZE);
    }
    
    public AffineTransform getTransform() {
        return this.transform;
    }
    
    public void setTransform(final AffineTransform affineTransform) {
        this.transform = ((affineTransform == null || affineTransform.isIdentity()) ? AttributeValues.DEFAULT.transform : new AffineTransform(affineTransform));
        this.updateDerivedTransforms();
        this.update(EAttribute.ETRANSFORM);
    }
    
    public void setTransform(final TransformAttribute transformAttribute) {
        this.transform = ((transformAttribute == null || transformAttribute.isIdentity()) ? AttributeValues.DEFAULT.transform : transformAttribute.getTransform());
        this.updateDerivedTransforms();
        this.update(EAttribute.ETRANSFORM);
    }
    
    public int getSuperscript() {
        return this.superscript;
    }
    
    public void setSuperscript(final int n) {
        this.superscript = (byte)n;
        this.update(EAttribute.ESUPERSCRIPT);
    }
    
    public Font getFont() {
        return this.font;
    }
    
    public void setFont(final Font font) {
        this.font = font;
        this.update(EAttribute.EFONT);
    }
    
    public GraphicAttribute getCharReplacement() {
        return this.charReplacement;
    }
    
    public void setCharReplacement(final GraphicAttribute charReplacement) {
        this.charReplacement = charReplacement;
        this.update(EAttribute.ECHAR_REPLACEMENT);
    }
    
    public Paint getForeground() {
        return this.foreground;
    }
    
    public void setForeground(final Paint foreground) {
        this.foreground = foreground;
        this.update(EAttribute.EFOREGROUND);
    }
    
    public Paint getBackground() {
        return this.background;
    }
    
    public void setBackground(final Paint background) {
        this.background = background;
        this.update(EAttribute.EBACKGROUND);
    }
    
    public int getUnderline() {
        return this.underline;
    }
    
    public void setUnderline(final int n) {
        this.underline = (byte)n;
        this.update(EAttribute.EUNDERLINE);
    }
    
    public boolean getStrikethrough() {
        return this.strikethrough;
    }
    
    public void setStrikethrough(final boolean strikethrough) {
        this.strikethrough = strikethrough;
        this.update(EAttribute.ESTRIKETHROUGH);
    }
    
    public int getRunDirection() {
        return this.runDirection;
    }
    
    public void setRunDirection(final int n) {
        this.runDirection = (byte)n;
        this.update(EAttribute.ERUN_DIRECTION);
    }
    
    public int getBidiEmbedding() {
        return this.bidiEmbedding;
    }
    
    public void setBidiEmbedding(final int n) {
        this.bidiEmbedding = (byte)n;
        this.update(EAttribute.EBIDI_EMBEDDING);
    }
    
    public float getJustification() {
        return this.justification;
    }
    
    public void setJustification(final float justification) {
        this.justification = justification;
        this.update(EAttribute.EJUSTIFICATION);
    }
    
    public Object getInputMethodHighlight() {
        return this.imHighlight;
    }
    
    public void setInputMethodHighlight(final Annotation imHighlight) {
        this.imHighlight = imHighlight;
        this.update(EAttribute.EINPUT_METHOD_HIGHLIGHT);
    }
    
    public void setInputMethodHighlight(final InputMethodHighlight imHighlight) {
        this.imHighlight = imHighlight;
        this.update(EAttribute.EINPUT_METHOD_HIGHLIGHT);
    }
    
    public int getInputMethodUnderline() {
        return this.imUnderline;
    }
    
    public void setInputMethodUnderline(final int n) {
        this.imUnderline = (byte)n;
        this.update(EAttribute.EINPUT_METHOD_UNDERLINE);
    }
    
    public boolean getSwapColors() {
        return this.swapColors;
    }
    
    public void setSwapColors(final boolean swapColors) {
        this.swapColors = swapColors;
        this.update(EAttribute.ESWAP_COLORS);
    }
    
    public NumericShaper getNumericShaping() {
        return this.numericShaping;
    }
    
    public void setNumericShaping(final NumericShaper numericShaping) {
        this.numericShaping = numericShaping;
        this.update(EAttribute.ENUMERIC_SHAPING);
    }
    
    public int getKerning() {
        return this.kerning;
    }
    
    public void setKerning(final int n) {
        this.kerning = (byte)n;
        this.update(EAttribute.EKERNING);
    }
    
    public float getTracking() {
        return this.tracking;
    }
    
    public void setTracking(final float n) {
        this.tracking = (byte)n;
        this.update(EAttribute.ETRACKING);
    }
    
    public int getLigatures() {
        return this.ligatures;
    }
    
    public void setLigatures(final int n) {
        this.ligatures = (byte)n;
        this.update(EAttribute.ELIGATURES);
    }
    
    public AffineTransform getBaselineTransform() {
        return this.baselineTransform;
    }
    
    public AffineTransform getCharTransform() {
        return this.charTransform;
    }
    
    public static int getMask(final EAttribute eAttribute) {
        return eAttribute.mask;
    }
    
    public static int getMask(final EAttribute... array) {
        int n = 0;
        for (int length = array.length, i = 0; i < length; ++i) {
            n |= array[i].mask;
        }
        return n;
    }
    
    public void unsetDefault() {
        this.defined &= this.nondefault;
    }
    
    public void defineAll(final int n) {
        this.defined |= n;
        if ((this.defined & EAttribute.EBASELINE_TRANSFORM.mask) != 0x0) {
            throw new InternalError("can't define derived attribute");
        }
    }
    
    public boolean allDefined(final int n) {
        return (this.defined & n) == n;
    }
    
    public boolean anyDefined(final int n) {
        return (this.defined & n) != 0x0;
    }
    
    public boolean anyNonDefault(final int n) {
        return (this.nondefault & n) != 0x0;
    }
    
    public boolean isDefined(final EAttribute eAttribute) {
        return (this.defined & eAttribute.mask) != 0x0;
    }
    
    public boolean isNonDefault(final EAttribute eAttribute) {
        return (this.nondefault & eAttribute.mask) != 0x0;
    }
    
    public void setDefault(final EAttribute eAttribute) {
        if (eAttribute.att == null) {
            throw new InternalError("can't set default derived attribute: " + eAttribute);
        }
        this.i_set(eAttribute, AttributeValues.DEFAULT);
        this.defined |= eAttribute.mask;
        this.nondefault &= ~eAttribute.mask;
    }
    
    public void unset(final EAttribute eAttribute) {
        if (eAttribute.att == null) {
            throw new InternalError("can't unset derived attribute: " + eAttribute);
        }
        this.i_set(eAttribute, AttributeValues.DEFAULT);
        this.defined &= ~eAttribute.mask;
        this.nondefault &= ~eAttribute.mask;
    }
    
    public void set(final EAttribute default1, final AttributeValues attributeValues) {
        if (default1.att == null) {
            throw new InternalError("can't set derived attribute: " + default1);
        }
        if (attributeValues == null || attributeValues == AttributeValues.DEFAULT) {
            this.setDefault(default1);
        }
        else if ((attributeValues.defined & default1.mask) != 0x0) {
            this.i_set(default1, attributeValues);
            this.update(default1);
        }
    }
    
    public void set(final EAttribute default1, final Object o) {
        if (default1.att == null) {
            throw new InternalError("can't set derived attribute: " + default1);
        }
        if (o != null) {
            try {
                this.i_set(default1, o);
                this.update(default1);
                return;
            }
            catch (final Exception ex) {}
        }
        this.setDefault(default1);
    }
    
    public Object get(final EAttribute eAttribute) {
        if (eAttribute.att == null) {
            throw new InternalError("can't get derived attribute: " + eAttribute);
        }
        if ((this.nondefault & eAttribute.mask) != 0x0) {
            return this.i_get(eAttribute);
        }
        return null;
    }
    
    public AttributeValues merge(final Map<? extends AttributedCharacterIterator.Attribute, ?> map) {
        return this.merge(map, AttributeValues.MASK_ALL);
    }
    
    public AttributeValues merge(final Map<? extends AttributedCharacterIterator.Attribute, ?> map, final int n) {
        if (map instanceof AttributeMap && ((AttributeMap)map).getValues() != null) {
            this.merge(((AttributeMap)map).getValues(), n);
        }
        else if (map != null && !map.isEmpty()) {
            for (final Map.Entry entry : map.entrySet()) {
                try {
                    final EAttribute forAttribute = EAttribute.forAttribute((AttributedCharacterIterator.Attribute)entry.getKey());
                    if (forAttribute == null || (n & forAttribute.mask) == 0x0) {
                        continue;
                    }
                    this.set(forAttribute, entry.getValue());
                }
                catch (final ClassCastException ex) {}
            }
        }
        return this;
    }
    
    public AttributeValues merge(final AttributeValues attributeValues) {
        return this.merge(attributeValues, AttributeValues.MASK_ALL);
    }
    
    public AttributeValues merge(final AttributeValues attributeValues, final int n) {
        int n2 = n & attributeValues.defined;
        for (final EAttribute eAttribute : EAttribute.atts) {
            if (n2 == 0) {
                break;
            }
            if ((n2 & eAttribute.mask) != 0x0) {
                n2 &= ~eAttribute.mask;
                this.i_set(eAttribute, attributeValues);
                this.update(eAttribute);
            }
        }
        return this;
    }
    
    public static AttributeValues fromMap(final Map<? extends AttributedCharacterIterator.Attribute, ?> map) {
        return fromMap(map, AttributeValues.MASK_ALL);
    }
    
    public static AttributeValues fromMap(final Map<? extends AttributedCharacterIterator.Attribute, ?> map, final int n) {
        return new AttributeValues().merge(map, n);
    }
    
    public Map<TextAttribute, Object> toMap(Map<TextAttribute, Object> hashMap) {
        if (hashMap == null) {
            hashMap = new HashMap<TextAttribute, Object>();
        }
        int i = this.defined;
        int n = 0;
        while (i != 0) {
            final EAttribute eAttribute = EAttribute.atts[n];
            if ((i & eAttribute.mask) != 0x0) {
                i &= ~eAttribute.mask;
                hashMap.put(eAttribute.att, this.get(eAttribute));
            }
            ++n;
        }
        return hashMap;
    }
    
    public static boolean is16Hashtable(final Hashtable<Object, Object> hashtable) {
        return hashtable.containsKey("sun.font.attributevalues.defined_key");
    }
    
    public static AttributeValues fromSerializableHashtable(final Hashtable<Object, Object> hashtable) {
        final AttributeValues attributeValues = new AttributeValues();
        if (hashtable != null && !hashtable.isEmpty()) {
            for (final Map.Entry entry : hashtable.entrySet()) {
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                if (key.equals("sun.font.attributevalues.defined_key")) {
                    attributeValues.defineAll((int)value);
                }
                else {
                    try {
                        final EAttribute forAttribute = EAttribute.forAttribute((AttributedCharacterIterator.Attribute)key);
                        if (forAttribute == null) {
                            continue;
                        }
                        attributeValues.set(forAttribute, value);
                    }
                    catch (final ClassCastException ex) {}
                }
            }
        }
        return attributeValues;
    }
    
    public Hashtable<Object, Object> toSerializableHashtable() {
        final Hashtable hashtable = new Hashtable();
        int defined = this.defined;
        int i = this.defined;
        int n = 0;
        while (i != 0) {
            final EAttribute eAttribute = EAttribute.atts[n];
            if ((i & eAttribute.mask) != 0x0) {
                i &= ~eAttribute.mask;
                final Object value = this.get(eAttribute);
                if (value != null) {
                    if (value instanceof Serializable) {
                        hashtable.put(eAttribute.att, value);
                    }
                    else {
                        defined &= ~eAttribute.mask;
                    }
                }
            }
            ++n;
        }
        hashtable.put("sun.font.attributevalues.defined_key", defined);
        return hashtable;
    }
    
    @Override
    public int hashCode() {
        return this.defined << 8 ^ this.nondefault;
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            return this.equals((AttributeValues)o);
        }
        catch (final ClassCastException ex) {
            return false;
        }
    }
    
    public boolean equals(final AttributeValues attributeValues) {
        return attributeValues != null && (attributeValues == this || (this.defined == attributeValues.defined && this.nondefault == attributeValues.nondefault && this.underline == attributeValues.underline && this.strikethrough == attributeValues.strikethrough && this.superscript == attributeValues.superscript && this.width == attributeValues.width && this.kerning == attributeValues.kerning && this.tracking == attributeValues.tracking && this.ligatures == attributeValues.ligatures && this.runDirection == attributeValues.runDirection && this.bidiEmbedding == attributeValues.bidiEmbedding && this.swapColors == attributeValues.swapColors && equals(this.transform, attributeValues.transform) && equals(this.foreground, attributeValues.foreground) && equals(this.background, attributeValues.background) && equals(this.numericShaping, attributeValues.numericShaping) && equals(this.justification, attributeValues.justification) && equals(this.charReplacement, attributeValues.charReplacement) && this.size == attributeValues.size && this.weight == attributeValues.weight && this.posture == attributeValues.posture && equals(this.family, attributeValues.family) && equals(this.font, attributeValues.font) && this.imUnderline == attributeValues.imUnderline && equals(this.imHighlight, attributeValues.imHighlight)));
    }
    
    public AttributeValues clone() {
        try {
            final AttributeValues attributeValues = (AttributeValues)super.clone();
            if (this.transform != null) {
                attributeValues.transform = new AffineTransform(this.transform);
                attributeValues.updateDerivedTransforms();
            }
            return attributeValues;
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('{');
        int i = this.defined;
        int n = 0;
        while (i != 0) {
            final EAttribute eAttribute = EAttribute.atts[n];
            if ((i & eAttribute.mask) != 0x0) {
                i &= ~eAttribute.mask;
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append(eAttribute);
                sb.append('=');
                switch (eAttribute) {
                    case EFAMILY: {
                        sb.append('\"');
                        sb.append(this.family);
                        sb.append('\"');
                        break;
                    }
                    case EWEIGHT: {
                        sb.append(this.weight);
                        break;
                    }
                    case EWIDTH: {
                        sb.append(this.width);
                        break;
                    }
                    case EPOSTURE: {
                        sb.append(this.posture);
                        break;
                    }
                    case ESIZE: {
                        sb.append(this.size);
                        break;
                    }
                    case ETRANSFORM: {
                        sb.append(this.transform);
                        break;
                    }
                    case ESUPERSCRIPT: {
                        sb.append(this.superscript);
                        break;
                    }
                    case EFONT: {
                        sb.append(this.font);
                        break;
                    }
                    case ECHAR_REPLACEMENT: {
                        sb.append(this.charReplacement);
                        break;
                    }
                    case EFOREGROUND: {
                        sb.append(this.foreground);
                        break;
                    }
                    case EBACKGROUND: {
                        sb.append(this.background);
                        break;
                    }
                    case EUNDERLINE: {
                        sb.append(this.underline);
                        break;
                    }
                    case ESTRIKETHROUGH: {
                        sb.append(this.strikethrough);
                        break;
                    }
                    case ERUN_DIRECTION: {
                        sb.append(this.runDirection);
                        break;
                    }
                    case EBIDI_EMBEDDING: {
                        sb.append(this.bidiEmbedding);
                        break;
                    }
                    case EJUSTIFICATION: {
                        sb.append(this.justification);
                        break;
                    }
                    case EINPUT_METHOD_HIGHLIGHT: {
                        sb.append(this.imHighlight);
                        break;
                    }
                    case EINPUT_METHOD_UNDERLINE: {
                        sb.append(this.imUnderline);
                        break;
                    }
                    case ESWAP_COLORS: {
                        sb.append(this.swapColors);
                        break;
                    }
                    case ENUMERIC_SHAPING: {
                        sb.append(this.numericShaping);
                        break;
                    }
                    case EKERNING: {
                        sb.append(this.kerning);
                        break;
                    }
                    case ELIGATURES: {
                        sb.append(this.ligatures);
                        break;
                    }
                    case ETRACKING: {
                        sb.append(this.tracking);
                        break;
                    }
                    default: {
                        throw new InternalError();
                    }
                }
                if ((this.nondefault & eAttribute.mask) == 0x0) {
                    sb.append('*');
                }
            }
            ++n;
        }
        sb.append("[btx=" + this.baselineTransform + ", ctx=" + this.charTransform + "]");
        sb.append('}');
        return sb.toString();
    }
    
    private static boolean equals(final Object o, final Object o2) {
        return (o == null) ? (o2 == null) : o.equals(o2);
    }
    
    private void update(final EAttribute default1) {
        this.defined |= default1.mask;
        if (this.i_validate(default1)) {
            if (this.i_equals(default1, AttributeValues.DEFAULT)) {
                this.nondefault &= ~default1.mask;
            }
            else {
                this.nondefault |= default1.mask;
            }
        }
        else {
            this.setDefault(default1);
        }
    }
    
    private void i_set(final EAttribute eAttribute, final AttributeValues attributeValues) {
        switch (eAttribute) {
            case EFAMILY: {
                this.family = attributeValues.family;
                break;
            }
            case EWEIGHT: {
                this.weight = attributeValues.weight;
                break;
            }
            case EWIDTH: {
                this.width = attributeValues.width;
                break;
            }
            case EPOSTURE: {
                this.posture = attributeValues.posture;
                break;
            }
            case ESIZE: {
                this.size = attributeValues.size;
                break;
            }
            case ETRANSFORM: {
                this.transform = attributeValues.transform;
                this.updateDerivedTransforms();
                break;
            }
            case ESUPERSCRIPT: {
                this.superscript = attributeValues.superscript;
                break;
            }
            case EFONT: {
                this.font = attributeValues.font;
                break;
            }
            case ECHAR_REPLACEMENT: {
                this.charReplacement = attributeValues.charReplacement;
                break;
            }
            case EFOREGROUND: {
                this.foreground = attributeValues.foreground;
                break;
            }
            case EBACKGROUND: {
                this.background = attributeValues.background;
                break;
            }
            case EUNDERLINE: {
                this.underline = attributeValues.underline;
                break;
            }
            case ESTRIKETHROUGH: {
                this.strikethrough = attributeValues.strikethrough;
                break;
            }
            case ERUN_DIRECTION: {
                this.runDirection = attributeValues.runDirection;
                break;
            }
            case EBIDI_EMBEDDING: {
                this.bidiEmbedding = attributeValues.bidiEmbedding;
                break;
            }
            case EJUSTIFICATION: {
                this.justification = attributeValues.justification;
                break;
            }
            case EINPUT_METHOD_HIGHLIGHT: {
                this.imHighlight = attributeValues.imHighlight;
                break;
            }
            case EINPUT_METHOD_UNDERLINE: {
                this.imUnderline = attributeValues.imUnderline;
                break;
            }
            case ESWAP_COLORS: {
                this.swapColors = attributeValues.swapColors;
                break;
            }
            case ENUMERIC_SHAPING: {
                this.numericShaping = attributeValues.numericShaping;
                break;
            }
            case EKERNING: {
                this.kerning = attributeValues.kerning;
                break;
            }
            case ELIGATURES: {
                this.ligatures = attributeValues.ligatures;
                break;
            }
            case ETRACKING: {
                this.tracking = attributeValues.tracking;
                break;
            }
            default: {
                throw new InternalError();
            }
        }
    }
    
    private boolean i_equals(final EAttribute eAttribute, final AttributeValues attributeValues) {
        switch (eAttribute) {
            case EFAMILY: {
                return equals(this.family, attributeValues.family);
            }
            case EWEIGHT: {
                return this.weight == attributeValues.weight;
            }
            case EWIDTH: {
                return this.width == attributeValues.width;
            }
            case EPOSTURE: {
                return this.posture == attributeValues.posture;
            }
            case ESIZE: {
                return this.size == attributeValues.size;
            }
            case ETRANSFORM: {
                return equals(this.transform, attributeValues.transform);
            }
            case ESUPERSCRIPT: {
                return this.superscript == attributeValues.superscript;
            }
            case EFONT: {
                return equals(this.font, attributeValues.font);
            }
            case ECHAR_REPLACEMENT: {
                return equals(this.charReplacement, attributeValues.charReplacement);
            }
            case EFOREGROUND: {
                return equals(this.foreground, attributeValues.foreground);
            }
            case EBACKGROUND: {
                return equals(this.background, attributeValues.background);
            }
            case EUNDERLINE: {
                return this.underline == attributeValues.underline;
            }
            case ESTRIKETHROUGH: {
                return this.strikethrough == attributeValues.strikethrough;
            }
            case ERUN_DIRECTION: {
                return this.runDirection == attributeValues.runDirection;
            }
            case EBIDI_EMBEDDING: {
                return this.bidiEmbedding == attributeValues.bidiEmbedding;
            }
            case EJUSTIFICATION: {
                return this.justification == attributeValues.justification;
            }
            case EINPUT_METHOD_HIGHLIGHT: {
                return equals(this.imHighlight, attributeValues.imHighlight);
            }
            case EINPUT_METHOD_UNDERLINE: {
                return this.imUnderline == attributeValues.imUnderline;
            }
            case ESWAP_COLORS: {
                return this.swapColors == attributeValues.swapColors;
            }
            case ENUMERIC_SHAPING: {
                return equals(this.numericShaping, attributeValues.numericShaping);
            }
            case EKERNING: {
                return this.kerning == attributeValues.kerning;
            }
            case ELIGATURES: {
                return this.ligatures == attributeValues.ligatures;
            }
            case ETRACKING: {
                return this.tracking == attributeValues.tracking;
            }
            default: {
                throw new InternalError();
            }
        }
    }
    
    private void i_set(final EAttribute eAttribute, final Object o) {
        switch (eAttribute) {
            case EFAMILY: {
                this.family = ((String)o).trim();
                break;
            }
            case EWEIGHT: {
                this.weight = ((Number)o).floatValue();
                break;
            }
            case EWIDTH: {
                this.width = ((Number)o).floatValue();
                break;
            }
            case EPOSTURE: {
                this.posture = ((Number)o).floatValue();
                break;
            }
            case ESIZE: {
                this.size = ((Number)o).floatValue();
                break;
            }
            case ETRANSFORM: {
                if (o instanceof TransformAttribute) {
                    final TransformAttribute transformAttribute = (TransformAttribute)o;
                    if (transformAttribute.isIdentity()) {
                        this.transform = null;
                    }
                    else {
                        this.transform = transformAttribute.getTransform();
                    }
                }
                else {
                    this.transform = new AffineTransform((AffineTransform)o);
                }
                this.updateDerivedTransforms();
                break;
            }
            case ESUPERSCRIPT: {
                this.superscript = (byte)(int)o;
                break;
            }
            case EFONT: {
                this.font = (Font)o;
                break;
            }
            case ECHAR_REPLACEMENT: {
                this.charReplacement = (GraphicAttribute)o;
                break;
            }
            case EFOREGROUND: {
                this.foreground = (Paint)o;
                break;
            }
            case EBACKGROUND: {
                this.background = (Paint)o;
                break;
            }
            case EUNDERLINE: {
                this.underline = (byte)(int)o;
                break;
            }
            case ESTRIKETHROUGH: {
                this.strikethrough = (boolean)o;
                break;
            }
            case ERUN_DIRECTION: {
                if (o instanceof Boolean) {
                    this.runDirection = (byte)(TextAttribute.RUN_DIRECTION_LTR.equals(o) ? 0 : 1);
                    break;
                }
                this.runDirection = (byte)(int)o;
                break;
            }
            case EBIDI_EMBEDDING: {
                this.bidiEmbedding = (byte)(int)o;
                break;
            }
            case EJUSTIFICATION: {
                this.justification = ((Number)o).floatValue();
                break;
            }
            case EINPUT_METHOD_HIGHLIGHT: {
                if (o instanceof Annotation) {
                    this.imHighlight = ((Annotation)o).getValue();
                    break;
                }
                this.imHighlight = o;
                break;
            }
            case EINPUT_METHOD_UNDERLINE: {
                this.imUnderline = (byte)(int)o;
                break;
            }
            case ESWAP_COLORS: {
                this.swapColors = (boolean)o;
                break;
            }
            case ENUMERIC_SHAPING: {
                this.numericShaping = (NumericShaper)o;
                break;
            }
            case EKERNING: {
                this.kerning = (byte)(int)o;
                break;
            }
            case ELIGATURES: {
                this.ligatures = (byte)(int)o;
                break;
            }
            case ETRACKING: {
                this.tracking = ((Number)o).floatValue();
                break;
            }
            default: {
                throw new InternalError();
            }
        }
    }
    
    private Object i_get(final EAttribute eAttribute) {
        switch (eAttribute) {
            case EFAMILY: {
                return this.family;
            }
            case EWEIGHT: {
                return this.weight;
            }
            case EWIDTH: {
                return this.width;
            }
            case EPOSTURE: {
                return this.posture;
            }
            case ESIZE: {
                return this.size;
            }
            case ETRANSFORM: {
                return (this.transform == null) ? TransformAttribute.IDENTITY : new TransformAttribute(this.transform);
            }
            case ESUPERSCRIPT: {
                return this.superscript;
            }
            case EFONT: {
                return this.font;
            }
            case ECHAR_REPLACEMENT: {
                return this.charReplacement;
            }
            case EFOREGROUND: {
                return this.foreground;
            }
            case EBACKGROUND: {
                return this.background;
            }
            case EUNDERLINE: {
                return this.underline;
            }
            case ESTRIKETHROUGH: {
                return this.strikethrough;
            }
            case ERUN_DIRECTION: {
                switch (this.runDirection) {
                    case 0: {
                        return TextAttribute.RUN_DIRECTION_LTR;
                    }
                    case 1: {
                        return TextAttribute.RUN_DIRECTION_RTL;
                    }
                    default: {
                        return null;
                    }
                }
                break;
            }
            case EBIDI_EMBEDDING: {
                return this.bidiEmbedding;
            }
            case EJUSTIFICATION: {
                return this.justification;
            }
            case EINPUT_METHOD_HIGHLIGHT: {
                return this.imHighlight;
            }
            case EINPUT_METHOD_UNDERLINE: {
                return this.imUnderline;
            }
            case ESWAP_COLORS: {
                return this.swapColors;
            }
            case ENUMERIC_SHAPING: {
                return this.numericShaping;
            }
            case EKERNING: {
                return this.kerning;
            }
            case ELIGATURES: {
                return this.ligatures;
            }
            case ETRACKING: {
                return this.tracking;
            }
            default: {
                throw new InternalError();
            }
        }
    }
    
    private boolean i_validate(final EAttribute eAttribute) {
        switch (eAttribute) {
            case EFAMILY: {
                if (this.family == null || this.family.length() == 0) {
                    this.family = AttributeValues.DEFAULT.family;
                }
                return true;
            }
            case EWEIGHT: {
                return this.weight > 0.0f && this.weight < 10.0f;
            }
            case EWIDTH: {
                return this.width >= 0.5f && this.width < 10.0f;
            }
            case EPOSTURE: {
                return this.posture >= -1.0f && this.posture <= 1.0f;
            }
            case ESIZE: {
                return this.size >= 0.0f;
            }
            case ETRANSFORM: {
                if (this.transform != null && this.transform.isIdentity()) {
                    this.transform = AttributeValues.DEFAULT.transform;
                }
                return true;
            }
            case ESUPERSCRIPT: {
                return this.superscript >= -7 && this.superscript <= 7;
            }
            case EFONT: {
                return true;
            }
            case ECHAR_REPLACEMENT: {
                return true;
            }
            case EFOREGROUND: {
                return true;
            }
            case EBACKGROUND: {
                return true;
            }
            case EUNDERLINE: {
                return this.underline >= -1 && this.underline < 6;
            }
            case ESTRIKETHROUGH: {
                return true;
            }
            case ERUN_DIRECTION: {
                return this.runDirection >= -2 && this.runDirection <= 1;
            }
            case EBIDI_EMBEDDING: {
                return this.bidiEmbedding >= -61 && this.bidiEmbedding < 62;
            }
            case EJUSTIFICATION: {
                this.justification = Math.max(0.0f, Math.min(this.justification, 1.0f));
                return true;
            }
            case EINPUT_METHOD_HIGHLIGHT: {
                return true;
            }
            case EINPUT_METHOD_UNDERLINE: {
                return this.imUnderline >= -1 && this.imUnderline < 6;
            }
            case ESWAP_COLORS: {
                return true;
            }
            case ENUMERIC_SHAPING: {
                return true;
            }
            case EKERNING: {
                return this.kerning >= 0 && this.kerning <= 1;
            }
            case ELIGATURES: {
                return this.ligatures >= 0 && this.ligatures <= 1;
            }
            case ETRACKING: {
                return this.tracking >= -1.0f && this.tracking <= 10.0f;
            }
            default: {
                throw new InternalError("unknown attribute: " + eAttribute);
            }
        }
    }
    
    public static float getJustification(final Map<?, ?> map) {
        if (map != null) {
            if (map instanceof AttributeMap && ((AttributeMap)map).getValues() != null) {
                return ((AttributeMap)map).getValues().justification;
            }
            final Number value = map.get(TextAttribute.JUSTIFICATION);
            if (value != null && value instanceof Number) {
                return Math.max(0.0f, Math.min(1.0f, value.floatValue()));
            }
        }
        return AttributeValues.DEFAULT.justification;
    }
    
    public static NumericShaper getNumericShaping(final Map<?, ?> map) {
        if (map != null) {
            if (map instanceof AttributeMap && ((AttributeMap)map).getValues() != null) {
                return ((AttributeMap)map).getValues().numericShaping;
            }
            final NumericShaper value = map.get(TextAttribute.NUMERIC_SHAPING);
            if (value != null && value instanceof NumericShaper) {
                return value;
            }
        }
        return AttributeValues.DEFAULT.numericShaping;
    }
    
    public AttributeValues applyIMHighlight() {
        if (this.imHighlight != null) {
            InputMethodHighlight inputMethodHighlight;
            if (this.imHighlight instanceof InputMethodHighlight) {
                inputMethodHighlight = (InputMethodHighlight)this.imHighlight;
            }
            else {
                inputMethodHighlight = (InputMethodHighlight)((Annotation)this.imHighlight).getValue();
            }
            Map<TextAttribute, ?> map = inputMethodHighlight.getStyle();
            if (map == null) {
                map = Toolkit.getDefaultToolkit().mapInputMethodHighlight(inputMethodHighlight);
            }
            if (map != null) {
                return this.clone().merge(map);
            }
        }
        return this;
    }
    
    public static AffineTransform getBaselineTransform(final Map<?, ?> map) {
        if (map != null) {
            AttributeValues attributeValues = null;
            if (map instanceof AttributeMap && ((AttributeMap)map).getValues() != null) {
                attributeValues = ((AttributeMap)map).getValues();
            }
            else if (map.get(TextAttribute.TRANSFORM) != null) {
                attributeValues = fromMap((Map<? extends AttributedCharacterIterator.Attribute, ?>)map);
            }
            if (attributeValues != null) {
                return attributeValues.baselineTransform;
            }
        }
        return null;
    }
    
    public static AffineTransform getCharTransform(final Map<?, ?> map) {
        if (map != null) {
            AttributeValues attributeValues = null;
            if (map instanceof AttributeMap && ((AttributeMap)map).getValues() != null) {
                attributeValues = ((AttributeMap)map).getValues();
            }
            else if (map.get(TextAttribute.TRANSFORM) != null) {
                attributeValues = fromMap((Map<? extends AttributedCharacterIterator.Attribute, ?>)map);
            }
            if (attributeValues != null) {
                return attributeValues.charTransform;
            }
        }
        return null;
    }
    
    public void updateDerivedTransforms() {
        if (this.transform == null) {
            this.baselineTransform = null;
            this.charTransform = null;
        }
        else {
            this.charTransform = new AffineTransform(this.transform);
            this.baselineTransform = extractXRotation(this.charTransform, true);
            if (this.charTransform.isIdentity()) {
                this.charTransform = null;
            }
            if (this.baselineTransform.isIdentity()) {
                this.baselineTransform = null;
            }
        }
        if (this.baselineTransform == null) {
            this.nondefault &= ~EAttribute.EBASELINE_TRANSFORM.mask;
        }
        else {
            this.nondefault |= EAttribute.EBASELINE_TRANSFORM.mask;
        }
    }
    
    public static AffineTransform extractXRotation(final AffineTransform affineTransform, final boolean b) {
        return extractRotation(new Point2D.Double(1.0, 0.0), affineTransform, b);
    }
    
    public static AffineTransform extractYRotation(final AffineTransform affineTransform, final boolean b) {
        return extractRotation(new Point2D.Double(0.0, 1.0), affineTransform, b);
    }
    
    private static AffineTransform extractRotation(final Point2D.Double double1, final AffineTransform affineTransform, final boolean b) {
        affineTransform.deltaTransform(double1, double1);
        final AffineTransform rotateInstance = AffineTransform.getRotateInstance(double1.x, double1.y);
        try {
            final AffineTransform inverse = rotateInstance.createInverse();
            final double translateX = affineTransform.getTranslateX();
            final double translateY = affineTransform.getTranslateY();
            affineTransform.preConcatenate(inverse);
            if (b && (translateX != 0.0 || translateY != 0.0)) {
                affineTransform.setTransform(affineTransform.getScaleX(), affineTransform.getShearY(), affineTransform.getShearX(), affineTransform.getScaleY(), 0.0, 0.0);
                rotateInstance.setTransform(rotateInstance.getScaleX(), rotateInstance.getShearY(), rotateInstance.getShearX(), rotateInstance.getScaleY(), translateX, translateY);
            }
        }
        catch (final NoninvertibleTransformException ex) {
            return null;
        }
        return rotateInstance;
    }
    
    static {
        DEFAULT = new AttributeValues();
        MASK_ALL = getMask((EAttribute[])EAttribute.class.getEnumConstants());
    }
}
