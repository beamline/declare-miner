package beamline.declare.miners.events.lossycounting.constraints;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;

import beamline.declare.data.LossyCounting;
import beamline.declare.miners.events.lossycounting.LCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class CoExistence implements LCTemplateReplayer {

	private Set<String> activityLabelsCoExistence = Sets.<String>newConcurrentHashSet();
	private LossyCounting<ConcurrentHashMap<String, Integer>> activityLabelsCounterCoExistence = new LossyCounting<ConcurrentHashMap<String, Integer>>();
	private LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> pendingConstraintsPerTraceCo = new LossyCounting<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>();

	@Override
	public void addObservation(String caseId, Integer currentBucket) {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ex1 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
		ConcurrentHashMap<String, Integer> ex2 = new ConcurrentHashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		try {
			pendingConstraintsPerTraceCo.addObservation(caseId, currentBucket, class1);
			activityLabelsCounterCoExistence.addObservation(caseId, currentBucket, class2);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup(Integer currentBucket) {
		pendingConstraintsPerTraceCo.cleanup(currentBucket);
		activityLabelsCounterCoExistence.cleanup(currentBucket);
	}

	@Override
	public void process(String event, String caseId) {
		activityLabelsCoExistence.add(event);
		ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();
		if(!activityLabelsCounterCoExistence.containsKey(caseId)){
			activityLabelsCounterCoExistence.putItem(caseId, counter);
		}else{
			counter = activityLabelsCounterCoExistence.getItem(caseId);
		}
		ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> pendingForThisTrace = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
		if(!pendingConstraintsPerTraceCo.containsKey(caseId)){
			pendingConstraintsPerTraceCo.putItem(caseId, pendingForThisTrace);
		}else{
			pendingForThisTrace = pendingConstraintsPerTraceCo.getItem(caseId);
		}
		if(!counter.containsKey(event)){
			if(activityLabelsCoExistence.size()>1){
				for(String existingEvent : activityLabelsCoExistence){
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
						pendingConstraintsPerTraceCo.putItem(caseId, pendingForThisTrace);
//						pendingConstraintsPerTraceCo.put(trace, pendingForThisTrace);
					}
				}
			}
		}else{
			if(activityLabelsCoExistence.size()>1){
				for(String existingEvent : activityLabelsCoExistence){
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
						pendingConstraintsPerTraceCo.putItem(caseId, pendingForThisTrace);
//						pendingConstraintsPerTraceCo.put(trace, pendingForThisTrace);
					}
				}
			}
		}

		int numberOfEvents = 1;
		if(!counter.containsKey(event)){
			counter.put(event, numberOfEvents);
		}else{
			numberOfEvents = counter.get(event);
			numberOfEvents++;
			counter.put(event, numberOfEvents); 
		}
		activityLabelsCounterCoExistence.putItem(caseId, counter);
//		activityLabelsCounterCoExistence.put(trace, counter);
		//***********************
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsCoExistence) {
			for(String param2 : activityLabelsCoExistence) {
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
						if(counter.containsKey(param1)) {
							double totnumber = counter.get(param1);
							tot = tot + totnumber;
							act = act + totnumber;
						}
						if(counter.containsKey(param2)) {
							double totnumber = counter.get(param2);
							tot = tot + totnumber;
							act = act + totnumber;
						}
						
						if(pendingForThisTrace.containsKey(param1)){
							if(pendingForThisTrace.get(param1).containsKey(param2)){
								found = true;
								double stillpending = pendingForThisTrace.get(param1).get(param2);
								fulfill = fulfill + (tot - stillpending);
							}
						}else if(pendingForThisTrace.containsKey(param2)){
							if(pendingForThisTrace.get(param2).containsKey(param1)){
								double stillpending = pendingForThisTrace.get(param2).get(param1);
								fulfill = fulfill + (tot - stillpending);
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
				activityLabelsCounterCoExistence.getSize() +
				pendingConstraintsPerTraceCo.getSize();
	}

}
