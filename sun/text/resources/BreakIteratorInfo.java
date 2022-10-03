package sun.text.resources;

import java.util.ListResourceBundle;

public class BreakIteratorInfo extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "BreakIteratorClasses", { "RuleBasedBreakIterator", "RuleBasedBreakIterator", "RuleBasedBreakIterator", "RuleBasedBreakIterator" } }, { "CharacterData", "CharacterBreakIteratorData" }, { "WordData", "WordBreakIteratorData" }, { "LineData", "LineBreakIteratorData" }, { "SentenceData", "SentenceBreakIteratorData" } };
    }
}
