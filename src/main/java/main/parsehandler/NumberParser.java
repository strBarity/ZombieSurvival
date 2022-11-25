package main.parsehandler;

public class NumberParser {
    /**
     * 특정 문자열이 숫자가 될 수 없는지 확인하는 메소드
     * @param i int가 아닌지 확인할 문자열
     * @return 문자열이 int가 아니라면 true 반환, 맞다면 false 반환
     */
    public static boolean isNotInt(String i) {
        try {
            Integer.parseInt(i);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
