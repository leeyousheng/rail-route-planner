package railrouteplanner.util;

import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestDataUtil {
    private TemporaryFolder folder;

    public void setFolder(TemporaryFolder folder) {
        this.folder = folder;
    }

    public File createTestDataFile(String... data) throws IOException {
        File file = folder.newFile("testfile.csv");

        FileWriter fileWriter = new FileWriter(file);
        StringBuilder sb = new StringBuilder();
        for (String d : data) {
            sb.append(String.format("%s%n", d));
        }
        fileWriter.write(sb.toString());
        fileWriter.close();

        return file;
    }
}
