package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintType;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import java.util.HashMap;
import com.adventnet.swissqlapi.sql.statement.create.PrimaryOrUniqueConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class AlterColumnClause
{
    private String alter;
    private String column;
    private String columnName;
    private String add;
    private String scope;
    private String addScopeIdentifier;
    private String drop;
    private String setString;
    private String defaultString;
    private String restrictOrCascade;
    private String defaultValue;
    private FunctionCalls defaultFunctionCalls;
    private SelectColumn selectColumn;
    private CreateColumn createColumn;
    private String diskAttributes;
    private String physicalCharacteristics;
    private String collate;
    private String collationName;
    private String nullOrNotNull;
    private String addOrDropRowguidcol;
    private String rowguidcol;
    private UserObjectContext context;
    private String dataTypeStr;
    private Datatype dataType;
    
    public AlterColumnClause() {
        this.context = null;
        this.dataTypeStr = null;
        this.dataType = null;
    }
    
    public void setAlter(final String alter) {
        this.alter = alter;
    }
    
    public void setColumn(final String column) {
        this.column = column;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public void setAdd(final String add) {
        this.add = add;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setAddScopeIdentifier(final String addScopeIdentifier) {
        this.addScopeIdentifier = addScopeIdentifier;
    }
    
    public void setDrop(final String drop) {
        this.drop = drop;
    }
    
    public void setSetString(final String setString) {
        this.setString = setString;
    }
    
    public void setDefaultString(final String defaultString) {
        this.defaultString = defaultString;
    }
    
    public void setRestrictOrCascade(final String restrictOrCascade) {
        this.restrictOrCascade = restrictOrCascade;
    }
    
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public void setDefaultFunctionCalls(final SelectColumn selectColumn) {
        this.selectColumn = selectColumn;
    }
    
    public void setCreateColumn(final CreateColumn createColumn) {
        this.createColumn = createColumn;
    }
    
    public void setDiskAttributes(final String diskAttributes) {
        this.diskAttributes = diskAttributes;
    }
    
    public void setPhysicalCharacteristics(final String physicalCharacteristics) {
        this.physicalCharacteristics = physicalCharacteristics;
    }
    
    public void setCollate(final String collate) {
        this.collate = collate;
    }
    
    public void setCollationName(final String collationName) {
        this.collationName = collationName;
    }
    
    public void setNullOrNotNull(final String nullOrNotNull) {
        this.nullOrNotNull = nullOrNotNull;
    }
    
    public void setAddOrDropRowguidcol(final String addOrDropRowguidcol) {
        this.addOrDropRowguidcol = addOrDropRowguidcol;
    }
    
    public void setRowguidcol(final String rowguidcol) {
        this.rowguidcol = rowguidcol;
    }
    
    public void setDataTypeStr(final String datatype) {
        this.dataTypeStr = datatype;
    }
    
    public void setDataType(final Datatype datatype) {
        this.dataType = datatype;
    }
    
    public String getAlter() {
        return this.alter;
    }
    
    public String getColumn() {
        return this.column;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public String getAdd() {
        return this.add;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public String getAddScopeIdentifier() {
        return this.addScopeIdentifier;
    }
    
    public String getDrop() {
        return this.drop;
    }
    
    public String getSetString() {
        return this.setString;
    }
    
    public String getDefaultString() {
        return this.defaultString;
    }
    
    public String getRestrictOrCascade() {
        return this.restrictOrCascade;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public SelectColumn getDefaultFunctionCalls() {
        return this.selectColumn;
    }
    
    public CreateColumn getCreateColumn() {
        return this.createColumn;
    }
    
    public String getPhysicalCharacteristics() {
        return this.physicalCharacteristics;
    }
    
    public String getDiskAttributes() {
        return this.diskAttributes;
    }
    
    public String getCollate() {
        return this.collate;
    }
    
    public String getCollationName() {
        return this.collationName;
    }
    
    public String getNullorNotNull() {
        return this.nullOrNotNull;
    }
    
    public String getAddOrDropRowguidcol() {
        return this.addOrDropRowguidcol;
    }
    
    public String getRowguidcol() {
        return this.rowguidcol;
    }
    
    public String getDataTypeStr() {
        return this.dataTypeStr;
    }
    
    public Datatype getDataType() {
        return this.dataType;
    }
    
    public AlterColumnClause toOracle() throws ConvertException {
        final AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
        if (tempAlterColumnClause.getAlter() != null) {
            tempAlterColumnClause.setAlter("MODIFY");
        }
        tempAlterColumnClause.setColumn(null);
        if (tempAlterColumnClause.getCreateColumn() != null) {
            final CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toOracleString();
            String colName = changeCreateColumn.getColumnName();
            if (colName != null) {
                boolean addQuotes = false;
                if (colName.startsWith("\"") && colName.endsWith("\"")) {
                    colName = colName.substring(1, colName.length() - 1);
                    addQuotes = true;
                }
                if (colName.length() > 30) {
                    colName = colName.substring(0, 30);
                    if (addQuotes) {
                        colName = "\"" + colName + "\"";
                    }
                    changeCreateColumn.setColumnName(colName);
                }
            }
            final Vector getConstraintVector = changeCreateColumn.getConstraintClause();
            if (getConstraintVector != null) {
                for (int j = 0; j < getConstraintVector.size(); ++j) {
                    final ConstraintClause toOracleConstraintClause = getConstraintVector.get(j);
                    if (toOracleConstraintClause != null) {
                        final ConstraintType changeConstraintType = toOracleConstraintClause.getConstraintType();
                        if (changeConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                            final PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)changeConstraintType;
                            primaryOrUniqueConstraintClause.setClustered(null);
                            if (primaryOrUniqueConstraintClause.getWith() != null) {
                                final Object fillfactor = primaryOrUniqueConstraintClause.getDiskAttr().get("FILLFACTOR");
                                if (fillfactor != null) {
                                    final String percent = fillfactor.toString();
                                    this.setDiskAttributes("PCTFREE " + percent);
                                }
                                primaryOrUniqueConstraintClause.setWith(null);
                                primaryOrUniqueConstraintClause.setDiskAttr(null);
                            }
                            if (changeCreateColumn.getColumnName() != null) {
                                primaryOrUniqueConstraintClause.setOpenBrace(null);
                                primaryOrUniqueConstraintClause.setConstraintColumnNames(null);
                                primaryOrUniqueConstraintClause.setClosedBrace(null);
                            }
                        }
                    }
                }
            }
        }
        if (tempAlterColumnClause.getPhysicalCharacteristics() != null) {
            tempAlterColumnClause.getPhysicalCharacteristics();
        }
        tempAlterColumnClause.setCollate(null);
        tempAlterColumnClause.setCollationName(null);
        tempAlterColumnClause.setAdd(null);
        tempAlterColumnClause.setAddScopeIdentifier(null);
        if (tempAlterColumnClause.getDrop() != null) {
            tempAlterColumnClause.getDrop();
        }
        if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol(null);
            tempAlterColumnClause.setDrop(null);
        }
        if (tempAlterColumnClause.getDefaultString() != null) {
            tempAlterColumnClause.setSetString(null);
            tempAlterColumnClause.setDefaultValue(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        return tempAlterColumnClause;
    }
    
    public AlterColumnClause toMSSQLServer() throws ConvertException {
        final AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
        if (tempAlterColumnClause.getAlter() != null) {
            tempAlterColumnClause.getAlter();
        }
        tempAlterColumnClause.setColumn("COLUMN");
        if (tempAlterColumnClause.getCreateColumn() != null) {
            CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toMSSQLServerString();
            if (tempAlterColumnClause.getDiskAttributes() != null) {
                changeCreateColumn = tempAlterColumnClause.getCreateColumn();
                final Vector getConstraintVector = changeCreateColumn.getConstraintClause();
                if (getConstraintVector != null) {
                    for (int j = 0; j < getConstraintVector.size(); ++j) {
                        final ConstraintClause toSQLServerConstraintClause = getConstraintVector.get(j);
                        final ConstraintType toSQLServerConstraintType = toSQLServerConstraintClause.getConstraintType();
                        if (toSQLServerConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                            final PrimaryOrUniqueConstraintClause pcc = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintType;
                            String fillfactor = tempAlterColumnClause.getDiskAttributes();
                            String tempFillfactor = "";
                            if (fillfactor != null) {
                                int fillIntValue = 0;
                                tempFillfactor = fillfactor;
                                tempFillfactor = tempFillfactor.substring(0, 7);
                                tempFillfactor = fillfactor.toUpperCase();
                                if (tempFillfactor.startsWith("PCTFREE")) {
                                    fillfactor = fillfactor.substring(8);
                                }
                                else if (tempFillfactor.startsWith("PCTUSED")) {
                                    fillfactor = fillfactor.substring(8);
                                    fillIntValue = Integer.parseInt(fillfactor);
                                    fillIntValue = 100 - fillIntValue;
                                    fillfactor = "" + fillIntValue;
                                }
                                pcc.setWith("WITH");
                                final HashMap diskAttr = new HashMap();
                                diskAttr.put("FILLFACTOR", new String(fillIntValue + ""));
                                pcc.setDiskAttr(diskAttr);
                                tempAlterColumnClause.setDiskAttributes(null);
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (tempAlterColumnClause.getAdd() != null) {
            tempAlterColumnClause.getAdd();
        }
        if (tempAlterColumnClause.getDrop() != null) {
            tempAlterColumnClause.getDrop();
        }
        if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.getAddOrDropRowguidcol();
        }
        tempAlterColumnClause.setColumnName(null);
        tempAlterColumnClause.setScope(null);
        tempAlterColumnClause.setAddScopeIdentifier(null);
        if (tempAlterColumnClause.getDefaultString() != null) {
            tempAlterColumnClause.setSetString(null);
            tempAlterColumnClause.setDefaultValue(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterColumnClause.setDefaultFunctionCalls(null);
        tempAlterColumnClause.setRestrictOrCascade(null);
        if (tempAlterColumnClause.getCollate() != null) {
            tempAlterColumnClause.getCollate();
        }
        if (tempAlterColumnClause.getCollationName() != null) {
            tempAlterColumnClause.getCollationName();
        }
        if (tempAlterColumnClause.getNullorNotNull() != null) {
            tempAlterColumnClause.getNullorNotNull();
        }
        return tempAlterColumnClause;
    }
    
    public AlterColumnClause toSybase() throws ConvertException {
        final AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
        if (tempAlterColumnClause.getAlter() != null) {
            tempAlterColumnClause.setAlter("MODIFY");
        }
        tempAlterColumnClause.setColumn(null);
        if (tempAlterColumnClause.getCreateColumn() != null) {
            final CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toSybaseString();
        }
        tempAlterColumnClause.setDiskAttributes(null);
        tempAlterColumnClause.setPhysicalCharacteristics(null);
        tempAlterColumnClause.setAdd(null);
        tempAlterColumnClause.setDrop(null);
        tempAlterColumnClause.setAddOrDropRowguidcol(null);
        tempAlterColumnClause.setColumnName(null);
        tempAlterColumnClause.setScope(null);
        tempAlterColumnClause.setAddScopeIdentifier(null);
        if (tempAlterColumnClause.getDefaultString() != null) {
            tempAlterColumnClause.setSetString(null);
            tempAlterColumnClause.setDefaultValue(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterColumnClause.setDefaultFunctionCalls(null);
        tempAlterColumnClause.setRestrictOrCascade(null);
        if (this.collate != null) {
            tempAlterColumnClause.setCollate("SORTKEY");
        }
        if (this.collationName != null) {
            tempAlterColumnClause.setCollationName(this.collationName);
        }
        if (this.nullOrNotNull != null) {
            tempAlterColumnClause.setNullOrNotNull(this.nullOrNotNull);
        }
        return tempAlterColumnClause;
    }
    
    public AlterColumnClause toDB2() throws ConvertException {
        final AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
        if (tempAlterColumnClause.getAlter() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterColumnClause.setColumn(null);
        tempAlterColumnClause.setColumnName(null);
        tempAlterColumnClause.setAdd(null);
        tempAlterColumnClause.setScope(null);
        tempAlterColumnClause.setCreateColumn(null);
        tempAlterColumnClause.setDiskAttributes(null);
        tempAlterColumnClause.setPhysicalCharacteristics(null);
        tempAlterColumnClause.setAddOrDropRowguidcol(null);
        tempAlterColumnClause.setRowguidcol(null);
        tempAlterColumnClause.setAddScopeIdentifier(null);
        tempAlterColumnClause.setDrop(null);
        tempAlterColumnClause.setSetString(null);
        tempAlterColumnClause.setDefaultString(null);
        tempAlterColumnClause.setRestrictOrCascade(null);
        tempAlterColumnClause.setDefaultValue(null);
        tempAlterColumnClause.setDefaultFunctionCalls(null);
        tempAlterColumnClause.setCollate(null);
        tempAlterColumnClause.setCollationName(null);
        tempAlterColumnClause.setNullOrNotNull(null);
        return tempAlterColumnClause;
    }
    
    public AlterColumnClause toANSI() throws ConvertException {
        final AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
        if (tempAlterColumnClause.getAlter() != null) {
            tempAlterColumnClause.getAlter();
        }
        if (tempAlterColumnClause.getColumn() != null) {
            tempAlterColumnClause.getColumn();
        }
        if (tempAlterColumnClause.getColumnName() != null) {
            tempAlterColumnClause.getColumnName();
        }
        tempAlterColumnClause.setAdd(null);
        if (tempAlterColumnClause.getDrop() != null) {
            tempAlterColumnClause.getDrop();
        }
        if (tempAlterColumnClause.getScope() != null) {
            tempAlterColumnClause.getScope();
        }
        if (tempAlterColumnClause.getAddScopeIdentifier() != null) {
            tempAlterColumnClause.getAddScopeIdentifier();
        }
        if (tempAlterColumnClause.getRestrictOrCascade() != null) {
            tempAlterColumnClause.getRestrictOrCascade();
        }
        if (tempAlterColumnClause.getSetString() != null) {
            tempAlterColumnClause.setDefaultString(null);
            tempAlterColumnClause.setDefaultValue(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
            final CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toANSIString();
        }
        tempAlterColumnClause.setDiskAttributes(null);
        tempAlterColumnClause.setPhysicalCharacteristics(null);
        tempAlterColumnClause.setCollate(null);
        tempAlterColumnClause.setCollationName(null);
        tempAlterColumnClause.setNullOrNotNull(null);
        if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol(null);
            tempAlterColumnClause.setDrop(null);
        }
        tempAlterColumnClause.setRowguidcol(null);
        return tempAlterColumnClause;
    }
    
    public AlterColumnClause toMySQL() throws ConvertException {
        final AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
        if (tempAlterColumnClause.getAlter() != null) {
            tempAlterColumnClause.getAlter();
        }
        if (tempAlterColumnClause.getColumn() != null) {
            tempAlterColumnClause.getColumn();
        }
        if (tempAlterColumnClause.getColumnName() != null) {
            tempAlterColumnClause.getColumnName();
        }
        if (tempAlterColumnClause.getSetString() != null) {
            tempAlterColumnClause.getSetString();
        }
        if (tempAlterColumnClause.getDefaultString() != null) {
            tempAlterColumnClause.getDefaultString();
        }
        if (tempAlterColumnClause.getDrop() != null) {
            tempAlterColumnClause.getDrop();
        }
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("CURRENT_TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER()");
            }
        }
        else if (this.getDefaultFunctionCalls() != null) {
            final SelectColumn tempSelectColumn = tempAlterColumnClause.getDefaultFunctionCalls();
            final SelectColumn oracleSelectColumn = tempSelectColumn.toMySQLSelect(null, null);
            tempAlterColumnClause.setDefaultFunctionCalls(oracleSelectColumn);
        }
        tempAlterColumnClause.setAdd(null);
        tempAlterColumnClause.setScope(null);
        if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
            final CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toMySQLString();
        }
        tempAlterColumnClause.setDiskAttributes(null);
        tempAlterColumnClause.setPhysicalCharacteristics(null);
        if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol(null);
            tempAlterColumnClause.setDrop(null);
        }
        tempAlterColumnClause.setRowguidcol(null);
        tempAlterColumnClause.setAddScopeIdentifier(null);
        tempAlterColumnClause.setRestrictOrCascade(null);
        tempAlterColumnClause.setCollate(null);
        tempAlterColumnClause.setCollationName(null);
        tempAlterColumnClause.setNullOrNotNull(null);
        return tempAlterColumnClause;
    }
    
    public AlterColumnClause toPostgreSQL() throws ConvertException {
        final AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
        if (tempAlterColumnClause.getAlter() != null) {
            tempAlterColumnClause.getAlter();
        }
        if (tempAlterColumnClause.getColumn() != null) {
            tempAlterColumnClause.getColumn();
        }
        if (tempAlterColumnClause.getColumnName() != null) {
            tempAlterColumnClause.getColumnName();
        }
        if (tempAlterColumnClause.getSetString() != null) {
            tempAlterColumnClause.getSetString();
        }
        if (tempAlterColumnClause.getDefaultString() != null) {
            tempAlterColumnClause.getDefaultString();
        }
        if (tempAlterColumnClause.getDrop() != null) {
            tempAlterColumnClause.getDrop();
        }
        if (tempAlterColumnClause.getSetString() != null) {
            tempAlterColumnClause.setDefaultString(null);
            tempAlterColumnClause.setDefaultValue(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterColumnClause.setAdd(null);
        tempAlterColumnClause.setScope(null);
        if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
            final CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toPostgreSQLString();
        }
        tempAlterColumnClause.setDiskAttributes(null);
        tempAlterColumnClause.setPhysicalCharacteristics(null);
        if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol(null);
            tempAlterColumnClause.setDrop(null);
        }
        tempAlterColumnClause.setRowguidcol(null);
        tempAlterColumnClause.setAddScopeIdentifier(null);
        tempAlterColumnClause.setRestrictOrCascade(null);
        tempAlterColumnClause.setCollate(null);
        tempAlterColumnClause.setCollationName(null);
        tempAlterColumnClause.setNullOrNotNull(null);
        return tempAlterColumnClause;
    }
    
    public AlterColumnClause toInformix() throws ConvertException {
        final AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
        if (tempAlterColumnClause.getAlter() != null) {
            tempAlterColumnClause.setAlter("MODIFY");
        }
        if (tempAlterColumnClause.getCreateColumn() != null) {
            final CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toInformixString();
        }
        tempAlterColumnClause.setColumn(null);
        tempAlterColumnClause.setColumnName(null);
        tempAlterColumnClause.setAdd(null);
        tempAlterColumnClause.setScope(null);
        tempAlterColumnClause.setDiskAttributes(null);
        tempAlterColumnClause.setPhysicalCharacteristics(null);
        tempAlterColumnClause.setAddOrDropRowguidcol(null);
        tempAlterColumnClause.setRowguidcol(null);
        tempAlterColumnClause.setAddScopeIdentifier(null);
        tempAlterColumnClause.setDrop(null);
        if (tempAlterColumnClause.getSetString() != null) {
            tempAlterColumnClause.setDefaultString(null);
            tempAlterColumnClause.setDefaultValue(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterColumnClause.setRestrictOrCascade(null);
        tempAlterColumnClause.setDefaultFunctionCalls(null);
        tempAlterColumnClause.setCollate(null);
        tempAlterColumnClause.setCollationName(null);
        tempAlterColumnClause.setNullOrNotNull(null);
        return tempAlterColumnClause;
    }
    
    public AlterColumnClause toNetezza() throws ConvertException {
        final AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
        if (tempAlterColumnClause.getAlter() != null) {
            tempAlterColumnClause.getAlter();
        }
        if (tempAlterColumnClause.getColumn() != null) {
            tempAlterColumnClause.getColumn();
        }
        if (tempAlterColumnClause.getColumnName() != null) {
            tempAlterColumnClause.getColumnName();
        }
        tempAlterColumnClause.setAdd(null);
        if (tempAlterColumnClause.getDrop() != null) {
            tempAlterColumnClause.getDrop();
        }
        if (tempAlterColumnClause.getScope() != null) {
            tempAlterColumnClause.getScope();
        }
        if (tempAlterColumnClause.getAddScopeIdentifier() != null) {
            tempAlterColumnClause.getAddScopeIdentifier();
        }
        if (tempAlterColumnClause.getRestrictOrCascade() != null) {
            tempAlterColumnClause.getRestrictOrCascade();
        }
        if (tempAlterColumnClause.getSetString() != null) {
            tempAlterColumnClause.setDefaultString(null);
            tempAlterColumnClause.setDefaultValue(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
            final CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toNetezzaString();
        }
        tempAlterColumnClause.setDiskAttributes(null);
        tempAlterColumnClause.setPhysicalCharacteristics(null);
        tempAlterColumnClause.setCollate(null);
        tempAlterColumnClause.setCollationName(null);
        tempAlterColumnClause.setNullOrNotNull(null);
        if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol(null);
            tempAlterColumnClause.setDrop(null);
        }
        tempAlterColumnClause.setRowguidcol(null);
        return tempAlterColumnClause;
    }
    
    public AlterColumnClause toTeradata() throws ConvertException {
        final AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
        if (tempAlterColumnClause.getAlter() != null) {
            tempAlterColumnClause.getAlter();
        }
        if (tempAlterColumnClause.getColumn() != null) {
            tempAlterColumnClause.getColumn();
        }
        if (tempAlterColumnClause.getColumnName() != null) {
            tempAlterColumnClause.getColumnName();
        }
        tempAlterColumnClause.setAdd(null);
        if (tempAlterColumnClause.getDrop() != null) {
            tempAlterColumnClause.getDrop();
        }
        if (tempAlterColumnClause.getScope() != null) {
            tempAlterColumnClause.getScope();
        }
        if (tempAlterColumnClause.getAddScopeIdentifier() != null) {
            tempAlterColumnClause.getAddScopeIdentifier();
        }
        if (tempAlterColumnClause.getRestrictOrCascade() != null) {
            tempAlterColumnClause.getRestrictOrCascade();
        }
        if (tempAlterColumnClause.getSetString() != null) {
            tempAlterColumnClause.setDefaultString(null);
            tempAlterColumnClause.setDefaultValue(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
            final CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toTeradataString();
        }
        tempAlterColumnClause.setDiskAttributes(null);
        tempAlterColumnClause.setPhysicalCharacteristics(null);
        tempAlterColumnClause.setCollate(null);
        tempAlterColumnClause.setCollationName(null);
        tempAlterColumnClause.setNullOrNotNull(null);
        if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol(null);
            tempAlterColumnClause.setDrop(null);
        }
        tempAlterColumnClause.setRowguidcol(null);
        return tempAlterColumnClause;
    }
    
    public AlterColumnClause copyObjectValues() {
        final AlterColumnClause dupAlterColumnClause = new AlterColumnClause();
        dupAlterColumnClause.setAlter(this.getAlter());
        dupAlterColumnClause.setColumn(this.getColumn());
        dupAlterColumnClause.setColumnName(this.getCollationName());
        dupAlterColumnClause.setAdd(this.getAdd());
        dupAlterColumnClause.setScope(this.getScope());
        dupAlterColumnClause.setAddScopeIdentifier(this.getAddScopeIdentifier());
        dupAlterColumnClause.setDrop(this.getDrop());
        dupAlterColumnClause.setSetString(this.getSetString());
        dupAlterColumnClause.setDefaultString(this.getDefaultString());
        dupAlterColumnClause.setRestrictOrCascade(this.getRestrictOrCascade());
        dupAlterColumnClause.setDefaultValue(this.getDefaultValue());
        dupAlterColumnClause.setDefaultFunctionCalls(this.getDefaultFunctionCalls());
        dupAlterColumnClause.setCreateColumn(this.getCreateColumn());
        dupAlterColumnClause.setDiskAttributes(this.getDiskAttributes());
        dupAlterColumnClause.setPhysicalCharacteristics(this.getPhysicalCharacteristics());
        dupAlterColumnClause.setCollate(this.getCollate());
        dupAlterColumnClause.setCollationName(this.getCollationName());
        dupAlterColumnClause.setNullOrNotNull(this.getNullorNotNull());
        dupAlterColumnClause.setAddOrDropRowguidcol(this.getAddOrDropRowguidcol());
        dupAlterColumnClause.setObjectContext(this.context);
        return dupAlterColumnClause;
    }
    
    public String removeIndent(String str) {
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        return str;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.alter != null) {
            sb.append(this.alter.toUpperCase());
        }
        if (this.column != null) {
            sb.append(" " + this.column.toUpperCase());
        }
        if (this.createColumn != null) {
            this.createColumn.setObjectContext(this.context);
            sb.append("\n\t" + this.createColumn.toString());
        }
        if (this.diskAttributes != null) {
            sb.append(" " + this.diskAttributes.toUpperCase());
        }
        if (this.physicalCharacteristics != null) {
            sb.append(" " + this.physicalCharacteristics.toUpperCase());
        }
        if (this.collate != null) {
            sb.append("\n" + this.collate.toUpperCase());
        }
        if (this.collationName != null) {
            sb.append(" " + this.collationName);
        }
        if (this.nullOrNotNull != null) {
            sb.append(" " + this.nullOrNotNull.toUpperCase());
        }
        if (this.add != null) {
            sb.append("\n" + this.add.toUpperCase());
        }
        if (this.drop != null) {
            sb.append("\n" + this.drop.toUpperCase());
        }
        if (this.setString != null) {
            sb.append("\n" + this.setString.toUpperCase());
        }
        if (this.defaultString != null) {
            sb.append(" " + this.defaultString.toUpperCase());
        }
        if (this.scope != null) {
            sb.append(" " + this.scope.toUpperCase());
        }
        if (this.addScopeIdentifier != null) {
            sb.append(" " + this.addScopeIdentifier);
        }
        if (this.restrictOrCascade != null) {
            sb.append(" " + this.restrictOrCascade.toUpperCase());
        }
        if (this.defaultValue != null) {
            sb.append(" " + this.defaultValue);
        }
        if (this.defaultFunctionCalls != null) {
            this.defaultFunctionCalls.setObjectContext(this.context);
            sb.append(" " + this.defaultFunctionCalls);
        }
        if (this.dataTypeStr != null) {
            sb.append(" " + this.dataTypeStr);
        }
        if (this.dataType != null) {
            sb.append(" " + this.dataType);
        }
        if (this.addOrDropRowguidcol != null) {
            sb.append(" " + this.addOrDropRowguidcol.toUpperCase());
        }
        return sb.toString();
    }
}
