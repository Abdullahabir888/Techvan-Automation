package com.techvan.utils;

import org.openqa.selenium.By;

/** Small XPath 1.0 helpers (case-insensitive text and attribute matching). */
public final class Xp {

    private static final String UP  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOW = "abcdefghijklmnopqrstuvwxyz";

    private Xp() {}

    /** translate(expr, UPPER, lower) */
    public static String lower(String expr) {
        return "translate(" + expr + ",'" + UP + "','" + LOW + "')";
    }

    /** True when the element's normalized text contains ANY of the words (case-insensitive). */
    public static String textHas(String... words) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < words.length; i++) {
            if (i > 0) sb.append(" or ");
            sb.append("contains(").append(lower("normalize-space()")).append(",'")
              .append(words[i].toLowerCase()).append("')");
        }
        return sb.append(")").toString();
    }

    /** Text input/textarea whose name, id, placeholder, aria-label or autocomplete contains ANY keyword. */
    public static By fieldLike(String... keywords) {
        String[] attrs = {"@name", "@id", "@placeholder", "@aria-label", "@autocomplete"};
        StringBuilder or = new StringBuilder();
        for (String k : keywords) {
            for (String a : attrs) {
                if (or.length() > 0) or.append(" or ");
                or.append("contains(").append(lower(a)).append(",'").append(k.toLowerCase()).append("')");
            }
        }
        return By.xpath("//*[(self::input or self::textarea) and not(@type='hidden') " +
                "and not(@type='password') and not(@type='checkbox') and not(@type='radio') and (" + or + ")]");
    }

    /** Visible validation / error / toast messages. */
    public static final String ERROR_MESSAGE =
            "//*[@role='alert' or contains(@class,'error') or contains(@class,'toast') " +
            "or contains(@class,'Toastify') or contains(@class,'invalid')][string-length(normalize-space())>0]";
}
