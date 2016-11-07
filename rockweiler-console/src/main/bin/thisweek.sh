#!/usr/bin/env bash

MONDAY=${1:-$(date -v -1d -v -Mon +%Y-%m-%d)}
shift

. $(dirname ${0})/env.sh

echo ${MONDAY}
grep -f ${SEASON}/danil.hitting.ids ${SEASON}/hitting.report | grep ${MONDAY} | grep ":H"
echo
echo
grep -f ${SEASON}/danil.pitching.ids ${SEASON}/hitting.report | grep ${MONDAY} | grep ":S"
echo
echo
grep -f ${SEASON}/danil.pitching.ids ${SEASON}/hitting.report | grep ${MONDAY} | grep ":R"
