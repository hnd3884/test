package com.me.mdm.onpremise.server.common;

import java.io.FileWriter;
import java.io.FileReader;
import com.adventnet.tools.prevalent.ConsoleOut;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.adventnet.persistence.PersistenceUtil;
import java.io.File;

public class SuperUserPassChanger
{
    public static void main(final String[] args) {
        try {
            final String changeDBPasswordBat = System.getProperty("server.home") + File.separator + "bin" + File.separator + "changeDBPassword.bat";
            final String newPassword = PersistenceUtil.generateRandomPassword();
            final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { changeDBPasswordBat, "-U", "postgres", "-p", "Stonebraker", "-P", newPassword });
            processBuilder.redirectErrorStream(true);
            final Process process = processBuilder.start();
            try (final BufferedReader ipBuf = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = null;
                while ((line = ipBuf.readLine()) != null) {
                    ConsoleOut.println(line);
                }
            }
            if (process.waitFor() == 0) {
                ConsoleOut.println("Super User Password Changed");
                changeSuperUserPWDinFile(newPassword);
            }
            else {
                ConsoleOut.println("Super User Password Not Changed");
            }
        }
        catch (final Exception ex) {
            ConsoleOut.println("Exception occurred while changing the password: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void changeSuperUserPWDinFile(final String newPassword) {
        final String dbParamsFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        final StringBuilder fileString = new StringBuilder();
        try (final FileReader fileReader = new FileReader(dbParamsFilePath);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                if (line.trim().equalsIgnoreCase("superuser_pass=Stonebraker")) {
                    line = "superuser_pass=" + newPassword;
                }
                fileString.append(line).append("\n");
            }
        }
        catch (final Exception ex) {
            ConsoleOut.println("Exception in reading DB params file: " + ex);
        }
        try (final FileWriter fileWriter = new FileWriter(dbParamsFilePath)) {
            fileWriter.write(fileString.toString());
        }
        catch (final Exception ex) {
            ConsoleOut.println("Exception in writing DB params file: " + ex);
        }
    }
}
