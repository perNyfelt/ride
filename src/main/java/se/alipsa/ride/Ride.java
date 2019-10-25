package se.alipsa.ride;

import static se.alipsa.ride.Constants.BRIGHT_THEME;
import static se.alipsa.ride.Constants.THEME;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.alipsa.ride.code.CodeComponent;
import se.alipsa.ride.console.ConsoleComponent;
import se.alipsa.ride.environment.EnvironmentComponent;
import se.alipsa.ride.inout.FileOpener;
import se.alipsa.ride.inout.InoutComponent;
import se.alipsa.ride.menu.MainMenu;
import se.alipsa.ride.utils.FileUtils;

import java.io.File;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

public class Ride extends Application {

  Logger log = LogManager.getLogger(Ride.class);
  private ConsoleComponent consoleComponent;
  private CodeComponent codeComponent;
  private EnvironmentComponent environmentComponent;
  private InoutComponent inoutComponent;
  private Stage primaryStage;
  private Scene scene;
  private MainMenu mainMenu;

  private FileOpener fileOpener;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    log.info("Starting Ride...");
    /*
    System.out.println(
        log.getName()
        + "\ntraceEnabled = " + log.isTraceEnabled()
        + "\ndebugEnabled = " + log.isDebugEnabled()
        + "\ninfoEnabled  = " + log.isInfoEnabled()
        + "\nwarnEnabled  = " + log.isWarnEnabled()
        + "\nerrorEnabled = " + log.isErrorEnabled()
    );*/

    this.primaryStage = primaryStage;

    BorderPane root = new BorderPane();
    VBox main = new VBox();
    main.setAlignment(Pos.CENTER);
    main.setFillWidth(true);

    root.setCenter(main);

    mainMenu = new MainMenu(this);
    root.setTop(mainMenu);

    scene = new Scene(root, 1366, 768);

    addStyleSheet(getPrefs().get(THEME, BRIGHT_THEME));

    SplitPane leftSplitPane = new SplitPane();
    leftSplitPane.setOrientation(Orientation.VERTICAL);

    consoleComponent = new ConsoleComponent(this);
    stretch(consoleComponent, root);

    codeComponent = new CodeComponent(this);
    stretch(codeComponent, root);
    leftSplitPane.getItems().addAll(codeComponent, consoleComponent);

    fileOpener = new FileOpener(codeComponent);

    SplitPane rightSplitPane = new SplitPane();
    rightSplitPane.setOrientation(Orientation.VERTICAL);

    environmentComponent = new EnvironmentComponent(this);
    stretch(environmentComponent, root);

    inoutComponent = new InoutComponent(this);
    stretch(inoutComponent, root);

    rightSplitPane.getItems().addAll(environmentComponent, inoutComponent);

    SplitPane splitPane = new SplitPane();
    splitPane.setOrientation(Orientation.HORIZONTAL);
    splitPane.getItems().addAll(leftSplitPane, rightSplitPane);

    main.getChildren().add(splitPane);

    primaryStage.setOnCloseRequest(t -> {
      if (getCodeComponent().hasUnsavedFiles()) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure you want to exit?");
        alert.setHeaderText("There are unsaved files");
        alert.setContentText("Are you sure you want to exit \n -even though you have unsaved files?");

        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent()) {
          t.consume();
          return;
        }
        if (result.get() != ButtonType.OK) {
          t.consume();
          return;
        }
      }
      endProgram();
    });

    primaryStage.setTitle("Ride, a Renjin IDE");
    primaryStage.getIcons().add(new Image(FileUtils.getResourceUrl("image/logo.png").toExternalForm()));
    primaryStage.setScene(scene);
    enableDragDrop(scene);
    primaryStage.show();
    consoleComponent.initRenjin(Thread.currentThread().getContextClassLoader());
  }

  private void enableDragDrop(Scene scene) {

    scene.setOnDragOver(event -> {
      Dragboard db = event.getDragboard();
      if (db.hasFiles()) {
        // I wish there was a TransferMode.OPEN but there is not
        event.acceptTransferModes(TransferMode.LINK);
        db.setDragView(new Image("image/file.png"));
      } else {
        event.consume();
      }
    });
    // Dropping over surface
    scene.setOnDragDropped(event -> {
      Dragboard db = event.getDragboard();
      boolean success = false;
      if (db.hasFiles()) {
        success = true;
        for (File file:db.getFiles()) {
          fileOpener.openFile(file, false);
        }
      }
      event.setDropCompleted(success);
      event.consume();
    });
  }

  public void addStyleSheet(String styleSheetPath) {
    scene.getStylesheets().add(FileUtils.getResourceUrl(styleSheetPath).toExternalForm());
  }

  public ObservableList<String> getStyleSheets() {
    return scene.getStylesheets();
  }

  public void endProgram() {
    Platform.exit();
    // Allow some time before calling system exist so stop() can be used to do stuff if neeed
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
      public void run() {
        System.exit(0);
      }
    };
    timer.schedule(task, 200);
  }

  private void stretch(Pane component, Pane root) {
    component.prefHeightProperty().bind(root.heightProperty());
    component.prefWidthProperty().bind(root.widthProperty());
  }

  private void stretch(Control component, Pane root) {
    component.prefHeightProperty().bind(root.heightProperty());
    component.prefWidthProperty().bind(root.widthProperty());
  }

  public ConsoleComponent getConsoleComponent() {
    return consoleComponent;
  }

  public CodeComponent getCodeComponent() {
    return codeComponent;
  }

  public EnvironmentComponent getEnvironmentComponent() {
    return environmentComponent;
  }

  public InoutComponent getInoutComponent() {
    return inoutComponent;
  }

  public Stage getStage() {
    return primaryStage;
  }

  public MainMenu getMainMenu() {
    return mainMenu;
  }

  public void setWaitCursor() {
    Platform.runLater(() -> {
      scene.setCursor(Cursor.WAIT);
      consoleComponent.setCursor(Cursor.WAIT);
    });
  }

  public void setNormalCursor() {
    Platform.runLater(() -> {
      scene.setCursor(Cursor.DEFAULT);
      consoleComponent.setCursor(Cursor.DEFAULT);
    });
  }

  public Preferences getPrefs() {
    return Preferences.userRoot().node(Ride.class.getName());
  }

  public Scene getScene() {
    return scene;
  }
}
