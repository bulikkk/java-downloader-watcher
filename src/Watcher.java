import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Created by piotrek on 23.05.17.
 */



public class Watcher {

    public static void watchDirectoryPath(Path path) {
        // Sanity check - Check if path is a folder
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(path,
                    "basic:isDirectory", NOFOLLOW_LINKS);
            if (!isFolder) {
                throw new IllegalArgumentException("Path: " + path + " is not a folder");
            }
        } catch (IOException ioe) {
            // Folder does not exists
            ioe.printStackTrace();
        }

        JFrame frame = new JFrame("Watcher");
        frame.setLayout(new GridLayout(0, 1));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        System.out.println("Watching path: " + path);

        JLabel label = new JLabel("Watching path: " + path);
        frame.getContentPane().add(label);
        frame.setSize(400, 100);
        frame.setVisible(true);

        // We obtain the file system of the Path
        FileSystem fs = path.getFileSystem ();

        // We create the new WatchService using the new try() block
        try(WatchService service = fs.newWatchService()) {

            // We register the path to the service
            // We watch for creation events
            path.register(service, ENTRY_CREATE);

            // Start the infinite polling loop
            WatchKey key = null;
            while(true) {
                key = service.take();

                // Dequeueing events
                Kind<?> kind = null;
                for(WatchEvent<?> watchEvent : key.pollEvents()) {
                    // Get the type of the event
                    kind = watchEvent.kind();
                    if (OVERFLOW == kind) {
                        continue; //loop
                    } else if (ENTRY_CREATE == kind) {
                        // A new Path was created
                        Path newPath = ((WatchEvent<Path>) watchEvent).context();
                        // Output
                        System.out.println("New path created: " + newPath);

                        JLabel label2 = new JLabel("New path created: " + newPath);
                        frame.getContentPane().add(label2);
                        frame.setVisible(true);

                        Saver obj = new Saver();
                        obj.insertRecords("/home/piotrek/IdeaProjects/Downloader/src/download/" + newPath);
                        SwingUtilities.invokeLater(() -> {
                            Charter ex = new Charter();
                            ex.setVisible(true);
                        });

                    }
                }

                if(!key.reset()) {
                    break; //loop
                }
            }

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        // Folder we are going to watch
        Path folder = Paths.get("/home/piotrek/IdeaProjects/Downloader/src/download");
        watchDirectoryPath(folder);
    }
}