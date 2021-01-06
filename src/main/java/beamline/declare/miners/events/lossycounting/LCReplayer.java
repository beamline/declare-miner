package beamline.declare.miners.events.lossycounting;

import java.util.ArrayList;
import java.util.List;

import beamline.declare.miners.events.lossycounting.constraints.AlternatePrecedence;
import beamline.declare.miners.events.lossycounting.constraints.AlternateResponse;
import beamline.declare.miners.events.lossycounting.constraints.AlternateSuccession;
import beamline.declare.miners.events.lossycounting.constraints.ChainPrecedence;
import beamline.declare.miners.events.lossycounting.constraints.ChainResponse;
import beamline.declare.miners.events.lossycounting.constraints.ChainSuccession;
import beamline.declare.miners.events.lossycounting.constraints.CoExistence;
import beamline.declare.miners.events.lossycounting.constraints.Precedence;
import beamline.declare.miners.events.lossycounting.constraints.RespondedExistence;
import beamline.declare.miners.events.lossycounting.constraints.Response;
import beamline.declare.miners.events.lossycounting.constraints.Succession;
import beamline.declare.model.DeclareModel;

public class LCReplayer {

	List<LCTemplateReplayer> replayers = new ArrayList<LCTemplateReplayer>();
	
	public LCReplayer() {
		replayers.add(new AlternatePrecedence());
		replayers.add(new AlternateResponse());
		replayers.add(new ChainPrecedence());
		replayers.add(new ChainResponse());
		replayers.add(new CoExistence());
		replayers.add(new Precedence());
		replayers.add(new RespondedExistence());
		replayers.add(new Response());
		replayers.add(new Succession());

		// new constraints
		replayers.add(new AlternateSuccession());
		replayers.add(new ChainSuccession());
	}

	/**
	 * 
	 * @param caseId
	 * @param currentBucket
	 */
	public void addObservation(String caseId, Integer currentBucket) {
		for(LCTemplateReplayer t : replayers) {
			t.addObservation(caseId, currentBucket);
		}
	}
	
	/**
	 * 
	 * @param event
	 * @param caseId
	 */
	public void process(String event, String caseId) {
		for(LCTemplateReplayer t : replayers) {
			t.process(event, caseId);
		}
	}
	
	/**
	 * 
	 * @param currentBucket
	 */
	public void cleanup(Integer currentBucket) {
		for(LCTemplateReplayer t : replayers) {
			t.cleanup(currentBucket);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public DeclareModel getModel() {
		DeclareModel d = new DeclareModel();
		for(LCTemplateReplayer t : replayers) {
			t.updateModel(d);
		}
		return d;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getSize() {
		Integer i = 0;
		for(LCTemplateReplayer t : replayers) {
			i += t.getSize();
		}
		return i;
	}
}