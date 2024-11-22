package hotelapp.traverser;

import hotelapp.model.DataModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;

/**
 * Invoked by the Controller class; Traverses a directory to find a json file.
 * Two static methods to traverse the directory
 * (1) Non-concurrent traverse
 * (2) Concurrent traverse
 */
public class JsonTraverser {
    private final static Logger logger = LogManager.getLogger();

    /**
     * When finds a json file, invokes dataModel.loadJson method.
     * @param path to be traversed.
     * @param dataModel to be loaded Hotel or Review object.
     */
    public static void traverse(Path path, DataModel dataModel) {
        if (path.toString().endsWith(".json")) {
            dataModel.loadJson(path);
            return;
        }

        if (Files.isDirectory(path)) {
            try {
                DirectoryStream<Path> pathsInDir = Files.newDirectoryStream(path);
                for (Path p : pathsInDir) {
                    traverse(p, dataModel);
                }
            } catch (IOException e) {
                System.out.println("Directory path is invalid: " + e);
            }
        }
    }

    /**
     * When finds a json file, submits the task to poolManager to work concurrently.
     * @param path to be traversed.
     * @param poolManager to be submitted a task.
     * @param dataModel to be loaded Hotel or Review objects; Must be thread-safe!
     * @param phaser to be used to register a task.
     */
    public static void concurrentTraverse(Path path, DataModel dataModel, ExecutorService poolManager, Phaser phaser) {
        if (path.toString().endsWith(".json")) {
            phaser.register();
            poolManager.submit(() -> {
                logger.debug("Found a json file and passing it to a worker: " + path);
                try {
                    dataModel.loadJson(path);
                } finally {
                    phaser.arriveAndDeregister();
                    logger.debug("A worker finished its work and was deregistered");
                }
            });
            return;
        }

        if (Files.isDirectory(path)) {
            try {
                DirectoryStream<Path> pathsInDir = Files.newDirectoryStream(path);
                for (Path p : pathsInDir) {
                    concurrentTraverse(p, dataModel, poolManager, phaser);
                }
            } catch (IOException e) {
                logger.error("Directory path is invalid: " + path.toString());
                System.out.println("Directory path is invalid: " + e);
            }
        }
    }
}
