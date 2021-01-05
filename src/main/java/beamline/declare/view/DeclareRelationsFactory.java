package beamline.declare.view;

import beamline.declare.model.SimplifiedDeclareModel.RELATION;
import beamline.declare.view.relations.DeclareRelationAlternatePrecedence;
import beamline.declare.view.relations.DeclareRelationAlternateResponse;
import beamline.declare.view.relations.DeclareRelationAlternateSuccession;
import beamline.declare.view.relations.DeclareRelationChainPrecedence;
import beamline.declare.view.relations.DeclareRelationChainResponse;
import beamline.declare.view.relations.DeclareRelationChainSuccession;
import beamline.declare.view.relations.DeclareRelationChoice;
import beamline.declare.view.relations.DeclareRelationCoexistence;
import beamline.declare.view.relations.DeclareRelationExclusiveChoice;
import beamline.declare.view.relations.DeclareRelationPrecedence;
import beamline.declare.view.relations.DeclareRelationRespondedExistence;
import beamline.declare.view.relations.DeclareRelationResponse;
import beamline.declare.view.relations.DeclareRelationSuccession;
import beamline.graphviz.DotEdge;
import beamline.graphviz.DotNode;

public class DeclareRelationsFactory {

	public static DotEdge getByType(DotNode source, DotNode target, RELATION relation) {
		if (relation == RELATION.EXCLUSIVE_CHOICE) {
			return new DeclareRelationExclusiveChoice(source, target);
		} else if (relation == RELATION.ALTERNATE_PRECEDENCE) {
			return new DeclareRelationAlternatePrecedence(source, target);
		} else if (relation == RELATION.ALTERNATE_RESPONSE) {
			return new DeclareRelationAlternateResponse(source, target);
		} else if (relation == RELATION.ALTERNATE_SUCCESSION) {
			return new DeclareRelationAlternateSuccession(source, target);
		} else if (relation == RELATION.CHAIN_PRECEDENCE) {
			return new DeclareRelationChainPrecedence(source, target);
		} else if (relation == RELATION.CHAIN_RESPONSE) {
			return new DeclareRelationChainResponse(source, target);
		} else if (relation == RELATION.CHAIN_SUCCESSION) {
			return new DeclareRelationChainSuccession(source, target);
		} else if (relation == RELATION.CHOICE) {
			return new DeclareRelationChoice(source, target);
		} else if (relation == RELATION.COEXISTENCE) {
			return new DeclareRelationCoexistence(source, target);
		} else if (relation == RELATION.PRECEDENCE) {
			return new DeclareRelationPrecedence(source, target);
		} else if (relation == RELATION.RESPONDED_EXISTENCE) {
			return new DeclareRelationRespondedExistence(source, target);
		} else if (relation == RELATION.RESPONSE) {
			return new DeclareRelationResponse(source, target);
		} else if (relation == RELATION.SUCCESSION) {
			return new DeclareRelationSuccession(source, target);
		}
//			else if (relation == RELATION.ABSENCE) {
//			return new DeclareRelationAbsence(source, target);
//		} else if (relation == RELATION.ABSENCE2) {
//			return new DeclareRelationAbsence2(source, target);
//		} else if (relation == RELATION.ABSENCE3) {
//		} else if (relation == RELATION.EXACTLY1) {
//			return new DeclareRelationExactly1(source, target);
//		} else if (relation == RELATION.EXACTLY2) {
//			return new DeclareRelationExactly2(source, target);
//		} else if (relation == RELATION.EXISTENCE) {
//			return new DeclareRelationExistence(source, target);
//		} else if (relation == RELATION.EXISTENCE2) {
//			return new DeclareRelationExistence2(source, target);
//		} else if (relation == RELATION.EXISTENCE3) {
//			return new DeclareRelationExistence3(source, target);
//		} else if (relation == RELATION.INIT) {
//			return new DeclareRelationInit(source, target);
//		}
		
		return null;
	}
}
