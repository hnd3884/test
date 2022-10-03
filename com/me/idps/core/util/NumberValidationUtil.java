package com.me.idps.core.util;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import java.util.logging.Logger;

public class NumberValidationUtil
{
    private static final int MIN_PHONE_NUMBER_LENGTH = 6;
    private static final int MAX_PHONE_NUMBER_LENGTH = 15;
    private static final String ERROR_MESSAGE = "Enter valid Phone Number";
    private static final Logger LOGGER;
    
    public static String[] validateAndParse(String unFormattedNumber) {
        if (unFormattedNumber == null) {
            throw new NullPointerException("Argument can't be null");
        }
        unFormattedNumber = unFormattedNumber.trim();
        if (unFormattedNumber.isEmpty()) {
            NumberValidationUtil.LOGGER.info("Empty string is provided for validation");
            throw new NumberFormatException("Enter valid Phone Number");
        }
        NumberValidationUtil.LOGGER.info("Unformatted number provided in format " + unFormattedNumber.replaceAll("\\d", "X"));
        final StringBuilder pattern = new StringBuilder();
        final String countryCode = computeCountryCode(unFormattedNumber, pattern);
        final String number = computePhoneNumber(unFormattedNumber, pattern.length(), pattern);
        if (number.length() < 6 || number.length() > 15) {
            NumberValidationUtil.LOGGER.info("Phone number violated 'E.164' international standard");
            throw new NumberFormatException("Enter valid Phone Number");
        }
        NumberValidationUtil.LOGGER.info("Phone Number is valid");
        return new String[] { toStandardFormat(countryCode, number), toUserFormat(countryCode + number, pattern.toString()) };
    }
    
    public static String[] validateWithAutoFillCountryCode(String unFormattedNumber) {
        if (unFormattedNumber == null) {
            throw new NullPointerException("Argument can't be null");
        }
        unFormattedNumber = unFormattedNumber.trim();
        if (unFormattedNumber.isEmpty()) {
            NumberValidationUtil.LOGGER.info("Empty string is provided for validation");
            throw new NumberFormatException("Enter valid Phone Number");
        }
        if (!unFormattedNumber.startsWith("+") && !unFormattedNumber.startsWith("(+")) {
            final String customerPhone = getCustomerPhone();
            if (!IdpsUtil.isStringValid(customerPhone)) {
                NumberValidationUtil.LOGGER.info("Country code can't be added");
                throw new NumberFormatException("Enter valid Phone Number");
            }
            final int index = customerPhone.indexOf(45);
            final String countryCode = customerPhone.substring(0, index);
            final String number = customerPhone.substring(index + 1);
            if (getNumberOfDigits(unFormattedNumber) != getNumberOfDigits(number)) {
                NumberValidationUtil.LOGGER.info("Number length mismatch, Country code can't be added");
                throw new NumberFormatException("Enter valid Phone Number");
            }
            unFormattedNumber = countryCode + "-" + unFormattedNumber;
            NumberValidationUtil.LOGGER.info("Country code of Customer gets added");
        }
        return validateAndParse(unFormattedNumber);
    }
    
    public static String getCustomerPhone() {
        String customerPhoneNumber = null;
        try {
            final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
            dirProdImplRequest.eventType = IdpEventConstants.GET_DEFAULT_CUST_PHONE_NUMBER;
            customerPhoneNumber = (String)DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
        }
        catch (final Exception ignored) {
            NumberValidationUtil.LOGGER.info("Exception while getting CustomerInfo");
        }
        return customerPhoneNumber;
    }
    
    public static String getCustomerCountryCode() {
        final String customerPhoneNumber = getCustomerPhone();
        if (!SyMUtil.isStringEmpty(customerPhoneNumber)) {
            return customerPhoneNumber.split("-")[0];
        }
        return null;
    }
    
    private static int getNumberOfDigits(String inputNumber) {
        inputNumber = inputNumber.replaceAll("[^\\d]+", "");
        return inputNumber.length();
    }
    
    private static String toStandardFormat(final String countryCode, final String number) {
        return "+" + countryCode + "-" + number;
    }
    
    private static String computeCountryCode(final String inputNumber, final StringBuilder pattern) {
        int index = 0;
        while (index < inputNumber.length()) {
            if (isASeparator(inputNumber.charAt(index))) {
                String countryCode = inputNumber.substring(0, index);
                if (countryCode.matches("\\+\\d{1,3}")) {
                    pattern.append('+');
                    countryCode = countryCode.substring(1);
                    pattern.append(countryCode.replaceAll("\\d", "X"));
                    pattern.append(inputNumber.charAt(index));
                    return countryCode;
                }
                if (countryCode.matches("\\(\\+\\d{1,3}\\)")) {
                    pattern.append('(');
                    pattern.append('+');
                    countryCode = countryCode.substring(2, countryCode.length() - 1);
                    pattern.append(countryCode.replaceAll("\\d", "X"));
                    pattern.append(')');
                    pattern.append(inputNumber.charAt(index));
                    return countryCode;
                }
                break;
            }
            else {
                ++index;
            }
        }
        NumberValidationUtil.LOGGER.info("Country code can't be identified from the phone number");
        throw new NumberFormatException("Enter valid Phone Number");
    }
    
    private static String computePhoneNumber(final String inputNumber, final int startIndex, final StringBuilder pattern) {
        final StringBuilder builder = new StringBuilder();
        int expressionValid = 0;
        for (int length = inputNumber.length(), i = startIndex; i < length; ++i) {
            final char c = inputNumber.charAt(i);
            if (isNumericCharacter(c)) {
                builder.append(c);
                pattern.append('X');
            }
            else {
                if (!isAValidDelimiter(c)) {
                    NumberValidationUtil.LOGGER.info("Invalid character " + c + " found at index " + i + " in phone number");
                    throw new NumberFormatException("Enter valid Phone Number");
                }
                if (c == '(') {
                    ++expressionValid;
                }
                else if (c == ')') {
                    if (expressionValid == 0) {
                        NumberValidationUtil.LOGGER.info("Unexpected ) at index " + i + " in phone number");
                        throw new NumberFormatException("Enter valid Phone Number");
                    }
                    --expressionValid;
                }
                pattern.append(c);
            }
        }
        if (expressionValid > 0) {
            NumberValidationUtil.LOGGER.info("Missing ) in phone number");
            throw new NumberFormatException("Enter valid Phone Number");
        }
        return builder.toString();
    }
    
    private static boolean isAValidDelimiter(final char c) {
        switch (c) {
            case ' ': {
                break;
            }
            case '(': {
                break;
            }
            case ')': {
                break;
            }
            case '-': {
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isNumericCharacter(final char c) {
        return c > '/' && c < ':';
    }
    
    private static String toUserFormat(final String standardNumber, final String displayFormat) {
        final StringBuilder builder = new StringBuilder();
        final char[] displayFormatArray = displayFormat.toCharArray();
        int index = 0;
        for (final char digit : displayFormatArray) {
            if (digit == 'X' || digit == 'x') {
                if (index >= standardNumber.length()) {
                    throw new IllegalArgumentException("Format Mismatch");
                }
                builder.append(standardNumber.charAt(index++));
            }
            else {
                builder.append(digit);
            }
        }
        if (index != standardNumber.length()) {
            throw new IllegalArgumentException("Format Mismatch");
        }
        return builder.toString();
    }
    
    private static boolean isASeparator(final char c) {
        return c == ' ' || c == '-';
    }
    
    static {
        LOGGER = Logger.getLogger(NumberValidationUtil.class.getName());
    }
}
