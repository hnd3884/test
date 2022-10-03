package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintType;
import java.util.HashMap;
import com.adventnet.swissqlapi.sql.statement.create.PrimaryOrUniqueConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.PartitionListAttributes;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class ModifyClause
{
    private UserObjectContext context;
    private String modify;
    private String constraint;
    private String column;
    private String openBrace;
    private Vector createColumnVector;
    private String notNullStr;
    private String closedBrace;
    private String diskAttributes;
    private String physicalCharacteristics;
    private ConstraintClause constraintClause;
    private DropClause dropClause;
    private String storage;
    private String physicalStorageAttributes;
    private boolean isSQLServerAlterSet;
    private PartitionListAttributes partitionListAttributes;
    private String loggingOrNoLogging;
    private String monitoringOrNoMonitoring;
    private String allocateOrDeAllocate;
    private String allocateOrDeAllocateIdentifier;
    private String cacheOrNoCache;
    
    public ModifyClause() {
        this.context = null;
    }
    
    public void setModify(final String modify) {
        this.modify = modify;
    }
    
    public void setConstraint(final String constraint) {
        this.constraint = constraint;
    }
    
    public void setColumn(final String column) {
        this.column = column;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setOpenBrace(final String openBrace) {
        this.openBrace = openBrace;
    }
    
    public void setCreateColumnVector(final Vector createColumnVector) {
        this.createColumnVector = createColumnVector;
    }
    
    public void setConstraintClause(final ConstraintClause constraintClause) {
        this.constraintClause = constraintClause;
    }
    
    public void setClosedBrace(final String closedBrace) {
        this.closedBrace = closedBrace;
    }
    
    public void setDiskAttributes(final String diskAttributes) {
        this.diskAttributes = diskAttributes;
    }
    
    public void setPhysicalCharacteristics(final String physicalCharacteristics) {
        this.physicalCharacteristics = physicalCharacteristics;
    }
    
    public void setDropClause(final DropClause dropClause) {
        this.dropClause = dropClause;
    }
    
    public void setStorage(final String storage) {
        this.storage = storage;
    }
    
    public void setPhysicalStorageAttributes(final String physicalStorageAttributes) {
        this.physicalStorageAttributes = physicalStorageAttributes;
    }
    
    public void setPartition(final PartitionListAttributes partitionListAttributes) {
        this.partitionListAttributes = partitionListAttributes;
    }
    
    public void setLoggingOrNoLogging(final String loggingOrNoLogging) {
        this.loggingOrNoLogging = loggingOrNoLogging;
    }
    
    public void setMonitoringOrNoMonitoring(final String monitoringOrNoMonitoring) {
        this.monitoringOrNoMonitoring = monitoringOrNoMonitoring;
    }
    
    public void setAllocateOrDeAllocate(final String allocateOrDeAllocate) {
        this.allocateOrDeAllocate = allocateOrDeAllocate;
    }
    
    public void setAllocateOrDeAllocateIdentifier(final String allocateOrDeAllocateIdentifier) {
        this.allocateOrDeAllocateIdentifier = allocateOrDeAllocateIdentifier;
    }
    
    public void setCacheOrNoCache(final String cacheOrNoCache) {
        this.cacheOrNoCache = cacheOrNoCache;
    }
    
    public void setSQLServerMultipleQueries(final boolean isSQLServerAlterSet) {
        this.isSQLServerAlterSet = isSQLServerAlterSet;
    }
    
    public String getModify() {
        return this.modify;
    }
    
    public String getConstraint() {
        return this.constraint;
    }
    
    public String getColumn() {
        return this.column;
    }
    
    public ConstraintClause getConstraintClause() {
        return this.constraintClause;
    }
    
    public Vector getCreateColumnVector() {
        return this.createColumnVector;
    }
    
    public String getDiskAttributes() {
        return this.diskAttributes;
    }
    
    public String getPhysicalCharacteristics() {
        return this.physicalCharacteristics;
    }
    
    public DropClause getDropClause() {
        return this.dropClause;
    }
    
    public String getStorage() {
        return this.storage;
    }
    
    public String getPhysicalStorageAttributes() {
        return this.physicalStorageAttributes;
    }
    
    public PartitionListAttributes getPartition() {
        return this.partitionListAttributes;
    }
    
    public String getLoggingOrNoLogging() {
        return this.loggingOrNoLogging;
    }
    
    public String getMonitoringOrNoMonitoring() {
        return this.monitoringOrNoMonitoring;
    }
    
    public String getAllocateOrDeAllocate() {
        return this.allocateOrDeAllocate;
    }
    
    public String getAllocateOrDeAllocateIdentifier() {
        return this.allocateOrDeAllocateIdentifier;
    }
    
    public String getCacheOrNoCache() {
        return this.cacheOrNoCache;
    }
    
    public ModifyClause toOracle() throws ConvertException {
        final ModifyClause tempModifyClause = this.copyObjectValues();
        if (tempModifyClause.getModify() != null) {
            tempModifyClause.getModify();
        }
        tempModifyClause.setColumn(null);
        if (tempModifyClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = tempModifyClause.getCreateColumnVector();
            if (columnNamesVector.size() > 1) {
                tempModifyClause.setOpenBrace("(");
                tempModifyClause.setClosedBrace(")");
            }
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toOracleString();
                    String tempColumnName = changeCreateColumn.getColumnName();
                    if (tempColumnName != null) {
                        boolean addQuotes = false;
                        if (tempColumnName.startsWith("\"") && tempColumnName.endsWith("\"")) {
                            tempColumnName = tempColumnName.substring(1, tempColumnName.length() - 1);
                            addQuotes = true;
                        }
                        if (tempColumnName.length() > 30) {
                            tempColumnName = tempColumnName.substring(0, 30);
                            if (addQuotes) {
                                tempColumnName = "\"" + tempColumnName + "\"";
                            }
                            changeCreateColumn.setColumnName(tempColumnName);
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
                                            tempModifyClause.setDiskAttributes("PCTFREE " + percent);
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
                                toOracleConstraintClause.toOracleString();
                            }
                        }
                    }
                }
            }
            if (tempModifyClause.getPhysicalCharacteristics() != null) {
                tempModifyClause.getPhysicalCharacteristics();
            }
        }
        if (tempModifyClause.getDropClause() != null) {
            final DropClause tempDropClause = tempModifyClause.getDropClause();
            tempDropClause.toOracle();
        }
        if (tempModifyClause.getStorage() != null) {
            tempModifyClause.getStorage();
        }
        if (tempModifyClause.getPhysicalStorageAttributes() != null) {
            tempModifyClause.getPhysicalStorageAttributes();
        }
        if (tempModifyClause.getAllocateOrDeAllocate() != null) {
            tempModifyClause.getAllocateOrDeAllocate();
        }
        if (tempModifyClause.getAllocateOrDeAllocateIdentifier() != null) {
            tempModifyClause.getAllocateOrDeAllocateIdentifier();
        }
        if (tempModifyClause.getPartition() != null) {
            tempModifyClause.getPartition();
        }
        if (tempModifyClause.getCacheOrNoCache() != null) {
            tempModifyClause.getCacheOrNoCache();
        }
        if (tempModifyClause.getLoggingOrNoLogging() != null) {
            tempModifyClause.getLoggingOrNoLogging();
        }
        if (tempModifyClause.getMonitoringOrNoMonitoring() != null) {
            tempModifyClause.getMonitoringOrNoMonitoring();
        }
        return tempModifyClause;
    }
    
    public ModifyClause toMSSQLServer() throws ConvertException {
        final ModifyClause tempModifyClause = this.copyObjectValues();
        if (tempModifyClause.getModify() != null) {
            tempModifyClause.setModify("ALTER COLUMN");
        }
        if (tempModifyClause.getColumn() != null) {
            tempModifyClause.getColumn();
        }
        if (tempModifyClause.getCreateColumnVector() != null) {
            final String notNullStr = "";
            final Vector columnNamesVector = tempModifyClause.getCreateColumnVector();
            if (columnNamesVector.size() > 1) {
                tempModifyClause.setSQLServerMultipleQueries(true);
            }
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toMSSQLServerString();
                    final Vector getConstraintVector = changeCreateColumn.getConstraintClause();
                    if (getConstraintVector != null) {
                        for (int j = 0; j < getConstraintVector.size(); ++j) {
                            final ConstraintClause toSQLServerConstraintClause = getConstraintVector.get(j);
                            final ConstraintType toSQLServerConstraintType = toSQLServerConstraintClause.getConstraintType();
                            toSQLServerConstraintClause.toMSSQLServerString();
                            if (toSQLServerConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                int fillIntValue = 0;
                                final PrimaryOrUniqueConstraintClause pcc = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintType;
                                String fillfactor = tempModifyClause.getDiskAttributes();
                                String tempFillfactor = "";
                                if (fillfactor != null) {
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
                                    tempModifyClause.setDiskAttributes(null);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        tempModifyClause.setDropClause(null);
        if (tempModifyClause.getStorage() != null || tempModifyClause.getPhysicalStorageAttributes() != null || tempModifyClause.getLoggingOrNoLogging() != null || tempModifyClause.getMonitoringOrNoMonitoring() != null || tempModifyClause.getAllocateOrDeAllocate() != null || tempModifyClause.getCacheOrNoCache() != null || tempModifyClause.getPartition() != null) {
            throw new ConvertException();
        }
        tempModifyClause.setStorage(null);
        tempModifyClause.setPhysicalStorageAttributes(null);
        tempModifyClause.setLoggingOrNoLogging(null);
        tempModifyClause.setMonitoringOrNoMonitoring(null);
        tempModifyClause.setAllocateOrDeAllocate(null);
        tempModifyClause.setAllocateOrDeAllocateIdentifier(null);
        tempModifyClause.setCacheOrNoCache(null);
        tempModifyClause.setPartition(null);
        return tempModifyClause;
    }
    
    public ModifyClause toSybase() throws ConvertException {
        final ModifyClause tempModifyClause = this.copyObjectValues();
        if (tempModifyClause.getModify() != null) {
            tempModifyClause.setModify("MODIFY");
        }
        if (tempModifyClause.getColumn() != null) {
            tempModifyClause.getColumn();
        }
        if (tempModifyClause.getCreateColumnVector() != null) {
            final String notNullStr = "";
            final Vector columnNamesVector = tempModifyClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toSybaseString();
                    final Vector getConstraintVector = changeCreateColumn.getConstraintClause();
                    if (getConstraintVector != null) {
                        for (int j = 0; j < getConstraintVector.size(); ++j) {
                            final ConstraintClause toSQLServerConstraintClause = getConstraintVector.get(j);
                            final ConstraintType toSQLServerConstraintType = toSQLServerConstraintClause.getConstraintType();
                            toSQLServerConstraintClause.toSybaseString();
                            if (toSQLServerConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                int fillIntValue = 0;
                                final PrimaryOrUniqueConstraintClause pcc = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintType;
                                String fillfactor = tempModifyClause.getDiskAttributes();
                                String tempFillfactor = "";
                                if (fillfactor != null) {
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
                                    tempModifyClause.setDiskAttributes(null);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        tempModifyClause.setDropClause(null);
        if (tempModifyClause.getStorage() != null || tempModifyClause.getPhysicalStorageAttributes() != null || tempModifyClause.getLoggingOrNoLogging() != null || tempModifyClause.getMonitoringOrNoMonitoring() != null || tempModifyClause.getAllocateOrDeAllocate() != null || tempModifyClause.getCacheOrNoCache() != null || tempModifyClause.getPartition() != null) {
            throw new ConvertException();
        }
        tempModifyClause.setStorage(null);
        tempModifyClause.setPhysicalStorageAttributes(null);
        tempModifyClause.setLoggingOrNoLogging(null);
        tempModifyClause.setMonitoringOrNoMonitoring(null);
        tempModifyClause.setAllocateOrDeAllocate(null);
        tempModifyClause.setAllocateOrDeAllocateIdentifier(null);
        tempModifyClause.setCacheOrNoCache(null);
        tempModifyClause.setPartition(null);
        return tempModifyClause;
    }
    
    public ModifyClause toDB2() throws ConvertException {
        final ModifyClause tempModifyClause = this.copyObjectValues();
        if (tempModifyClause.getModify() != null) {
            throw new ConvertException("Conversion Failure.. DB2 does not support Modify Clause for a table");
        }
        tempModifyClause.setColumn(null);
        tempModifyClause.setCreateColumnVector(null);
        tempModifyClause.setDiskAttributes(null);
        tempModifyClause.setPhysicalCharacteristics(null);
        tempModifyClause.setConstraintClause(null);
        tempModifyClause.setDropClause(null);
        tempModifyClause.setStorage(null);
        tempModifyClause.setPhysicalStorageAttributes(null);
        tempModifyClause.setLoggingOrNoLogging(null);
        tempModifyClause.setMonitoringOrNoMonitoring(null);
        tempModifyClause.setAllocateOrDeAllocate(null);
        tempModifyClause.setAllocateOrDeAllocateIdentifier(null);
        tempModifyClause.setCacheOrNoCache(null);
        tempModifyClause.setPartition(null);
        return tempModifyClause;
    }
    
    public ModifyClause toMySQL() throws ConvertException {
        final ModifyClause tempModifyClause = this.copyObjectValues();
        if (tempModifyClause.getModify() != null) {
            tempModifyClause.getModify();
        }
        if (tempModifyClause.getColumn() != null) {
            tempModifyClause.getColumn();
        }
        if (tempModifyClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = tempModifyClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toMySQLString();
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
            }
        }
        tempModifyClause.setPhysicalCharacteristics(null);
        tempModifyClause.setConstraintClause(null);
        tempModifyClause.setDropClause(null);
        if (tempModifyClause.getStorage() != null || tempModifyClause.getPhysicalStorageAttributes() != null || tempModifyClause.getLoggingOrNoLogging() != null || tempModifyClause.getMonitoringOrNoMonitoring() != null || tempModifyClause.getAllocateOrDeAllocate() != null || tempModifyClause.getCacheOrNoCache() != null || tempModifyClause.getPartition() != null) {
            throw new ConvertException();
        }
        tempModifyClause.setStorage(null);
        tempModifyClause.setPhysicalStorageAttributes(null);
        tempModifyClause.setLoggingOrNoLogging(null);
        tempModifyClause.setMonitoringOrNoMonitoring(null);
        tempModifyClause.setAllocateOrDeAllocate(null);
        tempModifyClause.setAllocateOrDeAllocateIdentifier(null);
        tempModifyClause.setCacheOrNoCache(null);
        tempModifyClause.setPartition(null);
        return tempModifyClause;
    }
    
    public ModifyClause toANSI() throws ConvertException {
        final ModifyClause tempModifyClause = this.copyObjectValues();
        if (tempModifyClause.getModify() != null) {
            tempModifyClause.setModify("ALTER");
        }
        if (tempModifyClause.getColumn() != null) {
            tempModifyClause.getColumn();
        }
        if (tempModifyClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = tempModifyClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toANSIString();
                    final Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                    if (changeConstraintVector != null) {
                        for (int j = 0; j < changeConstraintVector.size(); ++j) {
                            final ConstraintClause toANSISQLConstraintClause = changeConstraintVector.get(j);
                            if (toANSISQLConstraintClause != null) {
                                toANSISQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                toANSISQLConstraintClause.toANSIString();
                            }
                        }
                    }
                }
            }
        }
        tempModifyClause.setDiskAttributes(null);
        tempModifyClause.setPhysicalCharacteristics(null);
        tempModifyClause.setConstraintClause(null);
        tempModifyClause.setDropClause(null);
        if (tempModifyClause.getStorage() != null || tempModifyClause.getPhysicalStorageAttributes() != null || tempModifyClause.getLoggingOrNoLogging() != null || tempModifyClause.getMonitoringOrNoMonitoring() != null || tempModifyClause.getAllocateOrDeAllocate() != null || tempModifyClause.getCacheOrNoCache() != null || tempModifyClause.getPartition() != null) {
            throw new ConvertException();
        }
        tempModifyClause.setStorage(null);
        tempModifyClause.setPhysicalStorageAttributes(null);
        tempModifyClause.setLoggingOrNoLogging(null);
        tempModifyClause.setMonitoringOrNoMonitoring(null);
        tempModifyClause.setAllocateOrDeAllocate(null);
        tempModifyClause.setAllocateOrDeAllocateIdentifier(null);
        tempModifyClause.setCacheOrNoCache(null);
        tempModifyClause.setPartition(null);
        return tempModifyClause;
    }
    
    public ModifyClause toInformix() throws ConvertException {
        final ModifyClause tempModifyClause = this.copyObjectValues();
        if (tempModifyClause.getModify() != null) {
            tempModifyClause.getModify();
        }
        tempModifyClause.setOpenBrace("(");
        tempModifyClause.setClosedBrace(")");
        if (tempModifyClause.getColumn() != null) {
            tempModifyClause.getColumn();
        }
        if (tempModifyClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = tempModifyClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toInformixString();
                    final Vector constraintVector = changeCreateColumn.getConstraintClause();
                    if (constraintVector != null) {
                        for (int j = 0; j < constraintVector.size(); ++j) {
                            final ConstraintClause toInformixConstraintClause = constraintVector.get(j);
                            if (toInformixConstraintClause != null) {
                                final ConstraintType toInformixConstraintType = toInformixConstraintClause.getConstraintType();
                                toInformixConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                toInformixConstraintClause.toInformixString();
                            }
                        }
                    }
                }
            }
        }
        tempModifyClause.setDiskAttributes(null);
        tempModifyClause.setPhysicalCharacteristics(null);
        tempModifyClause.setConstraintClause(null);
        tempModifyClause.setDropClause(null);
        if (tempModifyClause.getStorage() != null || tempModifyClause.getPhysicalStorageAttributes() != null || tempModifyClause.getLoggingOrNoLogging() != null || tempModifyClause.getMonitoringOrNoMonitoring() != null || tempModifyClause.getAllocateOrDeAllocate() != null || tempModifyClause.getCacheOrNoCache() != null || tempModifyClause.getPartition() != null) {
            throw new ConvertException();
        }
        tempModifyClause.setStorage(null);
        tempModifyClause.setPhysicalStorageAttributes(null);
        tempModifyClause.setLoggingOrNoLogging(null);
        tempModifyClause.setMonitoringOrNoMonitoring(null);
        tempModifyClause.setAllocateOrDeAllocate(null);
        tempModifyClause.setAllocateOrDeAllocateIdentifier(null);
        tempModifyClause.setCacheOrNoCache(null);
        tempModifyClause.setPartition(null);
        return tempModifyClause;
    }
    
    public ModifyClause toPostgreSQL() throws ConvertException {
        final ModifyClause tempModifyClause = this.copyObjectValues();
        if (tempModifyClause.getModify() != null) {
            tempModifyClause.setModify("ALTER");
        }
        if (tempModifyClause.getColumn() != null) {
            tempModifyClause.getColumn();
        }
        if (tempModifyClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = tempModifyClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toPostgreSQLString();
                    final Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                    if (changeConstraintVector != null) {
                        for (int j = 0; j < changeConstraintVector.size(); ++j) {
                            final ConstraintClause toPostgreSQLConstraintClause = changeConstraintVector.get(j);
                            if (toPostgreSQLConstraintClause != null) {
                                toPostgreSQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                toPostgreSQLConstraintClause.toPostgreSQLString();
                            }
                        }
                    }
                }
            }
        }
        tempModifyClause.setDiskAttributes(null);
        tempModifyClause.setPhysicalCharacteristics(null);
        tempModifyClause.setConstraintClause(null);
        tempModifyClause.setDropClause(null);
        if (tempModifyClause.getStorage() != null || tempModifyClause.getPhysicalStorageAttributes() != null || tempModifyClause.getLoggingOrNoLogging() != null || tempModifyClause.getMonitoringOrNoMonitoring() != null || tempModifyClause.getAllocateOrDeAllocate() != null || tempModifyClause.getCacheOrNoCache() != null || tempModifyClause.getPartition() != null) {
            throw new ConvertException();
        }
        tempModifyClause.setStorage(null);
        tempModifyClause.setPhysicalStorageAttributes(null);
        tempModifyClause.setLoggingOrNoLogging(null);
        tempModifyClause.setMonitoringOrNoMonitoring(null);
        tempModifyClause.setAllocateOrDeAllocate(null);
        tempModifyClause.setAllocateOrDeAllocateIdentifier(null);
        tempModifyClause.setCacheOrNoCache(null);
        tempModifyClause.setPartition(null);
        return tempModifyClause;
    }
    
    public ModifyClause toNetezza() throws ConvertException {
        final ModifyClause tempModifyClause = this.copyObjectValues();
        if (tempModifyClause.getModify() != null) {
            tempModifyClause.setModify("ALTER");
        }
        if (tempModifyClause.getColumn() != null) {
            tempModifyClause.getColumn();
        }
        if (tempModifyClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = tempModifyClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toNetezzaString();
                    if (changeCreateColumn.getDatatype() != null && changeCreateColumn.getDatatype() instanceof CharacterClass && changeCreateColumn.getDatatype().getDatatypeName().equalsIgnoreCase("varchar")) {
                        tempModifyClause.setModify("MODIFY");
                    }
                    else {
                        if (changeCreateColumn.getDatatype() != null || changeCreateColumn.getDefault() == null) {
                            throw new ConvertException("/*SwisSQL Message: Netezza does not support modifying columns with datatype other than varchar*/");
                        }
                        changeCreateColumn.setDefault("SET DEFAULT");
                    }
                    final Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                    if (changeConstraintVector != null) {
                        for (int j = 0; j < changeConstraintVector.size(); ++j) {
                            final ConstraintClause toNetezzaSQLConstraintClause = changeConstraintVector.get(j);
                            if (toNetezzaSQLConstraintClause != null) {
                                toNetezzaSQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                toNetezzaSQLConstraintClause.toNetezzaString();
                            }
                        }
                    }
                }
            }
        }
        tempModifyClause.setDiskAttributes(null);
        tempModifyClause.setPhysicalCharacteristics(null);
        tempModifyClause.setConstraintClause(null);
        tempModifyClause.setDropClause(null);
        if (tempModifyClause.getStorage() != null || tempModifyClause.getPhysicalStorageAttributes() != null || tempModifyClause.getLoggingOrNoLogging() != null || tempModifyClause.getMonitoringOrNoMonitoring() != null || tempModifyClause.getAllocateOrDeAllocate() != null || tempModifyClause.getCacheOrNoCache() != null || tempModifyClause.getPartition() != null) {
            throw new ConvertException();
        }
        tempModifyClause.setStorage(null);
        tempModifyClause.setPhysicalStorageAttributes(null);
        tempModifyClause.setLoggingOrNoLogging(null);
        tempModifyClause.setMonitoringOrNoMonitoring(null);
        tempModifyClause.setAllocateOrDeAllocate(null);
        tempModifyClause.setAllocateOrDeAllocateIdentifier(null);
        tempModifyClause.setCacheOrNoCache(null);
        tempModifyClause.setPartition(null);
        return tempModifyClause;
    }
    
    public ModifyClause toTeradata() throws ConvertException {
        final ModifyClause tempModifyClause = this.copyObjectValues();
        if (tempModifyClause.getModify() != null) {
            tempModifyClause.setModify("ALTER");
        }
        if (tempModifyClause.getColumn() != null) {
            tempModifyClause.getColumn();
        }
        if (tempModifyClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = tempModifyClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toTeradataString();
                    final Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                    if (changeConstraintVector != null) {
                        for (int j = 0; j < changeConstraintVector.size(); ++j) {
                            final ConstraintClause toTeradataSQLConstraintClause = changeConstraintVector.get(j);
                            if (toTeradataSQLConstraintClause != null) {
                                toTeradataSQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                toTeradataSQLConstraintClause.toTeradataString();
                            }
                        }
                    }
                }
            }
        }
        tempModifyClause.setDiskAttributes(null);
        tempModifyClause.setPhysicalCharacteristics(null);
        tempModifyClause.setConstraintClause(null);
        tempModifyClause.setDropClause(null);
        if (tempModifyClause.getStorage() != null || tempModifyClause.getPhysicalStorageAttributes() != null || tempModifyClause.getLoggingOrNoLogging() != null || tempModifyClause.getMonitoringOrNoMonitoring() != null || tempModifyClause.getAllocateOrDeAllocate() != null || tempModifyClause.getCacheOrNoCache() != null || tempModifyClause.getPartition() != null) {
            throw new ConvertException();
        }
        tempModifyClause.setStorage(null);
        tempModifyClause.setPhysicalStorageAttributes(null);
        tempModifyClause.setLoggingOrNoLogging(null);
        tempModifyClause.setMonitoringOrNoMonitoring(null);
        tempModifyClause.setAllocateOrDeAllocate(null);
        tempModifyClause.setAllocateOrDeAllocateIdentifier(null);
        tempModifyClause.setCacheOrNoCache(null);
        tempModifyClause.setPartition(null);
        return tempModifyClause;
    }
    
    public String removeIndent(String str) {
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        return str;
    }
    
    public ModifyClause copyObjectValues() {
        final ModifyClause dupModifyClause = new ModifyClause();
        dupModifyClause.setModify(this.getModify());
        dupModifyClause.setConstraint(this.getConstraint());
        dupModifyClause.setColumn(this.getColumn());
        dupModifyClause.setCreateColumnVector(this.getCreateColumnVector());
        dupModifyClause.setDiskAttributes(this.getDiskAttributes());
        dupModifyClause.setPhysicalCharacteristics(this.getPhysicalCharacteristics());
        dupModifyClause.setConstraintClause(this.getConstraintClause());
        dupModifyClause.setDropClause(this.getDropClause());
        dupModifyClause.setStorage(this.getStorage());
        dupModifyClause.setPhysicalStorageAttributes(this.getPhysicalStorageAttributes());
        dupModifyClause.setLoggingOrNoLogging(this.getLoggingOrNoLogging());
        dupModifyClause.setMonitoringOrNoMonitoring(this.getMonitoringOrNoMonitoring());
        dupModifyClause.setAllocateOrDeAllocate(this.getAllocateOrDeAllocate());
        dupModifyClause.setAllocateOrDeAllocateIdentifier(this.getAllocateOrDeAllocateIdentifier());
        dupModifyClause.setCacheOrNoCache(this.getCacheOrNoCache());
        dupModifyClause.setPartition(this.getPartition());
        dupModifyClause.setObjectContext(this.context);
        return dupModifyClause;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.modify != null) {
            sb.append(this.modify.toUpperCase());
        }
        if (this.constraint != null) {
            sb.append(" " + this.constraint.toUpperCase());
        }
        if (this.openBrace != null) {
            sb.append("\n" + this.openBrace);
        }
        if (this.createColumnVector != null) {
            for (int i = 0; i < this.createColumnVector.size(); ++i) {
                final CreateColumn tempCreateColumn = this.createColumnVector.get(i);
                tempCreateColumn.setObjectContext(this.context);
                if (i == 0) {
                    sb.append("\n\t" + tempCreateColumn.toString());
                }
                else if (this.isSQLServerAlterSet) {
                    sb.append(",\nALTER\n\t" + tempCreateColumn.toString());
                }
                else {
                    sb.append(",\n\t" + tempCreateColumn.toString());
                }
            }
        }
        if (this.closedBrace != null) {
            sb.append("\n" + this.closedBrace);
        }
        if (this.dropClause != null) {
            this.dropClause.setObjectContext(this.context);
            sb.append(this.dropClause.toString());
        }
        if (this.storage != null) {
            sb.append(this.storage.toUpperCase());
        }
        if (this.physicalStorageAttributes != null) {
            sb.append("( " + this.physicalStorageAttributes.toUpperCase() + ")");
        }
        if (this.loggingOrNoLogging != null) {
            sb.append(" " + this.loggingOrNoLogging.toUpperCase());
        }
        if (this.monitoringOrNoMonitoring != null) {
            sb.append(" " + this.monitoringOrNoMonitoring.toUpperCase());
        }
        if (this.partitionListAttributes != null) {
            sb.append(this.partitionListAttributes);
        }
        if (this.allocateOrDeAllocate != null) {
            sb.append(" " + this.allocateOrDeAllocate.toUpperCase());
        }
        if (this.allocateOrDeAllocateIdentifier != null) {
            sb.append(" " + this.allocateOrDeAllocateIdentifier);
        }
        if (this.cacheOrNoCache != null) {
            sb.append(" " + this.cacheOrNoCache.toUpperCase());
        }
        return sb.toString();
    }
}
