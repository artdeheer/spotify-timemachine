import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JsonHandling {


    public static JSONArray parseJSONFile(String filepath) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        return new JSONArray(content);
    }

    //Sort multiple json files to one array
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

    public static void writeMultimapToJson(Multimap<String, Double> multimap) throws IOException {
        // Create an instance of ObjectMapper from Jackson library
        ObjectMapper mapper = new ObjectMapper();

        // Create a file object with the path to the JSON file
        File file = new File("/streaming data/multimap/multimap.json");

        // Use ObjectMapper to write the multimap to the JSON file
        mapper.writeValue(file, multimap);

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

    public static void addToMap(int outerCount, double validElements, JSONArray jsonArray, Multimap<String, Double> validSongs){
        JSONObject subject = jsonArray.getJSONObject(outerCount);
        int subSongcount = 0;

        int timeframe = 1; //months
        int min = 10; // minimum amount of streams a song has to have had within the period to avoid songs at the end easily getting into the list

        Instant subDate = Instant.parse(subject.getString("ts"));
        Instant endDate = subDate.atZone(ZoneId.of("UTC")).plusMonths(timeframe).toInstant();

        String trackID = subject.getString("spotify_track_uri");

        //declaring needed variables for calculations
        BigDecimal totalTimePlayed = new BigDecimal("0");
        BigDecimal subTimePlayed = new BigDecimal("0");

        for (int j = outerCount; j < validElements; j++){
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

        if (subSongcount > min){
            Double playPercentage = subTimePlayed.divide(totalTimePlayed, 6, RoundingMode.HALF_UP).doubleValue();
            validSongs.put(subject.get("spotify_track_uri").toString(), playPercentage);
        }
    }
}
