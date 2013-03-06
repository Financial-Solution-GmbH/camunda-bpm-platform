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
package org.camunda.bpm.container.impl.tomcat;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.camunda.bpm.container.RuntimeContainerDelegate;
import org.camunda.bpm.container.impl.jmx.JmxRuntimeContainerDelegate;
import org.camunda.bpm.container.impl.jmx.deployment.PlatformXmlStartProcessEnginesStep;
import org.camunda.bpm.container.impl.jmx.deployment.StopProcessApplicationsStep;
import org.camunda.bpm.container.impl.jmx.deployment.StopProcessEnginesStep;
import org.camunda.bpm.container.impl.tomcat.deployment.TomcatAttachments;
import org.camunda.bpm.container.impl.tomcat.deployment.TomcatCreateJndiBindingsStep;
import org.camunda.bpm.container.impl.tomcat.deployment.TomcatParseBpmPlatformXmlStep;
import org.camunda.bpm.engine.ProcessEngine;

/**
 * <p>Apache Tomcat server listener responsible for deploying the bpm platform.</p> 
 * 
 * @author Daniel Meyer
 *
 */
public class CamundaEngineServerListener implements LifecycleListener {
  
  private final static Logger LOGGER = Logger.getLogger(CamundaEngineServerListener.class.getName());
  
  protected ProcessEngine processEngine;

  protected JmxRuntimeContainerDelegate containerDelegate;
    
  public void lifecycleEvent(LifecycleEvent event) {

    if (Lifecycle.START_EVENT.equals(event.getType())) {
      
      // the Apache Tomcat integration uses the Jmx Container for managing process engines and applications.
      containerDelegate = (JmxRuntimeContainerDelegate) RuntimeContainerDelegate.INSTANCE.get();
            
      deployBpmPlatform(event);
      
    } else if (Lifecycle.STOP_EVENT.equals(event.getType())) {
      
      undeployBpmPlatform(event);
      
    }
   
  }

  protected void deployBpmPlatform(LifecycleEvent event) {
    
    final StandardServer server = (StandardServer) event.getSource();   
    
    containerDelegate.getServiceContainer().createDeploymentOperation("deploy BPM platform")
      .addAttachment(TomcatAttachments.SERVER, server)
      .addStep(new TomcatParseBpmPlatformXmlStep())
      .addStep(new PlatformXmlStartProcessEnginesStep())
      .addStep(new TomcatCreateJndiBindingsStep())      
      .execute();
    
    LOGGER.log(Level.INFO, "camunda BPM platform" + " sucessfully started on "+server.getServerInfo()+".");
    
  }
  

  protected void undeployBpmPlatform(LifecycleEvent event) {
    
    final StandardServer server = (StandardServer) event.getSource();   
    
    containerDelegate.getServiceContainer().createUndeploymentOperation("undeploy BPM platform")
      .addAttachment(TomcatAttachments.SERVER, server)
      .addStep(new StopProcessApplicationsStep())
      .addStep(new StopProcessEnginesStep())
      .execute();
    
    LOGGER.log(Level.INFO, "camunda BPM platform stopped.");
    
  }

}
