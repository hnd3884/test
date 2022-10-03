package com.adventnet.swissqlapi.sql.statement.create;

import java.util.Iterator;
import java.util.Set;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Map;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.HashMap;
import java.util.Vector;

public class PrimaryOrUniqueConstraintClause implements ConstraintType
{
    private Vector constraintColumnNames;
    private String constraintName;
    private String clusteredStatus;
    private String with;
    private HashMap diskAttr;
    private String openBrace;
    private String closedBrace;
    private String columnName;
    private String usingIndex;
    private String onString;
    private String onIndexOrIdentifier;
    private UserObjectContext context;
    private HashMap constrColSortClause;
    private String sortClause;
    private String tableNameFromCQS;
    private Map columnNameVsSize;
    
    public PrimaryOrUniqueConstraintClause() {
        this.context = null;
        this.columnNameVsSize = new HashMap();
    }
    
    public void addToColumnNameVsSize(String columnName, final String size) {
        if (columnName.startsWith("`")) {
            columnName = columnName.substring(1, columnName.length() - 1);
        }
        this.columnNameVsSize.put(columnName, size);
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
    
    public void setClustered(final String clusteredStatus) {
        this.clusteredStatus = clusteredStatus;
    }
    
    public void setWith(final String with) {
        this.with = with;
    }
    
    public void setDiskAttr(final HashMap diskAttr) {
        this.diskAttr = diskAttr;
    }
    
    public void setUsingIndex(final String usingIndex) {
        this.usingIndex = usingIndex;
    }
    
    public void setOpenBrace(final String openBrace) {
        this.openBrace = openBrace;
    }
    
    public void setClosedBrace(final String closedBrace) {
        this.closedBrace = closedBrace;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public void setOnString(final String onString) {
        this.onString = onString;
    }
    
    public void setOnIndexOrIdentifier(final String onIndexOrIdentifier) {
        this.onIndexOrIdentifier = onIndexOrIdentifier;
    }
    
    public void setConstrColumnSortClauseMap(final HashMap constrColSortClause) {
        this.constrColSortClause = constrColSortClause;
    }
    
    public void setSortClause(final String sortClause) {
        this.sortClause = sortClause;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public String getClustered() {
        return this.clusteredStatus;
    }
    
    public String getWith() {
        return this.with;
    }
    
    public HashMap getDiskAttr() {
        return this.diskAttr;
    }
    
    public String getUsingIndex() {
        return this.usingIndex;
    }
    
    public Vector getConstraintColumnNames() {
        return this.constraintColumnNames;
    }
    
    public String getConstraintName() {
        return this.constraintName;
    }
    
    public HashMap getConstrColumnSortClauseMap() {
        return this.constrColSortClause;
    }
    
    public String getSortClause() {
        return this.sortClause;
    }
    
    public void setTableNameFromCQS(final String tableNameFromCQS) {
        this.tableNameFromCQS = tableNameFromCQS;
    }
    
    @Override
    public void toDB2String() throws ConvertException {
        this.setClustered(null);
        this.setWith(null);
        this.setDiskAttr(null);
        this.setUsingIndex(null);
        this.setOnString(null);
        this.setOnIndexOrIdentifier(null);
        this.setConstrColumnSortClauseMap(null);
        this.setSortClause(null);
        if (this.getColumnName() != null) {
            this.setOpenBrace(null);
            this.setConstraintColumnNames(null);
            this.setClosedBrace(null);
        }
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    String strConst = this.getConstraintName();
                    if (strConst.toUpperCase().indexOf("UNIQUE KEY") != -1) {
                        strConst = "UNIQUE ";
                        this.setConstraintName(strConst);
                    }
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
    }
    
    @Override
    public void toMSSQLServerString() throws ConvertException {
        if (this.onString != null) {
            this.setUsingIndex(this.onString + " " + this.onIndexOrIdentifier);
        }
        this.setConstrColumnSortClauseMap(null);
        this.setSortClause(null);
        if (this.constraintColumnNames != null) {
            String columnsString = "";
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                String strConst = this.getConstraintName();
                if (strConst.toUpperCase().indexOf("UNIQUE KEY") != -1) {
                    strConst = "UNIQUE";
                    this.setConstraintName(strConst);
                }
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    String[] keywords = null;
                    if (SwisSQLUtils.getKeywords(2) != null) {
                        keywords = SwisSQLUtils.getKeywords(2);
                        if (constraintColumn.trim().length() > 0) {
                            constraintColumn = CustomizeUtil.objectNamesToBracedIdentifier(constraintColumn, keywords, null);
                        }
                    }
                    if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else if (constraintColumn.trim().startsWith("[") && constraintColumn.trim().endsWith("]")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                    }
                    else if (this.usingIndex != null && strConst != null && strConst.toUpperCase().indexOf("UNIQUE") == -1) {
                        if (i == this.constraintColumnNames.size() - 1) {
                            columnsString += constraintColumn;
                            this.setUsingIndex(null);
                            oracleColumnVector.add("CLUSTERED(" + columnsString + ")");
                        }
                        else {
                            columnsString = columnsString + constraintColumn + ",";
                        }
                        this.setClosedBrace(null);
                        this.setOpenBrace(null);
                    }
                    else {
                        oracleColumnVector.add(constraintColumn);
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            if (this.usingIndex != null) {
                this.setUsingIndex(null);
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
    }
    
    @Override
    public void toSybaseString() throws ConvertException {
        this.setUsingIndex(null);
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            if (this.columnNameVsSize != null && !this.columnNameVsSize.isEmpty()) {
                this.columnNameVsSize = new HashMap();
            }
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                String strConst = this.getConstraintName();
                if (strConst.toUpperCase().indexOf("UNIQUE KEY") != -1) {
                    strConst = "UNIQUE";
                    this.setConstraintName(strConst);
                }
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                        final String tempValue = this.constrColSortClause.get(constraintColumn);
                        this.constrColSortClause.remove(constraintColumn);
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        oracleColumnVector.add(constraintColumn);
                        this.constrColSortClause.put(constraintColumn, tempValue);
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
    }
    
    @Override
    public void toOracleString() throws ConvertException {
        if (this.getColumnName() != null) {
            this.setOpenBrace(null);
            this.setConstraintColumnNames(null);
            this.setClosedBrace(null);
        }
        this.setConstrColumnSortClauseMap(null);
        this.setSortClause(null);
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                String strConst = this.getConstraintName();
                if (strConst.toUpperCase().indexOf("UNIQUE KEY") != -1) {
                    strConst = "UNIQUE";
                    this.setConstraintName(strConst);
                }
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
        this.setUsingIndex(this.usingIndex);
        if (this.onString != null) {
            String indexString = this.onIndexOrIdentifier;
            if ((indexString.startsWith("[") && indexString.endsWith("]")) || (indexString.startsWith("`") && indexString.endsWith("`"))) {
                indexString = indexString.substring(1, indexString.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || indexString.indexOf(32) != -1) {
                    indexString = "\"" + indexString + "\"";
                }
            }
            if (!indexString.equalsIgnoreCase("primary")) {
                this.setUsingIndex("USING INDEX TABLESPACE " + indexString);
            }
            this.setOnString(null);
            this.setOnIndexOrIdentifier(null);
        }
    }
    
    @Override
    public void toPostgreSQLString() throws ConvertException {
        this.setClustered(null);
        this.setWith(null);
        this.setDiskAttr(null);
        this.setUsingIndex(null);
        this.setConstrColumnSortClauseMap(null);
        this.setSortClause(null);
        if (this.getColumnName() != null) {
            this.setOpenBrace(null);
            this.setConstraintColumnNames(null);
            this.setClosedBrace(null);
        }
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
    }
    
    @Override
    public void toANSIString() throws ConvertException {
        this.setClustered(null);
        this.setWith(null);
        this.setDiskAttr(null);
        this.setUsingIndex(null);
        this.setOnString(null);
        this.setOnIndexOrIdentifier(null);
        this.setConstrColumnSortClauseMap(null);
        this.setSortClause(null);
        if (this.getColumnName() != null) {
            this.setOpenBrace(null);
            this.setConstraintColumnNames(null);
            this.setClosedBrace(null);
        }
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
    }
    
    @Override
    public void toTeradataString() throws ConvertException {
        this.setClustered(null);
        this.setWith(null);
        this.setDiskAttr(null);
        this.setUsingIndex(null);
        this.setOnString(null);
        this.setOnIndexOrIdentifier(null);
        this.setConstrColumnSortClauseMap(null);
        this.setSortClause(null);
        if (this.getColumnName() != null) {
            this.setOpenBrace(null);
            this.setConstraintColumnNames(null);
            this.setClosedBrace(null);
        }
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
    }
    
    @Override
    public void toMySQLString() throws ConvertException {
        this.setClustered(null);
        this.setWith(null);
        this.setDiskAttr(null);
        this.setUsingIndex(null);
        this.setOnString(null);
        this.setOnIndexOrIdentifier(null);
        this.setConstrColumnSortClauseMap(null);
        this.setSortClause(null);
        if (this.getColumnName() != null && this.constraintColumnNames != null && this.constraintColumnNames.contains(this.getColumnName())) {
            this.setOpenBrace(null);
            this.setConstraintColumnNames(null);
            this.setClosedBrace(null);
        }
        if (this.constraintColumnNames != null) {
            final Vector oracleColumnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if ((constraintColumn.startsWith("[") && constraintColumn.endsWith("]")) || (constraintColumn.startsWith("\"") && constraintColumn.endsWith("\""))) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        constraintColumn = "`" + constraintColumn + "`";
                        oracleColumnVector.add(constraintColumn);
                    }
                    else if (constraintColumn.startsWith("`") || constraintColumn.endsWith("`")) {
                        oracleColumnVector.add(constraintColumn);
                    }
                    else {
                        constraintColumn = "`" + constraintColumn + "`";
                        oracleColumnVector.add(constraintColumn);
                    }
                }
                else {
                    oracleColumnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(oracleColumnVector);
        }
    }
    
    @Override
    public void toInformixString() throws ConvertException {
        this.setClustered(null);
        this.setWith(null);
        this.setDiskAttr(null);
        this.setUsingIndex(null);
        this.setOnString(null);
        this.setOnIndexOrIdentifier(null);
        this.setConstrColumnSortClauseMap(null);
        this.setSortClause(null);
        if (this.getColumnName() != null) {
            this.setOpenBrace(null);
            this.setConstraintColumnNames(null);
            this.setClosedBrace(null);
        }
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
    }
    
    @Override
    public void toTimesTenString() throws ConvertException {
        this.setUsingIndex(null);
        this.setConstrColumnSortClauseMap(null);
        this.setSortClause(null);
        this.setWith(null);
        this.setDiskAttr(null);
        this.setClustered(null);
        if (this.constraintColumnNames != null) {
            final Vector columnVector = new Vector();
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                String strConst = this.getConstraintName();
                if (strConst.toUpperCase().indexOf("UNIQUE KEY") != -1) {
                    strConst = "UNIQUE";
                    this.setConstraintName(strConst);
                }
                if (this.constraintColumnNames.elementAt(i) instanceof String) {
                    String constraintColumn = this.constraintColumnNames.get(i);
                    if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                        constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                        if (constraintColumn.indexOf(32) != -1) {
                            constraintColumn = "\"" + constraintColumn + "\"";
                        }
                        columnVector.add(constraintColumn);
                    }
                    else {
                        columnVector.add(this.constraintColumnNames.get(i));
                    }
                }
                else {
                    columnVector.add(this.constraintColumnNames.get(i));
                }
            }
            this.setConstraintColumnNames(columnVector);
        }
    }
    
    @Override
    public void toNetezzaString() throws ConvertException {
        this.setClustered(null);
        this.setWith(null);
        this.setDiskAttr(null);
        this.setUsingIndex(null);
        this.setOnString(null);
        this.setOnIndexOrIdentifier(null);
        this.setConstrColumnSortClauseMap(null);
        this.setSortClause(null);
        if (this.getColumnName() != null) {
            this.setOpenBrace(null);
            this.setConstraintColumnNames(null);
            this.setClosedBrace(null);
        }
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
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.constraintName != null) {
            sb.append(this.constraintName.toUpperCase() + " ");
        }
        if (this.clusteredStatus != null) {
            sb.append(this.clusteredStatus + " ");
        }
        if (this.sortClause != null) {
            sb.append(this.sortClause.toUpperCase() + " ");
        }
        if (this.openBrace != null) {
            sb.append(this.openBrace);
        }
        if (this.constraintColumnNames != null) {
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                String col = this.constraintColumnNames.get(i).toString();
                if (this.context != null) {
                    final String s = col;
                    final String sss = this.context.getEquivalent(s).toString();
                    if (!s.equals(sss)) {
                        col = sss;
                    }
                }
                String modifiedCol = col;
                if (i == 0) {
                    if (this.context != null) {
                        final String temp = this.context.getEquivalent(col).toString();
                        sb.append(temp);
                    }
                    else {
                        sb.append(col);
                        if (col.startsWith("`")) {
                            modifiedCol = col.substring(1, col.length() - 1);
                        }
                        final String sizeStr = this.columnNameVsSize.get(modifiedCol);
                        if (sizeStr != null) {
                            sb.append("(");
                            sb.append(sizeStr);
                            sb.append(")");
                        }
                    }
                }
                else if (this.context != null) {
                    final String temp = this.context.getEquivalent(col).toString();
                    sb.append(", " + temp);
                }
                else {
                    sb.append(", " + col);
                    if (modifiedCol.startsWith("`")) {
                        modifiedCol = modifiedCol.substring(1, modifiedCol.length() - 1);
                    }
                    final String sizeStr = this.columnNameVsSize.get(modifiedCol);
                    if (sizeStr != null) {
                        sb.append("(");
                        sb.append(sizeStr);
                        sb.append(")");
                    }
                }
                if (this.constrColSortClause != null && this.constrColSortClause.get(col) != null) {
                    sb.append(" " + this.constrColSortClause.get(col).toUpperCase());
                }
            }
        }
        if (this.closedBrace != null) {
            sb.append(this.closedBrace + " ");
        }
        if (this.with != null) {
            sb.append(this.with.toUpperCase() + " ");
        }
        if (this.diskAttr != null && this.diskAttr.size() > 0) {
            final Set keys = this.diskAttr.keySet();
            final Iterator it = keys.iterator();
            boolean start = true;
            while (it.hasNext()) {
                if (!start) {
                    sb.append(", ");
                }
                final Object obj = it.next();
                sb.append(obj.toString().toUpperCase() + " = " + this.diskAttr.get(obj));
                start = false;
            }
            sb.append(" ");
        }
        if (this.usingIndex != null) {
            sb.append(this.usingIndex + " ");
        }
        return sb.toString();
    }
    
    public ConstraintType copyObjectValues() {
        final PrimaryOrUniqueConstraintClause dupPrimaryOrUniqueConstraintClause = new PrimaryOrUniqueConstraintClause();
        dupPrimaryOrUniqueConstraintClause.setClosedBrace(this.closedBrace);
        dupPrimaryOrUniqueConstraintClause.setConstraintColumnNames(this.getConstraintColumnNames());
        dupPrimaryOrUniqueConstraintClause.setConstraintName(this.getConstraintName());
        dupPrimaryOrUniqueConstraintClause.setOpenBrace(this.openBrace);
        dupPrimaryOrUniqueConstraintClause.setConstrColumnSortClauseMap(this.constrColSortClause);
        dupPrimaryOrUniqueConstraintClause.setSortClause(this.sortClause);
        dupPrimaryOrUniqueConstraintClause.setClustered(this.getClustered());
        dupPrimaryOrUniqueConstraintClause.setWith(this.getWith());
        dupPrimaryOrUniqueConstraintClause.setDiskAttr(this.getDiskAttr());
        dupPrimaryOrUniqueConstraintClause.setUsingIndex(this.getUsingIndex());
        dupPrimaryOrUniqueConstraintClause.setOnString(this.onString);
        dupPrimaryOrUniqueConstraintClause.setOnIndexOrIdentifier(this.onIndexOrIdentifier);
        dupPrimaryOrUniqueConstraintClause.setObjectContext(this.context);
        if (this.columnNameVsSize != null) {
            final Iterator it = this.columnNameVsSize.keySet().iterator();
            while (it.hasNext()) {
                final String colName = it.next().toString();
                final String colSize = this.columnNameVsSize.get(colName).toString();
                dupPrimaryOrUniqueConstraintClause.addToColumnNameVsSize(colName, colSize);
            }
        }
        return dupPrimaryOrUniqueConstraintClause;
    }
}
