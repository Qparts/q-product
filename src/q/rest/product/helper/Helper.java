package q.rest.product.helper;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import q.rest.product.model.entity.v3.product.Category;
import q.rest.product.model.qstock.StockLive;
import q.rest.product.model.qstock.StockPurchaseItem;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Helper {

    public static String properTag(String tag){
        return tag.toLowerCase().trim().replaceAll(" ", "_");
    }


    public static double round(double number){
        return Math.round( number * 100.0) / 100.0;
    }

    public static double calculateAveragePrice(List<StockLive> lives, double unitCost, int quantity ){
        int totalQuantity = quantity;
        double totalCost = unitCost * quantity;
        for (var live : lives) {
            totalQuantity += live.getQuantity();
            totalCost += live.getAveragedCost() * live.getQuantity();
        }
        return totalCost / totalQuantity;
    }

    public List<Date> getAllDatesBetween(Date from, Date to, boolean excludeFriday){
        from = new Date(from.getTime());
        to = new Date(to.getTime() + (1000*60*60*24));
        LocalDate fromLocal = convertToLocalDate(from);
        LocalDate toLocal = convertToLocalDate(to);
        List<LocalDate> localDates;
        if(excludeFriday){
            Set<DayOfWeek> fridays = EnumSet.of(DayOfWeek.FRIDAY);
            localDates = fromLocal.datesUntil(toLocal).filter(d -> !fridays.contains(d.getDayOfWeek()))
                    .collect(Collectors.toList());
        }
        else{
            localDates = fromLocal.datesUntil(toLocal).collect(Collectors.toList());
        }
        List<Date> dates = new ArrayList<>();
        for(LocalDate ld : localDates){
            dates.add(convertToDate(ld));
        }
        return dates;
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


    public List<Date> getAllDatesBetween2(Date from, Date to){
        //from = new Date(from.getTime() - (1000*60*60*24));//start is inclusive in fromLocal
        to = new Date(to.getTime() + (1000*60*60*24));//because end is exclusive in toLocal
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

    public List<YearMonth> getAllMonthsBetween(Date from, Date to){
        String pattern = "MMM-YYYY";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        YearMonth startDate = YearMonth.parse(getDateFormat(from, pattern), formatter);
        YearMonth endDate = YearMonth.parse(getDateFormat(to, pattern), formatter);
        List<YearMonth> yearMonths = new ArrayList<>();
        while (startDate.isBefore(endDate)){
            System.out.println(startDate.format(formatter));
            yearMonths.add(startDate);
            startDate = startDate.plusMonths(1);
        }
        return yearMonths;
    }

    public List<YearMonth> getAllMonthsBetween(String from, String to){
        String pattern = "MMM-YYYY";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        YearMonth startDate = YearMonth.parse(from, formatter);
        YearMonth endDate = YearMonth.parse(to, formatter);
        List<YearMonth> yearMonths = new ArrayList<>();
        while (startDate.isBefore(endDate)){
            yearMonths.add(startDate);
            startDate = startDate.plusMonths(1);
        }
        return yearMonths;
    }

    public static List<YearMonth> getAllPreviousMonths(int year, int month, int length){
        List<YearMonth> yearMonths = new ArrayList<>();
        for(int i =length -1 ; i >= 0 ; i --){
            YearMonth ym = YearMonth.of(year, month).minusMonths(i);
            yearMonths.add(ym);
        }
        return yearMonths;
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


    public static int getSubscriberFromJWT(String header) {
        String token = header.substring("Bearer".length()).trim();
        Claims claims = Jwts.parserBuilder().setSigningKey(KeyConstant.PUBLIC_KEY).build().parseClaimsJws(token).getBody();
        return Integer.parseInt(claims.get("sub").toString());
    }


    public static int getCompanyFromJWT(String header) {
        String token = header.substring("Bearer".length()).trim();
        Claims claims = Jwts.parserBuilder().setSigningKey(KeyConstant.PUBLIC_KEY).build().parseClaimsJws(token).getBody();
        return Integer.parseInt(claims.get("comp").toString());
    }


    public static int convertToInteger(String query){
        try{
            return Integer.parseInt(query);
        }
        catch(Exception ex){
            return -1;
        }
    }


    public static long convertToLong(String query){
        try{
            return Long.parseLong(query);
        }
        catch(Exception ex){
            return -1;
        }
    }

    public static Date getToDate(int month, int year) {
        YearMonth ym = YearMonth.of(year,month);
        LocalDate to = ym.atEndOfMonth();
        return convertToDate(to);
    }

    public static Date getFromDate(int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        return convertToDate(from);
    }


}
