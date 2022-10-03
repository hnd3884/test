package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class TableClause
{
    private TableObject tableObject;
    private String dblink;
    private String partition;
    private String subPartition;
    private String alias;
    private boolean toMSSQLServer;
    private UserObjectContext context;
    
    public TableClause() {
        this.context = null;
        this.tableObject = null;
        this.dblink = null;
        this.partition = null;
        this.subPartition = null;
        this.alias = null;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setTableObject(final TableObject to) {
        this.tableObject = to;
    }
    
    public TableObject getTableObject() {
        return this.tableObject;
    }
    
    public void setdblink(final String s) {
        this.dblink = s;
    }
    
    public void setSubPartition(final String s) {
        this.subPartition = s;
    }
    
    public void setPartition(final String s) {
        this.partition = s;
    }
    
    public void setAlias(final String s) {
        this.alias = s;
    }
    
    public String getAlias() {
        return this.alias;
    }
    
    public void setToMSSQLServer(final boolean toMSSQLServer) {
        this.toMSSQLServer = toMSSQLServer;
    }
    
    public String getdblink() {
        return this.dblink;
    }
    
    public String getSubPartition() {
        return this.subPartition;
    }
    
    public String getPartition() {
        return this.partition;
    }
    
    public void toMySQL() throws ConvertException {
        if (this.tableObject != null) {
            if (this.tableObject.getTableName() != null) {
                String tableName = this.tableObject.getTableName();
                if ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("\"") && tableName.endsWith("\""))) {
                    tableName = tableName.substring(1, tableName.length() - 1);
                }
                if (!tableName.startsWith("`") && tableName.indexOf(32) != -1) {
                    tableName = "`" + tableName + "`";
                }
                this.tableObject.setTableName(tableName);
            }
            if (this.tableObject.getUser() != null) {
                String userName = this.tableObject.getUser();
                if (userName.startsWith("[") && userName.endsWith("]")) {
                    userName = userName.substring(1, userName.length() - 1);
                }
                if (userName.indexOf(32) != -1) {
                    userName = "`" + userName + "`";
                }
                this.tableObject.setUser(userName);
            }
            this.tableObject.toMySQL();
        }
    }
    
    public void toOracle() throws ConvertException {
        if (this.tableObject != null) {
            if (this.tableObject.getTableName() != null) {
                String tableName = this.tableObject.getTableName();
                if (this.tableObject.getDatabaseName() != null) {
                    String databaseName = this.tableObject.getDatabaseName();
                    if (tableName.startsWith("#")) {
                        tableName = tableName.substring(1);
                    }
                    if (databaseName.startsWith("@")) {
                        databaseName = "\"" + databaseName + "\"";
                    }
                    this.tableObject.setTableName(tableName);
                    this.tableObject.setDatabaseName(databaseName);
                }
                if ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`"))) {
                    tableName = tableName.substring(1, tableName.length() - 1);
                }
                if (!tableName.startsWith("\"") && (SwisSQLOptions.retainQuotedIdentifierForOracle || tableName.indexOf(32) != -1)) {
                    tableName = "\"" + tableName + "\"";
                }
                this.tableObject.setTableName(tableName);
            }
            if (this.tableObject.getUser() != null) {
                String userName = this.tableObject.getUser();
                if ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`"))) {
                    userName = userName.substring(1, userName.length() - 1);
                }
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
                this.tableObject.setUser(userName);
            }
            this.tableObject.toOracle();
        }
    }
    
    public void toMSSQLServer() throws ConvertException {
        if (this.tableObject != null) {
            if (this.tableObject.getTableName() != null) {
                String tableName = this.tableObject.getTableName();
                if (tableName.startsWith("`") && tableName.endsWith("`")) {
                    tableName = tableName.substring(1, tableName.length() - 1);
                }
                if (!tableName.startsWith("\"") && !tableName.startsWith("[") && tableName.indexOf(32) != -1) {
                    tableName = "[" + tableName + "]";
                }
                this.tableObject.setTableName(tableName);
            }
            if (this.tableObject.getUser() != null) {
                String userName = this.tableObject.getUser();
                if (userName.startsWith("`") && userName.endsWith("`")) {
                    userName = userName.substring(1, userName.length() - 1);
                }
                if (userName.indexOf(32) != -1) {
                    userName = "[" + userName + "]";
                }
                this.tableObject.setUser(userName);
            }
            this.tableObject.toMSSQLServer();
        }
    }
    
    public void toSybase() throws ConvertException {
        if (this.tableObject != null) {
            if (this.tableObject.getTableName() != null) {
                String tableName = this.tableObject.getTableName();
                if (tableName.startsWith("`") && tableName.endsWith("`")) {
                    tableName = tableName.substring(1, tableName.length() - 1);
                }
                if (!tableName.startsWith("\"") && !tableName.startsWith("[") && tableName.indexOf(32) != -1) {
                    tableName = "[" + tableName + "]";
                }
                this.tableObject.setTableName(tableName);
            }
            if (this.tableObject.getUser() != null) {
                String userName = this.tableObject.getUser();
                if (userName.startsWith("`") && userName.endsWith("`")) {
                    userName = userName.substring(1, userName.length() - 1);
                }
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
                this.tableObject.setUser(userName);
            }
            this.tableObject.toSybase();
        }
    }
    
    public void toPostgreSQL() throws ConvertException {
        if (this.tableObject != null) {
            this.tableObject.toPostgreSQL();
        }
    }
    
    public void toDB2() throws ConvertException {
        if (this.tableObject != null) {
            if (this.tableObject.getTableName() != null) {
                String tableName = this.tableObject.getTableName();
                if ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`"))) {
                    tableName = tableName.substring(1, tableName.length() - 1);
                }
                if (!tableName.startsWith("\"") && tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
                this.tableObject.setTableName(tableName);
            }
            if (this.tableObject.getUser() != null) {
                String userName = this.tableObject.getUser();
                if ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`"))) {
                    userName = userName.substring(1, userName.length() - 1);
                }
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
                this.tableObject.setUser(userName);
            }
            this.tableObject.toDB2();
        }
    }
    
    public void toInformix() throws ConvertException {
        if (this.tableObject != null) {
            if (this.tableObject.getTableName() != null) {
                String tableName = this.tableObject.getTableName();
                if ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`"))) {
                    tableName = tableName.substring(1, tableName.length() - 1);
                }
                if (!tableName.startsWith("\"") && tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
                this.tableObject.setTableName(tableName);
            }
            if (this.tableObject.getUser() != null) {
                String userName = this.tableObject.getUser();
                if ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`"))) {
                    userName = userName.substring(1, userName.length() - 1);
                }
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
                this.tableObject.setUser(userName);
            }
            this.tableObject.toInformix();
        }
    }
    
    public void toANSISQL() throws ConvertException {
        if (this.tableObject != null) {
            if (this.tableObject.getTableName() != null) {
                String tableName = this.tableObject.getTableName();
                if ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`"))) {
                    tableName = tableName.substring(1, tableName.length() - 1);
                }
                if (!tableName.startsWith("\"") && tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
                this.tableObject.setTableName(tableName);
            }
            if (this.tableObject.getUser() != null) {
                String userName = this.tableObject.getUser();
                if ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`"))) {
                    userName = userName.substring(1, userName.length() - 1);
                }
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
                this.tableObject.setUser(userName);
            }
            this.tableObject.toANSISQL();
        }
    }
    
    public void toTeradata() throws ConvertException {
        if (this.tableObject != null) {
            if (this.tableObject.getTableName() != null) {
                String tableName = this.tableObject.getTableName();
                if ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`"))) {
                    tableName = tableName.substring(1, tableName.length() - 1);
                }
                if (!tableName.startsWith("\"") && tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
                this.tableObject.setTableName(tableName);
            }
            if (this.tableObject.getUser() != null) {
                String userName = this.tableObject.getUser();
                if ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`"))) {
                    userName = userName.substring(1, userName.length() - 1);
                }
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
                this.tableObject.setUser(userName);
            }
            this.tableObject.toTeradata();
        }
    }
    
    public void toTimesTen() throws ConvertException {
        if (this.tableObject != null) {
            if (this.tableObject.getTableName() != null) {
                String tableName = this.tableObject.getTableName();
                if ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`"))) {
                    tableName = tableName.substring(1, tableName.length() - 1);
                    if (tableName.indexOf(32) != -1) {
                        tableName = "\"" + tableName + "\"";
                    }
                }
                this.tableObject.setTableName(tableName);
            }
            if (this.tableObject.getUser() != null) {
                String userName = this.tableObject.getUser();
                if ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`"))) {
                    userName = userName.substring(1, userName.length() - 1);
                }
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
                this.tableObject.setUser(userName);
            }
            this.tableObject.toTimesTen();
        }
    }
    
    public void toNetezza() throws ConvertException {
        if (this.tableObject != null) {
            if (this.tableObject.getTableName() != null) {
                String tableName = this.tableObject.getTableName();
                if ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`"))) {
                    tableName = tableName.substring(1, tableName.length() - 1);
                }
                if (!tableName.startsWith("\"") && tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
                this.tableObject.setTableName(tableName);
            }
            if (this.tableObject.getUser() != null) {
                String userName = this.tableObject.getUser();
                if ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`"))) {
                    userName = userName.substring(1, userName.length() - 1);
                }
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
                this.tableObject.setUser(userName);
            }
            if (this.dblink != null) {
                this.dblink = null;
            }
            if (this.partition != null) {
                this.partition = null;
            }
            if (this.subPartition != null) {
                this.subPartition = null;
            }
            this.tableObject.toNetezza();
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        if (this.tableObject != null) {
            if (this.context != null) {
                this.tableObject.setObjectContext(this.context);
            }
            if (!this.toMSSQLServer) {
                stringbuffer.append(this.tableObject.toString());
                stringbuffer.append(" ");
            }
            else if (this.alias != null) {
                stringbuffer.append("");
            }
            else {
                stringbuffer.append(this.tableObject.toString());
                stringbuffer.append(" ");
            }
        }
        if (this.dblink != null) {
            stringbuffer.append(this.dblink);
            stringbuffer.append(" ");
        }
        if (this.partition != null) {
            stringbuffer.append(this.partition);
            stringbuffer.append(" ");
        }
        if (this.subPartition != null) {
            stringbuffer.append(this.subPartition);
            stringbuffer.append(" ");
        }
        if (this.alias != null) {
            stringbuffer.append(this.alias);
            stringbuffer.append(" ");
        }
        return stringbuffer.toString();
    }
}
