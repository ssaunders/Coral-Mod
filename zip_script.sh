##!/bin/bash

usage()
{
cat << EOF
usage: $0 options

This script bzips all the txt files in a directory.
By default runs in /cygdrive/c/Users/${USER}/Desktop/Coral_Tests

OPTIONS:
   -h      Show this message
   -c      Runs the script in the current directory
   -d dir  Runs the script in directory "dir"
   -x      Deletes all bzipped files from chosen directory
   -p      Shows progress
EOF
}

CHANGE_DIR=1
DELETE=0
DIR="/cygdrive/c/Users/${USER}/Desktop/Coral_Tests"
SHOW_PROG=0
while getopts “:hcd:xp” OPTION
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
        p)
            SHOW_PROG=1;;
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
    curr=0
    csvList=$(find -name "2014*.txt")
    total=$(echo $csvList | grep 2014 | wc -w)
    for line in $csvList
	do
        $(bzip2 -k --best $line);
        curr=$(($curr + 1))
        if  ! (($curr % 5)) && [ $SHOW_PROG -eq 1 ]
        then
            echo -ne "     $(( ($curr * 100) / ($total) ))%\033[0K\r"
        fi
	done
fi

if [ $SHOW_PROG -eq 1 ]
then
    echo "Finished"
fi