package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest03 {

	private static NPTest03 instancia = new NPTest03();

	public void teste() {
		// Auto unboxing de referência nula
		Integer inteiro = null;
		int primitivo = inteiro;
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
