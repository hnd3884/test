package net.sf.jsqlparser.statement.alter;

import java.util.Collections;
import net.sf.jsqlparser.statement.select.PlainSelect;
import java.util.ArrayList;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.Index;
import java.util.List;

public class AlterExpression
{
    private AlterOperation operation;
    private String columnName;
    private List<ColumnDataType> colDataTypeList;
    private List<String> pkColumns;
    private List<String> ukColumns;
    private String ukName;
    private Index index;
    private String constraintName;
    private boolean onDeleteRestrict;
    private boolean onDeleteSetNull;
    private boolean onDeleteCascade;
    private List<String> fkColumns;
    private String fkSourceTable;
    private List<String> fkSourceColumns;
    private List<ConstraintState> constraints;
    
    public AlterExpression() {
        this.index = null;
    }
    
    public AlterOperation getOperation() {
        return this.operation;
    }
    
    public void setOperation(final AlterOperation operation) {
        this.operation = operation;
    }
    
    public boolean isOnDeleteCascade() {
        return this.onDeleteCascade;
    }
    
    public void setOnDeleteCascade(final boolean onDeleteCascade) {
        this.onDeleteCascade = onDeleteCascade;
    }
    
    public boolean isOnDeleteRestrict() {
        return this.onDeleteRestrict;
    }
    
    public void setOnDeleteRestrict(final boolean onDeleteRestrict) {
        this.onDeleteRestrict = onDeleteRestrict;
    }
    
    public boolean isOnDeleteSetNull() {
        return this.onDeleteSetNull;
    }
    
    public void setOnDeleteSetNull(final boolean onDeleteSetNull) {
        this.onDeleteSetNull = onDeleteSetNull;
    }
    
    public List<String> getFkColumns() {
        return this.fkColumns;
    }
    
    public void setFkColumns(final List<String> fkColumns) {
        this.fkColumns = fkColumns;
    }
    
    public String getFkSourceTable() {
        return this.fkSourceTable;
    }
    
    public void setFkSourceTable(final String fkSourceTable) {
        this.fkSourceTable = fkSourceTable;
    }
    
    public List<ColumnDataType> getColDataTypeList() {
        return this.colDataTypeList;
    }
    
    public void addColDataType(final String columnName, final ColDataType colDataType) {
        this.addColDataType(new ColumnDataType(columnName, colDataType, null));
    }
    
    public void addColDataType(final ColumnDataType columnDataType) {
        if (this.colDataTypeList == null) {
            this.colDataTypeList = new ArrayList<ColumnDataType>();
        }
        this.colDataTypeList.add(columnDataType);
    }
    
    public List<String> getFkSourceColumns() {
        return this.fkSourceColumns;
    }
    
    public void setFkSourceColumns(final List<String> fkSourceColumns) {
        this.fkSourceColumns = fkSourceColumns;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public String getConstraintName() {
        return this.constraintName;
    }
    
    public void setConstraintName(final String constraintName) {
        this.constraintName = constraintName;
    }
    
    public List<String> getPkColumns() {
        return this.pkColumns;
    }
    
    public void setPkColumns(final List<String> pkColumns) {
        this.pkColumns = pkColumns;
    }
    
    public List<String> getUkColumns() {
        return this.ukColumns;
    }
    
    public void setUkColumns(final List<String> ukColumns) {
        this.ukColumns = ukColumns;
    }
    
    public String getUkName() {
        return this.ukName;
    }
    
    public void setUkName(final String ukName) {
        this.ukName = ukName;
    }
    
    public Index getIndex() {
        return this.index;
    }
    
    public void setIndex(final Index index) {
        this.index = index;
    }
    
    public List<ConstraintState> getConstraints() {
        return this.constraints;
    }
    
    public void setConstraints(final List<ConstraintState> constraints) {
        this.constraints = constraints;
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(this.operation).append(" ");
        if (this.columnName != null) {
            b.append("COLUMN ").append(this.columnName);
        }
        else if (this.getColDataTypeList() != null) {
            if (this.colDataTypeList.size() > 1) {
                b.append("(");
            }
            else {
                b.append("COLUMN ");
            }
            b.append(PlainSelect.getStringList(this.colDataTypeList));
            if (this.colDataTypeList.size() > 1) {
                b.append(")");
            }
        }
        else if (this.constraintName != null) {
            b.append("CONSTRAINT ").append(this.constraintName);
        }
        else if (this.pkColumns != null) {
            b.append("PRIMARY KEY (").append(PlainSelect.getStringList(this.pkColumns)).append(')');
        }
        else if (this.ukColumns != null) {
            b.append("UNIQUE KEY ").append(this.ukName).append(" (").append(PlainSelect.getStringList(this.ukColumns)).append(")");
        }
        else if (this.fkColumns != null) {
            b.append("FOREIGN KEY (").append(PlainSelect.getStringList(this.fkColumns)).append(") REFERENCES ").append(this.fkSourceTable).append(" (").append(PlainSelect.getStringList(this.fkSourceColumns)).append(")");
            if (this.isOnDeleteCascade()) {
                b.append(" ON DELETE CASCADE");
            }
            else if (this.isOnDeleteRestrict()) {
                b.append(" ON DELETE RESTRICT");
            }
            else if (this.isOnDeleteSetNull()) {
                b.append(" ON DELETE SET NULL");
            }
        }
        else if (this.index != null) {
            b.append(this.index);
        }
        if (this.getConstraints() != null && !this.getConstraints().isEmpty()) {
            b.append(' ').append(PlainSelect.getStringList(this.constraints, false, false));
        }
        return b.toString();
    }
    
    public static class ColumnDataType
    {
        private final String columnName;
        private final ColDataType colDataType;
        private final List<String> columnSpecs;
        
        public ColumnDataType(final String columnName, final ColDataType colDataType, final List<String> columnSpecs) {
            this.columnName = columnName;
            this.colDataType = colDataType;
            this.columnSpecs = columnSpecs;
        }
        
        public String getColumnName() {
            return this.columnName;
        }
        
        public ColDataType getColDataType() {
            return this.colDataType;
        }
        
        public List<String> getColumnSpecs() {
            if (this.columnSpecs == null) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList((List<? extends String>)this.columnSpecs);
        }
        
        @Override
        public String toString() {
            return this.columnName + " " + this.colDataType + this.parametersToString();
        }
        
        private String parametersToString() {
            if (this.columnSpecs == null || this.columnSpecs.isEmpty()) {
                return "";
            }
            return " " + PlainSelect.getStringList(this.columnSpecs, false, false);
        }
    }
}
