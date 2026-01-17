package br.com.senior.tradeit.validation.user.regex;

public class UserValidationPatterns {
    public static final String EMAIL_PATTERN = "\\w+(\\.\\w+)?@\\w+(\\.\\w+)+";
    public static final String EIGHT_CHARACTERS_PASSWORD_PATTERN = ".{8}.*";
}
