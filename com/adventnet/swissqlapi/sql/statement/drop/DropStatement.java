package com.adventnet.swissqlapi.sql.statement.drop;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class DropStatement implements SwisSQLStatement
{
    private String drop;
    private String tableOrSequence;
    private String ifExists;
    private Vector tableObjectVector;
    private String restrictOrCascade;
    private String constraints;
    private UserObjectContext objectContext;
    private String multipleQuery;
    private String materializedObject;
    private CommentClass commentObject;
    
    public DropStatement() {
        this.objectContext = null;
    }
    
    public void setDrop(final String drop) {
        this.drop = drop;
    }
    
    public void setTableOrSequence(final String tableOrSequence) {
        this.tableOrSequence = tableOrSequence;
    }
    
    public void setIfExists(final String ifExists) {
        this.ifExists = ifExists;
    }
    
    public void setTableNameVector(final Vector tableObjectVector) {
        this.tableObjectVector = tableObjectVector;
    }
    
    public void setRestrictOrCascade(final String restrictOrCascade) {
        this.restrictOrCascade = restrictOrCascade;
    }
    
    public void setConstraints(final String constraints) {
        this.constraints = constraints;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentObject) {
        this.commentObject = commentObject;
    }
    
    public void setMultipleQuery(final String multipleQuery) {
        this.multipleQuery = multipleQuery;
    }
    
    public void setMaterializedView(final String materialized) {
        this.materializedObject = materialized;
    }
    
    public String getMaterializedView() {
        return this.materializedObject;
    }
    
    public String getTableOrSequence() {
        return this.tableOrSequence;
    }
    
    public String getConstraints() {
        return this.constraints;
    }
    
    public Vector getTableObjectVector() {
        return this.tableObjectVector;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return this.commentObject;
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        final DropStatement dropStatement = this.copyObjectValues();
        if (dropStatement.getTableObjectVector() != null) {
            final Vector tableObjVector = dropStatement.getTableObjectVector();
            for (int i = 0; i < tableObjVector.size(); ++i) {
                final TableObject tableObj = tableObjVector.get(i);
                tableObj.toANSISQL();
            }
        }
        dropStatement.setIfExists(null);
        dropStatement.setConstraints(null);
        return dropStatement.toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        final DropStatement dropStatement = this.copyObjectValues();
        if (dropStatement.commentObject != null) {
            dropStatement.commentObject.setSQLDialect(12);
        }
        if (dropStatement.getTableObjectVector() != null) {
            final Vector tableObjVector = dropStatement.getTableObjectVector();
            for (int i = 0; i < tableObjVector.size(); ++i) {
                final TableObject tableObj = tableObjVector.get(i);
                tableObj.toTeradata();
            }
        }
        dropStatement.setIfExists(null);
        dropStatement.setConstraints(null);
        return dropStatement.toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        final DropStatement dropStatement = this.copyObjectValues();
        if (dropStatement.getTableObjectVector() != null) {
            final Vector tableObjVector = dropStatement.getTableObjectVector();
            for (int i = 0; i < tableObjVector.size(); ++i) {
                final TableObject tableObj = tableObjVector.get(i);
                tableObj.toDB2();
            }
        }
        dropStatement.setIfExists(null);
        dropStatement.setRestrictOrCascade(null);
        dropStatement.setConstraints(null);
        return dropStatement.toString();
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        final DropStatement dropStatement = this.copyObjectValues();
        if (dropStatement.getTableObjectVector() != null) {
            final Vector tableObjVector = dropStatement.getTableObjectVector();
            for (int i = 0; i < tableObjVector.size(); ++i) {
                final TableObject tableObj = tableObjVector.get(i);
                tableObj.toInformix();
            }
        }
        dropStatement.setIfExists(null);
        dropStatement.setConstraints(null);
        return dropStatement.toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        final DropStatement dropStatement = this.copyObjectValues();
        if (dropStatement.getTableObjectVector() != null) {
            final Vector tableObjVector = dropStatement.getTableObjectVector();
            for (int i = 0; i < tableObjVector.size(); ++i) {
                final TableObject tableObj = tableObjVector.get(i);
                if (this.tableOrSequence.equalsIgnoreCase("INDEX")) {
                    tableObj.setTableType("INDEX");
                    if (tableObj.getUser() != null && SwisSQLOptions.fromSybase) {
                        tableObj.setOwner(tableObj.getUser());
                        tableObj.setUser(null);
                    }
                }
                tableObj.toMSSQLServer();
            }
        }
        dropStatement.setRestrictOrCascade(null);
        dropStatement.setIfExists(null);
        dropStatement.setConstraints(null);
        return dropStatement.toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        final DropStatement dropStatement = this.copyObjectValues();
        if (dropStatement.getTableObjectVector() != null) {
            final Vector tableObjVector = dropStatement.getTableObjectVector();
            for (int i = 0; i < tableObjVector.size(); ++i) {
                final TableObject tableObj = tableObjVector.get(i);
                tableObj.toMySQL();
            }
        }
        dropStatement.setRestrictOrCascade(null);
        dropStatement.setConstraints(null);
        return dropStatement.toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        final DropStatement dropStatement = this.copyObjectValues();
        if (dropStatement.getTableObjectVector() != null) {
            final Vector tableObjVector = dropStatement.getTableObjectVector();
            for (int i = 0; i < tableObjVector.size(); ++i) {
                final TableObject tableObj = tableObjVector.get(i);
                tableObj.toOracle();
                final String tableOrIndex = dropStatement.getTableOrSequence();
                if (tableOrIndex != null && tableOrIndex.equalsIgnoreCase("index")) {
                    tableObj.setUser(null);
                }
                String tableName = tableObj.getTableName();
                if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                    tableName = tableName.substring(1, tableName.length() - 1);
                    if (SwisSQLOptions.retainQuotedIdentifierForOracle || tableName.indexOf(" ") != -1) {
                        tableName = "\"" + tableName + "\"";
                    }
                    tableObj.setTableName(tableName);
                }
            }
        }
        dropStatement.setIfExists(null);
        return dropStatement.toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        final DropStatement dropStatement = this.copyObjectValues();
        if (dropStatement.getTableObjectVector() != null) {
            final Vector tableObjVector = dropStatement.getTableObjectVector();
            for (int i = 0; i < tableObjVector.size(); ++i) {
                final TableObject tableObj = tableObjVector.get(i);
                tableObj.toPostgreSQL();
            }
        }
        dropStatement.setRestrictOrCascade(null);
        dropStatement.setIfExists(null);
        dropStatement.setConstraints(null);
        return dropStatement.toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        final DropStatement dropStatement = this.copyObjectValues();
        if (dropStatement.getTableObjectVector() != null) {
            final Vector tableObjVector = dropStatement.getTableObjectVector();
            for (int i = 0; i < tableObjVector.size(); ++i) {
                final TableObject tableObj = tableObjVector.get(i);
                tableObj.toSybase();
            }
        }
        dropStatement.setRestrictOrCascade(null);
        dropStatement.setIfExists(null);
        dropStatement.setConstraints(null);
        return dropStatement.toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        final DropStatement dropStatement = this.copyObjectValues();
        if (dropStatement.getTableObjectVector() != null) {
            final Vector tableObjVector = dropStatement.getTableObjectVector();
            this.multipleQuery = "";
            for (int i = 0; i < tableObjVector.size(); ++i) {
                final TableObject tableObj = tableObjVector.get(i);
                tableObj.toTimesTen();
                if (i > 0) {
                    tableObjVector.remove(i);
                    --i;
                    this.multipleQuery = this.multipleQuery + "DROP " + this.tableOrSequence + " " + tableObj.toString().trim() + ";\n\n";
                }
            }
        }
        if (!this.multipleQuery.equalsIgnoreCase("")) {
            dropStatement.setMultipleQuery(this.multipleQuery);
        }
        dropStatement.setIfExists(null);
        dropStatement.setRestrictOrCascade(null);
        dropStatement.setConstraints(null);
        return dropStatement.toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        final DropStatement dropStatement = this.copyObjectValues();
        if (dropStatement.getTableObjectVector() != null) {
            final Vector tableObjVector = dropStatement.getTableObjectVector();
            for (int i = 0; i < tableObjVector.size(); ++i) {
                final TableObject tableObj = tableObjVector.get(i);
                tableObj.toNetezza();
            }
        }
        dropStatement.setIfExists(null);
        dropStatement.setConstraints(null);
        dropStatement.setRestrictOrCascade(null);
        return dropStatement.toString();
    }
    
    public DropStatement copyObjectValues() {
        final DropStatement dropStmt = new DropStatement();
        dropStmt.setConstraints(this.constraints);
        dropStmt.setCommentClass(this.commentObject);
        dropStmt.setDrop(this.drop);
        dropStmt.setIfExists(this.ifExists);
        dropStmt.setRestrictOrCascade(this.restrictOrCascade);
        dropStmt.setTableOrSequence(this.tableOrSequence);
        dropStmt.setTableNameVector(this.tableObjectVector);
        dropStmt.setObjectContext(this.objectContext);
        dropStmt.setMaterializedView(this.materializedObject);
        return dropStmt;
    }
    
    @Override
    public String removeIndent(String str) {
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        return str;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObject != null) {
            sb.append(this.commentObject.toString() + "\n");
        }
        if (this.drop != null) {
            sb.append(this.drop.toUpperCase());
        }
        if (this.materializedObject != null) {
            sb.append(" " + this.getMaterializedView() + " ");
        }
        if (this.tableOrSequence != null) {
            sb.append(" " + this.tableOrSequence.toUpperCase());
        }
        if (this.ifExists != null) {
            sb.append("  " + this.ifExists.toUpperCase());
        }
        if (this.tableObjectVector != null) {
            for (int i = 0; i < this.tableObjectVector.size(); ++i) {
                final TableObject tableObject = this.tableObjectVector.get(i);
                tableObject.setObjectContext(this.objectContext);
                if (i == 0) {
                    sb.append(" " + tableObject);
                }
                else {
                    sb.append(",\n\t" + tableObject);
                }
            }
        }
        if (this.restrictOrCascade != null) {
            sb.append("\n\t" + this.restrictOrCascade.toUpperCase());
        }
        if (this.constraints != null) {
            sb.append(" " + this.constraints.toUpperCase());
        }
        if (this.multipleQuery != null) {
            final StringBuffer sb2 = new StringBuffer();
            sb2.append(sb.toString().trim() + ";\n\n" + this.multipleQuery);
            return sb2.toString();
        }
        return sb.toString();
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return this.objectContext;
    }
    
    @Override
    public void setObjectContext(final UserObjectContext obj) {
        this.objectContext = obj;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
