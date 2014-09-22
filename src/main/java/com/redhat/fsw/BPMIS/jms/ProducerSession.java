package com.redhat.fsw.BPMIS.jms;

import java.util.Collection;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;

import org.jbpm.services.task.commands.GetTaskAssignedAsPotentialOwnerCommand;
import org.kie.services.client.api.command.exception.RemoteCommunicationException;
import org.kie.services.client.serialization.JaxbSerializationProvider;
import org.kie.services.client.serialization.SerializationConstants;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;

import com.redhat.fsw.BPMIS.process.ProcessContext;

public class ProducerSession extends JMSSession {

	MessageProducer producer;
	
	private MessageProducer getProducer() {
		if(producer == null)
			setProducer(createProducer());
		return producer;
	}
	private void setProducer(MessageProducer producer) {
		this.producer = producer;
	}
	private MessageProducer createProducer() {
		try {
			MessageProducer producer = getSession().createProducer(getQueue());

			getConnection().start();

			return producer;			
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
		
	}

	
	public ProducerSession(ProcessContext context) {
		super(context);
	}

	public static ProducerSession startProducerSession(ProcessContext context) {
		return new ProducerSession(context);
	}

	public void send(JaxbCommandsRequest cmd) throws JMSException {
		BytesMessage msg = createMessage(cmd);
		
		getProducer().send(msg);
	}
	public void send() throws JMSException {
		send(getCurrentProcessCommand());
	}
	
	private BytesMessage createMessage(JaxbCommandsRequest cmd) {
		// if necessary, add user-created classes here:
		// xmlSerializer.addJaxbClasses(MyType.class,
		// AnotherJaxbAnnotatedType.class);

		// Create msg
		BytesMessage msg;
		try {
			msg = getSession().createBytesMessage();

			// set properties
			msg.setJMSCorrelationID(getContext().getCorrelationId());
			msg.setIntProperty(
					SerializationConstants.SERIALIZATION_TYPE_PROPERTY_NAME,
					JaxbSerializationProvider.JMS_SERIALIZATION_TYPE);
			Collection<Class<?>> extraJaxbClasses = getSerializationProvider()
					.getExtraJaxbClasses();
			if (!extraJaxbClasses.isEmpty()) {
				String extraJaxbClassesPropertyValue = JaxbSerializationProvider
						.classSetToCommaSeperatedString(extraJaxbClasses);
				msg.setStringProperty(
						SerializationConstants.EXTRA_JAXB_CLASSES_PROPERTY_NAME,
						extraJaxbClassesPropertyValue);
				msg.setStringProperty(
						SerializationConstants.DEPLOYMENT_ID_PROPERTY_NAME,
						getContext().getDeploymentId());
			}

			// serialize request
			String xmlStr = getSerializationProvider().serialize(cmd);
			msg.writeUTF(xmlStr);
		} catch (JMSException jmse) {
			throw new RemoteCommunicationException(
					"Unable to create and fill a JMS message.", jmse);
		} 
		return msg;
	}

	@Override
	protected Queue initQueue() {
		return getRequestQueue();
	}
	
	public ProducerSession addGetTaskAssignedAsPotentialOwner(String userName, String locale) {
		addTaskCommand(new GetTaskAssignedAsPotentialOwnerCommand(userName,locale));
		
		return this;
	}
	public ProducerSession addGetTaskAssignedToContextUser(String locale) {
		addTaskCommand(new GetTaskAssignedAsPotentialOwnerCommand(getContext().getUser().getName(),locale));
		
		return this;
	}

	
	
}
