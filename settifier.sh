##!/bin/bash

usage()
{
cat << EOF
usage: $0 options

This script combines syncronous txt files into non-contiguous sets using a window of 4.
By default runs in /cygdrive/c/Users/${USER}/Desktop/Coral_Tests

OPTIONS:
   -h      Show this message
   # -c      runs the script in the current directory
   # -d dir  runs the script in directory "dir"
   -n num  combines every num file
EOF
}

CHANGE_DIR=1
NUM=0
DIR="/cygdrive/c/Users/${USER}/Desktop/Coral_Tests"
while getopts “:hcd:” OPTION
do
    case $OPTION in
        h)
            usage
            exit 1;;
        d)
            DIR=$OPTARG;;
        c)
            CHANGE_DIR=0;;
        # n) 
        #     NUM=$OPTARG;;
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


###   FUNCTIONS   ###

SET_DIR="Set_Directory"
makeSetDir() 
{
    if [ ! -d "$SET_DIR" ]
    then
        mkdir $SET_DIR
    fi
}


###   BEGIN PROGRAM EXECUTION   ###
makeSetDir

#Set up list for for loop
fileList=$(ls | grep 2014 | grep -v bz2)
temp=$(echo $fileList|cut -d " " -f1-3)
fileList=$(echo $fileList|cut -d " " -f4-);           ##### "10" is a DEBUG

#Set up vars for for loop
fileOne=$(echo $temp | cut -d " " -f1)
fileTwo=$(echo $temp | cut -d " " -f2)
fileThree=$(echo $temp | cut -d " " -f3)

declare -i count
count=4

#four is a file name
for fileFour in $fileList
do
    numOne=`expr $count - 3`
    numTwo=`expr $count - 2`
    numThree=`expr $count - 1`
    # echo "$numOne $numTwo $numThree \"$SET_DIR/$numOne-$numThree.txt\"  \"$SET_DIR/$numTwo-$count.txt\""


# printf "%0*s\n" 2 "3"
    fileName=$(printf "$SET_DIR/2014_%0*d-%0*d.txt" 2 $numOne 2 $numThree)
    cat $fileOne $fileThree > $fileName
    fileName=$(printf "$SET_DIR/2014_%0*d-%0*d.txt" 2 $numOne 2 $count)
    cat $fileOne $fileFour > $fileName

    fileOne=$fileTwo
    fileTwo=$fileThree
    fileThree=$fileFour

    count=`expr $count + 1`
done
fileName=$(printf "$SET_DIR/2014_%0*d-%0*d.txt" 2 $numTwo 2 `expr $count - 1`)
cat $fileTwo $fileFour > $fileName

# cd _Run12_2014-04-25_0\,52/2014-04-25_00\,52_\(4GT\,R\,3\)_9803/