package beamline.declare.miners.events.lossycounting.constraints;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;

import beamline.declare.data.LossyCounting;
import beamline.declare.miners.events.lossycounting.LCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class Precedence implements LCTemplateReplayer {
	
	private Set<String> activityLabelsPrecedence = Sets.<String>newConcurrentHashSet();
	private LossyCounting<ConcurrentHashMap<String, Integer>> activityLabelsCounterPrecedence = new LossyCounting<ConcurrentHashMap<String, Integer>>();
	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTrace = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>();

	@Override
	public void addObservation(String caseId, Integer currentBucket) {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		try {
			fulfilledConstraintsPerTrace.addObservation(caseId, currentBucket, class1);
			activityLabelsCounterPrecedence.addObservation(caseId, currentBucket, class2);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup(Integer currentBucket) {
		fulfilledConstraintsPerTrace.cleanup(currentBucket);
		activityLabelsCounterPrecedence.cleanup(currentBucket);
	}

	@Override
	public void process(String event, String caseId) {
		activityLabelsPrecedence.add(event);
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if (!activityLabelsCounterPrecedence.containsKey(caseId)) {
			activityLabelsCounterPrecedence.putItem(caseId, counter);
		} else {
			counter = activityLabelsCounterPrecedence.getItem(caseId);
		}
		
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfilledForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		if (!fulfilledConstraintsPerTrace.containsKey(caseId)) {
			fulfilledConstraintsPerTrace.putItem(caseId, fulfilledForThisTrace);
		} else {
			fulfilledForThisTrace = fulfilledConstraintsPerTrace.getItem(caseId);
		}
		
		if (activityLabelsPrecedence.size() > 1) {
			for (String existingEvent : activityLabelsPrecedence) {
				if (!existingEvent.equals(event)) {
					ConcurrentHashMap<String, Integer> secondElement = new ConcurrentHashMap<String, Integer>();
					int fulfillments = 0;
					if (fulfilledForThisTrace.containsKey(existingEvent)) {
						secondElement = fulfilledForThisTrace.get(existingEvent);
					}
					if (secondElement.containsKey(event)) {
						fulfillments = secondElement.get(event);
					}
					if (counter.containsKey(existingEvent)) {
						secondElement.put(event, fulfillments + 1);
						fulfilledForThisTrace.put(existingEvent, secondElement);
					}
				}
			}
			// for(String existingEvent : activityLabels){
			// if(!existingEvent.equals(event)){
			// ConcurrentHashMap<String, Integer> secondElement = new ConcurrentHashMap<String,
			// Integer>();
			// if(fulfilledForThisTrace.containsKey(event)){
			// secondElement = fulfilledForThisTrace.get(event);
			// }
			// secondElement.put(existingEvent, 1);
			// fulfilledForThisTrace.put(event,secondElement);
			// }
			// }
			fulfilledConstraintsPerTrace.putItem(caseId, fulfilledForThisTrace);
//			fulfilledConstraintsPerTrace.put(caseId, fulfilledForThisTrace);
		}

		// }else{
		//
		// for(String firstElement : fulfilledForThisTrace.keySet()){
		// if(!firstElement.equals(event)){
		// ConcurrentHashMap<String, Integer> secondElement =
		// fulfilledForThisTrace.get(firstElement);
		// secondElement.put(event, 0);
		// fulfilledForThisTrace.put(firstElement, secondElement);
		// pendingConstraintsPerTrace.put(trace, fulfilledForThisTrace);
		// }
		// }
		// ConcurrentHashMap<String, Integer> secondElement =
		// fulfilledForThisTrace.get(event);
		// for(String second : secondElement.keySet()){
		// if(!second.equals(event)){
		// Integer pendingNo = secondElement.get(second);
		// pendingNo ++;
		// secondElement.put(second, pendingNo);
		// }
		// }
		// fulfilledForThisTrace.put(event,secondElement);
		// pendingConstraintsPerTrace.put(trace, fulfilledForThisTrace);
		//
		// //activityLabelsCounter.put(trace, counter);
		// }

		// update the counter for the current trace and the current event
		// **********************

		int numberOfEvents = 1;
		if (!counter.containsKey(event)) {
			counter.put(event, numberOfEvents);
		} else {
			numberOfEvents = counter.get(event);
			numberOfEvents++;
			counter.put(event, numberOfEvents);
		}
		activityLabelsCounterPrecedence.putItem(caseId, counter);
		// ***********************
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsPrecedence) {
			for(String param2 : activityLabelsPrecedence) {
				if(!param1.equals(param2)){

					// let's generate precedences
					double fulfill = 0.0;
					double act = 0.0;
					for(String caseId : activityLabelsCounterPrecedence.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterPrecedence.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTrace.getItem(caseId);
						if (fulfillForThisTrace == null) {
							fulfillForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
						}
						if(counter.containsKey(param2)) {
							double totnumber = counter.get(param2);
							act = act + totnumber;
							if(fulfillForThisTrace.containsKey(param1)){
								if(fulfillForThisTrace.get(param1).containsKey(param2)) {
									fulfill = fulfill + fulfillForThisTrace.get(param1).get(param2);
								}
							}
						}
					}
					d.addPrecedence(param1, param2, act, fulfill);
				//	d.addNotPrecedence(param1, param2, act, act - fulfill);
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsPrecedence.size() +
				activityLabelsCounterPrecedence.getSize() +
				fulfilledConstraintsPerTrace.getSize();
	}

}
