package br.ramp.dcc888.nullanalyzer.extra;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VulnerableMethodsHelper {

	private static VulnerableMethodsHelper INSTANCE = null;

	public Set<String> vulnerableMethods = Collections
			.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	private String className;

	private VulnerableMethodsHelper() {
	}

	public synchronized static VulnerableMethodsHelper instance() {
		
		if (INSTANCE == null) {
			INSTANCE = new VulnerableMethodsHelper();
		}
		return INSTANCE;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}

	public void printVulnerableMethods() {
		System.out.println("======== " + className + ": MÉTODOS COM POSSIBILIDADE DE NPE ==========");
		if (vulnerableMethods.size() > 0) {
			for (Iterator<String> iterator = vulnerableMethods.iterator(); iterator
					.hasNext();) {
				String method = (String) iterator.next();

				System.out.println(method);

			}
		} else {
			System.out.println("Nenhum método encontrado com vulnerabilidade");
		}
		System.out.println("=====================================================");
	}

	public void addVulnerableMethod(String method) {
		vulnerableMethods.add(method);
	}

}
