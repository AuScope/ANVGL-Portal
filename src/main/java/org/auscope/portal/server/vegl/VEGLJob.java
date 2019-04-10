package org.auscope.portal.server.vegl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.auscope.portal.core.cloud.CloudJob;
import org.auscope.portal.server.gridjob.FileInformation;
import org.auscope.portal.server.vegl.VglParameter.ParameterType;

/**
 * A specialisation of a generic cloud job for the VEGL Portal
 *
 * A VEGL job is assumed to write all output to a specific cloud location
 * @author Josh Vote
 *
 */
@Entity
@Table(name = "jobs")
public class VEGLJob extends CloudJob implements Cloneable {
    private static final long serialVersionUID = -57851899164623641L;
    
    @SuppressWarnings("unused")
    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String registeredUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seriesId")
    private Integer seriesId;
    private boolean emailNotification;
    private String processTimeLog;
    private String storageBucket;
    private String promsReportUrl;
    private String computeVmRunCommand;

    /**
     * max walltime for the job. 0 or null indicate that no walltime applies to the job
     */
    private Integer walltime;
    private boolean containsPersistentVolumes;

    /** Time when the job executes as opposed to when the job was submitted **/
    private Date executeDate;

    /** A map of VglParameter objects keyed by their parameter names*/
    @OneToMany(mappedBy = "parent", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    @MapKey(name="name")
    private Map<String, VglParameter> jobParameters = new HashMap<String, VglParameter>();
    
    /** A list of VglDownload objects associated with this job*/
    @OneToMany(mappedBy="parent", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    private List<VglDownload> jobDownloads = new ArrayList<>();

    /** A list of FileInformation objects associated with this job*/
    private List<FileInformation> jobFiles = new ArrayList<>();

    /** A set of Solutions associated with this job */
    @ElementCollection
    @CollectionTable(name="job_solutions", joinColumns=@JoinColumn(name="job_id"))
    @Column(name="solution_id")
    private Set<String> jobSolutions = new HashSet<>();

    public boolean isContainsPersistentVolumes() {
        return containsPersistentVolumes;
    }


    public void setContainsPersistentVolumes(boolean containsPersistentVolumes) {
        this.containsPersistentVolumes = containsPersistentVolumes;
    }

    /**
     * Creates an unitialised VEGLJob
     */
    public VEGLJob() {
        super();
    }


    /**
     * 
     */
    public Integer getId() {
		return id;
	}


    /**
     * 
     */
	public void setId(Integer id) {
		this.id = id;
	}


	/**
     * Sets the processTimeLog
     * @param String time
     */
    public void setProcessTimeLog(String processTimeLog) {
        this.processTimeLog=processTimeLog;

    }

    /**
     * @return the processTimeLog
     */
    public String getProcessTimeLog() {
        return processTimeLog;
    }

    /**
     * Gets where this job has been registered
     * @return
     */
    public String getRegisteredUrl() {
        return registeredUrl;
    }

    /**
     * Sets where this job has been registered
     * @param registeredUrl
     */
    public void setRegisteredUrl(String registeredUrl) {
        this.registeredUrl = registeredUrl;
    }

    /**
     * Gets the ID of the series this job belongs to
     * @return
     */
    public Integer getSeriesId() {
        return seriesId;
    }

    /**
     * Sets the ID of the series this job belongs to
     * @param seriesId
     */
    public void setSeriesId(Integer seriesId) {
        this.seriesId = seriesId;
    }

    /**
     * Gets the email notification flag for this job
     * @return
     */
    public boolean getEmailNotification() {
        return emailNotification;
    }

    /**
     * Sets the email notification flag for this job
     * @param seriesId
     */
    public void setEmailNotification(boolean emailNotification) {
        this.emailNotification = emailNotification;
    }

    /**
     * A set of VglJobParameter objects
     * @return
     */
    public Map<String, VglParameter> getJobParameters() {
        return jobParameters;
    }

    /** A set of VglJobParameter objects*/
    public void setJobParameters(Map<String, VglParameter> jobParameters) {
        this.jobParameters = jobParameters;
        /*
        if(jobParameters != null && jobParameters.values() != null) {
	        for (VglParameter params : jobParameters.values()) {
	            params.setParent(this);
	        }
        }
        */
    }

    /**
     * Sets a single parameter within this job
     * @param name The name of the parameter (parameters with the same name will be overwritten)
     * @param value The value of the parameter
     * @param type The type of the parameter ('number' or 'string')
     */
    public void setJobParameter(String name, String value, ParameterType type) {
        VglParameter param = jobParameters.get(name);
        if (param == null) {
            param = new VglParameter();
        }

        param.setParent(this);
        param.setName(name);
        param.setValue(value);
        param.setType(type.name());

        jobParameters.put(name, param);
    }

    /**
     * Gets the VglParameter with a particular name
     * @param key
     * @return
     */
    public VglParameter getJobParameter(String key) {
        return this.jobParameters.get(key);
    }


    /**
     * A list of VglDownload objects associated with this job
     * @return
     */
    public List<VglDownload> getJobDownloads() {
        return jobDownloads;
    }

    /**
     * A list of VglDownload objects associated with this job
     * @param jobDownloads
     */
    public void setJobDownloads(List<VglDownload> jobDownloads) {
        this.jobDownloads = jobDownloads;
        for (VglDownload dl : jobDownloads) {
            dl.setParent(this);
        }
    }


    public List<FileInformation> getJobFiles() {
        return jobFiles;
    }

    //    public void setJobFiles(List<FileInformation> jobFiles) {
    //        this.jobFiles = jobFiles;
    //        for (FileInformation fi : jobFiles) {
    //            fi.setParent(this);
    //        }
    //    }

    public Set<String> getJobSolutions() {
        return this.jobSolutions;
    }

    public void addJobSolution(String solutionId) {
        this.jobSolutions.add(solutionId);
    }

    public void setJobSolutions(Set<String> solutions) {
        this.jobSolutions = solutions;
    }

    /**
     * Similar to clone but ensures compatibility with hibernate. No IDs or references (except for immutable ones)
     * will be shared by the clone and this object.
     * @return
     */
    public VEGLJob safeClone() {
        VEGLJob newJob = new VEGLJob();
        newJob.setComputeInstanceId(this.getComputeInstanceId());
        newJob.setComputeInstanceKey(this.getComputeInstanceKey());
        newJob.setComputeInstanceType(this.getComputeInstanceType());
        newJob.setComputeServiceId(this.getComputeServiceId());
        newJob.setComputeVmId(this.getComputeVmId());
        newJob.setComputeVmRunCommand(this.getComputeVmRunCommand());
        newJob.setDescription(this.getDescription());
        newJob.setEmailAddress(this.getEmailAddress());
        newJob.setName(this.getName());
        newJob.setRegisteredUrl(this.getRegisteredUrl());
        newJob.setSeriesId(this.getSeriesId());
        newJob.setStatus(this.getStatus()); //change the status
        newJob.setStorageServiceId(this.getStorageServiceId());
        newJob.setStorageBaseKey(this.getStorageBaseKey());
        newJob.setSubmitDate(this.getSubmitDate()); //this job isn't submitted yet
        newJob.setUser(this.getUser());
        newJob.setStorageBucket(this.getStorageBucket());
        newJob.setWalltime(this.getWalltime());
        newJob.setExecuteDate(this.getExecuteDate());
        newJob.setPromsReportUrl(this.getPromsReportUrl());
        newJob.setContainsPersistentVolumes(this.isContainsPersistentVolumes());

        List<VglDownload> newDownloads = new ArrayList<>();
        for (VglDownload dl : this.getJobDownloads()) {
            VglDownload dlClone = (VglDownload) dl.clone();
            dlClone.setId(null);
            newDownloads.add(dlClone);
        }
        newJob.setJobDownloads(newDownloads);

        Map<String, VglParameter> newParams = new HashMap<>();
        for (String key : this.jobParameters.keySet()) {
            VglParameter paramClone = (VglParameter)this.jobParameters.get(key).clone();
            paramClone.setId(null);
            newParams.put(key, paramClone);
        }
        newJob.setJobParameters(newParams);

        for (String key : properties.keySet()) {
            newJob.setProperty(key, getProperty(key));
        }

        newJob.setJobSolutions(new HashSet<>(this.getJobSolutions()));

        return newJob;
    }

    /**
     * The storage bucket name that will receive job artifacts (usually unique to user)
     */
    @Override
    public String getStorageBucket() {
        return storageBucket;
    }

    /**
     * The storage bucket name that will receive job artifacts (usually unique to user)
     * @param storageBucket
     */
    public void setStorageBucket(String storageBucket) {
        this.storageBucket = storageBucket;
    }

    /**
     * The walltime in minutes.
     * @return Walltime in minutes or null if no walltime is set.
     */
    public Integer getWalltime() {
        return walltime;
    }

    public boolean isWalltimeSet() {
        return getWalltime()!=null && getWalltime()>0;
    }

    /**
     * Set the walltime in minutes
     * @param walltime
     */
    public void setWalltime(Integer walltime) {
        this.walltime = walltime;
    }

    /**
     * @return The date of job execution
     */
    public Date getExecuteDate() {
        return executeDate;
    }

    public void setExecuteDate(Date executeDate) {
        this.executeDate = executeDate;
    }

    /**
     * @return The URL of the associated PROMS Report
     */
    public String getPromsReportUrl() {
        return promsReportUrl;
    }

    public void setPromsReportUrl(String promsReportUrl) {
        this.promsReportUrl = promsReportUrl;
    }

    /**
     * The command that will be used to run the python run script. If null, most providers will use 'python'
     * @return
     */
    public String getComputeVmRunCommand() {
        return computeVmRunCommand;
    }

    /**
     * The command that will be used to run the python run script. If null, most providers will use 'python'
     * @param computeVmRunCommand
     */
    public void setComputeVmRunCommand(String computeVmRunCommand) {
        this.computeVmRunCommand = computeVmRunCommand;
    }

    @Override
    public String toString() {
        return "VEGLJob [registeredUrl=" + registeredUrl + ", seriesId="
                + seriesId + ", id=" + id + ", name=" + name + ", description="
                + description + "]";
    }

}
