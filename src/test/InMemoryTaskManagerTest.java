import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    @Override
    public void createManager() {
        taskManager = new InMemoryTaskManager();
    }

}
