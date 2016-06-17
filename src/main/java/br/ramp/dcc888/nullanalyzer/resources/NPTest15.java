package br.ramp.dcc888.nullanalyzer.resources;


public class NPTest15 {
	
	private static NPTest15 instancia = new NPTest15();
	
	class Classe {
		private String campo = "descrição";

		public String getCampo() {
			return campo;
		}
	}
	
	public void teste(int param) {		
		// Indecidível NPE pois depende do fluxo
		Classe c = null;
		if (param > 0) c = new Classe();
		if (param > 3) c.getCampo();
	}
	
	public static void main(String[] args) {
		instancia.teste(10);
	}

}
