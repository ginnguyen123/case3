package utils;

import java.util.regex.Pattern;

public class ValidateUtils {
    public static final String ADDRESS_REGEX = "^[A-Za-z0-9][A-Za-z0-9\\s]{4,14}$";

    public static final String NAME_REGEX = "^([A-Z][a-z]*(\\s))+[A-Z][a-z]*$";
    public static boolean isAddressValid(String address) {
        return Pattern.matches(ADDRESS_REGEX, address);
    }
    public static boolean isNameValid(String name) {
        return Pattern.matches(NAME_REGEX, name);
    }
}
