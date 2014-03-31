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
DIR="/cygdrive/c/Users/Stephen/Desktop/Coral_Tests/"
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
         ?)
             usage
             exit;;
     esac
done

if [ $CHANGE_DIR -eq 1 ]
then
    cd "$DIR"
    if [ $? -ne 0 ]
    then
        exit
    fi
fi

#### #### #### #### ####

#Returns a list of bz2 file sizes 
processConcat() 
{
    CONCAT_SIZES=
    cd "$(ls | grep "Concat")"

    for fName in $(ls | grep .bz2)
    do
        CONCAT_SIZES=$CONCAT_SIZES,$(stat -c%s "$fName")
    done
    cd ..
}

processTest()  #1-folder name 2-file list
{
    # check for completion: _Aborted not here
    if [ "$(ls | grep "Abort")" != "" ]
    then
        echo "skipping aborted test $1"
        return
    fi
    processConcat #creates CONCAT_SIZES
    local HDR=${PWD#*(}
    local RN=${HDR#*)_}
    HDR=${HDR%)*}
    HDR=(${HDR//','/' '})
    HDR="Num: $RN,Test: ${HDR[0]},Colors: ${HDR[1]},Equation: ${HDR[2]},"$(cat _Description* | grep "Duration")
    
    local TIMES=
    local SIZES=
    local TXT_FILES=$(echo "$2" | grep .bz2)
    for tfName in $TXT_FILES
    do
        SIZES=$SIZES$(stat -c%s "$tfName"),
        tfName=${tfName#*,??_[0-9]*_}
        TIMES=$TIMES${tfName%.txt.bz2},
    done;

    #output to file:pol
    echo "$HDR">>$GLOBAL_FILE
    echo "$TIMES">>$GLOBAL_FILE
    echo "$SIZES">>$GLOBAL_FILE
    echo "$CONCAT_SIZES">>$GLOBAL_FILE
    echo "">>$GLOBAL_FILE
}

descend()
{
    cd "$1"
    if [ $? -ne 0 ]
    then
        return
    fi

    local FILES=$(ls)

    if [ "$(ls | grep "Concat")" = "" ]
    then
        for line in $FILES
        do
            # echo "  descending to $line"
            descend "$line"
        done
    else
        processTest "$1" "$FILES"
    fi
    cd ..
}

TEST_DIR="/cygdrive/c/Users/Stephen/Desktop/Coral_Tests/Result_Space/"
if [ ! -d "$TEST_DIR" ]
then
    echo "Making Result_Space"
    mkdir $TEST_DIR
fi
GLOBAL_FILE=$TEST_DIR"Coalesced_$(date +"%y-%m-%d_%H,%M").csv"
echo "">$GLOBAL_FILE

echo "BEGINNING PROCESSING"
descend .
echo "FINISHED PROCESSING"


# set -- "$STR"
# IFS=","; declare -a STR_ARRAY=($*)
# for x in "${STR_ARRAY[@]}"
# do
# echo "&gt; [$x]"
# done

# get base name: ${PWD##*/}