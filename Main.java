import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Javadoc's description part:
 * Program finds the minimum distance between two cities from Turkey and shows the path from source to destination with StdDraw
 * Javadoc tags part:
 * @author Baris Cakmak, Student ID: 2022400000
 * @since 24.03.2024
 */


public class Main {
    public static final int CURRENT_CITY_INDEX = 0;
    public static void main(String[] args) throws FileNotFoundException {

        File file1 = new File("city_connections.txt"); // Creating a file object to represent our 'city_connection.txt' file
        Scanner connectionsFile = new Scanner(file1); // Creating a scanner for our 'city_connections.txt' file
        File file2 = new File("city_coordinates.txt"); // Creating a file object to represent our 'city_coordinates.txt' file
        Scanner coordinatesFile = new Scanner(file2); // Creating a scanner for our 'city_coordinates.txt' file


        ArrayList<City> cities = new ArrayList<>(); // Declaring the ArrayList which will store our City objects

        // Scanning the 'city_coordinates.txt' file line by line
        while (coordinatesFile.hasNextLine()) {
            String[] temp1 = coordinatesFile.nextLine().split(", "); // Temp refers to 'temporary' we will store 3 values in it temporarily
            String name = temp1[CURRENT_CITY_INDEX];
            int x = Integer.parseInt(temp1[1]); // Variable x stores the 'x coordinate' of the current city
            int y = Integer.parseInt(temp1[2]); // Variable y stores the 'y coordinate' of the current city

            City city = new City(name, x, y); // Creating a city object with current name, x, and y
            cities.add(city); // Adding our current City object to the ArrayList we have declared before
        }


        // Scanning the 'city_connections.txt' file line by line
        while (connectionsFile.hasNextLine()) {
            String[] temp2 = connectionsFile.nextLine().split(","); // Temp refers to 'temporary' we will store 2 values in it temporarily
            int firstIndex = findCity(temp2[0], cities); // Index of the first city in the current line
            int secondIndex = findCity(temp2[1], cities); // Index of the second city in the current line

            cities.get(firstIndex).connections.add(temp2[1]); // Add city2 to connections of city1
            cities.get(secondIndex).connections.add(temp2[0]); // Add city1 to connections of city2
        }


        Scanner input = new Scanner(System.in); // Create a scanner to get input
        String source = ""; // Store the name of source city
        String destination = ""; // Store the name of destination city


        boolean isSourceNameValid = false; // Checks if source's name is valid
        while (!isSourceNameValid) {
            System.out.print("Enter starting city: ");
            source = input.nextLine();
            if ((findCity(source, cities) == -1)) { // Basically means there aren't any city whose name is source in cities ArrayList
                System.out.println("City named '" + source + "' not found. Please enter a valid city name.");
            } else {
                isSourceNameValid = true; // If the city is in cities ArrayList exit from this loop
            }
        }

        boolean isDestinationNameTrue = false; // Checks if destination's name is valid
        while (!isDestinationNameTrue) {
            System.out.print("Enter destination city: ");
            destination = input.nextLine();
            if ((findCity(destination, cities) == -1)) { // Basically means there aren't any city whose name is source in cities ArrayList
                System.out.println("City named '" + destination + "' not found. Please enter a valid city name.");
            } else {
                isDestinationNameTrue = true; // If the city is in cities ArrayList exit from this loop
            }
        }


        ArrayList<String> unVisited= new ArrayList<>(); // Contains city names that aren't visited yet (Will be used in dijkstra method)
        ArrayList<Double> shortestDistance = new ArrayList<>(); // Contains the shortest distance to the city found so far (Will be used in dijkstra method)
        //For example --> shortestDistance[56] refers to the shortest possible distance to the city so far with the index 56

        ArrayList<String> previousNode = new ArrayList<>(); // Contains from which city the shortest distance occurs to that city (Will be used in dijkstra method)
        //For example --> previousNode[37] refers to, currently from which city it is the shortest to come to the city with the index 37

        for (City currentCity: cities) {
            unVisited.add(currentCity.cityName);
        }

        for (int i = 0; i<81; i++) {
            shortestDistance.add(Double.MAX_VALUE); // Before the algorithm is executed, we set all shortest distances to infinity
        }

        for (int i = 0; i<81; i++) {
            previousNode.add(""); // Assigning '' to previous nodes as initial value to avoid errors
        }


        ArrayList<String> path; // Stores the path from source to destination

        path = dijkstra(cities, unVisited, shortestDistance, previousNode, source, destination); //giving parameters to dijkstra algorithm and returning the path

        // To evaluate the final result we must consider all possible scenarios
        // 1- Source and destination name are the same
        // 2- Source and destination are different but there is no path connecting them
        // 3- Source and destination are different and there is a path connecting them
        if (!source.equals(destination) && path.size()==1){ // Case 2
            System.out.println("No path could be found.");
        }
        else{ // Case 1 and Case 3
            System.out.print("Total Distance: ");
            System.out.printf("%.2f.%n", shortestDistance.get(findCity(destination, cities)));
            System.out.print("Path: ");
            for (String currentCity: path){
                System.out.print(" " + cities.get(findCity(currentCity, cities)).cityName); // Print cities
                if (path.indexOf(currentCity) != path.size()-1){ // If currentCity is the last element of path, don't print ' ->'
                    System.out.print(" ->");
                }
            }


            int width = 2377; // Width of the window
            int height = 1055; // Height of the window
            StdDraw.setCanvasSize(width / 2, height / 2);
            StdDraw.setXscale(0, width);
            StdDraw.setYscale(0, height);
            StdDraw.picture(width / 2.0, height / 2.0, "map.png", width, height);
            StdDraw.enableDoubleBuffering(); // Activate double buffering to get a smooth view


            for (City currentCity : cities){
                //If currentCity is in the path set color to BOOK_LIGHT_BLUE, otherwise set it to GRAY
                if (path.contains(currentCity.cityName)) {
                    StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                }
                else {
                    StdDraw.setPenColor(StdDraw.GRAY);
                }

                StdDraw.filledCircle(currentCity.x, currentCity.y, 7); // Draw the circles to represent cities
                StdDraw.setFont(new Font("Helvetica Bold", Font.BOLD, 12));
                StdDraw.text(currentCity.x, currentCity.y + 15, currentCity.cityName); // Write the name of the city slightly on top of it


                // Drawing the lines to represent connections
                for (String neighbours : currentCity.connections) { // Iterate through currentCity's connections
                    StdDraw.setPenColor(StdDraw.GRAY);
                    City neighbourCity = cities.get(findCity(neighbours, cities));
                    StdDraw.line(currentCity.x, currentCity.y, neighbourCity.x, neighbourCity.y); // Draw a line through the current city to all neighbours
                }
            }


            // Drawing the path
            if (!source.equals(destination)){ // Case 1 is eliminated only Case 3 left
                for (int i = 0; i < path.size(); i++) {
                    if (i == path.size() - 1) {  // Since we are drawing through index i element to i+1'th element, we should not do the last iteration
                        break;
                    }
                    int currentIndex = findCity(path.get(i), cities); // Store the index of the index i city in path ArrayList
                    int otherIndex = findCity(path.get(i + 1), cities); // Store the index of the i+1'th city in path ArrayList

                    StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                    StdDraw.setPenRadius(0.008);
                    StdDraw.line(cities.get(currentIndex).x, cities.get(currentIndex).y, cities.get(otherIndex).x, cities.get(otherIndex).y); // Draw the line through index i city to i+1 city in the path ArrayList
                }
            }
            StdDraw.show(); // Showing all the drawings and texts until now
        }
    }

    /**
     * Finds and returns the index of the city
     * @param cityName Name of the city we want to find the index of
     * @param cities The ArrayList that stores city objects
     * @return The index of the city provided, if it does not exist return -1
     */
    public static int findCity(String cityName, ArrayList<City> cities) {
        int index = 0; // Index counter
        for (City city : cities) {
            if (city.cityName.equals(cityName)) { // That means we found the city
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Finds and return the distance between 2 cities
     * @param city1 Name of the first city
     * @param city2 Name of the second city
     * @param cities The ArrayList that stores city objects
     * @return The distance between city1 and city2
     */
    public static double findDistance(String city1, String city2, ArrayList<City> cities) {

        int x1 = cities.get(findCity(city1, cities)).x; // X coordinate of city1
        int y1 = cities.get(findCity(city1, cities)).y; // Y coordinate of city1
        int x2 = cities.get(findCity(city2, cities)).x; // X coordinate of city2
        int y2 = cities.get(findCity(city2, cities)).y; // Y coordinate of city2
        return Math.pow((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)), 0.5); // Distance formula
    }

    /**
     * Finds the shortest distance between source and destination, then returns the path from source to destination with the help of Dijkstra's algorithm
     * @param cities ArrayList that stores city objects
     * @param unVisited ArrayList that stores the name of cities that are not visited yet, initially every city name is in unVisited
     * @param shortestDistance Arraylist that stores the shortest possible distance to a city
     * @param previousNode ArrayList that stores the name of cities which is the nearest connection of a city
     * @param source The string that stores the name of the source city
     * @param destination The string that stores the name of the destination city
     * @return An ArrayList that contains the path from source to destination in order
     */

    public static ArrayList<String> dijkstra(ArrayList<City> cities, ArrayList<String> unVisited, ArrayList<Double> shortestDistance, ArrayList<String> previousNode, String source, String destination){
        ArrayList<String> returning = new ArrayList<>(); // The ArrayList that will contain the elements of our path at the end, this ArrayList will be returned
        shortestDistance.set(findCity(source, cities), 0.0); // Shortest distance from source to source is 0
        String currentCity = source; // Stores our current city, initial value is source since we are beginning from the source city

        // unVisited contains the names of cities, in first iteration its size is equivalent to length of cities ArrayList
        while (unVisited.size()>1){ // Main condition, while loop executes until there is only one city left
            int indexCurrent = findCity(currentCity, cities); // Index of our current city
            for (String neighbour: cities.get(indexCurrent).connections){ // Iterate through the connections of our current city
                int indexNeighbour = findCity(neighbour, cities); // Index of the current connection of our current city

                if(!unVisited.contains(neighbour)) { // If the neighbour was the current city in a previous iteration, disregard this neighbour, move to the other neighbour
                    continue;
                }

                double currentDistance = findDistance(currentCity, neighbour, cities); // Find the distance between currentCity and its current neighbour
                double total = currentDistance + shortestDistance.get(indexCurrent); // Implicitly calculating one of the distance from source to the neighbour
                if (total < shortestDistance.get(indexNeighbour)){ // If we find a new shortest distance
                    shortestDistance.set(indexNeighbour, total); // Set the shortest distance to neighbour to total
                    previousNode.set(indexNeighbour, currentCity); // Set to previous node to neighbour to currentCity
                }
            }

            //After iterating through the current city's connections, we are done with this city, so we can mark it as visited by removing it from unVisited
            unVisited.remove(currentCity);

            // Before going to the next iteration of the while loop, we need a new current city
            // Our current city will be a city in the unVisited ArrayList that has the smallest shortestDistance value
            double smallestValue = Double.MAX_VALUE; // Assign the initial value of smallestValue to infinity
            String smallestCity = ""; // Declare the string in which we will store the city name that has the smallest shortestDistance value in unVisited

            for (String elem: unVisited){ // Iterate through unvisited city names
                if (shortestDistance.get(findCity(elem, cities)) <= smallestValue){ // If our current shortestDistance is less than smallestValue
                    smallestValue = shortestDistance.get(findCity(elem, cities)); // Set smallestValue to current shortestDistance
                    smallestCity = elem; // Set smallest city to current element
                }
            }

            //After this loop, we must have found the smallest distanced city
            //Note: <= is very important in the previous for loop, if it would be < there would be problems in some source destination paris
            currentCity = smallestCity; // Assign smallestCity name to currentCity name
        }

        // After our while loop is completed we now have important data's in shortestDistance and previousNode ArrayLists

        // In the while loop below, we will start from destination and back trace to source with the help of previous node and shortest distance values
        // For example we will search the index of destination and find the shortest distance to it and from where it is
        // By finding where we came to destination 1 step before, we will apply this until we encounter source

        String current = destination; // Stores the name of our current city (node)
        returning.add(destination); // Adding destination to our returning list
        while (!current.equals(source)){ // While we have not encountered source
            if (previousNode.get(findCity(current, cities)).isEmpty()) { // If there is not any previous node to this city, this city is unreachable
                break;
            }
            returning.addFirst(previousNode.get(findCity(current, cities))); // Adding previous node of current as the first element of returning array
            current = previousNode.get(findCity(current, cities)); // Find current city's previous node and assign it to our new current
        }
        // After all we will have an ArrayList of Strings that contains the path from source to path
        return returning; // Returning the path
    }
}