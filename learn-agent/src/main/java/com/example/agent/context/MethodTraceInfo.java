package com.example.agent.context;

public class MethodTraceInfo {
    private String className;
    private String methodName;
    private String methodDescriptor;
    private long startTime;
    private long endTime;
    private MethodTraceInfo parent;
    private TaskContext taskContext;
    private Throwable exception;

    public MethodTraceInfo(String className, String methodName, String methodDescriptor, long startTime) {
        this.className = className;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
        this.startTime = startTime;
    }

    public String getFullMethodSignature() {
        return className + "." + methodName + methodDescriptor;
    }

    // Getters and setters
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setParent(MethodTraceInfo parent) {
        this.parent = parent;
    }

    public void setTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public MethodTraceInfo getParent() {
        return parent;
    }

    public TaskContext getTaskContext() {
        return taskContext;
    }

    public Throwable getException() {
        return exception;
    }

    public long getDuration() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return String.format("%s.%s%s [duration=%dms]", 
            className, methodName, methodDescriptor, getDuration());
    }
}
