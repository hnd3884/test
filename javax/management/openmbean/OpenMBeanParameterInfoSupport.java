package javax.management.openmbean;

import javax.management.ImmutableDescriptor;
import javax.management.Descriptor;
import java.util.Set;
import javax.management.MBeanParameterInfo;

public class OpenMBeanParameterInfoSupport extends MBeanParameterInfo implements OpenMBeanParameterInfo
{
    static final long serialVersionUID = -7235016873758443122L;
    private OpenType<?> openType;
    private Object defaultValue;
    private Set<?> legalValues;
    private Comparable<?> minValue;
    private Comparable<?> maxValue;
    private transient Integer myHashCode;
    private transient String myToString;
    
    public OpenMBeanParameterInfoSupport(final String s, final String s2, final OpenType<?> openType) {
        this(s, s2, openType, null);
    }
    
    public OpenMBeanParameterInfoSupport(final String s, final String s2, final OpenType<?> openType, Descriptor descriptor) {
        super(s, (openType == null) ? null : openType.getClassName(), s2, ImmutableDescriptor.union(descriptor, (openType == null) ? null : openType.getDescriptor()));
        this.defaultValue = null;
        this.legalValues = null;
        this.minValue = null;
        this.maxValue = null;
        this.myHashCode = null;
        this.myToString = null;
        this.openType = openType;
        descriptor = this.getDescriptor();
        this.defaultValue = OpenMBeanAttributeInfoSupport.valueFrom(descriptor, "defaultValue", openType);
        this.legalValues = OpenMBeanAttributeInfoSupport.valuesFrom(descriptor, "legalValues", openType);
        this.minValue = OpenMBeanAttributeInfoSupport.comparableValueFrom(descriptor, "minValue", openType);
        this.maxValue = OpenMBeanAttributeInfoSupport.comparableValueFrom(descriptor, "maxValue", openType);
        try {
            OpenMBeanAttributeInfoSupport.check(this);
        }
        catch (final OpenDataException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
    
    public <T> OpenMBeanParameterInfoSupport(final String s, final String s2, final OpenType<T> openType, final T t) throws OpenDataException {
        this(s, s2, openType, t, null);
    }
    
    public <T> OpenMBeanParameterInfoSupport(final String s, final String s2, final OpenType<T> openType, final T t, final T[] array) throws OpenDataException {
        this(s, s2, openType, t, array, null, null);
    }
    
    public <T> OpenMBeanParameterInfoSupport(final String s, final String s2, final OpenType<T> openType, final T t, final Comparable<T> comparable, final Comparable<T> comparable2) throws OpenDataException {
        this(s, s2, openType, t, null, comparable, comparable2);
    }
    
    private <T> OpenMBeanParameterInfoSupport(final String s, final String s2, final OpenType<T> openType, final T defaultValue, final T[] array, final Comparable<T> minValue, final Comparable<T> maxValue) throws OpenDataException {
        super(s, (openType == null) ? null : openType.getClassName(), s2, OpenMBeanAttributeInfoSupport.makeDescriptor(openType, defaultValue, array, minValue, maxValue));
        this.defaultValue = null;
        this.legalValues = null;
        this.minValue = null;
        this.maxValue = null;
        this.myHashCode = null;
        this.myToString = null;
        this.openType = openType;
        final Descriptor descriptor = this.getDescriptor();
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.legalValues = (Set)descriptor.getFieldValue("legalValues");
        OpenMBeanAttributeInfoSupport.check(this);
    }
    
    private Object readResolve() {
        if (this.getDescriptor().getFieldNames().length == 0) {
            return new OpenMBeanParameterInfoSupport(this.name, this.description, this.openType, OpenMBeanAttributeInfoSupport.makeDescriptor(OpenMBeanAttributeInfoSupport.cast(this.openType), this.defaultValue, OpenMBeanAttributeInfoSupport.cast(this.legalValues), OpenMBeanAttributeInfoSupport.cast(this.minValue), OpenMBeanAttributeInfoSupport.cast(this.maxValue)));
        }
        return this;
    }
    
    @Override
    public OpenType<?> getOpenType() {
        return this.openType;
    }
    
    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    public Set<?> getLegalValues() {
        return this.legalValues;
    }
    
    @Override
    public Comparable<?> getMinValue() {
        return this.minValue;
    }
    
    @Override
    public Comparable<?> getMaxValue() {
        return this.maxValue;
    }
    
    @Override
    public boolean hasDefaultValue() {
        return this.defaultValue != null;
    }
    
    @Override
    public boolean hasLegalValues() {
        return this.legalValues != null;
    }
    
    @Override
    public boolean hasMinValue() {
        return this.minValue != null;
    }
    
    @Override
    public boolean hasMaxValue() {
        return this.maxValue != null;
    }
    
    @Override
    public boolean isValue(final Object o) {
        return OpenMBeanAttributeInfoSupport.isValue(this, o);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof OpenMBeanParameterInfo && OpenMBeanAttributeInfoSupport.equal(this, (OpenMBeanParameterInfo)o);
    }
    
    @Override
    public int hashCode() {
        if (this.myHashCode == null) {
            this.myHashCode = OpenMBeanAttributeInfoSupport.hashCode(this);
        }
        return this.myHashCode;
    }
    
    @Override
    public String toString() {
        if (this.myToString == null) {
            this.myToString = OpenMBeanAttributeInfoSupport.toString(this);
        }
        return this.myToString;
    }
}
