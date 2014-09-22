package com.redhat.fsw.BPMIS.jms;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jbpm.services.task.commands.TaskCommand;
import org.kie.services.client.serialization.JaxbSerializationProvider;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.fsw.BPMIS.process.ProcessContext;

public abstract class JMSSession {

	protected static final Logger logger = 
			LoggerFactory.getLogger(JMSSession.class);

	private ProcessContext context;
	private Queue queue;
	
	private Connection connection;
	private Session session;
	
	private boolean isActive = false;

	private JaxbSerializationProvider serializationProvider;

	private JaxbCommandsRequest current_command;

	private Context jndiContext;
	
	protected JMSSession(ProcessContext context) {
		this.context = context;
		this.queue = initQueue();	
		this.serializationProvider = new JaxbSerializationProvider();
		
	}
	
	abstract protected Queue initQueue();

	public Connection getConnection() {
		if(connection == null)
			setConnection(createConnection());
		
		return connection;
	}
	protected void setConnection(Connection connection) {
		this.connection = connection;
	}
	public Session getSession() throws JMSException {
		if(session == null)
			setSession(createSession());
		
		return session;
	}
	protected void setSession(Session session) {
		this.session = session;
	}
	public ProcessContext getContext() {
		return context;
	}
	protected void setContext(ProcessContext context) {
		this.context = context;
	}
	public Queue getQueue() {
		return queue;
	}
	protected void setQueue(Queue queue) {
		this.queue = queue;
	}
	protected JaxbSerializationProvider getSerializationProvider() {
		return serializationProvider;
	}
	protected void setSerializationProvider(
			JaxbSerializationProvider serializationProvider) {
		this.serializationProvider = serializationProvider;
	}
	protected void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public JaxbCommandsRequest getCurrentProcessCommand() {
		if(getCurrentProcessCommand() == null)
			throw new RuntimeException("You can't call add/send commands without setting a process-command.");
		
		return current_command;
	}
	public void setCurrentProcessCommand(JaxbCommandsRequest command) {
		this.current_command = command;
	}

	private Connection createConnection() {
		Connection connection = null;
		try {			
			ConnectionFactory factory = getConnectionFactory();
			
			String jmsUser = getContext().getUser().getName();
			String jmsPassword = getContext().getUser().getPassword();
			
			if (jmsUser != null) {
				connection = factory.createConnection(jmsUser, jmsPassword);
			} else {
				connection = factory.createConnection();
			}
		} catch (JMSException jmse) {
			throw new RuntimeException(
					"Unable to setup a JMS connection.", jmse);
		}
		
		return connection;
	}
	
	private Session createSession() throws JMSException {			
		return getConnection().createSession(false,Session.AUTO_ACKNOWLEDGE);			
	}
	
	
	protected Queue getRequestQueue() {
		return getJNDIQueue("java:/jms/queue/KIE.SESSION");
	}

	protected Queue getResponseQueue() {
		return getJNDIQueue("java:/jms/queue/KIE.RESPONSE");
	}

	
	private ConnectionFactory getConnectionFactory() {
		return lookupObject("jms/RemoteConnectionFactory", ConnectionFactory.class);		
	}
	
	private Queue getJNDIQueue(String name) {
		return lookupObject(name, Queue.class);
	}
	
	private <T> T lookupObject(String contextId,Class<T> object) {
		// Get JNDI context from server
		Context context = getRemoteJbossInitialContext();
		
		try {
			return (T) context.lookup(contextId);
		} catch (NamingException ne) {
			throw new RuntimeException(
					"Unable to lookup send or response queue", ne);
		}
	}
	
	public Context getRemoteJbossInitialContext() {
		if(getJNDIContext() == null) {
			Properties initialProps = new Properties();
			
			initialProps.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			
			String jbossServerHostName = getContext().getEndpoint().getHost();
					
			initialProps.setProperty(InitialContext.PROVIDER_URL, "remote://"+ jbossServerHostName + ":4547");
			
			initialProps.setProperty(InitialContext.SECURITY_PRINCIPAL, getContext().getUser().getName());
			initialProps.setProperty(InitialContext.SECURITY_CREDENTIALS, getContext().getUser().getPassword());
					
			try {
				setJNDIContext(new InitialContext(initialProps));
				
				return getJNDIContext();
			} catch (NamingException e) {	
				throw new RuntimeException("Unable to create "
						+ InitialContext.class.getSimpleName(), e);
			}			
		}
		else return getJNDIContext();
	}
	
	private Context getJNDIContext() {
		return this.jndiContext;
	}

	private void setJNDIContext(Context context) {
		this.jndiContext = context;
	}

	public void close() {
		try {
			getConnection().close();
			getSession().close();
		} catch (JMSException jmse) {
			logger.warn("Unable to close connection or session!", jmse);
		}
	}
	public boolean isActive() {
		return isActive;
	}
	
	public void setProcessCommand(JaxbCommandsRequest jaxbCommandsRequest) {
		setCurrentProcessCommand(jaxbCommandsRequest);
	}
	
	protected void addTaskCommand(TaskCommand<?> taskCommand) {		
		getCurrentProcessCommand().getCommands().add(taskCommand);
	}

}
