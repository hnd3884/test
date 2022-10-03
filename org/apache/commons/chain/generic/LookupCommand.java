package org.apache.commons.chain.generic;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Filter;

public class LookupCommand implements Filter
{
    private CatalogFactory catalogFactory;
    private String catalogName;
    private String name;
    private String nameKey;
    private boolean optional;
    private boolean ignoreExecuteResult;
    private boolean ignorePostprocessResult;
    
    public LookupCommand() {
        this(CatalogFactory.getInstance());
    }
    
    public LookupCommand(final CatalogFactory factory) {
        this.catalogFactory = null;
        this.catalogName = null;
        this.name = null;
        this.nameKey = null;
        this.optional = false;
        this.ignoreExecuteResult = false;
        this.ignorePostprocessResult = false;
        this.catalogFactory = factory;
    }
    
    public void setCatalogFactory(final CatalogFactory catalogFactory) {
        this.catalogFactory = catalogFactory;
    }
    
    public CatalogFactory getCatalogFactory() {
        return this.catalogFactory;
    }
    
    public String getCatalogName() {
        return this.catalogName;
    }
    
    public void setCatalogName(final String catalogName) {
        this.catalogName = catalogName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getNameKey() {
        return this.nameKey;
    }
    
    public void setNameKey(final String nameKey) {
        this.nameKey = nameKey;
    }
    
    public boolean isOptional() {
        return this.optional;
    }
    
    public void setOptional(final boolean optional) {
        this.optional = optional;
    }
    
    public boolean isIgnoreExecuteResult() {
        return this.ignoreExecuteResult;
    }
    
    public void setIgnoreExecuteResult(final boolean ignoreReturn) {
        this.ignoreExecuteResult = ignoreReturn;
    }
    
    public boolean isIgnorePostprocessResult() {
        return this.ignorePostprocessResult;
    }
    
    public void setIgnorePostprocessResult(final boolean ignorePostprocessResult) {
        this.ignorePostprocessResult = ignorePostprocessResult;
    }
    
    public boolean execute(final Context context) throws Exception {
        final Command command = this.getCommand(context);
        if (command != null) {
            final boolean result = command.execute(context);
            return !this.isIgnoreExecuteResult() && result;
        }
        return false;
    }
    
    public boolean postprocess(final Context context, final Exception exception) {
        final Command command = this.getCommand(context);
        if (command != null && command instanceof Filter) {
            final boolean result = ((Filter)command).postprocess(context, exception);
            return !this.isIgnorePostprocessResult() && result;
        }
        return false;
    }
    
    protected Catalog getCatalog(final Context context) {
        CatalogFactory lookupFactory = this.catalogFactory;
        if (lookupFactory == null) {
            lookupFactory = CatalogFactory.getInstance();
        }
        final String catalogName = this.getCatalogName();
        Catalog catalog = null;
        if (catalogName == null) {
            catalog = lookupFactory.getCatalog();
        }
        else {
            catalog = lookupFactory.getCatalog(catalogName);
        }
        if (catalog != null) {
            return catalog;
        }
        if (catalogName == null) {
            throw new IllegalArgumentException("Cannot find default catalog");
        }
        throw new IllegalArgumentException("Cannot find catalog '" + catalogName + "'");
    }
    
    protected Command getCommand(final Context context) {
        final Catalog catalog = this.getCatalog(context);
        Command command = null;
        final String name = this.getCommandName(context);
        if (name == null) {
            throw new IllegalArgumentException("No command name");
        }
        command = catalog.getCommand(name);
        if (command != null || this.isOptional()) {
            return command;
        }
        if (this.catalogName == null) {
            throw new IllegalArgumentException("Cannot find command '" + name + "' in default catalog");
        }
        throw new IllegalArgumentException("Cannot find command '" + name + "' in catalog '" + this.catalogName + "'");
    }
    
    protected String getCommandName(final Context context) {
        String name = this.getName();
        if (name == null) {
            name = context.get(this.getNameKey());
        }
        return name;
    }
}
