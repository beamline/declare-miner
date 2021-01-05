package beamline.declare.data;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;

/**
 * 
 * @author Andrea Burattin
 */
public class SharedDelta {

	public int currentBucket = 0;
	public int budget = -1;
	private Set<LossyCountingBudget<?>> sharedItems;
	
	/**
	 * 
	 */
	public SharedDelta() {
		this.sharedItems = new HashSet<LossyCountingBudget<?>>();
	}
	
	/**
	 * 
	 * @param dataStructure
	 */
	public void registerNewDataStructure(LossyCountingBudget<?> dataStructure) {
		sharedItems.add(dataStructure);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean removeFromAll() {
		boolean removed = false;
		for (LossyCountingBudget<?> lcb : sharedItems) {
			boolean actuallyRemoved = lcb.removeBelowDelta();
			removed = removed || actuallyRemoved;
		}
		return removed;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getMinDelta() {
		Integer minValue = Integer.MAX_VALUE;
		for (LossyCountingBudget<?> lcb : sharedItems) {
			for (Entry<?, ?> o : lcb.entrySet()) {
				@SuppressWarnings("unchecked")
				Triple<?, Integer, Integer> triple = (Triple<?, Integer, Integer>) o.getValue();
				if ((Integer) triple.getMiddle() + (Integer) triple.getRight() < minValue) {
					minValue = (Integer) triple.getMiddle() + (Integer) triple.getRight();
				}
			}
		}
		return minValue;
	}
	
	/**
	 * This method prepares the data structures in order to allow the storage of
	 * the provided new case id item
	 * 
	 * @param caseId the case id to store
	 */
	public void checkSpace(String caseId) {
		while (requiresCleanup(caseId)) {
			cleanup();
		}
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		currentBucket++;
		boolean removedOneItem = removeFromAll();
		if (!removedOneItem) {
			currentBucket = getMinDelta();
			removeFromAll();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int getSize() {
		int sum = 0;
		for (LossyCountingBudget<?> lcb : sharedItems) {
			sum += lcb.size();
		}
		return sum;
	}
	
	/**
	 * 
	 * @param caseId
	 * @return
	 */
	private boolean requiresCleanup(String caseId) {
		int newSize = getSize();
		for (LossyCountingBudget<?> lcb : sharedItems) {
			if (!lcb.containsKey(caseId)) {
				newSize++;
			}
		}
		return (newSize >= (budget - sharedItems.size()));
	}
}