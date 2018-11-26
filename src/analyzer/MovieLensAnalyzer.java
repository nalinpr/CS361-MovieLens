package analyzer;

import graph.Graph;
import graph.GraphAlgorithms;
import util.DataLoader;
import data.Movie;
import data.Reviewer;

import java.util.*;

public class MovieLensAnalyzer {

	public static void main(String[] args) {
		// Your program should take two command-line arguments: 
		// 1. A ratings file
		// 2. A movies file with information on each movie e.g. the title and genres
		args = new String[]{"./src/ml-latest-small/movies.csv", "./src/ml-latest-small/ratings.csv"};
		if (args.length != 2) {
			System.err.println("Usage: java MovieLensAnalyzer [ratings_file] [movie_title_file]");
			System.exit(-1);
		}
		// === DATA LOAD ===
		long startTime = System.nanoTime();
		DataLoader dl = new DataLoader();
		dl.loadData("./src/ml-latest-small/movies.csv", "./src/ml-latest-small/ratings.csv");

		// === PROCESSING STEP ===

		// Map of Movies
		Map<Integer, Movie> movies = dl.getMovies();
		// Map of Reviews
		Map<Integer, Reviewer> reviewers = dl.getReviewers();
		// Map of each movieID to an ArrayList of 6 ArrayLists
		// The initial ArrayList contains all reviews, the next five sort the reviews by their rating
		HashMap<Integer, ArrayList<ArrayList<Integer>>> movieMap = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
		// For a given movieID
		for (Integer id : movies.keySet()) {
			movieMap.put(id, new ArrayList<ArrayList<Integer>>(6));
			for (int i = 0; i < 6; i++) {
				movieMap.get(id).add(new ArrayList<Integer>());
			}
			// For each reviewer
			for (Integer reviewer : reviewers.keySet()) {
				// If they reviewed the movie, add them into out master data structure
				if (reviewers.get(reviewer).ratedMovie(id)) {
					int rating = (int) reviewers.get(reviewer).getMovieRating(id);
					movieMap.get(id).get(rating).add(reviewer);
					movieMap.get(id).get(0).add(reviewer);
				}
			}
		}

		// === START TEXT INTERFACE ===
		Scanner sc = new Scanner(System.in);

		System.out.println("=============== Welcome to MovieLens Analyzer ===============");
		System.out.println("The files being analyzed are:");
		System.out.println("./src/ml-latest-small/ratings.csv");
		System.out.println("./src/ml-latest-small/movies_top_1000.csv");

		System.out.println("\nThere are 2 choices for defining adjacency:");
		System.out.println("[Option 1] u and v are adjacent if avg reviews are within +- 0.1 points of each other and they have more than 1 genre in common");
		System.out.println("[Option 2] u and v are adjacent if the same 12 users gave the same rating to both movies");

		System.out.print("\nChoose an option to build the graph (1-2): ");
		int choice = sc.nextInt();
		Graph<Movie> movieGraph;
		if (choice == 1){
			System.out.print("\nCreating graph...");
			long start = System.nanoTime();
			movieGraph = makeGraphOne(movieMap,movies);
			long end = System.nanoTime();
			System.out.println("The graph has been created");
			System.out.println("Time to build: "+ (end-start)/1000000000.0+" secs");
		} else if (choice == 2){
			System.out.print("\nCreating graph...");
			long start = System.nanoTime();
			movieGraph = makeGraphTwo(movieMap,movies);
			long end = System.nanoTime();
			System.out.println("The graph has been created");
			System.out.println("Time to build: "+ (end-start)/1000000000.0+" secs");
		} else {
			throw new AssertionError("Choice is not 1 or 2");
		}

		System.out.println("\n[Option 1] Print graph stats!");
		System.out.println("[Option 2] Print node info");
		System.out.println("[Option 3] Display shortest path between 2 nodes");
		System.out.println("[Option 4] Quit");

		System.out.print("\nChoose an option to build the graph (1-4): ");
		choice = sc.nextInt();

		if(choice == 1){
			int numNodes = movieGraph.numVertices();
			int numEdges = movieGraph.numEdges();
			double density = 2.0*numEdges/(numNodes*(numNodes-1));

			int maxDegree = Integer.MIN_VALUE;
			for(Movie movie: movieGraph.getVertices()){
				if(movieGraph.getNeighbors(movie).size() > maxDegree){
					maxDegree = movieGraph.getNeighbors(movie).size();
				}
			}

			int[][] shortestPaths = GraphAlgorithms.floydWarshall(movieGraph,movies);
			int diameter = Integer.MIN_VALUE;
			double avgLength = 0;
			int numOfPaths = 0;
			for (int i = 0; i < 1000; i++) {
				for (int j = 0; j < 1000; j++) {
					int currPath = shortestPaths[i][j];
					if (currPath == 1001) continue; // This represents infinity
					if (currPath > diameter) diameter = currPath;
					avgLength += currPath;
					numOfPaths++;
				}
			}
			avgLength = avgLength/numOfPaths;

			System.out.println("\nNumber of Nodes: "+numNodes);
			System.out.println("Number of Edges: "+numEdges);
			System.out.println("Density of Graph: "+density);
			System.out.println("Max degree: "+maxDegree);
			System.out.println("Diameter of Graph: "+diameter);
			System.out.println("Avg length of Graph: "+avgLength);
		} else if (choice == 2){
			System.out.print("\nChoose a movie id to print information about (1-1000): ");
			choice = sc.nextInt();
			Movie movieChoice = movies.get(choice);
			System.out.println("\n"+movieChoice.toString());
			if (movieGraph.getNeighbors(movieChoice).size() == 0) System.out.println("This movie has no neighbors");
			else{
				System.out.println("Neighbors:\n");
				for (Movie currMovie: movieGraph.getNeighbors(movieChoice)) {
					System.out.println(currMovie.toString());
				}
			}
		}




//		ArrayList<Integer>[] adjList = makeAvgAdjList(movieMap, movies);
//		Graph<Movie> averageGraph = (Graph<Movie>) makeGraphOne(adjList, movies);
//		Graph<Movie> usersGraph = makeGraphTwo(movieMap,movies);
//		System.out.println(averageGraph.numVertices());
//		Movie start = movies.get(1);
//		Map<Integer, Integer> distances = GraphAlgorithms.djikstras(averageGraph, start, movies);
//		int min = Integer.MAX_VALUE;
//		int minKey = 0;
//		for (Integer key : distances.keySet()) {
//			if (distances.get(key) < min) {
//				min = distances.get(key);
//				minKey = key;
//			}
//		}
//		System.out.println("Min value =" + min + " movie = " + movies.get(minKey));
//		//System.out.println(averageGraph.getNeighbors(start));
//		long endTime = System.nanoTime();
//		long totalTime = endTime - startTime;
//		double totalTimeDivided = totalTime / 1000000000.0;
//		System.out.println(totalTimeDivided + " seconds");
//
//		startTime = System.nanoTime();
//		int[][] matrix = GraphAlgorithms.floydWarshall(averageGraph,movies);
//		endTime = System.nanoTime();
//		totalTime = endTime-startTime;
//		totalTimeDivided = totalTime / 1000000000.0;
//		ArrayList<Movie> test = (ArrayList<Movie>) usersGraph.getNeighbors(movies.get(3));
//		System.out.println(test.toString());




	}

	private static ArrayList<Integer>[] makeAvgAdjList(HashMap<Integer, ArrayList<ArrayList<Integer>>> movieMap, Map<Integer, Movie> movies ){
		HashMap<Integer, Double> movieAverages = new HashMap<Integer, Double>();
		for (Integer movie : movieMap.keySet()) {
			double average = 0.0;
			for (int i = 1; i < 6; i++) {
				average += movieMap.get(movie).get(i).size() * i;
			}
			average = average / movieMap.get(movie).get(0).size();
			movieAverages.put(movie, average);
			//System.out.println(average + " " + movies.get(movie).getTitle());
		}
		//creating adjacency list for movie averages
		ArrayList<Integer>[] adjList = new ArrayList[movies.keySet().size()];
		for (int m1 = 0; m1 < adjList.length; m1++) {
			adjList[m1] = new ArrayList<>();
			for (int m2 = 0; m2 < adjList.length; m2++) {
				if (m1 != m2) {
					double m1Average = movieAverages.get(m1 + 1);
					double m1Lower = m1Average - 0.1;
					double m1Upper = m1Average + 0.1;
					int genresInCommon = 0;
					for (String genre : movies.get(m1 + 1).getGenres()) {
						if (movies.get(m2 + 1).getGenres().contains(genre)) {
							genresInCommon++;
						}
					}
					if (m1Lower <= movieAverages.get(m2 + 1) && movieAverages.get(m2 + 1) <= m1Upper && genresInCommon > 1) {
						adjList[m1].add(m2);
					}
				}
			}
		}
		return adjList;
	}

	public static Graph<Movie> makeGraphOne(HashMap<Integer, ArrayList<ArrayList<Integer>>> movieMap, Map<Integer, Movie> movies) {
		ArrayList<Integer>[] adjList = makeAvgAdjList(movieMap, movies);
		Graph<Movie> graph = new Graph<>();
		//adding nodes to graph
		for (int i = 0; i < adjList.length; i++) {
			graph.addVertex(movies.get(i + 1));
		}
		//adding edges for each node
		for (int i = 0; i < adjList.length; i++) {
			for (int j : adjList[i]) {
				graph.addEdge(movies.get(i + 1), movies.get(j + 1));
			}
		}
		return graph;

	}

	public static Graph<Movie> makeGraphTwo (HashMap<Integer, ArrayList<ArrayList<Integer>>> movieMap, Map<Integer, Movie> movies){
		Graph<Movie> graph = new Graph<>();
		for (Integer movieID : movies.keySet()) {
			graph.addVertex(movies.get(movieID));
		}
		// For a given movie (outer movie)
		for (Integer outerID : movies.keySet()) {
			// Loop through all other movies (inner movies
			for (Integer innerID : movieMap.keySet()) {
				if (outerID.equals(innerID)) continue;

				int count = 0;

				// Loop through each of the five rating lists
				for (int i = 1; i < 6; i++){

					ArrayList<Integer> outerList = movieMap.get(outerID).get(i);
					ArrayList<Integer> innerList = movieMap.get(innerID).get(i);
					// If the same user appears in both ratings list, increase count
					for (Integer user : innerList) if (outerList.contains(user)) count++;
				}

				if (count >= 12) graph.addEdge(movies.get(outerID), movies.get(innerID));
			}
		}
		return graph;
	}
}
