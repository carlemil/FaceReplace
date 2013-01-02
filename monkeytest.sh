#!/bin/bash
adb shell monkey -p com.vodafone.cloud2 --monitor-native-crashes --ignore-security-exceptions --pct-majornav 30 --throttle 50 --ignore-timeouts $1 
