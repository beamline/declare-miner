package beamline.declare.miners.events.budgetlossycounting.constraints;

import java.util.concurrent.ConcurrentHashMap;

import beamline.declare.data.LossyCountingBudget;
import beamline.declare.data.SharedDelta;
import beamline.declare.miners.events.budgetlossycounting.BudgetLCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class Succession implements BudgetLCTemplateReplayer {

	private SharedDelta delta = new SharedDelta();
	private LossyCountingBudget<String> activityLabelsSuccession = null;
	private LossyCountingBudget<ConcurrentHashMap<String, Integer>> activityLabelsCounterSuccession = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> pendingConstraintsPerTraceSuccession = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTraceSuccession = null;
	
	public Succession(int budget) {
		delta.budget = budget;
		activityLabelsSuccession = new LossyCountingBudget<String>(delta);
		activityLabelsCounterSuccession = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		pendingConstraintsPerTraceSuccession = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
		fulfilledConstraintsPerTraceSuccession = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
	}

	@Override
	public void process(String event, String caseId) throws InstantiationException, IllegalAccessException {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		activityLabelsSuccession.addObservation(event, String.class);
		
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if (activityLabelsCounterSuccession.containsKey(caseId)) {
			counter = activityLabelsCounterSuccession.getItem(caseId);
		}
		
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> pendingForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		if (pendingConstraintsPerTraceSuccession.containsKey(caseId)) {
			pendingForThisTrace = pendingConstraintsPerTraceSuccession.getItem(caseId);
		}

		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfilledForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		if (fulfilledConstraintsPerTraceSuccession.containsKey(caseId)) {
			fulfilledForThisTrace = fulfilledConstraintsPerTraceSuccession.getItem(caseId);
		}

		if (!counter.containsKey(event)) {
			if (activityLabelsSuccession.size() > 1) {
				for (String existingEvent : activityLabelsSuccession.keySet()) {
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
				fulfilledConstraintsPerTraceSuccession.addObservation(caseId, class1);
				fulfilledConstraintsPerTraceSuccession.putItem(caseId, fulfilledForThisTrace);
//				fulfilledConstraintsPerTraceSuccession.put(caseId, fulfilledForThisTrace);
				for (String existingEvent : activityLabelsSuccession.keySet()) {
					if (!existingEvent.equals(event)) {
						ConcurrentHashMap<String, Integer> secondElement = new ConcurrentHashMap<String, Integer>();
						if (pendingForThisTrace.containsKey(event)) {
							secondElement = pendingForThisTrace.get(event);
						}
						secondElement.put(existingEvent, 1);
						pendingForThisTrace.put(event, secondElement);
					}
				}
				pendingConstraintsPerTraceSuccession.addObservation(caseId, class1);
				pendingConstraintsPerTraceSuccession.putItem(caseId, pendingForThisTrace);
//				pendingConstraintsPerTraceSuccession.put(caseId, pendingForThisTrace);
			}
		} else {

			for (String firstElement : pendingForThisTrace.keySet()) {
				if (!firstElement.equals(event)) {
					ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(firstElement);
					secondElement.put(event, 0);
					pendingForThisTrace.put(firstElement, secondElement);
					pendingConstraintsPerTraceSuccession.addObservation(caseId, class1);
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
			fulfilledConstraintsPerTraceSuccession.addObservation(caseId, class1);
			fulfilledConstraintsPerTraceSuccession.putItem(caseId, fulfilledForThisTrace);
//			fulfilledConstraintsPerTraceSuccession.put(caseId, fulfilledForThisTrace);
			if (pendingForThisTrace.contains(event)) {
				ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(event);
				for (String second : secondElement.keySet()) {
					if (!second.equals(event)) {
						Integer pendingNo = secondElement.get(second);
						pendingNo++;
						secondElement.put(second, pendingNo);
					}
				}
				pendingForThisTrace.put(event, secondElement);
				pendingConstraintsPerTraceSuccession.addObservation(caseId, class1);
				pendingConstraintsPerTraceSuccession.putItem(caseId, pendingForThisTrace);
			}
//			pendingConstraintsPerTraceSuccession.put(caseId, pendingForThisTrace);

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
		activityLabelsCounterSuccession.addObservation(caseId, class2);
		activityLabelsCounterSuccession.putItem(caseId, counter);
		// ***********************
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsSuccession.keySet()) {
			for(String param2 : activityLabelsSuccession.keySet()) {
				if(!param1.equals(param2)){

					// let's generate successions
					double fulfill = 0.0;
					double act = 0.0;
					for(String caseId : activityLabelsCounterSuccession.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterSuccession.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> pendingForThisTrace = pendingConstraintsPerTraceSuccession.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTraceSuccession.getItem(caseId);

						if(counter != null && counter.containsKey(param1)){
							double totnumber = counter.get(param1);
							act = act + totnumber;
							if(pendingForThisTrace != null && pendingForThisTrace.containsKey(param1)){
								if(pendingForThisTrace.get(param1).containsKey(param2)){	
									double stillpending = pendingForThisTrace.get(param1).get(param2);
									fulfill = fulfill + (totnumber - stillpending);
								}
							}
						}
						if(counter != null && counter.containsKey(param2)){
							double totnumber = counter.get(param2);
							act = act + totnumber;
							if(fulfillForThisTrace != null && fulfillForThisTrace.containsKey(param1)){
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
