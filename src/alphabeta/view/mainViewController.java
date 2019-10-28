/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.view;

import alphabeta.AlphaBeta;
import alphabeta.DICOM.CTImageStack;
import alphabeta.DICOM.TransversalImage;
import alphabeta.DICOM.ContourSlice;
import alphabeta.DICOM.DICOMPlan;
import alphabeta.DICOM.Structure;
import alphabeta.structure.Patient;
import alphabeta.structure.StructureSet;
import alphabeta.thread.LoadThread;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

/**
 *
 * @author shaesler
 */
public class mainViewController implements Initializable {

    @FXML
    private TreeView planTreeView;

    private final ObservableList<TransversalImage> images = FXCollections.observableArrayList();

    private final AlphaBeta mainApp;

    @FXML
    private Canvas dicomView;

    @FXML
    private StackPane stackedPane;

    @FXML
    private ScrollBar imageScroll;

    @FXML
    private Canvas structurCanvas;

    private Patient patient = new Patient();

    private DICOMPlan plan;

    private StructureSet ss;

    private final List<TransversalImage> activeImages = new ArrayList<>();

    private final ProgressForm pf = new ProgressForm();

    private double scaleFactor = 2.5;

    @FXML
    private TreeView detailsTreeView;

    @FXML
    private Label zLabel;

    @FXML
    private Label doseMax;

    @FXML
    private Group structureGroup;

    @FXML
    private TextField prescriptionDose;

    @FXML
    private AnchorPane zLabelPane;

    /**
     * @param mainApp
     * @TODO-20190306: Change to Topo from CT - disable -done
     *
     */
    public mainViewController(AlphaBeta mainApp) {
        this.mainApp = mainApp;

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.dicomView.widthProperty().bind(this.stackedPane.heightProperty());
        this.dicomView.heightProperty().bind(this.stackedPane.heightProperty());

        this.structurCanvas.widthProperty().bind(this.stackedPane.heightProperty());
        this.structurCanvas.heightProperty().bind(this.stackedPane.heightProperty());
        
        this.zLabelPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Height: " + newSceneWidth);
            }
        });
        
        this.zLabelPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
            }
        });
        //this.zLabelPane.prefHeightProperty().bind(this.stackedPane.heightProperty());

        this.doseMax.setLayoutX(50);

        imageScroll.setMin(0);
        imageScroll.setMax(3);
        imageScroll.setValue(0);
        zLabel.setVisible(false);
        detailsTreeView.setCellFactory(e -> new StructureCustomCell());
        detailsTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        planTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        planTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (oldSelection != newSelection || (TreeItem) oldSelection != ((TreeItem) newSelection).getParent()) {
                if (((TreeItem) newSelection).getValue().equals("Topo")) {
                    activeImages.clear();
                    activeImages.addAll(patient.getTopo().values());
                    structurCanvas.setVisible(false);
                    zLabel.setVisible(false);
                    doseMax.setVisible(false);
                }
                if (((TreeItem) newSelection).getValue() instanceof DICOMPlan) {
                    this.plan = (DICOMPlan) ((TreeItem) newSelection).getValue();
                    this.detailsTreeView.setRoot(new TreeItem(this.plan));
                    this.detailsTreeView.getRoot().setExpanded(true);
                    this.patient.getStructureSet().values().forEach(item -> {
                        if (item.getUid().equals(this.plan.getReferenceUIDStructure())) {
                            this.ss = item;
                            TreeItem structureItem = new TreeItem(item.getName());
                            structureItem.setExpanded(true);
                            this.detailsTreeView.getRoot().getChildren().add(structureItem);
                            item.getStructure().forEach(structure -> {
                                structureItem.getChildren().add(new CheckBoxTreeItem(structure));
                            });
                        }
                    });
                }
            }
        });

        detailsTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (oldSelection != newSelection || (TreeItem) oldSelection != ((TreeItem) newSelection).getParent()) {
                displayImages(this.ss);
            }
        });

        stackedPane.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY();
            double step = deltaY < 0 ? 1 : -1;
            double value = imageScroll.getValue();
            double temp = value + step;
            if ((temp >= imageScroll.getMin()) && (temp <= imageScroll.getMax())) {
                imageScroll.setValue(temp);
            }
        });

        imageScroll.valueProperty().addListener((obs, oldValue, newValue) -> {
            try {
                if (!activeImages.isEmpty()) {
                    this.dicomView.getGraphicsContext2D().clearRect(0, 0, this.dicomView.getWidth(), this.dicomView.getHeight());
                    paintImage(SwingFXUtils.toFXImage(activeImages.get(newValue.intValue()).getDicom().getBufferedImage(), null));
                    paintStructures((int) imageScroll.getValue(), ss.getStructure());
                    zLabel.setText(String.format("z: %.2f mm", activeImages.get(newValue.intValue()).getZ()));
                }
            } catch (IOException ex) {
                Logger.getLogger(mainViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void displayImages(StructureSet ss) {
        activeImages.clear();

        for (CTImageStack ct : this.patient.getCtImage().values()) {
            if (ct.getReferenceFrame().equals(ss.getReferenceCtUID())) {
                activeImages.addAll(ct.getImages());
            }
        }

        structurCanvas.setVisible(true);
        zLabel.setVisible(true);
        doseMax.setVisible(true);

        imageScroll.setMax(activeImages.size() - 1);
        if (imageScroll.getValue() > imageScroll.getMax()) {
            imageScroll.setValue(imageScroll.getMax());
        }
        try {
            if (!activeImages.isEmpty()) {
                this.dicomView.getGraphicsContext2D().clearRect(0, 0, this.dicomView.getWidth(), this.dicomView.getHeight());
                paintImage(SwingFXUtils.toFXImage(activeImages.get((int) imageScroll.getValue()).getDicom().getBufferedImage(), null));
                paintStructures((int) imageScroll.getValue(), ss.getStructure());
                zLabel.setText(String.format("z: %.2f mm", activeImages.get((int) imageScroll.getValue()).getZ()));
            }
        } catch (IOException ex) {
            Logger.getLogger(mainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void paintStructures(int indexOfCt, List<Structure> structures) {
        this.structurCanvas.getGraphicsContext2D().clearRect(0, 0, this.structurCanvas.getWidth(), this.structurCanvas.getHeight());
        structures.stream().filter((item) -> (item.isVisible())).forEachOrdered((item) -> {
            paintStructur(item, activeImages.get(indexOfCt));
        });
    }

    @FXML
    public void shutdownApp(Event event) {
        System.exit(0);
    }

    @FXML
    public void loadImage(ActionEvent event) {
        FileChooser loadImage = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DICOM Dateien (*.dcm)", "*.dcm");
        loadImage.getExtensionFilters().add(extFilter);
        List<File> files = loadImage.showOpenMultipleDialog(null);
        if (files != null && !files.isEmpty()) {
            ObservableList filess = FXCollections.observableArrayList();
            filess.addAll(files);
            LoadThread loadTask = new LoadThread(files);
            loadTask.setOnSucceeded((WorkerStateEvent event1) -> {
                patient = loadTask.getValue();
                planTreeView.setRoot(new TreeItem(patient.getPatientName()));
                if (!patient.getTopo().isEmpty()) {
                    TreeItem topoItem = new TreeItem("Topo");
                    planTreeView.getRoot().getChildren().add(topoItem);
                }
                if (!patient.getDICOMPlan().isEmpty()) {
                    patient.getDICOMPlan().values().stream().map((DICOMPlan planTemp) -> {
                        TreeItem planItem = new TreeItem(planTemp);
                        return planItem;
                    }).forEachOrdered((planItem) -> {
                        planTreeView.getRoot().getChildren().add(planItem);
                    });
                }
                planTreeView.getRoot().setExpanded(true);
                pf.getDialogStage().close();
            });
            try {
                pf.activateProgressBar(loadTask);
            } catch (InterruptedException ex) {
                Logger.getLogger(mainViewController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            pf.getDialogStage().show();
            Thread th = new Thread(loadTask);
            th.setDaemon(true);
            th.start();
        }

    }

    public void paintImage(Image image) {
        GraphicsContext gc = this.dicomView.getGraphicsContext2D();
        scaleFactor = this.dicomView.getHeight() / image.getHeight();
        gc.drawImage(image, 0, 0, image.getWidth() * scaleFactor, image.getHeight() * scaleFactor);

    }

    /**
     * @param structure
     * @param ctImage
     *
     */
    public void paintStructur(Structure structure, TransversalImage ctImage) {
        GraphicsContext gc = this.structurCanvas.getGraphicsContext2D();
        DecimalFormat format = new DecimalFormat("###000.##");

        gc.setStroke(structure.getColor());
        //Find the correct Slice to paint ContourSlice;
        for (ContourSlice item : structure.getPoints()) {
            ContourSlice slice = null;
            if (item.getUidCT().equals(ctImage.getUID())) {
                slice = item;
            }
            if (slice != null) {
                gc.beginPath();
                for (int i = 0; i < slice.getPoints().length; i++) {
                    double[] point = slice.getPoints()[i];
                    double oX = ctImage.getOriginX();
                    double oY = ctImage.getOriginY();

                    double xPx = Math.abs(point[0] - oX) / ctImage.getPixelSpaceX();
                    double yPx = Math.abs(point[1] - oY) / ctImage.getPixelSpaceY();
                    if (i == 0) {
                        gc.moveTo(xPx * scaleFactor, yPx * scaleFactor);
                    } else {
                        gc.lineTo(xPx * scaleFactor, yPx * scaleFactor);
                    }
                }
                gc.closePath();
                gc.stroke();
            }
        }
    }

    // Methode für die Berechnung der Distanz zwischen zwei Punkten.
    public static double getDistance(double xP1, double yP1, double xP2, double yP2) {
        return Math.sqrt(Math.pow((xP2 - xP1), 2) + Math.pow((yP2 - yP1), 2));

    }

    class StructureCustomCell extends TreeCell<Object> {

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            // If the cell is empty we don't show anything.
            if (isEmpty()) {
                setGraphic(null);
                setText(null);
            } else {
                // We only show the custom cell if it is a leaf, meaning it has
                // no children.
                if (this.getTreeItem().isLeaf() && item instanceof Structure) {

                    // A custom HBox that will contain your check box, label and
                    // button.
                    HBox cellBox = new HBox();
                    cellBox.setAlignment(Pos.CENTER_LEFT);
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(((Structure) item).isVisible());
                    Label label = new Label(item.toString());
                    // Here we bind the pref height of the label to the height of the checkbox. This way the label and the checkbox will have the same size. 
                    checkBox.prefHeightProperty().bind(label.prefHeightProperty());
                    checkBox.setStyle("-fx-base:#" + ((Structure) item).getColor().toString().substring(2));
                    cellBox.getChildren().addAll(checkBox, label);
                    checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        ((Structure) item).setVisible(checkBox.isSelected());
                        paintStructures((int) imageScroll.getValue(), ss.getStructure());
                    });
                    // We set the cellBox as the graphic of the cell.
                    setGraphic(cellBox);
                    setText(null);
                } else {
                    // If this is the root we just display the text.
                    setGraphic(null);
                    setText(item.toString());
                }
            }
        }
    }

}
