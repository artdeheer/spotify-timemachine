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
        getAllValidSongs();
    }

    public static ArrayList<String> getAllValidSongs() throws IOException {
        String dir = "../../streaming data/rawdata/";
        List<String> filelist = listFiles(dir);
        System.out.println(filelist);

        JSONArray jsonArray = sortJSONFiles(filelist, dir);

        double totalElements = totalElements(filelist, dir);
        double validElements = jsonArray.length();
        double difference = totalElements - validElements;

        System.out.println("Total elements: " + totalElements(filelist, dir));
        System.out.println("Valid elements: " + jsonArray.length());
        System.out.println("Skipped elements: " + difference);

        ArrayList<String> validSongs = new ArrayList<>();
        double elementcount = 0;

        //percentage that a song should have been played within given timeframe, 1 = 100%
        BigDecimal treshold = new BigDecimal("0.03");
        int timeframe = 1; //months
        int min = 10; // minimum amount of streams a song has to have had within the period

        for (int i = 0; i < validElements; i++){
            JSONObject subject = jsonArray.getJSONObject(i);
            int subSongcount = 0;
            if (validSongs.contains(subject.getString("spotify_track_uri"))){
                continue;
            }

            Instant subDate = Instant.parse(subject.getString("ts"));
            Instant endDate = subDate.atZone(ZoneId.of("UTC")).plusMonths(timeframe).toInstant();

            String trackID = subject.getString("spotify_track_uri");

            //declaring needed variables for calculations
            BigDecimal totalTimePlayed = new BigDecimal("0");
            BigDecimal subTimePlayed = new BigDecimal("0");

            for (int j = i; j < validElements; j++){
                JSONObject comparator = jsonArray.getJSONObject(j);
                Instant compDate = Instant.parse(comparator.get("ts").toString());
                if (compDate.isAfter(endDate)){
                    break;
                }
                totalTimePlayed = totalTimePlayed.add(new BigDecimal((comparator.get("ms_played")).toString()));

                if (trackID.equals(comparator.getString("spotify_track_uri"))){
                    subTimePlayed = subTimePlayed.add(new BigDecimal((subject.get("ms_played")).toString()));
                    subSongcount++;
                }

            }

            if (subTimePlayed.divide(totalTimePlayed, 6, RoundingMode.HALF_UP).compareTo(treshold) >= 0 && subSongcount > min){
                validSongs.add(subject.get("spotify_track_uri").toString());
            }

            elementcount++;
            //percentage
            System.out.println(String.format("%.2f", elementcount / validElements * 100) + "%");
        }

        System.out.println(validSongs);
        System.out.println("Amount: " + validSongs.size());
        writeArrayListToFile(new File("../../streaming data/valid songs/valid_songs.txt"), validSongs);
        return validSongs;
    }

    public static void writeArrayListToFile(File file, ArrayList<String> StringList){
        try {
            // Create the file
            boolean isFileCreated = file.createNewFile();

            if (isFileCreated) {
                System.out.println("File created successfully.");
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

        String filePath = "../../streaming data/valid songs/valid_songs.txt"; // Replace with your file path

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            String content = "This is the content to be written to the file.";

            for (String uri : StringList){
                writer.write(uri + "\n");
            }

            System.out.println("Content has been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int totalElements(List<String> fileList, String directory) throws IOException {
        int totalElements = 0;
        for (String file : fileList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(directory + "/" + file));

            for (JsonNode element : rootNode) {
                totalElements++;
            }
        }
        return totalElements;
    }

    public static List<String> listFiles(String filepath) {
        File dir = new File(filepath);
        List<String> list = new ArrayList<>();
        if (dir.isDirectory()) {
            String[] files = dir.list();
            Pattern p = Pattern.compile("^(.*?)\\.json$");
            for (String file : files) {
                Matcher m = p.matcher(file);
                if (m.matches()) {
                    list.add(m.group(1) + ".json");
                }
            }
        }
        return list;
    }

    public static JSONArray parseJSONFile(String filepath) throws JSONException, IOException{
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        return new JSONArray(content);
    }

    public static JSONArray sortJSONFiles(List<String> fileList, String directory) throws IOException {

        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<>();
        for (String file : fileList){
            String filepath = directory + file;
            JSONArray jsonArray = parseJSONFile(filepath);

            for (int i = 0; i < jsonArray.length(); i++){
                jsonValues.add(jsonArray.getJSONObject(i));
            }
        }

        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "ts";
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();
                try {
                    valA = (String) a.get(KEY_NAME);
                    valB = (String) b.get(KEY_NAME);
                }
                catch (JSONException e) {
                }
                return valA.compareTo(valB); //put "-" in front if sorting the other way
            }
        });

        for (int i = 0; i < jsonValues.size(); i++) {
            if (!jsonValues.get(i).get("spotify_track_uri").toString().equals("null")){
                sortedJsonArray.put(jsonValues.get(i));
            }
        }
        return sortedJsonArray;
    }
}