import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Paths;

//for comparing dates
import java.time.Instant;
import java.time.ZoneId;

//big integer, totalelements
import java.math.BigDecimal;
import java.math.RoundingMode;


public class HistoryAnalyzer {

    public static void main(String[] args) throws IOException {
        getAllValidSongs("streaming data/rawdata/");
    }

    public static Multimap<String, Double> getAllValidSongs(String directory) throws IOException {
        List<String> filelist = FileHandling.listFiles(directory);
        JSONArray jsonArray = JsonHandling.sortJSONFiles(filelist, directory);
        double validElements = Printer.printStats(filelist, directory, jsonArray);

        Multimap<String, Double> validSongs = ArrayListMultimap.create();
        double elementcount = 0;

        for (int i = 0; i < validElements; i++){
            JsonHandling.addToMap(i, validElements, jsonArray, validSongs);
            elementcount++;
            //percentage
            Printer.printPercentage(elementcount, validElements);
        }

        Printer.printMultimap(validSongs);
        return validSongs;
    }
}