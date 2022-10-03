package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.sql.statement.create.CheckConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.ForeignConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintType;
import java.util.List;
import java.util.HashMap;
import com.adventnet.swissqlapi.sql.statement.create.PrimaryOrUniqueConstraintClause;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.PartitionListAttributes;
import java.util.ArrayList;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class AddClause
{
    private UserObjectContext context;
    private String with;
    private String add;
    private String checkOrNoCheck;
    private String before;
    private String first;
    private String after;
    private String beforeOrAfterColumnName;
    private boolean beforeOrAfterOrFirstCalled;
    private CreateColumn createColumn;
    private Vector createColumnVector;
    private boolean commaIsSet;
    private ArrayList columnArrayList;
    private PartitionListAttributes partition;
    private String partitioning;
    private String key;
    private String using;
    private String hashing;
    private String openBrace;
    private String closeBrace;
    private String overflow;
    private String storage;
    private String physicalStorageAttributes;
    private Vector constraintClauseVector;
    private ConstraintClause constraintClause;
    private String column;
    private String diskAttributes;
    private String physicalCharacteristics;
    private ConstraintClause tableConstraint;
    private String uniqueOrPrimaryOrIndexOrFullText;
    private String uniqueConstraintName;
    private Vector indexColumnVector;
    private DatatypeMapping datatypeMapping;
    private String stmtTableName;
    
    public AddClause() {
        this.context = null;
        this.beforeOrAfterOrFirstCalled = false;
    }
    
    public void setAdd(final String add) {
        this.add = add;
    }
    
    public void setWith(final String with) {
        this.with = with;
    }
    
    public void setCheckOrNoCheck(final String checkOrNoCheck) {
        this.checkOrNoCheck = checkOrNoCheck;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setConstraintClauseVector(final Vector constraintClauseVector) {
        this.constraintClauseVector = constraintClauseVector;
    }
    
    public void setConstraintClause(final ConstraintClause constraintClause) {
        this.constraintClause = constraintClause;
    }
    
    public void setOpenBrace(final String openBrace) {
        this.openBrace = openBrace;
    }
    
    public void setColumn(final String column) {
        this.column = column;
    }
    
    public void setCreateColumnVector(final Vector createColumnVector) {
        this.createColumnVector = createColumnVector;
    }
    
    public void setCommaBooleanValue(final boolean commaIsSet) {
        this.commaIsSet = commaIsSet;
    }
    
    public void setBefore(final String before) {
        this.before = before;
    }
    
    public void setFirst(final String first) {
        this.first = first;
    }
    
    public void setAfter(final String after) {
        this.after = after;
    }
    
    public void setBeforeOrAfterColumnName(final String beforeOrAfterColumnName) {
        this.beforeOrAfterColumnName = beforeOrAfterColumnName;
    }
    
    public void setBeforeOrAfterOrFirstBooleanValue(final boolean before) {
        this.beforeOrAfterOrFirstCalled = true;
    }
    
    public void setClosedBrace(final String closeBrace) {
        this.closeBrace = closeBrace;
    }
    
    public void setPartition(final PartitionListAttributes partition) {
        this.partition = partition;
    }
    
    public void setPartitionKey(final String key) {
        this.key = key;
    }
    
    public void setPartitioning(final String partitioning) {
        this.partitioning = partitioning;
    }
    
    public void setUsing(final String using) {
        this.using = using;
    }
    
    public void setHashing(final String hashing) {
        this.hashing = hashing;
    }
    
    public void setColumnArrayList(final ArrayList columnArrayList) {
        this.columnArrayList = columnArrayList;
    }
    
    public void setOverflow(final String overflow) {
        this.overflow = overflow;
    }
    
    public void setStorage(final String storage) {
        this.storage = storage;
    }
    
    public void setPhysicalStorageAttributes(final String physicalStorageAttributes) {
        this.physicalStorageAttributes = physicalStorageAttributes;
    }
    
    public void setUniqueOrPrimaryOrIndexOrFullText(final String uniqueOrPrimaryOrIndexOrFullText) {
        this.uniqueOrPrimaryOrIndexOrFullText = uniqueOrPrimaryOrIndexOrFullText;
    }
    
    public void setUniqueConstraintName(final String uniqueConstraintName) {
        this.uniqueConstraintName = uniqueConstraintName;
    }
    
    public void setIndexColumnVector(final Vector indexColumnVector) {
        this.indexColumnVector = indexColumnVector;
    }
    
    public void setDiskAttributes(final String diskAttributes) {
        this.diskAttributes = diskAttributes;
    }
    
    public void setPhysicalCharacteristics(final String physicalCharacteristics) {
        this.physicalCharacteristics = physicalCharacteristics;
    }
    
    public void setIndexClause() {
    }
    
    public void setDatatypeMapping(final DatatypeMapping mapping) {
        this.datatypeMapping = mapping;
    }
    
    public void setStmtTableName(final String stmtTableName) {
        this.stmtTableName = stmtTableName;
    }
    
    public String getAdd() {
        return this.add;
    }
    
    public String getWith() {
        return this.with;
    }
    
    public String getCheckOrNoCheck() {
        return this.checkOrNoCheck;
    }
    
    public String getColumn() {
        return this.column;
    }
    
    public Vector getCreateColumnVector() {
        return this.createColumnVector;
    }
    
    public boolean getCommaBooleanValue() {
        return this.commaIsSet;
    }
    
    public Vector getConstraintClauseVector() {
        return this.constraintClauseVector;
    }
    
    public ConstraintClause getConstraintClause() {
        return this.constraintClause;
    }
    
    public String getDiskAttributes() {
        return this.diskAttributes;
    }
    
    public String getPhysicalCharacteristics() {
        return this.physicalCharacteristics;
    }
    
    public PartitionListAttributes getPartition() {
        return this.partition;
    }
    
    public String getPartitionKey() {
        return this.key;
    }
    
    public String getPartitioning() {
        return this.partitioning;
    }
    
    public String getUsing() {
        return this.using;
    }
    
    public String getHashing() {
        return this.hashing;
    }
    
    public ArrayList getColumnArraylist() {
        return this.columnArrayList;
    }
    
    public String getOpenBrace() {
        return this.openBrace;
    }
    
    public String getClosedBrace() {
        return this.closeBrace;
    }
    
    public String getOverflow() {
        return this.overflow;
    }
    
    public String getStorage() {
        return this.storage;
    }
    
    public String getPhysicalStorageAttributes() {
        return this.physicalStorageAttributes;
    }
    
    public String getFirst() {
        return this.first;
    }
    
    public String getAfter() {
        return this.after;
    }
    
    public String getBefore() {
        return this.before;
    }
    
    public void getIndexClause() {
    }
    
    public String getBeforeOrAfterColumnName() {
        return this.beforeOrAfterColumnName;
    }
    
    public String getUniqueOrPrimaryOrIndexOrFullText() {
        return this.uniqueOrPrimaryOrIndexOrFullText;
    }
    
    public String getUniqueConstraintName() {
        return this.uniqueConstraintName;
    }
    
    public Vector getIndexColumnVector() {
        return this.indexColumnVector;
    }
    
    public AddClause copyObjectValues() {
        final AddClause dupAddClause = new AddClause();
        dupAddClause.setAdd(this.getAdd());
        dupAddClause.setWith(this.getWith());
        dupAddClause.setAfter(this.getAfter());
        dupAddClause.setCheckOrNoCheck(this.getCheckOrNoCheck());
        dupAddClause.setClosedBrace(this.getClosedBrace());
        dupAddClause.setCreateColumnVector(this.getCreateColumnVector());
        dupAddClause.setCommaBooleanValue(this.getCommaBooleanValue());
        dupAddClause.setHashing(this.getHashing());
        dupAddClause.setColumnArrayList(this.getColumnArraylist());
        dupAddClause.setOpenBrace(this.getOpenBrace());
        dupAddClause.setOverflow(this.getOverflow());
        dupAddClause.setStorage(this.getStorage());
        dupAddClause.setPhysicalStorageAttributes(this.getPhysicalStorageAttributes());
        dupAddClause.setPartition(this.getPartition());
        dupAddClause.setPartitionKey(this.getPartitionKey());
        dupAddClause.setPartitioning(this.getPartitioning());
        dupAddClause.setUsing(this.getUsing());
        dupAddClause.setFirst(this.getFirst());
        dupAddClause.setColumn(this.getColumn());
        dupAddClause.setIndexClause();
        dupAddClause.setObjectContext(this.context);
        dupAddClause.setDiskAttributes(this.getDiskAttributes());
        dupAddClause.setPhysicalCharacteristics(this.getPhysicalCharacteristics());
        dupAddClause.setConstraintClause(this.getConstraintClause());
        dupAddClause.setConstraintClauseVector(this.getConstraintClauseVector());
        dupAddClause.setBefore(this.getBefore());
        dupAddClause.setBeforeOrAfterColumnName(this.getBeforeOrAfterColumnName());
        dupAddClause.setUniqueOrPrimaryOrIndexOrFullText(this.getUniqueOrPrimaryOrIndexOrFullText());
        dupAddClause.setUniqueConstraintName(this.getUniqueConstraintName());
        dupAddClause.setIndexColumnVector(this.getIndexColumnVector());
        return dupAddClause;
    }
    
    public AddClause toOracle() throws ConvertException {
        final AddClause dupAddClause = this.copyObjectValues();
        dupAddClause.setWith(null);
        dupAddClause.setCheckOrNoCheck(null);
        dupAddClause.setColumn(null);
        final String temp = dupAddClause.getUniqueOrPrimaryOrIndexOrFullText();
        if (temp != null) {
            if (!temp.equalsIgnoreCase("primary key")) {
                throw new ConvertException("Conversion Failure.. Invalid Query");
            }
            final Vector columns = dupAddClause.getIndexColumnVector();
            if (columns != null) {
                for (int i = 0; i < columns.size(); ++i) {
                    final Object obj = columns.get(i);
                    if (obj instanceof String) {
                        String columnName = (String)obj;
                        if (columnName.startsWith("`")) {
                            columnName = columnName.substring(1, columnName.length() - 1);
                            if (columnName.length() > 30) {
                                columnName = columnName.substring(0, 30);
                            }
                            if (SwisSQLOptions.retainQuotedIdentifierForOracle || columnName.indexOf(32) != -1) {
                                columnName = "\"" + columnName + "\"";
                            }
                        }
                        else if (columnName.startsWith("\"")) {
                            if (columnName.length() > 32) {
                                columnName = columnName.substring(0, 31) + "\"";
                            }
                        }
                        else if (columnName.length() > 30) {
                            columnName = columnName.substring(0, 30);
                        }
                        columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(1), null, 1);
                        columns.setElementAt(columnName, i);
                    }
                }
            }
        }
        if (dupAddClause.getOpenBrace() != null) {
            dupAddClause.getOpenBrace();
        }
        if (dupAddClause.getClosedBrace() != null) {
            dupAddClause.getClosedBrace();
        }
        if (dupAddClause.getCommaBooleanValue()) {
            dupAddClause.getCommaBooleanValue();
        }
        final ArrayList columnList = new ArrayList();
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = dupAddClause.getCreateColumnVector();
            for (int j = 0; j < columnNamesVector.size(); ++j) {
                if (columnNamesVector.get(j) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(j);
                    changeCreateColumn.toOracleString();
                    final Vector getConstraintVector = changeCreateColumn.getConstraintClause();
                    final Vector constraintVector = new Vector();
                    if (getConstraintVector != null) {
                        for (int k = 0; k < getConstraintVector.size(); ++k) {
                            final ConstraintClause toOracleConstraintClause = getConstraintVector.get(k);
                            if (toOracleConstraintClause != null) {
                                final ConstraintType changeConstraintType = toOracleConstraintClause.getConstraintType();
                                if (changeConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                    final PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)changeConstraintType;
                                    primaryOrUniqueConstraintClause.setClustered(null);
                                    if (primaryOrUniqueConstraintClause.getWith() != null) {
                                        final Object fillfactor = primaryOrUniqueConstraintClause.getDiskAttr().get("FILLFACTOR");
                                        if (fillfactor != null) {
                                            final String percent = fillfactor.toString();
                                            dupAddClause.setDiskAttributes("PCTFREE " + percent);
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
                            constraintVector.add(toOracleConstraintClause);
                        }
                        changeCreateColumn.setConstraintClause(constraintVector);
                    }
                }
            }
            if (dupAddClause.getCreateColumnVector() != null) {
                final Vector tempCreateColumnVector = dupAddClause.getCreateColumnVector();
                final Vector oracleCreateColumnVector = new Vector();
                if (tempCreateColumnVector.size() > 1) {
                    dupAddClause.setOpenBrace("(");
                    dupAddClause.setClosedBrace(")");
                }
                for (int l = 0; l < tempCreateColumnVector.size(); ++l) {
                    if (tempCreateColumnVector.elementAt(l) instanceof CreateColumn) {
                        final CreateColumn createColn = tempCreateColumnVector.get(l);
                        createColn.toOracleString();
                        if (createColn.getColumnName() != null) {
                            columnList.add(createColn.getColumnName());
                        }
                        oracleCreateColumnVector.add(createColn);
                    }
                }
                dupAddClause.setCreateColumnVector(oracleCreateColumnVector);
                final HashMap truncatedNames = SwisSQLUtils.truncateNames(columnList, 30);
                for (int m = 0; m < oracleCreateColumnVector.size(); ++m) {
                    final Object obj2 = oracleCreateColumnVector.get(m);
                    if (obj2 instanceof CreateColumn) {
                        final CreateColumn changeCreateColumn2 = (CreateColumn)obj2;
                        final String oracleColName = changeCreateColumn2.getColumnName();
                        final String truncatedName = truncatedNames.get(oracleColName);
                        if (truncatedName != null) {
                            changeCreateColumn2.setColumnName(truncatedName);
                        }
                    }
                }
            }
            if (dupAddClause.getPhysicalCharacteristics() != null) {
                dupAddClause.getPhysicalCharacteristics();
            }
        }
        if (dupAddClause.getOverflow() != null) {
            dupAddClause.getOverflow();
        }
        if (dupAddClause.getStorage() != null) {
            dupAddClause.getStorage();
        }
        if (dupAddClause.getPhysicalStorageAttributes() != null) {
            dupAddClause.getPhysicalStorageAttributes();
        }
        if (dupAddClause.getPartition() != null) {
            final PartitionListAttributes tempPartition = dupAddClause.getPartition();
            final PartitionListAttributes oraclePartitionListAttributesClause = tempPartition.toOracle();
            dupAddClause.setPartition(oraclePartitionListAttributesClause);
        }
        dupAddClause.setFirst(null);
        dupAddClause.setAfter(null);
        dupAddClause.setBefore(null);
        dupAddClause.setBeforeOrAfterColumnName(null);
        dupAddClause.setHashing(null);
        dupAddClause.setUsing(null);
        if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setPartitionKey(null);
        dupAddClause.setColumnArrayList(null);
        return dupAddClause;
    }
    
    public AddClause toSybase() throws ConvertException {
        final AddClause dupAddClause = this.copyObjectValues();
        dupAddClause.setOpenBrace(null);
        dupAddClause.setClosedBrace(null);
        dupAddClause.setWith(null);
        dupAddClause.setCheckOrNoCheck(null);
        dupAddClause.setColumn(null);
        if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setUniqueConstraintName(null);
        dupAddClause.setIndexColumnVector(null);
        dupAddClause.setFirst(null);
        dupAddClause.setAfter(null);
        dupAddClause.setBefore(null);
        dupAddClause.setBeforeOrAfterColumnName(null);
        dupAddClause.setHashing(null);
        dupAddClause.setUsing(null);
        if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setPartitionKey(null);
        dupAddClause.setColumnArrayList(null);
        if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setIndexClause();
        if (dupAddClause.getCommaBooleanValue()) {
            dupAddClause.getCommaBooleanValue();
        }
        if (dupAddClause.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setStorage(null);
        dupAddClause.setPhysicalStorageAttributes(null);
        dupAddClause.setDiskAttributes(null);
        dupAddClause.setPhysicalCharacteristics(null);
        if (dupAddClause.getCreateColumnVector() != null) {
            final String notNullStr = "";
            final Vector columnNamesVector = dupAddClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toSybaseString();
                    final Vector constraintVector = changeCreateColumn.getConstraintClause();
                    if (constraintVector != null) {
                        for (int j = 0; j < constraintVector.size(); ++j) {
                            final ConstraintClause toSybaseConstraintClause = constraintVector.get(j);
                            if (toSybaseConstraintClause != null) {
                                final ConstraintType toSybaseConstraintType = toSybaseConstraintClause.getConstraintType();
                                toSybaseConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                if (toSybaseConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                    final PrimaryOrUniqueConstraintClause primaryClause = (PrimaryOrUniqueConstraintClause)toSybaseConstraintType;
                                    primaryClause.setWith(null);
                                    primaryClause.setDiskAttr(null);
                                }
                                if (toSybaseConstraintType instanceof ForeignConstraintClause) {
                                    final ForeignConstraintClause foreignClause = (ForeignConstraintClause)toSybaseConstraintType;
                                    foreignClause.setOnDelete(null);
                                    foreignClause.setActionOnDelete(null);
                                    foreignClause.setOnUpdate(null);
                                    foreignClause.setActionOnUpdate(null);
                                }
                                toSybaseConstraintClause.toSybaseString();
                            }
                        }
                    }
                }
            }
        }
        return dupAddClause;
    }
    
    public AddClause toMSSQLServer() throws ConvertException {
        final AddClause dupAddClause = this.copyObjectValues();
        boolean handleUnique = false;
        dupAddClause.setOpenBrace(null);
        dupAddClause.setClosedBrace(null);
        if (dupAddClause.getWith() != null) {
            dupAddClause.getWith();
        }
        if (dupAddClause.getCheckOrNoCheck() != null) {
            dupAddClause.getCheckOrNoCheck();
        }
        dupAddClause.setColumn(null);
        if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
            if (!dupAddClause.getUniqueOrPrimaryOrIndexOrFullText().equalsIgnoreCase("UNIQUE")) {
                throw new ConvertException("Conversion Failure.. Invalid Query");
            }
            handleUnique = true;
            dupAddClause.setUniqueOrPrimaryOrIndexOrFullText("CONSTRAINT");
        }
        if (!handleUnique) {
            dupAddClause.setUniqueConstraintName(null);
            dupAddClause.setIndexColumnVector(null);
        }
        else {
            String newUniqueConstraintName = dupAddClause.getUniqueConstraintName();
            if (newUniqueConstraintName.startsWith("`") || newUniqueConstraintName.startsWith("\"")) {
                newUniqueConstraintName = newUniqueConstraintName.substring(1, newUniqueConstraintName.length() - 1);
            }
            dupAddClause.setUniqueConstraintName(newUniqueConstraintName + " " + "UNIQUE");
            final Vector columns = dupAddClause.getIndexColumnVector();
            if (columns != null) {
                for (int i = 0; i < columns.size(); ++i) {
                    final Object obj = columns.get(i);
                    if (obj instanceof String) {
                        String columnName = (String)obj;
                        if (columnName.startsWith("`") || columnName.startsWith("\"")) {
                            columnName = columnName.substring(1, columnName.length() - 1);
                        }
                        columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(2), null, 1);
                        columns.setElementAt(columnName, i);
                    }
                }
            }
        }
        dupAddClause.setFirst(null);
        dupAddClause.setAfter(null);
        dupAddClause.setBefore(null);
        dupAddClause.setBeforeOrAfterColumnName(null);
        dupAddClause.setHashing(null);
        dupAddClause.setUsing(null);
        if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setPartitionKey(null);
        dupAddClause.setColumnArrayList(null);
        if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setIndexClause();
        if (dupAddClause.getCommaBooleanValue()) {
            dupAddClause.getCommaBooleanValue();
        }
        if (dupAddClause.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setStorage(null);
        dupAddClause.setPhysicalStorageAttributes(null);
        if (dupAddClause.getCreateColumnVector() != null) {
            final String notNullStr = "";
            final Vector columnNamesVector = dupAddClause.getCreateColumnVector();
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
                                String fillfactor = dupAddClause.getDiskAttributes();
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
                                    dupAddClause.setDiskAttributes(null);
                                    break;
                                }
                            }
                        }
                    }
                    else if (dupAddClause.getDiskAttributes() != null) {
                        dupAddClause.setDiskAttributes(null);
                    }
                }
            }
        }
        dupAddClause.setPhysicalCharacteristics(null);
        return dupAddClause;
    }
    
    public AddClause toDB2() throws ConvertException {
        final AddClause dupAddClause = this.copyObjectValues();
        dupAddClause.setOpenBrace(null);
        dupAddClause.setClosedBrace(null);
        dupAddClause.setWith(null);
        dupAddClause.setCheckOrNoCheck(null);
        if (dupAddClause.getColumn() != null) {
            dupAddClause.getColumn();
        }
        if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setUniqueConstraintName(null);
        dupAddClause.setIndexColumnVector(null);
        dupAddClause.setFirst(null);
        dupAddClause.setAfter(null);
        dupAddClause.setBefore(null);
        dupAddClause.setBeforeOrAfterColumnName(null);
        if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (dupAddClause.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setStorage(null);
        dupAddClause.setPhysicalStorageAttributes(null);
        dupAddClause.setIndexClause();
        dupAddClause.setCommaBooleanValue(false);
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = dupAddClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.setAlterStatement(true);
                    changeCreateColumn.setDatatypeMapping(this.datatypeMapping);
                    Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                    if (changeConstraintVector != null) {
                        for (int j = 0; j < changeConstraintVector.size(); ++j) {
                            final ConstraintClause toDB2ConstraintClause = changeConstraintVector.get(j);
                            if (toDB2ConstraintClause != null && changeCreateColumn.getColumnName() == null && toDB2ConstraintClause.getConstraintType() != null && toDB2ConstraintClause.getConstraintType() instanceof CheckConstraintClause) {
                                final CheckConstraintClause ccClause = (CheckConstraintClause)toDB2ConstraintClause.getConstraintType();
                                ccClause.setStmtTableName(this.stmtTableName);
                            }
                        }
                    }
                    changeCreateColumn.toDB2String();
                    changeConstraintVector = changeCreateColumn.getConstraintClause();
                    if (changeConstraintVector != null) {
                        for (int j = 0; j < changeConstraintVector.size(); ++j) {
                            final ConstraintClause toDB2ConstraintClause = changeConstraintVector.get(j);
                            if (toDB2ConstraintClause != null) {
                                toDB2ConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                toDB2ConstraintClause.toDB2String();
                            }
                        }
                    }
                }
            }
        }
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector tempCreateColumnVector = dupAddClause.getCreateColumnVector();
            if (tempCreateColumnVector.size() > 1) {
                dupAddClause.setColumn(null);
            }
        }
        if (dupAddClause.getPartitioning() != null) {
            dupAddClause.getPartitioning();
        }
        if (dupAddClause.getPartitionKey() != null) {
            dupAddClause.getPartitionKey();
        }
        if (dupAddClause.getColumnArraylist() != null) {
            dupAddClause.getColumnArraylist();
        }
        if (dupAddClause.getUsing() != null) {
            dupAddClause.getUsing();
        }
        if (dupAddClause.getHashing() != null) {
            dupAddClause.getHashing();
        }
        dupAddClause.setPhysicalCharacteristics(null);
        dupAddClause.setDiskAttributes(null);
        return dupAddClause;
    }
    
    public AddClause toANSI() throws ConvertException {
        final AddClause dupAddClause = this.copyObjectValues();
        dupAddClause.setOpenBrace(null);
        dupAddClause.setClosedBrace(null);
        dupAddClause.setWith(null);
        dupAddClause.setCheckOrNoCheck(null);
        if (dupAddClause.getColumn() != null) {
            dupAddClause.getColumn();
        }
        if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setUniqueConstraintName(null);
        dupAddClause.setIndexColumnVector(null);
        dupAddClause.setFirst(null);
        dupAddClause.setAfter(null);
        dupAddClause.setBefore(null);
        dupAddClause.setBeforeOrAfterColumnName(null);
        dupAddClause.setPhysicalCharacteristics(null);
        dupAddClause.setDiskAttributes(null);
        dupAddClause.setHashing(null);
        dupAddClause.setUsing(null);
        if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setPartitionKey(null);
        dupAddClause.setColumnArrayList(null);
        if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setIndexClause();
        if (dupAddClause.getCommaBooleanValue()) {
            dupAddClause.getCommaBooleanValue();
        }
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = dupAddClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toANSIString();
                    final Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                    if (changeConstraintVector != null) {
                        for (int j = 0; j < changeConstraintVector.size(); ++j) {
                            final ConstraintClause toANSISQLConstraintClause = changeConstraintVector.get(j);
                            if (toANSISQLConstraintClause != null) {
                                final ConstraintType toANSIConstraintType = toANSISQLConstraintClause.getConstraintType();
                                toANSISQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                toANSISQLConstraintClause.toANSIString();
                            }
                        }
                    }
                }
            }
        }
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector tempCreateColumnVector = dupAddClause.getCreateColumnVector();
            if (tempCreateColumnVector.size() > 1) {
                dupAddClause.setColumn(null);
            }
        }
        if (dupAddClause.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setStorage(null);
        dupAddClause.setPhysicalStorageAttributes(null);
        return dupAddClause;
    }
    
    public AddClause toInformix() throws ConvertException {
        final AddClause dupAddClause = this.copyObjectValues();
        if (dupAddClause.getOpenBrace() != null) {
            dupAddClause.getOpenBrace();
        }
        if (dupAddClause.getClosedBrace() != null) {
            dupAddClause.getClosedBrace();
        }
        dupAddClause.setWith(null);
        dupAddClause.setCheckOrNoCheck(null);
        dupAddClause.setColumn(null);
        if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setUniqueConstraintName(null);
        dupAddClause.setIndexColumnVector(null);
        dupAddClause.setFirst(null);
        dupAddClause.setAfter(null);
        if (dupAddClause.getBefore() != null) {
            dupAddClause.getBefore();
        }
        if (dupAddClause.getBeforeOrAfterColumnName() != null) {
            dupAddClause.getBeforeOrAfterColumnName();
        }
        dupAddClause.setPhysicalCharacteristics(null);
        dupAddClause.setDiskAttributes(null);
        dupAddClause.setHashing(null);
        dupAddClause.setUsing(null);
        if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setPartitionKey(null);
        dupAddClause.setColumnArrayList(null);
        if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setIndexClause();
        if (dupAddClause.getCommaBooleanValue()) {
            dupAddClause.getCommaBooleanValue();
        }
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = dupAddClause.getCreateColumnVector();
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
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector tempCreateColumnVector = dupAddClause.getCreateColumnVector();
            if (tempCreateColumnVector.size() > 1) {
                dupAddClause.setOpenBrace("(");
                dupAddClause.setClosedBrace(")");
            }
        }
        if (dupAddClause.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setStorage(null);
        dupAddClause.setPhysicalStorageAttributes(null);
        return dupAddClause;
    }
    
    public AddClause toPostgreSQL() throws ConvertException {
        final AddClause dupAddClause = this.copyObjectValues();
        dupAddClause.setOpenBrace(null);
        dupAddClause.setClosedBrace(null);
        dupAddClause.setWith(null);
        dupAddClause.setCheckOrNoCheck(null);
        if (dupAddClause.getColumn() != null) {
            dupAddClause.getColumn();
        }
        if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setUniqueConstraintName(null);
        dupAddClause.setIndexColumnVector(null);
        dupAddClause.setFirst(null);
        dupAddClause.setAfter(null);
        dupAddClause.setBefore(null);
        dupAddClause.setBeforeOrAfterColumnName(null);
        dupAddClause.setPhysicalCharacteristics(null);
        dupAddClause.setDiskAttributes(null);
        dupAddClause.setHashing(null);
        dupAddClause.setUsing(null);
        if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setPartitionKey(null);
        dupAddClause.setColumnArrayList(null);
        if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setIndexClause();
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = dupAddClause.getCreateColumnVector();
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
        if (dupAddClause.getCommaBooleanValue()) {
            dupAddClause.setCommaBooleanValue(false);
            dupAddClause.setCreateColumnVector(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (dupAddClause.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setStorage(null);
        dupAddClause.setPhysicalStorageAttributes(null);
        return dupAddClause;
    }
    
    public AddClause toMySQL() throws ConvertException {
        final AddClause dupAddClause = this.copyObjectValues();
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = dupAddClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toMySQLString();
                    final Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                    if (changeConstraintVector != null) {
                        for (int j = 0; j < changeConstraintVector.size(); ++j) {
                            final ConstraintClause toMySQLConstraintClause = changeConstraintVector.get(j);
                            if (toMySQLConstraintClause != null) {
                                if (changeCreateColumn.getIdentity() != null) {
                                    toMySQLConstraintClause.setAutoIncrement("AUTO_INCREMENT");
                                    changeCreateColumn.setIdentity(null);
                                }
                                final ConstraintType toMySQLConstraintType = toMySQLConstraintClause.getConstraintType();
                                toMySQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                toMySQLConstraintClause.toMySQLString();
                            }
                        }
                    }
                }
            }
        }
        if (dupAddClause.getCommaBooleanValue()) {
            dupAddClause.getCommaBooleanValue();
        }
        if (dupAddClause.getOpenBrace() != null) {
            dupAddClause.getOpenBrace();
        }
        if (dupAddClause.getClosedBrace() != null) {
            dupAddClause.getClosedBrace();
        }
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector tempCreateColumnVector = dupAddClause.getCreateColumnVector();
            if (tempCreateColumnVector.size() > 1) {
                dupAddClause.setOpenBrace("(");
                dupAddClause.setClosedBrace(")");
                dupAddClause.setColumn(null);
            }
        }
        dupAddClause.setWith(null);
        dupAddClause.setCheckOrNoCheck(null);
        if (dupAddClause.getColumn() != null) {
            dupAddClause.getColumn();
        }
        if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
            dupAddClause.getUniqueOrPrimaryOrIndexOrFullText();
        }
        if (dupAddClause.getUniqueConstraintName() != null) {
            dupAddClause.getUniqueConstraintName();
        }
        if (dupAddClause.getIndexColumnVector() != null) {
            final Vector tempIndexColumnNamesVector = dupAddClause.getIndexColumnVector();
            for (int i = 0; i < tempIndexColumnNamesVector.size(); ++i) {
                final String s = tempIndexColumnNamesVector.get(i);
            }
        }
        if (dupAddClause.getFirst() != null) {
            dupAddClause.getFirst();
        }
        if (dupAddClause.getAfter() != null) {
            dupAddClause.getAfter();
        }
        if (dupAddClause.getBeforeOrAfterColumnName() != null) {
            dupAddClause.getBeforeOrAfterColumnName();
        }
        if (dupAddClause.getBefore() != null) {
            dupAddClause.setBefore(null);
            dupAddClause.setBeforeOrAfterColumnName(null);
        }
        dupAddClause.setPhysicalCharacteristics(null);
        dupAddClause.setDiskAttributes(null);
        dupAddClause.setHashing(null);
        dupAddClause.setUsing(null);
        if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setPartitionKey(null);
        dupAddClause.setColumnArrayList(null);
        if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setIndexClause();
        if (dupAddClause.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setStorage(null);
        dupAddClause.setPhysicalStorageAttributes(null);
        return dupAddClause;
    }
    
    public AddClause toTimesTen() throws ConvertException {
        final AddClause dupAddClause = this.copyObjectValues();
        dupAddClause.setWith(null);
        dupAddClause.setCheckOrNoCheck(null);
        dupAddClause.setColumn(null);
        if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
            throw new ConvertException("\nUnsupported SQL\n");
        }
        dupAddClause.setUniqueConstraintName(null);
        dupAddClause.setIndexColumnVector(null);
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = dupAddClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.setAlterStatement(true);
                    changeCreateColumn.setColumnName(CustomizeUtil.objectNamesToQuotedIdentifier(changeCreateColumn.getColumnName(), SwisSQLUtils.getKeywords(10), null, 10));
                    if (changeCreateColumn.getDefault() != null) {
                        if (AlterStatement.generalComment.trim().length() > 0 && AlterStatement.generalComment.indexOf("DEFAULT Constraint") == -1) {
                            AlterStatement.generalComment += "\n/* SwisSQL Message : DEFAULT Constraint is not supported TimesTen 5.1.21 */";
                        }
                        else if (AlterStatement.generalComment.indexOf("DEFAULT Constraint") == -1) {
                            AlterStatement.generalComment = "/* SwisSQL Message : DEFAULT Constraint is not supported in TimesTen 5.1.21 */";
                        }
                    }
                    changeCreateColumn.toTimesTenString();
                    if (changeCreateColumn.getNullStatus() != null) {
                        if (changeCreateColumn.getNullStatus().equalsIgnoreCase("not null")) {
                            if (AlterStatement.generalComment.trim().length() > 0 && AlterStatement.generalComment.indexOf("NOT NULL Constraint") == -1) {
                                AlterStatement.generalComment += "\n/* SwisSQL Message : NOT NULL Constraint is not supported in ALTER Queries in TimesTen 5.1.21 */";
                            }
                            else if (AlterStatement.generalComment.indexOf("NOT NULL Constraint") == -1) {
                                AlterStatement.generalComment = "/* SwisSQL Message : NOT NULL Constraint is not supported in ALTER Queries in TimesTen 5.1.21 */";
                            }
                        }
                        changeCreateColumn.setNullStatus(null);
                    }
                    final Vector getConstraintVector = changeCreateColumn.getConstraintClause();
                    final Vector constraintVector = new Vector();
                    if (getConstraintVector != null) {
                        for (int j = 0; j < getConstraintVector.size(); ++j) {
                            final ConstraintClause toTimesTenConstraintClause = getConstraintVector.get(j);
                            if (toTimesTenConstraintClause != null) {
                                final ConstraintType changeConstraintType = toTimesTenConstraintClause.getConstraintType();
                                if (changeConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                    throw new ConvertException("\nPrimary Key Constraints are not supported in ALTER Queries in TimesTen 5.1.21\n");
                                }
                            }
                            constraintVector.add(toTimesTenConstraintClause);
                        }
                        changeCreateColumn.setConstraintClause(constraintVector);
                    }
                }
            }
            if (dupAddClause.getCreateColumnVector() != null) {
                final Vector tempCreateColumnVector = dupAddClause.getCreateColumnVector();
                if (tempCreateColumnVector.size() > 1) {
                    dupAddClause.setOpenBrace("(");
                    dupAddClause.setClosedBrace(")");
                }
            }
        }
        if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition(null);
        }
        dupAddClause.setFirst(null);
        dupAddClause.setAfter(null);
        dupAddClause.setBefore(null);
        dupAddClause.setBeforeOrAfterColumnName(null);
        dupAddClause.setHashing(null);
        dupAddClause.setUsing(null);
        if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("\nUnsupported SQL\n");
        }
        dupAddClause.setPartitionKey(null);
        dupAddClause.setColumnArrayList(null);
        return dupAddClause;
    }
    
    public AddClause toNetezza() throws ConvertException {
        final AddClause dupAddClause = this.copyObjectValues();
        dupAddClause.setOpenBrace(null);
        dupAddClause.setClosedBrace(null);
        dupAddClause.setWith(null);
        dupAddClause.setCheckOrNoCheck(null);
        if (dupAddClause.getColumn() != null) {
            dupAddClause.getColumn();
        }
        if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setUniqueConstraintName(null);
        dupAddClause.setIndexColumnVector(null);
        dupAddClause.setFirst(null);
        dupAddClause.setAfter(null);
        dupAddClause.setBefore(null);
        dupAddClause.setBeforeOrAfterColumnName(null);
        dupAddClause.setPhysicalCharacteristics(null);
        dupAddClause.setDiskAttributes(null);
        dupAddClause.setHashing(null);
        dupAddClause.setUsing(null);
        if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setPartitionKey(null);
        dupAddClause.setColumnArrayList(null);
        if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setIndexClause();
        if (dupAddClause.getCommaBooleanValue()) {
            dupAddClause.getCommaBooleanValue();
        }
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = dupAddClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    if (changeCreateColumn.getColumnName() != null) {
                        throw new ConvertException("/*SwisSQL Message : Netezza does not support adding columns to table.*/");
                    }
                    final Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                    if (changeConstraintVector != null) {
                        for (int j = 0; j < changeConstraintVector.size(); ++j) {
                            final ConstraintClause toNetezzaSQLConstraintClause = changeConstraintVector.get(j);
                            if (toNetezzaSQLConstraintClause != null) {
                                final ConstraintType toNetezzaConstraintType = toNetezzaSQLConstraintClause.getConstraintType();
                                if (toNetezzaConstraintType instanceof CheckConstraintClause) {
                                    SwisSQLUtils.swissqlMessageList.add("CHECK Constraint is not supported in Netezza. Manual intervention required");
                                }
                                else {
                                    toNetezzaSQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                    toNetezzaSQLConstraintClause.toNetezzaString();
                                }
                            }
                        }
                    }
                    changeCreateColumn.toNetezzaString();
                }
            }
        }
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector tempCreateColumnVector = dupAddClause.getCreateColumnVector();
            if (tempCreateColumnVector.size() > 1) {
                dupAddClause.setColumn(null);
            }
        }
        if (dupAddClause.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setStorage(null);
        dupAddClause.setPhysicalStorageAttributes(null);
        return dupAddClause;
    }
    
    public AddClause toTeradata() throws ConvertException {
        final AddClause dupAddClause = this.copyObjectValues();
        dupAddClause.setOpenBrace(null);
        dupAddClause.setClosedBrace(null);
        dupAddClause.setWith(null);
        dupAddClause.setCheckOrNoCheck(null);
        if (dupAddClause.getColumn() != null) {
            dupAddClause.getColumn();
        }
        if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setUniqueConstraintName(null);
        dupAddClause.setIndexColumnVector(null);
        dupAddClause.setFirst(null);
        dupAddClause.setAfter(null);
        dupAddClause.setBefore(null);
        dupAddClause.setBeforeOrAfterColumnName(null);
        dupAddClause.setPhysicalCharacteristics(null);
        dupAddClause.setDiskAttributes(null);
        dupAddClause.setHashing(null);
        dupAddClause.setUsing(null);
        if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setPartitionKey(null);
        dupAddClause.setColumnArrayList(null);
        if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setIndexClause();
        if (dupAddClause.getCommaBooleanValue()) {
            dupAddClause.getCommaBooleanValue();
        }
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector columnNamesVector = dupAddClause.getCreateColumnVector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.toTeradataString();
                    final Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                    if (changeConstraintVector != null) {
                        for (int j = 0; j < changeConstraintVector.size(); ++j) {
                            final ConstraintClause toTeradataSQLConstraintClause = changeConstraintVector.get(j);
                            if (toTeradataSQLConstraintClause != null) {
                                final ConstraintType toTeradataConstraintType = toTeradataSQLConstraintClause.getConstraintType();
                                toTeradataSQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                toTeradataSQLConstraintClause.toTeradataString();
                            }
                        }
                    }
                }
            }
        }
        if (dupAddClause.getCreateColumnVector() != null) {
            final Vector tempCreateColumnVector = dupAddClause.getCreateColumnVector();
            if (tempCreateColumnVector.size() > 1) {
                dupAddClause.setColumn(null);
            }
        }
        if (dupAddClause.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        dupAddClause.setStorage(null);
        dupAddClause.setPhysicalStorageAttributes(null);
        return dupAddClause;
    }
    
    public String removeIndent(String str) {
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        return str;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.with != null) {
            sb.append(this.with.toUpperCase());
        }
        if (this.checkOrNoCheck != null) {
            sb.append(" " + this.checkOrNoCheck.toUpperCase() + " ");
        }
        if (this.add != null) {
            sb.append(this.add.toUpperCase());
        }
        if (this.column != null) {
            sb.append(" " + this.column.toUpperCase());
        }
        if (this.constraintClauseVector != null) {
            for (int i = 0; i < this.constraintClauseVector.size(); ++i) {
                final ConstraintClause tempConstraintClause = this.constraintClauseVector.get(i);
                tempConstraintClause.setObjectContext(this.context);
                sb.append(" " + tempConstraintClause.toString());
            }
        }
        if (this.constraintClause != null) {
            this.constraintClause.setObjectContext(this.context);
            sb.append(" " + this.constraintClause.toString());
        }
        if (this.openBrace != null) {
            sb.append("\n" + this.openBrace);
        }
        if (this.createColumnVector != null) {
            if (this.createColumnVector.size() == 1) {
                final CreateColumn createColumnObject = this.createColumnVector.get(0);
                createColumnObject.setObjectContext(this.context);
                sb.append("\n\t" + createColumnObject.toString());
                if (this.first != null) {
                    sb.append(" " + this.first.toUpperCase());
                }
                if (this.after != null) {
                    sb.append(" " + this.after.toUpperCase());
                }
                if (this.before != null) {
                    sb.append(" " + this.before);
                }
                if (this.beforeOrAfterColumnName != null) {
                    sb.append(" " + this.beforeOrAfterColumnName);
                }
            }
            else {
                for (int i = 0; i < this.createColumnVector.size(); ++i) {
                    final CreateColumn createColumnObject2 = this.createColumnVector.get(i);
                    createColumnObject2.setObjectContext(this.context);
                    if (i == 0) {
                        sb.append("\n\t" + createColumnObject2.toString());
                    }
                    else if (this.commaIsSet) {
                        sb.append(",\n\t" + createColumnObject2.toString());
                    }
                    else {
                        sb.append("\nADD\n\t" + createColumnObject2.toString());
                    }
                }
            }
        }
        if (this.closeBrace != null) {
            sb.append("\n" + this.closeBrace);
        }
        if (this.diskAttributes != null) {
            sb.append("\n\t" + this.diskAttributes.toUpperCase());
        }
        if (this.physicalCharacteristics != null) {
            sb.append("\n\t" + this.physicalCharacteristics.toUpperCase());
        }
        if (this.partition != null) {
            sb.append("\n\t" + this.partition.toString());
        }
        if (this.partitioning != null) {
            sb.append(" " + this.partitioning.toUpperCase());
        }
        if (this.key != null) {
            sb.append(" " + this.key.toUpperCase());
        }
        if (this.columnArrayList != null) {
            sb.append("\n(");
            for (int i = 0; i < this.columnArrayList.size(); ++i) {
                if (i == 0) {
                    sb.append("\n\t" + this.columnArrayList.get(i));
                }
                else {
                    sb.append("," + this.columnArrayList.get(i));
                }
            }
            sb.append("\n)");
        }
        if (this.using != null) {
            sb.append("\n" + this.using.toUpperCase());
        }
        if (this.hashing != null) {
            sb.append(" " + this.hashing.toUpperCase());
        }
        if (this.overflow != null) {
            sb.append(" " + this.overflow.toUpperCase());
        }
        if (this.storage != null) {
            sb.append(" " + this.storage.toUpperCase());
        }
        if (this.physicalStorageAttributes != null) {
            sb.append(" (" + this.physicalStorageAttributes.toUpperCase() + ")");
        }
        if (this.uniqueOrPrimaryOrIndexOrFullText != null) {
            sb.append("\n" + this.uniqueOrPrimaryOrIndexOrFullText.toUpperCase());
        }
        if (this.uniqueConstraintName != null) {
            if (this.context != null) {
                final String temp = this.context.getEquivalent(this.uniqueConstraintName).toString();
                sb.append(" " + temp);
            }
            else {
                sb.append(" " + this.uniqueConstraintName);
            }
        }
        if (this.indexColumnVector != null) {
            sb.append(" (");
            for (int i = 0; i < this.indexColumnVector.size(); ++i) {
                final String tempIndexColumnObject = this.indexColumnVector.get(i);
                if (i == 0) {
                    sb.append(tempIndexColumnObject);
                }
                else {
                    sb.append(", " + tempIndexColumnObject);
                }
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
