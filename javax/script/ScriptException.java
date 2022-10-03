package javax.script;

public class ScriptException extends Exception
{
    private static final long serialVersionUID = 8265071037049225001L;
    private String fileName;
    private int lineNumber;
    private int columnNumber;
    
    public ScriptException(final String s) {
        super(s);
        this.fileName = null;
        this.lineNumber = -1;
        this.columnNumber = -1;
    }
    
    public ScriptException(final Exception ex) {
        super(ex);
        this.fileName = null;
        this.lineNumber = -1;
        this.columnNumber = -1;
    }
    
    public ScriptException(final String s, final String fileName, final int lineNumber) {
        super(s);
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.columnNumber = -1;
    }
    
    public ScriptException(final String s, final String fileName, final int lineNumber, final int columnNumber) {
        super(s);
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    @Override
    public String getMessage() {
        String s = super.getMessage();
        if (this.fileName != null) {
            s = s + " in " + this.fileName;
            if (this.lineNumber != -1) {
                s = s + " at line number " + this.lineNumber;
            }
            if (this.columnNumber != -1) {
                s = s + " at column number " + this.columnNumber;
            }
        }
        return s;
    }
    
    public int getLineNumber() {
        return this.lineNumber;
    }
    
    public int getColumnNumber() {
        return this.columnNumber;
    }
    
    public String getFileName() {
        return this.fileName;
    }
}
