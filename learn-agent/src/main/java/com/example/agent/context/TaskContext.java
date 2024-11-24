package com.example.agent.context;

import java.util.UUID;

public class TaskContext {
    private final String taskId;
    private final String parentThreadId;
    private final String submissionPoint;
    
    public TaskContext(String parentThreadId, String submissionPoint) {
        this.taskId = UUID.randomUUID().toString();
        this.parentThreadId = parentThreadId;
        this.submissionPoint = submissionPoint;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public String getParentThreadId() {
        return parentThreadId;
    }
    
    public String getSubmissionPoint() {
        return submissionPoint;
    }
}
