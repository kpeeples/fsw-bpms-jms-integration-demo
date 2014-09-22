package com.redhat.fsw.BPMIS.process;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.switchyard.serial.graph.AccessType;
import org.switchyard.serial.graph.Strategy;

import com.redhat.fsw.BPMIS.user.BPMUser;
/**
 * <p>Java class for processContext complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="processContext">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="correlationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deploymentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processInstanceIds" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="user" type="{http://redhat.com/BPMIS}bpmUser" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "processContext", propOrder = {
    "endPoint",
    "correlationId",
    "deploymentId",
    "processId",
    "processInstanceIds",
    "user"
})
@XmlRootElement(name="processContext")
public class ProcessContext implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="user",required = true)
	BPMUser user;
	
    @XmlElement(name="endPoint",nillable = true)
	URL endPoint;
	
	@XmlElement(name="deploymentId",required = true)
	String deploymentId;
	@XmlElement(name="processId",required = true)
	String processId;

    @XmlElement(name="processInstanceIds",nillable = true)
	List<Long> processInstanceIds = new ArrayList<Long>(5);
	
    @XmlElement(name="correlationId",nillable = true)
	String correlationId;

	public ProcessContext() {
		try {
			setEndpoint("http://localhost:8180/jbpm-console/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private String generateCorellationId() {
	    return UUID.randomUUID().toString();
	}
	
	public BPMUser getUser() {
		return user;
	}
	public void setUser(BPMUser user) {
		this.user = user;
	}
	
	public void setEndpoint(String endpointURL) throws MalformedURLException {
		endPoint = new URL(endpointURL);
	}
	public URL getEndpoint() {
		return endPoint;
	}
	
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public List<Long> getProcessInstanceIds() {
		return processInstanceIds;
	}
	public void setProcessInstanceIds(List<Long> processInstanceIds) {
		this.processInstanceIds = processInstanceIds;
	}	
	public String getCorrelationId() {
		if(correlationId == null) 
			setCorrelationId(generateCorellationId());
		
		return correlationId;
	}
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
}
