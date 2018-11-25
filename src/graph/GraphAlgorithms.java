package graph;

import data.Movie;
import util.Pair;
import util.PriorityQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GraphAlgorithms {

    public static Map djikstras(Graph<Movie> g, Movie start, Map<Integer, Movie> movies){
        PriorityQueue queue = new PriorityQueue();
        HashMap<Integer, Integer> dist = new HashMap<>();
        HashMap <Integer, Integer> prev = new HashMap<>();
        //System.out.println(start.getTitle());
        for(Movie movie: g.getVertices()){
            if(movie.equals(start)){
                dist.put(movie.getMovieId(), 0);
                queue.push(0, movie.getMovieId());
            }
            else{
                dist.put(movie.getMovieId(), Integer.MAX_VALUE);
                queue.push(-1, movie.getMovieId());
            }
            prev.put(movie.getMovieId(), null);

        }
        while(!queue.isEmpty()){
            int leastPriority = queue.topPriority();
            int leastPriorityMovie = queue.topElement();
            queue.pop();
            for(Movie adj: g.getNeighbors(movies.get(leastPriorityMovie))){
                int alt = dist.get(leastPriorityMovie) -1;
                if(alt < dist.get(adj.getMovieId())){
                    dist.put(adj.getMovieId(),alt);
                    prev.put(adj.getMovieId(), leastPriorityMovie);
                    if(queue.isPresent(adj.getMovieId())){
                        //queue.changePriority(adj.getMovieId(), alt);
                    }
                }
            }
        }

        return dist;

    }

    public static int[][] floydWarshall(Graph<Movie> movieGraph, Map<Integer, Movie> movies) {
        int[][] adjMatrix = new int[1000][1000];
        for (int i = 3; i < 1000; i++) {
            Movie outerMovie = movies.get(i + 1);
            ArrayList<Movie> neighbors = (ArrayList<Movie>) movieGraph.getNeighbors(outerMovie);
            for (int j = 0; j < 1000; j++) {
                Movie innerMovie = movies.get(j + 1);
                // Diagonal of matrix should be zeros
                if (outerMovie.equals(innerMovie)) {
                    adjMatrix[i][j] = 0;
                } // If an edge exists, put a weight of 1. Else, give an "infinite" weight
                else if (neighbors.contains(innerMovie)) {
                    adjMatrix[i][j] = 1;
                }
                else {
                    adjMatrix[i][j] = 1001;
                }

            }
        }

        int[][] oldMatrix = adjMatrix;
        for (int k = 0; k < 1000; k++){
            for (int i = 0; i < 1000; i++) {
                for (int j = 0; j < 1000; j++) {
                    adjMatrix[i][j] = Math.min(oldMatrix[i][j], oldMatrix[i][k] + oldMatrix[k][j]);
                }
            }
            oldMatrix = adjMatrix;
        }

        return oldMatrix;
    }



}
