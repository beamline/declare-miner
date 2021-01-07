package beamline.declare.miners.events.budgetlossycounting.constraints;

import java.util.concurrent.ConcurrentHashMap;

import beamline.declare.data.LossyCountingBudget;
import beamline.declare.data.SharedDelta;
import beamline.declare.miners.events.budgetlossycounting.BudgetLCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class Precedence implements BudgetLCTemplateReplayer {

	private SharedDelta delta = new SharedDelta();
	private LossyCountingBudget<String> activityLabelsPrecedence = null;
	private LossyCountingBudget<ConcurrentHashMap<String, Integer>> activityLabelsCounterPrecedence = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTrace = null;
	
	public Precedence(int budget) {
		delta.budget = budget;
		activityLabelsPrecedence = new LossyCountingBudget<String>(delta);
		activityLabelsCounterPrecedence = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		fulfilledConstraintsPerTrace = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
	}

	@Override
	public void process(String event, String caseId) throws InstantiationException, IllegalAccessException {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		activityLabelsPrecedence.addObservation(event, String.class);
		
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if (activityLabelsCounterPrecedence.containsKey(caseId)) {
			counter = activityLabelsCounterPrecedence.getItem(caseId);
		}
		
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfilledForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		if (fulfilledConstraintsPerTrace.containsKey(caseId)) {
			fulfilledForThisTrace = fulfilledConstraintsPerTrace.getItem(caseId);
		}
		
		if (activityLabelsPrecedence.size() > 1) {
			for (String existingEvent : activityLabelsPrecedence.keySet()) {
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
			fulfilledConstraintsPerTrace.addObservation(caseId, class1);
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
		activityLabelsCounterPrecedence.addObservation(caseId, class2);
		activityLabelsCounterPrecedence.putItem(caseId, counter);
		// ***********************
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsPrecedence.keySet()) {
			for(String param2 : activityLabelsPrecedence.keySet()) {
				if(!param1.equals(param2)){

					// let's generate precedences
					double fulfill = 0.0;
					double act = 0.0;
					for(String caseId : activityLabelsCounterPrecedence.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterPrecedence.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTrace.getItem(caseId);

						if(counter != null && counter.containsKey(param2)) {
							double totnumber = counter.get(param2);
							act = act + totnumber;
							if(fulfillForThisTrace != null && fulfillForThisTrace.containsKey(param1)){
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
				activityLabelsCounterPrecedence.size() +
				fulfilledConstraintsPerTrace.size();
	}

}
