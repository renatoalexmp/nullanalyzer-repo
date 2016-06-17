package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest09 {

	private static NPTest09 instancia = new NPTest09();

	public void teste() {
		// Iterar sobre os valores de uma array de referência nula ou null
		String[] vetor = null;
		for (String valor : vetor) {
			System.out.println(valor);
		}
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
