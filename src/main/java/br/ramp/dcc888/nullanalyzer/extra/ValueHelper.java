package br.ramp.dcc888.nullanalyzer.extra;

import soot.RefType;
import soot.Value;
import soot.jimple.BinopExpr;
import soot.jimple.NullConstant;
import soot.jimple.NumericConstant;
import soot.jimple.ParameterRef;
import soot.jimple.StringConstant;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JLengthExpr;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JNewMultiArrayExpr;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;

public class ValueHelper {
	
	public static class Constant {
		
		public static boolean isNull(Value value) {
			return value instanceof NullConstant;		
		}	
		
		public static boolean isNonNull(Value value) {
			return (value instanceof soot.jimple.Constant) && !isNull(value);		
		}
		
		public static boolean isNumeric(Value value) {
			return value instanceof NumericConstant;		
		}
		
		public static boolean isString(Value value) {
			return value instanceof StringConstant;		
		}
		
	}
	
	public static class Expression {		
		
		public static boolean isStaticInvoke(Value value) {
			return value instanceof JStaticInvokeExpr;				
		}
		
		public static boolean isVirtualInvoke(Value value) {
			return value instanceof JVirtualInvokeExpr;				
		}	
		
		public static boolean isLength(Value value) {
			return value instanceof JLengthExpr;				
		}	
		
		public static boolean isBinaryOperation(Value value) {
			return value instanceof BinopExpr;				
		}
		
		public static boolean isNew(Value value) {
			return value instanceof JNewExpr;				
		}
		
		public static boolean isNewArray(Value value) {
			return value instanceof JNewArrayExpr;				
		}
		
		public static boolean isNewMultiArray(Value value) {
			return value instanceof JNewMultiArrayExpr;				
		}
		
		public static boolean isCast(Value value) {
			return value instanceof JCastExpr;				
		}
		
		public static boolean isEqual(Value value) {
			return value instanceof JEqExpr;				
		}
		
		public static boolean isNotEqual(Value value) {
			return value instanceof JNeExpr;				
		}
		
	}
	
	public static boolean isParameter(Value value) {
		return value instanceof ParameterRef;		
	}	
	
	public static boolean isLocal(Value value) {
		return value instanceof JimpleLocal;		
	}
	
	public static boolean isInstanceField(Value value) {
		return value instanceof JInstanceFieldRef;		
	}
	
	
	/* String representations */
		
	public static String asLocalName(String declaringClass, String method, Value value) {
		return String.format("%s:%s.%s", declaringClass, method, value);
	}
	
	public static String asInstanceFieldName(String declaringClass, Value value) {
		JInstanceFieldRef field = (JInstanceFieldRef) value;
		
		return String.format("%s:$%s", declaringClass, field.getFieldRef().name());
	}
	
	public static String asThrowName(String declaringClass, String method, Value value) {
		return String.format("%s:%s.@%s", declaringClass, method, value);
	}

	public static String asSyncronizedBlockName(String declaringClass, String method, Value value) {
		return String.format("%s:%s.*%s", declaringClass, method, value);
	}
	
	public static String asMethodReturn(String declaringClass, String method) {
		return String.format("%s:%s", declaringClass, method);
	}
	
	public static String getDeclaringClass(Value variable) {
		JInstanceFieldRef field = (JInstanceFieldRef) variable;
		RefType fieldRef = (RefType) field.getBaseBox().getValue().getType();		
		return fieldRef.getSootClass().getShortName();
	}	
	

}
