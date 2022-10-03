package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.create.PartitionListAttributes;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class DropClause
{
    private UserObjectContext context;
    private String drop;
    private String restrictOrCascade;
    private String checkOrNoCheck;
    private String constraintOrColumnName;
    private String constraintTypeOrTrigger;
    private ConstraintClause constraintClause;
    private CreateColumn createColumn;
    private String column;
    private String openBraces;
    private String closedBraces;
    private Vector columnNamesVector;
    private String all;
    private Vector columnOrConstraintOrTriggerNameVector;
    private boolean isOpenBracesForConstraintSet;
    private boolean isColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne;
    private String db2ConstraintName;
    private PartitionListAttributes partitionListAttributes;
    private String partitioningKey;
    private String index;
    private String indexName;
    
    public DropClause() {
        this.context = null;
        this.isColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne = false;
    }
    
    public void setDrop(final String drop) {
        this.drop = drop;
    }
    
    public void setColumn(final String column) {
        this.column = column;
    }
    
    public void setOpenBraces(final String openBraces) {
        this.openBraces = openBraces;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setColumnNamesVector(final Vector columnNamesVector) {
        this.columnNamesVector = columnNamesVector;
    }
    
    public void setClosedBraces(final String closedBraces) {
        this.closedBraces = closedBraces;
    }
    
    public void setRestrictOrCascade(final String restrictOrCascade) {
        this.restrictOrCascade = restrictOrCascade;
    }
    
    public void setCreateColumn(final CreateColumn createColumn) {
        this.createColumn = createColumn;
    }
    
    public void setConstraintClause(final ConstraintClause constraintClause) {
        this.constraintClause = constraintClause;
    }
    
    public void setConstraintTypeOrTrigger(final String constraintTypeOrTrigger) {
        this.constraintTypeOrTrigger = constraintTypeOrTrigger;
    }
    
    public void setConstraintOrColumnName(final String constraintOrColumnName) {
        this.constraintOrColumnName = constraintOrColumnName;
    }
    
    public void setCheckOrNoCheck(final String checkOrNoCheck) {
        this.checkOrNoCheck = checkOrNoCheck;
    }
    
    public void setAll(final String all) {
        this.all = all;
    }
    
    public void setDB2ConstraintName(final String db2ConstraintName) {
        this.db2ConstraintName = db2ConstraintName;
    }
    
    public void setOpenBracesForConstraint(final boolean isOpenBracesForConstraintSet) {
        this.isOpenBracesForConstraintSet = isOpenBracesForConstraintSet;
    }
    
    public void setColumnOrConstraintOrTriggerNameVector(final Vector columnOrConstraintOrTriggerNameVector) {
        this.columnOrConstraintOrTriggerNameVector = columnOrConstraintOrTriggerNameVector;
    }
    
    public void setColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne(final boolean isColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne) {
        this.isColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne = isColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne;
    }
    
    public void setPartition(final PartitionListAttributes partitionListAttributes) {
        this.partitionListAttributes = partitionListAttributes;
    }
    
    public void setPartitioningKey(final String partitioningKey) {
        this.partitioningKey = partitioningKey;
    }
    
    public void setIndex(final String index) {
        this.index = index;
    }
    
    public void setIndexName(final String indexName) {
        this.indexName = indexName;
    }
    
    public String getDrop() {
        return this.drop;
    }
    
    public String getColumn() {
        return this.column;
    }
    
    public String getOpenBraces() {
        return this.openBraces;
    }
    
    public Vector getColumnNamesVector() {
        return this.columnNamesVector;
    }
    
    public String getClosedBraces() {
        return this.closedBraces;
    }
    
    public String getAll() {
        return this.all;
    }
    
    public Vector getColumnOrConstraintOrTriggerNameVector() {
        return this.columnOrConstraintOrTriggerNameVector;
    }
    
    public String getRestrictOrCascade() {
        return this.restrictOrCascade;
    }
    
    public String getConstraintTypeOrTrigger() {
        return this.constraintTypeOrTrigger;
    }
    
    public String getConstraintOrColumnName() {
        return this.constraintOrColumnName;
    }
    
    public String getCheckOrNoCheck() {
        return this.checkOrNoCheck;
    }
    
    public CreateColumn getCreateColumn() {
        return this.createColumn;
    }
    
    public ConstraintClause getConstraintClause() {
        return this.constraintClause;
    }
    
    public PartitionListAttributes getPartition() {
        return this.partitionListAttributes;
    }
    
    public String getPartitioningKey() {
        return this.partitioningKey;
    }
    
    public String getIndex() {
        return this.index;
    }
    
    public String getIndexName() {
        return this.indexName;
    }
    
    public DropClause toOracle() throws ConvertException {
        final DropClause tempDropClause = this.copyObjectValues();
        if (tempDropClause.getDrop() != null) {
            final String tempDrop = tempDropClause.getDrop();
            if (tempDrop.toUpperCase().equalsIgnoreCase("DELETE")) {
                tempDropClause.setDrop("DROP");
            }
        }
        if (tempDropClause.getColumn() != null) {
            tempDropClause.getColumn();
        }
        if (tempDropClause.getOpenBraces() != null) {
            tempDropClause.getOpenBraces();
        }
        if (tempDropClause.getClosedBraces() != null) {
            tempDropClause.getClosedBraces();
        }
        if (tempDropClause.getColumnNamesVector() != null) {
            final Vector tempColumnNamesVector = tempDropClause.getColumnNamesVector();
            for (int i = 0; i < tempColumnNamesVector.size(); ++i) {
                String tempColumnName = tempColumnNamesVector.get(i);
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
                    tempColumnNamesVector.setElementAt(tempColumnName, i);
                }
            }
            if (tempColumnNamesVector.size() > 1) {
                tempDropClause.setOpenBraces("(");
                tempDropClause.setClosedBraces(")");
                tempDropClause.setColumn(null);
            }
        }
        if (tempDropClause.getPartition() != null) {
            final PartitionListAttributes tempPartitionListAttributes = tempDropClause.getPartition();
            tempPartitionListAttributes.toOracle();
        }
        tempDropClause.setConstraintClause(null);
        if (tempDropClause.getConstraintTypeOrTrigger() != null) {
            String tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
            if (!tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("UNIQUE") && !tempConstraintTypeOrTrigger.startsWith("PRIMARY")) {
                tempDropClause.setColumnOrConstraintOrTriggerNameVector(null);
                throw new ConvertException();
            }
            tempDropClause.setConstraintTypeOrTrigger(tempConstraintTypeOrTrigger);
            if (tempConstraintTypeOrTrigger.equalsIgnoreCase("UNIQUE")) {
                tempDropClause.setOpenBracesForConstraint(true);
            }
            else {
                tempDropClause.setOpenBracesForConstraint(false);
            }
        }
        if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
            final Vector tempColumnOrConstraintOrTriggerNameVector = tempDropClause.getColumnOrConstraintOrTriggerNameVector();
            for (int i = 0; i < tempColumnOrConstraintOrTriggerNameVector.size(); ++i) {
                String columnOrConstraintOrTriggerName = tempColumnOrConstraintOrTriggerNameVector.get(i);
                if ((columnOrConstraintOrTriggerName.startsWith("[") && columnOrConstraintOrTriggerName.endsWith("]")) || (columnOrConstraintOrTriggerName.startsWith("`") && columnOrConstraintOrTriggerName.endsWith("`"))) {
                    columnOrConstraintOrTriggerName = columnOrConstraintOrTriggerName.substring(1, columnOrConstraintOrTriggerName.length() - 1);
                    if (SwisSQLOptions.retainQuotedIdentifierForOracle || columnOrConstraintOrTriggerName.indexOf(32) != -1) {
                        columnOrConstraintOrTriggerName = "\"" + columnOrConstraintOrTriggerName + "\"";
                    }
                }
                columnOrConstraintOrTriggerName = CustomizeUtil.objectNamesToQuotedIdentifier(columnOrConstraintOrTriggerName, SwisSQLUtils.getKeywords(1), null, 1);
                tempColumnOrConstraintOrTriggerNameVector.setElementAt(columnOrConstraintOrTriggerName, i);
            }
        }
        if (tempDropClause.getRestrictOrCascade() != null) {
            String tempRestrictOrCascade = tempDropClause.getRestrictOrCascade();
            tempRestrictOrCascade = tempRestrictOrCascade.trim();
            tempRestrictOrCascade = tempRestrictOrCascade.toUpperCase();
            if (tempRestrictOrCascade.equalsIgnoreCase("CASCADE")) {
                tempDropClause.setRestrictOrCascade("CASCADE");
            }
            else {
                tempDropClause.setRestrictOrCascade(null);
            }
        }
        if (tempDropClause.getAll() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempDropClause.setCheckOrNoCheck(null);
        tempDropClause.setAll(null);
        if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        return tempDropClause;
    }
    
    public DropClause toMSSQLServer() throws ConvertException {
        final DropClause tempDropClause = this.copyObjectValues();
        tempDropClause.setOpenBraces(null);
        tempDropClause.setClosedBraces(null);
        if (tempDropClause.getDrop() != null) {
            final String tempDrop = tempDropClause.getDrop();
            if (tempDrop.toUpperCase().equalsIgnoreCase("DELETE")) {
                tempDropClause.setDrop("DROP");
            }
        }
        if (tempDropClause.getColumnNamesVector() != null) {
            tempDropClause.setColumn("COLUMN");
            final Vector tempColumnNamesVector = tempDropClause.getColumnNamesVector();
            for (int i = 0; i < tempColumnNamesVector.size(); ++i) {
                final String s = tempColumnNamesVector.get(i);
            }
        }
        tempDropClause.setOpenBracesForConstraint(false);
        if (tempDropClause.getCheckOrNoCheck() != null) {
            tempDropClause.getCheckOrNoCheck();
        }
        if (tempDropClause.getConstraintTypeOrTrigger() != null) {
            String tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
            if (tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT")) {
                tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
            }
            else if (tempConstraintTypeOrTrigger.startsWith("ENABLE") || tempConstraintTypeOrTrigger.startsWith("DISABLE")) {
                tempDropClause.setConstraintTypeOrTrigger(tempConstraintTypeOrTrigger);
            }
            else {
                if (!tempConstraintTypeOrTrigger.equalsIgnoreCase("FOREIGN KEY")) {
                    tempDropClause.setConstraintTypeOrTrigger("MSSQLSERVER DOES NOT SUPPORT THIS QUERY OF DROP CLAUSE");
                    tempDropClause.setColumnOrConstraintOrTriggerNameVector(null);
                    throw new ConvertException();
                }
                tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
            }
        }
        if (tempDropClause.getAll() != null) {
            tempDropClause.getAll();
        }
        if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
            tempDropClause.getColumnOrConstraintOrTriggerNameVector();
        }
        if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempDropClause.setRestrictOrCascade(null);
        tempDropClause.setConstraintClause(null);
        if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        return tempDropClause;
    }
    
    public DropClause toSybase() throws ConvertException {
        final DropClause tempDropClause = this.copyObjectValues();
        tempDropClause.setOpenBraces(null);
        tempDropClause.setClosedBraces(null);
        if (tempDropClause.getDrop() != null) {
            tempDropClause.getDrop();
        }
        if (tempDropClause.getColumnNamesVector() != null) {
            tempDropClause.setColumn(null);
            final Vector tempColumnNamesVector = tempDropClause.getColumnNamesVector();
            for (int i = 0; i < tempColumnNamesVector.size(); ++i) {
                final String s = tempColumnNamesVector.get(i);
            }
        }
        tempDropClause.setOpenBracesForConstraint(false);
        if (tempDropClause.getCheckOrNoCheck() != null) {
            tempDropClause.setCheckOrNoCheck("CONSTRAINT");
        }
        if (tempDropClause.getConstraintTypeOrTrigger() != null) {
            String tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
            if (tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT")) {
                tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
            }
            else {
                if (!tempConstraintTypeOrTrigger.startsWith("ENABLE") && !tempConstraintTypeOrTrigger.startsWith("DISABLE")) {
                    tempDropClause.setConstraintTypeOrTrigger("Sybase DOES NOT SUPPORT THIS QUERY OF DROP CLAUSE");
                    tempDropClause.setColumnOrConstraintOrTriggerNameVector(null);
                    throw new ConvertException();
                }
                tempDropClause.setConstraintTypeOrTrigger(tempConstraintTypeOrTrigger);
            }
        }
        if (tempDropClause.getAll() != null) {
            tempDropClause.getAll();
        }
        if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
            tempDropClause.getColumnOrConstraintOrTriggerNameVector();
        }
        if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempDropClause.setRestrictOrCascade(null);
        tempDropClause.setConstraintClause(null);
        if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        return tempDropClause;
    }
    
    public DropClause toDB2() throws ConvertException {
        final DropClause tempDropClause = this.copyObjectValues();
        if (tempDropClause.getDrop() != null) {
            final String tempDrop = tempDropClause.getDrop();
            if (tempDrop.toUpperCase().equalsIgnoreCase("DELETE")) {
                tempDropClause.setDrop("DROP");
            }
        }
        if (tempDropClause.getCheckOrNoCheck() != null) {
            tempDropClause.setColumnOrConstraintOrTriggerNameVector(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempDropClause.setOpenBracesForConstraint(false);
        if (tempDropClause.getConstraintTypeOrTrigger() != null) {
            String tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
            if (!tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("PRIMARY KEY") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("FOREIGN KEY") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("UNIQUE") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("CHECK")) {
                tempDropClause.setConstraintTypeOrTrigger("DB2 DOES NOT SUPPORT THIS QUERY OF DROP CLAUSE");
                tempDropClause.setDB2ConstraintName(null);
                throw new ConvertException();
            }
            tempDropClause.setConstraintTypeOrTrigger(tempConstraintTypeOrTrigger);
            tempDropClause.setDB2ConstraintName(tempConstraintTypeOrTrigger);
        }
        if (tempDropClause.getPartitioningKey() != null) {
            tempDropClause.getPartitioningKey();
        }
        if (tempDropClause.getColumnNamesVector() != null) {
            tempDropClause.setColumnNamesVector(null);
            final Vector tempColumnNamesVector = tempDropClause.getColumnNamesVector();
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
            final Vector tempColumnOrConstraintOrTriggerNameVector = tempDropClause.getColumnOrConstraintOrTriggerNameVector();
            if (tempColumnOrConstraintOrTriggerNameVector.size() > 1) {
                tempDropClause.setColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne(true);
            }
        }
        tempDropClause.setOpenBraces(null);
        tempDropClause.setClosedBraces(null);
        tempDropClause.setColumn(null);
        tempDropClause.setConstraintClause(null);
        if (tempDropClause.getAll() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempDropClause.setAll(null);
        tempDropClause.setRestrictOrCascade(null);
        if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        return tempDropClause;
    }
    
    public DropClause toANSI() throws ConvertException {
        final DropClause tempDropClause = this.copyObjectValues();
        if (tempDropClause.getDrop() != null) {
            final String tempDrop = tempDropClause.getDrop();
            if (tempDrop.toUpperCase().equalsIgnoreCase("DELETE")) {
                tempDropClause.setDrop("DROP");
            }
        }
        if (tempDropClause.getColumn() != null) {
            tempDropClause.getColumn();
        }
        if (tempDropClause.getColumnNamesVector() != null) {
            final Vector tempColumnNamesVector = tempDropClause.getColumnNamesVector();
            for (int i = 0; i < tempColumnNamesVector.size(); ++i) {
                if (tempColumnNamesVector.get(i) instanceof CreateColumn) {
                    final String s = tempColumnNamesVector.get(i);
                }
            }
        }
        tempDropClause.setOpenBracesForConstraint(false);
        if (tempDropClause.getConstraintTypeOrTrigger() != null) {
            String tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
            if (!tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT")) {
                throw new ConvertException();
            }
            tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
        }
        if (tempDropClause.getRestrictOrCascade() != null) {
            tempDropClause.getRestrictOrCascade();
        }
        if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
            tempDropClause.getColumnOrConstraintOrTriggerNameVector();
        }
        tempDropClause.setOpenBraces(null);
        tempDropClause.setClosedBraces(null);
        tempDropClause.setConstraintClause(null);
        if (tempDropClause.getAll() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempDropClause.setCheckOrNoCheck(null);
        tempDropClause.setAll(null);
        if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        return tempDropClause;
    }
    
    public DropClause toInformix() throws ConvertException {
        final DropClause tempDropClause = this.copyObjectValues();
        if (tempDropClause.getDrop() != null) {
            final String tempDrop = tempDropClause.getDrop();
            if (tempDrop.toUpperCase().equalsIgnoreCase("DELETE")) {
                tempDropClause.setDrop("DROP");
            }
        }
        if (tempDropClause.getColumn() != null) {
            tempDropClause.getColumn();
        }
        if (tempDropClause.getColumnNamesVector() != null) {
            final Vector tempColumnNamesVector = tempDropClause.getColumnNamesVector();
            for (int i = 0; i < tempColumnNamesVector.size(); ++i) {
                if (tempColumnNamesVector.get(i) instanceof CreateColumn) {
                    final String s = tempColumnNamesVector.get(i);
                }
            }
        }
        tempDropClause.setOpenBracesForConstraint(false);
        if (tempDropClause.getConstraintTypeOrTrigger() != null) {
            tempDropClause.getConstraintTypeOrTrigger();
        }
        if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
            tempDropClause.getColumnOrConstraintOrTriggerNameVector();
        }
        tempDropClause.setOpenBraces(null);
        tempDropClause.setClosedBraces(null);
        tempDropClause.setConstraintClause(null);
        if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempDropClause.getPartitioningKey() != null) {
            tempDropClause.setPartitioningKey("INFORMIX DOES NOT SUPPORT PARTITIONING KEY");
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempDropClause.getAll() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempDropClause.setCheckOrNoCheck(null);
        tempDropClause.setRestrictOrCascade(null);
        tempDropClause.setAll(null);
        return tempDropClause;
    }
    
    public DropClause toPostgreSQL() throws ConvertException {
        final DropClause tempDropClause = this.copyObjectValues();
        tempDropClause.setRestrictOrCascade(null);
        tempDropClause.setConstraintClause(null);
        tempDropClause.setConstraintTypeOrTrigger(null);
        tempDropClause.setOpenBraces(null);
        tempDropClause.setClosedBraces(null);
        tempDropClause.setOpenBracesForConstraint(false);
        tempDropClause.setCheckOrNoCheck(null);
        tempDropClause.setAll(null);
        tempDropClause.setColumnOrConstraintOrTriggerNameVector(null);
        if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition(null);
            tempDropClause.setDrop("PostgreSQL does not support Drop Clause with partition of Tables");
        }
        if (tempDropClause.getPartitioningKey() != null) {
            tempDropClause.setPartitioningKey("POSTGRESQL DOES NOT SUPPORT PARTITIONING KEY");
        }
        return tempDropClause;
    }
    
    public DropClause toMySQL() throws ConvertException {
        final DropClause tempDropClause = this.copyObjectValues();
        if (tempDropClause.getDrop() != null) {
            final String tempDrop = tempDropClause.getDrop();
            if (tempDrop.toUpperCase().equalsIgnoreCase("DELETE")) {
                tempDropClause.setDrop("DROP");
            }
        }
        if (tempDropClause.getColumn() != null) {
            tempDropClause.getColumn();
        }
        if (tempDropClause.getColumnNamesVector() != null) {
            final Vector tempColumnNamesVector = tempDropClause.getColumnNamesVector();
            for (int i = 0; i < tempColumnNamesVector.size(); ++i) {
                if (tempColumnNamesVector.get(i) instanceof CreateColumn) {
                    final String s = tempColumnNamesVector.get(i);
                }
            }
        }
        if (tempDropClause.getConstraintTypeOrTrigger() != null) {
            String tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
            if (!tempConstraintTypeOrTrigger.startsWith("PRIMARY") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("INDEX")) {
                throw new ConvertException();
            }
            tempDropClause.setConstraintTypeOrTrigger(tempConstraintTypeOrTrigger);
        }
        tempDropClause.setOpenBracesForConstraint(false);
        if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
            tempDropClause.getColumnOrConstraintOrTriggerNameVector();
        }
        tempDropClause.setOpenBraces(null);
        tempDropClause.setClosedBraces(null);
        tempDropClause.setRestrictOrCascade(null);
        tempDropClause.setConstraintClause(null);
        tempDropClause.setCheckOrNoCheck(null);
        tempDropClause.setAll(null);
        if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        return tempDropClause;
    }
    
    public DropClause toTimesTen() throws ConvertException {
        final DropClause tempDropClause = this.copyObjectValues();
        if (tempDropClause.getDrop() != null) {
            final String tempDrop = tempDropClause.getDrop();
            if (tempDrop.toUpperCase().equalsIgnoreCase("DELETE")) {
                tempDropClause.setDrop("DROP");
            }
        }
        tempDropClause.setOpenBracesForConstraint(false);
        if (this.columnNamesVector != null && this.columnNamesVector.size() > 1) {
            tempDropClause.setOpenBraces("(");
            tempDropClause.setClosedBraces(")");
        }
        tempDropClause.setConstraintClause(null);
        if (tempDropClause.getAll() != null) {
            throw new ConvertException("\nUnsupported SQL.\n");
        }
        tempDropClause.setCheckOrNoCheck(null);
        tempDropClause.setAll(null);
        if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition(null);
            throw new ConvertException("\nUnsupported SQL.\n");
        }
        if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("\nUnsupported SQL.\n");
        }
        return tempDropClause;
    }
    
    public DropClause toNetezza() throws ConvertException {
        final DropClause tempDropClause = this.copyObjectValues();
        if (tempDropClause.getDrop() != null) {
            final String tempDrop = tempDropClause.getDrop();
            if (tempDrop.toUpperCase().equalsIgnoreCase("DELETE")) {
                tempDropClause.setDrop("DROP");
            }
            else if (tempDrop.toUpperCase().equalsIgnoreCase("SET UNUSED")) {
                throw new ConvertException("/*SwisSQL Message: Netezza does not support SET UNUSED clause*/");
            }
        }
        if (tempDropClause.getColumn() != null) {
            final String tempColumn = tempDropClause.getColumn();
            throw new ConvertException("/*SwisSQL Message: Netezza does not support dropping of table columns*/");
        }
        if (tempDropClause.getColumnNamesVector() != null) {
            final Vector tempColumnNamesVector = tempDropClause.getColumnNamesVector();
            for (int i = 0; i < tempColumnNamesVector.size(); ++i) {
                if (tempColumnNamesVector.get(i) instanceof CreateColumn) {
                    final String s = tempColumnNamesVector.get(i);
                }
            }
        }
        tempDropClause.setOpenBracesForConstraint(false);
        if (tempDropClause.getConstraintTypeOrTrigger() != null) {
            String tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
            if (tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT")) {
                tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
            }
            else if (tempConstraintTypeOrTrigger.toLowerCase().startsWith("primary key")) {
                tempDropClause.setConstraintTypeOrTrigger(tempConstraintTypeOrTrigger.toUpperCase().replaceFirst("PRIMARY KEY", "CONSTRAINT"));
            }
            else {
                if (!tempConstraintTypeOrTrigger.equalsIgnoreCase("unique") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("foreign key")) {
                    throw new ConvertException();
                }
                tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
            }
        }
        if (tempDropClause.getRestrictOrCascade() != null) {
            tempDropClause.getRestrictOrCascade();
        }
        else {
            tempDropClause.setRestrictOrCascade("CASCADE");
        }
        if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
            tempDropClause.getColumnOrConstraintOrTriggerNameVector();
        }
        tempDropClause.setOpenBraces(null);
        tempDropClause.setClosedBraces(null);
        tempDropClause.setConstraintClause(null);
        if (tempDropClause.getAll() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempDropClause.setCheckOrNoCheck(null);
        tempDropClause.setAll(null);
        if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        return tempDropClause;
    }
    
    public DropClause toTeradata() throws ConvertException {
        final DropClause tempDropClause = this.copyObjectValues();
        if (tempDropClause.getDrop() != null) {
            final String tempDrop = tempDropClause.getDrop();
            if (tempDrop.toUpperCase().equalsIgnoreCase("DELETE")) {
                tempDropClause.setDrop("DROP");
            }
        }
        if (tempDropClause.getColumn() != null) {
            tempDropClause.getColumn();
        }
        if (tempDropClause.getColumnNamesVector() != null) {
            final Vector tempColumnNamesVector = tempDropClause.getColumnNamesVector();
            for (int i = 0; i < tempColumnNamesVector.size(); ++i) {
                if (tempColumnNamesVector.get(i) instanceof CreateColumn) {
                    final String s = tempColumnNamesVector.get(i);
                }
            }
        }
        tempDropClause.setOpenBracesForConstraint(false);
        if (tempDropClause.getConstraintTypeOrTrigger() != null) {
            String tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
            if (!tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT")) {
                throw new ConvertException();
            }
            tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
        }
        if (tempDropClause.getRestrictOrCascade() != null) {
            tempDropClause.getRestrictOrCascade();
        }
        if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
            tempDropClause.getColumnOrConstraintOrTriggerNameVector();
        }
        tempDropClause.setOpenBraces(null);
        tempDropClause.setClosedBraces(null);
        tempDropClause.setConstraintClause(null);
        if (tempDropClause.getAll() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        tempDropClause.setCheckOrNoCheck(null);
        tempDropClause.setAll(null);
        if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition(null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
        }
        return tempDropClause;
    }
    
    public DropClause copyObjectValues() {
        final DropClause dupDropClause = new DropClause();
        dupDropClause.setDrop(this.getDrop());
        dupDropClause.setColumn(this.getColumn());
        dupDropClause.setColumnNamesVector(this.getColumnNamesVector());
        dupDropClause.setRestrictOrCascade(this.getRestrictOrCascade());
        dupDropClause.setObjectContext(this.context);
        dupDropClause.setCreateColumn(this.getCreateColumn());
        dupDropClause.setConstraintClause(this.getConstraintClause());
        dupDropClause.setConstraintTypeOrTrigger(this.getConstraintTypeOrTrigger());
        dupDropClause.setCheckOrNoCheck(this.getCheckOrNoCheck());
        dupDropClause.setAll(this.getAll());
        dupDropClause.setColumnOrConstraintOrTriggerNameVector(this.getColumnOrConstraintOrTriggerNameVector());
        dupDropClause.setPartition(this.getPartition());
        dupDropClause.setPartitioningKey(this.getPartitioningKey());
        dupDropClause.setClosedBraces(this.getClosedBraces());
        dupDropClause.setOpenBraces(this.getOpenBraces());
        return dupDropClause;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.drop != null) {
            sb.append(this.drop.toUpperCase());
        }
        if (this.column != null) {
            sb.append(" " + this.column.toUpperCase());
        }
        if (this.openBraces != null) {
            sb.append("\n" + this.openBraces);
        }
        if (this.columnNamesVector != null) {
            for (int i = 0; i < this.columnNamesVector.size(); ++i) {
                final String createColumn = this.columnNamesVector.get(i);
                if (i == 0) {
                    sb.append("\n\t" + createColumn);
                }
                else {
                    sb.append(",\n\t" + createColumn);
                }
            }
        }
        if (this.closedBraces != null) {
            sb.append("\n" + this.closedBraces);
        }
        if (this.createColumn != null) {
            this.createColumn.setObjectContext(this.context);
            sb.append(this.createColumn.toString());
        }
        if (this.constraintClause != null) {
            this.constraintClause.setObjectContext(this.context);
            sb.append("\n" + this.constraintClause.toString());
        }
        if (this.partitionListAttributes != null) {
            sb.append("\n" + this.partitionListAttributes.toString());
        }
        if (this.partitioningKey != null) {
            sb.append(" " + this.partitioningKey.toUpperCase());
        }
        if (this.checkOrNoCheck != null) {
            sb.append(" " + this.checkOrNoCheck.toUpperCase());
        }
        if (this.constraintTypeOrTrigger != null) {
            sb.append(" " + this.constraintTypeOrTrigger.toUpperCase());
        }
        if (this.all != null) {
            sb.append(" " + this.all.toUpperCase());
        }
        if (this.columnOrConstraintOrTriggerNameVector != null) {
            if (this.isOpenBracesForConstraintSet) {
                sb.append("\n(\n");
                for (int i = 0; i < this.columnOrConstraintOrTriggerNameVector.size(); ++i) {
                    final String columnOrConstraintOrTriggerName = this.columnOrConstraintOrTriggerNameVector.get(i);
                    if (i == 0) {
                        sb.append("\t" + columnOrConstraintOrTriggerName);
                    }
                    else {
                        sb.append(", " + columnOrConstraintOrTriggerName);
                    }
                }
                sb.append("\n)");
            }
            else {
                for (int i = 0; i < this.columnOrConstraintOrTriggerNameVector.size(); ++i) {
                    final String columnOrConstraintOrTriggerName = this.columnOrConstraintOrTriggerNameVector.get(i);
                    if (i == 0) {
                        sb.append("\n\t " + columnOrConstraintOrTriggerName);
                    }
                    else if (this.isColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne) {
                        if (this.db2ConstraintName != null) {
                            sb.append("\nDROP " + this.db2ConstraintName.toUpperCase() + "\n\t " + columnOrConstraintOrTriggerName);
                        }
                        else {
                            sb.append("\nDROP \n\t " + columnOrConstraintOrTriggerName);
                        }
                    }
                    else {
                        sb.append(", " + columnOrConstraintOrTriggerName);
                    }
                }
            }
        }
        if (this.restrictOrCascade != null) {
            sb.append(" " + this.restrictOrCascade.toUpperCase());
        }
        return sb.toString();
    }
}
