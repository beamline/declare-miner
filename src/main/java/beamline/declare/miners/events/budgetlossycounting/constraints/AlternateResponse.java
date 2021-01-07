package beamline.declare.miners.events.budgetlossycounting.constraints;

import java.util.concurrent.ConcurrentHashMap;

import beamline.declare.data.LossyCountingBudget;
import beamline.declare.data.SharedDelta;
import beamline.declare.miners.events.budgetlossycounting.BudgetLCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class AlternateResponse implements BudgetLCTemplateReplayer {

	private SharedDelta delta = new SharedDelta();
	private LossyCountingBudget<String> activityLabelsAltResponse = null;
	private LossyCountingBudget<ConcurrentHashMap<String, Integer>> activityLabelsCounterAltResponse = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> pendingConstraintsPerTraceAlt = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> violatedConstraintsPerTrace = null;
	
	public AlternateResponse(int budget) {
		delta.budget = budget;
		activityLabelsAltResponse = new LossyCountingBudget<String>(delta);
		activityLabelsCounterAltResponse = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		pendingConstraintsPerTraceAlt = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
		violatedConstraintsPerTrace = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
	}

	@Override
	public void process(String event, String trace) throws InstantiationException, IllegalAccessException {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		activityLabelsAltResponse.addObservation(event, String.class);
		
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
							secondEl.put(event, pend);
							violatedForThisTrace.put(existingEvent, secondEl);
							violatedConstraintsPerTrace.addObservation(trace, class1);
							violatedConstraintsPerTrace.putItem(trace, violatedForThisTrace);
						}
						secondElement.put(event, 0);
						pendingForThisTrace.put(existingEvent, secondElement);

						//	pendingConstraintsPerTraceAlt.put(trace, pendingForThisTrace);
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

			for(String firstElement : pendingForThisTrace.keySet()){					
				if(!firstElement.equals(event)){
					ConcurrentHashMap<String, Integer> secondEl = new  ConcurrentHashMap<String, Integer>();
					if(violatedForThisTrace.containsKey(firstElement)){
						secondEl = violatedForThisTrace.get(firstElement);
					}
					ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(firstElement);
					
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
			ConcurrentHashMap<String, Integer> secondElement = pendingForThisTrace.get(event);
			for(String second : secondElement.keySet()){
				if(!second.equals(event)){
					Integer pendingNo = secondElement.get(second);
					pendingNo ++;
					secondElement.put(second, pendingNo);
				}
			}
			pendingForThisTrace.put(event,secondElement);
			
			pendingConstraintsPerTraceAlt.addObservation(trace, class1);
			pendingConstraintsPerTraceAlt.putItem(trace, pendingForThisTrace);

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
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsAltResponse.keySet()){
			for(String param2 : activityLabelsAltResponse.keySet()){
				if(!param1.equals(param2)){

					double fulfill = 0;
					//double viol = 0;
					double act = 0;
					for(String caseId : activityLabelsCounterAltResponse.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterAltResponse.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> pendingForThisTrace = pendingConstraintsPerTraceAlt.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> violForThisTrace = violatedConstraintsPerTrace.getItem(caseId);
						if(counter != null && counter.containsKey(param1)){
							double totnumber = counter.get(param1);
							act = act + totnumber;
							if(pendingForThisTrace != null && pendingForThisTrace.containsKey(param1)){
								if(pendingForThisTrace.get(param1).containsKey(param2)){	
									double stillpending = pendingForThisTrace.get(param1).get(param2);
									fulfill = fulfill + (totnumber - stillpending);
									if(violForThisTrace != null && violForThisTrace.containsKey(param1)){
										if(violForThisTrace.get(param1).containsKey(param2)){	
											double viol = violForThisTrace.get(param1).get(param2);
											fulfill = fulfill - viol;
											//viol = viol + stillpending;
										}
									}
								}
							}
						}
					}
					d.addAlternateResponse(param1, param2, act, fulfill);
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsAltResponse.size() +
				activityLabelsCounterAltResponse.size() +
				pendingConstraintsPerTraceAlt.size() +
				violatedConstraintsPerTrace.size();
	}

}
