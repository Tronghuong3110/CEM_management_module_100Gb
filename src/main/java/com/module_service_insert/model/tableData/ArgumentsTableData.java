package com.module_service_insert.model.tableData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    // Arrays.asList("Queue", "Thread", "CPU Binding", "Output", "Active Wait", "Mac address", "id", "Cpu Dispatcher",
    //                "Cpu Worker", "Cpu Output", "Min output size");
    private final StringProperty cpuDispatch = new SimpleStringProperty();
    private final StringProperty cpuWork = new SimpleStringProperty();
    private final StringProperty cpuOutput = new SimpleStringProperty();
    private final StringProperty macAddress = new SimpleStringProperty();
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty minOutputSize = new SimpleStringProperty();
    private final Map<String, String> argsMap = new HashMap<>();
    public ArgumentsTableData() {}

    public ArgumentsTableData(String queue, String cpuBinding, String output, String thread, String ativeWait, String cpuDispatcher, String cpuOutput, String macAddress, String id, String minOutputSize) {
        this.queue.set(queue);
        this.cpuBinding.set(cpuBinding);
        this.output.set(output);
        this.thread.set(thread);
        this.ativeWait.set(ativeWait);
        this.cpuOutput.set(cpuOutput);
        this.minOutputSize.set(minOutputSize);
        this.macAddress.set(macAddress);
        this.cpuDispatch.set(cpuDispatcher);
        this.id.set(id);
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
//        System.out.println("========== DEBUG ARG VALUES ==========");
//
//        printDebug("-q", "queue", queue.get(), this.argsMap.get("queue"));
//        printDebug("-g", "cpuBinding", cpuBinding.get(), this.argsMap.get("cpu worker"));
//        printDebug("-o", "output", output.get(), this.argsMap.get("output"));
//        printDebug("-l", "thread", thread.get(), this.argsMap.get("thread"));
//        printDebug("-a", "activeWait", ativeWait.get(), this.argsMap.get("active wait"));
//        printDebug("-b", "macAddress", macAddress.get(), this.argsMap.get("mac address"));
//        printDebug("-c", "id", id.get(), this.argsMap.get("id"));
//        printDebug("-t", "minOutputSize", minOutputSize.get(), this.argsMap.get("min output size"));
//        printDebug("-r", "cpuDispatch", cpuDispatch.get(), this.argsMap.get("cpu dispatcher"));
//        printDebug("-j", "cpuOutput", cpuOutput.get(), this.argsMap.get("cpu output"));
//
//        System.out.println("======================================");
        Map<String, String> args = Map.of(
                "-q", queue.get() != null ? queue.get() : this.argsMap.get("queue") != null ? this.argsMap.get("queue") : "unknown",
                "-g", cpuBinding.get() != null ? cpuBinding.get() : this.argsMap.get("cpu worker") != null ? this.argsMap.get("cpu worker") : "unknown",
                "-o", output.get() != null ? output.get() : this.argsMap.get("output") != null ? this.argsMap.get("output") : "unknown",
                "-l", thread.get() != null ? thread.get() : this.argsMap.get("thread") != null ? this.argsMap.get("thread") : "unknown",
                "-a", ativeWait.get() != null ? ativeWait.get() : this.argsMap.get("active wait") != null ? this.argsMap.get("active wait") : "unknown",
                "-b", macAddress.get() !=  null ? macAddress.get() : this.argsMap.get("mac address") != null ? this.argsMap.get("mac address") : "unknown",
                "-c", id.get() != null ? id.get() : this.argsMap.get("id") != null ? this.argsMap.get("id") : "unknown",
                "-t", minOutputSize.get() !=  null ? minOutputSize.get() : this.argsMap.get("min output size") != null ? this.argsMap.get("min output size") : "unknown",
                "-r", cpuDispatch.get() !=  null ? cpuDispatch.get() : this.argsMap.get("cpu dispatcher") != null ? this.argsMap.get("cpu dispatcher") : "unknown",
                "-j", cpuOutput.get() != null ? cpuOutput.get() : this.argsMap.get("cpu output") != null ? this.argsMap.get("cpu output") : "unknown"
        );

        String cmd = args.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().equalsIgnoreCase("unknown"))
                .map(e -> String.format("%s %s", e.getKey(), e.getValue()))
                .collect(Collectors.joining(" "));

        return cmd;
    }

    public Map<String, String> getArgsMap() {
        if (this.argsMap.isEmpty()) {
            Map<String, String> map = new LinkedHashMap<>();
            addIfValid(map, "queue", queue.get());
            addIfValid(map, "cpu worker", cpuBinding.get());
            addIfValid(map, "output", output.get());
            addIfValid(map, "thread", thread.get());
            addIfValid(map, "active wait", ativeWait.get());
            addIfValid(map, "cpu dispatcher", cpuDispatch.get()); // cpu dispatcher
            addIfValid(map, "mac address", macAddress.get());
            addIfValid(map, "id", id.get());
            addIfValid(map, "cpu output", cpuOutput.get());
            addIfValid(map, "min output size", minOutputSize.get());
            return map;
        }
        return this.argsMap;

    }

    private void addIfValid(Map<String, String> map, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            map.put(key, value);
        }
    }

    private void printDebug(String key, String fieldName, String fieldValue, String mapValue) {
        String finalValue = (fieldValue != null) ? fieldValue : mapValue;
        System.out.printf(
                "%s -> %s:\n   field(%s) = %s\n   map(%s) = %s\n   ==> finalValue = %s\n\n",
                key,
                fieldName,
                fieldName,
                fieldValue,
                key,
                mapValue,
                finalValue
        );
    }

}
