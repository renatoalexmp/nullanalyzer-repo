package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest11 {

	private static NPTest11 instancia = new NPTest11();

	class ClasseA {

	}

	class ClasseB extends ClasseA {
		String campo = "descrição";
	}

	public String teste() {
		// Tentar fazer um cast com uma referência nula, não deve lançar NPE
		ClasseA a = null;
		ClasseB b = (ClasseB) a;

		return b.campo;
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
