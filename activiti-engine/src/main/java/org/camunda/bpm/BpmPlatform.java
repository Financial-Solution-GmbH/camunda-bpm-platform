/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm;

import org.camunda.bpm.container.RuntimeContainerDelegate;
import org.camunda.bpm.engine.ProcessEngine;


/**
 * <p>Provides access to the camunda BPM platform services.</p>
 * 
 * @author Daniel Meyer
 *
 */
public final class BpmPlatform {
  
  public static ProcessEngineService getProcessEngineService() {
    return RuntimeContainerDelegate.INSTANCE.get().getProcessEngineService();
  }
  
  public static ProcessApplicationService getProcessApplicationService() {
    return RuntimeContainerDelegate.INSTANCE.get().getProcessApplicationService();
  }
  
  public static ProcessEngine getDefaultProcessEngine() {
    return getProcessEngineService().getDefaultProcessEngine();
  }
  
}