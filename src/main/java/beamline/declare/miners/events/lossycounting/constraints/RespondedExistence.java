package beamline.declare.miners.events.lossycounting.constraints;

import java.util.HashMap;
import java.util.HashSet;

import beamline.declare.data.LossyCounting;
import beamline.declare.miners.events.lossycounting.LCTemplateReplayer;
import beamline.declare.model.DeclareModel;

public class RespondedExistence implements LCTemplateReplayer {

	private HashSet<String> activityLabelsRespondedExistence = new HashSet<String>();
	private LossyCounting<HashMap<String, Integer>> activityLabelsCounterRespondedExistence = new LossyCounting<HashMap<String, Integer>>();
	private LossyCounting<HashMap<String, HashMap<String, Integer>>> pendingConstraintsPerTraceRe = new LossyCounting<HashMap<String, HashMap<String, Integer>>>();

	@Override
	public void addObservation(String caseId, Integer currentBucket) {
		HashMap<String, HashMap<String, Integer>> ex1 = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, Integer> ex2 = new HashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		try {
			pendingConstraintsPerTraceRe.addObservation(caseId, currentBucket, class1);
			activityLabelsCounterRespondedExistence.addObservation(caseId, currentBucket, class2);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup(Integer currentBucket) {
		pendingConstraintsPerTraceRe.cleanup(currentBucket);
		activityLabelsCounterRespondedExistence.cleanup(currentBucket);
	}

	@Override
	public void process(String event, String caseId) {
		activityLabelsRespondedExistence.add(event);
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		if(!activityLabelsCounterRespondedExistence.containsKey(caseId)) {
			activityLabelsCounterRespondedExistence.putItem(caseId, counter);
		} else {
			counter = activityLabelsCounterRespondedExistence.getItem(caseId);
		}
		HashMap<String,HashMap<String,Integer>> pendingForThisTrace = new HashMap<String,HashMap<String,Integer>>();
		if(!pendingConstraintsPerTraceRe.containsKey(caseId)){
			pendingConstraintsPerTraceRe.putItem(caseId, pendingForThisTrace);
		}else{
			pendingForThisTrace = pendingConstraintsPerTraceRe.getItem(caseId);
		}
		if (!counter.containsKey(event)) {
			if (activityLabelsRespondedExistence.size()>1) {
				for (String existingEvent : activityLabelsRespondedExistence) {
					if (!existingEvent.equals(event)){
						HashMap<String, Integer> secondElement = new HashMap<String, Integer>();
						if (pendingForThisTrace.containsKey(existingEvent)) {
							secondElement = pendingForThisTrace.get(existingEvent);
						}
						secondElement.put(event, 0);
						pendingForThisTrace.put(existingEvent,secondElement);
					}

				}
				for (String existingEvent : activityLabelsRespondedExistence) {
					if (!existingEvent.equals(event)) {

						HashMap<String, Integer> secondElement = new  HashMap<String, Integer>();
						if(pendingForThisTrace.containsKey(event)){
							secondElement = pendingForThisTrace.get(event);
						}
						if(!counter.containsKey(existingEvent)){
							secondElement.put(existingEvent, 1);
						}else{
							secondElement.put(existingEvent, 0);
						}
						pendingForThisTrace.put(event,secondElement);

					}
				}
				pendingConstraintsPerTraceRe.putItem(caseId, pendingForThisTrace);
//				pendingConstraintsPerTraceRe.put(trace, pendingForThisTrace);
			}
		} else {
			for (String firstElement : pendingForThisTrace.keySet()) {
				if (!firstElement.equals(event)) {
					HashMap<String, Integer> secondElement = pendingForThisTrace.get(firstElement);
					secondElement.put(event, 0);
					pendingForThisTrace.put(firstElement, secondElement);
					pendingConstraintsPerTraceRe.putItem(caseId, pendingForThisTrace);
//					pendingConstraintsPerTraceRe.put(trace, pendingForThisTrace);
				}
			}

			HashMap<String, Integer> secondElement = pendingForThisTrace.get(event);
			if (secondElement != null) {
				for (String second : secondElement.keySet()) {
					if (!second.equals(event)) {
						if (!counter.containsKey(second)) {
							Integer pendingNo = secondElement.get(second);
							pendingNo ++;
							secondElement.put(second, pendingNo);
						} else {
							secondElement.put(second, 0);
						}
					}
				}
			
				pendingForThisTrace.put(event,secondElement);
				pendingConstraintsPerTraceRe.putItem(caseId, pendingForThisTrace);
//				pendingConstraintsPerTraceRe.put(trace, pendingForThisTrace);
			}
		}

		//update the counter for the current trace and the current event
		//**********************

		int numberOfEvents = 1;
		if (!counter.containsKey(event)) {
			counter.put(event, numberOfEvents);
		} else {
			numberOfEvents = counter.get(event);
			numberOfEvents++;
			counter.put(event, numberOfEvents); 
		}
		activityLabelsCounterRespondedExistence.putItem(caseId, counter);
		//***********************
	}

	@Override
	public void updateModel(DeclareModel d) {
		for(String param1 : activityLabelsRespondedExistence) {
			for(String param2 : activityLabelsRespondedExistence) {
				if(!param1.equals(param2)){

					// let's generate responded existence
					double fulfill = 0.0;
					double act = 0.0;
					for(String caseId : activityLabelsCounterRespondedExistence.keySet()) {
						HashMap<String, Integer> counter = activityLabelsCounterRespondedExistence.getItem(caseId);
						HashMap<String, HashMap<String, Integer>> pendingForThisTrace = pendingConstraintsPerTraceRe.getItem(caseId);
						if (pendingForThisTrace == null) {
							pendingForThisTrace = new HashMap<String, HashMap<String, Integer>>();
						}

						if (counter.containsKey(param1)) {
							double totnumber = counter.get(param1);
							act = act + totnumber;
							if (pendingForThisTrace.containsKey(param1)) {
								if (pendingForThisTrace.get(param1).containsKey(param2)) {	
									double stillpending = pendingForThisTrace.get(param1).get(param2);
									fulfill = fulfill + (totnumber - stillpending);
								}
							}
						}
					}
					d.addRespondedExistence(param1, param2, act, fulfill);
				//	d.addNotCoExistence(param1, param2, act, act - fulfill);
				}
			}
		}
	}

	@Override
	public Integer getSize() {
		return activityLabelsRespondedExistence.size() +
				activityLabelsCounterRespondedExistence.getSize() +
				pendingConstraintsPerTraceRe.getSize();
	}

}
