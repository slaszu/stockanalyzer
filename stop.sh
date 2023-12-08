#!/bin/bash

echo "todo: add all option !!!!!!!!!!!!!!!!!!!!1"

dockerComposeTypes=('mysql' 'app')

dockerComposeTypesArgs=()

#prepare args
for name in "$@"
do
    if [[ " ${dockerComposeTypes[@]} " =~ " ${name} " ]]; then
        dockerType=$name
        if [[ ! " ${dockerComposeTypesArgs[@]} " =~ " ${dockerType} " ]]; then
            dockerComposeTypesArgs+=( $dockerType )
        fi
    fi
done

#default, without arg
if [ ${#dockerComposeTypesArgs[@]} -eq 0 ]; then
    echo "Stop only app ..."
    dockerComposeTypesArgs+=( 'app' )
fi

#prepare cmd
cmd="docker-compose";
for name in "${dockerComposeTypesArgs[@]}"
do
    cmd="$cmd -f docker-compose-$name.yml"
done
cmd="$cmd stop"

echo "Stop cmd : $cmd"
eval $cmd