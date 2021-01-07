package beamline.declare.miners.events.budgetlossycounting.constraints;

import java.util.concurrent.ConcurrentHashMap;

import beamline.declare.data.LossyCountingBudget;
import beamline.declare.data.SharedDelta;
import beamline.declare.miners.events.budgetlossycounting.BudgetLCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class RespondedExistence implements BudgetLCTemplateReplayer {

	private SharedDelta delta = new SharedDelta();
	private LossyCountingBudget<String> activityLabelsRespondedExistence = null;
	private LossyCountingBudget<ConcurrentHashMap<String, Integer>> activityLabelsCounterRespondedExistence = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> pendingConstraintsPerTraceRe = null;
	
	public RespondedExistence(int budget) {
		delta.budget = budget;
		activityLabelsRespondedExistence = new LossyCountingBudget<String>(delta);
		activityLabelsCounterRespondedExistence = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		pendingConstraintsPerTraceRe = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
	}

	@Override
	public void process(String event, String caseId) throws InstantiationException, IllegalAccessException {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		activityLabelsRespondedExistence.addObservation(event, String.class);
		
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if(activityLabelsCounterRespondedExistence.containsKey(caseId)) {
			counter = activityLabelsCounterRespondedExistence.getItem(caseId);
		}
		
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> pendingForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(pendingConstraintsPerTraceRe.containsKey(caseId)){
			pendingForThisTrace = pendingConstraintsPerTraceRe.getItem(caseId);
		}
		if (!counter.containsKey(event)) {
			if (activityLabelsRespondedExistence.size()>1) {
				for (String existingEvent : activityLabelsRespondedExistence.keySet()) {
					if (!existingEvent.equals(event)){
						ConcurrentHashMap<String, Integer> secondElement = new ConcurrentHashMap<String, Integer>();
						if (pendingForThisTrace.containsKey(existingEvent)) {
							secondElement = pendingForThisTrace.get(existingEvent);
						}
						secondElement.put(event, 0);
						pendingForThisTrace.put(existingEvent,secondElement);
					}

				}
				for (String existingEvent : activityLabelsRespondedExistence.keySet()) {
					if (!existingEvent.equals(event)) {

						ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
						if(pendingForThisTrace.containsKey(event)){
							secondElement = pendingForThisTrace.get(event);
						}
						if(!counter.containsKey(existingEvent)){
							secondElement.put(existingEvent, 1);
						}else{
							secondElement.put(existingEvent, 0);
						}
						pendingForThisTrace.put(event,secondElement);

					}
				}
				pendingConstraintsPerTraceRe.addObservation(caseId, class1);
				pendingConstraintsPerTraceRe.putItem(caseId, pendingForThisTrace);
//				pendingConstraintsPerTraceRe.put(trace, pendingForThisTrace);
			}
		} else {
			for (String firstElement : pendingForThisTrace.keySet()) {
				if (!firstElement.equals(event)) {
					ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(firstElement);
					secondElement.put(event, 0);
					pendingForThisTrace.put(firstElement, secondElement);
					pendingConstraintsPerTraceRe.addObservation(caseId, class1);
					pendingConstraintsPerTraceRe.putItem(caseId, pendingForThisTrace);
//					pendingConstraintsPerTraceRe.put(trace, pendingForThisTrace);
				}
			}

			ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(event);
			for (String second : secondElement.keySet()) {
				if (!second.equals(event)) {
					if (!counter.containsKey(second)) {
						Integer pendingNo = secondElement.get(second);
						pendingNo ++;
						secondElement.put(second, pendingNo);
					} else {
						secondElement.put(second, 0);
					}
				}
			}
			pendingForThisTrace.put(event,secondElement);
			pendingConstraintsPerTraceRe.addObservation(caseId, class1);
			pendingConstraintsPerTraceRe.putItem(caseId, pendingForThisTrace);
//			pendingConstraintsPerTraceRe.put(trace, pendingForThisTrace);
		}

		//update the counter for the current trace and the current event
		//**********************

		int numberOfEvents = 1;
		if (!counter.containsKey(event)) {
			counter.put(event, numberOfEvents);
		} else {
			numberOfEvents = counter.get(event);
			numberOfEvents++;
			counter.put(event, numberOfEvents); 
		}
		activityLabelsCounterRespondedExistence.addObservation(caseId, class2);
		activityLabelsCounterRespondedExistence.putItem(caseId, counter);
		//***********************
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsRespondedExistence.keySet()) {
			for(String param2 : activityLabelsRespondedExistence.keySet()) {
				if(!param1.equals(param2)){

					// let's generate responded existence
					double fulfill = 0.0;
					double act = 0.0;
					for(String caseId : activityLabelsCounterRespondedExistence.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterRespondedExistence.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> pendingForThisTrace = pendingConstraintsPerTraceRe.getItem(caseId);
						if (pendingForThisTrace == null) {
							pendingForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
						}

						if (counter != null && counter.containsKey(param1)) {
							double totnumber = counter.get(param1);
							act = act + totnumber;
							if (pendingForThisTrace != null && pendingForThisTrace.containsKey(param1)) {
								if (pendingForThisTrace.get(param1).containsKey(param2)) {	
									double stillpending = pendingForThisTrace.get(param1).get(param2);
									fulfill = fulfill + (totnumber - stillpending);
								}
							}
						}
					}
					d.addRespondedExistence(param1, param2, act, fulfill);
				//	d.addNotCoExistence(param1, param2, act, act - fulfill);
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsRespondedExistence.size() +
				activityLabelsCounterRespondedExistence.size() +
				pendingConstraintsPerTraceRe.size();
	}

}
