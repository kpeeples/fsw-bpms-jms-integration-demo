package com.redhat.fsw.BPMIS.jms;

import java.util.Collection;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.commons.lang.SerializationException;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;
import org.kie.services.client.api.command.exception.RemoteCommunicationException;
import org.kie.services.client.serialization.JaxbSerializationProvider;
import org.kie.services.client.serialization.SerializationConstants;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandResponse;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsResponse;
import org.kie.services.client.serialization.jaxb.rest.JaxbExceptionResponse;

import com.redhat.fsw.BPMIS.process.ProcessContext;

public class ReceiverSession extends JMSSession {
	
	private MessageConsumer consumer;
	private long timeout = 5;

	public ReceiverSession(ProcessContext context) {
		super(context);
	}
	
	public MessageConsumer getConsumer() {
		if(consumer == null)
			setConsumer(createConsumer());
		return consumer;
	}
	public void setConsumer(MessageConsumer consumer) {
		this.consumer = consumer;
	}
	private MessageConsumer createConsumer() {
		String selector = "JMSCorrelationID = '" + getContext().getCorrelationId() + "'";

		try {
			MessageConsumer consumer = getSession().createConsumer(getQueue(), selector);
			getConnection().start();
			return consumer;
			
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public static ReceiverSession startReceiverSession(ProcessContext context) {
		return new ReceiverSession(context);
	}

	public String receive() throws JMSException {
		return receiveMessages();
	}
	

	private String receiveMessages() {
		Message response;
		JaxbCommandsResponse cmdResponse = null;
				
		try {
			response = getConsumer().receive(getTimeout());
		} catch (JMSException jmse) {
			throw new RemoteCommunicationException(
					"Unable to receive or retrieve the JMS response.", jmse);
		}

		if (response == null) {
			logger.warn("Response is empty, leaving");
			return null;
		}
		// extract response
		assert response != null : "Response is empty.";

		String xmlStr;
		try {
			return ((BytesMessage) response).readUTF();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		/* Deserialization might happen here:
		 *  
		try {
			cmdResponse = (JaxbCommandsResponse) getSerializationProvider().deserialize(xmlStr);
		} catch (JMSException jmse) {
			throw new RemoteCommunicationException("Unable to extract "
					+ JaxbCommandsResponse.class.getSimpleName()
					+ " instance from JMS response.", jmse);
		} catch (SerializationException se) {
			throw new RemoteCommunicationException("Unable to extract "
					+ JaxbCommandsResponse.class.getSimpleName()
					+ " instance from JMS response.", se.getCause());
		}
		assert cmdResponse != null : "Jaxb Cmd Response was null!";
		
		int oompaProcessingResultIndex = 0;
		int loompaMonitoringResultIndex = 1;
		
	    ProcessInstance oompaProcInst = null;
	    List<TaskSummary> tasks = null;
	    for (JaxbCommandResponse<?> responseObj : cmdResponse.getResponses()) {                                                                  
	      if (responseObj instanceof JaxbExceptionResponse) {
	        // something went wrong on the server side
	        JaxbExceptionResponse exceptionResponse = (JaxbExceptionResponse) responseObj;
	        throw new RuntimeException(exceptionResponse.getMessage());
	      }

	      if (responseObj.getIndex() == oompaProcessingResultIndex) {                                                                            
	        oompaProcInst = (ProcessInstance) responseObj.getResult();                                                                           
	      } else if (responseObj.getIndex() == loompaMonitoringResultIndex) {                                                                    
	        tasks = (List<TaskSummary>) responseObj.getResult();                                                                         
	      }
	    }	
	    */	
		
	}

	@Override
	protected Queue initQueue() {
		return getResponseQueue();
	}
	
}
