import java.util.ArrayList;
import java.util.Collections;
import javafx.scene.paint.Color;

public class SelfOrganisingMap {
	// Make the structure of SelfOrganisingMap
	ArrayList<Node> listOfNodes = new ArrayList<>();
	
	public SelfOrganisingMap(int numberOfNodes, int inputShape) {
		// Make that many Nodes
		for (int i = 0; i < numberOfNodes; i++) {
			Node node = new Node(inputShape);
			listOfNodes.add(node);
		}
		
	}
	
	public ArrayList<Double> getDistance(ArrayList<Double> inputVector) throws Exception {
		ArrayList<Double> distances = new ArrayList<>();
		for(Node x: listOfNodes) {
			double dist = x.getEuclideanDistance(inputVector);
			distances.add(dist);
		}
		
		return distances;
		
	}
	
	public Node getBmu(ArrayList<Double> inputVector) throws Exception {
		ArrayList<Double> distanceList = this.getDistance(inputVector);
		int bmuIndex = distanceList.indexOf(Collections.min(distanceList));
		Node bmu = listOfNodes.get(bmuIndex);
		return bmu;
	}
	
	public void optimize(Color inputColor, int totalIteration, int iter, double radius) throws Exception {
		
		// get the color or input vector
		int[] input = hex2Rgb(inputColor.toString());
		
		// Convert the given input into double array and normalize by dividing with 255.0
		ArrayList<Double> inputList = new ArrayList<>();
		
		// Vectorize the color
		for (int c = 0; c < input.length; c++) {
			double element = (input[c] * 1.0) / 255.0;
			inputList.add(element);
		}
			
		// Get the BMU
		Node bmu = this.getBmu(inputList);
		
		for (int nodeCount = 0; nodeCount < listOfNodes.size(); nodeCount++) {
			
			Node myNode = listOfNodes.get(nodeCount);
			
			boolean flag = pointInCircle(bmu.radius, bmu.centerX, bmu.centerY, myNode.centerX ,myNode.centerY);
			
			if(!flag) {
				continue;
			}
			
			myNode.updateWeights(0.1, inputList, totalIteration, iter + 1);
		}
		bmu.shrinkRadius(iter + 1, radius, totalIteration);		
		
	}
	
	
	private static int[] hex2Rgb(String colorStr) {
	    return new int[] {
	            Integer.valueOf( colorStr.substring( 2, 4 ), 16 ),
	            Integer.valueOf( colorStr.substring( 4, 6 ), 16 ),
	            Integer.valueOf( colorStr.substring( 6, 8 ), 16 ) };
	}
	
	
	private static boolean pointInCircle(double radii, double bmuX, double bmuY, double nodeX, double nodeY) {
	    double dist = Math.sqrt(Math.pow(bmuX - nodeX, 2) + Math.pow(bmuY - nodeY, 2));
	    return dist <= radii * radii;
	}
	
}

// Helper class
class Node {

    // Weight of the Node 
    ArrayList<Double> weights = new ArrayList<Double>();
    // Radius of the Neighborhood
    double radius;
    
    // Position X and y
    double x;
    double y;
    double side;
    double centerX;
    double centerY;
    
    
    // constructor 
    public Node(int numberOfWeights) {
    	
    	// Randomly initialize the weight vector between 0 and 1
    	for(int i = 0; i < numberOfWeights; i++) {
    		// Generate a random number between 0 and 1
    		double random = Math.random() * 0.02;
    		weights.add(random);
    	}
    }
    
    public void updateCenters() {
    	double centx = x + side / 2;
    	double centy = y + side / 2;
    	this.centerX = centx;
    	this.centerY = centy;
    }

    
    public double getEuclideanDistance(ArrayList<Double> inputVector) throws Exception {
    	
    	// Iterate through the loop and check distance
    	if (inputVector.size() != weights.size()) {
    		throw new Exception("Vector Dimensions Mismatch");
    	}
    	
    	double squaredSum = 0;
    	for (int i = 0; i < inputVector.size(); i++) {
    		squaredSum += Math.pow(inputVector.get(i) - this.weights.get(i), 2);
    	}
    	
    	return Math.sqrt(squaredSum);
    }
    
    public void shrinkRadius(int iteration, double initialRadius, int totalIteration) {
    	double lambda = totalIteration / Math.log(initialRadius);
    	double newRadius = initialRadius * Math.exp(-iteration / lambda);
    	this.radius = newRadius;
    }
    
    public void updateWeights(double initialLearningRate, ArrayList<Double> inputVector, int numberOfIterations, int iteration) throws Exception {
    			double learningRate = initialLearningRate * Math.exp(-iteration / numberOfIterations*1.0);
    			double distance = getEuclideanDistance(inputVector);
    			double radiusScaler = Math.exp(- Math.pow(distance, 2) / Math.pow(this.radius, 2));
    			for(int j = 0; j < this.weights.size(); j++) {
    				double element = this.weights.get(j) + (radiusScaler * learningRate)*(inputVector.get(j) - this.weights.get(j));
    				this.weights.set(j, element);
    			}	    
 
    }
    
  }
