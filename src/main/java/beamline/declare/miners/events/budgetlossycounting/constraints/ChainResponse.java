package beamline.declare.miners.events.budgetlossycounting.constraints;

import java.util.concurrent.ConcurrentHashMap;

import beamline.declare.data.LossyCountingBudget;
import beamline.declare.data.SharedDelta;
import beamline.declare.miners.events.budgetlossycounting.BudgetLCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class ChainResponse implements BudgetLCTemplateReplayer {

	private SharedDelta delta = new SharedDelta();
	private LossyCountingBudget<String> activityLabelsChResponse = null;
	private LossyCountingBudget<ConcurrentHashMap<String, Integer>> activityLabelsCounterChResponse = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTraceCh = null;
	private LossyCountingBudget<String> lastActivity = null;
	
	public ChainResponse(int budget) {
		delta.budget = budget;
		activityLabelsChResponse = new LossyCountingBudget<String>(delta);
		activityLabelsCounterChResponse = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		fulfilledConstraintsPerTraceCh = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
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
		
		String previous = lastActivity.getItem(caseId);
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
		lastActivity.addObservation(caseId, String.class);
		lastActivity.putItem(caseId, event);
		//***********************
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
					d.addChainResponse(param1, param2, act, fulfill);
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsChResponse.size() +
				activityLabelsCounterChResponse.size() +
				fulfilledConstraintsPerTraceCh.size() +
				1; // this is for the lastActivity field
	}

}
