/*
 * Copyright 2010 the original author or authors.
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

import org.spockframework.lang.Wildcard;
import org.spockframework.runtime.InvalidSpecException;
import org.spockframework.util.Nullable;

import spock.mock.IMockInvocationResponder;

public class MockObject implements IMockObject {
  private final String name;
  private final Class<?> type;
  private final Object instance;
  private final boolean verified;
  private final boolean global;
  private final IMockInvocationResponder responder;

  public MockObject(@Nullable String name, Class<?> type, Object instance,
      boolean verified, boolean global, IMockInvocationResponder responder) {
    this.name = name;
    this.type = type;
    this.instance = instance;
    this.verified = verified;
    this.global = global;
    this.responder = responder;
  }

  @Nullable
  public String getName() {
    return name;
  }

  @Nullable
  public Class<?> getType() {
    return type;
  }

  public Object getInstance() {
    return instance;
  }

  public boolean isVerified() {
    return verified;
  }

  public IMockInvocationResponder getResponder() {
    return responder;
  }

  public boolean matches(Object target, IMockInteraction interaction) {
    if (target instanceof Wildcard) return verified || !interaction.isRequired();

    boolean match = global ? instance.getClass() == target.getClass() : instance == target;
    if (match) {
      checkRequiredInteractionAllowed(interaction);
    }
    return match;
  }

  private void checkRequiredInteractionAllowed(IMockInteraction interaction) {
    if (!verified && interaction.isRequired()) {
      String mockName = name != null ? name : "unnamed";
      throw new InvalidSpecException("Stub '%s' matches the following required interaction:" +
          "\n\n%s\n\nRemove the cardinality (e.g. '1 *'), or turn the stub into a mock.\n").withArgs(mockName, interaction);
    }
  }
}
