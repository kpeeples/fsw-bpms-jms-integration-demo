package com.redhat.fsw.BPMIS;

import javax.jms.JMSException;
import javax.naming.InitialContext;

import org.drools.core.command.runtime.process.StartProcessCommand;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.services.client.api.RemoteJmsRuntimeEngineFactory;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;
import org.kie.services.shared.AcceptedCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.fsw.BPMIS.jms.JMSSessionFactory;
import com.redhat.fsw.BPMIS.jms.ProducerSession;
import com.redhat.fsw.BPMIS.jms.ReceiverSession;
import com.redhat.fsw.BPMIS.process.ProcessContext;

public class BPMSession {

	protected static final Logger logger = 
			LoggerFactory.getLogger(BPMSession.class);

	ProcessContext context;
	
	private ProcessContext getContext() {
		return context;
	}
	public BPMSession(ProcessContext context) {
		this.context = context;
	}
	
	public static BPMSession create(ProcessContext context) {
		return new BPMSession(context);
	}
	
	public ProducerSession startProcess() {
		 
		/* uncomment this for more complexity / freedom of integration / the real stuff
		ProducerSession session = JMSSessionFactory.startProducerSession(getContext());
		
		StartProcessCommand startProcessCMD = null;
		for(Class c : AcceptedCommands.getSet().toArray(new Class[0])) {
			if(c.getName().equals("org.drools.core.command.runtime.process.StartProcessCommand")) {
				logger.info("Found the ...!");
				try {
					startProcessCMD = (StartProcessCommand) c.newInstance();
					startProcessCMD.setProcessId(getContext().getProcessId());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		if(startProcessCMD == null)
			throw new RuntimeException("Not found the ...!");
		
		session.setProcessCommand(new JaxbCommandsRequest(getContext().getDeploymentId(), startProcessCMD));
		
		return session;
		*/
		
		// workraound / testing code! uncomment the above
		logger.info(""+startProcessAndTaskViaJmsRemoteJavaAPI(getContext().getEndpoint().getHost(),
				getContext().getDeploymentId(),
				getContext().getUser().getName(),
				getContext().getUser().getPassword()));
		return null;
		
	}
		
	
	public String receiveQueueContent() throws JMSException {
		ReceiverSession session = JMSSessionFactory.startReceiverSession(getContext());

		return session.receive();		
	}
	
	public long startProcessAndTaskViaJmsRemoteJavaAPI(String serverHostName, String deploymentId, String user, String password) {
		ProducerSession session = JMSSessionFactory.startProducerSession(getContext());
		
		// Setup remote JMS runtime engine factory
		InitialContext remoteInitialContext = 
			(InitialContext) session.getRemoteJbossInitialContext();
		
		int maxTimeoutSecs = 5;
		RemoteJmsRuntimeEngineFactory remoteJmsFactory 
			= new RemoteJmsRuntimeEngineFactory(deploymentId, remoteInitialContext, user, password, maxTimeoutSecs);

		// Interface with JMS api
		RuntimeEngine engine = remoteJmsFactory.newRuntimeEngine();
		KieSession ksession = engine.getKieSession();
		ProcessInstance processInstance = ksession.startProcess("com.burns.reactor.maintenance.cycle");
		return processInstance.getId();
		/*
		long procId = processInstance.getId();
		TaskService taskService = engine.getTaskService();
		List<Long> tasks = taskService.getTasksByProcessInstanceId(procId);
		taskService.start(tasks.get(0), user);
		*/
		}
	
}
