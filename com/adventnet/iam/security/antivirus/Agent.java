package com.adventnet.iam.security.antivirus;

import java.io.InputStream;
import com.adventnet.iam.security.SecurityFilterProperties;
import java.io.File;

@Deprecated
public class Agent
{
    @Deprecated
    public static boolean scan(final File file, final SecurityFilterProperties config) {
        return com.adventnet.iam.security.antivirus.clamav.Agent.scanForFile(file, config.getClamAvConfig()).status() == AVScanResult.Status.VIRUS_DETECTED;
    }
    
    @Deprecated
    public static boolean scanForFileByte(final byte[] bytes, final SecurityFilterProperties config) {
        return com.adventnet.iam.security.antivirus.clamav.Agent.scanForByte(bytes, config.getClamAvConfig()).status() == AVScanResult.Status.VIRUS_DETECTED;
    }
    
    @Deprecated
    public static boolean nonPersistentScan(final File file, final SecurityFilterProperties config) {
        return com.adventnet.iam.security.antivirus.clamav.Agent.nonPersistentScanForFile(file, config.getClamAvConfig()).status() == AVScanResult.Status.VIRUS_DETECTED;
    }
    
    @Deprecated
    public static boolean nonPersistentScanForByte(final byte[] bytes, final SecurityFilterProperties config) {
        return com.adventnet.iam.security.antivirus.clamav.Agent.nonPersistentScanForByte(bytes, config.getClamAvConfig()).status() == AVScanResult.Status.VIRUS_DETECTED;
    }
    
    @Deprecated
    public static boolean nonPersistentStreamScan(final InputStream fileStream, final SecurityFilterProperties config) {
        return com.adventnet.iam.security.antivirus.clamav.Agent.nonPersistentStreamScan(fileStream, config.getClamAvConfig()).status() == AVScanResult.Status.VIRUS_DETECTED;
    }
}
