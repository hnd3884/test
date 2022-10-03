package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import java.util.Vector;

public class ForeignConstraintClause implements ConstraintType
{
    private Vector constraintColumnNames;
    private String constraintName;
    private String openBrace;
    private String closedBrace;
    private String referenceOpenBrace;
    private String referenceClosedBrace;
    private String reference;
    private TableObject referenceTable;
    private Vector referenceTableColumnNames;
    private String matchLevel;
    private String onUpdate;
    private String onDelete;
    private String actionOnDelete;
    private String actionOnUpdate;
    private String columnName;
    private UserObjectContext context;
    private String tableNameFromCQS;
    
    public ForeignConstraintClause() {
        this.context = null;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setConstraintColumnNames(final Vector constraintColumnNames) {
        this.constraintColumnNames = constraintColumnNames;
    }
    
    public void setConstraintName(final String constraintName) {
        this.constraintName = constraintName;
    }
    
    public void setReference(final String reference) {
        this.reference = reference;
    }
    
    public void setTableName(final TableObject referenceTable) {
        this.referenceTable = referenceTable;
    }
    
    public void setReferenceTableColumnNames(final Vector referenceTableColumnNames) {
        this.referenceTableColumnNames = referenceTableColumnNames;
    }
    
    public void setMatch(final String matchLevel) {
        this.matchLevel = matchLevel;
    }
    
    public void setOnUpdate(final String onUpdate) {
        this.onUpdate = onUpdate;
    }
    
    public void setOnDelete(final String onDelete) {
        this.onDelete = onDelete;
    }
    
    public void setActionOnUpdate(final String actionOnUpdate) {
        this.actionOnUpdate = actionOnUpdate;
    }
    
    public void setActionOnDelete(final String actionOnDelete) {
        this.actionOnDelete = actionOnDelete;
    }
    
    public void setOpenBrace(final String openBrace) {
        this.openBrace = openBrace;
    }
    
    public void setClosedBrace(final String closedBrace) {
        this.closedBrace = closedBrace;
    }
    
    public void setReferenceOpenBrace(final String referenceOpenBrace) {
        this.referenceOpenBrace = referenceOpenBrace;
    }
    
    public void setReferenceClosedBrace(final String referenceClosedBrace) {
        this.referenceClosedBrace = referenceClosedBrace;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public void setTableNameFromCQS(final String tableNameFromCQS) {
        this.tableNameFromCQS = tableNameFromCQS;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public Vector getConstraintColumnNames() {
        return this.constraintColumnNames;
    }
    
    public String getConstraintName() {
        return this.constraintName;
    }
    
    public String getReference() {
        return this.reference;
    }
    
    public TableObject getTableName() {
        return this.referenceTable;
    }
    
    public Vector getReferenceTableColumnNames() {
        return this.referenceTableColumnNames;
    }
    
    public String getMatch() {
        return this.matchLevel;
    }
    
    public String getOnUpdate() {
        return this.onUpdate;
    }
    
    public String getOnDelete() {
        return this.onDelete;
    }
    
    public String getActionOnUpdate() {
        return this.actionOnUpdate;
    }
    
    public String getActionOnDelete() {
        return this.actionOnDelete;
    }
    
    @Override
    public void toDB2String() throws ConvertException {
        this.setMatch(null);
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.constraintColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
        if (this.referenceTable != null) {
            String ownerName = this.referenceTable.getOwner();
            String tableName = this.referenceTable.getTableName();
            String userName = this.referenceTable.getUser();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            this.referenceTable.setOwner(ownerName);
            this.referenceTable.setTableName(tableName);
            this.referenceTable.setUser(userName);
            this.referenceTable.toDB2();
        }
        if (this.referenceTableColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.referenceTableColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                }
            }
            this.setReferenceTableColumnNames(oracleColumnVector);
        }
        if (this.getActionOnUpdate() != null && (this.getActionOnUpdate().equalsIgnoreCase("SET DEFAULT") || this.getActionOnUpdate().equalsIgnoreCase("SET NULL") || this.getActionOnUpdate().equalsIgnoreCase("CASCADE"))) {
            this.setActionOnUpdate(null);
            this.setOnUpdate(null);
        }
        if (this.getActionOnDelete() != null && this.getActionOnDelete().equalsIgnoreCase("SET DEFAULT")) {
            this.setActionOnDelete("SET NULL");
        }
        if (this.getColumnName() != null && this.getConstraintName() != null) {
            this.setConstraintName(null);
            this.setConstraintColumnNames(null);
        }
    }
    
    @Override
    public void toMSSQLServerString() throws ConvertException {
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.constraintColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
        if (this.referenceTableColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.referenceTableColumnNames.get(i);
                    if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                }
            }
            this.setReferenceTableColumnNames(oracleColumnVector);
        }
        if (this.referenceTable != null) {
            String ownerName = this.referenceTable.getOwner();
            String tableName = this.referenceTable.getTableName();
            String userName = this.referenceTable.getUser();
            if (ownerName != null && ownerName.startsWith("`") && ownerName.endsWith("`")) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && tableName.startsWith("`") && tableName.endsWith("`")) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (userName != null && userName.startsWith("`") && userName.endsWith("`")) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            this.referenceTable.setOwner(ownerName);
            this.referenceTable.setTableName(tableName);
            this.referenceTable.setUser(userName);
        }
        if (this.getActionOnUpdate() != null) {
            if (this.getActionOnUpdate().equalsIgnoreCase("SET DEFAULT") || this.getActionOnUpdate().equalsIgnoreCase("SET NULL")) {
                this.setOnUpdate(null);
                this.setActionOnUpdate(null);
            }
            else if (this.getActionOnUpdate().equalsIgnoreCase("RESTRICT")) {
                this.setActionOnUpdate("NO ACTION");
            }
        }
        if (this.getActionOnDelete() != null) {
            if (SwisSQLOptions.sqlServerTriggers && (this.getActionOnDelete().equalsIgnoreCase("SET DEFAULT") || this.getActionOnDelete().equalsIgnoreCase("SET NULL"))) {
                this.setOnDelete(null);
                this.setActionOnDelete(null);
            }
            else if (this.getActionOnDelete().equalsIgnoreCase("RESTRICT")) {
                this.setActionOnDelete("NO ACTION");
            }
        }
    }
    
    @Override
    public void toSybaseString() throws ConvertException {
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.constraintColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
        if (this.referenceTableColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.referenceTableColumnNames.get(i);
                    if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                }
            }
            this.setReferenceTableColumnNames(oracleColumnVector);
        }
        if (this.referenceTable != null) {
            String ownerName = this.referenceTable.getOwner();
            String tableName = this.referenceTable.getTableName();
            String userName = this.referenceTable.getUser();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            this.referenceTable.setOwner(ownerName);
            this.referenceTable.setTableName(tableName);
            this.referenceTable.setUser(userName);
        }
        if (this.getActionOnUpdate() != null) {
            if (this.getActionOnUpdate().equalsIgnoreCase("SET DEFAULT") || this.getActionOnUpdate().equalsIgnoreCase("SET NULL")) {
                this.setOnUpdate(null);
                this.setActionOnUpdate(null);
            }
            else if (this.getActionOnUpdate().equalsIgnoreCase("RESTRICT")) {
                this.setActionOnUpdate("NO ACTION");
            }
            this.setOnUpdate(null);
            this.setActionOnUpdate(null);
        }
        if (this.getActionOnDelete() != null) {
            if (this.getActionOnDelete().equalsIgnoreCase("SET DEFAULT") || this.getActionOnDelete().equalsIgnoreCase("SET NULL")) {
                this.setOnDelete(null);
                this.setActionOnDelete(null);
            }
            else if (this.getActionOnDelete().equalsIgnoreCase("RESTRICT")) {
                this.setActionOnDelete("NO ACTION");
            }
            this.setOnDelete(null);
            this.setActionOnDelete(null);
        }
    }
    
    @Override
    public void toOracleString() throws ConvertException {
        this.setMatch(null);
        if (this.getActionOnUpdate() != null) {
            this.setOnUpdate(null);
            this.setActionOnUpdate(null);
        }
        if (this.getActionOnDelete() != null) {
            if (this.getActionOnDelete().equalsIgnoreCase("SET DEFAULT")) {
                this.setActionOnDelete("SET NULL");
            }
            else if (this.getActionOnDelete().equalsIgnoreCase("RESTRICT") || this.getActionOnDelete().equalsIgnoreCase("NO ACTION")) {
                this.setOnDelete(null);
                this.setActionOnDelete(null);
            }
        }
        if (this.getColumnName() != null && this.getConstraintName() != null) {
            this.setConstraintName(null);
            this.setConstraintColumnNames(null);
            this.setOpenBrace(null);
            this.setClosedBrace(null);
        }
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    constraintColumn = CustomizeUtil.objectNamesToQuotedIdentifier(constraintColumn, SwisSQLUtils.getKeywords(1), null, 1);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (SwisSQLOptions.retainQuotedIdentifierForOracle || constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(constraintColumn);
                    }
                    if (this.tableNameFromCQS == null) {
                        boolean addQuotes = false;
                        if (constraintColumn.startsWith("\"") && constraintColumn.endsWith("\"")) {
                            constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                            addQuotes = true;
                        }
                        if (constraintColumn.length() > 30) {
                            constraintColumn = constraintColumn.substring(0, 30);
                            if (addQuotes) {
                                constraintColumn = "\"" + constraintColumn + "\"";
                            }
                            oracleColumnVector.setElementAt(constraintColumn, oracleColumnVector.size() - 1);
                        }
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
        if (this.referenceTableColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.referenceTableColumnNames.get(i);
                    constraintColumn = CustomizeUtil.objectNamesToQuotedIdentifier(constraintColumn, SwisSQLUtils.getKeywords(1), null, 1);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (SwisSQLOptions.retainQuotedIdentifierForOracle || constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(constraintColumn);
                    }
                    boolean addQuotes = false;
                    if (constraintColumn.startsWith("\"") && constraintColumn.endsWith("\"")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        addQuotes = true;
                    }
                    if (constraintColumn.length() > 30) {
                        constraintColumn = constraintColumn.substring(0, 30);
                        if (addQuotes) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.setElementAt(constraintColumn, oracleColumnVector.size() - 1);
                    }
                }
                else {
                    oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                }
            }
            this.setReferenceTableColumnNames(oracleColumnVector);
        }
        if (this.referenceTable != null) {
            String ownerName = this.referenceTable.getOwner();
            String tableName = this.referenceTable.getTableName();
            String userName = this.referenceTable.getUser();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(1), null, 1);
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            this.referenceTable.setOwner(ownerName);
            this.referenceTable.setTableName(tableName);
            this.referenceTable.setUser(userName);
        }
    }
    
    @Override
    public void toPostgreSQLString() throws ConvertException {
        this.setMatch(null);
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.constraintColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
        if (this.referenceTableColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.referenceTableColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                }
            }
            this.setReferenceTableColumnNames(oracleColumnVector);
        }
        if (this.referenceTable != null) {
            String ownerName = this.referenceTable.getOwner();
            String tableName = this.referenceTable.getTableName();
            String userName = this.referenceTable.getUser();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            this.referenceTable.setOwner(ownerName);
            this.referenceTable.setTableName(tableName);
            this.referenceTable.setUser(userName);
        }
        if (this.getColumnName() != null && this.getConstraintName() != null) {
            this.setConstraintName(null);
            this.setConstraintColumnNames(null);
            this.setOpenBrace(null);
            this.setClosedBrace(null);
        }
    }
    
    @Override
    public void toANSIString() throws ConvertException {
        this.setMatch(null);
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.constraintColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
        if (this.referenceTableColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.referenceTableColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                }
            }
            this.setReferenceTableColumnNames(oracleColumnVector);
        }
        if (this.referenceTable != null) {
            String ownerName = this.referenceTable.getOwner();
            String tableName = this.referenceTable.getTableName();
            String userName = this.referenceTable.getUser();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            this.referenceTable.setOwner(ownerName);
            this.referenceTable.setTableName(tableName);
            this.referenceTable.setUser(userName);
        }
        if (this.getColumnName() != null && this.getConstraintName() != null) {
            this.setConstraintName(null);
            this.setConstraintColumnNames(null);
            this.setOpenBrace(null);
            this.setClosedBrace(null);
        }
    }
    
    @Override
    public void toMySQLString() throws ConvertException {
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if (constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "`" + constraintColumn + "`";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.constraintColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
        if (this.referenceTableColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.referenceTableColumnNames.get(i);
                    if (constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "`" + constraintColumn + "`";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                }
            }
            this.setReferenceTableColumnNames(oracleColumnVector);
        }
        if (this.referenceTable != null) {
            String ownerName = this.referenceTable.getOwner();
            String tableName = this.referenceTable.getTableName();
            String userName = this.referenceTable.getUser();
            if (ownerName != null && ownerName.startsWith("[") && ownerName.endsWith("]")) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "`" + ownerName + "`";
                }
            }
            if (tableName != null && tableName.startsWith("[") && tableName.endsWith("]")) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "`" + tableName + "`";
                }
            }
            if (userName != null && userName.startsWith("[") && userName.endsWith("]")) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "`" + userName + "`";
                }
            }
            this.referenceTable.setOwner(ownerName);
            this.referenceTable.setTableName(tableName);
            this.referenceTable.setUser(userName);
        }
        this.setMatch(null);
        if (this.getColumnName() != null && this.getConstraintName() != null) {
            this.setConstraintName(null);
            this.setConstraintColumnNames(null);
            this.setOpenBrace(null);
            this.setClosedBrace(null);
        }
    }
    
    @Override
    public void toInformixString() throws ConvertException {
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.constraintColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
        if (this.referenceTableColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.referenceTableColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                }
            }
            this.setReferenceTableColumnNames(oracleColumnVector);
        }
        if (this.referenceTable != null) {
            String ownerName = this.referenceTable.getOwner();
            String tableName = this.referenceTable.getTableName();
            String userName = this.referenceTable.getUser();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            this.referenceTable.setOwner(ownerName);
            this.referenceTable.setTableName(tableName);
            this.referenceTable.setUser(userName);
        }
        this.setMatch(null);
        if (this.getActionOnUpdate() != null) {
            this.setOnUpdate(null);
            this.setActionOnUpdate(null);
        }
        if (this.getActionOnDelete() != null && (this.getActionOnDelete().equalsIgnoreCase("SET DEFAULT") || this.getActionOnDelete().equalsIgnoreCase("RESTRICT") || this.getActionOnDelete().equalsIgnoreCase("NO ACTION") || this.getActionOnDelete().equalsIgnoreCase("SET NULL"))) {
            this.setOnDelete(null);
            this.setActionOnDelete(null);
        }
        if (this.getColumnName() != null && this.getConstraintName() != null) {
            this.setConstraintName(null);
            this.setConstraintColumnNames(null);
            this.setOpenBrace(null);
            this.setClosedBrace(null);
        }
    }
    
    @Override
    public void toTimesTenString() throws ConvertException {
        if (this.constraintColumnNames != null) {
            final Vector columnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    constraintColumn = CustomizeUtil.objectNamesToQuotedIdentifier(constraintColumn, SwisSQLUtils.getKeywords(10), null, 10);
                    if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        columnVector.add(constraintColumn);
                    }
                    else {
                        columnVector.add(constraintColumn);
                    }
                }
                else {
                    columnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(columnVector);
        }
        if (this.referenceTableColumnNames != null) {
            final Vector columnVector = new Vector();
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.referenceTableColumnNames.get(i);
                    constraintColumn = CustomizeUtil.objectNamesToQuotedIdentifier(constraintColumn, SwisSQLUtils.getKeywords(10), null, 10);
                    if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        columnVector.add(constraintColumn);
                    }
                    else {
                        columnVector.add(constraintColumn);
                    }
                }
                else {
                    columnVector.add(this.referenceTableColumnNames.get(i));
                }
            }
            this.setReferenceTableColumnNames(columnVector);
        }
        if (this.referenceTable != null) {
            String ownerName = this.referenceTable.getOwner();
            String tableName = this.referenceTable.getTableName();
            String userName = this.referenceTable.getUser();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(10), null, 10);
            if (userName != null) {
                userName = null;
            }
            this.referenceTable.setOwner(ownerName);
            this.referenceTable.setTableName(tableName);
            this.referenceTable.setUser(userName);
        }
        if (this.getActionOnUpdate() != null) {
            this.setOnUpdate(null);
            this.setActionOnUpdate(null);
        }
        if (this.getActionOnDelete() != null) {
            this.setOnDelete(null);
            this.setActionOnDelete(null);
        }
        this.setMatch(null);
    }
    
    @Override
    public void toNetezzaString() throws ConvertException {
        this.setMatch(null);
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.constraintColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
        if (this.referenceTableColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.referenceTableColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                }
            }
            this.setReferenceTableColumnNames(oracleColumnVector);
        }
        if (this.referenceTable != null) {
            String ownerName = this.referenceTable.getOwner();
            String tableName = this.referenceTable.getTableName();
            String userName = this.referenceTable.getUser();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            this.referenceTable.setOwner(ownerName);
            this.referenceTable.setTableName(tableName);
            this.referenceTable.setUser(userName);
        }
        if (this.getColumnName() != null && this.getConstraintName() != null) {
            this.setConstraintName(null);
            this.setConstraintColumnNames(null);
            this.setOpenBrace(null);
            this.setClosedBrace(null);
        }
    }
    
    @Override
    public void toTeradataString() throws ConvertException {
        this.setMatch(null);
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.constraintColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
        if (this.referenceTableColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.referenceTableColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                    }
                }
                else {
                    oracleColumnVector.add(this.referenceTableColumnNames.get(i));
                }
            }
            this.setReferenceTableColumnNames(oracleColumnVector);
        }
        if (this.referenceTable != null) {
            String ownerName = this.referenceTable.getOwner();
            String tableName = this.referenceTable.getTableName();
            String userName = this.referenceTable.getUser();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            this.referenceTable.setOwner(ownerName);
            this.referenceTable.setTableName(tableName);
            this.referenceTable.setUser(userName);
        }
        if (this.getColumnName() != null && this.getConstraintName() != null) {
            this.setConstraintName(null);
            this.setConstraintColumnNames(null);
            this.setOpenBrace(null);
            this.setClosedBrace(null);
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.constraintName != null) {
            sb.append(this.constraintName.toUpperCase() + " ");
        }
        if (this.openBrace != null) {
            sb.append(this.openBrace);
        }
        if (this.constraintColumnNames != null) {
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                final String col = this.constraintColumnNames.get(i).toString();
                if (i == 0) {
                    if (this.context != null) {
                        final String temp = this.context.getEquivalent(col).toString();
                        sb.append(temp);
                    }
                    else {
                        sb.append(col);
                    }
                }
                else if (this.context != null) {
                    final String temp = this.context.getEquivalent(col).toString();
                    sb.append(", " + temp);
                }
                else {
                    sb.append(", " + col);
                }
            }
        }
        if (this.closedBrace != null) {
            sb.append(this.closedBrace + " ");
        }
        if (this.reference != null) {
            sb.append(this.reference.toUpperCase() + " ");
        }
        if (this.referenceTable != null) {
            if (this.context != null) {
                this.referenceTable.setObjectContext(this.context);
            }
            sb.append(this.referenceTable.toString() + " ");
        }
        if (this.referenceOpenBrace != null) {
            sb.append(this.referenceOpenBrace);
        }
        if (this.referenceTableColumnNames != null) {
            for (int i = 0; i < this.referenceTableColumnNames.size(); ++i) {
                if (this.referenceTableColumnNames.get(i) instanceof String) {
                    String col = this.referenceTableColumnNames.get(i).toString();
                    if (this.referenceTable != null && this.context != null) {
                        final String s = this.referenceTable.getTableName() + "." + col;
                        final String sss = this.context.getEquivalent(s).toString();
                        if (!s.equals(sss)) {
                            col = sss;
                        }
                    }
                    if (i == 0) {
                        if (this.context != null) {
                            final String temp = this.context.getEquivalent(col).toString();
                            sb.append(temp);
                        }
                        else {
                            sb.append(col);
                        }
                    }
                    else if (this.context != null) {
                        final String temp = this.context.getEquivalent(col).toString();
                        sb.append(", " + temp);
                    }
                    else {
                        sb.append(", " + col);
                    }
                }
            }
        }
        if (this.referenceClosedBrace != null) {
            sb.append(this.referenceClosedBrace + " ");
        }
        if (this.matchLevel != null) {
            sb.append(this.matchLevel + " ");
        }
        if (this.onUpdate != null) {
            sb.append(this.onUpdate + " ");
        }
        if (this.actionOnUpdate != null) {
            sb.append(this.actionOnUpdate + " ");
        }
        if (this.onDelete != null) {
            sb.append(this.onDelete + " ");
        }
        if (this.actionOnDelete != null) {
            sb.append(this.actionOnDelete + " ");
        }
        return sb.toString();
    }
    
    public ConstraintType copyObjectValues() {
        final ForeignConstraintClause dupForeignConstraintClause = new ForeignConstraintClause();
        dupForeignConstraintClause.setClosedBrace(this.closedBrace);
        dupForeignConstraintClause.setConstraintColumnNames(this.getConstraintColumnNames());
        dupForeignConstraintClause.setConstraintName(this.getConstraintName());
        dupForeignConstraintClause.setOpenBrace(this.openBrace);
        dupForeignConstraintClause.setActionOnUpdate(this.getActionOnUpdate());
        dupForeignConstraintClause.setActionOnDelete(this.getActionOnDelete());
        dupForeignConstraintClause.setMatch(this.getMatch());
        dupForeignConstraintClause.setOnUpdate(this.getOnUpdate());
        dupForeignConstraintClause.setOnDelete(this.getOnDelete());
        dupForeignConstraintClause.setReference(this.getReference());
        dupForeignConstraintClause.setReferenceClosedBrace(this.referenceClosedBrace);
        dupForeignConstraintClause.setReferenceOpenBrace(this.referenceOpenBrace);
        dupForeignConstraintClause.setReferenceTableColumnNames(this.getReferenceTableColumnNames());
        dupForeignConstraintClause.setTableName(this.getTableName());
        dupForeignConstraintClause.setObjectContext(this.context);
        return dupForeignConstraintClause;
    }
}
