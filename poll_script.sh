##!/bin/bash
debug=
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
EOF
}

CHANGE_DIR=1
DIR="/cygdrive/c/Users/${USER}/Desktop/Coral_Tests/"
NUM=0
while getopts “:hcd:n:rv” OPTION
do
    case $OPTION in
        h)
            usage
            exit 1;;
        d)
            DIR=$OPTARG;;
        c)
            CHANGE_DIR=0;;
        n)
            NUM=$OPTARG;;
        r)
            roll_call=true;;
        v)
            verbose=true;;
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

###   FUNCTIONS   ###
calcNCD() {     #PARAMS: si, sj, sij
    if [ $debug ]; then echo "    calcNCD - $1 $2 $3"; fi

    local si=$1
    local sj=$2
    local sij=$3
    local min=
    local max=

    [[ si -lt sj ]] && min=$si || min=$sj
    [[ si -gt sj ]] && max=$si || max=$sj

    result=$(echo "scale=7;( $sij - $min ) / $max"|bc)
}

#Returns a list of bz2 file sizes 
processConcat() 
{
    if [ $roll_call ]; then echo "    processConcat"; fi

    CONCAT_SIZES=
    if [ $NUM -eq 0 ]
    then
        cd "$(ls | grep "Concat")"
    else 
        echo 'b) num not 0'
        cd "every_"$NUM
    fi

    for fName in $(ls | grep .bz2)
    do
        CONCAT_SIZES=$CONCAT_SIZES,$(stat -c%s "$fName")
    done
    cd ..
}

processSets()
{
    if [ $roll_call ]; then echo "    processSets"; fi
    if [ ! -d "Set_Directory" ]
    then
        echo "    calling settify"
        settify -c
        zipper -d "Set_Directory"
    fi

    CCT_SIZES_D=
    local i=0
    cd "Set_Directory"
    for fName in $(ls | grep .bz2)
    do
        CCT_SIZES_D=$CCT_SIZES_D,$(stat -c%s "$fName")
    done
    if [ $verbose ]; then echo "    done"; fi
    cd ..
}

processDesc() {
    if [ $roll_call ]; then echo "    processDesc"; fi

    local file=$(cat _Description*)
    DESC=$(echo "$file" | grep "Duration"),$(echo "$file" | grep "Facility")
}

processTest()  #1-folder name 2-file list
{
    if [ $roll_call ]; then echo "  processTest $CURR_FOLDER"; fi

    # check for completion: _Aborted not here
    if [ "$(echo "$2" | grep "Abort")" != "" ]
    then
        echo "skipping aborted test $1"
        return
    elif [ "$(echo "$2" | grep .bz2)" == "" ]
    then
        echo "  !!  No zipped files found in "${PWD##*/}
        return
    fi

    local ERR_FLAG=
    if [ "$(echo "$2" | grep "_Errors")" != "" ]
    then
        ERROR=$(head -c 70 _Errors*)
        ERR_FLAG=ERROR
        echo "  X(  "${PWD##*/}" has errors. "$ERROR
    fi

    processConcat #creates CONCAT_SIZES
    processDesc #creates DESC
    processSets

    # HEADER SETUP
    if [ $verbose ]; then echo "    Headers"; fi
    local HDR=${PWD#*(}
    local RN=${HDR#*)_}
    HDR=${HDR%)*}
    HDR=(${HDR//','/' '})
    HDR="Num: $RN,Test: ${HDR[0]},Colors: ${HDR[1]},Equation: ${HDR[2]},"$DESC,,$ERR_FLAG
    
    local TIMES=
    local SIZES=

    local TXT_FILES=
    if [ $NUM -eq 0 ]
    then
        TXT_FILES=$(echo "$2" | grep .bz2)
    else 
        echo 'a) num not 0'
        TXT_FILES=$(ls | grep bz2 | awk 'NR%'$NUM'==1')
    fi

    # BEGIN PROCESSING
    if [ $verbose ]; then echo "    Running stat"; fi
    for tfName in $TXT_FILES
    do
        SIZES=$SIZES$(stat -c%s "$tfName"),
        tfName=${tfName#*,??_[0-9]*_}
        TIMES=$TIMES${tfName%.txt.bz2},
    done;

    #  ADD NCD delta 1
        #replace , with " "
    if [ $verbose ]; then echo "    NCD 1"; fi
    local surveyAry=(${SIZES//','/' '})
    local concatAry=(${CONCAT_SIZES//','/' '})
    NCD_str=
    NCD_D=(${CCT_SIZES_D//','/' '})
    NCD_A3=()
    NCD_A4=()
    result=
    local length=${#surveyAry[@]}
    for (( pos=1; pos < length; ++pos ))
    do
        calcNCD ${surveyAry[$pos-1]} ${surveyAry[$pos]} ${concatAry[$pos-1]}    #returns $result
        NCD_str=$NCD_str$result,
        NCD_A2[pos-1]=$result
    done

    #  ADD NCD delta 2,3
    if [ $verbose ]; then echo "    NCD 2,3"; fi
    length=`expr ${#NCD_D[@]} / 2`
    
    CCT_SIZES_3=
    CCT_SIZES_4=
    for (( pos=0; pos < length; ++pos ))
    do
        calcNCD ${surveyAry[$pos]} ${surveyAry[$pos+2]} ${NCD_D[2*$pos]}
        NCD_A3[pos]=$result
        # echo "3 - ${surveyAry[$pos]} ${surveyAry[$pos+2]} ${NCD_D[2*$pos]} | $result"     #!D
        # CCT_SIZES_3=$CCT_SIZES_3,${NCD_D[2*$pos]}       #! comment out in the output below
        
        calcNCD ${surveyAry[$pos]} ${surveyAry[$pos+3]} ${NCD_D[2*$pos+1]}
        NCD_A4[pos]=$result
        # echo "4 - ${surveyAry[$pos]} ${surveyAry[$pos+3]} ${NCD_D[2*$pos+1]} | $result"     #!D
        # CCT_SIZES_4=$CCT_SIZES_4,${NCD_D[2*$pos+1]}
    done
    calcNCD ${surveyAry[$pos]} ${surveyAry[$pos+2]} ${NCD_D[2*$pos]}
    NCD_A3[pos]=$result
    CCT_SIZES_3=$CCT_SIZES_3,${NCD_D[2*$pos]}

    # ADD SET COMPLEXITY
    if [ $verbose ]; then echo "    Set Complexity"; fi
    SET_CPX=
    local n=4
    local scalar=.0833333
    #$(echo "scale=7;1 / ( $n * ( $n-1 ) )"|bc)
    local sum_sizes=1
    local sum_ncds=1

    for (( pos=0; pos < length; ++pos ))
    do
        # echo "${surveyAry[$pos]} + ${surveyAry[$pos+1]} + ${surveyAry[$pos+2]} + ${surveyAry[$pos+3]}"
        sum_sizes=$(echo "scale=7;${surveyAry[$pos]} + ${surveyAry[$pos+1]} + ${surveyAry[$pos+2]} + ${surveyAry[$pos+3]}"|bc)
        a=${NCD_A2[$pos]}
        b=${NCD_A2[$pos+1]}
        c=${NCD_A2[$pos+2]}

        d=${NCD_A3[$pos]}
        e=${NCD_A3[$pos+1]}

        f=${NCD_A4[$pos]}

        # echo "$a * ( 1 - $a ) + $b * ( 1 - $b ) + $c * ( 1 - $c ) \
        #     + $d * ( 1 - $d ) + $e * ( 1 - $e ) \
        #     + $f * ( 1 - $f )"

        sum_ncds=$(echo "scale=7;$a * ( 1 - $a ) + $b * ( 1 - $b ) + $c * ( 1 - $c ) \
                               + $d * ( 1 - $d ) + $e * ( 1 - $e ) \
                               + $f * ( 1 - $f ) \
                 "|bc)
        SET_CPX=$SET_CPX$(echo "scale=7;$scalar * $sum_sizes * $sum_ncds"|bc),
    done

    # OUTPUT to file:pol
    if [ $verbose ]; then echo "    Output to file"; fi
    echo "$HDR">>$GLOBAL_FILE
    echo "min,$TIMES">>$GLOBAL_FILE
    echo "size (sgl),$SIZES">>$GLOBAL_FILE
    echo "size (cct),$CONCAT_SIZES">>$GLOBAL_FILE
    echo "ncd,,"$NCD_str>>$GLOBAL_FILE
    # IFS=','     #! if you comment out below, comment out inin the loop above
    # echo "size (cct_3),$CCT_SIZES_3">>$GLOBAL_FILE      
    # echo "ncd2,,${NCD_A3[*]}">>"$GLOBAL_FILE"
    # echo "size (cct_4),$CCT_SIZES_4">>$GLOBAL_FILE
    # echo "ncd3,,${NCD_A4[*]}">>"$GLOBAL_FILE"
    # IFS=' '
    echo "set cpx,,"$SET_CPX>>$GLOBAL_FILE
    echo "">>$GLOBAL_FILE

    if [ $verbose ]; then echo "  done"; fi
}

descend()
{
    if [ $roll_call ]; then echo "  descend to $1"; fi
    cd "$1"
    if [ $? -ne 0 ]
    then
        return
    fi

    local above=$CURR_FOLDER
    CURR_FOLDER=$1

    local FILES=$(ls | grep ^2014)

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
    CURR_FOLDER=$above
    cd ..
}

###   BEGINNING PROGRAM ###

TEST_DIR="/cygdrive/c/Users/${USER}/Desktop/Coral_Tests/Result_Space/"
if [ ! -d "$TEST_DIR" ]
then
    echo "Making Result_Space"
    mkdir $TEST_DIR
fi
GLOBAL_FILE=$TEST_DIR"Coalesced_$(date +"%y-%m-%d_%H,%M").csv"
echo "">$GLOBAL_FILE

t1=$(date +"%s")
echo ""
num_tests=$(ls -1 | grep ^2014 | wc -l)

echo "BEGINNING PROCESSING"
if [ "$(ls | grep "Concat")" = "" ]
then
    echo $num_tests" tests found. Processing will take appx "$(echo "scale=2;$num_tests * 1.8"|bc)" min"
else 
    echo "1 test found. Processing will take appx 1.8 min"
done

descend .
t2=$(date +"%s")
diff=$(($t2-$t1))
echo "FINISHED PROCESSING ($(($diff / 60))m $(($diff % 60))s)"

#  RESOURCES 
#-------------------
# set -- "$STR"
# IFS=","; declare -a STR_ARRAY=($*)
# for x in "${STR_ARRAY[@]}"
# do
# echo "&gt; [$x]"
# done

# get base name: ${PWD##*/}

# echo ${Q//PAT} ;# removes all of PAT from individuals in Q
# echo ${Q%%.txt}   ;#  BACK http://tldp.org/LDP/abs/html/string-manipulation.html
# echo ${Q$$.txt}   ;#  FRONT

# PROGRESS BAR TYPE THING
# for pc in {1..100}
# do
#     echo -ne "$pc%\033[0K\r"
#     usleep 100000
# done
# echo