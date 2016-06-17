package br.ramp.dcc888.nullanalyzer.resources;

import java.util.Random;

public class NPTest19 {
	
	private static NPTest19 instancia = new NPTest19();	
		
	public int teste() {	
		Integer x = null;
		x = 10 + new Random().nextInt();
		int y = x;
		return x + y + 10;
	}
	
	public static void main(String[] args) {
		instancia.teste();
	}

}
