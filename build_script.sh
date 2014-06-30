# install android ndk
#
if [ `uname -m` = x86_64 ]; then wget http://dl.google.com/android/ndk/android-ndk-r9d-linux-x86_64.tar.bz2 -O ndk.tgz; else wget http://dl.google.com/android/ndk/android-ndk-r9d-linux-x86.tar.bz2 -O ndk.tgz; fi
tar -xf ndk.tgz
export ANDROID_NDK_HOME=`pwd`/android-ndk-r9d
echo "ndkdir=$ANDROID_NDK_HOME" > gradle.properties
#
#
# build harfbuzz
#
sudo apt-get update
sudo apt-get install ragel autoconf libtool libcairo2-dev build-essential
cd src/main/native/
wget https://github.com/behdad/harfbuzz/archive/0.9.29.tar.gz -O harfbuzz-ng.tgz
tar -xf harfbuzz-ng.tgz
yes | cp -rf harfbuzz-ng/Android.mk harfbuzz-0.9.29
rm -rf harfbuzz-ng.tgz
rm -rf harfbuzz-ng
mv harfbuzz-0.9.29 harfbuzz-ng
cd harfbuzz-ng/
./autogen.sh
make
make check
cd ../../../..
