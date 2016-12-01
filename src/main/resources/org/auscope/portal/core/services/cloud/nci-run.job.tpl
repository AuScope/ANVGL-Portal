#!/bin/bash
#PBS -P {0}
#PBS -q normal
#PBS -l walltime={4}
#PBS -l mem={6}
#PBS -l jobfs={7}
#PBS -l ncpus={5}
#PBS -l wd
#PBS Join_Path=oe
#PBS Output_Path={3}/vl.sh.log

# This batch file is expected to be copied into and then run directly from the VL_OUTPUT_DIR
# It is responsible for running the user's job script in an environment with all modules loaded
# and data downloaded. The script will write all pertinent output files to  VL_OUTPUT_DIR
source nci-util.sh

export VL_PROJECT_ID="{0}"
export VL_JOB_ID="{1}"
export VL_WORKING_DIR="{2}"
export VL_OUTPUT_DIR="{3}"
export VL_TERMINATION_FILE="vl.end"

echo "#### Environment start ####"
echo "VL_PROJECT_ID = $VL_PROJECT_ID"
echo "VL_JOB_ID = $VL_JOB_ID"
echo "VL_WORKING_DIR = $VL_WORKING_DIR"
echo "VL_OUTPUT_DIR = $VL_OUTPUT_DIR"
echo "VL_TERMINATION_FILE = $VL_TERMINATION_FILE"
echo "#### Environment end ####"

# Move our working data to the job node file system
cp "$VL_WORKING_DIR/*" "$PBS_JOBFS" || finish 2 "ERROR: Unable to copy data from $VL_WORKING_DIR to job filesystem at $PBS_JOBFS"
cp "$VL_OUTPUT_DIR/*" "$PBS_JOBFS" || finish 2 "ERROR: Unable to copy data from $VL_OUTPUT_DIR to job filesystem at $PBS_JOBFS"
cd "$PBS_JOBFS" || finish 2 "ERROR: Unable to access job filesystem at $PBS_JOBFS"

# Load Modules
{8}

# Emulate our "cloud" command line tool
export PATH="$PBS_JOBFS:$PATH"
echo 'cp "$2" "$VL_OUTPUT_DIR/$2"' > cloud
chmod +x cloud

# Run User Script
echo "#### Python start ####"
computeStartTime=`date +%s`
python "vl_script.py"
computeEndTime=`date +%s` 
echo "#### Python start ####"

echo "#### Compute Time start ####"
totalComputeTime=`expr $computeEndTime - $computeStartTime`
echo "Total compute time was `expr $totalComputeTime / 3600` hour(s), `expr $totalComputeTime % 3600 / 60` minutes and `expr $totalComputeTime % 60` seconds"
echo "#### Compute Time end ####"

# Tidy up
finish 0 "INFO: Finished run job"
