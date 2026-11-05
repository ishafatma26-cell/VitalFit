package com.vitalfit.util;

/**
 * Centralized Input Validation & XSS Sanitization utility.
 * Escapes HTML entities to protect against Cross-Site Scripting (XSS)
 * in user profiles, workout logs, notes, and challenge descriptions.
 *
 * @author Isha Fatma (25SCSE1180892) - Team NOVA
 */
public class XssSanitizer {

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(input.length() + 16);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#x27;");
                    break;
                case '/':
                    sb.append("&#x2F;");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    public static String stripTags(String input) {
        if (input == null) return null;
        return input.replaceAll("<[^>]*>", "");
    }
}
