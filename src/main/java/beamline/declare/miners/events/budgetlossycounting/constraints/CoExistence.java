package beamline.declare.miners.events.budgetlossycounting.constraints;

import java.util.concurrent.ConcurrentHashMap;

import beamline.declare.data.LossyCountingBudget;
import beamline.declare.data.SharedDelta;
import beamline.declare.miners.events.budgetlossycounting.BudgetLCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class CoExistence implements BudgetLCTemplateReplayer {

	private SharedDelta delta = new SharedDelta();
	private LossyCountingBudget<String> activityLabelsCoExistence = null;
	private LossyCountingBudget<ConcurrentHashMap<String, Integer>> activityLabelsCounterCoExistence = null;
	private LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> pendingConstraintsPerTraceCo = null;
	
	public CoExistence(int budget) {
		delta.budget = budget;
		activityLabelsCoExistence = new LossyCountingBudget<String>(delta);
		activityLabelsCounterCoExistence = new LossyCountingBudget<ConcurrentHashMap<String, Integer>>(delta);
		pendingConstraintsPerTraceCo = new LossyCountingBudget<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>(delta);
	}

	@Override
	public void process(String event, String caseId) throws InstantiationException, IllegalAccessException {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
//		if(caseId.equals("case_id_589")){
//			System.out.println("STOP!");
//		}
		
		activityLabelsCoExistence.addObservation(event, String.class);
		
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if(activityLabelsCounterCoExistence.containsKey(caseId)){
			counter = activityLabelsCounterCoExistence.getItem(caseId);
		}
		
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> pendingForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(pendingConstraintsPerTraceCo.containsKey(caseId)){
			pendingForThisTrace = pendingConstraintsPerTraceCo.getItem(caseId);
		}
		
		if(!counter.containsKey(event)){
			if(activityLabelsCoExistence.size()>1){
				for(String existingEvent : activityLabelsCoExistence.keySet()){
					if(!existingEvent.equals(event)){	
						if(counter.containsKey(existingEvent)){
							ConcurrentHashMap<String, Integer> secondElement1 = new  ConcurrentHashMap<String, Integer>();
							if(pendingForThisTrace.containsKey(existingEvent)){
								secondElement1 = pendingForThisTrace.get(existingEvent);
							}
							secondElement1.put(event, 0);
							pendingForThisTrace.put(existingEvent,secondElement1);
							ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
							if(pendingForThisTrace.containsKey(event)){
								secondElement = pendingForThisTrace.get(event);
							}
							secondElement.put(existingEvent, 0);
							pendingForThisTrace.put(event, secondElement);
						}else{
							ConcurrentHashMap<String, Integer> secondElement1 = new  ConcurrentHashMap<String, Integer>();
							if(pendingForThisTrace.containsKey(existingEvent)){
								secondElement1 = pendingForThisTrace.get(existingEvent);
							}
							secondElement1.put(event, 1);
							pendingForThisTrace.put(existingEvent,secondElement1);
							ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
							if(pendingForThisTrace.containsKey(event)){
								secondElement = pendingForThisTrace.get(event);
							}
							secondElement.put(existingEvent, 1);
							pendingForThisTrace.put(event,secondElement);
						}
						pendingConstraintsPerTraceCo.addObservation(caseId, class1);
						pendingConstraintsPerTraceCo.putItem(caseId, pendingForThisTrace);
//						pendingConstraintsPerTraceCo.put(trace, pendingForThisTrace);
					}
				}
			}
		}else{
			if(activityLabelsCoExistence.size()>1){
				for(String existingEvent : activityLabelsCoExistence.keySet()){
					if(!existingEvent.equals(event)){	
						if(counter.containsKey(existingEvent)){
							ConcurrentHashMap<String, Integer> secondElement1 = new  ConcurrentHashMap<String, Integer>();
							if(pendingForThisTrace.containsKey(existingEvent)){
								secondElement1 = pendingForThisTrace.get(existingEvent);
							}
							secondElement1.put(event, 0);
							pendingForThisTrace.put(existingEvent,secondElement1);
							ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
							if(pendingForThisTrace.containsKey(event)){
								secondElement = pendingForThisTrace.get(event);
							}
							secondElement.put(existingEvent, 0);
							pendingForThisTrace.put(event,secondElement);

						}else{
							ConcurrentHashMap<String, Integer> secondElement1 = new  ConcurrentHashMap<String, Integer>();
							if(pendingForThisTrace.containsKey(event)){
								secondElement1 = pendingForThisTrace.get(event);
							}
							Integer pendingNo = 0;
							if(secondElement1.containsKey(existingEvent)){
								pendingNo = secondElement1.get(existingEvent);
							}
							pendingNo ++;

							secondElement1.put(existingEvent, pendingNo);
							pendingForThisTrace.put(event,secondElement1);

							ConcurrentHashMap<String, Integer> secondElement = new  ConcurrentHashMap<String, Integer>();
							if(pendingForThisTrace.containsKey(existingEvent)){
								secondElement = pendingForThisTrace.get(existingEvent);
							}
							pendingNo = 0;
							if(secondElement.containsKey(event)){
								pendingNo = secondElement.get(event);
							}
							pendingNo ++;
							secondElement.put(event, pendingNo);
							pendingForThisTrace.put(existingEvent,secondElement);
						}
						pendingConstraintsPerTraceCo.addObservation(caseId, class1);
						pendingConstraintsPerTraceCo.putItem(caseId, pendingForThisTrace);
//						pendingConstraintsPerTraceCo.put(trace, pendingForThisTrace);
					}
				}
			}
		}

//		System.out.println("CASE_ID = "+caseId);
//		System.out.println(counter);
//		System.out.println("+++++++++");
		int numberOfEvents = 1;
		if(!counter.containsKey(event)){
			counter.put(event, numberOfEvents);
		}else{
			numberOfEvents = counter.get(event);
			numberOfEvents++;
			counter.put(event, numberOfEvents); 
		}
		activityLabelsCounterCoExistence.addObservation(caseId, class2);
		activityLabelsCounterCoExistence.putItem(caseId, counter);
//		activityLabelsCounterCoExistence.put(trace, counter);
		//***********************
	}

	@Override
	public void updateModel(DeclareModel d) 
	{
		for(String param1 : activityLabelsCoExistence.keySet()) {
			for(String param2 : activityLabelsCoExistence.keySet()) {
				if(!param1.equals(param2)){
					
					// let's generate the co-exsitence
					double fulfill = 0.0;
					double act = 0.0;
					boolean found = false;
					for(String caseId : activityLabelsCounterCoExistence.keySet()) {
						ConcurrentHashMap<String, Integer> counter = activityLabelsCounterCoExistence.getItem(caseId);
						ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> pendingForThisTrace = pendingConstraintsPerTraceCo.getItem(caseId);
						if (pendingForThisTrace == null) {
							pendingForThisTrace = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
						}
						
						double tot = 0;
						if(counter != null && counter.containsKey(param1)) {
							double totnumber = counter.get(param1);
							tot = tot + totnumber;
							act = act + totnumber;
						}
						if(counter != null && counter.containsKey(param2)) {
							double totnumber = counter.get(param2);
//							System.out.println(counter);
							tot = tot + totnumber;
							act = act + totnumber;
						}
						
						if(counter != null && counter.containsKey(param1) &&pendingForThisTrace != null && pendingForThisTrace.containsKey(param1)){
							if(pendingForThisTrace.get(param1).containsKey(param2)){
								found = true;
								double stillpending = pendingForThisTrace.get(param1).get(param2);
								fulfill = fulfill + (tot - stillpending);
//								if(fulfill<0){
//									System.out.println("Trovato!!");
//									System.out.println(counter);
//								}
							}
						}else if(counter != null && counter.containsKey(param2) && pendingForThisTrace != null && pendingForThisTrace.containsKey(param2)){
							if(pendingForThisTrace.get(param2).containsKey(param1)){
								double stillpending = pendingForThisTrace.get(param2).get(param1);
								fulfill = fulfill + (tot - stillpending);
//								if(fulfill<0){
//									System.out.println("Trovato!!");
//									System.out.println(counter);
//								}
							}
						}
					}
					if(found){
						d.addCoExistence(param1, param2, act, fulfill);
//						d.addNotCoExistence(param1, param2, act, act-fulfill);
					}
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsCoExistence.size() +
				activityLabelsCounterCoExistence.size() +
				pendingConstraintsPerTraceCo.size();
	}

}
