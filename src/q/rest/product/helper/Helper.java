package q.rest.product.helper;


import q.rest.product.model.entity.v3.product.Category;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Helper {

    public static String properTag(String tag){
        return tag.toLowerCase().trim().replaceAll(" ", "_");
    }

    public List<Date> getAllDatesBetween(Date from, Date to){
        from = new Date(from.getTime() - (1000*60*60*24));
        to = new Date(to.getTime() + (1000*60*60*24));
        LocalDate fromLocal = convertToLocalDate(from);
        LocalDate toLocal = convertToLocalDate(to);

        List<LocalDate> localDates = fromLocal.datesUntil(toLocal)
                .collect(Collectors.toList());

        List<Date> dates = new ArrayList<>();
        for(LocalDate ld : localDates){
            dates.add(convertToDate(ld));
        }
        return dates;
    }


    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return LocalDate.ofInstant(
                dateToConvert.toInstant(), ZoneId.systemDefault());
    }

    public static Date convertToDate(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static Date addDays(Date original, long days) {
        return new Date(original.getTime() + (1000L * 60 * 60 * 24 * days));
    }

    public static List<String> getPullDataLinks(int count, String base, int chunk){
        final int N = chunk > 0 ? chunk : 500;
        List<Integer> ints = new ArrayList<>();
        int temp = 0;
        while(count > 0){
            ints.add(temp);
            if(count >= N) count -= N;
            else count -= count;
            temp += N;
        }
        List<String> links = new ArrayList<>();
        ints.forEach(g -> links.add(base + "offset/"+ g +"/max/" + N));
        return links;
    }


    public static String undecorate(String string) {
        if(string != null) {
            return string.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        }else return null;
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
                throw new Exception();
            }
        } catch (Exception ignore) {
            numbered = "asProductNumber";
        }
        return numbered;
    }

    public static long getQueryAsId(String query){
        long asId = 0;
        try {
            asId = Long.parseLong(query);
        } catch (Exception ignore) {

        }
        return asId;
    }


    public static String getCatalogIdFromMakeId(int makeId){
        return Bundler.getValue(String.valueOf(makeId));
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
