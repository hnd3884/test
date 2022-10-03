package sun.util.locale.provider;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Stack;
import java.text.CharacterIterator;
import java.io.IOException;

class DictionaryBasedBreakIterator extends RuleBasedBreakIterator
{
    private BreakDictionary dictionary;
    private boolean[] categoryFlags;
    private int dictionaryCharCount;
    private int[] cachedBreakPositions;
    private int positionInCache;
    
    DictionaryBasedBreakIterator(final String s, final String s2) throws IOException {
        super(s);
        final byte[] additionalData = super.getAdditionalData();
        if (additionalData != null) {
            this.prepareCategoryFlags(additionalData);
            super.setAdditionalData(null);
        }
        this.dictionary = new BreakDictionary(s2);
    }
    
    private void prepareCategoryFlags(final byte[] array) {
        this.categoryFlags = new boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            this.categoryFlags[i] = (array[i] == 1);
        }
    }
    
    @Override
    public void setText(final CharacterIterator text) {
        super.setText(text);
        this.cachedBreakPositions = null;
        this.dictionaryCharCount = 0;
        this.positionInCache = 0;
    }
    
    @Override
    public int first() {
        this.cachedBreakPositions = null;
        this.dictionaryCharCount = 0;
        this.positionInCache = 0;
        return super.first();
    }
    
    @Override
    public int last() {
        this.cachedBreakPositions = null;
        this.dictionaryCharCount = 0;
        this.positionInCache = 0;
        return super.last();
    }
    
    @Override
    public int previous() {
        final CharacterIterator text = this.getText();
        if (this.cachedBreakPositions != null && this.positionInCache > 0) {
            --this.positionInCache;
            text.setIndex(this.cachedBreakPositions[this.positionInCache]);
            return this.cachedBreakPositions[this.positionInCache];
        }
        this.cachedBreakPositions = null;
        final int previous = super.previous();
        if (this.cachedBreakPositions != null) {
            this.positionInCache = this.cachedBreakPositions.length - 2;
        }
        return previous;
    }
    
    @Override
    public int preceding(final int n) {
        final CharacterIterator text = this.getText();
        RuleBasedBreakIterator.checkOffset(n, text);
        if (this.cachedBreakPositions == null || n <= this.cachedBreakPositions[0] || n > this.cachedBreakPositions[this.cachedBreakPositions.length - 1]) {
            this.cachedBreakPositions = null;
            return super.preceding(n);
        }
        this.positionInCache = 0;
        while (this.positionInCache < this.cachedBreakPositions.length && n > this.cachedBreakPositions[this.positionInCache]) {
            ++this.positionInCache;
        }
        --this.positionInCache;
        text.setIndex(this.cachedBreakPositions[this.positionInCache]);
        return text.getIndex();
    }
    
    @Override
    public int following(final int n) {
        final CharacterIterator text = this.getText();
        RuleBasedBreakIterator.checkOffset(n, text);
        if (this.cachedBreakPositions == null || n < this.cachedBreakPositions[0] || n >= this.cachedBreakPositions[this.cachedBreakPositions.length - 1]) {
            this.cachedBreakPositions = null;
            return super.following(n);
        }
        this.positionInCache = 0;
        while (this.positionInCache < this.cachedBreakPositions.length && n >= this.cachedBreakPositions[this.positionInCache]) {
            ++this.positionInCache;
        }
        text.setIndex(this.cachedBreakPositions[this.positionInCache]);
        return text.getIndex();
    }
    
    @Override
    protected int handleNext() {
        final CharacterIterator text = this.getText();
        if (this.cachedBreakPositions == null || this.positionInCache == this.cachedBreakPositions.length - 1) {
            final int index = text.getIndex();
            this.dictionaryCharCount = 0;
            final int handleNext = super.handleNext();
            if (this.dictionaryCharCount <= 1 || handleNext - index <= 1) {
                this.cachedBreakPositions = null;
                return handleNext;
            }
            this.divideUpDictionaryRange(index, handleNext);
        }
        if (this.cachedBreakPositions != null) {
            ++this.positionInCache;
            text.setIndex(this.cachedBreakPositions[this.positionInCache]);
            return this.cachedBreakPositions[this.positionInCache];
        }
        return -9999;
    }
    
    @Override
    protected int lookupCategory(final int n) {
        final int lookupCategory = super.lookupCategory(n);
        if (lookupCategory != -1 && this.categoryFlags[lookupCategory]) {
            ++this.dictionaryCharCount;
        }
        return lookupCategory;
    }
    
    private void divideUpDictionaryRange(final int index, final int n) {
        final CharacterIterator text = this.getText();
        text.setIndex(index);
        for (int n2 = this.lookupCategory(this.getCurrent()); n2 == -1 || !this.categoryFlags[n2]; n2 = this.lookupCategory(this.getNext())) {}
        Stack<Integer> stack = new Stack<Integer>();
        final Stack<Integer> stack2 = new Stack<Integer>();
        final ArrayList list = new ArrayList();
        int nextStateFromCharacter = 0;
        int n3 = text.getIndex();
        Vector<Object> vector = null;
        int n4 = this.getCurrent();
        while (true) {
            if (this.dictionary.getNextState(nextStateFromCharacter, 0) == -1) {
                stack2.push(text.getIndex());
            }
            nextStateFromCharacter = this.dictionary.getNextStateFromCharacter(nextStateFromCharacter, n4);
            if (nextStateFromCharacter == -1) {
                stack.push(text.getIndex());
                break;
            }
            if (nextStateFromCharacter == 0 || text.getIndex() >= n) {
                if (text.getIndex() > n3) {
                    n3 = text.getIndex();
                    vector = (Stack)stack.clone();
                }
                while (!stack2.isEmpty() && list.contains(stack2.peek())) {
                    stack2.pop();
                }
                if (stack2.isEmpty()) {
                    if (vector != null) {
                        stack = (Stack<Integer>)vector;
                        if (n3 >= n) {
                            break;
                        }
                        text.setIndex(n3 + 1);
                    }
                    else {
                        if ((stack.size() == 0 || stack.peek() != text.getIndex()) && text.getIndex() != index) {
                            stack.push(new Integer(text.getIndex()));
                        }
                        this.getNext();
                        stack.push(new Integer(text.getIndex()));
                    }
                }
                else {
                    final Integer n5 = stack2.pop();
                    while (!stack.isEmpty() && n5 < stack.peek()) {
                        list.add(stack.pop());
                    }
                    stack.push(n5);
                    text.setIndex(stack.peek());
                }
                n4 = this.getCurrent();
                if (text.getIndex() >= n) {
                    break;
                }
                continue;
            }
            else {
                n4 = this.getNext();
            }
        }
        if (!stack.isEmpty()) {
            stack.pop();
        }
        stack.push(n);
        (this.cachedBreakPositions = new int[stack.size() + 1])[0] = index;
        for (int i = 0; i < stack.size(); ++i) {
            this.cachedBreakPositions[i + 1] = (int)stack.elementAt(i);
        }
        this.positionInCache = 0;
    }
}
