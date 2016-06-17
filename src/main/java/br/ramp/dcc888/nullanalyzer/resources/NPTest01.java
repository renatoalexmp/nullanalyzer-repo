package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest01 {

	private static NPTest01 instancia = new NPTest01();

	class Classe {
		String campo = "descrição";
	}

	public void teste() {
		// Acessar campo de instância de referência nula
		Classe c = null;
		String campo = c.campo;
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
