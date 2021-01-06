package beamline.declare.miners.events.lossycounting.constraints;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;

import beamline.declare.data.LossyCounting;
import beamline.declare.miners.events.lossycounting.LCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class Response implements LCTemplateReplayer {
	
	private Set<String> activityLabelsResponse = Sets.<String>newConcurrentHashSet();
	private LossyCounting<ConcurrentHashMap<String, Integer>> activityLabelsCounterResponse = new LossyCounting<ConcurrentHashMap<String, Integer>>();
	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> pendingConstraintsPerTrace = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>();
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
			pendingConstraintsPerTrace.addObservation(caseId, currentBucket, class1);
			fulfilledConstraintsPerTrace.addObservation(caseId, currentBucket, class1);
			activityLabelsCounterResponse.addObservation(caseId, currentBucket, class2);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup(Integer currentBucket) {
		pendingConstraintsPerTrace.cleanup(currentBucket);
		fulfilledConstraintsPerTrace.cleanup(currentBucket);
		activityLabelsCounterResponse.cleanup(currentBucket);
	}

	@Override
	public void process(String event, String caseId) {
		activityLabelsResponse.add(event);
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if (!activityLabelsCounterResponse.containsKey(caseId)) {
			activityLabelsCounterResponse.putItem(caseId, counter);
		} else {
			counter = activityLabelsCounterResponse.getItem(caseId);
		}
		
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> pendingForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		if (!pendingConstraintsPerTrace.containsKey(caseId)) {
			pendingConstraintsPerTrace.putItem(caseId, pendingForThisTrace);
		} else {
			pendingForThisTrace = pendingConstraintsPerTrace.getItem(caseId);
		}
		
		if (!counter.containsKey(event)) {
			if (activityLabelsResponse.size() > 1) {
				for (String existingEvent : activityLabelsResponse) {
					if (!existingEvent.equals(event)) {
						ConcurrentHashMap<String, Integer> secondElement = new ConcurrentHashMap<String, Integer>();
						if (pendingForThisTrace.containsKey(existingEvent)) {
							secondElement = pendingForThisTrace.get(existingEvent);
						}
						secondElement.put(event, 0);
						pendingForThisTrace.put(existingEvent, secondElement);
					}
				}
				for (String existingEvent : activityLabelsResponse) {
					if (!existingEvent.equals(event)) {
						ConcurrentHashMap<String, Integer> secondElement = new ConcurrentHashMap<String, Integer>();
						if (pendingForThisTrace.containsKey(event)) {
							secondElement = pendingForThisTrace.get(event);
						}
						secondElement.put(existingEvent, 1);
						pendingForThisTrace.put(event, secondElement);
					}
				}
				pendingConstraintsPerTrace.putItem(caseId, pendingForThisTrace);
//				pendingConstraintsPerTrace.put(caseId, pendingForThisTrace);
			}
		} else {

			for (String firstElement : pendingForThisTrace.keySet()) {
				if (!firstElement.equals(event)) {
					ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(firstElement);
					secondElement.put(event, 0);
					pendingForThisTrace.put(firstElement, secondElement);
					pendingConstraintsPerTrace.putItem(caseId, pendingForThisTrace);
//					pendingConstraintsPerTrace.put(caseId, pendingForThisTrace);
				}
			}
			ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(event);
			if (secondElement != null) {
				for (String second : secondElement.keySet()) {
					if (!second.equals(event)) {
						Integer pendingNo = secondElement.get(second);
						pendingNo++;
						secondElement.put(second, pendingNo);
					}
				}
				pendingForThisTrace.put(event, secondElement);
				pendingConstraintsPerTrace.putItem(caseId, pendingForThisTrace);
//				pendingConstraintsPerTrace.put(caseId, pendingForThisTrace);
			}

			// activityLabelsCounter.put(trace, counter);
		}

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
		activityLabelsCounterResponse.putItem(caseId, counter);
		// ***********************
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsResponse) {
			for(String param2 : activityLabelsResponse) {
				if(!param1.equals(param2)){
					
					// let's generate responses
					double fulfill = 0.0;
					double act = 0.0;
					for(String caseId : activityLabelsCounterResponse.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterResponse.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> pendingForThisTrace = pendingConstraintsPerTrace.getItem(caseId);
						if (pendingForThisTrace == null) {
							pendingForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
						}
						if(counter.containsKey(param1)){
							double totnumber = counter.get(param1);
							act = act + totnumber;
							if(pendingForThisTrace.containsKey(param1)){
								if(pendingForThisTrace.get(param1).containsKey(param2)){
									double stillpending = pendingForThisTrace.get(param1).get(param2);
									fulfill = fulfill + (totnumber - stillpending);
								}
							}
						}
					}
					d.addResponse(param1, param2, act, fulfill);
			  //  	d.addNotResponse(param1, param2, act, act - fulfill);
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsResponse.size() +
				activityLabelsCounterResponse.getSize() +
				pendingConstraintsPerTrace.getSize() +
				fulfilledConstraintsPerTrace.getSize();
	}

}
