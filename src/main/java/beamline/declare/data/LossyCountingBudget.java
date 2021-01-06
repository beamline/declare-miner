package beamline.declare.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Triple;

/**
 * This data structure is used to manage the different replayer.
 * 
 * This data structure is composed of:
 * <ul>
 * 	<li>case-id;</li>
 * 	<li>replayers data structure + frequency of the case id + current
 * 		bucket.</li>
 * </ul>
 * 
 * @author Andrea Burattin
 * @param <T>
 */
public class LossyCountingBudget<T> extends ConcurrentHashMap<String, Triple<T, Integer, Integer>> {

	private static final long serialVersionUID = -5610289457434845311L;
	private static int idGenerator = 0;
	private int id = 0;
	private SharedDelta delta;
	
	/**
	 * 
	 * @param delta
	 */
	public LossyCountingBudget(SharedDelta delta) {
		this.id = idGenerator++;
		this.delta = delta;
		this.delta.registerNewDataStructure(this);
	}
	
	/**
	 * 
	 * @param caseId
	 * @param inCaseOfNull
	 */
	public void addObservation(String caseId, T inCaseOfNull) {
		if (containsKey(caseId)) {
			Triple<T, Integer, Integer> v = get(caseId);
			put(caseId, Triple.of(v.getLeft(), v.getMiddle() + 1, v.getRight()));
		} else {
			if (delta.getSize() >= delta.budget) {
				delta.cleanup();
			}
			put(caseId, Triple.of(inCaseOfNull, 1, delta.currentBucket));
		}
	}
	
	/**
	 * 
	 * @param caseId
	 * @param inCaseOfNull
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void addObservation(String caseId, Class<?> inCaseOfNull) throws InstantiationException, IllegalAccessException {
		addObservation(caseId, (T) inCaseOfNull.newInstance());
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean removeBelowDelta() {
		boolean removedOneItem = false;
		Iterator<Map.Entry<String, Triple<T, Integer, Integer>>> iter = entrySet().iterator();
		while (iter.hasNext()) {
			Triple<T, Integer, Integer> triple = iter.next().getValue();
			if (triple.getMiddle() + triple.getRight() <= delta.currentBucket) {
				iter.remove();
				removedOneItem = true;
			}
		}
		return removedOneItem;
	}
	
	/**
	 * 
	 * @param caseId
	 * @return
	 */
	public T getItem(String caseId) {
		if (get(caseId) != null) {
			return get(caseId).getLeft();
		}
		return null;
	}
	
	/**
	 * 
	 * @param caseId
	 * @param item
	 */
	public void putItem(String caseId, T item) {
		Triple<T, Integer, Integer> v = get(caseId);
		put(caseId, Triple.of(item, v.getMiddle(), v.getRight()));
	}
	
	@Override
	public int size() {
		int tmp = super.size();
		if (tmp > 0) {
			for (Triple<T, Integer, Integer> i : values()) {
				T item = i.getLeft();
				if (item instanceof Collection<?>) {
					tmp += ((Collection<?>) item).size();
				}
			}
		}
		return tmp;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}