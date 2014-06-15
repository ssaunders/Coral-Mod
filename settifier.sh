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
one=$(echo $temp | cut -d " " -f1)
two=$(echo $temp | cut -d " " -f2)
three=$(echo $temp | cut -d " " -f3)
echo "$one|$two|$three"

declare -i count
count=4

#four is normally called "line"
for four in $fileList
do
    numOne=`expr $count - 3`
    numTwo=`expr $count - 2`
    numThree=`expr $count - 1`
    # echo "$numOne $numTwo $numThree \"$SET_DIR/$numOne-$numThree.txt\"  \"$SET_DIR/$numTwo-$count.txt\""

    cat $one $three > "$SET_DIR/$numOne-$numThree.txt"
    cat $one $four > "$SET_DIR/$numOne-$count.txt"
    
    # if [ "$prevTest" != "" ]
    # then 
    #     #concat files
    #     fileName=${prevTest%_*}'~'${line#*_*_}
    #     #save under new name
    #     cat "$prevTest" "$line" > "every_$NUM/$fileName"
    # fi
    # prevTest=$line

    one=$two
    two=$three
    three=$four

    count=`expr $count + 1`
done
cat $two $four > "$SET_DIR/$numTwo-`expr $count - 1`.txt"

# cd _Run12_2014-04-25_0\,52/2014-04-25_00\,52_\(4GT\,R\,3\)_9803/