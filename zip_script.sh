##!/bin/bash

usage()
{
cat << EOF
usage: $0 options

This script is meant to get the size of all the bzipped files.
By default runs in /cygdrive/c/Users/${USER}/Desktop/Coral_Tests

OPTIONS:
   -h      Show this message
   -c      runs the script in the current directory
   -d dir  runs the script in directory "dir"
   -x      deletes all bzipped files from chosen directory
EOF
}

CHANGE_DIR=1
DELETE=0
DIR="/cygdrive/c/Users/${USER}/Desktop/Coral_Tests"
while getopts “:hcd:x” OPTION
do
    case $OPTION in
        h)
            usage
            exit 1;;
        d)
            DIR=$OPTARG;;
        c)
            CHANGE_DIR=0;;
        x) 
            DELETE=1;;
        ?)
            usage
            exit;;
    esac
done

if [ $CHANGE_DIR -eq 1 ]
then
    cd $DIR
    if [ $? -ne 0 ]
    then
        exit
    fi
fi


if [ $DELETE -eq 1 ]
then
    rmList=$(find -name "*.bz2")
    rm $rmList
else
    csvList=$(find -name "2014*.txt")
    for line in $csvList
	do
        $(bzip2 -k --best $line);
	done
fi
