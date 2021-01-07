package beamline.declare.miners.events.budgetlossycounting.constraints;

import java.util.concurrent.ConcurrentHashMap;

import beamline.declare.data.LossyCountingBudget;
import beamline.declare.data.SharedDelta;
import beamline.declare.miners.events.budgetlossycounting.BudgetLCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class Response implements BudgetLCTemplateReplayer {

	private SharedDelta delta = new SharedDelta();
	private LossyCountingBudget<String> activityLabelsResponse = null;
	private LossyCountingBudget<ConcurrentHashMap<String, Integer>> activityLabelsCounterResponse = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> pendingConstraintsPerTrace = null;
//	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTrace = null;
	
	public Response(int budget) {
		delta.budget = budget;
		activityLabelsResponse = new LossyCountingBudget<String>(delta);
		activityLabelsCounterResponse = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		pendingConstraintsPerTrace = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
//		fulfilledConstraintsPerTrace = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
	}

	@Override
	public void process(String event, String caseId) throws InstantiationException, IllegalAccessException {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		activityLabelsResponse.addObservation(event, String.class);
		
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if (activityLabelsCounterResponse.containsKey(caseId)) {
			counter = activityLabelsCounterResponse.getItem(caseId);
		}
		
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> pendingForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		if (pendingConstraintsPerTrace.containsKey(caseId)) {
			pendingForThisTrace = pendingConstraintsPerTrace.getItem(caseId);
		}
		
		if (!counter.containsKey(event)) {
			if (activityLabelsResponse.size() > 1) {
				for (String existingEvent : activityLabelsResponse.keySet()) {
					if (!existingEvent.equals(event)) {
						ConcurrentHashMap<String, Integer> secondElement = new ConcurrentHashMap<String, Integer>();
						if (pendingForThisTrace.containsKey(existingEvent)) {
							secondElement = pendingForThisTrace.get(existingEvent);
						}
						secondElement.put(event, 0);
						pendingForThisTrace.put(existingEvent, secondElement);
					}
				}
				for (String existingEvent : activityLabelsResponse.keySet()) {
					if (!existingEvent.equals(event)) {
						ConcurrentHashMap<String, Integer> secondElement = new ConcurrentHashMap<String, Integer>();
						if (pendingForThisTrace.containsKey(event)) {
							secondElement = pendingForThisTrace.get(event);
						}
						secondElement.put(existingEvent, 1);
						pendingForThisTrace.put(event, secondElement);
					}
				}
				pendingConstraintsPerTrace.addObservation(caseId, class1);
				pendingConstraintsPerTrace.putItem(caseId, pendingForThisTrace);
//				pendingConstraintsPerTrace.put(caseId, pendingForThisTrace);
			}
		} else {

			for (String firstElement : pendingForThisTrace.keySet()) {
				if (!firstElement.equals(event)) {
					ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(firstElement);
					secondElement.put(event, 0);
					pendingForThisTrace.put(firstElement, secondElement);
					pendingConstraintsPerTrace.addObservation(caseId, class1);
					pendingConstraintsPerTrace.putItem(caseId, pendingForThisTrace);
//					pendingConstraintsPerTrace.put(caseId, pendingForThisTrace);
				}
			}
			ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(event);
			for (String second : secondElement.keySet()) {
				if (!second.equals(event)) {
					Integer pendingNo = secondElement.get(second);
					pendingNo++;
					secondElement.put(second, pendingNo);
				}
			}
			pendingForThisTrace.put(event, secondElement);
			pendingConstraintsPerTrace.addObservation(caseId, class1);
			pendingConstraintsPerTrace.putItem(caseId, pendingForThisTrace);
//			pendingConstraintsPerTrace.put(caseId, pendingForThisTrace);

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
		activityLabelsCounterResponse.addObservation(caseId, class2);
		activityLabelsCounterResponse.putItem(caseId, counter);
		// ***********************
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsResponse.keySet()) {
			for(String param2 : activityLabelsResponse.keySet()) {
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
			//		d.addNotResponse(param1, param2, act, act - fulfill);
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsResponse.size() +
				activityLabelsCounterResponse.size() +
				pendingConstraintsPerTrace.size();// +
//				fulfilledConstraintsPerTrace.size();
	}

}
