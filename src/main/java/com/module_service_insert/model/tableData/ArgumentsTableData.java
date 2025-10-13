package com.module_service_insert.model.tableData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Trọng Hướng
 */
// ,-q queue, -g cpu binding, -o output, -l thread, -a ative wait
public class ArgumentsTableData {
    private final StringProperty queue = new SimpleStringProperty();
    private final StringProperty cpuBinding = new SimpleStringProperty();
    private final StringProperty output = new SimpleStringProperty();
    private final StringProperty thread = new SimpleStringProperty();
    private final StringProperty ativeWait = new SimpleStringProperty();
    private final Map<String, String> argsMap = new HashMap<>();
    public ArgumentsTableData() {}

    public ArgumentsTableData(String queue, String cpuBinding, String output, String thread, String ativeWait) {
        this.queue.set(queue);
        this.cpuBinding.set(cpuBinding);
        this.output.set(output);
        this.thread.set(thread);
        this.ativeWait.set(ativeWait);
    }

    public StringProperty queueProperty() {return queue;}
    public StringProperty cpuBindingProperty() {return this.cpuBinding;}
    public StringProperty outputProperty() {return this.output;}
    public StringProperty threadProperty() {return this.thread;}
    public StringProperty ativeWaitProperty() {return this.ativeWait;}

    public void setQueue(String queue) {this.queue.set(queue);}
    public void setCpuBinding(String cpuBinding) {this.cpuBinding.set(cpuBinding);}
    public void setOutput(String output) {this.output.set(output);}
    public void setThread(String thread) {this.thread.set(thread);}
    public void setativeWait(String ativeWait) {this.ativeWait.set(ativeWait);}

    public String getQueue() {return this.queue.get();}
    public String getCpuBinding() {return this.cpuBinding.get();}
    public String getOutput() {return this.output.get();}
    public String getThread() {return this.thread.get();}
    public String getativeWait() {return this.ativeWait.get();}
    public void setArgsMap(Map<String, String> argsMap) {this.argsMap.putAll(argsMap);}
    // -q queue, -g cpu binding, -o output, -l thread, -a ative wait
    public String getArgsAsString() {
        return String.format("queue=%s, cpu binding=%s, output=%s, thread=%s, active wait=%s",
                queue.get(), cpuBinding.get(), output.get(), thread.get(), ativeWait.get());
    }

    public Map<String, String> getArgsMap() {
        if(this.argsMap.isEmpty()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("queue", queue.get());
            map.put("cpu binding", cpuBinding.get());
            map.put("output", output.get());
            map.put("thread", thread.get());
            map.put("active wait", ativeWait.get());
            return map;
        }
        return this.argsMap;
    }
}
