package q.rest.product.helper;


import q.rest.product.model.entity.Category;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Helper {

    public static String properTag(String tag){
        return tag.toLowerCase().trim().replaceAll(" ", "_");
    }

    public static String undecorate(String string) {
        return string.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    public static boolean isProbablyArabic(String s) {
        for (int i = 0; i < s.length();) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return true;
            i += Character.charCount(c);
        }
        return false;
    }



    public static void trimNames(Category category){
        category.setName(category.getName().trim());
        category.setNameAr(category.getNameAr().trim());
    }
    public static int getRandomInteger(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static Date addSeconds(Date original, int seconds) {
        return new Date(original.getTime() + (1000L * seconds));
    }

    public static Date addMinutes(Date original, int minutes) {
        return new Date(original.getTime() + (1000L * 60 * minutes));
    }

    public String getDateFormat(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");
        return sdf.format(date);
    }

    public String getDateFormat(Date date, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }


}
