package beamline.declare.miners.events.lossycounting.constraints;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;

import beamline.declare.data.LossyCounting;
import beamline.declare.miners.events.lossycounting.LCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class ChainPrecedence implements LCTemplateReplayer {

	private Set<String> activityLabelsChPrecedence = Sets.<String>newConcurrentHashSet();
	private LossyCounting<ConcurrentHashMap<String,Integer>> activityLabelsCounterChPrecedence = new LossyCounting<ConcurrentHashMap<String, Integer>>();
	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTraceChPrecedence = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>();
	private LossyCounting<String> lastActivity = new LossyCounting<String>();

	@Override
	public void addObservation(String caseId, Integer currentBucket) {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		try {
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
		fulfilledConstraintsPerTraceChPrecedence.cleanup(currentBucket);
		activityLabelsCounterChPrecedence.cleanup(currentBucket);
		lastActivity.cleanup(currentBucket);
	}

	@Override
	public void process(String event, String caseId) {
		activityLabelsChPrecedence.add(event);
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if(!activityLabelsCounterChPrecedence.containsKey(caseId)){
			activityLabelsCounterChPrecedence.putItem(caseId, counter);
		}else{
			counter = activityLabelsCounterChPrecedence.getItem(caseId);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> fulfilledForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(!fulfilledConstraintsPerTraceChPrecedence.containsKey(caseId)){
			fulfilledConstraintsPerTraceChPrecedence.putItem(caseId, fulfilledForThisTrace);
		}else{
			fulfilledForThisTrace = fulfilledConstraintsPerTraceChPrecedence.getItem(caseId);
		}
		String previousChPrecedence = lastActivity.getItem(caseId);
		if(previousChPrecedence!=null && !previousChPrecedence.equals("") && !previousChPrecedence.equals(event)){
			ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
			if(fulfilledForThisTrace.containsKey(previousChPrecedence)){
				secondElement = fulfilledForThisTrace.get(previousChPrecedence);
			}
			int nofull = 0;
			if(secondElement.containsKey(event)){
				nofull = secondElement.get(event);
			}
			secondElement.put(event, nofull+1);
			fulfilledForThisTrace.put(previousChPrecedence,secondElement);
			fulfilledConstraintsPerTraceChPrecedence.putItem(caseId, fulfilledForThisTrace);
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
		activityLabelsCounterChPrecedence.putItem(caseId, counter);
		//***********************
		lastActivity.putItem(caseId, event);
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsChPrecedence){
			for(String param2 : activityLabelsChPrecedence){
				if(!param1.equals(param2)){

					double fulfill = 0;
					double act = 0;
					for(String caseId : activityLabelsCounterChPrecedence.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterChPrecedence.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTraceChPrecedence.getItem(caseId);

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
					d.addChainPrecedence(param1, param2, act, fulfill);
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsChPrecedence.size() +
				activityLabelsCounterChPrecedence.getSize() +
				fulfilledConstraintsPerTraceChPrecedence.getSize() +
				1; // this is for lastActivity field
	}
}
