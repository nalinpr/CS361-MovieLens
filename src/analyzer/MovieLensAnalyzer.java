package analyzer;

import graph.Graph;
import graph.GraphAlgorithms;
import util.DataLoader;
import data.Movie;
import data.Reviewer;
import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
		// FILL IN THE REST OF YOUR PROGRAM
		long startTime = System.nanoTime();
		DataLoader dl = new DataLoader();
		dl.loadData("./src/ml-latest-small/movies.csv", "./src/ml-latest-small/ratings.csv");
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
		Graph averageGraph = makeGraphOne(adjList, movies);
		System.out.println(averageGraph.numVertices());
		Movie start = movies.get(1);
		Map<Integer, Integer> distances = GraphAlgorithms.djikstras(averageGraph, start, movies);
		int min = Integer.MAX_VALUE;
		int minKey = 0;
		for (Integer key : distances.keySet()) {
			if (distances.get(key) < min) {
				min = distances.get(key);
				minKey = key;
			}
		}
		System.out.println("Min value =" + min + " movie = " + movies.get(minKey));
		//System.out.println(averageGraph.getNeighbors(start));
		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		double totalTimeDivided = totalTime / 1000000000.0;
		System.out.println(totalTimeDivided + " seconds");
	}

	public static Graph makeGraphOne(ArrayList<Integer>[] adjList, Map<Integer, Movie> movies) {
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
	
	public static Graph makeGraphTwo (HashMap<Integer, ArrayList<ArrayList<Integer>>> movieMap, Map<Integer, Movie> movies){
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