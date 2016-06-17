package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest08 {

	private static NPTest08 instancia = new NPTest08();

	public void teste() {
		// Sincronizar bloco com referência nula
		Object[] objeto = null;
		synchronized (objeto) {
			teste2();
		}
	}

	private void teste2() {
		System.out.println("Feito!");
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
