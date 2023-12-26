import com.google.common.collect.Multimap;
import org.json.JSONArray;

import java.io.IOException;
import java.util.List;

public class Printer {
    public static void printPercentage(double elementcount, double validElements){
        System.out.println(String.format("%.2f", elementcount / validElements * 100) + "%");
    }

    public static void printMultimap(Multimap multimap){
        System.out.println("Amount: " + multimap.size());
        System.out.println(multimap);
    }


    public static double printStats(List<String> filelist, String directory, JSONArray jsonArray) throws IOException {
        System.out.println(filelist);

        double totalElements = JsonHandling.totalElements(filelist, directory);
        double validElements = jsonArray.length();
        double difference = totalElements - validElements;

        System.out.println("Total elements: " + JsonHandling.totalElements(filelist, directory));
        System.out.println("Valid elements: " + jsonArray.length());
        System.out.println("Skipped elements: " + difference);

        return validElements;
    }
}
