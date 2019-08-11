#!/usr/bin/env bash

# run Ride in target dir

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd ${DIR}

PROPERTY_FILE=version.properties

function getProperty {
   PROP_KEY=$1
   PROP_VALUE=`cat $PROPERTY_FILE | grep "$PROP_KEY" | cut -d'=' -f2`
   echo $PROP_VALUE | xargs
}

echo "# Reading property from $PROPERTY_FILE"
VERSION=$(getProperty "version")
JAR_NAME=$(getProperty "jar.name")
RELEASE_TAG=$(getProperty "release.tag")

TARGET=${PWD}/target
ZIPFILE="${TARGET}"/ride-"${RELEASE_TAG}-dist.zip"

if [[ ! -f "${ZIPFILE}" ]]; then
  echo "${ZIPFILE} does not exist, creating it..."
  mvn clean install -P online -P createProps
fi

ZIPDIR=${ZIPFILE%.*}
if [[ ! -e "${ZIPDIR}" ]]; then
  echo "Unpacking ${ZIPFILE} to ${ZIPDIR}"
  unzip -d "$ZIPDIR" "$ZIPFILE"
fi

cd ${ZIPDIR}
./ride.sh