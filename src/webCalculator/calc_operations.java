package webCalculator;

public class calc_operations {

    public static int addition(int arg1, int arg2){
        int res = arg1 + arg2;
        return res;
    }

    public static int subtration(int arg1, int arg2){
        int res = arg1 - arg2;
        return res;
    }
    
    public static int multiplication(int arg1, int arg2){
        int res = arg1 * arg2;
        return res;
    }

    public static int division(int arg1, int arg2){
        try {
            int res = arg1/arg2;
            return res;
        } catch (Exception e) {
            return 0;
        }
    }
}
