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

import org.spockframework.mock.IMockObject;
import org.spockframework.mock.IMockObjectProvider;

import spock.lang.Beta;

@Beta
public class MockDetector {
  public static boolean isMock(Object object) {
    return object instanceof IMockObjectProvider;
  }

  public static IMockObject asMock(Object object) {
    if (!isMock(object)) {
      throw new IllegalArgumentException("Not a mock object: " + object.toString());
    }

    IMockObjectProvider provider = (IMockObjectProvider) object;
    return provider.$spock_get();
  }
}
