#!/bin/bash

options=('--all')
optionsArgs=()
dockerComposeTypes=('mongo' 'app')

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

    if [[ " ${options[@]} " =~ " ${name} " ]]; then
        optionsArgs+=( $name )
    fi
done

#check options
for name in "${optionsArgs[@]}"
do
  if [[ " ${name} " =~ "all" ]]; then
      echo "Stop all services ..."
      for name in "${dockerComposeTypes[@]}"
      do
          dockerComposeTypesArgs+=( $name )
      done
  fi
done

#default, without arg
if [ ${#dockerComposeTypesArgs[@]} -eq 0 ]; then
    echo "Use only app ..."
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