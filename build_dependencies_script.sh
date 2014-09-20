#
# extract libpng
#
tar -xf dependencies/libpng.tar.gz --directory src/main/native/

#
# extract freetype
#
tar -xf dependencies/freetype.tar.gz --directory src/main/native/

#
# extract harfbuzz
#
tar -xf dependencies/harfbuzz.tar.gz --directory src/main/native/

#
# build harfbuzz
#
sudo apt-get update
sudo apt-get install ragel autoconf libtool libcairo2-dev build-essential
cd src/main/native/
cd harfbuzz/
./autogen.sh
make
make check
cd ../../../..
