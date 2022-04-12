
import manager.*;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        File filePath = new File("save.csv");
        FileBackedTasksManager file = new FileBackedTasksManager(filePath);
        file.printAllTasks();
        System.out.println(file.getHistoryManager().getHistory());
        System.out.println(file.getPrioritizedTasks());

    }
}
