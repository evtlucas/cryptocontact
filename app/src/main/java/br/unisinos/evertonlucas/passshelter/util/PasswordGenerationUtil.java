package br.unisinos.evertonlucas.passshelter.util;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by everton on 25/09/15.
 */
public class PasswordGenerationUtil {
    private static final String ALPHA_CAPS  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA       = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUM         = "0123456789";
    private static final String SPL_CHARS   = "!@#$%^&*_=+-/";

    public static String generatePassword() {
        char[] characters = new char[ALPHA.length() + ALPHA_CAPS.length() + NUM.length() + SPL_CHARS.length()];
        System.arraycopy(ALPHA_CAPS.toCharArray(), 0, characters, 0, ALPHA_CAPS.length());
        System.arraycopy(ALPHA.toCharArray(), 0, characters, ALPHA_CAPS.length(), ALPHA.length());
        System.arraycopy(NUM.toCharArray(), 0, characters, ALPHA_CAPS.length() + ALPHA.length(), NUM.length());
        System.arraycopy(SPL_CHARS.toCharArray(), 0, characters,
                ALPHA_CAPS.length() + ALPHA.length() + NUM.length(), SPL_CHARS.length());
        return RandomStringUtils.random(8, characters);
    }
}
