package util;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

/**
 * A minimum priority queue class implemented using a min heap.
 * Allows for negative priorities.
 * 
 * @author America Chambers
 * @version
 *
 */
public class PriorityQueue {

	private Map<Integer, Integer> location;
	private List<Pair<Integer, Integer>> heap;

	/**
	 *  Constructs an empty priority queue
	 */
	public PriorityQueue() {
		location = new HashMap<Integer, Integer>();
		heap = new ArrayList<Pair<Integer, Integer>>();
	}

	/**
	 *  Insert a new element into the queue with the
	 *  given priority.
	 *
	 *	@param priority priority of element to be inserted
	 *	@param element element to be inserted
	 *
	 *	<dt><b>Preconditions:</b><dd>
	 *	<ul>
	 *	<li> The element does not already appear in the priority queue.</li>
	 *	</ul>
	 *  
	 */
	public void push(int priority, int element) {
		// element must be unique
		assert(!location.containsKey(element));		
		
		// add the new element to the end of the list
		heap.add(new Pair<Integer,Integer>(priority, element));
		location.put(element,heap.size()-1);		

		// percolate up newly added value
		percolateUpLeaf();
	}

	/**
	 *  Remove the highest priority element
	 *  
	 *	<dt><b>Preconditions:</b><dd>
	 *	<ul>
	 *	<li> The priority queue is non-empty.</li>
	 *	</ul>
	 *  
	 */
	public void pop(){
		// heap must be non-empty
		assert (heap.size() > 0);
				
		int element = heap.get(0).element;
		int priority = heap.get(0).priority;

		// remove element from hash map
		location.remove(element);	

		// copy leaf node to root
		heap.set(0, heap.get(heap.size()-1));
		location.put(heap.get(0).element, 0);
		
		// now we can remove leaf node from heap
		heap.remove(heap.size()-1);
		
		// push new root down to proper place
		pushDownRoot();
	}


	/**
	 *  Returns the highest priority in the queue
	 *  @return highest priority value
	 *  
	 *	<dt><b>Preconditions:</b><dd>
	 *	<ul>
	 *	<li> The priority queue is non-empty.</li>
	 *	</ul>
	 */
	public int topPriority() {
		assert(heap.size() > 0);
		return heap.get(0).priority;		  
	}


	/**
	 *  Returns the element with the highest priority
	 *  @return element with highest priority
	 *
	 *	<dt><b>Preconditions:</b><dd>
	 *	<ul>
	 *	<li> The priority queue is non-empty.</li>
	 *	</ul>
	 */
	public int topElement() {
		assert(heap.size() > 0);
		return heap.get(0).element;
	}


	/**
	 *  Change the priority of an element already in the
	 *  priority queue.
	 *  
	 *  @param element element whose priority is to be changed
	 *  @param newpriority the new priority
	 *  
	 *	<dt><b>Preconditions:</b><dd>
	 *	<ul>
	 *	<li> The element exists in the priority queue</li>
	 *	</ul>
	 */
	public void changePriority(int element, int newpriority) {
		// make sure this element exists in the priority queue
		if(!location.containsKey(element)){
			System.out.println("element not found in change priority: " + element);
			assert(location.containsKey(element));		

		}

		int original_index = location.get(element);
		
		// update the priority
		heap.get(original_index).priority = newpriority;

		// given this new priority, try to percolate up
		int new_index = percolateUp(original_index);

		// if percolating up did not move the element, then
		// try pushing it down
		if(new_index == original_index) {
			pushDown(original_index);
		}
	}


	/**
	 *  Gets the priority of the element
	 *  
	 *  @param element the element whose priority is returned
	 *  @return the priority value
	 *
	 *	<dt><b>Preconditions:</b><dd>
	 *	<ul>
	 *	<li> The element exists in the priority queue</li>
	 *	</ul>
	 */
	public int getPriority(int element) {
		assert(location.containsKey(element));
		return heap.get(location.get(element)).priority;
	}

	/**
	 *  Returns true if the priority queue contains no elements
	 *  @return true if the queue contains no elements, false otherwise
	 */
	public boolean isEmpty() {
		return heap.size() == 0;
	}

	/**
	 *  Returns true if the element exists in the priority queue.
	 *  @return true if the element exists, false otherwise
	 */
	public boolean isPresent(int element) {
		return location.containsKey(element);
	}

	/**
	 *  Removes all elements from the priority queue
	 */
	public void clear() {
		heap.clear();
		location.clear();
	}

	/**
	 *  Returns the number of elements in the priority queue
	 *  @return number of elements in the priority queue
	 */
	public int size() {
		return heap.size();
	}

	/**
	 * Print the underlying list representation
	 */
	public void printHeap() {
		for(int i = 0; i < heap.size(); ++i) {
			Pair<Integer, Integer> pair = heap.get(i);
			System.out.println("(" + i + ") Priority: " + pair.priority + " Element: " + pair.element);
		}
		System.out.println();
	}

	/**
	 * Print the entries in the location map
	 */
	public void printMap() {
		for(Map.Entry<Integer, Integer> entry : location.entrySet()){
			System.out.println("key: " + entry.getKey() + ", " + "value: " + entry.getValue());
		}
		System.out.println();
	}

	
	/*************************************************
	 * 			Private Helper Methods
	 *************************************************/
	
	/**
	 * Push down the root element
	 * @return the index in the list where the element is finally stored
	 */
	private int pushDownRoot() {
		return pushDown(0);
	}

	/**
	 * Percolate up the last leaf in the heap, i.e. the most recently 
	 * added element which is stored in the last slot in the list
	 * 
	 * @return the index in the list where the element is finally stored
	 */
	private int percolateUpLeaf(){	
		return percolateUp(heap.size()-1);
	}

	/**
	 * Push down a given element 
	 * @param start_index the index of the element to be pushed down
	 * @return the index in the list where the element is finally stored
	 */
	private int pushDown(int start_index) {
		int curr = start_index;
		int l = left(curr);
		int r = right(curr);
		int swap_index;

		while(hasTwoChildren(curr)) {
			swap_index = (heap.get(l).priority < heap.get(r).priority) ? l : r;
			
			// swap_index now holds the index of the child with the numerically smallest priority value			

			if(heap.get(curr).priority <= heap.get(swap_index).priority) {
				break;
			}
			else{

				// move curr to the position of its largest child
				// move its largest child up to now occupy the parent spot
				swap(curr, swap_index);

				// update in preparation for the next iteration
				curr = swap_index;
				l = left(curr);
				r = right(curr);
			}
		}

		
		// Check if curr needs to be swapped with its one (left) child
		if(l < heap.size() && heap.get(l).priority < heap.get(curr).priority){
			swap(curr, l);
			curr = l;
		}
		return curr;
	}

	/**
	 * Percolate up a given element
	 * @param start_index the element to be percolated up
	 * @return the index in the list where the element is finally stored
	 */
	private int percolateUp(int start_index) {
		int curr = start_index;
		int p = parent(curr);
		while(curr > 0 && heap.get(curr).priority < heap.get(p).priority){
			swap(curr, p);
			curr = p;
			p = parent(curr);
		}
		return curr;
	}

	/**
	 * Returns true if element is a leaf in the heap
	 * @param i index of element in heap
	 * @return true if element is a leaf
	 */
	private boolean isLeaf(int i){
		return (left(i) > heap.size()) && (right(i) > heap.size());
	}

	/**
	 * Returns true if element has two children in the heap
	 * @param i index of element in the heap
	 * @return true if element in heap has two children
	 */
	private boolean hasTwoChildren(int i) {
		return (left(i) < heap.size()) && (right(i) < heap.size());
	}

	/**
	 * Swaps two elements in the priority queue by updating BOTH
	 * the list representing the heap AND the map
	 * @param i element to be swapped
	 * @param j element to be swapped
	 */
	private void swap(int i, int j) {
		Pair<Integer, Integer> temp = heap.get(i);

		heap.set(i, heap.get(j));
		heap.set(j, temp);

		int key = heap.get(j).element;
		location.put(key, j);

		key = heap.get(i).element;
		location.put(key, i);
	}

	/**
	 * Computes the index of the element's left child
	 * @param parent index of element in list
	 * @return index of element's left child in list
	 */
	private int left(int parent) {
		return 2*parent + 1;

	}

	/**
	 * Computes the index of the element's right child
	 * @param parent index of element in list
	 * @return index of element's right child in list
	 */
	private int right(int parent) {
		return 2*parent + 2;

	}

	/**
	 * Computes the index of the element's parent
	 * @param child index of element in list
	 * @return index of element's parent in list
	 */

	private int parent(int child) {
		return (child-1)/2;
	}


	/**
	 * The Pair class is only ever used inside the PriorityQueue class
	 * As a result, I made it an inner class
	 */
	private class Pair<P, E> {
		public P priority;
		public E element;
		
		public Pair(P p, E e) {
			priority = p;
			element = e;
		}
	}
	
}





