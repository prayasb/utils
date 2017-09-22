package com.prayasb.execution.timer;

import java.util.LinkedList;
import java.util.List;

public class Timer {
	private static final String NL = System.lineSeparator();
	
	private List<TimerTask> tasks = new LinkedList<>();
			
	public TimerTask getLastTimerTask() {
		return tasks.isEmpty() ? null : tasks.get(tasks.size() - 1);
	}
	
	public void start(String message) {
		TimerTask lastTimerTask = getLastTimerTask();
		if (lastTimerTask != null && !lastTimerTask.isDone()) {
			TimerTask newTimerTask = new TimerTask(message, lastTimerTask);
			lastTimerTask.append(newTimerTask);
			newTimerTask.start();
		} else {
			TimerTask newTimerTask = new TimerTask(message, null);
			tasks.add(newTimerTask);
			newTimerTask.start();
		}
	}
	
	public void end() {
		TimerTask lastTimerTask = getLastTimerTask();
		if (lastTimerTask != null && !lastTimerTask.isDone()) {
			lastTimerTask.end();
		} else {
			throw new RuntimeException("End called on Timer without a corresponding start");
		}
	}
	
	public String generateReport() {
		StringBuilder report = new StringBuilder(NL);
		for (TimerTask task : tasks) {
			report.append(task.getReport());
		}
		
		return report.toString();
	}
	
	
	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.start("Main title of this execution");
		timer.start("Node level one");
		timer.start("Node level one.one");
		timer.start("Node level one.one.one");
		timer.end(); // 1.1.1
		timer.start("Node level one.one.two");
		timer.end(); //1.1.2
		timer.end(); // 1.1
		timer.start("Node level one.two");
		timer.end(); // 1.2
		timer.end(); // 1
		timer.end(); // main
		
		System.out.println("Report:");
		System.out.println(timer.generateReport());
	}
}
