#!/bin/bash

#Bash script to process the data in csv files


cd /cygdrive/c/Users/${USER}/Desktop/Coral_Tests;

#Step 1: Find all csv files
csvList  = $(find -name "*.csv");

#Step 2: Iterate over list
	#PROBLEMS
		#I don't know how to group them
		#
	for line in $csvList
	do
		# get file size
		FILESIZE=$(stat -c%s "$line")
		echo "Size of $FILENAME = $FILESIZE bytes."

		#save to something

	done

#Step 3: Output results



convertToKB(sizeInBytes) 
{
	return sizeInBytes / 1024
}