package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.sql.statement.create.PrimaryOrUniqueConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintType;
import com.adventnet.swissqlapi.sql.statement.create.DefaultConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.statement.create.PhysicalAttributesClause;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.create.PartitionListAttributes;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class AlterTable
{
    private UserObjectContext context;
    private AddClause addClause;
    private ModifyClause modifyClause;
    private DropClause dropClause;
    private AlterColumnClause alterColumnClause;
    private String data;
    private String capture;
    private String noneOrChanges;
    private String rename;
    private String toTableName;
    private String asOrTo;
    private String renameColumn;
    private String tableName;
    private String toColumn;
    private String columnName;
    private String overflow;
    private String storage;
    private String storageClauseString;
    private String move;
    private String truncate;
    private String split;
    private String exchange;
    private PartitionListAttributes partitionListAttributes;
    private String allocateExtent;
    private String allocateExtentIdentifier;
    private String deAllocateUnused;
    private String deAllocateUnusedIdentifier;
    private String checkOrNoCheck;
    private String constraint;
    private String constraintAll;
    private Vector constraintNameVector;
    private String owner;
    private String ownerTo;
    private String ownerName;
    private String change;
    private String changeColumn;
    private CreateColumn createColumn;
    private Vector triggerNameVector;
    private String enableOrDisable;
    private String trigger;
    private String triggerAll;
    private String with;
    private String comma;
    private PhysicalAttributesClause physicalAttributesClause;
    private Vector physicalAttributesClauseVector;
    private DatatypeMapping datatypeMapping;
    private String alterTableName;
    private String tableOption;
    private String tableOptionParameter;
    private String sp_renameStmt;
    private String origColumn;
    
    public AlterTable() {
        this.context = null;
        this.sp_renameStmt = null;
        this.origColumn = null;
    }
    
    public void setAddClause(final AddClause addClause) {
        this.addClause = addClause;
    }
    
    public void setModifyClause(final ModifyClause modifyClause) {
        this.modifyClause = modifyClause;
    }
    
    public void setDropClause(final DropClause dropClause) {
        this.dropClause = dropClause;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setAlterColumnClause(final AlterColumnClause alterColumnClause) {
        this.alterColumnClause = alterColumnClause;
    }
    
    public void setData(final String data) {
        this.data = data;
    }
    
    public void setCapture(final String capture) {
        this.capture = capture;
    }
    
    public void setNoneOrChanges(final String noneOrChanges) {
        this.noneOrChanges = noneOrChanges;
    }
    
    public void setRename(final String rename) {
        this.rename = rename;
    }
    
    public void setToTableName(final String toTableName) {
        this.toTableName = toTableName;
    }
    
    public void setAsOrTo(final String asOrTo) {
        this.asOrTo = asOrTo;
    }
    
    public void setRenameColumn(final String renameColumn) {
        this.renameColumn = renameColumn;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public void setAlterTableName(final String alterTableName) {
        this.alterTableName = alterTableName;
    }
    
    public void setToColumn(final String toColumn) {
        this.toColumn = toColumn;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public void setOverflow(final String overflow) {
        this.overflow = overflow;
    }
    
    public void setStorage(final String storage) {
        this.storage = storage;
    }
    
    public void setStorageClause(final String storageClauseString) {
        this.storageClauseString = storageClauseString;
    }
    
    public void setMove(final String move) {
        this.move = move;
    }
    
    public void setTruncate(final String truncate) {
        this.truncate = truncate;
    }
    
    public void setSplit(final String split) {
        this.split = split;
    }
    
    public void setExchange(final String exchange) {
        this.exchange = exchange;
    }
    
    public void setPartition(final PartitionListAttributes partitionListAttributes) {
        this.partitionListAttributes = partitionListAttributes;
    }
    
    public void setAllocateExtent(final String allocateExtent) {
        this.allocateExtent = allocateExtent;
    }
    
    public void setAllocateExtentIdentifier(final String allocateExtentIdentifier) {
        this.allocateExtentIdentifier = allocateExtentIdentifier;
    }
    
    public void setDeAllocateUnused(final String deAllocateUnused) {
        this.deAllocateUnused = deAllocateUnused;
    }
    
    public void setDeAllocateUnusedIdentifier(final String deAllocateUnusedIdentifier) {
        this.deAllocateUnusedIdentifier = deAllocateUnusedIdentifier;
    }
    
    public void setCheckOrNoCheck(final String checkOrNoCheck) {
        this.checkOrNoCheck = checkOrNoCheck;
    }
    
    public void setConstraint(final String constraint) {
        this.constraint = constraint;
    }
    
    public void setConstraintAll(final String constraintAll) {
        this.constraintAll = constraintAll;
    }
    
    public void setConstraintNameVector(final Vector constraintNameVector) {
        this.constraintNameVector = constraintNameVector;
    }
    
    public void setOwner(final String owner) {
        this.owner = owner;
    }
    
    public void setOwnerTo(final String ownerTo) {
        this.ownerTo = ownerTo;
    }
    
    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }
    
    public void setChange(final String change) {
        this.change = change;
    }
    
    public void setChangeColumn(final String changeColumn) {
        this.changeColumn = changeColumn;
    }
    
    public void setCreateColumn(final CreateColumn createColumn) {
        this.createColumn = createColumn;
    }
    
    public void setTriggerNameVector(final Vector triggerNameVector) {
        this.triggerNameVector = triggerNameVector;
    }
    
    public void setEnableOrDisable(final String enableOrDisable) {
        this.enableOrDisable = enableOrDisable;
    }
    
    public void setTrigger(final String trigger) {
        this.trigger = trigger;
    }
    
    public void setTriggerAll(final String triggerAll) {
        this.triggerAll = triggerAll;
    }
    
    public void setWith(final String with) {
        this.with = with;
    }
    
    public void setComma(final String comma) {
        this.comma = comma;
    }
    
    public void setPhysicalAttributesClause(final PhysicalAttributesClause physicalAttributesClause) {
        this.physicalAttributesClause = physicalAttributesClause;
    }
    
    public void setPhysicalAttributesClauseVector(final Vector physicalAttributesClauseVector) {
        this.physicalAttributesClauseVector = physicalAttributesClauseVector;
    }
    
    public void setDatatypeMapping(final DatatypeMapping mapping) {
        this.datatypeMapping = mapping;
    }
    
    public void setTableOption(final String tableOption) {
        this.tableOption = tableOption;
    }
    
    public void setTableOptionParameter(final String tableOptionParam) {
        this.tableOptionParameter = tableOptionParam;
    }
    
    public void setsp_renameStmt(final String sp_renameStmt) {
        this.sp_renameStmt = sp_renameStmt;
    }
    
    public void setOrigColumn(final String origCol) {
        this.origColumn = origCol;
    }
    
    public AddClause getAddClause() {
        return this.addClause;
    }
    
    public ModifyClause getModifyClause() {
        return this.modifyClause;
    }
    
    public DropClause getDropClause() {
        return this.dropClause;
    }
    
    public AlterColumnClause getAlterColumnClause() {
        return this.alterColumnClause;
    }
    
    public String getData() {
        return this.data;
    }
    
    public String getCapture() {
        return this.capture;
    }
    
    public String getNoneOrChanges() {
        return this.noneOrChanges;
    }
    
    public String getRename() {
        return this.rename;
    }
    
    public String getAsOrTo() {
        return this.asOrTo;
    }
    
    public String getRenameColumn() {
        return this.renameColumn;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getToColumn() {
        return this.toColumn;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public String getOverflow() {
        return this.overflow;
    }
    
    public String getStorage() {
        return this.storage;
    }
    
    public String getStorageClause() {
        return this.storageClauseString;
    }
    
    public String getMove() {
        return this.move;
    }
    
    public String getTruncate() {
        return this.truncate;
    }
    
    public String getSplit() {
        return this.split;
    }
    
    public String getExchange() {
        return this.exchange;
    }
    
    public PartitionListAttributes getPartition() {
        return this.partitionListAttributes;
    }
    
    public String getAllocateExtent() {
        return this.allocateExtent;
    }
    
    public String getAllocateExtentIdentifier() {
        return this.allocateExtentIdentifier;
    }
    
    public String getDeAllocateUnused() {
        return this.deAllocateUnused;
    }
    
    public String getDeAllocateUnusedIdentifier() {
        return this.deAllocateUnusedIdentifier;
    }
    
    public String getCheckOrNoCheck() {
        return this.checkOrNoCheck;
    }
    
    public String getConstraint() {
        return this.constraint;
    }
    
    public String getConstraintAll() {
        return this.constraintAll;
    }
    
    public Vector getConstraintNameVector() {
        return this.constraintNameVector;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public String getOwnerTo() {
        return this.ownerTo;
    }
    
    public String getOwnerName() {
        return this.ownerName;
    }
    
    public String getChange() {
        return this.change;
    }
    
    public String getChangeColumn() {
        return this.changeColumn;
    }
    
    public CreateColumn getCreateColumn() {
        return this.createColumn;
    }
    
    public Vector getTriggerNameVector() {
        return this.triggerNameVector;
    }
    
    public String getEnableOrDisable() {
        return this.enableOrDisable;
    }
    
    public String getTrigger() {
        return this.trigger;
    }
    
    public String getTriggerAll() {
        return this.triggerAll;
    }
    
    public PhysicalAttributesClause getPhysicalAttributesClause() {
        return this.physicalAttributesClause;
    }
    
    public Vector getPhysicalAttributesClauseVector() {
        return this.physicalAttributesClauseVector;
    }
    
    public String getWith() {
        return this.with;
    }
    
    public String getTableOption() {
        return this.tableOption;
    }
    
    public String getTableOptionParameter() {
        return this.tableOptionParameter;
    }
    
    public String getsp_renameStmt() {
        return this.sp_renameStmt;
    }
    
    public String getOrigColumn() {
        return this.origColumn;
    }
    
    public AlterTable toOracle() throws ConvertException {
        final AlterTable tempAlterTable = this.copyObjectValues();
        if (tempAlterTable.getAddClause() != null) {
            final AddClause tempAddClause = tempAlterTable.getAddClause();
            final AddClause oracleAddClause = tempAddClause.toOracle();
            tempAlterTable.setAddClause(oracleAddClause);
        }
        tempAlterTable.setData(null);
        tempAlterTable.setCapture(null);
        tempAlterTable.setNoneOrChanges(null);
        if (tempAlterTable.getModifyClause() != null) {
            final ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
            final ModifyClause oracleModifyClause = tempModifyClause.toOracle();
            tempAlterTable.setModifyClause(oracleModifyClause);
        }
        if (tempAlterTable.getDropClause() != null) {
            final DropClause tempDropClause = tempAlterTable.getDropClause();
            final DropClause oracleDropClause = tempDropClause.toOracle();
            tempAlterTable.setDropClause(oracleDropClause);
        }
        if (tempAlterTable.getAlterColumnClause() != null) {
            final AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
            final AlterColumnClause oracleAlterColumnClause = tempAlterColumnClause.toOracle();
            tempAlterTable.setAlterColumnClause(oracleAlterColumnClause);
        }
        if (tempAlterTable.getRename() != null) {
            tempAlterTable.getRename();
        }
        if (tempAlterTable.getAsOrTo() != null) {
            String tempAsOrTo = tempAlterTable.getAsOrTo();
            tempAsOrTo = tempAsOrTo.trim();
            if (tempAsOrTo.equalsIgnoreCase("AS")) {
                tempAlterTable.setAsOrTo("TO");
            }
            else {
                tempAlterTable.setAsOrTo(tempAsOrTo);
            }
        }
        if (tempAlterTable.getRenameColumn() != null) {
            tempAlterTable.getRenameColumn();
        }
        if (this.toTableName != null) {
            tempAlterTable.setToTableName("AS");
        }
        if (tempAlterTable.getTableName() != null) {
            tempAlterTable.getTableName();
        }
        if (tempAlterTable.getToColumn() != null) {
            tempAlterTable.getToColumn();
        }
        if (tempAlterTable.getColumnName() != null) {
            tempAlterTable.getColumnName();
        }
        if (tempAlterTable.getOverflow() != null) {
            tempAlterTable.getOverflow();
        }
        if (tempAlterTable.getStorage() != null) {
            tempAlterTable.getStorage();
        }
        if (tempAlterTable.getStorageClause() != null) {
            tempAlterTable.getStorageClause();
        }
        if (tempAlterTable.getMove() != null) {
            tempAlterTable.getMove();
        }
        if (tempAlterTable.getTruncate() != null) {
            tempAlterTable.getTruncate();
        }
        if (tempAlterTable.getSplit() != null) {
            tempAlterTable.getSplit();
        }
        if (tempAlterTable.getExchange() != null) {
            tempAlterTable.getExchange();
        }
        if (tempAlterTable.getPartition() != null) {
            tempAlterTable.getPartition();
        }
        if (tempAlterTable.getAllocateExtent() != null) {
            tempAlterTable.getAllocateExtent();
        }
        if (tempAlterTable.getAllocateExtentIdentifier() != null) {
            tempAlterTable.getAllocateExtentIdentifier();
        }
        if (tempAlterTable.getDeAllocateUnused() != null) {
            tempAlterTable.getDeAllocateUnused();
        }
        if (tempAlterTable.getDeAllocateUnusedIdentifier() != null) {
            tempAlterTable.getDeAllocateUnusedIdentifier();
        }
        if (tempAlterTable.getCheckOrNoCheck() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setConstraint(null);
        tempAlterTable.setConstraintAll(null);
        tempAlterTable.setConstraintNameVector(null);
        if (tempAlterTable.getOwner() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setOwnerTo(null);
        tempAlterTable.setOwnerName(null);
        if (tempAlterTable.getChange() != null) {
            tempAlterTable.setChange("MODIFY");
        }
        tempAlterTable.setChangeColumn(null);
        if (tempAlterTable.getCreateColumn() != null) {
            final CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
            tempCreateColumn.toOracleString();
        }
        if (tempAlterTable.getEnableOrDisable() != null) {
            tempAlterTable.setEnableOrDisable(this.enableOrDisable);
            tempAlterTable.setToTableName(null);
            tempAlterTable.setTrigger(null);
            final StringBuffer sb = new StringBuffer();
            if (this.triggerNameVector != null) {
                for (int i = 0; i < this.triggerNameVector.size(); ++i) {
                    if (this.triggerNameVector.elementAt(i) instanceof String) {
                        if (i > 0) {
                            sb.append(",");
                        }
                        sb.append(this.triggerNameVector.get(i));
                    }
                    else if (this.triggerNameVector.elementAt(i) instanceof CreateColumn) {
                        final CreateColumn createColumn = this.triggerNameVector.get(i);
                        if (i > 0) {
                            sb.append(",");
                        }
                        sb.append(createColumn.toString());
                    }
                }
                tempAlterTable.setTableName(sb.toString());
            }
        }
        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
            final Vector oraclePhysicalAttributesClauseVector = new Vector();
            final Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();
            for (int j = 0; j < tempPhysicalAttributesClauseVector.size(); ++j) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = tempPhysicalAttributesClauseVector.get(j);
                final PhysicalAttributesClause oraclePhysicalAttributesClause = tempPhysicalAttributesClause.toOracle();
                oraclePhysicalAttributesClauseVector.add(oraclePhysicalAttributesClause);
            }
            tempAlterTable.setPhysicalAttributesClauseVector(oraclePhysicalAttributesClauseVector);
        }
        tempAlterTable.setWith(null);
        tempAlterTable.setTriggerAll(null);
        tempAlterTable.setTriggerNameVector(null);
        if (tempAlterTable.getAddClause() != null) {
            final Vector columnVector = tempAlterTable.getAddClause().getCreateColumnVector();
            final Vector colVector = new Vector();
            final ModifyClause modify = new ModifyClause();
            final AddClause addClause = new AddClause();
            final Vector pkClauseVector = new Vector();
            if (columnVector != null) {
                boolean isModify = false;
                boolean isAdd = false;
                for (int k = 0; k < columnVector.size(); ++k) {
                    final Vector constrClauseVector = columnVector.get(k).getConstraintClause();
                    if (constrClauseVector != null) {
                        for (int l = 0; l < constrClauseVector.size(); ++l) {
                            final ConstraintType constrType = constrClauseVector.get(l).getConstraintType();
                            if (constrType != null && constrType instanceof DefaultConstraintClause) {
                                if (((DefaultConstraintClause)constrType).getForClause() != null) {
                                    modify.setModify("MODIFY");
                                    modify.setClosedBrace(")");
                                    modify.setOpenBrace("(");
                                    final CreateColumn column = new CreateColumn();
                                    String colName = ((DefaultConstraintClause)constrType).getColumnName();
                                    if (colName.indexOf("[") != -1) {
                                        colName = colName.substring(1, colName.length() - 1);
                                    }
                                    column.setColumnName(colName);
                                    column.setDefault("DEFAULT");
                                    column.setDefaultValue(((DefaultConstraintClause)constrType).getDefaultValue());
                                    colVector.add(column);
                                    isModify = true;
                                }
                            }
                            else if (constrType != null) {
                                pkClauseVector.add(constrClauseVector.get(l));
                                isAdd = true;
                            }
                        }
                    }
                }
                if (isModify) {
                    modify.setCreateColumnVector(colVector);
                    if (isAdd) {
                        addClause.setAdd("ADD");
                        addClause.setConstraintClauseVector(pkClauseVector);
                        tempAlterTable.setAddClause(addClause);
                    }
                    else {
                        tempAlterTable.setAddClause(null);
                    }
                    tempAlterTable.setModifyClause(modify);
                }
            }
        }
        return tempAlterTable;
    }
    
    public AlterTable toMSSQLServer() throws ConvertException {
        final AlterTable tempAlterTable = this.copyObjectValues();
        if (tempAlterTable.getAddClause() != null) {
            final AddClause tempAddClause = tempAlterTable.getAddClause();
            final AddClause msSQLServerAddClause = tempAddClause.toMSSQLServer();
            tempAlterTable.setAddClause(msSQLServerAddClause);
        }
        tempAlterTable.setData(null);
        tempAlterTable.setCapture(null);
        tempAlterTable.setNoneOrChanges(null);
        if (tempAlterTable.getModifyClause() != null) {
            final ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
            final ModifyClause msSQLServerModifyClause = tempModifyClause.toMSSQLServer();
            tempAlterTable.setModifyClause(msSQLServerModifyClause);
        }
        if (tempAlterTable.getDropClause() != null) {
            final DropClause tempDropClause = tempAlterTable.getDropClause();
            final DropClause msSQLServerDropClause = tempDropClause.toMSSQLServer();
            tempAlterTable.setDropClause(msSQLServerDropClause);
        }
        if (tempAlterTable.getAlterColumnClause() != null) {
            final AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
            final AlterColumnClause msSQLServerAlterColumnClause = tempAlterColumnClause.toMSSQLServer();
            tempAlterTable.setAlterColumnClause(msSQLServerAlterColumnClause);
        }
        if (tempAlterTable.getRename() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setAsOrTo(null);
        tempAlterTable.setToTableName(null);
        tempAlterTable.setRenameColumn(null);
        tempAlterTable.setTableName(null);
        tempAlterTable.setToColumn(null);
        tempAlterTable.setColumnName(null);
        if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setStorage(null);
        tempAlterTable.setStorageClause(null);
        if (tempAlterTable.getMove() != null || tempAlterTable.getTruncate() != null || tempAlterTable.getSplit() != null || tempAlterTable.getExchange() != null || tempAlterTable.getAllocateExtent() != null || tempAlterTable.getDeAllocateUnused() != null) {
            throw new ConvertException();
        }
        tempAlterTable.setTruncate(null);
        tempAlterTable.setSplit(null);
        tempAlterTable.setExchange(null);
        tempAlterTable.setPartition(null);
        tempAlterTable.setAllocateExtent(null);
        tempAlterTable.setAllocateExtentIdentifier(null);
        tempAlterTable.setDeAllocateUnused(null);
        tempAlterTable.setDeAllocateUnusedIdentifier(null);
        if (tempAlterTable.getOwner() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setOwnerTo(null);
        tempAlterTable.setOwnerName(null);
        if (tempAlterTable.getChange() != null) {
            tempAlterTable.setChange("ALTER");
            tempAlterTable.setChangeColumn("COLUMN");
            if (tempAlterTable.getOrigColumn() != null) {
                String tempOrigCol = tempAlterTable.getOrigColumn();
                if (tempOrigCol.startsWith("`") && tempOrigCol.endsWith("`")) {
                    tempOrigCol = tempOrigCol.substring(1, tempOrigCol.length() - 1);
                    tempAlterTable.setOrigColumn(tempOrigCol);
                }
            }
        }
        if (tempAlterTable.getCreateColumn() != null) {
            final CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
            tempCreateColumn.toMSSQLServerString();
        }
        if (tempAlterTable.getCheckOrNoCheck() != null) {
            tempAlterTable.getCheckOrNoCheck();
        }
        if (tempAlterTable.getConstraint() != null) {
            tempAlterTable.getConstraint();
        }
        if (tempAlterTable.getConstraintAll() != null) {
            tempAlterTable.getConstraintAll();
        }
        if (tempAlterTable.getConstraintNameVector() != null) {
            final Vector tempConstraintNameVector = tempAlterTable.getConstraintNameVector();
            for (int i = 0; i < tempConstraintNameVector.size(); ++i) {
                final String s = tempConstraintNameVector.get(i);
            }
        }
        if (tempAlterTable.getEnableOrDisable() != null) {
            tempAlterTable.getEnableOrDisable();
        }
        if (tempAlterTable.getTrigger() != null) {
            tempAlterTable.getTriggerAll();
        }
        if (tempAlterTable.getTriggerAll() != null) {
            tempAlterTable.getTriggerAll();
        }
        if (tempAlterTable.getTriggerNameVector() != null) {
            final Vector tempTriggerNameVector = tempAlterTable.getTriggerNameVector();
            for (int i = 0; i < tempTriggerNameVector.size(); ++i) {
                final CreateColumn tempCreateColumn2 = tempTriggerNameVector.get(i);
                tempCreateColumn2.toMSSQLServerString();
            }
        }
        if (this.getWith() != null) {
            final String tempWith = this.getWith();
            tempAlterTable.setWith(tempWith);
        }
        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
            final Vector msSQLServerPhysicalAttributesClauseVector = new Vector();
            final Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();
            for (int j = 0; j < tempPhysicalAttributesClauseVector.size(); ++j) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = tempPhysicalAttributesClauseVector.get(j);
                final PhysicalAttributesClause msSQLServerPhysicalAttributesClause = tempPhysicalAttributesClause.toMSSQLServer();
                msSQLServerPhysicalAttributesClauseVector.add(msSQLServerPhysicalAttributesClause);
            }
            tempAlterTable.setPhysicalAttributesClauseVector(msSQLServerPhysicalAttributesClauseVector);
            final PhysicalAttributesClause tempPhysicalAttributesClause2 = tempPhysicalAttributesClauseVector.get(0);
            final PhysicalAttributesClause msSQLServerPhysicalAttributesClause2 = tempPhysicalAttributesClause2.toMSSQLServer();
            if (msSQLServerPhysicalAttributesClause2.getWith() == null && (msSQLServerPhysicalAttributesClause2.getFillFactor() != null || msSQLServerPhysicalAttributesClause2.getPadIndex() != null || (msSQLServerPhysicalAttributesClause2.getDiskAttr() != null && msSQLServerPhysicalAttributesClause2.getDiskAttr().get("IGNORE_DUP_KEY") != null) || msSQLServerPhysicalAttributesClause2.getDropExisting() != null || msSQLServerPhysicalAttributesClause2.getStatisticsNoreCompute() != null)) {
                tempAlterTable.setWith("WITH");
            }
        }
        tempAlterTable.setComma(",");
        return tempAlterTable;
    }
    
    public AlterTable toSybase() throws ConvertException {
        final AlterTable tempAlterTable = this.copyObjectValues();
        if (tempAlterTable.getAddClause() != null) {
            final AddClause tempAddClause = tempAlterTable.getAddClause();
            final AddClause sybaseAddClause = tempAddClause.toSybase();
            tempAlterTable.setAddClause(sybaseAddClause);
        }
        tempAlterTable.setData(null);
        tempAlterTable.setCapture(null);
        tempAlterTable.setNoneOrChanges(null);
        if (tempAlterTable.getModifyClause() != null) {
            final ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
            final ModifyClause sybaseModifyClause = tempModifyClause.toSybase();
            tempAlterTable.setModifyClause(sybaseModifyClause);
        }
        if (tempAlterTable.getDropClause() != null) {
            final DropClause tempDropClause = tempAlterTable.getDropClause();
            final DropClause sybaseDropClause = tempDropClause.toSybase();
            tempAlterTable.setDropClause(sybaseDropClause);
        }
        if (tempAlterTable.getAlterColumnClause() != null) {
            final AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
            final AlterColumnClause sybaseAlterColumnClause = tempAlterColumnClause.toSybase();
            tempAlterTable.setAlterColumnClause(sybaseAlterColumnClause);
        }
        if (tempAlterTable.getRename() != null) {
            tempAlterTable.getRename();
        }
        if (tempAlterTable.getAsOrTo() != null) {
            String tempAsOrTo = tempAlterTable.getAsOrTo();
            tempAsOrTo = tempAsOrTo.trim();
            if (tempAsOrTo.equalsIgnoreCase("AS")) {
                tempAlterTable.setAsOrTo("TO");
            }
            else {
                tempAlterTable.setAsOrTo(tempAsOrTo);
            }
        }
        if (tempAlterTable.getTableName() != null) {
            tempAlterTable.getTableName();
        }
        if (tempAlterTable.getToColumn() != null) {
            tempAlterTable.getToColumn();
        }
        if (tempAlterTable.getColumnName() != null) {
            tempAlterTable.getColumnName();
        }
        tempAlterTable.setRenameColumn(null);
        tempAlterTable.setToTableName(null);
        if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setStorage(null);
        tempAlterTable.setStorageClause(null);
        if (tempAlterTable.getMove() != null || tempAlterTable.getTruncate() != null || tempAlterTable.getSplit() != null || tempAlterTable.getExchange() != null || tempAlterTable.getAllocateExtent() != null || tempAlterTable.getDeAllocateUnused() != null) {
            throw new ConvertException();
        }
        tempAlterTable.setTruncate(null);
        tempAlterTable.setSplit(null);
        tempAlterTable.setExchange(null);
        tempAlterTable.setPartition(null);
        tempAlterTable.setAllocateExtent(null);
        tempAlterTable.setAllocateExtentIdentifier(null);
        tempAlterTable.setDeAllocateUnused(null);
        tempAlterTable.setDeAllocateUnusedIdentifier(null);
        if (tempAlterTable.getOwner() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setOwnerTo(null);
        tempAlterTable.setOwnerName(null);
        if (tempAlterTable.getChange() != null) {
            tempAlterTable.setChange("ALTER");
            tempAlterTable.setChangeColumn("COLUMN");
        }
        if (tempAlterTable.getCreateColumn() != null) {
            final CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
            tempCreateColumn.toSybaseString();
        }
        if (tempAlterTable.getCheckOrNoCheck() != null) {
            tempAlterTable.getCheckOrNoCheck();
        }
        if (tempAlterTable.getConstraint() != null) {
            tempAlterTable.getConstraint();
        }
        if (tempAlterTable.getConstraintAll() != null) {
            tempAlterTable.getConstraintAll();
        }
        if (tempAlterTable.getConstraintNameVector() != null) {
            final Vector tempConstraintNameVector = tempAlterTable.getConstraintNameVector();
            for (int i = 0; i < tempConstraintNameVector.size(); ++i) {
                final String s = tempConstraintNameVector.get(i);
            }
        }
        if (tempAlterTable.getEnableOrDisable() != null) {
            tempAlterTable.getEnableOrDisable();
        }
        if (tempAlterTable.getTrigger() != null) {
            tempAlterTable.getTriggerAll();
        }
        if (tempAlterTable.getTriggerAll() != null) {
            tempAlterTable.getTriggerAll();
        }
        if (tempAlterTable.getTriggerNameVector() != null) {
            final Vector tempTriggerNameVector = tempAlterTable.getTriggerNameVector();
            for (int i = 0; i < tempTriggerNameVector.size(); ++i) {
                final CreateColumn tempCreateColumn2 = tempTriggerNameVector.get(i);
                tempCreateColumn2.toSybaseString();
            }
        }
        if (this.getWith() != null) {
            final String tempWith = this.getWith();
            tempAlterTable.setWith(tempWith);
        }
        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
            tempAlterTable.setPhysicalAttributesClauseVector(null);
        }
        tempAlterTable.setComma(null);
        return tempAlterTable;
    }
    
    public AlterTable toDB2() throws ConvertException {
        final AlterTable tempAlterTable = this.copyObjectValues();
        if (tempAlterTable.getAddClause() != null) {
            final AddClause tempAddClause = tempAlterTable.getAddClause();
            tempAddClause.setDatatypeMapping(this.datatypeMapping);
            tempAddClause.setStmtTableName(this.alterTableName);
            final AddClause db2AddClause = tempAddClause.toDB2();
            tempAlterTable.setAddClause(db2AddClause);
        }
        if (tempAlterTable.getData() != null) {
            tempAlterTable.getData();
        }
        if (tempAlterTable.getCapture() != null) {
            tempAlterTable.getCapture();
        }
        if (tempAlterTable.getNoneOrChanges() != null) {
            tempAlterTable.getNoneOrChanges();
        }
        if (tempAlterTable.getDropClause() != null) {
            final DropClause tempDropClause = tempAlterTable.getDropClause();
            final DropClause db2DropClause = tempDropClause.toDB2();
            tempAlterTable.setDropClause(db2DropClause);
        }
        if (tempAlterTable.getAlterColumnClause() != null) {
            final AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
            final AlterColumnClause db2AlterColumnClause = tempAlterColumnClause.toDB2();
            tempAlterTable.setAlterColumnClause(db2AlterColumnClause);
        }
        if (tempAlterTable.getModifyClause() != null) {
            final ModifyClause mc = tempAlterTable.getModifyClause();
            final AlterColumnClause alterClause = new AlterColumnClause();
            alterClause.setAlter("ALTER");
            alterClause.setColumn("COLUMN");
            final Vector createColVec = mc.getCreateColumnVector();
            if (createColVec != null) {
                final CreateColumn cc = createColVec.get(0);
                cc.setDatatypeMapping(this.datatypeMapping);
                cc.toDB2String();
                alterClause.setCreateColumn(cc);
                if (cc.getDefault() != null) {
                    alterClause.setDefaultString(cc.getDefault());
                    cc.setDefault(null);
                    alterClause.setSetString("SET");
                }
                if (cc.getDefaultValue() != null) {
                    alterClause.setDefaultValue(cc.getDefaultValue());
                    cc.setDefaultValue(null);
                }
                if (cc.getDatatype() != null) {
                    final Datatype dt = cc.getDatatype();
                    alterClause.setSetString("SET");
                    alterClause.setDataTypeStr("DATA TYPE");
                    dt.toDB2String();
                    cc.setDatatype(null);
                    alterClause.setDataType(dt);
                }
            }
            tempAlterTable.setAlterColumnClause(alterClause);
            tempAlterTable.setModifyClause(null);
        }
        if (tempAlterTable.getRename() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setAsOrTo(null);
        tempAlterTable.setToTableName(null);
        tempAlterTable.setRenameColumn(null);
        tempAlterTable.setTableName(null);
        tempAlterTable.setToColumn(null);
        tempAlterTable.setColumnName(null);
        if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setStorage(null);
        tempAlterTable.setStorageClause(null);
        if (tempAlterTable.getMove() != null || tempAlterTable.getTruncate() != null || tempAlterTable.getSplit() != null || tempAlterTable.getExchange() != null || tempAlterTable.getAllocateExtent() != null || tempAlterTable.getDeAllocateUnused() != null) {
            throw new ConvertException();
        }
        tempAlterTable.setTruncate(null);
        tempAlterTable.setSplit(null);
        tempAlterTable.setExchange(null);
        tempAlterTable.setPartition(null);
        tempAlterTable.setAllocateExtent(null);
        tempAlterTable.setAllocateExtentIdentifier(null);
        tempAlterTable.setDeAllocateUnused(null);
        tempAlterTable.setDeAllocateUnusedIdentifier(null);
        if (tempAlterTable.getOwner() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setOwnerTo(null);
        tempAlterTable.setOwnerName(null);
        if (tempAlterTable.getChange() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setChangeColumn(null);
        tempAlterTable.setCreateColumn(null);
        if (tempAlterTable.getCheckOrNoCheck() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setConstraint(null);
        tempAlterTable.setConstraintAll(null);
        tempAlterTable.setConstraintNameVector(null);
        if (tempAlterTable.getEnableOrDisable() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
            final Vector db2PhysicalAttributesClauseVector = new Vector();
            final Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();
            for (int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = tempPhysicalAttributesClauseVector.get(i);
                final PhysicalAttributesClause db2PhysicalAttributesClause = tempPhysicalAttributesClause.toDB2();
                db2PhysicalAttributesClauseVector.add(db2PhysicalAttributesClause);
            }
            tempAlterTable.setPhysicalAttributesClauseVector(db2PhysicalAttributesClauseVector);
        }
        tempAlterTable.setWith(null);
        tempAlterTable.setTrigger(null);
        tempAlterTable.setTriggerAll(null);
        tempAlterTable.setTriggerNameVector(null);
        if (tempAlterTable.getAddClause() != null) {
            final Vector columnVector = tempAlterTable.getAddClause().getCreateColumnVector();
            if (columnVector != null) {
                final Vector constrClauseVector = columnVector.get(0).getConstraintClause();
                if (constrClauseVector != null) {
                    final ConstraintType constrType = constrClauseVector.get(0).getConstraintType();
                    if (constrType != null) {
                        if (constrType instanceof DefaultConstraintClause) {
                            if (((DefaultConstraintClause)constrType).getForClause() != null) {
                                throw new ConvertException("Conversion Failure.. DB2 does not support Alter / Modify Clause for a Column in a table with default value");
                            }
                        }
                        else if (constrType instanceof PrimaryOrUniqueConstraintClause) {
                            columnVector.get(0).setNullStatus(null);
                        }
                    }
                }
            }
        }
        return tempAlterTable;
    }
    
    public AlterTable toANSI() throws ConvertException {
        final AlterTable tempAlterTable = this.copyObjectValues();
        if (tempAlterTable.getAddClause() != null) {
            final AddClause tempAddClause = tempAlterTable.getAddClause();
            final AddClause ansiAddClause = tempAddClause.toANSI();
            tempAlterTable.setAddClause(ansiAddClause);
        }
        if (tempAlterTable.getDropClause() != null) {
            final DropClause tempDropClause = tempAlterTable.getDropClause();
            final DropClause ansiDropClause = tempDropClause.toANSI();
            tempAlterTable.setDropClause(ansiDropClause);
        }
        if (tempAlterTable.getAlterColumnClause() != null) {
            final AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
            final AlterColumnClause ansiAlterColumnClause = tempAlterColumnClause.toANSI();
            tempAlterTable.setAlterColumnClause(ansiAlterColumnClause);
        }
        if (tempAlterTable.getModifyClause() != null) {
            final ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
            final ModifyClause ansiModifyClause = tempModifyClause.toANSI();
            tempAlterTable.setModifyClause(ansiModifyClause);
        }
        tempAlterTable.setData(null);
        tempAlterTable.setCapture(null);
        tempAlterTable.setNoneOrChanges(null);
        if (tempAlterTable.getRename() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setAsOrTo(null);
        tempAlterTable.setToTableName(null);
        tempAlterTable.setRenameColumn(null);
        tempAlterTable.setTableName(null);
        tempAlterTable.setToColumn(null);
        tempAlterTable.setColumnName(null);
        if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setStorage(null);
        tempAlterTable.setStorageClause(null);
        if (tempAlterTable.getMove() != null || tempAlterTable.getTruncate() != null || tempAlterTable.getSplit() != null || tempAlterTable.getExchange() != null || tempAlterTable.getAllocateExtent() != null || tempAlterTable.getDeAllocateUnused() != null) {
            throw new ConvertException();
        }
        tempAlterTable.setTruncate(null);
        tempAlterTable.setSplit(null);
        tempAlterTable.setExchange(null);
        tempAlterTable.setPartition(null);
        tempAlterTable.setAllocateExtent(null);
        tempAlterTable.setAllocateExtentIdentifier(null);
        tempAlterTable.setDeAllocateUnused(null);
        tempAlterTable.setDeAllocateUnusedIdentifier(null);
        if (tempAlterTable.getOwner() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setOwnerTo(null);
        tempAlterTable.setOwnerName(null);
        if (tempAlterTable.getChange() != null) {
            tempAlterTable.setChange("ALTER");
        }
        if (tempAlterTable.getChangeColumn() != null) {
            tempAlterTable.getChangeColumn();
        }
        if (tempAlterTable.getCreateColumn() != null) {
            final CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
            tempCreateColumn.toANSIString();
        }
        if (tempAlterTable.getCheckOrNoCheck() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setConstraint(null);
        tempAlterTable.setConstraintAll(null);
        tempAlterTable.setConstraintNameVector(null);
        if (tempAlterTable.getEnableOrDisable() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
            final Vector ansiPhysicalAttributesClauseVector = new Vector();
            final Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();
            for (int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = tempPhysicalAttributesClauseVector.get(i);
                final PhysicalAttributesClause ansiPhysicalAttributesClause = tempPhysicalAttributesClause.toANSI();
                ansiPhysicalAttributesClauseVector.add(ansiPhysicalAttributesClause);
            }
            tempAlterTable.setPhysicalAttributesClauseVector(ansiPhysicalAttributesClauseVector);
        }
        tempAlterTable.setWith(null);
        tempAlterTable.setTrigger(null);
        tempAlterTable.setTriggerAll(null);
        tempAlterTable.setTriggerNameVector(null);
        return tempAlterTable;
    }
    
    public AlterTable toInformix() throws ConvertException {
        final AlterTable tempAlterTable = this.copyObjectValues();
        if (tempAlterTable.getAddClause() != null) {
            final AddClause tempAddClause = tempAlterTable.getAddClause();
            final AddClause informixAddClause = tempAddClause.toInformix();
            tempAlterTable.setAddClause(informixAddClause);
        }
        tempAlterTable.setData(null);
        tempAlterTable.setCapture(null);
        tempAlterTable.setNoneOrChanges(null);
        if (tempAlterTable.getModifyClause() != null) {
            final ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
            final ModifyClause informixModifyClause = tempModifyClause.toInformix();
            tempAlterTable.setModifyClause(informixModifyClause);
        }
        if (tempAlterTable.getDropClause() != null) {
            final DropClause tempDropClause = tempAlterTable.getDropClause();
            final DropClause informixDropClause = tempDropClause.toInformix();
            tempAlterTable.setDropClause(informixDropClause);
        }
        if (tempAlterTable.getAlterColumnClause() != null) {
            final AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
            final AlterColumnClause informixAlterColumnClause = tempAlterColumnClause.toInformix();
            tempAlterTable.setAlterColumnClause(informixAlterColumnClause);
        }
        tempAlterTable.setData(null);
        tempAlterTable.setCapture(null);
        tempAlterTable.setNoneOrChanges(null);
        if (tempAlterTable.getRename() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setAsOrTo(null);
        tempAlterTable.setToTableName(null);
        tempAlterTable.setRenameColumn(null);
        tempAlterTable.setTableName(null);
        tempAlterTable.setToColumn(null);
        tempAlterTable.setColumnName(null);
        if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setStorage(null);
        tempAlterTable.setStorageClause(null);
        if (tempAlterTable.getMove() != null || tempAlterTable.getTruncate() != null || tempAlterTable.getSplit() != null || tempAlterTable.getExchange() != null || tempAlterTable.getAllocateExtent() != null || tempAlterTable.getDeAllocateUnused() != null) {
            throw new ConvertException();
        }
        tempAlterTable.setTruncate(null);
        tempAlterTable.setSplit(null);
        tempAlterTable.setExchange(null);
        tempAlterTable.setPartition(null);
        tempAlterTable.setAllocateExtent(null);
        tempAlterTable.setAllocateExtentIdentifier(null);
        tempAlterTable.setDeAllocateUnused(null);
        tempAlterTable.setDeAllocateUnusedIdentifier(null);
        if (tempAlterTable.getOwner() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setOwnerTo(null);
        tempAlterTable.setOwnerName(null);
        if (tempAlterTable.getChange() != null) {
            tempAlterTable.setChange("MODIFY");
        }
        if (tempAlterTable.getChangeColumn() != null) {
            tempAlterTable.getChangeColumn();
        }
        if (tempAlterTable.getCreateColumn() != null) {
            final CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
            tempCreateColumn.toInformixString();
        }
        if (tempAlterTable.getCheckOrNoCheck() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setConstraint(null);
        tempAlterTable.setConstraintAll(null);
        tempAlterTable.setConstraintNameVector(null);
        if (tempAlterTable.getEnableOrDisable() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
            final Vector informixPhysicalAttributesClauseVector = new Vector();
            final Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();
            for (int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = tempPhysicalAttributesClauseVector.get(i);
                final PhysicalAttributesClause informixPhysicalAttributesClause = tempPhysicalAttributesClause.toInformix();
                informixPhysicalAttributesClauseVector.add(informixPhysicalAttributesClause);
            }
            tempAlterTable.setPhysicalAttributesClauseVector(informixPhysicalAttributesClauseVector);
        }
        tempAlterTable.setWith(null);
        tempAlterTable.setTrigger(null);
        tempAlterTable.setTriggerAll(null);
        tempAlterTable.setTriggerNameVector(null);
        return tempAlterTable;
    }
    
    public AlterTable toPostgreSQL() throws ConvertException {
        final AlterTable tempAlterTable = this.copyObjectValues();
        if (tempAlterTable.getAddClause() != null) {
            final AddClause tempAddClause = tempAlterTable.getAddClause();
            final AddClause postgreAddClause = tempAddClause.toPostgreSQL();
            tempAlterTable.setAddClause(postgreAddClause);
        }
        if (tempAlterTable.getDropClause() != null) {
            final DropClause tempDropClause = tempAlterTable.getDropClause();
            final DropClause postgreDropClause = tempDropClause.toPostgreSQL();
            tempAlterTable.setDropClause(postgreDropClause);
        }
        if (tempAlterTable.getAlterColumnClause() != null) {
            final AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
            final AlterColumnClause postgreAlterColumnClause = tempAlterColumnClause.toPostgreSQL();
            tempAlterTable.setAlterColumnClause(postgreAlterColumnClause);
        }
        if (tempAlterTable.getRename() != null) {
            tempAlterTable.getRename();
        }
        tempAlterTable.setToTableName(null);
        if (tempAlterTable.getAsOrTo() != null) {
            String tempAsOrTo = tempAlterTable.getAsOrTo();
            tempAsOrTo = tempAsOrTo.trim();
            if (tempAsOrTo.equalsIgnoreCase("AS")) {
                tempAlterTable.setAsOrTo("TO");
            }
            else {
                tempAlterTable.setAsOrTo(tempAsOrTo);
            }
        }
        if (tempAlterTable.getRenameColumn() != null) {
            tempAlterTable.getRenameColumn();
        }
        if (tempAlterTable.getTableName() != null) {
            tempAlterTable.getTableName();
        }
        if (tempAlterTable.getToColumn() != null) {
            tempAlterTable.getToColumn();
        }
        if (tempAlterTable.getColumnName() != null) {
            tempAlterTable.getColumnName();
        }
        if (tempAlterTable.getModifyClause() != null) {
            final ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
            final ModifyClause postgreSQLModifyClause = tempModifyClause.toPostgreSQL();
            tempAlterTable.setModifyClause(postgreSQLModifyClause);
        }
        tempAlterTable.setData(null);
        tempAlterTable.setCapture(null);
        tempAlterTable.setNoneOrChanges(null);
        if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setStorage(null);
        tempAlterTable.setStorageClause(null);
        if (tempAlterTable.getMove() != null || tempAlterTable.getTruncate() != null || tempAlterTable.getSplit() != null || tempAlterTable.getExchange() != null || tempAlterTable.getAllocateExtent() != null || tempAlterTable.getDeAllocateUnused() != null) {
            throw new ConvertException();
        }
        tempAlterTable.setTruncate(null);
        tempAlterTable.setSplit(null);
        tempAlterTable.setExchange(null);
        tempAlterTable.setPartition(null);
        tempAlterTable.setAllocateExtent(null);
        tempAlterTable.setAllocateExtentIdentifier(null);
        tempAlterTable.setDeAllocateUnused(null);
        tempAlterTable.setDeAllocateUnusedIdentifier(null);
        if (tempAlterTable.getOwner() != null) {
            tempAlterTable.getOwner();
        }
        if (tempAlterTable.getOwnerTo() != null) {
            tempAlterTable.getOwnerTo();
        }
        if (tempAlterTable.getOwnerName() != null) {
            tempAlterTable.getOwnerName();
        }
        if (tempAlterTable.getChange() != null) {
            tempAlterTable.setChange("ALTER");
        }
        if (tempAlterTable.getChangeColumn() != null) {
            tempAlterTable.getChangeColumn();
        }
        if (tempAlterTable.getCreateColumn() != null) {
            final CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
            tempCreateColumn.toPostgreSQLString();
        }
        if (tempAlterTable.getCheckOrNoCheck() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setConstraint(null);
        tempAlterTable.setConstraintAll(null);
        tempAlterTable.setConstraintNameVector(null);
        if (tempAlterTable.getEnableOrDisable() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
            final Vector postgreSQLPhysicalAttributesClauseVector = new Vector();
            final Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();
            for (int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = tempPhysicalAttributesClauseVector.get(i);
                final PhysicalAttributesClause postgreSQLPhysicalAttributesClause = tempPhysicalAttributesClause.toPostgreSQL();
                postgreSQLPhysicalAttributesClauseVector.add(postgreSQLPhysicalAttributesClause);
            }
            tempAlterTable.setPhysicalAttributesClauseVector(postgreSQLPhysicalAttributesClauseVector);
        }
        tempAlterTable.setWith(null);
        tempAlterTable.setTrigger(null);
        tempAlterTable.setTriggerAll(null);
        tempAlterTable.setTriggerNameVector(null);
        return tempAlterTable;
    }
    
    public AlterTable toMySQL() throws ConvertException {
        final AlterTable tempAlterTable = this.copyObjectValues();
        if (tempAlterTable.getAddClause() != null) {
            final AddClause tempAddClause = tempAlterTable.getAddClause();
            final AddClause mySQLAddClause = tempAddClause.toMySQL();
            tempAlterTable.setAddClause(mySQLAddClause);
        }
        if (tempAlterTable.getDropClause() != null) {
            final DropClause tempDropClause = tempAlterTable.getDropClause();
            final DropClause mySQLDropClause = tempDropClause.toMySQL();
            tempAlterTable.setDropClause(mySQLDropClause);
        }
        if (tempAlterTable.getAlterColumnClause() != null) {
            final AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
            final AlterColumnClause mySQLAlterColumnClause = tempAlterColumnClause.toMySQL();
            tempAlterTable.setAlterColumnClause(mySQLAlterColumnClause);
        }
        if (tempAlterTable.getModifyClause() != null) {
            final ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
            final ModifyClause mySQLModifyClause = tempModifyClause.toMySQL();
            tempAlterTable.setModifyClause(mySQLModifyClause);
        }
        if (tempAlterTable.getRename() != null) {
            tempAlterTable.getRename();
        }
        if (this.toTableName != null) {
            tempAlterTable.setToTableName("TO");
        }
        if (tempAlterTable.getAsOrTo() != null) {
            String tempAsOrTo = tempAlterTable.getAsOrTo();
            tempAsOrTo = tempAsOrTo.trim();
            if (tempAsOrTo.equalsIgnoreCase("As")) {
                tempAlterTable.setAsOrTo("TO");
            }
            else {
                tempAlterTable.setAsOrTo(tempAsOrTo);
            }
        }
        if (tempAlterTable.getRenameColumn() != null || tempAlterTable.getColumnName() != null) {
            tempAlterTable.setToColumn(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempAlterTable.getToColumn() != null) {
            tempAlterTable.getToColumn();
        }
        if (tempAlterTable.getTableName() != null) {
            tempAlterTable.getTableName();
        }
        tempAlterTable.setData(null);
        tempAlterTable.setCapture(null);
        tempAlterTable.setNoneOrChanges(null);
        if (tempAlterTable.getOwner() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setOwnerTo(null);
        tempAlterTable.setOwnerName(null);
        if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setStorage(null);
        tempAlterTable.setStorageClause(null);
        if (tempAlterTable.getMove() != null || tempAlterTable.getTruncate() != null || tempAlterTable.getSplit() != null || tempAlterTable.getExchange() != null || tempAlterTable.getAllocateExtent() != null || tempAlterTable.getDeAllocateUnused() != null) {
            throw new ConvertException();
        }
        tempAlterTable.setTruncate(null);
        tempAlterTable.setSplit(null);
        tempAlterTable.setExchange(null);
        tempAlterTable.setPartition(null);
        tempAlterTable.setAllocateExtent(null);
        tempAlterTable.setAllocateExtentIdentifier(null);
        tempAlterTable.setDeAllocateUnused(null);
        tempAlterTable.setDeAllocateUnusedIdentifier(null);
        if (tempAlterTable.getChange() != null) {
            tempAlterTable.getChange();
        }
        if (tempAlterTable.getChangeColumn() != null) {
            tempAlterTable.getChangeColumn();
        }
        if (tempAlterTable.getCreateColumn() != null) {
            final CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
            tempCreateColumn.toMySQLString();
        }
        if (tempAlterTable.getCheckOrNoCheck() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setConstraint(null);
        tempAlterTable.setConstraintAll(null);
        tempAlterTable.setConstraintNameVector(null);
        if (tempAlterTable.getEnableOrDisable() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
            final Vector mySQLPhysicalAttributesClauseVector = new Vector();
            final Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();
            for (int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = tempPhysicalAttributesClauseVector.get(i);
                final PhysicalAttributesClause mySQLPhysicalAttributesClause = tempPhysicalAttributesClause.toMySQL();
                mySQLPhysicalAttributesClauseVector.add(mySQLPhysicalAttributesClause);
            }
            tempAlterTable.setPhysicalAttributesClauseVector(mySQLPhysicalAttributesClauseVector);
        }
        tempAlterTable.setWith(null);
        tempAlterTable.setTrigger(null);
        tempAlterTable.setTriggerAll(null);
        tempAlterTable.setTriggerNameVector(null);
        return tempAlterTable;
    }
    
    public AlterTable toTimesTen() throws ConvertException {
        final AlterTable tempAlterTable = this.copyObjectValues();
        if (tempAlterTable.getAddClause() != null) {
            final AddClause tempAddClause = tempAlterTable.getAddClause();
            final AddClause timesTenAddClause = tempAddClause.toTimesTen();
            tempAlterTable.setAddClause(timesTenAddClause);
        }
        tempAlterTable.setData(null);
        tempAlterTable.setCapture(null);
        tempAlterTable.setNoneOrChanges(null);
        if (tempAlterTable.getModifyClause() != null) {
            throw new ConvertException("\nMODIFY clause is not supported in TimesTen 5.1.21\n");
        }
        if (tempAlterTable.getDropClause() != null) {
            final DropClause tempDropClause = tempAlterTable.getDropClause();
            final DropClause timesTenDropClause = tempDropClause.toTimesTen();
            tempAlterTable.setDropClause(timesTenDropClause);
        }
        if (tempAlterTable.getAlterColumnClause() != null) {}
        if (tempAlterTable.getCheckOrNoCheck() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setConstraint(null);
        tempAlterTable.setConstraintAll(null);
        tempAlterTable.setConstraintNameVector(null);
        if (tempAlterTable.getOwner() != null) {
            throw new ConvertException("\nUnsupported SQL in TimesTen 5.1.21\n");
        }
        tempAlterTable.setOwnerTo(null);
        tempAlterTable.setOwnerName(null);
        tempAlterTable.setChangeColumn(null);
        if (tempAlterTable.getCreateColumn() != null) {
            final CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
            tempCreateColumn.toTimesTenString();
        }
        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
            tempAlterTable.setPhysicalAttributesClauseVector(null);
        }
        tempAlterTable.setWith(null);
        tempAlterTable.setTriggerAll(null);
        tempAlterTable.setTriggerNameVector(null);
        return tempAlterTable;
    }
    
    public AlterTable toNetezza() throws ConvertException {
        final AlterTable tempAlterTable = this.copyObjectValues();
        if (tempAlterTable.getAddClause() != null) {
            final AddClause tempAddClause = tempAlterTable.getAddClause();
            final AddClause netezzaAddClause = tempAddClause.toNetezza();
            tempAlterTable.setAddClause(netezzaAddClause);
        }
        if (tempAlterTable.getDropClause() != null) {
            final DropClause tempDropClause = tempAlterTable.getDropClause();
            final DropClause netezzaDropClause = tempDropClause.toNetezza();
            tempAlterTable.setDropClause(netezzaDropClause);
        }
        if (tempAlterTable.getAlterColumnClause() != null) {
            final AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
            final AlterColumnClause netezzaAlterColumnClause = tempAlterColumnClause.toNetezza();
            tempAlterTable.setAlterColumnClause(netezzaAlterColumnClause);
        }
        if (tempAlterTable.getModifyClause() != null) {
            final ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
            final ModifyClause netezzaModifyClause = tempModifyClause.toNetezza();
            tempAlterTable.setModifyClause(netezzaModifyClause);
        }
        tempAlterTable.setData(null);
        tempAlterTable.setCapture(null);
        tempAlterTable.setNoneOrChanges(null);
        if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setStorage(null);
        tempAlterTable.setStorageClause(null);
        if (tempAlterTable.getMove() != null || tempAlterTable.getTruncate() != null || tempAlterTable.getSplit() != null || tempAlterTable.getExchange() != null || tempAlterTable.getAllocateExtent() != null || tempAlterTable.getDeAllocateUnused() != null) {
            throw new ConvertException("/*SwisSQL  Message - Statements which alter Oracle specific  properties are not converted*/");
        }
        tempAlterTable.setTruncate(null);
        tempAlterTable.setSplit(null);
        tempAlterTable.setExchange(null);
        tempAlterTable.setPartition(null);
        tempAlterTable.setAllocateExtent(null);
        tempAlterTable.setAllocateExtentIdentifier(null);
        tempAlterTable.setDeAllocateUnused(null);
        tempAlterTable.setDeAllocateUnusedIdentifier(null);
        if (tempAlterTable.getOwner() != null) {}
        if (tempAlterTable.getChange() != null) {
            tempAlterTable.setChange("ALTER");
        }
        if (tempAlterTable.getChangeColumn() != null) {
            tempAlterTable.getChangeColumn();
        }
        if (tempAlterTable.getCreateColumn() != null) {
            final CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
            tempCreateColumn.toNetezzaString();
        }
        if (tempAlterTable.getCheckOrNoCheck() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setConstraint(null);
        tempAlterTable.setConstraintAll(null);
        tempAlterTable.setConstraintNameVector(null);
        if (tempAlterTable.getEnableOrDisable() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
            final Vector netezzaPhysicalAttributesClauseVector = new Vector();
            final Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();
            for (int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = tempPhysicalAttributesClauseVector.get(i);
                final PhysicalAttributesClause netezzaPhysicalAttributesClause = tempPhysicalAttributesClause.toNetezza();
                netezzaPhysicalAttributesClauseVector.add(netezzaPhysicalAttributesClause);
            }
            tempAlterTable.setPhysicalAttributesClauseVector(netezzaPhysicalAttributesClauseVector);
        }
        tempAlterTable.setWith(null);
        tempAlterTable.setTrigger(null);
        tempAlterTable.setTriggerAll(null);
        tempAlterTable.setTriggerNameVector(null);
        return tempAlterTable;
    }
    
    public AlterTable toTeradata() throws ConvertException {
        final AlterTable tempAlterTable = this.copyObjectValues();
        if (tempAlterTable.getAddClause() != null) {
            final AddClause tempAddClause = tempAlterTable.getAddClause();
            final AddClause teradataAddClause = tempAddClause.toTeradata();
            tempAlterTable.setAddClause(teradataAddClause);
        }
        if (tempAlterTable.getDropClause() != null) {
            final DropClause tempDropClause = tempAlterTable.getDropClause();
            final DropClause teradataDropClause = tempDropClause.toTeradata();
            tempAlterTable.setDropClause(teradataDropClause);
        }
        if (tempAlterTable.getAlterColumnClause() != null) {
            final AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
            final AlterColumnClause teradataAlterColumnClause = tempAlterColumnClause.toTeradata();
            tempAlterTable.setAlterColumnClause(teradataAlterColumnClause);
        }
        if (tempAlterTable.getModifyClause() != null) {
            final ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
            final ModifyClause teradataModifyClause = tempModifyClause.toTeradata();
            tempAlterTable.setModifyClause(teradataModifyClause);
        }
        tempAlterTable.setData(null);
        tempAlterTable.setCapture(null);
        tempAlterTable.setNoneOrChanges(null);
        if (tempAlterTable.getRename() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setAsOrTo(null);
        tempAlterTable.setToTableName(null);
        tempAlterTable.setRenameColumn(null);
        tempAlterTable.setTableName(null);
        tempAlterTable.setToColumn(null);
        tempAlterTable.setColumnName(null);
        if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setStorage(null);
        tempAlterTable.setStorageClause(null);
        if (tempAlterTable.getMove() != null || tempAlterTable.getTruncate() != null || tempAlterTable.getSplit() != null || tempAlterTable.getExchange() != null || tempAlterTable.getAllocateExtent() != null || tempAlterTable.getDeAllocateUnused() != null) {
            throw new ConvertException();
        }
        tempAlterTable.setTruncate(null);
        tempAlterTable.setSplit(null);
        tempAlterTable.setExchange(null);
        tempAlterTable.setPartition(null);
        tempAlterTable.setAllocateExtent(null);
        tempAlterTable.setAllocateExtentIdentifier(null);
        tempAlterTable.setDeAllocateUnused(null);
        tempAlterTable.setDeAllocateUnusedIdentifier(null);
        if (tempAlterTable.getOwner() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setOwnerTo(null);
        tempAlterTable.setOwnerName(null);
        if (tempAlterTable.getChange() != null) {
            tempAlterTable.setChange("ALTER");
        }
        if (tempAlterTable.getChangeColumn() != null) {
            tempAlterTable.getChangeColumn();
        }
        if (tempAlterTable.getCreateColumn() != null) {
            final CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
            tempCreateColumn.toTeradataString();
        }
        if (tempAlterTable.getCheckOrNoCheck() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempAlterTable.setConstraint(null);
        tempAlterTable.setConstraintAll(null);
        tempAlterTable.setConstraintNameVector(null);
        if (tempAlterTable.getEnableOrDisable() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
            final Vector teradataPhysicalAttributesClauseVector = new Vector();
            final Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();
            for (int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = tempPhysicalAttributesClauseVector.get(i);
                final PhysicalAttributesClause teradataPhysicalAttributesClause = tempPhysicalAttributesClause.toTeradata();
                teradataPhysicalAttributesClauseVector.add(teradataPhysicalAttributesClause);
            }
            tempAlterTable.setPhysicalAttributesClauseVector(teradataPhysicalAttributesClauseVector);
        }
        tempAlterTable.setWith(null);
        tempAlterTable.setTrigger(null);
        tempAlterTable.setTriggerAll(null);
        tempAlterTable.setTriggerNameVector(null);
        return tempAlterTable;
    }
    
    public AlterTable copyObjectValues() {
        final AlterTable dupAlterTable = new AlterTable();
        dupAlterTable.setAddClause(this.getAddClause());
        dupAlterTable.setModifyClause(this.getModifyClause());
        dupAlterTable.setDropClause(this.getDropClause());
        dupAlterTable.setAlterColumnClause(this.getAlterColumnClause());
        dupAlterTable.setData(this.getData());
        dupAlterTable.setCapture(this.getCapture());
        dupAlterTable.setNoneOrChanges(this.getNoneOrChanges());
        dupAlterTable.setRename(this.getRename());
        dupAlterTable.setToTableName(this.toTableName);
        dupAlterTable.setAsOrTo(this.getAsOrTo());
        dupAlterTable.setRenameColumn(this.getRenameColumn());
        dupAlterTable.setTableName(this.getTableName());
        dupAlterTable.setToColumn(this.getToColumn());
        dupAlterTable.setColumnName(this.getColumnName());
        dupAlterTable.setObjectContext(this.context);
        dupAlterTable.setOverflow(this.getOverflow());
        dupAlterTable.setStorage(this.getStorage());
        dupAlterTable.setStorageClause(this.getStorageClause());
        dupAlterTable.setMove(this.getMove());
        dupAlterTable.setTruncate(this.getTruncate());
        dupAlterTable.setSplit(this.getSplit());
        dupAlterTable.setExchange(this.getExchange());
        dupAlterTable.setPartition(this.getPartition());
        dupAlterTable.setAllocateExtent(this.getAllocateExtent());
        dupAlterTable.setAllocateExtentIdentifier(this.getAllocateExtentIdentifier());
        dupAlterTable.setDeAllocateUnused(this.getDeAllocateUnused());
        dupAlterTable.setDeAllocateUnusedIdentifier(this.getDeAllocateUnusedIdentifier());
        dupAlterTable.setCheckOrNoCheck(this.getCheckOrNoCheck());
        dupAlterTable.setConstraint(this.getConstraint());
        dupAlterTable.setConstraintAll(this.getConstraintAll());
        dupAlterTable.setConstraintNameVector(this.getConstraintNameVector());
        dupAlterTable.setOwner(this.getOwner());
        dupAlterTable.setOwnerTo(this.getOwnerTo());
        dupAlterTable.setOwnerName(this.getOwnerName());
        dupAlterTable.setChange(this.getChange());
        dupAlterTable.setChangeColumn(this.getChangeColumn());
        dupAlterTable.setCreateColumn(this.getCreateColumn());
        dupAlterTable.setTriggerNameVector(this.getTriggerNameVector());
        dupAlterTable.setEnableOrDisable(this.getEnableOrDisable());
        dupAlterTable.setTrigger(this.getTrigger());
        dupAlterTable.setTriggerAll(this.getTriggerAll());
        dupAlterTable.setPhysicalAttributesClauseVector(this.getPhysicalAttributesClauseVector());
        dupAlterTable.setTableOption(this.getTableOption());
        dupAlterTable.setTableOptionParameter(this.getTableOptionParameter());
        dupAlterTable.setOrigColumn(this.getOrigColumn());
        return dupAlterTable;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.addClause != null) {
            this.addClause.setObjectContext(this.context);
            sb.append("\n" + this.addClause.toString());
        }
        if (this.dropClause != null) {
            this.dropClause.setObjectContext(this.context);
            sb.append("\n" + this.dropClause.toString());
        }
        if (this.data != null) {
            sb.append(" " + this.data.toUpperCase());
        }
        if (this.capture != null) {
            sb.append(" " + this.capture.toUpperCase());
        }
        if (this.noneOrChanges != null) {
            sb.append(" " + this.noneOrChanges);
        }
        if (this.modifyClause != null) {
            this.modifyClause.setObjectContext(this.context);
            sb.append("\n" + this.modifyClause.toString());
        }
        if (this.alterColumnClause != null) {
            this.alterColumnClause.setObjectContext(this.context);
            sb.append("\n" + this.alterColumnClause.toString());
        }
        if (this.rename != null) {
            sb.append("\n" + this.rename.toUpperCase());
        }
        if (this.renameColumn != null) {
            sb.append(" " + this.renameColumn.toUpperCase());
        }
        if (this.toTableName != null) {
            sb.append(" " + this.toTableName.toUpperCase());
        }
        if (this.tableName != null) {
            sb.append(" " + this.tableName);
        }
        if (this.asOrTo != null) {
            sb.append(" " + this.asOrTo.toUpperCase());
        }
        if (this.toColumn != null) {
            sb.append(" " + this.toColumn.toUpperCase());
        }
        if (this.columnName != null) {
            sb.append(" " + this.columnName);
        }
        if (this.overflow != null) {
            sb.append("\n" + this.overflow.toUpperCase());
        }
        if (this.storage != null) {
            sb.append(" " + this.storage.toUpperCase());
        }
        if (this.storageClauseString != null) {
            sb.append(" (" + this.storageClauseString + ")");
        }
        if (this.move != null) {
            sb.append("\n" + this.move.toUpperCase());
        }
        if (this.truncate != null) {
            sb.append(" " + this.truncate.toUpperCase());
        }
        if (this.split != null) {
            sb.append(" " + this.split.toUpperCase());
        }
        if (this.exchange != null) {
            sb.append(" " + this.exchange.toUpperCase());
        }
        if (this.partitionListAttributes != null) {
            sb.append("\n\t" + this.partitionListAttributes.toString());
        }
        if (this.allocateExtent != null) {
            sb.append("\n" + this.allocateExtent.toUpperCase());
        }
        if (this.allocateExtentIdentifier != null) {
            sb.append(" " + this.allocateExtentIdentifier);
        }
        if (this.deAllocateUnused != null) {
            sb.append("\n" + this.deAllocateUnused.toUpperCase());
        }
        if (this.deAllocateUnusedIdentifier != null) {
            sb.append(" " + this.deAllocateUnusedIdentifier);
        }
        if (this.checkOrNoCheck != null) {
            sb.append("\n" + this.checkOrNoCheck.toUpperCase());
        }
        if (this.constraint != null) {
            sb.append(" " + this.constraint.toUpperCase());
        }
        if (this.constraintAll != null) {
            sb.append(" " + this.constraintAll.toUpperCase());
        }
        if (this.constraintNameVector != null) {
            for (int i = 0; i < this.constraintNameVector.size(); ++i) {
                final String tempConstraintName = this.constraintNameVector.get(i);
                if (i == 0) {
                    sb.append(" " + tempConstraintName);
                }
                else {
                    sb.append("," + tempConstraintName);
                }
            }
        }
        if (this.owner != null) {
            sb.append("\n" + this.owner.toUpperCase());
        }
        if (this.ownerTo != null) {
            sb.append(" " + this.ownerTo.toUpperCase());
        }
        if (this.ownerName != null) {
            sb.append(" " + this.ownerName);
        }
        if (this.change != null) {
            sb.append("\n" + this.change.toUpperCase());
        }
        if (this.changeColumn != null) {
            sb.append(" " + this.changeColumn.toUpperCase());
        }
        if (this.createColumn != null) {
            this.createColumn.setObjectContext(this.context);
            sb.append(" " + this.createColumn.toString());
        }
        if (this.enableOrDisable != null) {
            sb.append("\n" + this.enableOrDisable.toUpperCase());
        }
        if (this.trigger != null) {
            sb.append(" " + this.trigger.toUpperCase());
        }
        if (this.triggerAll != null) {
            sb.append(" " + this.triggerAll.toUpperCase());
        }
        if (this.triggerNameVector != null) {
            for (int i = 0; i < this.triggerNameVector.size(); ++i) {
                final CreateColumn tempCreateColumn = this.triggerNameVector.get(i);
                tempCreateColumn.setObjectContext(this.context);
                if (i == 0) {
                    sb.append("  " + tempCreateColumn.toString());
                }
                else {
                    sb.append("," + tempCreateColumn.toString());
                }
            }
        }
        if (this.with != null) {
            sb.append("\n" + this.with.toUpperCase());
        }
        if (this.physicalAttributesClauseVector != null) {
            for (int i = 0; i < this.physicalAttributesClauseVector.size(); ++i) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = this.physicalAttributesClauseVector.get(i);
                if (i == 0) {
                    sb.append("\n\t" + tempPhysicalAttributesClause.toString());
                }
                else if (this.comma != null) {
                    sb.append(",\n\t" + tempPhysicalAttributesClause.toString());
                }
                else {
                    sb.append("\n\t" + tempPhysicalAttributesClause.toString());
                }
            }
        }
        if (this.tableOption != null) {
            sb.append(this.tableOption);
        }
        return sb.toString();
    }
}
