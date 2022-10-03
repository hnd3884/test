package org.apache.poi.ss.formula;

public class SheetIdentifier
{
    public String _bookName;
    public NameIdentifier _sheetIdentifier;
    
    public SheetIdentifier(final String bookName, final NameIdentifier sheetIdentifier) {
        this._bookName = bookName;
        this._sheetIdentifier = sheetIdentifier;
    }
    
    public String getBookName() {
        return this._bookName;
    }
    
    public NameIdentifier getSheetIdentifier() {
        return this._sheetIdentifier;
    }
    
    protected void asFormulaString(final StringBuilder sb) {
        if (this._bookName != null) {
            sb.append(" [").append(this._sheetIdentifier.getName()).append("]");
        }
        if (this._sheetIdentifier.isQuoted()) {
            sb.append("'").append(this._sheetIdentifier.getName()).append("'");
        }
        else {
            sb.append(this._sheetIdentifier.getName());
        }
    }
    
    public String asFormulaString() {
        final StringBuilder sb = new StringBuilder(32);
        this.asFormulaString(sb);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " [" + this.asFormulaString() + "]";
    }
}
