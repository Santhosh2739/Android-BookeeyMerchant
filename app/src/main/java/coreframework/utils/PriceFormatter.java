package coreframework.utils;

/**
 * Created by 30099 on 1/7/2016.
 */
public class PriceFormatter {

    static String ZEROES = "000000000000";
    static String BLANKS = "            ";

    public static String format( int val, int w)
    {	return format( Integer.toString(val), w); }

    public static String format( String s, int w)
    {
        int w1 = Math.abs(w);
        int n = w1-s.length();

        if ( n <= 0 ) return s;
        while (BLANKS.length()<n) BLANKS += "      ";
        if ( w<0 )
            return s + BLANKS.substring(0,n);
        else
            return BLANKS.substring(0,n) + s;
    }

    public static String format( double val, int n, int w)
    {
        if(val == 0d){
            return "0.000";
        }
        //	rounding
        double incr = 0.5;
        for( int j=n; j>0; j--) {
            incr /= 10;
        }
        val += incr;

        String s = Double.toString(val);
        int n1 = s.indexOf('.');
        int n2 = s.length() - n1 - 1;

        if (n>n2)  {
            int len = n-n2;
            while (ZEROES.length()<len) ZEROES += "000000";
            s = s+ZEROES.substring(0, len);
        }
        else if (n2>n) s = s.substring(0,n1+n+1);

        return format( s, w );
    }

}
