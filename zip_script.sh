##!/bin/bash

usage()
{
cat << EOF
usage: $0 options

This script is meant to get the size of all the bzipped files.
By default runs in /cygdrive/c/Users/Stephen/Desktop/Coral_Tests

OPTIONS:
   -h      Show this message
   -c      runs the script in the current directory
   -d dir  runs the script in directory "dir"
EOF
}

CHANGE_DIR=1
DIR="/cygdrive/c/Users/Stephen/Desktop/Coral_Tests"
while getopts “:hcd:” OPTION
do
     case $OPTION in
         h)
             usage
             exit 1
             ;;
         d)
            DIR=$OPTARG
            ;;
         c)
             CHANGE_DIR=0
             ;;
         ?)
             usage
             exit
             ;;
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

csvList=$(find -name "2014*.txt")

for line in $csvList
	do
        #bzip it
        $(bzip2 -k $line);
	done
