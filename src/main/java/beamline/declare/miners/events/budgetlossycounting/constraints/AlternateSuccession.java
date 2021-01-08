package beamline.declare.miners.events.budgetlossycounting.constraints;

import java.util.concurrent.ConcurrentHashMap;

import beamline.declare.data.LossyCountingBudget;
import beamline.declare.data.SharedDelta;
import beamline.declare.miners.events.budgetlossycounting.BudgetLCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class AlternateSuccession implements BudgetLCTemplateReplayer {

	private SharedDelta delta = new SharedDelta();
	private LossyCountingBudget<String> activityLabelsAltResponse = null;
	private LossyCountingBudget<ConcurrentHashMap<String, Integer>> activityLabelsCounterAltResponse = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> pendingConstraintsPerTraceAlt = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> violatedConstraintsPerTrace = null;
	
	private LossyCountingBudget<String> activityLabelsAltPrecedence = null;
	private LossyCountingBudget<ConcurrentHashMap<String, Integer>> activityLabelsCounterAltPrecedence = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> fulfilledConstraintsPerTraceAlt = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> satisfactionsConstraintsPerTrace = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>> isDuplicatedActivationPerTrace = null;
	

	public AlternateSuccession(int budget) {
		delta.budget = budget;
		
		activityLabelsAltResponse = new LossyCountingBudget<String>(delta);
		activityLabelsCounterAltResponse = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		pendingConstraintsPerTraceAlt = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
		violatedConstraintsPerTrace = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
		
		activityLabelsAltPrecedence = new LossyCountingBudget<String>(delta);
		activityLabelsCounterAltPrecedence = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		fulfilledConstraintsPerTraceAlt = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
		satisfactionsConstraintsPerTrace = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
		isDuplicatedActivationPerTrace = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>>(delta);
	}
	
	@Override
	public void process(String event, String trace) throws InstantiationException, IllegalAccessException {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> ex3 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		@SuppressWarnings("rawtypes")
		Class class3 = ex3.getClass();
		
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if(activityLabelsCounterAltResponse.containsKey(trace)){
			counter = activityLabelsCounterAltResponse.getItem(trace);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> pendingForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(pendingConstraintsPerTraceAlt.containsKey(trace)){
			pendingForThisTrace = pendingConstraintsPerTraceAlt.getItem(trace);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> violatedForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(violatedConstraintsPerTrace.containsKey(trace)){
			violatedForThisTrace = violatedConstraintsPerTrace.getItem(trace);
		}
		activityLabelsAltResponse.addObservation(event, String.class);

		if(!counter.containsKey(event)){
			if(activityLabelsAltResponse.size()>1){	
				for(String existingEvent : activityLabelsAltResponse.keySet()){
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
							
							violatedConstraintsPerTrace.addObservation(trace, class1);
							violatedConstraintsPerTrace.putItem(trace, violatedForThisTrace);
						}
						secondElement.put(event, 0);
						pendingForThisTrace.put(existingEvent, secondElement);

						//	pendingConstraintsPerTraceAlt.put(trace, pendingForThisTrace);
						//					}
					}
				}
				for(String existingEvent : activityLabelsAltResponse.keySet()){
					if(!existingEvent.equals(event)){
						ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
						if(pendingForThisTrace.containsKey(event)){
							secondElement = pendingForThisTrace.get(event);
						}
						secondElement.put(existingEvent, 1);
						pendingForThisTrace.put(event,secondElement);
					}
				}

				pendingConstraintsPerTraceAlt.addObservation(trace, class1);
				pendingConstraintsPerTraceAlt.putItem(trace, pendingForThisTrace);

			}
		}else{

			for(String firstElement : activityLabelsAltResponse.keySet()){
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
						
						violatedConstraintsPerTrace.addObservation(trace, class1);
						violatedConstraintsPerTrace.putItem(trace, violatedForThisTrace);
					}
					secondElement.put(event, 0);
					pendingForThisTrace.put(firstElement, secondElement);

					pendingConstraintsPerTraceAlt.addObservation(trace, class1);
					pendingConstraintsPerTraceAlt.putItem(trace, pendingForThisTrace);

				}
			}
			if (pendingForThisTrace.contains(event)) {
			ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(event);
				for(String second : activityLabelsAltResponse.keySet()){
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
				
				pendingConstraintsPerTraceAlt.addObservation(trace, class1);
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
		activityLabelsCounterAltResponse.addObservation(trace, class2);
		activityLabelsCounterAltResponse.putItem(trace, counter);
		//***********************

		
		activityLabelsAltPrecedence.addObservation(event, String.class);
		ConcurrentHashMap<String, Integer> counterPrec = new ConcurrentHashMap<String, Integer>();
		if(!activityLabelsCounterAltPrecedence.containsKey(trace)){
			activityLabelsCounterAltPrecedence.addObservation(trace, class2);
			activityLabelsCounterAltPrecedence.putItem(trace, counterPrec);
		}else{
			counterPrec = activityLabelsCounterAltPrecedence.getItem(trace);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> fulfilledForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(!fulfilledConstraintsPerTraceAlt.containsKey(trace)){
			fulfilledConstraintsPerTraceAlt.addObservation(trace, class1);
			fulfilledConstraintsPerTraceAlt.putItem(trace, fulfilledForThisTrace);
		}else{
			fulfilledForThisTrace = fulfilledConstraintsPerTraceAlt.getItem(trace);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> satisfactionsForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(!satisfactionsConstraintsPerTrace.containsKey(trace)){
			satisfactionsConstraintsPerTrace.addObservation(trace, class1);
			satisfactionsConstraintsPerTrace.putItem(trace, satisfactionsForThisTrace);
		}else{
			satisfactionsForThisTrace = satisfactionsConstraintsPerTrace.getItem(trace);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Boolean>> isDuplicatedForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Boolean>>();
		if(!isDuplicatedActivationPerTrace.containsKey(trace)){
			isDuplicatedActivationPerTrace.addObservation(trace, class3);
			isDuplicatedActivationPerTrace.putItem(trace, isDuplicatedForThisTrace);
		}else{
			isDuplicatedForThisTrace = isDuplicatedActivationPerTrace.getItem(trace);
		}
		if(activityLabelsAltPrecedence.size()>1){
			for(String existingEvent : activityLabelsAltPrecedence.keySet()){
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
			fulfilledConstraintsPerTraceAlt.addObservation(trace, class1);
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
		activityLabelsCounterAltPrecedence.addObservation(trace, class2);
		activityLabelsCounterAltPrecedence.putItem(trace, counterPrec);
		
	}

	@Override
	public void updateModel(DeclareModel d) {
		
		for(String param1 : activityLabelsAltResponse.keySet()){
			for(String param2 : activityLabelsAltResponse.keySet()){
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
							if(pendingForThisTrace != null && pendingForThisTrace.containsKey(param1)){
								if(pendingForThisTrace.get(param1).containsKey(param2)){	
									double stillpending = pendingForThisTrace.get(param1).get(param2);
									totPending += stillpending;
									fulfill = fulfill + (totnumber - stillpending);
									if(violForThisTrace != null && violForThisTrace.containsKey(param1)){
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
									if(violForThisTrace != null && violForThisTrace.containsKey(param1)){
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

						if(counter != null && counter.containsKey(param2)){
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
		return 	activityLabelsAltResponse.size() +
				activityLabelsCounterAltResponse.size() +
				pendingConstraintsPerTraceAlt.size() +
				violatedConstraintsPerTrace.size() +
				activityLabelsAltPrecedence.size() +
				activityLabelsCounterAltPrecedence.size() +
				fulfilledConstraintsPerTraceAlt.size() +
				satisfactionsConstraintsPerTrace.size() +
				isDuplicatedActivationPerTrace.size();
	}

}
