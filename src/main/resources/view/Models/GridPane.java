package view.Models;

public class GridPane extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Crear un GridPane
        GridPane gridPane = new GridPane();
        
        // Configurar hgap y vgap
        gridPane.setHgap(10); // Espaciado horizontal entre columnas
        gridPane.setVgap(10); // Espaciado vertical entre filas
        
        // Agregar algunos botones de ejemplo al GridPane
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button("Button " + (i * 3 + j + 1));
                gridPane.add(button, i, j); // Agrega el botón a la celda (i, j)
            }
        }

        // Crear un BorderPane y agregar el GridPane al centro
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPane);

        // Configurar la escena y el escenario
        Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("GridPane Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
