package com.unboundid.util;

import java.text.ParseException;
import java.util.Random;

final class RandomCharactersValuePatternComponent extends ValuePatternComponent
{
    private static final long serialVersionUID = 1653000400888202919L;
    private final char[] characterSet;
    private final int numCharacters;
    private final Random seedRandom;
    private final ThreadLocal<Random> threadLocalRandoms;
    
    RandomCharactersValuePatternComponent(final String pattern, final long randomSeed) throws ParseException {
        this.seedRandom = new Random(randomSeed);
        this.threadLocalRandoms = new ThreadLocal<Random>();
        final int secondColonPos = pattern.indexOf(58, 7);
        String numCharactersString;
        if (secondColonPos < 0) {
            this.characterSet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            numCharactersString = pattern.substring(7);
        }
        else {
            numCharactersString = pattern.substring(7, secondColonPos);
            final String characterSetString = pattern.substring(secondColonPos + 1);
            this.characterSet = characterSetString.toCharArray();
        }
        try {
            this.numCharacters = Integer.parseInt(numCharactersString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new ParseException(UtilityMessages.ERR_RANDOM_CHARS_VALUE_PATTERN_CANNOT_PARSE_LENGTH.get(pattern, numCharactersString), 7);
        }
        if (this.numCharacters <= 0) {
            throw new ParseException(UtilityMessages.ERR_RANDOM_CHARS_VALUE_PATTERN_INVALID_LENGTH.get(pattern, this.numCharacters), 7);
        }
        if (this.characterSet.length == 0) {
            throw new ParseException(UtilityMessages.ERR_RANDOM_CHARS_VALUE_PATTERN_EMPTY_CHAR_SET.get(pattern), secondColonPos + 1);
        }
    }
    
    @Override
    void append(final StringBuilder buffer) {
        final Random random = this.getRandom();
        for (int i = 0; i < this.numCharacters; ++i) {
            buffer.append(this.characterSet[random.nextInt(this.characterSet.length)]);
        }
    }
    
    @Override
    boolean supportsBackReference() {
        return true;
    }
    
    private Random getRandom() {
        Random random = this.threadLocalRandoms.get();
        if (random == null) {
            synchronized (this.seedRandom) {
                random = new Random(this.seedRandom.nextLong());
            }
            this.threadLocalRandoms.set(random);
        }
        return random;
    }
}
