package fe.de200221;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AccountService {
    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final int PASSWORD_HISTORY_SIZE = 3;
    public static final int MIN_AGE = 18;

    private final Map<String, Account> accountsByUsername = new HashMap<>(); // key: username lowercase
    private final Map<String, String> usernameByEmail = new HashMap<>();     // email lowercase -> username key
    private final Map<String, String> usernameByToken = new HashMap<>();     // token -> username key
    private final Map<String, String> tokenByUsername = new HashMap<>();     // username key -> token hiện hành


    public AccountService() { /* TODO: khởi tạo các Map */ }
    public ResultCode unlockAccount(String username) { throw new UnsupportedOperationException("TODO"); }
    // ... register, login, changePassword, requestPasswordReset, resetPassword,
    //     disableAccount, findByUsername, isLocked như mục 5.3
    public ResultCode register(String username, String email, String password,
                               String confirmPassword, LocalDate dateOfBirth, String phone) {
        LocalDate today = LocalDate.now();
        // BR-REG-01
        if (isBlank(username) || isBlank(email) || isBlank(password) || isBlank(confirmPassword)
                || dateOfBirth == null || dateOfBirth.isAfter(today)) {
            return ResultCode.INVALID_INPUT;
        }
        // BR-REG-02
        if (!AccountValidator.isValidUsername(username)) {
            return ResultCode.INVALID_USERNAME;
        }
        // BR-REG-04
        if (!AccountValidator.isValidEmail(email)) {
            return ResultCode.INVALID_EMAIL;
        }
        // BR-REG-06
        if (!AccountValidator.isValidPassword(password, username)) {
            return ResultCode.WEAK_PASSWORD;
        }
        // BR-REG-07
        if (!password.equals(confirmPassword)) {
            return ResultCode.PASSWORD_MISMATCH;
        }
        // BR-REG-08
        if (AccountValidator.calculateAge(dateOfBirth, today) < MIN_AGE) {
            return ResultCode.UNDERAGE;
        }
        // BR-REG-09 (phone tùy chọn: null hoặc "" được chấp nhận)
        if (phone != null && !phone.isEmpty() && !AccountValidator.isValidPhone(phone)) {
            return ResultCode.INVALID_PHONE;
        }
        String userKey = key(username);
        String emailKey = key(email);
        // BR-REG-03
        if (accountsByUsername.containsKey(userKey)) {
            return ResultCode.DUPLICATE_USERNAME;
        }
        // BR-REG-05
        if (usernameByEmail.containsKey(emailKey)) {
            return ResultCode.DUPLICATE_EMAIL;
        }
        // BR-REG-10
        String salt = PasswordHasher.generateSalt();
        Account account = new Account(username, emailKey, dateOfBirth, phone,
                salt, PasswordHasher.hash(salt, password));
        accountsByUsername.put(userKey, account);
        usernameByEmail.put(emailKey, userKey);
        return ResultCode.SUCCESS;
    }
    public ResultCode login(String username, String password) {
        if (isBlank(username) || isBlank(password)) return ResultCode.INVALID_INPUT;
        Account acc = accounts.get(key(username));
        if (acc == null) return ResultCode.INVALID_CREDENTIALS;              // không tiết lộ lý do
        if (acc.getStatus() == AccountStatus.DISABLED) return ResultCode.ACCOUNT_DISABLED;
        if (acc.isLocked()) return ResultCode.ACCOUNT_LOCKED;                // không tăng bộ đếm

        if (!PasswordHasher.matches(acc.getSalt(), password, acc.getCurrentPasswordHash())) {
            acc.incrementFailedAttempts();
            // TODO: nếu failedAttempts >= MAX_FAILED_ATTEMPTS -> acc.lock(), trả ACCOUNT_LOCKED
            return ResultCode.INVALID_CREDENTIALS;
        }
        // TODO: failedAttempts = 0
        return ResultCode.SUCCESS;
    }

    public ResultCode unlockAccount(String username) {          // BR-ADM-03
        Optional<Account> acc = findByUsername(username);
        if (acc.isEmpty()) return ResultCode.USER_NOT_FOUND;
        acc.get().unlock();                                         // locked = false, failedAttempts = 0
        return ResultCode.SUCCESS;
    }
    public ResultCode disableAccount(String username) {
        Optional<Account> account = findByUsername(username);
        if (account.isEmpty()) {
            return ResultCode.USER_NOT_FOUND;
        }
        account.get().setStatus(AccountStatus.DISABLED);
        return ResultCode.SUCCESS;
    }
    public boolean isLocked(String username) {
        return findByUsername(username).map(Account::isLocked).orElse(false);
    }
    public Optional<Account> findByUsername(String username) {
        if (isBlank(username)) {
            return Optional.empty();
        }
        return Optional.ofNullable(accountsByUsername.get(key(username)));
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String key(String s) {
        return s.toLowerCase(Locale.ROOT);
    }

}
