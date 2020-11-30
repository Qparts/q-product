package q.rest.product.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SysProps {
    private static final String FILE_DIRECTORY = "/configs/";
    private static final String FILE_NAME = "sys_props.properties";
    private static final String FILE_PATH =  FILE_DIRECTORY + FILE_NAME;
    private static Properties prop;

    static{
        try {
            loadFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadFile(){
        try(InputStream is = new FileInputStream(FILE_PATH)) {
            prop = new Properties();
            prop.load(is);
        } catch (FileNotFoundException e) {
            System.out.println("file not found exception ");
        } catch (IOException e) {
            System.out.println("IO exception");
        } catch (Exception ex){
            System.out.println("general exception ");
        }
    }




    public static String getValue(String key){
        System.out.println("checking key " + key);
        Object o = prop.get(key);
        System.out.println("found value " + o);
        return prop.get(key).toString();
    }


}
