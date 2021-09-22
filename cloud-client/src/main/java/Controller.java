import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);
    public TextField input;

    public ListView<String> listView;

    private DataInputStream is;
    private DataOutputStream os;

    private static final String TARGET_DIR = "could-server\\root\\";
    private static final String SOURCE_DIR = "cloud-client\\root\\";
    private static final byte[] buffer = new byte[1024];


    public void send(ActionEvent actionEvent) throws Exception {
        String message = input.getText();
        input.clear();
        os.writeUTF(message);
        os.flush();
    }

    public void transmit(MouseEvent mouseEvent) throws IOException {

        File chosenFile = new File(listView.getSelectionModel().getSelectedItem());
        // копирование файла (отображаемого в списке listView) в дирректорию сервера TARGET_DIR
        transfer(chosenFile, new File(TARGET_DIR + chosenFile.getName()));
        LOG.debug("Copying file " + chosenFile.getName() + " to server");
    }


    private void transfer(File src, File dst){ // метод копирования файлов на сервер
        try (FileInputStream is = new FileInputStream(src);
             FileOutputStream os = new FileOutputStream(dst)) {
            int read = 0;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            // files - список файлов в исходной дирректории "cloud-client\\root\\"
            File dir = new File(SOURCE_DIR);
            File[] files = dir.listFiles();
            for (File file : files) {
                listView.getItems().add(String.valueOf(file)); // вывожу список в listView
            }

            Thread daemon = new Thread(()-> {
                try {
                    while (true){
                        String message = is.readUTF();
                        LOG.debug("received: {}", message);
                        Platform.runLater(() ->listView.getItems().add(message));
                    }
                } catch (Exception e) {
                    LOG.error("exception while read from input stream");
                }

            });
            daemon.setDaemon(true);
            daemon.start();
        } catch (IOException e) {
            LOG.error("e= ", e);
        }
    }



}
