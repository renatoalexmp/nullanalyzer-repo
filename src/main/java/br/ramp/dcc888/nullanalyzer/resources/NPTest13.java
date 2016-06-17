package br.ramp.dcc888.nullanalyzer.resources;


public class NPTest13 {
	
	private static NPTest13 instancia = new NPTest13();
		
	class ClasseA {
		Integer numero = null;
	}
	
	class ClasseB{
		Integer numero = 123;
	}		
	
	public Integer teste() {
		ClasseA a = new ClasseA();
		ClasseB b = new ClasseB();
		
		return a.numero * b.numero;
	}
	
	public static void main(String[] args) {
		instancia.teste();
	}

}
