#!/bin/bash
# Autosigner

APK_IN=$PWD$1$2".apk"
APK_TEMP=$PWD$1$2"-temp.apk"
APK_FLD=$PWD$1
DEBUG_KS=$HOME"/.android/debug.keystore"

APK_OUT=$PWD$1$2"-signed.apk"


if [ -z "$3" ]; then
	if [ -z "$ANDROID_HOME" ]; then
	echo "Automatically setting ANDROID_HOME"
	ANDROID_HOME=$HOME/Android/Sdk
	fi
else
ANDROID_HOME=$3
echo "Inserted ANDROID_HOME:"$ANDROID_HOME
fi


if [ ! -d $ANDROID_HOME/build-tools/25* ]; then
	if [ ! -d $ANDROID_HOME/build-tools/26* ]; then
		echo "No Compatible API found"
		echo "Verify your ANDROID_HOME: "$ANDROID_HOME
		exit 1
	fi
fi
if [ ! -f $DEBUG_KS ]; then
    echo "Debug Keystore not found in"$DEBUG_KS
	exit 1
fi

	echo "Current Dir:" $PWD
	echo "Android Home:"$ANDROID_HOME
	echo "Home dir:" $HOME
	echo "APK IN: " $APK_IN
	echo "APK OUT: "$APK_OUT


	echo "-------------------------------"
	echo "Using Zipalign"
	cd $ANDROID_HOME/build-tools/25*
	./zipalign -f -v 4 $APK_IN $APK_OUT


	echo "------------------"
	echo "Using apksigner"
	./apksigner sign --ks $DEBUG_KS --ks-key-alias androiddebugkey --ks-pass pass:android $APK_OUT


	echo "-------------------------------"
	echo "APKsigner verification:"
	./apksigner verify --print-certs -v $APK_OUT

