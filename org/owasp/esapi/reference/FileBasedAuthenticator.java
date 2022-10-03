package org.owasp.esapi.reference;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Date;
import org.owasp.esapi.Randomizer;
import org.owasp.esapi.StringUtilities;
import org.owasp.esapi.EncoderConstants;
import org.owasp.esapi.errors.EncryptionException;
import org.owasp.esapi.errors.AuthenticationException;
import org.owasp.esapi.errors.AuthenticationCredentialsException;
import org.owasp.esapi.errors.AuthenticationAccountsException;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import org.owasp.esapi.ESAPI;
import java.util.List;
import org.owasp.esapi.User;
import java.util.Map;
import java.io.File;
import org.owasp.esapi.Logger;
import org.owasp.esapi.Authenticator;

public class FileBasedAuthenticator extends AbstractAuthenticator
{
    private static volatile Authenticator singletonInstance;
    private final Logger logger;
    private File userDB;
    private long checkInterval;
    private long lastModified;
    private long lastChecked;
    private static final int MAX_ACCOUNT_NAME_LENGTH = 250;
    private Map<Long, User> userMap;
    private Map<User, List<String>> passwordMap;
    
    public static Authenticator getInstance() {
        if (FileBasedAuthenticator.singletonInstance == null) {
            synchronized (FileBasedAuthenticator.class) {
                if (FileBasedAuthenticator.singletonInstance == null) {
                    FileBasedAuthenticator.singletonInstance = new FileBasedAuthenticator();
                }
            }
        }
        return FileBasedAuthenticator.singletonInstance;
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: Authenticator accountname password role");
            return;
        }
        final FileBasedAuthenticator auth = new FileBasedAuthenticator();
        final String accountName = args[0].toLowerCase();
        final String password = args[1];
        final String role = args[2];
        DefaultUser user = (DefaultUser)auth.getUser(args[0]);
        if (user == null) {
            user = new DefaultUser(accountName);
            final String newHash = auth.hashPassword(password, accountName);
            auth.setHashedPassword(user, newHash);
            user.addRole(role);
            user.enable();
            user.unlock();
            auth.userMap.put(user.getAccountId(), user);
            System.out.println("New user created: " + accountName);
            auth.saveUsers();
            System.out.println("User account " + user.getAccountName() + " updated");
        }
        else {
            System.err.println("User account " + user.getAccountName() + " already exists!");
        }
    }
    
    private void setHashedPassword(final User user, final String hash) {
        final List<String> hashes = this.getAllHashedPasswords(user, true);
        hashes.add(0, hash);
        if (hashes.size() > ESAPI.securityConfiguration().getMaxOldPasswordHashes()) {
            hashes.remove(hashes.size() - 1);
        }
        this.logger.info(Logger.SECURITY_SUCCESS, "New hashed password stored for " + user.getAccountName());
    }
    
    String getHashedPassword(final User user) {
        final List hashes = this.getAllHashedPasswords(user, false);
        return hashes.get(0);
    }
    
    void setOldPasswordHashes(final User user, final List<String> oldHashes) {
        final List<String> hashes = this.getAllHashedPasswords(user, true);
        if (hashes.size() > 1) {
            hashes.removeAll(hashes.subList(1, hashes.size()));
        }
        hashes.addAll(oldHashes);
    }
    
    List<String> getAllHashedPasswords(final User user, final boolean create) {
        List<String> hashes = this.passwordMap.get(user);
        if (hashes != null) {
            return hashes;
        }
        if (create) {
            hashes = new ArrayList<String>();
            this.passwordMap.put(user, hashes);
            return hashes;
        }
        throw new RuntimeException("No hashes found for " + user.getAccountName() + ". Is User.hashcode() and equals() implemented correctly?");
    }
    
    List<String> getOldPasswordHashes(final User user) {
        final List<String> hashes = this.getAllHashedPasswords(user, false);
        if (hashes.size() > 1) {
            return Collections.unmodifiableList((List<? extends String>)hashes.subList(1, hashes.size()));
        }
        return Collections.emptyList();
    }
    
    private FileBasedAuthenticator() {
        this.logger = ESAPI.getLogger("Authenticator");
        this.userDB = null;
        this.checkInterval = 60000L;
        this.lastModified = 0L;
        this.lastChecked = 0L;
        this.userMap = new HashMap<Long, User>();
        this.passwordMap = new Hashtable<User, List<String>>();
    }
    
    @Override
    public synchronized User createUser(final String accountName, final String password1, final String password2) throws AuthenticationException {
        this.loadUsersIfNecessary();
        if (accountName == null) {
            throw new AuthenticationAccountsException("Account creation failed", "Attempt to create user with null accountName");
        }
        if (this.getUser(accountName) != null) {
            throw new AuthenticationAccountsException("Account creation failed", "Duplicate user creation denied for " + accountName);
        }
        this.verifyAccountNameStrength(accountName);
        if (password1 == null) {
            throw new AuthenticationCredentialsException("Invalid account name", "Attempt to create account " + accountName + " with a null password");
        }
        final DefaultUser user = new DefaultUser(accountName);
        this.verifyPasswordStrength(null, password1, user);
        if (!password1.equals(password2)) {
            throw new AuthenticationCredentialsException("Passwords do not match", "Passwords for " + accountName + " do not match");
        }
        try {
            this.setHashedPassword(user, this.hashPassword(password1, accountName));
        }
        catch (final EncryptionException ee) {
            throw new AuthenticationException("Internal error", "Error hashing password for " + accountName, ee);
        }
        this.userMap.put(user.getAccountId(), user);
        this.logger.info(Logger.SECURITY_SUCCESS, "New user created: " + accountName);
        this.saveUsers();
        return user;
    }
    
    @Override
    public String generateStrongPassword() {
        return this.generateStrongPassword("");
    }
    
    private String generateStrongPassword(final String oldPassword) {
        final Randomizer r = ESAPI.randomizer();
        final int letters = r.getRandomInteger(4, 6);
        final int digits = 7 - letters;
        final String passLetters = r.getRandomString(letters, EncoderConstants.CHAR_PASSWORD_LETTERS);
        final String passDigits = r.getRandomString(digits, EncoderConstants.CHAR_PASSWORD_DIGITS);
        final String passSpecial = r.getRandomString(1, EncoderConstants.CHAR_PASSWORD_SPECIALS);
        final String newPassword = passLetters + passSpecial + passDigits;
        if (StringUtilities.getLevenshteinDistance(oldPassword, newPassword) > 5) {
            return newPassword;
        }
        return this.generateStrongPassword(oldPassword);
    }
    
    @Override
    public void changePassword(final User user, final String currentPassword, final String newPassword, final String newPassword2) throws AuthenticationException {
        final String accountName = user.getAccountName();
        try {
            final String currentHash = this.getHashedPassword(user);
            final String verifyHash = this.hashPassword(currentPassword, accountName);
            if (!currentHash.equals(verifyHash)) {
                throw new AuthenticationCredentialsException("Password change failed", "Authentication failed for password change on user: " + accountName);
            }
            if (newPassword == null || newPassword2 == null || !newPassword.equals(newPassword2)) {
                throw new AuthenticationCredentialsException("Password change failed", "Passwords do not match for password change on user: " + accountName);
            }
            this.verifyPasswordStrength(currentPassword, newPassword, user);
            user.setLastPasswordChangeTime(new Date());
            final String newHash = this.hashPassword(newPassword, accountName);
            if (this.getOldPasswordHashes(user).contains(newHash)) {
                throw new AuthenticationCredentialsException("Password change failed", "Password change matches a recent password for user: " + accountName);
            }
            this.setHashedPassword(user, newHash);
            this.logger.info(Logger.SECURITY_SUCCESS, "Password changed for user: " + accountName);
            this.saveUsers();
        }
        catch (final EncryptionException ee) {
            throw new AuthenticationException("Password change failed", "Encryption exception changing password for " + accountName, ee);
        }
    }
    
    @Override
    public boolean verifyPassword(final User user, final String password) {
        final String accountName = user.getAccountName();
        try {
            final String hash = this.hashPassword(password, accountName);
            final String currentHash = this.getHashedPassword(user);
            if (hash.equals(currentHash)) {
                user.setLastLoginTime(new Date());
                ((DefaultUser)user).setFailedLoginCount(0);
                this.logger.info(Logger.SECURITY_SUCCESS, "Password verified for " + accountName);
                return true;
            }
        }
        catch (final EncryptionException e) {
            this.logger.fatal(Logger.SECURITY_FAILURE, "Encryption error verifying password for " + accountName);
        }
        this.logger.fatal(Logger.SECURITY_FAILURE, "Password verification failed for " + accountName);
        return false;
    }
    
    @Override
    public String generateStrongPassword(final User user, final String oldPassword) {
        final String newPassword = this.generateStrongPassword(oldPassword);
        if (newPassword != null) {
            this.logger.info(Logger.SECURITY_SUCCESS, "Generated strong password for " + user.getAccountName());
        }
        return newPassword;
    }
    
    @Override
    public synchronized User getUser(final long accountId) {
        if (accountId == 0L) {
            return User.ANONYMOUS;
        }
        this.loadUsersIfNecessary();
        return this.userMap.get(accountId);
    }
    
    @Override
    public synchronized User getUser(final String accountName) {
        if (accountName == null) {
            return User.ANONYMOUS;
        }
        this.loadUsersIfNecessary();
        for (final User u : this.userMap.values()) {
            if (u.getAccountName().equalsIgnoreCase(accountName)) {
                return u;
            }
        }
        return null;
    }
    
    @Override
    public synchronized Set getUserNames() {
        this.loadUsersIfNecessary();
        final HashSet<String> results = new HashSet<String>();
        for (final User u : this.userMap.values()) {
            results.add(u.getAccountName());
        }
        return results;
    }
    
    @Override
    public String hashPassword(final String password, final String accountName) throws EncryptionException {
        final String salt = accountName.toLowerCase();
        return ESAPI.encryptor().hash(password, salt);
    }
    
    protected void loadUsersIfNecessary() {
        if (this.userDB == null) {
            this.userDB = ESAPI.securityConfiguration().getResourceFile("users.txt");
        }
        if (this.userDB == null) {
            this.userDB = new File(System.getProperty("user.home") + "/.esapi", "users.txt");
            try {
                if (!this.userDB.createNewFile()) {
                    throw new IOException("Unable to create the user file");
                }
                this.logger.warning(Logger.SECURITY_SUCCESS, "Created " + this.userDB.getAbsolutePath());
            }
            catch (final IOException e) {
                this.logger.fatal(Logger.SECURITY_FAILURE, "Could not create " + this.userDB.getAbsolutePath(), e);
            }
        }
        final long now = System.currentTimeMillis();
        if (now - this.lastChecked < this.checkInterval) {
            return;
        }
        this.lastChecked = now;
        if (this.lastModified == this.userDB.lastModified()) {
            return;
        }
        this.loadUsersImmediately();
    }
    
    protected void loadUsersImmediately() {
        synchronized (this) {
            this.logger.trace(Logger.SECURITY_SUCCESS, "Loading users from " + this.userDB.getAbsolutePath(), null);
            BufferedReader reader = null;
            try {
                final HashMap<Long, User> map = new HashMap<Long, User>();
                reader = new BufferedReader(new FileReader(this.userDB));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.length() > 0 && line.charAt(0) != '#') {
                        final DefaultUser user = this.createUser(line);
                        if (map.containsKey(new Long(user.getAccountId()))) {
                            this.logger.fatal(Logger.SECURITY_FAILURE, "Problem in user file. Skipping duplicate user: " + user, null);
                        }
                        map.put(user.getAccountId(), user);
                    }
                }
                this.userMap = map;
                this.lastModified = System.currentTimeMillis();
                this.logger.trace(Logger.SECURITY_SUCCESS, "User file reloaded: " + map.size(), null);
            }
            catch (final Exception e) {
                this.logger.fatal(Logger.SECURITY_FAILURE, "Failure loading user file: " + this.userDB.getAbsolutePath(), e);
                try {
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (final IOException e2) {
                    this.logger.fatal(Logger.SECURITY_FAILURE, "Failure closing user file: " + this.userDB.getAbsolutePath(), e2);
                }
            }
            finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (final IOException e3) {
                    this.logger.fatal(Logger.SECURITY_FAILURE, "Failure closing user file: " + this.userDB.getAbsolutePath(), e3);
                }
            }
        }
    }
    
    private DefaultUser createUser(final String line) throws AuthenticationException {
        final String[] parts = line.split(" *\\| *");
        final String accountIdString = parts[0];
        final long accountId = Long.parseLong(accountIdString);
        final String accountName = parts[1];
        this.verifyAccountNameStrength(accountName);
        final DefaultUser user = new DefaultUser(accountName);
        user.accountId = accountId;
        final String password = parts[2];
        this.verifyPasswordStrength(null, password, user);
        this.setHashedPassword(user, password);
        final String[] arr$;
        final String[] roles = arr$ = parts[3].toLowerCase().split(" *, *");
        for (final String role : arr$) {
            if (!"".equals(role)) {
                user.addRole(role);
            }
        }
        if (!"unlocked".equalsIgnoreCase(parts[4])) {
            user.lock();
        }
        if ("enabled".equalsIgnoreCase(parts[5])) {
            user.enable();
        }
        else {
            user.disable();
        }
        user.resetCSRFToken();
        this.setOldPasswordHashes(user, Arrays.asList(parts[6].split(" *, *")));
        user.setLastHostAddress("null".equals(parts[7]) ? null : parts[7]);
        user.setLastPasswordChangeTime(new Date(Long.parseLong(parts[8])));
        user.setLastLoginTime(new Date(Long.parseLong(parts[9])));
        user.setLastFailedLoginTime(new Date(Long.parseLong(parts[10])));
        user.setExpirationTime(new Date(Long.parseLong(parts[11])));
        user.setFailedLoginCount(Integer.parseInt(parts[12]));
        return user;
    }
    
    @Override
    public synchronized void removeUser(final String accountName) throws AuthenticationException {
        this.loadUsersIfNecessary();
        final User user = this.getUser(accountName);
        if (user == null) {
            throw new AuthenticationAccountsException("Remove user failed", "Can't remove invalid accountName " + accountName);
        }
        this.userMap.remove(user.getAccountId());
        this.logger.info(Logger.SECURITY_SUCCESS, "Removing user " + user.getAccountName());
        this.passwordMap.remove(user);
        this.saveUsers();
    }
    
    public synchronized void saveUsers() throws AuthenticationException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(this.userDB));
            writer.println("# This is the user file associated with the ESAPI library from http://www.owasp.org");
            writer.println("# accountId | accountName | hashedPassword | roles | locked | enabled | csrfToken | oldPasswordHashes | lastPasswordChangeTime | lastLoginTime | lastFailedLoginTime | expirationTime | failedLoginCount");
            writer.println();
            this.saveUsers(writer);
            writer.flush();
            this.logger.info(Logger.SECURITY_SUCCESS, "User file written to disk");
        }
        catch (final IOException e) {
            this.logger.fatal(Logger.SECURITY_FAILURE, "Problem saving user file " + this.userDB.getAbsolutePath(), e);
            throw new AuthenticationException("Internal Error", "Problem saving user file " + this.userDB.getAbsolutePath(), e);
        }
        finally {
            if (writer != null) {
                writer.close();
                this.lastModified = this.userDB.lastModified();
                this.lastChecked = this.lastModified;
            }
        }
    }
    
    protected synchronized void saveUsers(final PrintWriter writer) throws AuthenticationCredentialsException {
        for (final Object o : this.getUserNames()) {
            final String accountName = (String)o;
            final DefaultUser u = (DefaultUser)this.getUser(accountName);
            if (u == null || u.isAnonymous()) {
                throw new AuthenticationCredentialsException("Problem saving user", "Skipping save of user " + accountName);
            }
            writer.println(this.save(u));
        }
    }
    
    private String save(final DefaultUser user) {
        final StringBuilder sb = new StringBuilder();
        sb.append(user.getAccountId());
        sb.append(" | ");
        sb.append(user.getAccountName());
        sb.append(" | ");
        sb.append(this.getHashedPassword(user));
        sb.append(" | ");
        sb.append(this.dump(user.getRoles()));
        sb.append(" | ");
        sb.append(user.isLocked() ? "locked" : "unlocked");
        sb.append(" | ");
        sb.append(user.isEnabled() ? "enabled" : "disabled");
        sb.append(" | ");
        sb.append(this.dump(this.getOldPasswordHashes(user)));
        sb.append(" | ");
        sb.append(user.getLastHostAddress());
        sb.append(" | ");
        sb.append(user.getLastPasswordChangeTime().getTime());
        sb.append(" | ");
        sb.append(user.getLastLoginTime().getTime());
        sb.append(" | ");
        sb.append(user.getLastFailedLoginTime().getTime());
        sb.append(" | ");
        sb.append(user.getExpirationTime().getTime());
        sb.append(" | ");
        sb.append(user.getFailedLoginCount());
        return sb.toString();
    }
    
    private String dump(final Collection<String> c) {
        final StringBuilder sb = new StringBuilder();
        for (final String s : c) {
            sb.append(s).append(",");
        }
        if (c.size() > 0) {
            return sb.toString().substring(0, sb.length() - 1);
        }
        return "";
    }
    
    @Override
    public void verifyAccountNameStrength(final String newAccountName) throws AuthenticationException {
        if (newAccountName == null) {
            throw new AuthenticationCredentialsException("Invalid account name", "Attempt to create account with a null account name");
        }
        if (!ESAPI.validator().isValidInput("verifyAccountNameStrength", newAccountName, "AccountName", 250, false)) {
            throw new AuthenticationCredentialsException("Invalid account name", "New account name is not valid: " + newAccountName);
        }
    }
    
    @Override
    public void verifyPasswordStrength(final String oldPassword, final String newPassword, final User user) throws AuthenticationException {
        if (newPassword == null) {
            throw new AuthenticationCredentialsException("Invalid password", "New password cannot be null");
        }
        if (oldPassword != null) {
            for (int length = oldPassword.length(), i = 0; i < length - 2; ++i) {
                final String sub = oldPassword.substring(i, i + 3);
                if (newPassword.indexOf(sub) > -1) {
                    throw new AuthenticationCredentialsException("Invalid password", "New password cannot contain pieces of old password");
                }
            }
        }
        int charsets = 0;
        for (int i = 0; i < newPassword.length(); ++i) {
            if (Arrays.binarySearch(EncoderConstants.CHAR_LOWERS, newPassword.charAt(i)) >= 0) {
                ++charsets;
                break;
            }
        }
        for (int i = 0; i < newPassword.length(); ++i) {
            if (Arrays.binarySearch(EncoderConstants.CHAR_UPPERS, newPassword.charAt(i)) >= 0) {
                ++charsets;
                break;
            }
        }
        for (int i = 0; i < newPassword.length(); ++i) {
            if (Arrays.binarySearch(EncoderConstants.CHAR_DIGITS, newPassword.charAt(i)) >= 0) {
                ++charsets;
                break;
            }
        }
        for (int i = 0; i < newPassword.length(); ++i) {
            if (Arrays.binarySearch(EncoderConstants.CHAR_SPECIALS, newPassword.charAt(i)) >= 0) {
                ++charsets;
                break;
            }
        }
        final int strength = newPassword.length() * charsets;
        if (strength < 16) {
            throw new AuthenticationCredentialsException("Invalid password", "New password is not long and complex enough");
        }
        final String accountName = user.getAccountName();
        if (accountName.equalsIgnoreCase(newPassword)) {
            throw new AuthenticationCredentialsException("Invalid password", "Password matches account name, irrespective of case");
        }
    }
}
