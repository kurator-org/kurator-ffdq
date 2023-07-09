#!/usr/bin/env bash
parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
java -cp "$parent_path"/target/kurator-ffdq-2.0.2-SNAPSHOT.jar org.datakurator.ffdq.util.TestUtil "$@"
