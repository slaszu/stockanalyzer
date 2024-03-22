#!/bin/bash

cmd="git clone git@bitbucket.org:piotr_flasza/stockanalyzer-config.git ./config_credentials";

echo "GIT CLONE : $cmd"
eval $cmd


cmd="cd ./config_credentials && git pull"

echo "GIT PULL : $cmd"
eval $cmd