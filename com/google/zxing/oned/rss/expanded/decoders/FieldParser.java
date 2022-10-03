package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;

final class FieldParser
{
    private static final Object VARIABLE_LENGTH;
    private static final Object[][] TWO_DIGIT_DATA_LENGTH;
    private static final Object[][] THREE_DIGIT_DATA_LENGTH;
    private static final Object[][] THREE_DIGIT_PLUS_DIGIT_DATA_LENGTH;
    private static final Object[][] FOUR_DIGIT_DATA_LENGTH;
    
    private FieldParser() {
    }
    
    static String parseFieldsInGeneralPurpose(final String rawInformation) throws NotFoundException {
        if (rawInformation.length() == 0) {
            return null;
        }
        if (rawInformation.length() < 2) {
            throw NotFoundException.getNotFoundInstance();
        }
        final String firstTwoDigits = rawInformation.substring(0, 2);
        final Object[][] arr$ = FieldParser.TWO_DIGIT_DATA_LENGTH;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final Object[] dataLength = arr$[i$];
            if (dataLength[0].equals(firstTwoDigits)) {
                if (dataLength[1] == FieldParser.VARIABLE_LENGTH) {
                    return processVariableAI(2, (int)dataLength[2], rawInformation);
                }
                return processFixedAI(2, (int)dataLength[1], rawInformation);
            }
            else {
                ++i$;
            }
        }
        if (rawInformation.length() < 3) {
            throw NotFoundException.getNotFoundInstance();
        }
        final String firstThreeDigits = rawInformation.substring(0, 3);
        Object[][] arr$2 = FieldParser.THREE_DIGIT_DATA_LENGTH;
        int len$2 = arr$2.length;
        int i$2 = 0;
        while (i$2 < len$2) {
            final Object[] dataLength2 = arr$2[i$2];
            if (dataLength2[0].equals(firstThreeDigits)) {
                if (dataLength2[1] == FieldParser.VARIABLE_LENGTH) {
                    return processVariableAI(3, (int)dataLength2[2], rawInformation);
                }
                return processFixedAI(3, (int)dataLength2[1], rawInformation);
            }
            else {
                ++i$2;
            }
        }
        arr$2 = FieldParser.THREE_DIGIT_PLUS_DIGIT_DATA_LENGTH;
        len$2 = arr$2.length;
        i$2 = 0;
        while (i$2 < len$2) {
            final Object[] dataLength2 = arr$2[i$2];
            if (dataLength2[0].equals(firstThreeDigits)) {
                if (dataLength2[1] == FieldParser.VARIABLE_LENGTH) {
                    return processVariableAI(4, (int)dataLength2[2], rawInformation);
                }
                return processFixedAI(4, (int)dataLength2[1], rawInformation);
            }
            else {
                ++i$2;
            }
        }
        if (rawInformation.length() < 4) {
            throw NotFoundException.getNotFoundInstance();
        }
        final String firstFourDigits = rawInformation.substring(0, 4);
        final Object[][] arr$3 = FieldParser.FOUR_DIGIT_DATA_LENGTH;
        final int len$3 = arr$3.length;
        int i$3 = 0;
        while (i$3 < len$3) {
            final Object[] dataLength3 = arr$3[i$3];
            if (dataLength3[0].equals(firstFourDigits)) {
                if (dataLength3[1] == FieldParser.VARIABLE_LENGTH) {
                    return processVariableAI(4, (int)dataLength3[2], rawInformation);
                }
                return processFixedAI(4, (int)dataLength3[1], rawInformation);
            }
            else {
                ++i$3;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    private static String processFixedAI(final int aiSize, final int fieldSize, final String rawInformation) throws NotFoundException {
        if (rawInformation.length() < aiSize) {
            throw NotFoundException.getNotFoundInstance();
        }
        final String ai = rawInformation.substring(0, aiSize);
        if (rawInformation.length() < aiSize + fieldSize) {
            throw NotFoundException.getNotFoundInstance();
        }
        final String field = rawInformation.substring(aiSize, aiSize + fieldSize);
        final String remaining = rawInformation.substring(aiSize + fieldSize);
        final String result = '(' + ai + ')' + field;
        final String parsedAI = parseFieldsInGeneralPurpose(remaining);
        return (parsedAI == null) ? result : (result + parsedAI);
    }
    
    private static String processVariableAI(final int aiSize, final int variableFieldSize, final String rawInformation) throws NotFoundException {
        final String ai = rawInformation.substring(0, aiSize);
        int maxSize;
        if (rawInformation.length() < aiSize + variableFieldSize) {
            maxSize = rawInformation.length();
        }
        else {
            maxSize = aiSize + variableFieldSize;
        }
        final String field = rawInformation.substring(aiSize, maxSize);
        final String remaining = rawInformation.substring(maxSize);
        final String result = '(' + ai + ')' + field;
        final String parsedAI = parseFieldsInGeneralPurpose(remaining);
        return (parsedAI == null) ? result : (result + parsedAI);
    }
    
    static {
        VARIABLE_LENGTH = new Object();
        TWO_DIGIT_DATA_LENGTH = new Object[][] { { "00", 18 }, { "01", 14 }, { "02", 14 }, { "10", FieldParser.VARIABLE_LENGTH, 20 }, { "11", 6 }, { "12", 6 }, { "13", 6 }, { "15", 6 }, { "17", 6 }, { "20", 2 }, { "21", FieldParser.VARIABLE_LENGTH, 20 }, { "22", FieldParser.VARIABLE_LENGTH, 29 }, { "30", FieldParser.VARIABLE_LENGTH, 8 }, { "37", FieldParser.VARIABLE_LENGTH, 8 }, { "90", FieldParser.VARIABLE_LENGTH, 30 }, { "91", FieldParser.VARIABLE_LENGTH, 30 }, { "92", FieldParser.VARIABLE_LENGTH, 30 }, { "93", FieldParser.VARIABLE_LENGTH, 30 }, { "94", FieldParser.VARIABLE_LENGTH, 30 }, { "95", FieldParser.VARIABLE_LENGTH, 30 }, { "96", FieldParser.VARIABLE_LENGTH, 30 }, { "97", FieldParser.VARIABLE_LENGTH, 30 }, { "98", FieldParser.VARIABLE_LENGTH, 30 }, { "99", FieldParser.VARIABLE_LENGTH, 30 } };
        THREE_DIGIT_DATA_LENGTH = new Object[][] { { "240", FieldParser.VARIABLE_LENGTH, 30 }, { "241", FieldParser.VARIABLE_LENGTH, 30 }, { "242", FieldParser.VARIABLE_LENGTH, 6 }, { "250", FieldParser.VARIABLE_LENGTH, 30 }, { "251", FieldParser.VARIABLE_LENGTH, 30 }, { "253", FieldParser.VARIABLE_LENGTH, 17 }, { "254", FieldParser.VARIABLE_LENGTH, 20 }, { "400", FieldParser.VARIABLE_LENGTH, 30 }, { "401", FieldParser.VARIABLE_LENGTH, 30 }, { "402", 17 }, { "403", FieldParser.VARIABLE_LENGTH, 30 }, { "410", 13 }, { "411", 13 }, { "412", 13 }, { "413", 13 }, { "414", 13 }, { "420", FieldParser.VARIABLE_LENGTH, 20 }, { "421", FieldParser.VARIABLE_LENGTH, 15 }, { "422", 3 }, { "423", FieldParser.VARIABLE_LENGTH, 15 }, { "424", 3 }, { "425", 3 }, { "426", 3 } };
        THREE_DIGIT_PLUS_DIGIT_DATA_LENGTH = new Object[][] { { "310", 6 }, { "311", 6 }, { "312", 6 }, { "313", 6 }, { "314", 6 }, { "315", 6 }, { "316", 6 }, { "320", 6 }, { "321", 6 }, { "322", 6 }, { "323", 6 }, { "324", 6 }, { "325", 6 }, { "326", 6 }, { "327", 6 }, { "328", 6 }, { "329", 6 }, { "330", 6 }, { "331", 6 }, { "332", 6 }, { "333", 6 }, { "334", 6 }, { "335", 6 }, { "336", 6 }, { "340", 6 }, { "341", 6 }, { "342", 6 }, { "343", 6 }, { "344", 6 }, { "345", 6 }, { "346", 6 }, { "347", 6 }, { "348", 6 }, { "349", 6 }, { "350", 6 }, { "351", 6 }, { "352", 6 }, { "353", 6 }, { "354", 6 }, { "355", 6 }, { "356", 6 }, { "357", 6 }, { "360", 6 }, { "361", 6 }, { "362", 6 }, { "363", 6 }, { "364", 6 }, { "365", 6 }, { "366", 6 }, { "367", 6 }, { "368", 6 }, { "369", 6 }, { "390", FieldParser.VARIABLE_LENGTH, 15 }, { "391", FieldParser.VARIABLE_LENGTH, 18 }, { "392", FieldParser.VARIABLE_LENGTH, 15 }, { "393", FieldParser.VARIABLE_LENGTH, 18 }, { "703", FieldParser.VARIABLE_LENGTH, 30 } };
        FOUR_DIGIT_DATA_LENGTH = new Object[][] { { "7001", 13 }, { "7002", FieldParser.VARIABLE_LENGTH, 30 }, { "7003", 10 }, { "8001", 14 }, { "8002", FieldParser.VARIABLE_LENGTH, 20 }, { "8003", FieldParser.VARIABLE_LENGTH, 30 }, { "8004", FieldParser.VARIABLE_LENGTH, 30 }, { "8005", 6 }, { "8006", 18 }, { "8007", FieldParser.VARIABLE_LENGTH, 30 }, { "8008", FieldParser.VARIABLE_LENGTH, 12 }, { "8018", 18 }, { "8020", FieldParser.VARIABLE_LENGTH, 25 }, { "8100", 6 }, { "8101", 10 }, { "8102", 2 }, { "8110", FieldParser.VARIABLE_LENGTH, 30 } };
    }
}
