/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.syntax.Types;
import org.spockframework.util.CollectionUtil;
import org.spockframework.util.Identifiers;
import org.spockframework.util.Nullable;
import org.spockframework.util.ObjectUtil;

public class DefaultBuiltInMethodCall implements IBuiltInMethodCall {
  private final String methodName;
  private final Expression inferredName;
  private final Expression inferredType;
  private final MethodCallExpression methodCallExpr;
  @Nullable
  private final BinaryExpression binaryExpr;
  @Nullable
  private final ClosureExpression closureExpr;

  public DefaultBuiltInMethodCall(String methodName, Expression inferredName, Expression inferredType,
      MethodCallExpression methodCallExpr, @Nullable BinaryExpression binaryExpr, @Nullable ClosureExpression closureExpr) {
    this.methodName = methodName;
    this.inferredName = inferredName;
    this.inferredType = inferredType;
    this.binaryExpr = binaryExpr;
    this.methodCallExpr = methodCallExpr;
    this.closureExpr = closureExpr;
  }

  public boolean isMethodName(String name) {
    return name.equals(methodName);
  }

  public boolean isOneOfMethodNames(Collection<String> names) {
    return names.contains(methodName);
  }

  public boolean isExceptionCondition() {
    return isOneOfMethodNames(Identifiers.EXCEPTION_CONDITION_METHODS);
  }

  public boolean isThrownCall() {
    return isMethodName(Identifiers.THROWN);
  }

  public boolean isOldCall() {
    return isMethodName(Identifiers.OLD);
  }

  public boolean isInteractionCall() {
    return isMethodName(Identifiers.INTERACTION);
  }

  public boolean isWithCall() {
    return isMethodName(Identifiers.WITH);
  }

  public boolean isTestDouble() {
    return isOneOfMethodNames(Identifiers.TEST_DOUBLE_METHODS);
  }

  public boolean isExceptionCondition(MethodCallExpression expr) {
    return expr == methodCallExpr && isExceptionCondition();
  }

  public boolean isThrownCall(MethodCallExpression expr) {
    return expr == methodCallExpr && isThrownCall();
  }

  public boolean isOldCall(MethodCallExpression expr) {
    return expr == methodCallExpr && isOldCall();
  }

  public boolean isInteractionCall(MethodCallExpression expr) {
    return expr == methodCallExpr && isInteractionCall();
  }

  public boolean isWithCall(MethodCallExpression expr) {
    return expr == methodCallExpr && isWithCall();
  }

  public boolean isTestDouble(MethodCallExpression expr) {
    return expr == methodCallExpr && isTestDouble();
  }

  public boolean isMatch(Statement stat) {
    ExpressionStatement exprStat = ObjectUtil.asInstance(stat, ExpressionStatement.class);
    if (exprStat == null) return false;
    Expression expr = exprStat.getExpression();
    return expr == binaryExpr || expr == methodCallExpr;
  }

  public boolean isMatch(ClosureExpression expr) {
    return expr == closureExpr;
  }

  @Nullable
  public ClosureExpression getClosureExpr() {
    return closureExpr;
  }

  public void expand() {
    List<Expression> args = new ArrayList<Expression>();
    args.add(inferredName);
    args.add(inferredType);
    args.addAll(AstUtil.getArgumentList(methodCallExpr));

    ArgumentListExpression argsExpr = new ArgumentListExpression(args);
    AstUtil.copySourcePosition(methodCallExpr.getArguments(), argsExpr);
    methodCallExpr.setArguments(argsExpr);
    methodCallExpr.setMethod(new ConstantExpression(methodName + "Impl"));
  }

  public static DefaultBuiltInMethodCall parse(MethodCallExpression methodCallExpr, @Nullable BinaryExpression binaryExpr) {
    if (!AstUtil.isThisOrSuperExpression(methodCallExpr.getObjectExpression())) return null;

    String methodName = methodCallExpr.getMethodAsString();
    if (!Identifiers.BUILT_IN_METHODS.contains(methodName)) return null;

    Expression inferredName;
    Expression inferredType;
    if (binaryExpr != null && binaryExpr.getOperation().getType() == Types.ASSIGN && binaryExpr.getRightExpression() == methodCallExpr) {
      inferredName = AstUtil.getVariableName(binaryExpr);
      inferredType = AstUtil.getVariableType(binaryExpr);
    } else {
      binaryExpr = null; // not part of this built-in method call
      inferredName = ConstantExpression.NULL;
      inferredType = ConstantExpression.NULL;
    }

    // TODO: check that no explicit closure parameter, think about changing parameter name from 'it' to sth. unique
    ClosureExpression closureExpr = null;
    List<Expression> arguments = AstUtil.getArgumentList(methodCallExpr);
    if (!arguments.isEmpty()) {
      Expression lastArg = CollectionUtil.getLastElement(arguments);
      if (lastArg instanceof ClosureExpression) {
        closureExpr = (ClosureExpression) lastArg;
      }
    }

    return new DefaultBuiltInMethodCall(methodName, inferredName, inferredType, methodCallExpr, binaryExpr, closureExpr);
  }

  public String toString() {
    return String.format("method name: %s\ninferred name: %s\ninferred type: %s\nmethod call:%s\nclosure: %s\n",
        methodName, inferredName, inferredType, methodCallExpr, closureExpr);
  }
}
