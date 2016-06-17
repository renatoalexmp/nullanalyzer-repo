package br.ramp.dcc888.nullanalyzer.jtp;

import static br.ramp.dcc888.nullanalyzer.extra.StatementHelper.isAssign;
import static br.ramp.dcc888.nullanalyzer.extra.StatementHelper.isEnterMonitor;
import static br.ramp.dcc888.nullanalyzer.extra.StatementHelper.isGoto;
import static br.ramp.dcc888.nullanalyzer.extra.StatementHelper.isIdentity;
import static br.ramp.dcc888.nullanalyzer.extra.StatementHelper.isReturn;
import static br.ramp.dcc888.nullanalyzer.extra.StatementHelper.isThrow;
import static br.ramp.dcc888.nullanalyzer.extra.TypeHelper.isNull;
import static br.ramp.dcc888.nullanalyzer.extra.TypeHelper.isRef;
import static br.ramp.dcc888.nullanalyzer.extra.ValueHelper.asInstanceFieldName;
import static br.ramp.dcc888.nullanalyzer.extra.ValueHelper.asLocalName;
import static br.ramp.dcc888.nullanalyzer.extra.ValueHelper.asMethodReturn;
import static br.ramp.dcc888.nullanalyzer.extra.ValueHelper.asSyncronizedBlockName;
import static br.ramp.dcc888.nullanalyzer.extra.ValueHelper.asThrowName;
import static br.ramp.dcc888.nullanalyzer.extra.ValueHelper.getDeclaringClass;
import static br.ramp.dcc888.nullanalyzer.extra.ValueHelper.isInstanceField;
import static br.ramp.dcc888.nullanalyzer.extra.ValueHelper.isLocal;
import static br.ramp.dcc888.nullanalyzer.extra.ValueHelper.isParameter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.RefType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JEnterMonitorStmt;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JLengthExpr;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewMultiArrayExpr;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JThrowStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.tagkit.ColorTag;
import br.ramp.dcc888.nullanalyzer.extra.NullStateEnum;
import br.ramp.dcc888.nullanalyzer.extra.NullStateHelper;
import br.ramp.dcc888.nullanalyzer.extra.PrintUtil;
import br.ramp.dcc888.nullanalyzer.extra.ValueHelper.Constant;
import br.ramp.dcc888.nullanalyzer.extra.ValueHelper.Expression;
import br.ramp.dcc888.nullanalyzer.extra.VariableRelationshipHelper;
import br.ramp.dcc888.nullanalyzer.extra.VulnerableMethodsHelper;

public class NullAnalysisTransformer extends BodyTransformer {

	public String declaringClass;

	private final NullStateHelper NSH = NullStateHelper.instance();
	private final VulnerableMethodsHelper VMH = VulnerableMethodsHelper.instance();
	private final VariableRelationshipHelper VRH = VariableRelationshipHelper.instance();
	private final PrintUtil PRINTER = PrintUtil.instance(G.v().out);

	private final ColorTag RED_TAG = new ColorTag(ColorTag.RED);
	private final ColorTag YELLOW_TAG = new ColorTag(ColorTag.YELLOW);
	private final ColorTag GREEN_TAG = new ColorTag(ColorTag.GREEN);
	
	public final ConcurrentHashMap<String, String> pendingResolveVariables = new ConcurrentHashMap<String, String>();

	private final boolean ENABLE_INTERPROCEDURAL_ANALYSIS = false;

	protected synchronized void internalTransform(Body body, String phaseName,
			Map<String, String> options) {

//		PRINTER.disablePrinting();

		String methodName = body.getMethod().getName();

		declaringClass = body.getMethod().getDeclaringClass().getShortName();
		
		VMH.setClassName(declaringClass);

		Iterator<Unit> it = body.getUnits().iterator();

		while (it.hasNext()) {
			
			Stmt statement = (Stmt) it.next();
			
//			System.out.println("§ " + statement.getClass().getSimpleName());

			if (ENABLE_INTERPROCEDURAL_ANALYSIS && isReturn(statement)) {
				JReturnStmt returnStatement = (JReturnStmt) statement;

				Value returnValue = returnStatement.getOp();

				String returnVariable = asMethodReturn(declaringClass,
						methodName);

				if (Constant.isNull(returnValue)) {
					NullStateEnum state = NullStateEnum.NULL;
					NSH.changeVariableNullState(returnVariable,
							state);
					resolvesVariable(returnVariable, state);
				}
				if (Constant.isNonNull(returnValue)) {
					NullStateEnum state = NullStateEnum.NULL;
					NSH.changeVariableNullState(returnVariable,
							NullStateEnum.NON_NULL);
					resolvesVariable(returnVariable, state);
				} else {
					NullStateEnum state = NullStateEnum.NULL;
					if (isLocal(returnValue)) {
						state = NSH.getVariableNullState(asLocalName(
								declaringClass, methodName, returnValue));
						NSH.changeVariableNullState(
								returnVariable, state);
					} else if (isInstanceField(returnValue)) {
						state = NSH.getVariableNullState(asInstanceFieldName(
										getDeclaringClass(returnValue),
										returnValue));
						NSH.changeVariableNullState(returnVariable, state);
					}
					
					resolvesVariable(returnVariable, state);
				}
			}

			if (isGoto(statement)) {
				Unit target = getJumpUnit((JGotoStmt) statement);

				PRINTER.print("GOING TO", target);
			}

			//1 - Attempt to synchronize a null reference or null constant 
			checkBySynchronizingNullObject(methodName, statement);

			if (isIdentity(statement)) {
				JIdentityStmt identityStatement = (JIdentityStmt) statement;

				Value leftOperator = identityStatement.getLeftOp();
				Value rightOperator = identityStatement.getRightOp();

				String identityVariable = asLocalName(declaringClass,
						methodName, leftOperator);

				if (isParameter(rightOperator)) {
					ParameterRef parameter = (ParameterRef) rightOperator;

					if (isRef(parameter.getType())) {
						NSH.changeVariableNullState(identityVariable,
								NullStateEnum.UNKNOWN);
					} else {
						NSH.changeVariableNullState(identityVariable,
								NullStateEnum.NON_NULL);
					}

				}

			}

			//2 - Throw a null or NPE
			checkByThrowingNullOrDirectNPE(methodName, statement);

			if (isAssign(statement)) {
				AssignStmt assignStatement = (AssignStmt) statement;

				Value leftOperator = assignStatement.getLeftOp();
				Value rightOperator = assignStatement.getRightOp();

				String assigmentVariable = asLocalName(declaringClass,
						methodName, leftOperator);

				if (isInstanceField(leftOperator)) {
					assigmentVariable = asInstanceFieldName(declaringClass,
							leftOperator);
				}

				if (Constant.isNull(rightOperator)) {
					NSH.changeVariableNullState(assigmentVariable,
							NullStateEnum.NULL);
				}

				if (Constant.isNonNull(rightOperator)) {
					NSH.changeVariableNullState(assigmentVariable,
							NullStateEnum.NON_NULL);
				}

				if (isLocal(rightOperator)) {
					String asLocalName = asLocalName(declaringClass,
							methodName, rightOperator);
					
					NSH.changeVariableNullState(assigmentVariable, NSH
							.getVariableNullState(asLocalName));
					
					VRH.addRelation(assigmentVariable, asLocalName);
					
				}

				if (isInstanceField(rightOperator)) {
					
					String asInstanceFieldName = asInstanceFieldName(
							getDeclaringClass(rightOperator),
							rightOperator);
							
					if (NSH.isMapped(asInstanceFieldName)) {						
						NSH.changeVariableNullState(assigmentVariable, NSH
							.getVariableNullState(asInstanceFieldName));
						
						VRH.addRelation(assigmentVariable, asInstanceFieldName);
					} else {
						NSH.changeVariableNullState(assigmentVariable, NullStateEnum.UNKNOWN);
					}
				}

				if (Expression.isNew(rightOperator)) {
					NSH.changeVariableNullState(assigmentVariable,
							NullStateEnum.NON_NULL);
				}
				

				checkByNewArray(methodName, rightOperator, assigmentVariable);
				
				checkByNewMultiArray(methodName, rightOperator, assigmentVariable);

				// 3 - Attempt to cast a object to a distinct another type when it is null
				checkByUnboxingNull(methodName, rightOperator,
						assigmentVariable);

				checkByLengthOnNull(methodName, rightOperator,
						assigmentVariable);

				checkByStaticInvokeOnNull(rightOperator, assigmentVariable);

				checkByVirtualInvokeOnNull(rightOperator, assigmentVariable);

				checkByBinaryOperationOnNull(methodName, rightOperator,
						assigmentVariable);

			}

		}

		// Check for null states after analysis and add corresponding tags
		processResults(body, methodName);

	}

	private void checkByNewArray(String methodName, Value rightOperator,
			String assigmentVariable) {
		if (Expression.isNewArray(rightOperator)) {
			JNewArrayExpr newArrayExpression = (JNewArrayExpr) rightOperator;
			
			Value sizeBox = newArrayExpression.getSizeBox().getValue();
			
			if (Constant.isNull(sizeBox)) {
				NSH.changeVariableNullState(assigmentVariable,
						NullStateEnum.NULL);
			} else if (Constant.isNonNull(sizeBox)) {
				NSH.changeVariableNullState(assigmentVariable,
						NullStateEnum.NON_NULL);
			} else if (isLocal(sizeBox)) {
				String asLocalName = asLocalName(
						declaringClass, methodName, sizeBox);
				NSH.changeVariableNullState(assigmentVariable, NSH
						.getVariableNullState(asLocalName));
				
				VRH.addRelation(assigmentVariable, asLocalName);
				
			} else if (isInstanceField(sizeBox)) {
				String asInstanceFieldName = asInstanceFieldName(
						getDeclaringClass(sizeBox), sizeBox);
				
				NSH.changeVariableNullState(assigmentVariable, NSH
						.getVariableNullState(asInstanceFieldName));
				
				VRH.addRelation(assigmentVariable, asInstanceFieldName);
			}					
			
		}
	}
	
	private void checkByNewMultiArray(String methodName, Value rightOperator,
			String assigmentVariable) {
		if (Expression.isNewMultiArray(rightOperator)) {
			JNewMultiArrayExpr newMultiArrayExpression = (JNewMultiArrayExpr) rightOperator;
			
			List<Value> sizes = newMultiArrayExpression.getSizes();
			
			Boolean nullStateFound = false;
					
			//HashSet<NullStateEnum> tempStates = new HashSet<>();		
			
			for (Iterator<Value> iterator = sizes.iterator(); iterator.hasNext();) {
				Value value = (Value) iterator.next();
				
				if (Constant.isNull(value)) {
					nullStateFound = true;
					break;
				} else if (Constant.isNonNull(value)) {
					NSH.changeVariableNullState(assigmentVariable,
							NullStateEnum.NULL);
				} else if (isLocal(value)) {					
					NullStateEnum variableNullState = NSH
					.getVariableNullState(asLocalName(
							declaringClass, methodName, value));
					
					if (variableNullState.equals(NullStateEnum.NULL)) {
						nullStateFound = true;
						break;
					}
					
				} else if (isInstanceField(value)) {
					NullStateEnum variableNullState = NSH
					.getVariableNullState(asInstanceFieldName(
							getDeclaringClass(value), value));
					
					if (variableNullState.equals(NullStateEnum.NULL)) {
						nullStateFound = true;
						break;
					}
				}					
			}
			
			if (nullStateFound) {
				NSH.changeVariableNullState(assigmentVariable,
						NullStateEnum.NULL);
			} else {
				NSH.changeVariableNullState(assigmentVariable,
						NullStateEnum.NON_NULL);
			}		
			
		}
	}

	private void checkByStaticInvokeOnNull(Value rightOperator,
			String assigmentVariable) {
		if (Expression.isStaticInvoke(rightOperator)) {
			JStaticInvokeExpr staticInvokeExpression = (JStaticInvokeExpr) rightOperator;

			Type staticClassReturnType = staticInvokeExpression
					.getMethod().getReturnType();

			if (isRef(staticClassReturnType)) {
				String shortNameMethodClass = staticInvokeExpression
						.getMethod().getDeclaringClass().getShortName();
				String shortNameMethod = staticInvokeExpression
						.getMethod().getName();
				String refAsMethodReturn = asMethodReturn(
						shortNameMethodClass, shortNameMethod);

				if (ENABLE_INTERPROCEDURAL_ANALYSIS
						&& NSH.isMapped(refAsMethodReturn)) {
					NSH.changeVariableNullState(assigmentVariable,
							NSH.getVariableNullState(refAsMethodReturn));
					
					VRH.addRelation(assigmentVariable, refAsMethodReturn);
				} else {
					if (ENABLE_INTERPROCEDURAL_ANALYSIS) {
						addResolveLater(refAsMethodReturn, assigmentVariable);
					}
					NSH.changeVariableNullState(assigmentVariable,
							NullStateEnum.UNKNOWN);
				}
			} else {
				NSH.changeVariableNullState(assigmentVariable,
						NullStateEnum.NON_NULL);
			}
		}
	}

	private void checkByVirtualInvokeOnNull(Value rightOperator,
			String assigmentVariable) {
		if (Expression.isVirtualInvoke(rightOperator)) {
			JVirtualInvokeExpr virtualInvokeExpression = (JVirtualInvokeExpr) rightOperator;

			Type virtualClassReturnType = virtualInvokeExpression
					.getMethod().getReturnType();

			if (isRef(virtualClassReturnType)) {
				String shortNameMethodClass = virtualInvokeExpression
						.getMethod().getDeclaringClass().getShortName();
				String shortNameMethod = virtualInvokeExpression
						.getMethod().getName();
				String refAsMethodReturn = asMethodReturn(
						shortNameMethodClass, shortNameMethod);

				if (ENABLE_INTERPROCEDURAL_ANALYSIS
						&& NSH.isMapped(refAsMethodReturn)) {
					NSH.changeVariableNullState(assigmentVariable,
							NSH.getVariableNullState(refAsMethodReturn));
					
					VRH.addRelation(assigmentVariable, refAsMethodReturn);
				} else {
					if (ENABLE_INTERPROCEDURAL_ANALYSIS) {
						addResolveLater(refAsMethodReturn, assigmentVariable);
					}
					NSH.changeVariableNullState(assigmentVariable,
							NullStateEnum.UNKNOWN);
				}
			} else {
				NSH.changeVariableNullState(assigmentVariable,
						NullStateEnum.NON_NULL);
			}

		}
	}

	private void checkByBinaryOperationOnNull(String methodName,
			Value rightOperator, String assigmentVariable) {
		if (Expression.isBinaryOperation(rightOperator)) {
			BinopExpr binaryExpression = (BinopExpr) rightOperator;

			Value operatorA = binaryExpression.getOp1();
			Value operatorB = binaryExpression.getOp2();

			if ((isLocal(operatorA) && NSH.isNullState(asLocalName(
					declaringClass, methodName, operatorA)))
					|| (isLocal(operatorB) && NSH
							.isNullState(asLocalName(declaringClass,
									methodName, operatorB)))) {
				NSH.changeVariableNullState(assigmentVariable,
						NullStateEnum.NULL);
			} else if ((isLocal(operatorA) && NSH
					.isUnknownState(asLocalName(declaringClass,
							methodName, operatorA)))
					|| (isLocal(operatorB) && NSH
							.isUnknownState(asLocalName(declaringClass,
									methodName, operatorB)))) {
				NSH.changeVariableNullState(assigmentVariable,
						NullStateEnum.UNKNOWN);
			} else {
				NSH.changeVariableNullState(assigmentVariable,
						NullStateEnum.NON_NULL);
			}

		}
	}

	private void checkByLengthOnNull(String methodName, Value rightOperator,
			String assigmentVariable) {
		if (Expression.isLength(rightOperator)) {
			JLengthExpr lengthExpression = (JLengthExpr) rightOperator;

			Value valueLength = lengthExpression.getOp();

			if (Constant.isNull(valueLength)) {
				NSH.changeVariableNullState(assigmentVariable,
						NullStateEnum.NULL);
			} else if (isLocal(valueLength)) {
				NSH.changeVariableNullState(assigmentVariable, NSH
						.getVariableNullState(asLocalName(
								declaringClass, methodName, valueLength)));
			} else if (isInstanceField(valueLength)) {
				NSH.changeVariableNullState(assigmentVariable, NSH
						.getVariableNullState(asInstanceFieldName(
								getDeclaringClass(valueLength), valueLength)));
			}
		}
	}

	private void checkByUnboxingNull(String methodName, Value rightOperator,
			String assigmentVariable) {
		if (Expression.isCast(rightOperator)) {

			JCastExpr castExpression = (JCastExpr) rightOperator;

			Value value = castExpression.getOp();

			if (Constant.isNull(value)) {
				NSH.changeVariableNullState(assigmentVariable,
						NullStateEnum.NULL);
			} else if (isLocal(value)) {
				NSH.changeVariableNullState(assigmentVariable, NSH
						.getVariableNullState(asLocalName(
								declaringClass, methodName, value)));
			} else if (isInstanceField(value)) {
				NSH.changeVariableNullState(assigmentVariable, NSH
						.getVariableNullState(asInstanceFieldName(
								getDeclaringClass(value), value)));
			}
		}
	}

	private void checkByThrowingNullOrDirectNPE(String methodName,
			Stmt statement) {
		if (isThrow(statement)) {
			JThrowStmt throwStatement = (JThrowStmt) statement;
			
			Type typeOperator = throwStatement.getOpBox().getValue()
					.getType();

			String throwVariable = asThrowName(declaringClass, methodName,
					throwStatement.getOpBox().getValue());

			if (isRef(typeOperator)) {
				RefType ref = (RefType) throwStatement.getOpBox()
						.getValue().getType();

				if (ref.getClassName().equals(
						"java.lang.NullPointerException")) {
					NSH.changeVariableNullState(throwVariable,
							NullStateEnum.NULL);
				}

			} else if (isNull(typeOperator)) {
				NSH.changeVariableNullState(throwVariable,
						NullStateEnum.NULL);
			}
		}
	}

	private void checkBySynchronizingNullObject(String methodName,
			Stmt statement) {
		if (isEnterMonitor(statement)) {
			JEnterMonitorStmt enterMonitorStatement = (JEnterMonitorStmt) statement;

			Value enterMonitorValue = enterMonitorStatement.getOp();

			if (Constant.isNull(enterMonitorValue)) {
				NSH.changeVariableNullState(
						asSyncronizedBlockName(declaringClass, methodName,
								enterMonitorValue), NullStateEnum.NULL);
			}
		}
	}

	private void processResults(Body b, String methodLocal) {

		PRINTER.print("METÓDO", methodLocal, "DA CLASSE", declaringClass);

		Iterator<Unit> it2 = b.getUnits().iterator();

		while (it2.hasNext()) {
			Stmt statement = (Stmt) it2.next();

			PRINTER.padding(1).print("[CLASS]", statement.getClass().getName(),
					"\t[DESCRIPTION]", statement);

			PRINTER.padding(2).print("DEFINIÇÃO");

			Iterator<ValueBox> defBoxes = statement.getDefBoxes().iterator();

			while (defBoxes.hasNext()) {
				ValueBox value = (ValueBox) defBoxes.next();

				PRINTER.padding(3).print("[valor]", value.getValue(),
						"[classe]", value.getValue().getClass().getName());
			}

			PRINTER.padding(2).print("USO");

			Iterator<ValueBox> useBoxes = statement.getUseBoxes().iterator();

			while (useBoxes.hasNext()) {
				boolean throwStatement = false;
				boolean syncStatement = false;
				boolean returnStatement = false;
				boolean equalityComparison = false;

				ValueBox valueBox = (ValueBox) useBoxes.next();
				Value value = valueBox.getValue();

				PRINTER.padding(3).print("[valor]\t", value, "[classe]",
						value.getClass().getName());

				String variableName = asLocalName(declaringClass, methodLocal,
						value);

				if (isInstanceField(value)) {
					variableName = asInstanceFieldName(declaringClass, value);
				}
				
				if (isThrow(statement)) {
					variableName = asThrowName(declaringClass, methodLocal,
							value);
					throwStatement = true;
				}

				if (isEnterMonitor(statement)) {
					variableName = asSyncronizedBlockName(declaringClass,
							methodLocal, value);
					syncStatement = true;
				}
				
				if (Expression.isEqual(value) || Expression.isNotEqual(value)) {
					equalityComparison = true;
				}

				if (isReturn(statement)) {
					returnStatement = true;
				}

				if (!returnStatement && !equalityComparison) {
					if (isLocal(value)
							|| isInstanceField(value)
							|| ((throwStatement || syncStatement) && Constant
									.isNull(value))) {
						if (NSH.isNullState(variableName)) {
							valueBox.addTag(RED_TAG);
							PRINTER.print("NPE NA LINHA",
									statement.getJavaSourceStartLineNumber());
							int javaSourceStartLineNumber = statement.getJavaSourceStartLineNumber();
							VMH.addVulnerableMethod(methodLocal+(javaSourceStartLineNumber>=0?"@"+javaSourceStartLineNumber:""));
						} else if (NSH.isUnknownState(variableName)) {
							valueBox.addTag(YELLOW_TAG);
						} else if (NSH.isNonNullState(variableName)) {
							valueBox.addTag(GREEN_TAG);
						}
					}
				}
			}

		}

		PRINTER.line().print();
	}
	
	private void addResolveLater(String variableName, String pendingVariable) {
		pendingResolveVariables.put(variableName, pendingVariable);
	}
	
	private void resolvesVariable(String variableName, NullStateEnum nullState) {
		if (pendingResolveVariables.containsKey(variableName)) {
			NSH.changeVariableNullState(pendingResolveVariables.get(variableName),
					nullState);
			pendingResolveVariables.remove(variableName);
		}
	}
		
	private Unit getJumpUnit(JGotoStmt jgoto) {
		Unit target = jgoto.getTarget();
		if (target instanceof JGotoStmt) {
			return getJumpUnit((JGotoStmt) target);
		}
		return target;
	}
	
}
