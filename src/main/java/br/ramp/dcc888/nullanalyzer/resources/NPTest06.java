package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest06 {

	private static NPTest06 instancia = new NPTest06();

	public int teste() {
		// Obter o tamanho de uma array de referência nula ou null
		String[] vetor = teste2();
		String[] vetor2 = null;
		int tamanho = vetor.length;
		int tamanho2 = vetor2.length;
		return tamanho + tamanho2;
	}

	public String[] teste2() {
		return null;
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
