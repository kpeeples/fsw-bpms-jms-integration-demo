package com.redhat.fsw.BPMIS.transformator;

import org.switchyard.annotations.Transformer;

public final class OutTransformer {

	@Transformer(to = "{http://redhat.com/BPMIS}getTasksResponse")
	public String transformStringToGetTasksResponse(String from) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transformer(to = "{http://redhat.com/BPMIS}startProcessResponse")
	public String transformStringToStartProcessResponse(String from) {
		// TODO Auto-generated method stub
		return null;
	}

}
