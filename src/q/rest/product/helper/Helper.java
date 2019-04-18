package q.rest.product.helper;


import q.rest.product.model.entity.Category;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Helper {

    public static String properTag(String tag){
        return tag.toLowerCase().trim().replaceAll(" ", "_");
    }

    public static String undecorate(String string) {
        return string.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    public static List<Integer> extractParams(List<String> params){
        List<Integer> intParams = new ArrayList<>();
        if(params != null ){
        for(String v : params){
            try{
                int id = Integer.parseInt(v);
                intParams.add(id);
            }catch (Exception ignore){

            }
        }
        }
        return intParams;
    }


    public static String getEncodedUrl(String url){
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    private static boolean isProbablyArabic(String s) {
        for (int i = 0; i < s.length();) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return true;
            i += Character.charCount(c);
        }
        return false;
    }


    public static String getNumberedQuery(String query){
        String numbered = "";
        try {

            if(!Helper.isProbablyArabic(query)){
                numbered = "%" + Helper.undecorate(query).toUpperCase() +"%";
            }
            else{
                numbered="asProductNumber";
            }
        } catch (Exception ignore) {
            numbered = "asProductNumber";
        }
        return numbered;
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
