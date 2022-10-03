package net.sf.jsqlparser.schema;

public final class Database implements MultiPartName
{
    private Server server;
    private String databaseName;
    
    public Database(final String databaseName) {
        this.setDatabaseName(databaseName);
    }
    
    public Database(final Server server, final String databaseName) {
        this.setServer(server);
        this.setDatabaseName(databaseName);
    }
    
    public Server getServer() {
        return this.server;
    }
    
    public void setServer(final Server server) {
        this.server = server;
    }
    
    public String getDatabaseName() {
        return this.databaseName;
    }
    
    public void setDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
    }
    
    @Override
    public String getFullyQualifiedName() {
        String fqn = "";
        if (this.server != null) {
            fqn += this.server.getFullyQualifiedName();
        }
        if (!fqn.isEmpty()) {
            fqn += ".";
        }
        if (this.databaseName != null) {
            fqn += this.databaseName;
        }
        return fqn;
    }
    
    @Override
    public String toString() {
        return this.getFullyQualifiedName();
    }
}
