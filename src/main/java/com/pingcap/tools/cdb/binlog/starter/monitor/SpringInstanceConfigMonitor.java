package com.pingcap.tools.cdb.binlog.starter.monitor;

import com.google.common.collect.MapMaker;
import com.google.common.collect.MigrateMap;
import com.pingcap.tools.cdb.binlog.common.AbstractCDBLifeCycle;
import com.pingcap.tools.cdb.binlog.common.utils.NamedThreadFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by iamxy on 2017/2/17.
 */
public class SpringInstanceConfigMonitor extends AbstractCDBLifeCycle implements InstanceConfigMonitor {

    private static final Logger logger = LoggerFactory.getLogger(SpringInstanceConfigMonitor.class);

    private String rootConf;

    private long scanIntervalInSecond = 5;

    private InstanceAction defaultAction = null;

    private Map<String, InstanceAction> actions = new MapMaker().makeMap();

    private Map<String, InstanceConfigFiles> lastFiles = MigrateMap.makeComputingMap(destination -> new InstanceConfigFiles(destination));

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("cdb-instance-scan"));

    public void start() {
        super.start();
        Assert.notNull(rootConf, "root conf dir is null!");

        executor.scheduleWithFixedDelay(() -> {
            try {
                scan();
            } catch (Throwable e) {
                logger.error("scan failed", e);
            }
        }, 0, scanIntervalInSecond, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdownNow();
        actions.clear();
        lastFiles.clear();
        super.stop();
    }

    public void register(String destination, InstanceAction action) {
        if (action != null) {
            actions.put(destination, action);
        } else {
            actions.put(destination, defaultAction);
        }
    }

    public void unregister(String destination) {
        actions.remove(destination);
    }

    public void setRootConf(String rootConf) {
        this.rootConf = rootConf;
    }

    private void scan() {
        File rootdir = new File(rootConf);
        if (!rootdir.exists()) {
            return;
        }

        File[] instanceDirs = rootdir.listFiles(pathname -> {
            String filename = pathname.getName();
            return pathname.isDirectory() && !"spring".equalsIgnoreCase(filename);
        });

        Set<String> currentInstanceNames = new HashSet<String>();

        for (File instanceDir : instanceDirs) {
            String destination = instanceDir.getName();
            currentInstanceNames.add(destination);
            File[] instanceConfigs = instanceDir.listFiles((dir, name) -> StringUtils.equalsIgnoreCase(name, "instance.properties"));

            if (!actions.containsKey(destination) && instanceConfigs.length > 0) {
                notifyStart(instanceDir, destination);
            } else if (actions.containsKey(destination)) {
                if (instanceConfigs.length == 0) {
                    notifyStop(destination);
                } else {
                    InstanceConfigFiles lastFile = lastFiles.get(destination);
                    boolean hasChanged = judgeFileChanged(instanceConfigs, lastFile.getInstanceFiles());
                    if (hasChanged) {
                        notifyReload(destination);
                    }

                    if (hasChanged || CollectionUtils.isEmpty(lastFile.getInstanceFiles())) {
                        List<FileInfo> newFileInfo = new ArrayList<>();
                        for (File instanceConfig : instanceConfigs) {
                            newFileInfo.add(new FileInfo(instanceConfig.getName(), instanceConfig.lastModified()));
                        }

                        lastFile.setInstanceFiles(newFileInfo);
                    }
                }
            }

        }

        Set<String> deleteInstanceNames = new HashSet<>();
        for (String destination : actions.keySet()) {
            if (!currentInstanceNames.contains(destination)) {
                deleteInstanceNames.add(destination);
            }
        }
        for (String deleteInstanceName : deleteInstanceNames) {
            notifyStop(deleteInstanceName);
        }
    }

    private void notifyStart(File instanceDir, String destination) {
        try {
            defaultAction.start(destination);
            actions.put(destination, defaultAction);
            logger.info("auto notify start {} successful.", destination);
        } catch (Throwable e) {
            logger.error("scan add found[{}] but start failed", destination, ExceptionUtils.getFullStackTrace(e));
        }
    }

    private void notifyStop(String destination) {
        InstanceAction action = actions.remove(destination);
        try {
            action.stop(destination);
            lastFiles.remove(destination);
            logger.info("auto notify stop {} successful.", destination);
        } catch (Throwable e) {
            logger.error("scan delete found[{}] but stop failed", destination, ExceptionUtils.getFullStackTrace(e));
            actions.put(destination, action);
        }
    }

    private void notifyReload(String destination) {
        InstanceAction action = actions.get(destination);
        if (action != null) {
            try {
                action.reload(destination);
                logger.info("auto notify reload {} successful.", destination);
            } catch (Throwable e) {
                logger.error("scan reload found[{}] but reload failed",
                        destination,
                        ExceptionUtils.getFullStackTrace(e));
            }
        }
    }

    private boolean judgeFileChanged(File[] instanceConfigs, List<FileInfo> fileInfos) {
        boolean hasChanged = false;
        for (File instanceConfig : instanceConfigs) {
            for (FileInfo fileInfo : fileInfos) {
                if (instanceConfig.getName().equals(fileInfo.getName())) {
                    hasChanged |= (instanceConfig.lastModified() != fileInfo.getLastModified());
                    if (hasChanged) {
                        return hasChanged;
                    }
                }
            }
        }

        return hasChanged;
    }

    public void setDefaultAction(InstanceAction defaultAction) {
        this.defaultAction = defaultAction;
    }

    public void setScanIntervalInSecond(long scanIntervalInSecond) {
        this.scanIntervalInSecond = scanIntervalInSecond;
    }

    public static class InstanceConfigFiles {
        private String destination;
        private List<FileInfo> springFile = new ArrayList<>();
        private FileInfo rootFile;
        private List<FileInfo> instanceFiles = new ArrayList<>();

        public InstanceConfigFiles(String destination) {
            this.destination = destination;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public List<FileInfo> getSpringFile() {
            return springFile;
        }

        public void setSpringFile(List<FileInfo> springFile) {
            this.springFile = springFile;
        }

        public FileInfo getRootFile() {
            return rootFile;
        }

        public void setRootFile(FileInfo rootFile) {
            this.rootFile = rootFile;
        }

        public List<FileInfo> getInstanceFiles() {
            return instanceFiles;
        }

        public void setInstanceFiles(List<FileInfo> instanceFiles) {
            this.instanceFiles = instanceFiles;
        }
    }

    public static class FileInfo {
        private String name;
        private long lastModified = 0;

        public FileInfo(String name, long lastModified) {
            this.name = name;
            this.lastModified = lastModified;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }
    }
}
