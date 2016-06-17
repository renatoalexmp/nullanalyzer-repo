package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest07 {

	private static NPTest07 instancia = new NPTest07();

	public String teste() {
		// Obter o valor de uma posição de uma array de referência nula ou null
		String[] vetor = null;
		String valor = vetor[8];
		return valor;
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
