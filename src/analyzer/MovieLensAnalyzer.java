package analyzer;

import util.DataLoader;
import data.Movie;
import data.Reviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieLensAnalyzer {
	
	public static void main(String[] args){
		// Your program should take two command-line arguments: 
		// 1. A ratings file
		// 2. A movies file with information on each movie e.g. the title and genres
		args = new String[]{"./src/ml-latest-small/movies.csv","./src/ml-latest-small/ratings.csv"};
		if(args.length != 2){
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
		HashMap<Integer, ArrayList<ArrayList<Integer>>> movieArray = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
		// For a given movieID
		for (Integer id : movies.keySet()) {
			movieArray.put(id, new ArrayList<ArrayList<Integer>>(6));
			for (int i = 0; i<6;i++){
				movieArray.get(id).add(new ArrayList<Integer>());
			}
			// For each reviewer
			for (Integer reviewer: reviewers.keySet()){
				// If they reviewed the movie, add them into out master data structure
				if(reviewers.get(reviewer).ratedMovie(id)){
					int rating = (int)reviewers.get(reviewer).getMovieRating(id);
					movieArray.get(id).get(rating).add(reviewer);
					movieArray.get(id).get(0).add(reviewer);
				}
			}
		}
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		double totalTimeDivided = totalTime/1000000000.0;

		System.out.println(totalTimeDivided + " seconds");



		HashMap<Integer, Double> movieAverages = new HashMap<Integer, Double>();
		for(Integer movie: movieArray.keySet()){
		    double average = 0.0;
            for(int i = 1; i< 6; i++){
               average += movieArray.get(movie).get(i).size()*i;
            }
            average = average / movieArray.get(movie).get(0).size();
            movieAverages.put(movie, average);
            //System.out.println(average + " " + movies.get(movie).getTitle());
        }
	}
}
