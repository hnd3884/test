package javax.swing;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.Date;
import java.util.regex.Pattern;

public abstract class RowFilter<M, I>
{
    private static void checkIndices(final int[] array) {
        for (int i = array.length - 1; i >= 0; --i) {
            if (array[i] < 0) {
                throw new IllegalArgumentException("Index must be >= 0");
            }
        }
    }
    
    public static <M, I> RowFilter<M, I> regexFilter(final String s, final int... array) {
        return (RowFilter<M, I>)new RegexFilter(Pattern.compile(s), array);
    }
    
    public static <M, I> RowFilter<M, I> dateFilter(final ComparisonType comparisonType, final Date date, final int... array) {
        return (RowFilter<M, I>)new DateFilter(comparisonType, date.getTime(), array);
    }
    
    public static <M, I> RowFilter<M, I> numberFilter(final ComparisonType comparisonType, final Number n, final int... array) {
        return (RowFilter<M, I>)new NumberFilter(comparisonType, n, array);
    }
    
    public static <M, I> RowFilter<M, I> orFilter(final Iterable<? extends RowFilter<? super M, ? super I>> iterable) {
        return new OrFilter<M, I>(iterable);
    }
    
    public static <M, I> RowFilter<M, I> andFilter(final Iterable<? extends RowFilter<? super M, ? super I>> iterable) {
        return new AndFilter<M, I>(iterable);
    }
    
    public static <M, I> RowFilter<M, I> notFilter(final RowFilter<M, I> rowFilter) {
        return new NotFilter<M, I>(rowFilter);
    }
    
    public abstract boolean include(final Entry<? extends M, ? extends I> p0);
    
    public enum ComparisonType
    {
        BEFORE, 
        AFTER, 
        EQUAL, 
        NOT_EQUAL;
    }
    
    public abstract static class Entry<M, I>
    {
        public abstract M getModel();
        
        public abstract int getValueCount();
        
        public abstract Object getValue(final int p0);
        
        public String getStringValue(final int n) {
            final Object value = this.getValue(n);
            return (value == null) ? "" : value.toString();
        }
        
        public abstract I getIdentifier();
    }
    
    private abstract static class GeneralFilter extends RowFilter<Object, Object>
    {
        private int[] columns;
        
        GeneralFilter(final int[] columns) {
            checkIndices(columns);
            this.columns = columns;
        }
        
        @Override
        public boolean include(final Entry<?, ?> entry) {
            int valueCount = entry.getValueCount();
            if (this.columns.length > 0) {
                for (int i = this.columns.length - 1; i >= 0; --i) {
                    final int n = this.columns[i];
                    if (n < valueCount && this.include(entry, n)) {
                        return true;
                    }
                }
            }
            else {
                while (--valueCount >= 0) {
                    if (this.include(entry, valueCount)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        protected abstract boolean include(final Entry<?, ?> p0, final int p1);
    }
    
    private static class RegexFilter extends GeneralFilter
    {
        private Matcher matcher;
        
        RegexFilter(final Pattern pattern, final int[] array) {
            super(array);
            if (pattern == null) {
                throw new IllegalArgumentException("Pattern must be non-null");
            }
            this.matcher = pattern.matcher("");
        }
        
        @Override
        protected boolean include(final Entry<?, ?> entry, final int n) {
            this.matcher.reset(entry.getStringValue(n));
            return this.matcher.find();
        }
    }
    
    private static class DateFilter extends GeneralFilter
    {
        private long date;
        private ComparisonType type;
        
        DateFilter(final ComparisonType type, final long date, final int[] array) {
            super(array);
            if (type == null) {
                throw new IllegalArgumentException("type must be non-null");
            }
            this.type = type;
            this.date = date;
        }
        
        @Override
        protected boolean include(final Entry<?, ?> entry, final int n) {
            final Object value = entry.getValue(n);
            if (value instanceof Date) {
                final long time = ((Date)value).getTime();
                switch (this.type) {
                    case BEFORE: {
                        return time < this.date;
                    }
                    case AFTER: {
                        return time > this.date;
                    }
                    case EQUAL: {
                        return time == this.date;
                    }
                    case NOT_EQUAL: {
                        return time != this.date;
                    }
                }
            }
            return false;
        }
    }
    
    private static class NumberFilter extends GeneralFilter
    {
        private boolean isComparable;
        private Number number;
        private ComparisonType type;
        
        NumberFilter(final ComparisonType type, final Number number, final int[] array) {
            super(array);
            if (type == null || number == null) {
                throw new IllegalArgumentException("type and number must be non-null");
            }
            this.type = type;
            this.number = number;
            this.isComparable = (number instanceof Comparable);
        }
        
        @Override
        protected boolean include(final Entry<?, ?> entry, final int n) {
            final Object value = entry.getValue(n);
            if (value instanceof Number) {
                int n2;
                if (this.number.getClass() == ((Number)value).getClass() && this.isComparable) {
                    n2 = ((Comparable)this.number).compareTo(value);
                }
                else {
                    n2 = this.longCompare((Number)value);
                }
                switch (this.type) {
                    case BEFORE: {
                        return n2 > 0;
                    }
                    case AFTER: {
                        return n2 < 0;
                    }
                    case EQUAL: {
                        return n2 == 0;
                    }
                    case NOT_EQUAL: {
                        return n2 != 0;
                    }
                }
            }
            return false;
        }
        
        private int longCompare(final Number n) {
            final long n2 = this.number.longValue() - n.longValue();
            if (n2 < 0L) {
                return -1;
            }
            if (n2 > 0L) {
                return 1;
            }
            return 0;
        }
    }
    
    private static class OrFilter<M, I> extends RowFilter<M, I>
    {
        List<RowFilter<? super M, ? super I>> filters;
        
        OrFilter(final Iterable<? extends RowFilter<? super M, ? super I>> iterable) {
            this.filters = new ArrayList<RowFilter<? super M, ? super I>>();
            for (final RowFilter rowFilter : iterable) {
                if (rowFilter == null) {
                    throw new IllegalArgumentException("Filter must be non-null");
                }
                this.filters.add(rowFilter);
            }
        }
        
        @Override
        public boolean include(final Entry<? extends M, ? extends I> entry) {
            final Iterator<RowFilter<? super M, ? super I>> iterator = this.filters.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().include(entry)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    private static class AndFilter<M, I> extends OrFilter<M, I>
    {
        AndFilter(final Iterable<? extends RowFilter<? super M, ? super I>> iterable) {
            super(iterable);
        }
        
        @Override
        public boolean include(final Entry<? extends M, ? extends I> entry) {
            final Iterator<RowFilter<? super M, ? super I>> iterator = this.filters.iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().include(entry)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    private static class NotFilter<M, I> extends RowFilter<M, I>
    {
        private RowFilter<M, I> filter;
        
        NotFilter(final RowFilter<M, I> filter) {
            if (filter == null) {
                throw new IllegalArgumentException("filter must be non-null");
            }
            this.filter = filter;
        }
        
        @Override
        public boolean include(final Entry<? extends M, ? extends I> entry) {
            return !this.filter.include(entry);
        }
    }
}
