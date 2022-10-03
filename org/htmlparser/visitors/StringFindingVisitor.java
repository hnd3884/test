package org.htmlparser.visitors;

import org.htmlparser.Text;
import java.util.Locale;

public class StringFindingVisitor extends NodeVisitor
{
    private String stringToFind;
    private int foundCount;
    private boolean multipleSearchesWithinStrings;
    private Locale locale;
    
    public StringFindingVisitor(final String stringToFind) {
        this(stringToFind, null);
    }
    
    public StringFindingVisitor(final String stringToFind, final Locale locale) {
        this.locale = ((null == locale) ? Locale.ENGLISH : locale);
        this.stringToFind = stringToFind.toUpperCase(this.locale);
        this.foundCount = 0;
        this.multipleSearchesWithinStrings = false;
    }
    
    public void doMultipleSearchesWithinStrings() {
        this.multipleSearchesWithinStrings = true;
    }
    
    public void visitStringNode(final Text stringNode) {
        final String stringToBeSearched = stringNode.getText().toUpperCase(this.locale);
        if (!this.multipleSearchesWithinStrings && stringToBeSearched.indexOf(this.stringToFind) != -1) {
            ++this.foundCount;
        }
        else if (this.multipleSearchesWithinStrings) {
            int index = -1;
            do {
                index = stringToBeSearched.indexOf(this.stringToFind, index + 1);
                if (index != -1) {
                    ++this.foundCount;
                }
            } while (index != -1);
        }
    }
    
    public boolean stringWasFound() {
        return 0 != this.stringFoundCount();
    }
    
    public int stringFoundCount() {
        return this.foundCount;
    }
}
