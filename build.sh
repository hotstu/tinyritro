#!/usr/bin/env sh

set -x;
echo "[BUILD task begin] `date '+%Y-%m-%d %H:%M:%S'`\n";

if [ $# -eq 0 ]; then
    flavor="debug";
else
    flavor=`echo $1 | tr '[A-Z]' '[a-z]'`;
fi

if [ ${flavor} = "release" ]; then
    gradle assembleRelease;
else
    gradle assembleDebug;
fi

echo "[BUILD task end] `date '+%Y-%m-%d %H:%M:%S'`\n";