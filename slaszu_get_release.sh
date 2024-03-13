#!/bin/bash

repo="https://github.com/slaszu/stockanalyzer/releases/download/TAG/stockanalyzer.jar"

echo "Add tag name as argument"

repo=${repo/TAG/"$1"}

cmd="curl -L $repo -o ./stockanalyzer-github-release.jar"

echo "Command: $cmd"
eval $cmd