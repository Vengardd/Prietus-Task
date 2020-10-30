import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

public class HomeWatcherService {

    private final PathKeeper pathKeeper;

    public HomeWatcherService(PathKeeper pathKeeper) {
        this.pathKeeper = pathKeeper;
    }

    public void watch() throws IOException, InterruptedException {
        WatchService watchService
                = FileSystems.getDefault().newWatchService();

        Path homePath = pathKeeper.getHome();

        homePath.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                LocalDateTime actualDateTime = LocalDateTime.now();
                int actualHour = actualDateTime.getHour();
                System.out.println(
                        "Event kind:" + event.kind()
                                + ". File affected: " + event.context()
                                + " at " + actualHour + " hour.");
                String fileExtension = getFileExtension(event.context());
                if (fileExtension.equals(Extensions.XML)) {
                    moveToDev();
                } else if (fileExtension.equals(Extensions.JAR) && actualHour % 2 == 0) {
                    moveToDev();
                } else if (fileExtension.equals(Extensions.JAR) && actualHour % 2 == 1) {
                    moveToTest();
                }
            }
            key.reset();
        }
    }

    private static void moveToDev() {
        throw new RuntimeException("Not implemented");
        updateCount();
    }

    private static void moveToTest() {
        throw new RuntimeException("Not implemented");
        updateCount();
    }

    private static String getFileExtension(Object context) {
        Path pathOfFile = (Path) context;
        String[] asd = pathOfFile.getFileName().toString().split("\\.");
        return asd[asd.length - 1];
    }

}