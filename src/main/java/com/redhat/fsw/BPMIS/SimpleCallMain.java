package com.redhat.fsw.BPMIS;

import javax.jms.JMSException;

import com.redhat.fsw.BPMIS.process.ProcessContext;
import com.redhat.fsw.BPMIS.user.BPMUser;

public class SimpleCallMain {

	public static void main(String[] args) {
		try {
			ProcessContext proc = new ProcessContext();
			proc.setDeploymentId("customer:evaluation");
			proc.setProcessId("customer.evaluation");
			BPMUser user = new BPMUser();
			user.setName("kai");
			user.setPassword("change12_me!");
			proc.setUser(user);
			
			BPMSession.create(proc)
				.startProcess()
				.addGetTaskAssignedToContextUser("de_DE")
				.send();
			
			System.out.println(proc.getCorrelationId());
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
		
	}

}
