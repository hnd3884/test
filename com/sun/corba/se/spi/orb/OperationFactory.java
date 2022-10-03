package com.sun.corba.se.spi.orb;

import java.util.Arrays;
import java.lang.reflect.Array;
import java.util.StringTokenizer;
import java.net.MalformedURLException;
import java.net.URL;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import sun.corba.SharedSecrets;

public abstract class OperationFactory
{
    private static Operation suffixActionImpl;
    private static Operation valueActionImpl;
    private static Operation identityActionImpl;
    private static Operation booleanActionImpl;
    private static Operation integerActionImpl;
    private static Operation stringActionImpl;
    private static Operation classActionImpl;
    private static Operation setFlagActionImpl;
    private static Operation URLActionImpl;
    private static Operation convertIntegerToShortImpl;
    
    private OperationFactory() {
    }
    
    private static String getString(final Object o) {
        if (o instanceof String) {
            return (String)o;
        }
        throw new Error("String expected");
    }
    
    private static Object[] getObjectArray(final Object o) {
        if (o instanceof Object[]) {
            return (Object[])o;
        }
        throw new Error("Object[] expected");
    }
    
    private static StringPair getStringPair(final Object o) {
        if (o instanceof StringPair) {
            return (StringPair)o;
        }
        throw new Error("StringPair expected");
    }
    
    public static Operation maskErrorAction(final Operation operation) {
        return new MaskErrorAction(operation);
    }
    
    public static Operation indexAction(final int n) {
        return new IndexAction(n);
    }
    
    public static Operation identityAction() {
        return OperationFactory.identityActionImpl;
    }
    
    public static Operation suffixAction() {
        return OperationFactory.suffixActionImpl;
    }
    
    public static Operation valueAction() {
        return OperationFactory.valueActionImpl;
    }
    
    public static Operation booleanAction() {
        return OperationFactory.booleanActionImpl;
    }
    
    public static Operation integerAction() {
        return OperationFactory.integerActionImpl;
    }
    
    public static Operation stringAction() {
        return OperationFactory.stringActionImpl;
    }
    
    public static Operation classAction() {
        return OperationFactory.classActionImpl;
    }
    
    public static Operation setFlagAction() {
        return OperationFactory.setFlagActionImpl;
    }
    
    public static Operation URLAction() {
        return OperationFactory.URLActionImpl;
    }
    
    public static Operation integerRangeAction(final int n, final int n2) {
        return new IntegerRangeAction(n, n2);
    }
    
    public static Operation listAction(final String s, final Operation operation) {
        return new ListAction(s, operation);
    }
    
    public static Operation sequenceAction(final String s, final Operation[] array) {
        return new SequenceAction(s, array);
    }
    
    public static Operation compose(final Operation operation, final Operation operation2) {
        return new ComposeAction(operation, operation2);
    }
    
    public static Operation mapAction(final Operation operation) {
        return new MapAction(operation);
    }
    
    public static Operation mapSequenceAction(final Operation[] array) {
        return new MapSequenceAction(array);
    }
    
    public static Operation convertIntegerToShort() {
        return OperationFactory.convertIntegerToShortImpl;
    }
    
    static {
        OperationFactory.suffixActionImpl = new SuffixAction();
        OperationFactory.valueActionImpl = new ValueAction();
        OperationFactory.identityActionImpl = new IdentityAction();
        OperationFactory.booleanActionImpl = new BooleanAction();
        OperationFactory.integerActionImpl = new IntegerAction();
        OperationFactory.stringActionImpl = new StringAction();
        OperationFactory.classActionImpl = new ClassAction();
        OperationFactory.setFlagActionImpl = new SetFlagAction();
        OperationFactory.URLActionImpl = new URLAction();
        OperationFactory.convertIntegerToShortImpl = new ConvertIntegerToShort();
    }
    
    private abstract static class OperationBase implements Operation
    {
        @Override
        public boolean equals(final Object o) {
            return this == o || (o instanceof OperationBase && this.toString().equals(o.toString()));
        }
        
        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }
    }
    
    private static class MaskErrorAction extends OperationBase
    {
        private Operation op;
        
        public MaskErrorAction(final Operation op) {
            this.op = op;
        }
        
        @Override
        public Object operate(final Object o) {
            try {
                return this.op.operate(o);
            }
            catch (final Exception ex) {
                return null;
            }
        }
        
        @Override
        public String toString() {
            return "maskErrorAction(" + this.op + ")";
        }
    }
    
    private static class IndexAction extends OperationBase
    {
        private int index;
        
        public IndexAction(final int index) {
            this.index = index;
        }
        
        @Override
        public Object operate(final Object o) {
            return getObjectArray(o)[this.index];
        }
        
        @Override
        public String toString() {
            return "indexAction(" + this.index + ")";
        }
    }
    
    private static class SuffixAction extends OperationBase
    {
        @Override
        public Object operate(final Object o) {
            return getStringPair(o).getFirst();
        }
        
        @Override
        public String toString() {
            return "suffixAction";
        }
    }
    
    private static class ValueAction extends OperationBase
    {
        @Override
        public Object operate(final Object o) {
            return getStringPair(o).getSecond();
        }
        
        @Override
        public String toString() {
            return "valueAction";
        }
    }
    
    private static class IdentityAction extends OperationBase
    {
        @Override
        public Object operate(final Object o) {
            return o;
        }
        
        @Override
        public String toString() {
            return "identityAction";
        }
    }
    
    private static class BooleanAction extends OperationBase
    {
        @Override
        public Object operate(final Object o) {
            return new Boolean(getString(o));
        }
        
        @Override
        public String toString() {
            return "booleanAction";
        }
    }
    
    private static class IntegerAction extends OperationBase
    {
        @Override
        public Object operate(final Object o) {
            return new Integer(getString(o));
        }
        
        @Override
        public String toString() {
            return "integerAction";
        }
    }
    
    private static class StringAction extends OperationBase
    {
        @Override
        public Object operate(final Object o) {
            return o;
        }
        
        @Override
        public String toString() {
            return "stringAction";
        }
    }
    
    private static class ClassAction extends OperationBase
    {
        @Override
        public Object operate(final Object o) {
            final String access$600 = getString(o);
            try {
                return SharedSecrets.getJavaCorbaAccess().loadClass(access$600);
            }
            catch (final Exception ex) {
                throw ORBUtilSystemException.get("orb.lifecycle").couldNotLoadClass(ex, access$600);
            }
        }
        
        @Override
        public String toString() {
            return "classAction";
        }
    }
    
    private static class SetFlagAction extends OperationBase
    {
        @Override
        public Object operate(final Object o) {
            return Boolean.TRUE;
        }
        
        @Override
        public String toString() {
            return "setFlagAction";
        }
    }
    
    private static class URLAction extends OperationBase
    {
        @Override
        public Object operate(final Object o) {
            final String s = (String)o;
            try {
                return new URL(s);
            }
            catch (final MalformedURLException ex) {
                throw ORBUtilSystemException.get("orb.lifecycle").badUrl(ex, s);
            }
        }
        
        @Override
        public String toString() {
            return "URLAction";
        }
    }
    
    private static class IntegerRangeAction extends OperationBase
    {
        private int min;
        private int max;
        
        IntegerRangeAction(final int min, final int max) {
            this.min = min;
            this.max = max;
        }
        
        @Override
        public Object operate(final Object o) {
            final int int1 = Integer.parseInt(getString(o));
            if (int1 >= this.min && int1 <= this.max) {
                return new Integer(int1);
            }
            throw new IllegalArgumentException("Property value " + int1 + " is not in the range " + this.min + " to " + this.max);
        }
        
        @Override
        public String toString() {
            return "integerRangeAction(" + this.min + "," + this.max + ")";
        }
    }
    
    private static class ListAction extends OperationBase
    {
        private String sep;
        private Operation act;
        
        ListAction(final String sep, final Operation act) {
            this.sep = sep;
            this.act = act;
        }
        
        @Override
        public Object operate(final Object o) {
            final StringTokenizer stringTokenizer = new StringTokenizer(getString(o), this.sep);
            final int countTokens = stringTokenizer.countTokens();
            Object instance = null;
            int n = 0;
            while (stringTokenizer.hasMoreTokens()) {
                final Object operate = this.act.operate(stringTokenizer.nextToken());
                if (instance == null) {
                    instance = Array.newInstance(operate.getClass(), countTokens);
                }
                Array.set(instance, n++, operate);
            }
            return instance;
        }
        
        @Override
        public String toString() {
            return "listAction(separator=\"" + this.sep + "\",action=" + this.act + ")";
        }
    }
    
    private static class SequenceAction extends OperationBase
    {
        private String sep;
        private Operation[] actions;
        
        SequenceAction(final String sep, final Operation[] actions) {
            this.sep = sep;
            this.actions = actions;
        }
        
        @Override
        public Object operate(final Object o) {
            final StringTokenizer stringTokenizer = new StringTokenizer(getString(o), this.sep);
            final int countTokens = stringTokenizer.countTokens();
            if (countTokens != this.actions.length) {
                throw new Error("Number of tokens and number of actions do not match");
            }
            int n = 0;
            final Object[] array = new Object[countTokens];
            while (stringTokenizer.hasMoreTokens()) {
                array[n++] = this.actions[n].operate(stringTokenizer.nextToken());
            }
            return array;
        }
        
        @Override
        public String toString() {
            return "sequenceAction(separator=\"" + this.sep + "\",actions=" + Arrays.toString(this.actions) + ")";
        }
    }
    
    private static class ComposeAction extends OperationBase
    {
        private Operation op1;
        private Operation op2;
        
        ComposeAction(final Operation op1, final Operation op2) {
            this.op1 = op1;
            this.op2 = op2;
        }
        
        @Override
        public Object operate(final Object o) {
            return this.op2.operate(this.op1.operate(o));
        }
        
        @Override
        public String toString() {
            return "composition(" + this.op1 + "," + this.op2 + ")";
        }
    }
    
    private static class MapAction extends OperationBase
    {
        Operation op;
        
        MapAction(final Operation op) {
            this.op = op;
        }
        
        @Override
        public Object operate(final Object o) {
            final Object[] array = (Object[])o;
            final Object[] array2 = new Object[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = this.op.operate(array[i]);
            }
            return array2;
        }
        
        @Override
        public String toString() {
            return "mapAction(" + this.op + ")";
        }
    }
    
    private static class MapSequenceAction extends OperationBase
    {
        private Operation[] op;
        
        public MapSequenceAction(final Operation[] op) {
            this.op = op;
        }
        
        @Override
        public Object operate(final Object o) {
            final Object[] array = (Object[])o;
            final Object[] array2 = new Object[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = this.op[i].operate(array[i]);
            }
            return array2;
        }
        
        @Override
        public String toString() {
            return "mapSequenceAction(" + Arrays.toString(this.op) + ")";
        }
    }
    
    private static class ConvertIntegerToShort extends OperationBase
    {
        @Override
        public Object operate(final Object o) {
            return new Short(((Integer)o).shortValue());
        }
        
        @Override
        public String toString() {
            return "ConvertIntegerToShort";
        }
    }
}
