import javafx.application.*;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.shape.*;
import javafx.scene.Scene;
import javafx.scene.paint.*;
import java.util.*;
import javafx.scene.control.*;


public class SOMDemo extends Application {
	
	ArrayList<Rectangle> rectList = new ArrayList<>();
	ArrayList<Color> colorList = new ArrayList<>();
	int totalIteration = 4000;
	int dimx = 40;
	int dimy = 40;
	int totalNodes = dimx * dimy;
	ArrayList<ArrayList<Node>> finalColorList = new ArrayList<>();
	double radiusInitial;
	int timeConstant = (int)(totalIteration * 1.4);
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Text Box to show iterations
		Label label = new Label("Iteration: 0");
		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(5, 5, 5, 5));
		bp.setBottom(label);
		
        // Initialize Color List
		colorList.add(Color.rgb(0, 0, 255)); // blue
		colorList.add(Color.rgb(0, 50, 255)); // blue
		colorList.add(Color.rgb(0, 0, 139)); // deep blue
		colorList.add(Color.rgb(0, 255, 0)); // green
		colorList.add(Color.rgb(0, 100, 0)); // deep green
		colorList.add(Color.rgb(255, 0, 0)); // red
		colorList.add(Color.rgb(255, 69, 0)); // orange
		colorList.add(Color.rgb(255, 20, 147)); // pink
		colorList.add(Color.rgb(255, 255, 0)); // Yellow
	
		
		
		// create instance of self organising map with number of elements in output layer
		SelfOrganisingMap som = new SelfOrganisingMap(totalNodes, 3);
		ArrayList<Node> nodeList = som.listOfNodes;
		
		
		// Initialization
		GridPane pane = new GridPane();
		pane.setPadding(new Insets(5, 5, 5, 5));
		int side = 10;
		double xCoordinate = 0;
		double yCoordinate = 0;
		int counter = 0;
		for (int i = 0; i < dimx; i++) {
			for(int j = 0; j < dimy; j++) {				
				Rectangle rectangle = new Rectangle(xCoordinate , yCoordinate, side, side);
				// Generate a random color and fill the rectangles
				ArrayList<Double> weights = nodeList.get(counter).weights;
				int r = (int)(weights.get(0) * 25500.0 / 2.5);
				int g = (int)(weights.get(1) * 25500.0 / 2.5);
				int b = (int)(weights.get(2) * 25500.0 / 2.5);
				
				// Set X position and Y position
				nodeList.get(counter).x = xCoordinate;
				nodeList.get(counter).y = yCoordinate;
				nodeList.get(counter).side = side;
				
				// update centers
				nodeList.get(counter).updateCenters();
				
			 
				rectangle.setStroke(Color.BLACK);
				rectangle.setFill(Color.rgb(r, g, b));
				rectList.add(rectangle);
				pane.add(rectangle, j, i);
				xCoordinate += side;
				counter++;
			}
			xCoordinate = 0;
			yCoordinate += side;
		}
		
		bp.setTop(pane);
		
		
		
	 new Thread(() -> {
			try {
				// Write your update code here
				for (int iter = 0; iter < totalIteration ; iter++) {
					for (int i = 0; i < colorList.size(); i++) {
						
						// Try optimizing
						this.radiusInitial = Math.max(pane.getWidth(), pane.getHeight()) / 2;
						som.optimize(colorList.get(i), timeConstant, iter, radiusInitial);
						final int updateIteration = iter + 1;
						
						// Update Colors
						Platform.runLater(() -> {
							// Update Everyone's color
							for (int index = 0; index < rectList.size(); index++) {
								Node theNode = nodeList.get(index);
								ArrayList<Double> rgbColor = theNode.weights;
								int red = (int)(rgbColor.get(0) * 255);
								int green = (int)(rgbColor.get(1) * 255);
								int blue = (int)(rgbColor.get(2) * 255);
								
								rectList.get(index).setFill(Color.rgb(red, green, blue));
								label.setText("Iteration: " + updateIteration );
							}
						});
				  }
					Thread.sleep(4);
			  }
				
		   } catch(Exception ex) {
				ex.printStackTrace();
			}
		}).start();
	 
	 
	 	Scene scene = new Scene(bp);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Self Organising Maps");
		primaryStage.show();
	}
	
	

	public static void main(String[] args) {
		Application.launch(args);
	}

}
