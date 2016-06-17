package br.ramp.dcc888.nullanalyzer.extra;

import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JEnterMonitorStmt;
import soot.jimple.internal.JExitMonitorStmt;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JThrowStmt;

public class StatementHelper {
		
	public static boolean isGoto(Stmt statement) {
		return statement instanceof JGotoStmt;		
	}	
	
	public static boolean isEnterMonitor(Stmt statement) {
		return statement instanceof JEnterMonitorStmt;		
	}
	
	public static boolean isExitMonitor(Stmt statement) {
		return statement instanceof JExitMonitorStmt;		
	}
	
	public static boolean isIdentity(Stmt statement) {
		return statement instanceof JIdentityStmt;		
	}
	
	public static boolean isThrow(Stmt statement) {
		return statement instanceof JThrowStmt;		
	}
	
	public static boolean isAssign(Stmt statement) {
		return statement instanceof JAssignStmt;		
	}
	
	public static boolean isReturn(Stmt statement) {
		return statement instanceof JReturnStmt;		
	}

}
