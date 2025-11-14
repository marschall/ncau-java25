#!/bin/sh

set -e
set -u

ncamaven -am -pl ncau-java25-benchmarks exec:exec
