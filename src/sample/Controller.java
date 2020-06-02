package sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.lang.reflect.InvocationTargetException;

public class Controller {
    @FXML
    public Canvas canvas;
    @FXML
    public Button startButton;
    @FXML
    public Button stopButton;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public TextField pointNumberField;
    @FXML
    public TextField resultField;
    private AsyncTask task;
    private GraphicsContext gc;
    private int numberOfPoints;

    @FXML
    private void handleRunBtnAction(){
        if (task!=null &&task.getState().equals(Worker.State.RUNNING)) {//żeby sie nie posypalo jak się wciśnie start parę razy pod rząd
           task.cancel();
        }
        if (pointNumberField.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText("No data in Number of points field");
            alert.showAndWait();
        }
        else {
            resultField.setText("");
            gc=canvas.getGraphicsContext2D();
            numberOfPoints = Integer.valueOf(pointNumberField.getText());
            task = new AsyncTask(numberOfPoints,gc);
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    float var = (float) task.getValue();
                    String str = String.valueOf(var);
                    resultField.setText(str);
                }
            });
            new Thread(task).start();
            progressBar.progressProperty().bind(task.progressProperty());
        }
    }
    @FXML
    public void resumeThread(){
            if(task==null){
                return;
            }
            if (task.getState().equals(Worker.State.RUNNING)) {
                task.resume();}
    }

    @FXML
    public void stopThread(){
        if(task==null){
            return;
        }
        task.suspend();
    }
    @FXML
    public void cancelThread(){
        if(task==null){
            return;
        }
        task.cancel();
    }
}