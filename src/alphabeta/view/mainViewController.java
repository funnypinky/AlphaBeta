/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.view;

import alphabeta.AlphaBeta;
import alphabeta.DICOM.CTImage;
import alphabeta.DICOM.Contour;
import alphabeta.DICOM.DICOMPlan;
import alphabeta.DICOM.Structure;
import alphabeta.structure.Patient;
import alphabeta.structure.StructureSet;
import alphabeta.thread.LoadImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

/**
 *
 * @author shaesler
 */
public class mainViewController implements Initializable {

    @FXML
    private TreeView imageTreeView;

    private ObservableList<CTImage> images = FXCollections.observableArrayList();

    private AlphaBeta mainApp;

    @FXML
    private Canvas dicomView;

    @FXML
    private StackPane stackedPane;

    @FXML
    private ScrollBar imageScroll;

    @FXML
    private Canvas structurCanvas;

    private Patient patient = new Patient();

    private List<CTImage> activeImages = new ArrayList<>();

    private ProgressForm pf = new ProgressForm();

    private double scaleFactor = 1.5;

    @FXML
    private TreeView structureTree;

    @FXML
    private Label zLabel;
    
    @FXML
    private Label doseMax;

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
        imageScroll.setMin(0);
        imageScroll.setMax(3);
        imageScroll.setValue(0);
        zLabel.setVisible(false);
        imageTreeView.setCellFactory(e -> new CustomCell());
        imageTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        imageTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (oldSelection != newSelection || (TreeItem) oldSelection != ((TreeItem) newSelection).getParent()) {
                if (((TreeItem) newSelection).getValue().equals("Topo")) {
                    activeImages.clear();
                    activeImages.addAll(patient.getTopo());
                    structurCanvas.setVisible(false);
                    zLabel.setVisible(false);
                    doseMax.setVisible(false);
                }
                if (((TreeItem) newSelection).getValue() instanceof StructureSet
                        || ((TreeItem) newSelection).getValue() instanceof Structure
                        || ((TreeItem) newSelection).getValue() instanceof DICOMPlan) {
                    activeImages.clear();
                    activeImages.addAll(patient.getCtImage());
                    structurCanvas.setVisible(true);
                    zLabel.setVisible(true);
                    doseMax.setVisible(true);
                }
                imageScroll.setMax(activeImages.size() - 1);
                if (imageScroll.getValue() > imageScroll.getMax()) {
                    imageScroll.setValue(imageScroll.getMax());
                }
                try {
                    if (!activeImages.isEmpty()) {
                        this.dicomView.getGraphicsContext2D().clearRect(0, 0, this.dicomView.getWidth(), this.dicomView.getHeight());
                        paintImage(SwingFXUtils.toFXImage(activeImages.get((int) imageScroll.getValue()).getDicom().getBufferedImage(), null));
                        TreeItem tempItem = (TreeItem) imageTreeView.getSelectionModel().getSelectedItems().get(0);
                        if (tempItem.getValue() instanceof StructureSet) {
                            StructureSet temp = (StructureSet) tempItem.getValue();
                            paintStructures((int) imageScroll.getValue(), temp.getStructure());
                        } else if (tempItem.getValue() instanceof Structure) {
                            StructureSet temp = (StructureSet) tempItem.getParent().getValue();
                            paintStructures((int) imageScroll.getValue(), temp.getStructure());
                        } else if (tempItem.getValue() instanceof DICOMPlan) {
                            TreeItem itemTree = (TreeItem) tempItem.getChildren().get(0);
                            StructureSet temp = (StructureSet) itemTree.getValue();
                            paintStructures((int) imageScroll.getValue(), temp.getStructure());
                        }

                        zLabel.setText(String.format("z: %.2f mm", activeImages.get((int) imageScroll.getValue()).getZ()));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(mainViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
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
                    TreeItem tempItem = (TreeItem) imageTreeView.getSelectionModel().getSelectedItems().get(0);
                    if (tempItem.getValue() instanceof StructureSet) {
                        StructureSet temp = (StructureSet) tempItem.getValue();
                        paintStructures((int) imageScroll.getValue(), temp.getStructure());
                    } else if (tempItem.getValue() instanceof Structure) {
                        StructureSet temp = (StructureSet) tempItem.getParent().getValue();
                        paintStructures((int) imageScroll.getValue(), temp.getStructure());
                    } else if (tempItem.getValue() instanceof DICOMPlan) {
                        TreeItem itemTree = (TreeItem) tempItem.getChildren().get(0);
                        StructureSet temp = (StructureSet) itemTree.getValue();
                        paintStructures((int) imageScroll.getValue(), temp.getStructure());
                    }
                    zLabel.setText(String.format("z: %.2f mm", activeImages.get(newValue.intValue()).getZ()));
                }
            } catch (IOException ex) {
                Logger.getLogger(mainViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void paintStructures(int indexOfCt, List<Structure> structures) {
        this.structurCanvas.getGraphicsContext2D().clearRect(0, 0, this.structurCanvas.getWidth(), this.structurCanvas.getHeight());
        for (Structure item : structures) {
            if (item.isVisible()) {
                paintStructur(item, activeImages.get(indexOfCt));
            }
        }
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
            LoadImage loadTask = new LoadImage(files);
            loadTask.setOnSucceeded((WorkerStateEvent event1) -> {
                patient = loadTask.getValue();
                structureTree.setRoot(new TreeItem(patient.getPatientName()));
                imageTreeView.setRoot(new TreeItem(patient.getPatientName()));
                if (!patient.getTopo().isEmpty()) {
                    TreeItem topoItem = new TreeItem("Topo");
                    imageTreeView.getRoot().getChildren().add(topoItem);
                }
                if (!patient.getPlan().isEmpty()) {
                    for (DICOMPlan plan : patient.getPlan()) {
                        TreeItem planItem = new TreeItem(plan);

                        if (!patient.getStructureSet().isEmpty()) {
                            for (StructureSet structureSet : patient.getStructureSet()) {
                                if (plan.getReferenceUIDStructure() == null ? structureSet.getUid() == null : plan.getReferenceUIDStructure().equals(structureSet.getUid())) {
                                    TreeItem item = new TreeItem(structureSet);
                                    item.setExpanded(true);
                                    for (Structure structItem : structureSet.getStructure()) {
                                        CheckBoxTreeItem subItem = new CheckBoxTreeItem(structItem);
                                        item.getChildren().add(subItem);
                                    }
                                    planItem.getChildren().add(item);
                                }
                            }
                        }
                        structureTree.getRoot().getChildren().add(planItem);
                        imageTreeView.getRoot().getChildren().add(planItem);
                    }
                }
                imageTreeView.getRoot().setExpanded(true);
                structureTree.getRoot().setExpanded(true);
                pf.getDialogStage().close();
            });
            try {
                pf.activateProgressBar(loadTask);
            } catch (InterruptedException ex) {
                Logger.getLogger(mainViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            pf.getDialogStage().show();
            Thread th = new Thread(loadTask);
            th.setDaemon(true);
            th.start();
        }

    }

    private Rectangle paintBox(Color color) {
        Rectangle rectangle = new Rectangle(10, 10);
        rectangle.setFill(color);
        return rectangle;
    }

    public void paintImage(Image image) {
        GraphicsContext gc = this.dicomView.getGraphicsContext2D();
        gc.drawImage(image, 0, 0, image.getWidth() * scaleFactor, image.getHeight() * scaleFactor);

    }

    public void paintStructur(Structure structure, CTImage ctImage) {
        GraphicsContext gc = this.structurCanvas.getGraphicsContext2D();

        Contour slice = null;
        gc.setStroke(structure.getColor());
        //Find the correct Slice to paint Contour;
        for (Contour item : structure.getPoints()) {
            if (item.getUidCT().equals(ctImage.getUID())) {
                slice = item;
            }
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

    class CustomCell extends TreeCell<Object> {

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

                    cellBox.getChildren().addAll(checkBox, paintBox(((Structure) item).getColor()), label);
                    checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        ((Structure) item).setVisible(checkBox.isSelected());
                        TreeItem tempItem = (TreeItem) imageTreeView.getSelectionModel().getSelectedItems().get(0);
                        if (tempItem.getValue() instanceof StructureSet) {
                            StructureSet temp = (StructureSet) tempItem.getValue();
                            paintStructures((int) imageScroll.getValue(), temp.getStructure());
                        } else if (tempItem.getValue() instanceof Structure) {
                            StructureSet temp = (StructureSet) tempItem.getParent().getValue();
                            paintStructures((int) imageScroll.getValue(), temp.getStructure());
                        }
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
