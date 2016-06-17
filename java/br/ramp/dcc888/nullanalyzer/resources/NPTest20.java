package br.ramp.dcc888.nullanalyzer.resources;

import java.util.Random;

public class NPTest20 {
	
	private static NPTest20 instancia = new NPTest20();	
		
	public String teste() {	
		Integer x = null;
		String[] arrayString = new String[x];
		Integer y = new Random().nextInt();
		String[] arrayA = new String[new Integer(300)];
		String[] arrayB = new String[y];
		String[] arraySized = new String[100];
		Integer[][] arrayIntegerMulti = new Integer[4][x];
		int[] arrayNativa = new int[x];
		
		return arraySized + arrayString.toString() + arrayIntegerMulti + arrayNativa;
	}
	
	public static void main(String[] args) {
		instancia.teste();
	}

}
