import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHandling {
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
        else {
            System.out.println("Provided path is not a directory");
        }
        return list;
    }
}
