package beamline.declare.miners.events.lossycounting.constraints;

import java.util.HashMap;
import java.util.HashSet;

import beamline.declare.data.LossyCounting;
import beamline.declare.miners.events.lossycounting.LCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class ChainSuccession implements LCTemplateReplayer {

	private HashSet<String> activityLabelsChResponse = new HashSet<String>();
	private LossyCounting<HashMap<String, Integer>> activityLabelsCounterChResponse = new LossyCounting<HashMap<String, Integer>>();
	private LossyCounting<HashMap<String, HashMap<String, Integer>>> fulfilledConstraintsPerTraceCh = new LossyCounting<HashMap<String, HashMap<String, Integer>>>();
	private LossyCounting<String> lastActivityResponse = new LossyCounting<String>();
	
	private HashSet<String> activityLabelsChPrecedence = new HashSet<String>();
	private LossyCounting<HashMap<String,Integer>> activityLabelsCounterChPrecedence = new LossyCounting<HashMap<String, Integer>>();
	private LossyCounting<HashMap<String, HashMap<String, Integer>>> fulfilledConstraintsPerTraceChPrecedence = new LossyCounting<HashMap<String, HashMap<String, Integer>>>();
	private LossyCounting<String> lastActivity = new LossyCounting<String>();
	
	@Override
	public void addObservation(String caseId, Integer currentBucket) {
		HashMap<String, HashMap<String, Integer>> ex1 = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, Integer> ex2 = new HashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		try {
			fulfilledConstraintsPerTraceCh.addObservation(caseId, currentBucket, class1);
			activityLabelsCounterChResponse.addObservation(caseId, currentBucket, class2);
			lastActivityResponse.addObservation(caseId, currentBucket, "".getClass());
			fulfilledConstraintsPerTraceChPrecedence.addObservation(caseId, currentBucket, class1);
			activityLabelsCounterChPrecedence.addObservation(caseId, currentBucket, class2);
			lastActivity.addObservation(caseId, currentBucket, "".getClass());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup(Integer currentBucket) {
		fulfilledConstraintsPerTraceCh.cleanup(currentBucket);
		activityLabelsCounterChResponse.cleanup(currentBucket);
		lastActivityResponse.cleanup(currentBucket);
		fulfilledConstraintsPerTraceChPrecedence.cleanup(currentBucket);
		activityLabelsCounterChPrecedence.cleanup(currentBucket);
		lastActivity.cleanup(currentBucket);
	}

	@Override
	public void process(String event, String caseId) {
		activityLabelsChResponse.add(event);
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		if(!activityLabelsCounterChResponse.containsKey(caseId)){
			activityLabelsCounterChResponse.putItem(caseId, counter);
		}else{
			counter = activityLabelsCounterChResponse.getItem(caseId);
		}
		HashMap<String,HashMap<String,Integer>> fulfilledForThisTrace = new HashMap<String,HashMap<String,Integer>>();
		if(!fulfilledConstraintsPerTraceCh.containsKey(caseId)){
			fulfilledConstraintsPerTraceCh.putItem(caseId, fulfilledForThisTrace);
		}else{
			fulfilledForThisTrace = fulfilledConstraintsPerTraceCh.getItem(caseId);
		}
		String previous = lastActivityResponse.getItem(caseId);
		if(previous!=null && !previous.equals("") && !previous.equals(event)){
			HashMap<String, Integer> secondElement = new  HashMap<String, Integer>();
			if(fulfilledForThisTrace.containsKey(previous)){
				secondElement = fulfilledForThisTrace.get(previous);
			}
			int nofull = 0;
			if(secondElement.containsKey(event)){
				nofull = secondElement.get(event);
			}
			secondElement.put(event, nofull+1);
			fulfilledForThisTrace.put(previous,secondElement);
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
		activityLabelsCounterChResponse.putItem(caseId, counter);
		lastActivityResponse.putItem(caseId, event);
		//***********************
		
		
		
		
		
		activityLabelsChPrecedence.add(event);
		HashMap<String, Integer> counterPrec = new HashMap<String, Integer>();
		if(!activityLabelsCounterChPrecedence.containsKey(caseId)){
			activityLabelsCounterChPrecedence.putItem(caseId, counterPrec);
		}else{
			counterPrec = activityLabelsCounterChPrecedence.getItem(caseId);
		}
		HashMap<String,HashMap<String,Integer>> fulfilledForThisTracePrec = new HashMap<String,HashMap<String,Integer>>();
		if(!fulfilledConstraintsPerTraceChPrecedence.containsKey(caseId)){
			fulfilledConstraintsPerTraceChPrecedence.putItem(caseId, fulfilledForThisTracePrec);
		}else{
			fulfilledForThisTracePrec = fulfilledConstraintsPerTraceChPrecedence.getItem(caseId);
		}
		String previousChPrecedence = lastActivity.getItem(caseId);
		if(previousChPrecedence!=null && !previousChPrecedence.equals("") && !previousChPrecedence.equals(event)){
			HashMap<String, Integer> secondElement = new  HashMap<String, Integer>();
			if(fulfilledForThisTracePrec.containsKey(previousChPrecedence)){
				secondElement = fulfilledForThisTracePrec.get(previousChPrecedence);
			}
			int nofull = 0;
			if(secondElement.containsKey(event)){
				nofull = secondElement.get(event);
			}
			secondElement.put(event, nofull+1);
			fulfilledForThisTracePrec.put(previousChPrecedence,secondElement);
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
		activityLabelsCounterChPrecedence.putItem(caseId, counterPrec);
		//***********************
		lastActivity.putItem(caseId, event);
		
		
		
		
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsChResponse){
			for(String param2 : activityLabelsChResponse){
				if(!param1.equals(param2)){

					double fulfill = 0;
					double act = 0;
					for(String caseId : activityLabelsCounterChResponse.keySet()) {
						HashMap<String, Integer> counter = activityLabelsCounterChResponse.getItem(caseId);
						HashMap<String, HashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTraceCh.getItem(caseId);

						if(counter.containsKey(param1)){
							double totnumber = counter.get(param1);
							act = act + totnumber;
							if(fulfillForThisTrace.containsKey(param1)){
								if(fulfillForThisTrace.get(param1).containsKey(param2)){	
									double currentFullfill = fulfillForThisTrace.get(param1).get(param2);
									fulfill = fulfill + currentFullfill;
								}
							}
						}
					}
					for(String caseId : activityLabelsCounterChPrecedence.keySet()) {
						HashMap<String, Integer> counter = activityLabelsCounterChPrecedence.getItem(caseId);
						HashMap<String, HashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTraceChPrecedence.getItem(caseId);

						if(counter.containsKey(param2)){
							double totnumber = counter.get(param2);
							act = act + totnumber;
							if(fulfillForThisTrace.containsKey(param1)){
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
				activityLabelsCounterChResponse.getSize() +
				fulfilledConstraintsPerTraceCh.getSize() +
				lastActivityResponse.getSize() +
				activityLabelsChPrecedence.size() + 
				activityLabelsCounterChPrecedence.getSize() +
				fulfilledConstraintsPerTraceChPrecedence.getSize() +
				lastActivity.getSize();
	}
}
