package com.rohjans;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.rohjans.utils.Helpers;

import java.io.IOException;

/**
 * Main class
 *
 * @author amfs
 */
public class Main extends Application {

    /**
     * The default entry point of the application
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = Helpers.getLoadedFXML("main-view");
        Scene scene = new Scene(loader.load(), 1024, 768);
        stage.setTitle("Personal File System");
        stage.getIcons().add(Helpers.getAppIcon());
        stage.setScene(scene);
        stage.show();
    }

}
