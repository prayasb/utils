package com.prayasb.execution.timer;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

public class TimerTask {
	private static final String NL = System.lineSeparator();
	private static final String INDENT = "--";
	private Long start;
	private Long end;
	private String message;
	private TimerTask parent;
	private List<TimerTask> children = new LinkedList<>();
	
	public TimerTask(String message, TimerTask parent) {
		this.message = message;
		this.parent = parent;
	}
	
	public void start() {
		this.start = System.currentTimeMillis();
	}
	
	public void append(TimerTask child) {
		TimerTask lastTimerTask = getLastTimerTask();
		if (lastTimerTask != null && !lastTimerTask.isDone()) {
			lastTimerTask.append(child);
		} else {
			this.children.add(child);
		}
	}
	
	public TimerTask getLastTimerTask() {
		return children.isEmpty() ? null : children.get(children.size() - 1);
	}
	
	public void end() {
		TimerTask lastTimerTask = getLastTimerTask();
		if (lastTimerTask != null && !lastTimerTask.isDone()) {
			lastTimerTask.end();
		} else if (!isDone()) {
			this.end = System.currentTimeMillis();
		} else {
			throw new RuntimeException("End called on timer task without a corresponding start");
		}
	}
	
	public boolean isDone() {
		return end != null;
	}
	
	public Long getStart() {
		return start;
	}
	
	public Long getEnd() {
		return end;
	}
	
	public String getMessage() {
		return message;
	}
	
	public TimerTask getParent() {
		return parent;
	}
	
	public List<TimerTask> getChildren() {
		return children;
	}
	
	public String getMessageWithDuration() {
		return String.format("%s (took %s)", message, getDuration(start, end));
	}
	
	public String getDuration(long start, long end) {
		return (end - start) + " millisecond(s)";
	}
	
	public String getReport() {
		return getReport(0);
	}
	
	public String getReport(int indentLevel) {
		StringBuilder report = new StringBuilder(NL);
		report.append(Strings.repeat(INDENT, indentLevel)).append(getMessage());
		for (TimerTask child : children) {
			report.append(child.getReport(indentLevel + 1));
		}
		report.append(NL).append(Strings.repeat(INDENT, indentLevel)).append(getMessageWithDuration());
		
		return report.toString();
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(TimerTask.class)
			.add("message", message)
			.add("start", start)
			.add("end", end)
			.toString();
	}
}
