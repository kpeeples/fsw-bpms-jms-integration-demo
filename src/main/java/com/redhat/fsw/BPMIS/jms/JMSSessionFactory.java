package com.redhat.fsw.BPMIS.jms;

import com.redhat.fsw.BPMIS.process.ProcessContext;

public class JMSSessionFactory {

	public static final ProducerSession startProducerSession(ProcessContext context) {
		return ProducerSession.startProducerSession(context);
		
	}
	public static final ReceiverSession startReceiverSession(ProcessContext context) {
		return ReceiverSession.startReceiverSession(context);		
	}
}
