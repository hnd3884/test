package io.opencensus.trace;

import io.opencensus.internal.Utils;
import io.opencensus.common.Function;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class AttributeValue
{
    public static AttributeValue stringAttributeValue(final String stringValue) {
        return AttributeValueString.create(stringValue);
    }
    
    public static AttributeValue booleanAttributeValue(final boolean booleanValue) {
        return AttributeValueBoolean.create(booleanValue);
    }
    
    public static AttributeValue longAttributeValue(final long longValue) {
        return AttributeValueLong.create(longValue);
    }
    
    public static AttributeValue doubleAttributeValue(final double doubleValue) {
        return AttributeValueDouble.create(doubleValue);
    }
    
    AttributeValue() {
    }
    
    @Deprecated
    public abstract <T> T match(final Function<? super String, T> p0, final Function<? super Boolean, T> p1, final Function<? super Long, T> p2, final Function<Object, T> p3);
    
    public abstract <T> T match(final Function<? super String, T> p0, final Function<? super Boolean, T> p1, final Function<? super Long, T> p2, final Function<? super Double, T> p3, final Function<Object, T> p4);
    
    @Immutable
    abstract static class AttributeValueString extends AttributeValue
    {
        static AttributeValue create(final String stringValue) {
            return new AutoValue_AttributeValue_AttributeValueString(Utils.checkNotNull(stringValue, "stringValue"));
        }
        
        @Override
        public final <T> T match(final Function<? super String, T> stringFunction, final Function<? super Boolean, T> booleanFunction, final Function<? super Long, T> longFunction, final Function<Object, T> defaultFunction) {
            return stringFunction.apply(this.getStringValue());
        }
        
        @Override
        public final <T> T match(final Function<? super String, T> stringFunction, final Function<? super Boolean, T> booleanFunction, final Function<? super Long, T> longFunction, final Function<? super Double, T> doubleFunction, final Function<Object, T> defaultFunction) {
            return stringFunction.apply(this.getStringValue());
        }
        
        abstract String getStringValue();
    }
    
    @Immutable
    abstract static class AttributeValueBoolean extends AttributeValue
    {
        static AttributeValue create(final Boolean booleanValue) {
            return new AutoValue_AttributeValue_AttributeValueBoolean(Utils.checkNotNull(booleanValue, "booleanValue"));
        }
        
        @Override
        public final <T> T match(final Function<? super String, T> stringFunction, final Function<? super Boolean, T> booleanFunction, final Function<? super Long, T> longFunction, final Function<Object, T> defaultFunction) {
            return booleanFunction.apply(this.getBooleanValue());
        }
        
        @Override
        public final <T> T match(final Function<? super String, T> stringFunction, final Function<? super Boolean, T> booleanFunction, final Function<? super Long, T> longFunction, final Function<? super Double, T> doubleFunction, final Function<Object, T> defaultFunction) {
            return booleanFunction.apply(this.getBooleanValue());
        }
        
        abstract Boolean getBooleanValue();
    }
    
    @Immutable
    abstract static class AttributeValueLong extends AttributeValue
    {
        static AttributeValue create(final Long longValue) {
            return new AutoValue_AttributeValue_AttributeValueLong(Utils.checkNotNull(longValue, "longValue"));
        }
        
        @Override
        public final <T> T match(final Function<? super String, T> stringFunction, final Function<? super Boolean, T> booleanFunction, final Function<? super Long, T> longFunction, final Function<Object, T> defaultFunction) {
            return longFunction.apply(this.getLongValue());
        }
        
        @Override
        public final <T> T match(final Function<? super String, T> stringFunction, final Function<? super Boolean, T> booleanFunction, final Function<? super Long, T> longFunction, final Function<? super Double, T> doubleFunction, final Function<Object, T> defaultFunction) {
            return longFunction.apply(this.getLongValue());
        }
        
        abstract Long getLongValue();
    }
    
    @Immutable
    abstract static class AttributeValueDouble extends AttributeValue
    {
        static AttributeValue create(final Double doubleValue) {
            return new AutoValue_AttributeValue_AttributeValueDouble(Utils.checkNotNull(doubleValue, "doubleValue"));
        }
        
        @Override
        public final <T> T match(final Function<? super String, T> stringFunction, final Function<? super Boolean, T> booleanFunction, final Function<? super Long, T> longFunction, final Function<Object, T> defaultFunction) {
            return defaultFunction.apply(this.getDoubleValue());
        }
        
        @Override
        public final <T> T match(final Function<? super String, T> stringFunction, final Function<? super Boolean, T> booleanFunction, final Function<? super Long, T> longFunction, final Function<? super Double, T> doubleFunction, final Function<Object, T> defaultFunction) {
            return doubleFunction.apply(this.getDoubleValue());
        }
        
        abstract Double getDoubleValue();
    }
}
