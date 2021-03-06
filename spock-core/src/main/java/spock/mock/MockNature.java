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

package spock.mock;

public enum MockNature {
  STUB(false, true, EmptyOrStubResponder.INSTANCE),
  MOCK(true, true, ZeroOrNullResponder.INSTANCE),
  SPY(true, false, CallRealMethodResponder.INSTANCE);

  private final boolean verified;
  private final boolean useObjenesis;

  private final IMockInvocationResponder responder;

  MockNature(boolean verified, boolean useObjenesis, IMockInvocationResponder responder) {
    this.verified = verified;
    this.useObjenesis = useObjenesis;
    this.responder = responder;
  }

  boolean isVerified() {
    return verified;
  }

  boolean isUseObjenesis() {
    return useObjenesis;
  }

  IMockInvocationResponder getResponder() {
    return responder;
  }
}
