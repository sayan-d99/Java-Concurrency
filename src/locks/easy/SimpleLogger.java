package locks.easy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleLogger {

    private Path filePath;
    private ReentrantLock lock = new ReentrantLock(true);

    public SimpleLogger(Path p) throws IOException {
        this.filePath = p;
        if (!filePath.toFile().exists())
            Files.createFile(p);
    }

    public void log(String message) {
        try {
            lock.lock();
            System.out.printf("%s has acquired lock. Writing message - %s\n", Thread.currentThread().getName(), message);
            Files.writeString(filePath, message, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}