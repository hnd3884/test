package javax.servlet.jsp.tagext;

public abstract class TagLibraryInfo
{
    protected String prefix;
    protected String uri;
    protected TagInfo[] tags;
    protected TagFileInfo[] tagFiles;
    protected FunctionInfo[] functions;
    protected String tlibversion;
    protected String jspversion;
    protected String shortname;
    protected String urn;
    protected String info;
    
    protected TagLibraryInfo(final String prefix, final String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }
    
    public String getURI() {
        return this.uri;
    }
    
    public String getPrefixString() {
        return this.prefix;
    }
    
    public String getShortName() {
        return this.shortname;
    }
    
    public String getReliableURN() {
        return this.urn;
    }
    
    public String getInfoString() {
        return this.info;
    }
    
    public String getRequiredVersion() {
        return this.jspversion;
    }
    
    public TagInfo[] getTags() {
        return this.tags;
    }
    
    public TagFileInfo[] getTagFiles() {
        return this.tagFiles;
    }
    
    public TagInfo getTag(final String shortname) {
        final TagInfo[] tags = this.getTags();
        if (tags == null || tags.length == 0 || shortname == null) {
            return null;
        }
        for (final TagInfo tag : tags) {
            if (shortname.equals(tag.getTagName())) {
                return tag;
            }
        }
        return null;
    }
    
    public TagFileInfo getTagFile(final String shortname) {
        final TagFileInfo[] tagFiles = this.getTagFiles();
        if (tagFiles == null || tagFiles.length == 0) {
            return null;
        }
        for (final TagFileInfo tagFile : tagFiles) {
            if (tagFile.getName().equals(shortname)) {
                return tagFile;
            }
        }
        return null;
    }
    
    public FunctionInfo[] getFunctions() {
        return this.functions;
    }
    
    public FunctionInfo getFunction(final String name) {
        if (this.functions == null || this.functions.length == 0) {
            return null;
        }
        for (final FunctionInfo function : this.functions) {
            if (function.getName().equals(name)) {
                return function;
            }
        }
        return null;
    }
    
    public abstract TagLibraryInfo[] getTagLibraryInfos();
}
