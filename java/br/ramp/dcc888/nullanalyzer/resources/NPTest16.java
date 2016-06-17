package br.ramp.dcc888.nullanalyzer.resources;

import java.util.Random;


public class NPTest16 {
	
	private static NPTest16 instancia = new NPTest16();	
	
	class ClasseA {
		
		public Object nullReference() {
			return null;
		}		
	}
	
	public boolean teste(int param) {		
		Object z = null;
		Integer x = (Integer) z;				
		if (x > 9) {
			return true;
		}
		return false;		
	}
	
	public static void main(String[] args) {
		instancia.teste(10);
	}

}
