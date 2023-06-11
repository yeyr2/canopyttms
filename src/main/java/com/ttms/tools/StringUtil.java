package com.ttms.tools;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static String getString(List list) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean start = false;
        for(Object t : list) {
            if(start) {
                stringBuilder.append(",");
            }else {
                start = true;
            }
            if(t instanceof String) {
                stringBuilder.append(t);
            }else if(t instanceof Long) {
                String l = String.valueOf(t);
                stringBuilder.append(l);
            }else{
                return ',' == stringBuilder.charAt(stringBuilder.length()-1) ? stringBuilder.substring(0,stringBuilder.length()-1) : stringBuilder.toString();
            }
        }

        return ',' == stringBuilder.charAt(stringBuilder.length()-1) ? stringBuilder.substring(0,stringBuilder.length()-1) : stringBuilder.toString();
    }

    public static List getList(String str) {
        List list = new ArrayList();
        String[] split = str.split(",");
        for(String s : split) {
            try{
                long l = Long.parseLong(s);
                list.add(l);
            }catch (Exception e) {
                list.add(s);
            }
        }

        return list;
    }

    public static String removeSpaces(String str) {
        if(str == null || "".equals(str)){
            return "";
        }

        str = str.replace(" ","");
        return str;
    }
}
