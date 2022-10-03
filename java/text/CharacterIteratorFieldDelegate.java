package java.text;

import java.util.ArrayList;

class CharacterIteratorFieldDelegate implements Format.FieldDelegate
{
    private ArrayList<AttributedString> attributedStrings;
    private int size;
    
    CharacterIteratorFieldDelegate() {
        this.attributedStrings = new ArrayList<AttributedString>();
    }
    
    @Override
    public void formatted(final Format.Field field, final Object o, final int i, final int size, final StringBuffer sb) {
        if (i != size) {
            if (i < this.size) {
                int size2 = this.size;
                int n = this.attributedStrings.size() - 1;
                while (i < size2) {
                    final AttributedString attributedString = this.attributedStrings.get(n--);
                    final int n2 = size2 - attributedString.length();
                    final int max = Math.max(0, i - n2);
                    attributedString.addAttribute(field, o, max, Math.min(size - i, attributedString.length() - max) + max);
                    size2 = n2;
                }
            }
            if (this.size < i) {
                this.attributedStrings.add(new AttributedString(sb.substring(this.size, i)));
                this.size = i;
            }
            if (this.size < size) {
                final AttributedString attributedString2 = new AttributedString(sb.substring(Math.max(i, this.size), size));
                attributedString2.addAttribute(field, o);
                this.attributedStrings.add(attributedString2);
                this.size = size;
            }
        }
    }
    
    @Override
    public void formatted(final int n, final Format.Field field, final Object o, final int n2, final int n3, final StringBuffer sb) {
        this.formatted(field, o, n2, n3, sb);
    }
    
    public AttributedCharacterIterator getIterator(final String s) {
        if (s.length() > this.size) {
            this.attributedStrings.add(new AttributedString(s.substring(this.size)));
            this.size = s.length();
        }
        final int size = this.attributedStrings.size();
        final AttributedCharacterIterator[] array = new AttributedCharacterIterator[size];
        for (int i = 0; i < size; ++i) {
            array[i] = this.attributedStrings.get(i).getIterator();
        }
        return new AttributedString(array).getIterator();
    }
}
