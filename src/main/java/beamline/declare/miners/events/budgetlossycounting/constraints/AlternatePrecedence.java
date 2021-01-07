package beamline.declare.miners.events.budgetlossycounting.constraints;

import java.util.concurrent.ConcurrentHashMap;

import beamline.declare.data.LossyCountingBudget;
import beamline.declare.data.SharedDelta;
import beamline.declare.miners.events.budgetlossycounting.BudgetLCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class AlternatePrecedence implements BudgetLCTemplateReplayer {

	private SharedDelta delta = new SharedDelta();
	private LossyCountingBudget<String> activityLabelsAltPrecedence = null;
	private LossyCountingBudget<ConcurrentHashMap<String, Integer>> activityLabelsCounterAltPrecedence = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTraceAlt = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> satisfactionsConstraintsPerTrace = null;
	
	public AlternatePrecedence(int budget) {
		delta.budget = budget;
		activityLabelsAltPrecedence = new LossyCountingBudget<String>(delta);
		activityLabelsCounterAltPrecedence = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		fulfilledConstraintsPerTraceAlt = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
		satisfactionsConstraintsPerTrace = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
	}

	@Override
	public void process(String event, String caseId) throws InstantiationException, IllegalAccessException {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		activityLabelsAltPrecedence.addObservation(event, String.class);
		
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if(activityLabelsCounterAltPrecedence.containsKey(caseId)){
			counter = activityLabelsCounterAltPrecedence.getItem(caseId);
		}
		
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> fulfilledForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(fulfilledConstraintsPerTraceAlt.containsKey(caseId)){
			fulfilledForThisTrace = fulfilledConstraintsPerTraceAlt.getItem(caseId);
		}
		
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> satisfactionsForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		satisfactionsConstraintsPerTrace.addObservation(caseId, class1);
		if(!satisfactionsConstraintsPerTrace.containsKey(caseId)){
			satisfactionsConstraintsPerTrace.putItem(caseId, satisfactionsForThisTrace);
		}else{
			satisfactionsForThisTrace = satisfactionsConstraintsPerTrace.getItem(caseId);
		}
		if(activityLabelsAltPrecedence.size()>1){
			for(String existingEvent : activityLabelsAltPrecedence.keySet()){
				if(!existingEvent.equals(event)){
					ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
					int fulfillments = 0;
					if(fulfilledForThisTrace.containsKey(existingEvent)){
						secondElement = fulfilledForThisTrace.get(existingEvent);
					}
					if(secondElement.containsKey(event)){
						fulfillments = secondElement.get(event);
					}
					ConcurrentHashMap<String, Integer> secondSat = new  ConcurrentHashMap<String, Integer>();
					if(satisfactionsForThisTrace.containsKey(event)){
						secondSat = satisfactionsForThisTrace.get(event);
					}
					secondSat.put(existingEvent, 0);
					ConcurrentHashMap<String, Integer> secondSat2 = new  ConcurrentHashMap<String, Integer>();
					if(satisfactionsForThisTrace.containsKey(existingEvent)){
						secondSat2 = satisfactionsForThisTrace.get(existingEvent);
					}
					int sat = 0;
					if(secondSat2.containsKey(event)){
						sat = secondSat2.get(event);
					}
					secondSat2.put(existingEvent, sat + 1);
					if(counter.containsKey(existingEvent)){
						if(satisfactionsForThisTrace.get(existingEvent) == null || satisfactionsForThisTrace.get(existingEvent).get(event) == null || satisfactionsForThisTrace.get(existingEvent).get(event)<2){
							secondElement.put(event, fulfillments + 1);
							fulfilledForThisTrace.put(existingEvent, secondElement);
						}
					}
				}
			}
			fulfilledConstraintsPerTraceAlt.addObservation(caseId, class1);
			fulfilledConstraintsPerTraceAlt.putItem(caseId, fulfilledForThisTrace);
		}
		int numberOfEvents = 1;
		if(!counter.containsKey(event)){
			counter.put(event, numberOfEvents);
		}else{
			numberOfEvents = counter.get(event);
			numberOfEvents++;
			counter.put(event, numberOfEvents); 
		}
		activityLabelsCounterAltPrecedence.addObservation(caseId, class2);
		activityLabelsCounterAltPrecedence.putItem(caseId, counter);
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsAltPrecedence.keySet()){
			for(String param2 : activityLabelsAltPrecedence.keySet()){
				if(!param1.equals(param2)){
					double fulfill = 0;
					//double viol = 0;
					double act = 0;
					for(String caseId : activityLabelsCounterAltPrecedence.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterAltPrecedence.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTraceAlt.getItem(caseId);

						if(counter!= null && counter.containsKey(param2)){
							double totnumber = counter.get(param2);
							act = act + totnumber;
							if(fulfillForThisTrace != null && fulfillForThisTrace.containsKey(param1)){
								if(fulfillForThisTrace.get(param1).containsKey(param2)){	
									//double stillpending = fulfillForThisTrace.get(param1).get(param2);
									fulfill = fulfill + fulfillForThisTrace.get(param1).get(param2);
									//viol = viol + stillpending;
								}
							}
						}

					}
					d.addAlternatePrecedence(param1, param2, act, fulfill);
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsAltPrecedence.size() +
				activityLabelsCounterAltPrecedence.size() +
				fulfilledConstraintsPerTraceAlt.size() +
				satisfactionsConstraintsPerTrace.size();
	}
}
