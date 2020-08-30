package jms.requester.fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequesterMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxml = "requester.fxml";
        URL url = getClass().getClassLoader().getResource(fxml);
        if (url != null) {
            FXMLLoader loader = new FXMLLoader(url);
            RequesterController controller = new RequesterController();
            loader.setController(controller);
            Parent root = loader.load();
            primaryStage.setTitle("Store Manager");
            primaryStage.setScene(new Scene(root, 486, 318));
            primaryStage.setOnCloseRequest(t -> {
                controller.stop();
                Platform.exit();
                System.exit(0);
            });

            primaryStage.show();
        } else {
            System.err.println("Error: Could not load frame from "+ fxml);
        }
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        // Save file
        Logger.getLogger(RequesterController.class.getName()).log(Level.INFO, "on close ");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
