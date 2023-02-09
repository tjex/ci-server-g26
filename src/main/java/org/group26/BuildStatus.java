package org.group26;

import java.time.LocalDateTime;

/**
 * Class to represent the status of a build.
 * It contains information about the success status of the build, the time when
 * the build was executed, and the log information for the build.
 */
public class BuildStatus {
    /**
     * A boolean indicating the success status of the build
     */
    boolean success;

    /**
     * The time when the build was executed
     */
    LocalDateTime time;

    /**
     * The log information for the build
     */
    String log;

    /**
     * Constructor to create a {@code BuildStatus} object with the specified success status,
     * time, and log information.
     *
     * @param success a boolean indicating the success status of the build
     * @param time the time when the build was executed
     * @param log the log information for the build
     */
    public BuildStatus(boolean success, LocalDateTime time, String log){
        this.success = success;
        this.time = time;
        this.log = log;
    }

    /**
     * Constructor to create a {@code BuildStatus} object with default values.
     * This constructor sets the success status to false, the time to the current
     * time, and the log information to an empty string.
     */
    public BuildStatus(){
        this.success = false;
        this.time = LocalDateTime.now();
        this.log = "";
    }

}
