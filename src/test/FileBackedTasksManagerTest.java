import manager.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    @Override
    public void createManager() {
        taskManager = new FileBackedTasksManager("save.csv");
    }

    @AfterEach
    public void clearFile() {
        try {
            FileWriter fwOb = new FileWriter("save.csv", false);
            PrintWriter pwOb = new PrintWriter(fwOb, false);
            pwOb.flush();
            pwOb.close();
            fwOb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
