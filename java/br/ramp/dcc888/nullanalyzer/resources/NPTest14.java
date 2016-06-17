package br.ramp.dcc888.nullanalyzer.resources;


public class NPTest14 {
	
	private static NPTest14 instancia = new NPTest14();
	
	public int teste() {
		//Retornar referência nula para método com retorno primitivo
		Integer a = null;
		return a;
	}
	
	public static void main(String[] args) {
		instancia.teste();
	}

}
