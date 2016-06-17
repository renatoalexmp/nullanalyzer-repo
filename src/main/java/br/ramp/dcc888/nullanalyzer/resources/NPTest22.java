package br.ramp.dcc888.nullanalyzer.resources;

import java.util.Random;

public class NPTest22 {
	
	private static NPTest22 instancia = new NPTest22();	
			
	public void teste() {	
		Integer x = null;
		switch(x) {
		case 0: break;
		}
	}
	
	public static void main(String[] args) {
		instancia.teste();		
	}

}
