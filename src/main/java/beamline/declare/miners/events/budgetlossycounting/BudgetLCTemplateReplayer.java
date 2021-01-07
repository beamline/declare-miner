package beamline.declare.miners.events.budgetlossycounting;

import beamline.declare.model.DeclareModel;

public interface BudgetLCTemplateReplayer {

	/**
	 * Process the given event belonging to the given case id
	 * @param event
	 * @param caseId
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void process(String event, String caseId) throws InstantiationException, IllegalAccessException;
	
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
