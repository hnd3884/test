package com.adventnet.cli.messageset;

import java.io.Serializable;

public class CmdHelp implements Serializable
{
    private String description;
    private String syntax;
    private String helpOptions;
    private String example;
    private String remarks;
    private String relatedCommands;
    
    public CmdHelp() {
        this.description = null;
        this.syntax = null;
        this.helpOptions = null;
        this.example = null;
        this.remarks = null;
        this.relatedCommands = null;
    }
    
    public String getHelpDescription() {
        return this.description;
    }
    
    public void setHelpDescription(final String description) {
        this.description = description;
    }
    
    public String getHelpOptions() {
        return this.helpOptions;
    }
    
    public void setHelpOptions(final String helpOptions) {
        this.helpOptions = helpOptions;
    }
    
    public String getHelpSyntax() {
        return this.syntax;
    }
    
    public void setHelpSyntax(final String syntax) {
        this.syntax = syntax;
    }
    
    public String getHelpExample() {
        return this.example;
    }
    
    public void setHelpExample(final String example) {
        this.example = example;
    }
    
    public String getHelpRemarks() {
        return this.remarks;
    }
    
    public void setHelpRemarks(final String remarks) {
        this.remarks = remarks;
    }
    
    public String getHelpRelatedCommands() {
        return this.relatedCommands;
    }
    
    public void setHelpRelatedCommands(final String relatedCommands) {
        this.relatedCommands = relatedCommands;
    }
    
    public String toString() {
        return "HelpNode";
    }
}
