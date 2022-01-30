public class Main {
    public static void main(String[] args) {

        Manager manager = new Manager();

        manager.creatTask(new Task("TaskOne", "to", "NEW"));
        manager.creatTask(new Task("TaskTwo", "tt", "NEW"));
        manager.creatEpic(new Epic("FirstEpic", "fe"));
        manager.creatSubtask(new Subtask("firstSubtask", "fs", "NEW"));
        manager.creatSubtask(new Subtask("secondSubtask", "ss", "DONE"));
        manager.creatEpic(new Epic("SecondEpic", "se"));
        manager.creatSubtask(new Subtask("thirdSubtask", "ts", "IN_PROGRESS"));

        manager.getAllTasks();
        manager.updateSubtask(new Subtask("update", "up", "DONE"));
        manager.getAllTasks();

    }
}
