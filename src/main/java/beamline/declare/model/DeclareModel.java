package beamline.declare.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

public class DeclareModel {

	private boolean hasTraces = false;
	private Double maxFulfillment = Double.MIN_VALUE;
	private Double maxSatisfiedTraces = Double.MIN_VALUE;
	
	private HashSet<String> activities = new HashSet<String>();
	private HashMap<SimplifiedDeclareModel.RELATION, HashMap<Pair<String, String>, HashMap<String, Double>>> constraints = new HashMap<>();

	public DeclareModel() {
		constraints.put(SimplifiedDeclareModel.RELATION.ALTERNATE_PRECEDENCE,	new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.ALTERNATE_RESPONSE,		new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.ALTERNATE_SUCCESSION,	new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.CHAIN_PRECEDENCE,		new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.CHAIN_RESPONSE,			new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.CHAIN_SUCCESSION,		new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.COEXISTENCE,			new HashMap<Pair<String, String>, HashMap<String, Double>>());
//		constraints.put(DeclareModelForView.RELATION.Not_CoExistence,		new HashMap<Pair<String, String>, HashMap<String, Double>>());
//		constraints.put(DeclareModelForView.RELATION.Not_Succession,			new HashMap<Pair<String, String>, HashMap<String, Double>>());
		//constraints.put("notChainSuccessions",				new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.PRECEDENCE,				new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.RESPONDED_EXISTENCE,	new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.RESPONSE,				new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.SUCCESSION,				new HashMap<Pair<String, String>, HashMap<String, Double>>());
//		constraints.put(DeclareModelForView.RELATION.Existence,				new HashMap<Pair<String, String>, HashMap<String, Double>>());
//		constraints.put(DeclareModelForView.RELATION.Existence2,				new HashMap<Pair<String, String>, HashMap<String, Double>>());
//		constraints.put(DeclareModelForView.RELATION.Existence3,				new HashMap<Pair<String, String>, HashMap<String, Double>>());
//		constraints.put(DeclareModelForView.RELATION.Exactly1,				new HashMap<Pair<String, String>, HashMap<String, Double>>());
//		constraints.put(DeclareModelForView.RELATION.Exactly2,				new HashMap<Pair<String, String>, HashMap<String, Double>>());
//		constraints.put(DeclareModelForView.RELATION.Absence,				new HashMap<Pair<String, String>, HashMap<String, Double>>());
//		constraints.put(DeclareModelForView.RELATION.Absence2,				new HashMap<Pair<String, String>, HashMap<String, Double>>());
//		constraints.put(DeclareModelForView.RELATION.Absence3,				new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.CHOICE,					new HashMap<Pair<String, String>, HashMap<String, Double>>());
//		constraints.put(DeclareModelForView.RELATION.Init,					new HashMap<Pair<String, String>, HashMap<String, Double>>());
		constraints.put(SimplifiedDeclareModel.RELATION.EXCLUSIVE_CHOICE,		new HashMap<Pair<String, String>, HashMap<String, Double>>());
	}

	public void addResponse(String activityA, String activityB, Double activations, Double fulfillments) {
		add(SimplifiedDeclareModel.RELATION.RESPONSE, activityA, activityB, activations, fulfillments);
	}

	public void addResponse(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		add(SimplifiedDeclareModel.RELATION.RESPONSE, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
	}
	
//	public void addExistence(String activityA, Double activations, Double fulfillments) {
//		add(DeclareModelForView.RELATION.Existence, activityA, activityA, activations, fulfillments);
//	}
//
//	public void addExistence(String activityA, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add(DeclareModelForView.RELATION.Existence, activityA, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}
	
	public void addChoices(String activityA, String activityB, Double activations, Double fulfillments) {
		if (activityA.compareTo(activityB) <= 0) {
			add(SimplifiedDeclareModel.RELATION.CHOICE, activityA, activityB, activations, fulfillments);
		} else {
			add(SimplifiedDeclareModel.RELATION.CHOICE, activityB, activityA, activations, fulfillments);
		}
	}

	public void addChoices(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		if (activityA.compareTo(activityB) <= 0) {
			add(SimplifiedDeclareModel.RELATION.CHOICE, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
		} else {
			add(SimplifiedDeclareModel.RELATION.CHOICE, activityB, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
		}
	}
	
	public void addExclusiveChoices(String activityA, String activityB, Double activations, Double fulfillments) {
		if (activityA.compareTo(activityB) <= 0) {
			add(SimplifiedDeclareModel.RELATION.EXCLUSIVE_CHOICE, activityA, activityB, activations, fulfillments);
		} else {
			add(SimplifiedDeclareModel.RELATION.EXCLUSIVE_CHOICE, activityB, activityA, activations, fulfillments);
		}
	}

	public void addExclusiveChoices(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		if (activityA.compareTo(activityB) <= 0) {
			add(SimplifiedDeclareModel.RELATION.EXCLUSIVE_CHOICE, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
		} else {
			add(SimplifiedDeclareModel.RELATION.EXCLUSIVE_CHOICE, activityB, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
		}
	}
	
//	public void addAbsence(String activityA, Double activations, Double fulfillments) {
//		add(DeclareModelForView.RELATION.Absence, activityA, activityA, activations, fulfillments);
//	}
//
//	public void addAbsence(String activityA, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add(DeclareModelForView.RELATION.Absence, activityA, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}
//	
//	public void addInit(String activityA, Double activations, Double fulfillments) {
//		add(DeclareModelForView.RELATION.Init, activityA, activityA, activations, fulfillments);
//	}
//
//	public void addInit(String activityA, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add(DeclareModelForView.RELATION.Init, activityA, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}
//	
//	public void addAbsence2(String activityA, Double activations, Double fulfillments) {
//		add(DeclareModelForView.RELATION.Absence2, activityA, activityA, activations, fulfillments);
//	}
//
//	public void addAbsence2(String activityA, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add(DeclareModelForView.RELATION.Absence2, activityA, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}
//	
//	public void addAbsence3(String activityA, Double activations, Double fulfillments) {
//		add(DeclareModelForView.RELATION.Absence3, activityA, activityA, activations, fulfillments);
//	}
//
//	public void addAbsence3(String activityA, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add(DeclareModelForView.RELATION.Absence3, activityA, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}
//	
//	public void addExistence2(String activityA, Double activations, Double fulfillments) {
//		add(DeclareModelForView.RELATION.Existence2, activityA, activityA, activations, fulfillments);
//	}
//
//	public void addExistence2(String activityA, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add(DeclareModelForView.RELATION.Existence2, activityA, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}
//	
//	public void addExistence3(String activityA, Double activations, Double fulfillments) {
//		add(DeclareModelForView.RELATION.Existence3, activityA, activityA, activations, fulfillments);
//	}
//
//	public void addExistence3(String activityA, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add(DeclareModelForView.RELATION.Existence3, activityA, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}
//
//	public void addExactly1(String activityA, Double activations, Double fulfillments) {
//		add(DeclareModelForView.RELATION.Exactly1, activityA, activityA, activations, fulfillments);
//	}
//
//	public void addExactly1(String activityA, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add(DeclareModelForView.RELATION.Exactly1, activityA, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}
//	
//	public void addExactly2(String activityA, Double activations, Double fulfillments) {
//		add(DeclareModelForView.RELATION.Exactly2, activityA, activityA, activations, fulfillments);
//	}
//
//	public void addExactly2(String activityA, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add(DeclareModelForView.RELATION.Exactly2, activityA, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}

//	public void addNotResponse(String activityA, String activityB, Double activations, Double fulfillments) {
//		add(DeclareModelForView.RELATION.Not_Succession, activityA, activityB, activations, fulfillments);
//	}
//
//	public void addNotResponse(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add(DeclareModelForView.RELATION.Not_Succession, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}

	public void addSuccession(String activityA, String activityB, Double activations, Double fulfillments) {
		add(SimplifiedDeclareModel.RELATION.SUCCESSION, activityA, activityB, activations, fulfillments);
	}

	public void addSuccession(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		add(SimplifiedDeclareModel.RELATION.SUCCESSION, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
	}

//	public void addNotSuccession(String activityA, String activityB, Double activations, Double fulfillments) {
//		add(DeclareModelForView.RELATION.Not_Succession, activityA, activityB, activations, fulfillments);
//	}
//
//	public void addNotSuccession(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add(DeclareModelForView.RELATION.Not_Succession, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}

	public void addPrecedence(String activityA, String activityB, Double activations, Double fulfillments) {
		add(SimplifiedDeclareModel.RELATION.PRECEDENCE, activityA, activityB, activations, fulfillments);
	}

	public void addPrecedence(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		add(SimplifiedDeclareModel.RELATION.PRECEDENCE, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
	}

//	public void addNotPrecedence(String activityA, String activityB, Double activations, Double fulfillments) {
//		add("notPrecedences", activityA, activityB, activations, fulfillments);
//	}
//
//	public void addNotPrecedence(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		add("notPrecedences", activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//	}

	public void addRespondedExistence(String activityA, String activityB, Double activations, Double fulfillments) {
		add(SimplifiedDeclareModel.RELATION.RESPONDED_EXISTENCE, activityA, activityB, activations, fulfillments);
	}

	public void addRespondedExistence(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		add(SimplifiedDeclareModel.RELATION.RESPONDED_EXISTENCE, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
	}

//	public void addNotCoExistence(String activityA, String activityB, Double activations, Double fulfillments) {
//		if (activityA.compareTo(activityB) <= 0) {
//			add(DeclareModelForView.RELATION.Not_CoExistence, activityA, activityB, activations, fulfillments);
//		} else {
//			add(DeclareModelForView.RELATION.Not_CoExistence, activityB, activityA, activations, fulfillments);
//		}
//	}
//
//	public void addNotCoExistence(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
//		if (activityA.compareTo(activityB) <= 0) {
//			add(DeclareModelForView.RELATION.Not_CoExistence, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//		} else {
//			add(DeclareModelForView.RELATION.Not_CoExistence, activityB, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
//		}
//	}

	public void addCoExistence(String activityA, String activityB, Double activations, Double fulfillments) {
		if (activityA.compareTo(activityB) <= 0) {
			add(SimplifiedDeclareModel.RELATION.COEXISTENCE, activityA, activityB, activations, fulfillments);
		} else {
			add(SimplifiedDeclareModel.RELATION.COEXISTENCE, activityB, activityA, activations, fulfillments);
		}
	}

	public void addCoExistence(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		if (activityA.compareTo(activityB) <= 0) {
			add(SimplifiedDeclareModel.RELATION.COEXISTENCE, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
		} else {
			add(SimplifiedDeclareModel.RELATION.COEXISTENCE, activityB, activityA, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
		}
	}

	public void addChainResponse(String activityA, String activityB, Double activations, Double fulfillments) {
		add(SimplifiedDeclareModel.RELATION.CHAIN_RESPONSE, activityA, activityB, activations, fulfillments);
	}

	public void addChainResponse(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		add(SimplifiedDeclareModel.RELATION.CHAIN_RESPONSE, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
	}
	
	public void addChainSuccession(String activityA, String activityB, Double activations, Double fulfillments) {
		add(SimplifiedDeclareModel.RELATION.CHAIN_SUCCESSION, activityA, activityB, activations, fulfillments);
	}

	public void addChainSuccession(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		add(SimplifiedDeclareModel.RELATION.CHAIN_SUCCESSION, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
	}

	public void addChainPrecedence(String activityA, String activityB, Double activations, Double fulfillments) {
		add(SimplifiedDeclareModel.RELATION.CHAIN_PRECEDENCE, activityA, activityB, activations, fulfillments);
	}

	public void addChainPrecedence(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		add(SimplifiedDeclareModel.RELATION.CHAIN_PRECEDENCE, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
	}

	public void addAlternateResponse(String activityA, String activityB, Double activations, Double fulfillments) {
		add(SimplifiedDeclareModel.RELATION.ALTERNATE_RESPONSE, activityA, activityB, activations, fulfillments);
	}

	public void addAlternateResponse(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		add(SimplifiedDeclareModel.RELATION.ALTERNATE_RESPONSE, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
	}

	public void addAlternateSuccession(String activityA, String activityB, Double activations, Double fulfillments) {
		add(SimplifiedDeclareModel.RELATION.ALTERNATE_SUCCESSION, activityA, activityB, activations, fulfillments);
	}

	public void addAlternateSuccession(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		add(SimplifiedDeclareModel.RELATION.ALTERNATE_SUCCESSION, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
	}
	
	
	public void addAlternatePrecedence(String activityA, String activityB, Double activations, Double fulfillments) {
		add(SimplifiedDeclareModel.RELATION.ALTERNATE_PRECEDENCE, activityA, activityB, activations, fulfillments);
	}

	public void addAlternatePrecedence(String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		add(SimplifiedDeclareModel.RELATION.ALTERNATE_PRECEDENCE, activityA, activityB, completedTraces, satisfiedTraces, vacuouslySatisfiedTraces, violatedTraces);
	}

	public HashSet<String> getActivities() {
		return activities;
	}

	private void add(SimplifiedDeclareModel.RELATION template, String activityA, String activityB, Double activations, Double fulfillments) {
		if (constraints.containsKey(template)) {
			HashMap<Pair<String, String>, HashMap<String, Double>> constraint = constraints.get(template);

			Pair<String, String> k = Pair.of(activityA, activityB);
			HashMap<String, Double> v = new HashMap<String, Double>();
			v.put("activations", activations);
			v.put("fulfillments", fulfillments);
			constraint.put(k, v);

			constraints.put(template, constraint);

			activities.add(activityA);
			activities.add(activityB);
			
			maxFulfillment = Math.max(maxFulfillment, fulfillments);
			hasTraces = false;
		}
	}
	
	private void add(SimplifiedDeclareModel.RELATION template, String activityA, String activityB, Integer completedTraces, Integer satisfiedTraces, Integer vacuouslySatisfiedTraces, Integer violatedTraces) {
		if (constraints.containsKey(template)) {
			HashMap<Pair<String, String>, HashMap<String, Double>> constraint = constraints.get(template);

			Pair<String, String> k = Pair.of(activityA, activityB);
			HashMap<String, Double> v = new HashMap<String, Double>();
			v.put("completedTraces", completedTraces.doubleValue());
			v.put("satisfiedTraces", satisfiedTraces.doubleValue());
			v.put("vacuouslySatisfiedTraces", vacuouslySatisfiedTraces.doubleValue());
			v.put("violatedTraces", violatedTraces.doubleValue());
			constraint.put(k, v);

			constraints.put(template, constraint);

			activities.add(activityA);
			activities.add(activityB);
			
			maxSatisfiedTraces = Math.max(maxSatisfiedTraces, satisfiedTraces.doubleValue());
			hasTraces = true;
		}
	}

	private Double getValue(SimplifiedDeclareModel.RELATION template, String activityA, String activityB, String field) {
		if (constraints.containsKey(template)) {
			if (constraints.get(template).containsKey(Pair.of(activityA, activityB))) {
				if (constraints.get(template).get(Pair.of(activityA, activityB)).containsKey(field)) {
					return constraints.get(template).get(Pair.of(activityA, activityB)).get(field);
				}
			}
		}
		return 0.0;
	}

	private Double getActivations(SimplifiedDeclareModel.RELATION constraintName, String activityA, String activityB) {
		return getValue(constraintName, activityA, activityB, "activations");
	}

	private Double getFulfillment(SimplifiedDeclareModel.RELATION constraintName, String activityA, String activityB) {
		return getValue(constraintName, activityA, activityB, "fulfillments");
	}

	private Integer getCompletedTraces(SimplifiedDeclareModel.RELATION constraintName, String activityA, String activityB) {
		return getValue(constraintName, activityA, activityB, "completedTraces").intValue();
	}

	private Integer getSatisfiedTraces(SimplifiedDeclareModel.RELATION constraintName, String activityA, String activityB) {
		return getValue(constraintName, activityA, activityB, "satisfiedTraces").intValue();
	}

	private Integer getVacuouslySatisfiedTraces(SimplifiedDeclareModel.RELATION constraintName, String activityA, String activityB) {
		return getValue(constraintName, activityA, activityB, "vacuouslySatisfiedTraces").intValue();
	}

	private Integer getViolatedTraces(SimplifiedDeclareModel.RELATION constraintName, String activityA, String activityB) {
		return getValue(constraintName, activityA, activityB, "violatedTraces").intValue();
	}

	public int size() {
		int i = 0;
		for(SimplifiedDeclareModel.RELATION constraintName : constraints.keySet()) {
			i += constraints.get(constraintName).size();
		}
		return i;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(SimplifiedDeclareModel.RELATION constraintName : constraints.keySet()) {
			HashMap<Pair<String, String>, HashMap<String, Double>> constraint = constraints.get(constraintName);
			if (constraint.size() > 0) {
				sb.append("=== " + constraintName.toString().toUpperCase() + " === ("+ constraint.size() + " constraints)\n");
				
				for(Pair<String, String> r : constraint.keySet()) {
					//				Pair<Double, Double> vals = constraint.get(r);
					//				double fulfillRatio = (vals.getSecond() / vals.getLeft());
					sb.append(" - " + r /*+ ", " + constraint.get(r)*/ + "\n");// + "\t fulfil ratio: " + fulfillRatio + "\t vio ratio: " + (1-fulfillRatio) + "\n");
				}
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	public String toHTMLString() {
		StringBuilder sb = new StringBuilder();
		for(SimplifiedDeclareModel.RELATION constraintName : constraints.keySet()) {
			HashMap<Pair<String, String>, HashMap<String, Double>> constraint = constraints.get(constraintName);
			if (constraint.size() > 0) {
				String constraintTitle = constraintName.toString().replace("_", " ");
				constraintTitle = constraintTitle.substring(0, 1).toUpperCase() + constraintTitle.substring(1).toLowerCase();
				sb.append("<h6>" + constraintTitle + "</h6>");
				sb.append("<p>"+ constraint.size() + " constraints</p>");
				
				sb.append("<ul>");
				for(Pair<String, String> r : constraint.keySet()) {
					sb.append("<li>");
					sb.append(r + " <span class=\"text-secondary\">" + constraint.get(r) + "</span>");
					sb.append("</li>");
				}
				sb.append("</ul>");
			}
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public static int TP(DeclareModel candidate, DeclareModel goldStandard) {
		int i = 0;
		for (SimplifiedDeclareModel.RELATION template : candidate.constraints.keySet()) {
			for (Pair<String, String> constraint : candidate.constraints.get(template).keySet()) {
				if(template.equals(SimplifiedDeclareModel.RELATION.COEXISTENCE) ||
//						template.equals(DeclareModelForView.RELATION.Not_CoExistence) ||
						template.equals(SimplifiedDeclareModel.RELATION.CHOICE) ||
						template.equals(SimplifiedDeclareModel.RELATION.EXCLUSIVE_CHOICE)) {
					Pair<String, String> inverse = Pair.of(constraint.getRight(), constraint.getLeft());
					if (goldStandard.constraints.get(template).containsKey(constraint)||goldStandard.constraints.get(template).containsKey(inverse)) {
						i++;
					}
				} else if (goldStandard.constraints.get(template).containsKey(constraint)) {
					i++;
				}
			}
		}
		return i;
	}

	public static int FP(DeclareModel candidate, DeclareModel goldStandard) {
		int i = 0;
		for (SimplifiedDeclareModel.RELATION template : candidate.constraints.keySet()) {
			for (Pair<String, String> constraint : candidate.constraints.get(template).keySet()) {
				
				if(template.equals(SimplifiedDeclareModel.RELATION.COEXISTENCE) ||
//						template.equals(DeclareModelForView.RELATION.Not_CoExistence) ||
						template.equals(SimplifiedDeclareModel.RELATION.CHOICE) ||
						template.equals(SimplifiedDeclareModel.RELATION.EXCLUSIVE_CHOICE)){
					Pair<String, String> inverse = Pair.of(constraint.getRight(), constraint.getLeft());
					if(!goldStandard.constraints.get(template).containsKey(constraint) && !goldStandard.constraints.get(template).containsKey(inverse)){
						i++;
					}
				} else if (!goldStandard.constraints.get(template).containsKey(constraint)) {
					i++;
				}
			}
		}
		return i;
	}

	public static int FN(DeclareModel candidate, DeclareModel goldStandard) {
		int i = 0;
		for (SimplifiedDeclareModel.RELATION template : goldStandard.constraints.keySet()) {
			for (Pair<String, String> constraint : goldStandard.constraints.get(template).keySet()) {
				if(template.equals(SimplifiedDeclareModel.RELATION.COEXISTENCE) ||
//						template.equals(DeclareModelForView.RELATION.Not_CoExistence) ||
						template.equals(SimplifiedDeclareModel.RELATION.CHOICE) ||
						template.equals(SimplifiedDeclareModel.RELATION.EXCLUSIVE_CHOICE)){
					Pair<String, String> inverse = Pair.of(constraint.getRight(), constraint.getLeft());
					if(!candidate.constraints.get(template).containsKey(constraint) && !candidate.constraints.get(template).containsKey(inverse)){
						i++;
					}
				} else if (!candidate.constraints.get(template).containsKey(constraint)) {
					i++;
				}

			}
		}
		return i;
	}
	
	public Double getCost(SimplifiedDeclareModel.RELATION template, String ActA, String ActB) {
		if (hasTraces) {
			return getSatisfiedTraces(template, ActA, ActB).doubleValue() / maxSatisfiedTraces;
		} else {
			return getFulfillment(template, ActA, ActB) / maxFulfillment;
		}
	}
	
	public static DeclareModel getTopConstraints(final DeclareModel model, int k) {
		if (model == null || k <= 0) {
			return null;
		}
		// define our priority queue
		PriorityQueue<Triple<SimplifiedDeclareModel.RELATION, String, String>> p = new PriorityQueue<Triple<SimplifiedDeclareModel.RELATION, String, String>>(
			10,
			new Comparator<Triple<SimplifiedDeclareModel.RELATION, String, String>>() {
			@Override
			public int compare(Triple<SimplifiedDeclareModel.RELATION, String, String> A, Triple<SimplifiedDeclareModel.RELATION, String, String> B) {
				Double costA = model.getCost(A.getLeft(), A.getMiddle(), A.getRight());
				Double costB = model.getCost(B.getLeft(), B.getMiddle(), B.getRight());
				return costB.compareTo(costA);
			}
		});
		
		// populate our priority queue
		for (SimplifiedDeclareModel.RELATION constraint : model.constraints.keySet()) {
			for (Pair<String, String> instance : model.constraints.get(constraint).keySet()) {
				String activityA = instance.getLeft();
				String activityB = instance.getRight();
				p.add(Triple.of(constraint, activityA, activityB));
			}
		}
		
		// extract constraints from the priority queue
		DeclareModel filtered = new DeclareModel();
		filtered.hasTraces = model.hasTraces;
		Double lowestCost = 0.0;
		while(k-- > 0) {
			Triple<SimplifiedDeclareModel.RELATION, String, String> element = p.poll();
			if (element != null) {
				SimplifiedDeclareModel.RELATION constraintName = element.getLeft();
				String activityA = element.getMiddle();
				String activityB = element.getRight();
				if (model.hasTraces) {
					filtered.add(
						constraintName, activityA, activityB,
						model.getCompletedTraces(constraintName, activityA, activityB),
						model.getSatisfiedTraces(constraintName, activityA, activityB),
						model.getVacuouslySatisfiedTraces(constraintName, activityA, activityB),
						model.getViolatedTraces(constraintName, activityA, activityB));
				} else {
					filtered.add(constraintName, activityA, activityB,
							model.getActivations(constraintName, activityA, activityB),
							model.getFulfillment(constraintName, activityA, activityB));
				}
				lowestCost = model.getCost(constraintName, activityA, activityB);
			}
		}
		// extract all other constraints with the same cost
		boolean checkAnother = false;
		do {
			Triple<SimplifiedDeclareModel.RELATION, String, String> element = p.poll();
			if (element != null) {
				SimplifiedDeclareModel.RELATION constraintName = element.getLeft();
				String activityA = element.getMiddle();
				String activityB = element.getRight();
				Double currentCost = model.getCost(constraintName, activityA, activityB);
				if (currentCost == lowestCost) {
					if (model.hasTraces) {
						filtered.add(
							constraintName, activityA, activityB,
							model.getCompletedTraces(constraintName, activityA, activityB),
							model.getSatisfiedTraces(constraintName, activityA, activityB),
							model.getVacuouslySatisfiedTraces(constraintName, activityA, activityB),
							model.getViolatedTraces(constraintName, activityA, activityB));
					} else {
						filtered.add(constraintName, activityA, activityB,
							model.getActivations(constraintName, activityA, activityB),
							model.getFulfillment(constraintName, activityA, activityB));
					}
					checkAnother = true;
				} else {
					checkAnother = false;
				}
			}
		} while(checkAnother);
		return filtered;
	}

	public static DeclareModel filterOnFulfillmentRatio(DeclareModel model, double minFulfillmentRatio) {
		if (model == null || minFulfillmentRatio > 1 || minFulfillmentRatio < 0) {
			return null;
		}
		DeclareModel filtered = new DeclareModel();
		for (SimplifiedDeclareModel.RELATION constraint : model.constraints.keySet()) {
			for (Pair<String, String> instance : model.constraints.get(constraint).keySet()) {
				String activityA = instance.getLeft();
				String activityB = instance.getRight();
				Double activations = model.getActivations(constraint, activityA, activityB);
				Double fulfillments = model.getFulfillment(constraint, activityA, activityB);
				if (fulfillments / activations >= minFulfillmentRatio) {
					filtered.add(constraint, activityA, activityB, activations, fulfillments);
				}
			}
		}
		return filtered;
	}

	public static DeclareModel filterOnSpecificConstraint(DeclareModel model, SimplifiedDeclareModel.RELATION templateName, String activityA, String activityB) {
		if (model == null) {
			return null;
		}
		DeclareModel filtered = new DeclareModel();
		if (model.constraints.containsKey(templateName)) {
			if (model.constraints.get(templateName).containsKey(Pair.of(activityA, activityB))) {
				HashMap<Pair<String, String>, HashMap<String, Double>> v = new HashMap<Pair<String, String>, HashMap<String, Double>>();
				v.put(Pair.of(activityA, activityB), model.constraints.get(templateName).get(Pair.of(activityA, activityB)));
				filtered.constraints.put(templateName, v);
			}
		}
		return filtered;
	}

	public static DeclareModel filterOnTraceSupport(DeclareModel model, double minTraceSupport) {
		if (model == null || minTraceSupport > 1 || minTraceSupport < 0) {
			return null;
		}
		DeclareModel filtered = new DeclareModel();
		for (SimplifiedDeclareModel.RELATION constraint : model.constraints.keySet()) {
			for (Pair<String, String> instance : model.constraints.get(constraint).keySet()) {
				String activityA = instance.getLeft();
				String activityB = instance.getRight();

				double completedTraces = model.getCompletedTraces(constraint, activityA, activityB);
				double satisfiedTraces = model.getSatisfiedTraces(constraint, activityA, activityB);
				double vacuouslySatisfiedTraces = model.getVacuouslySatisfiedTraces(constraint, activityA, activityB);
				double violatedTraces = model.getViolatedTraces(constraint, activityA, activityB);

//				System.out.println("considering : " + constraint + "\t comp = " + completedTraces + "\t sat = " + satisfiedTraces + "\t vac = " + vacuouslySatisfiedTraces + "\t vio = " + violatedTraces);
				if (completedTraces > 0 && ((satisfiedTraces + vacuouslySatisfiedTraces) / completedTraces) >= minTraceSupport) {
//					System.out.println("adding : " + constraint + "\t comp = " + completedTraces + "\t sat = " + satisfiedTraces + "\t vac = " + vacuouslySatisfiedTraces + "\t vio = " + violatedTraces);
					filtered.add(constraint, activityA, activityB, (int) completedTraces, (int) satisfiedTraces, (int) vacuouslySatisfiedTraces, (int) violatedTraces);
				}
			}
		}
		return filtered;
	}

	public static double precision(DeclareModel goldStandard, DeclareModel candidate) {
		double tp = TP(goldStandard, candidate);
		double fp = FP(goldStandard, candidate);
		return tp / (tp + fp);
	}

	public static double recall(DeclareModel goldStandard, DeclareModel candidate) {
		double tp = TP(goldStandard, candidate);
		double fn = FN(goldStandard, candidate);
		return tp / (tp + fn);
	}

	public static double f1(DeclareModel candidate, DeclareModel goldStandard) {
		double precision = precision(goldStandard, candidate);
		double recall = recall(goldStandard, candidate);
		return (2 * precision * recall) / (precision + recall);
	}

	public HashMap<SimplifiedDeclareModel.RELATION, HashMap<Pair<String, String>, HashMap<String, Double>>> getConstraints() {
		return constraints;
	}

	public void setConstraints(HashMap<SimplifiedDeclareModel.RELATION, HashMap<Pair<String, String>, HashMap<String, Double>>> constraints) {
		this.constraints = constraints;
	}
	
	public boolean hasTraces() {
		return hasTraces;
	}
	
//	public void dumpModel(String filename) {
//		File f = new File(filename);
//		try {
//			PrintWriter pw = new PrintWriter(f);
//			for (DeclareModelForView.RELATION template : constraints.keySet()) {
//				for (Pair<String, String> pair : constraints.get(template).keySet()) {
//					pw.println(template + " (" + pair.getLeft() + ", " + pair.getRight() + ")");
//				}
//			}
//			pw.flush();
//			pw.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
	
//	public static DeclareModel convert(DeclareMap source) {
//		DeclareModel model = new DeclareModel();
//		
//		for(ConstraintDefinition cd : source.getModel().getConstraintDefinitions()) {
//			String activityA = null;
//			String activityB = null;
//			if (cd.getParameters().size() > 1) {
//				Iterator<Parameter> iter = cd.getParameters().iterator();
//				Parameter p1 = iter.next();
//				Parameter p2 = iter.next();
//
//				activityA = cd.getBranches(p1).iterator().next().getName();
//				activityB = cd.getBranches(p2).iterator().next().getName();
//			}
//			if (cd.getParameters().size() == 1) {
//				Iterator<Parameter> iter = cd.getParameters().iterator();
//				Parameter p1 = iter.next();
//				activityA = cd.getBranches(p1).iterator().next().getName();
//				activityB = cd.getBranches(p1).iterator().next().getName();
//			}
//
//			Double activations= 0.;
//			Double fulfillments= 0.;
//			
//			switch(DeclareMiner.getTemplate(cd)) {
//			case Absence2:
//				model.addAbsence2(activityA, activations, fulfillments);
//				break;
//			case Absence:
//				model.addAbsence(activityA, activations, fulfillments);
//				break;
//			case Init:
//				model.addInit(activityA, activations, fulfillments);
//				break;
//			case Absence3:
//				model.addAbsence3(activityA, activations, fulfillments);
//				break;
//			case ALTERNATE_SUCCESSION:
//				model.addAlternateSuccession(activityA, activityB, activations, fulfillments);
//				break;
//			case CHAIN_SUCCESSION:
//				model.addChainSuccession(activityA, activityB, activations, fulfillments);
//				break;
//			case CHOICE:
//				model.addChoices(activityA, activityB, activations, fulfillments);
//				break;
//			case Exactly1:
//				model.addExactly1(activityA, activations, fulfillments);
//				break;
//			case Exactly2:
//				model.addExactly2(activityA, activations, fulfillments);
//				break;
//			case EXCLUSIVE_CHOICE:
//				model.addExclusiveChoices(activityA, activityB, activations, fulfillments);
//				break;	
//			case Existence:
//				model.addExistence(activityA, activations, fulfillments);
//				break;
//			case Existence2:
//				model.addExistence2(activityA, activations, fulfillments);
//				break;
//			case Existence3:
//				model.addExistence3(activityA, activations, fulfillments);
//				break;
//			case ALTERNATE_PRECEDENCE:
//				model.addAlternatePrecedence(activityA, activityB, activations, fulfillments);
//				break;
//			case ALTERNATE_RESPONSE:
//				model.addAlternateResponse(activityA, activityB, activations, fulfillments);
//				break;
//			case CHAIN_PRECEDENCE:
//				model.addChainPrecedence(activityA, activityB, activations, fulfillments);
//				break;
//			case CHAIN_RESPONSE:
//				model.addChainResponse(activityA, activityB, activations, fulfillments);
//				break;
//			case COEXISTENCE:
//				model.addCoExistence(activityA, activityB, activations, fulfillments);
//				break;
//			case Not_CoExistence:
//				model.addNotCoExistence(activityA, activityB, activations, fulfillments);
//				break;
//			case Not_Succession:
//				model.addNotSuccession(activityA, activityB, activations, fulfillments);
//				break;
//			case PRECEDENCE:
//				model.addPrecedence(activityA, activityB, activations, fulfillments);
//				break;
//			case Response:
//				model.addResponse(activityA, activityB, activations, fulfillments);
//				break;
//			case RESPONDED_EXISTENCE:
//				model.addRespondedExistence(activityA, activityB, activations, fulfillments);
//				break;
//			case Succession:
//				model.addSuccession(activityA, activityB, activations, fulfillments);
//				break;
//		}
//	}
//		
//		return model;
//	}
	
//	public static DeclareModel readFromFile(String filename) {
//		AssignmentViewBroker broker = XMLBrokerFactory.newAssignmentBroker(filename);
//		AssignmentModel model = broker.readAssignment();
//		AssignmentModelView view = new AssignmentModelView(model);
//		broker.readAssignmentGraphical(model, view);
//		org.processmining.plugins.declare.visualizing.AssignmentViewBroker brokerCh = org.processmining.plugins.declare.visualizing.XMLBrokerFactory.newAssignmentBroker(filename);
//		org.processmining.plugins.declare.visualizing.AssignmentModel modelCh = brokerCh.readAssignment();
//		org.processmining.plugins.declare.visualizing.AssignmentModelView viewCheck = new org.processmining.plugins.declare.visualizing.AssignmentModelView(modelCh);
//		brokerCh.readAssignmentGraphical(modelCh, viewCheck);
//		DeclareMap decModel = new DeclareMap(model, modelCh, view,viewCheck, broker, brokerCh);
//		return convert(decModel);
//	}
}