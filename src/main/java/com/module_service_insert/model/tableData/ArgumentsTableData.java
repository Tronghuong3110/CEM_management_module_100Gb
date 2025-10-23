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
//    public String getArgsAsString() {
//        Map<String, String> args = new LinkedHashMap<>();
//        args.put("-q", queue.get() != null ? queue.get() : this.argsMap.getOrDefault("queue", "unknown"));
//        args.put("-g", cpuBinding.get() != null ? cpuBinding.get() : this.argsMap.getOrDefault("cpu worker", "unknown"));
//        args.put("-o", output.get() != null ? output.get() : this.argsMap.getOrDefault("output", "unknown"));
//        args.put("-l", thread.get() != null ? thread.get() : this.argsMap.getOrDefault("thread", "unknown"));
//        args.put("-a", ativeWait.get() != null ? ativeWait.get() : this.argsMap.getOrDefault("active wait", "unknown"));
//        args.put("-b", macAddress.get() != null ? macAddress.get() : this.argsMap.getOrDefault("mac address", "unknown"));
//        args.put("-c", id.get() != null ? id.get() : this.argsMap.getOrDefault("id", "unknown"));
//        args.put("-t", minOutputSize.get() != null ? minOutputSize.get() : this.argsMap.getOrDefault("min output size", "unknown"));
//        args.put("-r", cpuDispatch.get() != null ? cpuDispatch.get() : this.argsMap.getOrDefault("cpu dispatcher", "unknown"));
//        args.put("-j", cpuOutput.get() != null ? cpuOutput.get() : this.argsMap.getOrDefault("cpu output", "unknown"));
//
//        String cmd = args.entrySet().stream()
//                .filter(e -> e.getValue() != null && !"unknown".equalsIgnoreCase(e.getValue()))
//                .map(e -> String.format("%s %s", e.getKey(), e.getValue()))
//                .collect(Collectors.joining(" "));
//
//        return cmd;
//    }
//
//    // Map này được sử dụng để hiển thị
//    public Map<String, String> getArgsMap() {
//        if (this.argsMap.isEmpty()) {
//            Map<String, String> map = new LinkedHashMap<>();
//            addIfValid(map, "-q", queue.get());
//            addIfValid(map, "-g", cpuBinding.get());
//            addIfValid(map, "-o", output.get());
//            addIfValid(map, "-l", thread.get());
//            addIfValid(map, "-a", ativeWait.get());
//            addIfValid(map, "-r", cpuDispatch.get());
//            addIfValid(map, "-b", macAddress.get());
//            addIfValid(map, "-c", id.get());
//            addIfValid(map, "-j", cpuOutput.get());
//            addIfValid(map, "-t", minOutputSize.get());
//            return map;
//        }
//        return this.argsMap;
//
//    }

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
