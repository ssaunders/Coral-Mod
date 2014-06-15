##!/bin/bash

usage()
{
cat << EOF
usage: $0 options

This script combines txt files according to a delta.
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
while getopts “:hcd:n:” OPTION
do
    case $OPTION in
        h)
            usage
            exit 1;;
        # d)
        #     DIR=$OPTARG;;
        # c)
        #     CHANGE_DIR=0;;
        n) 
            NUM=$OPTARG;;
        ?)
            usage
            exit;;
    esac
done

# if [ $CHANGE_DIR -eq 1 ]
# then
#     cd $DIR
#     if [ $? -ne 0 ]
#     then
#         exit
#     fi
# fi
if [ $NUM -gt 1 ]
then
    if [ ! -d "every_"$NUM ]
    then
        echo "Making every_"$NUM
        mkdir "every_"$NUM
    fi

    fileList=$(ls | grep 2014 | grep -v bz2 | awk 'NR%'$NUM'==1')
    prevTest=
    for line in $fileList
    do
        if [ "$prevTest" != "" ]
        then 
            #concat files
            fileName=${prevTest%_*}'~'${line#*_*_}
            #save under new name
            cat "$prevTest" "$line" > "every_$NUM/$fileName"
        fi
        prevTest=$line
    done
else 
    echo "Cannot combine every $NUM file"
fi