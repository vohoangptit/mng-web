package com.nera.nms.constants;

import java.util.HashSet;
import java.util.Set;

public final class JobPlanStatus {
    
	private JobPlanStatus(){}

	public static final String PENDING = "Pending for Acceptance";
	public static final String REJECTED = "Rejected";
	public static final String ACCEPTED = "Accepted";
	public static final String EXECUTING = "Executing";
	public static final String FINISHED_REJECTED="Finished (Rejected)";
	public static final String FINISHED_APPROVED = "Finished (Approved)";
	public static final String FINISHED_STOPPED = "Finished (Stopped)";
	public static final String FINISHED_FAILED = "Finished (Failed)";
	private static final Set<String> JOB_PLANNING_STATUS = new HashSet<>();
    static {
		JOB_PLANNING_STATUS.add(PENDING);
		JOB_PLANNING_STATUS.add(REJECTED);
		JOB_PLANNING_STATUS.add(ACCEPTED);
		JOB_PLANNING_STATUS.add(EXECUTING);
		JOB_PLANNING_STATUS.add(FINISHED_REJECTED);
		JOB_PLANNING_STATUS.add(FINISHED_APPROVED);
		JOB_PLANNING_STATUS.add(FINISHED_STOPPED);
		JOB_PLANNING_STATUS.add(FINISHED_FAILED);
    }

	public static Set<String> getJobPlanningStatus() {
		return JOB_PLANNING_STATUS;
	}
}
