package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest04 {

	private static NPTest04 instancia = new NPTest04();

	public void teste() {
		// Lançar NPE diretamente
		throw new NullPointerException();
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
