package fe.de200221;

import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Pattern;

public final class AccountValidator {
    /** BR-REG-02: 5–20 ký tự, bắt đầu bằng chữ cái, chỉ gồm chữ ASCII, số, dấu _. */
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{4,19}$");
    /** BR-REG-04: local@domain.tld, các nhãn domain không rỗng, TLD >= 2 chữ cái. */
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");
    /** BR-REG-09: 10 chữ số, đầu số 03/05/07/08/09. */
    private static final Pattern PHONE = Pattern.compile("^0[35789]\\d{8}$");

    private static final int EMAIL_MAX_LENGTH = 100;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 32;
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=";

    private AccountValidator() {
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME.matcher(username).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null
                && email.length() <= EMAIL_MAX_LENGTH
                && EMAIL.matcher(email).matches();
    }

    /** BR-REG-06. username null/blank thì bỏ qua điều kiện "không chứa username". */
    public static boolean isValidPassword(String password, String username) {
        if (password == null
                || password.length() < PASSWORD_MIN_LENGTH
                || password.length() > PASSWORD_MAX_LENGTH) {
            return false;
        }
        boolean upper = false, lower = false, digit = false, special = false;
        for (char c : password.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                upper = true;
            } else if (c >= 'a' && c <= 'z') {
                lower = true;
            } else if (c >= '0' && c <= '9') {
                digit = true;
            } else if (SPECIAL_CHARS.indexOf(c) >= 0) {
                special = true;
            } else {
                return false; // khoảng trắng hoặc ký tự ngoài các nhóm cho phép
            }
        }
        if (!(upper && lower && digit && special)) {
            return false;
        }
        if (username != null && !username.isBlank()) {
            return !password.toLowerCase(Locale.ROOT)
                    .contains(username.toLowerCase(Locale.ROOT));
        }
        return true;
    }

    /** Chỉ kiểm tra định dạng; null trả false. Việc phone là tùy chọn do AccountService xử lý. */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE.matcher(phone).matches();
    }

    /** Số tuổi tròn năm tính đến ngày today (sinh nhật hôm nay là đã đủ tuổi). */
    public static int calculateAge(LocalDate dob, LocalDate today) {
        return Period.between(dob, today).getYears();
    }

}
