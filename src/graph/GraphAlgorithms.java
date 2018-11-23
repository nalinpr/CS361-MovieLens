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



}
