package java.beans;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import com.sun.beans.finder.PersistenceDelegateFinder;

public class Encoder
{
    private final PersistenceDelegateFinder finder;
    private Map<Object, Expression> bindings;
    private ExceptionListener exceptionListener;
    boolean executeStatements;
    private Map<Object, Object> attributes;
    
    public Encoder() {
        this.finder = new PersistenceDelegateFinder();
        this.bindings = new IdentityHashMap<Object, Expression>();
        this.executeStatements = true;
    }
    
    protected void writeObject(final Object o) {
        if (o == this) {
            return;
        }
        this.getPersistenceDelegate((o == null) ? null : o.getClass()).writeObject(o, this);
    }
    
    public void setExceptionListener(final ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }
    
    public ExceptionListener getExceptionListener() {
        return (this.exceptionListener != null) ? this.exceptionListener : Statement.defaultExceptionListener;
    }
    
    Object getValue(final Expression expression) {
        try {
            return (expression == null) ? null : expression.getValue();
        }
        catch (final Exception ex) {
            this.getExceptionListener().exceptionThrown(ex);
            throw new RuntimeException("failed to evaluate: " + expression.toString());
        }
    }
    
    public PersistenceDelegate getPersistenceDelegate(final Class<?> clazz) {
        PersistenceDelegate persistenceDelegate = this.finder.find(clazz);
        if (persistenceDelegate == null) {
            persistenceDelegate = MetaData.getPersistenceDelegate(clazz);
            if (persistenceDelegate != null) {
                this.finder.register(clazz, persistenceDelegate);
            }
        }
        return persistenceDelegate;
    }
    
    public void setPersistenceDelegate(final Class<?> clazz, final PersistenceDelegate persistenceDelegate) {
        this.finder.register(clazz, persistenceDelegate);
    }
    
    public Object remove(final Object o) {
        return this.getValue(this.bindings.remove(o));
    }
    
    public Object get(final Object o) {
        if (o == null || o == this || o.getClass() == String.class) {
            return o;
        }
        return this.getValue(this.bindings.get(o));
    }
    
    private Object writeObject1(final Object o) {
        Object o2 = this.get(o);
        if (o2 == null) {
            this.writeObject(o);
            o2 = this.get(o);
        }
        return o2;
    }
    
    private Statement cloneStatement(final Statement statement) {
        final Object writeObject1 = this.writeObject1(statement.getTarget());
        final Object[] arguments = statement.getArguments();
        final Object[] array = new Object[arguments.length];
        for (int i = 0; i < arguments.length; ++i) {
            array[i] = this.writeObject1(arguments[i]);
        }
        final Statement statement2 = Statement.class.equals(statement.getClass()) ? new Statement(writeObject1, statement.getMethodName(), array) : new Expression(writeObject1, statement.getMethodName(), array);
        statement2.loader = statement.loader;
        return statement2;
    }
    
    public void writeStatement(final Statement statement) {
        final Statement cloneStatement = this.cloneStatement(statement);
        if (statement.getTarget() != this && this.executeStatements) {
            try {
                cloneStatement.execute();
            }
            catch (final Exception ex) {
                this.getExceptionListener().exceptionThrown(new Exception("Encoder: discarding statement " + cloneStatement, ex));
            }
        }
    }
    
    public void writeExpression(final Expression expression) {
        final Object value = this.getValue(expression);
        if (this.get(value) != null) {
            return;
        }
        this.bindings.put(value, (Expression)this.cloneStatement(expression));
        this.writeObject(value);
    }
    
    void clear() {
        this.bindings.clear();
    }
    
    void setAttribute(final Object o, final Object o2) {
        if (this.attributes == null) {
            this.attributes = new HashMap<Object, Object>();
        }
        this.attributes.put(o, o2);
    }
    
    Object getAttribute(final Object o) {
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.get(o);
    }
}
