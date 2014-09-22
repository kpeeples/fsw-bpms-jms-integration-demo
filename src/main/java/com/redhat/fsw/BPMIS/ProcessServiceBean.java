package com.redhat.fsw.BPMIS;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.services.client.api.RemoteJmsRuntimeEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.component.bean.Service;

import com.redhat.fsw.BPMIS.process.ProcessContext;

@Service(ProcessService.class)
public class ProcessServiceBean implements ProcessService {

	protected static final Logger logger = 
			LoggerFactory.getLogger(ProcessServiceBean.class);
	
		
	/**
	 * returns ProcessContextId
	 */
	public String startProcess(ProcessContext processContext) {
		
		try {
			BPMSession.create(processContext)
				.startProcess()
				.addGetTaskAssignedToContextUser("de_DE")
				.send();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
		
		return processContext.getCorrelationId();
		
		//return ""+startProcessAndTaskViaJmsRemoteJavaAPI(processContext.getEndpoint().getHost(),processContext.getDeploymentId(),processContext.getUser().getName(),processContext.getUser().getPassword());
	}

	/**
	 * returns The queue content
	 */
	public String getTasks(ProcessContext processContext) {
		String queueContent;
		try {
			queueContent = BPMSession.create(processContext)
				.receiveQueueContent();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
		
		return queueContent;
	}	
}
