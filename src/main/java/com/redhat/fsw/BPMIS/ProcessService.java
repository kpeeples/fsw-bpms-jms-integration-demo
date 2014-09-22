package com.redhat.fsw.BPMIS;

import com.redhat.fsw.BPMIS.process.ProcessContext;

public interface ProcessService {

	String startProcess(ProcessContext context);
	String getTasks(ProcessContext context);
}
