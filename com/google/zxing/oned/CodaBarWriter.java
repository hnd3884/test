package com.google.zxing.oned;

public class CodaBarWriter extends OneDimensionalCodeWriter
{
    public CodaBarWriter() {
        super(20);
    }
    
    @Override
    public byte[] encode(final String contents) {
        if (!CodaBarReader.arrayContains(new char[] { 'A', 'B', 'C', 'D' }, Character.toUpperCase(contents.charAt(0)))) {
            throw new IllegalArgumentException("Codabar should start with one of the following: 'A', 'B', 'C' or 'D'");
        }
        if (!CodaBarReader.arrayContains(new char[] { 'T', 'N', '*', 'E' }, Character.toUpperCase(contents.charAt(contents.length() - 1)))) {
            throw new IllegalArgumentException("Codabar should end with one of the following: 'T', 'N', '*' or 'E'");
        }
        int resultLength = 20;
        final char[] charsWhichAreTenLengthEachAfterDecoded = { '/', ':', '+', '.' };
        for (int i = 1; i < contents.length() - 1; ++i) {
            if (Character.isDigit(contents.charAt(i)) || contents.charAt(i) == '-' || contents.charAt(i) == '$') {
                resultLength += 9;
            }
            else {
                if (!CodaBarReader.arrayContains(charsWhichAreTenLengthEachAfterDecoded, contents.charAt(i))) {
                    throw new IllegalArgumentException("Cannot encode : '" + contents.charAt(i) + '\'');
                }
                resultLength += 10;
            }
        }
        resultLength += contents.length() - 1;
        final byte[] result = new byte[resultLength];
        int position = 0;
        for (int index = 0; index < contents.length(); ++index) {
            char c = Character.toUpperCase(contents.charAt(index));
            if (index == contents.length() - 1) {
                if (c == '*') {
                    c = 'C';
                }
                else if (c == 'E') {
                    c = 'D';
                }
            }
            int code = 0;
            for (int j = 0; j < CodaBarReader.ALPHABET.length; ++j) {
                if (c == CodaBarReader.ALPHABET[j]) {
                    code = CodaBarReader.CHARACTER_ENCODINGS[j];
                    break;
                }
            }
            byte color = 1;
            int counter = 0;
            int bit = 0;
            while (bit < 7) {
                result[position] = color;
                ++position;
                if ((code >> 6 - bit & 0x1) == 0x0 || counter == 1) {
                    color ^= 0x1;
                    ++bit;
                    counter = 0;
                }
                else {
                    ++counter;
                }
            }
            if (index < contents.length() - 1) {
                result[position] = 0;
                ++position;
            }
        }
        return result;
    }
}
