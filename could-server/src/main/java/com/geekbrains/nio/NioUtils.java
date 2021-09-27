package com.geekbrains.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

public class NioUtils {

    private static final Logger LOG = LoggerFactory.getLogger(NioUtils.class);

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("could-server", "root");
        System.out.println(Files.exists(path));

        Path copy = Paths.get("could-server", "root", "copy.txt");

        WatchService watchService = FileSystems.getDefault()
                .newWatchService();

        new Thread(() -> {
          while (true){
              WatchKey key = null;
              try {
                  key = watchService.take();
                  if (key.isValid()) {
                      List<WatchEvent<?>> events = key.pollEvents();
                      for (WatchEvent<?> event : events) {
                          LOG.debug("kind {}, context {}", event.kind(), event.context());
                      }
                      key.reset();
                  }
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }

          }
        }).start();
        path.register(watchService,ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);

        Files.write(copy,"My message".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);

        Files.copy(copy, Paths.get("could-server", "root", "createTest.txt"), StandardCopyOption.REPLACE_EXISTING);

        Files.walk(path).forEach(System.out::println);

    }
}
