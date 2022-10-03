package com.me.devicemanagement.framework.server.util;

import com.me.devicemanagement.framework.server.exception.SyMException;
import biz.source_code.base64Coder.Base64Coder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Encoder
{
    private static Logger logger;
    
    public static String convertToNewBase(final String string) throws SyMException {
        Encoder.logger.log(Level.FINEST, "Encryption invoked");
        if (string == null) {
            return null;
        }
        String EncodedString = null;
        EncodedString = Base64Coder.encodeString(string);
        return EncodedString;
    }
    
    public static String convertFromBase(final String encodedString) throws Exception {
        Encoder.logger.log(Level.FINE, "Deconvertor called.");
        if (encodedString == null) {
            return null;
        }
        String DecodedString = null;
        try {
            DecodedString = Base64Coder.decodeString(encodedString);
        }
        catch (final IllegalArgumentException ex) {
            Encoder.logger.log(Level.WARNING, "Base64Coder.decodeString returned IllegalArugmentException " + ex);
            DecodedString = null;
        }
        return DecodedString;
    }
    
    private static String baseDeconvertor(String input) throws Exception {
        final String[] base = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        base[43] = "I";
        base[44] = "J";
        base[45] = "K";
        base[46] = "L";
        base[47] = "M";
        base[48] = "N";
        base[49] = "O";
        base[50] = "P";
        base[51] = "Q";
        base[52] = "R";
        base[53] = "S";
        base[54] = "T";
        base[55] = "U";
        base[56] = "V";
        base[57] = "W";
        base[58] = "X";
        base[59] = "Y";
        int Zpos = 0;
        while ((Zpos = input.indexOf("Z")) != -1) {
            final String temp1 = input.substring(0, Zpos);
            final String temp2 = input.substring(Zpos + 1);
            input = new StringBuffer(temp1).append("000").append(temp2).toString();
        }
        StringBuffer answer = new StringBuffer();
        int k = 0;
        long reminder = 0L;
        for (int co = input.length() / 6; k < co; ++k) {
            final String part = input.substring(6 * k, 6 * k + 6);
            final StringBuffer partnum = new StringBuffer();
            boolean startnum = false;
            for (int i = 0; i < 5; ++i) {
                boolean isthere = false;
                int pos = 0;
                while (!isthere) {
                    if (part.substring(i, i + 1).equals(base[pos])) {
                        isthere = true;
                        partnum.append(pos);
                        if (pos == 0) {
                            if (!startnum) {
                                answer.append("0");
                            }
                        }
                        else {
                            startnum = true;
                        }
                    }
                    ++pos;
                }
            }
            boolean isthere2 = false;
            int pos2 = 0;
            while (!isthere2) {
                if (part.substring(5).equals(base[pos2])) {
                    isthere2 = true;
                    reminder = pos2;
                }
                ++pos2;
            }
            if (partnum.toString().equals("00000")) {
                if (reminder != 0L) {
                    final String tempo = String.valueOf(reminder);
                    final String temp3 = answer.toString().substring(0, answer.length() - tempo.length());
                    answer = new StringBuffer(temp3).append(tempo);
                }
            }
            else {
                answer.append(Long.parseLong(partnum.toString()) * 60L + reminder);
            }
        }
        if (input.length() % 6 != 0) {
            final String end = input.substring(6 * k);
            final StringBuffer partnum = new StringBuffer();
            if (end.length() > 1) {
                int j = 0;
                boolean startnum2 = false;
                for (j = 0; j < end.length() - 1; ++j) {
                    boolean isthere = false;
                    int pos = 0;
                    while (!isthere) {
                        if (end.substring(j, j + 1).equals(base[pos])) {
                            isthere = true;
                            partnum.append(pos);
                            if (pos == 0) {
                                if (!startnum2) {
                                    answer.append("0");
                                }
                            }
                            else {
                                startnum2 = true;
                            }
                        }
                        ++pos;
                    }
                }
                boolean isthere = false;
                int pos = 0;
                while (!isthere) {
                    if (end.substring(j).equals(base[pos])) {
                        isthere = true;
                        reminder = pos;
                    }
                    ++pos;
                }
                answer.append(Long.parseLong(partnum.toString()) * 60L + reminder);
            }
            else {
                boolean isthere3 = false;
                int pos3 = 0;
                while (!isthere3) {
                    if (end.equals(base[pos3])) {
                        isthere3 = true;
                        reminder = pos3;
                    }
                    ++pos3;
                }
                answer.append(reminder);
            }
        }
        return answer.toString();
    }
    
    public static void main(final String[] args) throws Exception {
        Encoder.logger.log(Level.INFO, "To encode and decode");
        if (args.length == 0) {
            Encoder.logger.log(Level.INFO, "Please provide string to convert.");
            return;
        }
        final String encode = convertToNewBase(args[0]);
        Encoder.logger.log(Level.INFO, "Encoded string : " + encode);
        final String decode = convertFromBase(encode);
        Encoder.logger.log(Level.INFO, "Decoded string : " + decode);
    }
    
    static {
        Encoder.logger = Logger.getLogger(Encoder.class.getName());
    }
}
