package beamline.declare.miners.events.lossycounting.constraints;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;

import beamline.declare.data.LossyCounting;
import beamline.declare.miners.events.lossycounting.LCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class AlternateSuccession implements LCTemplateReplayer {

	private Set<String> activityLabelsAltResponse = Sets.<String>newConcurrentHashSet();
	private LossyCounting<ConcurrentHashMap<String, Integer>> activityLabelsCounterAltResponse = new LossyCounting<ConcurrentHashMap<String, Integer>>();
	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> pendingConstraintsPerTraceAlt = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>();
	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> violatedConstraintsPerTrace = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>();
	//	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>> isDuplicatedActivationPerTrace = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>>();
	
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
			pendingConstraintsPerTraceAlt.addObservation(caseId, currentBucket, class1);
			violatedConstraintsPerTrace.addObservation(caseId, currentBucket, class1);
			activityLabelsCounterAltResponse.addObservation(caseId, currentBucket, class2);
			satisfactionsConstraintsPerTrace.addObservation(caseId, currentBucket, class1);
			fulfilledConstraintsPerTraceAlt.addObservation(caseId, currentBucket, class1);
			activityLabelsCounterAltPrecedence.addObservation(caseId, currentBucket, class2);
			isDuplicatedActivationPerTrace.addObservation(caseId, currentBucket, class3);
			//		isDuplicatedActivationPerTrace.addObservation(caseId, currentBucket, class3);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup(Integer currentBucket) {
		pendingConstraintsPerTraceAlt.cleanup(currentBucket);
		violatedConstraintsPerTrace.cleanup(currentBucket);
		activityLabelsCounterAltResponse.cleanup(currentBucket);
		//isDuplicatedActivationPerTrace.cleanup(currentBucket);
		satisfactionsConstraintsPerTrace.cleanup(currentBucket);
		fulfilledConstraintsPerTraceAlt.cleanup(currentBucket);
		activityLabelsCounterAltPrecedence.cleanup(currentBucket);
		isDuplicatedActivationPerTrace.cleanup(currentBucket);
	}

	@Override
	public void process(String event, String trace) {
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if(!activityLabelsCounterAltResponse.containsKey(trace)){
			activityLabelsCounterAltResponse.putItem(trace, counter);
		}else{
			counter = activityLabelsCounterAltResponse.getItem(trace);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> pendingForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(!pendingConstraintsPerTraceAlt.containsKey(trace)){
			pendingConstraintsPerTraceAlt.putItem(trace, pendingForThisTrace);
		}else{
			pendingForThisTrace = pendingConstraintsPerTraceAlt.getItem(trace);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> violatedForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(!violatedConstraintsPerTrace.containsKey(trace)){
			violatedConstraintsPerTrace.putItem(trace, violatedForThisTrace);
		}else{
			violatedForThisTrace = violatedConstraintsPerTrace.getItem(trace);
		}
		activityLabelsAltResponse.add(event);

		if(!counter.containsKey(event)){
			if(activityLabelsAltResponse.size()>1){	
				for(String existingEvent : activityLabelsAltResponse){
					if(!existingEvent.equals(event)){
						int pend = 0;
						if(activityLabelsCounterAltResponse.containsKey(trace)){
							if(activityLabelsCounterAltResponse.getItem(trace).containsKey(existingEvent)){
								pend = activityLabelsCounterAltResponse.getItem(trace).get(existingEvent);
							}
						}
						ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
						if(pendingForThisTrace.containsKey(existingEvent)){
							secondElement = pendingForThisTrace.get(existingEvent);
						}
						if(pend>1){
							ConcurrentHashMap<String, Integer> secondEl = new  ConcurrentHashMap<String, Integer>();
							if(violatedForThisTrace.containsKey(existingEvent)){
								secondEl = violatedForThisTrace.get(existingEvent);
							}
							secondEl.put(event, pend-1);
							violatedForThisTrace.put(existingEvent, secondEl);
							violatedConstraintsPerTrace.putItem(trace, violatedForThisTrace);
						}
						secondElement.put(event, 0);
						pendingForThisTrace.put(existingEvent, secondElement);

						//	pendingConstraintsPerTraceAlt.put(trace, pendingForThisTrace);
						//					}
					}
				}
				for(String existingEvent : activityLabelsAltResponse){
					if(!existingEvent.equals(event)){
						ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
						if(pendingForThisTrace.containsKey(event)){
							secondElement = pendingForThisTrace.get(event);
						}
						secondElement.put(existingEvent, 1);
						pendingForThisTrace.put(event,secondElement);
					}
				}

				pendingConstraintsPerTraceAlt.putItem(trace, pendingForThisTrace);

			}
		}else{

			for(String firstElement : activityLabelsAltResponse){
				if(!firstElement.equals(event)){
					ConcurrentHashMap<String, Integer> secondEl = new  ConcurrentHashMap<String, Integer>();
					if(violatedForThisTrace.containsKey(firstElement)){
						secondEl = violatedForThisTrace.get(firstElement);
					}
					ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
					if(pendingForThisTrace.containsKey(firstElement)){
						secondElement = pendingForThisTrace.get(firstElement);
					}
					
					if(secondElement.containsKey(event) && secondElement.get(event)>1){
						Integer violNo = secondElement.get(event);
						Integer totviol = 0;
						if(secondEl.containsKey(event)){
							totviol = secondEl.get(event);
						}
						secondEl.put(event, totviol + violNo);
						violatedForThisTrace.put(firstElement, secondEl);
						violatedConstraintsPerTrace.putItem(trace, violatedForThisTrace);
					}
					secondElement.put(event, 0);
					pendingForThisTrace.put(firstElement, secondElement);

					pendingConstraintsPerTraceAlt.putItem(trace, pendingForThisTrace);

				}
			}
			ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(event);
			if(secondElement!=null){
				for(String second : activityLabelsAltResponse){
					if(!second.equals(event)){
						Integer pendingNo = 1;
						if(secondElement.containsKey(second)){
							pendingNo = secondElement.get(second);	
							pendingNo ++;
						}
						secondElement.put(second, pendingNo);
					}
				}
				pendingForThisTrace.put(event,secondElement);
				pendingConstraintsPerTraceAlt.putItem(trace, pendingForThisTrace);
			}
			//activityLabelsCounter.put(trace, counter);

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
		activityLabelsCounterAltResponse.putItem(trace, counter);
		//***********************

		
		
		
		activityLabelsAltPrecedence.add(event);
		ConcurrentHashMap<String, Integer> counterPrec = new ConcurrentHashMap<String, Integer>();
		if(!activityLabelsCounterAltPrecedence.containsKey(trace)){
			activityLabelsCounterAltPrecedence.putItem(trace, counterPrec);
		}else{
			counterPrec = activityLabelsCounterAltPrecedence.getItem(trace);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> fulfilledForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(!fulfilledConstraintsPerTraceAlt.containsKey(trace)){
			fulfilledConstraintsPerTraceAlt.putItem(trace, fulfilledForThisTrace);
		}else{
			fulfilledForThisTrace = fulfilledConstraintsPerTraceAlt.getItem(trace);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> satisfactionsForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(!satisfactionsConstraintsPerTrace.containsKey(trace)){
			satisfactionsConstraintsPerTrace.putItem(trace, satisfactionsForThisTrace);
		}else{
			satisfactionsForThisTrace = satisfactionsConstraintsPerTrace.getItem(trace);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Boolean>> isDuplicatedForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Boolean>>();
		if(!isDuplicatedActivationPerTrace.containsKey(trace)){
			isDuplicatedActivationPerTrace.putItem(trace, isDuplicatedForThisTrace);
		}else{
			isDuplicatedForThisTrace = isDuplicatedActivationPerTrace.getItem(trace);
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
					if(counterPrec.containsKey(existingEvent)){
						if(satisfactionsForThisTrace.get(existingEvent) == null || satisfactionsForThisTrace.get(existingEvent).get(event) == null || satisfactionsForThisTrace.get(existingEvent).get(event)<2){
							if(!violated){
								secondElement.put(event, fulfillments + 1);
								fulfilledForThisTrace.put(existingEvent, secondElement);
							}
						}
					}
				}
			}
			fulfilledConstraintsPerTraceAlt.putItem(trace, fulfilledForThisTrace);
		}
		numberOfEvents = 1;
		if(!counterPrec.containsKey(event)){
			counterPrec.put(event, numberOfEvents);
		}else{
			numberOfEvents = counterPrec.get(event);
			numberOfEvents++;
			counterPrec.put(event, numberOfEvents); 
		}
		activityLabelsCounterAltPrecedence.putItem(trace, counterPrec);
		
	}

	@Override
	public void updateModel(DeclareModel d) {
		
		for(String param1 : activityLabelsAltResponse){
			for(String param2 : activityLabelsAltResponse){
				if(!param1.equals(param2)){

					double fulfill = 0;
					//double viol = 0;
					double act = 0;

					@SuppressWarnings("unused")
					double totViol = 0;
					@SuppressWarnings("unused")
					double totPending = 0;

					for(String caseId : activityLabelsCounterAltResponse.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterAltResponse.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> pendingForThisTrace = pendingConstraintsPerTraceAlt.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> violForThisTrace = violatedConstraintsPerTrace.getItem(caseId);
						if(counter.containsKey(param1)){
							double totnumber = counter.get(param1);
							act = act + totnumber;
							if(pendingForThisTrace.containsKey(param1)){
								if(pendingForThisTrace.get(param1).containsKey(param2)){	
									double stillpending = pendingForThisTrace.get(param1).get(param2);
									totPending += stillpending;
									fulfill = fulfill + (totnumber - stillpending);
									if(violForThisTrace.containsKey(param1)){
										if(violForThisTrace.get(param1).containsKey(param2)){	
											double viol = violForThisTrace.get(param1).get(param2);
											totViol += viol;
											fulfill = fulfill - viol;
											//viol = viol + stillpending;
										}
									}
								}else{
									double stillpending = counter.get(param1);
									totPending += stillpending;
									fulfill = fulfill + (totnumber - stillpending);
									if(violForThisTrace.containsKey(param1)){
										if(violForThisTrace.get(param1).containsKey(param2)){	
											double viol = violForThisTrace.get(param1).get(param2);
											totViol += viol;
											fulfill = fulfill - viol;
											//viol = viol + stillpending;
										}
									}
								}
							}
						}
					}
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
					d.addAlternateSuccession(param1, param2, act, fulfill);
				}
			}
		}
		
		
		
//		for(String param1 : activityLabelsAltPrecedence){
//			for(String param2 : activityLabelsAltPrecedence){
//				if(!param1.equals(param2)){
//					double fulfill = 0;
//					//double viol = 0;
//					double act = 0;
//					
//					d.addAlternatePrecedence(param1, param2, act, fulfill);
//				}
//			}
//		}
		
		
		
	}

	@Override
	public Integer getSize() {
		return activityLabelsAltResponse.size() +
				activityLabelsCounterAltResponse.getSize() +
				pendingConstraintsPerTraceAlt.getSize() +
				violatedConstraintsPerTrace.getSize() + 
				activityLabelsAltPrecedence.size() +
				activityLabelsCounterAltPrecedence.getSize() +
				fulfilledConstraintsPerTraceAlt.getSize() +
				satisfactionsConstraintsPerTrace.getSize() +
				isDuplicatedActivationPerTrace.getSize();
	}

}
