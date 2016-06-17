package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest10 {

	private static NPTest10 instancia = new NPTest10();

	public int teste() {
		// Acesso ao método estático de uma referência nula, não deve causar NPE
		Integer t = null;
		int x = t.parseInt("888");
		return x + 2;
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
