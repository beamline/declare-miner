package beamline.declare.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

public class SimplifiedDeclareModel {

	public enum RELATION {
//		ABSENCE, ABSENCE2, ABSENCE3, EXACTLY1, EXACTLY2, EXISTENCE, EXISTENCE2, EXISTENCE3, INIT,
		ALTERNATE_PRECEDENCE, ALTERNATE_RESPONSE, ALTERNATE_SUCCESSION, CHAIN_PRECEDENCE, CHAIN_RESPONSE,
		CHAIN_SUCCESSION, CHOICE, COEXISTENCE, EXCLUSIVE_CHOICE, PRECEDENCE, RESPONDED_EXISTENCE, RESPONSE, SUCCESSION
	}
	
	private Set<Triple<String, String, RELATION>> relations = new HashSet<Triple<String, String, RELATION>>();

	public void addRelation(String source, String target, RELATION relation) {
		relations.add(Triple.of(source, target, relation));
	}

	public Set<String> getActivities() {
		Set<String> activities = new HashSet<String>();
		for (Triple<String, String, RELATION> r : relations) {
			activities.add(r.getLeft());
			activities.add(r.getMiddle());
		}
		return activities;
	}

	public Set<Triple<String, String, RELATION>> getRelations() {
		return relations;
	}

	public void addConstraintsFromModel(DeclareModel model) {
		HashMap<SimplifiedDeclareModel.RELATION, HashMap<Pair<String, String>, HashMap<String, Double>>> constraints = model.getConstraints();
		for (RELATION relation : constraints.keySet()) {
			for(Pair<String, String> activities : constraints.get(relation).keySet()) {
				addRelation(activities.getLeft(), activities.getRight(), relation);
			}
		}
	}
}
