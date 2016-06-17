package br.ramp.dcc888.nullanalyzer.extra;

import soot.NullType;
import soot.RefLikeType;
import soot.RefType;
import soot.Type;

public class TypeHelper {	
	
	public static boolean isNull(Type type) {
		return type instanceof NullType;
	}
	
	public static boolean isRef(Type type) {
		return type instanceof RefType;
	}
	
	public static boolean isRefLike(Type type) {
		return type instanceof RefLikeType;
	}

}
