package br.ramp.dcc888.nullanalyzer.resources;

import java.util.Random;

public class NPTest21 {
	
	private static NPTest21 instancia = new NPTest21();	
	
	class Classe {
		private String campo = "xxx";

		public String getCampo() {
			return campo;
		}
	}
	
	public static Classe nullReference() {
		return null;
	}
		
	public String teste() {	
		Classe c = nullReference();
		if (c != null && c.getCampo().equals("anything")) {
			return "";
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		instancia.teste();		
	}

}
