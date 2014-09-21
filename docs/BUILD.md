Build
=====

## Dependencies

This modules is dependent on 
 
 `
    sdk-common    ->    (Common module)
 `
 
 Please make sure all the dependencies are built first.

## Including in your project with Gradle

`sdk-render` module of `silpa-android-sdk` is pushed to Maven Central as a AAR, so you just need to add the following dependency to your `build.gradle`.

    dependencies {        
        compile 'org.silpa:sdk-common:1.0.0@aar'       
        compile 'org.silpa:sdk-render:1.0.0@aar'       
    }

## Including in your project with Maven

If you use maven to build your Android project you can simply add a dependency for this library.
    
    <dependency>
      <groupId>org.silpa</groupId>
      <artifactId>sdk-common</artifactId>
      <version>1.0.0</version>
      <type>aar</type>
    </dependency>

    <dependency>
      <groupId>org.silpa</groupId>
      <artifactId>sdk-render</artifactId>
      <version>1.0.0</version>
      <type>aar</type>
    </dependency>


## Referencing this module in Android Studio along with building Harfbuzz

- Clone a copy of this repository, or download it  
    `https://github.com/Project-SILPA/sdk-render`

- Select File -> New Module -> Import existing project -> set path to the cloned/downloaded repository
    
- Also clone and build dependent module - `sdk-common` . Add it in dependencies of `build.gradle` file of `sdk-render`.

- Go to Terminal and navigate to project location `sdk-render` and run `build_dependencies_script.sh`

- Open `gradle.properties` and set location of Android NDK build to variable `ndkdir`. For eg : `ndkdir=/home/user/android-ndk-r9d`

- Uncomment `ndk build task` from `build.gradle` of `sdk-render`.

- Clean the project

- Add the following to `settings.gradle` if not automatically included. Example :  
    `include ':MyApp', ':sdk-render'`
    
- Add the following to dependencies in `build.gradle` of the project to which you want to reference this module
   
```   
    dependencies {        
        compile project(':sdk-render')
    }
```


## Referencing this module in Eclipse along with building Harfbuzz

- Clone a copy of this repository, or download it (outside eclipse workspace)

- Import the code in your workspace.
    - Click New
    - Android
    - Android Project from existing code
    - Browse to be location of downloaded/cloned project.
    - Under `New Project Name` , rename to `sdk-render` or `silpa-render` or as you wish instead of "main".
    - Finish

- Add `sdk-common` as library project to this module. 

- Build project 
    - Mark java(*) folder as source (click on folder -> Build-Path -> use as source folder). You can also remove the src folder, from the project.
    - Mark project as Android Library (Properties -> Android -> Is library -> Apply). The library targets SDK 19 and works with minSdk 8.
    

- Native build
    - Go to Terminal
    - Navigate to project location `sdk-render` or `silpa-render`
    - Run `build_dependencies_script.sh`
    (This would build harfuzz with freetype and libpng)

    - Refactor folder `native` of module to `jni`. If refactoring causes error, manually navigate to folder location and rename it.

- Set up Android NDK
    - Select module
    - File -> Properties
    - Go to `Builders` tab
    - Select New -> Choose `Program`
    - Give name `NDK_BUILDER` or as you wish.
    - Under `Main` tab set `Location` to Android NDK build location. Eg. /home/user/android-ndk-r9d/ndk-build
    - Under `Main` tab set `Working Directory` to Project `sdk-render` or `silpa-render`.
    - Under `Build Options` tab check `Specify working set of relevant resources` and click `Specify Resources` and browse to `jni` folder.
    - Also check `During auto builds` and `During clean`
    - Click `Apply` and then `Ok`
    - In Builders window, move `NDK_BUILDER` to top of the list.

- Clean project    

- Now add library to any project
    - Project -> Properties -> Android -> Add
    - Select the project
    - Apply
    - Clean
