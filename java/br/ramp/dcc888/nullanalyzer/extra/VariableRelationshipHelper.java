package br.ramp.dcc888.nullanalyzer.extra;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class VariableRelationshipHelper {	
	
	private static VariableRelationshipHelper INSTANCE = null;

	private ConcurrentHashMap<String, String> variableRelationshipMap = new ConcurrentHashMap<String, String>();

	private VariableRelationshipHelper() {
	}
	
	public synchronized static VariableRelationshipHelper instance() {
		if (INSTANCE == null) {
			INSTANCE = new VariableRelationshipHelper();
		}
		return INSTANCE;
	}
	
	public boolean addRelation(String master, String slave) {
		getVariableRelationshipMap().put(master, slave);
		return false;		
	}
	
	public boolean isMapped(String variable) {
		return getVariableRelationshipMap().containsKey(variable);
	}

	public synchronized void printVariableRelationship() {
		PrintUtil instance = PrintUtil.instance();
		instance.print("======== RELATIONSHIP TABLE ==========");
		for (Iterator<Entry<String, String>> iterator = getVariableRelationshipMap().entrySet().iterator(); iterator
				.hasNext();) {
			Entry<String, String> entry = (Entry<String, String>) iterator
					.next();
			
			instance.delimiter("\t\t\t").print(entry.getKey(), entry.getValue());
			
		}
		instance.print("====================================");
	}

	public ConcurrentHashMap<String, String> getVariableRelationshipMap() {
		return variableRelationshipMap;
	}

}
