package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class ImportStatement implements SwisSQLStatement
{
    private String loadData;
    private String fileName;
    private String intoTable;
    private String tableName;
    private String fieldsTerminated;
    private String delimiter;
    private String bulkInsert;
    private String with;
    private String from;
    private String messages;
    private String messageFile;
    private String insertInto;
    private String type;
    
    public void setLoadData(final String loadData) {
        this.loadData = loadData;
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    public void setIntoTable(final String intoTable) {
        this.intoTable = intoTable;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public void setFieldsTerminated(final String fieldsTerminated) {
        this.fieldsTerminated = fieldsTerminated;
    }
    
    public void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }
    
    public void setBulkInsert(final String bulkInsert) {
        this.bulkInsert = bulkInsert;
    }
    
    public void setWith(final String with) {
        this.with = with;
    }
    
    public void setFrom(final String from) {
        this.from = from;
    }
    
    public void setMessages(final String messages) {
        this.messages = messages;
    }
    
    public void setMessageFile(final String messageFile) {
        this.messageFile = messageFile;
    }
    
    public void setInsertInto(final String insertInto) {
        this.insertInto = insertInto;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getLoadData() {
        return this.loadData;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public String getIntoTable() {
        return this.intoTable;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getFieldsTerminated() {
        return this.fieldsTerminated;
    }
    
    public String setDelimiter() {
        return this.delimiter;
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
    }
    
    public ImportStatement toANSIImport() throws ConvertException {
        throw new ConvertException("Query yet to be supported.");
    }
    
    public ImportStatement toTeradataImport() throws ConvertException {
        throw new ConvertException("Query yet to be supported.");
    }
    
    public ImportStatement toDB2Import() throws ConvertException {
        final ImportStatement importStmt = new ImportStatement();
        if (this.loadData != null) {
            importStmt.setBulkInsert("IMPORT FROM");
            importStmt.setLoadData(null);
        }
        if (this.fileName != null) {
            importStmt.setFileName(this.fileName);
        }
        if (this.intoTable != null) {
            importStmt.setIntoTable(null);
        }
        if (this.tableName != null) {
            importStmt.setTableName(this.tableName);
        }
        if (this.fieldsTerminated != null) {
            importStmt.setFieldsTerminated(null);
        }
        if (this.delimiter != null) {
            importStmt.setDelimiter(null);
        }
        importStmt.setInsertInto("INSERT INTO");
        importStmt.setMessages("MESSAGES");
        importStmt.setMessageFile("msg.txt");
        importStmt.setType("del");
        return importStmt;
    }
    
    public ImportStatement toInformixImport() throws ConvertException {
        throw new ConvertException("Query yet to be supported.");
    }
    
    public ImportStatement toMSSQLServerImport() throws ConvertException {
        final ImportStatement importStmt = new ImportStatement();
        if (this.loadData != null) {
            importStmt.setBulkInsert("BULK INSERT");
            importStmt.setLoadData(null);
        }
        if (this.fileName != null) {
            importStmt.setFileName(this.fileName);
        }
        if (this.intoTable != null) {
            importStmt.setIntoTable(null);
        }
        importStmt.setFrom("FROM");
        importStmt.setWith("WITH");
        if (this.tableName != null) {
            importStmt.setTableName(this.tableName);
        }
        if (this.fieldsTerminated != null) {
            importStmt.setFieldsTerminated("FIELDTERMINATOR");
        }
        if (this.delimiter != null) {
            importStmt.setDelimiter(this.delimiter);
        }
        return importStmt;
    }
    
    public ImportStatement toMySQLImport() throws ConvertException {
        final ImportStatement importStmt = new ImportStatement();
        if (this.loadData != null) {
            importStmt.setLoadData(this.loadData);
        }
        if (this.fileName != null) {
            importStmt.setFileName(this.fileName);
        }
        if (this.intoTable != null) {
            importStmt.setIntoTable(this.intoTable);
        }
        if (this.tableName != null) {
            importStmt.setTableName(this.tableName);
        }
        if (this.fieldsTerminated != null) {
            importStmt.setFieldsTerminated(this.fieldsTerminated);
        }
        if (this.delimiter != null) {
            importStmt.setDelimiter(this.delimiter);
        }
        return importStmt;
    }
    
    public ImportStatement toOracleImport() throws ConvertException {
        throw new ConvertException("Query yet to be supported.");
    }
    
    public ImportStatement toPostgreSQLImport() throws ConvertException {
        throw new ConvertException("Query yet to be supported.");
    }
    
    public ImportStatement toSybaseImport() throws ConvertException {
        final ImportStatement importStmt = new ImportStatement();
        if (this.loadData != null) {
            importStmt.setBulkInsert("BULK INSERT");
            importStmt.setLoadData(null);
        }
        if (this.fileName != null) {
            importStmt.setFileName(this.fileName);
        }
        if (this.intoTable != null) {
            importStmt.setIntoTable(null);
        }
        importStmt.setFrom("FROM");
        importStmt.setWith("WITH");
        if (this.tableName != null) {
            importStmt.setTableName(this.tableName);
        }
        if (this.fieldsTerminated != null) {
            importStmt.setFieldsTerminated("FIELDTERMINATOR");
        }
        if (this.delimiter != null) {
            importStmt.setDelimiter(this.delimiter);
        }
        return importStmt;
    }
    
    public ImportStatement toTimesTenImport() throws ConvertException {
        throw new ConvertException("\nUnsupported SQL\n");
    }
    
    public ImportStatement toNetezzaImport() throws ConvertException {
        throw new ConvertException("\nUnsupported SQL\n");
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toANSIImport().toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toTeradataImport().toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toDB2Import().toString();
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toInformixImport().toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toMSSQLServerImport().toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return this.toMySQLImport().toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return this.toOracleImport().toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toPostgreSQLImport().toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toSybaseImport().toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toTimesTenImport().toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toNetezzaImport().toString();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String indentString = "\n";
        if (this.loadData != null) {
            sb.append(indentString + this.loadData.toUpperCase());
        }
        if (this.bulkInsert != null) {
            sb.append(indentString + this.bulkInsert.toUpperCase());
        }
        if (this.tableName != null && this.bulkInsert != null && this.bulkInsert.equals("BULK INSERT")) {
            sb.append(" " + this.tableName);
        }
        if (this.from != null) {
            sb.append(" " + this.from.toUpperCase());
        }
        if (this.fileName != null) {
            if (this.bulkInsert != null && this.bulkInsert.equals("IMPORT FROM")) {
                sb.append(" " + this.fileName);
            }
            else {
                sb.append(" '" + this.fileName + "'");
            }
        }
        if (this.type != null) {
            sb.append(" OF " + this.type.toUpperCase());
        }
        if (this.messages != null) {
            sb.append(" " + this.messages.toUpperCase());
            if (this.messageFile != null) {
                sb.append(" " + this.messageFile);
            }
        }
        if (this.with != null) {
            sb.append(" " + this.with.toUpperCase());
        }
        if (this.intoTable != null) {
            sb.append(" " + this.intoTable.toUpperCase());
        }
        if (this.insertInto != null) {
            sb.append(" " + this.insertInto.toUpperCase());
        }
        if (this.tableName != null && (this.bulkInsert == null || (this.bulkInsert != null && this.bulkInsert.equals("IMPORT FROM")))) {
            sb.append(" " + this.tableName);
        }
        if (this.fieldsTerminated != null) {
            if (this.fieldsTerminated.equalsIgnoreCase("FIELDTERMINATOR")) {
                sb.append(" (" + this.fieldsTerminated.toUpperCase() + " =");
            }
            else {
                sb.append(" " + this.fieldsTerminated.toUpperCase());
            }
        }
        if (this.delimiter != null) {
            sb.append(" '" + this.delimiter.toUpperCase() + "'");
        }
        if (this.fieldsTerminated != null && this.fieldsTerminated.equalsIgnoreCase("FIELDTERMINATOR")) {
            sb.append(")");
        }
        return sb.toString();
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
