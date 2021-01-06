package beamline.declare.miners.events.lossycounting.constraints;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;

import beamline.declare.data.LossyCounting;
import beamline.declare.miners.events.lossycounting.LCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class Succession implements LCTemplateReplayer {
	
	private Set<String> activityLabelsSuccession = Sets.<String>newConcurrentHashSet();
	private LossyCounting<ConcurrentHashMap<String, Integer>> activityLabelsCounterSuccession = new LossyCounting<ConcurrentHashMap<String, Integer>>();
	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> pendingConstraintsPerTraceSuccession = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>();
	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTraceSuccession = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>();

	@Override
	public void addObservation(String caseId, Integer currentBucket) {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		try {
			pendingConstraintsPerTraceSuccession.addObservation(caseId, currentBucket, class1);
			fulfilledConstraintsPerTraceSuccession.addObservation(caseId, currentBucket, class1);
			activityLabelsCounterSuccession.addObservation(caseId, currentBucket, class2);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup(Integer currentBucket) {
		pendingConstraintsPerTraceSuccession.cleanup(currentBucket);
		fulfilledConstraintsPerTraceSuccession.cleanup(currentBucket);
		activityLabelsCounterSuccession.cleanup(currentBucket);
	}

	@Override
	public void process(String event, String caseId) {
		activityLabelsSuccession.add(event);
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if (!activityLabelsCounterSuccession.containsKey(caseId)) {
			activityLabelsCounterSuccession.putItem(caseId, counter);
		} else {
			counter = activityLabelsCounterSuccession.getItem(caseId);
		}
		
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> pendingForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		if (!pendingConstraintsPerTraceSuccession.containsKey(caseId)) {
			pendingConstraintsPerTraceSuccession.putItem(caseId, pendingForThisTrace);
		} else {
			pendingForThisTrace = pendingConstraintsPerTraceSuccession.getItem(caseId);
		}

		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfilledForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		if (!fulfilledConstraintsPerTraceSuccession.containsKey(caseId)) {
			fulfilledConstraintsPerTraceSuccession.putItem(caseId, fulfilledForThisTrace);
		} else {
			fulfilledForThisTrace = fulfilledConstraintsPerTraceSuccession.getItem(caseId);
		}

		if (!counter.containsKey(event)) {
			if (activityLabelsSuccession.size() > 1) {
				for (String existingEvent : activityLabelsSuccession) {
					if (!existingEvent.equals(event)) {
						ConcurrentHashMap<String, Integer> secondElement = new ConcurrentHashMap<String, Integer>();
						if (pendingForThisTrace.containsKey(existingEvent)) {
							secondElement = pendingForThisTrace.get(existingEvent);
						}
						secondElement.put(event, 0);
						pendingForThisTrace.put(existingEvent, secondElement);

						ConcurrentHashMap<String, Integer> secondElement2 = new ConcurrentHashMap<String, Integer>();
						int fulfillments = 0;
						if (fulfilledForThisTrace.containsKey(existingEvent)) {
							secondElement2 = fulfilledForThisTrace.get(existingEvent);
						}
						if (secondElement2.containsKey(event)) {
							fulfillments = secondElement2.get(event);
						}
						if (counter.containsKey(existingEvent)) {
							secondElement2.put(event, fulfillments + 1);
							fulfilledForThisTrace.put(existingEvent, secondElement2);
						}

					}
				}
				fulfilledConstraintsPerTraceSuccession.putItem(caseId, fulfilledForThisTrace);
//				fulfilledConstraintsPerTraceSuccession.put(caseId, fulfilledForThisTrace);
				for (String existingEvent : activityLabelsSuccession) {
					if (!existingEvent.equals(event)) {
						ConcurrentHashMap<String, Integer> secondElement = new ConcurrentHashMap<String, Integer>();
						if (pendingForThisTrace.containsKey(event)) {
							secondElement = pendingForThisTrace.get(event);
						}
						secondElement.put(existingEvent, 1);
						pendingForThisTrace.put(event, secondElement);
					}
				}
				pendingConstraintsPerTraceSuccession.putItem(caseId, pendingForThisTrace);
//				pendingConstraintsPerTraceSuccession.put(caseId, pendingForThisTrace);
			}
		} else {

			for (String firstElement : pendingForThisTrace.keySet()) {
				if (!firstElement.equals(event)) {
					ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(firstElement);
					secondElement.put(event, 0);
					pendingForThisTrace.put(firstElement, secondElement);
					pendingConstraintsPerTraceSuccession.putItem(caseId, pendingForThisTrace);
//					pendingConstraintsPerTraceSuccession.put(caseId, pendingForThisTrace);

					ConcurrentHashMap<String, Integer> secondElement2 = new ConcurrentHashMap<String, Integer>();
					int fulfillments = 0;
					if (fulfilledForThisTrace.containsKey(firstElement)) {
						secondElement2 = fulfilledForThisTrace.get(firstElement);
					}
					if (secondElement2.containsKey(event)) {
						fulfillments = secondElement2.get(event);
					}
					if (counter.containsKey(firstElement)) {
						secondElement2.put(event, fulfillments + 1);
						fulfilledForThisTrace.put(firstElement, secondElement2);
					}
				}
			}
			fulfilledConstraintsPerTraceSuccession.putItem(caseId, fulfilledForThisTrace);
//			fulfilledConstraintsPerTraceSuccession.put(caseId, fulfilledForThisTrace);
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
				pendingConstraintsPerTraceSuccession.putItem(caseId, pendingForThisTrace);
	//			pendingConstraintsPerTraceSuccession.put(caseId, pendingForThisTrace);
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
		activityLabelsCounterSuccession.putItem(caseId, counter);
		// ***********************
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsSuccession) {
			for(String param2 : activityLabelsSuccession) {
				if(!param1.equals(param2)){

					// let's generate successions
					double fulfill = 0.0;
					double act = 0.0;
					for(String caseId : activityLabelsCounterSuccession.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterSuccession.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> pendingForThisTrace = pendingConstraintsPerTraceSuccession.getItem(caseId);
						if (pendingForThisTrace == null) {
							pendingForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
						}
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTraceSuccession.getItem(caseId);
						if (fulfillForThisTrace == null) {
							fulfillForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
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
						if(counter.containsKey(param2)){
							double totnumber = counter.get(param2);
							act = act + totnumber;
							if(fulfillForThisTrace.containsKey(param1)){
								if(fulfillForThisTrace.get(param1).containsKey(param2)){	
									fulfill = fulfill + fulfillForThisTrace.get(param1).get(param2);
								}
							}
						}
					}
					d.addSuccession(param1, param2, act, fulfill);
//					d.addNotSuccession(param1, param2, act, act-fulfill);
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsSuccession.size() +
				activityLabelsCounterSuccession.size() + 
				pendingConstraintsPerTraceSuccession.size() + 
				fulfilledConstraintsPerTraceSuccession.size();
	}

}
