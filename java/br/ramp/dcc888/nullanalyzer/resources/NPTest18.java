package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest18 {
	
	private static NPTest18 instancia = new NPTest18();	
		
	public int teste(Integer param, int x, String y) {	
		return param * 10;
	}
	
	public static void main(String[] args) {
		instancia.teste(null, 0, "");
	}

}
