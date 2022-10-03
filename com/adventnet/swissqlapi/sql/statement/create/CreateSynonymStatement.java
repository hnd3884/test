package com.adventnet.swissqlapi.sql.statement.create;

import java.util.Vector;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.drop.DropStatement;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class CreateSynonymStatement implements SwisSQLStatement
{
    private String createOrReplace;
    private String createString;
    private String synonymString;
    private String publicString;
    private String synonymName;
    private String forString;
    private TableObject tableName;
    private String schemaName;
    private UserObjectContext context;
    private String comment;
    DropStatement dropSynonym;
    
    public CreateSynonymStatement() {
        this.context = null;
    }
    
    public void setCreateOrReplace(final String createOrReplace) {
        this.createOrReplace = createOrReplace;
    }
    
    public void setCreate(final String createString) {
        this.createString = createString;
    }
    
    public void setSynonym(final String synonymString) {
        this.synonymString = synonymString;
    }
    
    public void setPublic(final String publicString) {
        this.publicString = publicString;
    }
    
    public void setSynonymName(final String synonymName) {
        this.synonymName = synonymName;
    }
    
    public void setFor(final String forString) {
        this.forString = forString;
    }
    
    public void setTableName(final TableObject tableName) {
        this.tableName = tableName;
    }
    
    public void setSchemaName(final String schemaName) {
        this.schemaName = schemaName;
    }
    
    public void setDropSynonym(final DropStatement dropSynonym) {
        this.dropSynonym = dropSynonym;
    }
    
    public String getCreate() {
        return this.createString;
    }
    
    public String getCreateOrReplace() {
        return this.createOrReplace;
    }
    
    public String getSynonym() {
        return this.synonymString;
    }
    
    public String getPublic() {
        return this.publicString;
    }
    
    public String getSynonymName() {
        return this.synonymName;
    }
    
    public String getFor() {
        return this.forString;
    }
    
    public TableObject getTableName(final TableObject tableName) {
        return this.tableName;
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public DropStatement getDropSynonym() {
        return this.dropSynonym;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return null;
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return null;
    }
    
    @Override
    public String removeIndent(final String str) {
        return str;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentClass) {
    }
    
    @Override
    public void setObjectContext(final UserObjectContext userObjectContext) {
        this.context = userObjectContext;
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        final CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
        if (this.createString != null) {
            createSynonymStatement.setCreate(this.createString);
        }
        if (this.createOrReplace != null) {
            createSynonymStatement.setCreateOrReplace(this.createOrReplace);
        }
        if (this.publicString != null) {
            createSynonymStatement.setPublic(null);
        }
        if (this.synonymString != null) {
            createSynonymStatement.setSynonym(this.synonymString);
        }
        if (this.synonymName != null) {
            createSynonymStatement.setSynonymName(this.synonymName);
        }
        if (this.forString != null) {
            createSynonymStatement.setFor(this.forString);
        }
        if (this.tableName != null) {
            createSynonymStatement.setTableName(this.tableName);
        }
        return createSynonymStatement.toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        final CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
        if (this.createString != null) {
            createSynonymStatement.setCreate(this.createString);
        }
        if (this.createOrReplace != null) {
            createSynonymStatement.setCreateOrReplace(this.createOrReplace);
        }
        if (this.publicString != null) {
            createSynonymStatement.setPublic(null);
        }
        if (this.synonymString != null) {
            createSynonymStatement.setSynonym(this.synonymString);
        }
        if (this.synonymName != null) {
            createSynonymStatement.setSynonymName(this.synonymName);
        }
        if (this.forString != null) {
            createSynonymStatement.setFor(this.forString);
        }
        if (this.tableName != null) {
            createSynonymStatement.setTableName(this.tableName);
        }
        return createSynonymStatement.toString();
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        final CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
        if (this.createString != null) {
            createSynonymStatement.setCreate(this.createString);
        }
        if (this.createOrReplace != null) {
            createSynonymStatement.setCreateOrReplace(this.createOrReplace);
        }
        if (this.publicString != null) {
            createSynonymStatement.setPublic(null);
        }
        if (this.synonymString != null) {
            createSynonymStatement.setSynonym(this.synonymString);
        }
        if (this.synonymName != null) {
            createSynonymStatement.setSynonymName(this.synonymName);
        }
        if (this.forString != null) {
            createSynonymStatement.setFor(this.forString);
        }
        if (this.tableName != null) {
            createSynonymStatement.setTableName(this.tableName);
        }
        return createSynonymStatement.toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        final CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
        if (this.createString != null) {
            createSynonymStatement.setCreate("/* Synonym does not exists in SQL Server -- " + this.createString);
        }
        if (this.createOrReplace != null) {
            createSynonymStatement.setCreateOrReplace(this.createOrReplace);
        }
        if (this.publicString != null) {
            createSynonymStatement.setPublic(null);
        }
        if (this.synonymString != null) {
            createSynonymStatement.setSynonym(this.synonymString);
        }
        if (this.synonymName != null) {
            createSynonymStatement.setSynonymName(this.synonymName);
        }
        if (this.forString != null) {
            createSynonymStatement.setFor(this.forString);
        }
        if (this.tableName != null) {
            createSynonymStatement.setTableName(this.tableName);
        }
        createSynonymStatement.comment = " */";
        return createSynonymStatement.toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        final CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
        if (this.createString != null) {
            createSynonymStatement.setCreate(this.createString);
        }
        if (this.createOrReplace != null) {
            createSynonymStatement.setCreateOrReplace(this.createOrReplace);
        }
        if (this.publicString != null) {
            createSynonymStatement.setPublic(null);
        }
        if (this.synonymString != null) {
            createSynonymStatement.setSynonym(this.synonymString);
        }
        if (this.synonymName != null) {
            createSynonymStatement.setSynonymName(this.synonymName);
        }
        if (this.forString != null) {
            createSynonymStatement.setFor(this.forString);
        }
        if (this.tableName != null) {
            createSynonymStatement.setTableName(this.tableName);
        }
        return createSynonymStatement.toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        final CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
        if (this.createString != null) {
            createSynonymStatement.setCreate(this.createString);
        }
        if (this.createOrReplace != null) {
            createSynonymStatement.setCreateOrReplace(this.createOrReplace);
        }
        if (this.publicString != null) {
            createSynonymStatement.setPublic(this.publicString);
        }
        if (this.synonymString != null) {
            createSynonymStatement.setSynonym(this.synonymString);
        }
        if (this.synonymName != null) {
            createSynonymStatement.setSynonymName(this.synonymName);
        }
        if (this.forString != null) {
            createSynonymStatement.setFor(this.forString);
        }
        if (this.tableName != null) {
            createSynonymStatement.setTableName(this.tableName);
        }
        return createSynonymStatement.toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        final CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
        if (this.createString != null) {
            createSynonymStatement.setCreate(this.createString);
        }
        if (this.createOrReplace != null) {
            createSynonymStatement.setCreateOrReplace(this.createOrReplace);
        }
        if (this.publicString != null) {
            createSynonymStatement.setPublic(null);
        }
        if (this.synonymString != null) {
            createSynonymStatement.setSynonym(this.synonymString);
        }
        if (this.synonymName != null) {
            createSynonymStatement.setSynonymName(this.synonymName);
        }
        if (this.forString != null) {
            createSynonymStatement.setFor(this.forString);
        }
        if (this.tableName != null) {
            createSynonymStatement.setTableName(this.tableName);
        }
        return createSynonymStatement.toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        final CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
        createSynonymStatement.setObjectContext(this.context);
        if (this.createString != null) {
            createSynonymStatement.setCreate(this.createString);
        }
        if (this.createOrReplace != null) {
            createSynonymStatement.setCreateOrReplace(this.createOrReplace);
        }
        if (this.publicString != null) {
            createSynonymStatement.setPublic(null);
        }
        if (this.synonymString != null) {
            createSynonymStatement.setSynonym(this.synonymString);
        }
        if (this.synonymName != null) {
            createSynonymStatement.setSynonymName(this.synonymName);
        }
        if (this.forString != null) {
            createSynonymStatement.setFor(this.forString);
        }
        if (this.tableName != null) {
            createSynonymStatement.setTableName(this.tableName);
        }
        return createSynonymStatement.toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        throw new ConvertException("\nUnsupported SQL\n");
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        final CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
        if (this.createString != null) {
            createSynonymStatement.setCreate(this.createString);
        }
        if (this.publicString != null) {
            createSynonymStatement.setPublic(null);
        }
        if (this.synonymString != null) {
            createSynonymStatement.setSynonym(this.synonymString);
        }
        if (this.synonymName != null) {
            createSynonymStatement.setSynonymName(this.synonymName);
        }
        if (this.forString != null) {
            createSynonymStatement.setFor(this.forString);
        }
        if (this.tableName != null) {
            createSynonymStatement.setTableName(this.tableName);
        }
        if (this.createOrReplace != null) {
            createSynonymStatement.setCreateOrReplace(null);
            (this.dropSynonym = new DropStatement()).setTableOrSequence("SYNONYM");
            final Vector tableNameVector = new Vector();
            tableNameVector.add(this.tableName);
            this.dropSynonym.setTableNameVector(tableNameVector);
            this.dropSynonym.setDrop("DROP");
            createSynonymStatement.setDropSynonym(this.dropSynonym);
        }
        return createSynonymStatement.toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        final CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
        if (this.createString != null) {
            createSynonymStatement.setCreate(this.createString);
        }
        if (this.createOrReplace != null) {
            createSynonymStatement.setCreateOrReplace(this.createOrReplace);
        }
        if (this.publicString != null) {
            createSynonymStatement.setPublic(null);
        }
        if (this.synonymString != null) {
            createSynonymStatement.setSynonym(this.synonymString);
        }
        if (this.synonymName != null) {
            createSynonymStatement.setSynonymName(this.synonymName);
        }
        if (this.forString != null) {
            createSynonymStatement.setFor(this.forString);
        }
        if (this.tableName != null) {
            createSynonymStatement.setTableName(this.tableName);
        }
        return createSynonymStatement.toString();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String indentString = "\n";
        if (this.dropSynonym != null) {
            sb.append(indentString + this.dropSynonym.toString() + ";" + indentString);
        }
        if (this.createString != null) {
            sb.append(indentString + this.createString.toUpperCase());
        }
        if (this.createOrReplace != null) {
            sb.append(indentString + this.createOrReplace.toUpperCase());
        }
        if (this.publicString != null) {
            sb.append(" " + this.publicString.toUpperCase());
        }
        if (this.synonymString != null) {
            sb.append(" " + this.synonymString.toUpperCase());
        }
        if (this.synonymName != null) {
            if (this.context != null) {
                final String temp = this.context.getEquivalent(this.synonymName).toString();
                sb.append(" " + temp);
            }
            else {
                sb.append(" " + this.synonymName);
            }
        }
        if (this.forString != null) {
            sb.append(" " + this.forString.toUpperCase());
        }
        if (this.tableName != null) {
            this.tableName.setObjectContext(this.context);
            sb.append(" " + this.tableName.toString());
        }
        if (this.comment != null) {
            sb.append(" " + this.comment);
        }
        return sb.toString();
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
