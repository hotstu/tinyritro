#!/usr/bin/env sh

set -x;
echo "[DELIVER task begin] `date '+%Y-%m-%d %H:%M:%S'`\n";

timestamp=`date +%Y%m%d%H%M`;

if [ $# -ne 2 ]; then
    flavor="debug";
    branch_name="develop";
else
    flavor=`echo $1 | tr '[A-Z]' '[a-z]'`;
    branch_name=$2;
fi

if [ -f "app/build/outputs/apk/${flavor}/app-${flavor}.apk" ]; then
    echo `pwd`;
    mkdir -p /var/www/android;
    new_filename="app-${flavor}-${branch_name}-${timestamp}.apk";
    cp "app/build/outputs/apk/${flavor}/app-${flavor}.apk" "/var/www/android/${new_filename}";
    ln -s "/var/www/android/${new_filename}" "/var/www/android/ait-${flavor}-latest.apk";
    ln -s "/var/www/android/${new_filename}" "/var/www/android/ait-${branch_name}-latest.apk";
fi

echo "[DELIVER task end] `date '+%Y-%m-%d %H:%M:%S'`\n";