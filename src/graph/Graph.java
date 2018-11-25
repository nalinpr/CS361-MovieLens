package graph;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Set;

/**
 * This class represents a directed graph G = (V, E)
 * using an adjacency list representation. 
 * 
 * @author alchambers
 *
 */
public class Graph<V> implements GraphIfc<V> {
	private Map<V, List<V>> graph;
	private int numEdges;	

	public Graph(){
		numEdges = 0;
		graph = new HashMap<V, List<V>>();
	}

	@Override
	public int numVertices() {
		return graph.size();
	}

	@Override
	public void clear() {
		numEdges = 0;
		graph.clear();
	}

	@Override
	public void addVertex(V v) {
		insertNode(v);
	}

	@Override
	public void addEdge(V u, V v) {
		if(!containsNode(u) || !containsNode(v)) {
			throw new IllegalArgumentException();
		}
		numEdges++;
		insertEdge(u, v);
	}

	@Override
	public Set<V> getVertices() {
		return graph.keySet();		
	}

	@Override
	public List<V> getNeighbors(V v) {
		assert(validNodeIndex(v));
		return graph.get(v);
	}

	@Override
	public boolean containsNode(V v) {
		return graph.containsKey(v);
	}

	@Override
	public boolean edgeExists(V v, V u) {
		if(!containsNode(u) || !containsNode(v)) {
			throw new IllegalArgumentException();
		}

		List<V> adjList = graph.get(v);
		for(int i = 0; i < adjList.size(); i++){
			V neighbor = adjList.get(i);
			if(neighbor == u){
				return true;
			}			
		}
		return false;
	}

	@Override
	public int degree(V v) {
		if( !containsNode(v)) {
			throw new IllegalArgumentException();
		}
		List<V> adjList = graph.get(v);
		return adjList.size();
	}

	@Override
	public int numEdges(){
		return numEdges;
	}

	@Override
	public String toString(){
		String str = "";
		for(Map.Entry<V, List<V>> node : graph.entrySet()){
			if(node.getValue().size() > 0){
				V u = node.getKey();				
				str += "Node: " + u + "\n";
				List<V> adjList = node.getValue();
				for(V v : adjList){
					str += "\t(" + u + ", " + v + ")" + "\n";
				}			
			}
		}
		return str;
	}



	/********************************************************************
	 * 						Auxiliary Methods
	 ********************************************************************/

	private void insertNode(V u){
		if(!graph.containsKey(u)){
			ArrayList<V> adjList = new ArrayList<V>();
			graph.put(u,  adjList);			
		}
	}

	private void insertEdge(V from, V to){
		List<V> adjList = graph.get(from);
		adjList.add(to);
	}

	private boolean validNodeIndex(V v){
		return graph.containsKey(v);
	}
}
