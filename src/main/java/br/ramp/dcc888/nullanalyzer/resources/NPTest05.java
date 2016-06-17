package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest05 {

	private static NPTest05 instancia = new NPTest05();

	public void teste() {
		// Lançar referência nula ou null como exceção
		RuntimeException e = null;
		throw e;
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
