#!/usr/bin/env bash

ALIAS=${1}
BBREF=${2}

shift ; shift

. $(dirname ${0})/env.sh

EVENTS=${OOOL}/alias.overrides
ruby ${ROCKWEILER_RUBY}/specify_alias.rb ${BBREF} ${ALIAS} | tee -a ${EVENTS}