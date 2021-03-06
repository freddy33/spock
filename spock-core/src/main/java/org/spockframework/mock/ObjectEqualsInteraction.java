/*
 * Copyright 2009 the original author or authors.
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
package org.spockframework.mock;

public class ObjectEqualsInteraction extends DefaultInteraction {
  public static final ObjectEqualsInteraction INSTANCE = new ObjectEqualsInteraction();

  private ObjectEqualsInteraction() {}
  
  public String getText() {
    return "Object.equals() interaction";
  }

  public boolean matches(IMockInvocation invocation) {
    IMockMethod method = invocation.getMethod();

    return method.getName().equals("equals")
        && method.getParameterTypes().size() == 1
        && method.getParameterTypes().get(0) == Object.class;
  }

  public Object accept(IMockInvocation invocation) {
    return invocation.getMockObject().getInstance() == invocation.getArguments().get(0);
  }
}
