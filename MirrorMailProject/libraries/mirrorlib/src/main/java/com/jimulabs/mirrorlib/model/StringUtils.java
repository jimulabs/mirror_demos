package com.jimulabs.mirrorlib.model;

/**
 * Created by krxsky on 1/17/2014.
 */

public class StringUtils {

    private static String[] dimensionUnits = {"dp", "px", "sp", "pt", "mm", "in", "dip"};

    /* converts from snake_case and camelCase to UpperCamelCase
       ex. "snake_case_str" -> "SnakeCaseStr"
           "weIRd__Input" -> "WeIRdInput"
           "camelCase" -> "CamelCase"
     */
    public static String toUpperCamel(String str) {
        String[] words = str.split("_");
        StringBuilder upperCamelString = new StringBuilder();
        for (String word : words) {
            upperCamelString.append(capitalize(word));
        }
        return upperCamelString.toString();
    }

    /* capitalize a string
       ex. "str" -> "Str"
           "WeiRD" -> "WeiRD"
           "" -> ""
     */
    public static String capitalize(String str) {
        if (str.isEmpty()) {
            return str;
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    /* return true if str represents a dimension
       ex. "18px" -> true
           "134j4dp" -> false
           "134.5dp" -> true
     */
    public static boolean isDimension(String str) {
        if (str.length() < 2)
            return false;
        // special case when string endsWith "dip"
        if (str.endsWith("dip") &&
                isNumber(str.substring(0,str.length()-3)))
            return true;
        // for all other cases, check if the number part is a number
        String numPart = str.substring(0, str.length()-2);
        if (!isNumber(numPart))
            return false;
        // whether string ends with accepted units
        boolean endsWithUnit = false;
        for (String unit : dimensionUnits) {
            if (str.endsWith(unit)) {
                endsWithUnit = true;
                break;
            }
        }
        return endsWithUnit;
    }


    /* return true if str represents a number
       ex. "123.53" -> true
           "34kd34" -> false
           "45" -> true
     */
    public static boolean isNumber(String str) {
        // optional sign + ((digits + optional dot + optional digits) or (dot + digits))
        return str.matches("[+-]?(((\\d+)[.]?(\\d*))|(\\.(\\d+)))");
    }

}
