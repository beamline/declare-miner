package beamline.declare.view;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import beamline.declare.model.DeclareModel;
import beamline.declare.model.DeclareModel.RELATION;
import beamline.graphviz.Dot;
import beamline.graphviz.DotEdge;
import beamline.graphviz.DotNode;

public class DeclareModelView extends Dot {

	private DeclareModel model;
	Map<String, DotNode> activityToNode;
	
	public DeclareModelView(DeclareModel model) {
		this.model = model;
		this.activityToNode = new HashMap<String, DotNode>();
		
		realize();
	}
	
	private void realize() {
		for(Triple<String, String, RELATION> r : model.getRelations()) {
			addRelation(r.getLeft(), r.getMiddle(), r.getRight());
		}
	}
	
	public DotNode getNodeIfNeeded(String activity) {
		if (!activityToNode.containsKey(activity)) {
			DeclareActivity node = new DeclareActivity(activity);
			addNode(node);
			activityToNode.put(activity, node);
		}
		return activityToNode.get(activity);
	}
	
	public void addRelation(String source, String target, RELATION relation) {
		DotNode sourceNode = getNodeIfNeeded(source);
		DotNode targetNode = getNodeIfNeeded(target);
		DotEdge edge = DeclareRelationsFactory.getByType(sourceNode, targetNode, relation);
		if (edge != null) {
			addEdge(edge);
		}
	}
}
