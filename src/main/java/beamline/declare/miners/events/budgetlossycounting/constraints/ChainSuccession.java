package beamline.declare.miners.events.budgetlossycounting.constraints;

import java.util.concurrent.ConcurrentHashMap;

import beamline.declare.data.LossyCountingBudget;
import beamline.declare.data.SharedDelta;
import beamline.declare.miners.events.budgetlossycounting.BudgetLCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class ChainSuccession implements BudgetLCTemplateReplayer {

	private SharedDelta delta = new SharedDelta();
	private LossyCountingBudget<String> activityLabelsChResponse = null;
	private LossyCountingBudget<ConcurrentHashMap<String, Integer>> activityLabelsCounterChResponse = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTraceCh = null;
	private LossyCountingBudget<String> lastActivityResponse = null;
	
	private LossyCountingBudget<String> activityLabelsChPrecedence = null;
	private LossyCountingBudget<ConcurrentHashMap<String,Integer>> activityLabelsCounterChPrecedence = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTraceChPrecedence = null;
	private LossyCountingBudget<String> lastActivity = null;
	
	public ChainSuccession(int budget) {
		delta.budget = budget;
		
		activityLabelsChResponse = new LossyCountingBudget<String>(delta);
		activityLabelsCounterChResponse = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		fulfilledConstraintsPerTraceCh = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
		lastActivityResponse = new LossyCountingBudget<String>(delta);
		
		activityLabelsChPrecedence = new LossyCountingBudget<String>(delta);
		activityLabelsCounterChPrecedence = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		fulfilledConstraintsPerTraceChPrecedence = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
		lastActivity = new LossyCountingBudget<String>(delta);
	}
	
	@Override
	public void process(String event, String caseId) throws InstantiationException, IllegalAccessException {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		
		activityLabelsChResponse.addObservation(event, String.class);
		
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if(activityLabelsCounterChResponse.containsKey(caseId)){
			counter = activityLabelsCounterChResponse.getItem(caseId);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> fulfilledForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(fulfilledConstraintsPerTraceCh.containsKey(caseId)){
			fulfilledForThisTrace = fulfilledConstraintsPerTraceCh.getItem(caseId);
		}
		String previous = lastActivityResponse.getItem(caseId);
		if(previous!=null && !previous.equals("") && !previous.equals(event)){
			ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
			if(fulfilledForThisTrace.containsKey(previous)){
				secondElement = fulfilledForThisTrace.get(previous);
			}
			int nofull = 0;
			if(secondElement.containsKey(event)){
				nofull = secondElement.get(event);
			}
			secondElement.put(event, nofull+1);
			fulfilledForThisTrace.put(previous,secondElement);
			fulfilledConstraintsPerTraceCh.addObservation(caseId, class1);
			fulfilledConstraintsPerTraceCh.putItem(caseId, fulfilledForThisTrace);
		}

		//update the counter for the current trace and the current event
		//**********************

		int numberOfEvents = 1;
		if(!counter.containsKey(event)){
			counter.put(event, numberOfEvents);
		}else{
			numberOfEvents = counter.get(event);
			numberOfEvents++;
			counter.put(event, numberOfEvents);
		}
		
		activityLabelsCounterChResponse.addObservation(caseId, class2);
		activityLabelsCounterChResponse.putItem(caseId, counter);
		
		lastActivityResponse.addObservation(caseId, String.class);
		lastActivityResponse.putItem(caseId, event);
		//***********************
		
		
		
		activityLabelsChPrecedence.addObservation(event, String.class);
		
		ConcurrentHashMap<String, Integer> counterPrec = new ConcurrentHashMap<String, Integer>();
		if(activityLabelsCounterChPrecedence.containsKey(caseId)){
			counterPrec = activityLabelsCounterChPrecedence.getItem(caseId);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> fulfilledForThisTracePrec = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(fulfilledConstraintsPerTraceChPrecedence.containsKey(caseId)){
			fulfilledForThisTracePrec = fulfilledConstraintsPerTraceChPrecedence.getItem(caseId);
		}
		String previousChPrecedence = lastActivity.getItem(caseId);
		if(previousChPrecedence!=null && !previousChPrecedence.equals("") && !previousChPrecedence.equals(event)){
			ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
			if(fulfilledForThisTracePrec.containsKey(previousChPrecedence)){
				secondElement = fulfilledForThisTracePrec.get(previousChPrecedence);
			}
			int nofull = 0;
			if(secondElement.containsKey(event)){
				nofull = secondElement.get(event);
			}
			secondElement.put(event, nofull+1);
			fulfilledForThisTracePrec.put(previousChPrecedence,secondElement);
			fulfilledConstraintsPerTraceChPrecedence.addObservation(caseId, class1);
			fulfilledConstraintsPerTraceChPrecedence.putItem(caseId, fulfilledForThisTracePrec);
		}

		//update the counter for the current trace and the current event
		//**********************

		numberOfEvents = 1;
		if(!counterPrec.containsKey(event)){
			counterPrec.put(event, numberOfEvents);
		}else{
			numberOfEvents = counterPrec.get(event);
			numberOfEvents++;
			counterPrec.put(event, numberOfEvents);
		}
		activityLabelsCounterChPrecedence.addObservation(caseId, class2);
		activityLabelsCounterChPrecedence.putItem(caseId, counterPrec);
		//***********************
		
		lastActivity.addObservation(caseId, String.class);
		lastActivity.putItem(caseId, event);
		
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsChResponse.keySet()){
			for(String param2 : activityLabelsChResponse.keySet()){
				if(!param1.equals(param2)){

					double fulfill = 0;
					double act = 0;
					for(String caseId : activityLabelsCounterChResponse.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterChResponse.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTraceCh.getItem(caseId);

						if(counter != null && counter.containsKey(param1)){
							double totnumber = counter.get(param1);
							act = act + totnumber;
							if(fulfillForThisTrace != null && fulfillForThisTrace.containsKey(param1)){
								if(fulfillForThisTrace.get(param1).containsKey(param2)){	
									double currentFullfill = fulfillForThisTrace.get(param1).get(param2);
									fulfill = fulfill + currentFullfill;
								}
							}
						}
					}
					for(String caseId : activityLabelsCounterChPrecedence.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterChPrecedence.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTraceChPrecedence.getItem(caseId);

						if(counter != null && counter.containsKey(param2)){
							double totnumber = counter.get(param2);
							act = act + totnumber;
							if(fulfillForThisTrace != null && fulfillForThisTrace.containsKey(param1)){
								if(fulfillForThisTrace.get(param1).containsKey(param2)){	
									double currentFullfill = fulfillForThisTrace.get(param1).get(param2);
									fulfill = fulfill + currentFullfill;
									//viol = viol + stillpending;
								}
							}
						}

					}
					d.addChainSuccession(param1, param2, act, fulfill);
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsChResponse.size() +
			activityLabelsCounterChResponse.size() +
			fulfilledConstraintsPerTraceCh.size() +
			lastActivityResponse.size() +
			activityLabelsChPrecedence.size() +
			activityLabelsCounterChPrecedence.size() +
			fulfilledConstraintsPerTraceChPrecedence.size() +
			lastActivity.size();
	}
}
