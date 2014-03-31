cd /cygdrive/c/Users/Stephen/Desktop/Coral_Tests/

csvList=$(find -name "Concatenated Tests")

folder=""
i=0
uscore=""
for line in $csvList
	do
		if [ $((i%2)) -eq 0 ]
		then
			folder=$line
		else
			uscore=$folder"_"$line"/"
			folder=$folder" "$line
			mv "$folder" "$uscore"
		fi
		i=$i+1
	done