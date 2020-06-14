package yb.utilpack;

public class FilterMod {

    
    public static String reduceText(String text, int limit) {

        if (text.length() < limit) {
            return text;
        }
        return text.substring(0, limit-1);
    }

    public static String reduceAndSuspendText(String text, int limit) {
        return reduceText(text, limit-3) + "...";
    }

}
