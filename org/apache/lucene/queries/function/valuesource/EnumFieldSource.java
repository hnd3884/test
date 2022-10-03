package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.mutable.MutableValueInt;
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;

public class EnumFieldSource extends FieldCacheSource
{
    static final Integer DEFAULT_VALUE;
    final Map<Integer, String> enumIntToStringMap;
    final Map<String, Integer> enumStringToIntMap;
    
    public EnumFieldSource(final String field, final Map<Integer, String> enumIntToStringMap, final Map<String, Integer> enumStringToIntMap) {
        super(field);
        this.enumIntToStringMap = enumIntToStringMap;
        this.enumStringToIntMap = enumStringToIntMap;
    }
    
    private static Integer tryParseInt(final String valueStr) {
        Integer intValue = null;
        try {
            intValue = Integer.parseInt(valueStr);
        }
        catch (final NumberFormatException ex) {}
        return intValue;
    }
    
    private String intValueToStringValue(final Integer intVal) {
        if (intVal == null) {
            return null;
        }
        final String enumString = this.enumIntToStringMap.get(intVal);
        if (enumString != null) {
            return enumString;
        }
        return EnumFieldSource.DEFAULT_VALUE.toString();
    }
    
    private Integer stringValueToIntValue(final String stringVal) {
        if (stringVal == null) {
            return null;
        }
        final Integer enumInt = this.enumStringToIntMap.get(stringVal);
        if (enumInt != null) {
            return enumInt;
        }
        Integer intValue = tryParseInt(stringVal);
        if (intValue == null) {
            intValue = EnumFieldSource.DEFAULT_VALUE;
        }
        final String enumString = this.enumIntToStringMap.get(intValue);
        if (enumString != null) {
            return intValue;
        }
        return EnumFieldSource.DEFAULT_VALUE;
    }
    
    @Override
    public String description() {
        return "enum(" + this.field + ')';
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final NumericDocValues arr = DocValues.getNumeric(readerContext.reader(), this.field);
        final Bits valid = DocValues.getDocsWithField(readerContext.reader(), this.field);
        return new IntDocValues(this) {
            final MutableValueInt val = new MutableValueInt();
            
            @Override
            public int intVal(final int doc) {
                return (int)arr.get(doc);
            }
            
            @Override
            public String strVal(final int doc) {
                final Integer intValue = this.intVal(doc);
                return EnumFieldSource.this.intValueToStringValue(intValue);
            }
            
            @Override
            public boolean exists(final int doc) {
                return valid.get(doc);
            }
            
            @Override
            public ValueSourceScorer getRangeScorer(final IndexReader reader, final String lowerVal, final String upperVal, final boolean includeLower, final boolean includeUpper) {
                Integer lower = EnumFieldSource.this.stringValueToIntValue(lowerVal);
                Integer upper = EnumFieldSource.this.stringValueToIntValue(upperVal);
                if (lower == null) {
                    lower = Integer.MIN_VALUE;
                }
                else if (!includeLower && lower < Integer.MAX_VALUE) {
                    ++lower;
                }
                if (upper == null) {
                    upper = Integer.MAX_VALUE;
                }
                else if (!includeUpper && upper > Integer.MIN_VALUE) {
                    --upper;
                }
                final int ll = lower;
                final int uu = upper;
                return new ValueSourceScorer(reader, this) {
                    @Override
                    public boolean matches(final int doc) {
                        final int val = IntDocValues.this.intVal(doc);
                        return val >= ll && val <= uu;
                    }
                };
            }
            
            @Override
            public ValueFiller getValueFiller() {
                return new ValueFiller() {
                    private final MutableValueInt mval = new MutableValueInt();
                    
                    @Override
                    public MutableValue getValue() {
                        return (MutableValue)this.mval;
                    }
                    
                    @Override
                    public void fillValue(final int doc) {
                        this.mval.value = IntDocValues.this.intVal(doc);
                        this.mval.exists = valid.get(doc);
                    }
                };
            }
        };
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final EnumFieldSource that = (EnumFieldSource)o;
        return this.enumIntToStringMap.equals(that.enumIntToStringMap) && this.enumStringToIntMap.equals(that.enumStringToIntMap);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.enumIntToStringMap.hashCode();
        result = 31 * result + this.enumStringToIntMap.hashCode();
        return result;
    }
    
    static {
        DEFAULT_VALUE = -1;
    }
}
