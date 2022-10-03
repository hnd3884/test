package eu.medsea.mimeutil.detector;

import java.util.HashMap;
import java.util.Map;

final class MagicMimeEntryOperation
{
    private static final Map operationID2operation;
    public static final MagicMimeEntryOperation EQUALS;
    public static final MagicMimeEntryOperation LESS_THAN;
    public static final MagicMimeEntryOperation GREATER_THAN;
    public static final MagicMimeEntryOperation AND;
    public static final MagicMimeEntryOperation CLEAR;
    public static final MagicMimeEntryOperation NEGATED;
    public static final MagicMimeEntryOperation ANY;
    public static final MagicMimeEntryOperation NOT_EQUALS;
    private final char operationID;
    
    static {
        operationID2operation = new HashMap();
        EQUALS = new MagicMimeEntryOperation('=');
        LESS_THAN = new MagicMimeEntryOperation('<');
        GREATER_THAN = new MagicMimeEntryOperation('>');
        AND = new MagicMimeEntryOperation('&');
        CLEAR = new MagicMimeEntryOperation('^');
        NEGATED = new MagicMimeEntryOperation('~');
        ANY = new MagicMimeEntryOperation('x');
        NOT_EQUALS = new MagicMimeEntryOperation('!');
    }
    
    public static MagicMimeEntryOperation getOperation(final char operationID) {
        final Character operationIDCharacter = new Character(operationID);
        return MagicMimeEntryOperation.operationID2operation.get(operationIDCharacter);
    }
    
    public static MagicMimeEntryOperation getOperationForStringField(final String content) {
        final MagicMimeEntryOperation operation = getOperation(content);
        if (MagicMimeEntryOperation.EQUALS.equals(operation) || MagicMimeEntryOperation.LESS_THAN.equals(operation) || MagicMimeEntryOperation.GREATER_THAN.equals(operation)) {
            return operation;
        }
        return MagicMimeEntryOperation.EQUALS;
    }
    
    public static MagicMimeEntryOperation getOperationForNumberField(final String content) {
        return getOperation(content);
    }
    
    private static MagicMimeEntryOperation getOperation(final String content) {
        if (content.length() == 0) {
            return MagicMimeEntryOperation.EQUALS;
        }
        final MagicMimeEntryOperation operation = getOperation(content.charAt(0));
        if (operation == null) {
            return MagicMimeEntryOperation.EQUALS;
        }
        return operation;
    }
    
    private static void registerOperation(final MagicMimeEntryOperation operation) {
        final Character operationIDCharacter = new Character(operation.getOperationID());
        if (MagicMimeEntryOperation.operationID2operation.containsKey(operationIDCharacter)) {
            throw new IllegalStateException("Duplicate registration of operation " + operationIDCharacter);
        }
        MagicMimeEntryOperation.operationID2operation.put(operationIDCharacter, operation);
    }
    
    MagicMimeEntryOperation(final char operationID) {
        this.operationID = operationID;
        registerOperation(this);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.operationID;
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MagicMimeEntryOperation other = (MagicMimeEntryOperation)obj;
        return this.operationID == other.operationID;
    }
    
    public final char getOperationID() {
        return this.operationID;
    }
    
    public String toString() {
        return String.valueOf(this.getClass().getName()) + '[' + this.operationID + ']';
    }
}
