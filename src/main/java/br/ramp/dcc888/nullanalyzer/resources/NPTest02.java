package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest02 {

	private static NPTest02 instancia = new NPTest02();

	class Classe {
		private String campo = "descrição";

		public String getCampo() {
			return campo;
		}
	}

	public void teste() {
		// Acessar campo de instância de referência nula
		Classe c = null;
		String campo = c.getCampo();
	}

	public static void main(String[] args) {
		instancia.teste();
	}

}
