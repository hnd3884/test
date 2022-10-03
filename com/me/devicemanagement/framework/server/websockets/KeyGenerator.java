package com.me.devicemanagement.framework.server.websockets;

import java.security.SecureRandom;

public class KeyGenerator
{
    public static synchronized String generateSessionId(final int toolId) {
        String time = String.valueOf(System.currentTimeMillis());
        time = time.substring(time.length() - 12);
        final StringBuilder sessionId = new StringBuilder();
        sessionId.append(toolId);
        sessionId.append(time);
        return sessionId.toString();
    }
    
    public static String generateClientId(final int toolId) {
        final String randomId = generateRandomID();
        final StringBuilder clientId = new StringBuilder();
        clientId.append(toolId);
        clientId.append(randomId);
        return clientId.toString();
    }
    
    public static synchronized String generateRandomID() {
        final SecureRandom random = new SecureRandom();
        final int low = 100000;
        final int high = 999999;
        final long diff = high - (long)low + 1L;
        final long fraction = (long)(diff * random.nextDouble());
        final long randomInt = fraction * low;
        String randomID = String.valueOf(randomInt);
        randomID = randomID.substring(0, 6);
        return randomID;
    }
}
