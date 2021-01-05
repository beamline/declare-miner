package beamline.declare.miners.events.lossycounting;

import beamline.declare.model.DeclareModel;

public interface LCTemplateReplayer {

	/**
	 * Add a new event observation
	 * 
	 * @param caseId
	 * @param currentBucket
	 */
	public void addObservation(String caseId, Integer currentBucket);
	
	/**
	 * Process the given event belonging to the given case id
	 * @param event
	 * @param caseId
	 */
	public void process(String event, String caseId);
	
	/**
	 * Clean up the data structure
	 * 
	 * @param currentBucket
	 */
	public void cleanup(Integer currentBucket);
	
	/**
	 * Update the given model with the new constraints
	 * 
	 * @param d
	 */
	public void updateModel(DeclareModel d);

	/**
	 * 
	 * @return
	 */
	public Integer getSize();
}