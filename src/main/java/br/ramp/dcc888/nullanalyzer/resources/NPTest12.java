package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest12 {

	private static NPTest12 instancia = new NPTest12();

	public String teste() {
		// Concatenar String e null, não causa NPE
		return "prefixo" + null + "sufixo";
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
