package beamline.declare.miners.events.lossycounting.constraints;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;

import beamline.declare.data.LossyCounting;
import beamline.declare.miners.events.lossycounting.LCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class AlternatePrecedence implements LCTemplateReplayer {

	private Set<String> activityLabelsAltPrecedence = Sets.<String>newConcurrentHashSet();
	private LossyCounting<ConcurrentHashMap<String, Integer>> activityLabelsCounterAltPrecedence = new LossyCounting<ConcurrentHashMap<String, Integer>>();
	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTraceAlt = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>();
	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> satisfactionsConstraintsPerTrace = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>();
	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>> isDuplicatedActivationPerTrace = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>>();

	@Override
	public void addObservation(String caseId, Integer currentBucket) {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> ex3 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		@SuppressWarnings("rawtypes")
		Class class3 = ex3.getClass();

		try {
			satisfactionsConstraintsPerTrace.addObservation(caseId, currentBucket, class1);
			fulfilledConstraintsPerTraceAlt.addObservation(caseId, currentBucket, class1);
			activityLabelsCounterAltPrecedence.addObservation(caseId, currentBucket, class2);
			isDuplicatedActivationPerTrace.addObservation(caseId, currentBucket, class3);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup(Integer currentBucket) {
		satisfactionsConstraintsPerTrace.cleanup(currentBucket);
		fulfilledConstraintsPerTraceAlt.cleanup(currentBucket);
		activityLabelsCounterAltPrecedence.cleanup(currentBucket);
		isDuplicatedActivationPerTrace.cleanup(currentBucket);
	}

	@Override
	public void process(String event, String caseId) {
		activityLabelsAltPrecedence.add(event);
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if(!activityLabelsCounterAltPrecedence.containsKey(caseId)){
			activityLabelsCounterAltPrecedence.putItem(caseId, counter);
		}else{
			counter = activityLabelsCounterAltPrecedence.getItem(caseId);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> fulfilledForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(!fulfilledConstraintsPerTraceAlt.containsKey(caseId)){
			fulfilledConstraintsPerTraceAlt.putItem(caseId, fulfilledForThisTrace);
		}else{
			fulfilledForThisTrace = fulfilledConstraintsPerTraceAlt.getItem(caseId);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> satisfactionsForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(!satisfactionsConstraintsPerTrace.containsKey(caseId)){
			satisfactionsConstraintsPerTrace.putItem(caseId, satisfactionsForThisTrace);
		}else{
			satisfactionsForThisTrace = satisfactionsConstraintsPerTrace.getItem(caseId);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Boolean>> isDuplicatedForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Boolean>>();
		if(!isDuplicatedActivationPerTrace.containsKey(caseId)){
			isDuplicatedActivationPerTrace.putItem(caseId, isDuplicatedForThisTrace);
		}else{
			isDuplicatedForThisTrace = isDuplicatedActivationPerTrace.getItem(caseId);
		}
		if(activityLabelsAltPrecedence.size()>1){
			for(String existingEvent : activityLabelsAltPrecedence){
				if(!existingEvent.equals(event)){
					boolean violated = false;
					if(isDuplicatedForThisTrace.containsKey(event)){
						if(isDuplicatedForThisTrace.get(event).containsKey(existingEvent) && isDuplicatedForThisTrace.get(event).get(existingEvent)){
							violated = true;
						}
						isDuplicatedForThisTrace.get(event).put(existingEvent, true);
					}
					if(isDuplicatedForThisTrace.containsKey(existingEvent)){
						isDuplicatedForThisTrace.get(existingEvent).put(event, false);
					}
					if(!isDuplicatedForThisTrace.containsKey(event)){
						ConcurrentHashMap<String, Boolean> sec = new ConcurrentHashMap<String,Boolean>(); 
						sec.put(existingEvent, true);
						isDuplicatedForThisTrace.put(event, sec);
					}
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
							if(!violated){
								secondElement.put(event, fulfillments + 1);
								fulfilledForThisTrace.put(existingEvent, secondElement);
							}
						}
					}
				}
			}
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
		activityLabelsCounterAltPrecedence.putItem(caseId, counter);
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsAltPrecedence){
			for(String param2 : activityLabelsAltPrecedence){
				if(!param1.equals(param2)){
					double fulfill = 0;
					//double viol = 0;
					double act = 0;
					for(String caseId : activityLabelsCounterAltPrecedence.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterAltPrecedence.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTraceAlt.getItem(caseId);

						if(counter.containsKey(param2)){
							double totnumber = counter.get(param2);
							act = act + totnumber;
							if(fulfillForThisTrace.containsKey(param1)){
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
				activityLabelsCounterAltPrecedence.getSize() +
				fulfilledConstraintsPerTraceAlt.getSize() +
				satisfactionsConstraintsPerTrace.getSize()
				+isDuplicatedActivationPerTrace.getSize();
	}
}