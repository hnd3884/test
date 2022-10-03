package com.adventnet.persistence;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import com.adventnet.ds.query.Criteria;
import java.io.Serializable;

public class ActionInfo implements Serializable, Cloneable
{
    private static final long serialVersionUID = -7801659079333907751L;
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    public static final int ON_DELETE_CASCADE = 4;
    private int operation;
    private Row value;
    private Criteria condition;
    private int cause;
    
    public ActionInfo(final int operation, final Row value) {
        this(operation, value, null);
    }
    
    public ActionInfo(final int operation, final Row value, final Criteria condition) {
        this.cause = -1;
        this.setOperation(operation);
        this.setValue(value);
        this.setCondition(condition);
    }
    
    public ActionInfo(final int operation, final Row value, final Criteria condition, final int cause) {
        this.cause = -1;
        this.setOperation(operation);
        this.setValue(value);
        this.setCondition(condition);
        this.setCause(cause);
    }
    
    public int getOperation() {
        return this.operation;
    }
    
    void setOperation(final int v) {
        this.operation = v;
    }
    
    public Row getValue() {
        return this.value;
    }
    
    void setValue(final Row v) {
        this.value = v;
    }
    
    public Criteria getCondition() {
        return this.condition;
    }
    
    public void setCondition(final Criteria v) {
        this.condition = v;
    }
    
    public int getCause() {
        return this.cause;
    }
    
    public void setCause(final int v) {
        this.cause = v;
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        final String oper = this.getOperationStr(this.operation);
        buff.append("<").append(oper);
        if (this.condition != null) {
            buff.append(" CRITERIA= \"").append(this.condition).append("\" ");
        }
        if (this.cause != -1) {
            buff.append(" CAUSE= \"").append(this.cause).append("\" ");
        }
        buff.append(">");
        buff.append(this.value.getPKValues());
        buff.append("</").append(oper).append(">");
        return buff.toString();
    }
    
    public Object clone() {
        return this.doClone(true);
    }
    
    public Object doClone(final boolean isRowValue) {
        ActionInfo info = null;
        try {
            info = (ActionInfo)super.clone();
            info.operation = this.operation;
            info.value = ((this.value == null) ? null : ((Row)this.value.doClone(isRowValue)));
            info.condition = ((this.condition == null) ? null : ((Criteria)this.condition.clone()));
        }
        catch (final CloneNotSupportedException ex) {}
        return info;
    }
    
    private String getOperationStr(final int oper) {
        String retStr = null;
        switch (oper) {
            case 1: {
                retStr = "INSERT";
                break;
            }
            case 2: {
                retStr = "UPDATE";
                break;
            }
            case 3: {
                retStr = "DELETE";
                break;
            }
            case 4: {
                retStr = "ON_DELETE_CASCADE";
                break;
            }
            default: {
                retStr = "Unknown Operation";
                break;
            }
        }
        return retStr;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ActionInfo)) {
            return false;
        }
        final ActionInfo action = (ActionInfo)obj;
        return this.operation == action.getOperation() && this.value.equals(action.getValue());
    }
    
    @Override
    public int hashCode() {
        return this.operation + this.value.hashCode();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.operation);
        out.writeObject(this.value);
        out.writeInt(this.cause);
        out.writeObject(this.condition);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.operation = in.readInt();
        this.value = (Row)in.readObject();
        this.cause = in.readInt();
        this.condition = (Criteria)in.readObject();
    }
}
