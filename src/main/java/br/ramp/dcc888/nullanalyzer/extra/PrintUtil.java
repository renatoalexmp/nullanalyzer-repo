package br.ramp.dcc888.nullanalyzer.extra;

import java.io.PrintStream;

public class PrintUtil {	
	
	private static PrintUtil INSTANCE;
	
	private PrintStream printer = System.out;
	
	private boolean printingEnabled = true;
	
	private String delimiter = " ";
	
	private StringBuffer prefix = new StringBuffer();
	
	private StringBuffer buffer = new StringBuffer();
		
	private PrintUtil(PrintStream printStream) {
		if (printStream != null) {
			printer = printStream;
		}
	}
	
	public synchronized static PrintUtil instance(PrintStream printStream) {
		if (INSTANCE == null) {
			INSTANCE = new PrintUtil(printStream);	
			INSTANCE.enablePrinting();
		}
		return INSTANCE;
	}
	
	public synchronized static PrintUtil instance() {
		return instance(null);
	}
	
	private void reset() {
		prefix.setLength(0);
		buffer.setLength(0);
		delimiter = " ";
		prefix.setLength(0);
	}
	
	public PrintUtil append(Object... values) {		
		for (Object string : values) {
			buffer.append(string.toString() + delimiter);
		}			
		
		return this;		
	}
	
	public PrintUtil print(Object... values) {
		buffer.append(prefix);
		
		for (Object string : values) {
			buffer.append(string.toString() + delimiter);
		}		
		
		if (printingEnabled) {		
			printer.println(buffer.toString());
		}
		
		reset();
		
		return this;		
	}

	
	public PrintUtil delimiter(String value) {		
		delimiter = value == null? " " : value;
		
		return this;
	}
	
	public PrintUtil padding(int value) {
		for (int i = 0; i < value; i++) {
			prefix.append("\t");
		}
		
		return this;
	}
	
	public PrintUtil line() {
		prefix.append("\n");
		
		return this;
	}
	
	public void disablePrinting() {
		printingEnabled = false;
	}
	
	public void enablePrinting() {
		printingEnabled = true;
	}
	

}
