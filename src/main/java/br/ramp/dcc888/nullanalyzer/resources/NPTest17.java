package br.ramp.dcc888.nullanalyzer.resources;

public class NPTest17 {
	
	private static NPTest17 instancia = new NPTest17();	
	
	static class ClasseA {
		
		public static Integer nullReference() {
			return null;
		}	
		
		public static int staticMethod() {
			return 0;
		}
		
		public static Integer staticMethod2() {
			Integer x = nullReference();
			return x;
		}
	}
	
	public int teste(int param) {		
		Integer x = null;
		Integer y = nullReference();	
		Integer z = ClasseA.staticMethod();	
		Integer w = x + y + z;
		return w;
	}
	
	public static Integer nullReference() {
		return null;
	}
	
	public static void main(String[] args) {
		instancia.teste(10);
	}

}
