package br.ramp.dcc888.nullanalyzer.extra;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class NullStateHelper {	
	
	private static NullStateHelper INSTANCE = null;

	private ConcurrentHashMap<String, NullStateEnum> variableNullStateMap = new ConcurrentHashMap<String, NullStateEnum>();

	private NullStateHelper() {
	}
	
	public synchronized static NullStateHelper instance() {
		if (INSTANCE == null) {
			INSTANCE = new NullStateHelper();
		}
		return INSTANCE;
	}
	
	public void changeVariableNullState(String variable, NullStateEnum state) {
		if (state != null) {
			variableNullStateMap.put(variable, state);
		}
	}

	public NullStateEnum getVariableNullState(String variable) {
		return variableNullStateMap.get(variable);
	}
	
	public boolean isNullState(String variable) {
		NullStateEnum nullState = getVariableNullState(variable);
		if (nullState != null && nullState.equals(NullStateEnum.NULL)) {
			return true;
		}
		return false;
	}

	public boolean isUnknownState(String variable) {
		NullStateEnum nullState = getVariableNullState(variable);
		if (nullState != null && nullState.equals(NullStateEnum.UNKNOWN)) {
			return true;
		}
		return false;
	}

	public boolean isNonNullState(String variable) {
		NullStateEnum nullState = getVariableNullState(variable);
		if (nullState != null && nullState.equals(NullStateEnum.NON_NULL)) {
			return true;
		}
		return false;
	}
	
	public boolean isMapped(String variable) {
		return variableNullStateMap.containsKey(variable);
	}

	public synchronized void printVariableNullStates() {
		PrintUtil instance = PrintUtil.instance();
		instance.print("======== NULL STATES TABLE ==========");
		for (Iterator<Entry<String, NullStateEnum>> iterator = variableNullStateMap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<String, NullStateEnum> entry = (Entry<String, NullStateEnum>) iterator
					.next();
			
			instance.delimiter("\t\t\t").print(entry.getKey(), entry.getValue());
			
		}
		instance.print("====================================");
	}
	
	public ConcurrentHashMap<String, NullStateEnum> getVariableNullStateMap() {
		return variableNullStateMap;
	}

}
