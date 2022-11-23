package main.parsehandler;

public class NumberParser {
    public static boolean isNotInt(String i) {
        try {
            Integer.parseInt(i);
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
