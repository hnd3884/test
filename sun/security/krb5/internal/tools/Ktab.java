package sun.security.krb5.internal.tools;

import sun.security.krb5.internal.ktab.KeyTabEntry;
import sun.security.krb5.internal.crypto.EType;
import java.util.Date;
import java.text.DateFormat;
import java.util.Arrays;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import java.util.Locale;
import sun.security.krb5.internal.ktab.KeyTab;

public class Ktab
{
    KeyTab table;
    char action;
    String name;
    String principal;
    boolean showEType;
    boolean showTime;
    int etype;
    char[] password;
    boolean forced;
    boolean append;
    int vDel;
    int vAdd;
    
    public Ktab() {
        this.etype = -1;
        this.password = null;
        this.forced = false;
        this.append = false;
        this.vDel = -1;
        this.vAdd = -1;
    }
    
    public static void main(final String[] array) {
        final Ktab ktab = new Ktab();
        if (array.length == 1 && array[0].equalsIgnoreCase("-help")) {
            ktab.printHelp();
            return;
        }
        if (array == null || array.length == 0) {
            ktab.action = 'l';
        }
        else {
            ktab.processArgs(array);
        }
        ktab.table = KeyTab.getInstance(ktab.name);
        if (ktab.table.isMissing() && ktab.action != 'a') {
            if (ktab.name == null) {
                System.out.println("No default key table exists.");
            }
            else {
                System.out.println("Key table " + ktab.name + " does not exist.");
            }
            System.exit(-1);
        }
        if (!ktab.table.isValid()) {
            if (ktab.name == null) {
                System.out.println("The format of the default key table  is incorrect.");
            }
            else {
                System.out.println("The format of key table " + ktab.name + " is incorrect.");
            }
            System.exit(-1);
        }
        switch (ktab.action) {
            case 'l': {
                ktab.listKt();
                break;
            }
            case 'a': {
                ktab.addEntry();
                break;
            }
            case 'd': {
                ktab.deleteEntry();
                break;
            }
            default: {
                ktab.error("A command must be provided");
                break;
            }
        }
    }
    
    void processArgs(final String[] array) {
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            if (array[i].startsWith("-")) {
                final String lowerCase = array[i].toLowerCase(Locale.US);
                switch (lowerCase) {
                    case "-l": {
                        this.action = 'l';
                        break;
                    }
                    case "-a": {
                        this.action = 'a';
                        if (++i >= array.length || array[i].startsWith("-")) {
                            this.error("A principal name must be specified after -a");
                        }
                        this.principal = array[i];
                        break;
                    }
                    case "-d": {
                        this.action = 'd';
                        if (++i >= array.length || array[i].startsWith("-")) {
                            this.error("A principal name must be specified after -d");
                        }
                        this.principal = array[i];
                        break;
                    }
                    case "-e": {
                        if (this.action == 'l') {
                            this.showEType = true;
                            break;
                        }
                        if (this.action == 'd') {
                            if (++i >= array.length || array[i].startsWith("-")) {
                                this.error("An etype must be specified after -e");
                            }
                            try {
                                this.etype = Integer.parseInt(array[i]);
                                if (this.etype <= 0) {
                                    throw new NumberFormatException();
                                }
                            }
                            catch (final NumberFormatException ex) {
                                this.error(array[i] + " is not a valid etype");
                            }
                            break;
                        }
                        this.error(array[i] + " is not valid after -" + this.action);
                        break;
                    }
                    case "-n": {
                        if (++i >= array.length || array[i].startsWith("-")) {
                            this.error("A KVNO must be specified after -n");
                        }
                        try {
                            this.vAdd = Integer.parseInt(array[i]);
                            if (this.vAdd < 0) {
                                throw new NumberFormatException();
                            }
                        }
                        catch (final NumberFormatException ex2) {
                            this.error(array[i] + " is not a valid KVNO");
                        }
                        break;
                    }
                    case "-k": {
                        if (++i >= array.length || array[i].startsWith("-")) {
                            this.error("A keytab name must be specified after -k");
                        }
                        if (array[i].length() >= 5 && array[i].substring(0, 5).equalsIgnoreCase("FILE:")) {
                            this.name = array[i].substring(5);
                            break;
                        }
                        this.name = array[i];
                        break;
                    }
                    case "-t": {
                        this.showTime = true;
                        break;
                    }
                    case "-f": {
                        this.forced = true;
                        break;
                    }
                    case "-append": {
                        this.append = true;
                        break;
                    }
                    default: {
                        this.error("Unknown command: " + array[i]);
                        break;
                    }
                }
            }
            else {
                if (n != 0) {
                    this.error("Useless extra argument " + array[i]);
                }
                if (this.action == 'a') {
                    this.password = array[i].toCharArray();
                }
                else if (this.action == 'd') {
                    final String s = array[i];
                    switch (s) {
                        case "all": {
                            this.vDel = -1;
                            break;
                        }
                        case "old": {
                            this.vDel = -2;
                            break;
                        }
                        default: {
                            try {
                                this.vDel = Integer.parseInt(array[i]);
                                if (this.vDel < 0) {
                                    throw new NumberFormatException();
                                }
                            }
                            catch (final NumberFormatException ex3) {
                                this.error(array[i] + " is not a valid KVNO");
                            }
                            break;
                        }
                    }
                }
                else {
                    this.error("Useless extra argument " + array[i]);
                }
                n = 1;
            }
        }
    }
    
    void addEntry() {
        PrincipalName principalName = null;
        try {
            principalName = new PrincipalName(this.principal);
        }
        catch (final KrbException ex) {
            System.err.println("Failed to add " + this.principal + " to keytab.");
            ex.printStackTrace();
            System.exit(-1);
        }
        if (this.password == null) {
            try {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Password for " + principalName.toString() + ":");
                System.out.flush();
                this.password = bufferedReader.readLine().toCharArray();
            }
            catch (final IOException ex2) {
                System.err.println("Failed to read the password.");
                ex2.printStackTrace();
                System.exit(-1);
            }
        }
        try {
            this.table.addEntry(principalName, this.password, this.vAdd, this.append);
            Arrays.fill(this.password, '0');
            this.table.save();
            System.out.println("Done!");
            System.out.println("Service key for " + this.principal + " is saved in " + this.table.tabName());
        }
        catch (final KrbException ex3) {
            System.err.println("Failed to add " + this.principal + " to keytab.");
            ex3.printStackTrace();
            System.exit(-1);
        }
        catch (final IOException ex4) {
            System.err.println("Failed to save new entry.");
            ex4.printStackTrace();
            System.exit(-1);
        }
    }
    
    void listKt() {
        System.out.println("Keytab name: " + this.table.tabName());
        final KeyTabEntry[] entries = this.table.getEntries();
        if (entries != null && entries.length > 0) {
            final String[][] array = new String[entries.length + 1][this.showTime ? 3 : 2];
            int n = 0;
            array[0][n++] = "KVNO";
            if (this.showTime) {
                array[0][n++] = "Timestamp";
            }
            array[0][n++] = "Principal";
            for (int i = 0; i < entries.length; ++i) {
                n = 0;
                array[i + 1][n++] = entries[i].getKey().getKeyVersionNumber().toString();
                if (this.showTime) {
                    array[i + 1][n++] = DateFormat.getDateTimeInstance(3, 3).format(new Date(entries[i].getTimeStamp().getTime()));
                }
                final String string = entries[i].getService().toString();
                if (this.showEType) {
                    final int eType = entries[i].getKey().getEType();
                    array[i + 1][n++] = string + " (" + eType + ":" + EType.toString(eType) + ")";
                }
                else {
                    array[i + 1][n++] = string;
                }
            }
            final int[] array2 = new int[n];
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k <= entries.length; ++k) {
                    if (array[k][j].length() > array2[j]) {
                        array2[j] = array[k][j].length();
                    }
                }
                if (j != 0) {
                    array2[j] = -array2[j];
                }
            }
            for (int l = 0; l < n; ++l) {
                System.out.printf("%" + array2[l] + "s ", array[0][l]);
            }
            System.out.println();
            for (int n2 = 0; n2 < n; ++n2) {
                for (int n3 = 0; n3 < Math.abs(array2[n2]); ++n3) {
                    System.out.print("-");
                }
                System.out.print(" ");
            }
            System.out.println();
            for (int n4 = 0; n4 < entries.length; ++n4) {
                for (int n5 = 0; n5 < n; ++n5) {
                    System.out.printf("%" + array2[n5] + "s ", array[n4 + 1][n5]);
                }
                System.out.println();
            }
        }
        else {
            System.out.println("0 entry.");
        }
    }
    
    void deleteEntry() {
        PrincipalName principalName = null;
        try {
            principalName = new PrincipalName(this.principal);
            if (!this.forced) {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Are you sure you want to delete service key(s) for " + principalName.toString() + " (" + ((this.etype == -1) ? "all etypes" : ("etype=" + this.etype)) + ", " + ((this.vDel == -1) ? "all kvno" : ((this.vDel == -2) ? "old kvno" : ("kvno=" + this.vDel))) + ") in " + this.table.tabName() + "? (Y/[N]): ");
                System.out.flush();
                final String line = bufferedReader.readLine();
                if (!line.equalsIgnoreCase("Y")) {
                    if (!line.equalsIgnoreCase("Yes")) {
                        System.exit(0);
                    }
                }
            }
        }
        catch (final KrbException ex) {
            System.err.println("Error occurred while deleting the entry. Deletion failed.");
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (final IOException ex2) {
            System.err.println("Error occurred while deleting the entry.  Deletion failed.");
            ex2.printStackTrace();
            System.exit(-1);
        }
        final int deleteEntries = this.table.deleteEntries(principalName, this.etype, this.vDel);
        if (deleteEntries == 0) {
            System.err.println("No matched entry in the keytab. Deletion fails.");
            System.exit(-1);
        }
        else {
            try {
                this.table.save();
            }
            catch (final IOException ex3) {
                System.err.println("Error occurs while saving the keytab. Deletion fails.");
                ex3.printStackTrace();
                System.exit(-1);
            }
            System.out.println("Done! " + deleteEntries + " entries removed.");
        }
    }
    
    void error(final String... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            System.out.println("Error: " + array[i] + ".");
        }
        this.printHelp();
        System.exit(-1);
    }
    
    void printHelp() {
        System.out.println("\nUsage: ktab <commands> <options>");
        System.out.println();
        System.out.println("Available commands:");
        System.out.println();
        System.out.println("-l [-e] [-t]\n    list the keytab name and entries. -e with etype, -t with timestamp.");
        System.out.println("-a <principal name> [<password>] [-n <kvno>] [-append]\n    add new key entries to the keytab for the given principal name with\n    optional <password>. If a <kvno> is specified, new keys' Key Version\n    Numbers equal to the value, otherwise, automatically incrementing\n    the Key Version Numbers. If -append is specified, new keys are\n    appended to the keytab, otherwise, old keys for the\n    same principal are removed.");
        System.out.println("-d <principal name> [-f] [-e <etype>] [<kvno> | all | old]\n    delete key entries from the keytab for the specified principal. If\n    <kvno> is specified, delete keys whose Key Version Numbers match\n    kvno. If \"all\" is specified, delete all keys. If \"old\" is specified,\n    delete all keys except those with the highest kvno. Default action\n    is \"all\". If <etype> is specified, only keys of this encryption type\n    are deleted. <etype> should be specified as the numberic value etype\n    defined in RFC 3961, section 8. A prompt to confirm the deletion is\n    displayed unless -f is specified.");
        System.out.println();
        System.out.println("Common option(s):");
        System.out.println();
        System.out.println("-k <keytab name>\n    specify keytab name and path with prefix FILE:");
    }
}
