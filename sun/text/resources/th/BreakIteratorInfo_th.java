package sun.text.resources.th;

import java.util.ListResourceBundle;

public class BreakIteratorInfo_th extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "BreakIteratorClasses", { "RuleBasedBreakIterator", "DictionaryBasedBreakIterator", "DictionaryBasedBreakIterator", "RuleBasedBreakIterator" } }, { "WordData", "th/WordBreakIteratorData_th" }, { "LineData", "th/LineBreakIteratorData_th" }, { "WordDictionary", "th/thai_dict" }, { "LineDictionary", "th/thai_dict" } };
    }
}
