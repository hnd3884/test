package org.glassfish.jersey.internal.guava;

public final class MoreObjects
{
    private MoreObjects() {
    }
    
    public static ToStringHelper toStringHelper(final Object self) {
        return new ToStringHelper(simpleName(self.getClass()));
    }
    
    private static String simpleName(final Class<?> clazz) {
        String name = clazz.getName();
        name = name.replaceAll("\\$[0-9]+", "\\$");
        int start = name.lastIndexOf(36);
        if (start == -1) {
            start = name.lastIndexOf(46);
        }
        return name.substring(start + 1);
    }
    
    public static final class ToStringHelper
    {
        private final String className;
        private final ValueHolder holderHead;
        private ValueHolder holderTail;
        private final boolean omitNullValues = false;
        
        private ToStringHelper(final String className) {
            this.holderHead = new ValueHolder();
            this.holderTail = this.holderHead;
            this.className = Preconditions.checkNotNull(className);
        }
        
        public ToStringHelper add(final String name, final Object value) {
            return this.addHolder(name, value);
        }
        
        public ToStringHelper add(final String name, final int value) {
            return this.addHolder(name, String.valueOf(value));
        }
        
        public ToStringHelper add(final String name, final long value) {
            return this.addHolder(name, String.valueOf(value));
        }
        
        @Override
        public String toString() {
            String nextSeparator = "";
            final StringBuilder builder = new StringBuilder(32).append(this.className).append('{');
            for (ValueHolder valueHolder = this.holderHead.next; valueHolder != null; valueHolder = valueHolder.next) {
                builder.append(nextSeparator);
                nextSeparator = ", ";
                if (valueHolder.name != null) {
                    builder.append(valueHolder.name).append('=');
                }
                builder.append(valueHolder.value);
            }
            return builder.append('}').toString();
        }
        
        private ValueHolder addHolder() {
            final ValueHolder valueHolder = new ValueHolder();
            final ValueHolder holderTail = this.holderTail;
            final ValueHolder valueHolder2 = valueHolder;
            holderTail.next = valueHolder2;
            this.holderTail = valueHolder2;
            return valueHolder;
        }
        
        private ToStringHelper addHolder(final String name, final Object value) {
            final ValueHolder valueHolder = this.addHolder();
            valueHolder.value = value;
            valueHolder.name = Preconditions.checkNotNull(name);
            return this;
        }
        
        private static final class ValueHolder
        {
            String name;
            Object value;
            ValueHolder next;
        }
    }
}
