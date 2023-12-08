#!/bin/bash

echo "Use --build option to rebuild containers"
options=('--build')
optionsArgs=()

dockerComposeTypes=('mysql' 'app')
dockerComposeTypesArgs=()

#prepare composer types
for name in "$@"
do
    if [[ " ${dockerComposeTypes[@]} " =~ " ${name} " ]]; then
        dockerType=$name
        if [[ ! " ${dockerComposeTypesArgs[@]} " =~ " ${dockerType} " ]]; then
            dockerComposeTypesArgs+=( $dockerType )
        fi
    fi

    if [[ " ${options[@]} " =~ " ${name} " ]]; then
        optionsArgs+=( $name )
    fi
done

#default, without arg
if [ ${#optionsArgs[@]} -eq 0 ]; then
    optionsArgs+=( "up -d" )
fi
if [ ${#dockerComposeTypesArgs[@]} -eq 0 ]; then
    echo "Start all services ..."
    for name in "${dockerComposeTypes[@]}"
    do
        dockerComposeTypesArgs+=( $name )
    done
fi

#prepare cmd
cmd="docker-compose";


for name in "${dockerComposeTypesArgs[@]}"
do
    cmd="$cmd -f docker-compose-$name.yml"
done
for name in "${optionsArgs[@]}"
do
    cmd="$cmd ${name//--/}"
done

echo "Create network for sure"
eval "docker network create stockanalyzer-network"

echo "Start cmd : $cmd"
eval $cmd